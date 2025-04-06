package code.yousef.summon.components.input

import code.yousef.summon.*
import code.yousef.summon.core.UIElement
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

/**
 * A composable that displays a multi-line text input field (textarea).
 *
 * This composable follows the state hoisting pattern. The caller provides the current
 * `value` and an `onValueChange` callback.
 *
 * @param value The current text value to display.
 * @param onValueChange Lambda invoked when the user changes the text input.
 * @param modifier Optional [Modifier] for styling and layout.
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 * @param label Optional label text to display above or alongside the field.
 * @param placeholder Optional placeholder text displayed when the input is empty.
 * @param isError Indicates if the input currently has an error (e.g., failed validation).
 * @param rows The number of text lines visible by default.
 * @param maxLength Optional maximum number of characters allowed.
 * @param resizable Controls whether the user can resize the textarea (platform support may vary).
 */
@Composable
fun TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    rows: Int = 4,
    maxLength: Int? = null,
    resizable: Boolean = true
) {
    val textAreaData = TextAreaData(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = label,
        placeholder = placeholder,
        isError = isError,
        rows = rows,
        maxLength = maxLength,
        resizable = resizable
    )

    println("Composable TextArea function called with value: $value")
}

/**
 * Internal data class holding parameters for the TextArea renderer.
 */
internal data class TextAreaData(
    val value: String,
    val onValueChange: (String) -> Unit,
    val modifier: Modifier,
    val enabled: Boolean,
    val label: String?,
    val placeholder: String?,
    val isError: Boolean,
    val rows: Int,
    val maxLength: Int?,
    val resizable: Boolean
) 