package codes.yousef.summon.components.feedback

import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.mutableStateOf
import codes.yousef.summon.runtime.remember

/**
 * Toast notification variants that determine appearance, behavior, and semantic meaning.
 *
 * These variants provide consistent styling and appropriate visual cues for different
 * types of notifications. Each variant includes specific colors, icons, and default
 * duration settings that align with the urgency and importance of the message.
 *
 * ## Design Guidelines
 * - **INFO**: Use for neutral information that helps users understand context
 * - **SUCCESS**: Use to confirm completed actions and positive outcomes
 * - **WARNING**: Use for important notices that need attention but aren't critical
 * - **ERROR**: Use for failures and critical issues that require immediate action
 *
 * @see Toast for implementation details
 * @see ToastManager for programmatic toast management
 * @since 1.0.0
 */
enum class ToastVariant {
    /** Informational toast with blue styling. Use for helpful, neutral information. */
    INFO,

    /** Success toast with green styling. Use for confirmations and positive outcomes. */
    SUCCESS,

    /** Warning toast with orange styling. Use for important notices requiring attention. */
    WARNING,

    /** Error toast with red styling. Use for failures and critical issues. */
    ERROR
}

/**
 * Toast positioning options that determine where notifications appear on screen.
 *
 * Choose positioning based on your application layout and user experience needs:
 * - **Top positions**: Good for notifications that shouldn't interfere with main content
 * - **Bottom positions**: Better accessibility, less likely to cover important UI
 * - **Corner positions**: Unobtrusive, good for multiple toasts
 * - **Center positions**: More prominent, use for important notifications
 *
 * ## Accessibility Considerations
 * - Bottom positions are generally more accessible
 * - Consider screen reader announcement order with positioning
 * - Ensure toasts don't cover critical interactive elements
 *
 * @see ToastContainer for positioning implementation
 * @since 1.0.0
 */
enum class ToastPosition {
    /** Top-left corner. Unobtrusive positioning for secondary notifications. */
    TOP_LEFT,

    /** Top-center. Prominent positioning for important notifications. */
    TOP_CENTER,

    /** Top-right corner. Common position that doesn't interfere with navigation. */
    TOP_RIGHT,

    /** Bottom-left corner. Accessible positioning with good visibility. */
    BOTTOM_LEFT,

    /** Bottom-center. Central positioning with good accessibility. */
    BOTTOM_CENTER,

    /** Bottom-right corner. Popular choice for unobtrusive notifications. */
    BOTTOM_RIGHT
}

/**
 * Data class representing a toast notification with all its properties and behavior.
 *
 * ToastData encapsulates all the information needed to display and manage a toast
 * notification, including its content, appearance, timing, and interaction capabilities.
 *
 * @property id Unique identifier for the toast, used for tracking and dismissal.
 * @property message The text content to display in the toast notification.
 * @property variant The semantic variant that determines styling and icon (default: INFO).
 * @property duration Duration in milliseconds before auto-dismissal. Use 0 for no auto-dismiss.
 * @property dismissible Whether the toast can be manually dismissed by the user.
 * @property action Optional action button configuration for user interaction.
 *
 * @see ToastVariant for available styling options
 * @see ToastAction for action button configuration
 * @since 1.0.0
 */
data class ToastData(
    val id: String,
    val message: String,
    val variant: ToastVariant = ToastVariant.INFO,
    val duration: Long = 4000, // milliseconds
    val dismissible: Boolean = true,
    val action: ToastAction? = null
)

/**
 * Configuration for toast action buttons that provide user interaction capabilities.
 *
 * Toast actions allow users to perform related operations directly from the notification,
 * such as undoing an action, retrying a failed operation, or navigating to related content.
 *
 * ## Design Guidelines
 * - Keep action labels short and action-oriented (e.g., "Undo", "Retry", "View")
 * - Limit to one action per toast for clarity
 * - Use actions for immediate, related operations
 * - Ensure actions are accessible via keyboard navigation
 *
 * @property label The text to display on the action button. Should be concise and action-oriented.
 * @property onClick Callback invoked when the action button is clicked.
 *
 * @see ToastData for toast configuration
 * @since 1.0.0
 */
