package code.yousef.summon.examples.js.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.models.Theme
import code.yousef.summon.examples.js.state.appState
import code.yousef.summon.modifier.Modifier

@Composable
fun ThemeToggle() {
    val theme = appState.currentTheme.value
    val language = appState.currentLanguage.value
    
    Button(
        text = if (theme == Theme.LIGHT) "🌙 ${Translations.get("theme.dark", language)}" 
               else "☀️ ${Translations.get("theme.light", language)}",
        onClick = { appState.toggleTheme() },
        variant = ButtonVariant.SECONDARY,
        modifier = Modifier()
            .style("padding", "8px 16px")
            .style("border-radius", "20px")
            .style("font-size", "14px")
            .style("display", "flex")
            .style("align-items", "center")
            .style("gap", "8px")
            .style("transition", "all 0.2s ease")
            .style("min-width", "120px")
            .style("justify-content", "center")
    )
}