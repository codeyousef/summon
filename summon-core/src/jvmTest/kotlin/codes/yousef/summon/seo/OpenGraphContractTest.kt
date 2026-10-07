package codes.yousef.summon.seo

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class OpenGraphContractTest {
    @Test
    fun genericAndSpecializedTagsRenderOptionalAndNamespacedProperties() {
        val renderer = PlatformRenderer()
        renderer.renderComposableRoot {
            OpenGraphTags(
                title = "Generic",
                type = OGType.Book,
                url = "https://example.test",
                image = "/image.png",
                description = "Description",
                siteName = "Site",
                locale = "en_US",
                alternateLocales = listOf("ar_SA"),
                determiner = "the",
                audio = "/audio.mp3",
                video = "/video.mp4",
                imageAlt = "Preview",
                imageWidth = 1200,
                imageHeight = 630,
                customProperties = mapOf("custom" to "value", "book:isbn" to "123")
            )
            ArticleOpenGraphTags("Article", "/article", author = "Author", publishedTime = "p", modifiedTime = "m", expirationTime = "e", section = "News", tags = listOf("Kotlin"))
            ProductOpenGraphTags("Product", "/product", "/product.png", price = "10", currency = "USD", availability = ProductAvailability.InStock, condition = ProductCondition.New, retailerItemId = "sku", brand = "Brand")
            VideoOpenGraphTags("Video", "/video", "/movie.mp4", duration = 60, releaseDate = "date", actors = listOf("Actor"), directors = listOf("Director"), writers = listOf("Writer"), tags = listOf("Tag"), series = "Series")
            MusicType.entries.forEach { type ->
                MusicOpenGraphTags("Music", "/music", type, duration = 30, album = "Album", albumDisc = 1, albumTrack = 2, musicians = listOf("Musician"), songwriters = listOf("Writer"), releaseDate = "date")
            }
            OpenGraphTags("Minimal")
            ArticleOpenGraphTags("Minimal article", "/article-minimal")
            ProductOpenGraphTags("Minimal product", "/product-minimal", "/product-minimal.png")
            VideoOpenGraphTags("Minimal video", "/video-minimal", "/video-minimal.mp4")
            MusicOpenGraphTags("Minimal music", "/music-minimal", MusicType.Song)
        }
        val head = renderer.getHeadElements().joinToString("\n")
        listOf(
            "og:url", "og:image:height", "og:locale:alternate", "og:custom", "book:isbn",
            "article:author", "article:tag", "product:price:amount", "product:brand",
            "video:actor", "video:director", "video:writer", "video:series",
            "music:duration", "music:album:disc", "music:creator", "music:release_date",
            "music.song", "music.album", "music.playlist", "music.radio_station"
        ).forEach { assertContains(head, it) }
        assertFalse(head.contains("og:article:"))
        assertFalse(head.contains("og:product:"))
        assertFalse(head.contains("og:video:"))
        assertFalse(head.contains("og:music:"))
    }
}
