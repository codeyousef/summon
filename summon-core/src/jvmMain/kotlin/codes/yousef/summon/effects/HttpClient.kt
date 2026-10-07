@file:JvmName("HttpClientJvm")

package codes.yousef.summon.effects

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient as JdkHttpClient
import java.net.http.HttpRequest as JdkHttpRequest
import java.net.http.HttpResponse as JdkHttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.CompletionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Represents HTTP client.
 *
 * @property config The config value.
 */
actual class HttpClient(private val config: HttpClientConfig) {
    private val jdkClient = JdkHttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(config.timeout.coerceAtLeast(1)))
        .followRedirects(
            if (config.followRedirects && config.profile == HttpTransportProfile.GENERAL) {
                JdkHttpClient.Redirect.NORMAL
            } else {
                JdkHttpClient.Redirect.NEVER
            }
        )
        .build()

    /**
     * Executes the execute operation.
     *
     * @param request The request value.
     * @return The resulting value.
     */
    actual suspend fun execute(request: HttpRequest): HttpResponse {
        val (_, responseLimit) = request.validateFor(config)
        val csrfToken = if (config.profile == HttpTransportProfile.SUITE_JSON &&
            request.method != HttpMethod.GET && request.method != HttpMethod.HEAD && request.method != HttpMethod.OPTIONS
        ) {
            config.csrfTokenProvider?.token()?.takeIf { it.isNotBlank() }
                ?: throw HttpError.InvalidRequest("csrf_required")
        } else {
            null
        }
        val url = if (config.baseUrl != null && !request.url.startsWith("http")) {
            "${config.baseUrl.trimEnd('/')}/${request.url.trimStart('/')}"
        } else {
            request.url
        }
        val builder = JdkHttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofMillis((request.timeout.takeIf { it > 0 } ?: config.timeout).coerceAtLeast(1)))
        config.defaultHeaders.forEach(builder::header)
        request.headers.forEach(builder::header)
        request.operationId?.let { builder.header("Idempotency-Key", it) }
        csrfToken?.let { builder.header(config.csrfHeaderName, it) }
        when (request.method) {
            HttpMethod.GET -> builder.GET()
            HttpMethod.POST -> builder.POST(JdkHttpRequest.BodyPublishers.ofString(request.body ?: ""))
            HttpMethod.PUT -> builder.PUT(JdkHttpRequest.BodyPublishers.ofString(request.body ?: ""))
            HttpMethod.DELETE -> builder.DELETE()
            HttpMethod.PATCH -> builder.method("PATCH", JdkHttpRequest.BodyPublishers.ofString(request.body ?: ""))
            HttpMethod.HEAD -> builder.method("HEAD", JdkHttpRequest.BodyPublishers.noBody())
            HttpMethod.OPTIONS -> builder.method("OPTIONS", JdkHttpRequest.BodyPublishers.noBody())
        }

        try {
            val response = awaitResponse(
                jdkClient.sendAsync(builder.build(), JdkHttpResponse.BodyHandlers.ofInputStream())
            )
            val responseHeaders = response.headers().map().mapValues { it.value.joinToString(", ") }
            responseHeaders.entries.firstOrNull { it.key.equals("content-length", ignoreCase = true) }
                ?.value?.toLongOrNull()?.let { declared ->
                    if (declared > responseLimit) {
                        response.body().close()
                        throw HttpError.ResponseTooLarge(responseLimit)
                    }
                }
            if (response.statusCode() !in 200..299) response.body().close()
            throwForJvmStatus(response.statusCode(), responseHeaders)
            val body = response.body().use {
                if (request.method == HttpMethod.HEAD || response.statusCode() == 204) "" else it.readBounded(responseLimit).decodeToString()
            }
            return HttpResponse(
                status = response.statusCode(),
                statusText = statusText(response.statusCode()),
                headers = responseHeaders,
                body = body
            )
        } catch (error: CancellationException) {
            throw error
        } catch (error: HttpError) {
            throw error
        } catch (_: java.net.http.HttpTimeoutException) {
            throw HttpError.TimeoutError()
        } catch (_: java.net.ConnectException) {
            throw HttpError.NetworkError()
        } catch (_: java.net.UnknownHostException) {
            throw HttpError.NetworkError()
        } catch (error: CompletionException) {
            if (error.causedByTimeout()) throw HttpError.TimeoutError()
            throw HttpError.NetworkError()
        } catch (_: Exception) {
            throw HttpError.UnknownError()
        }
    }
