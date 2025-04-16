package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember

/**
 * A composable that allows selecting a single value from a range.
 *
 * @param value The current selected value.
 * @param onValueChange Callback invoked when the value changes.
 * @param modifier Modifier applied to the slider layout.
 * @param valueRange The total range allowed for selection.
 * @param steps The number of discrete steps. 0 for continuous.
 * @param enabled Controls the enabled state.
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier(),
    valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
    steps: Int = 0,
    enabled: Boolean = true
) {
    val renderer = LocalPlatformRenderer.current

    renderer.renderSlider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        modifier = modifier
    )
}

/**
 * Stateful version of Slider.
 *
 * ... (Add KDoc if needed) ...
 */
@Composable
fun StatefulSlider(
    initialValue: Float = 0.5f,
    onValueChange: (Float) -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
    steps: Int = 0,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
) {
    val sliderState = remember { mutableStateOf(initialValue) }

    Slider(
        value = sliderState.value,
        onValueChange = {
            sliderState.value = it
            onValueChange(it)
        },
        valueRange = valueRange,
        steps = steps,
        modifier = modifier,
        enabled = enabled
    )
} 
