package codes.yousef.summon.animation

import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LaunchedEffect
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.State
import codes.yousef.summon.state.mutableStateOf

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
 * Animates the appearance and disappearance of its content.
 * This implementation handles both entry and exit animations.
 *
 * @param visible Controls whether the content is visible.
 * @param modifier Modifier for the container.
 * @param enter Enter transition (e.g., EnterTransition.FADE_IN).
 * @param exit Exit transition (e.g., ExitTransition.FADE_OUT).
 * @param exitDuration Duration of the exit animation in milliseconds.
 * @param content The content to animate.
 */
@Composable
fun AnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier(),
    enter: EnterTransition = EnterTransition.FADE_IN,
    exit: ExitTransition = ExitTransition.FADE_OUT,
    exitDuration: Int = 300,
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    // Remember the last visibility state to handle exit animations
    val isVisible = remember { mutableStateOf(visible) }

    // Track whether we're in an exit animation
    val isExiting = remember { mutableStateOf(false) }

    // Update visibility state when the visible parameter changes
    if (visible != isVisible.value && !isExiting.value) {
        if (!visible) {
            // Starting exit animation
            isExiting.value = true
        } else {
            // Immediately show when becoming visible
            isVisible.value = true
        }
    }

    // Handle exit animation
    if (isExiting.value) {
        LaunchedEffect(Unit) {
            // Start the exit animation
            AnimationController.startAnimation(exitDuration)

            // Wait for the exit animation to complete
            delay(exitDuration.toLong())

            // After the animation duration, hide the content
            isVisible.value = false
            isExiting.value = false
        }
    }

    // Render content if it's visible or exiting
    if (isVisible.value || isExiting.value) {
        // Combine base modifier with animation hints
        val finalModifier = modifier.applyTransitionAttributes(enter, exit)
            .attribute("data-animation-state", if (isExiting.value) "exiting" else "entering")

        // Render the container block, content is rendered inside by renderBlock
        renderer.renderBlock(
            modifier = finalModifier,
            content = content // Pass content lambda directly
        )
    }
}

// Overload for State<Boolean>
@Composable
fun AnimatedVisibility(
    visible: State<Boolean>,
    modifier: Modifier = Modifier(),
    enter: EnterTransition = EnterTransition.FADE_IN,
    exit: ExitTransition = ExitTransition.FADE_OUT,
    exitDuration: Int = 300,
    content: @Composable FlowContent.() -> Unit
) {
    // Delegate to the Boolean version
    AnimatedVisibility(
        visible = visible.value,
        modifier = modifier,
        enter = enter,
        exit = exit,
        exitDuration = exitDuration,
        content = content
    )
}

/**
 * Helper to add data attributes for animation hints.
 * Platform-specific CSS/JS should target these attributes.
 */
private fun Modifier.applyTransitionAttributes(enter: EnterTransition, exit: ExitTransition): Modifier {
    return this
        .attribute("data-enter", enter.name.lowercase()) // Use enum name
        .attribute("data-exit", exit.name.lowercase())   // Use enum name
}
