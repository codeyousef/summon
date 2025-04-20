package code.yousef.summon.modifier

import kotlin.jvm.JvmName

/**
 * Extension functions for Styling Modifiers
 * These are implemented to match the test expectations in StylingModifierTest
 */

/**
 * Sets the background image.
 */
fun Modifier.backgroundImage(value: String): Modifier =
    style("background-image", value)

/**
 * Sets the background size.
 */
fun Modifier.backgroundSize(value: String): Modifier =
    style("background-size", value)

/**
 * Sets the background position.
 */
fun Modifier.backgroundPosition(value: String): Modifier =
    style("background-position", value)

/**
 * Sets the background repeat behavior.
 */
fun Modifier.backgroundRepeat(value: String): Modifier =
    style("background-repeat", value)

/**
 * Sets the background clip behavior.
 */
fun Modifier.backgroundClip(value: String): Modifier =
    style("background-clip", value)

/**
 * Sets the background clip behavior using the BackgroundClip enum.
 */
fun Modifier.backgroundClip(value: BackgroundClip): Modifier =
    backgroundClip(value.toString())

/**
 * Combined background settings.
 */
fun Modifier.background(color: String, image: String, position: String, size: String, repeat: String): Modifier =
    this.background(color)
        .backgroundImage(image)
        .backgroundPosition(position)
        .backgroundSize(size)
        .backgroundRepeat(repeat)

/**
 * Sets the font family.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("fontFamily(value, null)"))
fun Modifier.fontFamily(value: String): Modifier =
    fontFamily(value, null)

/**
 * Sets the font style (normal, italic, etc).
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("fontStyle(value, null)"))
fun Modifier.fontStyle(value: String): Modifier =
    fontStyle(value, null)

/**
 * Sets the text alignment.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textAlign(value, null)"))
fun Modifier.textAlign(value: String): Modifier =
    textAlign(value, null)

/**
 * Sets the text alignment using the TextAlign enum.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textAlign(value, null)"))
fun Modifier.textAlign(value: TextAlign): Modifier =
    textAlign(value, null)

/**
 * Sets the text decoration (underline, line-through, etc).
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textDecoration(value, null)"))
fun Modifier.textDecoration(value: String): Modifier =
    textDecoration(value, null)

/**
 * Sets the line height with a string value.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("lineHeight(value, null)"))
fun Modifier.lineHeight(value: String): Modifier =
    lineHeight(value, null)

/**
 * Sets the line height with a numeric value (e.g., 1.5).
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("lineHeight(value, null)"))
fun Modifier.lineHeight(value: Number): Modifier =
    lineHeight(value, null)

/**
 * Sets the letter spacing.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("letterSpacing(value, null)"))
fun Modifier.letterSpacing(value: String): Modifier =
    letterSpacing(value, null)

/**
 * Sets the letter spacing with a numeric value in pixels.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("letterSpacing(value, null)"))
fun Modifier.letterSpacing(value: Number): Modifier =
    letterSpacing(value, null)

/**
 * Sets the text transformation (uppercase, lowercase, etc).
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textTransform(value, null)"))
fun Modifier.textTransform(value: String): Modifier =
    textTransform(value, null)

/**
 * Sets the text transformation using the TextTransform enum.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("textTransform(value, null)"))
fun Modifier.textTransform(value: TextTransform): Modifier =
    textTransform(value, null)

/**
 * Sets the border width.
 * @deprecated Use the version with number parameter for type safety
 */
@Deprecated("Use the version with number parameter for type safety", ReplaceWith("borderWidth(value.toDouble())"))
fun Modifier.borderWidth(value: String): Modifier =
    style("border-width", value)

/**
 * Sets the border width with a numeric value in pixels.
 * 
 * @param value The width in pixels
 * @return A new Modifier with the border width style added
 */
fun Modifier.borderWidth(value: Number): Modifier =
    style("border-width", "${value}px")

