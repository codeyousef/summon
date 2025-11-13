package codes.yousef.example.todo.services

import codes.yousef.example.todo.models.Todo
import codes.yousef.example.todo.models.TodoSession
import codes.yousef.example.todo.models.TodoStats
import codes.yousef.example.todo.models.ValidationException

/**
 * Service interface for todo CRUD operations
 */
interface TodoService {
    /**
     * Create a new todo item
     * @param text The todo text (must be non-blank and ≤200 chars)
     * @return Created todo item
     * @throws ValidationException if text is invalid
     */
    fun create(text: String): Todo

    /**
     * Get all todos for the current session
     * @return List of todos ordered by creation date (newest first)
     */
    fun getAll(): List<Todo>

    /**
     * Update the text of an existing todo
     * @param id Todo ID to update
     * @param text New text (must be non-blank and ≤200 chars)
     * @return Updated todo or null if not found
     * @throws ValidationException if text is invalid
     */
    fun update(id: Int, text: String): Todo?

    /**
     * Toggle the completion status of a todo
     * @param id Todo ID to toggle
     * @return Updated todo or null if not found
     */
    fun toggle(id: Int): Todo?

    /**
     * Delete a todo item
     * @param id Todo ID to delete
     * @return true if deleted, false if not found
     */
    fun delete(id: Int): Boolean

    /**
     * Get a specific todo by ID
     * @param id Todo ID to retrieve
     * @return Todo item or null if not found
     */
    fun getById(id: Int): Todo?

    /**
     * Get todo statistics
     * @return TodoStats with total and completed counts
     */
    fun getStats(): TodoStats
}

/**
 * Session-based implementation of TodoService that returns updated sessions
 */
class SessionTodoService(
    var session: TodoSession = TodoSession()
) : TodoService {

    override fun create(text: String): Todo {
        // Validate input
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) {
            throw ValidationException("Todo text cannot be empty")
        }
        if (trimmedText.length > 200) {
            throw ValidationException("Todo text too long (max 200 characters)")
        }

        // Create and store todo
        val (id, newSession) = session.withNextId()
        val todo = Todo(id = id, text = trimmedText)
        session = newSession.withAddedTodo(todo)
        return todo
    }

    override fun getAll(): List<Todo> {
        return session.getAllTodos()
    }

    override fun update(id: Int, text: String): Todo? {
        // Validate input
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) {
            throw ValidationException("Todo text cannot be empty")
        }
        if (trimmedText.length > 200) {
            throw ValidationException("Todo text too long (max 200 characters)")
        }

        // Update if exists
        val existingTodo = session.getTodoById(id) ?: return null
        val updatedTodo = existingTodo.updateText(trimmedText)
        val newSession = session.withUpdatedTodo(id, updatedTodo) ?: return null
        session = newSession
        return updatedTodo
    }

    override fun toggle(id: Int): Todo? {
        val existingTodo = session.getTodoById(id) ?: return null
        val toggledTodo = existingTodo.toggle()
        val newSession = session.withUpdatedTodo(id, toggledTodo) ?: return null
        session = newSession
        return toggledTodo
    }

    override fun delete(id: Int): Boolean {
        val newSession = session.withRemovedTodo(id) ?: return false
        session = newSession
        return true
    }

    override fun getById(id: Int): Todo? {
        return session.getTodoById(id)
    }

    override fun getStats(): TodoStats {
        return session.getStats()
    }
}