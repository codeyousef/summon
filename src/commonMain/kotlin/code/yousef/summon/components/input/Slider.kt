package code.yousef.summon.components.input

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * A slider component that allows the user to select a value from a given range.
 *
 * @param value The current value of the slider.
 * @param onValueChange Callback invoked when the slider value changes.
 * @param valueRange The range of possible values for the slider.
 * @param steps The number of discrete steps the slider can snap to. If 0, the slider moves continuously.
 * @param modifier The modifier to be applied to the slider.
 * @param enabled Whether the slider is enabled.
 */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
) {
    val composer = CompositionLocal.currentComposer
    // Apply styling directly to the element via modifier
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f) // Assuming opacity is in Modifier
        .cursor(if (enabled) "pointer" else "default") // Assuming cursor is in Modifier
        .applyIf(!enabled) { pointerEvents("none") }

    composer?.startNode() // Start Slider node
    if (composer?.inserting == true) {
        val renderer = LocalPlatformRenderer.current
        renderer.renderSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            modifier = finalModifier
        )
    }
    composer?.endNode() // End Slider node (self-closing)
} 
