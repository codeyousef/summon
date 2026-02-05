package codes.yousef.summon.runtime

import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLStyleElement

/**
 * Manages dynamic CSS injection for pseudo-selectors, media queries, and other CSS rules
 * that can't be applied directly as inline styles.
 */
object StyleInjector {
    private val injectedStyles = mutableMapOf<String, HTMLStyleElement>()
    private val elementStyleIds = mutableMapOf<Element, MutableSet<String>>()

    /**
     * Generates a unique class name for scoped styles
     */
    private fun generateUniqueClass(prefix: String): String {
        return "$prefix-${js("Math.random().toString(36).substring(2, 10)") as String}"
    }

    /**
     * Injects CSS for pseudo-selector styles and returns the class name
     */
    fun injectPseudoSelectorStyles(element: Element, pseudoSelector: String, styles: Map<String, String>): String {
        val uniqueClass = generateUniqueClass("pseudo-$pseudoSelector")
        element.classList.add(uniqueClass)

        val cssRules = styles.entries.joinToString("\n  ") { "${it.key}: ${it.value};" }
        val cssText = ".$uniqueClass$pseudoSelector {\n  $cssRules\n}"

        val styleId = "pseudo-$uniqueClass"
        injectStyle(styleId, cssText)
        trackElementStyle(element, styleId)

        return uniqueClass
    }

    /**
     * Injects CSS for media query styles and returns the class name
     */
    fun injectMediaQueryStyles(element: Element, mediaQuery: String, styles: Map<String, String>): String {
        val uniqueClass = generateUniqueClass("media")
        element.classList.add(uniqueClass)

        val cssRules = styles.entries.joinToString("\n    ") { "${it.key}: ${it.value};" }
        val cssText = "@media $mediaQuery {\n  .$uniqueClass {\n    $cssRules\n  }\n}"

        val styleId = "media-$uniqueClass"
        injectStyle(styleId, cssText)
        trackElementStyle(element, styleId)

        return uniqueClass
    }

    /**
     * Injects a style element with the given ID and CSS text
     */
    private fun injectStyle(styleId: String, cssText: String) {
        if (injectedStyles.containsKey(styleId)) {
            // Style already exists, update it
            injectedStyles[styleId]?.textContent = cssText
        } else {
            // Create new style element
            val styleElement = document.createElement("style") as HTMLStyleElement
            styleElement.setAttribute("data-summon-style-id", styleId)
            styleElement.textContent = cssText
            document.head?.appendChild(styleElement)
            injectedStyles[styleId] = styleElement
        }
    }

    /**
     * Tracks which styles belong to which element for cleanup
     */
    private fun trackElementStyle(element: Element, styleId: String) {
        val elementStyles = elementStyleIds.getOrPut(element) { mutableSetOf() }
        elementStyles.add(styleId)
    }

    /**
     * Cleans up styles associated with an element when it's removed
     */
    fun cleanupElementStyles(element: Element) {
        elementStyleIds[element]?.forEach { styleId ->
            injectedStyles[styleId]?.remove()
            injectedStyles.remove(styleId)
        }
        elementStyleIds.remove(element)
    }

    /**
     * Converts camelCase to kebab-case for CSS property names
     */
    private fun toKebabCase(str: String): String {
        return str.replace(Regex("([a-z])([A-Z])")) { matchResult ->
            "${matchResult.groupValues[1]}-${matchResult.groupValues[2].lowercase()}"
        }.lowercase()
    }

    /**
     * Injects CSS for scoped style selectors (descendant, child, sibling combinators)
     * and returns the generated unique class name.
     *
     * @param element The host element to scope styles from
     * @param selectorType The type of CSS combinator (DESCENDANT, CHILD, ADJACENT, GENERAL_SIBLING)
     * @param targetSelector The CSS selector to match target elements
     * @param styles The CSS styles to apply to matched elements
     * @return The unique class name added to the host element
     */
    fun injectScopedStyles(
        element: Element,
        selectorType: String,
        targetSelector: String,
        styles: Map<String, String>
    ): String {
        val uniqueClass = generateUniqueClass("scoped")
        element.classList.add(uniqueClass)

        // Determine the CSS combinator based on selector type
        val combinator = when (selectorType.uppercase()) {
            "DESCENDANT" -> " "
            "CHILD" -> " > "
            "ADJACENT" -> " + "
            "GENERAL_SIBLING" -> " ~ "
            else -> " "  // Default to descendant
        }

        val cssRules = styles.entries.joinToString("\n    ") {
            "${toKebabCase(it.key)}: ${it.value};"
        }
        val cssText = ".$uniqueClass$combinator$targetSelector {\n    $cssRules\n}"

        val styleId = "scoped-$uniqueClass-${targetSelector.hashCode()}"
        injectStyle(styleId, cssText)
        trackElementStyle(element, styleId)

        return uniqueClass
    }

    /**
     * Processes the data-scoped-styles attribute and injects all scoped styles.
     *
     * The attribute format is: SELECTOR_TYPE|selector|styles||SELECTOR_TYPE|selector|styles
     * For example: "DESCENDANT|p|color:gray;font-size:14px||CHILD|.item|padding:8px"
     *
     * @param element The element with scoped styles to process
     */
    fun processScopedStyles(element: Element) {
        val scopedData = element.getAttribute("data-scoped-styles") ?: return
        if (scopedData.isEmpty()) return

        // Split by || to get individual definitions
        val definitions = scopedData.split("||")

        for (definition in definitions) {
            val parts = definition.split("|", limit = 3)
            if (parts.size != 3) continue

            val selectorType = parts[0]
            val targetSelector = parts[1]
            val stylesString = parts[2]

            // Parse styles string into map
            val styles = stylesString.split(";")
                .filter { it.isNotBlank() }
                .associate { rule ->
                    val colonIndex = rule.indexOf(':')
                    if (colonIndex > 0) {
                        rule.substring(0, colonIndex).trim() to rule.substring(colonIndex + 1).trim()
                    } else {
                        "" to ""
                    }
                }
                .filterKeys { it.isNotEmpty() }

            if (styles.isNotEmpty()) {
                injectScopedStyles(element, selectorType, targetSelector, styles)
            }
        }
    }
}

