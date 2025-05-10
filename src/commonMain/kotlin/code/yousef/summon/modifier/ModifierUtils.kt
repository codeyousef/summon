package code.yousef.summon.modifier

import code.yousef.summon.modifier.ModifierExtras.attribute
import code.yousef.summon.modifier.ModifierExtras.getAttribute

/**
 * This file contains all modifier functions organized by category.
 * It resolves ambiguity issues by providing a single implementation
 * that can be used throughout the codebase.
 */

/**
 * Layout-related modifiers for handling dimensions, positioning, and flex/grid layouts.
 */
object LayoutModifiers {
    /**
     * Sets the width of the element.
     */
    fun Modifier.width(value: String): Modifier = style("width", value)

    /**
     * Sets the height of the element.
     */
    fun Modifier.height(value: String): Modifier = style("height", value)

    /**
     * Sets the maximum width of the element.
     */
    fun Modifier.maxWidth(value: String): Modifier = style("max-width", value)

    /**
     * Sets the maximum height of the element.
     */
    fun Modifier.maxHeight(value: String): Modifier = style("max-height", value)

    /**
     * Sets the minimum width of the element.
     */
    fun Modifier.minWidth(value: String): Modifier = style("min-width", value)

    /**
     * Sets the minimum height of the element.
     */
    fun Modifier.minHeight(value: String): Modifier = style("min-height", value)

    /**
     * Sets the position property of the element.
     */
    fun Modifier.position(value: String): Modifier = style("position", value)

    /**
     * Sets the top position of the element.
     */
    fun Modifier.top(value: String): Modifier = style("top", value)

    /**
     * Sets the right position of the element.
     */
    fun Modifier.right(value: String): Modifier = style("right", value)

    /**
     * Sets the bottom position of the element.
     */
    fun Modifier.bottom(value: String): Modifier = style("bottom", value)

    /**
     * Sets the left position of the element.
     */
    fun Modifier.left(value: String): Modifier = style("left", value)

    /**
     * Sets the display property of the element.
     */
    fun Modifier.display(value: String): Modifier = style("display", value)

    /**
     * Sets the flex property (shorthand for flex-grow, flex-shrink, flex-basis).
     */
    fun Modifier.flex(value: String): Modifier = style("flex", value)

    /**
     * Sets the flex direction of the element.
     */
    fun Modifier.flexDirection(value: String): Modifier = style("flex-direction", value)

    /**
     * Sets the flex wrap property.
     */
    fun Modifier.flexWrap(value: String): Modifier = style("flex-wrap", value)

    /**
     * Sets the flex wrap property using the FlexWrap enum.
     */
    fun Modifier.flexWrap(value: FlexWrap): Modifier = style("flex-wrap", value.toString())

    /**
     * Sets the grid-template-columns property.
     */
    fun Modifier.gridTemplateColumns(value: String): Modifier = style("grid-template-columns", value)

    /**
     * Sets the grid-column-gap property.
     */
    fun Modifier.gridColumnGap(value: String): Modifier = style("grid-column-gap", value)

    /**
     * Sets the grid-row-gap property.
     */
    fun Modifier.gridRowGap(value: String): Modifier = style("grid-row-gap", value)

    /**
     * Sets the overflow property.
     */
    fun Modifier.overflow(value: String): Modifier = style("overflow", value)

    /**
     * Sets the gap property.
     */
    fun Modifier.gap(value: String): Modifier = style("gap", value)

    /**
     * Sets the justify-content property.
     */
    fun Modifier.justifyContent(value: String): Modifier = style("justify-content", value)

    /**
     * Sets the align-items property.
     */
    fun Modifier.alignItems(value: String): Modifier = style("align-items", value)
}

/**
 * Styling-related modifiers for appearance, typography, and visual effects.
 */
object StylingModifiers {
    /**
     * Sets the color property.
     */
    fun Modifier.color(value: String): Modifier = style("color", value)

    /**
     * Sets the color property using a Color object.
     */
    fun Modifier.color(value: code.yousef.summon.core.style.Color): Modifier = style("color", value.toString())

    /**
     * Sets the background-color property.
     */
    fun Modifier.backgroundColor(value: String): Modifier = style("background-color", value)

    /**
     * Sets the background-color property using a Color object.
     */
    fun Modifier.backgroundColor(value: code.yousef.summon.core.style.Color): Modifier = style("background-color", value.toString())

