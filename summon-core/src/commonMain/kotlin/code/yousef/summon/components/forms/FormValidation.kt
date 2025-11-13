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
package code.yousef.summon.components.forms

/**
 * Represents a validation rule with an error message.
 */
sealed class Validator {
    /**
     * Validates a value and returns an error message if invalid, null if valid.
     */
    abstract fun validate(value: String): String?

    /**
     * Required field validator.
     */
    data class Required(val message: String = "This field is required") : Validator() {
        override fun validate(value: String): String? {
            return if (value.trim().isEmpty()) message else null
        }
    }

    /**
     * Email format validator.
     */
    data class Email(val message: String = "Must be a valid email address") : Validator() {
        private val emailRegex = Regex(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )

        override fun validate(value: String): String? {
            return if (value.isNotEmpty() && !emailRegex.matches(value)) message else null
        }
    }

    /**
     * Minimum length validator.
     */
    data class MinLength(
        val length: Int,
        val message: String = "Must be at least $length characters"
    ) : Validator() {
        override fun validate(value: String): String? {
            return if (value.length < length) message else null
        }
    }

    /**
     * Maximum length validator.
     */
    data class MaxLength(
        val length: Int,
        val message: String = "Must be at most $length characters"
    ) : Validator() {
        override fun validate(value: String): String? {
            return if (value.length > length) message else null
        }
    }

    /**
     * Pattern/regex validator.
     */
    data class Pattern(
        val pattern: String,
        val message: String = "Invalid format"
    ) : Validator() {
        private val regex by lazy { Regex(pattern) }

        override fun validate(value: String): String? {
            return if (value.isNotEmpty() && !regex.matches(value)) message else null
        }
    }

    /**
     * Minimum value validator for numeric fields.
     */
    data class Min(
        val min: Double,
        val message: String = "Must be at least $min"
    ) : Validator() {
        override fun validate(value: String): String? {
            val num = value.toDoubleOrNull() ?: return "Must be a number"
            return if (num < min) message else null
        }
    }

    /**
     * Maximum value validator for numeric fields.
     */
    data class Max(
        val max: Double,
        val message: String = "Must be at most $max"
    ) : Validator() {
        override fun validate(value: String): String? {
            val num = value.toDoubleOrNull() ?: return "Must be a number"
            return if (num > max) message else null
        }
    }

    /**
     * URL format validator.
     */
    data class Url(val message: String = "Must be a valid URL") : Validator() {
        private val urlRegex = Regex(
            "^https?://[\\w.-]+(:\\d+)?(/[\\w./?#&=-]*)?$"
        )

        override fun validate(value: String): String? {
            return if (value.isNotEmpty() && !urlRegex.matches(value)) message else null
        }
    }

    /**
     * Custom validator with user-defined logic.
     */
    data class Custom(
        val validationFn: (String) -> String?
    ) : Validator() {
        override fun validate(value: String): String? {
            return validationFn(value)
        }
    }

    /**
     * Validator that checks if value matches another field.
     */
    data class Matches(
        val otherValue: String,
        val message: String = "Values do not match"
    ) : Validator() {
        override fun validate(value: String): String? {
            return if (value != otherValue) message else null
        }
    }

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
