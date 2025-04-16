package code.yousef.example.quarkus

import code.yousef.summon.components.layout.Box
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * A color picker component that's missing from the current Summon implementation.
 * This is a simple implementation that uses a standard HTML color input.
 *
 * @param value The current color value in hex format (e.g., "#FF0000")
 * @param onValueChange Callback that is invoked when the selected color changes
 * @param modifier The modifier to apply to this composable
 * @param enabled Whether the color picker is enabled
 */
@Composable
fun ColorPicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
) {
    // We'll use a Box with specific HTML attributes to create a color picker
    Box(
        modifier = modifier
            // This will render as <input type="color">
            .style("type", "color")
            .style("value", value)
            .style("onChange", "javascript: (event) => { ${onValueChange(value)} }")
            .style("disabled", if (!enabled) "true" else "")
    ) {
        // Empty content as input elements don't have content
    }
}

/**
 * A slider component with proper implementation for JVM platform.
 * This is a simplified version of the missing Slider implementation.
 *
 * @param value The current value
 * @param onValueChange Callback that is invoked when the value changes
 * @param min Minimum value
 * @param max Maximum value
 * @param step Step size
 * @param modifier The modifier to apply to this composable
 * @param enabled Whether the slider is enabled
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    min: Float = 0f,
    max: Float = 100f,
    step: Float = 1f,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
) {
    // We'll use a Box with specific HTML attributes to create a slider
    Box(
        modifier = modifier
            .style("type", "range")
            .style("min", min.toString())
            .style("max", max.toString())
            .style("step", step.toString())
            .style("value", value.toString())
            .style("onChange", "javascript: (event) => { ${onValueChange(value)} }")
            .style("disabled", if (!enabled) "true" else "")
    ) {
        // Empty content as input elements don't have content
    }
} 