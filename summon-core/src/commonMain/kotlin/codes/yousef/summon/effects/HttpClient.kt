package codes.yousef.summon.effects

import codes.yousef.summon.core.mapOfCompat

/**
 * HTTP request methods
 */
enum class HttpMethod {
    /** The get HTTP method option. */
    GET,
    /** The post HTTP method option. */
    POST,
    /** The put HTTP method option. */
    PUT,
    /** The delete HTTP method option. */
    DELETE,
    /** The patch HTTP method option. */
    PATCH,
    /** The head HTTP method option. */
    HEAD,
    /** The options HTTP method option. */
    OPTIONS
}

/** Browser transport policy. Suite profiles deliberately narrow the generic client. */
enum class HttpTransportProfile {
    /** The general HTTP transport profile option. */
    GENERAL,
    /** The suite JSON HTTP transport profile option. */
    SUITE_JSON
}

/** Supported HTTP retry policy values. */
enum class HttpRetryPolicy {
    /** The never HTTP retry policy option. */
    NEVER,
    /** The idempotent HTTP retry policy option. */
    IDEMPOTENT,
    /** The operation ID HTTP retry policy option. */
    OPERATION_ID
}

/** Contract for CSRF token provider. */
fun interface CsrfTokenProvider {
    /**
     * Returns the token.
     *
     * @return The resulting value.
     */
    fun token(): String?
}

/**
 * Represents HTTP response.
 *
 * @property status The status value.
 * @property statusText The status text value.
 * @property headers The headers value.
 * @property body The body value.
 */
data class HttpResponse(
    val status: Int,
    val statusText: String,
    val headers: Map<String, String>,
    val body: String
) {
    /** The property declaration value. */
    val isSuccess: Boolean get() = status in 200..299
    /** The property declaration value. */
    val isClientError: Boolean get() = status in 400..499
    /** The property declaration value. */
    val isServerError: Boolean get() = status in 500..599
}

/**
 * HTTP request configuration

 * @property url Target URL.
 * @property method The method value.
 * @property headers The headers value.
 * @property body The body value.
 * @property timeout Timeout in milliseconds.
 * @property requestByteLimit The request byte limit value.
 * @property responseByteLimit The response byte limit value.
 * @property retryPolicy The retry policy value.
 * @property operationId The operation id value.
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

/** Represents HTTP error. */
sealed class HttpError(message: String) : Exception(message) {
    /** Represents network error. */
    class NetworkError : HttpError("Network request failed")
    /** Represents timeout error. */
    class TimeoutError : HttpError("Request timed out")
    /** Represents redirect error. */
    class RedirectError : HttpError("HTTP redirect denied")
    /**
     * Represents request too large.
     *
     * @property limitBytes The limit bytes value.
     */
    class RequestTooLarge(val limitBytes: Int) : HttpError("Request body exceeds configured byte limit")
    /**
     * Represents response too large.
     *
     * @property limitBytes The limit bytes value.
     */
    class ResponseTooLarge(val limitBytes: Int) : HttpError("Response body exceeds configured byte limit")
    /**
     * Represents invalid request.
     *
     * @property code The code value.
     */
    class InvalidRequest(val code: String) : HttpError("HTTP request rejected: $code")
    /** Represents unexpected content type. */
    class UnexpectedContentType : HttpError("Response content type is not JSON")
    /**
     * Represents client error.
     *
     * @property status The status value.
     * @property errorCode The error code value.
     * @property requestId The request id value.
     * @property retryAfterMillis The retry after millis value.
     */
    class ClientError(
        val status: Int,
        val errorCode: String? = null,
        val requestId: String? = null,
        val retryAfterMillis: Long? = null
    ) : HttpError("HTTP client error ($status)")

    /**
     * Represents server error.
     *
     * @property status The status value.
     * @property errorCode The error code value.
     * @property requestId The request id value.
     * @property retryAfterMillis The retry after millis value.
     */
    class ServerError(
        val status: Int,
        val errorCode: String? = null,
        val requestId: String? = null,
        val retryAfterMillis: Long? = null
    ) : HttpError("HTTP server error ($status)")

    /** Represents unknown error. */
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

/**
 * Represents HTTP client config.
 *
 * @property baseUrl The base url value.
 * @property defaultHeaders The default headers value.
 * @property timeout Timeout in milliseconds.
 * @property followRedirects The follow redirects value.
 * @property maxRedirects The max redirects value.
 * @property maxRequestBytes The max request bytes value.
 * @property maxResponseBytes The max response bytes value.
 * @property profile The profile value.
 * @property csrfHeaderName The csrf header name value.
 * @property csrfTokenProvider The csrf token provider value.
 */
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

/**
 * Creates HTTP client.
 *
 * @param config The config value.
 * @return The resulting value.
 */
expect fun createHttpClient(config: HttpClientConfig = HttpClientConfig()): HttpClient

/**
 * Creates suite JSON HTTP client.
 *
 * @param csrfTokenProvider The csrf token provider value.
 * @param maxRequestBytes The max request bytes value.
 * @param maxResponseBytes The max response bytes value.
 * @param timeout Timeout in milliseconds.
 * @return The resulting value.
 */
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

/**
 * Represents ciphertext object request.
 *
 * @property url Target URL.
 * @property expiresAtEpochMillis The expires at epoch millis value.
 * @property rangeStart The range start value.
 * @property rangeEndInclusive The range end inclusive value.
 * @property maxBytes The max bytes value.
 */
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

/** Represents ciphertext object transport. */
expect class CiphertextObjectTransport {
    /**
     * Returns the operation.
     *
     * @param request The request value.
     * @return The resulting value.
     */
    suspend fun get(request: CiphertextObjectRequest): ByteArray
}

/**
 * Creates ciphertext object transport.
 *
 * @return The resulting value.
 */
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
/**
 * Converts this value to JSON.
 *
 * @param obj The obj value.
 * @return The resulting value.
 */
expect inline fun <reified T> toJson(obj: T): String
/**
 * Parses JSON.
 *
 * @param json The json value.
 * @return The resulting value.
 */
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
    suspend inline fun <reified Request, reified Response> HttpClient.postJson(url: String, body: Request): Response {
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
    suspend inline fun <reified Request, reified Response> HttpClient.putJson(url: String, body: Request): Response {
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
    suspend inline fun <reified Request, reified Response> HttpClient.patchJson(url: String, body: Request): Response {
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
/**
 * Executes the encode uri component operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
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