package code.yousef.summon.animation

import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.mutableStateOf
import kotlinx.html.TagConsumer

/**
 * Animation entry mode for AnimatedVisibility
 */
enum class EnterTransition {
    FADE_IN,     // Fade in from transparent to opaque
    SLIDE_IN,    // Slide in from the specified direction
    EXPAND_IN,   // Expand from a smaller size
    ZOOM_IN      // Zoom in from a smaller scale
}

/**
 * Animation exit mode for AnimatedVisibility
 */
enum class ExitTransition {
    FADE_OUT,    // Fade out from opaque to transparent
    SLIDE_OUT,   // Slide out to the specified direction
    SHRINK_OUT,  // Shrink to a smaller size
    ZOOM_OUT     // Zoom out to a smaller scale
}

/**
 * Slide direction for slide transitions
 */
enum class SlideDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN
}

/**
 * Parameters for entry and exit transitions
 */
data class TransitionParams(
    val duration: Int = 300,
    val easing: Easing = Easing.EASE_IN_OUT,
    val delay: Int = 0,
    val slideDirection: SlideDirection = SlideDirection.LEFT
)

/**
 * A composable that animates its content when it appears or disappears.
 *
 * @param visible Boolean state that controls the visibility
 * @param enterTransition The animation to use when the content appears
 * @param exitTransition The animation to use when the content disappears
 * @param enterParams Parameters for the enter transition
 * @param exitParams Parameters for the exit transition
 * @param modifier The modifier to apply to this composable
 * @param content The content to animate
 */
class AnimatedVisibility(
    val visible: Boolean,
    val enterTransition: EnterTransition = EnterTransition.FADE_IN,
    val exitTransition: ExitTransition = ExitTransition.FADE_OUT,
    val enterParams: TransitionParams = TransitionParams(),
    val exitParams: TransitionParams = TransitionParams(),
    val modifier: Modifier = Modifier(),
    val content: List<Composable>
) : Composable {

    private val isVisible = mutableStateOf(visible)
    private val isAnimating = mutableStateOf(false)

    init {
        if (isVisible.value != visible) {
            isVisible.value = visible
            isAnimating.value = true
            // Note: Animation handling is done in the renderer
        }
    }

    /**
     * Gets CSS styles for the current visibility state and animation
     */
    internal fun getAnimationStyles(): Map<String, String> {
        val styles = mutableMapOf<String, String>()

        // Base animation properties
        styles["transition-property"] = "opacity, transform"

        if (visible) {
            styles["transition-duration"] = "${enterParams.duration}ms"
            styles["transition-timing-function"] = enterParams.easing.toCssString()
            styles["transition-delay"] = "${enterParams.delay}ms"

            // Apply enter transition styles
            when (enterTransition) {
                EnterTransition.FADE_IN -> {
                    styles["opacity"] = "1"
                }

                EnterTransition.SLIDE_IN -> {
                    styles["opacity"] = "1"
                    styles["transform"] = "translateX(0) translateY(0)"
                }

                EnterTransition.EXPAND_IN -> {
                    styles["opacity"] = "1"
                    styles["transform"] = "scale(1)"
                    styles["max-height"] = "1000px" // Arbitrary large value
                    styles["max-width"] = "1000px" // Arbitrary large value
                }

                EnterTransition.ZOOM_IN -> {
                    styles["opacity"] = "1"
                    styles["transform"] = "scale(1)"
                }
            }
        } else {
            styles["transition-duration"] = "${exitParams.duration}ms"
            styles["transition-timing-function"] = exitParams.easing.toCssString()
            styles["transition-delay"] = "${exitParams.delay}ms"

            // Apply exit transition styles
            when (exitTransition) {
                ExitTransition.FADE_OUT -> {
                    styles["opacity"] = "0"
                }

                ExitTransition.SLIDE_OUT -> {
                    styles["opacity"] = "0"
                    val transform = when (exitParams.slideDirection) {
                        SlideDirection.LEFT -> "translateX(-100%)"
                        SlideDirection.RIGHT -> "translateX(100%)"
                        SlideDirection.UP -> "translateY(-100%)"
                        SlideDirection.DOWN -> "translateY(100%)"
                    }
                    styles["transform"] = transform
                }

                ExitTransition.SHRINK_OUT -> {
                    styles["opacity"] = "0"
                    styles["transform"] = "scale(0.8)"
                    styles["max-height"] = "0px"
                    styles["max-width"] = "0px"
                }

                ExitTransition.ZOOM_OUT -> {
                    styles["opacity"] = "0"
                    styles["transform"] = "scale(0.8)"
                }
            }
        }

        return styles
    }

    /**
     * Gets initial CSS styles for when the component first renders
     */
    internal fun getInitialStyles(): Map<String, String> {
        val styles = mutableMapOf<String, String>()

        if (!visible) {
            styles["display"] = "none"
            styles["opacity"] = "0"
        } else {
            styles["opacity"] = "1"
        }

        return styles
    }

    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderAnimatedVisibility(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
}

/**
 * Creates an AnimatedVisibility component with simplified parameters.
 *
 * @param visible Boolean state that controls the visibility
 * @param enterTransition The animation to use when the content appears
 * @param exitTransition The animation to use when the content disappears
 * @param duration Animation duration in milliseconds
 * @param modifier The modifier to apply to this composable
 * @param content Builder for the content to animate
 */
fun animatedVisibility(
    visible: Boolean,
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    exitTransition: ExitTransition = ExitTransition.FADE_OUT,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: () -> List<Composable>
): AnimatedVisibility {
    return AnimatedVisibility(
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        enterParams = TransitionParams(duration = duration),
        exitParams = TransitionParams(duration = duration),
        modifier = modifier,
        content = content()
    )
}

/**
 * Creates a fade-in/fade-out animated visibility component.
 *
 * @param visible Boolean state that controls the visibility
 * @param duration Animation duration in milliseconds
 * @param modifier The modifier to apply to this composable
 * @param content Builder for the content to animate
 */
fun fadeInOut(
    visible: Boolean,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: () -> List<Composable>
): AnimatedVisibility {
    return AnimatedVisibility(
        visible = visible,
        enterTransition = EnterTransition.FADE_IN,
        exitTransition = ExitTransition.FADE_OUT,
        enterParams = TransitionParams(duration = duration),
        exitParams = TransitionParams(duration = duration),
        modifier = modifier,
        content = content()
    )
}

/**
 * Creates a slide-in/slide-out animated visibility component.
 *
 * @param visible Boolean state that controls the visibility
 * @param direction The slide direction for both enter and exit
 * @param duration Animation duration in milliseconds
 * @param modifier The modifier to apply to this composable
 * @param content Builder for the content to animate
 */
fun slideInOut(
    visible: Boolean,
    direction: SlideDirection = SlideDirection.LEFT,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: () -> List<Composable>
): AnimatedVisibility {
    return AnimatedVisibility(
        visible = visible,
        enterTransition = EnterTransition.SLIDE_IN,
        exitTransition = ExitTransition.SLIDE_OUT,
        enterParams = TransitionParams(duration = duration, slideDirection = direction),
        exitParams = TransitionParams(duration = duration, slideDirection = direction),
        modifier = modifier,
        content = content()
    )
} 