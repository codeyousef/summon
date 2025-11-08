package code.yousef.summon.modifier

/**
 * Builds a CSS `min()` function value.
 *
 * ````kotlin
 * Modifier.width(cssMin("1200px", "92vw"))
 * ````
 */
fun cssMin(vararg values: String): String {
    require(values.size >= 2) { "cssMin requires at least two values" }
    return "min(${values.joinToString(", ")})"
}

/**
 * Builds a CSS `max()` function value.
 */
fun cssMax(vararg values: String): String {
    require(values.size >= 2) { "cssMax requires at least two values" }
    return "max(${values.joinToString(", ")})"
}

/**
 * Builds a CSS `clamp(min, preferred, max)` value.
 */
fun cssClamp(min: String, preferred: String, max: String): String =
    "clamp($min, $preferred, $max)"
