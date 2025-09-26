/**
 * # Animation Modifiers
 *
 * Comprehensive animation modifier extensions for the Summon framework.
 * This module provides type-safe, declarative animation capabilities through
 * the [Modifier] system, enabling rich interactive experiences across platforms.
 *
 * ## Key Features
 *
 * - **CSS Animation Integration**: Direct CSS animation property control
 * - **Predefined Animation Effects**: Common animations like fade, slide, zoom
 * - **Spring Physics**: Natural spring-based animations with customizable physics
 * - **Easing Functions**: Rich set of timing functions for smooth motion
 * - **Transition Control**: Fine-grained transition property management
 * - **Performance Optimized**: Hardware-accelerated animations where possible
 *
 * ## Animation Categories
 *
 * ### Basic Animations
 * - `animate()` - Core CSS animation configuration
 * - `animTransition()` - CSS transition properties
 * - `animOpacity()` - Opacity control with transitions
 *
 * ### Motion Effects
 * - `fadeIn()` / `fadeOut()` - Opacity-based entrance/exit
 * - `slideIn()` / `slideOut()` - Directional slide animations
 * - `zoomIn()` / `zoomOut()` - Scale-based entrance/exit
 * - `scale()` - Custom scaling with timing control
 *
 * ### Interactive Animations
 * - `bounce()` - Bouncing effect with elastic easing
 * - `elastic()` - Elastic overshoot animations
 * - `shake()` - Attention-grabbing shake effect
 * - `pulse()` - Rhythmic pulsing animation
 * - `float()` - Gentle floating motion
 *
 * ### Advanced Effects
 * - `spring()` - Physics-based spring animations
 * - `flipX()` / `flipY()` - 3D flip transformations
 * - `typingCursor()` - Text cursor blinking effect
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic fade-in animation
 * Box(
 *     modifier = Modifier()
 *         .fadeIn(duration = 300, easing = Easing.EASE_OUT)
 * )
 *
 * // Spring animation with custom physics
 * Button(
 *     modifier = Modifier()
 *         .spring(
 *             name = "button-press",
 *             stiffness = 200f,
 *             damping = 15f,
 *             duration = 400
 *         )
 * )
 *
 * // Slide animation with direction
 * Column(
 *     modifier = Modifier()
 *         .slideIn(
 *             direction = SlideDirection.LEFT,
 *             distance = "100%",
 *             duration = 500
 *         )
 * )
 *
 * // Complex animation chaining
 * Card(
 *     modifier = Modifier()
 *         .scale(scaleX = 1.05f, scaleY = 1.05f, duration = 200)
 *         .fadeIn(duration = 300, delay = 100)
 *         .animTransition("all", 200, "ease-out")
 * )
 * ```
 *
 * ## Performance Considerations
 *
 * - **Hardware Acceleration**: Transform and opacity animations are optimized
 * - **CSS Layer Promotion**: Complex animations automatically promote elements
 * - **Memory Efficiency**: Animations clean up automatically after completion
 * - **Cancellation Support**: All animations can be interrupted and restarted
 *
 * ## Cross-Platform Behavior
 *
 * - **JavaScript**: Uses CSS animations and transitions directly
 * - **JVM**: Generates CSS that works with HTML rendering engines
 * - **Consistent API**: Same modifier functions work identically across platforms
 *
 * @see Modifier for the core modifier system
 * @see Easing for available timing functions
 * @see TransitionTimingFunction for transition control
 * @see Animation for core animation primitives
 * @since 1.0.0
 */
package code.yousef.summon.animation

import code.yousef.summon.core.splitCompat

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.TransitionTimingFunction
import code.yousef.summon.modifier.transition

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
    val springAnimation = SpringAnimation(stiffness, damping, duration)
    return animate(
        name = name,
        duration = duration,
        timingFunction = springAnimation.toCssAnimationString().splitCompat(" ")[1],
        iterationCount = iterationCount
    )
}

/**
 * Adds an animation with a specified easing function.
 *
 * @param name The name of the animation
 * @param easing The easing function to use
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @param iterationCount Number of times the animation should run, or "infinite"
 * @param direction Direction of the animation
 * @param fillMode Fill mode of the animation
 * @return A new Modifier with the animation styles added
 */
fun Modifier.animateWithEasing(
    name: String,
    easing: Easing = Easing.EASE_IN_OUT,
    duration: Int = 300,
    delay: Int = 0,
    iterationCount: String = "1",
    direction: String = "normal",
    fillMode: String = "forwards"
): Modifier {
    return animate(
        name = name,
        duration = duration,
        timingFunction = easing.toCssString(),
        delay = delay,
        iterationCount = iterationCount,
        direction = direction,
        fillMode = fillMode
    )
}

