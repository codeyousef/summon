package codes.yousef.summon.accessibility

import codes.yousef.summon.accessibility.KeyboardNavigation.keyboardNavigation
import codes.yousef.summon.core.mapOfCompat
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Keys commonly used for keyboard navigation in web applications.
 */
object KeyboardKeys {
    /** Tab navigation key. */
    const val TAB = "Tab"
    /** Activation key used by buttons and links. */
    const val ENTER = "Enter"
    /** Space activation key. */
    const val SPACE = " " // Note: "Spacebar" or "Space" might be more common, ensure consistency with event.key
    /** Escape or dismissal key. */
    const val ESCAPE = "Escape"
    /** Up-arrow navigation key. */
    const val ARROW_UP = "ArrowUp"
    /** Down-arrow navigation key. */
    const val ARROW_DOWN = "ArrowDown"
    /** Left-arrow navigation key. */
    const val ARROW_LEFT = "ArrowLeft"
    /** Right-arrow navigation key. */
    const val ARROW_RIGHT = "ArrowRight"
    /** Start-of-list navigation key. */
    const val HOME = "Home"
    /** End-of-list navigation key. */
    const val END = "End"
    /** Previous-page navigation key. */
    const val PAGE_UP = "PageUp"
    /** Next-page navigation key. */
    const val PAGE_DOWN = "PageDown"
}

/**
 * Extension functions for adding keyboard navigation capabilities to components
 */
object KeyboardNavigation {
    /**
     * Keyboard navigation configuration.
     *
     * @property trapFocus keep focus within the component
     * @property useArrowKeys enable directional navigation
     * @property autoFocus request focus on mount
     * @property tabIndex browser tab-order value
     * @property keyHandlers callbacks keyed by browser `KeyboardEvent.key`
     */
    data class KeyboardNavigationConfig(
        val trapFocus: Boolean = false,
        val useArrowKeys: Boolean = false,
        val autoFocus: Boolean = false,
        val tabIndex: Int = 0, // 0 for focusable in natural order, -1 for not focusable by keyboard
        val keyHandlers: Map<String, () -> Unit> = emptyMap()
    )

    /**
     * Makes an element focusable and sets its tab index.
     *
     * @param tabIndex The tabindex attribute value (e.g., 0 for default, -1 to remove from tab order).
     * @return Modifier with tabindex attribute.
     */
    fun Modifier.focusable(tabIndex: Int = 0): Modifier {
        return this.attribute("tabindex", tabIndex.toString())
    }

    /**
     * Creates a focus trap that keeps keyboard focus within a component.
     * Useful for modals, dialogs, and other components that should trap focus.
     *
     * @param trapId Unique identifier for the focus trap (can be dynamically generated).
     * @return Modified Modifier with focus trap attributes.
     */
    fun Modifier.focusTrap(trapId: String): Modifier {
        val trapAttributes = mapOfCompat(
            "data-focus-trap" to trapId,
            "data-focus-trap-active" to "true" // Assuming JS will look for this
        )
        return this.attributes(trapAttributes)
    }

    /**
     * Causes an element to be autofocused when rendered.
     * Note: Use sparingly, as autofocus can be disruptive to accessibility.
     *
     * @return Modified Modifier with autofocus attribute.
     */
    fun Modifier.autoFocus(): Modifier {
        // HTML standard is just 'autofocus', the value doesn't strictly matter if present.
        // However, setting to "true" is common.
        return this.attribute("autofocus", "true")
    }

    /**
     * Adds keyboard handler attributes to a component.
     * These attributes are intended to be picked up by JavaScript to attach event listeners.
     *
     * @param keyHandlers Map of key codes (e.g., from `KeyboardKeys`) to handler functions.
     *                    The functions themselves are not directly transferred; this setup assumes
     *                    JS will re-connect these based on IDs or a similar mechanism.
     * @return Modified Modifier with keyboard event handler placeholder attributes.
     */
    fun Modifier.keyboardHandlers(keyHandlers: Map<String, () -> Unit>): Modifier {
        val keyHandlerAttributes = mutableMapOf<String, String>()

        // This approach of embedding handler information in data attributes is for
        // a client-side script to interpret. The lambdas themselves aren't serialized.
        keyHandlers.entries.forEachIndexed { index, (key, _) ->
            // A unique ID for the handler might be useful if JS needs to map back to specific Kotlin logic,
            // though direct execution of Kotlin lambdas from these attributes is complex.
            // For now, just marking the key and a generic handler ID.
            val handlerId = "kbd_handler_${key.replace(Regex("[^A-Za-z0-9]"), "_")}_$index"
            keyHandlerAttributes["data-kbd-key-$index"] = key
            keyHandlerAttributes["data-kbd-handler-id-$index"] = handlerId // Identifier for JS to potentially use
        }

        if (keyHandlers.isNotEmpty()) {
            keyHandlerAttributes["data-kbd-handlers-count"] = keyHandlers.size.toString()
        }

        return if (keyHandlerAttributes.isNotEmpty()) this.attributes(keyHandlerAttributes) else this
    }

