package code.yousef.summon.routing.seo

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.SideEffect


/**
 * OpenGraphTags component for adding social media metadata
 * These tags help social media platforms display rich previews when users share your content
 */
@Composable
fun OpenGraphTags(
    title: String,
    type: String = "website",
    url: String,
    image: String? = null,
    description: String? = null,
    siteName: String? = null,
    locale: String? = null,
    extraTags: Map<String, String> = emptyMap()
) {
    val composer = CompositionLocal.currentComposer

    // This is a head-only component, so we need to use a SideEffect to manipulate the head
    SideEffect {
        // TODO: Implement platform-specific head manipulation
        // For now, we'll just print what we would do
        println("OpenGraphTags SideEffect: Setting og:title='$title'")
        println("OpenGraphTags SideEffect: Setting og:type='$type'")
        println("OpenGraphTags SideEffect: Setting og:url='$url'")

        // In a real implementation, we would use the platform renderer to add these elements to the head
        // val renderer = getPlatformRenderer()
        // renderer.addHeadElement("<meta property=\"og:title\" content=\"$title\">")
        // renderer.addHeadElement("<meta property=\"og:type\" content=\"$type\">")
        // renderer.addHeadElement("<meta property=\"og:url\" content=\"$url\">")

        // Optional OG tags
        image?.let {
            println("OpenGraphTags SideEffect: Setting og:image='$it'")
            // renderer.addHeadElement("<meta property=\"og:image\" content=\"$it\">")
        }

        description?.let {
            println("OpenGraphTags SideEffect: Setting og:description='$it'")
            // renderer.addHeadElement("<meta property=\"og:description\" content=\"$it\">")
        }

        siteName?.let {
            println("OpenGraphTags SideEffect: Setting og:site_name='$it'")
            // renderer.addHeadElement("<meta property=\"og:site_name\" content=\"$it\">")
        }

        locale?.let {
            println("OpenGraphTags SideEffect: Setting og:locale='$it'")
            // renderer.addHeadElement("<meta property=\"og:locale\" content=\"$it\">")
        }

        // Add any additional OG tags
        extraTags.forEach { (name, content) ->
            println("OpenGraphTags SideEffect: Setting og:$name='$content'")
            // renderer.addHeadElement("<meta property=\"og:$name\" content=\"$content\">")
        }
    }
}

/**
 * Create OpenGraph tags for an article
 */
@Composable
fun OpenGraphArticle(
    title: String,
    url: String,
    image: String,
    description: String,
    publishedTime: String? = null,
    author: String? = null,
    siteName: String? = null
) {
    val extraTags = mutableMapOf<String, String>()
    publishedTime?.let { extraTags["article:published_time"] = it }
    author?.let { extraTags["article:author"] = it }

    OpenGraphTags(
        title = title,
        type = "article",
        url = url,
        image = image,
        description = description,
        siteName = siteName,
        extraTags = extraTags
    )
}

/**
 * Create OpenGraph tags for a product
 */
@Composable
fun OpenGraphProduct(
    title: String,
    url: String,
    image: String,
    description: String,
    price: String? = null,
    currency: String? = null,
    siteName: String? = null
) {
    val extraTags = mutableMapOf<String, String>()
    price?.let { extraTags["product:price:amount"] = it }
    currency?.let { extraTags["product:price:currency"] = it }

    OpenGraphTags(
        title = title,
        type = "product",
        url = url,
        image = image,
        description = description,
        siteName = siteName,
        extraTags = extraTags
    )
}

/**
 * Composable function to add an Open Graph (OG) meta tag to the document head.
 * Used for social media sharing previews.
 *
 * @param property The OG property name (e.g., "og:title", "og:image").
 * @param content The content value for the OG property.
 */
@Composable
fun OpenGraphTag(property: String, content: String) {
    val composer = CompositionLocal.currentComposer

    SideEffect {
        println("OpenGraphTag SideEffect: Setting property='$property' content='$content'")
        // TODO: Implement platform-specific head manipulation.
        // PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer().addHeadElement("<meta property=\"$property\" content=\"$content\">")
    }

    // Renders no UI.
}

// Convenience functions for common OG tags
@Composable
fun OgTitle(title: String) = OpenGraphTag("og:title", title)

@Composable
fun OgDescription(description: String) = OpenGraphTag("og:description", description)

@Composable
fun OgUrl(url: String) = OpenGraphTag("og:url", url)

@Composable
fun OgImage(imageUrl: String) = OpenGraphTag("og:image", imageUrl)

@Composable
fun OgType(type: String) = OpenGraphTag("og:type", type) // e.g., "website", "article"

@Composable
fun OgSiteName(name: String) = OpenGraphTag("og:site_name", name)
