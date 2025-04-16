package code.yousef.summon.animation

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.state.State
import kotlinx.html.FlowContent
import code.yousef.summon.modifier.ModifierExtras.attribute

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
 * Note: This version uses simple conditional rendering. Proper exit animations require state management.
 *
 * @param visible Controls whether the content is visible.
 * @param modifier Modifier for the container.
 * @param enter Enter transition (e.g., EnterTransition.FADE_IN).
 * @param exit Exit transition (e.g., ExitTransition.FADE_OUT).
 * @param content The content to animate.
 */
@Composable
fun AnimatedVisibility(
    visible: Boolean, // Using simple Boolean for now
    modifier: Modifier = Modifier(),
    enter: EnterTransition = EnterTransition.FADE_IN, // Use Enum constant
    exit: ExitTransition = ExitTransition.FADE_OUT,  // Use Enum constant
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current

    // Simple conditional rendering (no exit animation handling yet)
    if (visible) {
        // Combine base modifier with animation hints
        val finalModifier = modifier.applyTransitionAttributes(enter, exit)

        // Render the container block, content is rendered inside by renderBlock
        renderer.renderBlock(
            modifier = finalModifier,
            content = content // Pass content lambda directly
        )
    }
    // If not visible, render nothing.
    // TODO: For exit animations, need state to keep rendering during exit.
}

// Overload for State<Boolean>
@Composable
fun AnimatedVisibility(
    visible: State<Boolean>, 
    modifier: Modifier = Modifier(),
    enter: EnterTransition = EnterTransition.FADE_IN, // Use Enum constant
    exit: ExitTransition = ExitTransition.FADE_OUT,  // Use Enum constant
    content: @Composable FlowContent.() -> Unit
) {
    // Delegate to the Boolean version for now
    // Proper implementation would use rememberUpdatedState and LaunchedEffect for exit animations
    AnimatedVisibility(
        visible = visible.value,
        modifier = modifier,
        enter = enter,
        exit = exit,
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
