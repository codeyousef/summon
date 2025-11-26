package codes.yousef.summon.hydration

import codes.yousef.summon.action.UiAction
import codes.yousef.summon.runtime.wasmConsoleError
import kotlinx.serialization.json.Json
import kotlinx.browser.window
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * Client-side dispatcher for WASM target.
 * Handles UiAction commands like ToggleVisibility for hamburger menus.
 */
object ClientDispatcher {
    private val json = Json { ignoreUnknownKeys = true }

    fun dispatch(actionJson: String) {
        try {
            val action = json.decodeFromString<UiAction>(actionJson)
            dispatch(action)
        } catch (e: Exception) {
            wasmConsoleError("[Summon] Failed to dispatch action: ${e.message}")
        }
    }

    fun dispatch(action: UiAction) {
        when (action) {
            is UiAction.Navigate -> {
                window.location.href = action.url
            }
            is UiAction.ServerRpc -> {
                // TODO: Implement actual fetch to /summon/dispatch
            }
            is UiAction.ToggleVisibility -> {
                toggleElementVisibility(action.targetId)
            }
        }
    }

    /**
     * Toggles the visibility of an element and updates related accessibility attributes.
     * For hamburger menus, also updates the icon and aria-expanded state of the trigger button.
     */
    private fun toggleElementVisibility(targetId: String) {
        val el = document.getElementById(targetId) as? HTMLElement
        if (el == null) {
            wasmConsoleError("[Summon] ToggleVisibility: Element not found with id '$targetId'")
            return
        }

        val currentDisplay = window.getComputedStyle(el).display
        val isCurrentlyHidden = currentDisplay == "none"
        val newDisplay = if (isCurrentlyHidden) "block" else "none"

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
