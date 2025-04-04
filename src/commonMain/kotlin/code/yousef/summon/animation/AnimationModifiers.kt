package code.yousef.summon.animation

import code.yousef.summon.Modifier

/**
 * Animation extensions for the Modifier class
 */

/**
 * Adds a CSS animation to an element.
 *
 * @param name The name of the animation
 * @param duration Duration of the animation in milliseconds
 * @param timingFunction CSS timing function (e.g., "ease", "linear", etc.)
 * @param delay Delay before the animation starts, in milliseconds
 * @param iterationCount Number of times the animation should run, or "infinite"
 * @param direction Direction of the animation ("normal", "reverse", "alternate", etc.)
 * @param fillMode Fill mode of the animation ("none", "forwards", "backwards", "both")
 * @return A new Modifier with the animation styles added
 */
fun Modifier.animate(
    name: String,
    duration: Int = 300,
    timingFunction: String = "ease",
    delay: Int = 0,
    iterationCount: String = "1",
    direction: String = "normal",
    fillMode: String = "forwards"
): Modifier {
    val animation = "$name ${duration}ms $timingFunction ${delay}ms $iterationCount $direction $fillMode"
    return Modifier(this.styles + ("animation" to animation))
}

/**
 * Adds a spring animation using the SpringAnimation class.
 *
 * @param name The name of the animation
 * @param stiffness Spring stiffness coefficient
 * @param damping Spring damping coefficient
 * @param duration Duration of the animation in milliseconds
 * @param iterationCount Number of times the animation should run, or "infinite"
 * @return A new Modifier with the spring animation styles added
 */
fun Modifier.spring(
    name: String,
    stiffness: Float = 170f,
    damping: Float = 26f,
    duration: Int = 400,
    iterationCount: String = "1"
): Modifier {
    val springAnimation = SpringAnimation(stiffness, damping, duration, iterationCount == "infinite")
    val timingFunction = "cubic-bezier(0.5, 0.0, 0.2, 1.0)" // Spring-like curve

    val animation = "$name ${duration}ms $timingFunction 0ms $iterationCount normal forwards"
    return Modifier(this.styles + ("animation" to animation))
}

/**
 * Adds a CSS transition to an element.
 *
 * @param property CSS property to transition (or "all" for all properties)
 * @param duration Duration of the transition in milliseconds
 * @param timingFunction CSS timing function (e.g., "ease", "linear", etc.)
 * @param delay Delay before the transition starts, in milliseconds
 * @return A new Modifier with the transition styles added
 */
fun Modifier.transition(
    property: String = "all",
    duration: Int = 300,
    timingFunction: String = "ease",
    delay: Int = 0
): Modifier {
    val transition = "$property ${duration}ms $timingFunction ${delay}ms"
    return Modifier(this.styles + ("transition" to transition))
}

/**
 * Adds a fade-in animation to an element.
 *
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @return A new Modifier with the fade-in animation styles added
 */
fun Modifier.fadeIn(
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    return this
        .transition("opacity", duration, "ease-in", delay)
        .opacity(1f)
}

/**
 * Adds a fade-out animation to an element.
 *
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @return A new Modifier with the fade-out animation styles added
 */
fun Modifier.fadeOut(
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    return this
        .transition("opacity", duration, "ease-out", delay)
        .opacity(0f)
}

/**
 * Sets the opacity of an element.
 *
 * @param value Opacity value (0.0 to 1.0)
 * @return A new Modifier with the opacity style added
 */
fun Modifier.opacity(value: Float): Modifier {
    return Modifier(this.styles + ("opacity" to value.toString()))
}

/**
 * Adds a slide-in animation from the specified direction.
 *
 * @param direction The direction to slide from
 * @param distance The distance to slide (e.g., "100px", "100%")
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @return A new Modifier with the slide-in animation styles added
 */
fun Modifier.slideIn(
    direction: SlideDirection = SlideDirection.LEFT,
    distance: String = "100%",
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    val (property, value) = when (direction) {
        SlideDirection.LEFT -> "transform" to "translateX(-$distance)"
        SlideDirection.RIGHT -> "transform" to "translateX($distance)"
        SlideDirection.UP -> "transform" to "translateY(-$distance)"
        SlideDirection.DOWN -> "transform" to "translateY($distance)"
    }

    return this
        .transition(property, duration, "ease-out", delay)
        .transition("opacity", duration, "ease-out", delay)
        .opacity(1f)
}

/**
 * Adds a slide-out animation in the specified direction.
 *
 * @param direction The direction to slide to
 * @param distance The distance to slide (e.g., "100px", "100%")
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @return A new Modifier with the slide-out animation styles added
 */
fun Modifier.slideOut(
    direction: SlideDirection = SlideDirection.LEFT,
    distance: String = "100%",
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    val (property, value) = when (direction) {
        SlideDirection.LEFT -> "transform" to "translateX(-$distance)"
        SlideDirection.RIGHT -> "transform" to "translateX($distance)"
        SlideDirection.UP -> "transform" to "translateY(-$distance)"
        SlideDirection.DOWN -> "transform" to "translateY($distance)"
    }

    return this
        .transition(property, duration, "ease-in", delay)
        .transition("opacity", duration, "ease-in", delay)
        .opacity(0f)
        .style(property, value)
}

/**
 * Adds a scale animation to an element.
 *
 * @param scaleX X-axis scale factor
 * @param scaleY Y-axis scale factor
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @return A new Modifier with the scale animation styles added
 */
fun Modifier.scale(
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    duration: Int = 300,
    delay: Int = 0
): Modifier {
    val transform = "scale(${scaleX}, ${scaleY})"
    return this
        .transition("transform", duration, "ease", delay)
        .style("transform", transform)
}

/**
 * Adds a custom style property to a Modifier.
 *
 * @param property The CSS property name
 * @param value The CSS property value
 * @return A new Modifier with the style added
 */
fun Modifier.style(property: String, value: String): Modifier {
    return Modifier(this.styles + (property to value))
} 