package codes.yousef.example.todo.models

import kotlinx.serialization.Serializable

/**
 * Session-based storage for todos
 * Each user session maintains its own isolated todo list
 *
 * Using immutable Map for Ktor session compatibility
 */
@Serializable
data class TodoSession(
    val todos: Map<Int, Todo> = emptyMap(),
    val nextId: Int = 1
) {
    /**
     * Generate next unique ID and return updated session
     */
    fun withNextId(): Pair<Int, TodoSession> {
        return Pair(nextId, copy(nextId = nextId + 1))
    }

    /**
     * Add a todo to the session and return new session
     */
    fun withAddedTodo(todo: Todo): TodoSession {
        return copy(todos = todos + (todo.id to todo))
    }

    /**
     * Get all todos ordered by creation date (newest first)
     */
    fun getAllTodos(): List<Todo> = todos.values
        .sortedByDescending { it.createdAt }

    /**
     * Get todo by ID
     */
    fun getTodoById(id: Int): Todo? = todos[id]

    /**
     * Update existing todo and return new session
     */
    fun withUpdatedTodo(id: Int, updatedTodo: Todo): TodoSession? {
        return if (todos.containsKey(id)) {
            copy(todos = todos + (id to updatedTodo))
        } else {
            null
        }
    }

    /**
     * Remove todo by ID and return new session
     */
    fun withRemovedTodo(id: Int): TodoSession? {
        return if (todos.containsKey(id)) {
            copy(todos = todos - id)
        } else {
            null
        }
    }

    /**
     * Get statistics about todos
     */
    fun getStats(): TodoStats {
        val allTodos = getAllTodos()
        return TodoStats(
            total = allTodos.size,
            completed = allTodos.count { it.completed }
        )
    }

    /**
     * Check if session has any todos
     */
    fun isEmpty(): Boolean = todos.isEmpty()

    /**
     * Clear all todos from session and return new session
     */
    fun cleared(): TodoSession {
        return TodoSession()
    }
}