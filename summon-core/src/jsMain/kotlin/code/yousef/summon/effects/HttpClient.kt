package code.yousef.summon.effects

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.fetch.*
import kotlin.js.Promise

/**
 * JavaScript HTTP client implementation using Fetch API
 */
actual class HttpClient(private val config: HttpClientConfig) {
    
    actual suspend fun execute(request: HttpRequest): HttpResponse {
        try {
            val url = if (config.baseUrl != null && !request.url.startsWith("http")) {
                "${config.baseUrl.trimEnd('/')}/${request.url.trimStart('/')}"
            } else {
                request.url
            }
            
            val headers = Headers()
            
            // Add default headers
            config.defaultHeaders.forEach { (key, value) ->
                headers.append(key, value)
            }
            
            // Add request headers
            request.headers.forEach { (key, value) ->
                headers.append(key, value)
            }
            
            val requestInit = RequestInit(
                method = request.method.name,
                headers = headers,
                body = request.body
            )
            
            // Set up timeout
            val timeoutPromise = Promise<Response> { _, reject ->
                window.setTimeout({
                    reject(Exception("Request timeout"))
                }, (request.timeout.takeIf { it > 0 } ?: config.timeout).toInt())
            }
            
            val fetchPromise = window.fetch(url, requestInit)
            
            // Race between fetch and timeout
            val raceArray = arrayOf(fetchPromise, timeoutPromise)
            val response = js("Promise.race(arguments[0])").unsafeCast<Promise<Response>>(raceArray).await()
            
            val responseHeaders = mutableMapOf<String, String>()
            response.headers.forEach { value: String, key: String ->
                responseHeaders[key] = value
            }
            
            val responseBody = response.text().await()
            
            val httpResponse = HttpResponse(
                status = response.status.toInt(),
                statusText = response.statusText,
                headers = responseHeaders,
                body = responseBody
            )
            
            if (!response.ok) {
                when (response.status.toInt()) {
                    in 400..499 -> throw HttpError.ClientError(response.status.toInt(), "Client error: ${response.statusText}")
                    in 500..599 -> throw HttpError.ServerError(response.status.toInt(), "Server error: ${response.statusText}")
                    else -> throw HttpError.UnknownError("HTTP error: ${response.status} ${response.statusText}")
                }
            }
            
            return httpResponse
            
        } catch (e: Exception) {
            when {
                e.message?.contains("timeout", ignoreCase = true) == true -> 
                    throw HttpError.TimeoutError("Request timeout")
                e.message?.contains("network", ignoreCase = true) == true -> 
                    throw HttpError.NetworkError("Network error: ${e.message}")
                e is HttpError -> throw e
                else -> throw HttpError.UnknownError("Unknown error: ${e.message}")
            }
        }
    }
    
    actual suspend fun get(url: String, headers: Map<String, String>): HttpResponse {
        return execute(HttpRequest(url, HttpMethod.GET, headers))
    }
    
    actual suspend fun post(url: String, body: String, headers: Map<String, String>): HttpResponse {
        return execute(HttpRequest(url, HttpMethod.POST, headers, body))
    }
    
    actual suspend fun put(url: String, body: String, headers: Map<String, String>): HttpResponse {
        return execute(HttpRequest(url, HttpMethod.PUT, headers, body))
    }
    
    actual suspend fun delete(url: String, headers: Map<String, String>): HttpResponse {
        return execute(HttpRequest(url, HttpMethod.DELETE, headers))
    }
    
    actual suspend fun patch(url: String, body: String, headers: Map<String, String>): HttpResponse {
        return execute(HttpRequest(url, HttpMethod.PATCH, headers, body))
    }
}

/**
 * Create an HTTP client with configuration
 */
actual fun createHttpClient(config: HttpClientConfig): HttpClient {
    return HttpClient(config)
}

/**
 * JSON serialization implementation for JS
 */
actual fun toJson(obj: Any): String {
    return JSON.stringify(obj)
}

actual inline fun <reified T> parseJson(json: String): T {
    return JSON.parse<T>(json)
}

/**
 * URL encoding implementation for JS
 */
actual fun encodeURIComponent(value: String): String {
    return js("encodeURIComponent")(value).unsafeCast<String>()
}