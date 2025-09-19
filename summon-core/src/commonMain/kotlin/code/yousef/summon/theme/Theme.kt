package code.yousef.summon.theme

import code.yousef.summon.core.style.Color
import code.yousef.summon.modifier.*
import code.yousef.summon.modifier.StylingModifierExtras.textDecoration


/**
 * # Summon Theme System
 *
 * The Theme system provides a comprehensive, type-safe theming solution for Summon applications.
 * It combines colors, typography, spacing, borders, and elevation into a cohesive design system
 * that supports multiple themes, dark/light modes, and dynamic theme switching.
 *
 * ## Overview
 *
 * The Theme system is built around several key concepts:
 * - **Design Tokens**: Semantic design values (colors, spacing, typography)
 * - **Theme Configurations**: Complete theme definitions with all design tokens
 * - **Type Safety**: Strongly typed access to theme values
 * - **Extensibility**: Easy customization and theme creation
 * - **Runtime Switching**: Dynamic theme changes without restart
 *
 * ## Core Components
 *
 * ### Color System
 * Integrated with [ColorSystem] for semantic color management:
 * - Light and dark mode support
 * - Semantic color naming (primary, secondary, surface, etc.)
 * - Automatic contrast handling
 * - Custom color palette support
 *
 * ### Typography System
 * Complete typography scale with semantic styles:
 * - Heading hierarchy (h1-h6)
 * - Body text variants (body, bodyLarge, bodySmall)
 * - Specialized styles (caption, button, code, link)
 * - Font weight, size, and spacing control
 *
 * ### Spacing System
 * Consistent spacing scale for layout and positioning:
 * - Predefined spacing tokens (xs, sm, md, lg, xl, xxl)
 * - Semantic spacing application
 * - Responsive spacing support
 *
 * ### Design Tokens
 * Additional design system tokens:
 * - **Border Radius**: Consistent corner radius values
 * - **Elevation**: Box shadow system for depth
 * - **Custom Values**: Extensible custom design tokens
 *
 * ## Usage Examples
 *
 * ### Basic Theme Usage
 * ```kotlin
 * @Composable
 * fun ThemedComponent() {
 *     Column(
 *         modifier = Modifier()
 *             .themeBackgroundColor("surface")
 *             .themePadding("md")
 *             .themeBorderRadius("lg")
 *             .themeElevation("sm")
 *     ) {
 *         Text(
 *             text = "Themed Heading",
 *             modifier = Modifier.themeTextStyle("h2")
 *         )
 *         Text(
 *             text = "Themed body text with semantic colors",
 *             modifier = Modifier.themeTextStyle("body")
 *                 .themeColor("onSurface")
 *         )
 *     }
 * }
 * ```
 *
 * ### Theme Switching
 * ```kotlin
 * @Composable
 * fun App() {
 *     var isDarkMode by remember { mutableStateOf(false) }
 *
 *     LaunchedEffect(isDarkMode) {
 *         Theme.setTheme(
 *             if (isDarkMode) Theme.Themes.dark else Theme.Themes.light
 *         )
 *     }
 *
 *     Column {
 *         ThemeToggle(isDarkMode) { isDarkMode = it }
 *         ThemedContent()
 *     }
 * }
 * ```
 *
 * ### Custom Theme Creation
 * ```kotlin
 * val customTheme = Theme.createTheme(Theme.Themes.light) {
 *     copy(
 *         colorPalette = ColorSystem.purple,
 *         typographyTheme = typographyTheme.copy(
 *             h1 = TextStyle.create(
 *                 fontSize = 3.0,
 *                 fontWeight = FontWeight.ExtraBold
 *             )
 *         ),
 *         customValues = mapOf(
 *             "heroSpacing" to "5rem",
 *             "brandFont" to "Inter, sans-serif"
 *         )
 *     )
 * }
 *
 * Theme.setTheme(customTheme)
 * ```
 *
 * ### Advanced Typography
 * ```kotlin
 * @Composable
 * fun AdvancedText() {
 *     val customStyle = TextStyle.create(
 *         fontSize = 1.5,
 *         fontWeight = FontWeight.SemiBold,
 *         color = Color.hex("#2563eb"),
 *         letterSpacing = 0.02,
 *         lineHeight = 1.6
 *     )
 *
 *     Text(
 *         text = "Custom styled text",
 *         modifier = Modifier.applyTextStyle(customStyle)
 *     )
 * }
 * ```
 *
 * ## Predefined Themes
 *
 * The Theme system includes several predefined themes:
 * - **Light**: Default light theme with modern colors
 * - **Dark**: Dark mode theme with appropriate contrast
 * - **Blue**: Blue-tinted theme for professional applications
 * - **Green**: Nature-inspired green theme
 * - **Purple**: Creative purple theme for artistic applications
 *
 * ## Theme Architecture
 *
 * ### ThemeConfig Structure
 * ```kotlin
 * data class ThemeConfig(
 *     val colorPalette: ColorSystem.ColorPalette,    // Color system
 *     val typography: Map<String, TextStyle>,        // Text styles
 *     val spacing: Map<String, String>,              // Spacing values
 *     val borderRadius: Map<String, String>,         // Border radius values
 *     val elevation: Map<String, String>,            // Elevation/shadow values
 *     val customValues: Map<String, String>,         // Custom design tokens
 *     // Typed theme access
 *     val typographyTheme: TypographyTheme,
 *     val spacingTheme: SpacingTheme,
 *     val borderRadiusTheme: BorderRadiusTheme,
 *     val elevationTheme: ElevationTheme
 * )
 * ```
 *
 * ### Type-Safe Access
 * The theme system provides both string-based and type-safe access:
 * ```kotlin
 * // String-based access (flexible)
 * val color = Theme.getColor("primary")
 * val spacing = Theme.getSpacing("lg")
 *
 * // Type-safe access (preferred)
 * val typography = Theme.getTypographyTheme()
 * val headingStyle = typography.h1
 * val borderRadius = Theme.getBorderRadiusTheme().lg
 * ```
 *
 * ## Integration with Components
 *
 * ### Modifier Extensions
 * Theme values are applied through convenient modifier extensions:
 * ```kotlin
 * Modifier
 *     .themeColor("primary")              // Apply theme color
 *     .themeBackgroundColor("surface")    // Apply background color
 *     .themeTextStyle("h2")               // Apply text style
 *     .themePadding("md")                 // Apply spacing
 *     .themeBorderRadius("lg")            // Apply border radius
 *     .themeElevation("sm")               // Apply elevation
 * ```
 *
 * ### Component Integration
 * Components automatically inherit theme values:
 * ```kotlin
 * @Composable
 * fun ThemeAwareButton(
 *     text: String,
 *     onClick: () -> Unit,
 *     variant: ButtonVariant = ButtonVariant.Primary
 * ) {
 *     val colors = Theme.getColorSystem()
 *     val typography = Theme.getTypographyTheme()
 *
 *     Button(
 *         onClick = onClick,
 *         modifier = Modifier()
 *             .themeBackgroundColor(
 *                 when (variant) {
 *                     ButtonVariant.Primary -> "primary"
 *                     ButtonVariant.Secondary -> "secondary"
 *                 }
 *             )
 *             .themePadding("sm", "md")
 *             .themeBorderRadius("md")
 *             .themeTextStyle("button")
 *     ) {
 *         Text(text)
 *     }
 * }
 * ```
 *
 * ## Performance Considerations
 *
 * - **Theme Caching**: Theme values are cached for optimal performance
 * - **Lazy Evaluation**: Complex theme calculations are performed lazily
 * - **Minimal Recomposition**: Theme changes trigger minimal recomposition
 * - **Memory Efficiency**: Shared theme instances reduce memory usage
 *
 * ## Best Practices
 *
 * 1. **Use Semantic Names**: Prefer semantic color names over specific colors
 * 2. **Consistent Spacing**: Use theme spacing tokens consistently
 * 3. **Typography Hierarchy**: Follow the established typography scale
 * 4. **Theme Testing**: Test components in both light and dark themes
 * 5. **Custom Tokens**: Use custom values for application-specific needs
 *
 * @see ColorSystem for color management
 * @see Typography for typography utilities
 * @see Spacing for spacing constants
 * @see TextStyle for text styling
 * @since 1.0.0
 */
