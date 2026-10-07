package codes.yousef.summon.integration.quarkus.renderer

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.foundation.TrustedHtml
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.clearPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
import org.jboss.logging.Logger

/**
 * A renderer that is aware of HTMX attributes and raw HTML content.
 * This class wraps a JvmPlatformRenderer and processes modifiers to handle HTMX attributes
 * and raw HTML content properly.
 */
class HtmxAwareRenderer {
    private val logger = Logger.getLogger(HtmxAwareRenderer::class.java)
    private val platformRenderer = EnhancedJvmPlatformRenderer()

    /**
     * Renders a composable component to a string, handling HTMX attributes and raw HTML content.
     *
     * @param content The composable content to render
     * @return The rendered HTML as a string
     */
    fun renderToString(content: @Composable () -> Unit): String {
        logger.debug("HtmxAwareRenderer.renderToString() - Starting to render composable content")

        return try {
            setPlatformRenderer(platformRenderer)
            // Use the JvmPlatformRenderer to render the composable content
            logger.debug("HtmxAwareRenderer.renderToString() - Using platformRenderer.renderComposableRoot")
            val html = platformRenderer.renderComposableRoot {
                // Provide the platform renderer to the LocalPlatformRenderer
                val provided = LocalPlatformRenderer.provides(platformRenderer)
                provided.run {
                    content()
                }
            }

            logger.debug("HtmxAwareRenderer.renderToString() - Composable content rendered, HTML length: ${html.length}")

            html
        } catch (e: Exception) {
            logger.error("HtmxAwareRenderer.renderToString() - Error rendering composable content", e)
            throw e
        } finally {
            clearPlatformRenderer()
        }
    }


    /**
     * Renders a template with the given title and content.
     *
     * @param title The page title
     * @param content The HTML content to include in the template
     * @return The rendered HTML as a string
     */
    fun renderTemplate(title: String, content: TrustedHtml): String {
        logger.debug("HtmxAwareRenderer.renderTemplate() - Rendering trusted author template")
        val escapedTitle = escapeHtmlText(title)
        return """
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="description" content="A Summon application with Quarkus">
                    <title>$escapedTitle</title>
                </head>
                <body>
                    <main id="app">${content.value}</main>
                </body>
            </html>
        """.trimIndent()
    }

    private fun escapeHtmlText(value: String): String = buildString(value.length) {
        value.forEach { character ->
            when (character) {
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                '"' -> append("&quot;")
                '\'' -> append("&#39;")
                else -> append(character)
            }
        }
    }

    /**
     * Creates a composable function that renders raw HTML content.
     *
     * @param html The HTML content to render
     * @param modifier Additional modifiers to apply
     */
    @Composable
    fun RawHtml(html: TrustedHtml, modifier: Modifier = Modifier()) {
        val renderer = LocalPlatformRenderer.current
        renderer.renderBox(modifier) {
            renderer.renderRawHtml(html)
        }
    }
}
