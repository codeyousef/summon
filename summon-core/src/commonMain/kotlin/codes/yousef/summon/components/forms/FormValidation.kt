/**
 * # Form Validation Helpers
 *
 * Comprehensive form validation system for the Summon framework.
 * Provides type-safe validators, validation state management, and
 * built-in error handling for form fields.
 *
 * ## Features
 *
 * - **Built-in Validators**: Common validation patterns ready to use
 * - **Custom Validators**: Easy to create custom validation logic
 * - **Async Validation**: Support for server-side validation
 * - **Error Messages**: Customizable error messages
 * - **Composable**: Works with all form components
 *
 * ## Usage Examples
 *
 * ```kotlin
 * // Basic validation
 * FormTextField(
 *     name = "email",
 *     label = "Email",
 *     validators = listOf(
 *         Validator.required("Email is required"),
 *         Validator.email("Must be a valid email")
 *     )
 * )
 *
 * // Multiple validators
 * FormTextField(
 *     name = "password",
 *     label = "Password",
 *     validators = listOf(
 *         Validator.required("Password is required"),
 *         Validator.minLength(8, "Must be at least 8 characters"),
 *         Validator.pattern(
 *             "^(?=.*[A-Z])(?=.*[0-9]).*$",
 *             "Must contain uppercase and number"
 *         )
 *     )
 * )
 *
 * // Custom validator
 * FormTextField(
 *     name = "username",
 *     validators = listOf(
 *         Validator.custom { value ->
 *             if (value.contains(" ")) {
 *                 "Username cannot contain spaces"
 *             } else null
 *         }
 *     )
 * )
 * ```
 *
 * @see FormTextField for text field with validation
 * @see Form for form component
 * @since 1.0.0
 */
package codes.yousef.summon.components.forms

/**
 * Represents a validation rule with an error message.
 */
sealed class Validator {
    /**
     * Validates a value and returns an error message if invalid, null if valid.
     */
    abstract fun validate(value: String): String?

    /**
     * Rejects blank values.
     *
     * @property message rejection message
     */
    data class Required(val message: String = "This field is required") : Validator() {
        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            return if (value.trim().isEmpty()) message else null
        }
    }

    /**
     * Rejects non-empty malformed email addresses.
     *
     * @property message rejection message
     */
    data class Email(val message: String = "Must be a valid email address") : Validator() {
        private val emailRegex = Regex(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )

        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            return if (value.isNotEmpty() && !emailRegex.matches(value)) message else null
        }
    }

    /**
     * Enforces a minimum string length.
     *
     * @property length minimum length
     * @property message rejection message
     */
    data class MinLength(
        val length: Int,
        val message: String = "Must be at least $length characters"
    ) : Validator() {
        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            return if (value.length < length) message else null
        }
    }

    /**
     * Enforces a maximum string length.
     *
     * @property length maximum length
     * @property message rejection message
     */
    data class MaxLength(
        val length: Int,
        val message: String = "Must be at most $length characters"
    ) : Validator() {
        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            return if (value.length > length) message else null
        }
    }

    /**
     * Enforces a regular-expression match.
     *
     * @property pattern regular expression
     * @property message rejection message
     */
    data class Pattern(
        val pattern: String,
        val message: String = "Invalid format"
    ) : Validator() {
        private val regex by lazy { Regex(pattern) }

        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            return if (value.isNotEmpty() && !regex.matches(value)) message else null
        }
    }

    /**
     * Enforces a minimum numeric value.
     *
     * @property min minimum accepted value
     * @property message rejection message
     */
    data class Min(
        val min: Double,
        val message: String = "Must be at least $min"
    ) : Validator() {
        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            val num = value.toDoubleOrNull() ?: return "Must be a number"
            return if (num < min) message else null
        }
    }

    /**
     * Enforces a maximum numeric value.
     *
     * @property max maximum accepted value
     * @property message rejection message
     */
    data class Max(
        val max: Double,
        val message: String = "Must be at most $max"
    ) : Validator() {
        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            val num = value.toDoubleOrNull() ?: return "Must be a number"
            return if (num > max) message else null
        }
    }

    /**
     * Enforces an HTTP(S) URL shape.
     *
     * @property message rejection message
     */
    data class Url(val message: String = "Must be a valid URL") : Validator() {
        private val urlRegex = Regex(
            "^https?://[\\w.-]+(:\\d+)?(/[\\w./?#&=-]*)?$"
        )

        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            return if (value.isNotEmpty() && !urlRegex.matches(value)) message else null
        }
    }

    /**
     * Delegates validation to caller code.
     *
     * @property validationFn returns an error or `null`
     */
    data class Custom(
        val validationFn: (String) -> String?
    ) : Validator() {
        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            return validationFn(value)
        }
    }

    /**
     * Requires equality with another field value.
     *
     * @property otherValue comparison value
     * @property message rejection message
     */
    data class Matches(
        val otherValue: String,
        val message: String = "Values do not match"
    ) : Validator() {
        /**
         * Validates the supplied value.
         *
         * @param value Value to process.
         * @return The resulting value.
         */
        override fun validate(value: String): String? {
            return if (value != otherValue) message else null
        }
    }

    /** Convenience validator factories. */
    companion object {
        /**
         * Creates a required field validator.
         */
        fun required(message: String = "This field is required") = Required(message)

        /**
         * Creates an email validator.
         */
        fun email(message: String = "Must be a valid email address") = Email(message)

        /**
         * Creates a minimum length validator.
         */
        fun minLength(length: Int, message: String? = null) =
            MinLength(length, message ?: "Must be at least $length characters")

        /**
         * Creates a maximum length validator.
         */
        fun maxLength(length: Int, message: String? = null) =
            MaxLength(length, message ?: "Must be at most $length characters")

        /**
         * Creates a pattern validator.
         */
        fun pattern(pattern: String, message: String = "Invalid format") =
            Pattern(pattern, message)

        /**
         * Creates a minimum value validator.
         */
        fun min(min: Double, message: String? = null) =
            Min(min, message ?: "Must be at least $min")

        /**
         * Creates a maximum value validator.
         */
        fun max(max: Double, message: String? = null) =
            Max(max, message ?: "Must be at most $max")

        /**
         * Creates a URL validator.
         */
        fun url(message: String = "Must be a valid URL") = Url(message)

        /**
         * Creates a custom validator.
         */
        fun custom(validationFn: (String) -> String?) = Custom(validationFn)

        /**
         * Creates a matches validator.
         */
        fun matches(otherValue: String, message: String = "Values do not match") =
            Matches(otherValue, message)
    }
}

