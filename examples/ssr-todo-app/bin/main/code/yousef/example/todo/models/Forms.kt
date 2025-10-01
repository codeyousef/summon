package code.yousef.example.todo.models

/**
 * Form model for creating new todos
 */
data class CreateTodoForm(
    val text: String
) {
    /**
     * Validate the form data
     * @return null if valid, error message if invalid
     */
    fun validate(): String? = when {
        text.isBlank() -> "Todo text cannot be empty"
        text.length > 200 -> "Todo text too long (max 200 characters)"
        else -> null
    }

    /**
     * Check if form data is valid
     */
    fun isValid(): Boolean = validate() == null

    companion object {
        /**
         * Create form from HTTP parameters
         */
        fun fromParameters(parameters: Map<String, String>): CreateTodoForm {
            val text = parameters["text"]?.trim() ?: ""
            return CreateTodoForm(text)
        }
    }
}

/**
 * Form model for updating existing todos
 */
data class UpdateTodoForm(
    val id: Int,
    val text: String
) {
    /**
     * Validate the form data
     * @return null if valid, error message if invalid
     */
    fun validate(): String? = when {
        id <= 0 -> "Invalid todo ID"
        text.isBlank() -> "Todo text cannot be empty"
        text.length > 200 -> "Todo text too long (max 200 characters)"
        else -> null
    }

    /**
     * Check if form data is valid
     */
    fun isValid(): Boolean = validate() == null

    companion object {
        /**
         * Create form from HTTP parameters with ID
         */
        fun fromParameters(id: Int, parameters: Map<String, String>): UpdateTodoForm {
            val text = parameters["text"]?.trim() ?: ""
            return UpdateTodoForm(id, text)
        }
    }
}

/**
 * Form state for rendering with potential errors
 */
data class FormState(
    val errorMessage: String? = null,
    val formData: Map<String, String> = emptyMap()
) {
    val hasError: Boolean get() = errorMessage != null

    companion object {
        fun withError(message: String, formData: Map<String, String> = emptyMap()): FormState {
            return FormState(errorMessage = message, formData = formData)
        }

        fun success(): FormState = FormState()
    }
}