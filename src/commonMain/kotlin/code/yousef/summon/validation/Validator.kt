package code.yousef.summon.validation

import code.yousef.summon.validation.ValidationResult
import code.yousef.summon.validation.ValidationMessages

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
    
    /**
     * Error message to show when validation fails.
     * This is a convenience property for validators that have a fixed error message.
     */
    val errorMessage: String
        get() = ValidationMessages.VALIDATION_FAILED
}

/**
 * Required field validator that ensures a value is not empty.
 */
class RequiredValidator(
    override val errorMessage: String = ValidationMessages.REQUIRED_FIELD
) : Validator {
    override fun validate(value: String): ValidationResult {
        val isValid = value.isNotBlank()
        return ValidationResult(isValid, if (!isValid) errorMessage else null)
    }
}

/**
 * Email validator that ensures a value matches an email pattern.
 */
class EmailValidator(
    override val errorMessage: String = ValidationMessages.INVALID_EMAIL
) : Validator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override fun validate(value: String): ValidationResult {
        val isValid = value.isEmpty() || value.matches(emailRegex)
        return ValidationResult(isValid, if (!isValid) errorMessage else null)
    }
}

/**
 * Minimum length validator.
 */
class MinLengthValidator(
    private val minLength: Int,
    override val errorMessage: String = ValidationMessages.minLength(minLength)
) : Validator {
    override fun validate(value: String): ValidationResult {
        val isValid = value.length >= minLength
        return ValidationResult(isValid, if (!isValid) errorMessage else null)
    }
}

/**
 * Maximum length validator.
 */
class MaxLengthValidator(
    private val maxLength: Int,
    override val errorMessage: String = ValidationMessages.maxLength(maxLength)
) : Validator {
    override fun validate(value: String): ValidationResult {
        val isValid = value.length <= maxLength
        return ValidationResult(isValid, if (!isValid) errorMessage else null)
    }
}

/**
 * Pattern validator that ensures a value matches a regex pattern.
 */
class PatternValidator(
    private val pattern: Regex,
    override val errorMessage: String = ValidationMessages.INVALID_FORMAT
) : Validator {
    override fun validate(value: String): ValidationResult {
        val isValid = value.isEmpty() || value.matches(pattern)
        return ValidationResult(isValid, if (!isValid) errorMessage else null)
    }
}

/**
 * Custom validator that uses a provided validation function.
 */
class CustomValidator(
    private val validateFn: (String) -> Boolean,
    override val errorMessage: String
) : Validator {
    override fun validate(value: String): ValidationResult {
        val isValid = validateFn(value)
        return ValidationResult(isValid, if (!isValid) errorMessage else null)
    }
}

/**
 * Validates a boolean value.
 * @param value The boolean value to validate
 * @return ValidationResult containing whether the validation passed and any error message
 */
fun Validator.validateBoolean(value: Boolean): ValidationResult {
    return validate(value.toString())
} 