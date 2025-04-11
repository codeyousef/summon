package code.yousef.summon.integration.springboot

import code.yousef.summon.platform.JvmPlatformRenderer
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.setPlatformRenderer
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import jakarta.servlet.http.HttpServletResponse

/**
 * Integration class for rendering Summon components in a Spring Boot application.
 * This class provides methods to easily render Summon components to HTTP responses.
 *
 * IMPORTANT: To use this class, you must add the following dependencies to your project:
 * - org.springframework.boot:spring-boot-starter-web
 * - org.springframework.boot:spring-boot-starter-thymeleaf (optional, for template integration)
 */
class SpringBootRenderer {
    private val renderer = JvmPlatformRenderer()

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
        // Set up the renderer
        setPlatformRenderer(renderer)

        // Create HTML output
        val html = buildString {
            appendHTML().html {
                // Basic HTML structure
                head {
                    meta(charset = "UTF-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                    title(title)
                }
                body {
                    // Render the content
                    content()
                }
            }
        }

        // Return the response entity
        return ResponseEntity.status(status)
            .header("Content-Type", "text/html; charset=UTF-8")
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
        // Set up the renderer
        setPlatformRenderer(renderer)

        // Create HTML output
        val html = buildString {
            appendHTML().html {
                // Basic HTML structure
                head {
                    meta(charset = "UTF-8")
                    meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
                    title(title)
                }
                body {
                    // Render the content
                    content()
                }
            }
        }

        // Write the response
        response.contentType = "text/html; charset=UTF-8"
        response.writer.write(html)
        response.writer.flush()
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
        setPlatformRenderer(renderer)
        
        // Set response headers
        response.contentType = "text/html; charset=UTF-8"
        
        // Write the HTML header
        val writer = response.writer
        writer.write("<!DOCTYPE html><html><head>")
        writer.write("<meta charset=\"UTF-8\">")
        writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
        writer.write("<title>Summon Streaming App</title></head><body>")
        writer.flush()
        
        // Create HTML content
        val html = buildString {
            appendHTML().apply {
                content()
            }
        }
        
        // Stream the content
        writer.write(html)
        writer.write("</body></html>")
        writer.flush()
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