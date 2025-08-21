package code.yousef.summon.examples.js

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.ThemeProvider
import code.yousef.summon.components.core.EnhancedThemeConfig
import code.yousef.summon.components.style.GlobalStyle
import code.yousef.summon.examples.js.models.Theme
import code.yousef.summon.examples.js.pages.AuthPage
import code.yousef.summon.examples.js.pages.TodoPage
import code.yousef.summon.examples.js.state.appState

@Composable
fun TodoApp() {
    val isAuthenticated = appState.isAuthenticated.value
    val currentTheme = appState.currentTheme.value
    
    // Create theme configuration based on current theme
    val themeConfig = createThemeConfig(currentTheme)
    
    ThemeProvider(theme = themeConfig) {
        // Global styles
        GlobalStyleProvider()
        
        // Main app content
        if (isAuthenticated) {
            TodoPage()
        } else {
            AuthPage()
        }
    }
}

@Composable
private fun GlobalStyleProvider() {
    val currentTheme = appState.currentTheme.value
    
    GlobalStyle(
        css = buildString {
            appendLine(":root {")
            if (currentTheme == Theme.LIGHT) {
                appendLine("  --background-color: #ffffff;")
                appendLine("  --card-background: #ffffff;")
                appendLine("  --text-color: #1a1a1a;")
                appendLine("  --muted-text-color: #666666;")
                appendLine("  --border-color: #e1e5e9;")
                appendLine("  --primary-color: #007bff;")
                appendLine("  --secondary-color: #6c757d;")
                appendLine("  --success-color: #28a745;")
                appendLine("  --danger-color: #dc3545;")
                appendLine("  --warning-color: #ffc107;")
                appendLine("  --info-color: #17a2b8;")
                appendLine("  --muted-background: #f8f9fa;")
                appendLine("  --completed-background: #f8f9fa;")
            } else {
                appendLine("  --background-color: #1a1a1a;")
                appendLine("  --card-background: #2d2d2d;")
                appendLine("  --text-color: #ffffff;")
                appendLine("  --muted-text-color: #b0b0b0;")
                appendLine("  --border-color: #404040;")
                appendLine("  --primary-color: #4dabf7;")
                appendLine("  --secondary-color: #adb5bd;")
                appendLine("  --success-color: #51cf66;")
                appendLine("  --danger-color: #ff6b6b;")
                appendLine("  --warning-color: #ffd43b;")
                appendLine("  --info-color: #339af0;")
                appendLine("  --muted-background: #383838;")
                appendLine("  --completed-background: #383838;")
            }
            appendLine("}")
            
            appendLine()
            appendLine("* {")
            appendLine("  box-sizing: border-box;")
            appendLine("}")
            
            appendLine()
            appendLine("body {")
            appendLine("  margin: 0;")
            appendLine("  padding: 0;")
            appendLine("  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;")
            appendLine("  background-color: var(--background-color);")
            appendLine("  color: var(--text-color);")
            appendLine("  transition: background-color 0.3s ease, color 0.3s ease;")
            appendLine("}")
            
            appendLine()
            appendLine("@keyframes slideInFromRight {")
            appendLine("  from {")
            appendLine("    transform: translateX(100%);")
            appendLine("    opacity: 0;")
            appendLine("  }")
            appendLine("  to {")
            appendLine("    transform: translateX(0);")
            appendLine("    opacity: 1;")
            appendLine("  }")
            appendLine("}")
            
            appendLine()
            appendLine("input:focus, select:focus, textarea:focus, button:focus {")
            appendLine("  outline: 2px solid var(--primary-color);")
            appendLine("  outline-offset: 2px;")
            appendLine("}")
            
            appendLine()
            appendLine("button:hover {")
            appendLine("  transform: translateY(-1px);")
            appendLine("  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);")
            appendLine("}")
            
            appendLine()
            appendLine("input[type='checkbox'] {")
            appendLine("  width: 18px;")
            appendLine("  height: 18px;")
            appendLine("  accent-color: var(--primary-color);")
            appendLine("}")
        }
    )
}

private fun createThemeConfig(theme: Theme): EnhancedThemeConfig {
    return if (theme == Theme.LIGHT) {
        EnhancedThemeConfig(
            primaryColor = "#007bff",
            secondaryColor = "#6c757d",
            backgroundColor = "#ffffff",
            textColor = "#1a1a1a",
            borderColor = "#e1e5e9",
            isDarkMode = false
        )
    } else {
        EnhancedThemeConfig(
            primaryColor = "#4dabf7",
            secondaryColor = "#adb5bd",
            backgroundColor = "#1a1a1a",
            textColor = "#ffffff",
            borderColor = "#404040",
            isDarkMode = true
        )
    }
}