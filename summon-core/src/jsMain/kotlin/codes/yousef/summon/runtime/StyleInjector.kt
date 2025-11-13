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
}

