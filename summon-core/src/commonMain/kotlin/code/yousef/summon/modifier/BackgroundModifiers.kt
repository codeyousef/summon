package code.yousef.summon.modifier

import kotlin.jvm.JvmName


/**
 * Background-related [Modifier] helpers extracted from the legacy `StylingModifiers` monolith.
 *
 * These extensions keep all background responsibilities together so that the remaining styling
 * files can focus on typography, borders, effects, and interaction state.  They can be composed
 * freely and mirror the CSS properties of the same names.
 */

/**
 * Applies a CSS `background-image` declaration.
 *
 * Accepts any valid CSS value (urls, gradients, multiple layers, etc).
 */
fun Modifier.backgroundImage(value: String): Modifier =
    style("background-image", value)

/**
 * Applies a CSS `background-size` declaration.  Supports keywords such as `cover`, `contain`,
 * percentage values, or explicit dimensions.
 */
fun Modifier.backgroundSize(value: String): Modifier =
    style("background-size", value)

/**
 * Applies a CSS `background-position` declaration.
 */
fun Modifier.backgroundPosition(value: String): Modifier =
    style("background-position", value)

/**
 * Applies a CSS `background-repeat` declaration.
 */
fun Modifier.backgroundRepeat(value: String): Modifier =
    style("background-repeat", value)

/**
 * Applies a CSS `background-clip` declaration using a raw string value.
 */
fun Modifier.backgroundClip(value: String): Modifier =
    style("background-clip", value)

/**
 * Applies a CSS `background-clip` declaration using the [BackgroundClip] enum.
 */
fun Modifier.backgroundClip(value: BackgroundClip): Modifier =
    style("background-clip", value.value)

/**
 * Convenience helper that assigns multiple background related properties in one shot.  Parameters
 * default to the CSS initial values so callers can override only the pieces they care about.
 */
fun Modifier.background(
    color: String = "transparent",
    image: String = "none",
    position: String = "0% 0%",
    size: String = "auto auto",
    repeat: String = "repeat"
): Modifier = this
    .style("background-color", color)
    .style("background-image", image)
    .style("background-position", position)
    .style("background-size", size)
    .style("background-repeat", repeat)


fun Modifier.radialGradient(
    shape: String = "circle",
    colors: List<String>,
    position: String = "center"
): Modifier {
    val colorStops = colors.joinToString(", ")
    return backgroundImage("radial-gradient($shape at $position, $colorStops)")
}

