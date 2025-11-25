package codes.yousef.summon.integration.springboot

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.clearPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

/**
 * Integration class for rendering Summon components in a Spring Boot application.
 * This class provides methods to easily render Summon components to HTTP responses.
 *
 * IMPORTANT: To use this class, you must add the following dependencies to your project:
 * - org.springframework.boot:spring-boot-starter-web
 * - org.springframework.boot:spring-boot-starter-thymeleaf (optional, for template integration)
 */
class SpringBootRenderer {

    /**
     * Renders a Summon composable function to a Spring ResponseEntity.
     *
     * @param content The composable content to render
     * @param title Optional title for the HTML page
     * @param status HTTP status code to use in the response
     * @return ResponseEntity containing the rendered HTML
     */
    fun render(
        title: String = "Summon App",
        status: HttpStatus = HttpStatus.OK,
        content: @Composable () -> Unit
    ): ResponseEntity<String> {
        val renderer = PlatformRenderer()
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

        return ResponseEntity.status(status)
            .contentType(MediaType.TEXT_HTML)
            .body(html)
    }

    /**
     * Renders a Summon composable function directly to the HttpServletResponse.
     * Useful when you need to directly access the response in a controller.
     *
     * @param response The HttpServletResponse to write to
     * @param content The composable content to render
     * @param title Optional title for the HTML page
     */
    fun renderToResponse(
        response: HttpServletResponse,
        title: String = "Summon App",
        content: @Composable () -> Unit
    ) {
        val renderer = PlatformRenderer()
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

        // Write the response
        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.TEXT_HTML_VALUE
        response.writer.use {
            it.write(html)
        }
    }

    /**
     * Renders a Summon composable function to HttpServletResponse.
     * Simplified version of renderToResponse.
     *
     * @param response The HttpServletResponse to write to
     * @param content The composable content to render
     */
    fun renderHtml(
        response: HttpServletResponse,
        content: @Composable () -> Unit
    ) {
        renderToResponse(response, "Summon App", content)
    }

    /**
     * Renders a Summon composable with hydration metadata inside a ResponseEntity.
     */
    fun renderHydrated(
        status: HttpStatus = HttpStatus.OK,
        content: @Composable () -> Unit
    ): ResponseEntity<String> {
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        val html = try {
            renderer.renderComposableRootWithHydration(content)
        } finally {
            clearPlatformRenderer()
        }
        return ResponseEntity.status(status)
            .contentType(MediaType.TEXT_HTML)
            .body(html)
    }

    /**
     * Writes a hydrated Summon response directly to the servlet response.
     */
    fun renderHydratedToResponse(
        response: HttpServletResponse,
        status: HttpStatus = HttpStatus.OK,
        content: @Composable () -> Unit
    ) {
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
        val html = try {
            renderer.renderComposableRootWithHydration(content)
        } finally {
            clearPlatformRenderer()
        }
        response.status = status.value()
        response.contentType = MediaType.TEXT_HTML_VALUE
        response.writer.use { it.write(html) }
    }

    /**
     * Renders a Summon composable function as a streaming response.
     * This is useful for large pages or server-sent events.
     *
     * @param response The HttpServletResponse to write to
     * @param content The composable content to render
     */
    fun renderStream(
        response: HttpServletResponse,
        content: @Composable () -> Unit
    ) {
        // Set up the renderer
        // Set response headers
        response.contentType = "text/html; charset=UTF-8"

        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        val writer = response.writer
        try {
            writer.write("<!DOCTYPE html><html><head>")
            writer.write("<meta charset=\"UTF-8\">")
            writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            writer.write("<title>Summon Streaming App</title></head><body>")
            writer.flush()

            val html = buildString {
                appendHTML().apply {
                    content()
                }
            }

            writer.write(html)
            writer.write("</body></html>")
            writer.flush()
        } finally {
            clearPlatformRenderer()
        }
    }

    companion object {
        /**
         * Extension function to get a SpringBootRenderer for the current request.
         */
        fun getCurrentRenderer(): SpringBootRenderer {
            return SpringBootRenderer()
        }
        
        /**
         * Handles requests for Summon hydration assets from the library JAR.
         * Use this method in a Spring controller to serve assets.
         * 
         * Example usage in a controller:
         * ```kotlin
         * @GetMapping("/summon-hydration.js", "/summon-hydration.wasm", "/summon-hydration.wasm.js")
         * fun summonAssets(request: HttpServletRequest, response: HttpServletResponse) {
         *     SpringBootRenderer.handleSummonAsset(request, response)
         * }
         * ```
         */
        fun handleSummonAsset(request: HttpServletRequest, response: HttpServletResponse) {
            val path = request.servletPath
            val assetName = path.substringAfterLast('/')
            
            val contentType = when {
                assetName.endsWith(".wasm") && !assetName.endsWith(".wasm.js") -> "application/wasm"
                assetName.endsWith(".js") -> "application/javascript"
                else -> "application/octet-stream"
            }
            
            val payload = loadSummonAsset(assetName)
            if (payload != null) {
                response.status = HttpStatus.OK.value()
                response.contentType = contentType
                response.outputStream.use { it.write(payload) }
            } else {
                response.status = HttpStatus.NOT_FOUND.value()
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                response.writer.write("""{"status":"not-found","asset":"$assetName"}""")
            }
        }
        
        /**
         * Returns a ResponseEntity for a Summon asset.
         * 
         * Example usage:
         * ```kotlin
         * @GetMapping("/summon-hydration.js")
         * fun summonJs(): ResponseEntity<ByteArray> = SpringBootRenderer.getSummonAsset("summon-hydration.js")
         * ```
         */
        fun getSummonAsset(name: String): ResponseEntity<ByteArray> {
            val contentType = when {
                name.endsWith(".wasm") && !name.endsWith(".wasm.js") -> MediaType("application", "wasm")
                name.endsWith(".js") -> MediaType.parseMediaType("application/javascript")
                else -> MediaType.APPLICATION_OCTET_STREAM
            }
            
            val payload = loadSummonAsset(name)
            return if (payload != null) {
                ResponseEntity.ok()
                    .contentType(contentType)
                    .body(payload)
            } else {
                ResponseEntity.notFound().build()
            }
        }
        
        /**
         * Handles callback execution requests from the hydration client.
         * 
         * Example usage in a controller:
         * ```kotlin
         * @PostMapping("/summon/callback/{callbackId}")
         * fun callback(@PathVariable callbackId: String): ResponseEntity<String> {
         *     return SpringBootRenderer.handleCallback(callbackId)
         * }
         * ```
         */
        fun handleCallback(callbackId: String?): ResponseEntity<String> {
            if (callbackId.isNullOrBlank()) {
                return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""{"action":"error","status":"missing-id"}""")
            }
            
            val executed = CallbackRegistry.executeCallback(callbackId)
            return if (executed) {
                ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""{"action":"reload","status":"ok"}""")
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""{"action":"noop","status":"missing"}""")
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
