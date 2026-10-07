package codes.yousef.summon.effects

import codes.yousef.summon.core.mapOfCompat

/**
 * HTTP request methods
 */
enum class HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
}

/** Browser transport policy. Suite profiles deliberately narrow the generic client. */
enum class HttpTransportProfile {
    GENERAL,
    SUITE_JSON
}

enum class HttpRetryPolicy {
    NEVER,
    IDEMPOTENT,
    OPERATION_ID
}

fun interface CsrfTokenProvider {
    fun token(): String?
}

data class HttpResponse(
    val status: Int,
    val statusText: String,
    val headers: Map<String, String>,
    val body: String
) {
    val isSuccess: Boolean get() = status in 200..299
    val isClientError: Boolean get() = status in 400..499
    val isServerError: Boolean get() = status in 500..599
}

/**
 * HTTP request configuration
 */
data class HttpRequest(
    val url: String,
    val method: HttpMethod = HttpMethod.GET,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
    val timeout: Long = 0,
    val requestByteLimit: Int? = null,
    val responseByteLimit: Int? = null,
    val retryPolicy: HttpRetryPolicy = HttpRetryPolicy.NEVER,
    val operationId: String? = null
)

sealed class HttpError(message: String) : Exception(message) {
    class NetworkError : HttpError("Network request failed")
    class TimeoutError : HttpError("Request timed out")
    class RedirectError : HttpError("HTTP redirect denied")
    class RequestTooLarge(val limitBytes: Int) : HttpError("Request body exceeds configured byte limit")
    class ResponseTooLarge(val limitBytes: Int) : HttpError("Response body exceeds configured byte limit")
    class InvalidRequest(val code: String) : HttpError("HTTP request rejected: $code")
    class UnexpectedContentType : HttpError("Response content type is not JSON")
    class ClientError(
        val status: Int,
        val errorCode: String? = null,
        val requestId: String? = null,
        val retryAfterMillis: Long? = null
    ) : HttpError("HTTP client error ($status)")

    class ServerError(
        val status: Int,
        val errorCode: String? = null,
        val requestId: String? = null,
        val retryAfterMillis: Long? = null
    ) : HttpError("HTTP server error ($status)")

    class UnknownError : HttpError("HTTP request failed")
}

/**
 * Cross-platform HTTP client interface
 */
expect class HttpClient {
    /**
     * Execute an HTTP request
     * @param request The HTTP request to execute
     * @return The HTTP response
     * @throws HttpError on failure
     */
    suspend fun execute(request: HttpRequest): HttpResponse

    /**
     * Execute a GET request
     * @param url The URL to request
     * @param headers Optional headers
     * @return The HTTP response
     */
    suspend fun get(url: String, headers: Map<String, String> = emptyMap()): HttpResponse

    /**
     * Execute a POST request
     * @param url The URL to request
     * @param body The request body
     * @param headers Optional headers
     * @return The HTTP response
     */
    suspend fun post(url: String, body: String, headers: Map<String, String> = emptyMap()): HttpResponse

    /**
     * Execute a PUT request
     * @param url The URL to request
     * @param body The request body
     * @param headers Optional headers
     * @return The HTTP response
     */
    suspend fun put(url: String, body: String, headers: Map<String, String> = emptyMap()): HttpResponse

    /**
     * Execute a DELETE request
     * @param url The URL to request
     * @param headers Optional headers
     * @return The HTTP response
     */
    suspend fun delete(url: String, headers: Map<String, String> = emptyMap()): HttpResponse

    /**
     * Execute a PATCH request
     * @param url The URL to request
     * @param body The request body
     * @param headers Optional headers
     * @return The HTTP response
     */
    suspend fun patch(url: String, body: String, headers: Map<String, String> = emptyMap()): HttpResponse
}

data class HttpClientConfig(
    val baseUrl: String? = null,
    val defaultHeaders: Map<String, String> = emptyMap(),
    val timeout: Long = 30_000,
    val followRedirects: Boolean = true,
    val maxRedirects: Int = 5,
    val maxRequestBytes: Int = 1_048_576,
    val maxResponseBytes: Int = 4_194_304,
    val profile: HttpTransportProfile = HttpTransportProfile.GENERAL,
    val csrfHeaderName: String = "X-CSRF-Token",
    val csrfTokenProvider: CsrfTokenProvider? = null
) {
    init {
        require(timeout >= 0) { "HTTP timeout cannot be negative" }
        require(maxRedirects >= 0) { "Maximum redirects cannot be negative" }
        require(maxRequestBytes > 0) { "Request byte limit must be positive" }
        require(maxResponseBytes > 0) { "Response byte limit must be positive" }
        if (profile == HttpTransportProfile.SUITE_JSON) {
            require(baseUrl == null) { "Suite JSON transport is same-origin and does not accept a base URL" }
            require(!followRedirects) { "Suite JSON transport must reject redirects" }
        }
    }
}

expect fun createHttpClient(config: HttpClientConfig = HttpClientConfig()): HttpClient

fun createSuiteJsonHttpClient(
    csrfTokenProvider: CsrfTokenProvider,
    maxRequestBytes: Int = 1_048_576,
    maxResponseBytes: Int = 4_194_304,
    timeout: Long = 30_000
): HttpClient = createHttpClient(
    HttpClientConfig(
        timeout = timeout,
        followRedirects = false,
        maxRedirects = 0,
        maxRequestBytes = maxRequestBytes,
        maxResponseBytes = maxResponseBytes,
        profile = HttpTransportProfile.SUITE_JSON,
        csrfTokenProvider = csrfTokenProvider
    )
)

