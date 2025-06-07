package code.yousef.summon.components.input

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
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

    // Prepare the modifier, adding attributes as needed
    var finalModifier = modifier

    // Check if the modifier already has an ID
    val existingId = finalModifier.attributes?.get("id")

    // Generate an ID for the select element if label is provided and no ID exists
    val selectId = if (label != null) {
        existingId ?: "select-${selectedValue.hashCode()}"
    } else {
        existingId
    }

    if (disabled) {
        finalModifier = finalModifier.attribute("disabled", "disabled")
    }

    if (multiple) {
        finalModifier = finalModifier.attribute("multiple", "true")
    }

    if (size > 1) {
        finalModifier = finalModifier.attribute("size", size.toString())
    }

    // Only set the ID if we generated one (not if it already existed)
    if (selectId != null && existingId == null) {
        finalModifier = finalModifier.id(selectId)
    }

    // Map the component's SelectOption list to the renderer's SelectOption list
    val rendererOptions = mutableListOf<RendererSelectOption<T?>>()

    // Add placeholder option if provided
    if (placeholder != null) {
        rendererOptions.add(
            RendererSelectOption<T?>(
                value = null,
                label = placeholder,
                disabled = true
            )
        )
    }

    // Add all the regular options
    rendererOptions.addAll(options.map { componentOption ->
        RendererSelectOption(
            value = componentOption.value,
            label = componentOption.label,
            disabled = componentOption.disabled
            // Note: componentOption.selected is not used here as RendererSelectOption lacks it
        )
    })

    // Render label if provided
    if (label != null) {
        getPlatformRenderer().renderLabel(
            text = label,
            modifier = Modifier(),
            forElement = selectId
        )
    }

    // Update onSelectedChange to also update the state
    val enhancedOnSelectedChange: (T?) -> Unit = { value ->
        selectedValue.value = value
        onSelectedChange(value)
    }

    getPlatformRenderer().renderSelect<T?>(
        selectedValue = selectedValue.value,
        onSelectedChange = enhancedOnSelectedChange,
        options = rendererOptions, // Pass the mapped list
        modifier = finalModifier
    )
}
