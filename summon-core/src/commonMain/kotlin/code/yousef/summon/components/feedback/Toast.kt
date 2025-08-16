package code.yousef.summon.components.feedback

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember

/**
 * Toast notification variants that determine appearance and behavior
 */
enum class ToastVariant {
    /** Default informational toast */
    INFO,
    /** Success toast with green styling */
    SUCCESS,
    /** Warning toast with orange styling */
    WARNING,
    /** Error toast with red styling */
    ERROR
}

/**
 * Toast positioning options
 */
enum class ToastPosition {
    /** Top-left corner */
    TOP_LEFT,
    /** Top-center */
    TOP_CENTER,
    /** Top-right corner */
    TOP_RIGHT,
    /** Bottom-left corner */
    BOTTOM_LEFT,
    /** Bottom-center */
    BOTTOM_CENTER,
    /** Bottom-right corner */
    BOTTOM_RIGHT
}

/**
 * Data class representing a toast notification
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
 * Data class for toast action buttons
 */
data class ToastAction(
    val label: String,
    val onClick: () -> Unit
)

/**
 * A toast notification component that displays temporary messages.
 * 
 * Features:
 * - Multiple variants (info, success, warning, error)
 * - Configurable positioning
 * - Auto-dismiss with configurable duration
 * - Optional action buttons
 * - Manual dismiss capability
 * - Accessible with proper ARIA attributes
 *
 * @param toast The toast data to display
 * @param onDismiss Callback invoked when the toast is dismissed
 * @param modifier Modifier applied to the toast container
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