package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import code.yousef.summon.validation.Validator

/**
 * A composable that displays a text input field.
 *
 * @param value The current text value
 * @param onValueChange Callback that is invoked when the input value changes
 * @param label Optional label to display for the text field
 * @param placeholder Placeholder text to show when the field is empty
 * @param modifier The modifier to apply to this composable
 * @param type The type of input (text, password, email, etc.)
 * @param isError Whether the input is in an error state
 * @param isEnabled Whether the input is enabled
 * @param isReadOnly Whether the input is read-only
 * @param validators List of validators to apply to the input (optional)
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    label: String? = null,
    placeholder: String? = null,
    type: TextFieldType = TextFieldType.Text,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    validators: List<Validator> = emptyList()
) {
    // Internal state to track validation errors
    val validationErrors = remember { mutableStateOf(emptyList<String>()) }

    // Get the platform renderer
    val renderer = LocalPlatformRenderer.current

    // Render the TextField
    renderer.renderTextField(
        value = value,
        onValueChange = { newValue ->
            // Apply validation and then call the callback
            val errors = validators.mapNotNull { validator ->
                val result = validator.validate(newValue)
                if (!result.isValid) result.errorMessage else null
            }
            validationErrors.value = errors
            onValueChange(newValue)
        },
        modifier = modifier,
        placeholder = placeholder ?: "",
        isError = isError || validationErrors.value.isNotEmpty(),
        type = type.toString().lowercase()
    )

    // Optionally render a label if one is provided
    if (label != null) {
        // Label would be rendered here in a real implementation
    }

    // Optionally render validation errors
    if (validationErrors.value.isNotEmpty()) {
        // Errors would be displayed here in a real implementation
    }
}

/**
 * A stateful version of TextField that manages its own state.
 *
 * @param initialValue The initial text value
 * @param onValueChange Callback that is invoked when the input value changes
 * @param label Optional label to display for the text field
 * @param placeholder Placeholder text to show when the field is empty
 * @param modifier The modifier to apply to this composable
 * @param type The type of input (text, password, email, etc.)
 * @param isError Whether the input is in an error state
 * @param isEnabled Whether the input is enabled
 * @param isReadOnly Whether the input is read-only
 * @param validators List of validators to apply to the input (optional)
 */
@Composable
fun StatefulTextField(
    initialValue: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier(),
    label: String? = null,
    placeholder: String? = null,
    type: TextFieldType = TextFieldType.Text,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    validators: List<Validator> = emptyList()
) {
    // Create state to store the text value
    val textState = remember { mutableStateOf(initialValue) }

    // Use the stateless TextField composable
    TextField(
        value = textState.value,
        onValueChange = { newValue ->
            textState.value = newValue
            onValueChange(newValue)
        },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        type = type,
        isError = isError,
        isEnabled = isEnabled,
        isReadOnly = isReadOnly,
        validators = validators
    )
}

/**
 * Types of text input fields.
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