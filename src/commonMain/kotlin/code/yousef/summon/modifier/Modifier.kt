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
     * Alias for background() to match CSS property name
     */
    fun backgroundColor(color: String): Modifier =
        background(color)

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
     * Sets the maximum width of the element.
     */
    fun maxWidth(value: String): Modifier =
        style("max-width", value)

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
     * Adds a border to the element using the BorderStyle enum.
     */
    fun border(width: String, style: BorderStyle, color: String): Modifier =
        this.style("border", "$width ${style.toString()} $color")

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
     * @deprecated Use the version with component parameter for type safety
     */
    @Deprecated("Use the version with component parameter for type safety", ReplaceWith("fontWeight(value, null)"))
    fun fontWeight(value: String): Modifier =
        fontWeight(value, null)

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
     * Sets the object-fit property for images.
     * Valid values include: fill, contain, cover, none, scale-down
     * @deprecated Use the version with component parameter for type safety
     */
    @Deprecated("Use the version with component parameter for type safety", ReplaceWith("objectFit(value, null)"))
    fun objectFit(value: String): Modifier =
        objectFit(value, null)

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
     * Sets the cursor style using the Cursor enum.
     */
    fun cursor(value: Cursor): Modifier =
        style("cursor", value.toString())

    /**
     * Sets the z-index of the element.
     */
    fun zIndex(value: Int): Modifier =
        style("z-index", value.toString())

    // --- Text Specific Styles ---

    /**
     * Sets the text alignment.
     * @param value CSS text-align value (e.g., "left", "center", "right", "justify")
     */
    fun textAlign(value: String): Modifier =
        style("text-align", value)

    /**
     * Sets the text alignment using the TextAlign enum.
     * @param value TextAlign enum value (e.g., TextAlign.Left, TextAlign.Center, TextAlign.Right)
     */
    fun textAlign(value: TextAlign): Modifier =
        style("text-align", value.toString())

    /**
     * Sets the font family.
     * @param value CSS font-family value (e.g., "Arial, sans-serif")
     */
    fun fontFamily(value: String): Modifier =
        style("font-family", value)

    /**
     * Sets the text decoration.
     * @param value CSS text-decoration value (e.g., "none", "underline", "line-through")
     */
    fun textDecoration(value: String): Modifier =
        style("text-decoration", value)

    /**
     * Sets the text transformation.
     * @param value CSS text-transform value (e.g., "none", "uppercase", "lowercase", "capitalize")
     */
    fun textTransform(value: String): Modifier =
        style("text-transform", value)

    /**
     * Sets the text transformation using the TextTransform enum.
     * @param value TextTransform enum value (e.g., TextTransform.None, TextTransform.Uppercase)
     */
    fun textTransform(value: TextTransform): Modifier =
        style("text-transform", value.toString())

    /**
     * Sets the letter spacing.
     * @param value CSS letter-spacing value (e.g., "normal", "1px", "0.1em")
     */
    fun letterSpacing(value: String): Modifier =
        style("letter-spacing", value)

    /**
     * Sets the letter spacing with a numeric value in pixels.
     * @param value Numeric value in pixels (e.g., 1 becomes "1px")
     */
    fun letterSpacing(value: Number): Modifier =
        style("letter-spacing", "${value}px")

    /**
     * Sets how white space inside the element is handled.
     * @param value CSS white-space value (e.g., "normal", "nowrap", "pre")
     */
    fun whiteSpace(value: String): Modifier =
        style("white-space", value)

    /**
     * Sets how words are broken to prevent overflow.
     * @param value CSS word-break value (e.g., "normal", "break-all", "keep-all")
     */
    fun wordBreak(value: String): Modifier =
        style("word-break", value)

    /**
     * Sets the spacing between words.
     * @param value CSS word-spacing value (e.g., "normal", "2px")
     */
    fun wordSpacing(value: String): Modifier =
        style("word-spacing", value)

    /**
     * Sets the text shadow.
     * @param value CSS text-shadow value (e.g., "1px 1px 2px black")
     */
    fun textShadow(value: String): Modifier =
        style("text-shadow", value)

    /**
     * Sets the line height.
     * @param value CSS line-height value (e.g., "normal", "1.5", "24px")
     */
    fun lineHeight(value: String): Modifier =
        style("line-height", value)

    /**
     * Sets how text overflow is handled, often used with `maxLines` or `whiteSpace("nowrap")`.
     * @param value CSS overflow and text-overflow value (e.g., "ellipsis", "clip")
     */
    fun textOverflow(value: String): Modifier =
        // Note: Applying to both 'overflow' and 'text-overflow' might be needed depending on context
        style("overflow", "hidden") // Typically required for text-overflow to work
            .style("text-overflow", value)

    /**
     * Limits the text content to a specific number of lines, showing an ellipsis if overflow occurs.
     * Uses CSS -webkit-line-clamp, browser compatibility may vary.
     * @param lines Maximum number of lines.
     */
    fun maxLines(lines: Int): Modifier =
        styles(
            mapOf(
                "display" to "-webkit-box",
                "-webkit-line-clamp" to lines.toString(),
                "-webkit-box-orient" to "vertical",
                "overflow" to "hidden"
            )
        )

} 
