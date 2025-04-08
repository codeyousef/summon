package code.yousef.summon.theme

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.boxShadow


/**
 * Theme provides a centralized global styling configuration for the application.
 * It combines ColorSystem, Typography, and Spacing to create a consistent design system.
 * Theme allows applications to define and switch between different visual themes.
 */
object Theme {
    /**
     * Represents a text style with common properties.
     * Uses String? for nullable CSS values.
     */
    data class TextStyle(
        val fontFamily: String? = null,
        val fontSize: String? = null,
        val fontWeight: String? = null,
        val fontStyle: String? = null, // e.g., "italic"
        val color: String? = null,
        val textDecoration: String? = null, // e.g., "underline"
        val lineHeight: String? = null,
        val letterSpacing: String? = null
        // Add other relevant CSS text properties as needed
    )

    /**
     * Represents a complete theme configuration
     */
    data class ThemeConfig(
        val colorPalette: ColorSystem.ColorPalette = ColorSystem.default,
        val typography: Map<String, TextStyle> = defaultTypography,
        val spacing: Map<String, String> = defaultSpacing,
        val borderRadius: Map<String, String> = defaultBorderRadius,
        val elevation: Map<String, String> = defaultElevation,
        val customValues: Map<String, String> = emptyMap()
    )

    /**
     * Current active theme configuration
     */
    private var currentTheme: ThemeConfig = ThemeConfig()

    /**
     * Default typography values from Typography object
     */
    private val defaultTypography: Map<String, TextStyle> = mapOf(
        "h1" to TextStyle(fontSize = "2.5rem", fontWeight = "bold"),
        "h2" to TextStyle(fontSize = "2rem", fontWeight = "bold"),
        "h3" to TextStyle(fontSize = "1.75rem", fontWeight = "bold"),
        "h4" to TextStyle(fontSize = "1.5rem", fontWeight = "bold"),
        "h5" to TextStyle(fontSize = "1.25rem", fontWeight = "bold"),
        "h6" to TextStyle(fontSize = "1rem", fontWeight = "bold"),
        "subtitle" to TextStyle(fontSize = "1.1rem", fontWeight = "500", color = "#6c757d"),
        "body" to TextStyle(fontSize = "1rem", fontWeight = "normal"),
        "bodyLarge" to TextStyle(fontSize = "1.1rem", fontWeight = "normal"),
        "bodySmall" to TextStyle(fontSize = "0.9rem", fontWeight = "normal"),
        "caption" to TextStyle(fontSize = "0.8rem", fontWeight = "normal", color = "#868e96"),
        "button" to TextStyle(fontSize = "1rem", fontWeight = "500", textDecoration = "none"),
        "overline" to TextStyle(
            fontSize = "0.75rem",
            fontWeight = "600",
            letterSpacing = "0.05em",
            textDecoration = "uppercase"
        ),
        "link" to TextStyle(fontSize = "1rem", fontWeight = "normal", color = "#0d6efd", textDecoration = "underline"),
        "code" to TextStyle(fontSize = "0.9rem", fontFamily = "monospace")
    )

    /**
     * Default spacing values from Spacing object
     */
    private val defaultSpacing = mapOf(
        "xs" to Spacing.xs,
        "sm" to Spacing.sm,
        "md" to Spacing.md,
        "lg" to Spacing.lg,
        "xl" to Spacing.xl,
        "xxl" to Spacing.xxl
    )

    /**
     * Default border radius values
     */
    private val defaultBorderRadius = mapOf(
        "none" to "0",
        "sm" to "4px",
        "md" to "8px",
        "lg" to "16px",
        "xl" to "24px",
        "pill" to "9999px",
        "circle" to "50%"
    )

    /**
     * Default elevation (box-shadow) values
     */
    private val defaultElevation = mapOf(
        "none" to "none",
        "xs" to "0 1px 2px rgba(0, 0, 0, 0.05)",
        "sm" to "0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px rgba(0, 0, 0, 0.06)",
        "md" to "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)",
        "lg" to "0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)",
        "xl" to "0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)",
        "xxl" to "0 25px 50px -12px rgba(0, 0, 0, 0.25)"
    )

