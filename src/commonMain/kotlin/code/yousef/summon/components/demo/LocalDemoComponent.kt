package code.yousef.summon.components.demo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember

/**
 * A demo component that manages its own state using MutableState.
 * This component demonstrates local state management within a composable function.
 *
 * @param initialValue The initial text value to display
 * @param modifier The modifier to be applied to this demo
 */
@Composable
fun LocalDemoComponent(
    initialValue: String = "Default Text",
    modifier: Modifier = Modifier()
) {
    // Create a local state that will be remembered between recompositions
    val text = remember { mutableStateOf(initialValue) }

    // Render the component with the current state
    LocalDemoComponentImpl(
        text = text.value,
        onTextChange = { text.value = it },
        modifier = modifier
    )
}

/**
 * The stateless implementation of the demo component.
 * This represents the pure UI part without state management logic.
 *
 * @param text The text to display
 * @param onTextChange Callback for when text changes
 * @param modifier The modifier to be applied to this demo
 */
@Composable
private fun LocalDemoComponentImpl(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier()
) {
    // Use standard components
    Box(modifier = modifier) {
        Text(text = "Current Text: $text") // Display the actual text
        // Add an input or button here to use onTextChange if needed
    }
}

/**
 * An alternate version that shows state hoisting pattern.
 * This component accepts state from outside, demonstrating state hoisting.
 *
 * @param text The current text from the parent
 * @param onTextChange Callback to notify the parent about text changes
 * @param modifier The modifier to be applied to this demo
 */
@Composable
fun HoistedDemoComponent(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier()
) {
    // Use standard components
    Box(modifier = modifier) {
        Text(text = "Hoisted Text: $text") // Display the actual text
        // Add an input or button here to use onTextChange if needed
    }
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
    // Use standard components, e.g., Column for layout
    Column(modifier = modifier) {
        Text(text = title, modifier = Modifier()) // Display the title using Text
        // Invoke the content lambda directly within the Column scope
        content()
    }
} 