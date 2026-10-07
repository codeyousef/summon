package codes.yousef.summon.seo

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * OpenGraph protocol types for different content categories.

 * @property value Value to process.
 */
enum class OGType(val value: String) {
    /** The website og type option. */
    Website("website"),
    /** The article og type option. */
    Article("article"),
    /** The book og type option. */
    Book("book"),
    /** The profile og type option. */
    Profile("profile"),
    /** The video og type option. */
    Video("video"),
    /** The video movie og type option. */
    VideoMovie("video.movie"),
    /** The video episode og type option. */
    VideoEpisode("video.episode"),
    /** The video tv show og type option. */
    VideoTVShow("video.tv_show"),
    /** The video other og type option. */
    VideoOther("video.other"),
    /** The music og type option. */
    Music("music"),
    /** The music song og type option. */
    MusicSong("music.song"),
    /** The music album og type option. */
    MusicAlbum("music.album"),
    /** The music playlist og type option. */
    MusicPlaylist("music.playlist"),
    /** The music radio station og type option. */
    MusicRadioStation("music.radio_station"),
    /** The product og type option. */
    Product("product")
}

/**
 * Composable component for rendering OpenGraph meta tags for rich social media previews.
 * Implements the OpenGraph protocol for better content sharing on Facebook, LinkedIn, etc.
 *
 * @param title The title of the content
 * @param type The type of content (website, article, etc.)
 * @param url The canonical URL of the page
 * @param image The URL of the image to display in the preview
 * @param description A brief description of the content
 * @param siteName The name of the website
 * @param locale The locale of the content (e.g., "en_US")
 * @param alternateLocales Alternative locales available
 * @param determiner The word that appears before the title ("a", "an", "the", etc.)
 * @param audio URL to an audio file to accompany the content
 * @param video URL to a video file to accompany the content
 * @param imageAlt Alternative text for the image
 * @param imageWidth Width of the image in pixels
 * @param imageHeight Height of the image in pixels
 * @param customProperties Additional OpenGraph properties
 */
@Composable
fun OpenGraphTags(
    title: String,
    type: OGType = OGType.Website,
    url: String? = null,
    image: String? = null,
    description: String? = null,
    siteName: String? = null,
    locale: String? = null,
    alternateLocales: List<String>? = null,
    determiner: String? = null,
    audio: String? = null,
    video: String? = null,
    imageAlt: String? = null,
    imageWidth: Int? = null,
    imageHeight: Int? = null,
    customProperties: Map<String, String> = emptyMap()
) {
    val renderer = LocalPlatformRenderer.current

    renderer.renderHeadElements {
        // Required properties
        meta(property = "og:title", content = title)
        meta(property = "og:type", content = type.value)

        // Recommended properties
        url?.let { meta(property = "og:url", content = it) }
        image?.let { meta(property = "og:image", content = it) }
        description?.let { meta(property = "og:description", content = it) }
        siteName?.let { meta(property = "og:site_name", content = it) }

        // Optional properties
        locale?.let { meta(property = "og:locale", content = it) }
        alternateLocales?.forEach { altLocale ->
            meta(property = "og:locale:alternate", content = altLocale)
        }
        determiner?.let { meta(property = "og:determiner", content = it) }
        audio?.let { meta(property = "og:audio", content = it) }
        video?.let { meta(property = "og:video", content = it) }

        // Image properties
        imageAlt?.let { meta(property = "og:image:alt", content = it) }
        imageWidth?.let { meta(property = "og:image:width", content = it.toString()) }
        imageHeight?.let { meta(property = "og:image:height", content = it.toString()) }

        // Custom properties
        customProperties.forEach { (property, value) ->
            meta(property = if (':' in property) property else "og:$property", content = value)
        }
    }
}

/**
 * OpenGraph tags specifically for articles.
 */
@Composable
fun ArticleOpenGraphTags(
    title: String,
    url: String,
    image: String? = null,
    description: String? = null,
    author: String? = null,
    publishedTime: String? = null,
    modifiedTime: String? = null,
    expirationTime: String? = null,
    section: String? = null,
    tags: List<String>? = null,
    siteName: String? = null
) {
    OpenGraphTags(
        title = title,
        type = OGType.Article,
        url = url,
        image = image,
        description = description,
        siteName = siteName,
        customProperties = buildMap {
            author?.let { put("article:author", it) }
            publishedTime?.let { put("article:published_time", it) }
            modifiedTime?.let { put("article:modified_time", it) }
            expirationTime?.let { put("article:expiration_time", it) }
            section?.let { put("article:section", it) }
            tags?.forEach { tag ->
                put("article:tag", tag)
            }
        }
    )
}

