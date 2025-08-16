package code.yousef.example.quarkus.ui.components

import code.yousef.example.quarkus.ui.i18n.AppTranslations
import code.yousef.example.quarkus.ui.state.AppState
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Row
import code.yousef.summon.i18n.Language
import code.yousef.summon.i18n.LayoutDirection
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable

/**
 * Language selector component for switching between languages
 */
@Composable
fun LanguageSelector() {
    val theme = AppState.currentTheme.value
    val currentLanguage = AppState.currentLanguage.value
    
    // Using type-safe modifiers instead of raw CSS strings
    Row(
        modifier = Modifier()
            .style("gap", theme.spacing.xs)
            .style("align-items", "center")
    ) {
        Text(
            text = "🌐",
            modifier = Modifier()
                .marginRight(theme.spacing.xs)
        )
        
        listOf(
            "en" to "EN",
            "es" to "ES", 
            "fr" to "FR",
            "ar" to "AR"
        ).forEach { (code, label) ->
            Button(
                onClick = {
                    val newLanguage = when (code) {
                        "en" -> Language("en", "English", LayoutDirection.LTR)
                        "es" -> Language("es", "Español", LayoutDirection.LTR)
                        "fr" -> Language("fr", "Français", LayoutDirection.LTR)
                        "ar" -> Language("ar", "العربية", LayoutDirection.RTL)
                        else -> Language("en", "English", LayoutDirection.LTR)
                    }
                    AppState.setLanguage(newLanguage)
                },
                label = label,
                modifier = Modifier()
                    .backgroundColor(if (currentLanguage.code == code) theme.colors.primary else theme.colors.cardBackground)
                    .color(if (currentLanguage.code == code) theme.colors.onPrimary else theme.colors.textPrimary)
                    .border("1px", BorderStyle.Solid.value, theme.colors.border)
                    .borderRadius("4px")
                    .padding("${theme.spacing.xs} ${theme.spacing.sm}")
                    .cursor(Cursor.Pointer)
                    .fontSize("12px")
                    .style("min-width", "30px")
            )
        }
    }
}

