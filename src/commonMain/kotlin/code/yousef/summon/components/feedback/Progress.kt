package code.yousef.summon.components.feedback

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal

/**
 * Progress indicator types.
 * Linear is typically the default HTML <progress> element.
 * Circular usually requires custom SVG/CSS.
 */
enum class ProgressType {
    LINEAR, CIRCULAR
}

/**
 * A composable that displays a progress indicator.
 * Can be determinate (showing progress towards completion) or indeterminate.
 *
 * @param progress The current progress value between 0.0 and 1.0.
 *                 Set to `null` for an indeterminate progress indicator.
 * @param modifier Modifier applied to the progress indicator.
 * @param type The visual type (Linear or Circular). TODO: Needs renderer support for CIRCULAR.
 * @param color The color of the progress indicator track/bar. TODO: Handle via modifier.
 * @param trackColor The color of the background track (for linear). TODO: Handle via modifier.
 */
@Composable
fun Progress(
    modifier: Modifier = Modifier(),
    progress: Float? = null, // Null indicates indeterminate
    type: ProgressType = ProgressType.LINEAR,
    color: String? = null, // TODO: Handled by modifier/renderer
    trackColor: String? = null // TODO: Handled by modifier/renderer
) {
    // Validate progress value
    val clampedProgress = progress?.coerceIn(0.0f, 1.0f)

    // TODO: Apply default styles + type styles + color styles to modifier
    val finalModifier = modifier
        // .progressStyle(type, color, trackColor) // Example modifier usage

    // TODO: Replace PlatformRendererProvider with CompositionLocal access
    // val renderer = LocalPlatformRenderer.current
    val renderer = PlatformRendererProvider.getRenderer()

    // TODO: Renderer signature might need updating to handle type, colors explicitly if not via modifier
    renderer.renderProgress(
        value = clampedProgress,
        modifier = finalModifier
    )
}

/**
 * Creates a linear progress bar.
 * @param progress The current progress value (0.0-1.0 or null for indeterminate)
 * @param modifier The modifier to apply to this composable
 * @param color The color of the progress indicator. TODO: Handle via modifier.
 * @param trackColor The color of the background track. TODO: Handle via modifier.
 */
@Composable
fun LinearProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier(),
    color: String? = null, // Default styling handled by Progress or renderer
    trackColor: String? = null // Default styling handled by Progress or renderer
) {
    Progress(
        progress = progress,
        modifier = modifier,
        type = ProgressType.LINEAR,
        color = color, // Pass through, let Progress/modifier handle defaults
        trackColor = trackColor
    )
}

/**
 * Creates a circular spinner or progress indicator.
 * @param progress The current progress value (0.0-1.0 or null for indeterminate)
 * @param modifier The modifier to apply to this composable
 * @param color The color of the spinner. TODO: Handle via modifier.
 */
@Composable
fun CircularProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier(),
    color: String? = null // Default styling handled by Progress or renderer
) {
    Progress(
        progress = progress,
        modifier = modifier,
        type = ProgressType.CIRCULAR,
        color = color // Pass through
    )
}

// Removed old Progress data class and internal helper methods.
// Removed ProgressAnimation enum - should be handled via modifiers/renderer. 