package code.yousef.summon.components.demo

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.modifier.Modifier
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
    // Use renderBox or another layout component if needed for the modifier
    renderer.renderBox(modifier) {
        // Call the Text composable instead of renderer.renderText
        Text(text = text)
    }
}

/**
 * A stateful demo component that shows how to use state in Summon.
 * This component has a counter that increments when clicked.
 */
@Composable
fun StatefulCounter() {
    val composer = CompositionLocal.currentComposer
    val count: SummonMutableState<Int> = rememberMutableStateOf(0)

    composer?.startNode()
    if (composer?.inserting == true) {
        // **Important:** Click handling needs proper JS bridge or use Button composable.
        // This onClick string won't work as intended with Text.
        // val onClickJs = "javascript:void(0)" 
        // val clickModifier = Modifier().onClick(onClickJs)

        // Call the Text composable directly
        Text(
            text = "Clicks: ${count.value}",
            modifier = Modifier() // Add styling or click handler modifier here if needed
            // modifier = clickModifier // If using a JS-based click handler
        )
        // Placeholder Button to actually increment the counter
        Button(label = "Increment", onClick = { count.value++ })
    }
    composer?.endNode()
}

/**
 * A demo component that demonstrates how to maintain state across recompositions.
 * This component shows a toggleable element.
 */
@Composable
fun ToggleDemo() {
    val composer = CompositionLocal.currentComposer
    val isToggled: SummonMutableState<Boolean> = rememberMutableStateOf(false)

    composer?.startNode()
    if (composer?.inserting == true) {
        // val onClickJs = "javascript:void(0)" 
        val backgroundColor = if (isToggled.value) "#4CAF50" else "#F44336"
        val text = if (isToggled.value) "ON" else "OFF"

        // Apply modifier for background color and potentially click handler
        val textModifier = Modifier()
        // Example: .background(backgroundColor) // Assuming background modifier exists
        // Example: .onClick(onClickJs)

        // Call the Text composable
        Text(
            text = text,
            modifier = textModifier
        )
        // Placeholder Button to toggle
        Button(label = "Toggle", onClick = { isToggled.value = !isToggled.value })
    }
    composer?.endNode()
} 