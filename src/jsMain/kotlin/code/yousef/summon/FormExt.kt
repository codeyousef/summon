package code.yousef.summon

import code.yousef.summon.components.input.Form
import kotlinx.browser.document
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.events.Event

/**
 * JS-specific extension function to set up form submission handling.
 */
fun Form.setupJsFormHandler(formId: String) {
    val formElement = document.getElementById(formId) as? HTMLFormElement ?: return
    
    // Set up the submit event listener
    formElement.onsubmit = { event: Event ->
        // Prevent default browser form submission
        event.preventDefault()
        
        // Validate and submit the form
        submit()
    }
} 