/**
 * Sets the border width for a specific side with a numeric value in pixels.
 * 
 * @param value The width in pixels
 * @param side The side to apply the border width to (default is BorderSide.All)
 * @return A new Modifier with the border width style added
 */
fun Modifier.borderWidth(value: Number, side: BorderSide = BorderSide.All): Modifier =
    when (side) {
        BorderSide.All -> style("border-width", "${value}px")
        BorderSide.Top -> style("border-top-width", "${value}px")
        BorderSide.Right -> style("border-right-width", "${value}px")
        BorderSide.Bottom -> style("border-bottom-width", "${value}px")
        BorderSide.Left -> style("border-left-width", "${value}px")
    }

/**
 * Sets the top border width with a numeric value in pixels.
 * 
 * @param value The width in pixels
 * @return A new Modifier with the top border width style added
 */
fun Modifier.borderTopWidth(value: Number): Modifier =
    style("border-top-width", "${value}px")

/**
 * Sets the right border width with a numeric value in pixels.
 * 
 * @param value The width in pixels
 * @return A new Modifier with the right border width style added
 */
fun Modifier.borderRightWidth(value: Number): Modifier =
    style("border-right-width", "${value}px")

/**
 * Sets the bottom border width with a numeric value in pixels.
 * 
 * @param value The width in pixels
 * @return A new Modifier with the bottom border width style added
 */
fun Modifier.borderBottomWidth(value: Number): Modifier =
    style("border-bottom-width", "${value}px")

/**
 * Sets the left border width with a numeric value in pixels.
 * 
 * @param value The width in pixels
 * @return A new Modifier with the left border width style added
 */
fun Modifier.borderLeftWidth(value: Number): Modifier =
    style("border-left-width", "${value}px")

/**
 * Sets the border style (solid, dashed, etc).
 */
fun Modifier.borderStyle(value: String): Modifier =
    style("border-style", value)

/**
 * Sets the border style using the BorderStyle enum.
 */
fun Modifier.borderStyle(value: BorderStyle): Modifier =
    style("border-style", value.toString())

/**
 * Sets the border color.
 */
fun Modifier.borderColor(value: String): Modifier =
    style("border-color", value)


/**
 * Sets the border with all properties in one function.
 * 
 * @param width The border width (can be a Number or a String)
 * @param style The border style (can be a String or BorderStyle)
 * @param color The border color
 * @param radius The border radius (can be a Number or a String)
 * @return A new Modifier with all border properties applied
 */
fun Modifier.border(
    width: Number? = null,
    style: String? = null,
    color: String? = null,
    radius: Number? = null
): Modifier {
    var modifier = this
    if (width != null) {
        modifier = modifier.borderWidth(width)
    }
    if (style != null) {
        modifier = modifier.borderStyle(style)
    }
    if (color != null) {
        modifier = modifier.borderColor(color)
    }
    if (radius != null) {
        modifier = modifier.borderRadius("${radius}px")
    }
    return modifier
}

/**
 * Sets the border with all properties in one function.
 * 
 * @param width The border width (can be a Number or a String)
 * @param style The border style as a BorderStyle enum
 * @param color The border color
 * @param radius The border radius (can be a Number or a String)
 * @return A new Modifier with all border properties applied
 */
fun Modifier.border(
    width: Number? = null,
    style: BorderStyle? = null,
    color: String? = null,
    radius: Number? = null
): Modifier {
    var modifier = this
    if (width != null) {
        modifier = modifier.borderWidth(width)
    }
    if (style != null) {
        modifier = modifier.borderStyle(style)
    }
    if (color != null) {
        modifier = modifier.borderColor(color)
    }
    if (radius != null) {
        modifier = modifier.borderRadius("${radius}px")
    }
    return modifier
}

/**
 * Sets the border with all properties in one function.
 * 
 * @param width The border width as a String
 * @param style The border style (can be a String or BorderStyle)
 * @param color The border color
 * @param radius The border radius as a String
 * @return A new Modifier with all border properties applied
 */
