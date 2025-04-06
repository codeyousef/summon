package code.yousef.summon.accessibility

import code.yousef.summon.modifier.Modifier

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
    fun Modifier.keyboardNavigation(config: KeyboardNavigation.KeyboardNavigationConfig): Modifier {
        var modified = this.focusable(config.tabIndex)

        if (config.autoFocus) {
            modified = modified.autoFocus()
        }

        if (config.trapFocus) {
            val trapId = "focus-trap-${config.hashCode()}"
            modified = modified.focusTrap(trapId)
        }

        if (config.keyHandlers.isNotEmpty()) {
            modified = modified.keyboardHandlers(config.keyHandlers)
        }

        if (config.useArrowKeys) {
            val arrowKeyHandlers = mapOf(
                KeyboardKeys.ARROW_UP to {},
                KeyboardKeys.ARROW_DOWN to {},
                KeyboardKeys.ARROW_LEFT to {},
                KeyboardKeys.ARROW_RIGHT to {}
            )
            modified = modified.keyboardHandlers(arrowKeyHandlers)
        }

        return modified
    }
}

/**
 * A component that provides keyboard navigation capabilities.
 * NOTE: This class is likely obsolete after refactoring to @Composable functions.
 * Keyboard navigation attributes should be applied via Modifier directly to standard components.
 *
 * @param content The content to make keyboard navigable
 * @param config The keyboard navigation configuration
 */
class KeyboardNavigableContainer(
    // Content is no longer directly relevant here.
    // private val content: List<Composable>,
    private val config: KeyboardNavigation.KeyboardNavigationConfig = KeyboardNavigation.KeyboardNavigationConfig()
) /* : Composable, FocusableComponent */ { // Removed interface inheritance

    // Removed the compose method
    /*
    override fun <T> compose(receiver: T): T {
        val modifier = Modifier().keyboardNavigation(config)
        // TODO: Implement rendering with keyboard navigation
        // This logic is now defunct.
        return receiver
    }
    */
    // This class now primarily acts as a data holder, but its purpose is questionable.
    // Consider removing it and using the helper functions in KeyboardNavigation directly.
} 