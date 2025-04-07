package code.yousef.summon.components.input


import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.components.text.Text

/**
 * Visual style variants for the Button component.
 */
enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    TERTIARY,
    DANGER,
    SUCCESS,
    WARNING,
    INFO,
    LINK,
    GHOST
}

/**
 * Position of the icon relative to the label (Kept from original file)
 */
enum class IconPosition {
    START,
    END
}

// Helper to get variant-specific styles
private fun getVariantModifier(variant: ButtonVariant): Modifier {
    return when (variant) {
        ButtonVariant.PRIMARY -> Modifier().background("#0d6efd").color("#ffffff")
        ButtonVariant.SECONDARY -> Modifier().background("#6c757d").color("#ffffff")
        ButtonVariant.SUCCESS -> Modifier().background("#198754").color("#ffffff")
        ButtonVariant.DANGER -> Modifier().background("#dc3545").color("#ffffff")
        ButtonVariant.WARNING -> Modifier().background("#ffc107").color("#000000")
        ButtonVariant.INFO -> Modifier().background("#0dcaf0").color("#000000")
        ButtonVariant.TERTIARY -> Modifier().background("#e9ecef").color("#000000")
        ButtonVariant.LINK -> Modifier().background("transparent").color("#0d6efd").textDecoration("underline")
        ButtonVariant.GHOST -> Modifier().background("transparent").color("#6c757d")
    }
}

/**
 * A composable that displays a button and executes an action when clicked.
 *
 * @param onClick The callback to be invoked when the button is clicked
 * @param modifier The modifier to be applied to the button
 * @param enabled Controls the enabled state of the button. When false, the button will not be clickable
 * @param variant The visual style variant of the button
 * @param content The content to be displayed inside the button
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    content: @Composable () -> Unit
) {
    val renderer = PlatformRendererProvider.getPlatformRenderer()
    // In a real implementation, we would use variant to customize the renderer call
    renderer.renderButton(onClick = onClick, enabled = enabled, modifier = modifier)
    content()
    // In a real implementation, there would be a closing method call after content
}

/**
 * A simplified Button overload that takes text content directly.
 *
 * @param onClick The callback to be invoked when the button is clicked
 * @param modifier The modifier to be applied to the button
 * @param enabled Controls the enabled state of the button
 * @param variant The visual style variant of the button
 * @param text The text to display inside the button
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant
    ) {
        Text(text = text)
    }
}

/**
 * Convenience overload for Button that takes text and an optional icon.
 * NOTE: This overload is temporarily commented out as it depends on the Row composable,
 * which has not yet been refactored.
 */
/*
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    text: String,
    icon: @Composable (() -> Unit)? = null,
    iconPosition: IconPosition = IconPosition.START
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant
    ) {
        Row(modifier = Modifier.padding("0px")) { // Use Row to position icon and text
            if (iconPosition == IconPosition.START && icon != null) {
                icon()
                // TODO: Add spacing between icon and text if needed (e.g., Spacer(width = "8px"))
            }
            Text(text = text)
            if (iconPosition == IconPosition.END && icon != null) {
                // TODO: Add spacing between text and icon if needed
                icon()
            }
        }
    }
}
*/

// Keep samples, but update them to use the new @Composable function signatures
object ButtonSamples {
    @Composable
    fun BasicButton() {
        Button(
            text = "Click Me",
            onClick = { println("Button clicked!") }
        )
    }

    // Temporarily comment out samples that depend on the icon+text overload or Row
    /*
    @Composable
    fun ButtonWithIcon() {
        Button(
            text = "Download",
            onClick = { /* handle download */ },
            icon = { IconDefaults.Download() }, // Call the composable Icon
            variant = ButtonVariant.PRIMARY,
            iconPosition = IconPosition.START
        )
    }
    */

    @Composable
    fun DisabledButton() {
        Button(
            text = "Submit",
            onClick = { /* won't be called */ },
            enabled = false,
            variant = ButtonVariant.SECONDARY
        )
    }

    /*
    @Composable
    fun CustomContentButton() {
        Button(onClick = { /* ... */ }) {
            Row {
                IconDefaults.Add()
                Text("Create New Item")
            }
        }
    }
    */
}

// The old Button class and its methods are removed. 
