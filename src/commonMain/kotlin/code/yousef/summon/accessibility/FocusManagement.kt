package code.yousef.summon.accessibility

import code.yousef.summon.FocusableComponent
import code.yousef.summon.core.Composable
import code.yousef.summon.modifier.Modifier

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
 * A component that manages focus for its children.
 */
class FocusManager(
    private val content: List<Composable>,
    private val behavior: FocusManagement.FocusBehavior = FocusManagement.FocusBehavior.TABBABLE,
    private val focusId: String? = null,
    private val isFocusTrap: Boolean = false,
    private val scopeId: String? = null
) : Composable, FocusableComponent {

    override fun <T> compose(receiver: T): T {
        var modifier = FocusManagement.createFocusModifier(behavior)

        if (focusId != null) {
            val restoreFocus = isFocusTrap // Automatically restore focus when using a focus trap
            val focusPointModifier = FocusManagement.createFocusPoint(focusId, restoreFocus)
            modifier = Modifier(modifier.styles + focusPointModifier.styles)
        }

        if (isFocusTrap && focusId != null) {
            val trapModifier = FocusManagement.createFocusTrap(focusId)
            modifier = Modifier(modifier.styles + trapModifier.styles)
        }

        if (scopeId != null) {
            val scopeModifier = FocusManagement.createFocusScope(scopeId)
            modifier = Modifier(modifier.styles + scopeModifier.styles)
        }

        // TODO: Implement rendering with focus management
        return receiver
    }
} 