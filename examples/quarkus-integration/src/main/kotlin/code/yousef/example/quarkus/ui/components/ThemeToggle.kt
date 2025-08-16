package code.yousef.example.quarkus.ui.components

import code.yousef.example.quarkus.ui.i18n.AppTranslations
import code.yousef.example.quarkus.ui.state.AppState
import code.yousef.example.quarkus.ui.theme.AppThemes
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Switch
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Theme toggle component for switching between light and dark themes
 */
@Composable
fun ThemeToggle() {
    val theme = AppState.currentTheme.value
    val currentLanguage = AppState.currentLanguage.value.code
    val isDarkTheme = theme.name == "dark"
    
    Row(
        modifier = Modifier()
            .style("align-items", "center")
            .style("gap", theme.spacing.sm)
    ) {
        Text(
            text = if (isDarkTheme) "🌙" else "☀️",
            modifier = Modifier()
                .style("font-size", "16px")
        )
        
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { isChecked ->
                val newTheme = if (isChecked) AppThemes.darkTheme else AppThemes.lightTheme
                AppState.setTheme(newTheme)
                
                // In a real app, this would also update user preferences on the server
                // updateUserThemePreference(if (isChecked) "DARK" else "LIGHT")
            },
            modifier = Modifier()
                .style("cursor", "pointer")
        )
        
        Text(
            text = AppTranslations.getString("settings.theme", currentLanguage),
            modifier = Modifier()
                .style("font-size", "14px")
                .style("color", theme.colors.textSecondary)
        )
    }
}