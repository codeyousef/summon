package code.yousef.summon.components.input

import code.yousef.summon.Validator
import code.yousef.summon.mutableStateOf


/**
 * A reusable component that handles validation through composition.
 * This is an example of using composition over inheritance for validation logic.
 *
 * @param T The type of value to validate
 * @param initialValue The initial value of the field
 * @param validators The list of validators to apply to the value
 */
class ValidatableField<T>(
    private val initialValue: T,
    private val validators: List<Validator> = emptyList()
) {
    /**
     * The current value of the field
     */
    val state = mutableStateOf(initialValue)

    /**
     * The current validation errors, if any
     */
    val errors = mutableStateOf<List<String>>(emptyList())

    /**
     * Validates the current value against all validators.
     *
     * @return true if all validations pass, false otherwise
     */
    fun validate(): Boolean {
        val currentErrors = validators.mapNotNull { validator ->
            val isValid = when (val value = state.value) {
                is String -> validator.validate(value)
                else -> validator.validate(value.toString())
            }
            if (isValid) null else validator.errorMessage
        }

        errors.value = currentErrors
        return errors.value.isEmpty()
    }

    /**
     * Updates the value and optionally validates it.
     *
     * @param newValue The new value to set
     * @param validateImmediately Whether to validate the value immediately after setting it
     */
    fun setValue(newValue: T, validateImmediately: Boolean = false) {
        state.value = newValue
        if (validateImmediately) {
            validate()
        }
    }

    /**
     * Resets the field to its initial value and clears validation errors.
     */
    fun reset() {
        state.value = initialValue
        errors.value = emptyList()
    }

    /**
     * Returns whether the field has any validation errors.
     *
     * @return true if there are validation errors, false otherwise
     */
    fun hasErrors(): Boolean = errors.value.isNotEmpty()
} 