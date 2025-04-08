package code.yousef.summon.components.feedback

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider


/**
 * A circular progress indicator component.
 *
 * @param progress The current progress value (between 0.0 and 1.0). If null, an indeterminate progress indicator is shown.
 * @param modifier The modifier to be applied to the progress indicator
 */
@Composable
fun CircularProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier()
) {
    val composer = CompositionLocal.currentComposer
    // TODO: Apply default circular progress styles (size?) to modifier?
    val finalModifier = modifier // Placeholder

    composer?.startNode() // Start CircularProgress node
    if (composer?.inserting == true) {
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        renderer.renderProgress(progress, ProgressType.CIRCULAR, finalModifier)
    }
    composer?.endNode() // End CircularProgress node (self-closing)
} 
