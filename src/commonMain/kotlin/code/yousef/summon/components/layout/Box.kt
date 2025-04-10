package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * A layout component that stacks its children on top of each other.
 * 
 * @param modifier The modifier to be applied to this layout
 * @param content The content to be displayed inside the box
 */
@Composable
fun Box(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    // Use platform renderer directly
    val renderer = LocalPlatformRenderer.current
    renderer.renderBox(modifier)
    
    // Call content inside the box
    content()
}
