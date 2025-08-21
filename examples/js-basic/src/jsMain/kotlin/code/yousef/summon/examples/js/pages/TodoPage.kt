package code.yousef.summon.examples.js.pages

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.ButtonVariant
import code.yousef.summon.components.layout.*
import code.yousef.summon.examples.js.components.*
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.state.appState
import code.yousef.summon.modifier.Modifier

@Composable
fun TodoPage() {
    val language = appState.currentLanguage.value
    val user = appState.currentUser.value
    val showingMessage = appState.showingMessage.value
    
    Column(
        modifier = Modifier()
            .style("min-height", "100vh")
            .style("background", "var(--background-color)")
            .style("transition", "all 0.3s ease")
    ) {
        // Header
        Header()
        
        // Message toast
        if (showingMessage != null) {
            MessageToast(showingMessage)
        }
        
        // Main content
        Column(
            modifier = Modifier()
                .style("flex", "1")
                .style("padding", "20px")
                .style("display", "flex")
                .style("align-items", "center")
                .style("justify-content", "flex-start")
        ) {
            // Welcome message
            if (user != null) {
                Text(
                    text = "${Translations.get("auth.welcome", language)}, ${user.username}! 👋",
                    modifier = Modifier()
                        .style("font-size", "20px")
                        .style("font-weight", "600")
                        .style("color", "var(--text-color)")
                        .style("margin-bottom", "24px")
                        .style("text-align", "center")
                )
            }
            
            // Todo list
            TodoList()
        }
    }
}

@Composable
private fun Header() {
    val language = appState.currentLanguage.value
    val user = appState.currentUser.value
    
    Row(
        modifier = Modifier()
            .style("background", "var(--card-background)")
            .style("padding", "16px 24px")
            .style("box-shadow", "0 2px 8px rgba(0, 0, 0, 0.1)")
            .style("justify-content", "space-between")
            .style("align-items", "center")
            .style("position", "sticky")
            .style("top", "0")
            .style("z-index", "100")
    ) {
        // Logo and title
        Row(
            modifier = Modifier()
                .style("align-items", "center")
                .style("gap", "12px")
        ) {
            Text(
                text = "📝",
                modifier = Modifier()
                    .style("font-size", "32px")
            )
            
            Column {
                Text(
                    text = Translations.get("app.title", language),
                    modifier = Modifier()
                        .style("font-size", "24px")
                        .style("font-weight", "700")
                        .style("color", "var(--text-color)")
                        .style("margin-bottom", "2px")
                )
                
                Text(
                    text = Translations.get("app.subtitle", language),
                    modifier = Modifier()
                        .style("font-size", "12px")
                        .style("color", "var(--muted-text-color)")
                )
            }
        }
        
        // Controls
        Row(
            modifier = Modifier()
                .style("align-items", "center")
                .style("gap", "16px")
        ) {
            // Language selector
            LanguageSelector()
            
            // Theme toggle
            ThemeToggle()
            
            // User info and logout
            if (user != null) {
                Row(
                    modifier = Modifier()
                        .style("align-items", "center")
                        .style("gap", "12px")
                        .style("margin-left", "16px")
                        .style("padding-left", "16px")
                        .style("border-left", "1px solid var(--border-color)")
                ) {
                    Column(
                        modifier = Modifier()
                            .style("text-align", "right")
                    ) {
                        Text(
                            text = user.username,
                            modifier = Modifier()
                                .style("font-weight", "600")
                                .style("color", "var(--text-color)")
                                .style("font-size", "14px")
                        )
                        
                        Text(
                            text = user.email,
                            modifier = Modifier()
                                .style("color", "var(--muted-text-color)")
                                .style("font-size", "12px")
                        )
                    }
                    
                    Button(
                        onClick = { appState.logout() },
                        label = Translations.get("auth.logout", language),
                        variant = ButtonVariant.SECONDARY,
                        modifier = Modifier()
                            .style("padding", "8px 16px")
                            .style("font-size", "14px")
                            .style("border-radius", "6px")
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageToast(messageKey: String) {
    val language = appState.currentLanguage.value
    
    Row(
        modifier = Modifier()
            .style("position", "fixed")
            .style("top", "20px")
            .style("right", "20px")
            .style("background", "var(--primary-color)")
            .style("color", "white")
            .style("padding", "12px 20px")
            .style("border-radius", "8px")
            .style("box-shadow", "0 4px 16px rgba(0, 0, 0, 0.2)")
            .style("z-index", "1000")
            .style("animation", "slideInFromRight 0.3s ease")
            .style("max-width", "300px")
    ) {
        Text(
            text = "✅",
            modifier = Modifier()
                .style("margin-right", "8px")
                .style("font-size", "16px")
        )
        
        Text(
            text = Translations.get(messageKey, language),
            modifier = Modifier()
                .style("font-weight", "500")
                .style("font-size", "14px")
        )
    }
}