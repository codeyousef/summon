package codes.yousef.summon.components.input

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.attribute
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.getPlatformRenderer
import codes.yousef.summon.runtime.mutableStateOf
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.runtime.compositionGroup
import codes.yousef.summon.validation.Validator
import kotlin.js.JsName

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
@JsName("TextField")
/**
 * Renders text field.
 *
 * @param value Value to process.
 * @param onValueChange Callback invoked when value change.
 * @param modifier Styles and attributes applied to the rendered element.
 * @param label The label value.
 * @param placeholder The placeholder value.
 * @param type The type value.
 * @param isError The is error value.
 * @param isEnabled The is enabled value.
 * @param isReadOnly The is read only value.
 * @param validators The validators value.
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
) = compositionGroup("Summon.TextField") {
    val renderer = LocalPlatformRenderer.current
    val validationErrors = remember { mutableStateOf(emptyList<String>()) }


    // Add attributes to the modifier based on parameters
    var finalModifier = modifier
    if (label != null && "aria-label" !in finalModifier.attributes) {
        finalModifier = finalModifier.attribute("aria-label", label)
    }
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

    // Create a wrapper callback that avoids capturing mutable state directly
    // This helps prevent issues with JS minification mangling state access
    @JsName("handleValueChange")
    fun handleValueChange(newValue: String) {
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
    }

    // Call the available renderer function
    renderer.renderTextField(
        value = value,
        onValueChange = ::handleValueChange,
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
@JsName("StatefulTextField")
/**
 * Renders stateful text field.
 *
 * @param initialValue The initial value value.
 * @param onValueChange Callback invoked when value change.
 * @param modifier Styles and attributes applied to the rendered element.
 * @param label The label value.
 * @param placeholder The placeholder value.
 * @param type The type value.
 * @param isError The is error value.
 * @param isEnabled The is enabled value.
 * @param isReadOnly The is read only value.
 * @param validators The validators value.
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
) = compositionGroup("Summon.StatefulTextField") {
    // Create state to store the text value
    val textState = remember { mutableStateOf(initialValue) }

    // Create a wrapper callback to avoid direct state capture in lambda
    @JsName("handleValueChange")
    fun handleValueChange(newValue: String) {
        textState.value = newValue
        onValueChange(newValue)
    }

    // Use the stateless TextField composable
    TextField(
        value = textState.value,
        onValueChange = ::handleValueChange,
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
 * A minimal text input field without validation state.
 *
 * This is a simplified variant that directly passes through to the renderer
 * without any internal state management. It's safe for JS minification scenarios
 * where state capture in callbacks can cause issues.
 *
 * @param value The current text value
 * @param onValueChange Callback that is invoked when the input value changes
 * @param modifier The modifier to apply to this composable
 * @param placeholder Placeholder text to show when the field is empty
 * @param type The type of input (text, password, email, etc.)
 */
@JsName("BasicTextField")
/**
 * Renders basic text field.
 *
 * @param value Value to process.
 * @param onValueChange Callback invoked when value change.
 * @param modifier Styles and attributes applied to the rendered element.
 * @param placeholder The placeholder value.
 * @param type The type value.
 */
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    placeholder: String? = null,
    type: String = "text"
) {
    // Use getPlatformRenderer() for more reliable access in minified JS contexts
    val renderer = getPlatformRenderer()

    // Build modifier with placeholder if provided
    val finalModifier = if (placeholder != null) {
        modifier.attribute("placeholder", placeholder)
    } else {
        modifier
    }

    // Direct pass-through to renderer - no remembered state, no validation
    renderer.renderTextField(value, onValueChange, finalModifier, type)
}

/**
 * Types of text input fields.
 */
@JsName("TextFieldType")
/** Supported text field type values. */
enum class TextFieldType {
    /** The text text field type option. */
    Text,
    /** The password text field type option. */
    Password,
    /** The email text field type option. */
    Email,
    /** The number text field type option. */
    Number,
    /** The tel text field type option. */
    Tel,
    /** The URL text field type option. */
    Url,
    /** The search text field type option. */
    Search,
    /** The date text field type option. */
    Date,
    /** The time text field type option. */
    Time
}
