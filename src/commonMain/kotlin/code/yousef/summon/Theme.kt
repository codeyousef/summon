package code.yousef.summon

/**
 * Theme provides a centralized global styling configuration for the application.
 * It combines ColorSystem, Typography, and Spacing to create a consistent design system.
 * Theme allows applications to define and switch between different visual themes.
 */
object Theme {
    /**
     * Represents a complete theme configuration
     */
    data class ThemeConfig(
        val colorPalette: ColorSystem.ColorPalette = ColorSystem.default,
        val typography: Map<String, Typography.TextStyle> = defaultTypography,
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
    private val defaultTypography = mapOf(
        "h1" to Typography.h1,
        "h2" to Typography.h2,
        "h3" to Typography.h3,
        "h4" to Typography.h4,
        "h5" to Typography.h5,
        "h6" to Typography.h6,
        "subtitle" to Typography.subtitle,
        "body" to Typography.body,
        "bodyLarge" to Typography.bodyLarge,
        "bodySmall" to Typography.bodySmall,
        "caption" to Typography.caption,
        "button" to Typography.button,
        "overline" to Typography.overline,
        "link" to Typography.link,
        "code" to Typography.code
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
    fun getTextStyle(name: String): Typography.TextStyle {
        return currentTheme.typography[name] ?: Typography.body
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
fun Modifier.themeColor(colorName: String, themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()): Modifier =
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
fun Modifier.themeTextStyle(styleName: String): Modifier =
    Theme.getTextStyle(styleName).applyTo(this)

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