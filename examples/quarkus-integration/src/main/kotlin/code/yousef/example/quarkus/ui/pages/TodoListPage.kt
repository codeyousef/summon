package code.yousef.example.quarkus.ui.pages

import code.yousef.example.quarkus.model.Priority
import code.yousef.example.quarkus.model.Todo
import code.yousef.example.quarkus.ui.components.TodoItem
import code.yousef.example.quarkus.ui.i18n.AppTranslations
import code.yousef.example.quarkus.ui.state.AppState
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable
import code.yousef.summon.state.mutableStateOf
import java.time.LocalDateTime

/**
 * Todo list page component
 */
@Composable
fun TodoListPage() {
    val theme = AppState.currentTheme.value
    val currentLanguage = AppState.currentLanguage.value.code
    val filteredTodos = AppState.getFilteredTodos()
    
    // Add todo form state
    val newTodoTitle = mutableStateOf("")
    val newTodoDescription = mutableStateOf("")
    val newTodoPriority = mutableStateOf<String?>("MEDIUM")
    val showAddForm = mutableStateOf(false)
    
    Column(
        modifier = Modifier()
            .padding(theme.spacing.lg)
            .maxWidth("1200px")
            .margin("0", "auto")
            .style("gap", theme.spacing.lg)
    ) {
        // Statistics cards
        AppState.todoStats.value?.let { stats: code.yousef.example.quarkus.service.TodoStats ->
            Row(
                modifier = Modifier()
                    .style("gap", theme.spacing.md)
                    .marginBottom(theme.spacing.lg)
                    .flexWrap(FlexWrap.Wrap)
            ) {
                StatCard(
                    title = AppTranslations.getString("stats.total", currentLanguage),
                    value = stats.total.toString(),
                    color = theme.colors.primary
                )
                
                StatCard(
                    title = AppTranslations.getString("stats.completed", currentLanguage),
                    value = stats.completed.toString(),
                    color = "#4CAF50"
                )
                
                StatCard(
                    title = AppTranslations.getString("stats.pending", currentLanguage),
                    value = stats.pending.toString(),
                    color = "#FF9800"
                )
                
                StatCard(
                    title = AppTranslations.getString("stats.high.priority", currentLanguage),
                    value = stats.highPriority.toString(),
                    color = "#F44336"
                )
            }
        }
        
        // Add todo section
        Card(
            modifier = Modifier()
                .backgroundColor(theme.colors.cardBackground)
                .border("1px", BorderStyle.Solid.value, theme.colors.border)
                .borderRadius("12px")
                .padding(theme.spacing.lg)
        ) {
            Column(
                modifier = Modifier()
                    .style("gap", theme.spacing.md)
            ) {
                Row(
                    modifier = Modifier()
                        .style("justify-content", "space-between")
                        .style("align-items", "center")
                ) {
                    Text(
                        text = AppTranslations.getString("todo.add", currentLanguage),
                        modifier = Modifier()
                            .fontSize("20px")
                            .fontWeight(FontWeight.Bold.value)
                            .color(theme.colors.textPrimary)
                    )
                    
                    Button(
                        onClick = { showAddForm.value = !showAddForm.value },
                        label = if (showAddForm.value) "−" else "+",
                        modifier = Modifier()
                            .backgroundColor(theme.colors.primary)
                            .color(theme.colors.onPrimary)
                            .style("border", "none")
                            .borderRadius("50%")
                            .width("40px")
                            .height("40px")
                            .cursor(Cursor.Pointer)
                            .fontSize("20px")
                    )
                }
                
                if (showAddForm.value) {
                    Column(
                        modifier = Modifier()
                            .style("gap", theme.spacing.md)
                            .marginTop(theme.spacing.md)
                    ) {
                        TextField(
                            value = newTodoTitle.value,
                            onValueChange = { newTodoTitle.value = it },
                            label = AppTranslations.getString("todo.title", currentLanguage),
                            modifier = Modifier()
                                .fillMaxWidth()
                        )
                        
                        TextField(
                            value = newTodoDescription.value,
                            onValueChange = { newTodoDescription.value = it },
                            label = AppTranslations.getString("todo.description", currentLanguage),
                            modifier = Modifier()
                                .fillMaxWidth()
                        )
                        
                        Row(
                            modifier = Modifier()
                                .style("gap", theme.spacing.md)
                                .style("align-items", "center")
                        ) {
                            // Priority selector using buttons
                            Row(
                                modifier = Modifier()
                                    .style("gap", theme.spacing.xs)
                                    .style("align-items", "center")
                            ) {
                                Text(
                                    text = "Priority:",
                                    modifier = Modifier()
                                        .marginRight(theme.spacing.xs)
                                        .fontSize("14px")
                                        .color(theme.colors.textSecondary)
                                )
                                
                                listOf(
                                    "LOW" to AppTranslations.getString("priority.low", currentLanguage),
                                    "MEDIUM" to AppTranslations.getString("priority.medium", currentLanguage), 
                                    "HIGH" to AppTranslations.getString("priority.high", currentLanguage),
                                    "URGENT" to AppTranslations.getString("priority.urgent", currentLanguage)
                                ).forEach { (priority, label) ->
                                    Button(
                                        onClick = { newTodoPriority.value = priority },
                                        label = label,
                                        modifier = Modifier()
                                            .backgroundColor(if (newTodoPriority.value == priority) theme.colors.primary else theme.colors.cardBackground)
                                            .color(if (newTodoPriority.value == priority) theme.colors.onPrimary else theme.colors.textPrimary)
                                            .border("1px", BorderStyle.Solid.value, theme.colors.border)
                                            .borderRadius("4px")
                                            .padding("${theme.spacing.xs} ${theme.spacing.sm}")
                                            .cursor(Cursor.Pointer)
                                            .fontSize("12px")
                                    )
                                }
                            }
                            
                            Button(
                                onClick = {
                                    handleAddTodo(
                                        newTodoTitle.value,
                                        newTodoDescription.value,
                                        newTodoPriority.value ?: "MEDIUM"
                                    )
                                    newTodoTitle.value = ""
                                    newTodoDescription.value = ""
                                    newTodoPriority.value = "MEDIUM"
                                    showAddForm.value = false
                                },
                                label = AppTranslations.getString("todo.save", currentLanguage),
                                modifier = Modifier()
                                    .backgroundColor(theme.colors.secondary)
                                    .color(theme.colors.onSecondary)
                                    .style("border", "none")
                                    .borderRadius("6px")
                                    .padding("${theme.spacing.sm} ${theme.spacing.lg}")
                                    .cursor(Cursor.Pointer)
                            )
                        }
                    }
                }
            }
        }
        
        // Demo note for first time users
        if (filteredTodos.isNotEmpty() && filteredTodos.first().title.contains("Welcome")) {
            Card(
                modifier = Modifier()
                    .backgroundColor(theme.colors.surface)
                    .border("1px", BorderStyle.Solid.value, theme.colors.border)
                    .borderRadius("8px")
                    .padding(theme.spacing.md)
            ) {
                Text(
                    text = AppTranslations.getString("welcome.demo.note", currentLanguage),
                    modifier = Modifier()
                        .color(theme.colors.textSecondary)
                        .fontSize("14px")
                        .style("text-align", "center")
                )
            }
        }
        
        // Todos list
        Column(
            modifier = Modifier()
                .style("gap", theme.spacing.md)
        ) {
            if (filteredTodos.isEmpty()) {
                Card(
                    modifier = Modifier()
                        .backgroundColor(theme.colors.cardBackground)
                        .border("1px", BorderStyle.Solid.value, theme.colors.border)
                        .borderRadius("12px")
                        .padding(theme.spacing.xl)
                        .style("text-align", "center")
                ) {
                    Text(
                        text = "No todos found. Add your first todo above!",
                        modifier = Modifier()
                            .color(theme.colors.textSecondary)
                            .fontSize("16px")
                    )
                }
            } else {
                filteredTodos.forEach { todo ->
                    TodoItem(
                        todo = todo,
                        onToggle = { handleToggleTodo(todo.id) },
                        onDelete = { handleDeleteTodo(todo.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, color: String) {
    val theme = AppState.currentTheme.value
    
    Card(
        modifier = Modifier()
            .backgroundColor(theme.colors.cardBackground)
            .border("1px", BorderStyle.Solid.value, theme.colors.border)
            .borderRadius("8px")
            .padding(theme.spacing.md)
            .style("text-align", "center")
            .style("min-width", "120px")
            .style("flex", "1")
    ) {
        Column(
            modifier = Modifier()
                .style("gap", theme.spacing.xs)
        ) {
            Text(
                text = value,
                modifier = Modifier()
                    .fontSize("24px")
                    .fontWeight(FontWeight.Bold.value)
                    .color(color)
            )
            
            Text(
                text = title,
                modifier = Modifier()
                    .fontSize("12px")
                    .color(theme.colors.textSecondary)
                    .style("text-transform", "uppercase")
            )
        }
    }
}

private fun handleAddTodo(title: String, description: String, priority: String) {
    if (title.isBlank()) {
        AppState.showError("Title is required")
        return
    }
    
    val newTodo = Todo(
        title = title,
        description = description,
        priority = Priority.fromString(priority),
        userId = AppState.currentUser.value?.id ?: "demo-user-id",
        createdAt = LocalDateTime.now()
    )
    
    AppState.addTodo(newTodo)
    AppState.showSuccess("Todo added successfully!")
    
    // Update stats
    updateTodoStats()
}

private fun handleToggleTodo(todoId: String) {
    val todos = AppState.todos.value
    val updatedTodos = todos.map { todo ->
        if (todo.id == todoId) {
            todo.copy(completed = !todo.completed, updatedAt = LocalDateTime.now())
        } else {
            todo
        }
    }
    
    AppState.setTodos(updatedTodos)
    updateTodoStats()
}

private fun handleDeleteTodo(todoId: String) {
    AppState.removeTodo(todoId)
    AppState.showSuccess("Todo deleted successfully!")
    updateTodoStats()
}

private fun updateTodoStats() {
    val todos = AppState.todos.value
    val stats = code.yousef.example.quarkus.service.TodoStats(
        total = todos.size,
        completed = todos.count { it.completed },
        pending = todos.count { !it.completed },
        overdue = todos.count { 
            !it.completed && it.dueDate != null && it.dueDate.isBefore(LocalDateTime.now()) 
        },
        highPriority = todos.count { it.priority == Priority.HIGH || it.priority == Priority.URGENT }
    )
    AppState.setTodoStats(stats)
}

