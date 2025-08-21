package code.yousef.summon.examples.js.state

import code.yousef.summon.examples.js.models.*
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf
import kotlinx.browser.localStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set

class AppState {
    // Theme state
    val currentTheme: SummonMutableState<Theme> = mutableStateOf(loadTheme())
    
    // Language state
    val currentLanguage: SummonMutableState<Language> = mutableStateOf(loadLanguage())
    
    // Auth state
    val currentUser: SummonMutableState<User?> = mutableStateOf(loadUser())
    val isAuthenticated: SummonMutableState<Boolean> = mutableStateOf(loadUser() != null)
    
    // Todo state
    val todos: SummonMutableState<List<Todo>> = mutableStateOf(loadTodos())
    val currentFilter: SummonMutableState<TodoFilter> = mutableStateOf(TodoFilter.ALL)
    val newTodoText: SummonMutableState<String> = mutableStateOf("")
    val editingTodoId: SummonMutableState<String?> = mutableStateOf(null)
    val editingText: SummonMutableState<String> = mutableStateOf("")
    
    // UI state
    val showingMessage: SummonMutableState<String?> = mutableStateOf(null)
    
    // Computed properties
    val filteredTodos: List<Todo>
        get() = when (currentFilter.value) {
            TodoFilter.ALL -> todos.value
            TodoFilter.ACTIVE -> todos.value.filter { !it.completed }
            TodoFilter.COMPLETED -> todos.value.filter { it.completed }
        }
    
    val activeTodoCount: Int
        get() = todos.value.count { !it.completed }
    
    val completedTodoCount: Int
        get() = todos.value.count { it.completed }
    
    // Theme methods
    fun toggleTheme() {
        val newTheme = if (currentTheme.value == Theme.LIGHT) Theme.DARK else Theme.LIGHT
        currentTheme.value = newTheme
        saveTheme(newTheme)
    }
    
    private fun loadTheme(): Theme {
        return localStorage["theme"]?.let { Theme.valueOf(it.uppercase()) } ?: Theme.LIGHT
    }
    
    private fun saveTheme(theme: Theme) {
        localStorage["theme"] = theme.name
    }
    
    // Language methods
    fun setLanguage(language: Language) {
        currentLanguage.value = language
        saveLanguage(language)
    }
    
    private fun loadLanguage(): Language {
        return localStorage["language"]?.let { 
            Language.values().find { lang -> lang.code == it }
        } ?: Language.ENGLISH
    }
    
    private fun saveLanguage(language: Language) {
        localStorage["language"] = language.code
    }
    
    // Auth methods
    fun login(username: String, email: String) {
        val user = User(
            id = generateId(),
            username = username,
            email = email
        )
        currentUser.value = user
        isAuthenticated.value = true
        saveUser(user)
        showMessage("message.login_success")
    }
    
    fun logout() {
        currentUser.value = null
        isAuthenticated.value = false
        localStorage.removeItem("user")
        showMessage("message.logout_success")
    }
    
    private fun loadUser(): User? {
        return localStorage["user"]?.let { 
            try {
                Json.decodeFromString<User>(it)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun saveUser(user: User) {
        localStorage["user"] = Json.encodeToString(user)
    }
    
    // Todo methods
    fun addTodo() {
        if (newTodoText.value.isNotBlank()) {
            val todo = Todo(
                id = generateId(),
                text = newTodoText.value.trim()
            )
            todos.value = todos.value + todo
            newTodoText.value = ""
            saveTodos()
        }
    }
    
    fun toggleTodo(todoId: String) {
        todos.value = todos.value.map { todo ->
            if (todo.id == todoId) {
                todo.copy(completed = !todo.completed)
            } else {
                todo
            }
        }
        saveTodos()
    }
    
    fun deleteTodo(todoId: String) {
        todos.value = todos.value.filter { it.id != todoId }
        saveTodos()
    }
    
    fun startEditingTodo(todoId: String) {
        val todo = todos.value.find { it.id == todoId }
        if (todo != null) {
            editingTodoId.value = todoId
            editingText.value = todo.text
        }
    }
    
    fun saveEditingTodo() {
        val editingId = editingTodoId.value
        if (editingId != null && editingText.value.isNotBlank()) {
            todos.value = todos.value.map { todo ->
                if (todo.id == editingId) {
                    todo.copy(text = editingText.value.trim())
                } else {
                    todo
                }
            }
            editingTodoId.value = null
            editingText.value = ""
            saveTodos()
        }
    }
    
    fun cancelEditingTodo() {
        editingTodoId.value = null
        editingText.value = ""
    }
    
    fun clearCompleted() {
        todos.value = todos.value.filter { !it.completed }
        saveTodos()
    }
    
    fun setFilter(filter: TodoFilter) {
        currentFilter.value = filter
    }
    
    private fun loadTodos(): List<Todo> {
        return localStorage["todos"]?.let { 
            try {
                Json.decodeFromString<List<Todo>>(it)
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }
    
    private fun saveTodos() {
        localStorage["todos"] = Json.encodeToString(todos.value)
    }
    
    // Message methods
    fun showMessage(messageKey: String) {
        showingMessage.value = messageKey
        // Auto-hide message after 3 seconds
        kotlinx.browser.window.setTimeout({
            if (showingMessage.value == messageKey) {
                showingMessage.value = null
            }
        }, 3000)
    }
    
    private fun generateId(): String {
        return "${kotlin.js.Date.now().toLong()}-${kotlin.random.Random.nextInt(1000, 9999)}"
    }
}

// Global app state instance
val appState = AppState()