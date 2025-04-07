package code.yousef.summon.animation

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.getPlatformRenderer

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
 * Composable that animates its content when the `targetState` changes.
 *
 * @param T The type of the state that triggers content changes.
 * @param targetState The state value. When this changes, the content will animate.
 * @param modifier Modifier applied to the container.
 * @param content A lambda that takes the `targetState` and returns the composable content for that state.
 */
@Composable
fun <T> AnimatedContent(
    targetState: T,
    modifier: Modifier = Modifier(),
    content: @Composable (targetState: T) -> Unit
) {
    val composer = CompositionLocal.currentComposer

    composer?.startNode() // Start AnimatedContent node
    if (composer?.inserting == true) {
        val renderer = getPlatformRenderer()
        // Renderer likely observes changes within its scope to apply animations
        renderer.renderAnimatedContent(modifier = modifier)
    }

    // Compose the content for the *current* targetState within the node.
    // The renderer is responsible for animating the transition when this content changes.
    content(targetState)

    composer?.endNode() // End AnimatedContent node
}

// Removed placeholder ContentTransform typealias
// Removed defaultContentTransform function 
