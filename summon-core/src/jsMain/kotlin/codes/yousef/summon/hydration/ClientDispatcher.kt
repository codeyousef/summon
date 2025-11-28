package codes.yousef.summon.hydration

import codes.yousef.summon.action.UiAction
import codes.yousef.summon.js.console
import kotlinx.serialization.json.Json
import kotlinx.browser.window
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * Client-side dispatcher for UI actions.
 *
 * Handles UiAction commands like ToggleVisibility for hamburger menus.
 * Uses DOMBatcher for efficient DOM operations to avoid layout thrashing.
 */
object ClientDispatcher {
    private val json = Json { ignoreUnknownKeys = true }
    private val domBatcher = DOMBatcher.instance

    /**
     * Enable/disable verbose logging.
     */
    var enableLogging = true

    /**
     * Enable synchronous mode for testing.
     *
     * When true, DOM operations are flushed immediately after dispatch
     * instead of being batched in requestAnimationFrame. This is useful
     * for testing where assertions need to check DOM state immediately
     * after dispatch.
     *
     * Default: false (use RAF-based batching for production performance)
     */
    var syncMode = false

    fun dispatch(actionJson: String) {
        if (enableLogging) {
            console.log("[Summon JS] ClientDispatcher.dispatch() called with: $actionJson")
        }
        try {
            val action = json.decodeFromString<UiAction>(actionJson)
            if (enableLogging) {
                console.log("[Summon JS] Parsed action: $action")
            }
            dispatch(action)
        } catch (e: Exception) {
            console.error("[Summon JS] Failed to dispatch action: $actionJson")
            console.error("[Summon JS] Error:", e)
        }
    }

    fun dispatch(action: UiAction) {
        when (action) {
            is UiAction.Navigate -> {
                if (enableLogging) {
                    console.log("[Summon JS] Navigate to: ${action.url}")
                }
                window.location.href = action.url
            }
            is UiAction.ServerRpc -> {
                if (enableLogging) {
                    console.log("[Summon JS] ServerRpc: ${action.endpoint}")
                }
                // TODO: Implement actual fetch to /summon/dispatch
            }
            is UiAction.ToggleVisibility -> {
                if (enableLogging) {
                    console.log("[Summon JS] ToggleVisibility: ${action.targetId}")
                }
                toggleElementVisibility(action.targetId)
            }
        }
    }

    /**
     * Toggles the visibility of an element and updates related accessibility attributes.
     * For hamburger menus, also updates the icon and aria-expanded state of the trigger button.
     *
     * Uses DOMBatcher to batch read and write operations separately,
     * preventing layout thrashing by ensuring all reads complete before writes.
     */
    private fun toggleElementVisibility(targetId: String) {
        if (enableLogging) {
            console.log("[Summon JS] toggleElementVisibility('$targetId')")
        }

        // Capture values in read phase, apply changes in write phase
        var el: HTMLElement? = null
        var triggerButton: HTMLElement? = null
        var iconSpan: HTMLElement? = null
        var currentDisplay: String? = null
        var isCurrentlyHidden = false
        var isHamburger = false

        // Phase 1: Read all DOM values
        domBatcher.read {
            el = document.getElementById(targetId) as? HTMLElement
            if (el == null) {
                console.warn("[Summon JS] ToggleVisibility: Element not found with id '$targetId'")
                return@read
            }

            currentDisplay = window.getComputedStyle(el!!).display
            isCurrentlyHidden = currentDisplay == "none"

            // Find the trigger button that controls this element
            triggerButton = document.querySelector("[aria-controls='$targetId']") as? HTMLElement
            if (triggerButton != null) {
                isHamburger = triggerButton!!.getAttribute("data-hamburger-toggle") == "true"
                if (isHamburger) {
                    iconSpan = triggerButton!!.querySelector(".material-icons") as? HTMLElement
                }
            }
        }

        // Phase 2: Write all DOM changes
        domBatcher.write {
            if (el == null) return@write

            val newDisplay = if (isCurrentlyHidden) "block" else "none"
            if (enableLogging) {
                console.log("[Summon JS] Toggling '$targetId': $currentDisplay -> $newDisplay")
            }
            el!!.style.display = newDisplay

            triggerButton?.let { button ->
                // Update aria-expanded state
                button.setAttribute("aria-expanded", isCurrentlyHidden.toString())

                if (isHamburger) {
                    // Update aria-label for hamburger menus
                    button.setAttribute(
                        "aria-label",
                        if (isCurrentlyHidden) "Close menu" else "Open menu"
                    )

                    // Update the icon inside the hamburger button
                    iconSpan?.textContent = if (isCurrentlyHidden) "close" else "menu"
                }
            }
        }

        // In sync mode, flush immediately for test compatibility
        if (syncMode) {
            domBatcher.flush()
        }
    }
}
