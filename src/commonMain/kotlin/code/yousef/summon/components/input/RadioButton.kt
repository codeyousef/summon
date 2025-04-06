package code.yousef.summon.components.input

import code.yousef.summon.*
import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * A composable that displays a radio button, typically used as part of a group
 * where only one option can be selected at a time.
 *
 * State (which option is selected) must be managed externally and passed via the
 * `selected` parameter and the `onClick` lambda.
 *
 * @param selected Whether this radio button is currently selected.
 * @param onClick Lambda executed when this radio button is clicked. Typically, this lambda
 *                updates the external state to select this button.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param label Optional label text displayed alongside the radio button.
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null
) {
    val radioButtonData = RadioButtonData(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        label = label
    )

    println("Composable RadioButton function called with selected: $selected")
}

/**
 * Internal data class holding parameters for the RadioButton renderer.
 */
internal data class RadioButtonData(
    val selected: Boolean,
    val onClick: () -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: String?
) 