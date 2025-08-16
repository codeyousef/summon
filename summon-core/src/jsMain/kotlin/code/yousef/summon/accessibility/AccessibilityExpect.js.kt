package code.yousef.summon.accessibility

import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * JS implementation for setting focus using DOM APIs.
 *
 * @param elementId The ID of the element to focus.
 * @return `true` if the element was found and focus was attempted, `false` otherwise.
 */
actual fun applyFocusPlatform(elementId: String): Boolean {
    val element = document.getElementById(elementId) as? HTMLElement
    return if (element != null) {
        try {
            element.focus()
            true
        } catch (e: dynamic) { // Catch potential errors if element is not focusable
            println("[WARN] Attempt to focus element '$elementId' failed: ${e?.message}")
            false
        }
    } else {
        println("[WARN] Element with ID '$elementId' not found for focusing.")
        false
    }
} 