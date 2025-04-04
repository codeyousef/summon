package com.summon

import kotlinx.html.TagConsumer

/**
 * A composable that displays a text input field.
 * @param state The state that holds the current value of the text field
 * @param onValueChange Callback that is invoked when the input value changes
 * @param label Optional label to display for the text field
 * @param placeholder Placeholder text to show when the field is empty
 * @param modifier The modifier to apply to this composable
 * @param type The type of input (text, password, email, etc.)
 * @param validators List of validators to apply to the input
 */
class TextField(
    val state: MutableState<String>,
    val onValueChange: (String) -> Unit = {},
    val label: String? = null,
    val placeholder: String? = null,
    val modifier: Modifier = Modifier(),
    val type: TextFieldType = TextFieldType.Text,
    val validators: List<Validator> = emptyList()
) : Composable {
    // Internal state to track validation errors
    private val validationErrors = mutableStateOf<List<String>>(emptyList())
    
    /**
     * Renders this TextField composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderTextField(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
    
    /**
     * Validates the current input value against all validators.
     * @return True if validation passed, false otherwise
     */
    fun validate(): Boolean {
        val errors = validators.mapNotNull { validator ->
            if (!validator.validate(state.value)) validator.errorMessage else null
        }
        validationErrors.value = errors
        return errors.isEmpty()
    }
    
    /**
     * Gets the current validation errors.
     */
    fun getValidationErrors(): List<String> = validationErrors.value
    
    /**
     * Indicates whether the field is currently valid.
     */
    fun isValid(): Boolean = validationErrors.value.isEmpty()
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