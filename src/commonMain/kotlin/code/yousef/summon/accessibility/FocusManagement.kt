package code.yousef.summon.accessibility

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.DisposableEffect

/**
 * Utilities for managing focus in web applications.
 * This helps ensure keyboard and screen reader users can navigate the application effectively.
 */
object FocusManagement {
    /**
     * Types of focus behavior for elements.
     */
    enum class FocusBehavior {
        /** Element can be focused but is not in the tab order */
        FOCUSABLE,

        /** Element is in the tab order */
        TABBABLE,

        /** Element cannot be focused */
        DISABLED,

        /** Element is programmatically focused when rendered */
        AUTO_FOCUS
    }

    /**
     * Creates a Modifier with focus management attributes based on the behavior.
     */
    fun createFocusModifier(behavior: FocusBehavior): Modifier {
        val attributes = when (behavior) {
            FocusBehavior.FOCUSABLE -> mapOf("tabindex" to "-1")
            FocusBehavior.TABBABLE -> mapOf("tabindex" to "0")
            FocusBehavior.DISABLED -> mapOf(
                "tabindex" to "-1",
                "aria-disabled" to "true"
            )

            FocusBehavior.AUTO_FOCUS -> mapOf(
                "tabindex" to "0",
                "autofocus" to "true"
            )
        }

        return Modifier(attributes)
    }

    /**
     * Flag to track programmatic focus management.
     * This helps distinguish between user-initiated and programmatic focus changes.
     */
    private var isProgrammaticFocusChange = false

    /**
     * Ensures a component receives focus at a specified point in time.
     *
     * @param focusId Unique identifier for the element to focus
     * @param shouldRestore Whether focus should return to the previous element when done
     */
    fun createFocusPoint(focusId: String, shouldRestore: Boolean = false): Modifier {
        val attributes = mutableMapOf<String, String>()

        attributes["id"] = focusId
        attributes["data-focus-point"] = "true"

        if (shouldRestore) {
            attributes["data-focus-restore"] = "true"
        }

        return Modifier(attributes)
    }

    /**
     * Creates a focus trap that keeps focus within a component.
     * Useful for modals, dialogs, and other components that should trap focus.
     */
    fun createFocusTrap(trapId: String): Modifier {
        return Modifier(
            mapOf(
                "data-focus-trap" to trapId,
                "data-focus-trap-active" to "true"
            )
        )
    }

    /**
     * Creates a focus scope that groups focusable elements.
     * Useful for creating logical focus groups for arrow key navigation.
     */
    fun createFocusScope(scopeId: String): Modifier {
        return Modifier(
            mapOf(
                "data-focus-scope" to scopeId,
                "role" to "group"
            )
        )
    }
}

/**
 * Focus Manager for handling focus management in UI components.
 * This singleton provides functions to register focusable elements,
 * get and set focus, and manage focus traversal.
 */
object FocusManager {
    // Map of focusable elements by ID
    private val focusableElements = mutableMapOf<String, () -> Unit>()

    // Current focus state
    private var currentFocus: String? = null

    /**
     * Register a focusable element with the focus manager.
     *
     * @param id Unique identifier for the focusable element
     * @param focusAction Action to execute when focus is requested
     */
    fun registerFocusable(id: String, focusAction: () -> Unit) {
        focusableElements[id] = focusAction
    }

    /**
     * Unregister a focusable element from the focus manager.
     *
     * @param id Unique identifier for the focusable element
     */
    fun unregisterFocusable(id: String) {
        focusableElements.remove(id)
        if (currentFocus == id) {
            currentFocus = null
        }
    }

    /**
     * Request focus for a specific element by ID.
     *
     * @param id Unique identifier for the focusable element
     * @return true if focus was successfully set, false otherwise
     */
    fun requestFocus(id: String): Boolean {
        val focusAction = focusableElements[id] ?: return false
        focusAction()
        currentFocus = id
        return true
    }

    /**
     * Get the ID of the currently focused element.
     *
     * @return ID of the currently focused element, or null if no element is focused
     */
    fun getCurrentFocus(): String? {
        return currentFocus
    }

    /**
     * Clear the current focus.
     */
    fun clearFocus() {
        currentFocus = null
    }

    /**
     * Get a list of all registered focusable elements.
     *
     * @return List of IDs for all registered focusable elements
     */
    fun getFocusableElements(): List<String> {
        return focusableElements.keys.toList()
    }
}

/**
 * Helper function for DisposableEffect cleanup that returns a cleanup function.
 */
private fun onDispose(cleanup: () -> Unit): () -> Unit {
    return cleanup
}

/**
 * Creates a focusable component that can receive focus via keyboard navigation
 * or programmatic focus requests.
 *
 * @param modifier The base modifier for the component
 * @param focusId Optional custom focus ID, generated if not provided
 * @param onFocusChange Optional callback for focus state changes
 * @return A modified Modifier with focus attributes
 */
@Composable
fun makeFocusable(
    modifier: Modifier = Modifier(),
    focusId: String = generateRandomId(),
    onFocusChange: ((Boolean) -> Unit)? = null
): Modifier {
    // Platform-specific focus action that will be used through the platform bridge
    val focusAction: () -> Unit = {
        // Call the platform-specific focus implementation
        val success = applyFocusPlatform(focusId)
        if (!success) {
            println("FocusManager (via makeFocusable): Platform reported failure applying focus to $focusId")
        }
        // Call the onFocusChange callback if focus was attempted (regardless of platform success)
        onFocusChange?.invoke(true)
    }

    // Use DisposableEffect to handle registration and cleanup
    DisposableEffect(focusId) {
        // Register when the effect is first applied
        FocusManager.registerFocusable(focusId, focusAction)

        // Return cleanup function that will be called when the component is removed
        onDispose {
            FocusManager.unregisterFocusable(focusId)
        }
    }

    // Apply focus-related attributes via modifier
    return modifier
        .attribute("tabindex", "0") // Make it keyboard focusable
        .attribute("id", focusId) // Apply the ID for focus targeting
        .attribute("data-focusable", "true") // Mark as focusable
}

fun generateRandomId(): String {
    val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..10)
        .map { kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
} 
