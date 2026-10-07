package codes.yousef.summon.effects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals

class HttpPolicyContractTest {
    @Test
    fun clientConfigurationRejectsInvalidBoundsAndSuiteCapabilities() {
        assertFailsWith<IllegalArgumentException> { HttpClientConfig(timeout = -1) }
        assertFailsWith<IllegalArgumentException> { HttpClientConfig(maxRedirects = -1) }
        assertFailsWith<IllegalArgumentException> { HttpClientConfig(maxRequestBytes = 0) }
        assertFailsWith<IllegalArgumentException> { HttpClientConfig(maxResponseBytes = 0) }
        assertFailsWith<IllegalArgumentException> {
            HttpClientConfig(baseUrl = "https://example.test", followRedirects = false, profile = HttpTransportProfile.SUITE_JSON)
        }
        assertFailsWith<IllegalArgumentException> {
            HttpClientConfig(followRedirects = true, profile = HttpTransportProfile.SUITE_JSON)
        }

        val config = HttpClientConfig(
            followRedirects = false,
            maxRedirects = 0,
            profile = HttpTransportProfile.SUITE_JSON,
            csrfTokenProvider = CsrfTokenProvider { "token" }
        )
        assertEquals(HttpTransportProfile.SUITE_JSON, config.profile)
    }

    @Test
    fun requestValidationEnforcesLimitsRetriesIdsAndSameOriginPaths() {
        val general = HttpClientConfig(maxRequestBytes = 8, maxResponseBytes = 16)
        assertEquals(8 to 16, HttpRequest("https://example.test").validateFor(general))
        assertEquals(
            4 to 5,
            HttpRequest("https://example.test", requestByteLimit = 4, responseByteLimit = 5).validateFor(general)
        )

        assertInvalid("request_limit") { HttpRequest("/", requestByteLimit = 0).validateFor(general) }
        assertInvalid("request_limit") { HttpRequest("/", requestByteLimit = 9).validateFor(general) }
        assertInvalid("response_limit") { HttpRequest("/", responseByteLimit = 0).validateFor(general) }
        assertInvalid("response_limit") { HttpRequest("/", responseByteLimit = 17).validateFor(general) }
        assertInvalid("timeout") { HttpRequest("/", timeout = -1).validateFor(general) }
        assertFailsWith<HttpError.RequestTooLarge> {
            HttpRequest("/", method = HttpMethod.POST, body = "123456789").validateFor(general)
        }
        assertInvalid("operation_id_required") {
            HttpRequest("/", HttpMethod.POST, retryPolicy = HttpRetryPolicy.IDEMPOTENT).validateFor(general)
        }
        assertInvalid("operation_id") {
            HttpRequest("/", operationId = "bad id").validateFor(general)
        }
        assertInvalid("operation_id") {
            HttpRequest("/", operationId = "x".repeat(129)).validateFor(general)
        }
        HttpMethod.entries.forEach { method ->
            HttpRequest("/", method, retryPolicy = HttpRetryPolicy.IDEMPOTENT, operationId = "op_1").validateFor(general)
        }

        val suite = HttpClientConfig(
            followRedirects = false,
            maxRedirects = 0,
            profile = HttpTransportProfile.SUITE_JSON,
            csrfTokenProvider = CsrfTokenProvider { "token" }
        )
        listOf("https://example.test/x", "//example.test/x", "/bad\u0001path").forEach { url ->
            assertInvalid("same_origin_path") { HttpRequest(url).validateFor(suite) }
        }
        HttpRequest("/safe/path", HttpMethod.GET).validateFor(suite)
        HttpRequest("/safe/path", HttpMethod.HEAD).validateFor(suite)
        HttpRequest("/safe/path", HttpMethod.OPTIONS).validateFor(suite)

        val suiteWithoutCsrf = suite.copy(csrfTokenProvider = null)
        assertInvalid("csrf_required") { HttpRequest("/mutate", HttpMethod.POST).validateFor(suiteWithoutCsrf) }
    }

