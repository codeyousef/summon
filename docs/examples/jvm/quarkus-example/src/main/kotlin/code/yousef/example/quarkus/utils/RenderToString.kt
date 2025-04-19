package code.yousef.example.quarkus.utils

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.JvmPlatformRenderer
// The renderComposable import has been removed because the extension function was causing issues.
// It was calling renderComposable without first setting up the context via renderComposableRoot,
// which was causing the error "Rendering function called outside of renderComposableRoot scope".
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import kotlinx.html.title

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
    fun basic(renderer: JvmPlatformRenderer, composable: @Composable () -> Unit): String {
        // Use renderComposableRoot instead of renderComposable to properly set up the rendering context
        return renderer.renderComposableRoot(composable)
    }

    /**
     * Render a composable to HTML string with metadata.
     */
    fun withMetadata(
        renderer: JvmPlatformRenderer, 
        metadata: PageMetadata,
        composable: @Composable () -> Unit
    ): String {
        // Create a wrapper composable that includes the metadata
        val wrappedComposable: @Composable () -> Unit = {
            // The metadata will be added by renderComposableRoot
            // Just call the original composable
            composable()
        }

        // Add head elements to the renderer
        if (metadata.title != null) {
            renderer.addHeadElement("<title>${metadata.title}</title>")
        }
        if (metadata.description != null) {
            renderer.addHeadElement("<meta name=\"description\" content=\"${metadata.description}\">")
        }

        // Add custom head elements
        metadata.customHeadElements.forEach { element ->
            renderer.addHeadElement(element)
        }

        // Use renderComposableRoot to properly set up the rendering context
        val result = renderer.renderComposableRoot(wrappedComposable)

        // Add doctype if needed
        return if (metadata.includeDocType && !result.startsWith("<!DOCTYPE")) {
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
    val renderer = JvmPlatformRenderer()
    return RenderToString.basic(renderer, content)
} 
