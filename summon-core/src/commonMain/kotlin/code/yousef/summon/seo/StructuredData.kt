package codes.yousef.summon.seo

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Base class for structured data schemas.
 * Provides type-safe JSON-LD generation for search engine understanding.
 */
sealed class StructuredDataSchema {
    /**
     * Converts this schema to JSON-LD format.
     */
    abstract fun toJsonLD(): String

    /**
     * Helper to build JSON-LD strings safely.
     */
    protected fun buildJsonLD(builder: JsonLDBuilder.() -> Unit): String {
        val jsonBuilder = JsonLDBuilder()
        jsonBuilder.builder()
        return jsonBuilder.build()
    }
}

/**
 * Builder for creating JSON-LD strings in a type-safe way.
 */
class JsonLDBuilder {
    private val properties = mutableListOf<String>()

    fun put(key: String, value: String) {
        properties.add("\"$key\": \"${value.escapeJson()}\"")
    }

    fun put(key: String, value: Int) {
        properties.add("\"$key\": $value")
    }

    fun put(key: String, value: Double) {
        properties.add("\"$key\": $value")
    }

    fun put(key: String, value: Boolean) {
        properties.add("\"$key\": $value")
    }

    fun putObject(key: String, obj: JsonLDBuilder.() -> Unit) {
        val innerBuilder = JsonLDBuilder()
        innerBuilder.obj()
        properties.add("\"$key\": ${innerBuilder.build()}")
    }

    fun putArray(key: String, items: List<String>) {
        val jsonArray = items.joinToString(", ") { "\"${it.escapeJson()}\"" }
        properties.add("\"$key\": [$jsonArray]")
    }

    fun putObjectArray(key: String, items: List<JsonLDBuilder.() -> Unit>) {
        val jsonArray = items.joinToString(", ") { item ->
            val innerBuilder = JsonLDBuilder()
            innerBuilder.item()
            innerBuilder.build()
        }
        properties.add("\"$key\": [$jsonArray]")
    }

    fun build(): String = "{ ${properties.joinToString(", ")} }"

    private fun String.escapeJson(): String = this
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}

/**
 * Composable component for rendering structured data in JSON-LD format.
 * This helps search engines understand the content and context of your pages.
 *
 * @param schema The structured data schema to render
 */
@Composable
fun StructuredData(schema: StructuredDataSchema) {
    val renderer = LocalPlatformRenderer.current

    renderer.renderHeadElements {
        script(
            type = "application/ld+json",
            content = schema.toJsonLD()
        )
    }
}

/**
 * Render multiple structured data schemas.
 */
@Composable
fun StructuredData(vararg schemas: StructuredDataSchema) {
    schemas.forEach { schema ->
        StructuredData(schema)
    }
}

// ============= Schema Implementations =============

/**
 * WebSite schema for the main website.
 */
data class WebSiteSchema(
    val name: String,
    val url: String,
    val description: String? = null,
    val potentialAction: SearchAction? = null,
    val publisher: OrganizationSchema? = null,
    val inLanguage: String? = null
) : StructuredDataSchema() {
    override fun toJsonLD() = buildJsonLD {
        put("@context", "https://schema.org")
        put("@type", "WebSite")
        put("name", name)
        put("url", url)
        description?.let { put("description", it) }
        inLanguage?.let { put("inLanguage", it) }

        potentialAction?.let {
            putObject("potentialAction") {
                put("@type", "SearchAction")
                put("target", it.target)
                put("query-input", it.queryInput)
            }
        }

        publisher?.let { org ->
            putObject("publisher") {
                put("@type", "Organization")
                put("name", org.name)
                org.url?.let { put("url", it) }
                org.logo?.let { put("logo", it) }
            }
        }
    }

    data class SearchAction(
        val target: String,
        val queryInput: String = "required name=search_term_string"
    )
}

/**
 * WebApplication schema for web apps.
 */
