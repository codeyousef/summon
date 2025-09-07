package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember

/**
 * A multi-line text input field.
 *
 * @param value The current text value.
 * @param onValueChange Callback invoked when the text changes.
 * @param modifier Modifier applied to the text area.
 * @param enabled Controls the enabled state.
 * @param readOnly Controls the read-only state.
 * @param rows The number of visible text lines.
 * @param maxLength The maximum number of characters allowed.
 * @param placeholder Placeholder text displayed when the input is empty.
 */
@Composable
fun TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    rows: Int? = null,
    maxLength: Int? = null,
    placeholder: String? = null
) {
    val renderer = LocalPlatformRenderer.current

    renderer.renderTextArea(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        rows = rows,
        maxLength = maxLength,
        placeholder = placeholder,
        modifier = modifier
    )
}

/**
 * Stateful version of TextArea.
 */
@Composable
fun StatefulTextArea(
    initialValue: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    rows: Int? = null,
    maxLength: Int? = null,
    placeholder: String? = null
) {
    val textState = remember { mutableStateOf(initialValue) }

    TextArea(
        value = textState.value,
        onValueChange = {
            textState.value = it
            onValueChange(it)
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        rows = rows,
        maxLength = maxLength,
        placeholder = placeholder
    )
} 
