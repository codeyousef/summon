package codes.yousef.summon.components.feedback

import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer


/**
 * A component used to indicate the progress of an operation.
 *
 * @param progress The current progress value (between 0.0 and 1.0). If null, an indeterminate progress bar is shown.
 * @param modifier The modifier to be applied to the progress bar
 */
@Composable
fun ProgressBar(
    progress: Float? = null,
    modifier: Modifier = Modifier()
) {
    val composer = CompositionLocal.currentComposer
    // Apply default progress bar styles - consistent look and feel
    val finalModifier = modifier
        .height("8px")
        .background("#f0f0f0")
        .borderRadius("4px")

    composer?.startNode() // Start ProgressBar node
    if (composer?.inserting == true) {
        val renderer = LocalPlatformRenderer.current
        renderer.renderProgress(progress, ProgressType.LINEAR, finalModifier)
    }
    composer?.endNode() // End ProgressBar node (self-closing)
} 