data class WebApplicationSchema(
    val name: String,
    val description: String,
    val url: String? = null,
    val applicationCategory: String? = null,
    val operatingSystem: String? = null,
    val offers: Offer? = null,
    val aggregateRating: AggregateRating? = null,
    val author: OrganizationSchema? = null
) : StructuredDataSchema() {
    override fun toJsonLD() = buildJsonLD {
        put("@context", "https://schema.org")
        put("@type", "WebApplication")
        put("name", name)
        put("description", description)
        url?.let { put("url", it) }
        applicationCategory?.let { put("applicationCategory", it) }
        operatingSystem?.let { put("operatingSystem", it) }

        offers?.let { offer ->
            putObject("offers") {
                put("@type", "Offer")
                put("price", offer.price)
                put("priceCurrency", offer.priceCurrency)
                offer.availability?.let { put("availability", it) }
            }
        }

        aggregateRating?.let { rating ->
            putObject("aggregateRating") {
                put("@type", "AggregateRating")
                put("ratingValue", rating.ratingValue)
                put("ratingCount", rating.ratingCount)
                rating.bestRating?.let { put("bestRating", it) }
                rating.worstRating?.let { put("worstRating", it) }
            }
        }

        author?.let { org ->
            putObject("author") {
                put("@type", "Organization")
                put("name", org.name)
                org.url?.let { put("url", it) }
            }
        }
    }

    data class Offer(
        val price: String,
        val priceCurrency: String,
        val availability: String? = "https://schema.org/InStock"
    )

    data class AggregateRating(
        val ratingValue: Double,
        val ratingCount: Int,
        val bestRating: Double? = 5.0,
        val worstRating: Double? = 1.0
    )
}

/**
 * Organization schema for companies/organizations.
 */
data class OrganizationSchema(
    val name: String,
    val url: String? = null,
    val logo: String? = null,
    val description: String? = null,
    val email: String? = null,
    val telephone: String? = null,
    val address: PostalAddress? = null,
    val sameAs: List<String>? = null,
    val contactPoints: List<ContactPoint>? = null
) : StructuredDataSchema() {
    override fun toJsonLD() = buildJsonLD {
        put("@context", "https://schema.org")
        put("@type", "Organization")
        put("name", name)
        url?.let { put("url", it) }
        logo?.let { put("logo", it) }
        description?.let { put("description", it) }
        email?.let { put("email", it) }
        telephone?.let { put("telephone", it) }

        address?.let { addr ->
            putObject("address") {
                put("@type", "PostalAddress")
                addr.streetAddress?.let { put("streetAddress", it) }
                addr.addressLocality?.let { put("addressLocality", it) }
                addr.addressRegion?.let { put("addressRegion", it) }
                addr.postalCode?.let { put("postalCode", it) }
                addr.addressCountry?.let { put("addressCountry", it) }
            }
        }

        sameAs?.let { putArray("sameAs", it) }

        contactPoints?.let { points ->
            putObjectArray("contactPoint", points.map { point ->
                {
                    put("@type", "ContactPoint")
                    put("contactType", point.contactType)
                    point.telephone?.let { put("telephone", it) }
                    point.email?.let { put("email", it) }
                    point.areaServed?.let { put("areaServed", it) }
                    point.availableLanguage?.let { put("availableLanguage", it) }
                }
            })
        }
    }

    data class PostalAddress(
        val streetAddress: String? = null,
        val addressLocality: String? = null,
        val addressRegion: String? = null,
        val postalCode: String? = null,
        val addressCountry: String? = null
    )

    data class ContactPoint(
        val contactType: String,
        val telephone: String? = null,
        val email: String? = null,
        val areaServed: String? = null,
        val availableLanguage: String? = null
    )
}

/**
 * Article schema for blog posts and articles.
 */
