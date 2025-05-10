package code.yousef.summon.integration.quarkus

import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.setPlatformRenderer
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.RoutingContext
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

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
     * Renders a Summon composable function to the HTTP response.
     *
     * @param content The composable content to render
     * @param title Optional title for the HTML page
     */
    fun render(title: String = "Summon App", content: @Composable () -> Unit) {
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

        // Send the response
        response
            .putHeader("Content-Type", "text/html; charset=UTF-8")
            .end(html)
    }

    companion object {
        /**
         * Extension function for RoutingContext to easily create a QuarkusRenderer
         */
        fun RoutingContext.summonRenderer(title: String = "Summon App"): QuarkusRenderer {
            return QuarkusRenderer(response())
        }

        /**
         * Example usage in a Quarkus route handler:
         *
         * ```
         * @Route(path = "/example")
         * fun exampleRoute(context: RoutingContext) {
         *     context.summonRenderer("Example Page").render {
         *         Text("Hello from Summon!")
         *     }
         * }
         * ```
         */
    }
} 