    /**
     * Predefined themes
     */
    object Themes {
        /**
         * Default light theme
         */
        val light = ThemeConfig(
            colorPalette = ColorSystem.default
        )

        /**
         * Default dark theme
         */
        val dark = ThemeConfig(
            colorPalette = ColorSystem.default
        )

        /**
         * Blue theme
         */
        val blue = ThemeConfig(
            colorPalette = ColorSystem.blue
        )

        /**
         * Green theme
         */
        val green = ThemeConfig(
            colorPalette = ColorSystem.green
        )

        /**
         * Purple theme
         */
        val purple = ThemeConfig(
            colorPalette = ColorSystem.purple
        )
    }

    /**
     * Set the active theme for the application
     * @param theme The theme to set
     */
    fun setTheme(theme: ThemeConfig) {
        currentTheme = theme
        // Also set the matching color system theme mode
        ColorSystem.setThemeMode(ColorSystem.ThemeMode.SYSTEM)
    }

    /**
     * Get the current theme
     * @return The current theme configuration
     */
    fun getTheme(): ThemeConfig = currentTheme

    /**
     * Get a color from the current theme
     * @param name The semantic color name
     * @param themeMode Optional theme mode override
     * @return The color value
     */
    fun getColor(name: String, themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()): String {
        val colorMap = currentTheme.colorPalette.forMode(themeMode)
        return colorMap[name] ?: "#000000" // Fallback to black
    }

    /**
     * Get a text style from the current theme
     * @param name The text style name
     * @return The text style or default body style if not found
     */
    fun getTextStyle(name: String): TextStyle {
        return currentTheme.typography[name] ?: defaultTypography["body"]!!
    }

    /**
     * Get a spacing value from the current theme
     * @param name The spacing name
     * @return The spacing value or default if not found
     */
    fun getSpacing(name: String): String {
        return currentTheme.spacing[name] ?: Spacing.md
    }

    /**
     * Get a border radius value from the current theme
     * @param name The border radius name
     * @return The border radius value or default if not found
     */
    fun getBorderRadius(name: String): String {
        return currentTheme.borderRadius[name] ?: "0"
    }

    /**
     * Get an elevation value from the current theme
     * @param name The elevation name
     * @return The elevation (box-shadow) value or none if not found
     */
    fun getElevation(name: String): String {
        return currentTheme.elevation[name] ?: "none"
    }

    /**
     * Get a custom theme value
     * @param name The custom value name
     * @param defaultValue The default value to return if not found
     * @return The custom value or default if not found
     */
    fun getCustomValue(name: String, defaultValue: String): String {
        return currentTheme.customValues[name] ?: defaultValue
    }

    /**
     * Create a new theme by extending an existing theme
     * @param baseTheme The base theme to extend
     * @param modifications A lambda that modifies a ThemeConfig copy
     * @return A new ThemeConfig
     */
    fun createTheme(baseTheme: ThemeConfig = Themes.light, modifications: ThemeConfig.() -> ThemeConfig): ThemeConfig {
        return baseTheme.modifications()
    }
}

/**
 * Extension functions to easily apply theme values to components
 */

/**
 * Apply a theme color to a modifier
 */
fun Modifier.themeColor(
    colorName: String,
    themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()
): Modifier =
    this.color(Theme.getColor(colorName, themeMode))

/**
 * Apply a theme background color to a modifier
 */
fun Modifier.themeBackgroundColor(
    colorName: String,
    themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()
): Modifier =
    this.backgroundColor(Theme.getColor(colorName, themeMode))

/**
 * Apply a theme border to a modifier
 */
fun Modifier.themeStyleBorder(
    width: String,
    style: String,
    colorName: String,
    themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()
): Modifier =
    this.border(width, style, Theme.getColor(colorName, themeMode))

/**
 * Apply a theme text style to a modifier
 */
