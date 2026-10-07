package codes.yousef.summon.effects

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class JvmHttpClientContractTest {
    private lateinit var server: HttpServer
    private lateinit var baseUrl: String
    private val observed = AtomicReference(ObservedRequest("", "", emptyMap()))

    @BeforeTest
    fun startServer() {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.executor = Executors.newCachedThreadPool()
        server.createContext("/", ::handle)
        server.start()
        baseUrl = "http://127.0.0.1:${server.address.port}"
    }

    @AfterTest
    fun stopServer() {
        server.stop(0)
    }

    @Test
    fun executesEveryMethodCombinesConfigurationAndHandlesEmptyBodies() = runBlocking {
        val client = createHttpClient(
            HttpClientConfig(baseUrl = baseUrl, defaultHeaders = mapOf("X-Default" to "present"))
        )

        assertEquals("ok", client.get("/ok", mapOf("X-Request" to "get")).body)
        assertObserved("GET", "", "get")
        assertEquals("post", client.post("/echo", "post", mapOf("X-Request" to "post")).body)
        assertObserved("POST", "post", "post")
        assertEquals("put", client.put("/echo", "put", mapOf("X-Request" to "put")).body)
        assertObserved("PUT", "put", "put")
        assertEquals("patch", client.patch("/echo", "patch", mapOf("X-Request" to "patch")).body)
        assertObserved("PATCH", "patch", "patch")
        assertEquals("", client.delete("/echo", mapOf("X-Request" to "delete")).body)
        assertObserved("DELETE", "", "delete")

        val head = client.execute(HttpRequest("/ok", HttpMethod.HEAD))
        assertEquals(200, head.status)
        assertEquals("", head.body)
        assertObserved("HEAD", "", null)
        val options = client.execute(HttpRequest("/echo", HttpMethod.OPTIONS))
        assertEquals("", options.body)
        assertObserved("OPTIONS", "", null)
        assertEquals("", client.get("/no-content").body)

        val idempotent = client.execute(HttpRequest("/ok", operationId = "operation-1"))
        assertEquals("operation-1", idempotent.headers["x-received-idempotency"])
    }

    @Test
    fun reportsSuccessMetadataStatusErrorsAndRedirectPolicy() = runBlocking {
        val client = createHttpClient(HttpClientConfig(baseUrl = baseUrl, followRedirects = false))
        assertEquals("OK", client.get("/status/200").statusText)
        assertEquals("Created", client.get("/status/201").statusText)
        assertEquals("No Content", client.get("/status/204").statusText)
        assertEquals("HTTP 202", client.get("/status/202").statusText)

        val badRequest = expectHttpError<HttpError.ClientError> { client.get("/status/400") }
        assertEquals(400, badRequest.status)
        assertEquals("bad_request", badRequest.errorCode)
        assertEquals("request-1", badRequest.requestId)
        assertEquals(2_000, badRequest.retryAfterMillis)
        listOf(401, 403, 404, 409, 429).forEach { status ->
            assertEquals(status, expectHttpError<HttpError.ClientError> { client.get("/status/$status") }.status)
        }
        assertEquals(503, expectHttpError<HttpError.ServerError> { client.get("/status/503") }.status)
        assertEquals(500, expectHttpError<HttpError.ServerError> { client.get("/status/500") }.status)
        expectHttpError<HttpError.UnknownError> { client.get("/status/600") }
        expectHttpError<HttpError.RedirectError> { client.get("/redirect") }

        val following = createHttpClient(HttpClientConfig(baseUrl = baseUrl, followRedirects = true))
        assertEquals("ok", following.get("/redirect").body)
    }

    @Test
    fun enforcesResponseLimitsTimeoutCsrfAndHeaderSanitization() = runBlocking {
        val client = createHttpClient(HttpClientConfig(baseUrl = baseUrl, maxResponseBytes = 8))
        expectHttpError<HttpError.ResponseTooLarge> { client.get("/declared-large") }
        expectHttpError<HttpError.ResponseTooLarge> { client.get("/chunked-large") }
        expectHttpError<HttpError.TimeoutError> {
            client.execute(HttpRequest("/slow", timeout = 10))
        }

        val suite = createHttpClient(
            HttpClientConfig(
                followRedirects = false,
                maxRedirects = 0,
                profile = HttpTransportProfile.SUITE_JSON,
                csrfTokenProvider = CsrfTokenProvider { "" }
            )
        )
        assertEquals("csrf_required", expectHttpError<HttpError.InvalidRequest> {
            suite.execute(HttpRequest("/mutation", HttpMethod.POST))
        }.code)

        val unsafe = expectHttpError<HttpError.ClientError> {
            createHttpClient(HttpClientConfig(baseUrl = baseUrl)).get("/unsafe-error-headers")
        }
        assertEquals(null, unsafe.errorCode)
        assertEquals(null, unsafe.requestId)
        assertEquals(null, unsafe.retryAfterMillis)
    }

    @Test
    fun supportsJsonFormsEncodingAndExpiredCiphertextRejection() = runBlocking {
        val client = createHttpClient(HttpClientConfig(baseUrl = baseUrl))
        val json = with(JsonHttpClient) { client.getJson<JsonObject>("/json") }
        assertEquals("yes", json.getValue("ok").jsonPrimitive.content)

        val formPost = with(FormHttpClient) {
            client.postForm("/echo", linkedMapOf("message" to "hello world", "symbol" to "a+b"))
        }
        assertEquals("message=hello+world&symbol=a%2Bb", formPost.body)
        val formPut = with(FormHttpClient) { client.putForm("/echo", mapOf("x" to "one/two")) }
        assertEquals("x=one%2Ftwo", formPut.body)
        assertEquals("a%2Bb+c", encodeURIComponent("a+b c"))

        val request = CiphertextObjectRequest("https://example.test/object", 0)
        val error = expectHttpError<HttpError.InvalidRequest> {
            createCiphertextObjectTransport().get(request)
        }
        assertEquals("object_url_expired", error.code)
        assertContentEquals("quoted".encodeToByteArray(), parseJson<String>(toJson("quoted")).encodeToByteArray())
    }


    private fun handle(exchange: HttpExchange) {
        exchange.use {
            val body = exchange.requestBody.readBytes().decodeToString()
            val headers = exchange.requestHeaders.entries.associate { (key, values) -> key.lowercase() to values.joinToString(", ") }
            observed.set(ObservedRequest(exchange.requestMethod, body, headers))
            when (exchange.requestURI.path) {
                "/ok" -> respond(exchange, 200, "ok", mapOf("X-Received-Idempotency" to (headers["idempotency-key"] ?: "")))
                "/echo" -> respond(exchange, 200, body)
                "/json" -> respond(exchange, 200, "{\"ok\":\"yes\"}", mapOf("Content-Type" to "application/json; charset=utf-8"))
                "/no-content" -> respond(exchange, 204, "")
                "/redirect" -> respond(exchange, 302, "", mapOf("Location" to "/ok"))
                "/declared-large" -> respond(exchange, 200, "0123456789")
                "/chunked-large" -> {
                    exchange.sendResponseHeaders(200, 0)
                    exchange.responseBody.write("0123456789".encodeToByteArray())
                }
                "/slow" -> {
                    Thread.sleep(100)
                    respond(exchange, 200, "slow")
                }
                "/unsafe-error-headers" -> respond(
                    exchange,
                    400,
                    "",
                    mapOf("X-Error-Code" to "bad value", "X-Request-Id" to "x".repeat(129), "Retry-After" to "soon")
                )
                else -> {
                    val status = exchange.requestURI.path.removePrefix("/status/").toIntOrNull() ?: 404
                    val errorHeaders = if (status == 400) {
                        mapOf("X-Error-Code" to "bad_request", "X-Request-Id" to "request-1", "Retry-After" to "2")
                    } else emptyMap()
                    respond(exchange, status, "", errorHeaders)
                }
            }
        }
    }

    private fun respond(exchange: HttpExchange, status: Int, body: String, headers: Map<String, String> = emptyMap()) {
        headers.forEach { (name, value) -> exchange.responseHeaders.add(name, value) }
        val bytes = body.encodeToByteArray()
        exchange.sendResponseHeaders(status, if (exchange.requestMethod == "HEAD" || status == 204) -1 else bytes.size.toLong())
        if (exchange.requestMethod != "HEAD" && status != 204) exchange.responseBody.write(bytes)
    }

    private fun assertObserved(method: String, body: String, requestHeader: String?) {
        val request = observed.get()
        assertEquals(method, request.method)
        assertEquals(body, request.body)
        assertEquals("present", request.headers["x-default"])
        if (requestHeader != null) assertEquals(requestHeader, request.headers["x-request"])
    }

    private suspend inline fun <reified T : HttpError> expectHttpError(noinline block: suspend () -> Unit): T {
        val error = captureHttpError(block)
        assertTrue(error is T, "Expected ${T::class.simpleName}, got ${error::class.simpleName}")
        return assertIs<T>(error)
    }



    private suspend fun captureHttpError(block: suspend () -> Unit): HttpError =
        try {
            block()
            throw AssertionError("Expected HTTP error")
        } catch (error: HttpError) {
            error
        }

    private data class ObservedRequest(val method: String, val body: String, val headers: Map<String, String>)
}
