package codes.yousef.summon.modifier

import codes.yousef.summon.css.CssValue

/**
 * Builds a CSS `min()` function value.
 *
 * ````kotlin
 * Modifier.width(cssMin("1200px", "92vw"))
 * ````
 */
fun cssMin(vararg values: CssValue): CssValue {
    require(values.size >= 2) { "cssMin requires at least two values" }
    return "min(${values.joinToString(", ")})"
}

/**
 * Builds a CSS `max()` function value.
 */
fun cssMax(vararg values: CssValue): CssValue {
    require(values.size >= 2) { "cssMax requires at least two values" }
    return "max(${values.joinToString(", ")})"
}

/**
 * Builds a CSS `clamp(min, preferred, max)` value.
 */
fun cssClamp(min: CssValue, preferred: CssValue, max: CssValue): CssValue =
    "clamp($min, $preferred, $max)"
