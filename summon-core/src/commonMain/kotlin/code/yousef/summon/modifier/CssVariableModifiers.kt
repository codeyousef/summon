/**
 * # CSS Variable Modifiers
 *
 * Type-safe CSS custom property (CSS variable) support for the Summon framework.
 * This module provides helpers for defining and using CSS variables in a declarative way.
 *
 * ## Features
 *
 * - **Define Variables**: Set CSS custom properties on elements
 * - **Use Variables**: Reference CSS variables in styles
 * - **Type Safety**: Kotlin-first API for CSS variables
 * - **Theming Support**: Easy runtime theme switching
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Define CSS variables on a container
 * Box(
 *     modifier = Modifier()
 *         .cssVar("primary-color", "#ff4668")
 *         .cssVar("surface-color", "rgba(255,255,255,0.04)")
 *         .cssVar("spacing-unit", "8px")
 * ) {
 *     // Use CSS variables in child components
 *     Box(
 *         modifier = Modifier()
 *             .backgroundColor(cssVar("surface-color"))
 *             .padding(cssVar("spacing-unit"))
 *     )
 * }
 *
 * // Use multiple CSS variables
 * Box(
 *     modifier = Modifier()
 *         .cssVars(mapOf(
 *             "primary-color" to "#ff4668",
 *             "secondary-color" to "#00aaff",
 *             "accent-color" to "#ffaa00"
 *         ))
 * )
 * ```
 *
 * ## Theming Pattern
 *
 * ```kotlin
 * // Define theme variables at root
 * @Composable
 * fun App() {
 *     val isDark = remember { mutableStateOf(false) }
 *     
 *     Box(
 *         modifier = Modifier()
 *             .cssVars(if (isDark.value) darkTheme else lightTheme)
 *     ) {
 *         // All children can use theme variables
 *         MyContent()
 *     }
 * }
 * ```
 *
 * @see Modifier for the core modifier system
 * @see StyleModifiers for standard styling functions
 * @since 1.0.0
 */
package code.yousef.summon.modifier

/**
 * Sets a CSS custom property (CSS variable) on the element.
 * The variable can be referenced by descendants using `var(--name)` or the `cssVar()` function.
 *
 * @param name The name of the CSS variable (without the `--` prefix)
 * @param value The value of the CSS variable
 * @return A new [Modifier] with the CSS variable defined
 *
 * Example:
 * ```kotlin
 * Modifier().cssVar("primary-color", "#ff4668")
 * // Generates: style="--primary-color: #ff4668"
 * ```
 */
fun Modifier.cssVar(name: String, value: String): Modifier {
    val varName = if (name.startsWith("--")) name else "--$name"
    return style(varName, value)
}

/**
 * Sets multiple CSS custom properties at once.
 * Keys may be provided with or without the `--` prefix.
 *
 * @param variables Map of variable names to values
 * @return A new [Modifier] with all CSS variables defined
 *
 * Example:
 * ```kotlin
 * Modifier().cssVars(mapOf(
 *     "primary-color" to "#ff4668",
 *     "surface-color" to "rgba(255,255,255,0.04)"
 * ))
 * ```
 */
fun Modifier.cssVars(variables: Map<String, String>): Modifier {
    var updated = this
    variables.forEach { (name, value) ->
        updated = updated.cssVar(name, value)
    }
    return updated
}

/**
 * Returns a CSS `var()` reference string for use in style values.
 * This function generates the correct CSS syntax to reference a custom property.
 *
 * @param name The name of the CSS variable (without the `--` prefix)
 * @param fallback Optional fallback value if the variable is not defined
 * @return A string in the format `var(--name)` or `var(--name, fallback)`
 *
 * Example:
 * ```kotlin
 * Modifier().backgroundColor(cssVar("surface-color"))
 * // Generates: background-color: var(--surface-color)
 *
 * Modifier().color(cssVar("text-color", "#000000"))
 * // Generates: color: var(--text-color, #000000)
 * ```
 */
fun cssVar(name: String, fallback: String? = null): String {
    val varName = if (name.startsWith("--")) name else "--$name"
    return if (fallback != null) {
        "var($varName, $fallback)"
    } else {
        "var($varName)"
    }
}
