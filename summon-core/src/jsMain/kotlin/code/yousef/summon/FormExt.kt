package code.yousef.summon

import code.yousef.summon.components.input.FormState
import kotlinx.browser.document
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.events.Event

/**
 * JS-specific utilities for form handling.
 */
object FormJs {
    /**
     * Sets up JavaScript event handlers for a form element.
     *
     * @param formId The ID of the form element in the DOM
     * @param formState The FormState instance to use for validation and submission
     * @param onSubmit The callback to invoke when the form is submitted
     */
    fun setupFormHandler(formId: String, formState: FormState, onSubmit: (Map<String, String>) -> Unit) {
        val formElement = document.getElementById(formId) as? HTMLFormElement ?: return

        // Set up the submit event listener
        formElement.onsubmit = { event: Event ->
            // Prevent default browser form submission
            event.preventDefault()

            // Validate and submit the form
            formState.submit(onSubmit)
        }
    }
}
