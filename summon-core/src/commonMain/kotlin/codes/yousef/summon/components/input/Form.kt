package codes.yousef.summon.components.input

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.getPlatformRenderer

/**
 * Base interface for form fields
 */
interface FormField {
    val label: String?
    val value: String
    fun validate(): Boolean
}

/**
 * Class that manages form state, validation, and submission.
 */
class FormState {
    private val formFields = mutableListOf<FormField>()

    /**
     * Registers a form field with this form.
     * This allows the form to track all input fields for validation and submission.
     */
    fun registerField(field: FormField) {
        formFields.add(field)
    }

    /**
     * Validates all form fields.
     * @return True if all fields are valid, false otherwise
     */
    fun validate(): Boolean {
        return formFields.all { it.validate() }
    }

    /**
     * Submits the form if validation passes.
     * @return True if form was submitted, false if validation failed
     */
    fun submit(onSubmit: (Map<String, String>) -> Unit): Boolean {
        if (validate()) {
            // Collect all form values
            val formData = formFields.associate { field ->
                (field.label ?: field.hashCode().toString()) to field.value
            }

            // Call the onSubmit callback
            onSubmit(formData)
            return true
        }
        return false
    }
}

/**
 * A composable that represents an HTML form.
 * It manages form state, validation, and submission.
 *
 * @param onSubmit Callback that is invoked when the form is submitted
 * @param modifier The modifier to apply to this composable
 * @param content The form content containing input fields and submit buttons
 */
@Composable
fun Form(
    onSubmit: (Map<String, String>) -> Unit = {},
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
) {
    val formState = FormState()

    getPlatformRenderer().renderForm(
        onSubmit = { formState.submit(onSubmit) },
        modifier = modifier,
        content = content
    )
}
