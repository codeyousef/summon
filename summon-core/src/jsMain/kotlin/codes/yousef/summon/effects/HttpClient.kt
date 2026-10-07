package codes.yousef.summon.effects

import kotlinx.browser.window
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.await
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import org.khronos.webgl.Uint8Array
import org.w3c.fetch.Headers
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.Date
import kotlin.js.Promise

actual class HttpClient(private val config: HttpClientConfig) {
    actual suspend fun execute(request: HttpRequest): HttpResponse {
        val (_, responseLimit) = request.validateFor(config)
        val url = resolveUrl(request.url)
        val headers = Headers()
        config.defaultHeaders.forEach { (name, value) -> headers.append(name, value) }
        request.headers.forEach { (name, value) -> headers.append(name, value) }
        request.operationId?.let { headers.set("Idempotency-Key", it) }
        if (config.profile == HttpTransportProfile.SUITE_JSON &&
            request.method != HttpMethod.GET && request.method != HttpMethod.HEAD && request.method != HttpMethod.OPTIONS
        ) {
            val token = config.csrfTokenProvider?.token()?.takeIf { it.isNotBlank() }
                ?: throw HttpError.InvalidRequest("csrf_required")
            headers.set(config.csrfHeaderName, token)
        }

        val controller = js("new AbortController()")
        val init = RequestInit(
            method = request.method.name,
            headers = headers,
            body = request.body
        )
        init.asDynamic().signal = controller.signal
        init.asDynamic().redirect = if (config.profile == HttpTransportProfile.SUITE_JSON) {
            "error"
        } else if (config.followRedirects) {
            "follow"
        } else {
            "manual"
        }
        init.asDynamic().credentials =
            if (config.profile == HttpTransportProfile.SUITE_JSON) "same-origin" else "same-origin"

        val timeoutMillis = request.timeout.takeIf { it > 0 } ?: config.timeout
        var timedOut = false
        val timeoutHandle = if (timeoutMillis > 0) {
            window.setTimeout({
                timedOut = true
                controller.abort()
            }, timeoutMillis.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
        } else {
            null
        }
        val cancellation = currentCoroutineContext().job.invokeOnCompletion {
            controller.abort()
        }

        try {
            val response = window.fetch(url, init).await()
            if (config.profile == HttpTransportProfile.SUITE_JSON &&
                (response.redirected || response.status.toInt() in 300..399)
            ) {
                response.asDynamic().body?.cancel()
                throw HttpError.RedirectError()
            }
            val responseHeaders = responseHeaders(response)
            if (response.status.toInt() !in 200..299) response.asDynamic().body?.cancel()
            throwForStatus(response.status.toInt(), responseHeaders)
            val body = if (request.method == HttpMethod.HEAD || response.status.toInt() == 204) {
                response.asDynamic().body?.cancel()
                ""
            } else {
                readBoundedText(response, responseLimit)
            }
            return HttpResponse(
                status = response.status.toInt(),
                statusText = response.statusText,
                headers = responseHeaders,
                body = body
            )
        } catch (error: CancellationException) {
            controller.abort()
            throw error
        } catch (error: HttpError) {
            throw error
        } catch (_: Throwable) {
            if (timedOut) throw HttpError.TimeoutError()
            throw HttpError.NetworkError()
        } finally {
            timeoutHandle?.let(window::clearTimeout)
            cancellation.dispose()
        }
    }

    private fun resolveUrl(url: String): String =
        if (config.baseUrl != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            "${config.baseUrl.trimEnd('/')}/${url.trimStart('/')}"
        } else {
            url
        }

    actual suspend fun get(url: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.GET, headers))

    actual suspend fun post(url: String, body: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.POST, headers, body))

    actual suspend fun put(url: String, body: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.PUT, headers, body))

    actual suspend fun delete(url: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.DELETE, headers))

    actual suspend fun patch(url: String, body: String, headers: Map<String, String>): HttpResponse =
        execute(HttpRequest(url, HttpMethod.PATCH, headers, body))
}

private fun responseHeaders(response: Response): Map<String, String> = buildMap {
    for (name in arrayOf("content-type", "content-length", "x-error-code", "x-request-id", "retry-after")) {
        response.headers.get(name)?.let { put(name, it) }
    }
}

