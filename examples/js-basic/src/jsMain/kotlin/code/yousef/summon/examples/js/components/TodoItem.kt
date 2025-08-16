package code.yousef.summon.examples.js.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.Row
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.models.Todo
import code.yousef.summon.examples.js.state.appState
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.mutableStateOf

@Composable
fun TodoItem(todo: Todo) {
    val language = appState.currentLanguage.value
    val isEditing = appState.editingTodoId.value == todo.id
    
    Row(
        modifier = Modifier()
            .style("align-items", "center")
            .style("padding", "12px 16px")
            .style("border-bottom", "1px solid var(--border-color)")
            .style("background", if (todo.completed) "var(--completed-background)" else "var(--card-background)")
            .style("transition", "all 0.2s ease")
            .style("min-height", "60px")
    ) {
        // Checkbox
        Checkbox(
            checked = mutableStateOf(todo.completed),
            onCheckedChange = { appState.toggleTodo(todo.id) },
            modifier = Modifier()
                .style("margin-right", "12px")
                .style("flex-shrink", "0")
        )
        
        if (isEditing) {
            // Edit mode
            TextField(
                value = appState.editingText,
                onValueChange = { appState.editingText.value = it },
                placeholder = todo.text,
                modifier = Modifier()
                    .style("flex", "1")
                    .style("margin-right", "12px")
                    .style("padding", "8px 12px")
                    .style("border", "1px solid var(--primary-color)")
                    .style("border-radius", "4px")
                    .style("background", "var(--background-color)")
                    .style("color", "var(--text-color)")
            )
            
            // Save button
            Button(
                text = Translations.get("todo.save", language),
                onClick = { appState.saveEditingTodo() },
                variant = ButtonVariant.PRIMARY,
                modifier = Modifier()
                    .style("margin-right", "8px")
                    .style("padding", "6px 12px")
                    .style("font-size", "12px")
            )
            
            // Cancel button
            Button(
                text = Translations.get("todo.cancel", language),
                onClick = { appState.cancelEditingTodo() },
                variant = ButtonVariant.SECONDARY,
                modifier = Modifier()
                    .style("padding", "6px 12px")
                    .style("font-size", "12px")
            )
        } else {
            // Display mode
            Text(
                text = todo.text,
                modifier = Modifier()
                    .style("flex", "1")
                    .style("margin-right", "12px")
                    .style("text-decoration", if (todo.completed) "line-through" else "none")
                    .style("color", if (todo.completed) "var(--muted-text-color)" else "var(--text-color)")
                    .style("opacity", if (todo.completed) "0.6" else "1")
                    .style("word-break", "break-word")
                    .style("line-height", "1.4")
            )
            
            // Edit button
            Button(
                text = "✏️",
                onClick = { appState.startEditingTodo(todo.id) },
                variant = ButtonVariant.SECONDARY,
                modifier = Modifier()
                    .style("margin-right", "8px")
                    .style("padding", "6px 8px")
                    .style("font-size", "14px")
                    .style("min-width", "32px")
                    .style("background", "transparent")
                    .style("border", "1px solid var(--border-color)")
                    .style("border-radius", "4px")
                    .style("opacity", "0.7")
                    .attribute("title", Translations.get("todo.edit", language))
            )
            
            // Delete button
            Button(
                text = "🗑️",
                onClick = { appState.deleteTodo(todo.id) },
                variant = ButtonVariant.DANGER,
                modifier = Modifier()
                    .style("padding", "6px 8px")
                    .style("font-size", "14px")
                    .style("min-width", "32px")
                    .style("background", "transparent")
                    .style("border", "1px solid var(--danger-color)")
                    .style("border-radius", "4px")
                    .style("color", "var(--danger-color)")
                    .style("opacity", "0.7")
                    .attribute("title", Translations.get("todo.delete", language))
            )
        }
    }
}