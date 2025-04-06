package code.yousef.summon.routing.seo

import code.yousef.summon.annotation.Composable
import kotlinx.html.HEAD
import kotlinx.html.meta
import kotlinx.html.title

/**
 * Composable function for easily configuring page metadata within the HTML `<head>`.
 *
 * Note: This composable should only be used within a scope that provides a `HEAD` context
 * (e.g., inside a `Head` composable or a similar structure).
 *
 * @param title The page title.
 * @param description Meta description for search engines.
 * @param keywords Keywords for search engines (comma separated).
 * @param author Content author.
 * @param viewport Viewport settings (defaults to responsive configuration).
 * @param charset Character set (defaults to UTF-8).
 * @param extraTags Map of additional meta tags (name/content pairs).
 */
@Composable
fun MetaTags(
    title: String? = null,
    description: String? = null,
    keywords: String? = null,
    author: String? = null,
    viewport: String = "width=device-width, initial-scale=1.0",
    charset: String = "UTF-8",
    extraTags: Map<String, String> = emptyMap()
) {
    // Access the HEAD receiver provided by the composition context
    // This assumes the composable is called within a HEAD scope.
    // The actual mechanism for accessing the receiver needs composer integration.
    // Placeholder using a hypothetical function:
    // val headReceiver = currentComposer.consume<HEAD>() ?: error("MetaTags must be used within a HEAD scope")

    // Using 'this' assuming the context provides HEAD receiver (requires changes to composer)
    // headReceiver.apply { ... }
    // For now, the code below won't compile standalone without the context.
    
    // Example of direct emission (needs HEAD context provided by composer):
    // Ensure this function is called within a kotlinx.html.HEAD block.
    
    // Set page title if provided
    title?.let { title(it) }

    // Add charset tag
    meta(charset = charset)

    // Add viewport
    meta(name = "viewport", content = viewport)

    // Add basic SEO meta tags if provided
    description?.let { meta(name = "description", content = it) }
    keywords?.let { meta(name = "keywords", content = it) }
    author?.let { meta(name = "author", content = it) }

    // Add any additional meta tags
    extraTags.forEach { (name, content) ->
        meta(name = name, content = content)
    }
} 