# Theme API Reference

This document provides detailed information about the theming system in the Summon library. The theme system provides
consistent colors, typography, spacing, and visual design elements across your application.

## Table of Contents

- [Theme System Overview](#theme-system-overview)
- [Color System](#color-system)
- [Typography](#typography)
- [Spacing System](#spacing-system)
- [Theme Configuration](#theme-configuration)
- [Predefined Themes](#predefined-themes)
- [Theme Extensions](#theme-extensions)
- [Usage Examples](#usage-examples)

---

## Theme System Overview

The Summon theme system consists of several interconnected components:

- **ColorSystem**: Manages color palettes with light/dark mode support
- **Typography**: Defines text styles and type scales
- **Spacing**: Provides consistent spacing values
- **Theme**: Orchestrates all theme elements together

### Core Theme Object

```kotlin
object Theme {
    data class ThemeConfig(
        val colorPalette: ColorSystem.ColorPalette = ColorSystem.default,
        val typography: Map<String, TextStyle> = emptyMap(),
        val spacing: Map<String, String> = emptyMap(),
        val borderRadius: Map<String, String> = emptyMap(),
        val elevation: Map<String, String> = emptyMap(),
        val customValues: Map<String, String> = emptyMap(),
        val typographyTheme: TypographyTheme = TypographyTheme(),
        val spacingTheme: SpacingTheme = SpacingTheme(),
        val borderRadiusTheme: BorderRadiusTheme = BorderRadiusTheme(),
        val elevationTheme: ElevationTheme = ElevationTheme()
    )
}
```

---

## Color System

The ColorSystem provides semantic color names with automatic light/dark mode support.

### Color Palette Structure

```kotlin
object ColorSystem {
    enum class ThemeMode { LIGHT, DARK, SYSTEM }

    data class ColorPalette(
        val light: Map<String, String>,
        val dark: Map<String, String>
    ) {
        fun forMode(themeMode: ThemeMode): Map<String, String>
    }
}
```

### Semantic Color Names

The default color palette includes these semantic colors:

**Backgrounds:**

- `background` - Primary background color
- `surface` - Surface/card backgrounds
- `surfaceVariant` - Alternative surface color

**Text:**

- `onBackground` - Text on background
- `onSurface` - Text on surface
- `onSurfaceVariant` - Secondary text on surface
- `disabled` - Disabled text color

**Brand Colors:**

- `primary` - Primary brand color
- `primaryVariant` - Primary color variant
- `onPrimary` - Text on primary color
- `secondary` - Secondary brand color
- `onSecondary` - Text on secondary color

**Status Colors:**

- `error` - Error states
- `warning` - Warning states
- `success` - Success states
- `info` - Information states

### Usage Examples

```kotlin
// Get colors
val primaryColor = Theme.getColor("primary")
val backgroundColor = Theme.getColor("background", ColorSystem.ThemeMode.DARK)

// Apply to modifiers
modifier.themeColor("primary")
modifier.themeBackgroundColor("surface")
modifier.themeStyleBorder("1px", "solid", "primary")

// Set theme mode
ColorSystem.setThemeMode(ColorSystem.ThemeMode.DARK)
```

### Predefined Color Palettes

```kotlin
object ColorSystem {
    val default: ColorPalette  // Default blue-based palette
    val blue: ColorPalette     // Blue theme
    val green: ColorPalette    // Green theme
    val purple: ColorPalette   // Purple theme
}
```

---

## Typography

The typography system provides consistent text styles with both string-based and type-safe properties.

### TextStyle Definition

```kotlin
data class TextStyle(
    // String-based properties (backward compatibility)
    val fontFamily: String? = null,
    val fontSize: String? = null,
    val fontWeight: String? = null,
    val fontStyle: String? = null,
    val color: String? = null,
    val textDecoration: String? = null,
    val lineHeight: String? = null,
    val letterSpacing: String? = null,

    // Type-safe properties
    val fontWeightEnum: FontWeight? = null,
    val fontSizeNumber: Number? = null,
    val fontSizeUnit: String? = null,
    val lineHeightNumber: Number? = null,
    val letterSpacingNumber: Number? = null,
    val letterSpacingUnit: String? = null,
    val colorValue: Color? = null
) {
    companion object {
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
        ): TextStyle
    }
}
```

### Typography Theme

```kotlin
data class TypographyTheme(
    val h1: TextStyle = TextStyle.create(fontSize = 2.5, fontWeight = FontWeight.Bold),
    val h2: TextStyle = TextStyle.create(fontSize = 2.0, fontWeight = FontWeight.Bold),
    val h3: TextStyle = TextStyle.create(fontSize = 1.75, fontWeight = FontWeight.Bold),
    val h4: TextStyle = TextStyle.create(fontSize = 1.5, fontWeight = FontWeight.Bold),
    val h5: TextStyle = TextStyle.create(fontSize = 1.25, fontWeight = FontWeight.Bold),
    val h6: TextStyle = TextStyle.create(fontSize = 1.0, fontWeight = FontWeight.Bold),
    val subtitle: TextStyle = TextStyle.create(fontSize = 1.1, fontWeight = FontWeight.Medium),
    val body: TextStyle = TextStyle.create(fontSize = 1.0, fontWeight = FontWeight.Normal),
    val bodyLarge: TextStyle = TextStyle.create(fontSize = 1.1, fontWeight = FontWeight.Normal),
    val bodySmall: TextStyle = TextStyle.create(fontSize = 0.9, fontWeight = FontWeight.Normal),
    val caption: TextStyle = TextStyle.create(fontSize = 0.8, fontWeight = FontWeight.Normal),
    val button: TextStyle = TextStyle.create(fontSize = 1.0, fontWeight = FontWeight.Medium),
    val overline: TextStyle = TextStyle.create(fontSize = 0.75, fontWeight = FontWeight.SemiBold, letterSpacing = 0.05),
    val link: TextStyle = TextStyle.create(fontSize = 1.0, fontWeight = FontWeight.Normal, textDecoration = "underline"),
    val code: TextStyle = TextStyle.create(fontSize = 0.9, fontFamily = "monospace")
)
```

### Usage Examples

```kotlin
// Get text styles
val headingStyle = Theme.getTextStyle("h1")
val bodyStyle = Theme.getTextStyle("body")

// Apply to modifiers
modifier.themeTextStyle("h2")

// Access typed typography theme
val typography = Theme.getTypographyTheme()
val h1Style = typography.h1
```

---

## Spacing System

The Spacing system provides consistent spacing values based on a 4px base unit.

### Spacing Object

```kotlin
object Spacing {
    private const val BASE_UNIT = 4

    const val xs = "4px"    // BASE_UNIT
    const val sm = "8px"    // BASE_UNIT * 2
    const val md = "16px"   // BASE_UNIT * 4 (default)
    const val lg = "24px"   // BASE_UNIT * 6
    const val xl = "32px"   // BASE_UNIT * 8
    const val xxl = "48px"  // BASE_UNIT * 12

    fun custom(multiplier: Int): String = "${BASE_UNIT * multiplier}px"

    fun padding(top: String = "0", right: String = "0", bottom: String = "0", left: String = "0"): String
    fun padding(vertical: String = "0", horizontal: String = "0"): String
    fun margin(top: String = "0", right: String = "0", bottom: String = "0", left: String = "0"): String
    fun margin(vertical: String = "0", horizontal: String = "0"): String
}
```

### Spacing Theme

```kotlin
data class SpacingTheme(
    val xs: String = Spacing.xs,    // 4px
    val sm: String = Spacing.sm,    // 8px
    val md: String = Spacing.md,    // 16px
    val lg: String = Spacing.lg,    // 24px
    val xl: String = Spacing.xl,    // 32px
    val xxl: String = Spacing.xxl   // 48px
)
```

### Spacing Components

```kotlin
// Spacer components
@Composable
fun Spacer(modifier: Modifier)

// Convenience spacers
@Composable
fun HorizontalSpacerXS()
@Composable
fun HorizontalSpacerS()
@Composable
fun HorizontalSpacerM()
@Composable
fun VerticalSpacerXS()
@Composable
fun VerticalSpacerS()
@Composable
fun VerticalSpacerM()

// Spacing utilities
@Composable
fun Spacing.verticalSpace(size: String = md)
@Composable
fun Spacing.horizontalSpace(size: String = md)
```

### Usage Examples

```kotlin
// Direct spacing values
val smallPadding = Spacing.sm
val customSpacing = Spacing.custom(3) // 12px

// Spacing utilities
val padding = Spacing.padding("8px", "16px")
val margin = Spacing.margin(vertical = "12px", horizontal = "24px")

// Apply to modifiers
modifier.themePadding("md")
modifier.themeMargin("lg")
modifier.spacingPadding(Spacing.xl)

// Spacer components
Spacer(Modifier().height(Spacing.md))
VerticalSpacerM()
```

---

## Theme Configuration

### Border Radius & Elevation

```kotlin
data class BorderRadiusTheme(
    val none: String = "0",
    val sm: String = "4px",
    val md: String = "8px",
    val lg: String = "16px",
    val xl: String = "24px",
    val pill: String = "9999px",
    val circle: String = "50%"
)

data class ElevationTheme(
    val none: String = "none",
    val xs: String = "0 1px 2px rgba(0, 0, 0, 0.05)",
    val sm: String = "0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px rgba(0, 0, 0, 0.06)",
    val md: String = "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)",
    val lg: String = "0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)",
    val xl: String = "0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)",
    val xxl: String = "0 25px 50px -12px rgba(0, 0, 0, 0.25)"
)
```

### Theme Management

```kotlin
// Set active theme
Theme.setTheme(Theme.Themes.dark)

// Get current theme
val currentTheme = Theme.getTheme()

// Access theme values
val primaryColor = Theme.getColor("primary")
val headingStyle = Theme.getTextStyle("h1")
val mediumSpacing = Theme.getSpacing("md")
val roundedCorners = Theme.getBorderRadius("md")
val cardElevation = Theme.getElevation("sm")
val customValue = Theme.getCustomValue("myCustomProperty", "defaultValue")
```

---

## Predefined Themes

```kotlin
object Theme.Themes {
    val light: ThemeConfig      // Default light theme
    val dark: ThemeConfig       // Default dark theme
    val blue: ThemeConfig       // Blue color theme
    val green: ThemeConfig      // Green color theme
    val purple: ThemeConfig     // Purple color theme
}
```

### Usage Examples

```kotlin
// Switch to dark theme
Theme.setTheme(Theme.Themes.dark)

// Switch to colored theme
Theme.setTheme(Theme.Themes.green)

// Use system preference
ColorSystem.setThemeMode(ColorSystem.ThemeMode.SYSTEM)
```

---

## Theme Extensions

### Modifier Extensions

The theme system provides convenient modifier extensions:

```kotlin
// Color extensions
fun Modifier.themeColor(colorName: String, themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()): Modifier
fun Modifier.themeBackgroundColor(colorName: String, themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()): Modifier
fun Modifier.themeStyleBorder(width: String, style: String, colorName: String, themeMode: ColorSystem.ThemeMode = ColorSystem.getThemeMode()): Modifier

// Typography extensions
fun Modifier.themeTextStyle(styleName: String): Modifier

// Spacing extensions
fun Modifier.themePadding(spacingName: String): Modifier
fun Modifier.themeMargin(spacingName: String): Modifier
fun Modifier.themePadding(top: String? = null, right: String? = null, bottom: String? = null, left: String? = null): Modifier
fun Modifier.themeMargin(top: String? = null, right: String? = null, bottom: String? = null, left: String? = null): Modifier

// Other extensions
fun Modifier.themeBorderRadius(radiusName: String): Modifier
fun Modifier.themeElevation(elevationName: String): Modifier

// Spacing modifier extensions
fun Modifier.spacingPadding(value: String): Modifier
fun Modifier.spacingPadding(top: String, right: String, bottom: String, left: String): Modifier
fun Modifier.spacingPaddingHorizontal(value: String): Modifier
fun Modifier.spacingPaddingVertical(value: String): Modifier
fun Modifier.spacingMargin(value: String): Modifier
fun Modifier.spacingMarginHorizontal(value: String): Modifier
fun Modifier.spacingMarginVertical(value: String): Modifier
```

### Custom Theme Creation

```kotlin
// Create custom theme extending existing theme
val customTheme = Theme.createTheme(Theme.Themes.light) {
    copy(
        customValues = customValues + ("brandColor" to "#FF6B35"),
        typography = typography + ("brandHeading" to TextStyle.create(
            fontSize = 2.2,
            fontWeight = FontWeight.Bold,
            color = Color.hex("#FF6B35")
        ))
    )
}

// Apply custom theme
Theme.setTheme(customTheme)
```

---

## Usage Examples

### Complete Theme Setup

```kotlin
@Composable
fun ThemedApp() {
    // Set up theme
    Theme.setTheme(Theme.Themes.dark)
    ColorSystem.setThemeMode(ColorSystem.ThemeMode.SYSTEM)

    // Use themed components
    Column(
        modifier = Modifier()
            .themeBackgroundColor("background")
            .themePadding("lg")
    ) {
        Text(
            text = "Welcome",
            modifier = Modifier()
                .themeTextStyle("h1")
                .themeColor("onBackground")
        )

        VerticalSpacerM()

        Button(
            onClick = { },
            label = "Get Started",
            modifier = Modifier()
                .themeBackgroundColor("primary")
                .themeColor("onPrimary")
                .themeBorderRadius("md")
                .themeElevation("sm")
        )
    }
}
```

### Responsive Theming

```kotlin
@Composable
fun ResponsiveThemedComponent() {
    val typography = Theme.getTypographyTheme()
    val spacing = Theme.getSpacingTheme()

    Column {
        Text(
            text = "Heading",
            modifier = Modifier().themeTextStyle("h2")
        )

        Spacer(Modifier().height(spacing.md))

        Text(
            text = "Body text with theme-aware styling",
            modifier = Modifier()
                .themeTextStyle("body")
                .spacingPaddingHorizontal(spacing.sm)
        )
    }
}
```

### Dynamic Theme Switching

```kotlin
@Composable
fun ThemeSwitcher() {
    var isDark by remember { mutableStateOf(false) }

    Button(
        onClick = {
            isDark = !isDark
            val newTheme = if (isDark) Theme.Themes.dark else Theme.Themes.light
            Theme.setTheme(newTheme)
        },
        label = if (isDark) "Light Mode" else "Dark Mode",
        modifier = Modifier()
            .themeBackgroundColor("primary")
            .themeColor("onPrimary")
    )
}
```

## Best Practices

1. **Use semantic color names**: Prefer `"primary"` over hex colors for consistency
2. **Leverage spacing system**: Use predefined spacing values for visual harmony
3. **Apply theme extensions**: Use modifier extensions for clean, readable code
4. **Support theme modes**: Design for both light and dark modes
5. **Create custom themes sparingly**: Extend existing themes rather than creating from scratch
6. **Test theme switching**: Ensure your UI works with all theme modes
7. **Use typed properties**: Prefer type-safe TextStyle.create() over string-based styles

## Migration Notes

When upgrading theme usage:

- Replace hardcoded colors with semantic theme colors
- Update spacing to use the consistent spacing system
- Use theme modifier extensions for cleaner code
- Test with different theme modes (light/dark/system)

## See Also

- [Components API](components.md) - Using themes with UI components
- [Modifier API](modifier.md) - Styling system that integrates with themes
- [Color API](color.md) - Color system and utilities
- [State API](state.md) - Managing theme state in your application