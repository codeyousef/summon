package codes.yousef.summon.animation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.style.Color
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.MutableStateImpl
import codes.yousef.summon.state.State
import codes.yousef.summon.state.mutableStateOf

/**
 * Creates and remembers an InfiniteTransition instance that can be used for infinite animations.
 */
@Composable
fun rememberInfiniteTransition(): InfiniteTransition {
    return remember { InfiniteTransition() }
}

/**
 * Interface for converting between a domain type and an animatable value type.
 */
interface TwoWayConverter<T, V> {
    /**
     * Convert from a domain type to an animatable vector value.
     */
    fun convertToVector(value: T): V

    /**
     * Convert from an animatable vector value to a domain type.
     */
    fun convertFromVector(value: V): T
}

/**
 * A converter for Float values.
 */
object FloatConverter : TwoWayConverter<Float, Float> {
    override fun convertToVector(value: Float): Float = value
    override fun convertFromVector(value: Float): Float = value
}

/**
 * A converter for Int values.
 */
object IntConverter : TwoWayConverter<Int, Float> {
    override fun convertToVector(value: Int): Float = value.toFloat()
    override fun convertFromVector(value: Float): Int = value.toInt()
}

/**
 * A converter for Color values.
 */
object ColorConverter : TwoWayConverter<Color, Float> {
    override fun convertToVector(value: Color): Float = value.alpha.toFloat()
    override fun convertFromVector(value: Float): Color = Color(0u).withAlpha(value)
}

/**
 * Specification for a transition animation.
 */
class TransitionSpec(
    val animation: Animation = TweenAnimation()
)

/**
 * Manages a transition between values of type T.
 *
 * @param initialState The initial state value
 * @param spec The transition specification
 */
class Transition<T>(
    initialState: T,
    private val spec: TransitionSpec
) {
    private val _state = mutableStateOf(initialState)
    val state: State<T> get() = _state

    /**
     * Updates the current state with a new value.
     */
    fun updateState(newState: T) {
        _state.value = newState
    }

    /**
     * Creates an animated float value from a start to an end value.
     */
    @Composable
    fun <V> animateValue(
        initialValue: V,
        targetValue: V,
        converter: TwoWayConverter<V, Float>,
        animation: Animation = spec.animation
    ): State<V> {
        val initialFloat = converter.convertToVector(initialValue)
        val targetFloat = converter.convertToVector(targetValue)

        // Create a state to hold the animated value
        val animatedValue = remember { mutableStateOf(initialValue) }

        // Get the current progress of the animation (0.0 to 1.0)
        val progress = remember { mutableStateOf(0f) }

        // Start the animation when targetValue changes
        val key = targetValue.hashCode()

        // Use LaunchedEffect to handle the animation
        codes.yousef.summon.runtime.LaunchedEffect(key) {
            // Reset progress
            progress.value = 0f

            // Get the animation duration
            val duration = animation.durationMs

            // Use the AnimationController to track animation progress
            AnimationController.startAnimation(duration)

            // Update the animated value based on progress
            while (progress.value < 1.0f) {
                // Get current progress from AnimationController
                progress.value = AnimationController.progress

                // Calculate the current value based on progress
                val currentFloat = initialFloat + (targetFloat - initialFloat) * progress.value
                animatedValue.value = converter.convertFromVector(currentFloat)

                // Check if animation is complete
                if (progress.value >= 1.0f || AnimationController.status == AnimationStatus.STOPPED) {
                    break
                }

                // Small delay to avoid excessive updates
                codes.yousef.summon.animation.delay(16) // ~60fps
            }

            // Ensure final value is set
            animatedValue.value = targetValue
        }

        return animatedValue
    }

    /**
     * Creates an animated float value.
     */
    @Composable
    fun animateFloat(
        initialValue: Float,
        targetValue: Float,
        animation: Animation = spec.animation
    ): State<Float> {
        return animateValue(initialValue, targetValue, FloatConverter, animation)
    }

    /**
     * Creates an animated int value.
     */
    @Composable
    fun animateInt(
        initialValue: Int,
        targetValue: Int,
        animation: Animation = spec.animation
    ): State<Int> {
        return animateValue(initialValue, targetValue, IntConverter, animation)
    }

    /**
     * Creates an animated color value.
     */
    @Composable
    fun animateColor(
        initialValue: Color,
        targetValue: Color,
        animation: Animation = spec.animation
    ): State<Color> {
        return animateValue(initialValue, targetValue, ColorConverter, animation)
    }
}

/**
 * A class that manages infinite animations.
 */
class InfiniteTransition {
    private var isRunning = true

    /**
     * Pause the infinite animation.
     */
    fun pause() {
        isRunning = false
    }

    /**
     * Resume the infinite animation.
     */
    fun resume() {
        isRunning = true
    }

