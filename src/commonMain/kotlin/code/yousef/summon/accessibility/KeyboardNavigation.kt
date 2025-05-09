package code.yousef.summon.accessibility

import code.yousef.summon.accessibility.KeyboardNavigation.keyboardNavigation
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.ModifierExtras.attribute
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Keys commonly used for keyboard navigation in web applications.
 */
object KeyboardKeys {
    const val TAB = "Tab"
    const val ENTER = "Enter"
    const val SPACE = " "
    const val ESCAPE = "Escape"
    const val ARROW_UP = "ArrowUp"
    const val ARROW_DOWN = "ArrowDown"
    const val ARROW_LEFT = "ArrowLeft"
    const val ARROW_RIGHT = "ArrowRight"
    const val HOME = "Home"
    const val END = "End"
    const val PAGE_UP = "PageUp"
    const val PAGE_DOWN = "PageDown"
}

/**
 * Extension functions for adding keyboard navigation capabilities to components
 */
object KeyboardNavigation {
    /**
     * Keyboard navigation configuration options.
     */
    data class KeyboardNavigationConfig(
        val trapFocus: Boolean = false,
        val useArrowKeys: Boolean = false,
        val autoFocus: Boolean = false,
        val tabIndex: Int = 0,
        val keyHandlers: Map<String, () -> Unit> = emptyMap()
    )

    /**
     * Makes an element focusable and sets its tab index.
     *
     * @param tabIndex The tabindex attribute value (-1 for not focusable via keyboard)
     * @return Modifier with tabindex attribute
     */
    fun Modifier.focusable(tabIndex: Int = 0): Modifier {
        return Modifier.create().then(this).then(Modifier(mapOf("tabindex" to tabIndex.toString())))
    }

    /**
     * Creates a focus trap that keeps keyboard focus within a component.
     * Useful for modals, dialogs, and other components that should trap focus.
     *
     * @param trapId Unique identifier for the focus trap
     * @return Modified Modifier with focus trap attributes
     */
    fun Modifier.focusTrap(trapId: String): Modifier {
        val attributes = mapOf(
            "data-focus-trap" to trapId,
            "data-focus-trap-active" to "true"
        )
        return this.then(Modifier(attributes))
    }

    /**
     * Causes an element to be auto-focused when rendered.
     *
     * @return Modified Modifier with autofocus attribute
     */
    fun Modifier.autoFocus(): Modifier {
        return this.then(Modifier(mapOf("autofocus" to "true")))
    }

    /**
     * Adds keyboard handler attributes to a component.
     *
     * @param keyHandlers Map of key codes to handler functions
     * @return Modified Modifier with keyboard event handlers
     */
    fun Modifier.keyboardHandlers(keyHandlers: Map<String, () -> Unit>): Modifier {
        val keyHandlerAttributes = mutableMapOf<String, String>()

        keyHandlers.entries.forEachIndexed { index, (key, _) ->
            val handlerId = "kbd_handler_$index"
            keyHandlerAttributes["data-kbd-key-$index"] = key
            keyHandlerAttributes["data-kbd-handler-$index"] = handlerId
        }

        keyHandlerAttributes["data-kbd-handlers-count"] = keyHandlers.size.toString()

        return this.then(Modifier(keyHandlerAttributes))
    }

    /**
     * Sets keyboard navigation attributes for a component.
     *
     * @param config The keyboard navigation configuration
     * @return Modified Modifier with keyboard navigation attributes
     */
    fun Modifier.keyboardNavigation(config: KeyboardNavigationConfig): Modifier {
        var modified = this.focusable(config.tabIndex)

        if (config.autoFocus) {
            modified = modified.autoFocus()
        }

        if (config.trapFocus) {
            val trapId = "focus-trap-${config.hashCode()}"
            modified = modified.focusTrap(trapId)
        }

        // Combine custom key handlers and arrow key handlers if needed
        val allKeyHandlers = mutableMapOf<String, () -> Unit>()

        // Add custom key handlers
        allKeyHandlers.putAll(config.keyHandlers)

        // Add arrow key handlers if enabled
        if (config.useArrowKeys) {
            allKeyHandlers[KeyboardKeys.ARROW_UP] = {}
            allKeyHandlers[KeyboardKeys.ARROW_DOWN] = {}
            allKeyHandlers[KeyboardKeys.ARROW_LEFT] = {}
            allKeyHandlers[KeyboardKeys.ARROW_RIGHT] = {}
        }

        // Apply all key handlers at once if there are any
        if (allKeyHandlers.isNotEmpty()) {
            modified = modified.keyboardHandlers(allKeyHandlers)
        }

        return modified
    }
}

/**
 * A composable function that provides keyboard navigation capabilities.
 *
 * @param modifier The modifier to apply to the container
 * @param config The keyboard navigation configuration
 * @param content The content to make keyboard navigable
 */
@Composable
fun KeyboardNavigableContainer(
    modifier: Modifier = Modifier(),
    config: KeyboardNavigation.KeyboardNavigationConfig = KeyboardNavigation.KeyboardNavigationConfig(),
    content: @Composable () -> Unit
) {
    val navModifier = modifier.keyboardNavigation(config)

    // Create a container element with the keyboard navigation attributes
    val containerAttrs = mapOf(
        "class" to "keyboard-navigable-container",
        "data-keyboard-nav" to "true",
        "role" to "group"
    )

    // Apply attributes to the modifier
    val finalModifier = navModifier.applyAttributes(containerAttrs)

    // Use platform renderer to create a container with keyboard navigation attributes
    // This avoids reliance on specific Composer methods which may differ between versions
    val renderer = LocalPlatformRenderer.current
    renderer.renderBox(modifier = finalModifier, content = { content() })
}

// Helper function to apply attributes to a modifier
private fun Modifier.applyAttributes(attributes: Map<String, String>): Modifier {
    var result = this
    for ((key, value) in attributes) {
        result = result.attribute(key, value)
    }
    return result
} 
