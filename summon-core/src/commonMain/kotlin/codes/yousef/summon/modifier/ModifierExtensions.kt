package codes.yousef.summon.modifier

/**
 * Extension functions for Modifier to provide convenient number-based overloads
 * for common CSS properties.
 */

// Margin extensions - only Number overloads to avoid shadowing String members
/**
 * Executes the margin operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.margin(value: Number): Modifier = style("margin", "${value}px")
/**
 * Executes the margin operation.
 *
 * @param vertical The vertical value.
 * @param horizontal The horizontal value.
 * @return The resulting value.
 */
fun Modifier.margin(vertical: Number, horizontal: Number): Modifier = style("margin", "${vertical}px ${horizontal}px")

/**
 * Executes the margin top operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.marginTop(value: Number): Modifier = style("margin-top", "${value}px")

/**
 * Executes the margin right operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.marginRight(value: Number): Modifier = style("margin-right", "${value}px")

/**
 * Executes the margin bottom operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.marginBottom(value: Number): Modifier = style("margin-bottom", "${value}px")

/**
 * Executes the margin left operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.marginLeft(value: Number): Modifier = style("margin-left", "${value}px")

// Padding extensions - only Number overloads to avoid shadowing String members
/**
 * Executes the padding operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.padding(value: Number): Modifier = style("padding", "${value}px")
/**
 * Executes the padding operation.
 *
 * @param vertical The vertical value.
 * @param horizontal The horizontal value.
 * @return The resulting value.
 */
fun Modifier.padding(vertical: Number, horizontal: Number): Modifier = style("padding", "${vertical}px ${horizontal}px")

/**
 * Executes the padding top operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.paddingTop(value: Number): Modifier = style("padding-top", "${value}px")

/**
 * Executes the padding right operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.paddingRight(value: Number): Modifier = style("padding-right", "${value}px")

/**
 * Executes the padding bottom operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.paddingBottom(value: Number): Modifier = style("padding-bottom", "${value}px")

/**
 * Executes the padding left operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.paddingLeft(value: Number): Modifier = style("padding-left", "${value}px")

// Size extensions - only Number overloads to avoid shadowing String members
/**
 * Executes the width operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.width(value: Number): Modifier = style("width", "${value}px")

/**
 * Executes the height operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.height(value: Number): Modifier = style("height", "${value}px")

/**
 * Executes the min width operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.minWidth(value: Number): Modifier = style("min-width", "${value}px")

/**
 * Executes the min height operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.minHeight(value: Number): Modifier = style("min-height", "${value}px")

/**
 * Executes the max width operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.maxWidth(value: Number): Modifier = style("max-width", "${value}px")

/**
 * Executes the max height operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.maxHeight(value: Number): Modifier = style("max-height", "${value}px")

/**
 * Executes the size operation.
 *
 * @param width The width value.
 * @param height The height value.
 * @return The resulting value.
 */
fun Modifier.size(width: Number, height: Number): Modifier =
    this.width(width).height(height)

/**
 * Executes the size operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.size(value: Number): Modifier =
    this.width(value).height(value)

// Other styling extensions - only Number overloads to avoid shadowing String members
/**
 * Executes the border radius operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.borderRadius(value: Number): Modifier = style("border-radius", "${value}px")

/**
 * Executes the font size operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.fontSize(value: Number): Modifier = style("font-size", "${value}px")
