package codes.yousef.summon.components.feedback

import codes.yousef.summon.components.display.IconDefaults
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.input.ButtonVariant
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.runtime.LocalPlatformRenderer

// Import the AlertVariant enum from its own file

/**
 * A composable that displays messages to the user, often requiring acknowledgment or action.
 * Provides slots for icons, title, message, and actions.
 *
 * @param modifier Modifier applied to the alert container.
 * @param variant The semantic variant of the alert, influences default styling and icon.
 * @param onDismiss Optional callback invoked when the user attempts to dismiss the alert (e.g., clicks a close button).
 *                  Providing this typically makes a default close button appear.
 * @param icon Optional composable slot for an icon, placed at the start.
 * @param title Optional composable slot for the alert title.
 * @param content The main composable content/message of the alert.
 * @param actions Optional composable slot for action buttons, typically placed at the end.
 */
@Composable
fun Alert(
    modifier: Modifier = Modifier(),
    variant: AlertVariant = AlertVariant.INFO,
    onDismiss: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit // Main message content
) {
    val composer = CompositionLocal.currentComposer

    // Apply variant-specific styling
    val variantModifier = when (variant) {
        AlertVariant.INFO -> Modifier()
            .background("#e3f2fd") // Light blue
            .border("1px", "solid", "#2196f3")
            .color("#0d47a1")
            .padding("16px")

        AlertVariant.SUCCESS -> Modifier()
            .background("#e8f5e9") // Light green
            .border("1px", "solid", "#4caf50")
            .color("#1b5e20")
            .padding("16px")

        AlertVariant.WARNING -> Modifier()
            .background("#fff8e1") // Light amber
            .border("1px", "solid", "#ffc107")
            .color("#ff6f00")
            .padding("16px")

        AlertVariant.ERROR -> Modifier()
            .background("#ffebee") // Light red
            .border("1px", "solid", "#f44336")
            .color("#b71c1c")
            .padding("16px")

        AlertVariant.NEUTRAL -> Modifier()
            .background("#f5f5f5") // Light gray
            .border("1px", "solid", "#9e9e9e")
            .color("#212121")
            .padding("16px")
    }

    val finalModifier = variantModifier.then(modifier)

    // Determine default icon based on variant if none provided
    val defaultIcon: (@Composable () -> Unit)? = when (variant) {
        AlertVariant.INFO -> ({ IconDefaults.Info() })
        AlertVariant.SUCCESS -> ({ IconDefaults.CheckCircle() })
        AlertVariant.WARNING -> ({ IconDefaults.Warning() })
        AlertVariant.ERROR -> ({ IconDefaults.Error() })
        AlertVariant.NEUTRAL -> ({ IconDefaults.Info() })
    }
    val displayIcon = icon ?: defaultIcon

    composer?.startNode() // Start Alert node
    if (composer?.inserting == true) {
        val renderer = LocalPlatformRenderer.current
        // Use the renderAlertContainer method with the new signature
        renderer.renderAlertContainer(
            variant = variant,
            modifier = finalModifier
        ) { // Pass the internal layout as the content lambda
            // Create a structured and accessible alert with appropriate layout
            Row {
                // Icon section
                if (displayIcon != null) {
                    Column(Modifier().padding("0 8px 0 0")) {
                        displayIcon()
                    }
                }

                // Content section (takes most of the space)
                Column(Modifier().width("100%")) {
                    if (title != null) {
                        Column(Modifier().padding("0 0 8px 0")) {
                            title()
                        }
                    }
                    content() // The main content lambda passed to Alert
                }

                // Action buttons section
                if (actions != null) {
                    Column(Modifier().padding("0 0 0 16px")) {
                        actions()
                    }
                }

                // Dismiss button (if provided)
                if (onDismiss != null) {
                    Column(Modifier().padding("0 0 0 8px")) {
                        Button(
                            onClick = onDismiss,
                            label = "×", // Consider using an Icon component
                            modifier = Modifier().padding("4px"),
                            variant = ButtonVariant.GHOST,
                            disabled = false,
                            iconName = null
                        )
                    }
                }
            }
        }
    }

    composer?.endNode() // End Alert node
}

/**
 * Convenience overload for simple text alerts.
 */
@Composable
fun Alert(
    message: String,
    modifier: Modifier = Modifier(),
    variant: AlertVariant = AlertVariant.INFO,
    onDismiss: (() -> Unit)? = null,
    title: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null
) {
    Alert(
        modifier = modifier,
        variant = variant,
        onDismiss = onDismiss,
        icon = icon,
        title = title?.let { { Text(it) } }, // Wrap title string in Text composable
        actions = actions,
        content = { Text(message) } // Wrap message string in Text composable
    )
}
