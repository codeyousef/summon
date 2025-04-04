package code.yousef.summon.components.input

import code.yousef.summon.core.Composable
import code.yousef.summon.MutableState
import code.yousef.summon.Validator
import code.yousef.summon.components.FocusableComponent
import code.yousef.summon.components.InputComponent
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.mutableStateOf


/**
 * Abstract base class for input components to reduce code duplication.
 * 
 * @param T The type of value managed by this input component
 * @param state The mutable state that holds the component's value
 * @param onValueChange Callback invoked when the value changes
 * @param label Optional label for the input field
 * @param modifier Modifier for styling the component
 * @param disabled Whether the input field is disabled
 * @param validators List of validators to check the input value
 */
abstract class InputComponentBase<T>(
    open val state: MutableState<T>,
    open val onValueChange: (T) -> Unit = {},
    open val label: String? = null,
    open val modifier: Modifier = Modifier(),
    open val disabled: Boolean = false,
    open val validators: List<Validator> = emptyList()
) : Composable, InputComponent, FocusableComponent {
    
    /**
     * State for tracking validation errors
     */
    protected val validationErrors = mutableStateOf<List<String>>(emptyList())
    
    /**
     * Validates the current value against all validators.
     * 
     * @return true if all validations pass, false otherwise
     */
    fun validate(): Boolean {
        val errors = validators.mapNotNull { validator ->
            val isValid = when (val value = state.value) {
                is String -> validator.validate(value)
                else -> validator.validate(value.toString())
            }
            if (isValid) null else validator.errorMessage
        }
        
        validationErrors.value = errors
        return errors.isEmpty()
    }
    
    /**
     * Handles value changes, updates state, and invokes callbacks.
     * 
     * @param newValue The new value of the input
     */
    protected fun handleValueChange(newValue: T) {
        state.value = newValue
        onValueChange(newValue)
        // Auto-validate if there are already errors showing
        if (validationErrors.value.isNotEmpty()) {
            validate()
        }
    }
    
    /**
     * Resets the input field to its initial state.
     * 
     * @param newValue Optional new value to set
     */
    open fun reset(newValue: T? = null) {
        if (newValue != null) {
            state.value = newValue
        }
        validationErrors.value = emptyList()
    }
    
    /**
     * Returns the current validation errors.
     * 
     * @return List of validation error messages
     */
    fun getErrors(): List<String> = validationErrors.value
    
    /**
     * Checks if the input has any validation errors.
     * 
     * @return true if there are validation errors, false otherwise
     */
    fun hasErrors(): Boolean = validationErrors.value.isNotEmpty()
    
    /**
     * Implement the compose method as an abstract method that subclasses must provide
     */
    abstract override fun <T> compose(receiver: T): T 
} 