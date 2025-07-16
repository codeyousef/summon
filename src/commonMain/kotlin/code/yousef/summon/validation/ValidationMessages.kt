package code.yousef.summon.validation

/**
 * Centralized validation error messages used throughout the Summon framework.
 * This provides consistency and makes it easier to localize validation messages.
 */
object ValidationMessages {
    
    // Required field validation
    const val REQUIRED_FIELD = "This field is required"
    
    // Email validation
    const val INVALID_EMAIL = "Please enter a valid email address"
    
    // Length validation
    const val MIN_LENGTH_TEMPLATE = "Must be at least %d characters"
    const val MAX_LENGTH_TEMPLATE = "Must be no more than %d characters"
    
    // Format validation
    const val INVALID_FORMAT = "Input format is incorrect"
    
    // Number validation
    const val MUST_BE_NUMBER = "Must be a number"
    const val MUST_BE_POSITIVE = "Must be a positive number"
    
    // General validation
    const val VALIDATION_FAILED = "Validation failed"
    
    /**
     * Helper functions to format template messages
     */
    fun minLength(length: Int): String = "Must be at least $length characters"
    fun maxLength(length: Int): String = "Must be no more than $length characters"
}