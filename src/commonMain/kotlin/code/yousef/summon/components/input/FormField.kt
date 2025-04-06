package code.yousef.summon.components.input

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
// Layout imports
import code.yousef.summon.components.layout.Column 
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.modifier.height // Keep height (assuming it's in Modifier.kt or LayoutModifiers.kt)
// Modifier imports
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.modifier.border // Keep border (assuming it's in Modifier.kt or StylingModifiers.kt)

/**
 * A layout composable that wraps form controls, providing structure for labels,
 * helper text, and error messages.
 *
 * @param modifier Modifier applied to the FormField container.
 * @param label Optional composable lambda for the field's label.
 * @param helperText Optional composable lambda for displaying helper text below the field.
 * @param errorText Optional composable lambda for displaying error text below the field (shown when `isError` is true).
 * @param isError Indicates whether the field is currently in an error state.
 * @param isRequired Indicates whether the field is required (can be used for visual indicators like an asterisk).
 * @param fieldContent The composable lambda defining the actual input control (e.g., TextField, Select).
 */
@Composable
fun FormField(
    modifier: Modifier = Modifier(),
    label: @Composable (() -> Unit)? = null,
    helperText: @Composable (() -> Unit)? = null,
    errorText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    isRequired: Boolean = false, // TODO: Add visual indicator if true?
    fieldContent: @Composable () -> Unit
) {
    // TODO: Replace PlatformRendererProvider with CompositionLocal access
    val renderer = PlatformRendererProvider.getRenderer()

    // TODO: Renderer signature update required.
    // The renderer call might become simpler or be removed if Column handles the container.
    // For now, keep it but it might be redundant or only apply attributes.
    // renderer.renderFormField(modifier = modifier, label = "", isError = isError, isRequired = isRequired)

    // Use Column for vertical layout
    Column(modifier = modifier) { // Apply the main modifier to the Column
        // Compose Label if provided
        if (label != null) {
            label() // TODO: Add styling? Associate with input via 'for' attribute?
            // Add small space between label and field
            Spacer(modifier = Modifier().height("4px")) 
        }

        // Compose the actual input field
        fieldContent()

        // Compose Helper Text or Error Text (priority to error)
        val bottomText = if (isError && errorText != null) errorText else helperText
        if (bottomText != null) {
            // Add small space between field and helper/error text
            Spacer(modifier = Modifier().height("4px")) 
            // TODO: Add specific styling for helper/error text?
            bottomText()
        }
    }
} 