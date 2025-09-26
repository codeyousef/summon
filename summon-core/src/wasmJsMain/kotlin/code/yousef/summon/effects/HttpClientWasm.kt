package code.yousef.summon.effects

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError

actual class HttpClient {
    actual suspend fun execute(request: HttpRequest): HttpResponse {
        wasmConsoleLog("HTTP ${request.method} request to: ${request.url} - WASM stub")
        return HttpResponse(
            status = 200,
            statusText = "OK",
            headers = emptyMap(),
            body = "{\"status\": \"success\", \"data\": \"WASM stub response\"}"
        )
    }

    actual suspend fun get(url: String, headers: Map<String, String>): HttpResponse {
        wasmConsoleLog("HTTP GET request to: $url - WASM stub")
        return HttpResponse(
            status = 200,
            statusText = "OK",
            headers = emptyMap(),
            body = "{\"status\": \"success\", \"data\": \"WASM stub response\"}"
        )
    }

    actual suspend fun post(url: String, body: String, headers: Map<String, String>): HttpResponse {
        wasmConsoleLog("HTTP POST request to: $url with body: $body - WASM stub")
        return HttpResponse(
            status = 200,
            statusText = "OK",
            headers = emptyMap(),
            body = "{\"status\": \"success\", \"data\": \"WASM stub response\"}"
        )
    }

    actual suspend fun put(url: String, body: String, headers: Map<String, String>): HttpResponse {
        wasmConsoleLog("HTTP PUT request to: $url with body: $body - WASM stub")
        return HttpResponse(
            status = 200,
            statusText = "OK",
            headers = emptyMap(),
            body = "{\"status\": \"success\", \"data\": \"WASM stub response\"}"
        )
    }

    actual suspend fun delete(url: String, headers: Map<String, String>): HttpResponse {
        wasmConsoleLog("HTTP DELETE request to: $url - WASM stub")
        return HttpResponse(
            status = 200,
            statusText = "OK",
            headers = emptyMap(),
            body = "{\"status\": \"success\", \"data\": \"WASM stub response\"}"
        )
    }

    actual suspend fun patch(url: String, body: String, headers: Map<String, String>): HttpResponse {
        wasmConsoleLog("HTTP PATCH request to: $url with body: $body - WASM stub")
        return HttpResponse(
            status = 200,
            statusText = "OK",
            headers = emptyMap(),
            body = "{\"status\": \"success\", \"data\": \"WASM stub response\"}"
        )
    }
}

actual fun createHttpClient(config: HttpClientConfig): HttpClient {
    wasmConsoleLog("Creating HTTP client with config - WASM stub")
    return HttpClient()
}

actual fun toJson(obj: Any): String {
    wasmConsoleLog("Converting object to JSON - WASM stub")
    return "{}"
}

actual inline fun <reified T> parseJson(json: String): T {
    wasmConsoleLog("Parsing JSON: $json - WASM stub")
    throw UnsupportedOperationException("JSON parsing not implemented in WASM")
}

actual fun encodeURIComponent(value: String): String {
    wasmConsoleLog("Encoding URI component: $value - WASM stub")
    // Simple URL encoding for WASM stub
    return value.replace(" ", "%20").replace("&", "%26").replace("=", "%3D")
}

