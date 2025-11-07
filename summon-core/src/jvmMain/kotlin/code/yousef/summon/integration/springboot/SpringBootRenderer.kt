package code.yousef.summon.integration.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
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
    }
} 
