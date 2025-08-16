package code.yousef.summon.components.input

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.ModifierExtras.pointerEvents
import code.yousef.summon.modifier.applyIf
import code.yousef.summon.runtime.*
import code.yousef.summon.validation.Validator

/**
 * A composable that displays a checkbox input field.
 * @param checked The current checked state of the checkbox
 * @param onCheckedChange Callback that is invoked when the checkbox state changes
 * @param label Optional label to display for the checkbox
 * @param modifier The modifier to apply to this composable
 * @param enabled Controls whether the checkbox can be interacted with
 * @param isIndeterminate Whether the checkbox should be in an indeterminate state
 * @param validators List of validators to apply to the input
 */
@Composable
fun Checkbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    isIndeterminate: Boolean = false,
    validators: List<Validator> = emptyList()
) {
    // Track validation errors internally
    val validationErrors = remember { mutableStateOf<List<String>>(emptyList()) }

    // Set up the composer for composition
    val composer = CompositionLocal.currentComposer

    // Apply additional styling for disabled state
    val finalModifier = modifier
        .apply {
            if (enabled) this else this.copy(styles = this.styles + ("opacity" to "0.6"))
        }
        .cursor(if (enabled) "pointer" else "default")
        .applyIf(!enabled) { pointerEvents("none") }

    // Start composition node
    composer?.startNode()
    if (composer?.inserting == true) {
        val renderer = LocalPlatformRenderer.current

        // Cast to proper renderer function and call with parameters in the correct order
        val renderCheckboxFunction: (Boolean, (Boolean) -> Unit, Boolean, Modifier) -> Unit = renderer::renderCheckbox
        renderCheckboxFunction(
            checked,
            { newValue: Boolean ->
                if (enabled) {
                    // Validate the new value
                    val errors = validators.mapNotNull { validator ->
                        val result = validator.validate(newValue.toString())
                        if (!result.isValid) result.errorMessage else null
                    }
                    validationErrors.value = errors

                    // Call the callback with the new value
                    onCheckedChange(newValue)
                }
            },
            enabled,
            finalModifier
        )
    }

    // End composition node
    composer?.endNode()
}

/**
 * A stateful composable checkbox that maintains its own checked state.
 */
@Composable
fun StatefulCheckbox(
    initialChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier(),
    enabled: Boolean = true,
    label: String? = null,
    isIndeterminate: Boolean = false,
    validators: List<Validator> = emptyList()
) {
    val checkedState = remember { mutableStateOf(initialChecked) }

    Checkbox(
        checked = checkedState.value,
        onCheckedChange = {
            checkedState.value = it
            onCheckedChange(it)
        },
        modifier = modifier,
        enabled = enabled,
        label = label,
        isIndeterminate = isIndeterminate,
        validators = validators
    )
} 