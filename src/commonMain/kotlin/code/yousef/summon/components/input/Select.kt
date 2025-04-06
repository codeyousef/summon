package code.yousef.summon.components.input

import code.yousef.summon.*
import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * Data class representing an option within a [Select] composable.
 *
 * @param T The type of the option's value.
 * @param value The actual value represented by this option.
 * @param label The human-readable text displayed for this option.
 * @param disabled Whether this specific option should be disabled.
 */
data class SelectOption<T>(
    val value: T,
    val label: String,
    val disabled: Boolean = false
)

/**
 * A composable that displays a dropdown select field, allowing users to choose from a list of options.
 *
 * This composable follows the state hoisting pattern.
 *
 * @param T The type of value held by the options.
 * @param selectedValue The currently selected value (or null if none is selected).
 * @param onValueChange Lambda invoked when the user selects a different option.
 * @param options The list of [SelectOption] items to display in the dropdown.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param label Optional label text to display above or alongside the select field.
 * @param placeholder Optional placeholder text displayed when no value is selected (if supported by renderer).
 * @param isError Indicates if the select field currently has an error.
 */
@Composable
fun <T> Select(
    selectedValue: T?,
    onValueChange: (T?) -> Unit,
    options: List<SelectOption<T>>,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false
) {
    val selectData = SelectData(
        selectedValue = selectedValue,
        onValueChange = onValueChange,
        options = options,
        modifier = modifier,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        isError = isError
    )

    println("Composable Select function called with selectedValue: $selectedValue")
}

/**
 * Internal data class holding parameters for the Select renderer.
 */
internal data class SelectData<T>(
    val selectedValue: T?,
    val onValueChange: (T?) -> Unit,
    val options: List<SelectOption<T>>,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: String?,
    val placeholder: String?,
    val isError: Boolean
) 