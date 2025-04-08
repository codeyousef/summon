package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember

/**
 * A composable that displays a range slider input field.
 *
 * @param value The current value of the slider
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
fun RangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier(),
    isEnabled: Boolean = true,
    label: String? = null,
    showTooltip: Boolean = false,
    valueFormat: (Float) -> String = { it.toString() }
) {
    // Get the platform renderer
    val renderer = getPlatformRenderer()

    // Render the range slider
    renderer.renderRangeSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange
    )

    // Optionally render a label if provided
    if (label != null) {
        // Label would be rendered here in a real implementation
    }

    // Optionally render a tooltip if enabled
    if (showTooltip) {
        // Tooltip would be rendered here in a real implementation
        // Example: showing current values
        val startValueStr = valueFormat(value.start)
        val endValueStr = valueFormat(value.endInclusive)
        // Tooltip content: "$startValueStr - $endValueStr"
    }
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
        isEnabled = isEnabled,
        label = label,
        showTooltip = showTooltip,
        valueFormat = valueFormat
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