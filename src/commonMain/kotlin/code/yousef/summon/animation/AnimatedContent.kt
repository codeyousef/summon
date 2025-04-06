package code.yousef.summon.animation

import code.yousef.summon.State
import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier

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
 * A class holding logic for animating between different content when the targetState changes.
 * NOTE: This class structure is likely outdated after the refactor to @Composable functions.
 * The @Composable AnimatedContent function uses AnimatedContentData, and the rendering/animation
 * logic is primarily handled by the Composer/PlatformRenderer/JS.
 *
 * @param targetState The current state that determines which content to show
 * @param transitionType The animation type to use when content changes
 * @param direction The direction for sliding transitions
 * @param duration Animation duration in milliseconds
 * @param easing The animation easing function
 * @param modifier The modifier to apply to the container rendered by the PlatformRenderer
 * @param contentFactory Function that produces the old Composable list for a given state (OUTDATED)
 */
class AnimatedContent<T>(
    val targetState: State<T>,
    val transitionType: ContentTransitionType = ContentTransitionType.FADE,
    val direction: ContentDirection = ContentDirection.LEFT_TO_RIGHT,
    val duration: Int = 300,
    val easing: Easing = Easing.EASE_IN_OUT,
    val modifier: Modifier = Modifier(),
    // This contentFactory signature is based on the old Composable interface and is likely unused.
    // The new approach uses a content lambda: @Composable (T) -> Unit in AnimatedContentData.
    val contentFactory: (T) -> List<Composable> // Kept old import path for clarity
) /* : Composable */ { // Removed Composable inheritance

    // This internal state and logic might be useful if adapted for the Composer/JS animation handling,
    // but is not directly used by the refactored renderer.
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
     * Update content based on current target state (OUTDATED logic)
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

    // Removed compose method
    /*
    override fun <T> compose(receiver: T): T {
        updateContent()

        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            // This call used the old renderer signature
            // return PlatformRendererProvider.getRenderer().renderAnimatedContent(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
    */
}

// These factory functions create instances of the outdated AnimatedContent class.
// They likely need to be removed or updated to work with the new @Composable function and AnimatedContentData.
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
    content: (T) -> List<Composable> // Outdated signature
) {
    // ... (implementation remains, but creates outdated class instance)
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
    content: (T) -> List<Composable> // Outdated signature
) {
    // ... (implementation remains, but creates outdated class instance)
} 