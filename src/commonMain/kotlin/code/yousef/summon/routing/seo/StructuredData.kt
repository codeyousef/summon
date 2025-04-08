package code.yousef.summon.routing.seo


import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.SideEffect

import kotlinx.html.HEAD
import kotlinx.html.script
import kotlinx.html.unsafe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

/**
 * Composable function to embed JSON-LD structured data in the document head.
 * Helps search engines understand the content of the page.
 *
 * @param jsonLdString The structured data formatted as a JSON-LD string.
 */
@Composable
fun JsonLdStructuredData(jsonLdString: String) {
    val composer = CompositionLocal.currentComposer

    SideEffect {
        println("JsonLdStructuredData SideEffect: Adding JSON-LD script.")
        // TODO: Implement platform-specific head manipulation.
        // val scriptContent = escapeHtmlEntities(jsonLdString) // Ensure content is safe
        // PlatformRendererProvider.code.yousef.summon.runtime.PlatformRendererProvider.getPlatformRenderer().addHeadElement("<script type=\"application/ld+json\">$scriptContent</script>")
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
 * StructuredData component for generating JSON-LD structured data
 * This helps search engines understand the content of the page
 */
class StructuredData(
    private val jsonLd: JsonElement
) : Composable {

    @Suppress("UNCHECKED_CAST")
    override fun <T> compose(receiver: T): T {
        if (receiver is HEAD) {
            receiver.apply {
                script(type = "application/ld+json") {
                    unsafe {
                        +Json.encodeToString(jsonLd)
                    }
                }
            }
        }

        return receiver
    }

    companion object {
        private val json = Json {
            prettyPrint = false
            encodeDefaults = true
        }

        /**
         * Creates a WebPage structured data object
         */
        fun webPage(
            name: String,
            description: String,
            url: String
        ): StructuredData {
            val data = buildJsonObject {
                put("@context", "https://schema.org")
                put("@type", "WebPage")
                put("name", name)
                put("description", description)
                put("url", url)
            }

            return StructuredData(data)
        }

        /**
         * Creates a BreadcrumbList structured data object
         */
        fun breadcrumbs(items: List<Pair<String, String>>): StructuredData {
            val data = buildJsonObject {
                put("@context", "https://schema.org")
                put("@type", "BreadcrumbList")
                put("itemListElement", buildJsonArray {
                    items.forEachIndexed { index, (name, url) ->
                        addJsonObject {
                            put("@type", "ListItem")
                            put("position", (index + 1).toString())
                            put("name", name)
                            put("item", url)
                        }
                    }
                })
            }

            return StructuredData(data)
        }

        /**
         * Creates an Organization structured data object
         */
        fun organization(
            name: String,
            url: String,
            logo: String? = null,
            contactPoint: Map<String, String>? = null
        ): StructuredData {
            val data = buildJsonObject {
                put("@context", "https://schema.org")
                put("@type", "Organization")
                put("name", name)
                put("url", url)

                logo?.let { put("logo", it) }

                contactPoint?.let {
                    putJsonObject("contactPoint") {
                        put("@type", "ContactPoint")
                        put("contactType", it["contactType"] ?: "customer service")
                        put("telephone", it["telephone"] ?: "")
                        put("email", it["email"] ?: "")
                    }
                }
            }

            return StructuredData(data)
        }

        /**
         * Creates a Product structured data object
         */
        fun product(
            name: String,
            description: String,
            image: String,
            price: String,
            currency: String = "USD"
        ): StructuredData {
            val data = buildJsonObject {
                put("@context", "https://schema.org")
                put("@type", "Product")
                put("name", name)
                put("description", description)
                put("image", image)
                putJsonObject("offers") {
                    put("@type", "Offer")
                    put("price", price)
                    put("priceCurrency", currency)
                    put("availability", "https://schema.org/InStock")
                }
            }

            return StructuredData(data)
        }
    }
} 
