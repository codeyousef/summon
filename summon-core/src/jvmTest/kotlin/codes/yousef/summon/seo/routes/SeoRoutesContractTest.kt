package codes.yousef.summon.seo.routes

import codes.yousef.summon.routing.Route
import codes.yousef.summon.runtime.PlatformRenderer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SeoRoutesContractTest {
    @Test
    fun canonicalVariantsEscapeEveryAttributeAndCoverOptionalHeadLinks() {
        val html = PlatformRenderer().renderComposableRoot {
            CanonicalLinks(
                url = "https://example.test/?a=1&b=\"two\"",
                alternateLanguages = mapOf("en\" onclick=\"bad" to "https://example.test/en?a=1&b=2"),
                ampUrl = "https://example.test/amp?a=1&b=2"
            )
            SimpleCanonicalLink("https://example.test/simple")
            CanonicalLinksWithLanguages(
                defaultUrl = "https://example.test/default",
                defaultLanguage = "en",
                alternates = mapOf("fr" to "https://example.test/fr")
            )
            CanonicalLinksWithAmp("https://example.test/page", "https://example.test/page.amp")
            CanonicalLink("https://example.test/direct")
        }

        assertContains(html, "href=\"https://example.test/?a=1&amp;b=&quot;two&quot;\"")
        assertContains(html, "hreflang=\"en&quot; onclick=&quot;bad\"")
        assertContains(html, "rel=\"amphtml\"")
        assertContains(html, "hreflang=\"fr\"")
        assertContains(html, "href=\"https://example.test/direct\"")
        assertFalse(html.contains("hreflang=\"en\" onclick=\"bad\""))
    }

    @Test
    fun structuredDataBuildersEmitParseableJsonAndCannotCloseTheirScript() {
        val html = PlatformRenderer().renderComposableRoot {
            JsonLdStructuredData("{\"value\":\"</script><script>bad()</script>&\"}")
            StructuredData.webPage("Page", "Description", "https://example.test")
            StructuredData.organization("Org", "https://example.test", "https://example.test/logo.png")
            StructuredData.product("Product", "Description", "image.png", "12.50", "EUR")
            StructuredData.breadcrumbs(listOf("Home" to "/", "Product" to "/product"))
            StructuredData.article(
                headline = "Headline",
                author = "Author",
                datePublished = "2026-10-07",
                image = "image.png",
                url = "https://example.test/article",
                description = "Description",
                publisher = "Publisher",
                publisherLogo = "logo.png"
            )
            StructuredData.localBusiness(
                name = "Cafe",
                url = "https://example.test/cafe",
                description = "Coffee",
                address = mapOf("streetAddress" to "1 Main", "addressLocality" to "Town"),
                telephone = "+1-555-0100",
                logo = "logo.png",
                image = "cafe.png",
                priceRange = "$$",
                openingHours = mapOf("Monday" to "09:00-17:30", "Tuesday" to "09:00")
            )
            OrganizationStructuredData("Defaults", "https://example.test/defaults")
        }

        val scripts = Regex("<script type=\"application/ld\\+json\">(.*?)</script>")
            .findAll(html)
            .map { it.groupValues[1] }
            .toList()
        assertEquals(8, scripts.size)
        scripts.forEach { Json.decodeFromString(JsonElement.serializer(), it) }
        assertContains(scripts.first(), "\\u003C/script\\u003E")
        assertFalse(scripts.first().contains("<script>"))
        assertTrue(scripts.any { it.contains("OpeningHoursSpecification") })
        assertTrue(scripts.any { it.contains("\"logo\":null") })
    }

    @Test
    fun sitemapCoversOptionalFieldsRouteExpansionAndXmlEscaping() {
        val generator = SitemapGeneration()
        val urls = listOf(
            SitemapGeneration.SitemapUrl(
                loc = "/products?a=1&b=2",
                lastmod = "2026-10-07<&",
                changefreq = SitemapGeneration.ChangeFrequency.DAILY,
                priority = 0.9,
                alternates = mapOf("en\" bad=\"x" to "https://example.test/en?a=1&b=2")
            ),
            SitemapGeneration.SitemapUrl(loc = "plain")
        )
        val xml = generator.getXmlString(urls, "https://example.test/")

        assertContains(xml, "<loc>https://example.test/products?a=1&amp;b=2</loc>")
        assertContains(xml, "<lastmod>2026-10-07&lt;&amp;</lastmod>")
        assertContains(xml, "<changefreq>daily</changefreq>")
        assertContains(xml, "<priority>0.9</priority>")
        assertContains(xml, "hreflang=\"en&quot; bad=&quot;x\"")
        assertEquals("2026-02-03", SitemapGeneration.formatDate(2026, 2, 3))
        assertContains(SitemapGeneration.robotsTxt("https://example.test/sitemap.xml"), "Sitemap: https://example.test/sitemap.xml")
        assertEquals(
            listOf("/", "/users/1", "/files/"),
            SitemapGeneration.fromRoutes(
                listOf(Route("/") { {} }, Route("/users/:id") { {} }, Route("/files/*path") { {} }),
                "https://example.test"
            ).map { it.loc }
        )
        assertEquals(
            listOf("always", "hourly", "daily", "weekly", "monthly", "yearly", "never"),
            SitemapGeneration.ChangeFrequency.entries.map { it.value }
        )

        val html = PlatformRenderer().renderComposableRoot { generator.SitemapXml(urls, "https://example.test") }
        assertContains(html, "&lt;urlset")
    }
}
