package code.yousef.summon.seo.routes

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.splitCompat

/**
 * The StructuredData object provides utilities for generating JSON-LD structured data
 * for better SEO and rich search results.
 */
object StructuredData {
    /**
     * Creates a WebPage structured data for better SEO.
     *
     * @param name The name of the web page
     * @param description The description of the web page
     * @param url The URL of the web page
     * @return A Composable that adds the structured data to the document
     */
    @Composable
    fun webPage(
        name: String,
        description: String,
        url: String
    ) {
        WebPageStructuredData(name, description, url)
    }

    /**
     * Creates an Organization structured data for better organization representation in search.
     *
     * @param name The name of the organization
     * @param url The URL of the organization's website
     * @param logo The URL of the organization's logo
     * @return A Composable that adds the structured data to the document
     */
    @Composable
    fun organization(
        name: String,
        url: String,
        logo: String
    ) {
        OrganizationStructuredData(name, url, logo)
    }

    /**
     * Creates a Product structured data for better product representation in search.
     *
     * @param name The name of the product
     * @param description The description of the product
     * @param image The URL of the product's image
     * @param price The price of the product
     * @param currency The currency of the price (default: USD)
     * @return A Composable that adds the structured data to the document
     */
    @Composable
    fun product(
        name: String,
        description: String,
        image: String,
        price: String,
        currency: String = "USD"
    ) {
        ProductStructuredData(name, description, image, price, currency)
    }

    /**
     * Creates a BreadcrumbList structured data for better navigation understanding.
     *
     * @param items A list of name-URL pairs representing the breadcrumb trail
     * @return A Composable that adds the structured data to the document
     */
    @Composable
    fun breadcrumbs(items: List<Pair<String, String>>) {
        BreadcrumbsStructuredData(items)
    }

    /**
     * Creates an Article structured data for better article representation in search.
     *
     * @param headline The headline of the article
     * @param author The name of the author
     * @param datePublished The date the article was published (ISO 8601 format)
     * @param dateModified The date the article was last modified (ISO 8601 format)
     * @param image The URL of the article's featured image
     * @param url The URL of the article
     * @param description The description of the article
     * @param publisher The name of the publisher
     * @param publisherLogo The URL of the publisher's logo
     * @return A Composable that adds the structured data to the document
     */
    @Composable
    fun article(
        headline: String,
        author: String,
        datePublished: String,
        dateModified: String = datePublished,
        image: String,
        url: String,
        description: String,
        publisher: String,
        publisherLogo: String
    ) {
        val jsonLd = """
        {
            "@context": "https://schema.org",
            "@type": "Article",
            "headline": "$headline",
            "author": {
                "@type": "Person",
                "name": "$author"
            },
            "datePublished": "$datePublished",
            "dateModified": "$dateModified",
            "image": "$image",
            "url": "$url",
            "description": "$description",
            "publisher": {
                "@type": "Organization",
                "name": "$publisher",
                "logo": {
                    "@type": "ImageObject",
                    "url": "$publisherLogo"
                }
            }
        }
        """
        JsonLdStructuredData(jsonLd)
    }

    /**
     * Creates a LocalBusiness structured data for better business representation in search.
     *
     * @param name The name of the business
     * @param url The URL of the business's website
     * @param description The description of the business
     * @param address The address of the business
     * @param telephone The telephone number of the business
     * @param logo The URL of the business's logo
     * @param image The URL of an image of the business
     * @param priceRange The price range of the business (e.g. "$", "$$", "$$$")
     * @param openingHours A map of day of week to opening hours (e.g. "Monday" to "9:00-17:00")
     * @return A Composable that adds the structured data to the document
     */
    @Composable
    fun localBusiness(
        name: String,
        url: String,
        description: String,
        address: Map<String, String>,
        telephone: String,
        logo: String? = null,
        image: String? = null,
        priceRange: String? = null,
        openingHours: Map<String, String> = emptyMap()
    ) {
        val streetAddress = address["streetAddress"] ?: ""
        val addressLocality = address["addressLocality"] ?: ""
        val addressRegion = address["addressRegion"] ?: ""
        val postalCode = address["postalCode"] ?: ""
        val addressCountry = address["addressCountry"] ?: ""

        val hoursSpecification = if (openingHours.isNotEmpty()) {
            openingHours.entries.joinToString(",") { (day, hours) ->
                """
                {
                    "@type": "OpeningHoursSpecification",
                    "dayOfWeek": "$day",
                    "opens": "${hours.splitCompat("-")[0].trim()}",
                    "closes": "${hours.splitCompat("-").getOrElse(1) { "17:00" }.trim()}"
                }
                """
            }
        } else {
            ""
        }

        val jsonLd = """
        {
            "@context": "https://schema.org",
            "@type": "LocalBusiness",
            "name": "$name",
            "url": "$url",
            "description": "$description",
            "address": {
                "@type": "PostalAddress",
                "streetAddress": "$streetAddress",
                "addressLocality": "$addressLocality",
                "addressRegion": "$addressRegion",
                "postalCode": "$postalCode",
                "addressCountry": "$addressCountry"
            },
            "telephone": "$telephone"
            ${if (logo != null) """, "logo": "$logo"""" else ""}
            ${if (image != null) """, "image": "$image"""" else ""}
            ${if (priceRange != null) """, "priceRange": "$priceRange"""" else ""}
            ${if (openingHours.isNotEmpty()) """, "openingHoursSpecification": [$hoursSpecification]""" else ""}
        }
        """
        JsonLdStructuredData(jsonLd)
    }
} 