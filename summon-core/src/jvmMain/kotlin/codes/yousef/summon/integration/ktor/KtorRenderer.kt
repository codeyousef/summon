package codes.yousef.summon.integration.ktor

import codes.yousef.summon.annotation.Composable
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
    }
}
