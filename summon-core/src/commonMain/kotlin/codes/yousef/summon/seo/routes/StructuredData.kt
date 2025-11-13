package codes.yousef.summon.seo.routes


import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.mapOfCompat
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.SideEffect
import kotlinx.serialization.json.*

/**
 * Composable function to embed JSON-LD structured data in the document head.
 * Helps search engines understand the content of the page.
 *
 * @param jsonLdString The structured data formatted as a JSON-LD string.
 */
@Composable
fun JsonLdStructuredData(jsonLdString: String) {
    val renderer = LocalPlatformRenderer.current

    SideEffect {
        val scriptContent = escapeHtmlEntities(jsonLdString) // Ensure content is safe
        renderer.addHeadElement("<script type=\"application/ld+json\">$scriptContent</script>")
    }

    // Renders no UI.
}

// Helper to escape HTML entities (basic example)
private fun escapeHtmlEntities(str: String): String {
    return str
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}

/**
 * Composable function for rendering JSON-LD structured data
 * This helps search engines understand the content of the page
 */
@Composable
fun StructuredData(jsonLd: JsonElement) {
    val renderer = LocalPlatformRenderer.current

    SideEffect {
        val scriptContent = escapeHtmlEntities(Json.encodeToString(JsonElement.serializer(), jsonLd))
        renderer.addHeadElement("<script type=\"application/ld+json\">$scriptContent</script>")
    }
}

/**
 * Creates a WebPage structured data object
 */
@Composable
fun WebPageStructuredData(
    name: String,
    description: String,
    url: String
) {
    val data = JsonObject(
        mapOfCompat(
            "@context" to JsonPrimitive("https://schema.org"),
            "@type" to JsonPrimitive("WebPage"),
            "name" to JsonPrimitive(name),
            "description" to JsonPrimitive(description),
            "url" to JsonPrimitive(url)
        )
    )

    StructuredData(data)
}

/**
 * Creates a BreadcrumbList structured data object
 */
@Composable
fun BreadcrumbsStructuredData(items: List<Pair<String, String>>) {
    val itemListElements = items.mapIndexed { index, (name, url) ->
        JsonObject(
            mapOfCompat(
                "@type" to JsonPrimitive("ListItem"),
                "position" to JsonPrimitive((index + 1).toString()),
                "name" to JsonPrimitive(name),
                "item" to JsonPrimitive(url)
            )
        )
    }

    val data = JsonObject(
        mapOfCompat(
            "@context" to JsonPrimitive("https://schema.org"),
            "@type" to JsonPrimitive("BreadcrumbList"),
            "itemListElement" to JsonArray(itemListElements)
        )
    )

    StructuredData(data)
}

/**
 * Creates an Organization structured data object
 */
@Composable
fun OrganizationStructuredData(
    name: String,
    url: String,
    logo: String? = null,
    contactPoint: Map<String, String>? = null
) {
    val data = JsonObject(
        mapOfCompat(
            "@context" to JsonPrimitive("https://schema.org"),
            "@type" to JsonPrimitive("Organization"),
            "name" to JsonPrimitive(name),
            "url" to JsonPrimitive(url),
            "logo" to (logo?.let { JsonPrimitive(it) } ?: JsonNull),
            "contactPoint" to (contactPoint?.let {
                JsonObject(
                    mapOfCompat(
                        "@type" to JsonPrimitive("ContactPoint"),
                        "contactType" to JsonPrimitive(it["contactType"] ?: "customer service"),
                        "telephone" to JsonPrimitive(it["telephone"] ?: ""),
                        "email" to JsonPrimitive(it["email"] ?: "")
                    )
                )
            } ?: JsonNull)
        ))

    StructuredData(data)
}

/**
 * Creates a Product structured data object
 */
@Composable
fun ProductStructuredData(
    name: String,
    description: String,
    image: String,
    price: String,
    currency: String = "USD"
) {
    val data = JsonObject(
        mapOfCompat(
            "@context" to JsonPrimitive("https://schema.org"),
            "@type" to JsonPrimitive("Product"),
            "name" to JsonPrimitive(name),
            "description" to JsonPrimitive(description),
            "image" to JsonPrimitive(image),
            "offers" to JsonObject(
                mapOfCompat(
                    "@type" to JsonPrimitive("Offer"),
                    "price" to JsonPrimitive(price),
                    "priceCurrency" to JsonPrimitive(currency),
                    "availability" to JsonPrimitive("https://schema.org/InStock")
                )
            )
        )
    )

    StructuredData(data)
} 
