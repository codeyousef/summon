package code.yousef.summon.animation

/**
 * Represents the status of an animation.
 */
expect enum class AnimationStatus {
    IDLE,    // Animation has not started or has been reset.
    RUNNING, // Animation is currently playing.
    PAUSED,  // Animation is paused.
    STOPPED  // Animation has finished or been explicitly stopped.
}

/**
 * Controller for managing animations started by the platform renderer.
 * Allows pausing, resuming, stopping, and querying the state of an animation.
 */
expect object AnimationController { // Using object for simplicity, could be interface/class if needed
    /** Pauses the running animation. */
    fun pause()

    /** Resumes a paused animation. */
    fun resume()

    /** Cancels the animation, potentially resetting state. */
    fun cancel()

    /** Stops the animation, holding its current state. */
    fun stop()

    /** Gets the current status of the animation. */
    val status: AnimationStatus

    /** Gets the current progress of the animation (typically 0.0 to 1.0). */
    val progress: Float
}

/**
 * Base interface for animations in the Summon framework.
 */
interface Animation {
    /**
     * Get the current animation value.
     *
     * @param fraction The animation progress between 0.0 and 1.0
     * @return The animated value at the given progress
     */
    fun getValue(fraction: Float): Float

    /**
     * The total duration of the animation in milliseconds.
     */
    val durationMs: Int

    /**
     * Whether the animation should repeat.
     */
    val repeating: Boolean

    /**
     * Get the CSS animation string representation
     */
    fun toCssAnimationString(): String

    /**
     * Get the CSS keyframes definition
     */
    fun toCssKeyframes(keyframesName: String): String
}

/**
 * A physics-based spring animation.
 *
 * @param stiffness The stiffness of the spring (higher = more rigid)
 * @param damping The amount of damping (higher = less oscillation)
 * @param durationMs The total animation duration in milliseconds
 * @param repeating Whether the animation should repeat
 */
class SpringAnimation(
    val stiffness: Float = 150f,
    val damping: Float = 12f,
    override val durationMs: Int = 300,
    override val repeating: Boolean = false
) : Animation {
    override fun getValue(fraction: Float): Float {
        // Simple approximation of a spring animation
        return 1f - (1f - fraction) * kotlin.math.exp(-damping * fraction)
    }

    override fun toCssAnimationString(): String {
        val timingFunction = "cubic-bezier(0.5, 0.0, 0.2, 1.0)" // Spring-like curve
        return "${durationMs}ms $timingFunction"
    }

    override fun toCssKeyframes(keyframesName: String): String {
        val steps = 20
        val keyframes = StringBuilder("@keyframes $keyframesName {\n")

        for (i in 0..steps) {
            val progress = i.toFloat() / steps
            val value = getValue(progress)
            keyframes.append("  ${(progress * 100).toInt()}% { transform: scale($value); }\n")
        }

        keyframes.append("}")
        return keyframes.toString()
    }
}

/**
 * Animation that changes at a constant rate, with optional easing.
 *
 * @param durationMs The animation duration in milliseconds
 * @param easing The easing function to apply
 * @param repeating Whether the animation should repeat
 */
class TweenAnimation(
    override val durationMs: Int = 300,
    val easing: Easing = Easing.LINEAR,
    override val repeating: Boolean = false
) : Animation {
    override fun getValue(fraction: Float): Float {
        // Apply the easing function
        return easing.getValue(fraction)
    }

    override fun toCssAnimationString(): String {
        return "${durationMs}ms ${easing.toCssString()}"
    }

    override fun toCssKeyframes(keyframesName: String): String {
        return """
            @keyframes $keyframesName {
              0% { transform: scale(0); opacity: 0; }
              100% { transform: scale(1); opacity: 1; }
            }
        """.trimIndent()
    }
}

/**
 * Enhanced easing functions for animations.
 * This enum provides the standard easing functions as well as integration
 * with the more comprehensive EasingFunctions object.
 */
enum class Easing {
    LINEAR,      // Constant rate of change
    EASE_IN,     // Start slow, end fast
    EASE_OUT,    // Start fast, end slow
    EASE_IN_OUT, // Start slow, middle fast, end slow

    // Advanced Easing Types
    SINE_IN,
    SINE_OUT,
    SINE_IN_OUT,
    QUAD_IN,
    QUAD_OUT,
    QUAD_IN_OUT,
    CUBIC_IN,
    CUBIC_OUT,
    CUBIC_IN_OUT,
    QUART_IN,
    QUART_OUT,
    QUART_IN_OUT,
    QUINT_IN,
    QUINT_OUT,
    QUINT_IN_OUT,
    EXPO_IN,
    EXPO_OUT,
    EXPO_IN_OUT,
    CIRC_IN,
    CIRC_OUT,
    CIRC_IN_OUT,
    BACK_IN,
    BACK_OUT,
    BACK_IN_OUT,
    ELASTIC_IN,
    ELASTIC_OUT,
    ELASTIC_IN_OUT,
    BOUNCE_IN,
    BOUNCE_OUT,
    BOUNCE_IN_OUT;

