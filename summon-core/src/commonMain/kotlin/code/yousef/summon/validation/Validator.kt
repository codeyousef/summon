package code.yousef.summon.validation

/**
 * Core interface for implementing form field validation logic.
 *
 * Validator provides a contract for creating reusable validation logic that can be
 * applied to form inputs, components, and user data. The interface is designed to
 * be simple, composable, and suitable for both synchronous and asynchronous
 * validation scenarios.
 *
 * ## Design Principles
 *
 * - **Single Responsibility**: Each validator focuses on one validation rule
 * - **Composability**: Multiple validators can be combined for complex validation
 * - **Reusability**: Validators can be reused across different forms and components
 * - **Clear Error Messaging**: Validators provide meaningful error messages
 * - **Type Safety**: Strong typing ensures validation correctness
 *
 * ## Implementation Examples
 *
 * ### Simple Validator
 * ```kotlin
 * class NotEmptyValidator : Validator {
 *     override fun validate(value: String): ValidationResult {
 *         return if (value.trim().isNotEmpty()) {
 *             ValidationResult.success()
 *         } else {
 *             ValidationResult.error("Field cannot be empty")
 *         }
 *     }
 *
 *     override val errorMessage = "This field is required"
 * }
 * ```
 *
 * ### Parameterized Validator
 * ```kotlin
 * class RangeValidator(
 *     private val min: Int,
 *     private val max: Int
 * ) : Validator {
 *     override fun validate(value: String): ValidationResult {
 *         val number = value.toIntOrNull()
 *         return when {
 *             number == null -> ValidationResult.error("Must be a number")
 *             number < min -> ValidationResult.error("Must be at least $min")
 *             number > max -> ValidationResult.error("Must be at most $max")
 *             else -> ValidationResult.success()
 *         }
 *     }
 *
 *     override val errorMessage = "Must be between $min and $max"
 * }
 * ```
 *
 * ## Usage in Forms
 *
 * ```kotlin
 * @Composable
 * fun LoginForm() {
 *     var email by remember { mutableStateOf("") }
 *     var emailError by remember { mutableStateOf<String?>(null) }
 *
 *     val emailValidator = EmailValidator()
 *
 *     TextField(
 *         value = email,
 *         onValueChange = { newEmail ->
 *             email = newEmail
 *             val result = emailValidator.validate(newEmail)
 *             emailError = result.errorMessage
 *         },
 *         isError = emailError != null,
 *         supportingText = emailError
 *     )
 * }
 * ```
 *
 * ## Validation Chaining
 *
 * ```kotlin
 * class CompositeValidator(
 *     private val validators: List<Validator>
 * ) : Validator {
 *     override fun validate(value: String): ValidationResult {
 *         for (validator in validators) {
 *             val result = validator.validate(value)
 *             if (!result.isValid) {
 *                 return result
 *             }
 *         }
 *         return ValidationResult.success()
 *     }
 *
 *     override val errorMessage = "Validation failed"
 * }
 *
 * // Usage
 * val passwordValidator = CompositeValidator(listOf(
 *     RequiredValidator(),
 *     MinLengthValidator(8),
 *     PatternValidator(Regex(".*[A-Z].*"), "Must contain uppercase letter")
 * ))
 * ```
 *
 * @see ValidationResult for validation outcomes
 * @see ValidationMessages for standard error messages
 * @since 1.0.0
 */
interface Validator {
    /**
     * Validates the provided value according to the validator's rules.
     *
     * This method performs the core validation logic and returns a ValidationResult
     * indicating whether the validation passed or failed. The method should be
     * stateless and safe to call multiple times with the same input.
     *
     * ## Validation Logic
     *
     * Implementations should:
     * - Return ValidationResult.success() for valid values
     * - Return ValidationResult.error(message) for invalid values
     * - Handle edge cases gracefully (null, empty strings, etc.)
     * - Provide specific, actionable error messages
     *
     * ## Performance Considerations
     *
     * - Validation should be fast as it may be called on every keystroke
     * - Avoid expensive operations like network calls in synchronous validation
     * - Consider caching results for complex validation logic
     *
     * ## Thread Safety
     *
     * Validator implementations should be thread-safe as they may be used
     * concurrently in multi-threaded environments.
     *
     * @param value The string value to validate (may be empty but not null)
     * @return ValidationResult indicating success or failure with optional error message
     * @see ValidationResult for result structure
     * @since 1.0.0
     */
    fun validate(value: String): ValidationResult

    /**
     * Default error message to display when validation fails.
     *
     * This property provides a fallback error message for cases where the
     * validate method doesn't return a specific error message. It's useful
     * for validators with fixed error messages or as a backup for complex
     * validation scenarios.
     *
     * ## Usage
     *
     * The error message should be:
     * - User-friendly and easily understood
     * - Actionable (tell users how to fix the issue)
     * - Concise but informative
     * - Localized if the application supports multiple languages
     *
     * ## Example
     *
     * ```kotlin
     * class AgeValidator : Validator {
     *     override val errorMessage = "Age must be between 18 and 100"
     *
     *     override fun validate(value: String): ValidationResult {
     *         // validation logic...
     *         return if (isValid) {
     *             ValidationResult.success()
     *         } else {
     *             ValidationResult.error(errorMessage)
     *         }
     *     }
     * }
     * ```
     *
     * @return User-friendly error message for failed validation
     * @see ValidationMessages for standard error messages
     * @since 1.0.0
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
        val isValid = value.trim().isNotEmpty()
        return ValidationResult(isValid, if (!isValid) errorMessage else null)
    }
}

/**
 * Email validator that ensures a value matches an email pattern.
 */
class EmailValidator(
    override val errorMessage: String = ValidationMessages.INVALID_EMAIL
) : Validator {
    private val emailRegex = kotlin.text.Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    override fun validate(value: String): ValidationResult {
        val isValid = value.isEmpty() || emailRegex.matches(value)
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
    private val pattern: kotlin.text.Regex,
    override val errorMessage: String = ValidationMessages.INVALID_FORMAT
) : Validator {
    override fun validate(value: String): ValidationResult {
        val isValid = value.isEmpty() || pattern.matches(value)
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