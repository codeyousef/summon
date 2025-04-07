package code.yousef.summon.components.layout

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider


/**
 * A layout composable that places its children in a grid.
 *
 * @param modifier Modifier to apply to the grid container. Should typically include grid-template-columns/rows/areas.
 * @param content The composable content to display within the grid cells.
 */
@Composable
fun Grid(
    modifier: Modifier = Modifier(), 
    content: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer
    // TODO: Add Modifier properties for grid settings if not handled by renderer
    val finalModifier = modifier // Placeholder

    composer?.startNode() // Start Grid node
    if (composer?.inserting == true) {
        // TODO: Replace getPlatformRenderer with CompositionLocal access? Seems redundant now.
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        // Apply display:grid by default if not specified in modifier?
        // For now, assume modifier configures the grid layout (columns, rows, gap, etc.)
        renderer.renderGrid(modifier = finalModifier)
    }
    
    // Render the content within the grid container
    content()
    
    composer?.endNode() // End Grid node
} 
