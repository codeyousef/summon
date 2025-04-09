package code.yousef.summon.validation

import code.yousef.summon.validation.ValidationResult

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