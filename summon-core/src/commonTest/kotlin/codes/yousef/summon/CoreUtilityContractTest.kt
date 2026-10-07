package codes.yousef.summon

import codes.yousef.summon.core.CustomValidator
import codes.yousef.summon.core.EmailValidator
import codes.yousef.summon.core.MaxLengthValidator
import codes.yousef.summon.core.MinLengthValidator
import codes.yousef.summon.core.PatternValidator
import codes.yousef.summon.hydration.HydrationPriority
import codes.yousef.summon.i18n.I18nUtils
import codes.yousef.summon.core.RequiredValidator
import codes.yousef.summon.core.validateBoolean
import codes.yousef.summon.routing.Route
import codes.yousef.summon.runtime.HydrationPhase
import codes.yousef.summon.runtime.PerformanceConfig
import codes.yousef.summon.runtime.perfMarkEnd
import codes.yousef.summon.runtime.perfMarkStart
import codes.yousef.summon.runtime.withPerfMetrics
import codes.yousef.summon.seo.routes.SitemapGeneration
import codes.yousef.summon.ssr.SEOPrerender
import codes.yousef.summon.ssr.SeoMetadata
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("DEPRECATION")
class CoreUtilityContractTest {
    @AfterTest
    fun resetPerformanceOptIn() {
        PerformanceConfig.enabled = false
    }

    @Test
    fun legacyValidatorsRetainDocumentedBoundaries() {
        assertFalse(RequiredValidator().validate(" \t"))
        assertTrue(RequiredValidator().validate("value"))
        assertTrue(EmailValidator().validate(""))
        assertTrue(EmailValidator().validate("name+tag@example.test"))
        assertFalse(EmailValidator().validate("invalid"))
        assertFalse(MinLengthValidator(3).validate("ab"))
        assertTrue(MinLengthValidator(3).validate("abc"))
        assertTrue(MaxLengthValidator(3).validate("abc"))
        assertFalse(MaxLengthValidator(3).validate("abcd"))
        assertTrue(PatternValidator(Regex("[A-Z]+"), "uppercase").validate(""))
        assertTrue(PatternValidator(Regex("[A-Z]+"), "uppercase").validate("ABC"))
        assertFalse(PatternValidator(Regex("[A-Z]+"), "uppercase").validate("Ab"))
        val boolean = CustomValidator({ it == "true" }, "must be true")
        assertTrue(boolean.validateBoolean(true))
        assertFalse(boolean.validateBoolean(false))
    }

    @Test
    fun sitemapGenerationCoversOptionalMetadataAndRouteNormalization() {
        val generator = SitemapGeneration()
        val complete = SitemapGeneration.SitemapUrl(
            loc = "/article",
            lastmod = "2026-10-07",
            changefreq = SitemapGeneration.ChangeFrequency.DAILY,
            priority = 0.8,
            alternates = mapOf("ar" to "https://example.test/ar/article"),
        )
        val xml = generator.getXmlString(listOf(complete, SitemapGeneration.SitemapUrl("plain")), "https://example.test/")
        assertContains(xml, "<loc>https://example.test/article</loc>")
        assertContains(xml, "<lastmod>2026-10-07</lastmod>")
        assertContains(xml, "<changefreq>daily</changefreq>")
        assertContains(xml, "<priority>0.8</priority>")
        assertContains(xml, "hreflang=\"ar\"")
        assertContains(xml, "<loc>https://example.test/plain</loc>")

        val routes = listOf(
            Route("/") { {} },
            Route("") { {} },
            Route("/users/:id/*rest") { {} },
        )
        val entries = SitemapGeneration.fromRoutes(routes, "https://example.test")
        assertEquals(listOf(1.0, 1.0, 0.7), entries.map { it.priority })
        assertEquals("/users/1/", entries.last().loc)
        assertEquals("0007-03-09", SitemapGeneration.formatDate(7, 3, 9))
        assertContains(SitemapGeneration.robotsTxt("https://example.test/sitemap.xml"), "Sitemap: https://example.test/sitemap.xml")
    }

