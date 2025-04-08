package code.yousef.summon.components.demo

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.background
import code.yousef.summon.modifier.onClick

/**
 * Example component demonstrating the CompositionLocal pattern.
 * This component shows how to properly use CompositionLocal for composition lifecycle.
 * 
 * @param text The text to display
 * @param modifier Modifier to apply to the component
 * @param onClick Function to call when the component is clicked
 */
@Composable
fun LocalDemoComponent(
    text: String,
    modifier: Modifier = Modifier(),
    onClick: () -> Unit = {}
) {
    // Get the current composer from CompositionLocal
    val composer = CompositionLocal.currentComposer
    
    // Start a composition node
    composer?.startNode()
    
    // Only render if we're in the inserting phase
    if (composer?.inserting == true) {
        // Access the platform renderer
        val renderer = getPlatformRenderer()
        
        // Create a modified modifier with click handler
        val finalModifier = modifier.background("#f0f0f0")
            .onClick("javascript:void(0)") // This would be replaced with a real handler
            
        // Render the component using the platform renderer
        renderer.renderText(text, finalModifier, Any())
    }
    
    // End the composition node
    composer?.endNode()
}

/**
 * Example of a component that uses content slots.
 *
 * @param title The title to display
 * @param modifier Modifier to apply to the component
 * @param content A composable function that provides the content
 */
@Composable
fun LocalDemoContainer(
    title: String,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer
    
    composer?.startNode()
    
    if (composer?.inserting == true) {
        val renderer = getPlatformRenderer()
        renderer.renderBox(modifier)
        
        // Render the title
        renderer.renderText(title, Modifier(), Any())
    }
    
    // Execute the content composable function
    content()
    
    composer?.endNode()
} 