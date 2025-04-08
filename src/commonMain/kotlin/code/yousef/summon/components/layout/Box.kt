package code.yousef.summon.components.layout

import code.yousef.summon.runtime.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider

/**
 * A layout container that positions its children according to the given modifier.
 * 
 * Box allows content to be positioned within it, and stacks its children on top of each other.
 * 
 * @param modifier [Modifier] to be applied to the Box layout
 * @param content The content to display inside the Box
 */
@Composable
fun Box(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer
    
    composer?.startNode()
    
    if (composer?.inserting == true) {
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        renderer.renderBox(modifier)
    }
    
    content()
    
    composer?.endNode()
} 