    /**
     * Creates an animated value that animates infinitely between initialValue and targetValue.
     */
    @Composable
    fun <T> animateValue(
        initialValue: T,
        targetValue: T,
        converter: TwoWayConverter<T, Float>,
        animation: Animation = TweenAnimation()
    ): State<T> {
        // Create a state to hold the animated value
        val animatedValue = remember { mutableStateOf(initialValue) }

        // Get the initial and target values as floats
        val initialFloat = converter.convertToVector(initialValue)
        val targetFloat = converter.convertToVector(targetValue)

        // Create a progress state to track animation progress
        val progress = remember { mutableStateOf(0f) }
        val cycleCount = remember { mutableStateOf(0) }

        // Use LaunchedEffect to handle the infinite animation
        codes.yousef.summon.runtime.LaunchedEffect(Unit) {
            // Start the animation
            val duration = animation.durationMs

            // Use the AnimationController to track animation progress
            AnimationController.startAnimation(duration)

            // Continuously update the animated value
            while (true) {
                // For infinite animations, we use a sine wave to oscillate between 0 and 1
                // We'll use the AnimationController's progress and reset it when it completes

                if (AnimationController.status == AnimationStatus.STOPPED) {
                    // Animation completed, restart it and increment cycle count
                    cycleCount.value++
                    AnimationController.startAnimation(duration)
                }

                // Get current progress and adjust based on cycle count (even/odd)
                val rawProgress = AnimationController.progress
                progress.value = if (cycleCount.value % 2 == 0) {
                    rawProgress // Forward animation
                } else {
                    1f - rawProgress // Reverse animation
                }

                // Apply easing function if available
                val easedProgress = if (animation is TweenAnimation) {
                    animation.easing.getValue(progress.value)
                } else {
                    progress.value
                }

                // Calculate the current value based on progress
                val currentFloat = initialFloat + (targetFloat - initialFloat) * easedProgress
                animatedValue.value = converter.convertFromVector(currentFloat)

                // Small delay to avoid excessive updates
                codes.yousef.summon.animation.delay(16) // ~60fps
            }
        }

        return animatedValue
    }

    /**
     * Creates an animated float value that animates infinitely.
     */
    @Composable
    fun animateFloat(
        initialValue: Float,
        targetValue: Float,
        animation: Animation = TweenAnimation()
    ): State<Float> {
        return animateValue(initialValue, targetValue, FloatConverter, animation)
    }

    /**
     * Creates an animated int value that animates infinitely.
     */
    @Composable
    fun animateInt(
        initialValue: Int,
        targetValue: Int,
        animation: Animation = TweenAnimation()
    ): State<Int> {
        return animateValue(initialValue, targetValue, IntConverter, animation)
    }

    /**
     * Creates an animated color value that animates infinitely.
     */
    @Composable
    fun animateColor(
        initialValue: Color,
        targetValue: Color,
        animation: Animation = TweenAnimation()
    ): State<Color> {
        return animateValue(initialValue, targetValue, ColorConverter, animation)
    }
}

/**
 * A composable that provides animated transition between states.
 * This is a higher-order component that wraps content and animates it when a state changes.
 *
 * @param stateValue The state to monitor for changes
 * @param animation The animation to use for transitions
 * @param modifier The modifier to apply to this composable
 * @param content Function that produces content with animated properties
 */
@Composable
fun <T> TransitionComponent(
    stateValue: State<T>,
    animation: Animation = TweenAnimation(),
    modifier: Modifier = Modifier(),
    content: @Composable (Transition<T>) -> Unit
) {
    val transition = remember { Transition(stateValue.value, TransitionSpec(animation)) }
    val renderer = LocalPlatformRenderer.current

    // Update transition when state changes
    if (stateValue is MutableStateImpl) {
        stateValue.addListener { newValue ->
            transition.updateState(newValue as T)
        }
    }

    // Render the content with the transition
    renderer.renderBox(modifier) {
        content(transition)
    }
}

/**
 * A composable that provides continuous animations with an infinite transition.
 * This is useful for looping animations, loading indicators, and background effects.
 *
 * @param running Whether the animation is currently running
 * @param modifier The modifier to apply to this composable
 * @param content Function that produces content with animated properties
 */
@Composable
fun InfiniteTransitionEffect(
    running: Boolean = true,
    modifier: Modifier = Modifier(),
    content: @Composable (InfiniteTransition) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val renderer = LocalPlatformRenderer.current

    if (!running) {
        infiniteTransition.pause()
    } else {
        infiniteTransition.resume()
    }

    // Render the content with the infinite transition
    renderer.renderBox(modifier) {
        content(infiniteTransition)
    }
}

/**
 * Creates a transition component that tracks state changes and animates.
 *
 * @param stateValue The state to monitor for changes
 * @param animation The animation to use for transitions
 * @param modifier The modifier to apply to this layout
 * @param content Function that produces content with animated properties
 */
@Composable
fun <T> transition(
    stateValue: State<T>,
    animation: Animation = TweenAnimation(),
    modifier: Modifier = Modifier(),
    content: @Composable (Transition<T>) -> Unit
) {
    TransitionComponent(
        stateValue = stateValue,
        animation = animation,
        modifier = modifier,
        content = content
    )
}

/**
 * Creates an infinite transition component for continuous animations.
 *
 * @param running Whether the animation is running
 * @param modifier The modifier to apply to this layout
 * @param content Function that produces content with animated properties
 */
@Composable
fun infiniteTransition(
    running: Boolean = true,
    modifier: Modifier = Modifier(),
    content: @Composable (InfiniteTransition) -> Unit
) {
    InfiniteTransitionEffect(
        running = running,
        modifier = modifier,
        content = content
    )
}

/**
 * Extension function to create an animated float value in an infinite transition.
 *
 * @param initialValue The initial value
 * @param targetValue The target value to animate to
 * @param animation The animation to use
 */
fun InfiniteTransition.animateFloatExt(
    initialValue: Float,
    targetValue: Float,
    animation: Animation = TweenAnimation(durationMs = 1000, repeating = true)
): State<Float> {
    return animateValue(initialValue, targetValue, FloatConverter, animation)
}

/**
 * Extension function to create an animated int value in an infinite transition.
 *
 * @param initialValue The initial value
 * @param targetValue The target value to animate to
 * @param animation The animation to use
 */
fun InfiniteTransition.animateIntExt(
    initialValue: Int,
    targetValue: Int,
    animation: Animation = TweenAnimation(durationMs = 1000, repeating = true)
): State<Int> {
    return animateValue(initialValue, targetValue, IntConverter, animation)
} 
