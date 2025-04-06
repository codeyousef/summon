package code.yousef.summon.components.feedback

import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.runtime.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.components.feedback.ProgressType

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
    val renderer = getPlatformRenderer()
    renderer.renderProgress(progress, ProgressType.LINEAR, modifier)
} 