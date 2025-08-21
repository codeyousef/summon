package code.yousef.summon.examples.js.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Row
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.state.AppState
import code.yousef.summon.modifier.Modifier

/**
 * Input component for adding new todos
 */
@Composable
fun TodoInput(appState: AppState) {
    val currentLanguage = appState.language.value
    
    Row(
        modifier = Modifier()
            .style("gap", "12px")
            .style("margin-bottom", "24px")
            .style("width", "100%")
    ) {
        TextField(
            value = appState.newTodoText.value,
            onValueChange = { newValue ->
                appState.newTodoText.value = newValue
                // Handle Enter key by checking if we should add the todo
                // Note: This is a simplified approach - proper key handling would need platform-specific implementation
            },
            placeholder = Translations.getString("todo.new.placeholder", currentLanguage),
            modifier = Modifier()
                .style("flex", "1")
                .style("padding", "12px 16px")
                .style("border", "2px solid #e2e8f0")
                .style("border-radius", "8px")
                .style("font-size", "16px")
                .style("transition", "border-color 0.2s")
                .style("background-color", "var(--input-bg, #ffffff)")
                .style("color", "var(--text-color)")
                .attribute("data-hover-styles", "border-color: var(--primary-color, #4299e1)")
        )
        
        Button(
            onClick = { 
                appState.addTodo(appState.newTodoText.value)
            },
            label = Translations.getString("todo.add", currentLanguage),
            variant = ButtonVariant.PRIMARY,
            modifier = Modifier()
                .style("padding", "12px 24px")
                .style("font-size", "16px")
                .style("font-weight", "600")
                .style("border-radius", "8px")
                .style("background-color", "var(--primary-color, #4299e1)")
                .style("color", "white")
                .style("border", "none")
                .style("cursor", "pointer")
                .style("transition", "transform 0.2s, background-color 0.2s")
                .style("white-space", "nowrap")
                .attribute("data-hover-styles", "transform: translateY(-1px); background-color: var(--primary-hover, #3182ce)")
        )
    }
}