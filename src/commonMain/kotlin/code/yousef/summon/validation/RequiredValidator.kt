package code.yousef.summon.validation

/**
 * Validator that checks if a field has a non-empty value.
 * @param errorMessage The error message to display when validation fails
 */
class RequiredValidator(private val errorMessage: String) : Validator {
    override fun validate(value: String): ValidationResult {
        return if (value.isNotBlank()) {
            ValidationResult(true)
        } else {
            ValidationResult(false, errorMessage)
        }
    }
} 