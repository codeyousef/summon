package code.yousef.summon.integration.quarkus

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
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
    }
}
