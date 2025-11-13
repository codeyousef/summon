package codes.yousef.summon.components.input

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.mutableStateOf
import codes.yousef.summon.runtime.remember

/**
 * A composable that allows selecting a range of values within a given range.
 *
 * @param value The current selected range.
 * @param onValueChange Callback invoked when the selected range changes.
 * @param modifier Modifier applied to the slider layout.
 * @param valueRange The total range allowed for selection (from min to max).
 * @param steps The number of discrete steps the slider should have. Set to 0 for a continuous slider.
 * @param enabled Controls the enabled state of the slider.
 */
@Composable
fun RangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier(),
    valueRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
    steps: Int = 0,
    enabled: Boolean = true
) {
    val renderer = LocalPlatformRenderer.current

    // Wrap the callback to only execute if enabled
    val wrappedOnValueChange: (ClosedFloatingPointRange<Float>) -> Unit = {
        if (enabled) {
            onValueChange(it)
        }
    }

    // Call the platform renderer directly
    renderer.renderRangeSlider(
        value = value,
        onValueChange = wrappedOnValueChange,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        modifier = modifier
    )
}

/**
 * A stateful version of RangeSlider that manages its own state.
 *
 * @param initialValue The initial range value
 * @param onValueChange Callback that is invoked when the value changes
 * @param valueRange The range of values that the slider can take
 * @param steps The number of discrete steps within the range
 * @param modifier The modifier to apply to this composable
 * @param isEnabled Whether the slider is enabled
 * @param label Optional label to display for the slider
 * @param showTooltip Whether to show a tooltip with the current value
 * @param valueFormat Function to format the value for display
 */
@Composable
fun StatefulRangeSlider(
    initialValue: ClosedFloatingPointRange<Float> = 0.25f..0.75f,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit = {},
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier(),
    isEnabled: Boolean = true,
    label: String? = null,
    showTooltip: Boolean = false,
    valueFormat: (Float) -> String = { it.toString() }
) {
    // Create state to store the range value
    val rangeState = remember { mutableStateOf(initialValue) }

    // Use the stateless RangeSlider composable
    RangeSlider(
        value = rangeState.value,
        onValueChange = { newValue ->
            rangeState.value = newValue
            onValueChange(newValue)
        },
        valueRange = valueRange,
        steps = steps,
        modifier = modifier,
        enabled = isEnabled
    )
}

/**
 * Utility class to manage a mutable range of Float values.
 * Can be used with remember { mutableStateOf(FloatRange(0.25f, 0.75f)) }
 */
data class FloatRange(
    override val start: Float,
    override val endInclusive: Float
) : ClosedFloatingPointRange<Float> {

    override fun contains(value: Float): Boolean = value in start..endInclusive

    override fun isEmpty(): Boolean = start > endInclusive

    override fun lessThanOrEquals(a: Float, b: Float): Boolean = a <= b
}

// Consider adding a sample if needed later, once styling is addressed
// object RangeSliderSamples { ... } 