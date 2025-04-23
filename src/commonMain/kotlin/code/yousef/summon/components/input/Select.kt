package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.ModifierExtras.attribute
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.validation.Validator
import code.yousef.summon.runtime.SelectOption as RendererSelectOption

/**
 * Class that manages select state and validation.
 */
class SelectState<T>(
    val selectedValue: SummonMutableState<T?>,
    val validators: List<Validator> = emptyList()
) {
    // Internal state to track validation errors
    private val validationErrors = mutableStateOf<List<String>>(emptyList())

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
 * Data class representing a select option (Component's internal version).
 */
data class SelectOption<T>(
    val value: T,
    val label: String,
    val disabled: Boolean = false,
    val selected: Boolean = false
)

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
@Composable
fun <T> Select(
    selectedValue: SummonMutableState<T?>,
    options: List<SelectOption<T>>,
    onSelectedChange: (T?) -> Unit = {},
    label: String? = null,
    placeholder: String? = null,
    modifier: Modifier = Modifier(),
    multiple: Boolean = false,
    disabled: Boolean = false,
    size: Int = 1,
    validators: List<Validator> = emptyList()
) {
    val selectState = SelectState(selectedValue, validators)

    // Prepare the modifier, adding the disabled attribute if needed
    val finalModifier = if (disabled) {
        modifier.attribute("disabled", "disabled")
    } else {
        modifier
    }

    // Map the component's SelectOption list to the renderer's SelectOption list
    val rendererOptions = options.map { componentOption ->
        RendererSelectOption(
            value = componentOption.value,
            label = componentOption.label,
            disabled = componentOption.disabled
            // Note: componentOption.selected is not used here as RendererSelectOption lacks it
        )
    }

    getPlatformRenderer().renderSelect(
        selectedValue = selectedValue.value,
        onSelectedChange = onSelectedChange,
        options = rendererOptions, // Pass the mapped list
        modifier = finalModifier
    )
}
