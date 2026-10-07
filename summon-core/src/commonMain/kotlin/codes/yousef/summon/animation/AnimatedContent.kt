package codes.yousef.summon.animation

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.state.State

/**
 * Animation content transition type.
 */
enum class ContentTransitionType {
    /** The fade content transition type option. */
    FADE,
    /** The slide content transition type option. */
    SLIDE,
    /** The scale content transition type option. */
    SCALE,
    /** The crossfade content transition type option. */
    CROSSFADE // Crossfade between old and new content (smooth blend)
}

/**
 * Direction for content transitions
 */
enum class ContentDirection {
    /** The left to right content direction option. */
    LEFT_TO_RIGHT,
    /** The right to left content direction option. */
    RIGHT_TO_LEFT,
    /** The top to bottom content direction option. */
    TOP_TO_BOTTOM,
    /** The bottom to top content direction option. */
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
 * @param content Function that produces content for a given state
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
    val renderer = LocalPlatformRenderer.current

    val cssTransition = when (transitionType) {
        ContentTransitionType.FADE -> "opacity ${duration}ms ${easing.toCssString()}"
        ContentTransitionType.SLIDE -> "transform ${duration}ms ${easing.toCssString()}"
        ContentTransitionType.SCALE -> "transform ${duration}ms ${easing.toCssString()}"
        ContentTransitionType.CROSSFADE -> "opacity ${duration}ms ${easing.toCssString()}"
    }
    var animatedModifier = modifier.style("transition", cssTransition)
    animatedModifier = when (transitionType) {
        ContentTransitionType.FADE -> animatedModifier.style("opacity", "1")
        ContentTransitionType.SLIDE -> {
            val transform = when (direction) {
                ContentDirection.LEFT_TO_RIGHT, ContentDirection.RIGHT_TO_LEFT -> "translateX(0)"
                ContentDirection.TOP_TO_BOTTOM, ContentDirection.BOTTOM_TO_TOP -> "translateY(0)"
            }
            animatedModifier.style("transform", transform)
        }
        ContentTransitionType.SCALE -> animatedModifier.style("transform", "scale(1)")
        ContentTransitionType.CROSSFADE -> animatedModifier.style("opacity", "1")
    }

    // Use renderAnimatedContent with the enhanced modifier
    renderer.renderAnimatedContent(
        modifier = animatedModifier,
        content = { content(targetState.value) }
    )
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
