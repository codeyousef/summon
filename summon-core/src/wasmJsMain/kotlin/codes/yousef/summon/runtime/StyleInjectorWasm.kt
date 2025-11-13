package codes.yousef.summon.runtime

/**
 * Manages dynamic CSS injection for pseudo-selectors, media queries, and other CSS rules
 * that can't be applied directly as inline styles - WASM version.
 */
object StyleInjectorWasm {
    private val injectedStyles = mutableMapOf<String, String>()
    private val elementStyleIds = mutableMapOf<String, MutableSet<String>>()
    private var styleCounter = 0

    /**
     * Generates a unique class name for scoped styles
     */
    private fun generateUniqueClass(prefix: String): String {
        return "$prefix-${styleCounter++}"
    }

    /**
     * Injects CSS for pseudo-selector styles and returns the class name
     */
    fun injectPseudoSelectorStyles(elementId: String, pseudoSelector: String, styles: Map<String, String>): String {
        val uniqueClass = generateUniqueClass("pseudo-$pseudoSelector")

        // Add class to element using existing function
        wasmConsoleLog("Adding class $uniqueClass to element $elementId")

        val cssRules = styles.entries.joinToString("\n  ") { "${it.key}: ${it.value};" }
        val cssText = ".$uniqueClass$pseudoSelector {\n  $cssRules\n}"

        val styleId = "pseudo-$uniqueClass"
        injectStyle(styleId, cssText)
        trackElementStyle(elementId, styleId)

        return uniqueClass
    }

    /**
     * Injects CSS for media query styles and returns the class name
     */
    fun injectMediaQueryStyles(elementId: String, mediaQuery: String, styles: Map<String, String>): String {
        val uniqueClass = generateUniqueClass("media")

        // Add class to element using existing function
        wasmConsoleLog("Adding media query class $uniqueClass to element $elementId")

        val cssRules = styles.entries.joinToString("\n    ") { "${it.key}: ${it.value};" }
        val cssText = "@media $mediaQuery {\n  .$uniqueClass {\n    $cssRules\n  }\n}"

        val styleId = "media-$uniqueClass"
        injectStyle(styleId, cssText)
        trackElementStyle(elementId, styleId)

        return uniqueClass
    }

    /**
     * Injects a style element with the given ID and CSS text
     */
    private fun injectStyle(styleId: String, cssText: String) {
        if (injectedStyles.containsKey(styleId)) {
            // Style already exists, update it
            injectedStyles[styleId] = cssText
            wasmUpdateStyleElementWrapper(styleId, cssText)
        } else {
            // Create new style element
            injectedStyles[styleId] = cssText
            wasmCreateStyleElementWrapper(styleId, cssText)
        }
    }

    /**
     * Tracks which styles belong to which element for cleanup
     */
    private fun trackElementStyle(elementId: String, styleId: String) {
        val elementStyles = elementStyleIds.getOrPut(elementId) { mutableSetOf() }
        elementStyles.add(styleId)
    }

    /**
     * Cleans up styles associated with an element when it's removed
     */
    fun cleanupElementStyles(elementId: String) {
        elementStyleIds[elementId]?.forEach { styleId ->
            injectedStyles.remove(styleId)
            wasmRemoveStyleElementWrapper(styleId)
        }
        elementStyleIds.remove(elementId)
    }
}