data class ArticleSchema(
    val headline: String,
    val description: String? = null,
    val author: Person,
    val datePublished: String,
    val dateModified: String? = null,
    val image: String? = null,
    val publisher: OrganizationSchema? = null,
    val mainEntityOfPage: String? = null,
    val keywords: List<String>? = null,
    val articleSection: String? = null,
    val wordCount: Int? = null
) : StructuredDataSchema() {
    override fun toJsonLD() = buildJsonLD {
        put("@context", "https://schema.org")
        put("@type", "Article")
        put("headline", headline)
        description?.let { put("description", it) }

        putObject("author") {
            put("@type", "Person")
            put("name", author.name)
            author.url?.let { put("url", it) }
        }

        put("datePublished", datePublished)
        dateModified?.let { put("dateModified", it) }
        image?.let { put("image", it) }
        mainEntityOfPage?.let { put("mainEntityOfPage", it) }
        articleSection?.let { put("articleSection", it) }
        wordCount?.let { put("wordCount", it) }
        keywords?.let { putArray("keywords", it) }

        publisher?.let { org ->
            putObject("publisher") {
                put("@type", "Organization")
                put("name", org.name)
                org.logo?.let { put("logo", it) }
            }
        }
    }

    data class Person(
        val name: String,
        val url: String? = null
    )
}

/**
 * Product schema for e-commerce products.
 */
data class ProductSchema(
    val name: String,
    val description: String,
    val image: String,
    val brand: String,
    val sku: String? = null,
    val gtin: String? = null,
    val offers: List<OfferDetail>,
    val aggregateRating: Rating? = null,
    val review: List<Review>? = null
) : StructuredDataSchema() {
    override fun toJsonLD() = buildJsonLD {
        put("@context", "https://schema.org")
        put("@type", "Product")
        put("name", name)
        put("description", description)
        put("image", image)

        putObject("brand") {
            put("@type", "Brand")
            put("name", brand)
        }

        sku?.let { put("sku", it) }
        gtin?.let { put("gtin", it) }

        putObjectArray("offers", offers.map { offer ->
            {
                put("@type", "Offer")
                put("price", offer.price)
                put("priceCurrency", offer.priceCurrency)
                put("availability", offer.availability)
                offer.seller?.let { put("seller", it) }
                offer.validUntil?.let { put("priceValidUntil", it) }
            }
        })

        aggregateRating?.let { rating ->
            putObject("aggregateRating") {
                put("@type", "AggregateRating")
                put("ratingValue", rating.ratingValue)
                put("reviewCount", rating.reviewCount)
            }
        }

        review?.let { reviews ->
            putObjectArray("review", reviews.map { r ->
                {
                    put("@type", "Review")
                    put("author", r.author)
                    put("datePublished", r.datePublished)
                    putObject("reviewRating") {
                        put("@type", "Rating")
                        put("ratingValue", r.rating)
                        put("bestRating", 5)
                    }
                    r.reviewBody?.let { put("reviewBody", it) }
                }
            })
        }
    }

    data class OfferDetail(
        val price: Double,
        val priceCurrency: String,
        val availability: String = "https://schema.org/InStock",
        val seller: String? = null,
        val validUntil: String? = null
    )

    data class Rating(
        val ratingValue: Double,
        val reviewCount: Int
    )

    data class Review(
        val author: String,
        val datePublished: String,
        val rating: Int,
        val reviewBody: String? = null
    )
}

/**
 * BreadcrumbList schema for navigation breadcrumbs.
 */
data class BreadcrumbListSchema(
    val items: List<BreadcrumbItem>
) : StructuredDataSchema() {
    override fun toJsonLD() = buildJsonLD {
        put("@context", "https://schema.org")
        put("@type", "BreadcrumbList")

        putObjectArray("itemListElement", items.mapIndexed { index, item ->
            {
                put("@type", "ListItem")
                put("position", index + 1)
                put("name", item.name)
                put("item", item.url)
            }
        })
    }

    data class BreadcrumbItem(
        val name: String,
        val url: String
    )
}

/**
 * FAQPage schema for FAQ sections.
 */
data class FAQPageSchema(
    val questions: List<QuestionAnswer>
) : StructuredDataSchema() {
    override fun toJsonLD() = buildJsonLD {
        put("@context", "https://schema.org")
        put("@type", "FAQPage")

        putObjectArray("mainEntity", questions.map { qa ->
            {
                put("@type", "Question")
                put("name", qa.question)
                putObject("acceptedAnswer") {
                    put("@type", "Answer")
                    put("text", qa.answer)
                }
            }
        })
    }

    data class QuestionAnswer(
        val question: String,
        val answer: String
    )
}