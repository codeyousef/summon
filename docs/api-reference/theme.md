# Theme API Reference

This document provides detailed information about the theming APIs in the Summon library.

## Table of Contents

- [Theme](#theme)
- [ThemeManager](#thememanager)
- [Colors](#colors)
- [Typography](#typography)
- [Spacing](#spacing)
- [MediaQuery](#mediaquery)
- [StyleSheet](#stylesheet)

---

## Theme

The `Theme` object is the central container for styling information in a Summon application.

### Class Definition

```kotlin
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
     * Typed theme typography configuration
     */
    data class TypographyTheme(
        val h1: TextStyle = TextStyle(fontSize = "2.5rem", fontWeight = "bold"),
        val h2: TextStyle = TextStyle(fontSize = "2rem", fontWeight = "bold"),
        val h3: TextStyle = TextStyle(fontSize = "1.75rem", fontWeight = "bold"),
        // ... other text styles
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
        val typography: Map<String, TextStyle> = defaultTypography,
        val spacing: Map<String, String> = defaultSpacing,
        val borderRadius: Map<String, String> = defaultBorderRadius,
        val elevation: Map<String, String> = defaultElevation,
        val customValues: Map<String, String> = emptyMap(),
        // Typed theme properties for direct access
        val typographyTheme: TypographyTheme = TypographyTheme(),
        val spacingTheme: SpacingTheme = SpacingTheme(),
        val borderRadiusTheme: BorderRadiusTheme = BorderRadiusTheme(),
        val elevationTheme: ElevationTheme = ElevationTheme()
    )

    // Predefined themes
    object Themes {
        val light: ThemeConfig
        val dark: ThemeConfig
        val blue: ThemeConfig
        val green: ThemeConfig
        val purple: ThemeConfig
    }

    // Theme management methods
    fun setTheme(theme: ThemeConfig)
    fun getTheme(): ThemeConfig

    // Theme value getters (string-based for backward compatibility)
    fun getColor(name: String, themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()): String
    fun getTextStyle(name: String): TextStyle
    fun getSpacing(name: String): String
    fun getBorderRadius(name: String): String
    fun getElevation(name: String): String
    fun getCustomValue(name: String, defaultValue: String): String

    // Typed theme getters (for type-safe access)
    fun getTypographyTheme(): TypographyTheme
    fun getSpacingTheme(): SpacingTheme
    fun getBorderRadiusTheme(): BorderRadiusTheme
    fun getElevationTheme(): ElevationTheme

    // Theme creation
    fun createTheme(baseTheme: ThemeConfig = Themes.light, modifications: ThemeConfig.() -> ThemeConfig): ThemeConfig
}
```

### Description

The `Theme` object provides a centralized way to define consistent styles across an application. It uses a `ThemeConfig` data class to store theme values and provides methods to access and modify these values.

### Example

```kotlin
// Use a predefined theme
Theme.setTheme(Theme.Themes.light)

// Create a custom theme using string-based maps (backward compatibility)
val customTheme1 = Theme.createTheme(Theme.Themes.light) {
    copy(
        colorPalette = ColorSystem.blue,
        customValues = mapOf(
            "customColor" to "#ff5722",
            "customSpacing" to "24px"
        )
    )
}

// Create a custom theme using typed properties (more type-safe)
val customTheme2 = Theme.createTheme(Theme.Themes.light) {
    copy(
        colorPalette = ColorSystem.blue,
        typographyTheme = TypographyTheme(
            h1 = TextStyle.create(
                fontSize = 3.0,
                fontSizeUnit = "rem",
                fontWeight = FontWeight.Bold
            ),
            body = TextStyle.create(
                fontSize = 1.1,
                fontSizeUnit = "rem",
                lineHeight = 1.5
            )
        ),
        spacingTheme = SpacingTheme(
            md = "20px",
            lg = "32px"
        ),
        borderRadiusTheme = BorderRadiusTheme(
            md = "10px"
        ),
        customValues = mapOf(
            "customColor" to "#ff5722",
            "customSpacing" to "24px"
        )
    )
}

// Set the custom theme
Theme.setTheme(customTheme2)

// Access theme values using string-based getters (backward compatibility)
val primaryColor = Theme.getColor("primary")
val headingStyle = Theme.getTextStyle("h1")
val mediumSpacing = Theme.getSpacing("md")
val roundedCorners = Theme.getBorderRadius("lg")
val shadowEffect = Theme.getElevation("md")
val customValue = Theme.getCustomValue("customColor", "#000000")

// Access theme values using typed getters (more type-safe)
val typographyTheme = Theme.getTypographyTheme()
val h1Style = typographyTheme.h1
val bodyStyle = typographyTheme.body

val spacingTheme = Theme.getSpacingTheme()
val mdSpacing = spacingTheme.md
val lgSpacing = spacingTheme.lg

val borderRadiusTheme = Theme.getBorderRadiusTheme()
val mdRadius = borderRadiusTheme.md
val lgRadius = borderRadiusTheme.lg

val elevationTheme = Theme.getElevationTheme()
val mdElevation = elevationTheme.md
val lgElevation = elevationTheme.lg
```

---

## ThemeManager

The `ThemeManager` object manages typography settings for the application.

### Class Definition

```kotlin
object ThemeManager {
    private var currentTypography: Typography = Typography()

    fun getTypography(): Typography
    fun setTypography(typography: Typography)
}

interface ThemeConfiguration {
    val typography: Typography
    // Other theme components like colors, shapes, etc. can be added here
}

class DefaultThemeConfiguration : ThemeConfiguration {
    override val typography: Typography = Typography()
}
```

### Description

The `ThemeManager` provides a way to manage typography settings for the application. It's a simpler alternative to the full `Theme` object when you only need to manage typography.

### Example

```kotlin
// Get the current typography
val typography = ThemeManager.getTypography()

// Set a custom typography
val customTypography = Typography()
ThemeManager.setTypography(customTypography)

// Create a custom theme configuration
class MyThemeConfiguration : ThemeConfiguration {
    override val typography: Typography = Typography(
        // Custom typography settings
    )
}
```

---

## ColorSystem

The `ColorSystem` object provides a comprehensive color management system for the application.

### Class Definition

```kotlin
object ColorSystem {
    // Theme modes
    enum class ThemeMode {
        LIGHT, DARK, SYSTEM
    }

    // Color palette data class
    data class ColorPalette(
        val light: Map<String, String>,
        val dark: Map<String, String>
    ) {
        fun forMode(mode: ThemeMode): Map<String, String>
    }

    // Predefined color palettes
    val default: ColorPalette
    val blue: ColorPalette
    val green: ColorPalette
    val purple: ColorPalette

    // Theme mode management
    private var themeMode: ThemeMode = ThemeMode.SYSTEM
    fun getThemeMode(): ThemeMode
    fun setThemeMode(mode: ThemeMode)

    // Color utility functions
    fun darken(color: String, amount: Float): String
    fun lighten(color: String, amount: Float): String
    fun alpha(color: String, amount: Float): String
    fun complementary(color: String): String
    fun contrast(color: String): String
    fun hexToRgb(hex: String): Triple<Int, Int, Int>
    fun rgbToHex(r: Int, g: Int, b: Int): String
}

// Legacy Colors class for backward compatibility
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
)
```

### Description

The `ColorSystem` object provides a structured way to organize colors in a theme and ensure consistent usage throughout an application. It supports light and dark modes, and provides utility functions for color manipulation.

### Example

```kotlin
// Access predefined color palettes
val defaultPalette = ColorSystem.default
val bluePalette = ColorSystem.blue

// Set theme mode
ColorSystem.setThemeMode(ColorSystem.ThemeMode.DARK)

// Get current theme mode
val currentMode = ColorSystem.getThemeMode()

// Get colors for current theme mode
val colors = ColorSystem.default.forMode(currentMode)
val primaryColor = colors["primary"]
val backgroundColor = colors["background"]

// Use color utilities
val darkerPrimary = ColorSystem.darken(primaryColor!!, 0.2f)
val lighterSecondary = ColorSystem.lighten(colors["secondary"]!!, 0.1f)
val primaryWithOpacity = ColorSystem.alpha(primaryColor!!, 0.7f)
val contrastColor = ColorSystem.contrast(primaryColor!!)

// Convert between hex and RGB
val (r, g, b) = ColorSystem.hexToRgb("#3f51b5")
val hexColor = ColorSystem.rgbToHex(63, 81, 181)
```

---

## Typography

The `Typography` class defines text styles for a theme.

### Class Definition

```kotlin
class Typography {
    // Headings
    val h1: TextStyle
    val h2: TextStyle
    val h3: TextStyle
    val h4: TextStyle
    val h5: TextStyle
    val h6: TextStyle

    // Subtitles
    val subtitle1: TextStyle
    val subtitle2: TextStyle

    // Body text
    val body1: TextStyle
    val body2: TextStyle

    // Button text
    val button: TextStyle

    // Caption
    val caption: TextStyle

    // Overline
    val overline: TextStyle
}

// TextStyle is defined in Theme object
// See Theme.TextStyle for the full definition
```

### Description

The `Typography` class provides consistent text styling across an application with predefined styles for different text elements. Each style is defined as a `TextStyle` object with properties like font size, weight, and line height.

### Example

```kotlin
// Get the default typography
val typography = Typography()

// Access typography styles
val headingStyle = typography.h1
val bodyStyle = typography.body1
val buttonStyle = typography.button

// Using typography in components
Text(
    text = "Heading",
    modifier = Modifier.themeTextStyle("h1")
)

Text(
    text = "Body text",
    modifier = Modifier.themeTextStyle("body1")
)

// Using typography with ThemeManager
val managerTypography = ThemeManager.getTypography()
ThemeManager.setTypography(Typography())

// Using typography with Theme
val themeTextStyle = Theme.getTextStyle("h1")
```

---

## Spacing

The `Spacing` object defines consistent spacing values for a theme.

### Class Definition

```kotlin
object Spacing {
    // Base spacing unit (in pixels)
    private const val BASE_UNIT = 4

    // Standard spacing values
    const val xs = "${BASE_UNIT}px"           // 4px
    const val sm = "${BASE_UNIT * 2}px"       // 8px
    const val md = "${BASE_UNIT * 4}px"       // 16px
    const val lg = "${BASE_UNIT * 6}px"       // 24px
    const val xl = "${BASE_UNIT * 8}px"       // 32px
    const val xxl = "${BASE_UNIT * 12}px"     // 48px

    // Helper methods
    fun custom(multiplier: Int): String

    // Directional spacing helpers
    fun padding(
        top: String = "0",
        right: String = "0",
        bottom: String = "0",
        left: String = "0"
    ): String

    fun padding(vertical: String = "0", horizontal: String = "0"): String

    fun margin(
        top: String = "0",
        right: String = "0",
        bottom: String = "0",
        left: String = "0"
    ): String

    fun margin(vertical: String = "0", horizontal: String = "0"): String

    // Composable spacer components
    @Composable
    fun VerticalSpacer(size: String)

    @Composable
    fun HorizontalSpacer(size: String)
}
```

### Description

The `Spacing` object provides a systematic approach to spacing elements in an application, ensuring consistency. It defines standard spacing values and helper methods for creating custom spacing.

### Example

```kotlin
// Using standard spacing values
Box(
    modifier = Modifier
        .padding(Spacing.md)
        .margin(Spacing.sm)
)

// Custom spacing
val customSpacing = Spacing.custom(3) // 12px
Box(
    modifier = Modifier.padding(customSpacing)
)

// Directional spacing
val paddingValue = Spacing.padding(
    top = Spacing.md,
    right = Spacing.lg,
    bottom = Spacing.md,
    left = Spacing.lg
)
Box(
    modifier = Modifier.padding(paddingValue)
)

// Simplified directional spacing
val simplePadding = Spacing.padding(
    vertical = Spacing.sm,
    horizontal = Spacing.md
)
Box(
    modifier = Modifier.padding(simplePadding)
)

// Using theme spacing
Box(
    modifier = Modifier.themePadding("md")
)

// Using spacer components
Column {
    Text("First item")
    Spacing.VerticalSpacer(Spacing.md)
    Text("Second item")
}

Row {
    Text("Left")
    Spacing.HorizontalSpacer(Spacing.lg)
    Text("Right")
}
```

---

## MediaQuery

The `MediaQuery` object provides utilities for responsive design.

### Class Definition

```kotlin
object MediaQuery {
    // Media query methods for different screen sizes
    fun minWidth(breakpoint: Int, styleModifier: Modifier): MediaQueryModifier
    fun maxWidth(breakpoint: Int, styleModifier: Modifier): MediaQueryModifier
    fun minHeight(breakpoint: Int, styleModifier: Modifier): MediaQueryModifier
    fun maxHeight(breakpoint: Int, styleModifier: Modifier): MediaQueryModifier
    fun betweenWidth(minWidth: Int, maxWidth: Int, styleModifier: Modifier): MediaQueryModifier

    // Predefined device type queries
    fun mobile(styleModifier: Modifier): MediaQueryModifier
    fun tablet(styleModifier: Modifier): MediaQueryModifier
    fun desktop(styleModifier: Modifier): MediaQueryModifier

    // Other media features
    fun orientation(isPortrait: Boolean, styleModifier: Modifier): MediaQueryModifier
    fun darkMode(styleModifier: Modifier): MediaQueryModifier
    fun lightMode(styleModifier: Modifier): MediaQueryModifier
    fun reducedMotion(styleModifier: Modifier): MediaQueryModifier
}

// MediaQueryModifier class for applying responsive styles
class MediaQueryModifier(private val query: String, private val styleModifier: Modifier) {
    fun applyTo(baseModifier: Modifier): Modifier
    fun and(vararg others: MediaQueryModifier): MediaQueryModifier
}

// Extension functions for Modifier
fun Modifier.responsive(mediaQueryModifier: MediaQueryModifier): Modifier
fun Modifier.responsive(vararg mediaQueryModifiers: MediaQueryModifier): Modifier
```

### Description

The `MediaQuery` object provides tools for creating responsive layouts that adapt to different screen sizes and device characteristics. It uses CSS media queries to conditionally apply styles based on screen dimensions, orientation, color scheme, and other features.

### Example

```kotlin
// Basic responsive styling
Box(
    modifier = Modifier.responsive(
        MediaQuery.minWidth(768, Modifier.width("50%")),
        MediaQuery.maxWidth(767, Modifier.width("100%"))
    )
)

// Device-specific styling
Text(
    text = "Responsive Text",
    modifier = Modifier.responsive(
        MediaQuery.mobile(Modifier.fontSize("14px")),
        MediaQuery.tablet(Modifier.fontSize("16px")),
        MediaQuery.desktop(Modifier.fontSize("18px"))
    )
)

// Combining media queries
val responsiveStyles = MediaQuery.minWidth(600, Modifier.padding("20px"))
    .and(MediaQuery.darkMode(Modifier.backgroundColor("#333")))

Box(
    modifier = Modifier.responsive(responsiveStyles)
)

// Orientation-specific styling
Image(
    src = "logo.png",
    modifier = Modifier.responsive(
        MediaQuery.orientation(isPortrait = true, Modifier.width("80%")),
        MediaQuery.orientation(isPortrait = false, Modifier.width("40%"))
    )
)

// Dark mode styling
Card(
    modifier = Modifier.responsive(
        MediaQuery.darkMode(
            Modifier
                .backgroundColor("#222")
                .color("#fff")
        ),
        MediaQuery.lightMode(
            Modifier
                .backgroundColor("#fff")
                .color("#222")
        )
    )
)

// Accessibility - reduced motion
Button(
    text = "Click Me",
    modifier = Modifier.responsive(
        MediaQuery.reducedMotion(
            Modifier.transition("none")
        )
    )
)
```

### Using with Composable Effects

For reactive media query state in composable functions, use the `useMediaQuery` effect:

```kotlin
@Composable
fun ResponsiveLayout() {
    val isMobile = useMediaQuery("(max-width: 600px)")
    val isDarkMode = useMediaQuery("(prefers-color-scheme: dark)")

    Column(
        modifier = Modifier.padding(
            if (isMobile) "8px" else "16px"
        ).backgroundColor(
            if (isDarkMode) "#222" else "#fff"
        )
    ) {
        if (isMobile) {
            MobileLayout()
        } else {
            DesktopLayout()
        }
    }
}
```

---

## StyleSheet

The `StyleSheet` object provides a way to define reusable styles.

### Class Definition

```kotlin
object StyleSheet {
    // Define and register styles
    fun defineStyle(name: String, modifier: Modifier)
    fun getStyle(name: String): Modifier
    fun hasStyle(name: String): Boolean
    fun removeStyle(name: String)
    fun clearStyles()

    // Create and register styles in a single call
    fun createStyle(name: String, builder: Modifier.() -> Modifier): Modifier
    fun extendStyle(baseName: String, newName: String, extension: Modifier.() -> Modifier)
}

// StyleBuilder for declarative style definition
class StyleBuilder {
    fun style(name: String, builder: Modifier.() -> Modifier)
    fun extendStyle(name: String, baseName: String, builder: Modifier.() -> Modifier)
    fun registerAll()
}

// Helper function to create a stylesheet
fun createStyleSheet(builder: StyleBuilder.() -> Unit)

// Apply styles using modifier extensions
fun Modifier.applyStyle(styleName: String): Modifier
fun Modifier.applyStyles(vararg styleNames: String): Modifier
```

### Description

The `StyleSheet` object provides a way to define reusable styles that can be applied to components. It helps maintain consistent styling throughout the application and reduces duplication.

### Example

```kotlin
// Define individual styles
StyleSheet.defineStyle(
    "primaryButton", 
    Modifier
        .backgroundColor("#0077cc")
        .color("#ffffff")
        .padding("8px 16px")
        .borderRadius("4px")
)

// Create a style with a builder
StyleSheet.createStyle("card") {
    backgroundColor("#ffffff")
        .padding("16px")
        .borderRadius("8px")
        .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
}

// Extend an existing style
StyleSheet.extendStyle("primaryButton", "largeButton") {
    fontSize("18px")
        .padding("12px 24px")
}

// Check if a style exists
if (StyleSheet.hasStyle("primaryButton")) {
    // Use the style
}

// Create multiple styles with StyleBuilder
createStyleSheet {
    style("heading") {
        fontSize("24px")
            .fontWeight("bold")
            .marginBottom("16px")
    }

    style("paragraph") {
        fontSize("16px")
            .lineHeight("1.5")
            .marginBottom("8px")
    }

    extendStyle("heading", "sectionHeading") {
        color("#333333")
            .borderBottom("1px solid #eeeeee")
            .paddingBottom("8px")
    }
}

// Using the styles in components
Button(
    text = "Click Me",
    modifier = Modifier.applyStyle("primaryButton")
)

Card(
    modifier = Modifier.applyStyle("card")
) {
    Text(
        text = "Section Title",
        modifier = Modifier.applyStyle("sectionHeading")
    )

    Text(
        text = "This is a paragraph of text.",
        modifier = Modifier.applyStyle("paragraph")
    )
}

// Apply multiple styles to a component
Text(
    text = "Styled Text",
    modifier = Modifier.applyStyles("heading", "textCenter", "textPrimary")
)
```
