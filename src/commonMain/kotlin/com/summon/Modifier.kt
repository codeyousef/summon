package com.summon

/**
 * A Modifier is used to add styling information to a composable.
 * It holds a map of CSS property names to their values.
 */
data class Modifier(val styles: Map<String, String> = emptyMap()) {
    companion object {
        /**
         * Creates an empty modifier with no styles.
         */
        fun create(): Modifier = Modifier()
    }

    /**
     * Sets the background color of the element.
     * @param color The CSS color value (name, hex, rgb, etc.)
     * @return A new Modifier with the added style
     */
    fun background(color: String): Modifier =
        Modifier(styles + ("background-color" to color))

    /**
     * Sets the padding of the element.
     * @param value The CSS padding value (e.g., "10px", "1rem", etc.)
     * @return A new Modifier with the added style
     */
    fun padding(value: String): Modifier =
        Modifier(styles + ("padding" to value))

    /**
     * Sets padding for each side individually.
     */
    fun padding(top: String, right: String, bottom: String, left: String): Modifier =
        Modifier(styles + ("padding" to "$top $right $bottom $left"))

    /**
     * Sets the width of the element.
     */
    fun width(value: String): Modifier =
        Modifier(styles + ("width" to value))

    /**
     * Sets the height of the element.
     */
    fun height(value: String): Modifier =
        Modifier(styles + ("height" to value))

    /**
     * Sets both width and height at once.
     */
    fun size(width: String, height: String): Modifier =
        Modifier(styles + ("width" to width) + ("height" to height))

    /**
     * Sets equal width and height.
     */
    fun size(value: String): Modifier =
        size(value, value)

    /**
     * Adds a border to the element.
     */
    fun border(width: String, style: String, color: String): Modifier =
        Modifier(styles + ("border" to "$width $style $color"))

    /**
     * Sets the border radius for rounded corners.
     */
    fun borderRadius(value: String): Modifier =
        Modifier(styles + ("border-radius" to value))

    /**
     * Sets the text color.
     */
    fun color(value: String): Modifier =
        Modifier(styles + ("color" to value))

    /**
     * Sets the font size.
     */
    fun fontSize(value: String): Modifier =
        Modifier(styles + ("font-size" to value))

    /**
     * Sets the font weight (normal, bold, etc).
     */
    fun fontWeight(value: String): Modifier =
        Modifier(styles + ("font-weight" to value))

    /**
     * Sets margins around the element.
     */
    fun margin(value: String): Modifier =
        Modifier(styles + ("margin" to value))

    /**
     * Sets margins for each side individually.
     */
    fun margin(top: String, right: String, bottom: String, left: String): Modifier =
        Modifier(styles + ("margin" to "$top $right $bottom $left"))

    /**
     * Sets the element to fill its container.
     */
    fun fillMaxSize(): Modifier =
        Modifier(styles + ("width" to "100%") + ("height" to "100%"))

    /**
     * Sets the element to fill the width of its container.
     */
    fun fillMaxWidth(): Modifier =
        Modifier(styles + ("width" to "100%"))

    /**
     * Sets the element to fill the height of its container.
     */
    fun fillMaxHeight(): Modifier =
        Modifier(styles + ("height" to "100%"))

    /**
     * Sets box shadow for the element.
     */
    fun shadow(offsetX: String, offsetY: String, blurRadius: String, color: String): Modifier =
        Modifier(styles + ("box-shadow" to "$offsetX $offsetY $blurRadius $color"))

    /**
     * Adds a simple shadow with default values.
     */
    fun shadow(): Modifier =
        shadow("0px", "2px", "4px", "rgba(0,0,0,0.2)")

    /**
     * Sets the element's position to absolute.
     */
    fun absolutePosition(
        top: String? = null,
        right: String? = null,
        bottom: String? = null,
        left: String? = null
    ): Modifier {
        var newStyles = styles + ("position" to "absolute")
        if (top != null) newStyles = newStyles + ("top" to top)
        if (right != null) newStyles = newStyles + ("right" to right)
        if (bottom != null) newStyles = newStyles + ("bottom" to bottom)
        if (left != null) newStyles = newStyles + ("left" to left)
        return Modifier(newStyles)
    }

    /**
     * Sets the element's opacity.
     */
    fun opacity(value: Float): Modifier =
        Modifier(styles + ("opacity" to value.toString()))

    /**
     * Adds a hover effect. Note: this will be applied differently based on platform.
     * For JVM it will generate a CSS class, for JS it will attach event listeners.
     */
    fun hover(hoverStyles: Map<String, String>): Modifier {
        // Store hover styles in a special key that platform implementations will handle
        return Modifier(styles + ("__hover" to hoverStyles.entries.joinToString(";") { (k, v) -> "$k:$v" }))
    }

    /**
     * Combines this modifier with another modifier.
     * Styles from other will override styles with the same key in this modifier.
     */
    fun then(other: Modifier): Modifier =
        Modifier(styles + other.styles)

