package codes.yousef.summon.seo

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class StructuredDataContractTest {
    private val organization = OrganizationSchema(
        name = "Summon & Co",
        url = "https://example.test",
        logo = "https://example.test/logo.png",
        description = "UI <framework>",
        email = "hello@example.test",
        telephone = "+1-555-0100",
        address = OrganizationSchema.PostalAddress(
            streetAddress = "1 Main St",
            addressLocality = "Testville",
            addressRegion = "CA",
            postalCode = "90210",
            addressCountry = "US"
        ),
        sameAs = listOf("https://social.example/summon"),
        contactPoints = listOf(
            OrganizationSchema.ContactPoint(
                contactType = "support",
                telephone = "+1-555-0101",
                email = "support@example.test",
                areaServed = "US",
                availableLanguage = "en"
            )
        )
    )

    @Test
    fun organizationSerializesEveryOptionalFieldAndEscapesContent() {
        val json = organization.toJsonLD()

        assertContains(json, "\"@type\": \"Organization\"")
        assertContains(json, "Summon & Co")
        assertContains(json, "UI <framework>")
        assertContains(json, "\"streetAddress\": \"1 Main St\"")
        assertContains(json, "\"availableLanguage\": \"en\"")
        assertContains(json, "\"sameAs\": [\"https://social.example/summon\"]")
        OrganizationSchema(
            name = "Minimal",
            address = OrganizationSchema.PostalAddress(),
            contactPoints = listOf(OrganizationSchema.ContactPoint("support"))
        ).toJsonLD()
    }


    @Test
    fun websiteAndApplicationSupportCompleteAndMinimalContracts() {
        val website = WebSiteSchema(
            name = "Docs",
            url = "https://example.test/docs",
            description = "Searchable docs",
            potentialAction = WebSiteSchema.SearchAction("https://example.test/search?q={search_term_string}"),
            publisher = organization,
            inLanguage = "en"
        ).toJsonLD()
        assertContains(website, "\"@type\": \"SearchAction\"")
        assertContains(website, "\"publisher\"")
        assertContains(website, "\"inLanguage\": \"en\"")

        val minimalWebsite = WebSiteSchema("Docs", "https://example.test").toJsonLD()
        assertFalse(minimalWebsite.contains("potentialAction"))
        assertFalse(minimalWebsite.contains("publisher"))
        WebSiteSchema(
            "Docs",
            "https://example.test",
            publisher = OrganizationSchema("Minimal publisher")
        ).toJsonLD()

        val application = WebApplicationSchema(
            name = "Summon Studio",
            description = "Private UI",
            url = "https://app.example.test",
            applicationCategory = "DeveloperApplication",
            operatingSystem = "Web",
            offers = WebApplicationSchema.Offer("12.00", "USD"),
            aggregateRating = WebApplicationSchema.AggregateRating(4.8, 42),
            author = organization
        ).toJsonLD()
        assertContains(application, "\"priceCurrency\": \"USD\"")
        val applicationJson = Json.parseToJsonElement(application).jsonObject
        assertEquals(5.0, applicationJson.getValue("aggregateRating").jsonObject.getValue("bestRating").jsonPrimitive.double)
        assertContains(application, "\"author\"")

        val minimalApplication = WebApplicationSchema("App", "Description").toJsonLD()
        assertFalse(minimalApplication.contains("offers"))
        assertFalse(minimalApplication.contains("aggregateRating"))
        WebApplicationSchema(
            "App",
            "Description",
            offers = WebApplicationSchema.Offer("0", "USD", availability = null),
            aggregateRating = WebApplicationSchema.AggregateRating(1.0, 1, bestRating = null, worstRating = null),
            author = OrganizationSchema("Minimal author")
        ).toJsonLD()
    }

    @Test
    fun articleAndProductSerializeNestedRichResults() {
        val article = ArticleSchema(
            headline = "Release",
            description = "Details",
            author = ArticleSchema.Person("Ada", "https://example.test/ada"),
            datePublished = "2026-10-07",
            dateModified = "2026-10-08",
            image = "https://example.test/article.png",
            publisher = organization,
            mainEntityOfPage = "https://example.test/release",
            keywords = listOf("kotlin", "ui"),
            articleSection = "Engineering",
            wordCount = 900
        ).toJsonLD()
        assertContains(article, "\"name\": \"Ada\"")
        assertContains(article, "\"wordCount\": 900")
        assertContains(article, "\"keywords\": [\"kotlin\", \"ui\"]")

        val minimalArticle = ArticleSchema(
            headline = "Short",
            author = ArticleSchema.Person("Ada"),
            datePublished = "2026-10-07"
        ).toJsonLD()
        assertFalse(minimalArticle.contains("dateModified"))
        assertFalse(minimalArticle.contains("publisher"))

        val product = ProductSchema(
            name = "Widget",
            description = "Useful widget",
            image = "https://example.test/widget.png",
            brand = "Summon",
            sku = "SKU-1",
            gtin = "00000001",
            offers = listOf(ProductSchema.OfferDetail(19.95, "USD", seller = "Summon", validUntil = "2027-01-01")),
            aggregateRating = ProductSchema.Rating(4.5, 9),
            review = listOf(ProductSchema.Review("Grace", "2026-10-01", 5, "Excellent"))
        ).toJsonLD()
        assertContains(product, "\"sku\": \"SKU-1\"")
        assertContains(product, "\"seller\": \"Summon\"")
        assertContains(product, "\"reviewBody\": \"Excellent\"")

        val minimalProduct = ProductSchema(
            name = "Widget",
            description = "Useful widget",
            image = "https://example.test/widget.png",
            brand = "Summon",
            offers = listOf(ProductSchema.OfferDetail(1.0, "USD"))
        ).toJsonLD()
        assertFalse(minimalProduct.contains("aggregateRating"))
        assertFalse(minimalProduct.contains("review"))
    }

    @Test
    fun listSchemasPreserveOrderingAndNestedAnswers() {
        val breadcrumbs = BreadcrumbListSchema(
            listOf(
                BreadcrumbListSchema.BreadcrumbItem("Home", "https://example.test"),
                BreadcrumbListSchema.BreadcrumbItem("Docs", "https://example.test/docs")
            )
        ).toJsonLD()
        assertContains(breadcrumbs, "\"position\": 1")
        assertContains(breadcrumbs, "\"position\": 2")

        val faq = FAQPageSchema(
            listOf(FAQPageSchema.QuestionAnswer("Private?", "Yes"))
        ).toJsonLD()
        assertContains(faq, "\"@type\": \"Question\"")
        assertContains(faq, "\"text\": \"Yes\"")
    }
}
