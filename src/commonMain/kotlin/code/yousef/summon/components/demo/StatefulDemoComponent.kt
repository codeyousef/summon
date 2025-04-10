package code.yousef.summon.components.demo

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.onClick
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.rememberMutableStateOf
import code.yousef.summon.state.SummonMutableState

/**
 * A demo component that demonstrates state management within a class.
 * This approach allows for more complex state management patterns.
 *
 * @param initialValue The initial text value to display
 * @param modifier The modifier to be applied to this demo
 */
@Composable
fun StatefulDemoComponent(
    initialValue: String = "Default Text",
    modifier: Modifier = Modifier()
) {
    // Create an instance of our stateful component implementation
    val component = StatefulDemoComponentImpl(initialValue)

    // Render using the current state
    StatefulDemoComponentView(
        text = component.text,
        onTextChange = { component.updateText(it) },
        modifier = modifier
    )
}

/**
 * Internal implementation of the stateful component.
 */
private class StatefulDemoComponentImpl(initialValue: String) {
    // Internal mutable state
    var text: String = initialValue
        private set

    // Method to update the state
    fun updateText(newText: String) {
        text = newText
    }
}

/**
 * The stateless view part of the component.
 * This represents the pure UI part without state management logic.
 *
 * @param text The text to display
 * @param onTextChange Callback for when text changes
 * @param modifier The modifier to be applied to this demo
 */
@Composable
private fun StatefulDemoComponentView(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    // Instead of calling a non-existent renderStatefulDemoComponent method
    renderer.renderBox(modifier) {
        // Use basic rendering methods that exist in MigratedPlatformRenderer
        renderer.renderText(Modifier()) {
            // Render the text content
        }
    }
}

/**
 * A stateful demo component that shows how to use state in Summon.
 * This component has a counter that increments when clicked.
 */
@Composable
fun StatefulCounter() {
    // Get the current composer from CompositionLocal
    val composer = CompositionLocal.currentComposer

    // Create a state variable for the counter
    val count: SummonMutableState<Int> = rememberMutableStateOf(0)

    // Start a composition node
    composer?.startNode()

    // Only render if we're in the inserting phase
    if (composer?.inserting == true) {
        // Access the platform renderer
        val renderer = LocalPlatformRenderer.current

        // Create a click handler that increments the counter
        val onClick = "javascript:void(0)" // This would be replaced with a real handler that calls count.value++

        // Prepare the text to display
        val text = "Clicks: ${count.value}"

        // Create a modified modifier with click handler and styling
        val modifier = Modifier()
            .onClick(onClick)

        // Render the component
        renderer.renderText(modifier) {
            // Render the text content
        }
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
    val isToggled: SummonMutableState<Boolean> = rememberMutableStateOf(false)

    // Start a composition node
    composer?.startNode()

    // Only render if we're in the inserting phase
    if (composer?.inserting == true) {
        // Access the platform renderer
        val renderer = LocalPlatformRenderer.current

        // Create a click handler that toggles the state
        val onClick = "javascript:void(0)" // This would be replaced with a real handler

        // Apply different styling based on the toggle state
        val backgroundColor = if (isToggled.value) "#4CAF50" else "#F44336"
        val text = if (isToggled.value) "ON" else "OFF"

        // Create a modified modifier with styling
        val modifier = Modifier()
            .onClick(onClick)

        // Render the component
        renderer.renderText(modifier) {
            // Render the text content
        }
    }

    // End the composition node
    composer?.endNode()
} 