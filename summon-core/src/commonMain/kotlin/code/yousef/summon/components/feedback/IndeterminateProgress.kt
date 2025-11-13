package codes.yousef.summon.components.feedback

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Creates an indeterminate progress indicator for operations with unknown duration.
 *
 * IndeterminateProgress is perfect for loading states where you cannot estimate completion
 * time or progress percentage. It provides continuous animation to indicate that work is
 * happening in the background.
 *
 * ## Common Use Cases
 * - Initial data loading
 * - Background processing
 * - Network requests with unknown timing
 * - File processing operations
 * - Authentication flows
 * - System initialization
 *
 * ## Design Guidelines
 * - Use linear for page-level loading states
 * - Use circular for component-level loading
 * - Provide context with descriptive text
 * - Consider timeout handling for long operations
 *
 * @param type The type of progress indicator to display (LINEAR or CIRCULAR).
 * @param modifier Modifier to be applied to the progress indicator for styling and layout.
 *
 * @see Progress for determinate progress indicators
 * @see LinearProgress for linear progress with values
 * @see CircularProgress for circular progress with values
 * @sample code.yousef.summon.samples.feedback.ProgressSamples.indeterminateExample
 * @since 1.0.0
 */
@Composable
fun IndeterminateProgress(
    type: ProgressType = ProgressType.LINEAR,
    modifier: Modifier = Modifier()
) {
    LocalPlatformRenderer.current.renderProgress(null, type, modifier)
}
