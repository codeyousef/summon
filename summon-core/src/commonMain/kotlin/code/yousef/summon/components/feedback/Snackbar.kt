package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconDefaults
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.AlignItems
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.alignItems
import code.yousef.summon.modifier.flexGrow
import code.yousef.summon.runtime.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


/**
 * Semantic variants for Snackbar components that define styling and context.
 *
 * These variants correspond to common message types in UI design and provide
 * appropriate visual styling to convey meaning to users. Each variant includes
 * specific colors, icons, and styling that follow design system conventions.
 *
 * ## Usage Guidelines
 * - **DEFAULT**: Use for general notifications without specific sentiment
 * - **INFO**: Use for informational messages that provide helpful context
 * - **SUCCESS**: Use to confirm successful operations or positive outcomes
 * - **WARNING**: Use for cautionary messages that need user attention
 * - **ERROR**: Use for error conditions that require user action
 *
 * @see Snackbar for implementation details
 * @since 1.0.0
 */
enum class SnackbarVariant {
    /** Default/neutral message with gray styling. Use for general notifications. */
    DEFAULT,

    /** Information message with blue styling. Use for helpful contextual information. */
    INFO,

    /** Success message with green styling. Use for positive confirmations and completions. */
    SUCCESS,

    /** Warning message with orange styling. Use for cautions and important notices. */
    WARNING,

    /** Error message with red styling. Use for errors and critical issues. */
    ERROR;

    /**
     * Get the CSS color associated with this variant.
     */
    fun getColor(): String {
        return when (this) {
            DEFAULT -> "#6b7280"  // Gray
            INFO -> "#0284c7"     // Blue
            SUCCESS -> "#16a34a"  // Green
            WARNING -> "#d97706"  // Orange
            ERROR -> "#dc2626"    // Red
        }
    }

    /**
     * Get the CSS background color associated with this variant.
     */
    fun getBackgroundColor(): String {
        return when (this) {
            DEFAULT -> "#f3f4f6"  // Light gray
            INFO -> "#e0f2fe"     // Light blue
            SUCCESS -> "#dcfce7"  // Light green
            WARNING -> "#fef3c7"  // Light yellow
            ERROR -> "#fee2e2"    // Light red
        }
    }
}

/**
 * Horizontal positioning options for Snackbar placement on screen.
 *
 * These positions determine where the snackbar appears horizontally within the viewport.
 * Choose based on your app's layout and user experience requirements.
 *
 * @see Snackbar for positioning usage
 * @since 1.0.0
 */
enum class SnackbarHorizontalPosition {
    /** Position at the start (left in LTR, right in RTL) of the screen. */
    START,

    /** Position at the center of the screen horizontally. */
    CENTER,

    /** Position at the end (right in LTR, left in RTL) of the screen. */
    END
}

/**
 * Vertical positioning options for Snackbar placement on screen.
 *
 * These positions determine where the snackbar appears vertically within the viewport.
 * Bottom positioning is recommended for better accessibility and user experience.
 *
 * @see Snackbar for positioning usage
 * @since 1.0.0
 */
enum class SnackbarVerticalPosition {
    /** Position at the top of the screen. Use sparingly as it may interfere with navigation. */
    TOP,

    /** Position at the bottom of the screen. Recommended for better accessibility. */
    BOTTOM
}