object Theme {
    /**
     * Represents a text style with common properties.
     * Uses String? for nullable CSS values.
     */
    data class TextStyle(
        // String-based properties for backward compatibility
        val fontFamily: String? = null,
        val fontSize: String? = null,
        val fontWeight: String? = null,
        val fontStyle: String? = null, // e.g., "italic"
        val color: String? = null,
        val textDecoration: String? = null, // e.g., "underline"
        val lineHeight: String? = null,
        val letterSpacing: String? = null,
        // Typed properties for more type-safe access
        val fontWeightEnum: FontWeight? = null,
        val fontSizeNumber: Number? = null,
        val fontSizeUnit: String? = null, // e.g., "px", "rem", "em"
        val lineHeightNumber: Number? = null,
        val letterSpacingNumber: Number? = null,
        val letterSpacingUnit: String? = null, // e.g., "px", "em"
        val colorValue: Color? = null
        // Add other relevant CSS text properties as needed
    ) {
        companion object {
            /**
             * Creates a TextStyle with typed properties, automatically setting string equivalents
             */
            fun create(
                fontFamily: String? = null,
                fontSize: Number? = null,
                fontSizeUnit: String? = "rem",
                fontWeight: FontWeight? = null,
                fontStyle: String? = null,
                color: Color? = null,
                textDecoration: String? = null,
                lineHeight: Number? = null,
                letterSpacing: Number? = null,
                letterSpacingUnit: String? = "em"
            ): TextStyle {
                // Convert typed properties to string equivalents
                val fontSizeStr = if (fontSize != null && fontSizeUnit != null) "$fontSize$fontSizeUnit" else null
                val fontWeightStr = fontWeight?.value
                val colorStr = color?.toHexString()
                val lineHeightStr = if (lineHeight != null) "$lineHeight" else null
                val letterSpacingStr =
                    if (letterSpacing != null && letterSpacingUnit != null) "$letterSpacing$letterSpacingUnit" else null

                return TextStyle(
                    fontFamily = fontFamily,
                    fontSize = fontSizeStr,
                    fontWeight = fontWeightStr,
                    fontStyle = fontStyle,
                    color = colorStr,
                    textDecoration = textDecoration,
                    lineHeight = lineHeightStr,
                    letterSpacing = letterSpacingStr,
                    fontWeightEnum = fontWeight,
                    fontSizeNumber = fontSize,
                    fontSizeUnit = fontSizeUnit,
                    lineHeightNumber = lineHeight,
                    letterSpacingNumber = letterSpacing,
                    letterSpacingUnit = letterSpacingUnit,
                    colorValue = color
                )
            }
        }
    }

