package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember

/**
 * A toggle switch component.
 *
 * @param checked The current state of the switch.
 * @param onCheckedChange Callback invoked when the switch state changes.
 * @param modifier Modifier applied to the switch.
 * @param enabled Controls the enabled state.
 */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
) {
    val renderer = LocalPlatformRenderer.current

    renderer.renderSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier
    )
}

/**
 * Stateful version of Switch.
 */
@Composable
fun StatefulSwitch(
    initialChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
) {
    val switchState = remember { mutableStateOf(initialChecked) }

    Switch(
        checked = switchState.value,
        onCheckedChange = {
            switchState.value = it
            onCheckedChange(it)
        },
        modifier = modifier,
        enabled = enabled
    )
} 
