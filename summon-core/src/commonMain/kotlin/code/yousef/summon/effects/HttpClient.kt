package code.yousef.summon.effects

import code.yousef.summon.core.mapOfCompat

/**
 * HTTP request methods
 */
enum class HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
}

/**
 * HTTP response representation
 */
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
    val timeout: Long = 30000 // milliseconds
)

/**
 * HTTP client error types
 */
sealed class HttpError : Exception() {
    data class NetworkError(override val message: String) : HttpError()
    data class TimeoutError(override val message: String) : HttpError()
    data class ClientError(val status: Int, override val message: String) : HttpError()
    data class ServerError(val status: Int, override val message: String) : HttpError()
    data class UnknownError(override val message: String) : HttpError()
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
 * HTTP client configuration
 */
data class HttpClientConfig(
    val baseUrl: String? = null,
    val defaultHeaders: Map<String, String> = emptyMap(),
    val timeout: Long = 30000,
    val followRedirects: Boolean = true,
    val maxRedirects: Int = 5
)

/**
 * Create an HTTP client with configuration
 */
expect fun createHttpClient(config: HttpClientConfig = HttpClientConfig()): HttpClient

/**
 * Simple HTTP client factory
 */
fun createHttpClient(): HttpClient = createHttpClient(HttpClientConfig())

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
        return parseJson(response.body)
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
        ))
        return parseJson(response.body)
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
        ))
        return parseJson(response.body)
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
        ))
        return parseJson(response.body)
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