fun Modifier.border(
    width: String? = null,
    style: String? = null,
    color: String? = null,
    radius: String? = null
): Modifier {
    var modifier = this
    if (width != null) {
        modifier = modifier.style("border-width", width)
    }
    if (style != null) {
        modifier = modifier.borderStyle(style)
    }
    if (color != null) {
        modifier = modifier.borderColor(color)
    }
    if (radius != null) {
        modifier = modifier.borderRadius(radius)
    }
    return modifier
}

/**
 * Sets the border with all properties in one function.
 * 
 * @param width The border width as a String
 * @param style The border style as a BorderStyle enum
 * @param color The border color
 * @param radius The border radius as a String
 * @return A new Modifier with all border properties applied
 */
fun Modifier.border(
    width: String? = null,
    style: BorderStyle? = null,
    color: String? = null,
    radius: String? = null
): Modifier {
    var modifier = this
    if (width != null) {
        modifier = modifier.style("border-width", width)
    }
    if (style != null) {
        modifier = modifier.borderStyle(style)
    }
    if (color != null) {
        modifier = modifier.borderColor(color)
    }
    if (radius != null) {
        modifier = modifier.borderRadius(radius)
    }
    return modifier
}

/**
 * Sets the box shadow.
 */
fun Modifier.boxShadow(value: String): Modifier =
    style("box-shadow", value)

/**
 * Sets the box shadow with specific parameters.
 *
 * @param horizontalOffset The horizontal offset of the shadow
 * @param verticalOffset The vertical offset of the shadow
 * @param blurRadius The blur radius of the shadow (use with .px extension, e.g., 20.px)
 * @param color The color of the shadow
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    color: code.yousef.summon.core.style.Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters using string values for all offsets.
 *
 * @param horizontalOffset The horizontal offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param verticalOffset The vertical offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param blurRadius The blur radius of the shadow (use with unit extensions, e.g., 20.px)
 * @param color The color of the shadow
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    color: code.yousef.summon.core.style.Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters including spread radius.
 *
 * @param horizontalOffset The horizontal offset of the shadow
 * @param verticalOffset The vertical offset of the shadow
 * @param blurRadius The blur radius of the shadow (use with .px extension, e.g., 20.px)
 * @param spreadRadius The spread radius of the shadow (use with .px extension, e.g., 5.px)
 * @param color The color of the shadow
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    spreadRadius: String,
    color: code.yousef.summon.core.style.Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters using string values for all offsets including spread radius.
 *
 * @param horizontalOffset The horizontal offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param verticalOffset The vertical offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param blurRadius The blur radius of the shadow (use with unit extensions, e.g., 20.px)
 * @param spreadRadius The spread radius of the shadow (use with unit extensions, e.g., 5.px)
 * @param color The color of the shadow
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    spreadRadius: String,
    color: code.yousef.summon.core.style.Color
): Modifier {
    val shadow = "$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters including inset option.
 *
 * @param horizontalOffset The horizontal offset of the shadow
 * @param verticalOffset The vertical offset of the shadow
 * @param blurRadius The blur radius of the shadow (use with .px extension, e.g., 20.px)
 * @param color The color of the shadow
 * @param inset Whether the shadow should be inset (inner shadow)
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters including spread radius and inset option.
 *
 * @param horizontalOffset The horizontal offset of the shadow
 * @param verticalOffset The vertical offset of the shadow
 * @param blurRadius The blur radius of the shadow (use with .px extension, e.g., 20.px)
 * @param spreadRadius The spread radius of the shadow (use with .px extension, e.g., 5.px)
 * @param color The color of the shadow
 * @param inset Whether the shadow should be inset (inner shadow)
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: Number,
    verticalOffset: Number,
    blurRadius: String,
    spreadRadius: String,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters using string values for all offsets and including inset option.
 *
 * @param horizontalOffset The horizontal offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param verticalOffset The vertical offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param blurRadius The blur radius of the shadow (use with unit extensions, e.g., 20.px)
 * @param color The color of the shadow
 * @param inset Whether the shadow should be inset (inner shadow)
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the box shadow with specific parameters using string values for all offsets including spread radius and inset option.
 *
 * @param horizontalOffset The horizontal offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param verticalOffset The vertical offset of the shadow (use with unit extensions, e.g., 0.px)
 * @param blurRadius The blur radius of the shadow (use with unit extensions, e.g., 20.px)
 * @param spreadRadius The spread radius of the shadow (use with unit extensions, e.g., 5.px)
 * @param color The color of the shadow
 * @param inset Whether the shadow should be inset (inner shadow)
 * @return A new Modifier with the box shadow style added
 */
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    spreadRadius: String,
    color: code.yousef.summon.core.style.Color,
    inset: Boolean = false
): Modifier {
    val insetStr = if (inset) "inset " else ""
    val shadow = "$insetStr$horizontalOffset $verticalOffset $blurRadius $spreadRadius $color"
    return boxShadow(shadow)
}

