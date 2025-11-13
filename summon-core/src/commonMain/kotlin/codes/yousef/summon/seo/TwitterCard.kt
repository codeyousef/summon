package codes.yousef.summon.seo

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Types of Twitter Cards available.
 */
enum class TwitterCardType(val value: String) {
    /** Default card with title, description, and small image */
    Summary("summary"),

    /** Similar to summary but with a large image */
    SummaryLargeImage("summary_large_image"),

    /** Card for mobile apps */
    App("app"),

    /** Card with video or audio player */
    Player("player")
}

/**
 * Composable component for rendering Twitter Card meta tags for rich Twitter previews.
 * Implements the Twitter Card protocol for optimized content sharing on Twitter/X.
 *
 * @param card The type of Twitter Card to use
 * @param title The title of the content (max 70 characters for optimal display)
 * @param description A brief description (max 200 characters for optimal display)
 * @param image The URL of the image to display (min 144x144px for summary, 300x157px for large)
 * @param imageAlt Alternative text for the image
 * @param site The @username of the website
 * @param creator The @username of the content creator
 * @param player URL to iframe player for video/audio content
 * @param playerWidth Width of the player in pixels
 * @param playerHeight Height of the player in pixels
 * @param customProperties Additional Twitter Card properties
 */
@Composable
fun TwitterCard(
    card: TwitterCardType = TwitterCardType.Summary,
    title: String? = null,
    description: String? = null,
    image: String? = null,
    imageAlt: String? = null,
    site: String? = null,
    creator: String? = null,
    player: String? = null,
    playerWidth: Int? = null,
    playerHeight: Int? = null,
    customProperties: Map<String, String> = emptyMap()
) {
    val renderer = LocalPlatformRenderer.current

    renderer.renderHeadElements {
        // Card type is required
        meta(name = "twitter:card", content = card.value)

        // Core properties
        title?.let { meta(name = "twitter:title", content = it) }
        description?.let { meta(name = "twitter:description", content = it) }

        // Image properties
        image?.let { meta(name = "twitter:image", content = it) }
        imageAlt?.let { meta(name = "twitter:image:alt", content = it) }

        // Attribution
        site?.let { meta(name = "twitter:site", content = it) }
        creator?.let { meta(name = "twitter:creator", content = it) }

        // Player properties (for player cards)
        player?.let { meta(name = "twitter:player", content = it) }
        playerWidth?.let { meta(name = "twitter:player:width", content = it.toString()) }
        playerHeight?.let { meta(name = "twitter:player:height", content = it.toString()) }

        // Custom properties
        customProperties.forEach { (name, value) ->
            meta(name = "twitter:$name", content = value)
        }
    }
}

/**
 * Twitter Card optimized for articles and blog posts.
 */
@Composable
fun ArticleTwitterCard(
    title: String,
    description: String,
    image: String? = null,
    author: String? = null,
    site: String? = null,
    publishedTime: String? = null,
    modifiedTime: String? = null
) {
    TwitterCard(
        card = if (image != null) TwitterCardType.SummaryLargeImage else TwitterCardType.Summary,
        title = title,
        description = description,
        image = image,
        creator = author,
        site = site,
        customProperties = buildMap {
            publishedTime?.let { put("published_time", it) }
            modifiedTime?.let { put("modified_time", it) }
        }
    )
}

/**
 * Twitter Card for product pages.
 */
@Composable
fun ProductTwitterCard(
    title: String,
    description: String,
    image: String,
    price: String? = null,
    currency: String? = null,
    availability: String? = null,
    site: String? = null
) {
    TwitterCard(
        card = TwitterCardType.SummaryLargeImage,
        title = title,
        description = description,
        image = image,
        site = site,
        customProperties = buildMap {
            price?.let {
                put("label1", "Price")
                put("data1", "$currency$it")
            }
            availability?.let {
                put("label2", "Availability")
                put("data2", it)
            }
        }
    )
}

/**
 * Twitter Card for mobile apps.
 */
@Composable
fun AppTwitterCard(
    name: String,
    description: String? = null,
    appIdIPhone: String? = null,
    appIdIPad: String? = null,
    appIdGooglePlay: String? = null,
    appUrlIPhone: String? = null,
    appUrlIPad: String? = null,
    appUrlGooglePlay: String? = null,
    appCountry: String? = null,
    site: String? = null
) {
    TwitterCard(
        card = TwitterCardType.App,
        site = site,
        description = description,
        customProperties = buildMap {
            // App name
            put("app:name:iphone", name)
            put("app:name:ipad", name)
            put("app:name:googleplay", name)

            // App IDs
            appIdIPhone?.let { put("app:id:iphone", it) }
            appIdIPad?.let { put("app:id:ipad", it) }
            appIdGooglePlay?.let { put("app:id:googleplay", it) }

            // App URLs
            appUrlIPhone?.let { put("app:url:iphone", it) }
            appUrlIPad?.let { put("app:url:ipad", it) }
            appUrlGooglePlay?.let { put("app:url:googleplay", it) }

            // Country
            appCountry?.let { put("app:country", it) }
        }
    )
}

/**
 * Twitter Card for video content.
 */
@Composable
fun VideoTwitterCard(
    title: String,
    description: String,
    playerUrl: String,
    playerWidth: Int,
    playerHeight: Int,
    image: String,
    duration: Int? = null, // in seconds
    site: String? = null,
    creator: String? = null
) {
    TwitterCard(
        card = TwitterCardType.Player,
        title = title,
        description = description,
        image = image,
        player = playerUrl,
        playerWidth = playerWidth,
        playerHeight = playerHeight,
        site = site,
        creator = creator,
        customProperties = buildMap {
            duration?.let {
                val minutes = it / 60
                val seconds = it % 60
                put("player:stream:content_type", "video/mp4")
                put("duration", "$minutes:${seconds.toString().padStart(2, '0')}")
            }
        }
    )
}

/**
 * Twitter Card for audio/podcast content.
 */
@Composable
fun AudioTwitterCard(
    title: String,
    description: String,
    playerUrl: String,
    image: String? = null,
    duration: Int? = null, // in seconds
    artist: String? = null,
    album: String? = null,
    site: String? = null
) {
    TwitterCard(
        card = TwitterCardType.Player,
        title = title,
        description = description,
        image = image,
        player = playerUrl,
        playerWidth = 435,  // Standard width for audio players
        playerHeight = 200, // Standard height for audio players
        site = site,
        creator = artist,
        customProperties = buildMap {
            duration?.let {
                val minutes = it / 60
                val seconds = it % 60
                put("player:stream:content_type", "audio/mpeg")
                put("duration", "$minutes:${seconds.toString().padStart(2, '0')}")
            }
            album?.let { put("audio:album", it) }
            artist?.let { put("audio:artist", it) }
        }
    )
}

/**
 * Twitter Card for photo galleries.
 */
@Composable
fun GalleryTwitterCard(
    title: String,
    description: String,
    images: List<String>,
    site: String? = null,
    creator: String? = null
) {
    require(images.isNotEmpty()) { "Gallery must have at least one image" }
    require(images.size <= 4) { "Twitter supports maximum 4 images in gallery" }

    val renderer = LocalPlatformRenderer.current

    TwitterCard(
        card = TwitterCardType.SummaryLargeImage,
        title = title,
        description = description,
        site = site,
        creator = creator
    )

    // Add multiple images for gallery
    renderer.renderHeadElements {
        images.forEachIndexed { index, imageUrl ->
            meta(name = "twitter:image$index", content = imageUrl)
        }
    }
}