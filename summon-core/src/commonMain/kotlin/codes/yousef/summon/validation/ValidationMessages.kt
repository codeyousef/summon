package codes.yousef.summon.validation

/**
 * Centralized validation error messages used throughout the Summon framework.
 * This provides consistency and makes it easier to localize validation messages.
 */
object ValidationMessages {

    // Required field validation
    /** The property declaration value. */
    const val REQUIRED_FIELD = "This field is required"

    // Email validation
    /** The property declaration value. */
    const val INVALID_EMAIL = "Please enter a valid email address"

    // Length validation
    /** The property declaration value. */
    const val MIN_LENGTH_TEMPLATE = "Must be at least %d characters"
    /** The property declaration value. */
    const val MAX_LENGTH_TEMPLATE = "Must be no more than %d characters"

    // Format validation
    /** The property declaration value. */
    const val INVALID_FORMAT = "Input format is incorrect"

    // Number validation
    /** The property declaration value. */
    const val MUST_BE_NUMBER = "Must be a number"
    /** The property declaration value. */
    const val MUST_BE_POSITIVE = "Must be a positive number"

    // General validation
    /** The property declaration value. */
    const val VALIDATION_FAILED = "Validation failed"

    /**
     * Helper functions to format template messages
     */
    fun minLength(length: Int): String = "Must be at least $length characters"
    /**
     * Executes the max length operation.
     *
     * @param length The length value.
     * @return The resulting value.
     */
    fun maxLength(length: Int): String = "Must be no more than $length characters"
}