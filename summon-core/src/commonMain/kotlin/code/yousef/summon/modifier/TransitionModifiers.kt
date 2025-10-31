package code.yousef.summon.modifier

import code.yousef.summon.core.mapOfCompat

fun Modifier.transition(value: String): Modifier =
    style("transition", value)

fun Modifier.transition(
    property: TransitionProperty = TransitionProperty.All,
    duration: Number = 300,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: Number = 0
): Modifier {
    val transition = "${property} ${duration}ms ${timingFunction} ${delay}ms"
    return style("transition", transition)
}

fun Modifier.transition(
    property: String,
    duration: Number = 300,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: Number = 0
): Modifier {
    val transition = "$property ${duration}ms ${timingFunction} ${delay}ms"
    return style("transition", transition)
}

fun Modifier.transition(
    property: TransitionProperty = TransitionProperty.All,
    duration: String,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: String = "0ms"
): Modifier {
    val transition = "${property} $duration ${timingFunction} $delay"
    return style("transition", transition)
}

fun Modifier.transitionProperty(value: String): Modifier =
    style("transition-property", value)

fun Modifier.transitionProperty(value: TransitionProperty): Modifier =
    style("transition-property", value.toString())

fun Modifier.transitionDuration(value: String): Modifier =
    style("transition-duration", value)

fun Modifier.transitionDuration(value: Number): Modifier =
    style("transition-duration", value.toString() + "ms")

fun Modifier.transitionTimingFunction(value: String): Modifier =
    style("transition-timing-function", value)

fun Modifier.transitionTimingFunction(value: TransitionTimingFunction): Modifier =
    style("transition-timing-function", value.toString())

fun Modifier.transitionDelay(value: String): Modifier =
    style("transition-delay", value)

fun Modifier.transitionDelay(value: Number): Modifier =
    style("transition-delay", value.toString() + "ms")

fun Modifier.hover(hoverModifier: Modifier): Modifier =
    hover(hoverModifier.styles)

fun Modifier.pointerEvents(value: String): Modifier =
    style("pointer-events", value)

fun Modifier.backdropFilter(value: String): Modifier =
    style("backdrop-filter", value)

fun Modifier.backdropBlur(value: String): Modifier =
    backdropFilter("blur($value)")

fun Modifier.backdropBlur(value: Number): Modifier =
    backdropBlur("${value}px")

fun Modifier.backdropBrightness(value: Number): Modifier =
    backdropFilter("brightness($value)")

fun Modifier.backdropContrast(value: Number): Modifier =
    backdropFilter("contrast($value)")

fun Modifier.backdropSaturate(value: Number): Modifier =
    backdropFilter("saturate($value)")

fun Modifier.backdropGrayscale(value: Number): Modifier =
    backdropFilter("grayscale($value)")

fun Modifier.backdropHueRotate(degrees: Number): Modifier =
    backdropFilter("hue-rotate(${degrees}deg)")

fun Modifier.backdropInvert(value: Number): Modifier =
    backdropFilter("invert($value)")

fun Modifier.backdropSepia(value: Number): Modifier =
    backdropFilter("sepia($value)")

fun Modifier.backdropFilters(vararg filters: String): Modifier =
    backdropFilter(filters.joinToString(" "))

fun Modifier.hoverElevation(elevation: String): Modifier =
    hover(mapOfCompat("box-shadow" to elevation))
