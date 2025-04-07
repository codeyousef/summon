package code.yousef.summon.accessibility

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.attribute
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider

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
 * Manages keyboard navigation within a specific scope.
 * Handles key presses like Tab, Shift+Tab, Arrows, Enter, Escape.
 */
object KeyboardNavigator {
    // TODO: Needs significant implementation.
    // - How to capture key events (JS event listeners? Platform-specific integration?)
    // - How to interact with FocusManager to move focus?
    // - How to define navigation scopes (e.g., within a dialog, menu, grid)?

    fun handleKeyPress(key: String, scopeId: String? = null) {
        println("KeyboardNavigator: Key '$key' pressed (Scope: $scopeId). Handler not implemented.")
        when (key) {
            "Tab" -> FocusManager.moveFocusNext() // Needs FocusManager integration
            "Shift+Tab" -> FocusManager.moveFocusPrevious() // Needs FocusManager integration
            "ArrowDown", "ArrowRight" -> { /* Move focus within scope */ }
            "ArrowUp", "ArrowLeft" -> { /* Move focus within scope */ }
            "Enter", "Space" -> { /* Activate focused item */ }
            "Escape" -> { /* Dismiss/close scope? */ }
        }
    }
}

/**
 * Defines a scope for keyboard navigation.
 * Components within this scope can be navigated using arrow keys, etc.
 *
 * @param scopeId A unique identifier for this navigation scope (optional).
 * @param modifier Modifier applied to the scope container.
 * @param content The content within which keyboard navigation should operate.
 */
@Composable
fun KeyboardNavigationScope(
    scopeId: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer

    // TODO: How to capture key events for this scope?
    // Option 1: Modifier attaches event listeners (e.g., .onKeyDown)
    // Option 2: Global listener filters by active scope (requires tracking active scope)

    val keyboardScopeModifier = modifier
        // Example: Attach key down listener via modifier if possible
        // .onKeyDown { keyEvent -> KeyboardNavigator.handleKeyPress(keyEvent.key, scopeId) }
        // Add scope identifier if needed for global listener
        .apply { scopeId?.let { attribute("data-nav-scope", it) } }

    composer?.startNode() // Start keyboard navigation scope node
    if (composer?.inserting == true) {
        // Render a container (like Box) applying the modifier.
       PlatformRendererProvider.getPlatformRenderer().renderBox(modifier = keyboardScopeModifier)
    }
    content() // Compose the actual UI content within the scope
    composer?.endNode() // End keyboard navigation scope node
} 
