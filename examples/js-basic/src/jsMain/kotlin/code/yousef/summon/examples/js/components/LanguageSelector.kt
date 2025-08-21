package code.yousef.summon.examples.js.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.input.Select
import code.yousef.summon.runtime.SelectOption
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.models.Language
import code.yousef.summon.examples.js.state.appState
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.state.SummonMutableState

@Composable
fun LanguageSelector() {
    val selectedLanguageCode: SummonMutableState<String?> = mutableStateOf(appState.currentLanguage.value.code)
    val currentLanguage = appState.currentLanguage.value
    
    val options = Language.values().map { lang ->
        SelectOption(
            value = lang.code,
            label = "${getLanguageFlag(lang)} ${lang.displayName}"
        )
    }
    
    Select(
        selectedValue = selectedLanguageCode,
        onSelectedChange = { newValue ->
            val language = Language.values().find { it.code == newValue }
            if (language != null) {
                appState.setLanguage(language)
                selectedLanguageCode.value = language.code
            }
        },
        options = options,
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