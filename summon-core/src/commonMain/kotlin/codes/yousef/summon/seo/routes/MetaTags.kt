package codes.yousef.summon.seo.routes

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.getPlatformRenderer

/**
 * MetaTags component for easily configuring page metadata in HTML head
 *
 * @param title The page title
 * @param description Meta description for search engines
 * @param keywords Keywords for search engines (comma separated)
 * @param author Content author
 * @param viewport Viewport settings (defaults to responsive configuration)
 * @param charset Character set (defaults to UTF-8)
 * @param extraTags Map of additional meta tags (name/content pairs)
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
    val renderer = getPlatformRenderer()

    // Add title if provided
    title?.let {
        renderer.addHeadElement("<title>$it</title>")
    }

    // Add charset meta tag
    renderer.addHeadElement("<meta charset=\"$charset\">")

    // Add viewport meta tag
    renderer.addHeadElement("<meta name=\"viewport\" content=\"$viewport\">")

    // Add description meta tag if provided
    description?.let {
        renderer.addHeadElement("<meta name=\"description\" content=\"$it\">")
    }

    // Add keywords meta tag if provided
    keywords?.let {
        renderer.addHeadElement("<meta name=\"keywords\" content=\"$it\">")
    }

    // Add author meta tag if provided
    author?.let {
        renderer.addHeadElement("<meta name=\"author\" content=\"$it\">")
    }

    // Add any additional meta tags
    extraTags.forEach { (name, content) ->
        renderer.addHeadElement("<meta name=\"$name\" content=\"$content\">")
    }
}

/**
 * Create MetaTags with common metadata
 *
 * @param title The page title
 * @param description Meta description for search engines
 * @param keywords Keywords for search engines (comma separated)
 * @param author Content author
 */
@Composable
fun StandardMetaTags(
    title: String,
    description: String,
    keywords: String? = null,
    author: String? = null
) {
    MetaTags(
        title = title,
        description = description,
        keywords = keywords,
        author = author
    )
}