private fun throwForStatus(status: Int, headers: Map<String, String>) {
    if (status in 200..299) return
    val errorCode = safeHeader(headers, "x-error-code")
    val requestId = safeHeader(headers, "x-request-id")
    val retryAfter = safeHeader(headers, "retry-after")?.toLongOrNull()?.times(1_000)
    when (status) {
        in 400..499 -> throw HttpError.ClientError(status, errorCode, requestId, retryAfter)
        in 500..599 -> throw HttpError.ServerError(status, errorCode, requestId, retryAfter)
        else -> throw HttpError.UnknownError()
    }
}

private fun safeHeader(headers: Map<String, String>, name: String): String? =
    headers.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value
        ?.takeIf { it.length <= 128 && it.all { character -> character.isLetterOrDigit() || character in "._:-" } }

private suspend fun readBoundedText(response: Response, limit: Int): String =
    readBoundedBytes(response, limit).decodeToString()

private suspend fun readBoundedBytes(response: Response, limit: Int): ByteArray {
    response.headers.get("content-length")?.toLongOrNull()?.let {
        if (it > limit) throw HttpError.ResponseTooLarge(limit)
    }
    val reader = response.asDynamic().body?.getReader()
    if (reader == null) {
        val text = response.text().await()
        val bytes = text.encodeToByteArray()
        if (bytes.size > limit) throw HttpError.ResponseTooLarge(limit)
        return bytes
    }
    val chunks = mutableListOf<Uint8Array>()
    var total = 0
    try {
        while (true) {
            val result = reader.read().unsafeCast<Promise<dynamic>>().await()
            if (result.done == true) break
            val chunk = result.value.unsafeCast<Uint8Array>()
            if (chunk.length > limit - total) {
                reader.cancel()
                throw HttpError.ResponseTooLarge(limit)
            }
            chunks += chunk
            total += chunk.length
        }
    } catch (error: CancellationException) {
        reader.cancel()
        throw error
    } catch (error: Throwable) {
        reader.cancel()
        throw error
    }
    val result = ByteArray(total)
    var offset = 0
    chunks.forEach { chunk ->
        for (index in 0 until chunk.length) result[offset++] = chunk.asDynamic()[index]
    }
    return result
}

actual fun createHttpClient(config: HttpClientConfig): HttpClient = HttpClient(config)

actual class CiphertextObjectTransport {
    actual suspend fun get(request: CiphertextObjectRequest): ByteArray {
        if (Date.now().toLong() >= request.expiresAtEpochMillis) {
            throw HttpError.InvalidRequest("object_url_expired")
        }
        val headers = Headers()
        request.rangeEndInclusive?.let {
            headers.set("Range", "bytes=${request.rangeStart}-$it")
        }
        val controller = js("new AbortController()")
        val init = RequestInit(method = "GET", headers = headers)
        init.asDynamic().signal = controller.signal
        init.asDynamic().credentials = "omit"
        init.asDynamic().redirect = "error"
        val cancellation = currentCoroutineContext().job.invokeOnCompletion { controller.abort() }
        try {
            val response = window.fetch(request.url, init).await()
            if (response.redirected || response.status.toInt() in 300..399) {
                response.asDynamic().body?.cancel()
                throw HttpError.RedirectError()
            }
            if (response.status.toInt() !in 200..299) response.asDynamic().body?.cancel()
            throwForStatus(response.status.toInt(), responseHeaders(response))
            return readBoundedBytes(response, request.maxBytes)
        } catch (error: CancellationException) {
            controller.abort()
            throw error
        } catch (error: HttpError) {
            throw error
        } catch (_: Throwable) {
            throw HttpError.NetworkError()
        } finally {
            cancellation.dispose()
        }
    }
}

actual fun createCiphertextObjectTransport(): CiphertextObjectTransport = CiphertextObjectTransport()

actual fun toJson(obj: Any): String = JSON.stringify(obj)

actual inline fun <reified T> parseJson(json: String): T = JSON.parse(json)

actual fun encodeURIComponent(value: String): String =
    js("encodeURIComponent")(value).unsafeCast<String>()