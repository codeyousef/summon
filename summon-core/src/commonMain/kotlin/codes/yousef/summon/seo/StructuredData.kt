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

    /**
     * Executes the put operation.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    fun put(key: String, value: String) {
        properties.add("\"$key\": \"${value.escapeJson()}\"")
    }

    /**
     * Executes the put operation.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    fun put(key: String, value: Int) {
        properties.add("\"$key\": $value")
    }

    /**
     * Executes the put operation.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    fun put(key: String, value: Double) {
        properties.add("\"$key\": $value")
    }

    /**
     * Executes the put operation.
     *
     * @param key Lookup key.
     * @param value Value to process.
     */
    fun put(key: String, value: Boolean) {
        properties.add("\"$key\": $value")
    }

    /**
     * Executes the put object operation.
     *
     * @param key Lookup key.
     * @param obj The obj value.
     */
    fun putObject(key: String, obj: JsonLDBuilder.() -> Unit) {
        val innerBuilder = JsonLDBuilder()
        innerBuilder.obj()
        properties.add("\"$key\": ${innerBuilder.build()}")
    }

    /**
     * Executes the put array operation.
     *
     * @param key Lookup key.
     * @param items The items value.
     */
    fun putArray(key: String, items: List<String>) {
        val jsonArray = items.joinToString(", ") { "\"${it.escapeJson()}\"" }
        properties.add("\"$key\": [$jsonArray]")
    }

    /**
     * Executes the put object array operation.
     *
     * @param key Lookup key.
     * @param items The items value.
     */
    fun putObjectArray(key: String, items: List<JsonLDBuilder.() -> Unit>) {
        val jsonArray = items.joinToString(", ") { item ->
            val innerBuilder = JsonLDBuilder()
            innerBuilder.item()
            innerBuilder.build()
        }
        properties.add("\"$key\": [$jsonArray]")
    }

    /**
     * Builds the operation.
     *
     * @return The resulting value.
     */
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

 * @property name Human-readable name.
 * @property url Target URL.
 * @property description The description value.
 * @property potentialAction The potential action value.
 * @property publisher The publisher value.
 * @property inLanguage The in language value.
 */
data class WebSiteSchema(
    val name: String,
    val url: String,
    val description: String? = null,
    val potentialAction: SearchAction? = null,
    val publisher: OrganizationSchema? = null,
    val inLanguage: String? = null
) : StructuredDataSchema() {
    /** Converts this value to JSON ld. */
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

    /**
     * Represents search action.
     *
     * @property target The target value.
     * @property queryInput The query input value.
     */
    data class SearchAction(
        val target: String,
        val queryInput: String = "required name=search_term_string"
    )
}

/**
 * WebApplication schema for web apps.

 * @property name Human-readable name.
 * @property description The description value.
 * @property url Target URL.
 * @property applicationCategory The application category value.
 * @property operatingSystem The operating system value.
 * @property offers The offers value.
 * @property aggregateRating The aggregate rating value.
 * @property author The author value.
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
    /** Converts this value to JSON ld. */
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

    /**
     * Represents offer.
     *
     * @property price The price value.
     * @property priceCurrency The price currency value.
     * @property availability The availability value.
     */
    data class Offer(
        val price: String,
        val priceCurrency: String,
        val availability: String? = "https://schema.org/InStock"
    )

    /**
     * Represents aggregate rating.
     *
     * @property ratingValue The rating value value.
     * @property ratingCount The rating count value.
     * @property bestRating The best rating value.
     * @property worstRating The worst rating value.
     */
    data class AggregateRating(
        val ratingValue: Double,
        val ratingCount: Int,
        val bestRating: Double? = 5.0,
        val worstRating: Double? = 1.0
    )
}

/**
 * Organization schema for companies/organizations.

 * @property name Human-readable name.
 * @property url Target URL.
 * @property logo The logo value.
 * @property description The description value.
 * @property email The email value.
 * @property telephone The telephone value.
 * @property address The address value.
 * @property sameAs The same as value.
 * @property contactPoints The contact points value.
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
    /** Converts this value to JSON ld. */
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

    /**
     * Represents postal address.
     *
     * @property streetAddress The street address value.
     * @property addressLocality The address locality value.
     * @property addressRegion The address region value.
     * @property postalCode The postal code value.
     * @property addressCountry The address country value.
     */
    data class PostalAddress(
        val streetAddress: String? = null,
        val addressLocality: String? = null,
        val addressRegion: String? = null,
        val postalCode: String? = null,
        val addressCountry: String? = null
    )

    /**
     * Represents contact point.
     *
     * @property contactType The contact type value.
     * @property telephone The telephone value.
     * @property email The email value.
     * @property areaServed The area served value.
     * @property availableLanguage The available language value.
     */
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

 * @property headline The headline value.
 * @property description The description value.
 * @property author The author value.
 * @property datePublished The date published value.
 * @property dateModified The date modified value.
 * @property image The image value.
 * @property publisher The publisher value.
 * @property mainEntityOfPage The main entity of page value.
 * @property keywords The keywords value.
 * @property articleSection The article section value.
 * @property wordCount The word count value.
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
    /** Converts this value to JSON ld. */
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

    /**
     * Represents person.
     *
     * @property name Human-readable name.
     * @property url Target URL.
     */
    data class Person(
        val name: String,
        val url: String? = null
    )
}

/**
 * Product schema for e-commerce products.

 * @property name Human-readable name.
 * @property description The description value.
 * @property image The image value.
 * @property brand The brand value.
 * @property sku The sku value.
 * @property gtin The gtin value.
 * @property offers The offers value.
 * @property aggregateRating The aggregate rating value.
 * @property review The review value.
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
    /** Converts this value to JSON ld. */
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

    /**
     * Represents offer detail.
     *
     * @property price The price value.
     * @property priceCurrency The price currency value.
     * @property availability The availability value.
     * @property seller The seller value.
     * @property validUntil The valid until value.
     */
    data class OfferDetail(
        val price: Double,
        val priceCurrency: String,
        val availability: String = "https://schema.org/InStock",
        val seller: String? = null,
        val validUntil: String? = null
    )

    /**
     * Represents rating.
     *
     * @property ratingValue The rating value value.
     * @property reviewCount The review count value.
     */
    data class Rating(
        val ratingValue: Double,
        val reviewCount: Int
    )

    /**
     * Represents review.
     *
     * @property author The author value.
     * @property datePublished The date published value.
     * @property rating The rating value.
     * @property reviewBody The review body value.
     */
    data class Review(
        val author: String,
        val datePublished: String,
        val rating: Int,
        val reviewBody: String? = null
    )
}

/**
 * BreadcrumbList schema for navigation breadcrumbs.

 * @property items The items value.
 */
data class BreadcrumbListSchema(
    val items: List<BreadcrumbItem>
) : StructuredDataSchema() {
    /** Converts this value to JSON ld. */
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

    /**
     * Represents breadcrumb item.
     *
     * @property name Human-readable name.
     * @property url Target URL.
     */
    data class BreadcrumbItem(
        val name: String,
        val url: String
    )
}

/**
 * FAQPage schema for FAQ sections.

 * @property questions The questions value.
 */
data class FAQPageSchema(
    val questions: List<QuestionAnswer>
) : StructuredDataSchema() {
    /** Converts this value to JSON ld. */
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

    /**
     * Represents question answer.
     *
     * @property question The question value.
     * @property answer The answer value.
     */
    data class QuestionAnswer(
        val question: String,
        val answer: String
    )
}