/**
 * OpenGraph tags specifically for product pages.
 */
@Composable
fun ProductOpenGraphTags(
    title: String,
    url: String,
    image: String,
    description: String? = null,
    price: String? = null,
    currency: String? = null,
    availability: ProductAvailability? = null,
    condition: ProductCondition? = null,
    retailerItemId: String? = null,
    brand: String? = null,
    siteName: String? = null
) {
    OpenGraphTags(
        title = title,
        type = OGType.Product,
        url = url,
        image = image,
        description = description,
        siteName = siteName,
        customProperties = buildMap {
            price?.let { put("product:price:amount", it) }
            currency?.let { put("product:price:currency", it) }
            availability?.let { put("product:availability", it.value) }
            condition?.let { put("product:condition", it.value) }
            retailerItemId?.let { put("product:retailer_item_id", it) }
            brand?.let { put("product:brand", it) }
        }
    )
}

/**
 * Product availability status for OpenGraph.

 * @property value Value to process.
 */
enum class ProductAvailability(val value: String) {
    /** The in stock product availability option. */
    InStock("instock"),
    /** The out of stock product availability option. */
    OutOfStock("oos"),
    /** The discontinued product availability option. */
    Discontinued("discontinued"),
    /** The pending product availability option. */
    Pending("pending")
}

/**
 * Product condition for OpenGraph.

 * @property value Value to process.
 */
enum class ProductCondition(val value: String) {
    /** The new product condition option. */
    New("new"),
    /** The refurbished product condition option. */
    Refurbished("refurbished"),
    /** The used product condition option. */
    Used("used")
}

/**
 * OpenGraph tags specifically for video content.
 */
@Composable
fun VideoOpenGraphTags(
    title: String,
    url: String,
    videoUrl: String,
    image: String? = null,
    description: String? = null,
    duration: Int? = null, // in seconds
    releaseDate: String? = null,
    actors: List<String>? = null,
    directors: List<String>? = null,
    writers: List<String>? = null,
    tags: List<String>? = null,
    series: String? = null,
    siteName: String? = null
) {
    OpenGraphTags(
        title = title,
        type = OGType.Video,
        url = url,
        image = image,
        video = videoUrl,
        description = description,
        siteName = siteName,
        customProperties = buildMap {
            duration?.let { put("video:duration", it.toString()) }
            releaseDate?.let { put("video:release_date", it) }
            actors?.forEach { actor ->
                put("video:actor", actor)
            }
            directors?.forEach { director ->
                put("video:director", director)
            }
            writers?.forEach { writer ->
                put("video:writer", writer)
            }
            tags?.forEach { tag ->
                put("video:tag", tag)
            }
            series?.let { put("video:series", it) }
        }
    )
}

/**
 * OpenGraph tags specifically for music content.
 */
@Composable
fun MusicOpenGraphTags(
    title: String,
    url: String,
    type: MusicType,
    image: String? = null,
    description: String? = null,
    duration: Int? = null, // in seconds
    album: String? = null,
    albumDisc: Int? = null,
    albumTrack: Int? = null,
    musicians: List<String>? = null,
    songwriters: List<String>? = null,
    releaseDate: String? = null,
    siteName: String? = null
) {
    val ogType = when (type) {
        MusicType.Song -> OGType.MusicSong
        MusicType.Album -> OGType.MusicAlbum
        MusicType.Playlist -> OGType.MusicPlaylist
        MusicType.RadioStation -> OGType.MusicRadioStation
    }

    OpenGraphTags(
        title = title,
        type = ogType,
        url = url,
        image = image,
        description = description,
        siteName = siteName,
        customProperties = buildMap {
            duration?.let { put("music:duration", it.toString()) }
            album?.let { put("music:album", it) }
            albumDisc?.let { put("music:album:disc", it.toString()) }
            albumTrack?.let { put("music:album:track", it.toString()) }
            musicians?.forEach { musician ->
                put("music:musician", musician)
            }
            songwriters?.forEach { songwriter ->
                put("music:creator", songwriter)
            }
            releaseDate?.let { put("music:release_date", it) }
        }
    )
}

/**
 * Types of music content for OpenGraph.
 */
enum class MusicType {
    /** The song music type option. */
    Song,
    /** The album music type option. */
    Album,
    /** The playlist music type option. */
    Playlist,
    /** The radio station music type option. */
    RadioStation
}