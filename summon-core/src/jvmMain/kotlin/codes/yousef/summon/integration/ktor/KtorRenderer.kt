package codes.yousef.summon.integration.ktor

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.clearPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.html.stream.createHTML

/**
 * Integration class for rendering Summon components in a Ktor application.
 * This class provides methods to easily render Summon components to HTTP responses.
 *
 * IMPORTANT: To use this class, you must add the following dependencies to your project:
 * - io.ktor:ktor-server-core
 * - io.ktor:ktor-server-netty (or another engine)
 * - io.ktor:ktor-server-html-builder
 */
class KtorRenderer {

    /**
     * Renders a Summon composable function to an HTML string.
     *
     * @param content The composable content to render
     * @return HTML string output of the rendered content
     */
    fun renderToString(content: @Composable () -> Unit): String {
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        // Render the component to a string
        return try {
            createHTML().html {
                head {
                    meta(charset = "UTF-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                }
                body {
                    content()
                }
            }
        } finally {
            clearPlatformRenderer()
        }
    }

    /**
     * Renders a Summon composable function to an HTTP response.
     *
     * @param call The Ktor ApplicationCall to respond to
     * @param content The composable content to render
     */
    suspend fun renderHtml(call: ApplicationCall, content: @Composable () -> Unit) {
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        try {
            call.respondHtml {
                head {
                    meta(charset = "UTF-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                    title("Summon App")
                }
                body {
                    content()
                }
            }
        } finally {
            clearPlatformRenderer()
        }
    }

    /**
     * Renders a Summon composable function as a streaming response.
     * This is useful for large pages or server-sent events.
     *
     * @param call The Ktor ApplicationCall to respond to
     * @param content The composable content to render
     */
    suspend fun renderStream(call: ApplicationCall, content: @Composable () -> Unit) {
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        try {
            call.respondTextWriter(contentType = ContentType.Text.Html) {
                append("<!DOCTYPE html><html><head>")
                append("<meta charset=\"UTF-8\">")
                append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                append("<title>Summon Streaming App</title></head><body>")

                val html = createHTML().apply {
                    content()
                }.toString()
                append(html)

                append("</body></html>")
                flush()
            }
        } finally {
            clearPlatformRenderer()
        }
    }

    companion object {
        /**
         * Extension function to create a route that renders a Summon composable.
         * This function integrates with Ktor's routing.
         *
         * @param path The route path
         * @param method The HTTP method (default GET)
         * @param title The HTML page title
         * @param status The HTTP status code (default OK)
         * @param content The composable content to render
         */
        fun Route.summon(
            path: String,
            method: HttpMethod = HttpMethod.Get,
            title: String = "Summon App",
            status: HttpStatusCode = HttpStatusCode.OK,
            content: @Composable () -> Unit
        ) {
            route(path, method) {
                handle {
                    val renderer = PlatformRenderer()
                    setPlatformRenderer(renderer)

                    try {
                        call.respondHtml(status) {
                            head {
                                meta(charset = "UTF-8")
                                meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                                title(title)
                            }
                            body {
                                content()
                            }
                        }
                    } finally {
                        clearPlatformRenderer()
                    }
                }
            }
        }

        /**
         * Extension function to respond with a Summon component.
         * This function integrates with Ktor's ApplicationCall.
         *
         * @param title The HTML page title
         * @param status The HTTP status code (default OK)
         * @param content The composable content to render
         */
        suspend fun ApplicationCall.respondSummon(
            title: String = "Summon App",
            status: HttpStatusCode = HttpStatusCode.OK,
            content: @Composable () -> Unit
        ) {
            val renderer = PlatformRenderer()
            setPlatformRenderer(renderer)

            try {
                respondHtml(status) {
                    head {
                        meta(charset = "UTF-8")
                        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                        title(title)
                    }
                    body {
                        content()
                    }
                }
            } finally {
                clearPlatformRenderer()
            }
        }

        /**
         * Hydrated SSR response: renders a Summon component with hydration data/scripts.
         * Uses PlatformRenderer.renderComposableRootWithHydration to produce a full HTML document.
         * 
         * This method ensures callback context stability across coroutine thread switches,
         * which is critical for SSR hydration to work correctly. Without this, callbacks
         * registered during rendering may not match the callback IDs in the hydration data
         * sent to the client, causing onClick handlers and other interactive features to fail.
         */
        suspend fun ApplicationCall.respondSummonHydrated(
            status: HttpStatusCode = HttpStatusCode.OK,
            content: @Composable () -> Unit
        ) {
            val renderer = PlatformRenderer()
            setPlatformRenderer(renderer)
            
            // Create stable callback context for this request to ensure callbacks
            // registered during rendering can be reliably collected even if the
            // coroutine switches threads (common in Ktor with thread pools)
            val callbackContext = codes.yousef.summon.runtime.CallbackContextElement()
            
            try {
                // CRITICAL: Install callback context BEFORE rendering starts
                // withContext is not enough because renderComposableRootWithHydration is not suspend
                val html = kotlinx.coroutines.withContext(callbackContext) {
                    // The context is now properly installed in the thread-local before rendering
                    renderer.renderComposableRootWithHydration(content)
                }
                respondText(html, ContentType.Text.Html.withCharset(Charsets.UTF_8), status)
            } finally {
                clearPlatformRenderer()
            }
        }
        
        /**
         * Convenience alias for [respondSummonHydrated].
         * Renders a Summon page with full hydration support, automatically injecting
         * the hydration script and data for client-side interactivity.
         * 
         * Usage:
         * ```kotlin
         * get("/") {
         *     call.respondSummonPage { 
         *         MyLandingPage()
         *     }
         * }
         * ```
         */
        suspend fun ApplicationCall.respondSummonPage(
            status: HttpStatusCode = HttpStatusCode.OK,
            content: @Composable () -> Unit
        ) = respondSummonHydrated(status, content)
        
        /**
         * Serves Summon hydration assets (JS, WASM) directly from the library JAR.
         * This removes the need for users to manually extract and serve static files.
         * 
         * Assets are served at the following paths:
         * - `/summon-hydration.js` - JavaScript hydration client (for JS mode)
         * - `/summon-hydration.wasm` - WebAssembly module (stable name)
         * - `/summon-hydration.wasm.js` - WASM loader script
         * - `/{hash}.wasm` - Hashed WASM files (webpack generates these with content hashes)
         * 
         * Usage:
         * ```kotlin
         * routing {
         *     summonStaticAssets()
         *     // ... other routes
         * }
         * ```
         */
        fun Route.summonStaticAssets() {
            get("/summon-hydration.js") {
                call.respondSummonAsset("summon-hydration.js", ContentType.Application.JavaScript)
            }
            get("/summon-hydration.wasm") {
                call.respondSummonAsset("summon-hydration.wasm", ContentType("application", "wasm"))
            }
            get("/summon-hydration.wasm.js") {
                call.respondSummonAsset("summon-hydration.wasm.js", ContentType.Application.JavaScript)
            }
            // Serve hashed WASM files (webpack generates these with content hashes)
            get("/{filename}.wasm") {
                val filename = call.parameters["filename"]
                if (filename != null && filename.matches(Regex("^[a-f0-9]+$"))) {
                    call.respondSummonAsset("$filename.wasm", ContentType("application", "wasm"))
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            // Also serve from /summon-assets/ prefix for consistency
            route("/summon-assets") {
                get("/summon-hydration.js") {
                    call.respondSummonAsset("summon-hydration.js", ContentType.Application.JavaScript)
                }
                get("/summon-hydration.wasm") {
                    call.respondSummonAsset("summon-hydration.wasm", ContentType("application", "wasm"))
                }
                get("/summon-hydration.wasm.js") {
                    call.respondSummonAsset("summon-hydration.wasm.js", ContentType.Application.JavaScript)
                }
            }
        }
        
        /**
         * Handles callback execution requests from the hydration client.
         * This allows server-side callbacks to be triggered by client-side events.
         * 
         * Endpoints:
         * - `POST /summon/callback/{callbackId}` - Executes a registered callback
         * 
         * Response format (JSON):
         * - Success: `{"action":"reload","status":"ok"}`
         * - Not found: `{"action":"noop","status":"missing"}`
         * - Invalid: `{"action":"error","status":"missing-id"}`
         * 
         * Usage:
         * ```kotlin
         * routing {
         *     summonCallbackHandler()
         *     // ... other routes
         * }
         * ```
         */
        fun Route.summonCallbackHandler() {
            post("/summon/callback/{callbackId}") {
                val callbackId = call.parameters["callbackId"]
                if (callbackId.isNullOrBlank()) {
                    call.respondText(
                        """{"action":"error","status":"missing-id"}""",
                        ContentType.Application.Json,
                        HttpStatusCode.BadRequest
                    )
                } else {
                    val executed = CallbackRegistry.executeCallback(callbackId)
                    val (status, payload) = if (executed) {
                        HttpStatusCode.OK to """{"action":"reload","status":"ok"}"""
                    } else {
                        HttpStatusCode.NotFound to """{"action":"noop","status":"missing"}"""
                    }
                    call.respondText(payload, ContentType.Application.Json, status)
                }
            }
        }
        
        /**
         * Loads and responds with a Summon asset from the library JAR resources.
         */
        private suspend fun ApplicationCall.respondSummonAsset(name: String, contentType: ContentType) {
            val payload = loadSummonAsset(name)
            if (payload != null) {
                respondBytes(payload, contentType)
            } else {
                respondText(
                    """{"status":"not-found","asset":"$name"}""",
                    ContentType.Application.Json,
                    HttpStatusCode.NotFound
                )
            }
        }
        
        /**
         * Loads a Summon asset from the library JAR resources.
         * Searches in multiple locations for compatibility.
         */
        private fun loadSummonAsset(name: String): ByteArray? {
            val locations = listOf(
                "static/$name",
                "META-INF/resources/static/$name",
                "codes/yousef/summon/static/$name"
            )
            for (path in locations) {
                val resource = Thread.currentThread().contextClassLoader.getResourceAsStream(path)
                if (resource != null) {
                    return resource.use { it.readBytes() }
                }
            }
            return null
        }
    }
}
