package code.yousef.summon.effects.browser

import code.yousef.summon.effects.CompositionScope
import code.yousef.summon.effects.KeyModifier
import code.yousef.summon.effects.KeyboardEvent
import code.yousef.summon.effects.onMountWithCleanup
import code.yousef.summon.runtime.Composable
import kotlinx.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent as DomKeyboardEvent

/**
 * Check if a keyboard event matches the specified key and modifiers
 */
private fun DomKeyboardEvent.matches(key: String, modifiers: Set<KeyModifier>): Boolean {
    // Check if the key matches
    if (this.key.lowercase() != key.lowercase()) {
        return false
    }

    // Check modifiers
    val hasCtrl = this.ctrlKey
    val hasAlt = this.altKey
    val hasShift = this.shiftKey
    val hasMeta = this.metaKey

    // Verify all required modifiers are pressed
    val requiredCtrl = KeyModifier.CTRL in modifiers
    val requiredAlt = KeyModifier.ALT in modifiers
    val requiredShift = KeyModifier.SHIFT in modifiers
    val requiredMeta = KeyModifier.META in modifiers

    return requiredCtrl == hasCtrl &&
            requiredAlt == hasAlt &&
            requiredShift == hasShift &&
            requiredMeta == hasMeta
}

/**
 * Effect for handling keyboard shortcuts
 *
 * @param key The key to listen for
 * @param modifiers Set of modifier keys required
 * @param handler Function to call when the shortcut is triggered
 */
@Composable
fun CompositionScope.useKeyboardShortcut(
    key: String,
    modifiers: Set<KeyModifier> = emptySet(),
    handler: (KeyboardEvent) -> Unit
) {
    onMountWithCleanup {
        // Event handler function for key events
        val keydownHandler = { event: Event ->
            val keyEvent = event as DomKeyboardEvent

            // Check if this event matches our shortcut
            if (keyEvent.matches(key, modifiers)) {
                // Convert to our KeyboardEvent type
                val ourEvent = KeyboardEvent(
                    key = keyEvent.key,
                    modifiers = buildSet {
                        if (keyEvent.ctrlKey) add(KeyModifier.CTRL)
                        if (keyEvent.altKey) add(KeyModifier.ALT)
                        if (keyEvent.shiftKey) add(KeyModifier.SHIFT)
                        if (keyEvent.metaKey) add(KeyModifier.META)
                    }
                )

                // Prevent default browser action for this key
                keyEvent.preventDefault()

                // Call the handler
                handler(ourEvent)
            }
        }

        // Add the event listener to the document
        document.addEventListener("keydown", keydownHandler)

        // Return cleanup function to remove the event listener
        return@onMountWithCleanup {
            document.removeEventListener("keydown", keydownHandler)
        }
    }
}

/**
 * Effect for handling specific key press events
 *
 * @param keys List of keys to listen for
 * @param handler Function to call when any of the keys is pressed
 */
@Composable
fun CompositionScope.useKeyPress(
    vararg keys: String,
    handler: (String) -> Unit
) {
    onMountWithCleanup {
        // Convert keys to lowercase for case-insensitive comparison
        val keysList = keys.map { it.lowercase() }

        // Event handler for keydown events
        val keydownHandler = { event: Event ->
            val keyEvent = event as DomKeyboardEvent
            val pressedKey = keyEvent.key.lowercase()

            // Check if this key is in our list
            if (pressedKey in keysList) {
                handler(keyEvent.key)
            }
        }

        // Add the event listener
        document.addEventListener("keydown", keydownHandler)

        // Return cleanup function
        return@onMountWithCleanup {
            document.removeEventListener("keydown", keydownHandler)
        }
    }
}

/**
 * Effect for tracking keyboard focus state
 *
 * @param elementId ID of the element to track focus state for
 * @param onFocusIn Function to call when the element receives focus
 * @param onFocusOut Function to call when the element loses focus
 */
@Composable
fun CompositionScope.useFocusTracking(
    elementId: String,
    onFocusIn: () -> Unit = {},
    onFocusOut: () -> Unit = {}
) {
    onMountWithCleanup {
        // Get the element by ID
        val element = document.getElementById(elementId)

        // Create event handlers
        val focusInHandler = { _: Event -> onFocusIn() }
        val focusOutHandler = { _: Event -> onFocusOut() }

        // Add event listeners if the element exists
        element?.addEventListener("focusin", focusInHandler)
        element?.addEventListener("focusout", focusOutHandler)

        // Return cleanup function
        return@onMountWithCleanup {
            element?.removeEventListener("focusin", focusInHandler)
            element?.removeEventListener("focusout", focusOutHandler)
        }
    }
} 
