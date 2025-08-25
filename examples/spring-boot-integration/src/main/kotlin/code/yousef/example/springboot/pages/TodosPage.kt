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
    theme: String = "light"
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
    
    Column(
        modifier = Modifier()
            .fillMaxWidth()
            .minHeight(100.vh)
            .backgroundColor(if (isDarkTheme) "#121212" else "#f5f5f5")
    ) {
        // Header
        TodoHeader(username, language, isDarkTheme, renderSession)
        
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
                // Custom alert div
                Div(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .padding(1.rem)
                        .backgroundColor("#f8d7da")
                        .color("#721c24")
                        .borderRadius(4.px)
                        .border("1px", "solid", "#f5c6cb")
                        .onClick { errorMessage.value = null }
                ) {
                    Text(error)
                }
            }
            
            // Todo card with glass morphism
            Div(
                modifier = Modifier()
                    .fillMaxWidth()
                    .backgroundColor(if (isDarkTheme) "#2d2d2d" else "white")
                    .boxShadow(if (isDarkTheme) "0 4px 6px rgba(0, 0, 0, 0.3)" else "0 4px 6px rgba(0, 0, 0, 0.1)")
                    .borderRadius(16.px)
                    .style("overflow", "hidden")
                    .style("max-width", "800px")
                    .style("width", "100%")
            ) {
                Column(modifier = Modifier().padding(1.5.rem)) {
                    // Title
                    Text(
                        Translations.get("app.title", language),
                        modifier = Modifier()
                            .fontSize(2.rem)
                            .fontWeight(FontWeight.Bold)
                            .color(if (isDarkTheme) "white" else "#333")
                            .marginBottom(1.rem)
                    )
                    
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
                            TextField(
                                value = newTodoText.value,
                                onValueChange = { newTodoText.value = it },
                                placeholder = Translations.get("todo.add_placeholder", language),
                                modifier = Modifier()
                                    .flex(1)
                                    .padding(0.75.rem)
                                    .fontSize(1.rem)
                                    .borderRadius(4.px)
                                    .border("1px", "solid", if (isDarkTheme) "#444" else "#ddd")
                                    .backgroundColor(if (isDarkTheme) "#1a1a1a" else "white")
                                    .color(if (isDarkTheme) "white" else "#333")
                                    .attribute("id", "newTodoInput")
                                    .attribute("name", "todoText")
                            )
                            
                            run {
                                // Register the action and get the callback ID
                                val callbackId = "add-todo-${renderSession}"
                                // CallbackRegistry.registerCallback {
                                //     SummonLogger.log("Add todo callback executed")
                                // }
                                ActionRegistry.registerAction(callbackId, ActionType.AddTodo(""))
                                
                                Button(
                                    onClick = { /* This callback ID will be used by hydration */ },
                                    label = if (isLoading.value) "Loading..." else Translations.get("todo.add_button", language),
                                    modifier = Modifier()
                                        .padding("0.75rem 1.5rem")
                                        .backgroundColor("#1976d2")
                                        .color("white")
                                        .borderRadius(4.px)
                                        .cursor("pointer")
                                        .hover { backgroundColor("#1565c0") }
                                        .attribute("data-onclick-id", callbackId)
                                        .attribute("data-onclick-action", "true")
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
                                
                                Button(
                                    onClick = { filter.value = filterType },
                                    label = when (filterType) {
                                        TodoFilter.ALL -> Translations.get("filter.all", language)
                                        TodoFilter.ACTIVE -> Translations.get("filter.active", language)
                                        TodoFilter.COMPLETED -> Translations.get("filter.completed", language)
                                    },
                                    modifier = Modifier()
                                        .padding("0.5rem 1rem")
                                        .backgroundColor(
                                            if (filter.value == filterType) "#1976d2" else 
                                            if (isDarkTheme) "#444" else "#e0e0e0"
                                        )
                                        .color(
                                            if (filter.value == filterType) "white" else
                                            if (isDarkTheme) "#ccc" else "#666"
                                        )
                                        .borderRadius(4.px)
                                        .cursor("pointer")
                                        .attribute("data-onclick-id", callbackId)
                                        .attribute("data-onclick-action", "true")
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
                            Div(
                                modifier = Modifier()
                                    .padding(2.rem)
                                    .textAlign("center")
                                    .color(if (isDarkTheme) "#999" else "#666")
                            ) {
                                Text(
                                    when (filter.value) {
                                        TodoFilter.ACTIVE -> Translations.get("message.no_active", language)
                                        TodoFilter.COMPLETED -> Translations.get("message.no_completed", language)
                                        else -> Translations.get("message.no_todos", language)
                                    }
                                )
                            }
                        } else {
                            filteredTodos.forEach { todo ->
                                TodoItem(
                                    todo = todo,
                                    isDarkTheme = isDarkTheme,
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
                    Row(
                        modifier = Modifier()
                            .fillMaxWidth()
                            .marginTop(1.rem)
                            .padding(1.rem)
                            .borderTop("1px", "solid", if (isDarkTheme) "#444" else "#e0e0e0")
                            .justifyContent("space-between")
                            .alignItems("center")
                    ) {
                        Text(
                            "$activeCount ${if (activeCount == 1) Translations.get("todo.item_left", language) else Translations.get("todo.items_left", language)}",
                            modifier = Modifier()
                                .color(if (isDarkTheme) "#999" else "#666")
                        )
                        
                        if (todos.value.any { it.completed }) {
                            run {
                                // Register the action and get the callback ID
                                val callbackId = "clear-completed-${renderSession}"
                                ActionRegistry.registerAction(callbackId, ActionType.ClearCompleted)
                                
                                Button(
                                    onClick = {
                                        // This will be handled by JavaScript
                                    },
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
                        }
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
    isDarkTheme: Boolean,
    renderSession: Long
) {
    Div(
        modifier = Modifier()
            .fillMaxWidth()
            .backgroundColor(if (isDarkTheme) "#2d2d2d" else "white")
            .boxShadow(if (isDarkTheme) "0 2px 4px rgba(0,0,0,0.3)" else "0 2px 4px rgba(0,0,0,0.1)")
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
                    .color(if (isDarkTheme) "white" else "#333")
                    .attribute("id", "username")
            )
            
            Row(
                modifier = Modifier()
                    .gap(1.rem)
                    .alignItems("center")
            ) {
                // Language selector
                run {
                    // Register the action and get the callback ID
                    val callbackId = "language-toggle-${renderSession}"
                    // CallbackRegistry.registerCallback {
                    //     SummonLogger.log("Language toggle callback executed")
                    // }
                    ActionRegistry.registerAction(callbackId, ActionType.ToggleLanguage)
                    
                    Button(
                        onClick = { /* This callback ID will be used by hydration */ },
                        label = when (language) {
                            "es" -> "ES"
                            "fr" -> "FR"
                            else -> "EN"
                        },
                        modifier = Modifier()
                            .padding("0.5rem 1rem")
                            .backgroundColor(if (isDarkTheme) "#404040" else "#e0e0e0")
                            .color(if (isDarkTheme) "#ccc" else "#666")
                            .borderRadius(4.px)
                            .cursor("pointer")
                            .attribute("data-onclick-id", callbackId)
                            .attribute("data-onclick-action", "true")
                    )
                }
                
                // Theme toggle button
                run {
                    // Register the action and get the callback ID
                    val callbackId = "theme-toggle-${renderSession}"
                    // CallbackRegistry.registerCallback {
                    //     SummonLogger.log("Theme toggle callback executed")
                    // }
                    ActionRegistry.registerAction(callbackId, ActionType.ToggleTheme)
                    
                    Button(
                        onClick = { /* This callback ID will be used by hydration */ },
                        label = if (isDarkTheme) "🌙" else "☀️",
                        modifier = Modifier()
                            .padding("0.5rem 1rem")
                            .backgroundColor(if (isDarkTheme) "#404040" else "#e0e0e0")
                            .color(if (isDarkTheme) "#ccc" else "#666")
                            .borderRadius(4.px)
                            .cursor("pointer")
                            .attribute("data-onclick-id", callbackId)
                            .attribute("data-onclick-action", "true")
                    )
                }
                
                // Logout button
                run {
                    // Register the action and get the callback ID
                    val callbackId = "logout-${renderSession}"
                    // CallbackRegistry.registerCallback {
                    //     SummonLogger.log("Logout callback executed")
                    // }
                    ActionRegistry.registerAction(callbackId, ActionType.Logout)
                    
                    Button(
                        onClick = { /* This callback ID will be used by hydration */ },
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
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    isDarkTheme: Boolean,
    language: String,
    renderSession: Long,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier()
            .fillMaxWidth()
            .padding(0.75.rem)
            .borderBottom("1px", "solid", if (isDarkTheme) "#444" else "#e0e0e0")
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
                .color(if (isDarkTheme) "white" else "#333")
                .textDecoration(if (todo.completed) "line-through" else "none")
        )
        
        run {
            // Register the delete action and get the callback ID
            val callbackId = "delete-todo-${todo.id}-${renderSession}"
            ActionRegistry.registerAction(callbackId, ActionType.DeleteTodo(todo.id))
            
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
                    .attribute("data-todo-id", todo.id.toString())
                    .attribute("data-onclick-id", callbackId)
                    .attribute("data-onclick-action", "true")
            )
        }
    }
}