package code.yousef.summon.components.input

import code.yousef.summon.*
import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * Types of text input fields, corresponding to HTML input types.
 */
enum class TextFieldType {
    Text,
    Password,
    Email,
    Number,
    Tel,
    Url,
    Search,
    Date,
    Time
}

/**
 * A composable that allows users to input and edit text.
 *
 * This composable follows the state hoisting pattern. The caller provides the current
 * `value` and an `onValueChange` callback to update the state.
 *
 * @param value The current text value to display.
 * @param onValueChange Lambda invoked when the user changes the text input.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param label Optional label text to display above or alongside the field.
 * @param placeholder Optional placeholder text displayed when the input is empty.
 * @param type The type of input (e.g., Text, Password, Email) using [TextFieldType].
 * @param isError Indicates if the input currently has an error (e.g., failed validation).
 *                Controls visual styling. Validation logic itself is handled by the caller.
 */
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    type: TextFieldType = TextFieldType.Text,
    isError: Boolean = false
) {
    val textFieldData = TextFieldData(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        type = type,
        isError = isError
    )

    println("Composable TextField function called with value: $value")
}

/**
 * Internal data class holding parameters for the TextField renderer.
 */
internal data class TextFieldData(
    val value: String,
    val onValueChange: (String) -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: String?,
    val placeholder: String?,
    val type: TextFieldType,
    val isError: Boolean
) 