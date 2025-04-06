package code.yousef.summon.animation

import code.yousef.summon.annotation.Composable
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
 * A class holding logic and parameters for animating visibility.
 * NOTE: This class structure is likely outdated after the refactor to @Composable functions.
 * The @Composable AnimatedVisibility function uses AnimatedVisibilityData, and the rendering/animation
 * logic is primarily handled by the Composer/PlatformRenderer/JS.
 *
 * @param visible Boolean state that controls the visibility
 * @param enterTransition The animation to use when the content appears
 * @param exitTransition The animation to use when the content disappears
 * @param enterParams Parameters for the enter transition
 * @param exitParams Parameters for the exit transition
 * @param modifier The modifier to apply to the container rendered by the PlatformRenderer
 * @param content The old Composable list content (OUTDATED)
 */
class AnimatedVisibility(
    val visible: Boolean,
    val enterTransition: EnterTransition = EnterTransition.FADE_IN,
    val exitTransition: ExitTransition = ExitTransition.FADE_OUT,
    val enterParams: TransitionParams = TransitionParams(),
    val exitParams: TransitionParams = TransitionParams(),
    val modifier: Modifier = Modifier(),
    // This content signature is based on the old Composable interface and is likely unused.
    // The new approach uses a content lambda: @Composable () -> Unit in AnimatedVisibilityData.
    val content: List<Composable> // Kept old import path for clarity
) /* : Composable */ { // Removed Composable inheritance

    // This internal state and logic might be useful if adapted for the Composer/JS animation handling,
    // but is not directly used by the refactored renderer.
    private val isVisible = mutableStateOf(visible)
    private val isAnimating = mutableStateOf(false)

    init {
        if (isVisible.value != visible) {
            isVisible.value = visible
            isAnimating.value = true
            // Note: Animation handling is now done elsewhere (Composer/Renderer/JS)
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

    // Removed compose method
    /*
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
             // This call used the old renderer signature
            // return PlatformRendererProvider.getRenderer().renderAnimatedVisibility(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
    */
}

// REMOVING OBSOLETE FACTORY FUNCTIONS BELOW

/* // Removed obsolete factory function animatedVisibility
fun animatedVisibility(
    visible: Boolean,
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    exitTransition: ExitTransition = ExitTransition.FADE_OUT,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: () -> List<code.yousef.summon.core.Composable> // Outdated signature
): AnimatedVisibility {
    return AnimatedVisibility(
        visible = visible,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        enterParams = TransitionParams(duration = duration),
        exitParams = TransitionParams(duration = duration),
        modifier = modifier,
        content = content()
    ) // implementation remains, but creates outdated class instance
}
*/

/* // Removed obsolete factory function fadeInOut
fun fadeInOut(
    visible: Boolean,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: () -> List<code.yousef.summon.core.Composable>
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
*/

/* // Removed obsolete factory function slideInOut
fun slideInOut(
    visible: Boolean,
    direction: SlideDirection = SlideDirection.LEFT,
    duration: Int = 300,
    modifier: Modifier = Modifier(),
    content: () -> List<code.yousef.summon.core.Composable>
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
*/

/**
 * A composable that animates its content when entering and exiting.
 *
 * @param visible Whether the content should be shown
 * @param modifier The modifier to apply to the animation container
 * @param enter The animation to use when the content appears
 * @param exit The animation to use when the content disappears
 * @param enterDurationMillis Duration for the enter animation in milliseconds
 * @param exitDurationMillis Duration for the exit animation in milliseconds
 * @param enterDelayMillis Delay before starting the enter animation in milliseconds
 * @param exitDelayMillis Delay before starting the exit animation in milliseconds
 * @param content The content to animate
 */
@Composable
fun animatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier(),
    enter: EnterTransition = EnterTransition.FADE_IN,
    exit: ExitTransition = ExitTransition.FADE_OUT,
    enterDurationMillis: Int = 300,
    exitDurationMillis: Int = 300,
    enterDelayMillis: Int = 0,
    exitDelayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    val enterParams = TransitionParams(
        duration = enterDurationMillis,
        delay = enterDelayMillis
    )
    
    val exitParams = TransitionParams(
        duration = exitDurationMillis,
        delay = exitDelayMillis
    )
    
    val animatedVisibilityData = AnimatedVisibilityData(
        visible = visible,
        enterTransition = enter,
        exitTransition = exit,
        enterParams = enterParams,
        exitParams = exitParams,
        modifier = modifier
    )
    
    // For now, let's use a simpler approach similar to Text component
    // Placeholder: Actual rendering call needs integration with composer context
    // The renderer will handle showing/hiding based on visibility state
    println("Composable animatedVisibility function called with visible=$visible")
    
    // Only render content if visible
    if (visible) {
        content()
    }
}

/**
 * Internal data class holding parameters for AnimatedVisibility.
 */
internal data class AnimatedVisibilityData(
    val visible: Boolean,
    val enterTransition: EnterTransition,
    val exitTransition: ExitTransition,
    val enterParams: TransitionParams,
    val exitParams: TransitionParams,
    val modifier: Modifier
) {
    /**
     * Gets animation-related CSS styles based on visibility state.
     */
    internal fun getAnimationStyles(): Map<String, String> {
        val styles = mutableMapOf<String, String>()

        // Base animation properties
        styles["transition-property"] = "opacity, transform"

        if (visible) {
            styles["transition-duration"] = "${enterParams.duration}ms"
            styles["transition-timing-function"] = enterParams.easing.toCssString()
            styles["transition-delay"] = "${enterParams.delay}ms"
            styles["opacity"] = "1"
            
            // Additional enter styles based on transition type
            when (enterTransition) {
                EnterTransition.SLIDE_IN -> {
                    styles["transform"] = "translateX(0) translateY(0)"
                }
                EnterTransition.EXPAND_IN, EnterTransition.ZOOM_IN -> {
                    styles["transform"] = "scale(1)"
                }
                else -> {} // FADE_IN needs no additional styles
            }
        } else {
            styles["transition-duration"] = "${exitParams.duration}ms"
            styles["transition-timing-function"] = exitParams.easing.toCssString()
            styles["transition-delay"] = "${exitParams.delay}ms"
            styles["opacity"] = "0"
            
            // Additional exit styles based on transition type
            when (exitTransition) {
                ExitTransition.SLIDE_OUT -> {
                    val transform = when (exitParams.slideDirection) {
                        SlideDirection.LEFT -> "translateX(-100%)"
                        SlideDirection.RIGHT -> "translateX(100%)"
                        SlideDirection.UP -> "translateY(-100%)"
                        SlideDirection.DOWN -> "translateY(100%)"
                    }
                    styles["transform"] = transform
                }
                ExitTransition.SHRINK_OUT, ExitTransition.ZOOM_OUT -> {
                    styles["transform"] = "scale(0.8)"
                }
                else -> {} // FADE_OUT needs no additional styles
            }
        }

        return styles
    }
}

// End of file 