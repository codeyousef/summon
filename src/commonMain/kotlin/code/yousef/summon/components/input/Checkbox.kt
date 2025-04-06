package code.yousef.summon.components.input

import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.components.layout.Alignment
import code.yousef.summon.modifier.width
import code.yousef.summon.modifier.cursor
import code.yousef.summon.modifier.opacity

/**
 * A composable that displays a checkbox, allowing users to select a boolean state.
 *
 * @param checked Whether the checkbox is currently checked.
 * @param onCheckedChange Callback invoked when the checked state changes due to user interaction.
 * @param modifier Modifier applied to the checkbox layout (often includes label).
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param label Optional composable lambda for displaying a label beside the checkbox.
 * // TODO: Add support for indeterminate state.
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
    // Removed label String param - use a trailing lambda or Row for label composable
    // Removed validators
    // Removed isIndeterminate for now
) {
    // TODO: Layout checkbox and label if provided (e.g., using Row)
    // TODO: Apply styles based on checked/enabled state
    
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f)
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }

    val renderer = getPlatformRenderer()

    // TODO: Renderer signature update? Needs to handle label rendering if done here.
    // The current renderer.renderCheckbox expects a label string.
    // For now, pass an empty label. Label rendering should be handled by composition.
    renderer.renderCheckbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = finalModifier,
        enabled = enabled
    )
}

/**
 * Convenience composable that lays out a Checkbox followed by a label.
 * The entire row can be clicked to toggle the checkbox state.
 *
 * @param checked Whether the checkbox is currently checked.
 * @param onCheckedChange Callback invoked when the checked state changes.
 * @param modifier Modifier applied to the Row container.
 * @param enabled Controls the enabled state.
 * @param label The composable lambda for displaying the label content.
 */
@Composable
fun CheckboxWithLabel(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: @Composable () -> Unit
) {
    // TODO: Implement clickable modifier properly
    val rowModifier = modifier
        .cursor(if (enabled) "pointer" else "default")
        .opacity(if (enabled) 1f else 0.6f)
        .applyIf(!enabled) { pointerEvents("none") }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.Vertical.CenterVertically // Use imported Alignment
    ) {
        // Checkbox itself - Pass all relevant parameters
        Checkbox(
            checked = checked, 
            onCheckedChange = onCheckedChange, 
            enabled = enabled
            // modifier = Modifier() // Apply specific modifier to Checkbox if needed
        )
        
        // Spacer between checkbox and label
        Spacer(modifier = Modifier().width("8px")) // Use width modifier
        
        // Label content
        label()
    }
}

// The old Checkbox class and its methods are removed. 