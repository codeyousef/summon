package codes.yousef.summon.effects

import kotlinx.coroutines.CoroutineScope

/** Supported web socket state values. */
enum class WebSocketState {
    /** The connecting web socket state option. */
    CONNECTING,
    /** The open web socket state option. */
    OPEN,
    /** The paused web socket state option. */
    PAUSED,
    /** The closing web socket state option. */
    CLOSING,
    /** The closed web socket state option. */
    CLOSED
}

/** Supported web socket error code values. */
enum class WebSocketErrorCode {
    /** The connection web socket error code option. */
    CONNECTION,
    /** The not connected web socket error code option. */
    NOT_CONNECTED,
    /** The message too large web socket error code option. */
    MESSAGE_TOO_LARGE,
    /** The queue overflow web socket error code option. */
    QUEUE_OVERFLOW,
    /** The invalid hint web socket error code option. */
    INVALID_HINT
}

/** Represents web socket event. */
sealed class WebSocketEvent {
    /** Provides connected operations. */
    data object Connected : WebSocketEvent()
    /**
     * Represents message.
     *
     * @property data The data value.
     */
    data class Message(val data: String) : WebSocketEvent()
    /**
     * Represents hint.
     *
     * @property opaqueId The opaque id value.
     */
    data class Hint(val opaqueId: String) : WebSocketEvent()
    /**
     * Represents error.
     *
     * @property code The code value.
     */
    data class Error(val code: WebSocketErrorCode) : WebSocketEvent()
    /** Provides disconnected operations. */
    data object Disconnected : WebSocketEvent()
}

/** Supported web socket payload policy values. */
enum class WebSocketPayloadPolicy {
    /** The general web socket payload policy option. */
    GENERAL,
    /** The opaque hint web socket payload policy option. */
    OPAQUE_HINT
}

/** Represents web socket client. */
expect class WebSocketClient {
    /**
     * Executes the connect operation.
     *
     * @param url Target URL.
     * @param protocols The protocols value.
     */
    fun connect(url: String, protocols: List<String> = emptyList())
    /**
     * Sends the supplied value.
     *
     * @param message Message content.
     */
    fun send(message: String)
    /**
     * Sends the supplied value.
     *
     * @param data The data value.
     */
    fun send(data: ByteArray)
    /** Pauses the operation. */
    fun pause()
    /** Resumes the operation. */
    fun resume()
    /**
     * Closes the operation.
     *
     * @param code The code value.
     * @param reason The reason value.
     */
    fun close(code: Int = 1000, reason: String = "")
    /** Disposes the operation. */
    fun dispose()
    /** The property declaration value. */
    val state: WebSocketState
    /** The property declaration value. */
    val isConnected: Boolean
    /**
     * Handles event.
     *
     * @param handler The handler value.
     */
    fun onEvent(handler: (WebSocketEvent) -> Unit)
}

/**
 * Represents web socket config.
 *
 * @property url Target URL.
 * @property protocols The protocols value.
 * @property autoReconnect The auto reconnect value.
 * @property reconnectDelay The reconnect delay value.
 * @property maxReconnectDelay The max reconnect delay value.
 * @property maxReconnectAttempts The max reconnect attempts value.
 * @property pingInterval The ping interval value.
 * @property pongTimeout The pong timeout value.
 * @property maxMessageBytes The max message bytes value.
 * @property maxQueuedEvents The max queued events value.
 * @property pauseWhenOffline The pause when offline value.
 * @property pauseWhenHidden The pause when hidden value.
 * @property payloadPolicy The payload policy value.
 */
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

/**
 * Creates web socket.
 *
 * @param config The config value.
 * @param scope Coroutine or composition scope.
 * @return The resulting value.
 */
expect fun createWebSocket(config: WebSocketConfig, scope: CoroutineScope): WebSocketClient

/**
 * Creates web socket.
 *
 * @param url Target URL.
 * @param scope Coroutine or composition scope.
 * @return The resulting value.
 */
fun createWebSocket(url: String, scope: CoroutineScope): WebSocketClient =
    createWebSocket(WebSocketConfig(url), scope)