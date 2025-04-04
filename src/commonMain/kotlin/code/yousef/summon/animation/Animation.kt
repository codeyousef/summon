package code.yousef.summon.animation

import code.yousef.summon.State
import code.yousef.summon.mutableStateOf
import kotlin.math.pow

/**
 * Base interface for all animation types
 */
interface Animation {
    /**
     * Get the current interpolated value at the specified animation progress (0.0 to 1.0)
     */
    fun getValueAtProgress(progress: Float): Float

    /**
     * The total duration of the animation in milliseconds
     */
    val durationMs: Int

    /**
     * Whether the animation should automatically repeat
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
 * Spring animation that follows physics-based motion
 *
 * @param stiffness The spring stiffness coefficient
 * @param damping The spring damping coefficient
 * @param durationMs The approximate animation duration in milliseconds
 * @param repeating Whether the animation should repeat
 */
class SpringAnimation(
    val stiffness: Float = 170f,
    val damping: Float = 26f,
    override val durationMs: Int = 400,
    override val repeating: Boolean = false
) : Animation {

    override fun getValueAtProgress(progress: Float): Float {
        // Simple damped spring approximation
        val decay = (-damping * progress).coerceIn(-30f, 0f)
        val response = 1f - exp(decay) * cos(stiffness * progress)
        return response.coerceIn(0f, 1f)
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
            val value = getValueAtProgress(progress)
            keyframes.append("  ${(progress * 100).toInt()}% { transform: scale($value); }\n")
        }

        keyframes.append("}")
        return keyframes.toString()
    }

    companion object {
        private fun exp(x: Float): Float = kotlin.math.exp(x.toDouble()).toFloat()
        private fun cos(x: Float): Float = kotlin.math.cos(x.toDouble()).toFloat()
    }
}

/**
 * Tween animation that follows a specified easing curve
 *
 * @param durationMs The animation duration in milliseconds
 * @param easing The easing function to use
 * @param repeating Whether the animation should repeat
 */
class TweenAnimation(
    override val durationMs: Int = 300,
    val easing: Easing = Easing.EASE_IN_OUT,
    override val repeating: Boolean = false
) : Animation {

    override fun getValueAtProgress(progress: Float): Float {
        return easing.apply(progress)
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
 * Common easing functions for animations
 */
enum class Easing {
    LINEAR,
    EASE_IN,
    EASE_OUT,
    EASE_IN_OUT;

    /**
     * Apply the easing function to a progress value (0.0 to 1.0)
     */
    fun apply(progress: Float): Float {
        return when (this) {
            LINEAR -> progress
            EASE_IN -> progress * progress
            EASE_OUT -> 1 - (1 - progress).pow(2)
            EASE_IN_OUT -> {
                when {
                    progress < 0.5f -> 2 * progress * progress
                    else -> 1 - (-2 * progress + 2).pow(2) / 2
                }
            }
        }
    }

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

/**
 * Animation controller that manages animation states
 */
class AnimationController(
    val animation: Animation,
    initialValue: Float = 0f
) {
    val progress = mutableStateOf(initialValue)
    val isRunning = mutableStateOf(false)
    val targetValue = mutableStateOf(initialValue)

    /**
     * Start the animation to the target value
     */
    fun animateTo(target: Float) {
        targetValue.value = target
        isRunning.value = true
        // Note: Actual animation is handled by the platform renderer
    }

    /**
     * Stop the animation at the current position
     */
    fun stop() {
        isRunning.value = false
    }
}

/**
 * Animation specification for transitions
 */
data class TransitionSpec(
    val animation: Animation = TweenAnimation(),
    val delay: Int = 0
)

/**
 * State transition with animation
 */
class Transition<T>(
    initialState: T,
    private val transitionSpec: TransitionSpec = TransitionSpec()
) {
    val state = mutableStateOf(initialState)
    val progress = mutableStateOf(1f)
    private val previousState = mutableStateOf(initialState)
    private val controller = AnimationController(transitionSpec.animation, 1f)

    /**
     * Update the state with animation
     */
    fun updateState(newState: T) {
        if (newState != state.value) {
            previousState.value = state.value
            state.value = newState
            progress.value = 0f
            controller.animateTo(1f)
        }
    }

    /**
     * Get the animation key for the current transition
     */
    fun getAnimationKey(): String {
        return "transition-${state.value.hashCode()}-${previousState.value.hashCode()}"
    }
}

/**
 * Creates an infinite transition for continuous animations
 */
class InfiniteTransition {
    private val animations = mutableListOf<AnimationController>()
    private val isActive = mutableStateOf(true)

    /**
     * Creates an animated value that continuously animates between specified values
     */
    fun <T> animateValue(
        initialValue: T,
        targetValue: T,
        typeConverter: TypeConverter<T>,
        animation: Animation
    ): State<T> {
        val controller = AnimationController(animation, 0f)
        controller.isRunning.value = isActive.value
        animations.add(controller)

        return object : State<T> {
            override val value: T
                get() = typeConverter.convert(controller.progress.value, initialValue, targetValue)
        }
    }

    /**
     * Pauses all animations in this infinite transition
     */
    fun pause() {
        isActive.value = false
        animations.forEach { it.stop() }
    }

    /**
     * Resumes all animations in this infinite transition
     */
    fun resume() {
        isActive.value = true
        animations.forEach { it.isRunning.value = true }
    }
}

/**
 * Interface for converting between different value types during animation
 */
interface TypeConverter<T> {
    fun convert(progress: Float, from: T, to: T): T
}

/**
 * Type converter for Float values
 */
object FloatConverter : TypeConverter<Float> {
    override fun convert(progress: Float, from: Float, to: Float): Float {
        return from + (to - from) * progress
    }
}

/**
 * Type converter for Int values
 */
object IntConverter : TypeConverter<Int> {
    override fun convert(progress: Float, from: Int, to: Int): Int {
        return (from + (to - from) * progress).toInt()
    }
} 