package codes.yousef.summon.effects

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class JvmWebSocketLifecycleTest {
    @Test
    fun disconnectedClientReportsBoundedSendsAndOwnedPauseResumeDisposal() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val client = createWebSocket(
            WebSocketConfig(
                url = "ws://127.0.0.1:1/live",
                maxMessageBytes = 4,
                pingInterval = 0
            ),
            scope
        )
        val events = Channel<WebSocketEvent>(Channel.UNLIMITED)
        client.onEvent { events.trySend(it) }

        client.send("12345")
        awaitError(events, WebSocketErrorCode.MESSAGE_TOO_LARGE)
        client.send(byteArrayOf(1, 2, 3, 4, 5))
        awaitError(events, WebSocketErrorCode.MESSAGE_TOO_LARGE)
        client.send("ok")
        awaitError(events, WebSocketErrorCode.NOT_CONNECTED)
        client.send(byteArrayOf(1))
        awaitError(events, WebSocketErrorCode.NOT_CONNECTED)

        assertFalse(client.isConnected)
        client.pause()
        assertEquals(WebSocketState.PAUSED, client.state)
        client.pause()
        client.resume()
        assertEquals(WebSocketState.CLOSED, client.state)
        client.close(1000, "test")
        assertEquals(WebSocketState.CLOSED, client.state)
        client.dispose()
        client.dispose()
        assertFalse(client.isConnected)
        client.resume()
        scope.cancel()
    }

    @Test
    fun reconnectReplacesConfigurationAndUninitializedClientRejectsConnect() = runBlocking {
        val raw = WebSocketClient()
        assertFailsWith<IllegalStateException> { raw.connect("ws://127.0.0.1:1") }
        raw.dispose()

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val client = createWebSocket(
            WebSocketConfig(
                url = "ws://127.0.0.1:1/first",
                protocols = listOf("summon.v1", "fallback"),
                autoReconnect = true,
                reconnectDelay = 1,
                maxReconnectDelay = 2,
                maxReconnectAttempts = 2,
                pingInterval = 0
            ),
            scope
        )
        val events = Channel<WebSocketEvent>(Channel.UNLIMITED)
        client.onEvent { events.trySend(it) }
        awaitError(events, WebSocketErrorCode.CONNECTION)
        client.connect("ws://127.0.0.1:1/replacement", emptyList())
        awaitError(events, WebSocketErrorCode.CONNECTION)
        client.dispose()
        scope.cancel()
    }

    private suspend fun awaitError(events: Channel<WebSocketEvent>, expected: WebSocketErrorCode) {
        withTimeout(5_000) {
            while (true) {
                val event = events.receive()
                if (event is WebSocketEvent.Error && event.code == expected) return@withTimeout
            }
        }
    }
}
