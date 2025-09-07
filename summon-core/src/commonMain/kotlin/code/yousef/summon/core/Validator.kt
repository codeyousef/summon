package code.yousef.summon.core

import code.yousef.summon.validation.ValidationMessages

/**
 * Interface for validating input fields.
 *
 * @deprecated Use code.yousef.summon.validation.Validator instead
 */
@Deprecated(
    message = "Use code.yousef.summon.validation.Validator instead",
    replaceWith = ReplaceWith("code.yousef.summon.validation.Validator", "code.yousef.summon.validation.Validator")
)
interface Validator {
    /**
     * Validates the input value.
     * @param value The value to validate
     * @return True if validation passed, false otherwise
     */
    fun validate(value: String): Boolean

    /**
     * Error message to show when validation fails.
     */
    val errorMessage: String
}

/**
 * Required field validator that ensures a value is not empty.
 */
class RequiredValidator(
    override val errorMessage: String = ValidationMessages.REQUIRED_FIELD
) : Validator {
    override fun validate(value: String): Boolean = value.isNotBlank()
}

/**
 * Email validator that ensures a value matches an email pattern.
 */
class EmailValidator(
    override val errorMessage: String = ValidationMessages.INVALID_EMAIL
) : Validator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override fun validate(value: String): Boolean {
        return value.isEmpty() || value.matches(emailRegex)
    }
}

/**
 * Minimum length validator.
 */
class MinLengthValidator(
    private val minLength: Int,
    override val errorMessage: String = ValidationMessages.minLength(minLength)
) : Validator {
    override fun validate(value: String): Boolean = value.length >= minLength
}

/**
 * Maximum length validator.
 */
class MaxLengthValidator(
    private val maxLength: Int,
    override val errorMessage: String = ValidationMessages.maxLength(maxLength)
) : Validator {
    override fun validate(value: String): Boolean = value.length <= maxLength
}

/**
 * Pattern validator that ensures a value matches a regex pattern.
 */
class PatternValidator(
    private val pattern: Regex,
    override val errorMessage: String = ValidationMessages.INVALID_FORMAT
) : Validator {
    override fun validate(value: String): Boolean = value.isEmpty() || value.matches(pattern)
}

/**
 * Custom validator that uses a provided validation function.
 */
class CustomValidator(
    private val validateFn: (String) -> Boolean,
    override val errorMessage: String
) : Validator {
    override fun validate(value: String): Boolean = validateFn(value)
}

/**
 * Validates a boolean value.
 * @param value The boolean value to validate
 * @return True if validation passed, false otherwise
 */
fun Validator.validateBoolean(value: Boolean): Boolean {
    return validate(value.toString())
} 
