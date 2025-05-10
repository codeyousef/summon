package code.yousef.summon

import code.yousef.summon.components.feedback.BadgeType
import code.yousef.summon.components.feedback.BadgeShape
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * Data class to hold Badge-related extension properties for JS implementation
 */
data class BadgeJsExtension(
    val type: BadgeType = BadgeType.PRIMARY,
    val shape: BadgeShape = BadgeShape.ROUNDED,
    val onClick: (() -> Unit)? = null
)

/**
 * Sets up a JavaScript click handler for the Badge component.
 * This extension function is called from PlatformRenderer.
 *
 * @param badgeId The ID of the badge element in the DOM
 * @param badgeExt Extension properties containing callbacks
 */
fun setupJsClickHandler(badgeId: String, badgeExt: BadgeJsExtension) {
    // Get the badge element from the DOM
    val element = document.getElementById(badgeId) as? HTMLElement ?: return

    // Add click event listener
    element.onclick = { event ->
        // Call the onClick handler if available
        badgeExt.onClick?.invoke()
        // Prevent default action and stop propagation
        event.preventDefault()
        event.stopPropagation()
    }

    // Add keyboard event listener for accessibility (Enter and Space keys)
    element.onkeydown = { event ->
        if (event.key == "Enter" || event.key == " ") {
            // Call the onClick handler if available
            badgeExt.onClick?.invoke()
            // Prevent default action and stop propagation
            event.preventDefault()
            event.stopPropagation()
        }
    }
}