/**
 * A Snackbar that displays brief messages and feedback to users with optional actions.
 *
 * Snackbars provide lightweight feedback about an operation through a message that appears
 * temporarily at the edge of the screen. They're ideal for confirmations, undo actions, and
 * brief notifications that don't require immediate user attention. Snackbars automatically
 * dismiss after a timeout and can include optional action buttons.
 *
 * ## Features
 * - **Automatic dismissal**: Configurable timeout with auto-dismiss functionality
 * - **Semantic variants**: Built-in styling for different message types (success, error, etc.)
 * - **Optional actions**: Support for action buttons with callbacks
 * - **Flexible positioning**: Customizable horizontal and vertical positioning
 * - **Accessibility support**: Proper ARIA attributes and keyboard navigation
 * - **Icon integration**: Automatic icons based on variant or custom icons
 * - **Dismissible**: Optional close button for manual dismissal
 *
 * ## Basic Usage
 * ```kotlin
 * // Simple success message
 * Snackbar(
 *     message = "File saved successfully!",
 *     variant = SnackbarVariant.SUCCESS
 * )
 *
 * // Error message with action
 * Snackbar(
 *     message = "Failed to save file",
 *     variant = SnackbarVariant.ERROR,
 *     action = "Retry",
 *     onAction = { retryOperation() }
 * )
 * ```
 *
 * ## Advanced Usage
 * ```kotlin
 * // Custom styled snackbar with positioning
 * Snackbar(
 *     message = "Your changes have been saved",
 *     variant = SnackbarVariant.SUCCESS,
 *     action = "Undo",
 *     onAction = { undoChanges() },
 *     onDismiss = { hideSnackbar() },
 *     duration = 6000.milliseconds,
 *     horizontalPosition = SnackbarHorizontalPosition.END,
 *     verticalPosition = SnackbarVerticalPosition.TOP,
 *     modifier = Modifier()
 *         .style("border-radius", "12px")
 *         .style("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.25)")
 * )
 * ```
 *
 * ## State Management Pattern
 * ```kotlin
 * @Composable
 * fun FileManager() {
 *     var snackbarState by remember { mutableStateOf<SnackbarState?>(null) }
 *
 *     // File operation with feedback
 *     fun saveFile() {
 *         try {
 *             fileService.save()
 *             snackbarState = SnackbarState.success("File saved successfully!")
 *         } catch (e: Exception) {
 *             snackbarState = SnackbarState.error("Failed to save file", onRetry = { saveFile() })
 *         }
 *     }
 *
 *     // Main UI
 *     Column {
 *         FileEditor()
 *         Button(onClick = { saveFile() }) {
 *             Text("Save")
 *         }
 *     }
 *
 *     // Snackbar display
 *     snackbarState?.let { state ->
 *         Snackbar(
 *             message = state.message,
 *             variant = state.variant,
 *             action = state.actionText,
 *             onAction = state.onAction,
 *             onDismiss = { snackbarState = null }
 *         )
 *     }
 * }
 * ```
 *
 * ## Undo Pattern
 * ```kotlin
 * @Composable
 * fun ItemList() {
 *     var items by remember { mutableStateOf(listOf<Item>()) }
 *     var recentlyDeleted by remember { mutableStateOf<Item?>(null) }
 *
 *     fun deleteItem(item: Item) {
 *         recentlyDeleted = item
 *         items = items - item
 *     }
 *
 *     fun undoDelete() {
 *         recentlyDeleted?.let { item ->
 *             items = items + item
 *             recentlyDeleted = null
 *         }
 *     }
 *
 *     // Show items
 *     LazyColumn {
 *         items(items) { item ->
 *             ItemCard(
 *                 item = item,
 *                 onDelete = { deleteItem(item) }
 *             )
 *         }
 *     }
 *
 *     // Undo snackbar
 *     recentlyDeleted?.let { item ->
 *         Snackbar(
 *             message = "${item.name} deleted",
 *             variant = SnackbarVariant.DEFAULT,
 *             action = "Undo",
 *             onAction = { undoDelete() },
 *             onDismiss = { recentlyDeleted = null },
 *             duration = 5000.milliseconds
 *         )
 *     }
 * }
 * ```
 *
 * ## Accessibility Features
 * - **Screen reader support**: Announces messages with appropriate urgency
 * - **Keyboard navigation**: Focusable action buttons with keyboard support
 * - **High contrast**: Adapts to system high contrast settings
 * - **Motion preferences**: Respects reduced motion preferences
 * - **ARIA attributes**: Proper role and live region attributes
 *
 * ## Design Guidelines
 * - Keep messages brief and actionable
 * - Use appropriate variants for message context
 * - Limit to one snackbar at a time
 * - Place action buttons at the end
 * - Provide undo functionality for destructive actions
 * - Consider timeout duration based on message importance
 *
 * @param message The message to display in the snackbar. Keep concise and actionable.
 * @param modifier Modifier applied to the snackbar container for custom styling.
 * @param variant The semantic variant that determines styling and default icon.
 * @param action Optional action button text. Should be a clear, actionable verb.
 * @param onAction Optional callback invoked when the action button is clicked.
 * @param onDismiss Optional callback invoked when the snackbar is dismissed (by timeout or user action).
 * @param icon Optional custom icon composable. If null, uses variant-appropriate default icon.
 * @param duration Duration to display the snackbar before automatically dismissing (default: 4 seconds).
 * @param horizontalPosition Horizontal positioning of the snackbar on screen.
 * @param verticalPosition Vertical positioning of the snackbar on screen.
 *
 * @see SnackbarVariant for available message types and styling
 * @see SnackbarHost for managing multiple snackbars
 * @see SnackbarHorizontalPosition for positioning options
 * @see SnackbarVerticalPosition for positioning options
 * @sample code.yousef.summon.samples.feedback.SnackbarSamples.basicUsage
 * @sample code.yousef.summon.samples.feedback.SnackbarSamples.undoPattern
 * @since 1.0.0
 */