/**
 * Sets the transition property.
 */
fun Modifier.transition(value: String): Modifier =
    style("transition", value)

/**
 * Adds a CSS transition to an element with specified parameters.
 *
 * @param property CSS property to transition (as a TransitionProperty enum)
 * @param duration Duration of the transition in milliseconds
 * @param timingFunction CSS timing function (as a TransitionTimingFunction enum)
 * @param delay Delay before the transition starts, in milliseconds
 * @return A new Modifier with the transition styles added
 */
fun Modifier.transition(
    property: TransitionProperty = TransitionProperty.All,
    duration: Number = 300,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: Number = 0
): Modifier {
    val transition = "${property} ${duration}ms ${timingFunction} ${delay}ms"
    return style("transition", transition)
}

/**
 * Adds a CSS transition to an element with specified parameters.
 *
 * @param property CSS property to transition (as a String)
 * @param duration Duration of the transition in milliseconds
 * @param timingFunction CSS timing function (as a TransitionTimingFunction enum)
 * @param delay Delay before the transition starts, in milliseconds
 * @return A new Modifier with the transition styles added
 */
fun Modifier.transition(
    property: String,
    duration: Number = 300,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: Number = 0
): Modifier {
    val transition = "$property ${duration}ms ${timingFunction} ${delay}ms"
    return style("transition", transition)
}

/**
 * Adds a CSS transition to an element with specified parameters.
 *
 * @param property CSS property to transition (as a TransitionProperty enum)
 * @param duration Duration of the transition (using time unit extensions like 0.3.s or 300.ms)
 * @param timingFunction CSS timing function (as a TransitionTimingFunction enum)
 * @param delay Delay before the transition starts (using time unit extensions like 0.s or 0.ms)
 * @return A new Modifier with the transition styles added
 */
fun Modifier.transition(
    property: TransitionProperty = TransitionProperty.All,
    duration: String,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: String = "0ms"
): Modifier {
    val transition = "${property} $duration ${timingFunction} $delay"
    return style("transition", transition)
}

/**
 * Sets which properties should transition.
 */
fun Modifier.transitionProperty(value: String): Modifier =
    style("transition-property", value)

/**
 * Sets which properties should transition using the TransitionProperty enum.
 */
fun Modifier.transitionProperty(value: TransitionProperty): Modifier =
    style("transition-property", value.toString())

/**
 * Sets the transition duration.
 */
fun Modifier.transitionDuration(value: String): Modifier =
    style("transition-duration", value)

/**
 * Sets the transition duration using a Number with time unit extensions.
 * @param value The duration as a Number with time unit (e.g., 0.3.s, 300.ms)
 */
fun Modifier.transitionDuration(value: Number): Modifier =
    style("transition-duration", value.toString() + "ms")

/**
 * Sets the transition timing function.
 */
fun Modifier.transitionTimingFunction(value: String): Modifier =
    style("transition-timing-function", value)

/**
 * Sets the transition timing function using the TransitionTimingFunction enum.
 */
