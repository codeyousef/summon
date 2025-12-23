package codes.yousef.summon.i18n

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable

/**
 * Creates padding that adapts to the current layout direction
 *
 * @param value The padding value
 * @return Modifier with appropriate directional padding
 */
@Composable
fun Modifier.paddingStart(value: String): Modifier {
    val direction = LocalLayoutDirection.current
    // Always invoke the function to get the actual value
    val actualDirection = direction.invoke()
    return if (actualDirection == LayoutDirection.LTR) {
        style("padding-left", value)
    } else {
        style("padding-right", value)
    }
}

/**
 * Creates padding that adapts to the current layout direction
 *
 * @param value The padding value
 * @return Modifier with appropriate directional padding
 */
@Composable
fun Modifier.paddingEnd(value: String): Modifier {
    val direction = LocalLayoutDirection.current
    // Always invoke the function to get the actual value
    val actualDirection = direction.invoke()
    return if (actualDirection == LayoutDirection.LTR) {
        style("padding-right", value)
    } else {
        style("padding-left", value)
    }
}

/**
 * Creates margin that adapts to the current layout direction
 *
 * @param value The margin value
 * @return Modifier with appropriate directional margin
 */
@Composable
fun Modifier.marginStart(value: String): Modifier {
    val direction = LocalLayoutDirection.current
    // Always invoke the function to get the actual value
    val actualDirection = direction.invoke()
    return if (actualDirection == LayoutDirection.LTR) {
        style("margin-left", value)
    } else {
        style("margin-right", value)
    }
}

/**
 * Creates margin that adapts to the current layout direction
 *
 * @param value The margin value
 * @return Modifier with appropriate directional margin
 */
@Composable
fun Modifier.marginEnd(value: String): Modifier {
    val direction = LocalLayoutDirection.current
    // Always invoke the function to get the actual value
    val actualDirection = direction.invoke()
    return if (actualDirection == LayoutDirection.LTR) {
        style("margin-right", value)
    } else {
        style("margin-left", value)
    }
}

/**
 * Adds a border to the start (leading) edge of an element
 *
 * @param width Border width
 * @param style Border style
 * @param color Border color
 * @return Modifier with appropriate directional border
 */
@Composable
fun Modifier.borderStart(width: String, style: String, color: String): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = direction.invoke()

    val property = if (actualDirection == LayoutDirection.LTR) "border-left" else "border-right"
    return style(property, "$width $style $color")
}

/**
 * Adds a border to the end (trailing) edge of an element
 *
 * @param width Border width
 * @param style Border style
 * @param color Border color
 * @return Modifier with appropriate directional border
 */
@Composable
fun Modifier.borderEnd(width: String, style: String, color: String): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = direction.invoke()

    val property = if (actualDirection == LayoutDirection.LTR) "border-right" else "border-left"
    return style(property, "$width $style $color")
}

/**
 * Adds flexbox direction that respects the current layout direction
 *
 * @return Modifier with appropriate flex direction
 */
@Composable
fun Modifier.flexRow(): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = direction.invoke()

    val value = if (actualDirection == LayoutDirection.LTR) "row" else "row-reverse"
    return style("flex-direction", value)
}

/**
 * Set the text alignment to the start (leading) edge
 *
 * @return Modifier with appropriate text alignment
 */
@Composable
fun Modifier.textStart(): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = direction.invoke()

    val value = if (actualDirection == LayoutDirection.LTR) "left" else "right"
    return style("text-align", value)
}

/**
 * Set the text alignment to the end (trailing) edge
 *
 * @return Modifier with appropriate text alignment
 */
@Composable
fun Modifier.textEnd(): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = direction.invoke()

    val value = if (actualDirection == LayoutDirection.LTR) "right" else "left"
    return style("text-align", value)
}

/**
 * Apply the direction attribute to an element
 *
 * @return Modifier with the direction attribute
 */
@Composable
fun Modifier.withDirection(): Modifier {
    val direction = LocalLayoutDirection.current
    val actualDirection = direction.invoke()

    val value = if (actualDirection == LayoutDirection.LTR) "ltr" else "rtl"
    return attribute("dir", value)
} 