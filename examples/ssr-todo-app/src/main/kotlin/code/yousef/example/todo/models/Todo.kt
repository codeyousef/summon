package code.yousef.example.todo.models

/**
 * Data model representing a todo item.
 */
data class Todo(
    val id: Int,
    val text: String,
    val isCompleted: Boolean = false
)