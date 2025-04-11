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

The `Theme` class is the central container for styling information in a Summon application.

### Class Definition

```kotlin
package code.yousef.summon.theme

class Theme(
    val colors: Colors,
    val typography: Typography,
    val spacing: Spacing,
    val borderRadius: BorderRadius = BorderRadius(),
    val shadows: Shadows = Shadows(),
    val transitions: Transitions = Transitions(),
    val zIndex: ZIndex = ZIndex(),
    val darkMode: Boolean = false
) {
    // Factory method
    companion object {
        fun createDefaultTheme(darkMode: Boolean = false): Theme
        fun createCustomTheme(
            colors: Colors? = null,
            typography: Typography? = null,
            spacing: Spacing? = null,
            borderRadius: BorderRadius? = null,
            shadows: Shadows? = null,
            transitions: Transitions? = null,
            zIndex: ZIndex? = null,
            darkMode: Boolean = false
        ): Theme
    }
}

// Theme components
data class BorderRadius(
    val small: String = "2px",
    val medium: String = "4px",
    val large: String = "8px",
    val xl: String = "12px",
    val xxl: String = "16px",
    val round: String = "50%"
)

data class Shadows(
    val small: String = "0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24)",
    val medium: String = "0 3px 6px rgba(0,0,0,0.15), 0 2px 4px rgba(0,0,0,0.12)",
    val large: String = "0 10px 20px rgba(0,0,0,0.15), 0 3px 6px rgba(0,0,0,0.10)",
    val xl: String = "0 14px 28px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22)",
    val xxl: String = "0 19px 38px rgba(0,0,0,0.30), 0 15px 12px rgba(0,0,0,0.22)"
)

data class Transitions(
    val short: String = "150ms",
    val medium: String = "300ms",
    val long: String = "500ms"
)

data class ZIndex(
    val base: Int = 0,
    val dropdown: Int = 1000,
    val sticky: Int = 1100,
    val fixed: Int = 1200,
    val modalBackdrop: Int = 1300,
    val modal: Int = 1400,
    val popover: Int = 1500,
    val tooltip: Int = 1600
)
```

### Description

The `Theme` class provides a centralized way to define consistent styles across an application.

### Example

```kotlin
// Create a default theme
val defaultTheme = Theme.createDefaultTheme()

// Create a custom theme
val customTheme = Theme.createCustomTheme(
    colors = Colors(
        primary = "#3f51b5",
        secondary = "#f50057",
        background = "#ffffff",
        surface = "#f5f5f5",
        error = "#b00020",
        onPrimary = "#ffffff",
        onSecondary = "#ffffff",
        onBackground = "#000000",
        onSurface = "#000000",
        onError = "#ffffff"
    ),
    typography = Typography(
        fontFamily = "Roboto, sans-serif",
        h1 = TextStyle(
            fontSize = "2.5rem",
            fontWeight = 300,
            lineHeight = 1.2
        ),
        // ... other text styles
    ),
    spacing = Spacing(
        unit = 8,
        xs = 1,
        sm = 2,
        md = 3,
        lg = 4,
        xl = 5
    ),
    darkMode = true
)
```

---

## ThemeManager

The `ThemeManager` class handles theme switching and provides the current theme to components.

### Class Definition

```kotlin
package code.yousef.summon.theme

object ThemeManager {
    val currentTheme: Theme
    
    fun setTheme(theme: Theme)
    fun toggleDarkMode()
    
    @Composable
    fun ThemeProvider(
        theme: Theme = currentTheme,
        content: @Composable () -> Unit
    )
}
```

### Description

The `ThemeManager` provides a way to change themes at runtime and context for components to access the current theme.

### Example

```kotlin
// Set a theme
ThemeManager.setTheme(Theme.createDefaultTheme(darkMode = true))

// Toggle between light and dark mode
Button(
    text = "Toggle Dark Mode",
    onClick = {
        ThemeManager.toggleDarkMode()
    }
)

// Provide theme to components
@Composable
fun App() {
    ThemeManager.ThemeProvider {
        // Components will have access to the current theme
        AppContent()
    }
}
```

---

## Colors

The `Colors` class defines the color palette for a theme.

### Class Definition

```kotlin
package code.yousef.summon.theme

data class Colors(
    // Brand colors
    val primary: String,
    val secondary: String,
    val tertiary: String? = null,
    
    // Surface colors
    val background: String,
    val surface: String,
    
    // State colors
    val error: String,
    val warning: String? = null,
    val success: String? = null,
    val info: String? = null,
    
    // On colors (text/icon colors on top of the above surfaces)
    val onPrimary: String,
    val onSecondary: String,
    val onTertiary: String? = null,
    val onBackground: String,
    val onSurface: String,
    val onError: String,
    val onWarning: String? = null,
    val onSuccess: String? = null,
    val onInfo: String? = null,
    
    // Additional colors
    val divider: String? = null,
    val disabled: String? = null
) {
    companion object {
        fun createDefaultLightColors(): Colors
        fun createDefaultDarkColors(): Colors
    }
}

// Color system provides utility functions for working with colors
object ColorSystem {
    fun darken(color: String, amount: Float): String
    fun lighten(color: String, amount: Float): String
    fun alpha(color: String, amount: Float): String
    fun complementary(color: String): String
    fun contrast(color: String): String
}
```

