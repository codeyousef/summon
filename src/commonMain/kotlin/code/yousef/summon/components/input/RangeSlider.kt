package code.yousef.summon.components.input

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.PlatformRendererProvider


/**
 * A composable that allows users to select a value or range of values from a continuous range.
 * Note: Standard HTML range input only supports a single value. Rendering a true range
 * might require custom elements or two separate inputs.
 * This composable currently reflects the `renderRangeSlider` interface which expects a range.
 *
 * @param value The current selected range.
 * @param onValueChange Callback invoked when the selected range changes.
 * @param modifier Modifier applied to the slider layout.
 * @param enabled Controls the enabled state.
 * @param valueRange The full possible range of the slider.
 * @param steps Defines the number of discrete steps within the `valueRange`. If 0, the slider is continuous.
 */
@Composable
fun RangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0 // TODO: Implement step logic if needed
    // Removed label, showTooltip, valueFormat - handle via composition/modifier
) {
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f)
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }

    // TODO: Replace runtime.PlatformRendererProvider with CompositionLocal access
    val renderer = PlatformRendererProvider.getPlatformRenderer()

    // TODO: Renderer signature update? Needs to handle valueRange, steps, enabled.
    // The current renderer.renderRangeSlider only takes value, onValueChange, modifier.
    // It will need access to valueRange and steps for proper rendering (e.g., setting min/max/step attributes).
    // For now, just call with the existing signature.
    renderer.renderRangeSlider(
        value = value,
        // Guard callback, potentially adjust range based on which thumb moved if it's a true range slider
        onValueChange = { if (enabled) onValueChange(it) }, 
        modifier = finalModifier
    )
} 