    /**
     * Sets keyboard navigation attributes for a component.
     *
     * @param config The keyboard navigation configuration.
     * @return Modified Modifier with keyboard navigation attributes.
     */
    fun Modifier.keyboardNavigation(config: KeyboardNavigationConfig): Modifier {
        var modifiedModifier = this.focusable(config.tabIndex)

        if (config.autoFocus) {
            modifiedModifier = modifiedModifier.autoFocus()
        }

        if (config.trapFocus) {
            // Generate a unique ID for the trap if not provided
            val trapId = "focus-trap-${config.hashCode().toString(16)}"
            modifiedModifier = modifiedModifier.focusTrap(trapId)
        }

        val allKeyHandlers = mutableMapOf<String, () -> Unit>()
        allKeyHandlers.putAll(config.keyHandlers)

        if (config.useArrowKeys) {
            // Placeholder handlers; actual navigation logic would be in JS or more complex Kotlin.
            // These entries signal to the `keyboardHandlers` function to add data attributes for these keys.
            // Only add default arrow key handlers if they haven't been provided in config.keyHandlers
            if (!allKeyHandlers.containsKey(KeyboardKeys.ARROW_UP)) {
                allKeyHandlers[KeyboardKeys.ARROW_UP] = { /* Default Arrow Up Action Placeholder */ }
            }
            if (!allKeyHandlers.containsKey(KeyboardKeys.ARROW_DOWN)) {
                allKeyHandlers[KeyboardKeys.ARROW_DOWN] = { /* Default Arrow Down Action Placeholder */ }
            }
            if (!allKeyHandlers.containsKey(KeyboardKeys.ARROW_LEFT)) {
                allKeyHandlers[KeyboardKeys.ARROW_LEFT] = { /* Default Arrow Left Action Placeholder */ }
            }
            if (!allKeyHandlers.containsKey(KeyboardKeys.ARROW_RIGHT)) {
                allKeyHandlers[KeyboardKeys.ARROW_RIGHT] = { /* Default Arrow Right Action Placeholder */ }
            }
        }

        if (allKeyHandlers.isNotEmpty()) {
            modifiedModifier = modifiedModifier.keyboardHandlers(allKeyHandlers)
        }

        return modifiedModifier
    }
}

/**
 * A composable function that wraps content with keyboard navigation capabilities.
 *
 * @param modifier The base modifier to apply to the container.
 * @param config The keyboard navigation configuration.
 * @param content The composable content to be made keyboard navigable.
 */
@Composable
fun KeyboardNavigableContainer(
    modifier: Modifier = Modifier(), // Uses the @JsExported Modifier
    config: KeyboardNavigation.KeyboardNavigationConfig = KeyboardNavigation.KeyboardNavigationConfig(),
    content: @Composable () -> Unit
) {
    // Apply keyboard navigation configurations to the passed modifier
    val navModifier = modifier.keyboardNavigation(config)

    // Define base attributes for the container
    val containerBaseAttributes = mapOfCompat(
        "class" to "keyboard-navigable-container", // For styling or JS selection
        "data-keyboard-nav" to "true",             // Marker for JS
        "role" to "group"                          // ARIA role for a group of related elements
    )

    // Combine the navigation modifier with these base attributes
    val finalModifier = navModifier.attributes(containerBaseAttributes)

    // Use the platform renderer to render a simple Box or div-like container
    // The actual element type (e.g., "div") would be determined by the renderer's renderBox implementation.
    val renderer = LocalPlatformRenderer.current
    renderer.renderBox(modifier = finalModifier) {
        content()
    }
}

// The local applyAttributes helper is no longer strictly necessary if Modifier.attributes covers its use case,
// but it was used to apply a map of attributes. Modifier.attributes(attrs: Map<String, String>) does this now.
// If it was used for other specific logic, it might need to be re-evaluated.
// For now, KeyboardNavigableContainer uses navModifier.attributes(containerBaseAttributes) directly.