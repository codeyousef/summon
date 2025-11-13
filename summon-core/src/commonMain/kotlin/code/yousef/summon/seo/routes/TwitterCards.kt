package codes.yousef.summon.seo.routes

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.SideEffect

/**
 * Enum for Twitter card types
 */
enum class TwitterCardType(val value: String) {
    SUMMARY("summary"),
    SUMMARY_LARGE_IMAGE("summary_large_image"),
    APP("app"),
    PLAYER("player")
}

/**
 * TwitterCards component for adding Twitter-specific metadata
 * These tags help display rich previews when users share your content on Twitter
 */
@Composable
fun TwitterCards(
    card: TwitterCardType = TwitterCardType.SUMMARY,
    title: String? = null,
    description: String? = null,
    image: String? = null,
    imageAlt: String? = null,
    site: String? = null,
    creator: String? = null,
    extraTags: Map<String, String> = emptyMap()
) {
    val renderer = LocalPlatformRenderer.current

    SideEffect {
        // Card type
        renderer.addHeadElement("<meta name=\"twitter:card\" content=\"${card.value}\">")

        // Basic tags
        title?.let {
            renderer.addHeadElement("<meta name=\"twitter:title\" content=\"$it\">")
        }
        description?.let {
            renderer.addHeadElement("<meta name=\"twitter:description\" content=\"$it\">")
        }

        // Image
        image?.let {
            renderer.addHeadElement("<meta name=\"twitter:image\" content=\"$it\">")
        }
        imageAlt?.let {
            renderer.addHeadElement("<meta name=\"twitter:image:alt\" content=\"$it\">")
        }

        // Account information
        site?.let {
            renderer.addHeadElement("<meta name=\"twitter:site\" content=\"$it\">")
        }
        creator?.let {
            renderer.addHeadElement("<meta name=\"twitter:creator\" content=\"$it\">")
        }

        // Add any additional Twitter tags
        extraTags.forEach { (name, content) ->
            renderer.addHeadElement("<meta name=\"twitter:$name\" content=\"$content\">")
        }
    }
}

/**
 * Create Twitter card for an article
 */
@Composable
fun TwitterArticleCard(
    title: String,
    description: String,
    image: String? = null,
    site: String? = null,
    creator: String? = null
) {
    TwitterCards(
        card = TwitterCardType.SUMMARY_LARGE_IMAGE,
        title = title,
        description = description,
        image = image,
        site = site,
        creator = creator
    )
}

/**
 * Create Twitter card for a product
 */
@Composable
fun TwitterProductCard(
    title: String,
    description: String,
    image: String,
    price: String? = null,
    site: String? = null
) {
    val extraTags = mutableMapOf<String, String>()
    price?.let { extraTags["label1"] = "Price"; extraTags["data1"] = it }

    TwitterCards(
        card = TwitterCardType.SUMMARY,
        title = title,
        description = description,
        image = image,
        site = site,
        extraTags = extraTags
    )
}

/**
 * Composable function to add a Twitter Card meta tag to the document head.
 * Used for Twitter sharing previews.
 *
 * @param name The Twitter card property name (e.g., "twitter:card", "twitter:title").
 * @param content The content value for the property.
 */
@Composable
fun TwitterCardTag(name: String, content: String) {
    val renderer = LocalPlatformRenderer.current

    SideEffect {
        renderer.addHeadElement("<meta name=\"$name\" content=\"$content\">")
    }
    // Renders no UI.
}

// Convenience functions for common Twitter Card tags
@Composable
fun TwitterCard(cardType: String) = TwitterCardTag("twitter:card", cardType) // e.g., "summary", "summary_large_image"

@Composable
fun TwitterSite(siteHandle: String) = TwitterCardTag("twitter:site", siteHandle)

@Composable
fun TwitterCreator(creatorHandle: String) = TwitterCardTag("twitter:creator", creatorHandle)

@Composable
fun TwitterTitle(title: String) = TwitterCardTag("twitter:title", title)

@Composable
fun TwitterDescription(description: String) = TwitterCardTag("twitter:description", description)

@Composable
fun TwitterImage(imageUrl: String) = TwitterCardTag("twitter:image", imageUrl)
