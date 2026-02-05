/**
 * # Scoped Style Selectors
 *
 * This module provides CSS scoped style selectors for applying styles to descendant,
 * child, adjacent sibling, and general sibling elements within a component.
 *
 * ## Overview
 *
 * Scoped styles allow you to define CSS rules that target elements relative to the
 * current component, using CSS combinator selectors. This is useful for:
 *
 * - Styling nested content without adding classes to every element
 * - Creating component-level style encapsulation
 * - Applying context-dependent styles to children
 *
 * ## Usage Examples
 *
 * ### Descendant Selector (space)
 * Styles any matching element anywhere inside the component:
 *
 * ```kotlin
 * Box(
 *     modifier = Modifier()
 *         .descendant("p") { color("gray") }  // Styles all <p> inside
 *         .descendant(".highlight") { backgroundColor("yellow") }
 * ) {
 *     P { Text("This is gray") }
 *     Div {
 *         P { Text("This is also gray - nested") }
 *     }
 * }
 * ```
 *
 * ### Child Selector (>)
 * Styles only direct children:
 *
 * ```kotlin
 * Box(
 *     modifier = Modifier()
 *         .child("p") { marginBottom("16px") }  // Only direct <p> children
 * ) {
 *     P { Text("Has margin") }
 *     Div {
 *         P { Text("No margin - not direct child") }
 *     }
 * }
 * ```
 *
 * ### Adjacent Sibling Selector (+)
 * Styles the immediately following sibling:
 *
 * ```kotlin
 * H2(
 *     modifier = Modifier()
 *         .adjacentSibling("p") { marginTop("0") }  // First <p> after this H2
 * ) {
 *     Text("Heading")
 * }
 * P { Text("This has no top margin") }
 * P { Text("This has normal margin") }
 * ```
 *
 * ### General Sibling Selector (~)
 * Styles all following siblings:
 *
 * ```kotlin
 * H2(
 *     modifier = Modifier()
 *         .generalSibling("p") { fontSize("14px") }  // All <p> after this H2
 * ) {
 *     Text("Heading")
 * }
 * P { Text("14px font") }
 * P { Text("Also 14px font") }
 * ```
 *
 * ## Implementation Notes
 *
 * Scoped styles are stored as data attributes on the element and processed by
 * platform-specific renderers:
 *
 * - **JS/WASM**: Uses StyleInjector to create CSS rules with generated unique classes
 * - **JVM/SSR**: Generates `<style>` elements with scoped rules in the output
 *
 * @since 0.7.0
 */
package codes.yousef.summon.modifier

/**
 * Types of CSS selector combinators for scoped styles.
 */
enum class SelectorType(val combinator: String) {
    /**
     * Descendant selector (space).
     * Matches any element that is a descendant of the parent.
     * CSS: `.parent .target`
     */
    DESCENDANT(" "),

    /**
     * Child selector (>).
     * Matches only direct children of the parent.
     * CSS: `.parent > .target`
     */
    CHILD(" > "),

    /**
     * Adjacent sibling selector (+).
     * Matches the immediately following sibling.
     * CSS: `.parent + .target`
     */
    ADJACENT(" + "),

    /**
     * General sibling selector (~).
     * Matches all following siblings.
     * CSS: `.parent ~ .target`
     */
    GENERAL_SIBLING(" ~ ")
}

/**
 * Represents a scoped style definition that targets elements relative to the host element.
 *
 * @property selector The CSS selector to target (e.g., "p", ".highlight", "span.error")
 * @property selectorType The type of CSS combinator to use
 * @property styles The CSS styles to apply to matched elements
 */
data class ScopedStyleDefinition(
    val selector: String,
    val selectorType: SelectorType,
    val styles: Map<String, String>
)

// ============================================
// Scoped Style Modifier Functions
// ============================================

/**
 * Applies styles to all descendant elements matching the selector.
 *
 * Uses the CSS descendant combinator (space) to target any matching element
 * anywhere inside the component tree.
 *
 * @param selector The CSS selector to target (e.g., "p", ".class", "div.special")
 * @param builder DSL builder for styles to apply to matched descendants
 * @return A new [Modifier] with the scoped descendant styles
 *
 * Example:
 * ```kotlin
 * Modifier()
 *     .descendant("a") { color("blue").textDecoration("underline") }
 *     .descendant("a:hover") { color("darkblue") }
 * ```
 */
fun Modifier.descendant(selector: String, builder: Modifier.() -> Modifier): Modifier =
    scopedStyle(selector, SelectorType.DESCENDANT, builder)

