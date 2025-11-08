package code.yousef.summon.modifier

import code.yousef.summon.modifier.aspectRatio as baseAspectRatio
import code.yousef.summon.modifier.inset as baseInset
import code.yousef.summon.modifier.positionInset as basePositionInset

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
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
    fun Modifier.width(value: String): Modifier = style("width", value)

    /**
     * Sets the height of the element.
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
    fun Modifier.height(value: String): Modifier = style("height", value)

    /**
     * Sets the maximum width of the element.
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
    fun Modifier.maxWidth(value: String): Modifier = style("max-width", value)

    /**
     * Sets the maximum height of the element.
     */
    fun Modifier.maxHeight(value: String): Modifier = style("max-height", value)

    fun Modifier.aspectRatio(value: Number): Modifier =
        baseAspectRatio(value)

    fun Modifier.aspectRatio(width: Number, height: Number): Modifier =
        baseAspectRatio(width, height)

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
     * Applies CSS inset shorthand.
     */
    fun Modifier.inset(value: String): Modifier =
        baseInset(value)

    fun Modifier.inset(vertical: String, horizontal: String): Modifier =
        baseInset(vertical, horizontal)

    fun Modifier.inset(top: String, right: String, bottom: String, left: String): Modifier =
        baseInset(top, right, bottom, left)

    fun Modifier.positionInset(
        top: String? = null,
        right: String? = null,
        bottom: String? = null,
        left: String? = null
    ): Modifier = basePositionInset(top, right, bottom, left)

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
     * Sets the flex direction using the FlexDirection enum.
     */
    fun Modifier.flexDirection(value: FlexDirection): Modifier = style("flex-direction", value.toString())

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
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
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
    fun Modifier.backgroundColor(value: code.yousef.summon.core.style.Color): Modifier =
        style("background-color", value.toString())


    /**
     * Sets the border-radius property.
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
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
     * Sets the font-style property using the FontStyle enum.
     */
    fun Modifier.fontStyle(value: FontStyle): Modifier = style("font-style", value.toString())

    /**
     * Sets the line-height property.
     */
    fun Modifier.lineHeight(value: Number): Modifier = style("line-height", value.toString())


    /**
     * Sets the text-decoration property.
     */
    fun Modifier.textDecoration(value: String): Modifier = style("text-decoration", value)


    /**
     * Sets the transform property.
     */
    fun Modifier.transform(value: String): Modifier = style("transform", value)

    /**
     * Sets the opacity property.
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
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
    fun Modifier.onClick(handler: () -> Unit): Modifier =
        copy(eventHandlers = this.eventHandlers + ("click" to handler))

    /**
     * Sets the pointer-events property.
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
    fun Modifier.pointerEvents(value: String): Modifier = style("pointer-events", value)

    /**
     * Sets the pointer-events property using the PointerEvents enum.
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
    fun Modifier.pointerEvents(value: PointerEvents): Modifier = style("pointer-events", value.toString())
}

/**
 * Attribute-related modifiers for handling HTML attributes and accessibility.
 */
object AttributeModifiers {
    /**
     * Adds a custom attribute to the element.
     */
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    @Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
    fun Modifier.attribute(name: String, value: String): Modifier {
        return Modifier(this.styles, this.attributes + (name to value), this.eventHandlers)
    }

    /**
     * Gets an attribute value or null if not present.
     */
    fun Modifier.getAttribute(name: String): String? =
        attributes[name]

    /**
     * Sets the HTML button type attribute (`type="button|submit|reset"`).
     */
    fun Modifier.buttonType(value: ButtonType): Modifier =
        attribute("type", value.value)
}

// Add additional semantic aliases for common usage patterns
typealias Layout = LayoutModifiers
typealias Styling = StylingModifiers
typealias Events = EventModifiers
typealias Attributes = AttributeModifiers

// --- Moved from jsMain/ModifierExtensions.kt ---

// Note: toStyleString() is now defined as a member function in Modifier.kt
// The extension function has been removed to avoid conflicts