    /**
     * Typed theme typography configuration
     */
    data class TypographyTheme(
        // Using the new create method with typed properties
        val h1: TextStyle = TextStyle.create(
            fontSize = 2.5,
            fontWeight = FontWeight.Bold
        ),
        val h2: TextStyle = TextStyle.create(
            fontSize = 2.0,
            fontWeight = FontWeight.Bold
        ),
        val h3: TextStyle = TextStyle.create(
            fontSize = 1.75,
            fontWeight = FontWeight.Bold
        ),
        val h4: TextStyle = TextStyle.create(
            fontSize = 1.5,
            fontWeight = FontWeight.Bold
        ),
        val h5: TextStyle = TextStyle.create(
            fontSize = 1.25,
            fontWeight = FontWeight.Bold
        ),
        val h6: TextStyle = TextStyle.create(
            fontSize = 1.0,
            fontWeight = FontWeight.Bold
        ),
        val subtitle: TextStyle = TextStyle.create(
            fontSize = 1.1,
            fontWeight = FontWeight.Medium,
            color = Color.hex("#6c757d")
        ),
        val body: TextStyle = TextStyle.create(
            fontSize = 1.0,
            fontWeight = FontWeight.Normal
        ),
        val bodyLarge: TextStyle = TextStyle.create(
            fontSize = 1.1,
            fontWeight = FontWeight.Normal
        ),
        val bodySmall: TextStyle = TextStyle.create(
            fontSize = 0.9,
            fontWeight = FontWeight.Normal
        ),
        val caption: TextStyle = TextStyle.create(
            fontSize = 0.8,
            fontWeight = FontWeight.Normal,
            color = Color.hex("#868e96")
        ),
        val button: TextStyle = TextStyle.create(
            fontSize = 1.0,
            fontWeight = FontWeight.Medium,
            textDecoration = "none"
        ),
        val overline: TextStyle = TextStyle.create(
            fontSize = 0.75,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.05,
            textDecoration = "uppercase"
        ),
        val link: TextStyle = TextStyle.create(
            fontSize = 1.0,
            fontWeight = FontWeight.Normal,
            color = Color.hex("#0d6efd"),
            textDecoration = "underline"
        ),
        val code: TextStyle = TextStyle.create(
            fontSize = 0.9,
            fontFamily = "monospace"
        )
    )

