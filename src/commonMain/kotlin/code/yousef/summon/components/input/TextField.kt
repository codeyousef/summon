package code.yousef.summon.components.input

import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.modifier.border
import code.yousef.summon.modifier.padding
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Alignment

/**
 * Types of text input fields (kept from original).
 */
enum class TextFieldType {
    Text,
    Password,
    Email,
    Number,
    Tel,
    Url,
    Search,
    Date,
    Time
}

/**
 * A composable that displays a text input field, allowing users to enter text.
 * This is a controlled component; its value is controlled by the `value` parameter
 * and updates are communicated via the `onValueChange` callback.
 *
 * @param value The current text value to display.
 * @param onValueChange Callback invoked when the user changes the text value.
 * @param modifier Modifier applied to the text field container.
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param readOnly Controls the read-only state. When `true`, the value cannot be modified.
 * @param label Optional label displayed typically above or beside the text field.
 * @param placeholder Optional placeholder text displayed when the field is empty.
 * @param leadingIcon Optional composable displayed at the start of the text field.
 * @param trailingIcon Optional composable displayed at the end of the text field.
 * @param isError Indicates if the text field's current value is invalid.
 * @param type The type of input (text, password, email, etc.), influences keyboard and semantics.
 * // TODO: Add parameters for visual variations (e.g., outlined, filled), keyboard options, ime actions.
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    type: TextFieldType = TextFieldType.Text
    // Removed validators - validation should be handled externally based on state.
) {
    val isDisabled = !enabled || readOnly
    
    // Base modifier without layout specifics for the input itself
    val baseInputModifier = Modifier()
        .opacity(if (enabled) 1f else 0.6f) 
        .cursor(if (isDisabled) "default" else "text") 
        .applyIf(isDisabled) { pointerEvents("none") } 
        .applyIf(isError) { border("1px", "solid", "#D32F2F") /* Red color */ }

    // Calculate padding for the input based on icons
    // These are example values, might need refinement
    val startPadding = if (leadingIcon != null) "36px" else "12px" 
    val endPadding = if (trailingIcon != null) "36px" else "12px" 
    val topPadding = "8px" // Example
    val bottomPadding = "8px" // Example

    // Modifier for the actual input element, includes base styles + padding
    val inputModifierWithPadding = baseInputModifier
        // Use positional padding arguments
        .padding(topPadding, endPadding, bottomPadding, startPadding) 
        // TODO: Add other necessary input-specific styles (height, background?)?
        .fillMaxWidth() // Make input fill width within the box
        .height("40px") // Example fixed height
        .background("#FFFFFF") // Example background
        .borderRadius("4px") // Example border radius

    // Use the original modifier passed by the caller for the outer Box container
    Box(modifier = modifier) {
        // Render the input field itself
        val renderer = getPlatformRenderer()
        renderer.renderTextField(
            value = value,
            onValueChange = { if (enabled && !readOnly) onValueChange(it) },
            label = "", // Label handled via composition/overlay if needed
            modifier = inputModifierWithPadding, // Apply padding and styles here
            placeholder = placeholder,
            type = type,
            enabled = enabled,
            readOnly = readOnly,
            isError = isError
        )

        // Position leading icon inside the Box
        if (leadingIcon != null) {
            Box(
                modifier = Modifier // Removed align modifier
                    .padding("0px", "0px", "0px", "8px") // Example padding (Positional)
            ) {
                leadingIcon()
            }
        }

        // Position trailing icon inside the Box
        if (trailingIcon != null) {
            Box(
                modifier = Modifier // Removed align modifier
                     .padding("0px", "8px", "0px", "0px") // Example padding (Positional)
           ) {
                trailingIcon()
            }
        }

        // TODO: Implement Label rendering (e.g., floating label requires more state/logic)
        // Basic label example (rendered inside the box, might overlap):
        // label?.let { Text(it, modifier = Modifier.padding(start="8px")) }
    }
}

// The old TextField class and its methods (compose, validate, etc.) are removed. 