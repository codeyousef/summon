package code.yousef.summon.integrations.quarkus.htmx

import code.yousef.summon.modifier.Modifier
import kotlinx.html.CommonAttributeGroupFacade

/**
 * Handler for HTMX attributes in Summon components.
 * This class processes HTMX-specific attributes and applies them correctly to HTML elements.
 */
class HtmxAttributeHandler {
    companion object {
        // Prefix for HTML attributes in the Modifier styles map
        const val HTML_ATTRIBUTE_PREFIX = "__html_attr_"

        // List of common HTMX attributes
        private val HTMX_ATTRIBUTES = listOf(
            "hx-get",
            "hx-post",
            "hx-put",
            "hx-delete",
            "hx-patch",
            "hx-target",
            "hx-swap",
            "hx-trigger",
            "hx-indicator",
            "hx-push-url",
            "hx-select",
            "hx-select-oob",
            "hx-headers",
            "hx-vals",
            "hx-confirm",
            "hx-prompt",
            "hx-boost",
            "hx-ext",
            "hx-params",
            "hx-sync",
            "hx-validate"
        )

        /**
         * Checks if a style key is an HTMX attribute.
         *
         * @param key The style key to check
         * @return True if the key is an HTMX attribute, false otherwise
         */
        fun isHtmxAttribute(key: String): Boolean {
            return HTMX_ATTRIBUTES.any { key.startsWith(it) }
        }

        /**
         * Applies HTMX attributes from a Modifier to an HTML element.
         *
         * @param element The HTML element to apply the attributes to
         * @param modifier The Modifier containing the attributes
         */
        fun applyHtmxAttributes(element: CommonAttributeGroupFacade, modifier: Modifier) {
            // Extract HTML attributes (both explicit and HTMX attributes)
            val htmlAttributes = modifier.styles.filter { (key, _) ->
                key.startsWith(HTML_ATTRIBUTE_PREFIX) || isHtmxAttribute(key)
            }

            // Apply HTML attributes as separate attributes
            htmlAttributes.forEach { (key, value) ->
                val attributeName = if (key.startsWith(HTML_ATTRIBUTE_PREFIX)) {
                    key.removePrefix(HTML_ATTRIBUTE_PREFIX)
                } else {
                    key
                }
                element.attributes[attributeName] = value
            }
        }

        /**
         * Filters out HTMX attributes from a Modifier's styles.
         *
         * @param modifier The Modifier to filter
         * @return A new Modifier without HTMX attributes
         */
        fun filterHtmxAttributes(modifier: Modifier): Modifier {
            val filteredStyles = modifier.styles.filterNot { (key, _) ->
                key.startsWith(HTML_ATTRIBUTE_PREFIX) || isHtmxAttribute(key)
            }
            return Modifier(filteredStyles)
        }
    }
}

/**
 * Extension function for Modifier to add an HTML attribute.
 *
 * @param name The attribute name
 * @param value The attribute value
 * @return A new Modifier with the added HTML attribute
 */
fun Modifier.htmlAttribute(name: String, value: String): Modifier =
    this.style("${HtmxAttributeHandler.HTML_ATTRIBUTE_PREFIX}$name", value)

/**
 * Extension function for Modifier to add an HTMX attribute.
 *
 * @param name The HTMX attribute name (without the "hx-" prefix)
 * @param value The attribute value
 * @return A new Modifier with the added HTMX attribute
 */
fun Modifier.htmx(name: String, value: String): Modifier =
    this.style("hx-$name", value)

/**
 * Extension function for Modifier to add common HTMX attributes.
 *
 * @param get The URL to GET (for hx-get)
 * @param target The target element to update (for hx-target)
 * @param swap The swap method to use (for hx-swap)
 * @param trigger The trigger event (for hx-trigger)
 * @return A new Modifier with the added HTMX attributes
 */
fun Modifier.htmxGet(
    get: String,
    target: String? = null,
    swap: String? = null,
    trigger: String? = null
): Modifier {
    var result = this.style("hx-get", get)
    if (target != null) result = result.style("hx-target", target)
    if (swap != null) result = result.style("hx-swap", swap)
    if (trigger != null) result = result.style("hx-trigger", trigger)
    return result
}

/**
 * Extension function for Modifier to add common HTMX attributes for POST requests.
 *
 * @param post The URL to POST to (for hx-post)
 * @param target The target element to update (for hx-target)
 * @param swap The swap method to use (for hx-swap)
 * @param trigger The trigger event (for hx-trigger)
 * @return A new Modifier with the added HTMX attributes
 */
fun Modifier.htmxPost(
    post: String,
    target: String? = null,
    swap: String? = null,
    trigger: String? = null
): Modifier {
    var result = this.style("hx-post", post)
    if (target != null) result = result.style("hx-target", target)
    if (swap != null) result = result.style("hx-swap", swap)
    if (trigger != null) result = result.style("hx-trigger", trigger)
    return result
}