data class ToastAction(
    val label: String,
    val onClick: () -> Unit
)

/**
 * A toast notification component that displays temporary messages with rich functionality.
 *
 * Toast notifications provide unobtrusive feedback to users about operations, events, and
 * state changes. They appear temporarily and can include actions for user interaction.
 * Unlike snackbars, toasts can stack and appear in various screen positions.
 *
 * ## Features
 * - **Multiple variants**: Info, success, warning, and error with appropriate styling
 * - **Flexible positioning**: Appears at configurable screen locations
 * - **Auto-dismissal**: Configurable timeout with automatic removal
 * - **Manual dismissal**: User can close toasts manually when dismissible
 * - **Action buttons**: Optional interactive buttons for related actions
 * - **Accessibility**: Full screen reader support and keyboard navigation
 * - **Stacking support**: Multiple toasts can be displayed simultaneously
 *
 * ## Basic Usage
 * ```kotlin
 * // Simple toast notification
 * val toastData = ToastData(
 *     id = "success-1",
 *     message = "File saved successfully!",
 *     variant = ToastVariant.SUCCESS
 * )
 *
 * Toast(
 *     toast = toastData,
 *     onDismiss = { /* handle dismissal */ }
 * )
 * ```
 *
 * ## Toast with Action
 * ```kotlin
 * val toastWithAction = ToastData(
 *     id = "undo-1",
 *     message = "Item deleted",
 *     variant = ToastVariant.INFO,
 *     duration = 5000,
 *     action = ToastAction(
 *         label = "Undo",
 *         onClick = { restoreItem() }
 *     )
 * )
 *
 * Toast(
 *     toast = toastWithAction,
 *     onDismiss = { /* handle dismissal */ }
 * )
 * ```
 *
 * ## Integration with ToastManager
 * ```kotlin
 * @Composable
 * fun FileOperations() {
 *     val toastManager = rememberToastManager()
 *
 *     fun saveFile() {
 *         try {
 *             fileService.save()
 *             toastManager.showSuccess("File saved successfully!")
 *         } catch (e: Exception) {
 *             toastManager.showError(
 *                 message = "Failed to save file: ${e.message}",
 *                 duration = 8000
 *             )
 *         }
 *     }
 *
 *     Button(onClick = { saveFile() }) {
 *         Text("Save File")
 *     }
 * }
 * ```
 *
 * ## Accessibility Features
 * - **Screen reader announcements**: Toast content is announced with appropriate urgency
 * - **Keyboard navigation**: Action buttons are keyboard accessible
 * - **Focus management**: Proper focus handling for interactive elements
 * - **High contrast support**: Adapts to system contrast preferences
 * - **Motion sensitivity**: Respects reduced motion preferences
 *
 * ## Design Guidelines
 * - Keep messages concise and actionable
 * - Use appropriate variants for message context
 * - Position toasts to avoid covering critical UI elements
 * - Limit concurrent toasts to prevent overwhelming users
 * - Provide actions for recoverable operations
 *
 * @param toast The toast data containing message, variant, duration, and other properties.
 * @param onDismiss Callback invoked when the toast is dismissed (manually or automatically).
 * @param modifier Modifier applied to the toast container for custom styling.
 *
 * @see ToastData for toast configuration options
 * @see ToastManager for programmatic toast management
 * @see ToastContainer for managing multiple toasts
 * @see ToastProvider for app-wide toast functionality
 * @sample codes.yousef.summon.samples.feedback.ToastSamples.basicUsage
 * @sample codes.yousef.summon.samples.feedback.ToastSamples.toastWithAction
 * @since 1.0.0
 */
@Composable
fun Toast(
    toast: ToastData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current

    renderer.renderToast(
        toast = toast,
        onDismiss = onDismiss,
        modifier = modifier
    )
}

/**
 * Toast container that manages multiple toast notifications.
 *
 * @param toasts List of active toast notifications
 * @param position Position where toasts should appear
 * @param onDismiss Callback invoked when a toast is dismissed
 * @param modifier Modifier applied to the container
 */
@Composable
fun ToastContainer(
    toasts: List<ToastData>,
    position: ToastPosition = ToastPosition.TOP_RIGHT,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier()
) {
    if (toasts.isEmpty()) return

    val positionModifier = when (position) {
        ToastPosition.TOP_LEFT -> Modifier()
            .style("position", "fixed")
            .style("top", "16px")
            .style("left", "16px")
            .zIndex(9999)

        ToastPosition.TOP_CENTER -> Modifier()
            .style("position", "fixed")
            .style("top", "16px")
            .style("left", "50%")
            .style("transform", "translateX(-50%)")
            .zIndex(9999)

        ToastPosition.TOP_RIGHT -> Modifier()
            .style("position", "fixed")
            .style("top", "16px")
            .style("right", "16px")
            .zIndex(9999)

        ToastPosition.BOTTOM_LEFT -> Modifier()
            .style("position", "fixed")
            .style("bottom", "16px")
            .style("left", "16px")
            .zIndex(9999)

        ToastPosition.BOTTOM_CENTER -> Modifier()
            .style("position", "fixed")
            .style("bottom", "16px")
            .style("left", "50%")
            .style("transform", "translateX(-50%)")
            .zIndex(9999)

        ToastPosition.BOTTOM_RIGHT -> Modifier()
            .style("position", "fixed")
            .style("bottom", "16px")
            .style("right", "16px")
            .zIndex(9999)
    }

    Box(
        modifier = modifier
            .then(positionModifier)
            .style("display", "flex")
            .style("flex-direction", "column")
            .style("gap", "8px")
            .maxWidth("400px")
    ) {
        toasts.forEach { toast ->
            Toast(
                toast = toast,
                onDismiss = { onDismiss(toast.id) }
            )
        }
    }
}

/**
 * Simple toast notification manager for managing toast state
 */
class ToastManager {
    private val _toasts = mutableStateOf<List<ToastData>>(emptyList())
    val toasts: List<ToastData> get() = _toasts.value

    /**
     * Show a toast notification
     */
    fun showToast(
        message: String,
        variant: ToastVariant = ToastVariant.INFO,
        duration: Long = 4000,
        dismissible: Boolean = true,
        action: ToastAction? = null
    ) {
        val toast = ToastData(
            id = generateId(),
            message = message,
            variant = variant,
            duration = duration,
            dismissible = dismissible,
            action = action
        )

        _toasts.value = _toasts.value + toast

        // Auto-dismiss after duration if duration > 0
        if (duration > 0) {
            // Note: In a real implementation, you'd use a proper timer mechanism
            // This is a simplified version for demonstration
        }
    }

    /**
     * Dismiss a specific toast by ID
     */
    fun dismissToast(id: String) {
        _toasts.value = _toasts.value.filterNot { it.id == id }
    }

    /**
     * Dismiss all toasts
     */
    fun dismissAll() {
        _toasts.value = emptyList()
    }

    /**
     * Convenience methods for different toast types
     */
    fun showInfo(message: String, duration: Long = 4000) {
        showToast(message, ToastVariant.INFO, duration)
    }

    fun showSuccess(message: String, duration: Long = 4000) {
        showToast(message, ToastVariant.SUCCESS, duration)
    }

    fun showWarning(message: String, duration: Long = 6000) {
        showToast(message, ToastVariant.WARNING, duration)
    }

    fun showError(message: String, duration: Long = 8000) {
        showToast(message, ToastVariant.ERROR, duration)
    }

    private fun generateId(): String {
        return "toast-${++idCounter}-${(0..999).random()}"
    }

    companion object {
        private var idCounter = 0
    }
}

/**
 * Remember a toast manager instance
 */
@Composable
fun rememberToastManager(): ToastManager {
    return remember { ToastManager() }
}

/**
 * Composable function that provides toast functionality to its children
 *
 * @param position Position where toasts should appear
 * @param content Content that can access the toast manager
 */
@Composable
fun ToastProvider(
    position: ToastPosition = ToastPosition.TOP_RIGHT,
    content: @Composable (ToastManager) -> Unit
) {
    val toastManager = rememberToastManager()

    Box {
        content(toastManager)

        ToastContainer(
            toasts = toastManager.toasts,
            position = position,
            onDismiss = { id -> toastManager.dismissToast(id) }
        )
    }
}