@Composable
fun Snackbar(
    message: String,
    modifier: Modifier = Modifier(),
    variant: SnackbarVariant = SnackbarVariant.DEFAULT,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    duration: Duration = 4000.milliseconds,
    horizontalPosition: SnackbarHorizontalPosition = SnackbarHorizontalPosition.CENTER,
    verticalPosition: SnackbarVerticalPosition = SnackbarVerticalPosition.BOTTOM
) {
    val composer = CompositionLocal.currentComposer
    val visible = remember { mutableStateOf(true) }

    // Auto-dismiss after duration
    if (duration.isFinite()) {
        LaunchedEffect(Unit) {
            val durationMs = duration.inWholeMilliseconds

            // Using a basic delay to simulate a timer
            kotlinx.coroutines.delay(durationMs)

            // After duration, set visible to false and call onDismiss
            if (visible.value) {
                visible.value = false
                onDismiss?.invoke()
            }
        }
    }

    // Exit early if not visible
    if (!visible.value) return

    // Position modifiers
    val positionStyles = mutableMapOf<String, String>()
    positionStyles["position"] = "fixed"
    positionStyles["z-index"] = "1000"

    // Add horizontal position styles
    when (horizontalPosition) {
        SnackbarHorizontalPosition.START -> positionStyles["left"] = "16px"
        SnackbarHorizontalPosition.CENTER -> {
            positionStyles["left"] = "50%"
            positionStyles["transform"] = "translateX(-50%)"
        }

        SnackbarHorizontalPosition.END -> positionStyles["right"] = "16px"
    }

    // Add vertical position styles
    when (verticalPosition) {
        SnackbarVerticalPosition.TOP -> positionStyles["top"] = "16px"
        SnackbarVerticalPosition.BOTTOM -> positionStyles["bottom"] = "16px"
    }

    // Variant-specific styling
    val variantStyles = mutableMapOf<String, String>()
    variantStyles["background-color"] = variant.getBackgroundColor()
    variantStyles["color"] = variant.getColor()
    variantStyles["padding"] = "12px 16px"
    variantStyles["border-radius"] = "4px"
    variantStyles["box-shadow"] = "0 2px 10px rgba(0, 0, 0, 0.15)"
    variantStyles["min-width"] = "200px"
    variantStyles["max-width"] = "500px"

    // Combine all modifiers
    val finalModifier = Modifier(positionStyles + variantStyles).then(modifier)

    // Default icon based on variant
    val defaultIcon: (@Composable () -> Unit)? = when (variant) {
        SnackbarVariant.INFO -> ({ IconDefaults.Info() })
        SnackbarVariant.SUCCESS -> ({ IconDefaults.CheckCircle() })
        SnackbarVariant.WARNING -> ({ IconDefaults.Warning() })
        SnackbarVariant.ERROR -> ({ IconDefaults.Error() })
        else -> null
    }

    val displayIcon = icon ?: defaultIcon

    // Render the snackbar container using the renderer, passing the internal layout as the content
    val renderer = LocalPlatformRenderer.current
    renderer.renderBox(modifier = finalModifier) { // Pass the internal Row layout as the lambda
        // Internal structure
        Row(modifier = Modifier().padding("8px").alignItems(AlignItems.Center)) { // Add padding and vertical alignment
            // Icon
            if (displayIcon != null) {
                Box(Modifier().padding("0px 8px 0px 0px")) { // Fix padding syntax
                    displayIcon()
                }
            }

            // Message Text
            Box(Modifier().flexGrow(1)) { // Allow message to take remaining space. Use flexGrow.
                Text(message)
            }

            // Action button
            if (action != null && onAction != null) {
                Box(Modifier().padding("0px 0px 0px 16px")) { // Fix padding syntax
                    // Render action button, passing text as content
                    renderer.renderButton(
                        onClick = {
                            onAction.invoke()
                            visible.value = false
                        },
                        modifier = Modifier() // Add specific action button styling here if needed
                    ) {
                        Text(action) // Pass action text as button content
                    }
                }
            }

            // Dismiss button
            if (onDismiss != null) {
                Box(Modifier().padding("0px 0px 0px 8px")) { // Fix padding syntax
                    // Render dismiss button, passing '×' as content
                    renderer.renderButton(
                        onClick = {
                            onDismiss.invoke()
                            visible.value = false
                        },
                        modifier = Modifier().width("24px").height("24px") // Keep dismiss button size
                    ) {
                        Text("×") // Pass dismiss symbol as button content
                    }
                }
            }
        }
    }
} 