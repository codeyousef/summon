package code.yousef.summon.animation

import code.yousef.summon.state.MutableStateImpl
import code.yousef.summon.state.State
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.style.Color
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.remember
import code.yousef.summon.runtime.getPlatformRenderer

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

        // In a real implementation, this would use the animation system
        // For simplicity, just return the target value
        animatedValue.value = targetValue

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

        // In a real implementation, this would use the animation system
        // For simplicity, just return the initial value
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
    val renderer = getPlatformRenderer()

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
    val renderer = getPlatformRenderer()

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
fun InfiniteTransition.animateFloat(
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
fun InfiniteTransition.animateInt(
    initialValue: Int,
    targetValue: Int,
    animation: Animation = TweenAnimation(durationMs = 1000, repeating = true)
): State<Int> {
    return animateValue(initialValue, targetValue, IntConverter, animation)
} 