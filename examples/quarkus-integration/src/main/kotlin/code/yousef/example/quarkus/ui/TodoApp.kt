package code.yousef.example.quarkus.ui

import code.yousef.example.quarkus.ui.components.ThemeToggle
import code.yousef.example.quarkus.ui.components.LanguageSelector
import code.yousef.example.quarkus.ui.i18n.AppTranslations
import code.yousef.example.quarkus.ui.pages.LoginPage
import code.yousef.example.quarkus.ui.pages.TodoListPage
import code.yousef.example.quarkus.ui.state.AppState
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable

/**
 * Main Todo Application component
 */
@Composable
fun TodoApp() {
    val isLoggedIn = AppState.isLoggedIn.value
    val theme = AppState.currentTheme.value
    val currentLanguage = AppState.currentLanguage.value.code
    
    Column(
        modifier = Modifier()
            .style("min-height", "100vh")
            .backgroundColor(theme.colors.background)
            .color(theme.colors.textPrimary)
            .style("font-family", "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif")
            .style("direction", if (AppState.currentLanguage.value.direction.name == "RTL") "rtl" else "ltr")
    ) {
        if (isLoggedIn) {
            // Header
            Row(
                modifier = Modifier()
                    .fillMaxWidth()
                    .backgroundColor(theme.colors.surface)
                    .style("border-bottom", "1px solid ${theme.colors.border}")
                    .padding("${theme.spacing.md} ${theme.spacing.lg}")
                    .style("justify-content", "space-between")
                    .style("align-items", "center")
            ) {
                Row(
                    modifier = Modifier()
                        .style("align-items", "center")
                        .style("gap", theme.spacing.md)
                ) {
                    Text(
                        text = AppTranslations.getString("welcome.title", currentLanguage),
                        modifier = Modifier()
                            .fontSize("24px")
                            .fontWeight(FontWeight.Bold.value)
                            .color(theme.colors.textPrimary)
                    )
                    
                    AppState.currentUser.value?.let { user ->
                        Text(
                            text = "- ${user.username}",
                            modifier = Modifier()
                                .fontSize("16px")
                                .color(theme.colors.textSecondary)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier()
                        .style("align-items", "center")
                        .style("gap", theme.spacing.md)
                ) {
                    ThemeToggle()
                    LanguageSelector()
                    
                    Button(
                        onClick = { 
                            AppState.clearUserSession()
                            AppState.showSuccess("Logged out successfully")
                        },
                        label = AppTranslations.getString("auth.logout", currentLanguage),
                        modifier = Modifier()
                            .backgroundColor(theme.colors.error)
                            .color(theme.colors.onError)
                            .style("border", "none")
                            .borderRadius("6px")
                            .padding("${theme.spacing.sm} ${theme.spacing.md}")
                            .cursor(Cursor.Pointer)
                    )
                }
            }
            
            // Main content
            TodoListPage()
        } else {
            LoginPage()
        }
        
        // Messages
        AppState.errorMessage.value?.let { message ->
            ErrorMessage(message)
        }
        
        AppState.successMessage.value?.let { message ->
            SuccessMessage(message)
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    val theme = AppState.currentTheme.value
    
    Row(
        modifier = Modifier()
            .style("position", "fixed")
            .style("top", "20px")
            .style("right", "20px")
            .backgroundColor(theme.colors.error)
            .color(theme.colors.onError)
            .padding("${theme.spacing.md} ${theme.spacing.lg}")
            .borderRadius("8px")
            .boxShadow("0 4px 12px rgba(0,0,0,0.2)")
            .zIndex(1000)
            .maxWidth("400px")
    ) {
        Text(
            text = message,
            modifier = Modifier()
                .fontSize("14px")
                .fontWeight("500")
        )
        
        Button(
            onClick = { AppState.clearMessages() },
            label = "×",
            modifier = Modifier()
                .style("background", "transparent")
                .style("border", "none")
                .color(theme.colors.onError)
                .cursor(Cursor.Pointer)
                .marginLeft(theme.spacing.md)
                .fontSize("18px")
                .padding("0")
        )
    }
}

@Composable
fun SuccessMessage(message: String) {
    val theme = AppState.currentTheme.value
    
    Row(
        modifier = Modifier()
            .style("position", "fixed")
            .style("top", "20px")
            .style("right", "20px")
            .backgroundColor(theme.colors.secondary)
            .color(theme.colors.onSecondary)
            .padding("${theme.spacing.md} ${theme.spacing.lg}")
            .borderRadius("8px")
            .boxShadow("0 4px 12px rgba(0,0,0,0.2)")
            .zIndex(1000)
            .maxWidth("400px")
    ) {
        Text(
            text = message,
            modifier = Modifier()
                .fontSize("14px")
                .fontWeight("500")
        )
        
        Button(
            onClick = { AppState.clearMessages() },
            label = "×",
            modifier = Modifier()
                .style("background", "transparent")
                .style("border", "none")
                .color(theme.colors.onSecondary)
                .cursor(Cursor.Pointer)
                .marginLeft(theme.spacing.md)
                .fontSize("18px")
                .padding("0")
        )
    }
}