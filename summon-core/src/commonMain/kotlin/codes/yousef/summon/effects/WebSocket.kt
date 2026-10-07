package codes.yousef.summon.effects

import kotlinx.coroutines.CoroutineScope

enum class WebSocketState {
    CONNECTING,
    OPEN,
    PAUSED,
    CLOSING,
    CLOSED
}

enum class WebSocketErrorCode {
    CONNECTION,
    NOT_CONNECTED,
    MESSAGE_TOO_LARGE,
    QUEUE_OVERFLOW,
    INVALID_HINT
}

sealed class WebSocketEvent {
    data object Connected : WebSocketEvent()
    data class Message(val data: String) : WebSocketEvent()
    data class Hint(val opaqueId: String) : WebSocketEvent()
    data class Error(val code: WebSocketErrorCode) : WebSocketEvent()
    data object Disconnected : WebSocketEvent()
}

enum class WebSocketPayloadPolicy {
    GENERAL,
    OPAQUE_HINT
}

expect class WebSocketClient {
    fun connect(url: String, protocols: List<String> = emptyList())
    fun send(message: String)
    fun send(data: ByteArray)
    fun pause()
    fun resume()
    fun close(code: Int = 1000, reason: String = "")
    fun dispose()
    val state: WebSocketState
    val isConnected: Boolean
    fun onEvent(handler: (WebSocketEvent) -> Unit)
}

data class WebSocketConfig(
    val url: String,
    val protocols: List<String> = emptyList(),
    val autoReconnect: Boolean = false,
    val reconnectDelay: Long = 1_000,
    val maxReconnectDelay: Long = 30_000,
    val maxReconnectAttempts: Int = 8,
    val pingInterval: Long = 30_000,
    val pongTimeout: Long = 5_000,
    val maxMessageBytes: Int = 65_536,
    val maxQueuedEvents: Int = 64,
    val pauseWhenOffline: Boolean = true,
    val pauseWhenHidden: Boolean = true,
    val payloadPolicy: WebSocketPayloadPolicy = WebSocketPayloadPolicy.GENERAL
) {
    init {
        require(url.startsWith("wss://") || url.startsWith("ws://")) { "WebSocket URL must use ws or wss" }
        require(reconnectDelay > 0 && maxReconnectDelay >= reconnectDelay) { "Invalid reconnect delay" }
        require(maxReconnectAttempts >= 0) { "Reconnect attempt count cannot be negative" }
        require(pingInterval >= 0 && pongTimeout >= 0) { "Ping intervals cannot be negative" }
        require(maxMessageBytes > 0) { "Message byte limit must be positive" }
        require(maxQueuedEvents > 0) { "Event queue limit must be positive" }
    }
}

expect fun createWebSocket(config: WebSocketConfig, scope: CoroutineScope): WebSocketClient

fun createWebSocket(url: String, scope: CoroutineScope): WebSocketClient =
    createWebSocket(WebSocketConfig(url), scope)