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
// import code.yousef.summon.i18n.rememberI18n  // Temporarily disabled
import code.yousef.example.springboot.*

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
    val isDarkTheme = theme == "dark"
    // val i18n = rememberI18n()  // Temporarily disabled
    
    // Temporarily disabled theming features
    
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
            .backgroundColor("#f0f2f5") // Simple background instead of gradient
    ) {
        // Header
        TodoHeader(username, language, isDarkTheme)
        
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
                    .backgroundColor("white")
                    .boxShadow("0 4px 6px rgba(0, 0, 0, 0.1)") // Simple shadow instead of glassmorphism
                    .borderRadius(16.px)
                    .style("overflow", "hidden")
                    .style("max-width", "800px")
                    .style("width", "100%")
            ) {
                Column(modifier = Modifier().padding(1.5.rem)) {
                    // Title
                    Text(
                        "Todo List",
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
                                placeholder = "What needs to be done?",
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
                            
                            Button(
                                onClick = {
                                    // This will be handled by JavaScript
                                },
                                label = if (isLoading.value) "Loading..." else "Add",
                                modifier = Modifier()
                                    .padding("0.75rem 1.5rem")
                                    .backgroundColor("#1976d2")
                                    .color("white")
                                    .borderRadius(4.px)
                                    .cursor("pointer")
                                    .hover { backgroundColor("#1565c0") }
                                    .attribute("type", "submit")
                            )
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
                            Button(
                                onClick = { filter.value = filterType },
                                label = when (filterType) {
                                    TodoFilter.ALL -> "All"
                                    TodoFilter.ACTIVE -> "Active"
                                    TodoFilter.COMPLETED -> "Completed"
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
                            )
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
                                        TodoFilter.ACTIVE -> "No active todos"
                                        TodoFilter.COMPLETED -> "No completed todos"
                                        else -> "No todos yet"
                                    }
                                )
                            }
                        } else {
                            filteredTodos.forEach { todo ->
                                TodoItem(
                                    todo = todo,
                                    isDarkTheme = isDarkTheme,
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
                            "$activeCount ${if (activeCount == 1) "item left" else "items left"}",
                            modifier = Modifier()
                                .color(if (isDarkTheme) "#999" else "#666")
                        )
                        
                        if (todos.value.any { it.completed }) {
                            Button(
                                onClick = {
                                    // This will be handled by JavaScript
                                },
                                label = "Clear completed",
                                modifier = Modifier()
                                    .padding("0.5rem 1rem")
                                    .backgroundColor("transparent")
                                    .color("#d32f2f")
                                    .border("1px", "solid", "#d32f2f")
                                    .borderRadius(4.px)
                                    .cursor("pointer")
                                    .hover { backgroundColor("#ffebee") }
                                    .attribute("id", "clearCompletedBtn")
                            )
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
    isDarkTheme: Boolean
) {
    Div(
        modifier = Modifier()
            .fillMaxWidth()
            .backgroundColor(if (isDarkTheme) "#2d2d2d" else "white")
            .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
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
                "Welcome, $username!",
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
                Button(
                    onClick = { /* This will be handled by JavaScript */ },
                    label = language.uppercase(),
                    modifier = Modifier()
                        .padding("0.5rem 1rem")
                        .backgroundColor("#e0e0e0")
                        .color("#666")
                        .borderRadius(4.px)
                        .cursor("pointer")
                        .attribute("id", "languageBtn")
                        .attribute("type", "button") // Explicitly set button type
                )
                
                // Theme toggle button
                Button(
                    onClick = { /* This will be handled by JavaScript */ },
                    label = if (isDarkTheme) "🌙" else "☀️",
                    modifier = Modifier()
                        .padding("0.5rem 1rem")
                        .backgroundColor("#e0e0e0")
                        .color("#666")
                        .borderRadius(4.px)
                        .cursor("pointer")
                        .attribute("id", "themeBtn")
                        .attribute("type", "button") // Explicitly set button type
                )
                
                // Logout button
                Button(
                    onClick = {
                        // This will be handled by JavaScript
                    },
                    label = "Logout",
                    modifier = Modifier()
                        .padding("0.5rem 1rem")
                        .backgroundColor("#d32f2f")
                        .color("white")
                        .borderRadius(4.px)
                        .cursor("pointer")
                        .hover { backgroundColor("#c62828") }
                        .attribute("id", "logoutBtn")
                        .attribute("type", "button") // Explicitly set button type
                )
            }
        }
    }
}

@Composable
fun TodoItem(
    todo: Todo,
    isDarkTheme: Boolean,
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
        
        Button(
            onClick = onDelete,
            label = "Delete",
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
        )
    }
}