    /**
     * Typed theme spacing configuration
     */
    data class SpacingTheme(
        val xs: String = Spacing.xs,
        val sm: String = Spacing.sm,
        val md: String = Spacing.md,
        val lg: String = Spacing.lg,
        val xl: String = Spacing.xl,
        val xxl: String = Spacing.xxl
    )

    /**
     * Typed theme border radius configuration
     */
    data class BorderRadiusTheme(
        val none: String = "0",
        val sm: String = "4px",
        val md: String = "8px",
        val lg: String = "16px",
        val xl: String = "24px",
        val pill: String = "9999px",
        val circle: String = "50%"
    )

    /**
     * Typed theme elevation configuration
     */
    data class ElevationTheme(
        val none: String = "none",
        val xs: String = "0 1px 2px rgba(0, 0, 0, 0.05)",
        val sm: String = "0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px rgba(0, 0, 0, 0.06)",
        val md: String = "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)",
        val lg: String = "0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)",
        val xl: String = "0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)",
        val xxl: String = "0 25px 50px -12px rgba(0, 0, 0, 0.25)"
    )

    /**
     * Represents a complete theme configuration
     */
    data class ThemeConfig(
        val colorPalette: ColorSystem.ColorPalette = ColorSystem.default,
        val typography: Map<String, TextStyle> = emptyMap(),
        val spacing: Map<String, String> = emptyMap(),
        val borderRadius: Map<String, String> = emptyMap(),
        val elevation: Map<String, String> = emptyMap(),
        val customValues: Map<String, String> = emptyMap(),
        // Typed theme properties for direct access
        val typographyTheme: TypographyTheme = TypographyTheme(),
        val spacingTheme: SpacingTheme = SpacingTheme(),
        val borderRadiusTheme: BorderRadiusTheme = BorderRadiusTheme(),
        val elevationTheme: ElevationTheme = ElevationTheme()
    )

