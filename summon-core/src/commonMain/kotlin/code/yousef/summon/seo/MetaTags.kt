package code.yousef.summon.seo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Composable component for rendering standard HTML meta tags in a type-safe way.
 * Provides a Kotlin-idiomatic API for SEO metadata.
 *
 * @param title The page title
 * @param description The page description for SEO
 * @param keywords List of keywords for SEO
 * @param author The author of the page
 * @param viewport Viewport settings for responsive design
 * @param charset Character encoding (default: UTF-8)
 * @param robots Search engine crawling instructions
 * @param canonical Canonical URL to avoid duplicate content
 * @param themeColor Browser theme color
 * @param customTags Additional custom meta tags as key-value pairs
 */
@Composable
fun MetaTags(
    title: String? = null,
    description: String? = null,
    keywords: List<String>? = null,
    author: String? = null,
    viewport: String = "width=device-width, initial-scale=1.0",
    charset: String = "UTF-8",
    robots: String? = null,
    canonical: String? = null,
    themeColor: String? = null,
    customTags: Map<String, String> = emptyMap()
) {
    val renderer = LocalPlatformRenderer.current

    renderer.renderHeadElements {
        // Charset should come first
        meta(charset = charset)

        // Title
        title?.let { title(it) }

        // Viewport for responsive design
        meta(name = "viewport", content = viewport)

        // Standard meta tags
        description?.let { meta(name = "description", content = it) }
        keywords?.takeIf { it.isNotEmpty() }?.let {
            meta(name = "keywords", content = it.joinToString(", "))
        }
        author?.let { meta(name = "author", content = it) }
        robots?.let { meta(name = "robots", content = it) }

        // Theme color for mobile browsers
        themeColor?.let { meta(name = "theme-color", content = it) }

        // Canonical URL
        canonical?.let { link(rel = "canonical", href = it) }

        // Custom meta tags
        customTags.forEach { (name, content) ->
            meta(name = name, content = content)
        }
    }
}

/**
 * Convenience function for basic meta tags setup.
 */
@Composable
fun BasicMetaTags(
    title: String,
    description: String
) {
    MetaTags(
        title = title,
        description = description
    )
}

/**
 * Meta tags for a blog post or article.
 */
@Composable
fun ArticleMetaTags(
    title: String,
    description: String,
    author: String,
    publishedTime: String? = null,
    modifiedTime: String? = null,
    keywords: List<String>? = null,
    canonical: String? = null
) {
    MetaTags(
        title = title,
        description = description,
        author = author,
        keywords = keywords,
        canonical = canonical,
        customTags = buildMap {
            publishedTime?.let { put("article:published_time", it) }
            modifiedTime?.let { put("article:modified_time", it) }
        }
    )
}

/**
 * Meta tags for a product page.
 */
@Composable
fun ProductMetaTags(
    title: String,
    description: String,
    price: String? = null,
    currency: String? = null,
    availability: String? = null,
    brand: String? = null,
    keywords: List<String>? = null,
    canonical: String? = null
) {
    MetaTags(
        title = title,
        description = description,
        keywords = keywords,
        canonical = canonical,
        customTags = buildMap {
            price?.let { put("product:price", it) }
            currency?.let { put("product:currency", it) }
            availability?.let { put("product:availability", it) }
            brand?.let { put("product:brand", it) }
        }
    )
}

/**
 * Meta tags for preventing search engine indexing (useful for admin pages, etc.)
 */
@Composable
fun NoIndexMetaTags(
    title: String? = null
) {
    MetaTags(
        title = title,
        robots = "noindex, nofollow"
    )
}

/**
 * Meta tags for Progressive Web Apps (PWA)
 */
@Composable
fun PWAMetaTags(
    title: String,
    description: String,
    themeColor: String,
    manifestUrl: String,
    appleTouchIcon: String? = null,
    maskIcon: String? = null,
    msApplicationConfig: String? = null
) {
    val renderer = LocalPlatformRenderer.current

    MetaTags(
        title = title,
        description = description,
        themeColor = themeColor
    )

    renderer.renderHeadElements {
        // Link to manifest
        link(rel = "manifest", href = manifestUrl)

        // Apple-specific
        appleTouchIcon?.let {
            link(rel = "apple-touch-icon", href = it)
        }

        maskIcon?.let {
            link(rel = "mask-icon", href = it)
        }

        // Microsoft-specific
        msApplicationConfig?.let {
            meta(name = "msapplication-config", content = it)
        }

        // Mobile web app capable
        meta(name = "mobile-web-app-capable", content = "yes")
        meta(name = "apple-mobile-web-app-capable", content = "yes")
        meta(name = "apple-mobile-web-app-status-bar-style", content = "default")
    }
}