    /**
     * Converts the styles map to a CSS inline style string.
     * @return A string in the format "property1:value1;property2:value2;..."
     */
    fun toStyleString(): String =
        styles.entries
            .filter { it.key != "__hover" } // Filter out special keys
            .joinToString(";") { (key, value) -> "$key:$value" }

    /**
     * Sets the maximum width of the element.
     */
    fun maxWidth(value: String): Modifier =
        Modifier(styles + ("max-width" to value))

    /**
     * Sets the maximum height of the element.
     */
    fun maxHeight(value: String): Modifier =
        Modifier(styles + ("max-height" to value))

    /**
     * Sets the background color of the element (alias for background).
     * @param color The CSS color value (name, hex, rgb, etc.)
     * @return A new Modifier with the added style
     */
    fun backgroundColor(color: String): Modifier =
        Modifier(styles + ("background-color" to color))

    /**
     * Sets the margin at the bottom of the element.
     */
    fun marginBottom(value: String): Modifier =
        Modifier(styles + ("margin-bottom" to value))

    /**
     * Sets the margin at the top of the element.
     */
    fun marginTop(value: String): Modifier =
        Modifier(styles + ("margin-top" to value))

    /**
     * Sets the margin at the left of the element.
     */
    fun marginLeft(value: String): Modifier =
        Modifier(styles + ("margin-left" to value))

    /**
     * Sets the margin at the right of the element.
     */
    fun marginRight(value: String): Modifier =
        Modifier(styles + ("margin-right" to value))

    /**
     * Sets the cursor style.
     */
    fun cursor(value: String): Modifier =
        Modifier(styles + ("cursor" to value))

    /**
     * Sets CSS transitions for animating property changes.
     */
    fun transitions(value: String): Modifier =
        Modifier(styles + ("transition" to value))

    /**
     * Sets the flex-wrap property for flex containers.
     */
    fun flexWrap(value: String): Modifier =
        Modifier(styles + ("flex-wrap" to value))

    /**
     * Adds a hover effect with a Modifier.
     * Converts the Modifier's styles to the format expected by the base hover method.
     */
    fun hover(hoverModifier: Modifier): Modifier {
        return hover(hoverModifier.styles)
    }

    /**
     * Sets the box-shadow property directly with a CSS value.
     * @param value The CSS box-shadow value
     * @return A new Modifier with the added style
     */
    fun boxShadow(value: String): Modifier =
        Modifier(styles + ("box-shadow" to value))

    /**
     * Sets the align-items property for flex containers.
     * @param value The alignment value (center, flex-start, flex-end, etc.)
     * @return A new Modifier with the added style
     */
    fun alignItems(value: String): Modifier =
        Modifier(styles + ("align-items" to value))

    /**
     * Sets the justify-content property for flex containers.
     * @param value The justification value (center, flex-start, flex-end, space-between, space-around, etc.)
     * @return A new Modifier with the added style
     */
    fun justifyContent(value: String): Modifier =
        Modifier(styles + ("justify-content" to value))

    /**
     * Sets the flex-direction property for flex containers.
     * @param value The direction value (row, column, row-reverse, column-reverse)
     * @return A new Modifier with the added style
     */
    fun flexDirection(value: String): Modifier =
        Modifier(styles + ("flex-direction" to value))

    /**
     * Sets the display property for the element.
     * @param value The display value (flex, block, inline, none, etc.)
     * @return A new Modifier with the added style
     */
    fun display(value: String): Modifier =
        Modifier(styles + ("display" to value))

    /**
     * Sets the text-decoration property for text styling.
     * @param value The text-decoration value (none, underline, line-through, etc.)
     * @return A new Modifier with the added style
     */
    fun textDecoration(value: String): Modifier =
        Modifier(styles + ("text-decoration" to value))

    /**
     * Shorthand to create a flex container.
     * Sets display to "flex" and optionally configures other flex properties.
     * @return A new Modifier with flex display style
     */
    fun flex(): Modifier =
        display("flex")

    /**
     * Shorthand to create a flex container with custom settings.
     * Sets display to "flex" and configures other flex properties based on parameters.
     *
     * @param direction The flex-direction value (row, column, row-reverse, column-reverse)
     * @param wrap The flex-wrap value (nowrap, wrap, wrap-reverse)
     * @param alignItems The align-items value (center, flex-start, flex-end, etc.)
     * @param justifyContent The justify-content value (center, flex-start, flex-end, space-between, etc.)
     * @return A new Modifier with flex display and specified properties
     */
    fun flex(
        direction: String? = null,
        wrap: String? = null,
        alignItems: String? = null,
        justifyContent: String? = null
    ): Modifier {
        var modifier = display("flex")

        if (direction != null) {
            modifier = modifier.flexDirection(direction)
        }

        if (wrap != null) {
            modifier = modifier.flexWrap(wrap)
        }

        if (alignItems != null) {
            modifier = modifier.alignItems(alignItems)
        }

        if (justifyContent != null) {
            modifier = modifier.justifyContent(justifyContent)
        }

        return modifier
    }
} 