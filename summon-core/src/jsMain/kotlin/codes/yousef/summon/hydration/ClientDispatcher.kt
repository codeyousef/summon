package codes.yousef.summon.hydration

import codes.yousef.summon.action.UiAction
import codes.yousef.summon.js.console
import kotlinx.browser.window
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLAnchorElement
import kotlin.js.JSON

/**
 * Client-side dispatcher for UI actions.
 *
 * Handles UiAction commands like ToggleVisibility for hamburger menus.
 * Uses DOMBatcher for efficient DOM operations to avoid layout thrashing.
 */
object ClientDispatcher {
    private val domBatcher = DOMBatcher.instance

    /**
     * Enable/disable verbose logging.
     */
    var enableLogging = false

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
        try {
            parseUiAction(actionJson)?.let(::dispatch)
        } catch (error: Exception) {
            console.error("[Summon JS] Rejected invalid client action")
        }
    }

    /**
     * Parse UiAction from JSON using native JS parsing.
     * Avoids kotlinx-serialization dependency for smaller bundle size.
     */
    private fun parseUiAction(jsonStr: String): UiAction? {
        if (jsonStr.isEmpty() || jsonStr.length > MAX_ACTION_LENGTH || jsonStr.any { it.code > 0x7f }) {
            return null
        }
        val parsed = JSON.parse<dynamic>(jsonStr)
        val type = parsed.type as? String ?: return null
        val keys = js("Object.keys(parsed)") as Array<String>

        return when (type) {
            "nav" -> {
                if (keys.size != 2 || "url" !in keys) return null
                val url = parsed.url as? String ?: return null
                val anchor = document.createElement("a") as HTMLAnchorElement
                anchor.href = url
                if (
                    anchor.origin != window.location.origin ||
                    (anchor.protocol != "http:" && anchor.protocol != "https:")
                ) {
                    return null
                }
                UiAction.Navigate(anchor.href)
            }
            "toggle" -> {
                if (keys.size != 2 || "targetId" !in keys) return null
                val targetId = parsed.targetId as? String ?: return null
                if (!isSafeElementId(targetId)) return null
                UiAction.ToggleVisibility(targetId)
            }
            else -> null
        }
    }

    private fun isSafeElementId(value: String): Boolean =
        value.length in 1..128 &&
            (value[0].isLetter() || value[0] == '_') &&
            value.all { it.isLetterOrDigit() || it == '_' || it == '-' || it == ':' }

    fun dispatch(action: UiAction) {
        when (action) {
            is UiAction.Navigate -> {
                if (enableLogging) {
                    console.log("[Summon JS] Navigate to: ${action.url}")
                }
                window.location.href = action.url
            }
            is UiAction.ServerRpc -> {
                console.warn("[Summon JS] Server RPC actions are not executable client strings")
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
     * For disclosure toggles, updates the +/- icon.
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
        var disclosureIcon: HTMLElement? = null
        var currentDisplay: String? = null
        var originalDisplay: String? = null
        var isCurrentlyHidden = false
        var isHamburger = false

        // Phase 1: Read all DOM values
        domBatcher.read {
            el = document.getElementById(targetId) as? HTMLElement
            if (el == null) {
                console.warn("[Summon JS] ToggleVisibility: Element not found with id '$targetId'")
                return@read
            }

            // Get computed display to check visibility and store original
            val computedDisplay = window.getComputedStyle(el!!).display
            currentDisplay = computedDisplay
            isCurrentlyHidden = computedDisplay == "none"

            // Check for stored original display value
            originalDisplay = el!!.getAttribute("data-original-display")
            if (originalDisplay == null && !isCurrentlyHidden) {
                // Store original display value on first read when visible
                originalDisplay = computedDisplay
            }

            // Find the trigger button that controls this element
            triggerButton = document.querySelector("[aria-controls='$targetId']") as? HTMLElement
            if (triggerButton == null) {
                // Also look for data-action trigger pointing to this target
                triggerButton = document.querySelector("[data-action*='\"targetId\":\"$targetId\"']") as? HTMLElement
            }

            if (triggerButton != null) {
                isHamburger = triggerButton!!.getAttribute("data-hamburger-toggle") == "true"
                if (isHamburger) {
                    iconSpan = triggerButton!!.querySelector(".material-icons") as? HTMLElement
                } else {
                    // Look for disclosure +/- icon
                    disclosureIcon = triggerButton!!.querySelector("span:not(.material-icons)") as? HTMLElement
                }
            }
        }

        // Phase 2: Write all DOM changes
        domBatcher.write {
            if (el == null) return@write

            // Store original display value if not already stored
            if (el!!.getAttribute("data-original-display") == null && originalDisplay != null && originalDisplay != "none") {
                el!!.setAttribute("data-original-display", originalDisplay!!)
            }

            // Use stored original display, or 'flex' as default (common for layout containers)
            val showDisplay = el!!.getAttribute("data-original-display") ?: "flex"
            val newDisplay = if (isCurrentlyHidden) showDisplay else "none"

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
                } else {
                    // Update +/- disclosure icon if present
                    disclosureIcon?.let { icon ->
                        val iconText = icon.textContent?.trim() ?: ""
                        if (iconText == "+" || iconText == "−" || iconText == "-") {
                            icon.textContent = if (isCurrentlyHidden) "−" else "+"
                        }
                    }
                }
            }
        }

        // In sync mode, flush immediately for test compatibility
        if (syncMode) {
            domBatcher.flush()
        }
    }
    private const val MAX_ACTION_LENGTH = 2_048
}
