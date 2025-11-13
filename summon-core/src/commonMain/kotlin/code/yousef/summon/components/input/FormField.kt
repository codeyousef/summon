package codes.yousef.summon.components.input

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.theme.ColorHelpers
import codes.yousef.summon.theme.Spacer


/**
 * A layout composable that wraps form controls, providing structure for labels,
 * helper text, and error messages.
 *
 * @param modifier Modifier applied to the FormField container.
 * @param label Optional composable lambda for the field's label.
 * @param helperText Optional composable lambda for displaying helper text below the field.
 * @param errorText Optional composable lambda for displaying error text below the field (shown when `isError` is true).
 * @param isError Indicates whether the field is currently in an error state.
 * @param isRequired Indicates whether the field is required (displays a red asterisk after the label).
 * @param fieldContent The composable lambda defining the actual input control (e.g., TextField, Select).
 */
@Composable
fun FormField(
    modifier: Modifier = Modifier(),
    label: @Composable (() -> Unit)? = null,
    helperText: @Composable (() -> Unit)? = null,
    errorText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    isRequired: Boolean = false,
    fieldContent: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer
    val renderer = LocalPlatformRenderer.current

    // Use the new renderFormField method from MigratedPlatformRenderer
    composer?.startNode() // Start FormField node
    if (composer?.inserting == true) {
        renderer.renderFormField(
            modifier = modifier,
            labelId = null, // In the future, we could generate IDs for labels
            isRequired = isRequired,
            isError = isError,
            errorMessageId = null,
            content = {
                // Internal structure
                // Use Column for vertical layout
                Column {
                    // Compose Label if provided
                    if (label != null) {
                        Row {
                            label() // Display the label first

                            // Add required indicator (red asterisk) if field is required
                            if (isRequired) {
                                // Add a small space between label and asterisk
                                Spacer(modifier = Modifier().width("4px"))
                                // Red asterisk indicator
                                Text(
                                    text = "*",
                                    modifier = Modifier().style("color", ColorHelpers.error)
                                )
                            }
                        }
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

                        // Add styling for error text if in error state
                        if (isError && errorText != null) {
                            Column(modifier = Modifier().style("color", ColorHelpers.error)) {
                                bottomText()
                            }
                        } else {
                            // Regular helper text
                            bottomText()
                        }
                    }
                }
            }
        )
    }
    composer?.endNode() // End FormField node
} 
