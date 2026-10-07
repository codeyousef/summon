package codes.yousef.summon.modifier

import codes.yousef.summon.extensions.px

/**
 * Executes the border width operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.borderWidth(value: Number): Modifier =
    style("border-width", value.px)

/**
 * Executes the border width operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
@Deprecated("Use numeric overload for consistent units", replaceWith = ReplaceWith("borderWidth(value.px)"))
fun Modifier.borderWidth(value: String): Modifier =
    style("border-width", value)

/**
 * Executes the border width operation.
 *
 * @param value Value to process.
 * @param side The side value.
 * @return The resulting value.
 */
fun Modifier.borderWidth(value: Number, side: BorderSide): Modifier =
    style("border-${side.value}-width", value.px)

/**
 * Executes the border top width operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.borderTopWidth(value: Number): Modifier =
    style("border-top-width", value.px)

/**
 * Executes the border right width operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.borderRightWidth(value: Number): Modifier =
    style("border-right-width", value.px)

/**
 * Executes the border bottom width operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.borderBottomWidth(value: Number): Modifier =
    style("border-bottom-width", value.px)

/**
 * Executes the border left width operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.borderLeftWidth(value: Number): Modifier =
    style("border-left-width", value.px)

/**
 * Executes the border style operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.borderStyle(value: BorderStyle): Modifier =
    style("border-style", value.toString())

/**
 * Executes the border style operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.borderStyle(value: String): Modifier =
    style("border-style", value)

/**
 * Executes the border color operation.
 *
 * @param value Value to process.
 * @return The resulting value.
 */
fun Modifier.borderColor(value: String): Modifier =
    style("border-color", value)

/**
 * Executes the border color operation.
 *
 * @param color The color value.
 * @return The resulting value.
 */
fun Modifier.borderColor(color: codes.yousef.summon.core.style.Color): Modifier =
    style("border-color", color.toCssString())

/**
 * Executes the border operation.
 *
 * @param width The width value.
 * @param style The style value.
 * @param color The color value.
 * @param radius The radius value.
 * @return The resulting value.
 */
fun Modifier.border(
    width: Number? = null,
    style: String? = null,
    color: String? = null,
    radius: Number? = null
): Modifier {
    var result = this
    width?.let { result = result.borderWidth(it) }
    style?.let { result = result.borderStyle(style) }
    color?.let { result = result.borderColor(color) }
    radius?.let { result = result.borderRadius(it) }
    return result
}

/** Applies a complete border declaration to one physical side without affecting the others. */
fun Modifier.border(
    side: BorderSide,
    width: Number,
    style: BorderStyle,
    color: String
): Modifier = style("border-${side.value}", "${width.px} ${style.value} $color")
