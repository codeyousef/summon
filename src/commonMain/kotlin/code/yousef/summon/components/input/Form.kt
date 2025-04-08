package code.yousef.summon.components.input

import code.yousef.summon.core.Composable
import code.yousef.summon.LayoutComponent
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer

/**
 * A composable that represents an HTML form.
 * It manages form state, validation, and submission.
 *
 * @param content The form content containing input fields and submit buttons
 * @param onSubmit Callback that is invoked when the form is submitted
 * @param modifier The modifier to apply to this composable
 */
class Form(
    val content: List<Composable>,
    val onSubmit: (Map<String, String>) -> Unit = {},
    val modifier: Modifier = Modifier()
) : Composable, LayoutComponent {
    private val formFields = mutableListOf<TextField>()

    /**
     * Registers a TextField with this form.
     * This allows the form to track all input fields for validation and submission.
     */
    fun registerField(field: TextField) {
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
    fun submit(): Boolean {
        if (validate()) {
            // Collect all form values
            val formData = formFields.associate<TextField, String, String> { field ->
                (field.label ?: field.hashCode().toString()) to field.state.value
            }

            // Call the onSubmit callback
            onSubmit(formData)
            return true
        }
        return false
    }

    /**
     * Renders this Form composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderForm(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
} 