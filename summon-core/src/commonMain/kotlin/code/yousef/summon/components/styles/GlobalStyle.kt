package code.yousef.summon.components.styles

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.mapOfCompat
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer

/**
 * # GlobalStyle
 *
 * A styling component that injects CSS styles into the document head, enabling
 * global styling, theme management, and CSS framework integration for Summon applications.
 *
 * ## Overview
 *
 * GlobalStyle provides comprehensive styling capabilities with:
 * - **Global CSS injection** - Add styles that apply to the entire document
 * - **Theme management** - Create and manage application-wide themes
 * - **CSS framework integration** - Import and use external CSS frameworks
 * - **Performance optimization** - Efficient style injection and updating
 * - **SSR compatibility** - Server-side style rendering support
 *
 * ## Key Features
 *
 * ### Style Management
 * - **Global styles** - Document-wide CSS rules and resets
 * - **CSS variables** - Theme-based custom properties
 * - **Media queries** - Responsive design rules
 * - **Keyframe animations** - CSS animation definitions
 * - **Font imports** - External font loading and definitions
 *
 * ### Theme Support
 * - **Light/Dark themes** - Automatic theme switching
 * - **Custom themes** - Brand-specific design systems
 * - **Theme persistence** - Remember user theme preferences
 * - **Dynamic theming** - Runtime theme updates
 *
 * ## Basic Usage
 *
 * ### Global CSS Reset
 * ```kotlin
 * @Composable
 * fun App() {
 *     GlobalStyle(css = """
 *         * {
 *             margin: 0;
 *             padding: 0;
 *             box-sizing: border-box;
 *         }
 *
 *         body {
 *             font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
 *             line-height: 1.6;
 *             color: #333;
 *             background-color: #ffffff;
 *         }
 *
 *         h1, h2, h3, h4, h5, h6 {
 *             margin-bottom: 0.5rem;
 *             font-weight: 600;
 *             line-height: 1.2;
 *         }
 *     """)
 *
 *     AppContent()
 * }
 * ```
 *
 * ### CSS Variables Theme
 * ```kotlin
 * @Composable
 * fun ThemedApp() {
 *     CssVariables(
 *         variables = mapOfCompat(
 *             "--color-primary" to "#3b82f6",
 *             "--color-secondary" to "#6366f1",
 *             "--color-success" to "#10b981",
 *             "--color-warning" to "#f59e0b",
 *             "--color-error" to "#ef4444",
 *             "--color-text" to "#1f2937",
 *             "--color-background" to "#ffffff",
 *             "--spacing-xs" to "0.25rem",
 *             "--spacing-sm" to "0.5rem",
 *             "--spacing-md" to "1rem",
 *             "--spacing-lg" to "1.5rem",
 *             "--spacing-xl" to "3rem"
 *         )
 *     )
 *
 *     GlobalStyle(css = """
 *         .btn-primary {
 *             background-color: var(--color-primary);
 *             color: white;
 *             padding: var(--spacing-sm) var(--spacing-md);
 *             border: none;
 *             border-radius: 0.375rem;
 *             cursor: pointer;
 *             transition: background-color 0.2s ease;
 *         }
 *
 *         .btn-primary:hover {
 *             background-color: color-mix(in srgb, var(--color-primary) 85%, black);
 *         }
 *     """)
 *
 *     AppContent()
 * }
 * ```
 *
 * ### Dark Theme Support
 * ```kotlin
 * @Composable
 * fun DarkThemeApp() {
 *     ThemeProvider(
 *         lightTheme = ThemeConfig(
 *             colors = mapOfCompat(
 *                 "background" to "#ffffff",
 *                 "surface" to "#f8fafc",
 *                 "text" to "#1f2937",
 *                 "border" to "#e5e7eb"
 *             )
 *         ),
 *         darkTheme = ThemeConfig(
 *             colors = mapOfCompat(
 *                 "background" to "#0f172a",
 *                 "surface" to "#1e293b",
 *                 "text" to "#f1f5f9",
 *                 "border" to "#334155"
 *             )
 *         )
 *     )
 *
 *     GlobalStyle(css = """
 *         .theme-toggle {
 *             background: var(--color-surface);
 *             color: var(--color-text);
 *             border: 1px solid var(--color-border);
 *             border-radius: 0.5rem;
 *             padding: 0.5rem;
 *             cursor: pointer;
 *         }
 *
 *         @media (prefers-color-scheme: dark) {
 *             :root {
 *                 color-scheme: dark;
 *             }
 *         }
 *     """)
 *
 *     AppContent()
 * }
 * ```
 *
 * ### Animation Definitions
 * ```kotlin
 * @Composable
 * fun AnimatedApp() {
 *     GlobalKeyframes(
 *         name = "fadeIn",
 *         keyframes = """
 *             0% { opacity: 0; transform: translateY(20px); }
 *             100% { opacity: 1; transform: translateY(0); }
 *         """
 *     )
 *
 *     GlobalKeyframes(
 *         name = "slideIn",
 *         keyframes = """
 *             0% { transform: translateX(-100%); }
 *             100% { transform: translateX(0); }
 *         """
 *     )
 *
 *     GlobalStyle(css = """
 *         .fade-in {
 *             animation: fadeIn 0.5s ease-out;
 *         }
 *
 *         .slide-in {
 *             animation: slideIn 0.3s ease-out;
 *         }
 *
 *         .loading-spinner {
 *             animation: spin 1s linear infinite;
 *         }
 *
 *         @keyframes spin {
 *             from { transform: rotate(0deg); }
 *             to { transform: rotate(360deg); }
 *         }
 *     """)
 *
 *     AppContent()
 * }
 * ```
 *
 * @param css The CSS content to inject
 * @param modifier The modifier to apply to this component
 *
 * @see CssVariables for CSS custom properties
 * @see ThemeProvider for theme management
 * @see GlobalKeyframes for animation definitions
 * @see MediaQuery for responsive styles
 *
 * @sample GlobalStyleSamples.cssReset
 * @sample GlobalStyleSamples.themeVariables
 * @sample GlobalStyleSamples.darkTheme
 * @sample GlobalStyleSamples.animations
 *
 * @since 1.0.0
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
    val modernPrimary = mapOfCompat(
        "primary" to "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
        "secondary" to "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)",
        "success" to "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)",
        "warning" to "linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)",
        "danger" to "linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)"
    )

    val glassMorphism = mapOfCompat(
        "glass-light" to "rgba(255, 255, 255, 0.25)",
        "glass-dark" to "rgba(30, 41, 59, 0.25)",
        "glass-backdrop" to "blur(10px)"
    )
}