data class CiphertextObjectRequest(
    val url: String,
    val expiresAtEpochMillis: Long,
    val rangeStart: Long = 0,
    val rangeEndInclusive: Long? = null,
    val maxBytes: Int = 16_777_216
) {
    init {
        require(url.startsWith("https://")) { "Ciphertext object URLs must use HTTPS" }
        require(rangeStart >= 0) { "Ciphertext range start cannot be negative" }
        require(rangeEndInclusive == null || rangeEndInclusive >= rangeStart) { "Invalid ciphertext byte range" }
        require(maxBytes > 0) { "Ciphertext byte limit must be positive" }
        rangeEndInclusive?.let {
            require(it - rangeStart < maxBytes.toLong()) { "Ciphertext range exceeds byte limit" }
        }
    }
}

expect class CiphertextObjectTransport {
    suspend fun get(request: CiphertextObjectRequest): ByteArray
}

expect fun createCiphertextObjectTransport(): CiphertextObjectTransport

private fun HttpMethod.isSafe(): Boolean =
    this == HttpMethod.GET || this == HttpMethod.HEAD || this == HttpMethod.OPTIONS

internal fun HttpRequest.validateFor(config: HttpClientConfig): Pair<Int, Int> {
    val requestLimit = requestByteLimit ?: config.maxRequestBytes
    val responseLimit = responseByteLimit ?: config.maxResponseBytes
    if (requestLimit <= 0 || requestLimit > config.maxRequestBytes) {
        throw HttpError.InvalidRequest("request_limit")
    }
    if (responseLimit <= 0 || responseLimit > config.maxResponseBytes) {
        throw HttpError.InvalidRequest("response_limit")
    }
    if (timeout < 0) throw HttpError.InvalidRequest("timeout")
    if (body != null && body.encodeToByteArray().size > requestLimit) {
        throw HttpError.RequestTooLarge(requestLimit)
    }
    if (retryPolicy != HttpRetryPolicy.NEVER && !method.isSafe() && operationId.isNullOrBlank()) {
        throw HttpError.InvalidRequest("operation_id_required")
    }
    if (operationId != null &&
        (operationId.length !in 1..128 || operationId.any { !it.isLetterOrDigit() && it !in "._:-" })
    ) {
        throw HttpError.InvalidRequest("operation_id")
    }
    if (config.profile == HttpTransportProfile.SUITE_JSON) {
        if (!url.startsWith("/") || url.startsWith("//") || url.any { it.code < 0x20 }) {
            throw HttpError.InvalidRequest("same_origin_path")
        }
        if (!method.isSafe() && config.csrfTokenProvider == null) {
            throw HttpError.InvalidRequest("csrf_required")
        }
    }
    return requestLimit to responseLimit
}

@PublishedApi
internal fun HttpResponse.requireJsonBody(): String {
    if (status == 204 || body.isEmpty()) return body
    val contentType = headers.entries.firstOrNull { it.key.equals("content-type", ignoreCase = true) }?.value
        ?.substringBefore(';')?.trim()?.lowercase()
    if (contentType != "application/json" && contentType?.endsWith("+json") != true) {
        throw HttpError.UnexpectedContentType()
    }
    return body
}

// JSON serialization functions - implemented in platform-specific code
expect fun toJson(obj: Any): String
expect inline fun <reified T> parseJson(json: String): T

/**
 * JSON HTTP client extension functions
 */
object JsonHttpClient {

    /**
     * Execute a GET request and parse JSON response
     */
    suspend inline fun <reified T> HttpClient.getJson(url: String): T {
        val response = get(url, mapOfCompat("Accept" to "application/json"))
        return parseJson(response.requireJsonBody())
    }
    /**
     * Execute a POST request with JSON body and parse JSON response
     */
    suspend inline fun <reified T> HttpClient.postJson(url: String, body: Any): T {
        val jsonBody = toJson(body)
        val response = post(
            url, jsonBody, mapOfCompat(
                "Content-Type" to "application/json",
                "Accept" to "application/json"
            )
        )
        return parseJson(response.requireJsonBody())
    }

    /**
     * Execute a PUT request with JSON body and parse JSON response
     */
    suspend inline fun <reified T> HttpClient.putJson(url: String, body: Any): T {
        val jsonBody = toJson(body)
        val response = put(
            url, jsonBody, mapOfCompat(
                "Content-Type" to "application/json",
                "Accept" to "application/json"
            )
        )
        return parseJson(response.requireJsonBody())
    }

    /**
     * Execute a PATCH request with JSON body and parse JSON response
     */
    suspend inline fun <reified T> HttpClient.patchJson(url: String, body: Any): T {
        val jsonBody = toJson(body)
        val response = patch(
            url, jsonBody, mapOfCompat(
                "Content-Type" to "application/json",
                "Accept" to "application/json"
            )
        )
        return parseJson(response.requireJsonBody())
    }
}

// URL encoding function - implemented in platform-specific code
expect fun encodeURIComponent(value: String): String

/**
 * Form data HTTP client extension functions
 */
object FormHttpClient {

    /**
     * Execute a POST request with form data
     */
    suspend fun HttpClient.postForm(url: String, formData: Map<String, String>): HttpResponse {
        val body = formData.entries.joinToString("&") { (key, value) ->
            "$key=${encodeURIComponent(value)}"
        }
        return post(url, body, mapOfCompat("Content-Type" to "application/x-www-form-urlencoded"))
    }

    /**
     * Execute a PUT request with form data
     */
    suspend fun HttpClient.putForm(url: String, formData: Map<String, String>): HttpResponse {
        val body = formData.entries.joinToString("&") { (key, value) ->
            "$key=${encodeURIComponent(value)}"
        }
        return put(url, body, mapOfCompat("Content-Type" to "application/x-www-form-urlencoded"))
    }
}