/**
 * Applies styles to direct child elements matching the selector.
 *
 * Uses the CSS child combinator (>) to target only immediate children.
 *
 * @param selector The CSS selector to target
 * @param builder DSL builder for styles to apply to matched children
 * @return A new [Modifier] with the scoped child styles
 *
 * Example:
 * ```kotlin
 * Modifier()
 *     .child("li") { listStyleType("none").padding("8px") }
 * ```
 */
fun Modifier.child(selector: String, builder: Modifier.() -> Modifier): Modifier =
    scopedStyle(selector, SelectorType.CHILD, builder)

/**
 * Applies styles to the immediately following sibling matching the selector.
 *
 * Uses the CSS adjacent sibling combinator (+) to target only the
 * first matching sibling that immediately follows this element.
 *
 * @param selector The CSS selector to target
 * @param builder DSL builder for styles to apply to the adjacent sibling
 * @return A new [Modifier] with the scoped adjacent sibling styles
 *
 * Example:
 * ```kotlin
 * // Remove top margin from paragraph immediately following a heading
 * Modifier()
 *     .adjacentSibling("p") { marginTop("0") }
 * ```
 */
fun Modifier.adjacentSibling(selector: String, builder: Modifier.() -> Modifier): Modifier =
    scopedStyle(selector, SelectorType.ADJACENT, builder)

/**
 * Applies styles to all following siblings matching the selector.
 *
 * Uses the CSS general sibling combinator (~) to target all matching
 * siblings that follow this element (not just the immediate one).
 *
 * @param selector The CSS selector to target
 * @param builder DSL builder for styles to apply to general siblings
 * @return A new [Modifier] with the scoped general sibling styles
 *
 * Example:
 * ```kotlin
 * // Style all paragraphs that follow a heading
 * Modifier()
 *     .generalSibling("p") { textIndent("1em") }
 * ```
 */
fun Modifier.generalSibling(selector: String, builder: Modifier.() -> Modifier): Modifier =
    scopedStyle(selector, SelectorType.GENERAL_SIBLING, builder)

/**
 * Internal function to add scoped styles to the modifier.
 *
 * Stores scoped style information as a data attribute for platform renderers to process.
 */
private fun Modifier.scopedStyle(
    selector: String,
    selectorType: SelectorType,
    builder: Modifier.() -> Modifier
): Modifier {
    val scopedModifier = builder(Modifier())
    if (scopedModifier.styles.isEmpty()) return this

    // Serialize styles to a CSS-like format
    val stylesString = scopedModifier.styles.entries.joinToString(";") {
        "${it.key}:${it.value}"
    }

    // Encode as: SELECTOR_TYPE|selector|styles
    val encoded = "${selectorType.name}|$selector|$stylesString"

    // Append to existing scoped styles if any
    val existingScoped = attributes["data-scoped-styles"] ?: ""
    val newScoped = if (existingScoped.isEmpty()) {
        encoded
    } else {
        "$existingScoped||$encoded"  // Use || as separator between definitions
    }

    return attribute("data-scoped-styles", newScoped)
}

// ============================================
// Convenience Extension Functions
// ============================================

/**
 * Applies styles to all paragraph elements inside this component.
 *
 * Convenience function for `.descendant("p") { ... }`.
 */
fun Modifier.paragraphStyle(builder: Modifier.() -> Modifier): Modifier =
    descendant("p", builder)

/**
 * Applies styles to all link elements inside this component.
 *
 * Convenience function for `.descendant("a") { ... }`.
 */
fun Modifier.linkStyle(builder: Modifier.() -> Modifier): Modifier =
    descendant("a", builder)

/**
 * Applies styles to all list item elements inside this component.
 *
 * Convenience function for `.descendant("li") { ... }`.
 */
fun Modifier.listItemStyle(builder: Modifier.() -> Modifier): Modifier =
    descendant("li", builder)

/**
 * Applies styles to all heading elements (h1-h6) inside this component.
 *
 * Convenience function that targets all heading levels.
 */
fun Modifier.headingStyle(builder: Modifier.() -> Modifier): Modifier =
    descendant("h1, h2, h3, h4, h5, h6", builder)

/**
 * Applies styles to elements with a specific class inside this component.
 *
 * @param className The class name (without the leading dot)
 * @param builder DSL builder for styles
 */
fun Modifier.classStyle(className: String, builder: Modifier.() -> Modifier): Modifier =
    descendant(".$className", builder)

/**
 * Applies styles to the first direct child of this component.
 *
 * Uses the `:first-child` pseudo-selector combined with child combinator.
 */
fun Modifier.firstChildStyle(builder: Modifier.() -> Modifier): Modifier =
    child(":first-child", builder)

/**
 * Applies styles to the last direct child of this component.
 *
 * Uses the `:last-child` pseudo-selector combined with child combinator.
 */
fun Modifier.lastChildStyle(builder: Modifier.() -> Modifier): Modifier =
    child(":last-child", builder)
