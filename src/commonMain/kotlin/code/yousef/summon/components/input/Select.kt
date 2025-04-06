package code.yousef.summon.components.input

import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents

/**
 * Data class representing a select option.
 */
data class SelectOption<T>(
    val value: T,
    val label: String,
    val disabled: Boolean = false
)

/**
 * A composable that displays a dropdown select field (single selection).
 *
 * @param T The type of the value.
 * @param value The currently selected value.
 * @param onValueChange Callback invoked when the selection changes.
 * @param options The list of `SelectOption` items to display in the dropdown.
 * @param modifier Modifier applied to the select container.
 * @param enabled Controls the enabled state.
 * @param placeholder Optional composable lambda for displaying a placeholder when no value is selected (value is null).
 *                    Note: Placeholder rendering might need specific renderer support.
 * @param label Optional composable lambda for a label associated with the select (consider using FormField).
 */
@Composable
fun <T> Select(
    value: T?,
    onValueChange: (T?) -> Unit,
    options: List<SelectOption<T>>,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null
) {
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f)
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }

    val renderer = PlatformRendererProvider.getRenderer()

    val selectedOpt = options.find { it.value == value }
    renderer.renderSelect(
        selectedOption = selectedOpt,
        options = options,
        onOptionSelected = { selectedOption ->
            if (enabled) {
                onValueChange(selectedOption?.value)
            }
        },
        label = "",
        modifier = finalModifier
    )
} 