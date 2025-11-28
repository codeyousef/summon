package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.clearPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.io.ByteArrayOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.GZIPOutputStream

/**
 * Integration class for rendering Summon components in a Quarkus application.
 * This class provides methods to easily render Summon components to HTTP responses.
 *
 * IMPORTANT: To use this class, you must add the following dependencies to your project:
 * - io.quarkus:quarkus-core
 * - io.quarkus:quarkus-vertx-web
 * - io.quarkus:quarkus-kotlin
 */
class QuarkusRenderer(private val response: HttpServerResponse) {
    private val renderer = PlatformRenderer()

    /**
     * Renders a Summon composable function to the HTTP response as static HTML.
     *
     * @param content The composable content to render
     * @param title Optional title for the HTML page
     */
    fun render(
        title: String = "Summon App",
        statusCode: Int = 200,
        content: @Composable () -> Unit
    ) {
        setPlatformRenderer(renderer)
        val html = try {
            buildString {
                appendHTML().html {
                    head {
                        meta(charset = "UTF-8")
                        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                        title(title)
                    }
                    body {
                        content()
                    }
                }
            }
        } finally {
            clearPlatformRenderer()
        }
        sendHtml(html, statusCode)
    }

    /**
     * Renders a Summon composable function with hydration metadata so the client
     * can seamlessly attach interactive behavior.
     *
     * @param statusCode HTTP status code to use for the response
     * @param content Composable content to render
     */
    fun renderHydrated(statusCode: Int = 200, content: @Composable () -> Unit) {
        setPlatformRenderer(renderer)
        val html = try {
            renderer.renderComposableRootWithHydration(content)
        } finally {
            clearPlatformRenderer()
        }
        sendHtml(html, statusCode)
    }

    private fun sendHtml(html: String, statusCode: Int = 200) {
        response
            .setStatusCode(statusCode)
            .putHeader("Content-Type", "text/html; charset=UTF-8")
            .end(html)
    }

