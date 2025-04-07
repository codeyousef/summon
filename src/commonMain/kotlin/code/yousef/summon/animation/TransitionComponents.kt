package code.yousef.summon.animation

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.State
import code.yousef.summon.runtime.MutableState
import code.yousef.summon.core.style.Color

/**
 * A composable that provides animated transition between states.
 * This is a higher-order component that wraps content and animates it when a state changes.
 *
 * @param state The state to monitor for changes
 * @param animation The animation to use for transitions
 * @param modifier The modifier to apply to this composable
 * @param content Function that produces content with animated properties
 */
@Composable
fun <T> Transition(
    state: State<T>,
    animation: Animation = TweenAnimation(),
    modifier: Modifier = Modifier(),
    content: @Composable (Transition<T>) -> Unit
) {
    val composer = CompositionLocal.currentComposer
    
    composer?.startNode()
    
    val transition = createTransition(state.value, TransitionSpec(animation))
    
    // Render container
    if (composer?.inserting == true) {
        // Render container with modifier
    }
    
    // Execute content with transition state
    content(transition)
    
    composer?.endNode()
}

/**
 * Animation specification for transitions.
 */
class TransitionSpec(val animation: Animation = TweenAnimation())

/**
 * Creates a transition object for animation.
 */
private fun <T> createTransition(initialState: T, spec: TransitionSpec): Transition<T> {
    return Transition(initialState, spec)
}

/**
 * A transition that manages animated values between state changes.
 */
class Transition<T>(
    initialState: T,
    private val spec: TransitionSpec
) {
    /**
     * Current target state of the transition.
     */
    private val state = mutableStateOf(initialState)
    
    /**
     * Create an animated value of type Float.
     */
    fun animateFloat(initialValue: Float, targetValue: Float): State<Float> {
        return mutableStateOf(targetValue)
    }
    
    /**
     * Create an animated value of type Int.
     */
    fun animateInt(initialValue: Int, targetValue: Int): State<Int> {
        return mutableStateOf(targetValue)
    }
    
    /**
     * Create an animated value of type Color.
     */
    fun animateColor(initialValue: Color, targetValue: Color): State<Color> {
        return mutableStateOf(targetValue)
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
    val composer = CompositionLocal.currentComposer
    
    composer?.startNode()
    
    val infiniteTransition = code.yousef.summon.animation.rememberInfiniteTransition()
    
    // Render container
    if (composer?.inserting == true) {
        // Render container with modifier
    }
    
    // Execute content with infinite transition
    content(infiniteTransition)
    
    composer?.endNode()
}

/**
 * Converter between a type and float animation value.
 */
interface TwoWayConverter<T, V> {
    fun convertToVector(value: T): V
    fun convertFromVector(value: V): T
}

/**
 * Float converter - simply passes through the float value.
 */
object FloatConverter : TwoWayConverter<Float, Float> {
    override fun convertToVector(value: Float): Float = value
    override fun convertFromVector(value: Float): Float = value
}

/**
 * Int converter - converts between int and float.
 */
object IntConverter : TwoWayConverter<Int, Float> {
    override fun convertToVector(value: Int): Float = value.toFloat()
    override fun convertFromVector(value: Float): Int = value.toInt()
}

/**
 * Color converter - converts between Color and float vector.
 */
object ColorConverter : TwoWayConverter<Color, Float> {
    override fun convertToVector(value: Color): Float = value.alpha.toFloat()
    override fun convertFromVector(value: Float): Color = Color(0u).withAlpha(value.toInt())
} 
