package code.yousef.summon.animation

import code.yousef.summon.State
import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
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
class AnimatedContent<T>(
    val targetState: State<T>,
    val transitionType: ContentTransitionType = ContentTransitionType.FADE,
    val direction: ContentDirection = ContentDirection.LEFT_TO_RIGHT,
    val duration: Int = 300,
    val easing: Easing = Easing.EASE_IN_OUT,
    val modifier: Modifier = Modifier(),
    val contentFactory: (T) -> List<Composable>
) : Composable {

    private var currentContent: List<Composable> = contentFactory(targetState.value)
    private var previousContent: List<Composable>? = null
    private var isTransitioning: Boolean = false

    /**
     * Gets transition styles for the current content animation
     */
    internal fun getCurrentContentStyles(): Map<String, String> {
        val styles = mutableMapOf<String, String>()

        // Base transition properties
        styles["transition-property"] = getTransitionProperties()
        styles["transition-duration"] = "${duration}ms"
        styles["transition-timing-function"] = easing.toCssString()

        // Current content is always visible and at final position
        styles["opacity"] = "1"
        styles["transform"] = "translateX(0) translateY(0) scale(1)"
        styles["z-index"] = "2" // Ensure current content is on top

        return styles
    }

    /**
     * Gets transition styles for the previous content animation
     */
    internal fun getPreviousContentStyles(): Map<String, String> {
        val styles = mutableMapOf<String, String>()

        // Base transition properties
        styles["transition-property"] = getTransitionProperties()
        styles["transition-duration"] = "${duration}ms"
        styles["transition-timing-function"] = easing.toCssString()
        styles["z-index"] = "1" // Previous content behind current

        // Apply different exit animations based on transition type
        when (transitionType) {
            ContentTransitionType.FADE -> {
                styles["opacity"] = "0"
            }

            ContentTransitionType.SLIDE -> {
                val transform = when (direction) {
                    ContentDirection.LEFT_TO_RIGHT -> "translateX(-100%)"
                    ContentDirection.RIGHT_TO_LEFT -> "translateX(100%)"
                    ContentDirection.TOP_TO_BOTTOM -> "translateY(-100%)"
                    ContentDirection.BOTTOM_TO_TOP -> "translateY(100%)"
                }
                styles["transform"] = transform
                styles["opacity"] = "0"
            }

            ContentTransitionType.SCALE -> {
                styles["transform"] = "scale(0.8)"
                styles["opacity"] = "0"
            }

            ContentTransitionType.CROSSFADE -> {
                styles["opacity"] = "0"
            }
        }

        return styles
    }

    /**
     * Get transition CSS properties based on transition type
     */
    private fun getTransitionProperties(): String {
        return when (transitionType) {
            ContentTransitionType.FADE -> "opacity"
            ContentTransitionType.SLIDE -> "opacity, transform"
            ContentTransitionType.SCALE -> "opacity, transform"
            ContentTransitionType.CROSSFADE -> "opacity"
        }
    }

    /**
     * Gets container styles for the animated content wrapper
     */
    internal fun getContainerStyles(): Map<String, String> {
        return mapOf(
            "position" to "relative",
            "overflow" to "hidden"
        )
    }

    /**
     * Update content based on current target state
     */
    internal fun updateContent() {
        val newState = targetState.value
        val newContent = contentFactory(newState)

        if (currentContent != newContent) {
            previousContent = currentContent
            currentContent = newContent
            isTransitioning = true
        }
    }

    override fun <T> compose(receiver: T): T {
        updateContent()

        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderAnimatedContent(this, receiver as TagConsumer<T>)
        }
        return receiver
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
fun <T> animatedContent(
    targetState: State<T>,
    transitionType: ContentTransitionType = ContentTransitionType.FADE,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: (T) -> List<Composable>
): AnimatedContent<T> {
    return AnimatedContent(
        targetState = targetState,
        transitionType = transitionType,
        duration = duration,
        modifier = modifier,
        contentFactory = content
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
fun <T> crossfade(
    targetState: State<T>,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: (T) -> List<Composable>
): AnimatedContent<T> {
    return AnimatedContent(
        targetState = targetState,
        transitionType = ContentTransitionType.CROSSFADE,
        duration = duration,
        modifier = modifier,
        contentFactory = content
    )
} 