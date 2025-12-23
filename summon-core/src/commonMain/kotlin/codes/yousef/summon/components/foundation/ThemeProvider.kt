package codes.yousef.summon.components.foundation

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.styles.GlobalStyle
import codes.yousef.summon.runtime.CompositionLocal
import codes.yousef.summon.theme.Theme
import codes.yousef.summon.modifier.*

/**
 * Enhanced theme configuration that extends the base theme with additional design tokens.
 */
data class EnhancedThemeConfig(
    val primaryColor: String = "#007bff",
    val secondaryColor: String = "#6c757d",
    val backgroundColor: String = "#ffffff",
    val textColor: String = "#212529",
    val borderColor: String = "#dee2e6",
    val isDarkMode: Boolean? = null,
    val designTokens: Map<String, String>? = null,
    val breakpoints: Map<String, String>? = null,
    val typography: Map<String, String>? = null
)

/**
 * CompositionLocal for providing theme configuration down the component tree.
 */
val LocalTheme = CompositionLocal.compositionLocalOf(EnhancedThemeConfig())

/**
 * Provides theme configuration to child components and optionally injects CSS variables.
 *
 * @param theme The theme configuration to provide
 * @param content The content to render with the theme
 */
@Composable
fun ThemeProvider(
    theme: EnhancedThemeConfig = EnhancedThemeConfig(),
    content: @Composable () -> Unit
) {
    // Inject CSS variables if design tokens are provided
    theme.designTokens?.let { tokens ->
        val cssVariables = ":root { ${tokens.map { (key, value) -> "$key: $value;" }.joinToString(" ")} }"
        GlobalStyle(cssVariables)
    }

    // Set the theme in the existing Theme system for backward compatibility
    val legacyTheme = Theme.ThemeConfig()
    Theme.setTheme(legacyTheme)

    // Provide the enhanced theme through CompositionLocal
    val provider = LocalTheme.provides(theme)
    provider.current // Access current to set the value
    content()
}

/**
 * Hook to access the current theme configuration.
 *
 * @return The current theme configuration
 */
@Composable
fun useTheme(): EnhancedThemeConfig = LocalTheme.current

/**
 * Convenience extension functions for theme-aware modifiers.
 * These should be called from within a Composable context with a theme provider.
 */
@Composable
fun codes.yousef.summon.modifier.Modifier.themeColor(colorName: String): codes.yousef.summon.modifier.Modifier {
    val theme = useTheme()
    return when (colorName) {
        "primary" -> this.style("color", theme.primaryColor)
        "secondary" -> this.style("color", theme.secondaryColor)
        "background" -> this.style("background-color", theme.backgroundColor)
        "text" -> this.style("color", theme.textColor)
        "border" -> this.style("border-color", theme.borderColor)
        else -> this.style("color", "var(--color-$colorName)")
    }
}

@Composable
fun codes.yousef.summon.modifier.Modifier.themeBorder(): codes.yousef.summon.modifier.Modifier {
    val theme = useTheme()
    return this.style("border", "1px solid ${theme.borderColor}")
}

@Composable
fun codes.yousef.summon.modifier.Modifier.themeSpacing(size: String): codes.yousef.summon.modifier.Modifier {
    return this.style("padding", "var(--spacing-$size)")
}
