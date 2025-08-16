package code.yousef.summon.examples.js.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.models.TodoFilter
import code.yousef.summon.examples.js.state.appState
import code.yousef.summon.modifier.Modifier

@Composable
fun TodoList() {
    val language = appState.currentLanguage.value
    val filteredTodos = appState.filteredTodos
    val activeTodoCount = appState.activeTodoCount
    val completedTodoCount = appState.completedTodoCount
    
    Column(
        modifier = Modifier()
            .style("background", "var(--card-background)")
            .style("border-radius", "8px")
            .style("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.1)")
            .style("overflow", "hidden")
            .style("max-width", "800px")
            .style("width", "100%")
    ) {
        // Add todo section
        AddTodoSection()
        
        // Filter buttons
        if (appState.todos.value.isNotEmpty()) {
            FilterSection()
        }
        
        // Todo items or empty message
        if (filteredTodos.isEmpty()) {
            EmptyState()
        } else {
            Column {
                filteredTodos.forEach { todo ->
                    TodoItem(todo = todo)
                }
            }
        }
        
        // Footer with count and clear completed
        if (appState.todos.value.isNotEmpty()) {
            TodoFooter()
        }
    }
}

@Composable
private fun AddTodoSection() {
    val language = appState.currentLanguage.value
    
    Row(
        modifier = Modifier()
            .style("padding", "20px")
            .style("border-bottom", "1px solid var(--border-color)")
            .style("align-items", "center")
            .style("gap", "12px")
    ) {
        TextField(
            value = appState.newTodoText,
            onValueChange = { appState.newTodoText.value = it },
            placeholder = Translations.get("todo.add_placeholder", language),
            modifier = Modifier()
                .style("flex", "1")
                .style("padding", "12px 16px")
                .style("border", "2px solid var(--border-color)")
                .style("border-radius", "25px")
                .style("background", "var(--background-color)")
                .style("color", "var(--text-color)")
                .style("font-size", "16px")
                .style("outline", "none")
                .style("transition", "border-color 0.2s ease")
        )
        
        Button(
            text = Translations.get("todo.add_button", language),
            onClick = { appState.addTodo() },
            variant = ButtonVariant.PRIMARY,
            disabled = appState.newTodoText.value.isBlank(),
            modifier = Modifier()
                .style("padding", "12px 24px")
                .style("border-radius", "25px")
                .style("font-weight", "600")
                .style("font-size", "14px")
                .style("min-width", "100px")
        )
    }
}

@Composable
private fun FilterSection() {
    val language = appState.currentLanguage.value
    val currentFilter = appState.currentFilter.value
    
    Row(
        modifier = Modifier()
            .style("padding", "12px 20px")
            .style("border-bottom", "1px solid var(--border-color)")
            .style("justify-content", "center")
            .style("gap", "8px")
    ) {
        TodoFilter.values().forEach { filter ->
            val isActive = currentFilter == filter
            val filterText = when (filter) {
                TodoFilter.ALL -> Translations.get("filter.all", language)
                TodoFilter.ACTIVE -> Translations.get("filter.active", language)
                TodoFilter.COMPLETED -> Translations.get("filter.completed", language)
            }
            
            Button(
                text = filterText,
                onClick = { appState.setFilter(filter) },
                variant = if (isActive) ButtonVariant.PRIMARY else ButtonVariant.SECONDARY,
                modifier = Modifier()
                    .style("padding", "6px 16px")
                    .style("border-radius", "20px")
                    .style("font-size", "14px")
                    .style("min-width", "80px")
                    .style("opacity", if (isActive) "1" else "0.7")
                    .style("transform", if (isActive) "scale(1.05)" else "scale(1)")
                    .style("transition", "all 0.2s ease")
            )
        }
    }
}

@Composable
private fun EmptyState() {
    val language = appState.currentLanguage.value
    val currentFilter = appState.currentFilter.value
    
    val message = when (currentFilter) {
        TodoFilter.ALL -> Translations.get("message.no_todos", language)
        TodoFilter.ACTIVE -> Translations.get("message.no_active", language)
        TodoFilter.COMPLETED -> Translations.get("message.no_completed", language)
    }
    
    Column(
        modifier = Modifier()
            .style("padding", "60px 20px")
            .style("text-align", "center")
            .style("align-items", "center")
    ) {
        Text(
            text = "📝",
            modifier = Modifier()
                .style("font-size", "48px")
                .style("margin-bottom", "16px")
                .style("opacity", "0.5")
        )
        
        Text(
            text = message,
            modifier = Modifier()
                .style("color", "var(--muted-text-color)")
                .style("font-size", "16px")
                .style("line-height", "1.5")
        )
    }
}

@Composable
private fun TodoFooter() {
    val language = appState.currentLanguage.value
    val activeTodoCount = appState.activeTodoCount
    val completedTodoCount = appState.completedTodoCount
    
    Row(
        modifier = Modifier()
            .style("padding", "16px 20px")
            .style("justify-content", "space-between")
            .style("align-items", "center")
            .style("background", "var(--muted-background)")
            .style("border-top", "1px solid var(--border-color)")
    ) {
        // Todo count
        Text(
            text = if (activeTodoCount == 1) {
                "$activeTodoCount ${Translations.get("todo.item_left", language)}"
            } else {
                "$activeTodoCount ${Translations.get("todo.items_left", language)}"
            },
            modifier = Modifier()
                .style("color", "var(--muted-text-color)")
                .style("font-size", "14px")
        )
        
        // Clear completed button
        if (completedTodoCount > 0) {
            Button(
                text = Translations.get("todo.clear_completed", language),
                onClick = { appState.clearCompleted() },
                variant = ButtonVariant.SECONDARY,
                modifier = Modifier()
                    .style("padding", "6px 12px")
                    .style("font-size", "14px")
                    .style("color", "var(--danger-color)")
                    .style("border", "1px solid var(--danger-color)")
                    .style("background", "transparent")
                    .style("border-radius", "4px")
                    .style("opacity", "0.8")
                    .style("transition", "opacity 0.2s ease")
            )
        }
    }
}