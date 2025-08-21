package code.yousef.summon.examples.js.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.input.Select
import code.yousef.summon.runtime.SelectOption
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.models.AppTheme
import code.yousef.summon.examples.js.state.AppState
import code.yousef.summon.i18n.LayoutDirection
import code.yousef.summon.modifier.Modifier

/**
 * Header component with user info, theme toggle, language selector, and logout
 */
@Composable
fun Header(appState: AppState) {
    val currentLanguage = appState.language.value
    val currentTheme = appState.theme.value
    val currentUser = appState.currentUser.value
    
    Row(
        modifier = Modifier()
            .style("width", "100%")
            .style("padding", "16px 24px")
            .style("background-color", "var(--header-bg, #ffffff)")
            .style("border-bottom", "2px solid var(--border-color, #e2e8f0)")
            .style("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .style("align-items", "center")
            .style("gap", "16px")
            .style("flex-wrap", "wrap")
            .style("position", "sticky")
            .style("top", "0")
            .style("z-index", "100")
            .style("backdrop-filter", "blur(8px)")
            .style("direction", if (currentLanguage.direction == LayoutDirection.RTL) "rtl" else "ltr")
    ) {
        // App Title
        Text(
            text = Translations.getString("app.title", currentLanguage),
            modifier = Modifier()
                .style("font-size", "24px")
                .style("font-weight", "700")
                .style("color", "var(--primary-color, #4299e1)")
                .style("margin", "0")
                .style("white-space", "nowrap")
        )
        
        // User Welcome Message
        currentUser?.let { user ->
            Text(
                text = "👋 ${Translations.getString("auth.welcome.back", currentLanguage)}, ${user.displayName}!",
                modifier = Modifier()
                    .style("font-size", "14px")
                    .style("color", "var(--text-color, #4a5568)")
                    .style("font-weight", "500")
            )
        }
        
        // Spacer to push controls to the right
        Spacer(
            modifier = Modifier()
                .style("flex", "1")
                .style("min-width", "16px")
        )
        
        // Language Selector
        Select(
            selectedValue = remember { mutableStateOf(currentLanguage.code) },
            onSelectedChange = { languageCode ->
                val newLanguage = Translations.availableLanguages.find { it.code == languageCode }
                newLanguage?.let { appState.setLanguage(it) }
            },
            options = Translations.availableLanguages.map { language ->
                SelectOption(
                    value = language.code,
                    label = "${getLanguageFlag(language.code)} ${language.name}"
                )
            },
            modifier = Modifier()
                .style("padding", "6px 12px")
                .style("border", "1px solid var(--border-color, #e2e8f0)")
                .style("border-radius", "8px")
                .style("font-size", "14px")
                .style("background-color", "var(--input-bg, #ffffff)")
                .style("color", "var(--text-color)")
                .style("cursor", "pointer")
                .style("min-width", "120px")
                .style("transition", "border-color 0.2s")
                .attribute("title", Translations.getString("language.select", currentLanguage))
                .attribute("data-hover-styles", "border-color: var(--primary-color, #4299e1)")
        )
        
        // Theme Toggle Button
        Button(
            onClick = { appState.toggleTheme() },
            label = when (currentTheme) {
                AppTheme.LIGHT -> "🌙"
                AppTheme.DARK -> "☀️"
            },
            variant = ButtonVariant.SECONDARY,
            modifier = Modifier()
                .style("padding", "8px 12px")
                .style("border-radius", "8px")
                .style("font-size", "18px")
                .style("background-color", "var(--card-bg, #f7fafc)")
                .style("border", "1px solid var(--border-color, #e2e8f0)")
                .style("cursor", "pointer")
                .style("transition", "all 0.2s ease")
                .style("min-width", "44px")
                .style("text-align", "center")
                .attribute("title", Translations.getString("theme.toggle", currentLanguage))
                .attribute("data-hover-styles", "transform: scale(1.1); background-color: var(--primary-color, #4299e1); color: white; border-color: var(--primary-color, #4299e1)")
        )
        
        // Logout Button
        Button(
            onClick = { appState.logout() },
            label = Translations.getString("auth.logout", currentLanguage),
            variant = ButtonVariant.SECONDARY,
            modifier = Modifier()
                .style("padding", "8px 16px")
                .style("border-radius", "8px")
                .style("font-size", "14px")
                .style("font-weight", "500")
                .style("background-color", "#fed7d7")
                .style("color", "#c53030")
                .style("border", "1px solid #feb2b2")
                .style("cursor", "pointer")
                .style("transition", "all 0.2s ease")
                .style("white-space", "nowrap")
                .attribute("data-hover-styles", "background-color: #feb2b2; transform: translateY(-1px); box-shadow: 0 2px 8px rgba(197, 48, 48, 0.3)")
        )
    }
}

/**
 * Helper function to get flag emoji for language codes
 */
private fun getLanguageFlag(languageCode: String): String {
    return when (languageCode) {
        "en" -> "🇺🇸"
        "es" -> "🇪🇸"
        "fr" -> "🇫🇷"
        "ar" -> "🇸🇦"
        else -> "🌐"
    }
}