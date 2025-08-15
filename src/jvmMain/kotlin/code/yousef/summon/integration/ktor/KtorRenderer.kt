package code.yousef.summon.integration.ktor

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
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
    val renderer = PlatformRenderer()

    /**
     * Renders a Summon composable function to an HTML string.
     *
     * @param content The composable content to render
     * @return HTML string output of the rendered content
     */
    fun renderToString(content: @Composable () -> Unit): String {
        // Set up the renderer
        setPlatformRenderer(renderer)

        // Render the component to a string
        return createHTML().html {
            head {
                meta(charset = "UTF-8")
                meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
            }
            body {
                // Render the content
                content()
            }
        }
    }

    /**
     * Renders a Summon composable function to an HTTP response.
     *
     * @param call The Ktor ApplicationCall to respond to
     * @param content The composable content to render
     */
    suspend fun renderHtml(call: ApplicationCall, content: @Composable () -> Unit) {
        setPlatformRenderer(renderer)

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
    }

    /**
     * Renders a Summon composable function as a streaming response.
     * This is useful for large pages or server-sent events.
     *
     * @param call The Ktor ApplicationCall to respond to
     * @param content The composable content to render
     */
    suspend fun renderStream(call: ApplicationCall, content: @Composable () -> Unit) {
        setPlatformRenderer(renderer)

        call.respondTextWriter(contentType = ContentType.Text.Html) {
            append("<!DOCTYPE html><html><head>")
            append("<meta charset=\"UTF-8\">")
            append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            append("<title>Summon Streaming App</title></head><body>")

            // Stream the content
            val html = createHTML().apply {
                content()
            }.toString()
            append(html)

            append("</body></html>")
            flush()
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
                    val renderer = KtorRenderer()
                    setPlatformRenderer(renderer.renderer)

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
            val renderer = KtorRenderer()
            setPlatformRenderer(renderer.renderer)

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
        }
    }
} 