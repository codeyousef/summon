package code.yousef.summon

import code.yousef.summon.components.feedback.Tooltip
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

/**
 * Sets up JavaScript event handlers for the Tooltip component.
 * This extension function is called from JsPlatformRenderer.
 *
 * @param tooltipId The ID of the tooltip wrapper element in the DOM
 * @param contentId The ID of the tooltip content element in the DOM
 */
fun Tooltip.setupJsHandlers(tooltipId: String, contentId: String) {
    // Get the tooltip wrapper element from the DOM
    val tooltipElement = document.getElementById(tooltipId) as? HTMLElement ?: return
    val contentElement = document.getElementById(contentId) as? HTMLElement ?: return

    // Variables to track timer IDs
    var showTimeoutId: Int? = null
    var hideTimeoutId: Int? = null

    // Function to show the tooltip
    val showTooltip = {
        // Clear any hide timeout
        hideTimeoutId?.let { window.clearTimeout(it) }

        // Set the show timeout
        showTimeoutId = window.setTimeout({
            contentElement.style.opacity = "1"
        }, showDelay)
    }

    // Function to hide the tooltip
    val hideTooltip = {
        // Clear any show timeout
        showTimeoutId?.let { window.clearTimeout(it) }

        // Set the hide timeout
        hideTimeoutId = window.setTimeout({
            contentElement.style.opacity = "0"
        }, hideDelay)
    }

    // Show tooltip on mouse enter
    tooltipElement.onmouseenter = {
        showTooltip()
        true
    }

    // Hide tooltip on mouse leave
    tooltipElement.onmouseleave = {
        hideTooltip()
        true
    }

    // Show tooltip on focus for accessibility
    tooltipElement.onfocus = {
        showTooltip()
        true
    }

    // Hide tooltip on blur for accessibility
    tooltipElement.onblur = {
        hideTooltip()
        true
    }

    // Show tooltip on click if enabled
    if (showOnClick) {
        tooltipElement.onclick = { event ->
            showTooltip()
            true
        }
    }
} 