### Description

The `Colors` class provides a structured way to organize colors in a theme and ensure consistent usage throughout an application.

### Example

```kotlin
// Create a custom color scheme
val colors = Colors(
    primary = "#3f51b5",
    secondary = "#f50057",
    background = "#ffffff",
    surface = "#f5f5f5",
    error = "#b00020",
    onPrimary = "#ffffff",
    onSecondary = "#ffffff",
    onBackground = "#000000",
    onSurface = "#000000",
    onError = "#ffffff",
    success = "#4caf50",
    warning = "#ff9800",
    info = "#2196f3",
    onSuccess = "#ffffff",
    onWarning = "#000000",
    onInfo = "#ffffff"
)

// Use color utilities
val darkerPrimary = ColorSystem.darken(colors.primary, 0.2f)
val lighterSecondary = ColorSystem.lighten(colors.secondary, 0.1f)
val primaryWithOpacity = ColorSystem.alpha(colors.primary, 0.7f)
val contrastColor = ColorSystem.contrast(colors.primary)
```

---

## Typography

The `Typography` class defines text styles for a theme.

### Class Definition

```kotlin
package code.yousef.summon.theme

data class Typography(
    val fontFamily: String = "system-ui, -apple-system, sans-serif",
    val h1: TextStyle = TextStyle(),
    val h2: TextStyle = TextStyle(),
    val h3: TextStyle = TextStyle(),
    val h4: TextStyle = TextStyle(),
    val h5: TextStyle = TextStyle(),
    val h6: TextStyle = TextStyle(),
    val subtitle1: TextStyle = TextStyle(),
    val subtitle2: TextStyle = TextStyle(),
    val body1: TextStyle = TextStyle(),
    val body2: TextStyle = TextStyle(),
    val button: TextStyle = TextStyle(),
    val caption: TextStyle = TextStyle(),
    val overline: TextStyle = TextStyle()
) {
    companion object {
        fun createDefaultTypography(): Typography
    }
}

data class TextStyle(
    val fontSize: String? = null,
    val fontWeight: Int? = null,
    val lineHeight: Any? = null,
    val letterSpacing: String? = null,
    val textTransform: String? = null,
    val fontStyle: String? = null
)
```

### Description

The `Typography` class provides consistent text styling across an application with predefined styles for different text elements.

### Example

```kotlin
// Create a custom typography
val typography = Typography(
    fontFamily = "'Roboto', sans-serif",
    h1 = TextStyle(
        fontSize = "2.5rem",
        fontWeight = 300,
        lineHeight = 1.2
    ),
    h2 = TextStyle(
        fontSize = "2rem",
        fontWeight = 400,
        lineHeight = 1.3
    ),
    body1 = TextStyle(
        fontSize = "1rem",
        fontWeight = 400,
        lineHeight = 1.5
    ),
    button = TextStyle(
        fontSize = "0.875rem",
        fontWeight = 500,
        lineHeight = 1.75,
        letterSpacing = "0.02em",
        textTransform = "uppercase"
    )
)

// Using typography in components
Text(
    text = "Heading",
    modifier = Modifier.applyTextStyle(ThemeManager.currentTheme.typography.h1)
)

Text(
    text = "Body text",
    modifier = Modifier.applyTextStyle(ThemeManager.currentTheme.typography.body1)
)
```

---

## Spacing

The `Spacing` class defines consistent spacing values for a theme.

### Class Definition

```kotlin
package code.yousef.summon.theme

data class Spacing(
    val unit: Int = 8,
    val xs: Int = 1,  // 8px
    val sm: Int = 2,  // 16px
    val md: Int = 3,  // 24px
    val lg: Int = 4,  // 32px
    val xl: Int = 5   // 40px
) {
    // Convert to pixel values
    val xsPx: String
        get() = "${xs * unit}px"
    
    val smPx: String
        get() = "${sm * unit}px"
    
    val mdPx: String
        get() = "${md * unit}px"
    
    val lgPx: String
        get() = "${lg * unit}px"
    
    val xlPx: String
        get() = "${xl * unit}px"
    
    // Helper method to get custom spacing
    fun custom(multiplier: Int): String = "${multiplier * unit}px"
    fun custom(multiplier: Float): String = "${(multiplier * unit).toInt()}px"
}
```

### Description

The `Spacing` class provides a systematic approach to spacing elements in an application, ensuring consistency.

### Example

```kotlin
// Create a custom spacing system
val spacing = Spacing(
    unit = 4,  // 4px base unit
    xs = 1,    // 4px
    sm = 3,    // 12px
    md = 6,    // 24px
    lg = 12,   // 48px
    xl = 24    // 96px
)

// Using spacing in components
Box(
    modifier = Modifier
        .padding(ThemeManager.currentTheme.spacing.mdPx)
        .margin(ThemeManager.currentTheme.spacing.smPx)
)

// Custom spacing
val customSpacing = ThemeManager.currentTheme.spacing.custom(1.5f)
Box(
    modifier = Modifier.padding(customSpacing)
)
```

