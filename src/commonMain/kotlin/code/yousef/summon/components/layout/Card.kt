package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider

/**
 * A composable that displays content in a card with elevation, rounded corners, and optional border.
 *
 * @param modifier Modifier to be applied to the card container.
 * @param content The content to be displayed inside the card.
 */
@Composable
fun Card(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer
    // TODO: Apply default card styles (shadow, border-radius, background) to modifier if not overridden
    val finalModifier = modifier // Placeholder

    composer?.startNode() // Start Card node
    if (composer?.inserting == true) {
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        renderer.renderCard(finalModifier) // Render the card container
    }
    
    // Execute the content lambda within the Card's scope
    content()
    
    composer?.endNode() // End Card node
}

// The old Card class and its constructors/methods are removed. 
