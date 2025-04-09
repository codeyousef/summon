package code.yousef.summon

import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * Data class to hold Icon-related extension properties for JS implementation
 */
data class IconJsExtension(
    val onClick: (() -> Unit)? = null,
    val name: String = ""
)

/**
 * Sets up a JavaScript click handler for the Icon component.
 * This extension function is called from JsPlatformRenderer.
 *
 * @param iconId The ID of the icon element in the DOM
 * @param iconExt Extension properties containing callbacks
 */
fun setupJsClickHandler(iconId: String, iconExt: IconJsExtension) {
    // Get the icon element from the DOM
    val element = document.getElementById(iconId) as? HTMLElement ?: return

    // Add click event listener
    element.onclick = { event ->
        // Call the onClick handler if available
        iconExt.onClick?.invoke()
        // Prevent default action and stop propagation
        event.preventDefault()
        event.stopPropagation()
    }

    // Add keyboard event listener for accessibility (Enter and Space keys)
    element.onkeydown = { event ->
        if (event.key == "Enter" || event.key == " ") {
            // Call the onClick handler if available
            iconExt.onClick?.invoke()
            // Prevent default action and stop propagation
            event.preventDefault()
            event.stopPropagation()
        }
    }
} 
