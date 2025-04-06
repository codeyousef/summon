package code.yousef.summon.components.input

import code.yousef.summon.core.getPlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.modifier.pointerEvents

/**
 * A composable that displays a toggle switch control.
 * Switches allow users to toggle a setting on or off.
 *
 * @param checked Whether the switch is currently in the 'on' state.
 * @param onCheckedChange Callback invoked when the checked state changes due to user interaction.
 * @param modifier Modifier applied to the switch layout (often includes label).
 * @param enabled Controls the enabled state. When `false`, interaction is disabled.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
) {
    val finalModifier = modifier
        .opacity(if (enabled) 1f else 0.6f)
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }

    val renderer = getPlatformRenderer()

    renderer.renderSwitch(
        checked = checked,
        onCheckedChange = { if (enabled) onCheckedChange(it) },
        modifier = finalModifier
    )
} 