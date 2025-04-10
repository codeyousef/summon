package code.yousef.summon.components.input

import code.yousef.summon.components.layout.Alignment
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.onClick
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.theme.Spacer

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
    val composer = CompositionLocal.currentComposer
    // Apply styling directly to the element via modifier
    val finalModifier = modifier
        .apply {
            if (enabled) this else this.copy(styles = this.styles + ("opacity" to "0.6"))
        }
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }

    composer?.startNode() // Start RadioButton node
    if (composer?.inserting == true) {
        val renderer = LocalPlatformRenderer.current
        // Call the PlatformRenderer implementation which has params in order: selected, onClick, enabled, modifier
        (renderer as code.yousef.summon.core.PlatformRenderer).renderRadioButton(
            selected = selected,
            onClick = { if (enabled) onClick() },
            enabled = enabled,
            modifier = finalModifier
        )
    }
    composer?.endNode() // End RadioButton node (self-closing)
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
    // Implement clickable behavior on the Row
    val rowModifier = modifier
        .cursor(if (enabled) "pointer" else "default")
        .apply {
            if (enabled) this else this.copy(styles = this.styles + ("opacity" to "0.6"))
        }
        .applyIf(!enabled) { pointerEvents("none") }
        .onClick { if (enabled) onClick() } // Add onClick handler to make the entire row clickable

    Row(modifier = rowModifier) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled
        )
        Spacer(modifier = Modifier().width("8px"))
        label()
    }
}

/**
 * A RadioButton with a label.
 * 
 * @param selected Whether the radio button is selected
 * @param onClick Callback to be invoked when the radio button is clicked
 * @param label The label text to display next to the radio button
 * @param enabled Whether the radio button is enabled
 * @param modifier The modifier to be applied to this component
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    
    // Create a row to hold the radio button and label
    renderer.renderRow(modifier)
    
    // Render the radio button
    RadioButton(
        selected = selected,
        onClick = onClick,
        enabled = enabled
    )
    
    // Render the label text
    renderer.renderText(label, Modifier())
}

// Utility extension
private fun Modifier.cursor(cursor: String): Modifier {
    return this.copy(styles = this.styles + ("cursor" to cursor))
}

// For Spacer
@Composable
private fun Spacer(modifier: Modifier) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderSpacer(modifier)
}

// Width extension for Modifier
private fun Modifier.width(width: String): Modifier {
    return this.copy(styles = this.styles + ("width" to width))
}