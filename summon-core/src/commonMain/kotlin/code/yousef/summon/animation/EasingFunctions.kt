package code.yousef.summon.animation

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Advanced easing functions collection that provides a comprehensive set of
 * easing functions for animations. These implementation follows standard easing
 * functions used in animation libraries.
 */
object EasingFunctions {
    // Define PI as Float constant to avoid conversion issues
    private const val PI_FLOAT = 3.1415927f

    // Linear - no easing, no acceleration
    fun linear(t: Float): Float = t

    // Sine easing functions
    fun easeInSine(t: Float): Float = 1 - cos((t * PI_FLOAT) / 2)
    fun easeOutSine(t: Float): Float = sin((t * PI_FLOAT) / 2)
    fun easeInOutSine(t: Float): Float = -(cos(PI_FLOAT * t) - 1) / 2

    // Quadratic easing functions
    fun easeInQuad(t: Float): Float = t * t
    fun easeOutQuad(t: Float): Float = 1 - (1 - t) * (1 - t)
    fun easeInOutQuad(t: Float): Float = if (t < 0.5f) 2 * t * t else 1 - (-2 * t + 2).pow(2) / 2

    // Cubic easing functions
    fun easeInCubic(t: Float): Float = t * t * t
    fun easeOutCubic(t: Float): Float = 1 - (1 - t).pow(3)
    fun easeInOutCubic(t: Float): Float = if (t < 0.5f) 4 * t * t * t else 1 - (-2 * t + 2).pow(3) / 2

    // Quartic easing functions 
    fun easeInQuart(t: Float): Float = t * t * t * t
    fun easeOutQuart(t: Float): Float = 1 - (1 - t).pow(4)
    fun easeInOutQuart(t: Float): Float = if (t < 0.5f) 8 * t * t * t * t else 1 - (-2 * t + 2).pow(4) / 2

    // Quintic easing functions
    fun easeInQuint(t: Float): Float = t * t * t * t * t
    fun easeOutQuint(t: Float): Float = 1 - (1 - t).pow(5)
    fun easeInOutQuint(t: Float): Float = if (t < 0.5f) 16 * t * t * t * t * t else 1 - (-2 * t + 2).pow(5) / 2

    // Exponential easing functions
    fun easeInExpo(t: Float): Float = if (t == 0f) 0f else 2f.pow(10 * t - 10)
    fun easeOutExpo(t: Float): Float = if (t == 1f) 1f else 1 - 2f.pow(-10 * t)
    fun easeInOutExpo(t: Float): Float = when {
        t == 0f -> 0f
        t == 1f -> 1f
        t < 0.5f -> 2f.pow(20 * t - 10) / 2
        else -> (2 - 2f.pow(-20 * t + 10)) / 2
    }

    // Circular easing functions
    fun easeInCirc(t: Float): Float = 1 - sqrt(1 - t.pow(2))
    fun easeOutCirc(t: Float): Float = sqrt(1 - (t - 1).pow(2))
    fun easeInOutCirc(t: Float): Float = if (t < 0.5f)
        (1 - sqrt(1 - (2 * t).pow(2))) / 2
    else
        (sqrt(1 - (-2 * t + 2).pow(2)) + 1) / 2

    // Back easing functions with overshoot
    fun easeInBack(t: Float): Float {
        val c1 = 1.70158f
        val c3 = c1 + 1
        return c3 * t * t * t - c1 * t * t
    }

    fun easeOutBack(t: Float): Float {
        val c1 = 1.70158f
        val c3 = c1 + 1
        return 1 + c3 * (t - 1).pow(3) + c1 * (t - 1).pow(2)
    }

    fun easeInOutBack(t: Float): Float {
        val c1 = 1.70158f
        val c2 = c1 * 1.525f

        return if (t < 0.5f) {
            ((2 * t).pow(2) * ((c2 + 1) * 2 * t - c2)) / 2
        } else {
            ((2 * t - 2).pow(2) * ((c2 + 1) * (t * 2 - 2) + c2) + 2) / 2
        }
    }

    // Elastic easing functions
    fun easeInElastic(t: Float): Float {
        val c4 = (2 * PI_FLOAT) / 3

        return if (t == 0f) {
            0f
        } else if (t == 1f) {
            1f
        } else {
            -2f.pow(10 * t - 10) * sin((t * 10 - 10.75f) * c4)
        }
    }

    fun easeOutElastic(t: Float): Float {
        val c4 = (2 * PI_FLOAT) / 3

        return if (t == 0f) {
            0f
        } else if (t == 1f) {
            1f
        } else {
            2f.pow(-10 * t) * sin((t * 10 - 0.75f) * c4) + 1
        }
    }

    fun easeInOutElastic(t: Float): Float {
        val c5 = (2 * PI_FLOAT) / 4.5f

        return when {
            t == 0f -> 0f
            t == 1f -> 1f
            t < 0.5f -> -(2f.pow(20 * t - 10) * sin((20 * t - 11.125f) * c5)) / 2
            else -> (2f.pow(-20 * t + 10) * sin((20 * t - 11.125f) * c5)) / 2 + 1
        }
    }

    // Bounce easing functions
    fun easeInBounce(t: Float): Float = 1 - easeOutBounce(1 - t)

    fun easeOutBounce(t: Float): Float {
        val n1 = 7.5625f
        val d1 = 2.75f

        return when {
            t < 1 / d1 -> n1 * t * t
            t < 2 / d1 -> n1 * (t - 1.5f / d1) * (t - 1.5f / d1) + 0.75f
            t < 2.5 / d1 -> n1 * (t - 2.25f / d1) * (t - 2.25f / d1) + 0.9375f
            else -> n1 * (t - 2.625f / d1) * (t - 2.625f / d1) + 0.984375f
        }
    }

    fun easeInOutBounce(t: Float): Float = if (t < 0.5f) {
        (1 - easeOutBounce(1 - 2 * t)) / 2
    } else {
        (1 + easeOutBounce(2 * t - 1)) / 2
    }

    /**
     * Converts an easing function to its CSS cubic-bezier equivalent representation.
     * This is an approximation as not all easing functions can be perfectly represented by cubic-bezier.
     */
    fun toCssCubicBezier(easingFunction: (Float) -> Float): String {
        // Sample points to approximate cubic bezier
        val p1x = 0.25f
        val p1y = easingFunction(p1x)
        val p2x = 0.75f
        val p2y = easingFunction(p2x)

        return "cubic-bezier($p1x, $p1y, $p2x, $p2y)"
    }
} 
