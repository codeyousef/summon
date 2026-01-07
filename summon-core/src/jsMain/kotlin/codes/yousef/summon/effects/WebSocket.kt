package codes.yousef.summon.effects

import kotlinx.browser.window
import org.w3c.dom.CloseEvent
import org.w3c.dom.WebSocket as DomWebSocket

/**
 * JavaScript WebSocket client implementation
 */
actual class WebSocketClient {
    private var webSocket: DomWebSocket? = null
    private var eventHandler: ((WebSocketEvent) -> Unit)? = null
    private var config: WebSocketConfig? = null
    private var reconnectAttempts = 0
    private var reconnectTimer: Int? = null
    private var pingTimer: Int? = null

    actual fun connect(url: String, protocols: List<String>) {
        connect(WebSocketConfig(url, protocols))
    }

    fun connect(config: WebSocketConfig) {
        this.config = config
        close() // Close any existing connection

        try {
            webSocket = if (config.protocols.isNotEmpty()) {
                DomWebSocket(config.url, config.protocols.toTypedArray())
            } else {
                DomWebSocket(config.url)
            }

            setupEventListeners()
        } catch (e: Exception) {
            eventHandler?.invoke(WebSocketEvent.Error("Failed to create WebSocket: ${e.message}"))
        }
    }

    private fun setupEventListeners() {
        webSocket?.let { ws ->
            ws.onopen = { event ->
                reconnectAttempts = 0
                startPingTimer()
                eventHandler?.invoke(WebSocketEvent.Connected)
            }

            ws.onmessage = { event ->
                when (val data = event.data) {
                    is String -> eventHandler?.invoke(WebSocketEvent.Message(data))
                    else -> {
                        // Handle binary data if needed
                        eventHandler?.invoke(WebSocketEvent.Message(data.toString()))
                    }
                }
            }

            ws.onerror = { event ->
                eventHandler?.invoke(WebSocketEvent.Error("WebSocket error occurred"))
            }

            ws.onclose = { event ->
                val closeEvent = event as CloseEvent
                stopPingTimer()
                eventHandler?.invoke(WebSocketEvent.Disconnected)

                // Auto-reconnect if enabled and not a normal closure
                if (config?.autoReconnect == true && closeEvent.code.toInt() != 1000 &&
                    reconnectAttempts < (config?.maxReconnectAttempts ?: 3)
                ) {
                    scheduleReconnect()
                }
            }
        }
    }

    private fun scheduleReconnect() {
        reconnectAttempts++
        val delay = config?.reconnectDelay ?: 5000

        reconnectTimer = window.setTimeout({
            config?.let { connect(it) }
        }, delay.toInt())
    }

    private fun startPingTimer() {
        config?.let { cfg ->
            if (cfg.pingInterval > 0) {
                pingTimer = window.setInterval({
                    if (isConnected) {
                        send("ping") // Send ping message
                    }
                }, cfg.pingInterval.toInt())
            }
        }
    }

    private fun stopPingTimer() {
        pingTimer?.let { window.clearInterval(it) }
        pingTimer = null
    }

    actual fun send(message: String) {
        webSocket?.let { ws ->
            if (ws.readyState == DomWebSocket.OPEN) {
                ws.send(message)
            } else {
                eventHandler?.invoke(WebSocketEvent.Error("WebSocket is not connected"))
            }
        } ?: eventHandler?.invoke(WebSocketEvent.Error("WebSocket not initialized"))
    }

    actual fun send(data: ByteArray) {
        webSocket?.let { ws ->
            if (ws.readyState == DomWebSocket.OPEN) {
                // Convert ByteArray to ArrayBuffer for JS
                // Convert ByteArray to base64 string for transmission
                val dataString = data.joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
                ws.send(dataString)
            } else {
                eventHandler?.invoke(WebSocketEvent.Error("WebSocket is not connected"))
            }
        } ?: eventHandler?.invoke(WebSocketEvent.Error("WebSocket not initialized"))
    }

    actual fun close(code: Int, reason: String) {
        reconnectTimer?.let { window.clearTimeout(it) }
        stopPingTimer()
        webSocket?.close(code.toShort(), reason)
        webSocket = null
    }

    actual val state: WebSocketState
        get() = webSocket?.let { ws ->
            when (ws.readyState) {
                DomWebSocket.CONNECTING -> WebSocketState.CONNECTING
                DomWebSocket.OPEN -> WebSocketState.OPEN
                DomWebSocket.CLOSING -> WebSocketState.CLOSING
                DomWebSocket.CLOSED -> WebSocketState.CLOSED
                else -> WebSocketState.CLOSED
            }
        } ?: WebSocketState.CLOSED

    actual val isConnected: Boolean
        get() = webSocket?.readyState == DomWebSocket.OPEN

    actual fun onEvent(handler: (WebSocketEvent) -> Unit) {
        this.eventHandler = handler
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