package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider


/**
 * A flexible space component that can be used to add space between other components.
 *
 * @param modifier The modifier to be applied to the Spacer. Should typically include width or height.
 */
@Composable
fun Spacer(modifier: Modifier) {
    val composer = CompositionLocal.currentComposer
    val finalModifier = modifier // Spacer relies entirely on the modifier for size

    composer?.startNode() // Start Spacer node
    if (composer?.inserting == true) {
        // TODO: Replace getPlatformRenderer with CompositionLocal access? Seems redundant.
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        renderer.renderSpacer(modifier = finalModifier)
    }
    composer?.endNode() // End Spacer node (self-closing)
} 
