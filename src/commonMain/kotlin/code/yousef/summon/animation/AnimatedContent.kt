package code.yousef.summon.animation

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.State
import kotlinx.html.TagConsumer

/**
 * Animation content transition type.
 */
enum class ContentTransitionType {
    FADE,     // Fade between old and new content
    SLIDE,    // Slide between old and new content
    SCALE,    // Scale between old and new content
    CROSSFADE // Crossfade between old and new content (smooth blend)
}

/**
 * Direction for content transitions
 */
enum class ContentDirection {
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT,
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP
}

/**
 * A composable that animates between different content when the targetState changes.
 *
 * @param targetState The current state that determines which content to show
 * @param transitionType The animation type to use when content changes
 * @param direction The direction for sliding transitions
 * @param duration Animation duration in milliseconds
 * @param easing The animation easing function
 * @param modifier The modifier to apply to this composable
 * @param contentFactory Function that produces content for a given state
 */
@Composable
fun <T> AnimatedContent(
    targetState: State<T>,
    transitionType: ContentTransitionType = ContentTransitionType.FADE,
    direction: ContentDirection = ContentDirection.LEFT_TO_RIGHT,
    duration: Int = 300,
    easing: Easing = Easing.EASE_IN_OUT,
    modifier: Modifier = Modifier(),
    content: @Composable (T) -> Unit
) {
    val renderer = getPlatformRenderer()
    renderer.renderAnimatedContent(modifier) {
        // Call the content with the current target state
        content(targetState.value)
    }
}

/**
 * Creates an AnimatedContent component with simplified parameters.
 *
 * @param targetState The current state that determines which content to show
 * @param transitionType The animation type to use when content changes
 * @param duration Animation duration in milliseconds
 * @param modifier The modifier to apply to this composable
 * @param content Function that produces content for a given state
 */
@Composable
fun <T> animatedContent(
    targetState: State<T>,
    transitionType: ContentTransitionType = ContentTransitionType.FADE,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionType = transitionType,
        duration = duration,
        modifier = modifier,
        content = content
    )
}

/**
 * Creates a crossfade animation between different content states.
 *
 * @param targetState The current state that determines which content to show
 * @param duration Animation duration in milliseconds
 * @param modifier The modifier to apply to this composable
 * @param content Function that produces content for a given state
 */
@Composable
fun <T> crossfade(
    targetState: State<T>,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionType = ContentTransitionType.CROSSFADE,
        duration = duration,
        modifier = modifier,
        content = content
    )
} 