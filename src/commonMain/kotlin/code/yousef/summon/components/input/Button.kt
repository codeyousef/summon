package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.components.display.Icon
import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier

/**
 * A button component that triggers an action when clicked.
 *
 * Buttons are used to trigger actions or events, such as submitting a form,
 * opening a dialog, canceling an action, or performing a delete operation.
 *
 * @param onClick The callback to invoke when the button is clicked
 * @param label The text to display on the button
 * @param modifier The modifier to apply to this composable
 * @param variant The visual style variant of the button (primary, secondary, etc.)
 * @param disabled Whether the button is disabled and cannot be clicked
 * @param iconName Optional icon to display alongside the button text
 * @param iconPosition Position of the icon relative to the label (start or end)
 * 
 * @sample code.yousef.summon.components.input.ButtonSamples.BasicButton
 * @sample code.yousef.summon.components.input.ButtonSamples.ButtonWithIcon
 * @sample code.yousef.summon.components.input.ButtonSamples.DisabledButton
 */
@Composable
fun Button(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier(),
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    disabled: Boolean = false,
    iconName: String? = null,
    iconPosition: IconPosition = IconPosition.START
) {
    val renderer = LocalPlatformRenderer.current
    
    // Apply variant-specific styling to the modifier
    val finalModifier = when (variant) {
        ButtonVariant.PRIMARY -> modifier
            .background("#0d6efd")
            .color("#ffffff")
        ButtonVariant.SECONDARY -> modifier
            .background("#6c757d")
            .color("#ffffff")
        ButtonVariant.DANGER -> modifier
            .background("#dc3545")
            .color("#ffffff")
        ButtonVariant.SUCCESS -> modifier
            .background("#198754")
            .color("#ffffff")
        ButtonVariant.WARNING -> modifier
            .background("#ffc107")
            .color("#000000")
        ButtonVariant.INFO -> modifier
            .background("#0dcaf0")
            .color("#000000")
        ButtonVariant.LINK -> modifier
            .background("transparent")
            .color("#0d6efd")
        ButtonVariant.GHOST -> modifier
            .background("transparent")
            .color("inherit")
        ButtonVariant.TERTIARY -> modifier
            .background("#f8f9fa")
            .color("#000000")
    }
        .style("cursor", if (disabled) "not-allowed" else "pointer")
        .style("pointer-events", if (disabled) "none" else "auto")
        .style("onClick", onClick.toString()) // Store onClick as a custom property that the renderer will handle
    
    // Use the renderButton method with just modifier and content
    renderer.renderButton(
        modifier = finalModifier
    ) {
        // Button content
        if (iconName != null && iconPosition == IconPosition.START) {
            Icon(iconName)
        }
        
        Text(label)
        
        if (iconName != null && iconPosition == IconPosition.END) {
            Icon(iconName)
        }
    }
}

/**
 * Button style variant
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
 * Position of the icon relative to the label
 */
enum class IconPosition {
    START,
    END
}

/**
 * Contains sample implementations of the Button component for documentation.
 */
object ButtonSamples {
    /**
     * Example of a basic button.
     */
    @Composable
    fun BasicButton() {
        Button(
            onClick = { println("Button clicked!") },
            label = "Click Me"
        )
    }
    
    /**
     * Example of a button with an icon.
     */
    @Composable
    fun ButtonWithIcon() {
        Button(
            onClick = { /* handle download */ },
            label = "Download",
            iconName = "download",
            variant = ButtonVariant.PRIMARY
        )
    }
    
    /**
     * Example of a disabled button.
     */
    @Composable
    fun DisabledButton() {
        Button(
            onClick = { /* won't be called when disabled */ },
            label = "Submit",
            disabled = true,
            variant = ButtonVariant.SECONDARY
        )
    }
} 