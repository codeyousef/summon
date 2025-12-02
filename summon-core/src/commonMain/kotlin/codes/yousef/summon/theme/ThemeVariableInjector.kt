package codes.yousef.summon.theme

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.LaunchedEffect
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.SummonMutableState

/**
 * Manages automatic injection of theme CSS variables into the document root.
 *
 * This object provides the "Vibe" system functionality - instant theme switching
 * without requiring a tree re-render. Theme values are converted to CSS custom
 * properties and injected into `:root` (document.documentElement).
 *
 * ## Features
 *
 * - **Reactive Updates**: Uses LaunchedEffect to automatically update when theme state changes
 * - **Kebab-Case Conversion**: Theme properties are converted to CSS custom property names
 * - **Browser Integration**: Uses `document.documentElement.style.setProperty()` for injection
 *
 * ## Usage
 *
 * ```kotlin
 * // Create a theme state
 * val themeState = remember { mutableStateOf(Theme.Themes.light) }
 *
 * // Apply theme variables - will update whenever themeState changes
 * ApplyThemeVariables(themeState)
 *
 * // In your CSS/components, use the variables:
 * // color: var(--colors-primary-main);
 * // padding: var(--spacing-md);
 * ```
 *
 * @since 1.0.0
 */
expect object ThemeVariableInjector {
    /**
     * Injects all theme CSS variables into the document root.
     *
     * @param variables Map of CSS variable names (with -- prefix) to values
     */
    fun injectVariables(variables: Map<String, String>)
    
    /**
     * Gets a CSS variable value from the document root.
     *
     * @param name The CSS variable name (with -- prefix)
     * @return The current value, or empty string if not set
     */
    fun getVariable(name: String): String
    
    /**
     * Removes a CSS variable from the document root.
     *
     * @param name The CSS variable name (with -- prefix)
     */
    fun removeVariable(name: String)
    
    /**
     * Clears all theme CSS variables from the document root.
     * Only removes variables that start with common theme prefixes.
     */
    fun clearVariables()
}

/**
 * Common implementation for generating CSS variable map from Theme.ThemeConfig.
 */
object ThemeVariableGenerator {
    /**
     * Generates CSS variables from a ThemeConfig.
     *
     * @param config The theme configuration to convert
     * @return Map of CSS variable names to values
     */
    fun generateVariables(config: Theme.ThemeConfig): Map<String, String> {
        val variables = mutableMapOf<String, String>()
        
        // Colors from color palette
        val colorMode = ColorSystem.getThemeMode()
        val colors = config.colorPalette.forMode(colorMode)
        colors.forEach { (name, value) ->
            variables["--colors-$name"] = value
        }
        
        // Typography
        val typography = config.typographyTheme
        addTypographyVariables(variables, "h1", typography.h1)
        addTypographyVariables(variables, "h2", typography.h2)
        addTypographyVariables(variables, "h3", typography.h3)
        addTypographyVariables(variables, "h4", typography.h4)
        addTypographyVariables(variables, "h5", typography.h5)
        addTypographyVariables(variables, "h6", typography.h6)
        addTypographyVariables(variables, "body", typography.body)
        addTypographyVariables(variables, "body-large", typography.bodyLarge)
        addTypographyVariables(variables, "body-small", typography.bodySmall)
        addTypographyVariables(variables, "caption", typography.caption)
        addTypographyVariables(variables, "button", typography.button)
        addTypographyVariables(variables, "link", typography.link)
        addTypographyVariables(variables, "code", typography.code)
        
        // Spacing
        val spacing = config.spacingTheme
        variables["--spacing-xs"] = spacing.xs
        variables["--spacing-sm"] = spacing.sm
        variables["--spacing-md"] = spacing.md
        variables["--spacing-lg"] = spacing.lg
        variables["--spacing-xl"] = spacing.xl
        variables["--spacing-xxl"] = spacing.xxl
        
        // Border Radius
        val borderRadius = config.borderRadiusTheme
        variables["--border-radius-none"] = borderRadius.none
        variables["--border-radius-sm"] = borderRadius.sm
        variables["--border-radius-md"] = borderRadius.md
        variables["--border-radius-lg"] = borderRadius.lg
        variables["--border-radius-xl"] = borderRadius.xl
        variables["--border-radius-pill"] = borderRadius.pill
        variables["--border-radius-circle"] = borderRadius.circle
        
        // Elevation/Shadows
        val elevation = config.elevationTheme
        variables["--shadow-none"] = elevation.none
        variables["--shadow-xs"] = elevation.xs
        variables["--shadow-sm"] = elevation.sm
        variables["--shadow-md"] = elevation.md
        variables["--shadow-lg"] = elevation.lg
        variables["--shadow-xl"] = elevation.xl
        variables["--shadow-xxl"] = elevation.xxl
        
        // Custom values
        config.customValues.forEach { (name, value) ->
            val cssName = if (name.startsWith("--")) name else "--custom-$name"
            variables[cssName] = value
        }
        
        return variables
    }
    
    private fun addTypographyVariables(
        target: MutableMap<String, String>,
        prefix: String,
        style: Theme.TextStyle
    ) {
        style.fontFamily?.let { target["--typography-$prefix-font-family"] = it }
        style.fontSize?.let { target["--typography-$prefix-font-size"] = it }
        style.fontWeight?.let { target["--typography-$prefix-font-weight"] = it }
        style.lineHeight?.let { target["--typography-$prefix-line-height"] = it }
        style.letterSpacing?.let { target["--typography-$prefix-letter-spacing"] = it }
        style.color?.let { target["--typography-$prefix-color"] = it }
    }
}

/**
 * Composable that applies theme CSS variables to the document root.
 *
 * This should be called at the root of your app to ensure theme variables
 * are available to all components.
 *
 * @param themeState The reactive theme state to watch for changes
 */
@Composable
fun ApplyThemeVariables(themeState: SummonMutableState<Theme.ThemeConfig>) {
    val currentTheme = themeState.value
    
    LaunchedEffect(currentTheme) {
        val variables = ThemeVariableGenerator.generateVariables(currentTheme)
        ThemeVariableInjector.injectVariables(variables)
    }
}

/**
 * Composable that applies the current global theme's CSS variables.
 *
 * This is a convenience wrapper that uses Theme.getTheme() directly.
 */
@Composable
fun ApplyCurrentThemeVariables() {
    val theme = remember { Theme.getTheme() }
    
    LaunchedEffect(theme) {
        val variables = ThemeVariableGenerator.generateVariables(theme)
        ThemeVariableInjector.injectVariables(variables)
    }
}
