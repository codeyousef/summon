package code.yousef.summon.components.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.getPlatformRenderer

/**
 * A basic Text component that renders text.
 * This is a simpler version of the Text component with fewer options.
 */
@Composable
fun BasicText(
    text: String,
    modifier: Modifier = Modifier.create()
) {
    // Get the platform renderer
    val renderer = getPlatformRenderer()
    
    // Render the text using the platform renderer
    renderer.renderText(text, modifier)
}

/**
 * Marker annotation for composable functions.
 * This will eventually be replaced by the actual Compose annotation.
 */
// Removed redundant annotation class as we're now using the one from runtime package 