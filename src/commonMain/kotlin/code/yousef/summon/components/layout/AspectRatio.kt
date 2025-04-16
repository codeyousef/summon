package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer


/**
 * A layout composable that attempts to size its content to a specific aspect ratio.
 *
 * @param ratio The desired aspect ratio (width / height).
 * @param modifier Modifier to apply to the aspect ratio container.
 * @param content The composable content to display within the aspect ratio container.
 */
@Composable
fun AspectRatio(
    ratio: Float,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    val platformRenderer = LocalPlatformRenderer.current

    // Use the platform renderer to create the aspect ratio container
    platformRenderer.renderAspectRatio(
        ratio = ratio,
        modifier = modifier,
        content = {
            content()
        }
    )
} 
