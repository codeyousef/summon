@file:JvmName("WebSocketJvm")

package codes.yousef.summon.effects

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.min

actual class WebSocketClient {
    @Volatile
    private var webSocket: WebSocket? = null
    private var eventHandler: ((WebSocketEvent) -> Unit)? = null
    private lateinit var config: WebSocketConfig
    private lateinit var scope: CoroutineScope
    private lateinit var events: Channel<WebSocketEvent>
    private val httpClient = HttpClient.newHttpClient()
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    private var reconnectTask: ScheduledFuture<*>? = null
    private var pingTask: ScheduledFuture<*>? = null
    private var reconnectAttempts = 0
    private var generation = 0L
    private var paused = false
    private var disposed = false

    internal fun initialize(config: WebSocketConfig, scope: CoroutineScope) {
        this.config = config
        this.scope = scope
        events = Channel(config.maxQueuedEvents)
        scope.launch {
            while (isActive) {
                val event = events.receive()
                while (isActive && eventHandler == null) delay(1)
                eventHandler?.invoke(event)
            }
        }
        scope.coroutineContext[kotlinx.coroutines.Job]?.invokeOnCompletion { dispose() }
        open()
    }

    actual fun connect(url: String, protocols: List<String>) {
        check(::config.isInitialized) { "WebSocket client is not initialized" }
        generation++
        paused = false
        cancelTasks()
        closeSocket(1000, "")
        config = config.copy(url = url, protocols = protocols)
        open()
    }

    private fun open() {
        if (disposed || paused || !scope.isActive) return
        val connectionGeneration = generation
        try {
            val builder = httpClient.newWebSocketBuilder()
            if (config.protocols.isNotEmpty()) {
                builder.subprotocols(config.protocols.first(), *config.protocols.drop(1).toTypedArray())
            }
            builder.buildAsync(URI.create(config.url), Listener(connectionGeneration))
                .whenComplete { socket, error ->
                    if (connectionGeneration != generation || disposed || paused) {
                        socket?.abort()
                    } else if (error == null) {
                        webSocket = socket
                    } else {
                        emit(WebSocketEvent.Error(WebSocketErrorCode.CONNECTION))
                        scheduleReconnect(connectionGeneration)
                    }
                }
        } catch (_: Throwable) {
            emit(WebSocketEvent.Error(WebSocketErrorCode.CONNECTION))
            scheduleReconnect(connectionGeneration)
        }
    }

    private fun emit(event: WebSocketEvent) {
        if (!events.trySend(event).isSuccess) {
            eventHandler?.invoke(WebSocketEvent.Error(WebSocketErrorCode.QUEUE_OVERFLOW))
        }
    }

    private fun scheduleReconnect(connectionGeneration: Long) {
        if (!config.autoReconnect || paused || disposed || reconnectAttempts >= config.maxReconnectAttempts) return
        reconnectAttempts++
        val multiplier = 1L shl min(reconnectAttempts - 1, 20)
        val delayMillis = min(config.maxReconnectDelay, config.reconnectDelay * multiplier)
        reconnectTask?.cancel(false)
        reconnectTask = scheduler.schedule({
            if (connectionGeneration == generation && !paused && !disposed) open()
        }, delayMillis, TimeUnit.MILLISECONDS)
    }

    private fun startPing(connectionGeneration: Long) {
        if (config.pingInterval <= 0) return
        pingTask?.cancel(false)
        pingTask = scheduler.scheduleAtFixedRate({
            if (connectionGeneration == generation && isConnected) {
                webSocket?.sendPing(ByteBuffer.allocate(0))
            }
        }, config.pingInterval, config.pingInterval, TimeUnit.MILLISECONDS)
    }

    private fun cancelTasks() {
        reconnectTask?.cancel(false)
        reconnectTask = null
        pingTask?.cancel(false)
        pingTask = null
    }

    actual fun send(message: String) {
        if (message.encodeToByteArray().size > config.maxMessageBytes) {
            emit(WebSocketEvent.Error(WebSocketErrorCode.MESSAGE_TOO_LARGE))
        } else if (isConnected) {
            webSocket?.sendText(message, true)
        } else {
            emit(WebSocketEvent.Error(WebSocketErrorCode.NOT_CONNECTED))
        }
    }

    actual fun send(data: ByteArray) {
        if (data.size > config.maxMessageBytes) {
            emit(WebSocketEvent.Error(WebSocketErrorCode.MESSAGE_TOO_LARGE))
        } else if (isConnected) {
            webSocket?.sendBinary(ByteBuffer.wrap(data), true)
        } else {
            emit(WebSocketEvent.Error(WebSocketErrorCode.NOT_CONNECTED))
        }
    }

    actual fun pause() {
        if (paused || disposed) return
        paused = true
        generation++
        cancelTasks()
        closeSocket(1000, "")
    }

    actual fun resume() {
        if (!paused || disposed || !scope.isActive) return
        paused = false
        generation++
        open()
    }

    actual fun close(code: Int, reason: String) {
        generation++
        paused = false
        cancelTasks()
        closeSocket(code, reason)
    }

    private fun closeSocket(code: Int, reason: String) {
        val socket = webSocket
        webSocket = null
        socket?.sendClose(code, reason)
    }

    actual fun dispose() {
        if (disposed) return
        disposed = true
        generation++
        cancelTasks()
        closeSocket(1000, "")
        scheduler.shutdownNow()
        if (::events.isInitialized) events.close()
        eventHandler = null
    }

    actual val state: WebSocketState
        get() = if (paused) {
            WebSocketState.PAUSED
        } else {
            webSocket?.let {
                if (it.isOutputClosed && it.isInputClosed) WebSocketState.CLOSED
                else if (it.isOutputClosed || it.isInputClosed) WebSocketState.CLOSING
                else WebSocketState.OPEN
            } ?: WebSocketState.CLOSED
        }

    actual val isConnected: Boolean
        get() = !disposed && !paused && webSocket?.let { !it.isOutputClosed && !it.isInputClosed } == true

    actual fun onEvent(handler: (WebSocketEvent) -> Unit) {
        eventHandler = handler
    }

    private inner class Listener(private val connectionGeneration: Long) : WebSocket.Listener {
        private val text = StringBuilder()
        private var textBytes = 0

        override fun onOpen(socket: WebSocket) {
            if (connectionGeneration != generation || disposed || paused) {
                socket.abort()
                return
            }
            webSocket = socket
            reconnectAttempts = 0
            startPing(connectionGeneration)
            emit(WebSocketEvent.Connected)
            socket.request(1)
        }

        override fun onText(socket: WebSocket, data: CharSequence, last: Boolean): CompletableFuture<*>? {
            if (connectionGeneration != generation || disposed) return null
            val fragment = data.toString()
            textBytes += fragment.encodeToByteArray().size
            if (textBytes > config.maxMessageBytes) {
                text.clear()
                textBytes = 0
                emit(WebSocketEvent.Error(WebSocketErrorCode.MESSAGE_TOO_LARGE))
                socket.abort()
                return null
            }
            text.append(fragment)
            if (last) {
                val message = text.toString()
                text.clear()
                textBytes = 0
                if (config.payloadPolicy == WebSocketPayloadPolicy.OPAQUE_HINT) {
                    if (message.length <= 256 && message.all { it.isLetterOrDigit() || it in "._:-" }) {
                        emit(WebSocketEvent.Hint(message))
                    } else {
                        emit(WebSocketEvent.Error(WebSocketErrorCode.INVALID_HINT))
                    }
                } else {
                    emit(WebSocketEvent.Message(message))
                }
            }
            socket.request(1)
            return null
        }

        override fun onBinary(socket: WebSocket, data: ByteBuffer, last: Boolean): CompletableFuture<*>? {
            emit(WebSocketEvent.Error(WebSocketErrorCode.INVALID_HINT))
            socket.request(1)
            return null
        }

        override fun onPing(socket: WebSocket, message: ByteBuffer): CompletableFuture<*>? {
            socket.sendPong(message)
            socket.request(1)
            return null
        }

        override fun onPong(socket: WebSocket, message: ByteBuffer): CompletableFuture<*>? {
            socket.request(1)
            return null
        }

        override fun onClose(socket: WebSocket, statusCode: Int, reason: String): CompletableFuture<*>? {
            if (connectionGeneration == generation && !disposed) {
                webSocket = null
                pingTask?.cancel(false)
                emit(WebSocketEvent.Disconnected)
                if (!paused && statusCode != 1000) scheduleReconnect(connectionGeneration)
            }
            return null
        }

        override fun onError(socket: WebSocket, error: Throwable) {
            if (connectionGeneration == generation && !disposed) {
                emit(WebSocketEvent.Error(WebSocketErrorCode.CONNECTION))
            }
        }
    }
}

actual fun createWebSocket(config: WebSocketConfig, scope: CoroutineScope): WebSocketClient =
    WebSocketClient().also { it.initialize(config, scope) }