    @Test
    fun seoPrerenderingDetectsKnownAgentsAndPreservesExplicitMetadata() {
        assertTrue(SEOPrerender.isSearchEngineCrawler("Mozilla GoogleBot/2.1"))
        assertTrue(SEOPrerender.isSearchEngineCrawler("TwitterBot"))
        assertFalse(SEOPrerender.isSearchEngineCrawler("Mozilla/5.0"))

        val short = SEOPrerender.enrichSeoMetadata(SeoMetadata(title = "Title", description = "short"))
        assertContains(short.description, "Enhanced with Summon")
        assertEquals("Summon", short.customMetaTags["generator"])
        assertEquals("width=device-width, initial-scale=1.0", short.customMetaTags["viewport"])

        val description = "x".repeat(50)
        val explicit = SEOPrerender.enrichSeoMetadata(
            SeoMetadata(description = description, customMetaTags = mapOf("viewport" to "custom", "generator" to "custom"))
        )
        assertEquals(description, explicit.description)
        assertEquals("custom", explicit.customMetaTags["viewport"])
        assertEquals("custom", explicit.customMetaTags["generator"])
        assertEquals("", SEOPrerender.enrichSeoMetadata(SeoMetadata()).description)
        val openGraph = SEOPrerender.createOpenGraphMetadata(SeoMetadata(title = "T", description = "D"), "https://example.test", "image", "site")
        assertEquals("T", openGraph.title)
        assertEquals("image", openGraph.image)
    }

    @Test
    fun performanceHelpersPreserveResultsWithAndWithoutOptIn() {
        PerformanceConfig.enabled = false
        assertEquals("plain", withPerfMetrics("plain") { "plain" })
        perfMarkStart("disabled")
        assertEquals(0.0, perfMarkEnd("disabled"))

        PerformanceConfig.enabled = true
        assertEquals(42, withPerfMetrics("enabled", HydrationPhase.EVENT_SYSTEM) { 42 })
        perfMarkStart("manual", HydrationPhase.COMPONENT_HYDRATION)
        assertTrue(perfMarkEnd("manual") >= 0.0)
    }

    @Test
    fun hydrationPriorityParsesEveryHintAndFailsClosed() {
        assertEquals(HydrationPriority.CRITICAL, HydrationPriority.fromString("CRITICAL"))
        assertEquals(HydrationPriority.VISIBLE, HydrationPriority.fromString("visible"))
        assertEquals(HydrationPriority.NEAR, HydrationPriority.fromString("near"))
        assertEquals(HydrationPriority.DEFERRED, HydrationPriority.fromString("deferred"))
        assertEquals(HydrationPriority.DEFERRED, HydrationPriority.fromString("unknown"))
        assertEquals(HydrationPriority.DEFERRED, HydrationPriority.fromString(null))
    }

    @Test
    fun i18nMessagesCoverPluralAndGenderFallbacks() {
        assertEquals("Hello Ada, 3!", I18nUtils.formatMessage("Hello {name}, {count}!", mapOf("name" to "Ada", "count" to 3)))
        assertEquals("none", I18nUtils.pluralString(0, "none", "one", "few", "many", "{count} others"))
        assertEquals("one", I18nUtils.pluralString(1, null, "one", "few", "many", "{count} others"))
        assertEquals("3 few", I18nUtils.pluralString(3, null, "one", "{count} few", "many", "{count} others"))
        assertEquals("8 many", I18nUtils.pluralString(8, null, "one", "few", "{count} many", "{count} others"))
        assertEquals("0 others", I18nUtils.pluralString(0, null, "one", "few", "many", "{count} others"))
        assertEquals("30 others", I18nUtils.pluralString(30, null, "one", "few", "many", "{count} others"))
        assertEquals("male", I18nUtils.genderString("MALE", "male", "female", "other"))
        assertEquals("female", I18nUtils.genderString("female", "male", "female", "other"))
        assertEquals("other", I18nUtils.genderString("unknown", "male", "female", "other"))
    }

}
