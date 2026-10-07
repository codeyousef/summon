package codes.yousef.summon.effects

import kotlinx.serialization.json.Json
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private data class WasmFetchResult(
    val status: Int,
    val statusText: String,
    val headersJson: String,
    val bytes: JsAny
)

@JsFun("() => new Headers()")
private external fun wasmHeaders(): JsAny

@JsFun("(headers, name, value) => headers.set(name, value)")
private external fun wasmSetHeader(headers: JsAny, name: String, value: String)

@JsFun("(handle) => handle.abort()")
private external fun wasmAbortFetch(handle: JsAny)

@JsFun("(bytes) => bytes.length")
private external fun wasmByteLength(bytes: JsAny): Int

@JsFun("(bytes, index) => bytes[index]")
private external fun wasmByteAt(bytes: JsAny, index: Int): Int

@JsFun("(headersJson, name) => { const value = JSON.parse(headersJson)[name]; return value === undefined ? null : value; }")
private external fun wasmResponseHeader(headersJson: String, name: String): String?

@JsFun("() => Date.now()")
private external fun wasmNow(): Double

@JsFun("(value) => encodeURIComponent(value)")
private external fun wasmEncodeURIComponent(value: String): String

private val wasmJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@JsFun(
    """(url, method, body, headers, redirect, credentials, timeout, limit, onSuccess, onFailure) => {
        const controller = new AbortController();
        let timedOut = false;
        const timer = timeout > 0 ? setTimeout(() => { timedOut = true; controller.abort(); }, timeout) : null;
        const finish = () => { if (timer !== null) clearTimeout(timer); };
        (async () => {
            try {
                const init = { method, headers, redirect, credentials, signal: controller.signal };
                if (method !== 'GET' && method !== 'HEAD') init.body = body;
                const response = await fetch(url, init);
                if (response.redirected || response.status >= 300 && response.status < 400) {
                    if (response.body) await response.body.cancel();
                    finish();
                    onFailure('redirect');
                    return;
                }
                const selected = {};
                for (const name of ['content-type', 'content-length', 'x-error-code', 'x-request-id', 'retry-after']) {
                    const value = response.headers.get(name);
                    if (value !== null) selected[name] = value;
                }
                if (response.status < 200 || response.status > 299 || method === 'HEAD' || response.status === 204) {
                    if (response.body) await response.body.cancel();
                    finish();
                    onSuccess(response.status, response.statusText, JSON.stringify(selected), new Uint8Array(0));
                    return;
                }
                const declared = Number(response.headers.get('content-length'));
                if (Number.isFinite(declared) && declared > limit) {
                    if (response.body) await response.body.cancel();
                    finish();
                    onFailure('too-large');
                    return;
                }
                const reader = response.body ? response.body.getReader() : null;
                if (!reader) {
                    const bytes = new Uint8Array(await response.arrayBuffer());
                    finish();
                    if (bytes.length > limit) onFailure('too-large');
                    else onSuccess(response.status, response.statusText, JSON.stringify(selected), bytes);
                    return;
                }
                const chunks = [];
                let total = 0;
                while (true) {
                    const part = await reader.read();
                    if (part.done) break;
                    if (part.value.length > limit - total) {
                        await reader.cancel();
                        finish();
                        onFailure('too-large');
                        return;
                    }
                    chunks.push(part.value);
                    total += part.value.length;
                }
                const bytes = new Uint8Array(total);
                let offset = 0;
                for (const chunk of chunks) {
                    bytes.set(chunk, offset);
                    offset += chunk.length;
                }
                finish();
                onSuccess(response.status, response.statusText, JSON.stringify(selected), bytes);
            } catch (_) {
                finish();
                onFailure(timedOut ? 'timeout' : 'network');
            }
        })();
        return controller;
    }"""
)
private external fun wasmFetch(
    url: String,
    method: String,
    body: String,
    headers: JsAny,
    redirect: String,
    credentials: String,
    timeout: Int,
    limit: Int,
    onSuccess: (Int, String, String, JsAny) -> Unit,
    onFailure: (String) -> Unit
): JsAny

