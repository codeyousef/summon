package code.yousef.example.springboot.pages

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.style.*
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.FontWeight
// import code.yousef.summon.modifier.GradientPresets.modernPrimary  // Temporarily disabled
// import code.yousef.summon.modifier.glassMorphism  // Temporarily disabled
import code.yousef.summon.extensions.*
import code.yousef.summon.state.*
import code.yousef.summon.runtime.remember
import code.yousef.example.springboot.*
import code.yousef.example.springboot.i18n.Translations
import code.yousef.example.springboot.theme.AppTheme
// import code.yousef.summon.runtime.CallbackRegistry // Not available in JVM-only project
// import code.yousef.summon.runtime.SummonLogger // Not available in JVM-only project

data class Todo(
    val id: Long = 0,
    val text: String = "",
    val completed: Boolean = false,
    val userId: Long = 0
)

enum class TodoFilter {
    ALL, ACTIVE, COMPLETED
}

/**
 * Pure Summon todos page - no raw HTML/CSS/JS
 */
@Composable
fun TodosPage(
    username: String = "User",
    initialTodos: List<Todo> = emptyList(),
    language: String = "en",
    theme: String = "light",
    themeConfig: code.yousef.summon.components.core.EnhancedThemeConfig? = null
) {
    // Clear and initialize registries for this page render
    ActionRegistry.clear()
    
    // Generate a consistent session ID for this render
    val renderSession = System.currentTimeMillis()
    
    val isDarkTheme = theme == "dark"
    
    // State management
    val todos = remember { mutableStateOf(initialTodos.toMutableList()) }
    val newTodoText = remember { mutableStateOf("") }
    val filter = remember { mutableStateOf(TodoFilter.ALL) }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    
    // Computed values
    val filteredTodos = remember(todos.value, filter.value) {
        when (filter.value) {
            TodoFilter.ALL -> todos.value
            TodoFilter.ACTIVE -> todos.value.filter { !it.completed }
            TodoFilter.COMPLETED -> todos.value.filter { it.completed }
        }
    }
    
    val activeCount = remember(todos.value) {
        todos.value.count { !it.completed }
    }
    
    // Use ThemeProvider to provide theme context
    code.yousef.example.springboot.theme.AppThemeProvider(isDarkMode = isDarkTheme) {
        Column(
            modifier = Modifier()
                .fillMaxWidth()
                .minHeight(100.vh)
                .style("background-color", if (isDarkTheme) "#0f172a" else "#f5f5f5")
        ) {
            // Header
            TodoHeader(username, language, renderSession)
        
        // Main content
        Div(
            modifier = Modifier()
                .maxWidth(800.px)
                .margin("0 auto")
                .padding(2.rem)
                .fillMaxWidth()
        ) {
            // Error message
            errorMessage.value?.let { error ->
                ErrorAlert(error) { errorMessage.value = null }
            }
            
            // Todo card with glass morphism
            TodoCard {
                Column(modifier = Modifier().padding(1.5.rem)) {
                    // Title
                    TodoTitle(language)
                    
                    // Add todo form
                    Div(
                        modifier = Modifier()
                            .attribute("id", "addTodoForm")
                            .fillMaxWidth()
                            .marginBottom(1.rem)
                    ) {
                        Row(
                            modifier = Modifier()
                                .fillMaxWidth()
                                .gap(0.5.rem)
                        ) {
                            ThemedTextField(
                                value = newTodoText.value,
                                onValueChange = { newTodoText.value = it },
                                placeholder = Translations.get("todo.add_placeholder", language)
                            )
                            
                            run {
                                // Register the action and get the callback ID
                                val callbackId = "add-todo-${renderSession}"
                                // CallbackRegistry.registerCallback {
                                //     SummonLogger.log("Add todo callback executed")
                                // }
                                ActionRegistry.registerAction(callbackId, ActionType.AddTodo(""))
                                
                                PrimaryButton(
                                    onClick = { /* This callback ID will be used by hydration */ },
                                    label = if (isLoading.value) "Loading..." else Translations.get("todo.add_button", language),
                                    callbackId = callbackId
                                )
                            }
                        }
                    }
                    
                    // Filter buttons
                    Row(
                        modifier = Modifier()
                            .fillMaxWidth()
                            .marginBottom(1.rem)
                            .gap(0.5.rem)
                    ) {
                        TodoFilter.values().forEach { filterType ->
                            run {
                                // Register the action and get the callback ID
                                val callbackId = "filter-${filterType.name.lowercase()}-${renderSession}"
                                ActionRegistry.registerAction(callbackId, ActionType.SetFilter(filterType))
                                
                                FilterButton(
                                    onClick = { filter.value = filterType },
                                    label = when (filterType) {
                                        TodoFilter.ALL -> Translations.get("filter.all", language)
                                        TodoFilter.ACTIVE -> Translations.get("filter.active", language)
                                        TodoFilter.COMPLETED -> Translations.get("filter.completed", language)
                                    },
                                    isSelected = filter.value == filterType,
                                    callbackId = callbackId
                                )
                            }
                        }
                    }
                    
                    // Todo list
                    Column(
                        modifier = Modifier()
                            .fillMaxWidth()
                            .maxHeight(400.px)
                            .overflowY("auto")
                    ) {
                        if (filteredTodos.isEmpty()) {
                            EmptyState(
                                message = when (filter.value) {
                                    TodoFilter.ACTIVE -> Translations.get("message.no_active", language)
                                    TodoFilter.COMPLETED -> Translations.get("message.no_completed", language)
                                    else -> Translations.get("message.no_todos", language)
                                }
                            )
                        } else {
                            filteredTodos.forEach { todo ->
                                TodoItem(
                                    todo = todo,
                                    language = language,
                                    renderSession = renderSession,
                                    onToggle = {
                                        val index = todos.value.indexOf(todo)
                                        if (index != -1) {
                                            todos.value = todos.value.toMutableList().apply {
                                                this[index] = todo.copy(completed = !todo.completed)
                                            }
                                        }
                                    },
                                    onDelete = {
                                        todos.value = todos.value.toMutableList().apply { remove(todo) }
                                    }
                                )
                            }
                        }
                    }
                    
                    // Footer
                    TodoFooter(
                        activeCount = activeCount,
                        language = language,
                        hasCompletedTodos = todos.value.any { it.completed },
                        renderSession = renderSession
                    )
                }
            }
        }
        }
    }
}

