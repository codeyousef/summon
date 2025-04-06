package code.yousef.summon.components.input

import code.yousef.summon.*
import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * A composable that allows users to select a date using a platform-provided date picker interface.
 *
 * This composable follows the state hoisting pattern. The value is typically represented
 * as a string in "YYYY-MM-DD" format, compatible with the HTML date input.
 *
 * @param value The currently selected date string ("YYYY-MM-DD"), or an empty string if none selected.
 * @param onValueChange Lambda invoked when the user selects a date, providing the new date string.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param label Optional label text to display above or alongside the date picker.
 * @param placeholder Optional placeholder text (browser support may vary for date inputs).
 * @param isError Indicates if the date picker currently has an error.
 * @param min Optional minimum selectable date string ("YYYY-MM-DD").
 * @param max Optional maximum selectable date string ("YYYY-MM-DD").
 */
@Composable
fun DatePicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    min: String? = null,
    max: String? = null
) {
    val datePickerData = DatePickerData(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        isError = isError,
        min = min,
        max = max
    )

    println("Composable DatePicker function called with value: $value")
}

/**
 * Internal data class holding parameters for the DatePicker renderer.
 */
internal data class DatePickerData(
    val value: String,
    val onValueChange: (String) -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: String?,
    val placeholder: String?,
    val isError: Boolean,
    val min: String?,
    val max: String?
) 