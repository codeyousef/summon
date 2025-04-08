package code.yousef.summon.components.core

import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider

/**
 * A basic text component that renders text with styling.
 * 
 * @param text The text to display
 * @param modifier Modifier to apply styling to the text
 */
@Composable
fun BasicText(
    text: String,
    modifier: Modifier = Modifier
) {
    val composer = CompositionLocal.currentComposer
    
    composer?.startNode()
    
    if (composer?.inserting == true) {
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        renderer.renderText(text, modifier, Any()) // style parameter is Any() as a placeholder
    }
    
    composer?.endNode()
}

/**
 * Marker annotation for composable functions.
 * This will eventually be replaced by the actual Compose annotation.
 */
// Removed redundant annotation class as we're now using the one from runtime package 