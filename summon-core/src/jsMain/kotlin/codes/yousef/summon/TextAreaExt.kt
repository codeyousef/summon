package codes.yousef.summon

import codes.yousef.summon.state.SummonMutableState
import codes.yousef.summon.validation.ValidationResult
import kotlinx.browser.document
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event

/**
 * Data class to hold TextArea-related extension properties for JS implementation
 */
data class TextAreaJsExtension(
    val state: SummonMutableState<String>,
    val onValueChange: (String) -> Unit,
    val validators: List<(String) -> ValidationResult> = emptyList(),
    val validate: () -> Unit
)

/**
 * JS-specific function to set up textarea change handling.
 */
fun setupJsTextAreaHandler(fieldId: String, textAreaExt: TextAreaJsExtension) {
    val textareaElement = document.getElementById(fieldId) as? HTMLTextAreaElement ?: return

    // Set up the input event listener
    textareaElement.oninput = { event: Event ->
        val newValue = textareaElement.value

        // Update the state
        textAreaExt.state.value = newValue

        // Call the onValueChange callback
        textAreaExt.onValueChange(newValue)

        // Validate if there are validators
        if (textAreaExt.validators.isNotEmpty()) {
            textAreaExt.validate()
        }

        // Prevent default to avoid form submission
        event.preventDefault()
    }
} 