---

## MediaQuery

The `MediaQuery` class provides utilities for responsive design.

### Class Definition

```kotlin
package code.yousef.summon.theme

object MediaQuery {
    // Predefined breakpoints
    val xs: String = "(max-width: 599px)"
    val sm: String = "(min-width: 600px) and (max-width: 959px)"
    val md: String = "(min-width: 960px) and (max-width: 1279px)"
    val lg: String = "(min-width: 1280px) and (max-width: 1919px)"
    val xl: String = "(min-width: 1920px)"
    
    // Custom media query
    fun custom(query: String): String
    
    // Media query state
    @Composable
    fun useMediaQuery(query: String): Boolean
}

// Responsive components
@Composable
fun Responsive(
    xs: (@Composable () -> Unit)? = null,
    sm: (@Composable () -> Unit)? = null,
    md: (@Composable () -> Unit)? = null,
    lg: (@Composable () -> Unit)? = null,
    xl: (@Composable () -> Unit)? = null
)

// Hide components based on breakpoints
@Composable
fun Hide(
    xs: Boolean = false,
    sm: Boolean = false,
    md: Boolean = false,
    lg: Boolean = false,
    xl: Boolean = false,
    content: @Composable () -> Unit
)
```

### Description

The `MediaQuery` provides tools for creating responsive layouts that adapt to different screen sizes.

### Example

```kotlin
// Check media query
@Composable
fun ResponsiveLayout() {
    val isMobile = MediaQuery.useMediaQuery(MediaQuery.xs)
    val isTablet = MediaQuery.useMediaQuery(MediaQuery.sm)
    
    Column(
        modifier = Modifier.padding(
            if (isMobile) "8px" else "16px"
        )
    ) {
        if (isMobile) {
            MobileLayout()
        } else if (isTablet) {
            TabletLayout()
        } else {
            DesktopLayout()
        }
    }
}

// Using responsive component
@Composable
fun FlexibleContent() {
    Responsive(
        xs = { 
            // Small mobile layout with single column
            SingleColumnLayout() 
        },
        sm = { 
            // Tablet layout with two columns
            TwoColumnLayout() 
        },
        md = { 
            // Desktop layout with three columns
            ThreeColumnLayout() 
        },
        lg = { 
            // Large desktop layout with four columns
            FourColumnLayout() 
        }
    )
}

// Hide components based on breakpoints
@Composable
fun AdaptiveNavigation() {
    // Show drawer on mobile, hide on larger screens
    Hide(sm = true, md = true, lg = true, xl = true) {
        NavigationDrawer()
    }
    
    // Hide navbar on mobile, show on larger screens
    Hide(xs = true) {
        NavigationBar()
    }
}
```

---

## StyleSheet

The `StyleSheet` class provides a way to define reusable styles.

### Class Definition

```kotlin
package code.yousef.summon.theme

class StyleSheet {
    // Add style definitions
    fun style(name: String, vararg properties: Pair<String, String>)
    fun style(name: String, modifier: Modifier)
    
    // Get a style by name
    fun getStyle(name: String): Modifier
    
    // Add class-conditional styles
    fun styleVariants(name: String, variantsBuilder: VariantsBuilder.() -> Unit)
    
    // Define keyframes animations
    fun keyframes(name: String, builder: KeyframesBuilder.() -> Unit)
}

// Helper function to create a stylesheet
fun createStyleSheet(builder: StyleSheet.() -> Unit): StyleSheet

// Apply styles using modifier
fun Modifier.applyStyle(styleName: String): Modifier
fun Modifier.applyVariant(styleName: String, variantName: String): Modifier
```

### Description

The `StyleSheet` class provides a way to define reusable styles that can be applied to components.

### Example

```kotlin
// Create a stylesheet
val styleSheet = createStyleSheet {
    // Simple style
    style("button", 
        "background-color" to "#0077cc",
        "color" to "#ffffff",
        "padding" to "8px 16px",
        "border-radius" to "4px"
    )
    
    // Using modifiers
    style("card", Modifier
        .backgroundColor("#ffffff")
        .padding("16px")
        .borderRadius("8px")
        .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
    )
    
    // Style variants
    styleVariants("button") {
        variant("primary") {
            "background-color" to "#0077cc"
            "color" to "#ffffff"
        }
        variant("secondary") {
            "background-color" to "#6c757d"
            "color" to "#ffffff"
        }
        variant("danger") {
            "background-color" to "#dc3545"
            "color" to "#ffffff"
        }
    }
    
    // Keyframes animation
    keyframes("fadeIn") {
        from {
            "opacity" to "0"
        }
        to {
            "opacity" to "1"
        }
    }
}

// Using the styles
Button(
    text = "Click Me",
    modifier = Modifier.applyStyle("button")
)

Button(
    text = "Primary Button",
    modifier = Modifier.applyVariant("button", "primary")
)

Card(
    modifier = Modifier.applyStyle("card")
) {
    Text("This is a styled card")
}
``` 