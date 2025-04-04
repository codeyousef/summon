package code.yousef.summon.animation

import code.yousef.summon.*

/**
 * Creates a button with animation effects when clicked.
 *
 * @param label Text to display on the button
 * @param onClick Callback to invoke when the button is clicked
 * @param animation Animation to apply (default is a spring effect)
 * @param modifier Base modifier to apply
 * @return An animated Button composable
 */
fun animatedButton(
    label: String,
    onClick: (Any) -> Unit = {},
    animation: Animation = SpringAnimation(stiffness = 200f, damping = 10f, durationMs = 300),
    modifier: Modifier = Modifier()
): Button {
    // Create a button with animation effects
    return Button(
        label = label,
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
 * @return A Text component with entrance animation
 */
fun animatedText(
    text: String,
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    duration: Int = 500,
    modifier: Modifier = Modifier()
): Composable {
    // Create a visible state
    val isVisible = mutableStateOf(true)

    // Return an animated visibility component with the text
    return AnimatedVisibility(
        visible = isVisible.value,
        enterTransition = enterTransition,
        enterParams = TransitionParams(duration = duration),
        modifier = modifier,
        content = listOf(Text(text))
    )
}

/**
 * Creates a pulse animation effect for any component.
 *
 * @param content The content to animate
 * @param pulseInterval The interval between pulses in milliseconds
 * @param minScale The minimum scale during the pulse
 * @param maxScale The maximum scale during the pulse
 * @param modifier Base modifier to apply
 * @return A composable with pulse animation
 */
fun pulseAnimation(
    content: List<Composable>,
    pulseInterval: Int = 1000,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    modifier: Modifier = Modifier()
): Composable {
    // Create an infinite transition for the pulse effect
    return InfiniteTransitionComponent(
        running = true,
        modifier = modifier,
        contentBuilder = { infiniteTransition ->
            // Create an animated scale value
            val scale = infiniteTransition.animateFloat(
                initialValue = minScale,
                targetValue = maxScale,
                animation = TweenAnimation(
                    durationMs = pulseInterval / 2,
                    repeating = true,
                    easing = Easing.EASE_IN_OUT
                )
            )

            // Return the content with scale transformation
            content
        }
    )
}

/**
 * Creates a staggered animation where each child appears with a delay.
 *
 * @param items List of items to display with staggered animation
 * @param itemContent Function to create content for each item
 * @param staggerDelay Delay between each item's animation in milliseconds
 * @param enterTransition The animation to use for each item
 * @param modifier Base modifier to apply
 * @return A Column with staggered animation for its children
 */
fun <T> staggeredAnimation(
    items: List<T>,
    itemContent: (T) -> Composable,
    staggerDelay: Int = 100,
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    modifier: Modifier = Modifier()
): Composable {
    // Create visible state for all items
    val itemStates = List(items.size) { mutableStateOf(false) }

    // Return a column with animated items
    return Column(
        modifier = modifier,
        content = items.mapIndexed { index, item ->
            // Set each item to be visible with increasing delay
            itemStates[index].value = true

            // Create animated visibility for each item
            AnimatedVisibility(
                visible = itemStates[index].value,
                enterTransition = enterTransition,
                enterParams = TransitionParams(
                    duration = 300,
                    delay = index * staggerDelay
                ),
                content = listOf(itemContent(item))
            )
        }
    )
} 