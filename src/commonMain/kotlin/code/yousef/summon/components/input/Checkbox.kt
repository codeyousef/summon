package code.yousef.summon.components.input

import code.yousef.summon.*
import code.yousef.summon.components.FocusableComponent
import code.yousef.summon.components.InputComponent
import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.validation.Validator
import code.yousef.summon.validation.ValidationResult
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import kotlinx.html.TagConsumer

/**
 * A composable that displays a checkbox input field.
 * @param state The state that holds the current value of the checkbox
 * @param onValueChange Callback that is invoked when the input value changes
 * @param label Optional label to display for the checkbox
 * @param modifier The modifier to apply to this composable
 * @param isIndeterminate Whether the checkbox should be in an indeterminate state
 * @param validators List of validators to apply to the input
 */
class Checkbox(
    val state: SummonMutableState<Boolean>,
    val onValueChange: (Boolean) -> Unit = {},
    val label: String? = null,
    val modifier: Modifier = Modifier(),
    val isIndeterminate: Boolean = false,
    val validators: List<Validator> = emptyList()
) : Composable, InputComponent, FocusableComponent {
    // Internal state to track validation errors
    private val validationErrors = mutableStateOf<List<String>>(emptyList())

    /**
     * Renders this Checkbox composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val typedReceiver = receiver as TagConsumer<T>
            PlatformRendererProvider.getRenderer().renderCheckbox(
                checked = state.value,
                onCheckedChange = { newValue ->
                    state.value = newValue
                    onValueChange(newValue)
                },
                enabled = true,
                modifier = modifier
            )
            return receiver
        }
        return receiver
    }

    /**
     * Validates the current input value against all validators.
     * @return True if validation passed, false otherwise
     */
    fun validate(): Boolean {
        val errors = validators.mapNotNull { validator ->
            val result = validator.validate(state.value.toString())
            if (!result.isValid) result.errorMessage else null
        }
        validationErrors.value = errors.filterNotNull()
        return errors.isEmpty()
    }

    /**
     * Gets the current validation errors.
     */
    fun getValidationErrors(): List<String> = validationErrors.value

    /**
     * Indicates whether the field is currently valid.
     */
    fun isValid(): Boolean = validationErrors.value.isEmpty()
} 