package code.yousef.example.quarkus.ui.state

import code.yousef.example.quarkus.model.*
import code.yousef.example.quarkus.service.TodoStats
import code.yousef.example.quarkus.ui.theme.AppTheme
import code.yousef.example.quarkus.ui.theme.AppThemes
import code.yousef.summon.i18n.Language
import code.yousef.summon.state.mutableStateOf

/**
 * Global application state management
 */
object AppState {
    
    // Authentication state
    val isLoggedIn = mutableStateOf(false)
    val currentUser = mutableStateOf<PublicUser?>(null)
    val sessionToken = mutableStateOf<String?>(null)
    
    // UI state
    val currentTheme = mutableStateOf(AppThemes.lightTheme)
    val currentLanguage = mutableStateOf(Language("en", "English", code.yousef.summon.i18n.LayoutDirection.LTR))
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)
    
    // Todo state
    val todos = mutableStateOf<List<Todo>>(emptyList())
    val todoStats = mutableStateOf<TodoStats?>(null)
    
    // Filter and sort state
    val filterCompleted = mutableStateOf<Boolean?>(null) // null = show all, true = completed only, false = pending only
    val sortBy = mutableStateOf(TodoSortBy.PRIORITY)
    val searchQuery = mutableStateOf("")
    
    /**
     * Updates user session
     */
    fun setUserSession(user: PublicUser, token: String) {
        currentUser.value = user
        sessionToken.value = token
        isLoggedIn.value = true
        
        // Update theme and language from user preferences
        currentTheme.value = when (user.themePreference) {
            ThemePreference.DARK -> AppThemes.darkTheme
            ThemePreference.LIGHT -> AppThemes.lightTheme
            ThemePreference.AUTO -> AppThemes.lightTheme // TODO: Implement auto detection
        }
        
        currentLanguage.value = Language(
            code = user.languagePreference,
            name = getLanguageName(user.languagePreference),
            direction = if (user.languagePreference == "ar") code.yousef.summon.i18n.LayoutDirection.RTL 
                       else code.yousef.summon.i18n.LayoutDirection.LTR
        )
    }
    
    /**
     * Clears user session
     */
    fun clearUserSession() {
        currentUser.value = null
        sessionToken.value = null
        isLoggedIn.value = false
        todos.value = emptyList()
        todoStats.value = null
    }
    
    /**
     * Updates the current theme
     */
    fun setTheme(theme: AppTheme) {
        currentTheme.value = theme
    }
    
    /**
     * Updates the current language
     */
    fun setLanguage(language: Language) {
        currentLanguage.value = language
    }
    
    /**
     * Updates the todos list
     */
    fun setTodos(todoList: List<Todo>) {
        todos.value = todoList
    }
    
    /**
     * Adds a new todo to the list
     */
    fun addTodo(todo: Todo) {
        todos.value = todos.value + todo
    }
    
    /**
     * Updates an existing todo
     */
    fun updateTodo(updatedTodo: Todo) {
        todos.value = todos.value.map { if (it.id == updatedTodo.id) updatedTodo else it }
    }
    
    /**
     * Removes a todo from the list
     */
    fun removeTodo(todoId: String) {
        todos.value = todos.value.filter { it.id != todoId }
    }
    
    /**
     * Updates todo statistics
     */
    fun setTodoStats(stats: TodoStats) {
        todoStats.value = stats
    }
    
    /**
     * Sets loading state
     */
    fun setLoading(loading: Boolean) {
        isLoading.value = loading
    }
    
    /**
     * Shows error message
     */
    fun showError(message: String) {
        errorMessage.value = message
        successMessage.value = null
    }
    
    /**
     * Shows success message
     */
    fun showSuccess(message: String) {
        successMessage.value = message
        errorMessage.value = null
    }
    
    /**
     * Clears all messages
     */
    fun clearMessages() {
        errorMessage.value = null
        successMessage.value = null
    }
    
    /**
     * Gets filtered and sorted todos
     */
    fun getFilteredTodos(): List<Todo> {
        var filteredTodos = todos.value
        
        // Apply completion filter
        filterCompleted.value?.let { showCompleted ->
            filteredTodos = filteredTodos.filter { it.completed == showCompleted }
        }
        
        // Apply search filter
        if (searchQuery.value.isNotBlank()) {
            val query = searchQuery.value.lowercase()
            filteredTodos = filteredTodos.filter {
                it.title.lowercase().contains(query) || 
                it.description.lowercase().contains(query)
            }
        }
        
        // Apply sorting
        filteredTodos = when (sortBy.value) {
            TodoSortBy.PRIORITY -> filteredTodos.sortedWith(
                compareByDescending<Todo> { it.priority.ordinal }
                    .thenBy { it.completed }
                    .thenBy { it.createdAt }
            )
            TodoSortBy.DUE_DATE -> filteredTodos.sortedWith(
                compareBy<Todo> { it.dueDate ?: java.time.LocalDateTime.MAX }
                    .thenBy { it.completed }
            )
            TodoSortBy.CREATED_DATE -> filteredTodos.sortedWith(
                compareByDescending<Todo> { it.createdAt }
                    .thenBy { it.completed }
            )
            TodoSortBy.TITLE -> filteredTodos.sortedWith(
                compareBy<Todo> { it.title.lowercase() }
                    .thenBy { it.completed }
            )
        }
        
        return filteredTodos
    }
    
    private fun getLanguageName(code: String): String = when (code) {
        "en" -> "English"
        "es" -> "Español"
        "fr" -> "Français"
        "ar" -> "العربية"
        else -> "English"
    }
}

/**
 * Todo sorting options
 */
enum class TodoSortBy {
    PRIORITY,
    DUE_DATE,
    CREATED_DATE,
    TITLE
}