/**
 * Creates a radial gradient background using enum values for shape and position.
 *
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param colors List of color stops in the format "color position" (e.g., "rgba(0, 247, 255, 0.15) 0%")
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithEnums")
fun Modifier.radialGradient(
    shape: RadialGradientShape = RadialGradientShape.Circle,
    colors: List<String>,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(shape.toString(), colors, position.toString())
}

/**
 * Creates a radial gradient background with a simplified API.
 *
 * @param innerColor The color at the center of the gradient
 * @param outerColor The color at the edge of the gradient
 * @param innerPosition The position of the inner color (e.g., "0%")
 * @param outerPosition The position of the outer color (e.g., "70%")
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
fun Modifier.radialGradient(
    innerColor: String,
    outerColor: String,
    innerPosition: String = "0%",
    outerPosition: String = "100%",
    shape: String = "circle",
    position: String = "center"
): Modifier {
    return radialGradient(
        shape = shape,
        colors = listOf("$innerColor $innerPosition", "$outerColor $outerPosition"),
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using enum values for shape and position.
 *
 * @param innerColor The color at the center of the gradient
 * @param outerColor The color at the edge of the gradient
 * @param innerPosition The position of the inner color (e.g., "0%")
 * @param outerPosition The position of the outer color (e.g., "70%")
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithEnumsSimplified")
fun Modifier.radialGradient(
    innerColor: String,
    outerColor: String,
    innerPosition: String = "0%",
    outerPosition: String = "100%",
    shape: RadialGradientShape = RadialGradientShape.Circle,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(
        shape = shape.toString(),
        colors = listOf("$innerColor $innerPosition", "$outerColor $outerPosition"),
        position = position.toString()
    )
}

/**
 * Creates a radial gradient background with a simplified API using Number extensions for positions.
 *
 * @param innerColor The color at the center of the gradient
 * @param outerColor The color at the edge of the gradient
 * @param innerPosition The position of the inner color as a Number (will be converted to a percentage)
 * @param outerPosition The position of the outer color as a Number (will be converted to a percentage)
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithNumberPositions")
fun Modifier.radialGradient(
    innerColor: String,
    outerColor: String,
    innerPosition: Number,
    outerPosition: Number,
    shape: String = "circle",
    position: String = "center"
): Modifier {
    return radialGradient(
        innerColor = innerColor,
        outerColor = outerColor,
        innerPosition = "${innerPosition}%",
        outerPosition = "${outerPosition}%",
        shape = shape,
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using enum values and Number extensions.
 *
 * @param innerColor The color at the center of the gradient
 * @param outerColor The color at the edge of the gradient
 * @param innerPosition The position of the inner color as a Number (will be converted to a percentage)
 * @param outerPosition The position of the outer color as a Number (will be converted to a percentage)
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithEnumsAndNumberPositions")
fun Modifier.radialGradient(
    innerColor: String,
    outerColor: String,
    innerPosition: Number,
    outerPosition: Number,
    shape: RadialGradientShape = RadialGradientShape.Circle,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(
        innerColor = innerColor,
        outerColor = outerColor,
        innerPosition = "${innerPosition}%",
        outerPosition = "${outerPosition}%",
        shape = shape.toString(),
        position = position.toString()
    )
}

/**
 * Creates a radial gradient background using Color objects.
 *
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param colorStops List of Pairs containing Color objects and their positions
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorStops")
fun Modifier.radialGradient(
    shape: String = "circle",
    colorStops: List<Pair<code.yousef.summon.core.style.Color, String>>,
    position: String = "center"
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}" }
    return radialGradient(shape, colorStopStrings, position)
}

/**
 * Creates a radial gradient background using Color objects and enum values for shape and position.
 *
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param colorStops List of Pairs containing Color objects and their positions
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorStopsAndEnums")
fun Modifier.radialGradient(
    shape: RadialGradientShape = RadialGradientShape.Circle,
    colorStops: List<Pair<code.yousef.summon.core.style.Color, String>>,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}" }
    return radialGradient(shape.toString(), colorStopStrings, position.toString())
}

/**
 * Creates a radial gradient background using Color objects with Number positions.
 *
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param colorStops List of Pairs containing Color objects and their positions as Numbers (will be converted to percentages)
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorStopsAndNumberPositions")
fun Modifier.radialGradient(
    shape: String = "circle",
    colorStops: List<Pair<code.yousef.summon.core.style.Color, Number>>,
    position: String = "center"
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}%" }
    return radialGradient(shape, colorStopStrings, position)
}

/**
 * Creates a radial gradient background using Color objects, enum values, and Number positions.
 *
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param colorStops List of Pairs containing Color objects and their positions as Numbers (will be converted to percentages)
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorStopsEnumsAndNumberPositions")
fun Modifier.radialGradient(
    shape: RadialGradientShape = RadialGradientShape.Circle,
    colorStops: List<Pair<code.yousef.summon.core.style.Color, Number>>,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}%" }
    return radialGradient(shape.toString(), colorStopStrings, position.toString())
}

/**
 * Creates a radial gradient background with a simplified API using Color objects.
 *
 * @param innerColor The Color object at the center of the gradient
 * @param outerColor The Color object at the edge of the gradient
 * @param innerPosition The position of the inner color (e.g., "0%")
 * @param outerPosition The position of the outer color (e.g., "70%")
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorObjects")
fun Modifier.radialGradient(
    innerColor: code.yousef.summon.core.style.Color,
    outerColor: code.yousef.summon.core.style.Color,
    innerPosition: String = "0%",
    outerPosition: String = "100%",
    shape: String = "circle",
    position: String = "center"
): Modifier {
    return radialGradient(
        shape = shape,
        colorStops = listOf(innerColor to innerPosition, outerColor to outerPosition),
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using Color objects and enum values.
 *
 * @param innerColor The Color object at the center of the gradient
 * @param outerColor The Color object at the edge of the gradient
 * @param innerPosition The position of the inner color (e.g., "0%")
 * @param outerPosition The position of the outer color (e.g., "70%")
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorObjectsAndEnums")
fun Modifier.radialGradient(
    innerColor: code.yousef.summon.core.style.Color,
    outerColor: code.yousef.summon.core.style.Color,
    innerPosition: String = "0%",
    outerPosition: String = "100%",
    shape: RadialGradientShape = RadialGradientShape.Circle,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(
        shape = shape,
        colorStops = listOf(innerColor to innerPosition, outerColor to outerPosition),
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using Color objects and Number positions.
 *
 * @param innerColor The Color object at the center of the gradient
 * @param outerColor The Color object at the edge of the gradient
 * @param innerPosition The position of the inner color as a Number (will be converted to a percentage)
 * @param outerPosition The position of the outer color as a Number (will be converted to a percentage)
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorObjectsAndNumberPositions")
fun Modifier.radialGradient(
    innerColor: code.yousef.summon.core.style.Color,
    outerColor: code.yousef.summon.core.style.Color,
    innerPosition: Number,
    outerPosition: Number,
    shape: String = "circle",
    position: String = "center"
): Modifier {
    return radialGradient(
        innerColor = innerColor,
        outerColor = outerColor,
        innerPosition = "${innerPosition}%",
        outerPosition = "${outerPosition}%",
        shape = shape,
        position = position
    )
}

/**
 * Creates a radial gradient background with a simplified API using Color objects, enum values, and Number positions.
 *
 * @param innerColor The Color object at the center of the gradient
 * @param outerColor The Color object at the edge of the gradient
 * @param innerPosition The position of the inner color as a Number (will be converted to a percentage)
 * @param outerPosition The position of the outer color as a Number (will be converted to a percentage)
 * @param shape The shape of the gradient as a RadialGradientShape enum
 * @param position Optional position of the gradient center as a RadialGradientPosition enum
 * @return A new Modifier with the radial gradient background
 */
