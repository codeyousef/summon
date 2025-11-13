package codes.yousef.summon.modifier

import codes.yousef.summon.extensions.px

fun Modifier.borderWidth(value: Number): Modifier =
    style("border-width", value.px)

@Deprecated("Use numeric overload for consistent units", replaceWith = ReplaceWith("borderWidth(value.px)"))
fun Modifier.borderWidth(value: String): Modifier =
    style("border-width", value)

fun Modifier.borderWidth(value: Number, side: BorderSide): Modifier =
    style("border-${side.value}-width", value.px)

fun Modifier.borderTopWidth(value: Number): Modifier =
    style("border-top-width", value.px)

fun Modifier.borderRightWidth(value: Number): Modifier =
    style("border-right-width", value.px)

fun Modifier.borderBottomWidth(value: Number): Modifier =
    style("border-bottom-width", value.px)

fun Modifier.borderLeftWidth(value: Number): Modifier =
    style("border-left-width", value.px)

fun Modifier.borderStyle(value: BorderStyle): Modifier =
    style("border-style", value.toString())

fun Modifier.borderStyle(value: String): Modifier =
    style("border-style", value)

fun Modifier.borderColor(value: String): Modifier =
    style("border-color", value)

fun Modifier.borderColor(color: code.yousef.summon.core.style.Color): Modifier =
    style("border-color", color.toCssString())

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
