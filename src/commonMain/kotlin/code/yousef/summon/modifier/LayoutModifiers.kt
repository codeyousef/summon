package code.yousef.summon.modifier

/**
 * Extension functions for Layout Modifiers
 * These are implemented to match the test expectations in LayoutModifierTest
 */

/**
 * Sets the minimum width of the element.
 */
fun Modifier.minWidth(value: String): Modifier =
    style("min-width", value)

/**
 * Sets the minimum height of the element.
 */
fun Modifier.minHeight(value: String): Modifier =
    style("min-height", value)

/**
 * Sets the maximum height of the element.
 */
fun Modifier.maxHeight(value: String): Modifier =
    style("max-height", value)

/**
 * Sets width to 100%.
 */
fun Modifier.fillMaxWidth(): Modifier =
    style("width", "100%")

/**
 * Sets padding on all sides.
 */
fun Modifier.padding(value: String): Modifier =
    style("padding", value)

/**
 * Sets different padding for horizontal and vertical.
 */
fun Modifier.padding(vertical: String, horizontal: String): Modifier =
    style("padding", "$vertical $horizontal")

/**
 * Sets padding for the top side of the element.
 */
fun Modifier.paddingTop(value: String): Modifier =
    style("padding-top", value)

/**
 * Sets padding for the right side of the element.
 */
fun Modifier.paddingRight(value: String): Modifier =
    style("padding-right", value)

/**
 * Sets padding for the bottom side of the element.
 */
fun Modifier.paddingBottom(value: String): Modifier =
    style("padding-bottom", value)

/**
 * Sets padding for the left side of the element.
 */
fun Modifier.paddingLeft(value: String): Modifier =
    style("padding-left", value)

/**
 * Sets margin for all sides.
 */
fun Modifier.margin(value: String): Modifier =
    style("margin", value)

/**
 * Sets margin for horizontal and vertical dimensions.
 */
fun Modifier.margin(vertical: String, horizontal: String): Modifier =
    style("margin", "$vertical $horizontal")

/**
 * Sets margin for all four sides individually.
 */
fun Modifier.margin(top: String, right: String, bottom: String, left: String): Modifier =
    style("margin", "$top $right $bottom $left")

/**
 * Sets margin for specific sides using named parameters.
 * This allows for more readable code when setting margin on only specific sides.
 */
fun Modifier.marginOf(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    // If only one parameter is provided, set that specific margin direction
    if (top != null && right == null && bottom == null && left == null) {
        return style("margin-top", top)
    }
    if (right != null && top == null && bottom == null && left == null) {
        return style("margin-right", right)
    }
    if (bottom != null && top == null && right == null && left == null) {
        return style("margin-bottom", bottom)
    }
    if (left != null && top == null && right == null && bottom == null) {
        return style("margin-left", left)
    }
    
    // If multiple parameters are provided, construct the margin string
    val marginParts = arrayOf(
        top ?: "0",
        right ?: "0",
        bottom ?: "0",
        left ?: "0"
    )
    return style("margin", marginParts.joinToString(" "))
}

/**
 * Sets padding for a specific side using a named parameter.
 * This allows for more readable code when setting padding on only one side.
 */
fun Modifier.paddingOf(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    // If only one parameter is provided, set that specific padding direction
    if (top != null && right == null && bottom == null && left == null) {
        return style("padding-top", top)
    }
    if (right != null && top == null && bottom == null && left == null) {
        return style("padding-right", right)
    }
    if (bottom != null && top == null && right == null && left == null) {
        return style("padding-bottom", bottom)
    }
    if (left != null && top == null && right == null && bottom == null) {
        return style("padding-left", left)
    }
    
    // If multiple parameters are provided, construct the padding string
    val paddingParts = arrayOf(
        top ?: "0",
        right ?: "0",
        bottom ?: "0",
        left ?: "0"
    )
    return style("padding", paddingParts.joinToString(" "))
}

/**
 * Sets margin for the top side of the element.
 */
fun Modifier.marginTop(value: String): Modifier =
    style("margin-top", value)

/**
 * Sets margin for the right side of the element.
 */
fun Modifier.marginRight(value: String): Modifier =
    style("margin-right", value)

/**
 * Sets margin for the bottom side of the element.
 */
fun Modifier.marginBottom(value: String): Modifier =
    style("margin-bottom", value)

/**
 * Sets margin for the left side of the element.
 */
fun Modifier.marginLeft(value: String): Modifier =
    style("margin-left", value)

/**
 * Sets the flex wrap property.
 */
fun Modifier.flexWrap(value: String): Modifier =
    style("flex-wrap", value)

/**
 * Sets the flex grow property.
 */
fun Modifier.flexGrow(value: Int): Modifier =
    style("flex-grow", value.toString())

/**
 * Sets the flex shrink property.
 */
fun Modifier.flexShrink(value: Int): Modifier =
    style("flex-shrink", value.toString())

/**
 * Sets the flex basis property.
 */
fun Modifier.flexBasis(value: String): Modifier =
    style("flex-basis", value)

/**
 * Sets the align-self property.
 */
fun Modifier.alignSelf(value: String): Modifier =
    style("align-self", value)

/**
 * Sets the align-content property.
 */
fun Modifier.alignContent(value: String): Modifier =
    style("align-content", value)

/**
 * Sets the justify-items property.
 */
fun Modifier.justifyItems(value: String): Modifier =
    style("justify-items", value)

/**
 * Sets the justify-self property.
 */
fun Modifier.justifySelf(value: String): Modifier =
    style("justify-self", value)

/**
 * Sets the grid-gap property.
 */
fun Modifier.gridGap(value: String): Modifier =
    style("grid-gap", value)

/**
 * Sets the grid-area property.
 */
fun Modifier.gridArea(value: String): Modifier =
    style("grid-area", value)

/**
 * Sets the grid-column property.
 */
fun Modifier.gridColumn(value: String): Modifier =
    style("grid-column", value)

/**
 * Sets the grid-row property.
 */
fun Modifier.gridRow(value: String): Modifier =
    style("grid-row", value)

/**
 * Sets the z-index property.
 */
fun Modifier.zIndex(value: String): Modifier =
    style("z-index", value)

/**
 * Sets the overflow-x property.
 */
fun Modifier.overflowX(value: String): Modifier =
    style("overflow-x", value)

/**
 * Sets the overflow-y property.
 */
fun Modifier.overflowY(value: String): Modifier =
    style("overflow-y", value)

/**
 * Sets the visibility property.
 */
fun Modifier.visibility(value: String): Modifier =
    style("visibility", value)

/**
 * Sets just the border width property.
 */
fun Modifier.border(width: String): Modifier =
    style("border-width", width)

/**
 * Sets the border-left property.
 */
fun Modifier.borderLeft(width: String, style: String, color: String): Modifier =
    style("border-left", "$width $style $color")

/**
 * Sets the font-size property.
 */
fun Modifier.fontSize(value: String): Modifier =
    style("font-size", value)

/**
 * Sets the cursor property.
 */
fun Modifier.cursor(value: String): Modifier =
    style("cursor", value)
