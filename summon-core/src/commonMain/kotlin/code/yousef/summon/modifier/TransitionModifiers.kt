package codes.yousef.summon.modifier

import codes.yousef.summon.core.mapOfCompat

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

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated("Extension shadowed by member function", level = DeprecationLevel.HIDDEN)
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

/**
 * Applies styles when the element has focus.
 * 
 * @param focusModifier Modifier containing styles to apply on focus
 * @return A new [Modifier] with focus styles
 */
fun Modifier.focus(focusModifier: Modifier): Modifier =
    focus(focusModifier.styles)

/**
 * Applies styles when the element has focus using a map of CSS properties.
 * 
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with focus styles
 */
fun Modifier.focus(styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val focusString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-focus-styles", focusString)
}

/**
 * Applies styles when the element is active (being clicked/pressed).
 * 
 * @param activeModifier Modifier containing styles to apply when active
 * @return A new [Modifier] with active styles
 */
fun Modifier.active(activeModifier: Modifier): Modifier =
    active(activeModifier.styles)

/**
 * Applies styles when the element is active using a map of CSS properties.
 * 
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with active styles
 */
fun Modifier.active(styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val activeString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-active-styles", activeString)
}

/**
 * Applies styles when the element or any of its descendants have focus.
 * 
 * @param focusWithinModifier Modifier containing styles to apply when focus is within
 * @return A new [Modifier] with focus-within styles
 */
fun Modifier.focusWithin(focusWithinModifier: Modifier): Modifier =
    focusWithin(focusWithinModifier.styles)

/**
 * Applies styles when the element or any of its descendants have focus using a map of CSS properties.
 * 
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with focus-within styles
 */
fun Modifier.focusWithin(styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val focusWithinString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-focus-within-styles", focusWithinString)
}

/**
 * Applies styles when the element is the first child of its parent.
 * 
 * @param firstChildModifier Modifier containing styles to apply when first child
 * @return A new [Modifier] with first-child styles
 */
fun Modifier.firstChild(firstChildModifier: Modifier): Modifier =
    firstChild(firstChildModifier.styles)

/**
 * Applies styles when the element is the first child using a map of CSS properties.
 * 
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with first-child styles
 */
fun Modifier.firstChild(styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val firstChildString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-first-child-styles", firstChildString)
}

/**
 * Applies styles when the element is the last child of its parent.
 * 
 * @param lastChildModifier Modifier containing styles to apply when last child
 * @return A new [Modifier] with last-child styles
 */
fun Modifier.lastChild(lastChildModifier: Modifier): Modifier =
    lastChild(lastChildModifier.styles)

/**
 * Applies styles when the element is the last child using a map of CSS properties.
 * 
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with last-child styles
 */
fun Modifier.lastChild(styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val lastChildString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-last-child-styles", lastChildString)
}

/**
 * Applies styles when the element matches the nth-child selector.
 * 
 * @param n The nth-child formula (e.g., "2n", "odd", "even", "3")
 * @param nthChildModifier Modifier containing styles to apply
 * @return A new [Modifier] with nth-child styles
 */
fun Modifier.nthChild(n: String, nthChildModifier: Modifier): Modifier =
    nthChild(n, nthChildModifier.styles)

/**
 * Applies styles when the element matches the nth-child selector using a map of CSS properties.
 * 
 * @param n The nth-child formula (e.g., "2n", "odd", "even", "3")
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with nth-child styles
 */
fun Modifier.nthChild(n: String, styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val nthChildString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-nth-child-styles", "$n:$nthChildString")
}

/**
 * Applies styles when the element is an only child.
 * 
 * @param onlyChildModifier Modifier containing styles to apply when only child
 * @return A new [Modifier] with only-child styles
 */
fun Modifier.onlyChild(onlyChildModifier: Modifier): Modifier =
    onlyChild(onlyChildModifier.styles)

/**
 * Applies styles when the element is an only child using a map of CSS properties.
 * 
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with only-child styles
 */
fun Modifier.onlyChild(styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val onlyChildString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-only-child-styles", onlyChildString)
}

/**
 * Applies styles when the element is visited (for links).
 * 
 * @param visitedModifier Modifier containing styles to apply when visited
 * @return A new [Modifier] with visited styles
 */
fun Modifier.visited(visitedModifier: Modifier): Modifier =
    visited(visitedModifier.styles)

/**
 * Applies styles when the element is visited using a map of CSS properties.
 * 
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with visited styles
 */
fun Modifier.visited(styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val visitedString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-visited-styles", visitedString)
}

/**
 * Applies styles when the element is disabled.
 * 
 * @param disabledModifier Modifier containing styles to apply when disabled
 * @return A new [Modifier] with disabled styles
 */
fun Modifier.disabledStyles(disabledModifier: Modifier): Modifier =
    disabledStyles(disabledModifier.styles)

/**
 * Applies styles when the element is disabled using a map of CSS properties.
 * 
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with disabled styles
 */
fun Modifier.disabledStyles(styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val disabledString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-disabled-styles", disabledString)
}

/**
 * Applies styles when the element is checked (for checkboxes/radio buttons).
 * 
 * @param checkedModifier Modifier containing styles to apply when checked
 * @return A new [Modifier] with checked styles
 */
fun Modifier.checkedStyles(checkedModifier: Modifier): Modifier =
    checkedStyles(checkedModifier.styles)

/**
 * Applies styles when the element is checked using a map of CSS properties.
 * 
 * @param styles Map of CSS property names to values
 * @return A new [Modifier] with checked styles
 */
fun Modifier.checkedStyles(styles: Map<String, String>): Modifier {
    if (styles.isEmpty()) return this
    val checkedString = styles.entries.joinToString(";") { "${it.key}:${it.value}" }
    return attribute("data-checked-styles", checkedString)
}
