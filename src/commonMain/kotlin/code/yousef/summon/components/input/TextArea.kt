package code.yousef.summon.components.input

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.modifier.border

/**
 * A composable that displays a multi-line text input field.
 *
 * @param value The current text value of the text area.
 * @param onValueChange Callback invoked when the text value changes.
 * @param modifier Modifier applied to the text area.
 * @param enabled Controls the enabled state.
 * @param placeholder Optional composable lambda for displaying placeholder text.
 * @param label Optional composable lambda for a label (consider using FormField).
 * @param rows Optional hint for the number of visible text rows.
 * @param maxLength Optional maximum number of characters allowed.
 * @param isError Indicates if the text area should be styled as invalid.
 */
@Composable
fun TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    rows: Int? = null,
    maxLength: Int? = null,
    isError: Boolean = false
) {
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f)
        .cursor(if (enabled) "text" else "default")
        .applyIf(!enabled) { pointerEvents("none") }
        .applyIf(isError) { border("1px", "solid", "#D32F2F") }

    val renderer = PlatformRendererProvider.getRenderer()

    renderer.renderTextArea(
        value = value,
        onValueChange = { if (enabled) onValueChange(it) },
        modifier = finalModifier,
        label = ""
    )
} 