package com.summon

/**
 * Interface for validating input fields.
 */
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
    override val errorMessage: String = "This field is required"
) : Validator {
    override fun validate(value: String): Boolean = value.isNotBlank()
}

/**
 * Email validator that ensures a value matches an email pattern.
 */
class EmailValidator(
    override val errorMessage: String = "Please enter a valid email address"
) : Validator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    
    override fun validate(value: String): Boolean {
        return value.isEmpty() || value.matches(emailRegex)
    }
}

/**
 * Minimum length validator.
 */
class MinLengthValidator(
    private val minLength: Int,
    override val errorMessage: String = "Must be at least $minLength characters"
) : Validator {
    override fun validate(value: String): Boolean = value.length >= minLength
}

/**
 * Maximum length validator.
 */
class MaxLengthValidator(
    private val maxLength: Int,
    override val errorMessage: String = "Must be no more than $maxLength characters"
) : Validator {
    override fun validate(value: String): Boolean = value.length <= maxLength
}

/**
 * Pattern validator that ensures a value matches a regex pattern.
 */
class PatternValidator(
    private val pattern: Regex,
    override val errorMessage: String = "Input format is incorrect"
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