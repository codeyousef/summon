package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Creates a linear progress indicator that represents the progress of an operation.
 *
 * LinearProgress provides a simplified interface for creating horizontal progress bars.
 * It's ideal for showing completion progress of tasks with known duration or steps.
 *
 * ## Features
 * - **Simplified API**: Easy-to-use interface for common linear progress needs
 * - **Standard styling**: Consistent appearance across your application
 * - **Accessibility**: Built-in ARIA attributes and screen reader support
 * - **Performance optimized**: Efficient rendering through platform renderers
 *
 * ## Basic Usage
 * ```kotlin
 * // Determinate progress (0.0 to 1.0)
 * LinearProgress(progress = 0.65f)
 *
 * // Indeterminate progress
 * LinearProgress(progress = null)
 * ```
 *
 * ## Integration with State
 * ```kotlin
 * @Composable
 * fun DownloadProgress() {
 *     var downloadProgress by remember { mutableStateOf(0f) }
 *
 *     LaunchedEffect(Unit) {
 *         // Simulate download progress
 *         for (i in 0..100) {
 *             downloadProgress = i / 100f
 *             delay(50)
 *         }
 *     }
 *
 *     LinearProgress(
 *         progress = downloadProgress,
 *         modifier = Modifier()
 *             .width(Width.FULL)
 *             .height(Height.of("8px"))
 *     )
 * }
 * ```
 *
 * ## Common Use Cases
 * - File upload/download progress
 * - Form completion indicators
 * - Loading progress with known steps
 * - Page loading indicators
 * - Data processing progress
 *
 * @param progress Value representing the progress between 0.0 and 1.0. If null, shows indeterminate progress.
 * @param modifier Modifier to be applied to the progress indicator for styling and layout.
 *
 * @see Progress for full-featured progress component
 * @see CircularProgress for circular progress indicators
 * @sample code.yousef.summon.samples.feedback.ProgressSamples.linearProgressExample
 * @since 1.0.0
 */
@Composable
fun LinearProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier()
) {
    LocalPlatformRenderer.current.renderProgress(progress, ProgressType.LINEAR, modifier)
}