private fun Throwable.causedByTimeout(): Boolean {
    var current: Throwable? = this
    while (current != null) {
        if (current is java.net.http.HttpTimeoutException) return true
        current = current.cause
    }
    return false
}


    /**
     * Returns the operation.
     *
     * @param url Target URL.
     * @param headers The headers value.
     * @return The resulting value.
     */
    actual suspend fun get(url: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.GET, headers))

    /**
     * Executes the post operation.
     *
     * @param url Target URL.
     * @param body The body value.
     * @param headers The headers value.
     * @return The resulting value.
     */
    actual suspend fun post(url: String, body: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.POST, headers, body))

    /**
     * Executes the put operation.
     *
     * @param url Target URL.
     * @param body The body value.
     * @param headers The headers value.
     * @return The resulting value.
     */
    actual suspend fun put(url: String, body: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.PUT, headers, body))

    /**
     * Executes the delete operation.
     *
     * @param url Target URL.
     * @param headers The headers value.
     * @return The resulting value.
     */
    actual suspend fun delete(url: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.DELETE, headers))

    /**
     * Executes the patch operation.
     *
     * @param url Target URL.
     * @param body The body value.
     * @param headers The headers value.
     * @return The resulting value.
     */
    actual suspend fun patch(url: String, body: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.PATCH, headers, body))
}

private suspend fun <T> awaitResponse(future: java.util.concurrent.CompletableFuture<T>): T =
    suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { future.cancel(true) }
        future.whenComplete { value, error ->
            if (error == null) continuation.resume(value) else continuation.resumeWithException(error)
        }
    }

private fun InputStream.readBounded(limit: Int): ByteArray {
    val result = java.io.ByteArrayOutputStream(minOf(limit, 8_192))
    val buffer = ByteArray(8_192)
    var total = 0
    while (true) {
        val count = read(buffer)
        if (count < 0) break
        if (count > limit - total) throw HttpError.ResponseTooLarge(limit)
        result.write(buffer, 0, count)
        total += count
    }
    return result.toByteArray()
}

private fun throwForJvmStatus(status: Int, headers: Map<String, String>) {
    if (status in 300..399) throw HttpError.RedirectError()
    if (status in 200..299) return
    val errorCode = safeJvmHeader(headers, "x-error-code")
    val requestId = safeJvmHeader(headers, "x-request-id")
    val retryAfter = safeJvmHeader(headers, "retry-after")?.toLongOrNull()?.times(1_000)
    when (status) {
        in 400..499 -> throw HttpError.ClientError(status, errorCode, requestId, retryAfter)
        in 500..599 -> throw HttpError.ServerError(status, errorCode, requestId, retryAfter)
        else -> throw HttpError.UnknownError()
    }
}

private fun safeJvmHeader(headers: Map<String, String>, name: String): String? =
    headers.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value
        ?.takeIf { it.length <= 128 && it.all { character -> character.isLetterOrDigit() || character in "._:-" } }

private fun statusText(status: Int): String = when (status) {
    200 -> "OK"
    201 -> "Created"
    204 -> "No Content"
    else -> "HTTP $status"
}

/**
 * Creates HTTP client.
 *
 * @param config The config value.
 * @return The resulting value.
 */
actual fun createHttpClient(config: HttpClientConfig): HttpClient = HttpClient(config)

/** Represents ciphertext object transport. */
actual class CiphertextObjectTransport {
    /**
     * Returns the operation.
     *
     * @param request The request value.
     * @return The resulting value.
     */
    actual suspend fun get(request: CiphertextObjectRequest): ByteArray = withContext(Dispatchers.IO) {
        if (System.currentTimeMillis() >= request.expiresAtEpochMillis) {
            throw HttpError.InvalidRequest("object_url_expired")
        }
        val builder = JdkHttpRequest.newBuilder(URI.create(request.url)).GET()
        request.rangeEndInclusive?.let { builder.header("Range", "bytes=${request.rangeStart}-$it") }
        val client = JdkHttpClient.newBuilder().followRedirects(JdkHttpClient.Redirect.NEVER).build()
        val response = awaitResponse(client.sendAsync(builder.build(), JdkHttpResponse.BodyHandlers.ofInputStream()))
        val headers = response.headers().map().mapValues { it.value.joinToString(", ") }
        headers.entries.firstOrNull { it.key.equals("content-length", ignoreCase = true) }
            ?.value?.toLongOrNull()?.let { declared ->
                if (declared > request.maxBytes) {
                    response.body().close()
                    throw HttpError.ResponseTooLarge(request.maxBytes)
                }
            }
        if (response.statusCode() !in 200..299) response.body().close()
        throwForJvmStatus(response.statusCode(), headers)
        response.body().use { it.readBounded(request.maxBytes) }
    }
}

/**
 * Creates ciphertext object transport.
 *
 * @return The resulting value.
 */
actual fun createCiphertextObjectTransport(): CiphertextObjectTransport = CiphertextObjectTransport()

@PublishedApi
internal val httpJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

/**
 * Converts this value to JSON.
 *
 * @param obj The obj value.
 * @return The resulting value.
 */
actual inline fun <reified T> toJson(obj: T): String = httpJson.encodeToString(obj)

/**
 * Parses JSON.
 *
 * @param json The json value.
 * @return The resulting value.
 */
actual inline fun <reified T> parseJson(json: String): T = Json.decodeFromString(json)

/**
 * Executes the encode uri component operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
actual fun encodeURIComponent(value: String): String =
    URLEncoder.encode(value, StandardCharsets.UTF_8)