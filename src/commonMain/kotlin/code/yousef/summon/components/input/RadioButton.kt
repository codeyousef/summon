package code.yousef.summon.components.input

import code.yousef.summon.components.layout.Alignment
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.onClick
import code.yousef.summon.modifier.ModifierExtras.pointerEvents
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.theme.Spacer
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.padding
import code.yousef.summon.modifier.alignItems
import code.yousef.summon.modifier.AlignItems
import code.yousef.summon.modifier.cursor
import code.yousef.summon.components.layout.Box
import code.yousef.summon.modifier.EventModifiers.onClick

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
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }

    composer?.startNode() // Start RadioButton node
    if (composer?.inserting == true) {
        val renderer = LocalPlatformRenderer.current
        // Call the PlatformRenderer implementation which has params in order: selected, onClick, enabled, modifier
        renderer.renderRadioButton(
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
    val rowModifier = modifier
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }
        .onClick { if (enabled) onClick() }
        .alignItems(AlignItems.Center) // Corrected to AlignItems.Center

    Row(modifier = rowModifier) { // Remove verticalAlignment parameter
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled
        )
        // Fix padding and add empty content lambda for Box
        Box(modifier = Modifier().padding("0px 0px 0px 8px")) {}
        label()
    }
}

/**
 * A composable function that displays a radio button, typically used within a RadioButtonGroup.
 *
 * @param selected Whether this radio button is currently selected.
 * @param onClick Lambda invoked when the radio button is clicked.
 * @param modifier Modifier applied to the entire radio button component (including label).
 * @param enabled Controls the enabled state of the radio button.
 * @param label Optional label displayed next to the radio button.
 * @param labelPosition Determines whether the label appears before or after the radio button.
 * @param radioButtonStyle Modifier applied specifically to the radio button input element.
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    labelPosition: LabelPosition = LabelPosition.END,
    radioButtonStyle: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current

    // Define the radio button rendering logic
    val renderRadioInput = @Composable { ->
        renderer.renderRadioButton(
            selected = selected,
            onClick = { if (enabled) onClick() },
            enabled = enabled,
            modifier = radioButtonStyle
                .cursor(if (enabled) "pointer" else "default")
        )
    }

    // Define the label rendering logic if a label exists
    val renderLabel = label?.let {
        @Composable { ->
            Text(text = it)
        }
    }

    // Arrange radio button and label in a Row
    Row(
        modifier = modifier
            .cursor(if (enabled) "pointer" else "default")
            .onClick { if (enabled) onClick() } // Make row clickable
            .alignItems(AlignItems.Center), // Corrected to AlignItems.Center
    ) {
        if (labelPosition == LabelPosition.START && renderLabel != null) {
            renderLabel()
            // Add empty content lambda for Box
            Box(Modifier().padding("0px 0px 0px 4px")) {}
        }

        renderRadioInput()

        if (labelPosition == LabelPosition.END && renderLabel != null) {
             // Add empty content lambda for Box
            Box(Modifier().padding("0px 0px 0px 4px")) {}
            renderLabel()
        }
    }
}

/**
 * Defines the position of the label relative to the input component.
 */
enum class LabelPosition {
    START, // Label appears before the input
    END    // Label appears after the input
}

// Removed RadioButtonSamples as it had renderSpacer issue
// Consider adding samples back later if needed