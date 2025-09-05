package code.yousef.example.springboot.theme

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.EnhancedThemeConfig
import code.yousef.summon.components.core.ThemeProvider
import code.yousef.summon.components.core.useTheme

/**
 * App theme configuration using Summon's ThemeProvider system
 */
object AppTheme {
    
    /**
     * Light theme configuration
     */
    val lightTheme = EnhancedThemeConfig(
        primaryColor = "#1976d2",
        secondaryColor = "#424242",
        backgroundColor = "#f5f5f5",
        textColor = "#2d3748",
        borderColor = "#e2e8f0",
        isDarkMode = false,
        designTokens = mapOf(
            // Colors
            "--color-primary" to "#1976d2",
            "--color-primary-hover" to "#1565c0",
            "--color-secondary" to "#424242",
            "--color-background" to "#f5f5f5",
            "--color-surface" to "#ffffff",
            "--color-card-background" to "rgba(255, 255, 255, 0.9)",
            "--color-text-primary" to "#2d3748",
            "--color-text-secondary" to "#718096",
            "--color-border" to "#e2e8f0",
            
            // Gradients
            "--gradient-primary" to "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
            "--gradient-secondary" to "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
            
            // Shadows
            "--shadow-light" to "0 2px 4px rgba(0, 0, 0, 0.1)",
            "--shadow-medium" to "0 4px 6px rgba(0, 0, 0, 0.1)",
            "--shadow-strong" to "0 10px 25px rgba(0, 0, 0, 0.1)",
            
            // Glass effects
            "--glass-background" to "rgba(255, 255, 255, 0.25)",
            "--glass-border" to "rgba(255, 255, 255, 0.2)",
            
            // Spacing
            "--space-xs" to "0.25rem",
            "--space-sm" to "0.5rem",
            "--space-md" to "1rem",
            "--space-lg" to "1.5rem",
            "--space-xl" to "2rem",
            
            // Border radius
            "--radius-sm" to "4px",
            "--radius-md" to "8px",
            "--radius-lg" to "12px"
        )
    )
    
    /**
     * Dark theme configuration
     */
    val darkTheme = EnhancedThemeConfig(
        primaryColor = "#60a5fa",
        secondaryColor = "#cbd5e1",
        backgroundColor = "#0f172a",
        textColor = "#f1f5f9",
        borderColor = "#334155",
        isDarkMode = true,
        designTokens = mapOf(
            // Colors
            "--color-primary" to "#60a5fa",
            "--color-primary-hover" to "#3b82f6",
            "--color-secondary" to "#cbd5e1",
            "--color-background" to "#0f172a",
            "--color-surface" to "#1e293b",
            "--color-card-background" to "rgba(30, 41, 59, 0.9)",
            "--color-text-primary" to "#f1f5f9",
            "--color-text-secondary" to "#cbd5e1",
            "--color-border" to "#334155",
            
            // Gradients
            "--gradient-primary" to "linear-gradient(135deg, #4a5568 0%, #2d3748 100%)",
            "--gradient-secondary" to "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
            
            // Shadows
            "--shadow-light" to "0 2px 4px rgba(0, 0, 0, 0.3)",
            "--shadow-medium" to "0 4px 6px rgba(0, 0, 0, 0.3)",
            "--shadow-strong" to "0 10px 25px rgba(0, 0, 0, 0.4)",
            
            // Glass effects
            "--glass-background" to "rgba(30, 41, 59, 0.25)",
            "--glass-border" to "rgba(100, 116, 139, 0.2)",
            
            // Spacing (same as light theme)
            "--space-xs" to "0.25rem",
            "--space-sm" to "0.5rem",
            "--space-md" to "1rem",
            "--space-lg" to "1.5rem",
            "--space-xl" to "2rem",
            
            // Border radius (same as light theme)
            "--radius-sm" to "4px",
            "--radius-md" to "8px",
            "--radius-lg" to "12px"
        )
    )
    
    /**
     * Get theme based on isDarkMode flag
     */
    fun getTheme(isDarkMode: Boolean): EnhancedThemeConfig {
        return if (isDarkMode) darkTheme else lightTheme
    }
}

/**
 * Composable wrapper that provides theme context to the entire application
 */
@Composable
fun AppThemeProvider(
    isDarkMode: Boolean,
    content: @Composable () -> Unit
) {
    val theme = AppTheme.getTheme(isDarkMode)
    
    ThemeProvider(theme = theme) {
        content()
    }
}

/**
 * Hook to access current theme state in components
 */
@Composable
fun useAppTheme(): EnhancedThemeConfig {
    return useTheme()
}