    /**
     * Get the easing function value at the specified fraction.
     */
    fun getValue(fraction: Float): Float {
        return when (this) {
            LINEAR -> EasingFunctions.linear(fraction)
            EASE_IN -> EasingFunctions.easeInQuad(fraction)
            EASE_OUT -> EasingFunctions.easeOutQuad(fraction)
            EASE_IN_OUT -> EasingFunctions.easeInOutQuad(fraction)
            SINE_IN -> EasingFunctions.easeInSine(fraction)
            SINE_OUT -> EasingFunctions.easeOutSine(fraction)
            SINE_IN_OUT -> EasingFunctions.easeInOutSine(fraction)
            QUAD_IN -> EasingFunctions.easeInQuad(fraction)
            QUAD_OUT -> EasingFunctions.easeOutQuad(fraction)
            QUAD_IN_OUT -> EasingFunctions.easeInOutQuad(fraction)
            CUBIC_IN -> EasingFunctions.easeInCubic(fraction)
            CUBIC_OUT -> EasingFunctions.easeOutCubic(fraction)
            CUBIC_IN_OUT -> EasingFunctions.easeInOutCubic(fraction)
            QUART_IN -> EasingFunctions.easeInQuart(fraction)
            QUART_OUT -> EasingFunctions.easeOutQuart(fraction)
            QUART_IN_OUT -> EasingFunctions.easeInOutQuart(fraction)
            QUINT_IN -> EasingFunctions.easeInQuint(fraction)
            QUINT_OUT -> EasingFunctions.easeOutQuint(fraction)
            QUINT_IN_OUT -> EasingFunctions.easeInOutQuint(fraction)
            EXPO_IN -> EasingFunctions.easeInExpo(fraction)
            EXPO_OUT -> EasingFunctions.easeOutExpo(fraction)
            EXPO_IN_OUT -> EasingFunctions.easeInOutExpo(fraction)
            CIRC_IN -> EasingFunctions.easeInCirc(fraction)
            CIRC_OUT -> EasingFunctions.easeOutCirc(fraction)
            CIRC_IN_OUT -> EasingFunctions.easeInOutCirc(fraction)
            BACK_IN -> EasingFunctions.easeInBack(fraction)
            BACK_OUT -> EasingFunctions.easeOutBack(fraction)
            BACK_IN_OUT -> EasingFunctions.easeInOutBack(fraction)
            ELASTIC_IN -> EasingFunctions.easeInElastic(fraction)
            ELASTIC_OUT -> EasingFunctions.easeOutElastic(fraction)
            ELASTIC_IN_OUT -> EasingFunctions.easeInOutElastic(fraction)
            BOUNCE_IN -> EasingFunctions.easeInBounce(fraction)
            BOUNCE_OUT -> EasingFunctions.easeOutBounce(fraction)
            BOUNCE_IN_OUT -> EasingFunctions.easeInOutBounce(fraction)
        }
    }

    /**
     * Convert to CSS timing function string.
     * For advanced easing functions, we convert them to approximate cubic-bezier values.
     */
    fun toCssString(): String {
        return when (this) {
            LINEAR -> "linear"
            EASE_IN -> "cubic-bezier(0.4, 0, 1, 1)"
            EASE_OUT -> "cubic-bezier(0, 0, 0.2, 1)"
            EASE_IN_OUT -> "cubic-bezier(0.4, 0, 0.2, 1)"
            SINE_IN -> "cubic-bezier(0.12, 0, 0.39, 0)"
            SINE_OUT -> "cubic-bezier(0.61, 1, 0.88, 1)"
            SINE_IN_OUT -> "cubic-bezier(0.37, 0, 0.63, 1)"
            QUAD_IN -> "cubic-bezier(0.11, 0, 0.5, 0)"
            QUAD_OUT -> "cubic-bezier(0.5, 1, 0.89, 1)"
            QUAD_IN_OUT -> "cubic-bezier(0.45, 0, 0.55, 1)"
            CUBIC_IN -> "cubic-bezier(0.32, 0, 0.67, 0)"
            CUBIC_OUT -> "cubic-bezier(0.33, 1, 0.68, 1)"
            CUBIC_IN_OUT -> "cubic-bezier(0.65, 0, 0.35, 1)"
            QUART_IN -> "cubic-bezier(0.5, 0, 0.75, 0)"
            QUART_OUT -> "cubic-bezier(0.25, 1, 0.5, 1)"
            QUART_IN_OUT -> "cubic-bezier(0.76, 0, 0.24, 1)"
            QUINT_IN -> "cubic-bezier(0.64, 0, 0.78, 0)"
            QUINT_OUT -> "cubic-bezier(0.22, 1, 0.36, 1)"
            QUINT_IN_OUT -> "cubic-bezier(0.83, 0, 0.17, 1)"
            EXPO_IN -> "cubic-bezier(0.7, 0, 0.84, 0)"
            EXPO_OUT -> "cubic-bezier(0.16, 1, 0.3, 1)"
            EXPO_IN_OUT -> "cubic-bezier(0.87, 0, 0.13, 1)"
            CIRC_IN -> "cubic-bezier(0.55, 0, 1, 0.45)"
            CIRC_OUT -> "cubic-bezier(0, 0.55, 0.45, 1)"
            CIRC_IN_OUT -> "cubic-bezier(0.85, 0, 0.15, 1)"
            BACK_IN -> "cubic-bezier(0.36, 0, 0.66, -0.56)"
            BACK_OUT -> "cubic-bezier(0.34, 1.56, 0.64, 1)"
            BACK_IN_OUT -> "cubic-bezier(0.68, -0.6, 0.32, 1.6)"
            ELASTIC_IN -> "cubic-bezier(0.7, 0, 0.3, 1)"
            ELASTIC_OUT -> "cubic-bezier(0.3, 1, 0.7, 1)"
            ELASTIC_IN_OUT -> "cubic-bezier(0.9, 0.1, 0.1, 0.9)"
            BOUNCE_IN -> "cubic-bezier(0.5, 0, 1, 1)"
            BOUNCE_OUT -> "cubic-bezier(0, 0, 0.5, 1)"
            BOUNCE_IN_OUT -> "cubic-bezier(0.5, 0, 0.5, 1)"
        }
    }
}
