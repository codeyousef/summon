package code.yousef.summon

import code.yousef.summon.components.input.TextField
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

/**
 * JS-specific extension function to set up input change handling for TextField.
 */
fun TextField.setupJsInputHandler(fieldId: String) {
    val inputElement = document.getElementById(fieldId) as? HTMLInputElement ?: return
    
    // Set up the input event listener
    inputElement.oninput = { event: Event ->
        val newValue = inputElement.value
        
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
