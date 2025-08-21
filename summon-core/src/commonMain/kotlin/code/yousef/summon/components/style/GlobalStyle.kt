package code.yousef.summon.components.style

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * Injects global CSS styles into the document head.
 * This component allows you to define styles that apply globally to the entire document.
 *
 * @param css The CSS content to inject
 * @param modifier The modifier to apply to this component
 */
@Composable
fun GlobalStyle(
    css: String,
    modifier: Modifier = Modifier()
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderGlobalStyle(css)
}

/**
 * Injects CSS keyframe animations into the document head.
 *
 * @param name The name of the keyframe animation
 * @param keyframes The keyframe definition (without @keyframes wrapper)
 * @param modifier The modifier to apply to this component
 */
@Composable
fun GlobalKeyframes(
    name: String,
    keyframes: String,
    modifier: Modifier = Modifier()
) {
    val css = "@keyframes $name { $keyframes }"
    GlobalStyle(css, modifier)
}

/**
 * Injects CSS custom properties (variables) into the document root.
 *
 * @param variables A map of CSS variable names to their values
 * @param modifier The modifier to apply to this component
 */
@Composable
fun CssVariables(
    variables: Map<String, String>,
    modifier: Modifier = Modifier()
) {
    val css = ":root { ${variables.map { (key, value) -> "$key: $value;" }.joinToString(" ")} }"
    GlobalStyle(css, modifier)
}

/**
 * Injects CSS media queries into the document head.
 *
 * @param query The media query (e.g., "@media (max-width: 768px)")
 * @param css The CSS content to apply within the media query
 * @param modifier The modifier to apply to this component
 */
@Composable
fun MediaQuery(
    query: String,
    css: String,
    modifier: Modifier = Modifier()
) {
    val wrappedCss = "$query { $css }"
    GlobalStyle(wrappedCss, modifier)
}

/**
 * Theme configuration for consistent styling across the application.
 */
data class ThemeConfig(
    val colors: Map<String, String> = emptyMap(),
    val gradients: Map<String, String> = emptyMap(),
    val shadows: Map<String, String> = emptyMap(),
    val spacing: Map<String, String> = emptyMap(),
    val typography: Map<String, String> = emptyMap()
)

/**
 * Creates a comprehensive theme with CSS variables for colors, gradients, shadows, etc.
 *
 * @param lightTheme Configuration for light theme
 * @param darkTheme Configuration for dark theme (optional)
 * @param modifier The modifier to apply to this component
 */
@Composable
fun ThemeProvider(
    lightTheme: ThemeConfig,
    darkTheme: ThemeConfig? = null,
    modifier: Modifier = Modifier()
) {
    // Build CSS variables for light theme
    val lightVariables = buildMap {
        lightTheme.colors.forEach { (key, value) -> put("--color-$key", value) }
        lightTheme.gradients.forEach { (key, value) -> put("--gradient-$key", value) }
        lightTheme.shadows.forEach { (key, value) -> put("--shadow-$key", value) }
        lightTheme.spacing.forEach { (key, value) -> put("--spacing-$key", value) }
        lightTheme.typography.forEach { (key, value) -> put("--font-$key", value) }
    }
    
    // Apply light theme variables to :root
    CssVariables(lightVariables, modifier)
    
    // Apply dark theme variables if provided
    if (darkTheme != null) {
        val darkVariables = buildMap {
            darkTheme.colors.forEach { (key, value) -> put("--color-$key", value) }
            darkTheme.gradients.forEach { (key, value) -> put("--gradient-$key", value) }
            darkTheme.shadows.forEach { (key, value) -> put("--shadow-$key", value) }
            darkTheme.spacing.forEach { (key, value) -> put("--spacing-$key", value) }
            darkTheme.typography.forEach { (key, value) -> put("--font-$key", value) }
        }
        
        val darkThemeCss = buildString {
            append("body.dark-theme, .dark-theme {")
            darkVariables.forEach { (key, value) ->
                append("$key: $value;")
            }
            append("}")
        }
        
        GlobalStyle(darkThemeCss, modifier)
    }
}

/**
 * Creates a gradient background utility class.
 *
 * @param gradients Map of gradient names to CSS gradient definitions
 * @param modifier The modifier to apply to this component
 */
@Composable
fun GradientUtilities(
    gradients: Map<String, String>,
    modifier: Modifier = Modifier()
) {
    val css = buildString {
        gradients.forEach { (name, gradient) ->
            append(".bg-gradient-$name { background: $gradient; }")
            append(".bg-gradient-$name-to-r { background: linear-gradient(to right, $gradient); }")
            append(".bg-gradient-$name-to-b { background: linear-gradient(to bottom, $gradient); }")
        }
    }
    
    GlobalStyle(css, modifier)
}

/**
 * Predefined gradient themes for common use cases.
 */
object GradientThemes {
    val modernPrimary = mapOf(
        "primary" to "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
        "secondary" to "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
        "success" to "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
        "warning" to "linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)",
        "danger" to "linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)"
    )
    
    val glassMorphism = mapOf(
        "glass-light" to "rgba(255, 255, 255, 0.25)",
        "glass-dark" to "rgba(30, 41, 59, 0.25)",
        "glass-backdrop" to "blur(10px)"
    )
}