@JvmName("radialGradientWithColorObjectsEnumsAndNumberPositions")
fun Modifier.radialGradient(
    innerColor: code.yousef.summon.core.style.Color,
    outerColor: code.yousef.summon.core.style.Color,
    innerPosition: Number,
    outerPosition: Number,
    shape: RadialGradientShape = RadialGradientShape.Circle,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier {
    return radialGradient(
        innerColor = innerColor,
        outerColor = outerColor,
        innerPosition = "${innerPosition}%",
        outerPosition = "${outerPosition}%",
        shape = shape,
        position = position
    )
}

/**
 * Creates a linear gradient background.
 *
 * @param direction The direction of the gradient (e.g., "to right", "to bottom", "45deg")
 * @param colors List of color stops in the format "color position" (e.g., "rgba(0, 247, 255, 0.15) 0%")
 * @return A new Modifier with the linear gradient background
 */
fun Modifier.linearGradient(
    direction: String = "to right",
    colors: List<String>
): Modifier {
    val colorStops = colors.joinToString(", ")
    return backgroundImage("linear-gradient($direction, $colorStops)")
}

/**
 * Creates a linear gradient background with a simplified API.
 *
 * @param startColor The color at the start of the gradient
 * @param endColor The color at the end of the gradient
 * @param startPosition The position of the start color (e.g., "0%")
 * @param endPosition The position of the end color (e.g., "100%")
 * @param direction The direction of the gradient (e.g., "to right", "to bottom", "45deg")
 * @return A new Modifier with the linear gradient background
 */
fun Modifier.linearGradient(
    startColor: String,
    endColor: String,
    startPosition: String = "0%",
    endPosition: String = "100%",
    direction: String = "to right"
): Modifier {
    return linearGradient(
        direction = direction,
        colors = listOf("$startColor $startPosition", "$endColor $endPosition")
    )
}

/**
 * Creates a linear gradient background using Color objects.
 *
 * @param direction The direction of the gradient (e.g., "to right", "to bottom", "45deg")
 * @param colorStops List of Pairs containing Color objects and their positions
 * @return A new Modifier with the linear gradient background
 */
@JvmName("linearGradientWithColorStops")
fun Modifier.linearGradient(
    direction: String = "to right",
    colorStops: List<Pair<code.yousef.summon.core.style.Color, String>>
): Modifier {
    val colorStopStrings = colorStops.map { "${it.first} ${it.second}" }
    return linearGradient(direction, colorStopStrings)
}

/**
 * Creates a linear gradient background with a simplified API using Color objects.
 *
 * @param startColor The Color object at the start of the gradient
 * @param endColor The Color object at the end of the gradient
 * @param startPosition The position of the start color (e.g., "0%")
 * @param endPosition The position of the end color (e.g., "100%")
 * @param direction The direction of the gradient (e.g., "to right", "to bottom", "45deg")
 * @return A new Modifier with the linear gradient background
 */
@JvmName("linearGradientWithColorObjects")
fun Modifier.linearGradient(
    startColor: code.yousef.summon.core.style.Color,
    endColor: code.yousef.summon.core.style.Color,
    startPosition: String = "0%",
    endPosition: String = "100%",
    direction: String = "to right"
): Modifier {
    return linearGradient(
        direction = direction,
        colorStops = listOf(startColor to startPosition, endColor to endPosition)
    )
}

/**
 * Creates a linear gradient background with direction first and Color objects.
 *
 * @param gradientDirection The direction of the gradient (e.g., "to right", "to bottom", "45deg", "90deg")
 * @param startColor The Color object at the start of the gradient
 * @param endColor The Color object at the end of the gradient
 * @param startPosition The position of the start color (e.g., "0%")
 * @param endPosition The position of the end color (e.g., "100%")
 * @return A new Modifier with the linear gradient background
 */
@JvmName("linearGradientWithDirectionFirst")
fun Modifier.linearGradient(
    gradientDirection: String,
    startColor: code.yousef.summon.core.style.Color,
    endColor: code.yousef.summon.core.style.Color,
    startPosition: String = "0%",
    endPosition: String = "100%"
): Modifier {
    return linearGradient(
        direction = gradientDirection,
        colorStops = listOf(startColor to startPosition, endColor to endPosition)
    )
}
