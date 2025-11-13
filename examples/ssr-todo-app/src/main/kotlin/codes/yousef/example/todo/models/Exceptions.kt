package codes.yousef.example.todo.models

/**
 * Exception thrown when validation fails
 */
class ValidationException(message: String) : Exception(message)

/**
 * Exception thrown when a todo is not found
 */
class TodoNotFoundException(id: Int) : Exception("Todo with ID $id not found")