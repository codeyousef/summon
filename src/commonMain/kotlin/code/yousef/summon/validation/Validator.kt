package code.yousef.summon.validation

/**
 * Base interface for form field validators.
 */
interface Validator {
    /**
     * Validates the given value.
     * @param value The value to validate
     * @return ValidationResult containing whether the validation passed and any error message
     */
    fun validate(value: String): ValidationResult
}

/**
 * Represents the result of a validation operation.
 * @property isValid Whether the validation passed
 * @property errorMessage The error message if validation failed, null otherwise
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
) 