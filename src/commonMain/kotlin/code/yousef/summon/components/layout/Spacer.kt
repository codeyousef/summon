package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * A layout component that creates empty space with the given dimensions.
 * 
 * @param modifier The modifier to be applied to this component
 */
@Composable
fun Spacer(
    modifier: Modifier = Modifier()
) {
    // Use platform renderer directly
    val renderer = LocalPlatformRenderer.current
    renderer.renderSpacer(modifier)
} 