/**
 * Validates a value against a list of validators.
 * Returns the first error message encountered, or null if all validators pass.
 */
fun validateValue(value: String, validators: List<Validator>): String? {
    validators.forEach { validator ->
        val error = validator.validate(value)
        if (error != null) return error
    }
    return null
}

/**
 * Validates a value against a list of validators and returns all error messages.
 */
fun validateValueAll(value: String, validators: List<Validator>): List<String> {
    return validators.mapNotNull { it.validate(value) }
}

/**
 * Async validator for server-side validation.
 * Platform-specific implementations provide the actual validation logic.
 */
interface AsyncValidator {
    /**
     * Validates a value asynchronously and returns an error message if invalid.
     */
    suspend fun validate(value: String): String?
}

/**
 * Synchronous or asynchronous validation state.
 */
sealed class ValidationResult {
    /**
     * Completed validation.
     *
     * @property error rejection message, or `null` when valid
     */
    data class Sync(val error: String?) : ValidationResult()
    /**
     * Asynchronous validation state.
     *
     * @property pending whether validation is in progress
     * @property error rejection message, or `null` when valid or pending
     */
    data class Async(val pending: Boolean, val error: String?) : ValidationResult()
}

/**
 * Form validation state manager.
 */
class FormValidationState {
    private val fieldErrors = mutableMapOf<String, String?>()
    private val fieldValidators = mutableMapOf<String, List<Validator>>()
    private val asyncValidators = mutableMapOf<String, AsyncValidator>()
    private val fieldValidationRevisions = mutableMapOf<String, Long>()

    private fun nextValidationRevision(fieldName: String): Long {
        val next = (fieldValidationRevisions[fieldName] ?: 0L) + 1L
        fieldValidationRevisions[fieldName] = next
        return next
    }

    private fun validateFieldNow(fieldName: String, value: String): String? {
        val validators = fieldValidators[fieldName].orEmpty()
        return validateValue(value, validators)
    }

    /**
     * Registers validators for a field.
     */
    fun registerField(fieldName: String, validators: List<Validator>) {
        fieldValidators[fieldName] = validators
    }

    /**
     * Registers an async validator for a field.
     */
    fun registerAsyncValidator(fieldName: String, validator: AsyncValidator) {
        asyncValidators[fieldName] = validator
    }

    /**
     * Validates a single field.
     */
    fun validateField(fieldName: String, value: String): String? {
        nextValidationRevision(fieldName)
        val error = validateFieldNow(fieldName, value)
        fieldErrors[fieldName] = error
        return error
    }

    /**
     * Validates a single field asynchronously.
     */
    suspend fun validateFieldAsync(fieldName: String, value: String): String? {
        val revision = nextValidationRevision(fieldName)
        val syncError = validateFieldNow(fieldName, value)
        if (syncError != null) {
            if (fieldValidationRevisions[fieldName] == revision) fieldErrors[fieldName] = syncError
            return if (fieldValidationRevisions[fieldName] == revision) syncError else null
        }

        val asyncError = asyncValidators[fieldName]?.validate(value)
        if (fieldValidationRevisions[fieldName] != revision) return null
        fieldErrors[fieldName] = asyncError
        return asyncError
    }

    /**
     * Validates all registered fields.
     */
    fun validateAll(values: Map<String, String>): Map<String, String?> {
        val errors = mutableMapOf<String, String?>()
        fieldValidators.keys.forEach { fieldName ->
            val value = values[fieldName] ?: ""
            errors[fieldName] = validateField(fieldName, value)
        }
        return errors
    }

    /**
     * Gets the error message for a field.
     */
    fun getError(fieldName: String): String? = fieldErrors[fieldName]

    /**
     * Checks if a field has an error.
     */
    fun hasError(fieldName: String): Boolean = fieldErrors[fieldName] != null

    /**
     * Checks if the entire form is valid.
     */
    fun isValid(): Boolean = fieldErrors.values.all { it == null }

    /**
     * Clears errors for a specific field.
     */
    fun clearFieldError(fieldName: String) {
        nextValidationRevision(fieldName)
        fieldErrors[fieldName] = null
    }

    /**
     * Clears all errors.
     */
    fun clearAllErrors() {
        fieldValidationRevisions.keys.toList().forEach(::nextValidationRevision)
        fieldErrors.clear()
    }
}

/**
 * Server-side validation request.
 *
 * @property fieldName validated field
 * @property value candidate value
 * @property formData other submitted field values
 */
data class ServerValidationRequest(
    val fieldName: String,
    val value: String,
    val formData: Map<String, String> = emptyMap()
)

/**
 * Server-side validation response.
 *
 * @property valid whether the candidate passed
 * @property error optional rejection message
 */
data class ServerValidationResponse(
    val valid: Boolean,
    val error: String? = null
)