    @Test
    fun responsesAndJsonContentTypeExposeStableClassification() {
        assertTrue(HttpResponse(200, "OK", emptyMap(), "").isSuccess)
        assertTrue(HttpResponse(299, "", emptyMap(), "").isSuccess)
        assertTrue(HttpResponse(400, "", emptyMap(), "").isClientError)
        assertTrue(HttpResponse(499, "", emptyMap(), "").isClientError)
        assertTrue(HttpResponse(500, "", emptyMap(), "").isServerError)
        assertTrue(HttpResponse(599, "", emptyMap(), "").isServerError)
        assertFalse(HttpResponse(300, "", emptyMap(), "").isSuccess)

        assertEquals("", HttpResponse(204, "No Content", emptyMap(), "").requireJsonBody())
        assertEquals("", HttpResponse(200, "OK", emptyMap(), "").requireJsonBody())
        assertEquals(
            "{}",
            HttpResponse(200, "OK", mapOf("Content-Type" to "application/json; charset=utf-8"), "{}").requireJsonBody()
        )
        assertEquals(
            "{}",
            HttpResponse(200, "OK", mapOf("content-type" to "application/problem+json"), "{}").requireJsonBody()
        )
        assertFailsWith<HttpError.UnexpectedContentType> {
            HttpResponse(200, "OK", mapOf("content-type" to "text/html"), "{}").requireJsonBody()
        }
        assertFailsWith<HttpError.UnexpectedContentType> {
            HttpResponse(200, "OK", emptyMap(), "{}").requireJsonBody()
        }
    }

    @Test
    fun transportModelsClassifyBoundariesAndIncludeEveryFieldInEquality() {
        listOf(199, 300, 399, 600).forEach { status ->
            val response = HttpResponse(status, "", emptyMap(), "")
            assertFalse(response.isSuccess)
            assertFalse(response.isClientError)
            assertFalse(response.isServerError)
        }
        val response = HttpResponse(200, "OK", mapOf("x" to "y"), "body")
        listOf(
            response.copy(status = 201),
            response.copy(statusText = "Created"),
            response.copy(headers = emptyMap()),
            response.copy(body = "other"),
        ).forEach { assertNotEquals(response, it) }
        assertEquals(response, response.copy())
        assertEquals(response, response)

        val request = HttpRequest(
            url = "/x",
            method = HttpMethod.POST,
            headers = mapOf("x" to "y"),
            body = "{}",
            timeout = 1,
            requestByteLimit = 2,
            responseByteLimit = 3,
            retryPolicy = HttpRetryPolicy.OPERATION_ID,
            operationId = "op"
        )
        listOf(
            request.copy(url = "/y"),
            request.copy(method = HttpMethod.PUT),
            request.copy(headers = emptyMap()),
            request.copy(body = null),
            request.copy(timeout = 2),
            request.copy(requestByteLimit = null),
            request.copy(responseByteLimit = null),
            request.copy(retryPolicy = HttpRetryPolicy.NEVER),
            request.copy(operationId = null),
        ).forEach { assertNotEquals(request, it) }
        assertEquals(request, request.copy())
        assertEquals(request, request)

        val config = HttpClientConfig(baseUrl = "/api", defaultHeaders = mapOf("x" to "y"), timeout = 1)
        listOf(
            config.copy(baseUrl = null),
            config.copy(defaultHeaders = emptyMap()),
            config.copy(timeout = 2),
            config.copy(followRedirects = false),
            config.copy(maxRedirects = 4),
            config.copy(maxRequestBytes = 2),
            config.copy(maxResponseBytes = 2),
            config.copy(profile = HttpTransportProfile.SUITE_JSON, baseUrl = null, followRedirects = false),
            config.copy(csrfHeaderName = "csrf"),
            config.copy(csrfTokenProvider = CsrfTokenProvider { null }),
        ).forEach { assertNotEquals(config, it) }
        assertEquals(config, config.copy())
        assertEquals(config, config)
    }

    @Test
    fun ciphertextRequestsRequireHttpsBoundedOrderedRanges() {
        val valid = CiphertextObjectRequest(
            url = "https://objects.example.test/blob",
            expiresAtEpochMillis = Long.MAX_VALUE,
            rangeStart = 10,
            rangeEndInclusive = 19,
            maxBytes = 10
        )
        assertEquals(10, valid.maxBytes)
        assertFailsWith<IllegalArgumentException> { valid.copy(url = "http://objects.example.test/blob") }
        assertFailsWith<IllegalArgumentException> { valid.copy(rangeStart = -1) }
        assertFailsWith<IllegalArgumentException> { valid.copy(rangeStart = 10, rangeEndInclusive = 9) }
        assertFailsWith<IllegalArgumentException> { valid.copy(maxBytes = 0) }
        assertFailsWith<IllegalArgumentException> { valid.copy(rangeStart = 10, rangeEndInclusive = 20) }
        assertEquals(valid, valid.copy())
        listOf(
            valid.copy(url = "https://objects.example.test/other"),
            valid.copy(rangeStart = 11),
            valid.copy(expiresAtEpochMillis = Long.MAX_VALUE - 1),
            valid.copy(rangeEndInclusive = 18),
            valid.copy(maxBytes = 11),
        ).forEach { assertNotEquals(valid, it) }
    }

    private fun assertInvalid(code: String, block: () -> Unit) {
        val error = assertFailsWith<HttpError.InvalidRequest>(block = block)
        assertEquals(code, error.code)
    }
}
