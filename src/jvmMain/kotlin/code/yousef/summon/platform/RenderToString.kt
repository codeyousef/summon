package code.yousef.summon.platform

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.JvmPlatformRenderer
import kotlinx.html.*
import kotlinx.html.stream.createHTML

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
        val docType = "<!DOCTYPE html>\n"
        val consumer = createHTML()
        val htmlString = consumer.html {
            body {
                renderer.renderComposable(composable)
            }
        }
        return docType + htmlString
    }
    
    /**
     * Render a composable to HTML string with metadata.
     */
    fun withMetadata(
        renderer: JvmPlatformRenderer,
        metadata: PageMetadata,
        composable: @Composable () -> Unit
    ): String {
        val docTypeString = if (metadata.includeDocType) "<!DOCTYPE html>\n" else ""
        val consumer = createHTML()
        val htmlString = consumer.html {
            head {
                metadata.title?.let { title(it) }
                metadata.description?.let {
                    meta {
                        name = "description"
                        content = it
                    }
                }
                
                meta {
                    name = "viewport"
                    content = "width=device-width, initial-scale=1.0"
                }
                meta {
                    charset = "UTF-8"
                }
                
                // Add custom head elements
                metadata.customHeadElements.forEach { element ->
                    unsafe { +element }
                }
                
                // Add head elements from the renderer
                renderer.getHeadElements().forEach { element ->
                    unsafe { +element }
                }
            }
            body {
                renderer.renderComposable(composable)
            }
        }
        return docTypeString + htmlString
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