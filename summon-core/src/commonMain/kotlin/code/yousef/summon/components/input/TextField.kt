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
    val renderer = LocalPlatformRenderer.current
    val validationErrors = remember { mutableStateOf(emptyList<String>()) }

    // Generate stable unique ID based on TextField's stable properties (not random)
    // Use properties that don't change across recompositions: placeholder, type, position
    val stableProps = listOf(
        placeholder ?: "no-placeholder",
        type.name,
        label ?: "no-label",
        isError.toString(),
        isEnabled.toString(),
        isReadOnly.toString()
    ).joinToString("-")
    val uniqueId = "textfield-" + stableProps.hashCode().toString().replace("-", "")

    // Add attributes to the modifier based on parameters
    var finalModifier = modifier
        .attribute("data-summon-id", uniqueId)
    placeholder?.let { finalModifier = finalModifier.attribute("placeholder", it) }
    if (isError || validationErrors.value.isNotEmpty()) {
        finalModifier = finalModifier.attribute("aria-invalid", "true")
        // Consider adding an error class: finalModifier = finalModifier.addClass("error")
    }
    if (!isEnabled) {
        finalModifier = finalModifier.attribute("disabled", "true")
        // Consider opacity/cursor changes: finalModifier = finalModifier.opacity(0.6f).cursor("default")
    }
    if (isReadOnly) {
        finalModifier = finalModifier.attribute("readonly", "true")
    }

    // Call the available renderer function
    renderer.renderTextField(
        value = value,
        onValueChange = { newValue ->
            // Perform validation before calling the external callback
            val errors = validators.mapNotNull { validator ->
                val result = validator.validate(newValue)
                if (!result.isValid) result.errorMessage else null
            }
            validationErrors.value = errors
            // Only call the external onValueChange if enabled
            if (isEnabled) {
                onValueChange(newValue)
            }
        },
        modifier = finalModifier,
        type = type.name.lowercase() // Convert enum to lowercase string for HTML type
    )

    // Removed rendering logic for label and errors, should be handled externally (e.g., FormField)
    // if (label != null) { ... }
    // if (validationErrors.value.isNotEmpty()) { ... }
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