package codes.yousef.summon.effects

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JvmWebSocketServerContractTest {
    @Test
    fun realServerExercisesMessagesHintsBinaryFramesAndNormalClose() = runBlocking {
        val vertx = Vertx.vertx()
        val server = vertx.createHttpServer().webSocketHandler { socket ->
            when (socket.path()) {
                "/general" -> {
                    socket.writeTextMessage("hello")
                    socket.writeBinaryMessage(Buffer.buffer(byteArrayOf(1, 2)))
                }
                "/oversized" -> socket.writeTextMessage("too-large")
                "/normal-close" -> socket.close(1000, "done")
                "/abnormal-close" -> socket.close(1011, "failed")
                "/valid-hint" -> socket.writeTextMessage("opaque_1")
                "/valid-long-hint" -> socket.writeTextMessage("a".repeat(256))
                "/invalid-hint" -> socket.writeTextMessage("not a private hint")
                "/invalid-long-hint" -> socket.writeTextMessage("a".repeat(257))
            }
        }
        .listen(0).toCompletionStage().toCompletableFuture().get(5, TimeUnit.SECONDS)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        try {
            val generalEvents = Channel<WebSocketEvent>(Channel.UNLIMITED)
            val general = createWebSocket(
                WebSocketConfig("ws://127.0.0.1:${server.actualPort()}/general", pingInterval = 10),
                scope,
            )
            general.onEvent { generalEvents.trySend(it) }
            assertEquals(WebSocketEvent.Connected, await(generalEvents) { it is WebSocketEvent.Connected })
            assertEquals(WebSocketEvent.Message("hello"), await(generalEvents) { it is WebSocketEvent.Message })
            assertEquals(
                WebSocketEvent.Error(WebSocketErrorCode.INVALID_HINT),
                await(generalEvents) { it is WebSocketEvent.Error && it.code == WebSocketErrorCode.INVALID_HINT },
            )
            assertTrue(general.isConnected)
            general.send("client text")
            general.send(byteArrayOf(3, 4))
            general.close(1000, "done")

            val validEvents = Channel<WebSocketEvent>(Channel.UNLIMITED)
            val valid = createWebSocket(
                WebSocketConfig(
                    "ws://127.0.0.1:${server.actualPort()}/valid-hint",
                    pingInterval = 0,
                    payloadPolicy = WebSocketPayloadPolicy.OPAQUE_HINT,
                ),
                scope,
            )
            valid.onEvent { validEvents.trySend(it) }
            assertEquals(WebSocketEvent.Hint("opaque_1"), await(validEvents) { it is WebSocketEvent.Hint })

            val invalidEvents = Channel<WebSocketEvent>(Channel.UNLIMITED)
            val invalid = createWebSocket(
                WebSocketConfig(
                    "ws://127.0.0.1:${server.actualPort()}/invalid-hint",
                    pingInterval = 0,
                    payloadPolicy = WebSocketPayloadPolicy.OPAQUE_HINT,
                ),
                scope,
            )
            invalid.onEvent { invalidEvents.trySend(it) }
            assertEquals(
                WebSocketEvent.Error(WebSocketErrorCode.INVALID_HINT),
                await(invalidEvents) { it is WebSocketEvent.Error && it.code == WebSocketErrorCode.INVALID_HINT },
            )


            val oversizedEvents = Channel<WebSocketEvent>(Channel.UNLIMITED)
            val oversized = createWebSocket(
                WebSocketConfig(
                    "ws://127.0.0.1:${server.actualPort()}/oversized",
                    maxMessageBytes = 4,
                    pingInterval = 0,
                ),
                scope,
            )
            oversized.onEvent { oversizedEvents.trySend(it) }
            assertEquals(
                WebSocketEvent.Error(WebSocketErrorCode.MESSAGE_TOO_LARGE),
                await(oversizedEvents) { it is WebSocketEvent.Error && it.code == WebSocketErrorCode.MESSAGE_TOO_LARGE },
            )

            val boundaryEvents = Channel<WebSocketEvent>(Channel.UNLIMITED)
            val boundary = createWebSocket(
                WebSocketConfig(
                    "ws://127.0.0.1:${server.actualPort()}/valid-long-hint",
                    pingInterval = 0,
                    payloadPolicy = WebSocketPayloadPolicy.OPAQUE_HINT,
                ),
                scope,
            )
            boundary.onEvent { boundaryEvents.trySend(it) }
            assertEquals(WebSocketEvent.Hint("a".repeat(256)), await(boundaryEvents) { it is WebSocketEvent.Hint })

            val longHintEvents = Channel<WebSocketEvent>(Channel.UNLIMITED)
            val longHint = createWebSocket(
                WebSocketConfig(
                    "ws://127.0.0.1:${server.actualPort()}/invalid-long-hint",
                    pingInterval = 0,
                    payloadPolicy = WebSocketPayloadPolicy.OPAQUE_HINT,
                ),
                scope,
            )
            longHint.onEvent { longHintEvents.trySend(it) }
            assertEquals(
                WebSocketEvent.Error(WebSocketErrorCode.INVALID_HINT),
                await(longHintEvents) { it is WebSocketEvent.Error && it.code == WebSocketErrorCode.INVALID_HINT },
            )

            val normalCloseEvents = Channel<WebSocketEvent>(Channel.UNLIMITED)
            val normalClose = createWebSocket(
                WebSocketConfig("ws://127.0.0.1:${server.actualPort()}/normal-close", autoReconnect = true, pingInterval = 0),
                scope,
            )
            normalClose.onEvent { normalCloseEvents.trySend(it) }
            assertEquals(WebSocketEvent.Disconnected, await(normalCloseEvents) { it is WebSocketEvent.Disconnected })

            val abnormalCloseEvents = Channel<WebSocketEvent>(Channel.UNLIMITED)
            val abnormalClose = createWebSocket(
                WebSocketConfig("ws://127.0.0.1:${server.actualPort()}/abnormal-close", autoReconnect = false, pingInterval = 0),
                scope,
            )
            abnormalClose.onEvent { abnormalCloseEvents.trySend(it) }
            assertEquals(WebSocketEvent.Disconnected, await(abnormalCloseEvents) { it is WebSocketEvent.Disconnected })

            oversized.dispose()
            boundary.dispose()
            longHint.dispose()
            normalClose.dispose()
            abnormalClose.dispose()
            general.dispose()
            valid.dispose()
            invalid.dispose()
        } finally {
            scope.cancel()
            server.close().toCompletionStage().toCompletableFuture().get(5, TimeUnit.SECONDS)
            vertx.close().toCompletionStage().toCompletableFuture().get(5, TimeUnit.SECONDS)
        }
    }

    private suspend fun await(
        events: Channel<WebSocketEvent>,
        predicate: (WebSocketEvent) -> Boolean,
    ): WebSocketEvent = withTimeout(5_000) {
        while (true) {
            val event = events.receive()
            if (predicate(event)) return@withTimeout event
        }
        error("unreachable")
    }
}
