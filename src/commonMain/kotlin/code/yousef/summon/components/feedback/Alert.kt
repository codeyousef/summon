package code.yousef.summon.components.feedback

import code.yousef.summon.runtime.PlatformRendererProvider

import code.yousef.summon.components.display.IconDefaults
import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import components.feedback.AlertVariant
import code.yousef.summon.components.input.Button

import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Spacer

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
    
    // TODO: Apply variant-specific styling (background, border, text color) via modifier.
    val variantModifier = Modifier() // Replace with actual style lookup based on variant
        .padding("16px")
        .border("1px", "solid", "#ccc") // Example default border
    val finalModifier = variantModifier.then(modifier)

    // Determine default icon based on variant if none provided
    val defaultIcon: (@Composable () -> Unit)? = when (variant) {
        AlertVariant.INFO -> ({ IconDefaults.Info() })
        AlertVariant.SUCCESS -> ({ IconDefaults.CheckCircle() })
        AlertVariant.WARNING -> ({ IconDefaults.Warning() })
        AlertVariant.DANGER -> ({ IconDefaults.Error() })
    }
    val displayIcon = icon ?: defaultIcon

    composer?.startNode() // Start Alert node
    if (composer?.inserting == true) {
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        // Use the updated renderAlertContainer method
        renderer.renderAlertContainer(variant, finalModifier)
    }

    // --- Compose internal structure --- 
    // TODO: Ensure composition context places children correctly within the rendered Alert container.
    Row {
        if (displayIcon != null) {
            displayIcon()
            Spacer(Modifier().width("8px"))
        }
        
        Column {
            if (title != null) {
                title()
            }
            content()
        }
        
        if (actions != null) {
            Spacer(Modifier().width("16px"))
            actions()
        }
        
        if (onDismiss != null) {
            Spacer(Modifier().width("8px"))
            Button(onClick = onDismiss) {
                Text("×")
            }
        }
    }
    // --- End internal structure --- 
    
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