/**
 * Creates a bounce animation effect using the BOUNCE_OUT easing function.
 *
 * @param name The name of the animation
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @param iterationCount Number of times the animation should run, or "infinite"
 * @return A new Modifier with the bounce animation styles added
 */
fun Modifier.bounce(
    name: String = "bounce",
    duration: Int = 800,
    delay: Int = 0,
    iterationCount: String = "1"
): Modifier {
    return animateWithEasing(
        name = name,
        easing = Easing.BOUNCE_OUT,
        duration = duration,
        delay = delay,
        iterationCount = iterationCount
    )
}

/**
 * Creates an elastic animation effect using the ELASTIC_OUT easing function.
 *
 * @param name The name of the animation
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @param iterationCount Number of times the animation should run, or "infinite"
 * @return A new Modifier with the elastic animation styles added
 */
fun Modifier.elastic(
    name: String = "elastic",
    duration: Int = 1000,
    delay: Int = 0,
    iterationCount: String = "1"
): Modifier {
    return animateWithEasing(
        name = name,
        easing = Easing.ELASTIC_OUT,
        duration = duration,
        delay = delay,
        iterationCount = iterationCount
    )
}

/**
 * Creates a fade-in animation effect.
 *
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @param easing The easing function to use
 * @return A new Modifier with the fade-in animation styles added
 */
fun Modifier.fadeIn(
    duration: Int = 300,
    delay: Int = 0,
    easing: Easing = Easing.EASE_IN_OUT
): Modifier {
    return animateWithEasing(
        name = "fade-in",
        easing = easing,
        duration = duration,
        delay = delay
    )
}

/**
 * Creates a fade-out animation effect.
 *
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @param easing The easing function to use
 * @return A new Modifier with the fade-out animation styles added
 */
fun Modifier.fadeOut(
    duration: Int = 300,
    delay: Int = 0,
    easing: Easing = Easing.EASE_IN_OUT
): Modifier {
    return animateWithEasing(
        name = "fade-out",
        easing = easing,
        duration = duration,
        delay = delay
    )
}

/**
 * Creates a slide-in animation effect from the top.
 *
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @param easing The easing function to use
 * @return A new Modifier with the slide-in animation styles added
 */
fun Modifier.slideInFromTop(
    duration: Int = 500,
    delay: Int = 0,
    easing: Easing = Easing.CUBIC_OUT
): Modifier {
    return animateWithEasing(
        name = "slide-in-top",
        easing = easing,
        duration = duration,
        delay = delay
    )
}

/**
 * Creates a slide-in animation effect from the bottom.
 *
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @param easing The easing function to use
 * @return A new Modifier with the slide-in animation styles added
 */
fun Modifier.slideInFromBottom(
    duration: Int = 500,
    delay: Int = 0,
    easing: Easing = Easing.CUBIC_OUT
): Modifier {
    return animateWithEasing(
        name = "slide-in-bottom",
        easing = easing,
        duration = duration,
        delay = delay
    )
}

/**
 * Creates a zoom-in animation effect.
 *
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @param easing The easing function to use
 * @return A new Modifier with the zoom-in animation styles added
 */
fun Modifier.zoomIn(
    duration: Int = 400,
    delay: Int = 0,
    easing: Easing = Easing.CUBIC_OUT
): Modifier {
    return animateWithEasing(
        name = "zoom-in",
        easing = easing,
        duration = duration,
        delay = delay
    )
}

/**
 * Creates a zoom-out animation effect.
 *
 * @param duration Duration of the animation in milliseconds
 * @param delay Delay before the animation starts, in milliseconds
 * @param easing The easing function to use
 * @return A new Modifier with the zoom-out animation styles added
 */
fun Modifier.zoomOut(
    duration: Int = 400,
    delay: Int = 0,
    easing: Easing = Easing.CUBIC_IN
): Modifier {
    return animateWithEasing(
        name = "zoom-out",
        easing = easing,
        duration = duration,
        delay = delay
    )
}

/**
 * Creates a pulse animation effect that repeats infinitely.
 *
 * @param duration Duration of each pulse cycle in milliseconds
 * @param easing The easing function to use
 * @return A new Modifier with the pulsing animation styles added
 */
fun Modifier.pulse(
    duration: Int = 1500,
    easing: Easing = Easing.SINE_IN_OUT
): Modifier {
    return animateWithEasing(
        name = "pulse",
        easing = easing,
        duration = duration,
        iterationCount = "infinite"
    )
}

