package code.yousef.summon.modifier

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
     * Generic style method for adding any CSS property.
     * This is the core method that all other styling methods use.
     * 
     * @param propertyName The CSS property name
     * @param value The property value
     * @return A new Modifier with the added style
     */
    fun style(propertyName: String, value: String): Modifier = 
        Modifier(styles + (propertyName to value))
    
    /**
     * Sets multiple style properties at once.
     * 
     * @param properties Map of property names to values
     * @return A new Modifier with the added styles
     */
    fun styles(properties: Map<String, String>): Modifier =
        Modifier(styles + properties)

    /**
     * Sets the background color of the element.
     * @param color The CSS color value (name, hex, rgb, etc.)
     * @return A new Modifier with the added style
     */
    fun background(color: String): Modifier =
        style("background-color", color)

    /**
     * Sets the padding of the element.
     * @param value The CSS padding value (e.g., "10px", "1rem", etc.)
     * @return A new Modifier with the added style
     */
    fun padding(value: String): Modifier =
        style("padding", value)

    /**
     * Sets padding for each side individually.
     */
    fun padding(top: String, right: String, bottom: String, left: String): Modifier =
        style("padding", "$top $right $bottom $left")

    /**
     * Sets the width of the element.
     */
    fun width(value: String): Modifier =
        style("width", value)

    /**
     * Sets the height of the element.
     */
    fun height(value: String): Modifier =
        style("height", value)

    /**
     * Sets both width and height at once.
     */
    fun size(width: String, height: String): Modifier =
        this.width(width).height(height)

    /**
     * Sets equal width and height.
     */
    fun size(value: String): Modifier =
        size(value, value)

    /**
     * Adds a border to the element.
     */
    fun border(width: String, style: String, color: String): Modifier =
        this.style("border", "$width $style $color")

    /**
     * Sets the border radius for rounded corners.
     */
    fun borderRadius(value: String): Modifier =
        style("border-radius", value)

    /**
     * Sets the text color.
     */
    fun color(value: String): Modifier =
        style("color", value)

    /**
     * Sets the font size.
     */
    fun fontSize(value: String): Modifier =
        style("font-size", value)

    /**
     * Sets the font weight (normal, bold, etc).
     */
    fun fontWeight(value: String): Modifier =
        style("font-weight", value)

    /**
     * Sets margins around the element.
     */
    fun margin(value: String): Modifier =
        style("margin", value)

    /**
     * Sets margins for each side individually.
     */
    fun margin(top: String, right: String, bottom: String, left: String): Modifier =
        style("margin", "$top $right $bottom $left")

    /**
     * Sets the element to fill its container.
     */
    fun fillMaxSize(): Modifier =
        this.fillMaxWidth().fillMaxHeight()

    /**
     * Sets the element to fill the width of its container.
     */
    fun fillMaxWidth(): Modifier =
        style("width", "100%")

    /**
     * Sets the element to fill the height of its container.
     */
    fun fillMaxHeight(): Modifier =
        style("height", "100%")

    /**
     * Sets box shadow for the element.
     */
    fun shadow(offsetX: String, offsetY: String, blurRadius: String, color: String): Modifier =
        style("box-shadow", "$offsetX $offsetY $blurRadius $color")

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
        var result = style("position", "absolute")
        if (top != null) result = result.style("top", top)
        if (right != null) result = result.style("right", right)
        if (bottom != null) result = result.style("bottom", bottom)
        if (left != null) result = result.style("left", left)
        return result
    }

    /**
     * Sets the element's opacity.
     */
    fun opacity(value: Float): Modifier =
        style("opacity", value.toString())

    /**
     * Adds a hover effect. Note: this will be applied differently based on platform.
     * For JVM it will generate a CSS class, for JS it will attach event listeners.
     */
    fun hover(hoverStyles: Map<String, String>): Modifier {
        // Store hover styles in a special key that platform implementations will handle
        return style("__hover", hoverStyles.entries.joinToString(";") { (k, v) -> "$k:$v" })
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
        style("max-width", value)

    /**
     * Sets the maximum height of the element.
     */
    fun maxHeight(value: String): Modifier =
        style("max-height", value)

    /**
     * Sets the background color of the element (alias for background).
     * @param color The CSS color value (name, hex, rgb, etc.)
     * @return A new Modifier with the added style
     */
    fun backgroundColor(color: String): Modifier =
        style("background-color", color)

    /**
     * Sets the margin at the bottom of the element.
     */
    fun marginBottom(value: String): Modifier =
        style("margin-bottom", value)

    /**
     * Sets the margin at the top of the element.
     */
    fun marginTop(value: String): Modifier =
        style("margin-top", value)

    /**
     * Sets the margin at the left of the element.
     */
    fun marginLeft(value: String): Modifier =
        style("margin-left", value)

    /**
     * Sets the margin at the right of the element.
     */
    fun marginRight(value: String): Modifier =
        style("margin-right", value)

    /**
     * Sets the cursor style.
     */
    fun cursor(value: String): Modifier =
        style("cursor", value)

    /**
     * Sets CSS transitions for animating property changes.
     */
    fun transitions(value: String): Modifier =
        style("transition", value)

    /**
     * Sets the flex-wrap property for flex containers.
     */
    fun flexWrap(value: String): Modifier =
        style("flex-wrap", value)

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
        style("box-shadow", value)

    /**
     * Sets the align-items property for flex containers.
     * @param value The alignment value (center, flex-start, flex-end, etc.)
     * @return A new Modifier with the added style
     */
    fun alignItems(value: String): Modifier =
        style("align-items", value)

    /**
     * Sets the justify-content property for flex containers.
     * @param value The justification value (center, flex-start, flex-end, space-between, space-around, etc.)
     * @return A new Modifier with the added style
     */
    fun justifyContent(value: String): Modifier =
        style("justify-content", value)

    /**
     * Sets the flex-direction property for flex containers.
     * @param value The direction value (row, column, row-reverse, column-reverse)
     * @return A new Modifier with the added style
     */
    fun flexDirection(value: String): Modifier =
        style("flex-direction", value)

    /**
     * Sets the display property for the element.
     * @param value The display value (flex, block, inline, none, etc.)
     * @return A new Modifier with the added style
     */
    fun display(value: String): Modifier =
        style("display", value)

    /**
     * Sets the text-decoration property for text styling.
     * @param value The text-decoration value (none, underline, line-through, etc.)
     * @return A new Modifier with the added style
     */
    fun textDecoration(value: String): Modifier =
        style("text-decoration", value)

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