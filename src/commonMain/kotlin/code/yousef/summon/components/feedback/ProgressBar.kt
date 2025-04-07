package code.yousef.summon.components.feedback

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider



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
    // TODO: Apply default progress bar styles (height, background) to modifier?
    val finalModifier = modifier // Placeholder

    composer?.startNode() // Start ProgressBar node
    if (composer?.inserting == true) {
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        renderer.renderProgress(progress, ProgressType.LINEAR, finalModifier)
    }
    composer?.endNode() // End ProgressBar node (self-closing)
} 
