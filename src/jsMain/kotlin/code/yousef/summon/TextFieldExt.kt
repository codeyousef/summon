package code.yousef.summon

import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.validation.ValidationResult
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

/**
 * Data class to hold TextField-related extension properties for JS implementation
 */
data class TextFieldJsExtension(
    val state: SummonMutableState<String>,
    val onValueChange: (String) -> Unit,
    val validators: List<(String) -> ValidationResult> = emptyList(),
    val validate: () -> Unit
)

/**
 * JS-specific function to set up input change handling.
 */
fun setupJsInputHandler(fieldId: String, textFieldExt: TextFieldJsExtension) {
    val inputElement = document.getElementById(fieldId) as? HTMLInputElement ?: return
    
    // Set up the input event listener
    inputElement.oninput = { event: Event ->
        val newValue = inputElement.value
        
        // Update the state
        textFieldExt.state.value = newValue
        
        // Call the onValueChange callback
        textFieldExt.onValueChange(newValue)
        
        // Validate if there are validators
        if (textFieldExt.validators.isNotEmpty()) {
            textFieldExt.validate()
        }
        
        // Prevent default to avoid form submission
        event.preventDefault()
    }
}
