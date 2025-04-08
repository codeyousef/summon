package code.yousef.summon.animation

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.getPlatformRenderer

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
 * Composable that animates the appearance and disappearance of its content.
 *
 * @param visible Whether the content should be visible.
 * @param modifier Modifier applied to the container.
 * @param enterTransition Enter transition for appearance animation.
 * @param exitTransition Exit transition for disappearance animation.
 * @param enterParams Parameters for enter transition.
 * @param exitParams Parameters for exit transition.
 * @param content The content to be animated.
 */
@Composable
fun AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier(),
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    exitTransition: ExitTransition = ExitTransition.FADE_OUT,
    enterParams: TransitionParams = TransitionParams(),
    exitParams: TransitionParams = TransitionParams(),
    content: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer

    composer?.startNode()
    
    if (composer?.inserting == true) {
        val renderer = getPlatformRenderer()
        renderer.renderAnimatedVisibility(visible, modifier)
    }

    if (visible) {
        content()
    }

    composer?.endNode()
}

/**
 * Old implementation for compatibility - supports list of composables.
 * This will be deprecated eventually.
 */
@Composable
fun AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier(),
    enterTransition: EnterTransition = EnterTransition.FADE_IN,
    exitTransition: ExitTransition = ExitTransition.FADE_OUT,
    enterParams: TransitionParams = TransitionParams(),
    exitParams: TransitionParams = TransitionParams(),
    content: List<@Composable () -> Unit>
) {
    val composer = CompositionLocal.currentComposer

    composer?.startNode()
    
    if (composer?.inserting == true) {
        val renderer = getPlatformRenderer()
        renderer.renderAnimatedVisibility(visible, modifier)
    }

    if (visible) {
        // Legacy approach for list of composables - not the modern API
        // In future versions, this will be removed
        content.forEach { it() }
    }

    composer?.endNode()
}

// Helper functions for transitions
fun fadeIn() = EnterTransition.FADE_IN
fun fadeOut() = ExitTransition.FADE_OUT