actual class HttpClient(private val config: HttpClientConfig) {
    actual suspend fun execute(request: HttpRequest): HttpResponse {
        val (_, responseLimit) = request.validateFor(config)
        val headers = wasmHeaders()
        config.defaultHeaders.forEach { (name, value) -> wasmSetHeader(headers, name, value) }
        request.headers.forEach { (name, value) -> wasmSetHeader(headers, name, value) }
        request.operationId?.let { wasmSetHeader(headers, "Idempotency-Key", it) }
        if (config.profile == HttpTransportProfile.SUITE_JSON &&
            request.method != HttpMethod.GET && request.method != HttpMethod.HEAD && request.method != HttpMethod.OPTIONS
        ) {
            val token = config.csrfTokenProvider?.token()?.takeIf { it.isNotBlank() }
                ?: throw HttpError.InvalidRequest("csrf_required")
            wasmSetHeader(headers, config.csrfHeaderName, token)
        }
        val url = if (config.baseUrl != null && !request.url.startsWith("http")) {
            "${config.baseUrl.trimEnd('/')}/${request.url.trimStart('/')}"
        } else {
            request.url
        }
        val timeout = (request.timeout.takeIf { it > 0 } ?: config.timeout)
            .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        val result = awaitWasmFetch(
            url = url,
            method = request.method.name,
            body = request.body ?: "",
            headers = headers,
            redirect = if (config.profile == HttpTransportProfile.SUITE_JSON) "error" else if (config.followRedirects) "follow" else "manual",
            credentials = "same-origin",
            timeout = timeout,
            limit = responseLimit
        )
        throwForWasmStatus(result.status, result.headersJson)
        val bytes = result.bytes.toByteArray()
        return HttpResponse(
            status = result.status,
            statusText = result.statusText,
            headers = selectedWasmHeaders(result.headersJson),
            body = bytes.decodeToString()
        )
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

private suspend fun awaitWasmFetch(
    url: String,
    method: String,
    body: String,
    headers: JsAny,
    redirect: String,
    credentials: String,
    timeout: Int,
    limit: Int
): WasmFetchResult = suspendCancellableCoroutine { continuation ->
    var completed = false
    val handle = wasmFetch(
        url, method, body, headers, redirect, credentials, timeout, limit,
        { status, statusText, headersJson, bytes ->
            if (!completed) {
                completed = true
                continuation.resume(WasmFetchResult(status, statusText, headersJson, bytes))
            }
        },
        { kind ->
            if (!completed) {
                completed = true
                continuation.resumeWithException(
                    when (kind) {
                        "timeout" -> HttpError.TimeoutError()
                        "redirect" -> HttpError.RedirectError()
                        "too-large" -> HttpError.ResponseTooLarge(limit)
                        else -> HttpError.NetworkError()
                    }
                )
            }
        }
    )
    continuation.invokeOnCancellation {
        completed = true
        wasmAbortFetch(handle)
    }
}

private fun JsAny.toByteArray(): ByteArray {
    val result = ByteArray(wasmByteLength(this))
    for (index in result.indices) result[index] = wasmByteAt(this, index).toByte()
    return result
}

private fun selectedWasmHeaders(json: String): Map<String, String> = buildMap {
    for (name in arrayOf("content-type", "content-length", "x-error-code", "x-request-id", "retry-after")) {
        wasmResponseHeader(json, name)?.let { put(name, it) }
    }
}

private fun safeWasmHeader(json: String, name: String): String? =
    wasmResponseHeader(json, name)
        ?.takeIf { it.length <= 128 && it.all { character -> character.isLetterOrDigit() || character in "._:-" } }

private fun throwForWasmStatus(status: Int, headersJson: String) {
    if (status in 200..299) return
    val errorCode = safeWasmHeader(headersJson, "x-error-code")
    val requestId = safeWasmHeader(headersJson, "x-request-id")
    val retryAfter = safeWasmHeader(headersJson, "retry-after")?.toLongOrNull()?.times(1_000)
    when (status) {
        in 400..499 -> throw HttpError.ClientError(status, errorCode, requestId, retryAfter)
        in 500..599 -> throw HttpError.ServerError(status, errorCode, requestId, retryAfter)
        else -> throw HttpError.UnknownError()
    }
}

actual fun createHttpClient(config: HttpClientConfig): HttpClient = HttpClient(config)

actual class CiphertextObjectTransport {
    actual suspend fun get(request: CiphertextObjectRequest): ByteArray {
        if (wasmNow().toLong() >= request.expiresAtEpochMillis) {
            throw HttpError.InvalidRequest("object_url_expired")
        }
        val headers = wasmHeaders()
        request.rangeEndInclusive?.let { wasmSetHeader(headers, "Range", "bytes=${request.rangeStart}-$it") }
        val result = awaitWasmFetch(
            url = request.url,
            method = "GET",
            body = "",
            headers = headers,
            redirect = "error",
            credentials = "omit",
            timeout = 30_000,
            limit = request.maxBytes
        )
        throwForWasmStatus(result.status, result.headersJson)
        return result.bytes.toByteArray()
    }
}

actual fun createCiphertextObjectTransport(): CiphertextObjectTransport = CiphertextObjectTransport()

actual fun toJson(obj: Any): String = wasmJson.encodeToString(obj)

actual inline fun <reified T> parseJson(json: String): T = Json.decodeFromString(json)

actual fun encodeURIComponent(value: String): String = wasmEncodeURIComponent(value)
