package code.yousef.example.quarkus.model

import java.time.LocalDateTime
import java.util.*

/**
 * Todo item model
 */
data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val completed: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: LocalDateTime? = null,
    val userId: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Creates a copy of this todo with updated fields
     */
    fun update(
        title: String = this.title,
        description: String = this.description,
        completed: Boolean = this.completed,
        priority: Priority = this.priority,
        dueDate: LocalDateTime? = this.dueDate
    ): Todo = copy(
        title = title,
        description = description,
        completed = completed,
        priority = priority,
        dueDate = dueDate,
        updatedAt = LocalDateTime.now()
    )
}

/**
 * Priority levels for todos
 */
enum class Priority(val displayName: String, val color: String) {
    LOW("Low", "#4CAF50"),
    MEDIUM("Medium", "#FF9800"),
    HIGH("High", "#F44336"),
    URGENT("Urgent", "#9C27B0");
    
    companion object {
        fun fromString(value: String): Priority = 
            values().find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
    }
}

/**
 * Request model for creating a new todo
 */
data class CreateTodoRequest(
    val title: String,
    val description: String = "",
    val priority: String = "MEDIUM",
    val dueDate: String? = null // ISO format string
)

/**
 * Request model for updating an existing todo
 */
data class UpdateTodoRequest(
    val title: String? = null,
    val description: String? = null,
    val completed: Boolean? = null,
    val priority: String? = null,
    val dueDate: String? = null // ISO format string
)