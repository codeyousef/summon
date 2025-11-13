package codes.yousef.summon

import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement

/**
 * Data class to hold Link-related extension properties for JS implementation
 */
data class LinkJsExtension(
    val onClick: (() -> Unit)? = null,
    val href: String = "",
    val target: String? = null
)

/**
 * JS-specific function to handle link click events.
 * This can be called after the link is added to the DOM to set up event handling.
 *
 * @param linkId The ID of the link element in the DOM
 * @param linkExt Extension properties containing callbacks
 */
fun setupJsClickHandler(linkId: String, linkExt: LinkJsExtension) {
    val link = document.getElementById(linkId) as? HTMLAnchorElement
    link?.let {
        it.onclick = { event ->
            // Prevent default navigation if there's a click handler
            linkExt.onClick?.let { handler ->
                event.preventDefault()
                handler()
            }
            true
        }
    }
} 
