package code.yousef.summon.modifier

import code.yousef.summon.modifier.ModifierExtras.attribute
import code.yousef.summon.modifier.ModifierExtras.pointerEvents
import code.yousef.summon.modifier.StylingModifierExtras.transform
import code.yousef.summon.modifier.StylingModifierExtras.textAlign
import code.yousef.summon.modifier.StylingModifierExtras.boxShadow
import code.yousef.summon.modifier.StylingModifierExtras.transition
import code.yousef.summon.modifier.StylingModifierExtras.fontStyle
import code.yousef.summon.modifier.StylingModifierExtras.textDecoration
import code.yousef.summon.modifier.ModifierExtras.onClick

/**
 * Extension functions for the Modifier class to support additional styling options.
 */

/**
 * Sets the gap between flex items.
 * @param value The CSS gap value (e.g., "10px", "1rem", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.gap(value: String): Modifier =
    style("gap", value)

/**
 * Sets the opacity of the element.
 * @param value The opacity value (0.0 to 1.0 or percentage string)
 * @return A new Modifier with the added style
 */
fun Modifier.opacity(value: Float): Modifier =
    style("opacity", value.toString())

/**
 * Sets the opacity of the element using a string value.
 * @param value The opacity value as a string
 * @return A new Modifier with the added style
 */
fun Modifier.opacity(value: String): Modifier =
    style("opacity", value)

/**
 * Sets the border radius of the element.
 * @param value The CSS border radius value (e.g., "4px", "50%", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.borderRadius(value: String): Modifier =
    style("border-radius", value)

/**
 * Sets the maximum width of the element.
 * @param value The CSS max-width value (e.g., "800px", "100%", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.maxWidth(value: String): Modifier =
    style("max-width", value)

/**
 * Sets the position property of the element.
 * @param value The CSS position value (e.g., "absolute", "relative", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.position(value: String): Modifier =
    style("position", value)

/**
 * Sets the top position of the element.
 * @param value The CSS top value (e.g., "10px", "0", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.top(value: String): Modifier =
    style("top", value)

/**
 * Sets the right position of the element.
 * @param value The CSS right value (e.g., "10px", "0", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.right(value: String): Modifier =
    style("right", value)

/**
 * Sets the bottom position of the element.
 * @param value The CSS bottom value (e.g., "10px", "0", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.bottom(value: String): Modifier =
    style("bottom", value)

/**
 * Sets the left position of the element.
 * @param value The CSS left value (e.g., "10px", "0", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.left(value: String): Modifier =
    style("left", value)

/**
 * Sets the display property of the element.
 * @param value The CSS display value (e.g., "flex", "grid", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.display(value: String): Modifier =
    style("display", value)

/**
 * Sets the flex-direction property of the element.
 * @param value The CSS flex-direction value (e.g., "row", "column", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.flexDirection(value: String): Modifier =
    style("flex-direction", value)

/**
 * Sets the justify-content property of the element.
 * @param value The CSS justify-content value (e.g., "center", "space-between", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.justifyContent(value: String): Modifier =
    style("justify-content", value)

/**
 * Sets the align-items property of the element.
 * @param value The CSS align-items value (e.g., "center", "stretch", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.alignItems(value: String): Modifier =
    style("align-items", value)

/**
 * Sets one or more borders for the element.
 * @param width The border width (e.g., "1px")
 * @param style The border style (e.g., "solid")
 * @param color The border color (e.g., "#000")
 * @return A new Modifier with the added style
 */
fun Modifier.border(width: String, style: String, color: String): Modifier =
    style("border", "$width $style $color")

/**
 * Sets the overflow property of the element.
 * @param value The CSS overflow value (e.g., "hidden", "auto", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.overflow(value: String): Modifier =
    style("overflow", value)

/**
 * Sets the flex property of the element.
 * @param value The CSS flex value (e.g., "1", "0 1 auto", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.flex(value: String): Modifier =
    style("flex", value)

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
 * Sets the font-weight property of the element.
 * @param value The CSS font-weight value (e.g., "bold", "400", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.fontWeight(value: String): Modifier =
    style("font-weight", value)

/**
 * Sets the text color of the element.
 * @param value The CSS color value (name, hex, rgb, etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.color(value: String): Modifier =
    style("color", value)

/**
 * Sets the width of the element.
 * @param value The CSS width value (e.g., "200px", "50%", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.width(value: String): Modifier =
    style("width", value)

/**
 * Sets the height of the element.
 * @param value The CSS height value (e.g., "100px", "50vh", etc.)
 * @return A new Modifier with the added style
 */
fun Modifier.height(value: String): Modifier =
    style("height", value)

/**
 * Adds a custom attribute to the element.
 * @param name The attribute name
 * @param value The attribute value
 * @return A new Modifier with the added attribute
 */
fun Modifier.attribute(name: String, value: String): Modifier =
    style("__attr:$name", value)

/**
 * Sets an onClick handler for the element.
 * @param handler The handler function
 * @return A new Modifier with the added event handler
 */
fun Modifier.onClick(handler: () -> Unit): Modifier =
    style("onclick", handler.toString()) 