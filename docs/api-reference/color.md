# Color API Reference

This document provides detailed information about the Color API in the Summon library.

## Table of Contents

- [Color](#color)
- [Color Creation](#color-creation)
- [Color Extensions](#color-extensions)
- [Color Presets](#color-presets)
- [Material Design 3 Colors](#material-design-3-colors)
- [Catppuccin Colors](#catppuccin-colors)

---

## Color

The `Color` class represents an RGBA color in the Summon library.

### Class Definition

```kotlin
class Color(val value: UInt) {
    val red: Int
    val green: Int
    val blue: Int
    val alpha: Int
    val alphaFloat: Float

    fun withAlpha(alpha: Float): Color
    fun toRgbaString(): String
    fun toHexString(): String

    override fun toString(): String
}
```

### Description

`Color` is the main class for representing colors in Summon. It stores the color as a 32-bit unsigned integer with 8 bits each for red, green, blue, and alpha components.

### Example

```kotlin
// Create a color with RGB values
val red = Color.rgb(255, 0, 0)

// Create a color with RGBA values using int alpha (0-255)
val transparentBlue1 = Color.rgba(0, 0, 255, 128)

// Create a color with RGBA values using float alpha (0.0-1.0)
val transparentBlue2 = Color.rgba(0, 0, 255, 0.5f)

// Create a color from a hex string
val green = Color.fromHex("#00FF00")

// Get color components
println("Red: ${red.red}, Green: ${red.green}, Blue: ${red.blue}, Alpha: ${red.alpha}")
println("Alpha as float: ${red.alphaFloat}") // 1.0

// Convert to strings
println(red.toRgbaString()) // "rgba(255, 0, 0, 1.0)"
println(red.toHexString())  // "#FF0000FF"
```

---

## Color Creation

Summon provides several ways to create colors:

### Static Methods

```kotlin
// Create a color from RGB values (0-255) with alpha=255
fun Color.Companion.rgb(red: Int, green: Int, blue: Int): Color

// Create a color from RGBA values (0-255) with alpha as int (0-255)
fun Color.Companion.rgba(red: Int, green: Int, blue: Int, alpha: Int): Color

// Create a color from RGBA values (0-255) with alpha as float (0.0-1.0)
fun Color.Companion.rgba(red: Int, green: Int, blue: Int, alpha: Float): Color

// Create a color from a CSS hex string (#RRGGBB or #RRGGBBAA)
fun Color.Companion.fromHex(hex: String): Color
```

### Extension Properties

```kotlin
// Create a color using RGB values
val Color.Companion.rgb: RGB

// Create a color using RGBA values
val Color.Companion.rgba: RGBA

// Create a color from a hex string
val Color.Companion.hex: HEX
```

### Example

```kotlin
// Using static methods
val red1 = Color.rgb(255, 0, 0)
val blue1 = Color.rgba(0, 0, 255, 255)      // Using int alpha (0-255)
val blue1a = Color.rgba(0, 0, 255, 1.0f)    // Using float alpha (0.0-1.0)
val green1 = Color.fromHex("#00FF00")

// Using extension properties
val red2 = Color.rgb(255, 0, 0)
val blue2 = Color.rgba(0, 0, 255, 255)      // Using int alpha (0-255)
val blue2a = Color.rgba(0, 0, 255, 1.0f)    // Using float alpha (0.0-1.0)
val green2 = Color.hex("#00FF00")
```

---

## Color Presets

Summon provides a set of predefined color constants:

### Basic Colors

```kotlin
val Color.Companion.BLACK: Color
val Color.Companion.WHITE: Color
val Color.Companion.RED: Color
val Color.Companion.GREEN: Color
val Color.Companion.BLUE: Color
val Color.Companion.YELLOW: Color
val Color.Companion.CYAN: Color
val Color.Companion.MAGENTA: Color
val Color.Companion.TRANSPARENT: Color

// Additional basic colors
val Color.Companion.GRAY: Color
val Color.Companion.LIGHT_GRAY: Color
val Color.Companion.DARK_GRAY: Color
val Color.Companion.ORANGE: Color
val Color.Companion.PINK: Color
val Color.Companion.PURPLE: Color
val Color.Companion.BROWN: Color
val Color.Companion.NAVY: Color
val Color.Companion.TEAL: Color
val Color.Companion.OLIVE: Color
val Color.Companion.MAROON: Color
val Color.Companion.LIME: Color
val Color.Companion.INDIGO: Color
val Color.Companion.VIOLET: Color
val Color.Companion.SILVER: Color
val Color.Companion.GOLD: Color
```

### Material Design Colors

```kotlin
val Color.Companion.PRIMARY: Color
val Color.Companion.PRIMARY_LIGHT: Color
val Color.Companion.PRIMARY_DARK: Color
val Color.Companion.SECONDARY: Color
val Color.Companion.ERROR: Color
val Color.Companion.WARNING: Color
val Color.Companion.INFO: Color
val Color.Companion.SUCCESS: Color
```

### Example

```kotlin
// Using basic colors
val black = Color.BLACK
val white = Color.WHITE
val transparent = Color.TRANSPARENT

// Using material design colors
val primary = Color.PRIMARY
val error = Color.ERROR
```

---

## Material Design 3 Colors

Summon provides a set of Material Design 3 colors:

```kotlin
val Color.Companion.Material3.PRIMARY: Color
val Color.Companion.Material3.ON_PRIMARY: Color
val Color.Companion.Material3.PRIMARY_CONTAINER: Color
val Color.Companion.Material3.ON_PRIMARY_CONTAINER: Color
val Color.Companion.Material3.SECONDARY: Color
val Color.Companion.Material3.ON_SECONDARY: Color
val Color.Companion.Material3.SECONDARY_CONTAINER: Color
val Color.Companion.Material3.ON_SECONDARY_CONTAINER: Color
val Color.Companion.Material3.TERTIARY: Color
val Color.Companion.Material3.ON_TERTIARY: Color
val Color.Companion.Material3.TERTIARY_CONTAINER: Color
val Color.Companion.Material3.ON_TERTIARY_CONTAINER: Color
val Color.Companion.Material3.ERROR: Color
val Color.Companion.Material3.ON_ERROR: Color
val Color.Companion.Material3.ERROR_CONTAINER: Color
val Color.Companion.Material3.ON_ERROR_CONTAINER: Color
val Color.Companion.Material3.BACKGROUND: Color
val Color.Companion.Material3.ON_BACKGROUND: Color
val Color.Companion.Material3.SURFACE: Color
val Color.Companion.Material3.ON_SURFACE: Color
val Color.Companion.Material3.SURFACE_VARIANT: Color
val Color.Companion.Material3.ON_SURFACE_VARIANT: Color
val Color.Companion.Material3.OUTLINE: Color
val Color.Companion.Material3.OUTLINE_VARIANT: Color
val Color.Companion.Material3.SCRIM: Color
```

### Example

```kotlin
// Using Material Design 3 colors
val primary = Color.Material3.PRIMARY
val onPrimary = Color.Material3.ON_PRIMARY
val surface = Color.Material3.SURFACE
```

---

## Catppuccin Colors

Summon provides Catppuccin color palettes in both light (Latte) and dark (Mocha) variants:

### Latte (Light) Theme

```kotlin
val Color.Companion.Catppuccin.Latte.ROSEWATER: Color
val Color.Companion.Catppuccin.Latte.FLAMINGO: Color
val Color.Companion.Catppuccin.Latte.PINK: Color
val Color.Companion.Catppuccin.Latte.MAUVE: Color
val Color.Companion.Catppuccin.Latte.RED: Color
val Color.Companion.Catppuccin.Latte.MAROON: Color
val Color.Companion.Catppuccin.Latte.PEACH: Color
val Color.Companion.Catppuccin.Latte.YELLOW: Color
val Color.Companion.Catppuccin.Latte.GREEN: Color
val Color.Companion.Catppuccin.Latte.TEAL: Color
val Color.Companion.Catppuccin.Latte.SKY: Color
val Color.Companion.Catppuccin.Latte.SAPPHIRE: Color
val Color.Companion.Catppuccin.Latte.BLUE: Color
val Color.Companion.Catppuccin.Latte.LAVENDER: Color
val Color.Companion.Catppuccin.Latte.TEXT: Color
val Color.Companion.Catppuccin.Latte.SUBTEXT1: Color
val Color.Companion.Catppuccin.Latte.SUBTEXT0: Color
val Color.Companion.Catppuccin.Latte.OVERLAY2: Color
val Color.Companion.Catppuccin.Latte.OVERLAY1: Color
val Color.Companion.Catppuccin.Latte.OVERLAY0: Color
val Color.Companion.Catppuccin.Latte.SURFACE2: Color
val Color.Companion.Catppuccin.Latte.SURFACE1: Color
val Color.Companion.Catppuccin.Latte.SURFACE0: Color
val Color.Companion.Catppuccin.Latte.BASE: Color
val Color.Companion.Catppuccin.Latte.MANTLE: Color
val Color.Companion.Catppuccin.Latte.CRUST: Color
```

### Mocha (Dark) Theme

```kotlin
val Color.Companion.Catppuccin.Mocha.ROSEWATER: Color
val Color.Companion.Catppuccin.Mocha.FLAMINGO: Color
val Color.Companion.Catppuccin.Mocha.PINK: Color
val Color.Companion.Catppuccin.Mocha.MAUVE: Color
val Color.Companion.Catppuccin.Mocha.RED: Color
val Color.Companion.Catppuccin.Mocha.MAROON: Color
val Color.Companion.Catppuccin.Mocha.PEACH: Color
val Color.Companion.Catppuccin.Mocha.YELLOW: Color
val Color.Companion.Catppuccin.Mocha.GREEN: Color
val Color.Companion.Catppuccin.Mocha.TEAL: Color
val Color.Companion.Catppuccin.Mocha.SKY: Color
val Color.Companion.Catppuccin.Mocha.SAPPHIRE: Color
val Color.Companion.Catppuccin.Mocha.BLUE: Color
val Color.Companion.Catppuccin.Mocha.LAVENDER: Color
val Color.Companion.Catppuccin.Mocha.TEXT: Color
val Color.Companion.Catppuccin.Mocha.SUBTEXT1: Color
val Color.Companion.Catppuccin.Mocha.SUBTEXT0: Color
val Color.Companion.Catppuccin.Mocha.OVERLAY2: Color
val Color.Companion.Catppuccin.Mocha.OVERLAY1: Color
val Color.Companion.Catppuccin.Mocha.OVERLAY0: Color
val Color.Companion.Catppuccin.Mocha.SURFACE2: Color
val Color.Companion.Catppuccin.Mocha.SURFACE1: Color
val Color.Companion.Catppuccin.Mocha.SURFACE0: Color
val Color.Companion.Catppuccin.Mocha.BASE: Color
val Color.Companion.Catppuccin.Mocha.MANTLE: Color
val Color.Companion.Catppuccin.Mocha.CRUST: Color
```

### Example

```kotlin
// Using Catppuccin Latte colors
val blue = Color.Catppuccin.Latte.BLUE
val red = Color.Catppuccin.Latte.RED

// Using Catppuccin Mocha colors
val darkBlue = Color.Catppuccin.Mocha.BLUE
val darkRed = Color.Catppuccin.Mocha.RED
```

---

## Material Design Color Palettes

Summon provides comprehensive Material Design color palettes with multiple shade levels:

### Color Palette Structure

Each Material Design color includes shades from 50 (lightest) to 900 (darkest), plus accent variants:

```kotlin
object Red {
    val `50`: Color = Color.fromHex("#FFEBEE")
    val `100`: Color = Color.fromHex("#FFCDD2")
    val `200`: Color = Color.fromHex("#EF9A9A")
    val `300`: Color = Color.fromHex("#E57373")
    val `400`: Color = Color.fromHex("#EF5350")
    val `500`: Color = Color.fromHex("#F44336")  // Primary shade
    val `600`: Color = Color.fromHex("#E53935")
    val `700`: Color = Color.fromHex("#D32F2F")
    val `800`: Color = Color.fromHex("#C62828")
    val `900`: Color = Color.fromHex("#B71C1C")
    
    // Accent shades
    val A100: Color = Color.fromHex("#FF8A80")
    val A200: Color = Color.fromHex("#FF5252")
    val A400: Color = Color.fromHex("#FF1744")
    val A700: Color = Color.fromHex("#D50000")
}
```

### Available Color Palettes

```kotlin
// Primary colors
Color.Red[500]      // Primary red
Color.Pink[500]     // Primary pink
Color.Purple[500]   // Primary purple
Color.DeepPurple[500]
Color.Indigo[500]
Color.Blue[500]
Color.LightBlue[500]
Color.Cyan[500]
Color.Teal[500]
Color.Green[500]
Color.LightGreen[500]
Color.Lime[500]
Color.Yellow[500]
Color.Amber[500]
Color.Orange[500]
Color.DeepOrange[500]

// Neutral colors
Color.Brown[500]
Color.Grey[500]
Color.BlueGrey[500]

// Accessing different shades
val lightBlue = Color.Blue[100]
val primaryBlue = Color.Blue[500]
val darkBlue = Color.Blue[900]
val accentBlue = Color.Blue.A200
```

### Using Material Design Colors

```kotlin
// Building a theme with Material Design colors
Column(
    modifier = Modifier
        .backgroundColor(Color.Blue[50])  // Light background
) {
    // App bar
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .backgroundColor(Color.Blue[700])  // Primary color
            .padding(16.px)
    ) {
        Text(
            "App Title",
            modifier = Modifier.color(Color.WHITE)
        )
    }
    
    // Content area
    Card(
        modifier = Modifier
            .margin(16.px)
            .backgroundColor(Color.WHITE)
            .boxShadow("0", "2px", "4px", "0", Color.Grey[300])
    ) {
        Text(
            "Card content",
            modifier = Modifier
                .padding(16.px)
                .color(Color.Grey[900])  // Dark text
        )
        
        Button(
            onClick = { },
            modifier = Modifier
                .backgroundColor(Color.Blue.A200)  // Accent color
                .color(Color.WHITE)
        ) {
            Text("Action")
        }
    }
}
```

### Material Design Color Utilities

```kotlin
// Check if a color is light or dark
fun Color.isLight(): Boolean
fun Color.isDark(): Boolean

// Get contrasting text color (black or white)
fun Color.contrastingTextColor(): Color

// Blend two colors
fun Color.blend(other: Color, ratio: Float = 0.5f): Color

// Example usage
val backgroundColor = Color.Blue[500]
val textColor = backgroundColor.contrastingTextColor()  // Returns white

val blendedColor = Color.Red[500].blend(Color.Blue[500], 0.3f)  // 30% blue, 70% red
```
