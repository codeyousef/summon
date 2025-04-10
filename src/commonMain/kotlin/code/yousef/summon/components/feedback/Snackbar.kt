package code.yousef.summon.components.feedback

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.IconDefaults
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

/**
 * Variants for Snackbar components.
 * These correspond to common message types in UI design.
 */
enum class SnackbarVariant {
    /** Default/neutral message, usually gray */
    DEFAULT,

    /** Information message, usually blue */
    INFO,

    /** Success message, usually green */
    SUCCESS,

    /** Warning message, usually yellow or orange */
    WARNING,

    /** Error message, usually red */
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
 * Horizontal position for the Snackbar
 */
enum class SnackbarHorizontalPosition {
    START, CENTER, END
}

/**
 * Vertical position for the Snackbar
 */
enum class SnackbarVerticalPosition {
    TOP, BOTTOM
}

/**
 * A composable that displays a brief message at the bottom of the screen.
 *
 * Snackbars provide brief feedback about an operation through a message at the
 * bottom of the screen. They can include an optional action.
 *
 * @param message The message to display in the snackbar
 * @param modifier Modifier applied to the snackbar container
 * @param variant The semantic variant of the snackbar, influences styling
 * @param action Optional action button text
 * @param onAction Optional callback invoked when the action button is clicked
 * @param onDismiss Optional callback invoked when the snackbar is dismissed
 * @param icon Optional composable slot for an icon, placed at the start
 * @param duration Duration to display the snackbar before automatically dismissing
 * @param horizontalPosition Horizontal positioning of the snackbar
 * @param verticalPosition Vertical positioning of the snackbar
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
            // TODO: Replace with actual timer implementation once available
            // For now, we're just showing how it would work conceptually
            // We would start a timer here to dismiss after duration
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

    // Render the snackbar
    composer?.startNode() // Start Snackbar node
    if (composer?.inserting == true) {
        val renderer = getPlatformRenderer()
        // Render the snackbar container
        // TODO: Update when PlatformRenderer has specific snackbar rendering
        renderer.renderBox(finalModifier)
    }

    // Internal structure
    Row {
        // Icon
        if (displayIcon != null) {
            displayIcon()
            // Add spacing
            Text("", Modifier().width("8px"))
        }

        Text(message)

        // Action button
        if (action != null && onAction != null) {
            // Add spacing
            Text("", Modifier().width("16px"))

            // Use correct Button parameters
            composer?.startNode()
            if (composer?.inserting == true) {
                val renderer = getPlatformRenderer()
                val actionButtonModifier = Modifier()

                // Render action button
                renderer.renderButton(
                    onClick = {
                        onAction.invoke()
                        visible.value = false
                    },
                    enabled = true,
                    modifier = actionButtonModifier
                )
            }

            // Button content
            Text(action)

            composer?.endNode()
        }

        // Dismiss button
        if (onDismiss != null) {
            // Add spacing
            Text("", Modifier().width("8px"))

            // Use correct Button parameters
            composer?.startNode()
            if (composer?.inserting == true) {
                val renderer = getPlatformRenderer()
                val dismissButtonModifier = Modifier().width("24px").height("24px")

                // Render dismiss button
                renderer.renderButton(
                    onClick = {
                        onDismiss.invoke()
                        visible.value = false
                    },
                    enabled = true,
                    modifier = dismissButtonModifier
                )
            }

            // Button content
            Text("×")

            composer?.endNode()
        }
    }

    composer?.endNode() // End Snackbar node
} 