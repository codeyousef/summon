package codes.yousef.summon.seo.routes

import codes.yousef.summon.runtime.PlatformRenderer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class StructuredDataObjectContractTest {
    private fun render(block: () -> Unit): String = PlatformRenderer().renderComposableRoot { block() }

    @Test
    fun convenienceStructuredDataFunctionsEmitEscapedJsonLd() {
        val hostile = "<&>\""
        val html = render {
            StructuredData.webPage("page$hostile", "description", "https://example.test")
            StructuredData.organization("org$hostile", "https://example.test", "/logo.png")
            StructuredData.product("product$hostile", "description", "/image.png", "10")
            StructuredData.breadcrumbs(listOf("Home$hostile" to "/", "Current" to "/current"))
            StructuredData.article(
                headline = "headline$hostile", author = "author", datePublished = "2026-01-01",
                image = "/image.png", url = "https://example.test/article", description = "description",
                publisher = "publisher", publisherLogo = "/logo.png"
            )
        }
        assertContains(html, "application/ld+json")
        assertContains(html, "\\u003C")
        assertContains(html, "\\u0026")
        assertFalse(html.contains("headline<&>"))
        assertContains(html, "BreadcrumbList")
        assertContains(html, "Article")
    }

    @Test
    fun localBusinessIncludesOnlyProvidedOptionalFieldsAndNormalizesHours() {
        val full = render {
            StructuredData.localBusiness(
                name = "Cafe", url = "https://example.test", description = "Coffee",
                address = mapOf("streetAddress" to "1 Main", "addressCountry" to "US"),
                telephone = "+1", logo = "/logo", image = "/image", priceRange = "$$",
                openingHours = linkedMapOf("Monday" to "09:00-17:00", "Sunday" to "10:00")
            )
        }
        assertContains(full, "LocalBusiness")
        assertContains(full, "openingHoursSpecification")
        assertContains(full, "09:00")
        assertContains(full, "17:00")
        assertContains(full, "logo")

        val minimal = render {
            StructuredData.localBusiness(
                name = "Cafe", url = "https://example.test", description = "Coffee",
                address = emptyMap(), telephone = "+1"
            )
        }
        assertFalse(minimal.contains("openingHoursSpecification"))
        assertFalse(minimal.contains("priceRange"))
    }
}
