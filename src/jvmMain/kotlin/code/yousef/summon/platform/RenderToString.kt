package code.yousef.summon.platform

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Metadata for HTML pages
 */
data class PageMetadata(
    val title: String? = null,
    val description: String? = null,
    val includeDocType: Boolean = true,
    val customHeadElements: List<String> = emptyList()
)

/**
 * Functions for rendering composables to HTML strings.
 */
object RenderToString {
    /**
     * Render a composable to HTML string.
     */
    fun basic(renderer: PlatformRenderer, composable: @Composable () -> Unit): String {
        // Use renderComposableRoot instead of renderComposable to properly set up the rendering context
        val result = renderer.renderComposableRoot(composable)

        // Add doctype if needed
        return if (result.startsWith("<!DOCTYPE")) {
            result
        } else {
            result
        }
    }

    /**
     * Render a composable to HTML string with metadata.
     */
    fun withMetadata(
        renderer: PlatformRenderer,
        metadata: PageMetadata,
        composable: @Composable () -> Unit
    ): String {
        // Add head elements to the renderer
        if (metadata.title != null) {
            renderer.addHeadElement("<title>${metadata.title}</title>")
        }
        if (metadata.description != null) {
            renderer.addHeadElement("<meta name=\"description\" content=\"${metadata.description}\">")
        }

        // Add viewport and charset meta tags
        renderer.addHeadElement("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
        renderer.addHeadElement("<meta charset=\"UTF-8\">")

        // Add custom head elements
        metadata.customHeadElements.forEach { element ->
            renderer.addHeadElement(element)
        }

        // Use renderComposableRoot to properly set up the rendering context
        val result = renderer.renderComposableRoot(composable)

        // Add doctype if needed
        return if (metadata.includeDocType && result.startsWith("<!DOCTYPE")) {
            result
        } else if (metadata.includeDocType) {
            "<!DOCTYPE html>\n$result"
        } else {
            result
        }
    }
}

/**
 * Direct replacement for the original renderToString function.
 * This makes migration from the old API simpler.
 */
fun renderToString(content: @Composable () -> Unit): String {
    val renderer = PlatformRenderer()
    return RenderToString.basic(renderer, content)
} 