@Composable
fun TodoHeader(
    username: String,
    language: String,
    renderSession: Long
) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Div(
        modifier = Modifier()
            .fillMaxWidth()
            .style("background-color", theme.backgroundColor)
            .style("box-shadow", if (theme.isDarkMode == true) "0 2px 4px rgba(0,0,0,0.3)" else "0 2px 4px rgba(0,0,0,0.1)")
            .padding(1.rem)
    ) {
        Row(
            modifier = Modifier()
                .maxWidth(1200.px)
                .margin("0 auto")
                .justifyContent("space-between")
                .alignItems("center")
        ) {
            Text(
                "${Translations.get("auth.welcome", language)}, $username!",
                modifier = Modifier()
                    .fontSize(1.25.rem)
                    .fontWeight(FontWeight.Medium)
                    .style("color", theme.textColor)
                    .attribute("id", "username")
            )
            
            Row(
                modifier = Modifier()
                    .gap(1.rem)
                    .alignItems("center")
            ) {
                // Language selector
                LanguageButton(language, renderSession)
                
                // Theme toggle button
                ThemeToggleButton(theme.isDarkMode == true, renderSession)
                
                // Logout button
                LogoutButton(language, renderSession)
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    language: String,
    renderSession: Long,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Row(
        modifier = Modifier()
            .fillMaxWidth()
            .padding(0.75.rem)
            .style("border-bottom", "1px solid ${theme.borderColor}")
            .alignItems("center")
            .opacity(if (todo.completed) 0.6 else 1.0)
    ) {
        Checkbox(
            checked = todo.completed,
            onCheckedChange = { onToggle() },
            modifier = Modifier()
                .marginRight(1.rem)
                .attribute("class", "todo-toggle")
                .attribute("data-todo-id", todo.id.toString())
        )
        
        Text(
            todo.text,
            modifier = Modifier()
                .flex(1)
                .style("color", theme.textColor)
                .textDecoration(if (todo.completed) "line-through" else "none")
        )
        
        DeleteTodoButton(
            todoId = todo.id,
            language = language,
            renderSession = renderSession,
            onDelete = onDelete
        )
    }
}

// Themed Components using Summon's useTheme hook

@Composable
fun TodoCard(content: @Composable () -> Unit) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Div(
        modifier = Modifier()
            .fillMaxWidth()
            .style("background-color", if (theme.isDarkMode == true) "rgba(30, 41, 59, 0.9)" else "rgba(255, 255, 255, 0.9)")
            .style("box-shadow", if (theme.isDarkMode == true) "0 4px 6px rgba(0, 0, 0, 0.3)" else "0 4px 6px rgba(0, 0, 0, 0.1)")
            .borderRadius(16.px)
            .style("overflow", "hidden")
            .style("max-width", "800px")
            .style("width", "100%")
    ) {
        content()
    }
}

@Composable
fun TodoTitle(language: String) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Text(
        Translations.get("app.title", language),
        modifier = Modifier()
            .fontSize(2.rem)
            .fontWeight(FontWeight.Bold)
            .style("color", theme.textColor)
            .marginBottom(1.rem)
    )
}

