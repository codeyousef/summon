package code.yousef.summon

import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement

/**
 * JS-specific extension for Link to handle click events.
 * This can be called after the link is added to the DOM to set up event handling.
 */
fun Link.setupJsClickHandler(linkId: String) {
    val link = document.getElementById(linkId) as? HTMLAnchorElement
    link?.let {
        it.onclick = { event ->
            // Prevent default navigation if there's a click handler
            onClick?.let { handler ->
                event.preventDefault()
                handler()
            }
            true
        }
    }
} 