    /**
     * Default typography values from Typography object
     */
    private val defaultTypography: Map<String, TextStyle> = mapOf(
        "h1" to TextStyle.create(fontSize = 2.5, fontWeight = FontWeight.Bold),
        "h2" to TextStyle.create(fontSize = 2.0, fontWeight = FontWeight.Bold),
        "h3" to TextStyle.create(fontSize = 1.75, fontWeight = FontWeight.Bold),
        "h4" to TextStyle.create(fontSize = 1.5, fontWeight = FontWeight.Bold),
        "h5" to TextStyle.create(fontSize = 1.25, fontWeight = FontWeight.Bold),
        "h6" to TextStyle.create(fontSize = 1.0, fontWeight = FontWeight.Bold),
        "subtitle" to TextStyle.create(fontSize = 1.1, fontWeight = FontWeight.Medium, color = Color.hex("#6c757d")),
        "body" to TextStyle.create(fontSize = 1.0, fontWeight = FontWeight.Normal),
        "bodyLarge" to TextStyle.create(fontSize = 1.1, fontWeight = FontWeight.Normal),
        "bodySmall" to TextStyle.create(fontSize = 0.9, fontWeight = FontWeight.Normal),
        "caption" to TextStyle.create(fontSize = 0.8, fontWeight = FontWeight.Normal, color = Color.hex("#868e96")),
        "button" to TextStyle.create(fontSize = 1.0, fontWeight = FontWeight.Medium, textDecoration = "none"),
        "overline" to TextStyle.create(
            fontSize = 0.75,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.05,
            textDecoration = "uppercase"
        ),
        "link" to TextStyle.create(
            fontSize = 1.0,
            fontWeight = FontWeight.Normal,
            color = Color.hex("#0d6efd"),
            textDecoration = "underline"
        ),
        "code" to TextStyle.create(fontSize = 0.9, fontFamily = "monospace")
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
     * Current active theme configuration
     */
    private var currentTheme: ThemeConfig = ThemeConfig(
        typography = defaultTypography,
        spacing = defaultSpacing,
        borderRadius = defaultBorderRadius,
        elevation = defaultElevation
    )

    /**
     * Predefined themes
     */
    object Themes {
        /**
         * Default light theme
         */
        val light = ThemeConfig(
            colorPalette = ColorSystem.default,
            typography = defaultTypography,
            spacing = defaultSpacing,
            borderRadius = defaultBorderRadius,
            elevation = defaultElevation
        )

        /**
         * Default dark theme
         */
        val dark = ThemeConfig(
            colorPalette = ColorSystem.default,
            typography = defaultTypography,
            spacing = defaultSpacing,
            borderRadius = defaultBorderRadius,
            elevation = defaultElevation
        )

        /**
         * Blue theme
         */
        val blue = ThemeConfig(
            colorPalette = ColorSystem.blue,
            typography = defaultTypography,
            spacing = defaultSpacing,
            borderRadius = defaultBorderRadius,
            elevation = defaultElevation
        )

        /**
         * Green theme
         */
        val green = ThemeConfig(
            colorPalette = ColorSystem.green,
            typography = defaultTypography,
            spacing = defaultSpacing,
            borderRadius = defaultBorderRadius,
            elevation = defaultElevation
        )

        /**
         * Purple theme
         */
        val purple = ThemeConfig(
            colorPalette = ColorSystem.purple,
            typography = defaultTypography,
            spacing = defaultSpacing,
            borderRadius = defaultBorderRadius,
            elevation = defaultElevation
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

    /**
     * Get the typography theme from the current theme
     * @return The typography theme
     */
    fun getTypographyTheme(): TypographyTheme = currentTheme.typographyTheme

    /**
     * Get the spacing theme from the current theme
     * @return The spacing theme
     */
    fun getSpacingTheme(): SpacingTheme = currentTheme.spacingTheme

    /**
     * Get the border radius theme from the current theme
     * @return The border radius theme
     */
    fun getBorderRadiusTheme(): BorderRadiusTheme = currentTheme.borderRadiusTheme

    /**
     * Get the elevation theme from the current theme
     * @return The elevation theme
     */
    fun getElevationTheme(): ElevationTheme = currentTheme.elevationTheme
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

    // Apply font family
    style.fontFamily?.let { modified = modified.fontFamily(it, null) }

    // Apply font size - prefer typed property if available
    if (style.fontSizeNumber != null && style.fontSizeUnit != null) {
        val fontSize = "${style.fontSizeNumber}${style.fontSizeUnit}"
        modified = modified.fontSize(fontSize)
    } else {
        style.fontSize?.let { modified = modified.fontSize(it) }
    }

    // Apply font weight - prefer enum if available
    if (style.fontWeightEnum != null) {
        modified = modified.fontWeight(style.fontWeightEnum.value)
    } else {
        style.fontWeight?.let { modified = modified.fontWeight(it) }
    }

    // Apply font style
    style.fontStyle?.let { modified = modified.style("font-style", it) }

    // Apply color - prefer Color object if available
    if (style.colorValue != null) {
        modified = modified.color(style.colorValue.toString())
    } else {
        style.color?.let { modified = modified.color(it) }
    }

    // Apply text decoration
    style.textDecoration?.let { modified = modified.textDecoration(it) }

    // Apply line height - prefer number if available
    if (style.lineHeightNumber != null) {
        modified = modified.lineHeight(style.lineHeightNumber.toString(), null)
    } else {
        style.lineHeight?.let { modified = modified.lineHeight(it, null) }
    }

    // Apply letter spacing - prefer typed property if available
    if (style.letterSpacingNumber != null && style.letterSpacingUnit != null) {
        val letterSpacing = "${style.letterSpacingNumber}${style.letterSpacingUnit}"
        modified = modified.letterSpacing(letterSpacing, null)
    } else {
        style.letterSpacing?.let { modified = modified.letterSpacing(it, null) }
    }

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