fun Modifier.transitionTimingFunction(value: TransitionTimingFunction): Modifier =
    style("transition-timing-function", value.toString())

/**
 * Sets the transition delay.
 */
fun Modifier.transitionDelay(value: String): Modifier =
    style("transition-delay", value)

/**
 * Sets the transition delay using a Number with time unit extensions.
 * @param value The delay as a Number with time unit (e.g., 0.s, 300.ms)
 */
fun Modifier.transitionDelay(value: Number): Modifier =
    style("transition-delay", value.toString() + "ms")

/**
 * Sets the transform property.
 */
fun Modifier.transform(value: String): Modifier =
    style("transform", value)

/**
 * Translates the element along the X axis.
 */
fun Modifier.translateX(value: String): Modifier =
    style("transform", "translateX(${value})")

/**
 * Translates the element along the Y axis.
 */
fun Modifier.translateY(value: String): Modifier =
    style("transform", "translateY(${value})")

/**
 * Scales the element.
 */
fun Modifier.scale(value: Double): Modifier =
    style("transform", "scale(${value})")

/**
 * Rotates the element.
 */
fun Modifier.rotate(value: String): Modifier =
    style("transform", "rotate(${value})")

/**
 * Applies a blur filter.
 */
fun Modifier.blur(value: String): Modifier =
    style("filter", "blur(${value})")

/**
 * Applies a brightness filter.
 */
fun Modifier.brightness(value: Double): Modifier =
    style("filter", "brightness(${value})")

/**
 * Applies a contrast filter.
 */
fun Modifier.contrast(value: Double): Modifier =
    style("filter", "contrast(${value})")

/**
 * Applies a grayscale filter.
 */
fun Modifier.grayscale(value: Double): Modifier =
    style("filter", "grayscale(${value})")

/**
 * Applies a custom filter.
 */
fun Modifier.filter(value: String): Modifier =
    style("filter", value)

/**
 * Sets the animation.
 */
fun Modifier.animation(value: String): Modifier =
    style("animation", value)

/**
 * Sets the animation name.
 */
fun Modifier.animationName(value: String): Modifier =
    style("animation-name", value)

/**
 * Sets the animation duration.
 */
fun Modifier.animationDuration(value: String): Modifier =
    style("animation-duration", value)

/**
 * Sets the animation timing function.
 */
fun Modifier.animationTimingFunction(value: String): Modifier =
    style("animation-timing-function", value)

/**
 * Sets the animation delay.
 */
fun Modifier.animationDelay(value: String): Modifier =
    style("animation-delay", value)

/**
 * Sets the animation iteration count.
 */
fun Modifier.animationIterationCount(value: String): Modifier =
    style("animation-iteration-count", value)

/**
 * Sets the scroll behavior.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("scrollBehavior(value, null)"))
fun Modifier.scrollBehavior(value: String): Modifier =
    scrollBehavior(value, null)

/**
 * Sets the scrollbar width.
 * @deprecated Use the version with component parameter for type safety
 */
@Deprecated("Use the version with component parameter for type safety", ReplaceWith("scrollbarWidth(value, null)"))
fun Modifier.scrollbarWidth(value: String): Modifier =
    scrollbarWidth(value, null)

/**
 * Adds hover effect using another Modifier.
 */
fun Modifier.hover(hoverModifier: Modifier): Modifier {
    return hover(hoverModifier.styles)
}

/**
 * Sets the pointer-events CSS property.
 * Common values: "auto", "none".
 */
fun Modifier.pointerEvents(value: String): Modifier =
    style("pointer-events", value)

/**
 * Creates a radial gradient background.
 * 
 * @param shape The shape of the gradient (e.g., "circle", "ellipse")
 * @param colors List of color stops in the format "color position" (e.g., "rgba(0, 247, 255, 0.15) 0%")
 * @param position Optional position of the gradient center (e.g., "center", "top left")
 * @return A new Modifier with the radial gradient background
 */
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
