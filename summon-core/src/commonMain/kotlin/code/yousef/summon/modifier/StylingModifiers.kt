package code.yousef.summon.modifier

import code.yousef.summon.extensions.px

/**
 * Box shadow and remaining styling utilities that have not yet been split into dedicated modules.
 * Most styling concerns now live in files such as `BackgroundModifiers`, `TypographyModifiers`,
 * and `BorderModifiers`.
 */
// ========================
// Type-Safe Transform & Filter APIs  
// ========================

/**
 * Applies multiple CSS transform functions in a type-safe manner.
 *
 * @param transforms Variable number of transform function pairs
 * @return A new Modifier with combined transforms applied
 */
fun Modifier.transform(vararg transforms: Pair<TransformFunction, String>): Modifier =
    style("transform", transforms.joinToString(" ") { "${it.first}(${it.second})" })

/**
 * Applies multiple CSS filter functions in a type-safe manner.
 *
 * @param filters Variable number of filter function pairs
 * @return A new Modifier with combined filters applied
 */
fun Modifier.filter(vararg filters: Pair<FilterFunction, String>): Modifier =
    style("filter", filters.joinToString(" ") { "${it.first}(${it.second})" })

/**
 * Applies a single CSS filter function.
 */
fun Modifier.filter(function: FilterFunction, value: String): Modifier =
    filter(function to value)

/**
 * Applies a single CSS filter function with numeric value and optional unit.
 */
fun Modifier.filter(function: FilterFunction, value: Number, unit: String): Modifier =
    filter(function to "$value$unit")
/**
 * DSL for composing CSS filters with strong typing.
 */
class FilterBuilder internal constructor() {
    private val parts = mutableListOf<String>()

    private fun add(function: FilterFunction, value: String) {
        parts += "${function}($value)"
    }

    fun blur(radius: Number) = add(FilterFunction.Blur, radius.px)
    fun brightness(amount: Number) = add(FilterFunction.Brightness, amount.toString())
    fun contrast(amount: Number) = add(FilterFunction.Contrast, amount.toString())
    fun grayscale(amount: Number) = add(FilterFunction.Grayscale, amount.toString())
    fun hueRotate(degrees: Number) = add(FilterFunction.HueRotate, "${degrees}deg")
    fun invert(amount: Number) = add(FilterFunction.Invert, amount.toString())
    fun saturate(amount: Number) = add(FilterFunction.Saturate, amount.toString())
    fun sepia(amount: Number) = add(FilterFunction.Sepia, amount.toString())
    fun dropShadow(offsetX: String, offsetY: String, blurRadius: String = "0", color: String = "currentColor") =
        add(FilterFunction.DropShadow, listOf(offsetX, offsetY, blurRadius, color).joinToString(" ").trim())

    fun raw(value: String) {
        parts += value
    }

    internal fun build(): String = parts.joinToString(" ")
}

/**
 * Filter builder variant: `Modifier.filter { blur(12.px); saturate(1.1) }`.
 */
fun Modifier.filter(builder: FilterBuilder.() -> Unit): Modifier {
    val result = FilterBuilder().apply(builder).build()
    require(result.isNotBlank()) { "filter builder must define at least one filter" }
    return style("filter", result)
}

/**
 * Applies CSS backdrop-filter using the builder DSL.
 */
fun Modifier.backdropFilter(builder: FilterBuilder.() -> Unit): Modifier {
    val result = FilterBuilder().apply(builder).build()
    require(result.isNotBlank()) { "backdropFilter builder must define at least one filter" }
    return style("backdrop-filter", result)
}

/**
 * Applies a 3D rotation with type-safe parameters.
 *
 * @param x X-axis component
 * @param y Y-axis component
 * @param z Z-axis component
 * @param angle Rotation angle with unit extension (e.g., 45.deg)
 * @return A new Modifier with 3D rotation applied
 */
fun Modifier.rotate3d(x: Number, y: Number, z: Number, angle: Number): Modifier =
    transform(TransformFunction.Rotate3d to "$x $y $z ${angle}deg")

/**
 * Applies CSS perspective with type-safe units.
 *
 * @param distance Perspective distance with unit extension (e.g., 1000.px)
 * @return A new Modifier with perspective applied
 */
fun Modifier.perspective(distance: Number): Modifier =
    transform(TransformFunction.Perspective to distance.px)

/**
 * Applies multiple filters with type-safe parameters.
 *
 * @param blur Blur radius with unit extension (e.g., 4.px)
 * @param brightness Brightness multiplier (e.g., 1.1)
 * @param contrast Contrast multiplier (e.g., 1.05)
 * @param saturate Saturation multiplier (e.g., 1.2)
 * @return A new Modifier with combined filters applied
 */
fun Modifier.multiFilter(
    blur: Number? = null,
    brightness: Number? = null,
    contrast: Number? = null,
    saturate: Number? = null
): Modifier {
    val filters = mutableListOf<Pair<FilterFunction, String>>()
    blur?.let { filters.add(FilterFunction.Blur to it.px) }
    brightness?.let { filters.add(FilterFunction.Brightness to it.toString()) }
    contrast?.let { filters.add(FilterFunction.Contrast to it.toString()) }
    saturate?.let { filters.add(FilterFunction.Saturate to it.toString()) }
    return filter(*filters.toTypedArray())
}

/**
 * Applies type-safe CSS animation with enum parameters.
 *
 * @param name Animation name
 * @param duration Animation duration enum
 * @param easing Easing function
 * @param delay Animation delay enum
 * @param iterationCount Number of iterations or "infinite"
 * @param direction Animation direction enum
 * @param fillMode Animation fill mode enum
 * @return A new Modifier with animation applied
 */
fun Modifier.animation(
    name: String,
    duration: AnimationDuration = AnimationDuration.Medium,
    easing: String = "ease",
    delay: AnimationDuration = AnimationDuration.Instant,
    iterationCount: String = "1",
    direction: AnimationDirection = AnimationDirection.Normal,
    fillMode: AnimationFillMode = AnimationFillMode.None
): Modifier =
    style("animation", "$name $duration $easing $delay $iterationCount $direction $fillMode")

/**
 * Applies CSS mix-blend-mode using a type-safe enum.
 */
fun Modifier.mixBlendMode(value: BlendMode): Modifier =
    style("mix-blend-mode", value.toString())

/**
 * Applies CSS mix-blend-mode using a raw value.
 */
fun Modifier.mixBlendMode(value: String): Modifier =
    style("mix-blend-mode", value)

/**
 * Applies CSS background-blend-mode for multiple background layers using type-safe enums.
 *
 * @param modes Blend modes applied in order; must provide at least one value.
 */
fun Modifier.backgroundBlendModes(vararg modes: BlendMode): Modifier {
    require(modes.isNotEmpty()) { "backgroundBlendModes requires at least one mode" }
    return style("background-blend-mode", modes.joinToString(", ") { it.toString() })
}

/**
 * Applies CSS background-blend-mode using a raw string (for advanced custom sequences).
 */
fun Modifier.backgroundBlendModes(value: String): Modifier =
    style("background-blend-mode", value)
