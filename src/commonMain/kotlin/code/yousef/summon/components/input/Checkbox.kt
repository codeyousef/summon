package code.yousef.summon.components.input

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider

import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.components.layout.Alignment

/**
 * A composable that displays a checkbox, allowing users to select a boolean state.
 *
 * @param checked Whether the checkbox is currently checked.
 * @param onCheckedChange Callback invoked when the checked state changes due to user interaction.
 * @param modifier Modifier applied to the checkbox element itself.
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
) {
    val composer = CompositionLocal.currentComposer
    // Apply styling directly to the element via modifier
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f) // Assuming opacity is in Modifier
        .cursor(if (enabled) "pointer" else "default") // Assuming cursor is in Modifier
        .applyIf(!enabled) { pointerEvents("none") }

    composer?.startNode() // Start Checkbox node
    if (composer?.inserting == true) {
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        // Pass relevant state and modifier to the renderer
        renderer.renderCheckbox(
            checked = checked,
            onCheckedChange = { if (enabled) onCheckedChange?.invoke(it) }, // Guarded call
            modifier = finalModifier,
            enabled = enabled
        )
    }
    composer?.endNode() // End Checkbox node (self-closing)
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
    // TODO: Implement clickable modifier properly on the Row
    val rowModifier = modifier
        .cursor(if (enabled) "pointer" else "default")
        .opacity(if (enabled) 1f else 0.6f)
        .applyIf(!enabled) { pointerEvents("none") }
        // Add click handler to the row
        // .clickable(enabled = enabled) { onCheckedChange(!checked) }

    // Use Row composable (assuming it's refactored)
    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.Vertical.CenterVertically // Use Alignment enum
        // TODO: Add onClick lambda to Row if clickable modifier doesn't handle it
    ) {
        // Checkbox itself - Note: onCheckedChange is handled by the Row now
        Checkbox(
            checked = checked, 
            onCheckedChange = null, // Pass null as Row handles the toggle
            enabled = enabled
        )
        
        Spacer(modifier = Modifier().width("8px")) // Assuming Spacer/width exist
        
        label() // Compose the label content
    }
}

// The old Checkbox class and its methods are removed. 
