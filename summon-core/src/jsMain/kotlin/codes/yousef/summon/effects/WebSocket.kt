package codes.yousef.summon.effects

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.khronos.webgl.Int8Array
import org.w3c.dom.CloseEvent
import org.w3c.dom.Document
import org.w3c.dom.events.Event
import org.w3c.dom.WebSocket as DomWebSocket
import kotlin.math.min

private val Document.visibilityState: String
    get() = asDynamic().visibilityState as String

actual class WebSocketClient {
    private var webSocket: DomWebSocket? = null
    private var eventHandler: ((WebSocketEvent) -> Unit)? = null
    private lateinit var config: WebSocketConfig
    private lateinit var scope: CoroutineScope
    private lateinit var events: Channel<WebSocketEvent>
    private var reconnectAttempts = 0
    private var reconnectTimer: Int? = null
    private var pingTimer: Int? = null
    private var generation = 0L
    private var paused = false
    private var disposed = false

    private val onlineListener: (Event) -> Unit = { if (paused && config.pauseWhenOffline) resume() }
    private val offlineListener: (Event) -> Unit = { if (config.pauseWhenOffline) pause() }
    private val visibilityListener: (Event) -> Unit = {
        if (config.pauseWhenHidden) {
            if (document.visibilityState == "hidden") pause() else resume()
        }
    }

    internal fun initialize(config: WebSocketConfig, scope: CoroutineScope) {
        this.config = config
        this.scope = scope
        this.events = Channel(config.maxQueuedEvents)
        scope.launch {
            while (isActive) {
                val event = events.receive()
                while (isActive && eventHandler == null) delay(1)
                eventHandler?.invoke(event)
            }
        }
        scope.coroutineContext[kotlinx.coroutines.Job]?.invokeOnCompletion { dispose() }
        window.addEventListener("online", onlineListener)
        window.addEventListener("offline", offlineListener)
        document.addEventListener("visibilitychange", visibilityListener)
        open()
    }

    actual fun connect(url: String, protocols: List<String>) {
        check(::config.isInitialized) { "WebSocket client is not initialized" }
        config = config.copy(url = url, protocols = protocols)
        paused = false
        generation++
        cancelTimers()
        closeSocket(1000, "")
        open()
    }

    private fun open() {
        if (disposed || paused || !scope.isActive) return
        if (config.pauseWhenOffline && !window.navigator.onLine) {
            paused = true
            return
        }
        if (config.pauseWhenHidden && document.visibilityState == "hidden") {
            paused = true
            return
        }
        val connectionGeneration = generation
        try {
            val socket = if (config.protocols.isEmpty()) {
                DomWebSocket(config.url)
            } else {
                DomWebSocket(config.url, config.protocols.toTypedArray())
            }
            webSocket = socket
            socket.onopen = {
                if (connectionGeneration == generation && !disposed) {
                    reconnectAttempts = 0
                    startPingTimer(connectionGeneration)
                    emit(WebSocketEvent.Connected)
                }
            }
            socket.onmessage = { event ->
                if (connectionGeneration == generation && !disposed) {
                    val message = event.data as? String
                    if (message == null || message.encodeToByteArray().size > config.maxMessageBytes) {
                        emit(WebSocketEvent.Error(WebSocketErrorCode.MESSAGE_TOO_LARGE))
                    } else if (config.payloadPolicy == WebSocketPayloadPolicy.OPAQUE_HINT) {
                        if (message.length <= 256 && message.all { it.isLetterOrDigit() || it in "._:-" }) {
                            emit(WebSocketEvent.Hint(message))
                        } else {
                            emit(WebSocketEvent.Error(WebSocketErrorCode.INVALID_HINT))
                        }
                    } else {
                        emit(WebSocketEvent.Message(message))
                    }
                }
            }
            socket.onerror = {
                if (connectionGeneration == generation && !disposed) {
                    emit(WebSocketEvent.Error(WebSocketErrorCode.CONNECTION))
                }
            }
            socket.onclose = { rawEvent ->
                if (connectionGeneration == generation && !disposed) {
                    stopPingTimer()
                    webSocket = null
                    emit(WebSocketEvent.Disconnected)
                    val closeEvent = rawEvent as CloseEvent
                    if (!paused && config.autoReconnect && closeEvent.code.toInt() != 1000) {
                        scheduleReconnect(connectionGeneration)
                    }
                }
            }
        } catch (_: Throwable) {
            emit(WebSocketEvent.Error(WebSocketErrorCode.CONNECTION))
            if (config.autoReconnect) scheduleReconnect(connectionGeneration)
        }
    }

    private fun emit(event: WebSocketEvent) {
        if (!events.trySend(event).isSuccess) {
            eventHandler?.invoke(WebSocketEvent.Error(WebSocketErrorCode.QUEUE_OVERFLOW))
        }
    }

    private fun scheduleReconnect(connectionGeneration: Long) {
        if (reconnectAttempts >= config.maxReconnectAttempts || disposed || paused) return
        reconnectAttempts++
        val multiplier = 1L shl min(reconnectAttempts - 1, 20)
        val delayMillis = min(config.maxReconnectDelay, config.reconnectDelay * multiplier)
        reconnectTimer?.let(window::clearTimeout)
        reconnectTimer = window.setTimeout({
            reconnectTimer = null
            if (connectionGeneration == generation && !disposed && !paused) open()
        }, delayMillis.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
    }

    private fun startPingTimer(connectionGeneration: Long) {
        if (config.pingInterval <= 0) return
        pingTimer = window.setInterval({
            if (connectionGeneration == generation && isConnected) webSocket?.send("ping")
        }, config.pingInterval.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
    }

    private fun stopPingTimer() {
        pingTimer?.let(window::clearInterval)
        pingTimer = null
    }

    private fun cancelTimers() {
        reconnectTimer?.let(window::clearTimeout)
        reconnectTimer = null
        stopPingTimer()
    }

    actual fun send(message: String) {
        if (message.encodeToByteArray().size > config.maxMessageBytes) {
            emit(WebSocketEvent.Error(WebSocketErrorCode.MESSAGE_TOO_LARGE))
        } else if (isConnected) {
            webSocket?.send(message)
        } else {
            emit(WebSocketEvent.Error(WebSocketErrorCode.NOT_CONNECTED))
        }
    }

    actual fun send(data: ByteArray) {
        if (data.size > config.maxMessageBytes) {
            emit(WebSocketEvent.Error(WebSocketErrorCode.MESSAGE_TOO_LARGE))
        } else if (isConnected) {
            val bytes = Int8Array(data.size)
            data.forEachIndexed { index, byte -> bytes.asDynamic()[index] = byte }
            webSocket?.send(bytes.buffer)
        } else {
            emit(WebSocketEvent.Error(WebSocketErrorCode.NOT_CONNECTED))
        }
    }

    actual fun pause() {
        if (disposed || paused) return
        paused = true
        generation++
        cancelTimers()
        closeSocket(1000, "")
    }

    actual fun resume() {
        if (disposed || !paused || !scope.isActive) return
        if (config.pauseWhenOffline && !window.navigator.onLine) return
        if (config.pauseWhenHidden && document.visibilityState == "hidden") return
        paused = false
        generation++
        open()
    }

    actual fun close(code: Int, reason: String) {
        generation++
        paused = false
        cancelTimers()
        closeSocket(code, reason)
    }

    private fun closeSocket(code: Int, reason: String) {
        val socket = webSocket
        webSocket = null
        if (socket != null && socket.readyState < DomWebSocket.CLOSING) socket.close(code.toShort(), reason)
    }

    actual fun dispose() {
        if (disposed) return
        disposed = true
        generation++
        cancelTimers()
        closeSocket(1000, "")
        window.removeEventListener("online", onlineListener)
        window.removeEventListener("offline", offlineListener)
        document.removeEventListener("visibilitychange", visibilityListener)
        if (::events.isInitialized) events.close()
        eventHandler = null
    }

    actual val state: WebSocketState
        get() = if (paused) {
            WebSocketState.PAUSED
        } else {
            when (webSocket?.readyState) {
                DomWebSocket.CONNECTING -> WebSocketState.CONNECTING
                DomWebSocket.OPEN -> WebSocketState.OPEN
                DomWebSocket.CLOSING -> WebSocketState.CLOSING
                else -> WebSocketState.CLOSED
            }
        }

    actual val isConnected: Boolean
        get() = !disposed && !paused && webSocket?.readyState == DomWebSocket.OPEN

    actual fun onEvent(handler: (WebSocketEvent) -> Unit) {
        eventHandler = handler
    }
}

actual fun createWebSocket(config: WebSocketConfig, scope: CoroutineScope): WebSocketClient =
    WebSocketClient().also { it.initialize(config, scope) }