    /**
     * Sets the border property.
     */
    fun Modifier.border(width: String, style: String, color: String): Modifier = 
        style("border", "$width $style $color")

    /**
     * Sets the border property using the BorderStyle enum.
     */
    fun Modifier.border(width: String, style: BorderStyle, color: String): Modifier = 
        style("border", "$width ${style.toString()} $color")

    /**
     * Sets the border-radius property.
     */
    fun Modifier.borderRadius(value: String): Modifier = style("border-radius", value)

    /**
     * Sets the font-weight property.
     */
    fun Modifier.fontWeight(value: String): Modifier = style("font-weight", value)

    /**
     * Sets the font-weight property using a FontWeight enum.
     */
    fun Modifier.fontWeight(value: FontWeight): Modifier = style("font-weight", value.toString())

    /**
     * Sets the font-weight property using a numeric value (100-900).
     */
    fun Modifier.fontWeight(value: Int): Modifier = style("font-weight", value.toString())

    /**
     * Sets the font-style property.
     */
    fun Modifier.fontStyle(value: String): Modifier = style("font-style", value)

    /**
     * Sets the line-height property.
     */
    fun Modifier.lineHeight(value: Number): Modifier = style("line-height", value.toString())

    /**
     * Sets the text-align property.
     */
    fun Modifier.textAlign(value: String): Modifier = style("text-align", value)

    /**
     * Sets the text-align property using a TextAlign enum.
     */
    fun Modifier.textAlign(value: TextAlign): Modifier = style("text-align", value.toString())

    /**
     * Sets the text-decoration property.
     */
    fun Modifier.textDecoration(value: String): Modifier = style("text-decoration", value)

    /**
     * Sets the box-shadow property.
     */
    fun Modifier.boxShadow(value: String): Modifier = style("box-shadow", value)

    /**
     * Sets the transition property.
     */
    fun Modifier.transition(value: String): Modifier = style("transition", value)

    /**
     * Sets the transform property.
     */
    fun Modifier.transform(value: String): Modifier = style("transform", value)

    /**
     * Sets the opacity property.
     */
    fun Modifier.opacity(value: Float): Modifier = style("opacity", value.toString())

    /**
     * Sets the opacity property.
     */
    fun Modifier.opacity(value: String): Modifier = style("opacity", value)
}

/**
 * Event-related modifiers for handling user interactions.
 */
object EventModifiers {
    /**
     * Sets an onClick handler for the element.
     */
    fun Modifier.onClick(handler: () -> Unit): Modifier = style("onclick", handler.toString())

    /**
     * Sets the pointer-events property.
     */
    fun Modifier.pointerEvents(value: String): Modifier = style("pointer-events", value)
}

/**
 * Attribute-related modifiers for handling HTML attributes and accessibility.
 */
object AttributeModifiers {
    /**
     * Adds a custom attribute to the element.
     */
    fun Modifier.attribute(name: String, value: String): Modifier {
        val key = "__attr:$name"
        return Modifier(this.styles + (key to value))
    }

    /**
     * Gets an attribute value or null if not present.
     */
    fun Modifier.getAttribute(name: String): String? = 
        styles["__attr:$name"]
}

// Add additional semantic aliases for common usage patterns
typealias Layout = LayoutModifiers
typealias Styling = StylingModifiers
typealias Events = EventModifiers
typealias Attributes = AttributeModifiers 

// --- Moved from jsMain/ModifierExtensions.kt ---

/**
 * Convert modifier styles to a valid CSS style string that can be used in HTML style attributes.
 * This ensures all property names are in kebab-case format as required by browsers.
 */
fun Modifier.toStyleString(): String {
    if (this.styles.isEmpty()) {
        return ""
    }
    
    // Determine if the key is already in kebab-case or needs conversion from camelCase
    return this.styles
        .map { (key, value) -> 
            val cssPropertyName = if (key.contains('-')) {
                // Already in kebab-case
                key
            } else {
                // Convert from camelCase to kebab-case
                key.camelToKebabCase()
            }
            "$cssPropertyName: $value"
        }
        .joinToString(separator = "; ", postfix = ";")
}

/**
 * Converts a camelCase string to kebab-case.
 * For example: "backgroundColor" becomes "background-color"
 */
private fun String.camelToKebabCase(): String {
    if (this.isEmpty()) {
        return this
    }
    
    // Replace each uppercase letter with a hyphen followed by the lowercase letter
    return this.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
}
