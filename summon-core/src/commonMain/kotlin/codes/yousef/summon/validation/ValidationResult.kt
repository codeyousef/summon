package codes.yousef.summon.validation

/**
 * Simple class to represent validation result for form components

 * @property isValid The is valid value.
 * @property errorMessage The error message value.
 */
data class ValidationResult(val isValid: Boolean, val errorMessage: String? = null) 