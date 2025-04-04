package com.summon

import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * Sets up a JavaScript click handler for the Badge component.
 * This extension function is called from JsPlatformRenderer.
 *
 * @param badgeId The ID of the badge element in the DOM
 */
fun Badge.setupJsClickHandler(badgeId: String) {
    // Get the badge element from the DOM
    val element = document.getElementById(badgeId) as? HTMLElement ?: return

    // Add click event listener
    element.onclick = { event ->
        // Call the onClick handler if available
        onClick?.invoke()
        // Prevent default action and stop propagation
        event.preventDefault()
        event.stopPropagation()
    }

    // Add keyboard event listener for accessibility (Enter and Space keys)
    element.onkeydown = { event ->
        if (event.key == "Enter" || event.key == " ") {
            // Call the onClick handler if available
            onClick?.invoke()
            // Prevent default action and stop propagation
            event.preventDefault()
            event.stopPropagation()
        }
    }
} 