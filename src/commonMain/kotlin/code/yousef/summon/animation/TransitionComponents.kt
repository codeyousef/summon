package code.yousef.summon.animation

import code.yousef.summon.State
import code.yousef.summon.core.UIElement
import code.yousef.summon.mutableStateOf
import code.yousef.summon.annotation.Composable

/**
 * Sets up a transition that animates its content when the state changes.
 * This function NO LONGER renders a container element.
 * Call the content lambda within your own layout composable.
 *
 * @param state The state to monitor for changes.
 * @param animation The animation specification to use for transitions.
 * @param content A composable lambda that receives the current [Transition] state 
 *                and emits the UI content based on it.
 */
fun <T> Transition(
    state: State<T>,
    animation: Animation = TweenAnimation(),
    content: @Composable (transition: Transition<T>) -> Unit
) {
    val transition = Transition(state.value, TransitionSpec(animation))

    if (state is code.yousef.summon.MutableStateImpl<*>) {
        (state as code.yousef.summon.MutableStateImpl<T>).addListener { newValue ->
            transition.updateState(newValue)
        }
    }

    content(transition)
}

/**
 * Sets up an infinite transition for continuous animations.
 * This function NO LONGER renders a container element.
 * Call the content lambda within your own layout composable.
 *
 * @param running Controls whether the animation is currently active.
 * @param content A composable lambda that receives the current [InfiniteTransition] state
 *                and emits the UI content based on it.
 */
fun InfiniteTransition(
    running: Boolean = true,
    content: @Composable (transition: InfiniteTransition) -> Unit
) {
    val infiniteTransition = InfiniteTransition()

    if (running) infiniteTransition.resume() else infiniteTransition.pause()

    content(infiniteTransition)
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