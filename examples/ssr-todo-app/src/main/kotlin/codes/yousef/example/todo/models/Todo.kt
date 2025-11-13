package codes.yousef.example.todo.models

import kotlinx.serialization.Serializable

/**
 * Core Todo entity representing a single todo item
 */
@Serializable
data class Todo(
    val id: Int,
    val text: String,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    init {
        require(id > 0) { "Todo ID must be positive" }
        require(text.isNotBlank()) { "Todo text cannot be empty" }
        require(text.length <= 200) { "Todo text too long (max 200 characters)" }
    }

    /**
     * Create a copy with toggled completion status
     */
    fun toggle(): Todo = copy(completed = !completed)

    /**
     * Create a copy with updated text
     */
    fun updateText(newText: String): Todo {
        require(newText.isNotBlank()) { "Todo text cannot be empty" }
        require(newText.length <= 200) { "Todo text too long (max 200 characters)" }
        return copy(text = newText)
    }
}

/**
 * Statistics about todo completion
 */
data class TodoStats(
    val total: Int,
    val completed: Int
) {
    val remaining: Int get() = total - completed
}