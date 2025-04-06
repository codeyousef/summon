package code.yousef.summon.components.input

import code.yousef.summon.*
import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * A composable that allows users to select a time using a platform-provided time picker interface.
 *
 * This composable follows the state hoisting pattern. The value is typically represented
 * as a string in "HH:MM" or "HH:MM:SS" format, compatible with the HTML time input.
 *
 * @param value The currently selected time string ("HH:MM" or "HH:MM:SS"), or an empty string.
 * @param onValueChange Lambda invoked when the user selects a time, providing the new time string.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param label Optional label text to display above or alongside the time picker.
 * @param placeholder Optional placeholder text (browser support may vary for time inputs).
 * @param isError Indicates if the time picker currently has an error.
 * @param min Optional minimum selectable time string ("HH:MM" or "HH:MM:SS").
 * @param max Optional maximum selectable time string ("HH:MM" or "HH:MM:SS").
 * @param showSeconds Hint to the browser to include seconds in the input (requires browser support).
 */
@Composable
fun TimePicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    min: String? = null,
    max: String? = null,
    showSeconds: Boolean = false
) {
    val stepValue = if (showSeconds) 1 else null

    val timePickerData = TimePickerData(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        isError = isError,
        min = min,
        max = max,
        stepAttribute = stepValue
    )

    println("Composable TimePicker function called with value: $value")
}

/**
 * Internal data class holding parameters for the TimePicker renderer.
 */
internal data class TimePickerData(
    val value: String,
    val onValueChange: (String) -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: String?,
    val placeholder: String?,
    val isError: Boolean,
    val min: String?,
    val max: String?,
    val stepAttribute: Int?
) 