package code.yousef.summon.components.demo

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.rememberMutableStateOf
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.background
import code.yousef.summon.modifier.onClick
import code.yousef.summon.runtime.PlatformRendererProvider

/**
 * A stateful demo component that shows how to use state in Summon.
 * This component has a counter that increments when clicked.
 */
@Composable
fun StatefulCounter() {
    // Get the current composer from CompositionLocal
    val composer = CompositionLocal.currentComposer
    
    // Create a state variable for the counter
    val count = rememberMutableStateOf(0)
    
    // Start a composition node
    composer?.startNode()
    
    // Only render if we're in the inserting phase
    if (composer?.inserting == true) {
        // Access the platform renderer
        val renderer = getPlatformRenderer()
        
        // Create a click handler that increments the counter
        val onClick = "javascript:void(0)" // This would be replaced with a real handler that calls count.value++
        
        // Prepare the text to display
        val text = "Clicks: ${count.value}"
        
        // Create a modified modifier with click handler and styling
        val modifier = Modifier()
            .background("#e0e0e0")
            .onClick(onClick)
            
        // Render the component
        renderer.renderText(text, modifier, Any())
    }
    
    // End the composition node
    composer?.endNode()
}

/**
 * A demo component that demonstrates how to maintain state across recompositions.
 * This component shows a toggleable element.
 */
@Composable
fun ToggleDemo() {
    // Get the current composer from CompositionLocal
    val composer = CompositionLocal.currentComposer
    
    // Create a state variable for the toggle
    val isToggled = rememberMutableStateOf(false)
    
    // Start a composition node
    composer?.startNode()
    
    // Only render if we're in the inserting phase
    if (composer?.inserting == true) {
        // Access the platform renderer
        val renderer = getPlatformRenderer()
        
        // Create a click handler that toggles the state
        val onClick = "javascript:void(0)" // This would be replaced with a real handler
        
        // Apply different styling based on the toggle state
        val backgroundColor = if (isToggled.value) "#4CAF50" else "#F44336"
        val text = if (isToggled.value) "ON" else "OFF"
        
        // Create a modified modifier with styling
        val modifier = Modifier()
            .background(backgroundColor)
            .onClick(onClick)
            
        // Render the component
        renderer.renderText(text, modifier, Any())
    }
    
    // End the composition node
    composer?.endNode()
} 