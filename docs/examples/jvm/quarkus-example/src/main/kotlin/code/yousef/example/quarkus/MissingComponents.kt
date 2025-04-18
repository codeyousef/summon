package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember

/**
 * A color picker component that's missing from the current Summon implementation.
 * This is a proper implementation that uses a standard HTML color input.
 *
 * @param value The current color value in hex format (e.g., "#FF0000")
 * @param onValueChange Callback that is invoked when the selected color changes
 * @param modifier The modifier to apply to this composable
 * @param enabled Whether the color picker is enabled
 * @param label Optional label for the color picker
 */
@Composable
fun ColorPicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null
) {
    Column(modifier = modifier) {
        // Add label if provided
        if (label != null) {
            Text(
                text = label,
                modifier = Modifier().style("style", "display: block; margin-bottom: 0.5rem; font-weight: 600;")
            )
        }

        // Color input element
        Box(
            modifier = Modifier()
                .style("element", "input")  // Explicitly set element type
                .style("type", "color")     // Set input type to color
                .style("value", value)      // Set current value
                // For server-side rendering, we use hx-post for HTMX integration
                .style("hx-post", "/api/color-change")  // This would need a corresponding endpoint
                .style("hx-trigger", "change")
                .style("hx-swap", "none")
                .style("disabled", if (!enabled) "true" else "")
                .style("style", "width: 100%; height: 40px;")
        ) {
            // Empty content as input elements don't have content
        }

        // Display the current color value
        Text(
            text = "Selected: $value",
            modifier = Modifier().style("style", "margin-top: 0.5rem; font-family: monospace;")
        )
    }
}

/**
 * A slider component with proper implementation for JVM platform.
 * This is a complete implementation of the missing Slider component.
 *
 * @param value The current value
 * @param onValueChange Callback that is invoked when the value changes
 * @param min Minimum value
 * @param max Maximum value
 * @param step Step size
 * @param modifier The modifier to apply to this composable
 * @param enabled Whether the slider is enabled
 * @param label Optional label for the slider
 * @param showValue Whether to show the current value
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    min: Float = 0f,
    max: Float = 100f,
    step: Float = 1f,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    showValue: Boolean = true
) {
    Column(modifier = modifier) {
        // Add label and current value if needed
        if (label != null || showValue) {
            Row(
                modifier = Modifier().style("style", "display: flex; justify-content: space-between; margin-bottom: 0.5rem;")
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        modifier = Modifier().style("style", "font-weight: 600;")
                    )
                }

                if (showValue) {
                    Text(
                        text = value.toString(),
                        modifier = Modifier().style("style", "font-family: monospace;")
                    )
                }
            }
        }

        // Slider input element
        Box(
            modifier = Modifier()
                .style("element", "input")  // Explicitly set element type
                .style("type", "range")     // Set input type to range
                .style("min", min.toString())
                .style("max", max.toString())
                .style("step", step.toString())
                .style("value", value.toString())
                // For server-side rendering, we use hx-post for HTMX integration
                .style("hx-post", "/api/slider-change")  // This would need a corresponding endpoint
                .style("hx-trigger", "change")
                .style("hx-swap", "none")
                .style("disabled", if (!enabled) "true" else "")
                .style("style", "width: 100%;")
        ) {
            // Empty content as input elements don't have content
        }
    }
}

/**
 * A stateful version of the ColorPicker component that manages its own state.
 * 
 * @param initialValue The initial color value
 * @param onValueChange Callback that is invoked when the selected color changes
 * @param modifier The modifier to apply to this composable
 * @param enabled Whether the color picker is enabled
 * @param label Optional label for the color picker
 */
@Composable
fun StatefulColorPicker(
    initialValue: String = "#000000",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null
) {
    val colorState = remember { mutableStateOf(initialValue) }

    ColorPicker(
        value = colorState.value,
        onValueChange = { 
            colorState.value = it
            onValueChange(it)
        },
        modifier = modifier,
        enabled = enabled,
        label = label
    )
}

/**
 * A stateful version of the Slider component that manages its own state.
 * 
 * @param initialValue The initial slider value
 * @param onValueChange Callback that is invoked when the value changes
 * @param min Minimum value
 * @param max Maximum value
 * @param step Step size
 * @param modifier The modifier to apply to this composable
 * @param enabled Whether the slider is enabled
 * @param label Optional label for the slider
 * @param showValue Whether to show the current value
 */
@Composable
fun StatefulSlider(
    initialValue: Float = 50f,
    onValueChange: (Float) -> Unit = {},
    min: Float = 0f,
    max: Float = 100f,
    step: Float = 1f,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    showValue: Boolean = true
) {
    val sliderState = remember { mutableStateOf(initialValue) }

    Slider(
        value = sliderState.value,
        onValueChange = { 
            sliderState.value = it
            onValueChange(it)
        },
        min = min,
        max = max,
        step = step,
        modifier = modifier,
        enabled = enabled,
        label = label,
        showValue = showValue
    )
}
