package code.yousef.summon.modifier

/**
 * Provides layout-related extension functions for the Modifier class,
 * organized in a way that allows for explicit imports 
 * to resolve ambiguity issues.
 */
object LayoutModifierExtras {
    /**
     * Sets the max-width property of the element.
     * @param value The CSS max-width value (e.g., "100%", "300px", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.maxWidth(value: String): Modifier =
        style("max-width", value)
        
    /**
     * Sets the width property of the element.
     * @param value The CSS width value (e.g., "100%", "300px", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.width(value: String): Modifier =
        style("width", value)
        
    /**
     * Sets the height property of the element.
     * @param value The CSS height value (e.g., "100%", "300px", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.height(value: String): Modifier =
        style("height", value)
        
    /**
     * Sets the position property of the element.
     * @param value The CSS position value (e.g., "relative", "absolute", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.position(value: String): Modifier =
        style("position", value)
        
    /**
     * Sets the top property of the element.
     * @param value The CSS top value (e.g., "0", "10px", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.top(value: String): Modifier =
        style("top", value)
        
    /**
     * Sets the right property of the element.
     * @param value The CSS right value (e.g., "0", "10px", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.right(value: String): Modifier =
        style("right", value)
        
    /**
     * Sets the bottom property of the element.
     * @param value The CSS bottom value (e.g., "0", "10px", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.bottom(value: String): Modifier =
        style("bottom", value)
        
    /**
     * Sets the left property of the element.
     * @param value The CSS left value (e.g., "0", "10px", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.left(value: String): Modifier =
        style("left", value)
        
    /**
     * Sets the flex property of the element.
     * @param value The CSS flex value (e.g., "1", "1 1 auto", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.flex(value: String): Modifier =
        style("flex", value)
        
    /**
     * Sets the flex-direction property of the element.
     * @param value The CSS flex-direction value (e.g., "row", "column", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.flexDirection(value: String): Modifier =
        style("flex-direction", value)
        
    /**
     * Sets the display property of the element.
     * @param value The CSS display value (e.g., "flex", "block", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.display(value: String): Modifier =
        style("display", value)
        
    /**
     * Sets the grid-template-columns property of the element.
     * @param value The CSS grid-template-columns value (e.g., "1fr 1fr", "repeat(3, 1fr)", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.gridTemplateColumns(value: String): Modifier =
        style("grid-template-columns", value)
        
    /**
     * Sets the grid-column-gap property of the element.
     * @param value The CSS grid-column-gap value (e.g., "10px", "1rem", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.gridColumnGap(value: String): Modifier =
        style("grid-column-gap", value)
        
    /**
     * Sets the grid-row-gap property of the element.
     * @param value The CSS grid-row-gap value (e.g., "10px", "1rem", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.gridRowGap(value: String): Modifier =
        style("grid-row-gap", value)
        
    /**
     * Sets the overflow property of the element.
     * @param value The CSS overflow value (e.g., "hidden", "auto", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.overflow(value: String): Modifier =
        style("overflow", value)
        
    /**
     * Sets the border property of the element.
     * @param width The border width
     * @param style The border style
     * @param color The border color
     * @return A new Modifier with the added style
     */
    fun Modifier.border(width: String, style: String, color: String): Modifier =
        style("border", "$width $style $color")
        
    /**
     * Sets the border-radius property of the element.
     * @param value The CSS border-radius value (e.g., "4px", "50%", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.borderRadius(value: String): Modifier =
        style("border-radius", value)
        
    /**
     * Sets the font-weight property of the element.
     * @param value The CSS font-weight value (e.g., "bold", "400", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.fontWeight(value: String): Modifier =
        style("font-weight", value)
        
    /**
     * Sets the color property of the element.
     * @param value The CSS color value (e.g., "red", "#ff0000", etc.)
     * @return A new Modifier with the added style
     */
    fun Modifier.color(value: String): Modifier =
        style("color", value)
} 