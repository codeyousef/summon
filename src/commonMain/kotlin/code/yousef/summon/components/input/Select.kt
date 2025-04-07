package code.yousef.summon.components.input

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.PlatformRendererProvider


/**
 * Represents an option within a Select component.
 * @param T The type of the value associated with the option.
 * @param value The actual value of the option.
 * @param label The display text for the option.
 * @param disabled Whether this specific option is disabled.
 */
data class SelectOption<T>(val value: T, val label: String, val disabled: Boolean = false)

/**
 * A dropdown select component that allows the user to choose from a list of options.
 *
 * @param T The type of the value being selected.
 * @param value The currently selected value.
 * @param onValueChange Callback invoked when the selection changes.
 * @param options The list of available options.
 * @param modifier Modifier to apply to the select element.
 * @param enabled Whether the select component is enabled.
 * @param placeholder Optional placeholder text shown when no value is selected.
 */
@Composable
fun <T> Select(
    value: T?,
    onValueChange: (T?) -> Unit,
    options: List<SelectOption<T>>,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    placeholder: String? = null
) {
    val composer = CompositionLocal.currentComposer
    // Apply styling directly to the element via modifier
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f) // Assuming opacity is in Modifier
        .cursor(if (enabled) "pointer" else "default") // Assuming cursor is in Modifier
        .applyIf(!enabled) { pointerEvents("none") }

    composer?.startNode() // Start Select node
    if (composer?.inserting == true) {
        val renderer = PlatformRendererProvider.getPlatformRenderer()
        // Pass relevant state and modifier to the renderer
        renderer.renderSelect(
            value = value,
            onValueChange = { if (enabled) onValueChange(it) }, // Guard callback
            options = options,
            enabled = enabled,
            modifier = finalModifier
            // TODO: Pass placeholder to renderer if supported
        )
    }
    composer?.endNode() // End Select node (self-closing)
} 