fun Modifier.themeTextStyle(styleName: String): Modifier {
    val style = Theme.getTextStyle(styleName)
    var modified = this
    style.fontFamily?.let { modified = modified.fontFamily(it) }
    style.fontSize?.let { modified = modified.fontSize(it) }
    style.fontWeight?.let { modified = modified.fontWeight(it) }
    style.fontStyle?.let { modified = modified.style("font-style", it) }
    style.color?.let { modified = modified.color(it) }
    style.textDecoration?.let { modified = modified.textDecoration(it) }
    style.lineHeight?.let { modified = modified.lineHeight(it) }
    style.letterSpacing?.let { modified = modified.letterSpacing(it) }
    return modified
}

/**
 * Apply a theme border radius to a modifier
 */
fun Modifier.themeBorderRadius(radiusName: String): Modifier =
    this.borderRadius(Theme.getBorderRadius(radiusName))

/**
 * Apply theme padding to a modifier
 */
fun Modifier.themePadding(spacingName: String): Modifier =
    this.padding(Theme.getSpacing(spacingName))

/**
 * Apply theme elevation to a modifier
 */
fun Modifier.themeElevation(elevationName: String): Modifier =
    this.boxShadow(Theme.getElevation(elevationName))

/**
 * Apply theme margin to a modifier
 */
fun Modifier.themeMargin(spacingName: String): Modifier =
    this.margin(Theme.getSpacing(spacingName))

/**
 * Apply directional theme padding to a modifier
 */
fun Modifier.themePadding(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    val topValue = if (top != null) Theme.getSpacing(top) else null
    val rightValue = if (right != null) Theme.getSpacing(right) else null
    val bottomValue = if (bottom != null) Theme.getSpacing(bottom) else null
    val leftValue = if (left != null) Theme.getSpacing(left) else null

    // Create the CSS padding value string based on which values are provided
    val paddingValue = when {
        topValue != null && rightValue != null && bottomValue != null && leftValue != null ->
            "$topValue $rightValue $bottomValue $leftValue"

        topValue != null && rightValue != null && bottomValue != null ->
            "$topValue $rightValue $bottomValue"

        topValue != null && rightValue != null ->
            "$topValue $rightValue"

        topValue != null ->
            topValue

        else -> "0"
    }

    return this.padding(paddingValue)
}

/**
 * Apply directional theme margin to a modifier
 */
fun Modifier.themeMargin(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier {
    val topValue = if (top != null) Theme.getSpacing(top) else null
    val rightValue = if (right != null) Theme.getSpacing(right) else null
    val bottomValue = if (bottom != null) Theme.getSpacing(bottom) else null
    val leftValue = if (left != null) Theme.getSpacing(left) else null

    // Create the CSS margin value string based on which values are provided
    val marginValue = when {
        topValue != null && rightValue != null && bottomValue != null && leftValue != null ->
            "$topValue $rightValue $bottomValue $leftValue"

        topValue != null && rightValue != null && bottomValue != null ->
            "$topValue $rightValue $bottomValue"

        topValue != null && rightValue != null ->
            "$topValue $rightValue"

        topValue != null ->
            topValue

        else -> "0"
    }

    return this.margin(marginValue)
}

// --- Theme Data Classes ---
data class Colors(
    val primary: String,
    val secondary: String,
    val background: String,
    val surface: String,
    val error: String,
    val onPrimary: String,
    val onSecondary: String,
    val onBackground: String,
    val onSurface: String,
    val onError: String
    // Add more colors as needed (variants, states)
)

data class ThemeTypography(
    // Placeholder properties - Define actual styles later
    val h1: String = "font-size: 2em; font-weight: bold;", // Example inline style
    val body1: String = "font-size: 1em;"
)

data class Shapes(
    val small: Float = 4f,
    val medium: Float = 8f
)

// --- Default Theme Values ---
val LightColors = Colors(
    primary = "#6200EE",
    secondary = "#03DAC6",
    background = "#FFFFFF",
    surface = "#FFFFFF",
    error = "#B00020",
    onPrimary = "#FFFFFF",
    onSecondary = "#000000",
    onBackground = "#000000",
    onSurface = "#000000",
    onError = "#FFFFFF"
)

val DefaultTypography = ThemeTypography()
val DefaultShapes = Shapes() 
