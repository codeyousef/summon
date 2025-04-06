package code.yousef.summon.components.input

import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.components.display.Icon
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * Style variants for the Button composable.
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
 * A composable that provides a clickable button with styling and optional content.
 *
 * Buttons allow users to trigger actions. They can contain text, icons, or other content.
 *
 * @param onClick Lambda executed when the button is clicked.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param enabled Controls the enabled state of the button. When `false`, onClick will not be invoked.
 * @param variant The visual style [ButtonVariant] of the button.
 * @param content The composable content to display inside the button. Typically [Text] and/or [Icon].
 *                The content is laid out in a Row layout by the renderer.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    content: @Composable () -> Unit
) {
    val buttonData = ButtonData(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant
    )

    println("Composable Button function called. Content lambda will be executed by renderer.")
}

/**
 * Internal data class holding non-content parameters for the Button.
 * Content lambda is handled separately by the composition process.
 */
data class ButtonData(
    val onClick: () -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val variant: ButtonVariant,
    val onLongClick: (() -> Unit)? = null,
    val type: String = "button" // button, submit, reset
) 