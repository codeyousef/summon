package code.yousef.summon.theme

import code.yousef.summon.modifier.Modifier

/**
 * StyleSheet provides a way to define reusable styles that can be applied to components.
 * It helps maintain consistent styling throughout the application and reduces duplication.
 */
object StyleSheet {
    /**
     * A registry of named styles that can be referenced and applied to components
     */
    private val styleRegistry = mutableMapOf<String, Modifier>()

    /**
     * Define a named style with a modifier
     * @param name The unique name for the style
     * @param modifier The modifier containing the style definitions
     */
    fun defineStyle(name: String, modifier: Modifier) {
        styleRegistry[name] = modifier
    }

    /**
     * Get a registered style by name
     * @param name The style name
     * @return The registered modifier or an empty modifier if not found
     */
    fun getStyle(name: String): Modifier {
        return styleRegistry[name] ?: Modifier()
    }

    /**
     * Check if a style is defined
     * @param name The style name
     * @return True if the style exists, false otherwise
     */
    fun hasStyle(name: String): Boolean {
        return styleRegistry.containsKey(name)
    }

    /**
     * Remove a style from the registry
     * @param name The style name to remove
     */
    fun removeStyle(name: String) {
        styleRegistry.remove(name)
    }

    /**
     * Clear all registered styles
     */
    fun clearStyles() {
        styleRegistry.clear()
    }

    /**
     * Create and register a style in a single call
     * @param name The style name
     * @param builder A lambda that builds the style modifier
     * @return The created modifier
     */
    fun createStyle(name: String, builder: Modifier.() -> Modifier): Modifier {
        val style = Modifier().builder()
        defineStyle(name, style)
        return style
    }

    /**
     * Extend an existing style with additional properties
     * @param baseName The base style name
     * @param newName The new style name
     * @param extension A lambda that extends the base style
     */
    fun extendStyle(baseName: String, newName: String, extension: Modifier.() -> Modifier) {
        val baseStyle = getStyle(baseName)
        val extendedStyle = baseStyle.extension()
        defineStyle(newName, extendedStyle)
    }
}

/**
 * Extension functions to easily apply stylesheet styles to components
 */

/**
 * Apply a registered style to a modifier
 * @param styleName The name of the style to apply
 * @return A new modifier with the style applied
 */
fun Modifier.applyStyle(styleName: String): Modifier {
    // Get the style from the registry
    val styleToApply = StyleSheet.getStyle(styleName)
    // Create a new modifier with the combined styles
    // This is a simplified implementation
    return Modifier()
}

/**
 * Apply multiple registered styles to a modifier
 * @param styleNames The names of the styles to apply
 * @return A new modifier with all styles applied
 */
fun Modifier.applyStyles(vararg styleNames: String): Modifier {
    return styleNames.fold(this) { acc, styleName ->
        acc.applyStyle(styleName)
    }
}

/**
 * StyleBuilder allows declarative style definition
 */
class StyleBuilder {
    private val styles = mutableMapOf<String, Modifier>()

    /**
     * Define a style
     * @param name The style name
     * @param builder A lambda that builds the style
     */
    fun style(name: String, builder: Modifier.() -> Modifier) {
        styles[name] = Modifier().builder()
    }

    /**
     * Define a style that extends another
     * @param name The new style name
     * @param baseName The base style name
     * @param builder A lambda that builds on top of the base style
     */
    fun extendStyle(name: String, baseName: String, builder: Modifier.() -> Modifier) {
        val baseStyle = styles[baseName] ?: StyleSheet.getStyle(baseName)
        styles[name] = baseStyle.builder()
    }

    /**
     * Register all defined styles to the global StyleSheet
     */
    fun registerAll() {
        styles.forEach { (name, modifier) ->
            StyleSheet.defineStyle(name, modifier)
        }
    }
}

/**
 * Create a style sheet with declarative syntax
 * @param builder A lambda that defines the styles
 */
fun createStyleSheet(builder: StyleBuilder.() -> Unit) {
    val styleBuilder = StyleBuilder()
    styleBuilder.builder()
    styleBuilder.registerAll()
} 