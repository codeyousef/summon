package codes.yousef.summon.seo

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith

class RichSeoRenderingTest {
    @Test
    fun completeSeoFamiliesRenderSearchAndSocialMetadata() {
        val html = PlatformRenderer().renderComposableRoot {
            SEO(
                title = "Summon private suite",
                description = "Private application shell",
                keywords = listOf("kotlin", "privacy"),
                image = "https://example.test/cover.png",
                url = "https://example.test/app",
                type = OGType.Website,
                twitterCard = TwitterCardType.SummaryLargeImage,
                structuredData = WebSiteSchema("Summon", "https://example.test"),
                author = "Summon team",
                siteName = "Summon",
                locale = "en_US",
                themeColor = "#112233",
                robots = "noindex, nofollow",
                twitterSite = "@summon",
                twitterCreator = "@author",
                customMetaTags = mapOf("application-name" to "Summon"),
                customOpenGraphProperties = mapOf("og:locale:alternate" to "ar_SA"),
                customTwitterProperties = mapOf("twitter:label1" to "Privacy")
            )
            ArticleSEO(
                title = "Release notes",
                description = "Release details",
                author = "Ada",
                publishedTime = "2026-10-07",
                modifiedTime = "2026-10-08",
                image = "https://example.test/article.png",
                url = "https://example.test/article",
                keywords = listOf("release", "kotlin"),
                section = "Engineering",
                siteName = "Summon",
                twitterSite = "@summon"
            )
            ProductSEO(
                name = "Summon Pro",
                description = "UI toolkit",
                image = "https://example.test/product.png",
                price = 19.95,
                currency = "USD",
                brand = "Summon",
                sku = "SUMMON-PRO",
                rating = ProductSchema.Rating(4.8, 25),
                url = "https://example.test/product",
                siteName = "Summon",
                twitterSite = "@summon"
            )
            WebAppSEO(
                name = "Summon Studio",
                description = "Private studio",
                url = "https://example.test/studio",
                image = "https://example.test/studio.png",
                category = "DeveloperApplication",
                author = "Summon team",
                siteName = "Summon",
                twitterSite = "@summon",
                themeColor = "#000000",
                manifestUrl = "/manifest.webmanifest"
            )
            VideoSEO(
                title = "Demo",
                description = "Framework demo",
                videoUrl = "https://example.test/demo.mp4",
                thumbnailImage = "https://example.test/demo.png",
                duration = 90,
                uploadDate = "2026-10-07",
                url = "https://example.test/demo",
                siteName = "Summon",
                creator = "Ada",
                twitterSite = "@summon",
                playerWidth = 1280,
                playerHeight = 720
            )
            PageWithBreadcrumbsSEO(
                title = "Docs",
                description = "Documentation",
                breadcrumbs = listOf(
                    BreadcrumbListSchema.BreadcrumbItem("Home", "https://example.test"),
                    BreadcrumbListSchema.BreadcrumbItem("Docs", "https://example.test/docs")
                ),
                image = "https://example.test/docs.png",
                url = "https://example.test/docs",
                siteName = "Summon",
                twitterSite = "@summon"
            )
            FAQSEO(
                title = "FAQ",
                description = "Answers",
                questions = listOf(FAQPageSchema.QuestionAnswer("Private?", "Yes")),
                url = "https://example.test/faq",
                siteName = "Summon",
                twitterSite = "@summon"
            )
        }

        assertContains(html, "<title>Summon private suite</title>")
        assertContains(html, "property=\"og:image\"")
        assertContains(html, "name=\"twitter:player:width\"")
        assertContains(html, "name=\"theme-color\"")
        assertContains(html, "rel=\"manifest\"")
        assertContains(html, "application/ld+json")
        assertContains(html, "BreadcrumbList")
        assertContains(html, "FAQPage")
    }

