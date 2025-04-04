package code.yousef.summon

/**
 * Type-specific modifier extensions that only apply to components with specific capabilities.
 * This file contains extension functions that extend the base Modifier class but are only
 * intended to be used with specific component types.
 */

/**
 * ScrollableComponent specific modifiers
 */
class ScrollableModifier internal constructor(private val baseModifier: Modifier) {
    /**
     * Sets the overflow behavior for scrollable components.
     * @param value The overflow CSS value (auto, scroll, hidden, visible)
     */
    fun overflow(value: String): ScrollableModifier {
        return ScrollableModifier(baseModifier.then(Modifier(mapOf("overflow" to value))))
    }

    /**
     * Enables vertical scrolling with automatic scrollbars.
     */
    fun verticalScroll(): ScrollableModifier {
        return overflow("auto").then(
            Modifier(mapOf("overflow-y" to "auto", "overflow-x" to "hidden"))
        )
    }

    /**
     * Enables horizontal scrolling with automatic scrollbars.
     */
    fun horizontalScroll(): ScrollableModifier {
        return overflow("auto").then(
            Modifier(mapOf("overflow-x" to "auto", "overflow-y" to "hidden"))
        )
    }

    /**
     * Sets the scroll behavior for the component.
     * @param value The scroll-behavior CSS value (auto, smooth)
     */
    fun scrollBehavior(value: String): ScrollableModifier {
        return ScrollableModifier(baseModifier.then(Modifier(mapOf("scroll-behavior" to value))))
    }

    /**
     * Converts back to a regular Modifier.
     */
    fun then(other: Modifier): ScrollableModifier {
        return ScrollableModifier(baseModifier.then(other))
    }

    /**
     * Converts this specialized modifier back to a base Modifier.
     */
    fun toModifier(): Modifier {
        return baseModifier
    }
}

/**
 * InputComponent specific modifiers
 */
class InputModifier internal constructor(private val baseModifier: Modifier) {
    /**
     * Sets the placeholder text color for input components.
     * @param color The CSS color value for placeholder text
     */
    fun placeholderColor(color: String): InputModifier {
        val placeholderStyles = mapOf(
            "__placeholder" to "color:$color"
        )
        return InputModifier(baseModifier.then(Modifier(placeholderStyles)))
    }

    /**
     * Sets the border color when the input is focused.
     * @param color The CSS color value for the focus border
     */
    fun focusBorderColor(color: String): InputModifier {
        val focusStyles = mapOf(
            "__focus" to "border-color:$color;outline-color:$color"
        )
        return InputModifier(baseModifier.then(Modifier(focusStyles)))
    }

    /**
     * Disables the input field.
     */
    fun disabled(): InputModifier {
        return InputModifier(
            baseModifier.then(
                Modifier(
                    mapOf(
                        "opacity" to "0.6",
                        "cursor" to "not-allowed",
                        "__disabled" to "true"
                    )
                )
            )
        )
    }

    /**
     * Converts back to a regular Modifier.
     */
    fun then(other: Modifier): InputModifier {
        return InputModifier(baseModifier.then(other))
    }

    /**
     * Converts this specialized modifier back to a base Modifier.
     */
    fun toModifier(): Modifier {
        return baseModifier
    }
}

/**
 * TextComponent specific modifiers
 */
class TextModifier internal constructor(private val baseModifier: Modifier) {
    /**
     * Sets text truncation with ellipsis for overflow.
     */
    fun ellipsis(): TextModifier {
        return TextModifier(
            baseModifier.then(
                Modifier(
                    mapOf(
                        "white-space" to "nowrap",
                        "overflow" to "hidden",
                        "text-overflow" to "ellipsis"
                    )
                )
            )
        )
    }

    /**
     * Controls text wrapping behavior.
     * @param value The white-space CSS value (normal, nowrap, pre, pre-wrap, etc.)
     */
    fun whiteSpace(value: String): TextModifier {
        return TextModifier(baseModifier.then(Modifier(mapOf("white-space" to value))))
    }

    /**
     * Sets text alignment.
     * @param value The text-align CSS value (left, right, center, justify)
     */
    fun textAlign(value: String): TextModifier {
        return TextModifier(baseModifier.then(Modifier(mapOf("text-align" to value))))
    }

    /**
     * Transforms text case.
     * @param value The text-transform CSS value (uppercase, lowercase, capitalize, none)
     */
    fun textTransform(value: String): TextModifier {
        return TextModifier(baseModifier.then(Modifier(mapOf("text-transform" to value))))
    }

    /**
     * Sets line height for the text.
     * @param value The line-height CSS value
     */
    fun lineHeight(value: String): TextModifier {
        return TextModifier(baseModifier.then(Modifier(mapOf("line-height" to value))))
    }

    /**
     * Converts back to a regular Modifier.
     */
    fun then(other: Modifier): TextModifier {
        return TextModifier(baseModifier.then(other))
    }

    /**
     * Converts this specialized modifier back to a base Modifier.
     */
    fun toModifier(): Modifier {
        return baseModifier
    }
}

