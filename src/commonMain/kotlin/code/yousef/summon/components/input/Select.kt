package code.yousef.summon.components.input

import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.components.FocusableComponent
import code.yousef.summon.components.InputComponent
import code.yousef.summon.core.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.runtime.PlatformRendererProviderLegacy.getRenderer
import code.yousef.summon.validation.Validator
import kotlinx.html.TagConsumer

/**
 * A composable that displays a dropdown select field.
 * @param selectedValue The state that holds the current selected value
 * @param options The list of options to display in the dropdown
 * @param onSelectedChange Callback that is invoked when selection changes
 * @param label Optional label to display for the select
 * @param placeholder Placeholder text to show when no option is selected
 * @param modifier The modifier to apply to this composable
 * @param multiple Whether multiple selections are allowed
 * @param disabled Whether the select is disabled
 * @param size Number of visible options when dropdown is open
 * @param validators List of validators to apply to the input
 */
class Select<T>(
    val selectedValue: SummonMutableState<T?>,
    val options: List<SelectOption<T>>,
    val onSelectedChange: (T?) -> Unit = {},
    val label: String? = null,
    val placeholder: String? = null,
    val modifier: Modifier = Modifier(),
    val multiple: Boolean = false,
    val disabled: Boolean = false,
    val size: Int = 1,
    val validators: List<Validator> = emptyList()
) : Composable, InputComponent, FocusableComponent {
    // Internal state to track validation errors
    private val validationErrors = mutableStateOf<List<String>>(emptyList())

    /**
     * Renders this Select composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            getRenderer().renderSelect(
                selectedValue.value,
                onSelectedChange,
                options,
                !disabled,
                modifier
            )
        }
        return receiver
    }

    /**
     * Validates the current input value against all validators.
     * @return True if validation passed, false otherwise
     */
    fun validate(): Boolean {
        val errors = validators.mapNotNull { validator ->
            val valueToValidate = selectedValue.value?.toString() ?: ""
            val result = validator.validate(valueToValidate)
            if (!result.isValid) result.errorMessage else null
        }
        validationErrors.value = errors
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

/**
 * Data class representing a select option.
 */
data class SelectOption<T>(
    val value: T,
    val label: String,
    val disabled: Boolean = false,
    val selected: Boolean = false
) 