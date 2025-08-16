package code.yousef.summon.examples.js.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.Select
import code.yousef.summon.components.input.SelectOption
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.models.Language
import code.yousef.summon.examples.js.state.appState
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.mutableStateOf

@Composable
fun LanguageSelector() {
    val selectedLanguage = mutableStateOf(appState.currentLanguage.value)
    val currentLanguage = appState.currentLanguage.value
    
    val options = Language.values().map { lang ->
        SelectOption(
            value = lang.code,
            label = "${getLanguageFlag(lang)} ${lang.name}"
        )
    }
    
    Select(
        selectedValue = selectedLanguage,
        options = options,
        onSelectedChange = { newValue ->
            val language = Language.values().find { it.code == newValue }
            if (language != null) {
                appState.setLanguage(language)
                selectedLanguage.value = language
            }
        },
        modifier = Modifier()
            .style("min-width", "140px")
            .style("border-radius", "8px")
            .style("border", "1px solid var(--border-color)")
            .style("background", "var(--background-color)")
            .style("color", "var(--text-color)")
            .style("padding", "8px 12px")
            .style("font-size", "14px")
            .style("transition", "all 0.2s ease")
    )
}

private fun getLanguageFlag(language: Language): String {
    return when (language) {
        Language.ENGLISH -> "🇺🇸"
        Language.SPANISH -> "🇪🇸"
        Language.FRENCH -> "🇫🇷"
    }
}