/**
 * LayoutComponent specific modifiers
 */
class LayoutModifier internal constructor(private val baseModifier: Modifier) {
    /**
     * Sets the flex-grow property of the layout.
     * @param value The flex-grow CSS value
     */
    fun flexGrow(value: Int): LayoutModifier {
        return LayoutModifier(baseModifier.then(Modifier(mapOf("flex-grow" to value.toString()))))
    }

    /**
     * Sets the flex-shrink property of the layout.
     * @param value The flex-shrink CSS value
     */
    fun flexShrink(value: Int): LayoutModifier {
        return LayoutModifier(baseModifier.then(Modifier(mapOf("flex-shrink" to value.toString()))))
    }

    /**
     * Sets the flex-basis property of the layout.
     * @param value The flex-basis CSS value
     */
    fun flexBasis(value: String): LayoutModifier {
        return LayoutModifier(baseModifier.then(Modifier(mapOf("flex-basis" to value))))
    }

    /**
     * Sets the grid-template-columns property for Grid layouts.
     * @param value The grid-template-columns CSS value
     */
    fun gridTemplateColumns(value: String): LayoutModifier {
        return LayoutModifier(baseModifier.then(Modifier(mapOf("grid-template-columns" to value))))
    }

    /**
     * Sets the grid-template-rows property for Grid layouts.
     * @param value The grid-template-rows CSS value
     */
    fun gridTemplateRows(value: String): LayoutModifier {
        return LayoutModifier(baseModifier.then(Modifier(mapOf("grid-template-rows" to value))))
    }

    /**
     * Sets the grid-gap property for Grid layouts.
     * @param value The grid-gap CSS value
     */
    fun gridGap(value: String): LayoutModifier {
        return LayoutModifier(baseModifier.then(Modifier(mapOf("grid-gap" to value))))
    }

    /**
     * Converts back to a regular Modifier.
     */
    fun then(other: Modifier): LayoutModifier {
        return LayoutModifier(baseModifier.then(other))
    }

    /**
     * Converts this specialized modifier back to a base Modifier.
     */
    fun toModifier(): Modifier {
        return baseModifier
    }
}

/**
 * MediaComponent specific modifiers
 */
class MediaModifier internal constructor(private val baseModifier: Modifier) {
    /**
     * Sets the object-fit property for media elements.
     * @param value The object-fit CSS value (cover, contain, fill, etc.)
     */
    fun objectFit(value: String): MediaModifier {
        return MediaModifier(baseModifier.then(Modifier(mapOf("object-fit" to value))))
    }

    /**
     * Sets the object-position property for media elements.
     * @param value The object-position CSS value (center, top, etc.)
     */
    fun objectPosition(value: String): MediaModifier {
        return MediaModifier(baseModifier.then(Modifier(mapOf("object-position" to value))))
    }

    /**
     * Makes the image maintain its aspect ratio when resized.
     */
    fun preserveAspectRatio(): MediaModifier {
        return MediaModifier(baseModifier.then(Modifier(mapOf("object-fit" to "contain"))))
    }

    /**
     * Makes the image cover its container, possibly cropping the image.
     */
    fun coverContainer(): MediaModifier {
        return MediaModifier(baseModifier.then(Modifier(mapOf("object-fit" to "cover"))))
    }

    /**
     * Applies a filter to the media element.
     * @param value The filter CSS value (blur, brightness, contrast, etc.)
     */
    fun filter(value: String): MediaModifier {
        return MediaModifier(baseModifier.then(Modifier(mapOf("filter" to value))))
    }

    /**
     * Converts back to a regular Modifier.
     */
    fun then(other: Modifier): MediaModifier {
        return MediaModifier(baseModifier.then(other))
    }

    /**
     * Converts this specialized modifier back to a base Modifier.
     */
    fun toModifier(): Modifier {
        return baseModifier
    }
}

/**
 * Extension functions to convert the base Modifier to type-specific modifiers
 */

/**
 * Converts a Modifier to a ScrollableModifier for use with ScrollableComponent.
 * This should only be used with components that implement ScrollableComponent.
 */
fun Modifier.scrollable(): ScrollableModifier {
    return ScrollableModifier(this)
}

/**
 * Converts a Modifier to an InputModifier for use with InputComponent.
 * This should only be used with components that implement InputComponent.
 */
fun Modifier.input(): InputModifier {
    return InputModifier(this)
}

/**
 * Converts a Modifier to a TextModifier for use with TextComponent.
 * This should only be used with components that implement TextComponent.
 */
fun Modifier.text(): TextModifier {
    return TextModifier(this)
}

/**
 * Converts a Modifier to a LayoutModifier for use with LayoutComponent.
 * This should only be used with components that implement LayoutComponent.
 */
fun Modifier.layout(): LayoutModifier {
    return LayoutModifier(this)
}

/**
 * Converts a Modifier to a MediaModifier for use with MediaComponent.
 * This should only be used with components that implement MediaComponent.
 */
fun Modifier.media(): MediaModifier {
    return MediaModifier(this)
} 