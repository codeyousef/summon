package code.yousef.summon

import code.yousef.summon.components.input.TextArea
import kotlinx.browser.document
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event

/**
 * JS-specific extension function to set up textarea change handling for TextArea.
 */
fun TextArea.setupJsTextAreaHandler(fieldId: String) {
    val textareaElement = document.getElementById(fieldId) as? HTMLTextAreaElement ?: return

    // Set up the input event listener
    textareaElement.oninput = { event: Event ->
        val newValue = textareaElement.value

        // Update the state
        state.value = newValue

        // Call the onValueChange callback
        onValueChange(newValue)

        // Validate if there are validators
        if (validators.isNotEmpty()) {
            validate()
        }

        // Prevent default to avoid form submission
        event.preventDefault()
    }
}

/**
 * JS-specific extension function to set up textarea change handling.
 * This function is called from the JsPlatformRenderer.
 */
fun setupJsTextAreaHandler(fieldId: String, textArea: TextArea) {
    textArea.setupJsTextAreaHandler(fieldId)
} 