/**
 * Creates a shake animation effect.
 *
 * @param duration Duration of the animation in milliseconds
 * @param iterationCount Number of times the animation should run
 * @return A new Modifier with the shake animation styles added
 */
fun Modifier.shake(
    duration: Int = 500,
    iterationCount: String = "1"
): Modifier {
    return animate(
        name = "shake",
        duration = duration,
        timingFunction = "ease-in-out",
        iterationCount = iterationCount
    )
}

/**
 * Creates a float animation effect that moves the element up and down slightly.
 *
 * @param duration Duration of each float cycle in milliseconds
 * @param iterationCount Number of times the animation should run, or "infinite"
 * @param easing The easing function to use
 * @return A new Modifier with the floating animation styles added
 */
fun Modifier.float(
    duration: Int = 3000,
    iterationCount: String = "infinite",
    easing: Easing = Easing.SINE_IN_OUT
): Modifier {
    return animateWithEasing(
        name = "float",
        easing = easing,
        duration = duration,
        iterationCount = iterationCount,
        direction = "alternate"
    )
}

/**
 * Creates a typing cursor animation effect for text inputs.
 *
 * @param duration Duration of each blink cycle in milliseconds
 * @return A new Modifier with the cursor animation styles added
 */
fun Modifier.typingCursor(
    duration: Int = 800
): Modifier {
    return animate(
        name = "blink",
        duration = duration,
        timingFunction = "steps(1)",
        iterationCount = "infinite"
    )
}

/**
 * Creates a flip animation effect on the X axis.
 *
 * @param duration Duration of the animation in milliseconds
 * @param easing The easing function to use
 * @return A new Modifier with the flip animation styles added
 */
fun Modifier.flipX(
    duration: Int = 600,
    easing: Easing = Easing.CUBIC_IN_OUT
): Modifier {
    return animateWithEasing(
        name = "flip-x",
        easing = easing,
        duration = duration
    )
}

/**
 * Creates a flip animation effect on the Y axis.
 *
 * @param duration Duration of the animation in milliseconds
 * @param easing The easing function to use
 * @return A new Modifier with the flip animation styles added
 */
fun Modifier.flipY(
    duration: Int = 600,
    easing: Easing = Easing.CUBIC_IN_OUT
): Modifier {
    return animateWithEasing(
        name = "flip-y",
        easing = easing,
        duration = duration
    )
}

/**
 * Adds a CSS transition to an element with customizable parameters.
 * Named animTransition to avoid conflicts with the simpler transition() function.
 *
 * @param property CSS property to transition (or "all" for all properties)
 * @param duration Duration of the transition in milliseconds
 * @param timingFunction CSS timing function (e.g., "ease", "linear", etc.)
 * @param delay Delay before the transition starts, in milliseconds
 * @return A new Modifier with the transition styles added
 */
fun Modifier.animTransition(
    property: String = "all",
    duration: Int = 300,
    timingFunction: String = "ease",
    delay: Int = 0
): Modifier {
    val transition = "$property ${duration}ms $timingFunction ${delay}ms"
    return Modifier(this.styles + ("transition" to transition))
}

/**
 * Sets the opacity of an element.
 *
 * @param value Opacity value (0.0 to 1.0)
 * @return A new Modifier with the opacity style added
 */
fun Modifier.animOpacity(value: Float): Modifier {
    // Ensure decimal point is always included, even for whole numbers
    val formattedValue = if (value == value.toInt().toFloat()) {
        "${value.toInt()}.0"
    } else {
        value.toString()
    }
    return Modifier(this.styles + ("opacity" to formattedValue))
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
        .transition(property, duration, TransitionTimingFunction.EaseOut, delay)
        .transition("opacity", duration, TransitionTimingFunction.EaseOut, delay)
        .animOpacity(1f)
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
        .transition(property, duration, TransitionTimingFunction.EaseIn, delay)
        .transition("opacity", duration, TransitionTimingFunction.EaseIn, delay)
        .animOpacity(0f)
        .animStyle(property, value)
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
        .transition("transform", duration, TransitionTimingFunction.Ease, delay)
        .animStyle("transform", transform)
}

/**
 * Adds a custom style property to a Modifier.
 *
 * @param property The CSS property name
 * @param value The CSS property value
 * @return A new Modifier with the style added
 */
fun Modifier.animStyle(property: String, value: String): Modifier {
    return Modifier(this.styles + (property to value))
} 
