package codes.yousef.summon.hydration

import codes.yousef.summon.action.UiAction
import codes.yousef.summon.js.console
import kotlinx.serialization.json.Json
import kotlinx.browser.window
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

object ClientDispatcher {
    private val json = Json { ignoreUnknownKeys = true }

    fun dispatch(actionJson: String) {
        console.log("[Summon JS] ClientDispatcher.dispatch() called with: $actionJson")
        try {
            val action = json.decodeFromString<UiAction>(actionJson)
            console.log("[Summon JS] Parsed action: $action")
            dispatch(action)
        } catch (e: Exception) {
            console.error("[Summon JS] Failed to dispatch action: $actionJson")
            console.error("[Summon JS] Error:", e)
        }
    }

    fun dispatch(action: UiAction) {
        when (action) {
            is UiAction.Navigate -> {
                console.log("[Summon JS] Navigate to: ${action.url}")
                window.location.href = action.url
            }
            is UiAction.ServerRpc -> {
                console.log("[Summon JS] ServerRpc: ${action.endpoint}")
                // TODO: Implement actual fetch to /summon/dispatch
            }
            is UiAction.ToggleVisibility -> {
                console.log("[Summon JS] ToggleVisibility: ${action.targetId}")
                toggleElementVisibility(action.targetId)
            }
        }
    }

    /**
     * Toggles the visibility of an element and updates related accessibility attributes.
     * For hamburger menus, also updates the icon and aria-expanded state of the trigger button.
     */
    private fun toggleElementVisibility(targetId: String) {
        console.log("[Summon JS] toggleElementVisibility('$targetId')")
        val el = document.getElementById(targetId) as? HTMLElement
        if (el == null) {
            console.warn("[Summon JS] ToggleVisibility: Element not found with id '$targetId'")
            return
        }

        val currentDisplay = window.getComputedStyle(el).display
        val isCurrentlyHidden = currentDisplay == "none"
        val newDisplay = if (isCurrentlyHidden) "block" else "none"

        console.log("[Summon JS] Toggling '$targetId': $currentDisplay -> $newDisplay")
        el.style.display = newDisplay

        // Find the trigger button that controls this element (by aria-controls attribute)
        val triggerButton = document.querySelector("[aria-controls='$targetId']") as? HTMLElement
        if (triggerButton != null) {
            // Update aria-expanded state
            triggerButton.setAttribute("aria-expanded", isCurrentlyHidden.toString())

            // Update aria-label for hamburger menus
            val isHamburger = triggerButton.getAttribute("data-hamburger-toggle") == "true"
            if (isHamburger) {
                triggerButton.setAttribute(
                    "aria-label",
                    if (isCurrentlyHidden) "Close menu" else "Open menu"
                )

                // Update the icon inside the hamburger button
                // Look for Material Icons span inside the button
                val iconSpan = triggerButton.querySelector(".material-icons") as? HTMLElement
                if (iconSpan != null) {
                    iconSpan.textContent = if (isCurrentlyHidden) "close" else "menu"
                }
            }
        }
    }
}
