package code.yousef.example.quarkus.ui.components

import code.yousef.example.quarkus.model.Priority
import code.yousef.example.quarkus.model.Todo
import code.yousef.example.quarkus.ui.i18n.AppTranslations
import code.yousef.example.quarkus.ui.state.AppState
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import java.time.format.DateTimeFormatter

/**
 * Todo item component
 */
@Composable
fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val theme = AppState.currentTheme.value
    val currentLanguage = AppState.currentLanguage.value.code
    
    Card(
        modifier = Modifier()
            .style("background-color", if (todo.completed) theme.colors.surface else theme.colors.cardBackground)
            .style("border", "1px solid ${theme.colors.border}")
            .style("border-radius", "12px")
            .style("padding", theme.spacing.lg)
            .style("opacity", if (todo.completed) "0.7" else "1.0")
    ) {
        Row(
            modifier = Modifier()
                .style("gap", theme.spacing.md)
                .style("align-items", "flex-start")
        ) {
            
            // Content
            Column(
                modifier = Modifier()
                    .style("flex", "1")
                    .style("gap", theme.spacing.sm)
            ) {
                // Title and priority
                Row(
                    modifier = Modifier()
                        .style("justify-content", "space-between")
                        .style("align-items", "flex-start")
                        .style("gap", theme.spacing.md)
                ) {
                    Text(
                        text = todo.title,
                        modifier = Modifier()
                            .style("font-size", "18px")
                            .style("font-weight", "600")
                            .style("color", theme.colors.textPrimary)
                            .style("text-decoration", if (todo.completed) "line-through" else "none")
                            .style("flex", "1")
                    )
                    
                    // Priority badge
                    PriorityBadge(todo.priority)
                }
                
                // Description
                if (todo.description.isNotBlank()) {
                    Text(
                        text = todo.description,
                        modifier = Modifier()
                            .style("font-size", "14px")
                            .style("color", theme.colors.textSecondary)
                            .style("line-height", "1.5")
                            .style("text-decoration", if (todo.completed) "line-through" else "none")
                    )
                }
                
                // Due date and metadata
                Row(
                    modifier = Modifier()
                        .style("justify-content", "space-between")
                        .style("align-items", "center")
                        .style("margin-top", theme.spacing.sm)
                ) {
                    Column(
                        modifier = Modifier()
                            .style("gap", theme.spacing.xs)
                    ) {
                        todo.dueDate?.let { dueDate ->
                            val isOverdue = !todo.completed && dueDate.isBefore(java.time.LocalDateTime.now())
                            Text(
                                text = "Due: ${dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))}",
                                modifier = Modifier()
                                    .style("font-size", "12px")
                                    .style("color", if (isOverdue) theme.colors.error else theme.colors.textSecondary)
                                    .style("font-weight", if (isOverdue) "600" else "normal")
                            )
                        }
                        
                        Text(
                            text = "Created: ${todo.createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                            modifier = Modifier()
                                .style("font-size", "11px")
                                .style("color", theme.colors.textHint)
                        )
                    }
                    
                    // Actions using Summon Button components with onClick
                    Row(
                        modifier = Modifier()
                            .style("gap", theme.spacing.sm)
                    ) {
                        Button(
                            onClick = onToggle,
                            label = if (todo.completed) {
                                AppTranslations.getString("todo.mark.incomplete", currentLanguage)
                            } else {
                                AppTranslations.getString("todo.mark.complete", currentLanguage)
                            },
                            modifier = Modifier()
                                .style("background-color", if (todo.completed) "#FF9800" else "#4CAF50")
                                .style("color", "white")
                                .style("border", "none")
                                .style("border-radius", "6px")
                                .style("padding", "${theme.spacing.xs} ${theme.spacing.sm}")
                                .style("font-size", "12px")
                                .style("cursor", "pointer")
                        )
                        
                        Button(
                            onClick = onDelete,
                            label = AppTranslations.getString("todo.delete", currentLanguage),
                            modifier = Modifier()
                                .style("background-color", theme.colors.error)
                                .style("color", theme.colors.onError)
                                .style("border", "none")
                                .style("border-radius", "6px")
                                .style("padding", "${theme.spacing.xs} ${theme.spacing.sm}")
                                .style("font-size", "12px")
                                .style("cursor", "pointer")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val theme = AppState.currentTheme.value
    val currentLanguage = AppState.currentLanguage.value.code
    
    val (color, bgColor) = when (priority) {
        Priority.LOW -> "white" to "#4CAF50"
        Priority.MEDIUM -> "white" to "#FF9800"
        Priority.HIGH -> "white" to "#F44336"
        Priority.URGENT -> "white" to "#9C27B0"
    }
    
    Text(
        text = AppTranslations.getString("priority.${priority.name.lowercase()}", currentLanguage),
        modifier = Modifier()
            .style("background-color", bgColor)
            .style("color", color)
            .style("padding", "${theme.spacing.xs} ${theme.spacing.sm}")
            .style("border-radius", "12px")
            .style("font-size", "11px")
            .style("font-weight", "600")
            .style("text-transform", "uppercase")
            .style("letter-spacing", "0.5px")
    )
}