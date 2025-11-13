package codes.yousef.summon.validation

/**
 * Simple class to represent validation result for form components
 */
data class ValidationResult(val isValid: Boolean, val errorMessage: String? = null) 