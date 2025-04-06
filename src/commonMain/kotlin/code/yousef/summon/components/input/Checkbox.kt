package code.yousef.summon.components.input

import code.yousef.summon.*
import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * A composable that displays a checkbox, allowing users to select a boolean state.
 *
 * This composable follows the state hoisting pattern. The caller provides the current
 * `checked` state and an `onCheckedChange` callback.
 *
 * @param checked Whether the checkbox is currently checked.
 * @param onCheckedChange Lambda invoked when the user clicks the checkbox, providing the new checked state.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param label Optional label text displayed alongside the checkbox.
 * @param isError Indicates if the checkbox is in an error state (visually).
 * @param isIndeterminate Whether the checkbox should display an indeterminate state.
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    isError: Boolean = false,
    isIndeterminate: Boolean = false
) {
    val checkboxData = CheckboxData(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
        isError = isError,
        isIndeterminate = isIndeterminate
    )

    println("Composable Checkbox function called with checked: $checked")
}

/**
 * Internal data class holding parameters for the Checkbox renderer.
 */
internal data class CheckboxData(
    val checked: Boolean,
    val onCheckedChange: (Boolean) -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: String?,
    val isError: Boolean,
    val isIndeterminate: Boolean
) 