package codes.yousef.summon.components.feedback

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Creates a circular progress indicator that represents the progress of an operation.
 *
 * CircularProgress provides a compact, circular progress indicator perfect for small spaces
 * and mobile interfaces. It's ideal for showing progress in buttons, cards, or any space-
 * constrained UI element.
 *
 * ## Features
 * - **Compact design**: Perfect for small UI elements and mobile interfaces
 * - **Standard styling**: Consistent circular appearance across platforms
 * - **Accessibility**: Built-in ARIA attributes and screen reader support
 * - **Performance optimized**: Smooth circular animations through platform renderers
 *
 * ## Basic Usage
 * ```kotlin
 * // Determinate circular progress
 * CircularProgress(progress = 0.75f)
 *
 * // Indeterminate spinning indicator
 * CircularProgress(progress = null)
 * ```
 *
 * ## Common Use Cases
 * - Button loading states
 * - Compact progress indicators
 * - Dashboard widgets
 * - Mobile interface elements
 * - Inline loading spinners
 *
 * @param progress Value representing the progress between 0.0 and 1.0. If null, shows indeterminate spinner.
 * @param modifier Modifier to be applied to the progress indicator for sizing and styling.
 *
 * @see Progress for full-featured progress component
 * @see LinearProgress for linear progress bars
 * @sample codes.yousef.summon.samples.feedback.ProgressSamples.circularProgressExample
 * @since 1.0.0
 */
@Composable
fun CircularProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier()
) {
    LocalPlatformRenderer.current.renderProgress(progress, ProgressType.CIRCULAR, modifier)
}
