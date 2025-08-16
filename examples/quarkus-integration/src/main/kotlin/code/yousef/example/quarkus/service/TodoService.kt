package code.yousef.example.quarkus.service

import code.yousef.example.quarkus.model.*
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for managing todos
 */
@ApplicationScoped
class TodoService {
    
    private val todos = ConcurrentHashMap<String, Todo>()
    
    init {
        // Demo todos will be created when needed for the actual demo user
    }
    
    /**
     * Gets all todos for a user
     */
    fun getTodosByUserId(userId: String): List<Todo> {
        // Create demo todos for first access
        if (todos.values.none { it.userId == userId }) {
            createDemoTodos(userId)
        }
        
        return todos.values.filter { it.userId == userId }
            .sortedWith(compareByDescending<Todo> { it.priority.ordinal }
                .thenBy { it.completed }
                .thenBy { it.createdAt })
    }
    
    /**
     * Gets a specific todo by ID
     */
    fun getTodoById(todoId: String, userId: String): Todo? {
        val todo = todos[todoId]
        return if (todo?.userId == userId) todo else null
    }
    
    /**
     * Creates a new todo
     */
    fun createTodo(request: CreateTodoRequest, userId: String): Result<Todo> {
        if (request.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Title cannot be empty"))
        }
        
        val dueDate = request.dueDate?.let {
            try {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e: Exception) {
                null
            }
        }
        
        val todo = Todo(
            title = request.title.trim(),
            description = request.description.trim(),
            priority = Priority.fromString(request.priority),
            dueDate = dueDate,
            userId = userId
        )
        
        todos[todo.id] = todo
        return Result.success(todo)
    }
    
    /**
     * Updates an existing todo
     */
    fun updateTodo(todoId: String, request: UpdateTodoRequest, userId: String): Result<Todo> {
        val existingTodo = getTodoById(todoId, userId)
            ?: return Result.failure(IllegalArgumentException("Todo not found"))
        
        val dueDate = request.dueDate?.let {
            try {
                LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e: Exception) {
                existingTodo.dueDate
            }
        }
        
        val updatedTodo = existingTodo.update(
            title = request.title?.takeIf { it.isNotBlank() } ?: existingTodo.title,
            description = request.description ?: existingTodo.description,
            completed = request.completed ?: existingTodo.completed,
            priority = request.priority?.let { Priority.fromString(it) } ?: existingTodo.priority,
            dueDate = dueDate ?: existingTodo.dueDate
        )
        
        todos[todoId] = updatedTodo
        return Result.success(updatedTodo)
    }
    
    /**
     * Toggles the completion status of a todo
     */
    fun toggleTodo(todoId: String, userId: String): Result<Todo> {
        val existingTodo = getTodoById(todoId, userId)
            ?: return Result.failure(IllegalArgumentException("Todo not found"))
        
        val updatedTodo = existingTodo.update(completed = !existingTodo.completed)
        todos[todoId] = updatedTodo
        return Result.success(updatedTodo)
    }
    
    /**
     * Deletes a todo
     */
    fun deleteTodo(todoId: String, userId: String): Result<Unit> {
        val todo = getTodoById(todoId, userId)
            ?: return Result.failure(IllegalArgumentException("Todo not found"))
        
        todos.remove(todoId)
        return Result.success(Unit)
    }
    
    /**
     * Gets todo statistics for a user
     */
    fun getTodoStats(userId: String): TodoStats {
        val userTodos = getTodosByUserId(userId)
        return TodoStats(
            total = userTodos.size,
            completed = userTodos.count { it.completed },
            pending = userTodos.count { !it.completed },
            overdue = userTodos.count { 
                !it.completed && it.dueDate != null && it.dueDate.isBefore(LocalDateTime.now()) 
            },
            highPriority = userTodos.count { it.priority == Priority.HIGH || it.priority == Priority.URGENT }
        )
    }
    
    /**
     * Creates demo todos for testing
     */
    private fun createDemoTodos(userId: String) {
        val demoTodos = listOf(
            Todo(
                title = "Welcome to Summon Todo App!",
                description = "This is a demo todo created with Summon UI framework and Quarkus backend. Try creating, editing, and completing todos!",
                priority = Priority.HIGH,
                userId = userId
            ),
            Todo(
                title = "Try the theme switcher",
                description = "Click the theme toggle in the header to switch between light and dark modes",
                priority = Priority.MEDIUM,
                userId = userId
            ),
            Todo(
                title = "Test language switching",
                description = "Use the language selector to see the app in different languages",
                priority = Priority.LOW,
                userId = userId
            ),
            Todo(
                title = "Completed task example",
                description = "This task shows how completed todos look",
                completed = true,
                priority = Priority.MEDIUM,
                userId = userId,
                updatedAt = LocalDateTime.now().minusHours(2)
            ),
            Todo(
                title = "Urgent task with due date",
                description = "This demonstrates urgent priority and due date functionality",
                priority = Priority.URGENT,
                dueDate = LocalDateTime.now().plusDays(1),
                userId = userId
            )
        )
        
        demoTodos.forEach { todo ->
            todos[todo.id] = todo
        }
    }
}

/**
 * Todo statistics model
 */
data class TodoStats(
    val total: Int,
    val completed: Int,
    val pending: Int,
    val overdue: Int,
    val highPriority: Int
)