@file:JvmName("HttpClientJvm")

package codes.yousef.summon.effects

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.net.http.HttpClient as JdkHttpClient
import java.net.http.HttpRequest as JdkHttpRequest
import java.net.http.HttpResponse as JdkHttpResponse

/**
 * JVM HTTP client implementation using Java 11+ HttpClient
 */
actual class HttpClient(private val config: HttpClientConfig) {
    
    private val jdkClient = JdkHttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(config.timeout))
        .followRedirects(
            if (config.followRedirects) JdkHttpClient.Redirect.NORMAL 
            else JdkHttpClient.Redirect.NEVER
        )
        .build()
    
    actual suspend fun execute(request: HttpRequest): HttpResponse = withContext(Dispatchers.IO) {
        try {
            val url = if (config.baseUrl != null && !request.url.startsWith("http")) {
                "${config.baseUrl.trimEnd('/')}/${request.url.trimStart('/')}"
            } else {
                request.url
            }
            
            val requestBuilder = JdkHttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(request.timeout.takeIf { it > 0 } ?: config.timeout))
            
            // Add default headers
            config.defaultHeaders.forEach { (key, value) ->
                requestBuilder.header(key, value)
            }
            
            // Add request headers
            request.headers.forEach { (key, value) ->
                requestBuilder.header(key, value)
            }
            
            // Set method and body
            when (request.method) {
                HttpMethod.GET -> requestBuilder.GET()
                HttpMethod.POST -> requestBuilder.POST(
                    JdkHttpRequest.BodyPublishers.ofString(request.body ?: "")
                )
                HttpMethod.PUT -> requestBuilder.PUT(
                    JdkHttpRequest.BodyPublishers.ofString(request.body ?: "")
                )
                HttpMethod.DELETE -> requestBuilder.DELETE()
                HttpMethod.PATCH -> requestBuilder.method("PATCH", 
                    JdkHttpRequest.BodyPublishers.ofString(request.body ?: "")
                )
                HttpMethod.HEAD -> requestBuilder.method("HEAD", 
                    JdkHttpRequest.BodyPublishers.noBody()
                )
                HttpMethod.OPTIONS -> requestBuilder.method("OPTIONS", 
                    JdkHttpRequest.BodyPublishers.noBody()
                )
            }
            
            val jdkRequest = requestBuilder.build()
            val jdkResponse = jdkClient.send(jdkRequest, JdkHttpResponse.BodyHandlers.ofString())
            
            val responseHeaders = jdkResponse.headers().map().mapValues { (_, values) ->
                values.joinToString(", ")
            }
            
            val httpResponse = HttpResponse(
                status = jdkResponse.statusCode(),
                statusText = when (jdkResponse.statusCode()) {
                    200 -> "OK"
                    201 -> "Created"
                    204 -> "No Content"
                    400 -> "Bad Request"
                    401 -> "Unauthorized"
                    403 -> "Forbidden"
                    404 -> "Not Found"
                    500 -> "Internal Server Error"
                    else -> "HTTP ${jdkResponse.statusCode()}"
                },
                headers = responseHeaders,
                body = jdkResponse.body()
            )
            
            if (!httpResponse.isSuccess) {
                when (jdkResponse.statusCode()) {
                    in 400..499 -> throw HttpError.ClientError(
                        jdkResponse.statusCode(), 
                        "Client error: ${httpResponse.statusText}"
                    )
                    in 500..599 -> throw HttpError.ServerError(
                        jdkResponse.statusCode(), 
                        "Server error: ${httpResponse.statusText}"
                    )
                    else -> throw HttpError.UnknownError(
                        "HTTP error: ${jdkResponse.statusCode()} ${httpResponse.statusText}"
                    )
                }
            }
            
            return@withContext httpResponse
            
        } catch (e: java.net.http.HttpTimeoutException) {
            throw HttpError.TimeoutError("Request timeout")
        } catch (e: java.net.ConnectException) {
            throw HttpError.NetworkError("Connection failed: ${e.message}")
        } catch (e: java.net.UnknownHostException) {
            throw HttpError.NetworkError("Unknown host: ${e.message}")
        } catch (e: HttpError) {
            throw e
        } catch (e: Exception) {
            throw HttpError.UnknownError("Unknown error: ${e.message}")
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
 * JSON HTTP client implementation for JVM
 */
private val json = Json { 
    ignoreUnknownKeys = true
    encodeDefaults = true 
}

/**
 * JSON serialization implementation for JVM
 */
actual fun toJson(obj: Any): String {
    return json.encodeToString(obj)
}

actual inline fun <reified T> parseJson(json: String): T {
    return Json.decodeFromString<T>(json)
}

/**
 * URL encoding implementation for JVM
 */
actual fun encodeURIComponent(value: String): String {
    return URLEncoder.encode(value, StandardCharsets.UTF_8)
}