package code.yousef.summon.extensions

import code.yousef.summon.modifier.Modifier

/**
 * Extensions for Modifier to work with Number values
 */

// Margin extensions

/**
 * Sets the margin for all sides using a Number in pixels.
 */
fun Modifier.margin(value: Number): Modifier = margin("${value}px")

/**
 * Sets the margin for vertical and horizontal sides using Numbers in pixels.
 */
fun Modifier.margin(vertical: Number, horizontal: Number): Modifier = 
    margin("${vertical}px ${horizontal}px")

/**
 * Sets the top margin using a Number in pixels.
 */
fun Modifier.marginTop(value: Number): Modifier = marginTop("${value}px")

/**
 * Sets the right margin using a Number in pixels.
 */
fun Modifier.marginRight(value: Number): Modifier = marginRight("${value}px")

/**
 * Sets the bottom margin using a Number in pixels.
 */
fun Modifier.marginBottom(value: Number): Modifier = marginBottom("${value}px")

/**
 * Sets the left margin using a Number in pixels.
 */
fun Modifier.marginLeft(value: Number): Modifier = marginLeft("${value}px")

// Padding extensions

/**
 * Sets the padding for all sides using a Number in pixels.
 */
fun Modifier.padding(value: Number): Modifier = padding("${value}px")

/**
 * Sets the padding for vertical and horizontal sides using Numbers in pixels.
 */
fun Modifier.padding(vertical: Number, horizontal: Number): Modifier = 
    padding("${vertical}px ${horizontal}px")

/**
 * Sets the top padding using a Number in pixels.
 */
fun Modifier.paddingTop(value: Number): Modifier = style("padding-top", "${value}px")

/**
 * Sets the right padding using a Number in pixels.
 */
fun Modifier.paddingRight(value: Number): Modifier = style("padding-right", "${value}px")

/**
 * Sets the bottom padding using a Number in pixels.
 */
fun Modifier.paddingBottom(value: Number): Modifier = style("padding-bottom", "${value}px")

/**
 * Sets the left padding using a Number in pixels.
 */
fun Modifier.paddingLeft(value: Number): Modifier = style("padding-left", "${value}px")

// Size extensions

/**
 * Sets the width using a Number in pixels.
 */
fun Modifier.width(value: Number): Modifier = width("${value}px")

/**
 * Sets the width using a CSS viewport width value.
 */
fun Modifier.width(vw: String): Modifier = width(vw)

/**
 * Sets the height using a Number in pixels.
 */
fun Modifier.height(value: Number): Modifier = height("${value}px")

/**
 * Sets the height using a CSS viewport height value.
 */
fun Modifier.height(vh: String): Modifier = height(vh)

/**
 * Sets the minimum width using a Number in pixels.
 */
fun Modifier.minWidth(value: Number): Modifier = style("min-width", "${value}px")

/**
 * Sets the minimum width using a CSS unit string.
 */
fun Modifier.minWidth(value: String): Modifier = style("min-width", value)

/**
 * Sets the minimum height using a Number in pixels.
 */
fun Modifier.minHeight(value: Number): Modifier = style("min-height", "${value}px")

/**
 * Sets the minimum height using a CSS unit string.
 */
fun Modifier.minHeight(value: String): Modifier = style("min-height", value)

/**
 * Sets the maximum width using a Number in pixels.
 */
fun Modifier.maxWidth(value: Number): Modifier = maxWidth("${value}px")

/**
 * Sets the maximum width using a CSS unit string.
 */
fun Modifier.maxWidth(value: String): Modifier = style("max-width", value)

/**
 * Sets the maximum height using a Number in pixels.
 */
fun Modifier.maxHeight(value: Number): Modifier = style("max-height", "${value}px")

/**
 * Sets the maximum height using a CSS unit string.
 */
fun Modifier.maxHeight(value: String): Modifier = style("max-height", value)

/**
 * Sets both width and height using Numbers in pixels.
 */
fun Modifier.size(width: Number, height: Number): Modifier = size("${width}px", "${height}px")

/**
 * Sets equal width and height using a Number in pixels.
 */
fun Modifier.size(value: Number): Modifier = size("${value}px")

/**
 * Sets the border radius using a Number in pixels.
 */
fun Modifier.borderRadius(value: Number): Modifier = borderRadius("${value}px")

/**
 * Sets the font size using a Number in pixels.
 */
fun Modifier.fontSize(value: Number): Modifier = fontSize("${value}px")

/**
 * Sets the font size using a CSS unit string (rem, em, etc).
 */
fun Modifier.fontSize(value: String): Modifier = style("font-size", value) 