    companion object {
        /**
         * Extension function for RoutingContext to easily create a QuarkusRenderer.
         */
        fun RoutingContext.summonRenderer(title: String = "Summon App"): QuarkusRenderer {
            // Title is retained for API compatibility; use render()/renderHydrated() to apply it.
            return QuarkusRenderer(response())
        }

        /**
         * Hydrated SSR helper to respond with Summon content including hydration data/scripts.
         *
         * Example usage:
         * ```
         * @Route(path = "/example")
         * fun exampleRoute(context: RoutingContext) {
         *     context.respondSummonHydrated {
         *         HomePage()
         *     }
         * }
         * ```
         */
        fun RoutingContext.respondSummonHydrated(
            statusCode: Int = 200,
            content: @Composable () -> Unit
        ) {
            QuarkusRenderer(response()).renderHydrated(statusCode, content)
        }
        
        /**
         * Convenience alias for [respondSummonHydrated].
         * Renders a Summon page with full hydration support.
         */
        fun RoutingContext.respondSummonPage(
            statusCode: Int = 200,
            content: @Composable () -> Unit
        ) = respondSummonHydrated(statusCode, content)
        
        /**
         * Registers routes to serve Summon hydration assets from the library JAR.
         * 
         * Assets are served at:
         * - `/summon-hydration.js` - JavaScript hydration client (for JS mode)
         * - `/summon-hydration.wasm` - WebAssembly module (stable name)
         * - `/summon-hydration.wasm.js` - WASM loader script
         * - `/{hash}.wasm` - Hashed WASM files (webpack generates these with content hashes)
         * 
         * Usage:
         * ```kotlin
         * router.summonStaticAssets()
         * ```
         */
        fun Router.summonStaticAssets() {
            get("/summon-hydration.js").handler { ctx ->
                ctx.respondSummonAsset("summon-hydration.js", "application/javascript")
            }
            get("/summon-hydration.wasm").handler { ctx ->
                ctx.respondSummonAsset("summon-hydration.wasm", "application/wasm")
            }
            get("/summon-hydration.wasm.js").handler { ctx ->
                ctx.respondSummonAsset("summon-hydration.wasm.js", "application/javascript")
            }
            // Serve hashed WASM files (webpack generates these with content hashes)
            getWithRegex("/([a-f0-9]+)\\.wasm").handler { ctx ->
                val filename = ctx.pathParam("param0")
                if (filename != null) {
                    ctx.respondSummonAsset("$filename.wasm", "application/wasm")
                } else {
                    ctx.response().setStatusCode(404).end()
                }
            }
            // Also serve from /summon-assets/ prefix
            get("/summon-assets/summon-hydration.js").handler { ctx ->
                ctx.respondSummonAsset("summon-hydration.js", "application/javascript")
            }
            get("/summon-assets/summon-hydration.wasm").handler { ctx ->
                ctx.respondSummonAsset("summon-hydration.wasm", "application/wasm")
            }
            get("/summon-assets/summon-hydration.wasm.js").handler { ctx ->
                ctx.respondSummonAsset("summon-hydration.wasm.js", "application/javascript")
            }
        }
        
        /**
         * Registers the callback handler route for hydration callbacks.
         * 
         * Endpoint: `POST /summon/callback/:callbackId`
         * 
         * Usage:
         * ```kotlin
         * router.summonCallbackHandler()
         * ```
         */
        fun Router.summonCallbackHandler() {
            post("/summon/callback/:callbackId").handler { ctx ->
                val callbackId = ctx.pathParam("callbackId")
                if (callbackId.isNullOrBlank()) {
                    ctx.response()
                        .setStatusCode(400)
                        .putHeader("Content-Type", "application/json")
                        .end("""{"action":"error","status":"missing-id"}""")
                } else {
                    val executed = CallbackRegistry.executeCallback(callbackId)
                    val (statusCode, payload) = if (executed) {
                        200 to """{"action":"reload","status":"ok"}"""
                    } else {
                        404 to """{"action":"noop","status":"missing"}"""
                    }
                    ctx.response()
                        .setStatusCode(statusCode)
                        .putHeader("Content-Type", "application/json")
                        .end(payload)
                }
            }
        }
        
        // Cache for compressed assets
        private val compressedAssetCache = ConcurrentHashMap<String, ByteArray>()
        private val rawAssetCache = ConcurrentHashMap<String, ByteArray>()

        /**
         * Responds with a Summon asset from the library JAR.
         * Supports gzip compression and caching for optimal performance.
         */
        private fun RoutingContext.respondSummonAsset(name: String, contentType: String) {
            val payload = loadSummonAssetCached(name)
            if (payload == null) {
                response()
                    .setStatusCode(404)
                    .putHeader("Content-Type", "application/json")
                    .end("""{"status":"not-found","asset":"$name"}""")
                return
            }

            val resp = response()
                .setStatusCode(200)
                .putHeader("Content-Type", contentType)
                .putHeader("Cache-Control", "public, max-age=31536000, immutable")
                .putHeader("Vary", "Accept-Encoding")

            // Check if client accepts gzip
            val acceptEncoding = request().getHeader("Accept-Encoding") ?: ""
            if (acceptEncoding.contains("gzip") && !name.endsWith(".wasm")) {
                val compressed = getCompressedAsset(name, payload)
                resp.putHeader("Content-Encoding", "gzip")
                    .end(io.vertx.core.buffer.Buffer.buffer(compressed))
            } else {
                resp.end(io.vertx.core.buffer.Buffer.buffer(payload))
            }
        }

        /**
         * Gets or creates a gzip-compressed version of an asset.
         */
        private fun getCompressedAsset(name: String, original: ByteArray): ByteArray {
            return compressedAssetCache.getOrPut(name) {
                ByteArrayOutputStream().use { baos ->
                    GZIPOutputStream(baos).use { gzos ->
                        gzos.write(original)
                    }
                    baos.toByteArray()
                }
            }
        }

        /**
         * Loads a Summon asset with caching.
         */
        private fun loadSummonAssetCached(name: String): ByteArray? {
            return rawAssetCache.getOrPut(name) {
                loadSummonAsset(name) ?: return null
            }
        }

        /**
         * Loads a Summon asset from the library JAR resources.
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
