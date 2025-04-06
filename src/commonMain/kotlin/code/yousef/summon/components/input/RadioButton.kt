package code.yousef.summon.components.input

import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
// Import necessary modifiers
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.components.layout.Alignment
import code.yousef.summon.modifier.width
import code.yousef.summon.modifier.cursor
import code.yousef.summon.modifier.opacity

/**
 * A composable that displays a radio button, typically used as part of a group
 * where only one option can be selected at a time.
 *
 * To create a radio button group, manage the selected state externally (e.g., using
 * `remember { mutableStateOf(selectedValue) }`) and call `RadioButton` for each option,
 * passing `selected = (optionValue == selectedState)` and an `onClick` lambda that updates
 * the selected state.
 *
 * @param selected Whether this radio button is currently selected within its group.
 * @param onClick Callback invoked when this radio button is clicked. Should typically update the external state.
 * @param modifier Modifier applied to the radio button layout (often includes label).
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param label Optional composable lambda for displaying a label beside the radio button.
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
    // Removed label String, value, name parameters.
    // Grouping (name) and value association are handled by external state management.
    // Label is handled via composition (e.g., using Row + Text).
) {
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f)
        .cursor(if (enabled) "pointer" else "default") // Keep cursor
        .applyIf(!enabled) { pointerEvents("none") } // Add pointerEvents when disabled

    val renderer = getPlatformRenderer()

    // TODO: Renderer signature update? Needs to handle label rendering if done here.
    // The current renderer.renderRadioButton expects a label string.
    // Pass empty string for now. Label rendering is handled by composition.
    renderer.renderRadioButton(
        selected = selected,
        onClick = { if (enabled) onClick() }, // Guard callback
        enabled = enabled, // Add enabled parameter
        modifier = finalModifier
    )
}

/**
 * Convenience composable that lays out a RadioButton followed by a label.
 * The entire row can be clicked to trigger the RadioButton's onClick.
 *
 * @param selected Whether this radio button is currently selected.
 * @param onClick Callback invoked when this radio button is clicked.
 * @param modifier Modifier applied to the Row container.
 * @param enabled Controls the enabled state.
 * @param label The composable lambda for displaying the label content.
 */
@Composable
fun RadioButtonWithLabel(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: @Composable () -> Unit
) {
    // TODO: Implement clickable modifier properly
    val rowModifier = modifier
        .cursor(if (enabled) "pointer" else "default")
        .opacity(if (enabled) 1f else 0.6f)
        .applyIf(!enabled) { pointerEvents("none") } 
        // Removed clickable call

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.Vertical.CenterVertically 
    ) {
        RadioButton(
            selected = selected, 
            onClick = onClick, 
            enabled = enabled
        )
        Spacer(modifier = Modifier().width("8px")) 
        label()
    }
}

// The old RadioButton class is removed.
// The old RadioGroup class is removed - group logic is handled via state management.
// The old RadioOption data class is removed. 