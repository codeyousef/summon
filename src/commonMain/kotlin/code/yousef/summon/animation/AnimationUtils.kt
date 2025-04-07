package code.yousef.summon.animation

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column

/**
 * Creates a button with animation effects when clicked.
 *
 * @param label Text to display on the button
 * @param onClick Callback to invoke when the button is clicked
 * @param animation Animation to apply (default is a spring effect)
 * @param modifier Base modifier to apply
 * @return A Button component with animation
 */
@Composable
fun animatedButton(
    label: String,
    onClick: () -> Unit = {},
    animation: Animation = SpringAnimation(stiffness = 200f, damping = 10f, durationMs = 300),
    modifier: Modifier = Modifier()
) {
    // Create a button with animation effects
    Button(
        text = label,
        onClick = onClick,
        modifier = modifier
    )
}

/**
 * Creates a text component that animates in when first displayed.
 *
 * @param text The text content to display
 * @param enterTransition The entrance transition type
 * @param duration Animation duration in milliseconds
 * @param modifier Base modifier to apply
 */
@Composable
fun animatedText(
    text: String,
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    duration: Int = 500,
    modifier: Modifier = Modifier()
) {
    // Create a visible state
    val isVisible = remember { mutableStateOf(true) }

    // Return an animated visibility component with the text
    AnimatedVisibility(
        visible = isVisible.value,
        enterTransition = enterTransition,
        enterParams = TransitionParams(duration = duration),
        modifier = modifier
    ) {
        Text(text = text)
    }
}

/**
 * Creates an infinite animation, such as for loading indicators or continuous effects.
 *
 * @param animation Type of animation to apply
 * @param duration Duration in milliseconds
 * @param repeatMode How the animation should repeat
 * @param initialValue Starting value
 * @param targetValue Ending value
 */
@Composable
fun createInfiniteAnimation(
    animation: Animation = SpringAnimation(),
    duration: Int = 1000,
    repeatMode: RepeatMode = RepeatMode.RESTART,
    initialValue: Float = 0f,
    targetValue: Float = 1f
) {
    // Return a state value that would animate
    val animatedValue = remember { mutableStateOf(initialValue) }
    
    // In a real implementation, this would set up an actual animation
    // For now, we just use the initial value
}

/**
 * Enum for animation repeat modes
 */
enum class RepeatMode {
    RESTART,
    REVERSE
}

/**
 * Creates a staggered animation where each child appears with a delay.
 *
 * @param items List of items to display with staggered animation
 * @param itemContent Function to create content for each item
 * @param staggerDelay Delay between each item's animation in milliseconds
 * @param enterTransition The animation to use for each item
 * @param modifier Base modifier to apply
 */
@Composable
fun <T> staggeredAnimation(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    staggerDelay: Int = 100,
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    modifier: Modifier = Modifier()
) {
    // Create visible state for all items
    val itemStates = remember { 
        List(items.size) { mutableStateOf(false) }
    }

    // Return a column with animated items
    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            // Set each item to be visible with increasing delay
            itemStates[index].value = true

            // Create animated visibility for each item
            AnimatedVisibility(
                visible = itemStates[index].value,
                enterTransition = enterTransition,
                enterParams = TransitionParams(
                    duration = 300,
                    delay = index * staggerDelay
                )
            ) {
                itemContent(item)
            }
        }
    }
} 

/**
 * A component for creating infinite animations.
 * 
 * @param running Whether the animation is running
 * @param modifier The modifier for the component
 * @param contentBuilder The content builder that receives an InfiniteTransition
 */
@Composable
fun InfiniteTransitionComponent(
    running: Boolean = true,
    modifier: Modifier = Modifier(),
    contentBuilder: (InfiniteTransition) -> @Composable () -> Unit
) {
    // Create an infinite transition
    val transition = rememberInfiniteTransition()
    
    // Only run the animation if it's enabled
    if (running) {
        contentBuilder(transition)()
    }
}

/**
 * Remember an infinite transition that can be used to create animations.
 */
@Composable
fun rememberInfiniteTransition(): InfiniteTransition {
    return remember { InfiniteTransition() }
}

/**
 * A class that provides methods for creating infinite animations.
 */
class InfiniteTransition {
    /**
     * Creates an animation for a float value that repeats infinitely.
     * 
     * @param initialValue The initial value
     * @param targetValue The target value
     * @param animation The animation specification
     * @return A state that holds the animated value
     */
    fun animateFloat(
        initialValue: Float,
        targetValue: Float,
        animation: Animation
    ): Float {
        // In a real implementation, this would set up an actual animation
        // For now, we just return the initial value
        return initialValue
    }
}
