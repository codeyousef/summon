package com.summon

import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

/**
 * JS-specific extension function to set up checkbox change handling for Checkbox.
 */
fun Checkbox.setupJsCheckboxHandler(checkboxId: String) {
    val checkboxElement = document.getElementById(checkboxId) as? HTMLInputElement ?: return
    
    // Set up the change event listener
    checkboxElement.onchange = { event: Event ->
        val newValue = checkboxElement.checked
        
        // Update the state
        state.value = newValue
        
        // Call the onValueChange callback
        onValueChange(newValue)
        
        // Validate if there are validators
        if (validators.isNotEmpty()) {
            validate()
        }
        
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

/**
 * JS-specific extension function to set up checkbox change handling.
 * This function is called from the JsPlatformRenderer.
 */
fun setupJsCheckboxHandler(checkboxId: String, checkbox: Checkbox) {
    checkbox.setupJsCheckboxHandler(checkboxId)
} 