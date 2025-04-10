package code.yousef.summon

import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

/**
 * JS-specific function to set up checkbox change handling.
 * 
 * @param checkboxId The ID of the checkbox element in the DOM
 * @param checked The current checked state of the checkbox
 * @param onCheckedChange Callback that is invoked when the checkbox state changes
 * @param isIndeterminate Whether the checkbox should be in an indeterminate state
 * @param validateAndUpdate An optional function that performs validation and updates UI
 */
fun setupJsCheckboxHandler(
    checkboxId: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isIndeterminate: Boolean = false,
    validateAndUpdate: ((Boolean) -> Unit)? = null
) {
    val checkboxElement = document.getElementById(checkboxId) as? HTMLInputElement ?: return
    
    // Set up the change event listener
    checkboxElement.onchange = { event: Event ->
        val newValue = checkboxElement.checked
        
        // Call the validation function if provided
        validateAndUpdate?.invoke(newValue)
        
        // Call the onCheckedChange callback
        onCheckedChange(newValue)
        
        // Prevent default if needed
        if (event.defaultPrevented) {
            event.preventDefault()
        }
    }
    
    // Apply indeterminate state if needed
    if (isIndeterminate) {
        checkboxElement.indeterminate = true
    }
} 
