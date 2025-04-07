package code.yousef.summon.animation

import kotlin.math.pow

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
        return when (easing) {
            Easing.LINEAR -> fraction
            Easing.EASE_IN -> fraction * fraction
            Easing.EASE_OUT -> 1f - (1f - fraction) * (1f - fraction)
            Easing.EASE_IN_OUT -> {
                if (fraction < 0.5f) {
                    2f * fraction * fraction
                } else {
                    1f - (-2f * fraction + 2f) * (-2f * fraction + 2f) / 2f
                }
            }
        }
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
 * Common easing functions for animations.
 */
enum class Easing {
    LINEAR,      // Constant rate of change
    EASE_IN,     // Start slow, end fast
    EASE_OUT,    // Start fast, end slow
    EASE_IN_OUT;  // Start slow, middle fast, end slow

    /**
     * Convert to CSS timing function string
     */
    fun toCssString(): String {
        return when (this) {
            LINEAR -> "linear"
            EASE_IN -> "cubic-bezier(0.4, 0, 1, 1)"
            EASE_OUT -> "cubic-bezier(0, 0, 0.2, 1)"
            EASE_IN_OUT -> "cubic-bezier(0.4, 0, 0.2, 1)"
        }
    }
}
