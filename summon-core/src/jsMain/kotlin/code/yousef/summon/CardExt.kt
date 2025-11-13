package codes.yousef.summon

import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * Data class to hold Card-related extension properties for JS implementation
 */
data class CardJsExtension(
    val onClick: (() -> Unit)? = null,
    val isInteractive: Boolean = false
)

/**
 * Sets up a JavaScript click handler for the Card component.
 * This extension function is called from PlatformRenderer.
 *
 * @param cardId The ID of the card element in the DOM
 * @param cardExt Extension properties containing callbacks
 */
fun setupJsClickHandler(cardId: String, cardExt: CardJsExtension) {
    // Get the card element from the DOM
    val element = document.getElementById(cardId) as? HTMLElement ?: return

    // Add click event listener
    element.onclick = { event ->
        // Call the onClick handler if available
        cardExt.onClick?.invoke()
        // Prevent default action and stop propagation
        event.preventDefault()
        event.stopPropagation()
    }

    // Add keyboard event listener for accessibility (Enter and Space keys)
    element.onkeydown = { event ->
        if (event.key == "Enter" || event.key == " ") {
            // Call the onClick handler if available
            cardExt.onClick?.invoke()
            // Prevent default action and stop propagation
            event.preventDefault()
            event.stopPropagation()
        }
    }
} 
