package codes.yousef.summon.modifier

/**
 * Extension functions for Modifier to provide convenient number-based overloads
 * for common CSS properties.
 */

// Margin extensions - only Number overloads to avoid shadowing String members
fun Modifier.margin(value: Number): Modifier = style("margin", "${value}px")
fun Modifier.margin(vertical: Number, horizontal: Number): Modifier = style("margin", "${vertical}px ${horizontal}px")

fun Modifier.marginTop(value: Number): Modifier = style("margin-top", "${value}px")

fun Modifier.marginRight(value: Number): Modifier = style("margin-right", "${value}px")

fun Modifier.marginBottom(value: Number): Modifier = style("margin-bottom", "${value}px")

fun Modifier.marginLeft(value: Number): Modifier = style("margin-left", "${value}px")

// Padding extensions - only Number overloads to avoid shadowing String members
fun Modifier.padding(value: Number): Modifier = style("padding", "${value}px")
fun Modifier.padding(vertical: Number, horizontal: Number): Modifier = style("padding", "${vertical}px ${horizontal}px")

fun Modifier.paddingTop(value: Number): Modifier = style("padding-top", "${value}px")

fun Modifier.paddingRight(value: Number): Modifier = style("padding-right", "${value}px")

fun Modifier.paddingBottom(value: Number): Modifier = style("padding-bottom", "${value}px")

fun Modifier.paddingLeft(value: Number): Modifier = style("padding-left", "${value}px")

// Size extensions - only Number overloads to avoid shadowing String members
fun Modifier.width(value: Number): Modifier = style("width", "${value}px")

fun Modifier.height(value: Number): Modifier = style("height", "${value}px")

fun Modifier.minWidth(value: Number): Modifier = style("min-width", "${value}px")

fun Modifier.minHeight(value: Number): Modifier = style("min-height", "${value}px")

fun Modifier.maxWidth(value: Number): Modifier = style("max-width", "${value}px")

fun Modifier.maxHeight(value: Number): Modifier = style("max-height", "${value}px")

fun Modifier.size(width: Number, height: Number): Modifier =
    this.width(width).height(height)

fun Modifier.size(value: Number): Modifier =
    this.width(value).height(value)

// Other styling extensions - only Number overloads to avoid shadowing String members
fun Modifier.borderRadius(value: Number): Modifier = style("border-radius", "${value}px")

fun Modifier.fontSize(value: Number): Modifier = style("font-size", "${value}px")
