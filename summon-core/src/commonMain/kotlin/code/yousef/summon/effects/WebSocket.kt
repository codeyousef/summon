package code.yousef.summon.effects

/**
 * WebSocket connection state
 */
enum class WebSocketState {
    CONNECTING,
    OPEN,
    CLOSING,
    CLOSED
}

/**
 * WebSocket event types
 */
sealed class WebSocketEvent {
    object Connected : WebSocketEvent()
    data class Message(val data: String) : WebSocketEvent()
    data class Error(val error: String) : WebSocketEvent()
    object Disconnected : WebSocketEvent()
}

/**
 * Cross-platform WebSocket client interface
 */
expect class WebSocketClient {
    /**
     * Connect to a WebSocket server
     * @param url The WebSocket URL to connect to
     * @param protocols Optional list of sub-protocols
     */
    fun connect(url: String, protocols: List<String> = emptyList())
    
    /**
     * Send a text message
     * @param message The message to send
     */
    fun send(message: String)
    
    /**
     * Send binary data
     * @param data The binary data to send
     */
    fun send(data: ByteArray)
    
    /**
     * Close the WebSocket connection
     * @param code Close code (default: 1000 - normal closure)
     * @param reason Optional reason for closing
     */
    fun close(code: Int = 1000, reason: String = "")
    
    /**
     * Get the current connection state
     */
    val state: WebSocketState
    
    /**
     * Check if the connection is open
     */
    val isConnected: Boolean
    
    /**
     * Set event handlers
     */
    fun onEvent(handler: (WebSocketEvent) -> Unit)
}

/**
 * WebSocket configuration options
 */
data class WebSocketConfig(
    val url: String,
    val protocols: List<String> = emptyList(),
    val autoReconnect: Boolean = false,
    val reconnectDelay: Long = 5000, // milliseconds
    val maxReconnectAttempts: Int = 3,
    val pingInterval: Long = 30000, // milliseconds
    val pongTimeout: Long = 5000 // milliseconds
)

/**
 * Create a WebSocket client with configuration
 */
expect fun createWebSocket(config: WebSocketConfig): WebSocketClient

/**
 * Simple WebSocket client factory
 */
fun createWebSocket(url: String): WebSocketClient = createWebSocket(WebSocketConfig(url))