@Composable
fun ThemedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = Modifier()
            .flex(1)
            .padding(0.75.rem)
            .fontSize(1.rem)
            .borderRadius(4.px)
            .style("border", "1px solid ${theme.borderColor}")
            .style("background-color", theme.backgroundColor)
            .style("color", theme.textColor)
            .attribute("id", "newTodoInput")
            .attribute("name", "todoText")
    )
}

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    label: String,
    callbackId: String
) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Button(
        onClick = onClick,
        label = label,
        modifier = Modifier()
            .padding("0.75rem 1.5rem")
            .style("background-color", theme.primaryColor)
            .color("white")
            .borderRadius(4.px)
            .cursor("pointer")
            .hover { 
                style("background-color", if (theme.isDarkMode == true) "#3b82f6" else "#1565c0")
            }
            .attribute("data-onclick-id", callbackId)
            .attribute("data-onclick-action", "true")
    )
}

@Composable
fun FilterButton(
    onClick: () -> Unit,
    label: String,
    isSelected: Boolean,
    callbackId: String
) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Button(
        onClick = onClick,
        label = label,
        modifier = Modifier()
            .padding("0.5rem 1rem")
            .style(
                "background-color",
                if (isSelected) theme.primaryColor else theme.backgroundColor
            )
            .style(
                "color",
                if (isSelected) "white" else theme.secondaryColor
            )
            .borderRadius(4.px)
            .cursor("pointer")
            .attribute("data-onclick-id", callbackId)
            .attribute("data-onclick-action", "true")
    )
}

@Composable
fun EmptyState(message: String) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Div(
        modifier = Modifier()
            .padding(2.rem)
            .textAlign("center")
            .style("color", theme.secondaryColor)
    ) {
        Text(message)
    }
}

@Composable
fun TodoFooter(
    activeCount: Int,
    language: String,
    hasCompletedTodos: Boolean,
    renderSession: Long
) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    
    Row(
        modifier = Modifier()
            .fillMaxWidth()
            .marginTop(1.rem)
            .padding(1.rem)
            .style("border-top", "1px solid ${theme.borderColor}")
            .justifyContent("space-between")
            .alignItems("center")
    ) {
        Text(
            "$activeCount ${if (activeCount == 1) Translations.get("todo.item_left", language) else Translations.get("todo.items_left", language)}",
            modifier = Modifier()
                .style("color", theme.secondaryColor)
        )
        
        if (hasCompletedTodos) {
            ClearCompletedButton(language, renderSession)
        }
    }
}

