@file:JvmName("WebSocketJvm")

package codes.yousef.summon.effects

import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * JVM WebSocket client implementation using Java 11+ WebSocket API
 */
actual class WebSocketClient {
    private var webSocket: WebSocket? = null
    private var eventHandler: ((WebSocketEvent) -> Unit)? = null
    private var config: WebSocketConfig? = null
    private var reconnectAttempts = 0
    private var scheduler: ScheduledExecutorService? = null
    private val httpClient = HttpClient.newHttpClient()
    
    actual fun connect(url: String, protocols: List<String>) {
        connect(WebSocketConfig(url, protocols))
    }
    
    fun connect(config: WebSocketConfig) {
        this.config = config
        close() // Close any existing connection
        
        try {
            val builder = httpClient.newWebSocketBuilder()
            
            // Add sub-protocols if specified
            config.protocols.forEach { protocol ->
                builder.subprotocols(protocol)
            }
            
            val listener = JvmWebSocketListener()
            
            webSocket = builder.buildAsync(URI.create(config.url), listener).join()
            
            // Setup ping timer if configured
            if (config.pingInterval > 0) {
                startPingTimer()
            }
            
        } catch (e: Exception) {
            eventHandler?.invoke(WebSocketEvent.Error("Failed to connect: ${e.message}"))
        }
    }
    
    private fun startPingTimer() {
        config?.let { cfg ->
            if (scheduler == null) {
                scheduler = Executors.newScheduledThreadPool(1)
            }
            
            scheduler?.scheduleAtFixedRate({
                if (isConnected) {
                    send("ping")
                }
            }, cfg.pingInterval, cfg.pingInterval, TimeUnit.MILLISECONDS)
        }
    }
    
    private fun stopPingTimer() {
        scheduler?.shutdown()
        scheduler = null
    }
    
    private fun scheduleReconnect() {
        if (reconnectAttempts >= (config?.maxReconnectAttempts ?: 3)) {
            return
        }
        
        reconnectAttempts++
        val delay = config?.reconnectDelay ?: 5000
        
        if (scheduler == null) {
            scheduler = Executors.newScheduledThreadPool(1)
        }
        
        scheduler?.schedule({
            config?.let { connect(it) }
        }, delay, TimeUnit.MILLISECONDS)
    }

    actual fun send(message: String) {
        webSocket?.let { ws ->
            try {
                ws.sendText(message, true).join()
            } catch (e: Exception) {
                eventHandler?.invoke(WebSocketEvent.Error("Failed to send message: ${e.message}"))
            }
        } ?: eventHandler?.invoke(WebSocketEvent.Error("WebSocket not connected"))
    }

    actual fun send(data: ByteArray) {
        webSocket?.let { ws ->
            try {
                val buffer = ByteBuffer.wrap(data)
                ws.sendBinary(buffer, true).join()
            } catch (e: Exception) {
                eventHandler?.invoke(WebSocketEvent.Error("Failed to send binary data: ${e.message}"))
            }
        } ?: eventHandler?.invoke(WebSocketEvent.Error("WebSocket not connected"))
    }

    actual fun close(code: Int, reason: String) {
        stopPingTimer()
        webSocket?.sendClose(code, reason)?.join()
        webSocket = null
    }

    actual val state: WebSocketState
        get() = webSocket?.let { ws ->
            if (ws.isOutputClosed && ws.isInputClosed) {
                WebSocketState.CLOSED
            } else if (ws.isOutputClosed || ws.isInputClosed) {
                WebSocketState.CLOSING
            } else {
                WebSocketState.OPEN
            }
        } ?: WebSocketState.CLOSED

    actual val isConnected: Boolean
        get() = webSocket?.let { ws ->
            !ws.isOutputClosed && !ws.isInputClosed
        } ?: false

    actual fun onEvent(handler: (WebSocketEvent) -> Unit) {
        this.eventHandler = handler
    }
    
    /**
     * JVM WebSocket listener implementation
     */
    private inner class JvmWebSocketListener : WebSocket.Listener {
        
        override fun onOpen(webSocket: WebSocket) {
            reconnectAttempts = 0
            eventHandler?.invoke(WebSocketEvent.Connected)
            webSocket.request(1) // Request one message
        }
        
        override fun onText(webSocket: WebSocket, data: CharSequence, last: Boolean): CompletableFuture<*>? {
            if (last) {
                eventHandler?.invoke(WebSocketEvent.Message(data.toString()))
            }
            webSocket.request(1) // Request next message
            return null
        }
        
        override fun onBinary(webSocket: WebSocket, data: ByteBuffer, last: Boolean): CompletableFuture<*>? {
            if (last) {
                val bytes = ByteArray(data.remaining())
                data.get(bytes)
                // Convert binary to string for now - could be enhanced to support binary events
                eventHandler?.invoke(WebSocketEvent.Message(bytes.toString(Charsets.UTF_8)))
            }
            webSocket.request(1) // Request next message
            return null
        }
        
        override fun onPing(webSocket: WebSocket, message: ByteBuffer): CompletableFuture<*>? {
            // Automatically respond to ping with pong
            webSocket.sendPong(message)
            webSocket.request(1)
            return null
        }
        
        override fun onPong(webSocket: WebSocket, message: ByteBuffer): CompletableFuture<*>? {
            // Handle pong response (useful for keepalive)
            webSocket.request(1)
            return null
        }
        
        override fun onClose(webSocket: WebSocket, statusCode: Int, reason: String): CompletableFuture<*>? {
            stopPingTimer()
            eventHandler?.invoke(WebSocketEvent.Disconnected)
            
            // Auto-reconnect if enabled and not a normal closure
            if (config?.autoReconnect == true && statusCode != 1000) {
                scheduleReconnect()
            }
            
            return null
        }
        
        override fun onError(webSocket: WebSocket, error: Throwable) {
            eventHandler?.invoke(WebSocketEvent.Error("WebSocket error: ${error.message}"))
        }
    }
}

/**
 * Create a WebSocket client with configuration
 */
actual fun createWebSocket(config: WebSocketConfig): WebSocketClient {
    val client = WebSocketClient()
    client.connect(config)
    return client
}