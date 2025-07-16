package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.components.display.Icon
import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.*
import code.yousef.summon.modifier.ModifierExtras.withAttribute

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

    // Apply base button styling
    val baseModifier = modifier
        .style("display", "inline-flex")
        .style("align-items", "center")
        .style("justify-content", "center")
        .style("padding", "8px 16px")
        .style("border", "none")
        .style("border-radius", "6px")
        .style("cursor", "pointer")
        .style("transition", "all 0.2s ease")
        .style("margin", "0.25rem")
        .hover(Modifier()
            .style("transform", "translateY(-1px)")
        )

    // Apply variant-specific styling to the modifier
    val finalModifier = when (variant) {
        ButtonVariant.PRIMARY -> baseModifier
            .style("background-color", "#0d6efd")
            .style("color", "#ffffff")
            .transition("all 0.2s ease")
        ButtonVariant.SECONDARY -> baseModifier
            .style("background-color", "#6c757d")
            .style("color", "#ffffff")
            .transition("all 0.2s ease")
        ButtonVariant.DANGER -> baseModifier
            .style("background-color", "#dc3545 !important")
            .style("color", "#ffffff !important")
            .style("border", "none !important")
            .padding("10px 16px")
            .borderRadius("30px")
            .fontWeight("700")
            .style("text-transform", "capitalize !important")
            .style("box-shadow", "0 2px 4px rgba(220, 53, 69, 0.4) !important")
            .transition("all 0.2s ease")
            .hover(Modifier()
                .style("background-color", "#bb2d3b !important")
                .style("transform", "translateY(-3px) !important")
                .style("box-shadow", "0 4px 8px rgba(220, 53, 69, 0.5) !important")
            )
        ButtonVariant.SUCCESS -> baseModifier
            .style("background-color", "#198754 !important")
            .style("color", "#ffffff !important")
            .padding("8px")
            .borderRadius("4px")
            .fontWeight("500")
            .transition("all 0.2s ease")
            .hover(Modifier()
                .style("background-color", "#157347 !important")
            )
        ButtonVariant.WARNING -> baseModifier
            .style("background-color", "#ffc107 !important")
            .style("color", "#000000 !important")
            .padding("8px")
            .borderRadius("4px")
            .fontWeight("500")
            .transition("all 0.2s ease")
            .hover(Modifier()
                .style("background-color", "#ffca2c !important")
            )
        ButtonVariant.INFO -> baseModifier
            .style("background-color", "#0dcaf0 !important")
            .style("color", "#000000 !important")
            .padding("8px")
            .borderRadius("4px")
            .fontWeight("500")
            .transition("all 0.2s ease")
            .hover(Modifier()
                .style("background-color", "#31d2f2 !important")
            )
        ButtonVariant.LINK -> baseModifier
            .style("background-color", "transparent !important")
            .style("color", "#0d6efd !important")
            .padding("8px")
            .fontWeight("500")
            .style("text-decoration", "underline")
            .transition("all 0.2s ease")
            .hover(Modifier()
                .style("color", "#0a58ca !important")
            )
        ButtonVariant.GHOST -> baseModifier
            .style("background-color", "transparent !important")
            .style("color", "inherit !important")
            .padding("8px")
            .borderRadius("4px")
            .border("1px", "solid", "transparent")
            .transition("all 0.2s ease")
            .hover(Modifier()
                .style("background-color", "rgba(0, 0, 0, 0.05) !important")
            )
        ButtonVariant.TERTIARY -> baseModifier
            .style("background-color", "#f8f9fa !important")
            .style("color", "#000000 !important")
            .padding("8px")
            .borderRadius("4px")
            .border("1px", "solid", "#dee2e6")
            .fontWeight("500")
            .transition("all 0.2s ease")
            .hover(Modifier()
                .style("background-color", "#e9ecef !important")
            )
    }
        .style("cursor", if (disabled) "not-allowed" else "pointer")
        .style("pointer-events", if (disabled) "none" else "auto")
        // Add data-variant attribute for CSS targeting
        .withAttribute("data-variant", variant.name.lowercase())

    // Use the renderButton method, passing onClick, modifier and content
    renderer.renderButton(
        onClick = if (disabled) { {} } else onClick,
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