@Composable
fun ClearCompletedButton(language: String, renderSession: Long) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    val callbackId = "clear-completed-${renderSession}"
    ActionRegistry.registerAction(callbackId, ActionType.ClearCompleted)
    
    Button(
        onClick = { /* Handled by JavaScript */ },
        label = Translations.get("todo.clear_completed", language),
        modifier = Modifier()
            .padding("0.5rem 1rem")
            .backgroundColor("transparent")
            .color("#d32f2f")
            .border("1px", "solid", "#d32f2f")
            .borderRadius(4.px)
            .cursor("pointer")
            .hover { backgroundColor("#ffebee") }
            .attribute("id", "clearCompletedBtn")
            .attribute("data-onclick-id", callbackId)
            .attribute("data-onclick-action", "true")
    )
}

@Composable
fun LanguageButton(language: String, renderSession: Long) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    val callbackId = "language-toggle-${renderSession}"
    ActionRegistry.registerAction(callbackId, ActionType.ToggleLanguage)
    
    Button(
        onClick = { /* Handled by hydration */ },
        label = when (language) {
            "es" -> "ES"
            "fr" -> "FR"
            else -> "EN"
        },
        modifier = Modifier()
            .padding("0.5rem 1rem")
            .style("background-color", theme.backgroundColor)
            .style("color", theme.secondaryColor)
            .borderRadius(4.px)
            .cursor("pointer")
            .attribute("data-onclick-id", callbackId)
            .attribute("data-onclick-action", "true")
    )
}

@Composable
fun ThemeToggleButton(isDarkTheme: Boolean, renderSession: Long) {
    val theme = code.yousef.example.springboot.theme.useAppTheme()
    val callbackId = "theme-toggle-${renderSession}"
    ActionRegistry.registerAction(callbackId, ActionType.ToggleTheme)
    
    Button(
        onClick = { /* Handled by hydration */ },
        label = if (isDarkTheme) "🌙" else "☀️",
        modifier = Modifier()
            .padding("0.5rem 1rem")
            .style("background-color", theme.backgroundColor)
            .style("color", theme.secondaryColor)
            .borderRadius(4.px)
            .cursor("pointer")
            .attribute("data-onclick-id", callbackId)
            .attribute("data-onclick-action", "true")
    )
}

@Composable
fun LogoutButton(language: String, renderSession: Long) {
    val callbackId = "logout-${renderSession}"
    ActionRegistry.registerAction(callbackId, ActionType.Logout)
    
    Button(
        onClick = { /* Handled by hydration */ },
        label = Translations.get("auth.logout", language),
        modifier = Modifier()
            .padding("0.5rem 1rem")
            .backgroundColor("#d32f2f")
            .color("white")
            .borderRadius(4.px)
            .cursor("pointer")
            .hover { backgroundColor("#c62828") }
            .attribute("data-onclick-id", callbackId)
            .attribute("data-onclick-action", "true")
    )
}

@Composable
fun DeleteTodoButton(
    todoId: Long,
    language: String,
    renderSession: Long,
    onDelete: () -> Unit
) {
    val callbackId = "delete-todo-${todoId}-${renderSession}"
    ActionRegistry.registerAction(callbackId, ActionType.DeleteTodo(todoId))
    
    Button(
        onClick = onDelete,
        label = Translations.get("todo.delete", language),
        modifier = Modifier()
            .padding("0.25rem 0.5rem")
            .backgroundColor("#d32f2f")
            .color("white")
            .borderRadius(4.px)
            .fontSize(0.875.rem)
            .cursor("pointer")
            .hover { backgroundColor("#c62828") }
            .attribute("class", "todo-delete")
            .attribute("data-todo-id", todoId.toString())
            .attribute("data-onclick-id", callbackId)
            .attribute("data-onclick-action", "true")
    )
}

@Composable
fun ErrorAlert(message: String, onDismiss: () -> Unit) {
    Div(
        modifier = Modifier()
            .fillMaxWidth()
            .padding(1.rem)
            .backgroundColor("#f8d7da")
            .color("#721c24")
            .borderRadius(4.px)
            .border("1px", "solid", "#f5c6cb")
            .cursor("pointer")
            .onClick { onDismiss() }
    ) {
        Text(message)
    }
}
