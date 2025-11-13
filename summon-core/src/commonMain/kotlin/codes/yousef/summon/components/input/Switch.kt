package codes.yousef.summon.components.input

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.LocalPlatformRenderer
import codes.yousef.summon.runtime.mutableStateOf
import codes.yousef.summon.runtime.remember

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

    // Wrap the callback to only execute if enabled
    val wrappedOnCheckedChange: (Boolean) -> Unit = {
        if (enabled) {
            onCheckedChange(it)
        }
    }

    renderer.renderSwitch(
        checked = checked,
        onCheckedChange = wrappedOnCheckedChange, // Use the wrapped callback
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