    @Test
    fun specializedSocialCardsRenderAllOptionalMetadata() {
        val html = PlatformRenderer().renderComposableRoot {
            ArticleOpenGraphTags(
                title = "Article",
                url = "https://example.test/article",
                image = "https://example.test/article.png",
                description = "Description",
                author = "https://example.test/ada",
                publishedTime = "2026-10-07",
                modifiedTime = "2026-10-08",
                expirationTime = "2027-10-07",
                section = "Engineering",
                tags = listOf("kotlin", "ui"),
                siteName = "Summon"
            )
            ProductOpenGraphTags(
                title = "Product",
                url = "https://example.test/product",
                image = "https://example.test/product.png",
                description = "Description",
                price = "19.95",
                currency = "USD",
                availability = ProductAvailability.InStock,
                condition = ProductCondition.New,
                retailerItemId = "SKU-1",
                brand = "Summon",
                siteName = "Summon"
            )
            VideoOpenGraphTags(
                title = "Video",
                url = "https://example.test/video",
                videoUrl = "https://example.test/video.mp4",
                image = "https://example.test/video.png",
                description = "Description",
                duration = 120,
                releaseDate = "2026-10-07",
                actors = listOf("Actor"),
                directors = listOf("Director"),
                writers = listOf("Writer"),
                tags = listOf("demo"),
                series = "Series",
                siteName = "Summon"
            )
            MusicType.entries.forEach { type ->
                MusicOpenGraphTags(
                    title = type.name,
                    url = "https://example.test/music/${type.name}",
                    type = type,
                    image = "https://example.test/music.png",
                    description = "Description",
                    duration = 180,
                    album = "Album",
                    albumDisc = 1,
                    albumTrack = 2,
                    musicians = listOf("Musician"),
                    songwriters = listOf("Writer"),
                    releaseDate = "2026-10-07",
                    siteName = "Summon"
                )
            }
            ArticleTwitterCard("Article", "Description", "https://example.test/a.png", "@ada", "@summon", "2026", "2027")
            ProductTwitterCard("Product", "Description", "https://example.test/p.png", "19.95", "USD", "in stock", "@summon")
            AppTwitterCard("App", "Description", "1", "2", "3", "app://iphone", "app://ipad", "app://android", "US", "@summon")
            VideoTwitterCard("Video", "Description", "https://example.test/player", 640, 360, "https://example.test/v.png", 90, "@summon", "@ada")
            AudioTwitterCard("Audio", "Description", "https://example.test/audio", "https://example.test/audio.png", 60, "Artist", "Album", "@summon")
            GalleryTwitterCard("Gallery", "Description", listOf("https://example.test/1.png", "https://example.test/2.png"), "@summon", "@ada")
        }

        assertContains(html, "property=\"product:price:amount\" content=\"19.95\"")
        assertContains(html, "property=\"video:duration\" content=\"120\"")
        assertContains(html, "property=\"music:album:track\" content=\"2\"")
        assertContains(html, "name=\"twitter:app:id:googleplay\" content=\"3\"")
        assertContains(html, "name=\"twitter:image1\" content=\"https://example.test/2.png\"")
    }

    @Test
    fun minimalMetadataFamiliesOmitEveryOptionalProperty() {
        val html = PlatformRenderer().renderComposableRoot {
            MetaTags()
            ArticleMetaTags("Article", "Description", "Author")
            ProductMetaTags("Product", "Description")
            NoIndexMetaTags()
            TwitterCard()
            ArticleTwitterCard("Article", "Description")
            ProductTwitterCard("Product", "Description", "image.png")
            AppTwitterCard("App", "Description")
            VideoTwitterCard("Video", "Description", "https://example.test/player", 640, 360, "image.png")
            AudioTwitterCard("Audio", "Description", "https://example.test/audio")
            GalleryTwitterCard("Gallery", "Description", listOf("one.png"))
        }

        assertContains(html, "name=\"robots\" content=\"noindex, nofollow\"")
        assertContains(html, "name=\"twitter:card\" content=\"summary\"")
        assertFalse(html.contains("article:published_time"))
        assertFalse(html.contains("product:price"))
        assertFalse(html.contains("twitter:creator"))
        assertFalse(html.contains("twitter:app:id:iphone"))
        assertFalse(html.contains("twitter:duration"))
    }

    @Test
    fun galleryRejectsEmptyAndOversizedImageSets() {
        assertFailsWith<IllegalArgumentException> {
            PlatformRenderer().renderComposableRoot { GalleryTwitterCard("Gallery", "Description", emptyList()) }
        }
        assertFailsWith<IllegalArgumentException> {
            PlatformRenderer().renderComposableRoot {
                GalleryTwitterCard("Gallery", "Description", List(5) { "https://example.test/$it.png" })
            }
        }
    }
}
