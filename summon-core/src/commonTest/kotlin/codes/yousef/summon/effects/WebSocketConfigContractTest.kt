package codes.yousef.summon.effects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class WebSocketConfigContractTest {
    @Test
    fun acceptsSecureAndLoopbackSchemesWithExplicitBounds() {
        val secure = WebSocketConfig("wss://example.test/live")
        assertEquals(WebSocketPayloadPolicy.GENERAL, secure.payloadPolicy)
        val local = WebSocketConfig(
            url = "ws://127.0.0.1/live",
            reconnectDelay = 5,
            maxReconnectDelay = 10,
            maxReconnectAttempts = 0,
            pingInterval = 0,
            pongTimeout = 0,
            maxMessageBytes = 1,
            maxQueuedEvents = 1,
            payloadPolicy = WebSocketPayloadPolicy.OPAQUE_HINT
        )
        assertEquals(WebSocketPayloadPolicy.OPAQUE_HINT, local.payloadPolicy)
    }

    @Test
    fun rejectsInvalidSchemesTimingAttemptsAndBounds() {
        assertFailsWith<IllegalArgumentException> { WebSocketConfig("https://example.test/live") }
        assertFailsWith<IllegalArgumentException> { WebSocketConfig("wss://example.test", reconnectDelay = 0) }
        assertFailsWith<IllegalArgumentException> {
            WebSocketConfig("wss://example.test", reconnectDelay = 10, maxReconnectDelay = 9)
        }
        assertFailsWith<IllegalArgumentException> { WebSocketConfig("wss://example.test", maxReconnectAttempts = -1) }
        assertFailsWith<IllegalArgumentException> { WebSocketConfig("wss://example.test", pingInterval = -1) }
        assertFailsWith<IllegalArgumentException> { WebSocketConfig("wss://example.test", pongTimeout = -1) }
        assertFailsWith<IllegalArgumentException> { WebSocketConfig("wss://example.test", maxMessageBytes = 0) }
        assertFailsWith<IllegalArgumentException> { WebSocketConfig("wss://example.test", maxQueuedEvents = 0) }
    }

    @Test
    fun configurationAndEventIdentityIncludesEveryContractField() {
        val config = WebSocketConfig(
            url = "wss://example.test/live",
            protocols = listOf("private"),
            autoReconnect = true,
            reconnectDelay = 2,
            maxReconnectDelay = 3,
            maxReconnectAttempts = 4,
            pingInterval = 5,
            pongTimeout = 6,
            maxMessageBytes = 7,
            maxQueuedEvents = 8,
            pauseWhenOffline = false,
            pauseWhenHidden = false,
            payloadPolicy = WebSocketPayloadPolicy.OPAQUE_HINT
        )
        assertEquals(config, config.copy())
        assertEquals(config, config)
        listOf(
            config.copy(url = "wss://example.test/other"),
            config.copy(protocols = emptyList()),
            config.copy(autoReconnect = false),
            config.copy(reconnectDelay = 1),
            config.copy(maxReconnectDelay = 4),
            config.copy(maxReconnectAttempts = 5),
            config.copy(pingInterval = 6),
            config.copy(pongTimeout = 7),
            config.copy(maxMessageBytes = 8),
            config.copy(maxQueuedEvents = 9),
            config.copy(pauseWhenOffline = true),
            config.copy(pauseWhenHidden = true),
            config.copy(payloadPolicy = WebSocketPayloadPolicy.GENERAL),
        ).forEach { assertNotEquals(config, it) }

        val message = WebSocketEvent.Message("payload")
        assertEquals(message, message.copy())
        assertNotEquals(message, message.copy(data = "other"))
        val hint = WebSocketEvent.Hint("opaque")
        assertEquals(hint, hint.copy())
        assertNotEquals(hint, hint.copy(opaqueId = "other"))
        val error = WebSocketEvent.Error(WebSocketErrorCode.CONNECTION)
        assertEquals(error, error.copy())
        assertNotEquals(error, error.copy(code = WebSocketErrorCode.INVALID_HINT))
    }
}
