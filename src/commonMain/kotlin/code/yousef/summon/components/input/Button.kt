package code.yousef.summon.components.input

import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.components.ClickableComponent
import code.yousef.summon.components.FocusableComponent
import code.yousef.summon.components.display.Icon
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer

/**
 * A button component that triggers an action when clicked.
 *
 * Buttons are used to trigger actions or events, such as submitting a form,
 * opening a dialog, canceling an action, or performing a delete operation.
 *
 * @param label The text to display on the button
 * @param onClick The callback to invoke when the button is clicked
 * @param modifier The modifier to apply to this composable
 * @param variant The visual style variant of the button (primary, secondary, etc.)
 * @param disabled Whether the button is disabled and cannot be clicked
 * @param icon Optional icon to display alongside the button text
 * @param iconPosition Position of the icon relative to the label (start or end)
 * 
 * @sample code.yousef.summon.components.input.ButtonSamples.BasicButton
 * @sample code.yousef.summon.components.input.ButtonSamples.ButtonWithIcon
 * @sample code.yousef.summon.components.input.ButtonSamples.DisabledButton
 */
class Button(
    val label: String,
    val onClick: (Any) -> Unit = {},
    val modifier: Modifier = Modifier(),
    val variant: ButtonVariant = ButtonVariant.PRIMARY,
    val disabled: Boolean = false,
    val icon: Icon? = null,
    val iconPosition: IconPosition = IconPosition.START
) : Composable, ClickableComponent, FocusableComponent {
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
     * Renders this Button composable using the platform-specific renderer.
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderButton(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
}

/**
 * Contains sample implementations of the Button component for documentation.
 */
object ButtonSamples {
    /**
     * Example of a basic button.
     */
    fun BasicButton() {
        Button(
            label = "Click Me",
            onClick = { println("Button clicked!") }
        )
    }
    
    /**
     * Example of a button with an icon.
     */
    fun ButtonWithIcon() {
        Button(
            label = "Download",
            onClick = { /* handle download */ },
            icon = Icon.Download,
            variant = Button.ButtonVariant.PRIMARY
        )
    }
    
    /**
     * Example of a disabled button.
     */
    fun DisabledButton() {
        Button(
            label = "Submit",
            onClick = { /* won't be called when disabled */ },
            disabled = true,
            variant = Button.ButtonVariant.SECONDARY
        )
    }
} 