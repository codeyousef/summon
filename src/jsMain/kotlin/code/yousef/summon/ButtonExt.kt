package code.yousef.summon

import code.yousef.summon.components.input.Button
import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement

/**
 * JS-specific extension for Button to handle click events.
 * This can be called after the button is added to the DOM to set up event handling.
 */
fun Button.setupJsClickHandler(buttonId: String) {
    val button = document.getElementById(buttonId) as? HTMLButtonElement
    button?.let {
        it.onclick = { event ->
            onClick(event)
            true
        }
    }
} 