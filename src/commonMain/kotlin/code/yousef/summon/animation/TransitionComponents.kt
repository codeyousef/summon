package code.yousef.summon.animation

import code.yousef.summon.Composable
import code.yousef.summon.Modifier
import code.yousef.summon.State
import code.yousef.summon.mutableStateOf
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.style

/**
 * A composable that provides animated transition between states.
 * This is a higher-order component that wraps content and animates it when a state changes.
 *
 * @param stateValue The state to monitor for changes
 * @param animation The animation to use for transitions
 * @param modifier The modifier to apply to this composable
 * @param contentBuilder Function that produces content with animated properties
 */
class TransitionComponent<T>(
    val stateValue: State<T>,
    val animation: Animation = TweenAnimation(),
    val modifier: Modifier = Modifier(),
    val contentBuilder: (Transition<T>) -> List<Composable>
) : Composable {

    private val transition = Transition(stateValue.value, TransitionSpec(animation))

    init {
        // Update transition when state changes
        if (stateValue is code.yousef.summon.MutableStateImpl) {
            stateValue.addListener { newValue ->
                transition.updateState(newValue)
            }
        }
    }

    /**
     * Renders this component using simple div approach
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            // Create a container for the content
            val container = receiver

            // Create a div with the content
            (container as kotlinx.html.TagConsumer<*>).div {
                this.style = modifier.styles.entries.joinToString(";") {
                    "${it.key}:${it.value}"
                }

                // Render the content
                contentBuilder(transition).forEach { child ->
                    child.compose(this)
                }
            }
        }

        return receiver
    }

    /**
     * Gets the current transition
     */
    fun getTransition(): Transition<T> = transition
}

/**
 * A composable that provides continuous animations with an infinite transition.
 * This is useful for looping animations, loading indicators, and background effects.
 *
 * @param running Whether the animation is currently running
 * @param modifier The modifier to apply to this composable
 * @param contentBuilder Function that produces content with animated properties
 */
class InfiniteTransitionComponent(
    val running: Boolean = true,
    val modifier: Modifier = Modifier(),
    val contentBuilder: (InfiniteTransition) -> List<Composable>
) : Composable {

    private val infiniteTransition = InfiniteTransition()
    private val isRunning = mutableStateOf(running)

    init {
        if (isRunning.value != running) {
            isRunning.value = running
            if (running) {
                infiniteTransition.resume()
            } else {
                infiniteTransition.pause()
            }
        }
    }

    /**
     * Renders this component using simple div approach
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            // Create a container for the content
            val container = receiver

            // Create a div with the content
            (container as kotlinx.html.TagConsumer<*>).div {
                this.style = modifier.styles.entries.joinToString(";") {
                    "${it.key}:${it.value}"
                }

                // Render the content
                contentBuilder(infiniteTransition).forEach { child ->
                    child.compose(this)
                }
            }
        }

        return receiver
    }

    /**
     * Gets the infinite transition
     */
    fun getInfiniteTransition(): InfiniteTransition = infiniteTransition
}

/**
 * Creates a transition that animates when the state changes.
 *
 * @param state The state to monitor for changes
 * @param animation The animation to use for transitions
 * @param modifier The modifier to apply
 * @param content Function that produces content with the transition
 */
fun <T> transition(
    state: State<T>,
    animation: Animation = TweenAnimation(),
    modifier: Modifier = Modifier(),
    content: (Transition<T>) -> List<Composable>
): TransitionComponent<T> {
    return TransitionComponent(
        stateValue = state,
        animation = animation,
        modifier = modifier,
        contentBuilder = content
    )
}

/**
 * Creates an infinite transition for continuous animations.
 *
 * @param running Whether the animation is currently running
 * @param modifier The modifier to apply
 * @param content Function that produces content with the infinite transition
 */
fun infiniteTransition(
    running: Boolean = true,
    modifier: Modifier = Modifier(),
    content: (InfiniteTransition) -> List<Composable>
): InfiniteTransitionComponent {
    return InfiniteTransitionComponent(
        running = running,
        modifier = modifier,
        contentBuilder = content
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