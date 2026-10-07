package codes.yousef.summon.fixture

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.effects.CsrfTokenProvider
import codes.yousef.summon.effects.HttpError
import codes.yousef.summon.effects.HttpMethod
import codes.yousef.summon.effects.HttpRequest
import codes.yousef.summon.effects.HttpRetryPolicy
import codes.yousef.summon.effects.WebSocketConfig
import codes.yousef.summon.effects.WebSocketEvent
import codes.yousef.summon.effects.WebSocketPayloadPolicy
import codes.yousef.summon.effects.createSuiteJsonHttpClient
import codes.yousef.summon.effects.createWebSocket
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.state.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TransportFixture(private val signalUrl: String) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val client = createSuiteJsonHttpClient(CsrfTokenProvider { "synthetic-csrf" })
    val result = mutableStateOf("idle")
    val signalResult = mutableStateOf("idle")
    private var signalConnections = 0
    private var hints = 0
    private var errors = 0

    fun runHttp() {
        scope.launch {
            val outcomes = mutableListOf<String>()
            outcomes += client.execute(HttpRequest("/transport/json")).body
            for (status in listOf(409, 429, 503)) {
                try {
                    client.execute(HttpRequest("/transport/status/$status"))
                } catch (error: HttpError.ClientError) {
                    outcomes += "${error.status}:${error.errorCode}:${error.requestId}:${error.retryAfterMillis}"
                } catch (error: HttpError.ServerError) {
                    outcomes += "${error.status}:${error.errorCode}:${error.requestId}:${error.retryAfterMillis}"
                }
            }
            try {
                client.execute(HttpRequest("/transport/slow", timeout = 100))
            } catch (_: HttpError.TimeoutError) {
                outcomes += "timeout"
            }
            try {
                client.execute(HttpRequest("/transport/oversize", responseByteLimit = 128))
            } catch (_: HttpError.ResponseTooLarge) {
                outcomes += "oversize"
            }
            val operation = client.execute(
                HttpRequest(
                    url = "/transport/operation",
                    method = HttpMethod.POST,
                    body = "{}",
                    headers = mapOf("Content-Type" to "application/json"),
                    retryPolicy = HttpRetryPolicy.OPERATION_ID,
                    operationId = "synthetic-operation-01"
                )
            )
            outcomes += operation.body
            scope.launch {
                try {
                    client.execute(HttpRequest("/transport/redirect", timeout = 500))
                } catch (_: HttpError) {
                    // The observable invariant is that the cross-origin target is never reached.
                }
            }
            outcomes += "redirect-guarded"
            result.value = outcomes.joinToString("|")
        }
    }

    fun startSignals() {
        signalResult.value = "starting"
        try {
            val socket = createWebSocket(
                WebSocketConfig(
                    url = signalUrl,
                    autoReconnect = true,
                    reconnectDelay = 25,
                    maxReconnectDelay = 100,
                    maxReconnectAttempts = 4,
                    pingInterval = 0,
                    maxMessageBytes = 64,
                    pauseWhenOffline = false,
                    maxQueuedEvents = 8,
                    pauseWhenHidden = false,
                    payloadPolicy = WebSocketPayloadPolicy.OPAQUE_HINT
                ),
                scope
            )
            socket.onEvent { event ->
                when (event) {
                    WebSocketEvent.Connected -> signalConnections++
                    is WebSocketEvent.Hint -> hints++
                    is WebSocketEvent.Error -> errors++
                    else -> Unit
                }
                signalResult.value = "connections=$signalConnections;hints=$hints;errors=$errors"
            }
            scope.launch {
                client.execute(HttpRequest("/transport/slow?stale=true"))
                signalResult.value = "stale-http-response"
            }
        } catch (_: Throwable) {
            signalResult.value = "construction-failed"
        }
    }

    fun dispose() {
        scope.cancel()
        result.value = "disposed"
        signalResult.value = "disposed"
    }

    @Composable
    fun Content() {
        DisposableEffect(this) { { dispose() } }
        Column {
            Text("Transport qualification", Modifier().attribute("data-testid", "transport-title"))
            Text(result.value, Modifier().attribute("data-testid", "transport-result"))
            Button(onClick = ::runHttp, label = "Run transport probes")
            Text(signalResult.value, Modifier().attribute("data-testid", "signal-result"))
            Button(onClick = ::startSignals, label = "Start signals")
            Button(onClick = ::dispose, label = "Dispose transport")
        }
    }
}
