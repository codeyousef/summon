package codes.yousef.summon.effects

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class TransportPolicyTest {
    private val suiteConfig = HttpClientConfig(
        followRedirects = false,
        maxRedirects = 0,
        profile = HttpTransportProfile.SUITE_JSON,
        csrfTokenProvider = CsrfTokenProvider { "csrf-token" }
    )

    @Test
    fun requestLimitCountsUtf8Bytes() {
        val error = assertFailsWith<HttpError.RequestTooLarge> {
            HttpRequest(
                url = "/records",
                method = HttpMethod.POST,
                body = "éé",
                requestByteLimit = 3
            ).validateFor(suiteConfig)
        }

        assertEquals(3, error.limitBytes)
    }

    @Test
    fun suiteJsonAcceptsOnlyRootRelativeSameOriginPaths() {
        listOf("https://example.test/records", "//example.test/records", "/records\nnext").forEach { url ->
            val error = assertFailsWith<HttpError.InvalidRequest> {
                HttpRequest(url).validateFor(suiteConfig)
            }
            assertEquals("same_origin_path", error.code)
        }
        HttpRequest("/records?cursor=opaque").validateFor(suiteConfig)
    }

    @Test
    fun unsafeRetryRequiresBoundedOperationId() {
        val missing = assertFailsWith<HttpError.InvalidRequest> {
            HttpRequest(
                url = "/records",
                method = HttpMethod.POST,
                retryPolicy = HttpRetryPolicy.OPERATION_ID
            ).validateFor(suiteConfig)
        }
        assertEquals("operation_id_required", missing.code)

        HttpRequest(
            url = "/records",
            method = HttpMethod.POST,
            retryPolicy = HttpRetryPolicy.OPERATION_ID,
            operationId = "operation:01"
        ).validateFor(suiteConfig)
    }

    @Test
    fun unsafeSuiteRequestRequiresCsrfAuthority() {
        val config = suiteConfig.copy(csrfTokenProvider = null)
        val error = assertFailsWith<HttpError.InvalidRequest> {
            HttpRequest("/records", method = HttpMethod.DELETE).validateFor(config)
        }
        assertEquals("csrf_required", error.code)
    }

    @Test
    fun jsonDecodeRequiresJsonMediaTypeButAllowsNoContent() {
        val error = assertFailsWith<HttpError.UnexpectedContentType> {
            HttpResponse(200, "OK", mapOf("content-type" to "text/html"), "secret").requireJsonBody()
        }
        assertIs<HttpError.UnexpectedContentType>(error)
        assertEquals("", HttpResponse(204, "No Content", emptyMap(), "").requireJsonBody())
        assertEquals("{}", HttpResponse(200, "OK", mapOf("Content-Type" to "application/problem+json"), "{}").requireJsonBody())
    }

    @Test
    fun ciphertextRangesAreHttpsAndBounded() {
        assertFailsWith<IllegalArgumentException> {
            CiphertextObjectRequest("http://objects.test/part", Long.MAX_VALUE)
        }
        assertFailsWith<IllegalArgumentException> {
            CiphertextObjectRequest(
                url = "https://objects.test/part",
                expiresAtEpochMillis = Long.MAX_VALUE,
                rangeStart = 10,
                rangeEndInclusive = 20,
                maxBytes = 10
            )
        }
        CiphertextObjectRequest(
            url = "https://objects.test/part",
            expiresAtEpochMillis = Long.MAX_VALUE,
            rangeStart = 10,
            rangeEndInclusive = 19,
            maxBytes = 10
        )
    }

    @Test
    fun liveSignalConfigurationRejectsUnboundedResources() {
        assertFailsWith<IllegalArgumentException> {
            WebSocketConfig("wss://signals.test", maxMessageBytes = 0)
        }
        assertFailsWith<IllegalArgumentException> {
            WebSocketConfig("wss://signals.test", maxQueuedEvents = 0)
        }
        assertEquals(
            WebSocketPayloadPolicy.OPAQUE_HINT,
            WebSocketConfig("wss://signals.test", payloadPolicy = WebSocketPayloadPolicy.OPAQUE_HINT).payloadPolicy
        )
    }
}
