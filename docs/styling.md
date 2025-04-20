# Styling

Summon provides a powerful and intuitive styling system that works across platforms, allowing you to create consistent UIs in both browser and JVM environments.

## Modifier API

The primary way to style components in Summon is through the `Modifier` API, which provides a fluent interface for applying styles:

```kotlin
import code.yousef.summon.modifier.*
import code.yousef.summon.extensions.*

Button(
    text = "Click me",
    onClick = { /* handle click */ },
    modifier = Modifier
        .padding(16.px)
        .backgroundColor("#0077cc")
        .color("#ffffff")
        .borderRadius(4.px)
        .fontWeight(700)
        .hover {
            backgroundColor("#005599")
        }
)
```

## CSS Units

Summon provides convenient extension properties for numbers to create CSS dimension values without needing to add string units manually:

```kotlin
import code.yousef.summon.extensions.*

// General CSS units
val pixels = 16.px        // "16px"
val rootEm = 1.5.rem      // "1.5rem"
val emUnits = 1.2.em      // "1.2em"
val percentage = 50.percent  // "50%"
val viewportWidth = 100.vw   // "100vw"
val viewportHeight = 100.vh  // "100vh"
val viewportMin = 50.vmin    // "50vmin"
val viewportMax = 50.vmax    // "50vmax"

// Font-specific CSS units
val scaleIndependentPixels = 14.sp   // "14sp" - Scale-independent pixels for font sizes
val characterUnits = 2.ch            // "2ch" - Width of the "0" character
val xHeight = 3.ex                   // "3ex" - Height of the letter 'x'
val points = 12.pt                   // "12pt" - Traditional print measurement (1/72 inch)
val picas = 6.pc                     // "6pc" - Traditional print measurement (1pc = 12pt)
```

These extensions can be used with all modifiers that accept CSS dimension values:

```kotlin
Text(
    text = "Styled text with various units",
    modifier = Modifier
        .width(200.px)
        .fontSize(1.2.rem)         // Root relative unit
        .marginTop(10.px)          // Pixels
        .marginBottom(1.5.em)      // Relative to current font size
        .letterSpacing(0.5.ex)     // Based on x-height
        .lineHeight(2.ch)          // Based on character width
)
```

## Individual Property Modifiers

Summon supports applying individual properties for margin and padding:

```kotlin
Modifier
    // Individual margin properties
    .marginTop(8.px)      // Only apply to top margin
    .marginRight(16.px)   // Only apply to right margin
    .marginBottom(8.px)   // Only apply to bottom margin
    .marginLeft(16.px)    // Only apply to left margin

    // Individual padding properties
    .paddingTop(8.px)     // Only apply to top padding
    .paddingRight(16.px)  // Only apply to right padding
    .paddingBottom(8.px)  // Only apply to bottom padding
    .paddingLeft(16.px)   // Only apply to left padding
```

### Layout Modifiers

```kotlin
Modifier
    // Size modifiers
    .width(200.px)
    .height(100.px)
    .size(200.px, 100.px)
    .maxWidth(500.px)
    .minHeight(50.px)

    // Margin and padding
    .margin(8.px)
    .margin(8.px, 16.px) // vertical, horizontal
    .margin(8.px, 16.px, 8.px, 16.px) // top, right, bottom, left
    .padding(8.px)
    .padding(8.px, 16.px) // vertical, horizontal
    .padding(8.px, 16.px, 8.px, 16.px) // top, right, bottom, left

    // Auto margins for centering
    .marginAuto() // center in all directions
    .marginHorizontalAutoZero() // center horizontally (no parameters needed)
    .marginHorizontalAuto() // center horizontally only (no top/bottom margins)
    .marginVerticalAutoZero() // center vertically (no parameters needed)
    .marginVerticalAuto() // center vertically only (no left/right margins)
    .marginHorizontalAuto(20.px) // center horizontally with 20px top/bottom margins
    .marginVerticalAuto(10.px) // center vertically with 10px left/right margins

    // Flexbox layout
    .display(Display.Flex)
    .flexDirection(FlexDirection.Column)
    .justifyContent(JustifyContent.SpaceBetween)
    .justifyItems(JustifyItems.Center)
    .justifySelf(JustifySelf.Center)
    .alignItems(AlignItems.Center)
    .alignSelf(AlignSelf.FlexStart)
    .alignContent(AlignContent.SpaceBetween)
    .flexWrap(FlexWrap.Wrap)
    .gap(8.px)
    .rowGap(8.px)
    .columnGap(16.px)
    .flex(1)
    .flexGrow(1)
    .flexShrink(0)
    .flexBasis("auto")

    // Row and Column specific alignment
    .verticalAlignment(Alignment.Vertical.CenterVertically) // For Row components
    .horizontalAlignment(Alignment.Horizontal.CenterHorizontally) // For Column components

    // Grid layout
    .display(Display.Grid)
    .gridTemplateColumns("1fr 1fr 1fr")
    .gridTemplateRows("auto")
    .gridColumn("1 / 3")
    .gridRow("1 / 2")
    .gridArea("header")

    // Positioning
    .position(Position.Relative)
    .position(Position.Absolute)
    .top(0.px)
    .right(0.px)
    .bottom(0.px)
    .left(0.px)
    .zIndex(10)
```

### Appearance Modifiers

```kotlin
Modifier
    // Colors
    .backgroundColor("#f0f0f0")
    .color("#333333")
    .opacity(0.8)

    // Borders - comprehensive border modifier
    .border(width = 1, style = BorderStyle.Solid, color = "#cccccc", radius = 4)
    .border(width = 1, style = "dashed", color = "#cccccc")

    // Individual border properties
    .borderWidth(1) // Sets all borders to 1px
    .borderWidth(2, BorderSide.Top) // Sets top border to 2px
    .borderTopWidth(2) // Sets top border to 2px
    .borderRightWidth(1) // Sets right border to 1px
    .borderBottomWidth(2) // Sets bottom border to 2px
    .borderLeftWidth(1) // Sets left border to 1px
    .borderStyle(BorderStyle.Dashed)
    .borderColor("#cccccc")
    .borderRadius(4.px)
    .borderRadius(4.px, 0.px, 4.px, 0.px) // top-left, top-right, bottom-right, bottom-left

    // Shadows
    .boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
    .textShadow("1px 1px 2px rgba(0, 0, 0, 0.2)")

    // Typography
    .fontFamily("Arial, sans-serif")
    .fontSize(16.px)
    .fontWeight(400)                        // Using numeric value
    .fontWeight(FontWeight.Bold)            // Using FontWeight enum
    .fontWeight(FontWeight.SemiBold)        // Using FontWeight enum
    .fontStyle(FontStyle.Italic)
    .textAlign(TextAlign.Center)
    .textDecoration(TextDecoration.Underline)
    .textTransform(TextTransform.Uppercase)
    .letterSpacing(0.5.px)
    .lineHeight(1.5)                        // Using numeric value directly
    .whiteSpace(WhiteSpace.NoWrap)

    // Backgrounds
    .backgroundImage("url('image.jpg')")
    .backgroundSize(BackgroundSize.Cover)
    .backgroundPosition("center")
    .backgroundRepeat(BackgroundRepeat.NoRepeat)
    .backgroundClip(BackgroundClip.Text) // Clip background to text

    // Radial gradients
    .radialGradient(
        shape = "circle",
        colors = listOf("rgba(0, 247, 255, 0.15) 0%", "rgba(0, 247, 255, 0) 70%"),
        position = "center"
    )
    .radialGradient(
        innerColor = "rgba(0, 247, 255, 0.15)",
        outerColor = "rgba(0, 247, 255, 0)",
        innerPosition = "0%",
        outerPosition = "70%"
    )
    .radialGradient(
        innerColor = Color.rgba(0, 247, 255, 0.15f), // Using float alpha (0.0-1.0)
        outerColor = Color.rgba(0, 247, 255, 0f),    // Using float alpha (0.0-1.0)
        innerPosition = "0%",
        outerPosition = "70%"
    )
    .radialGradient(
        shape = "circle",
        colorStops = listOf(
            Color.rgba(0, 247, 255, 0.15f) to "0%", // Using float alpha (0.0-1.0)
            Color.hex("#ff2a6d") to "100%"
        ),
        position = "center"
    )
    // Using enum values for shape and position
    .radialGradient(
        shape = RadialGradientShape.Circle,
        colors = listOf("rgba(0, 247, 255, 0.15) 0%", "rgba(0, 247, 255, 0) 70%"),
        position = RadialGradientPosition.Center
    )
    // Using numeric values for positions (converted to percentages)
    .radialGradient(
        innerColor = "rgba(0, 247, 255, 0.15)",
        outerColor = "rgba(0, 247, 255, 0)",
        innerPosition = 0,
        outerPosition = 70,
        shape = "circle",
        position = "center"
    )
    // Using Color objects, enum values, and numeric positions
    .radialGradient(
        innerColor = Color.rgba(0, 247, 255, 0.15f),
        outerColor = Color.rgba(0, 247, 255, 0f),
        innerPosition = 0,
        outerPosition = 70,
        shape = RadialGradientShape.Ellipse,
        position = RadialGradientPosition.TopLeft
    )

    // Linear gradients
    .linearGradient(
        direction = "to right",
        colors = listOf("rgba(255, 0, 0, 0.8) 0%", "rgba(0, 0, 255, 0.8) 100%")
    )
    .linearGradient(
        direction = "90deg",
        colors = listOf("#00f7ff 0%", "#ff2a6d 100%")
    )
    .linearGradient(
        startColor = "rgba(255, 0, 0, 0.8)",
        endColor = "rgba(0, 0, 255, 0.8)",
        direction = "to bottom"
    )
    .linearGradient(
        gradientDirection = "45deg",
        startColor = Color.rgb(255, 0, 0),
        endColor = Color.rgb(0, 0, 255)
    )

    // Filters
    .filter("blur(5px)")
    .filter("brightness(150%)")

    // Transitions
    .transition("background-color 0.3s ease")
    .transition("all 0.3s ease")

    // Transitions with enums and time unit extensions
    .transition(
        property = TransitionProperty.BackgroundColor,
        duration = 0.3.s,
        timingFunction = TransitionTimingFunction.EaseInOut,
        delay = 0.s
    )

    // Individual transition properties with enums
    .transitionProperty(TransitionProperty.All)
    .transitionDuration(300.ms)
    .transitionTimingFunction(TransitionTimingFunction.Ease)
    .transitionDelay(0.ms)

    // Visibility
    .display(Display.None)
    .visibility(Visibility.Hidden)
    .overflow(Overflow.Hidden)
    .overflowX(Overflow.Auto)
    .overflowY(Overflow.Scroll)
```

### State Modifiers

Apply styles conditionally based on component state:

```kotlin
Modifier
    // Hover state
    .hover {
        backgroundColor("#f0f0f0")
        color("#0077cc")
    }

    // Focus state
    .focus {
        outline("2px solid #0077cc")
        boxShadow("0 0 0 3px rgba(0, 119, 204, 0.3)")
    }

    // Active state
    .active {
        backgroundColor("#005599")
        transform("scale(0.98)")
    }

    // Disabled state
    .disabled {
        opacity(0.5)
        cursor(Cursor.NotAllowed)
    }

    // Visited state (for links)
    .visited {
        color("#551A8B")
    }

    // Custom state based on a condition
    .applyIf(isSelected) {
        backgroundColor("#e0f0ff")
        fontWeight(700)
    }
```

### Animation Modifiers

Apply animations to components:

```kotlin
Modifier
    // Transition animations
    .transition("all 0.3s ease")

    // Using enums and time unit extensions
    .transition(
        property = TransitionProperty.All,
        duration = 0.3.s,
        timingFunction = TransitionTimingFunction.EaseInOut
    )

    // Transform
    .transform("rotate(45deg)")
    .transform("scale(1.1)")
    .transform("translateX(10px)")

    // Individual transforms
    .rotate(45.deg)
    .scale(1.1)
    .translateX(10.px)
    .translateY(-5.px)

    // Predefined animations
    .animation(
        name = "fadeIn", 
        duration = 0.3.s, 
        timingFunction = TimingFunction.EaseInOut
    )

    // Custom animation
    .keyframes("slideIn") {
        from {
            opacity(0)
            transform("translateY(-20px)")
        }
        to {
            opacity(1)
            transform("translateY(0)")
        }
    }
```

### Responsive Modifiers

Apply styles based on screen size:

```kotlin
Modifier
    // Apply styles at specific breakpoints
    .media("(min-width: 768px)") {
        flexDirection(FlexDirection.Row)
        gap(16.px)
    }
    .media("(max-width: 767px)") {
        flexDirection(FlexDirection.Column)
        gap(8.px)
    }

    // Predefined breakpoints
    .small {
        fontSize(14.px)
    }
    .medium {
        fontSize(16.px)
    }
    .large {
        fontSize(18.px)
    }
```

## Global Styles

Define global styles that apply to all components:

```kotlin
import code.yousef.summon.theme.*

@Composable
fun MyApp() {
    // Define global styles
    GlobalStyles {
        // Target specific elements
        style("body") {
            margin(0.px)
            fontFamily("Arial, sans-serif")
            backgroundColor("#f9f9f9")
        }

        // Target classes
        style(".container") {
            maxWidth(1200.px)
            margin(0.px, "auto")
            padding(16.px)
        }

        // Target components
        style(Button::class) {
            backgroundColor("#0077cc")
            color("#ffffff")
            padding(8.px, 16.px)
            borderRadius(4.px)
            cursor(Cursor.Pointer)

            hover {
                backgroundColor("#005599")
            }
        }
    }

    // Application content
    Div(
        modifier = Modifier.className("container")
    ) {
        Text("Hello, World!")
        Button(text = "Click me")
    }
}
```

## Themes

Summon provides two approaches for creating and applying themes for consistent styling:

### 1. Using the Theme API (Recommended)

The Theme API provides a centralized way to define and access theme values with type-safe properties:

```kotlin
import code.yousef.summon.theme.*

// Create a custom theme using the Theme API
val customTheme = Theme.createTheme(Theme.Themes.light) {
    copy(
        colorPalette = ColorSystem.blue,
        typographyTheme = Theme.TypographyTheme(
            h1 = Theme.TextStyle(fontSize = "2.5rem", fontWeight = "bold"),
            body = Theme.TextStyle(fontSize = "1rem", lineHeight = "1.5")
        ),
        spacingTheme = Theme.SpacingTheme(
            md = "16px",
            lg = "24px"
        ),
        borderRadiusTheme = Theme.BorderRadiusTheme(
            md = "8px"
        ),
        elevationTheme = Theme.ElevationTheme(
            md = "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)"
        ),
        customValues = mapOf(
            "customColor" to "#ff5722",
            "customSpacing" to "24px"
        )
    )
}

// Set the custom theme
Theme.setTheme(customTheme)

// Access theme values using typed getters
@Composable
fun ThemedComponent() {
    // Get typed theme objects
    val typography = Theme.getTypographyTheme()
    val spacing = Theme.getSpacingTheme()
    val borderRadius = Theme.getBorderRadiusTheme()
    val elevation = Theme.getElevationTheme()

    Column(
        modifier = Modifier
            .padding(spacing.md)
            .backgroundColor(Theme.getColor("background"))
    ) {
        Text(
            text = "Heading",
            modifier = Modifier
                .fontSize(typography.h1.fontSize!!)
                .fontWeight(typography.h1.fontWeight!!)
                .marginBottom(spacing.sm)
        )

        Card(
            modifier = Modifier
                .padding(spacing.sm)
                .borderRadius(borderRadius.md)
                .boxShadow(elevation.md)
        ) {
            Text(
                text = "Card content",
                modifier = Modifier
                    .fontSize(typography.body.fontSize!!)
                    .lineHeight(typography.body.lineHeight!!)
                    .padding(spacing.md)
            )
        }
    }
}

// Use theme modifiers for simplified access
@Composable
fun ThemedComponentWithModifiers() {
    Column(
        modifier = Modifier
            .themePadding("md")
            .themeBackgroundColor("background")
    ) {
        Text(
            text = "Heading",
            modifier = Modifier
                .themeTextStyle("h1")
                .themeMargin(bottom = "sm")
        )

        Card(
            modifier = Modifier
                .themePadding("sm")
                .themeBorderRadius("md")
                .themeElevation("md")
        ) {
            Text(
                text = "Card content",
                modifier = Modifier
                    .themeTextStyle("body")
                    .themePadding("md")
            )
        }
    }
}
```

### 2. Using Custom Theme Objects (Alternative)

For more manual control, you can define your own theme object:

```kotlin
import code.yousef.summon.theme.*

// Define theme variables
object MyTheme {
    // Colors
    val primary = "#0077cc"
    val secondary = "#6c757d"
    val success = "#28a745"
    val error = "#dc3545"
    val warning = "#ffc107"
    val info = "#17a2b8"
    val background = "#f8f9fa"
    val surface = "#ffffff"
    val onPrimary = "#ffffff"

    // Typography
    val fontFamily = "Roboto, Arial, sans-serif"
    val fontSize = 16.px
    val fontWeightLight = 300
    val fontWeightNormal = 400
    val fontWeightBold = 700

    // Spacing
    val spacingSmall = 4.px
    val spacingMedium = 8.px
    val spacingLarge = 16.px
    val spacingXLarge = 32.px

    // Borders
    val borderRadius = 4.px
    val borderWidth = 1.px

    // Shadows
    val shadowSmall = "0 1px 2px rgba(0, 0, 0, 0.1)"
    val shadowMedium = "0 2px 4px rgba(0, 0, 0, 0.1)"
    val shadowLarge = "0 4px 8px rgba(0, 0, 0, 0.1)"

    // Create button style
    val buttonStyle = Modifier
        .backgroundColor(primary)
        .color(onPrimary)
        .padding(spacingMedium, spacingLarge)
        .borderRadius(borderRadius)
        .fontFamily(fontFamily)
        .fontWeight(fontWeightNormal)
        .hover {
            backgroundColor("#005599")
        }
}

// Use theme in components
@Composable
fun ThemedButton(
    text: String,
    onClick: () -> Unit,
    variant: ButtonVariant = ButtonVariant.PRIMARY
) {
    enum class ButtonVariant { PRIMARY, SECONDARY, SUCCESS, ERROR }

    val style = when (variant) {
        ButtonVariant.PRIMARY -> Modifier
            .backgroundColor(MyTheme.primary)
            .color(MyTheme.onPrimary)
        ButtonVariant.SECONDARY -> Modifier
            .backgroundColor(MyTheme.secondary)
            .color(MyTheme.onPrimary)
        ButtonVariant.SUCCESS -> Modifier
            .backgroundColor(MyTheme.success)
            .color(MyTheme.onPrimary)
        ButtonVariant.ERROR -> Modifier
            .backgroundColor(MyTheme.error)
            .color(MyTheme.onPrimary)
    }

    Button(
        text = text,
        onClick = onClick,
        modifier = style
            .padding(MyTheme.spacingMedium, MyTheme.spacingLarge)
            .borderRadius(MyTheme.borderRadius)
            .fontFamily(MyTheme.fontFamily)
            .fontWeight(MyTheme.fontWeightNormal)
            .boxShadow(MyTheme.shadowSmall)
            .hover {
                boxShadow(MyTheme.shadowMedium)
            }
    )
}

// Use in your app
@Composable
fun MyApp() {
    Column(
        modifier = Modifier
            .padding(MyTheme.spacingLarge)
            .gap(MyTheme.spacingMedium)
            .backgroundColor(MyTheme.background)
    ) {
        Text(
            text = "Themed Buttons",
            modifier = Modifier
                .fontSize(24.px)
                .fontWeight(MyTheme.fontWeightBold)
                .fontFamily(MyTheme.fontFamily)
                .color(MyTheme.primary)
        )

        Row(
            modifier = Modifier
                .gap(MyTheme.spacingMedium)
        ) {
            ThemedButton(
                text = "Primary Button",
                onClick = { println("Primary clicked") },
                variant = ThemedButton.ButtonVariant.PRIMARY
            )

            ThemedButton(
                text = "Secondary Button",
                onClick = { println("Secondary clicked") },
                variant = ThemedButton.ButtonVariant.SECONDARY
            )

            ThemedButton(
                text = "Success Button",
                onClick = { println("Success clicked") },
                variant = ThemedButton.ButtonVariant.SUCCESS
            )

            ThemedButton(
                text = "Error Button",
                onClick = { println("Error clicked") },
                variant = ThemedButton.ButtonVariant.ERROR
            )
        }
    }
}
```

## CSS-in-JS

For complex styling needs, you can use the CSS-in-JS approach:

```kotlin
import code.yousef.summon.theme.*

@Composable
fun StyledComponent() {
    // Define styles with CSS syntax
    val styles = css {
        """
        .container {
            display: flex;
            flex-direction: column;
            gap: 16px;
            padding: 24px;
            background-color: #f8f9fa;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .container:hover {
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
        }

        .heading {
            font-size: 24px;
            font-weight: 700;
            color: #333333;
            margin-bottom: 16px;
        }

        .button {
            padding: 8px 16px;
            background-color: #0077cc;
            color: white;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        .button:hover {
            background-color: #005599;
        }

        @media (max-width: 768px) {
            .container {
                padding: 16px;
            }

            .heading {
                font-size: 20px;
            }
        }
        """
    }

    // Use the defined styles
    Div(
        modifier = Modifier
            .className("container")
            .cssRules(styles)
    ) {
        Text(
            text = "CSS-in-JS Example",
            modifier = Modifier.className("heading")
        )

        Button(
            text = "Click me",
            onClick = { println("Button clicked") },
            modifier = Modifier.className("button")
        )
    }
}
```

## Platform-Specific Styling

### JS-Specific Styling

For browser-specific styling:

```kotlin
import code.yousef.summon.modifier.*

Div(
    modifier = Modifier
        .jsOnly {
            // Apply browser-specific styles
            userSelect(UserSelect.None)
            cursor(Cursor.Pointer)
            webkitTapHighlightColor("transparent")
        }
)
```

### JVM-Specific Styling

For JVM-specific styling:

```kotlin
import code.yousef.summon.modifier.*

Div(
    modifier = Modifier
        .jvmOnly {
            // Apply JVM-specific styles
            accessKey('H')
            tabIndex(1)
        }
)
```

### Auto Margins for Centering

The library provides specialized auto margin modifiers for easy element centering.
Import these functions from the dedicated package:

```kotlin
import code.yousef.summon.modifier.AutoMarginModifiers.marginHorizontalAuto
import code.yousef.summon.modifier.AutoMarginModifiers.marginVerticalAuto
import code.yousef.summon.modifier.AutoMarginModifiers.marginAuto
import code.yousef.summon.modifier.AutoMarginModifiers.marginHorizontalAutoZero
import code.yousef.summon.modifier.AutoMarginModifiers.marginVerticalAutoZero
```

You can then use them in your modifiers:

```kotlin
Modifier
    // Auto margins for centering
    .marginAuto()                // center in all directions
    .marginHorizontalAutoZero()  // center horizontally (no parameters needed)
    .marginHorizontalAuto()      // center horizontally only (no top/bottom margins)
    .marginVerticalAutoZero()    // center vertically (no parameters needed)
    .marginVerticalAuto()        // center vertically only (no left/right margins)
    .marginHorizontalAuto(20.px) // center horizontally with 20px top/bottom margins
    .marginVerticalAuto(10.px)   // center vertically with 10px left/right margins
```

The `marginHorizontalAutoZero()` and `marginVerticalAutoZero()` convenience functions are provided
to avoid ambiguity issues with overloaded functions. 

## Color System

Summon provides a comprehensive color system with various ways to create and use colors:

### Color Creation

```kotlin
// Create colors using hex values
val blue = Color.hex("#0077cc")
val redWithAlpha = Color.hex("#FF0000AA") // With alpha

// Create colors using RGB values
val green = Color.rgb(0, 255, 0)
val yellow = Color.rgb(255, 255, 0)

// Create colors using RGBA values
val transparentBlue1 = Color.rgba(0, 0, 255, 128)  // Alpha as int (0-255)
val transparentBlue2 = Color.rgba(0, 0, 255, 0.5f) // Alpha as float (0.0-1.0)
```

### Color Properties and Methods

```kotlin
val color = Color.hex("#FF5500")

// Access color components
val red = color.red       // 255
val green = color.green   // 85
val blue = color.blue     // 0
val alpha = color.alpha   // 255
val alphaFloat = color.alphaFloat // 1.0

// Create a new color with modified alpha
val transparent = color.withAlpha(0.5f)

// Convert to strings
val rgbaString = color.toRgbaString() // "rgba(255, 85, 0, 1.0)"
val hexString = color.toHexString()   // "#FF5500FF"
```

### Color Presets

Summon includes a wide range of predefined colors:

```kotlin
// Basic colors
val black = Color.BLACK
val white = Color.WHITE
val red = Color.RED
val green = Color.GREEN
val blue = Color.BLUE
val yellow = Color.YELLOW
val transparent = Color.TRANSPARENT

// Additional colors
val orange = Color.ORANGE
val purple = Color.PURPLE
val teal = Color.TEAL
val pink = Color.PINK
```

### Material Design 3 Colors

```kotlin
// Material Design 3 colors
val primary = Color.Material3.PRIMARY
val onPrimary = Color.Material3.ON_PRIMARY
val secondary = Color.Material3.SECONDARY
val surface = Color.Material3.SURFACE
val error = Color.Material3.ERROR
```

### Catppuccin Colors

```kotlin
// Catppuccin Latte (Light) colors
val latteBlue = Color.Catppuccin.Latte.BLUE
val latteRed = Color.Catppuccin.Latte.RED
val latteGreen = Color.Catppuccin.Latte.GREEN

// Catppuccin Mocha (Dark) colors
val mochaBlue = Color.Catppuccin.Mocha.BLUE
val mochaRed = Color.Catppuccin.Mocha.RED
val mochaGreen = Color.Catppuccin.Mocha.GREEN
```

### Using Colors with Modifiers

```kotlin
Modifier
    // Using color strings
    .backgroundColor("#0077cc")
    .color("#ffffff")

    // Using Color objects directly
    .backgroundColor(Color.Material3.PRIMARY)
    .color(Color.Material3.ON_PRIMARY)
    .color(Color.TRANSPARENT)
    .backgroundColor(Color.hex("#00f7ff"))

    // Using Color objects with gradients
    .linearGradient(
        direction = "to right",
        startColor = Color.Catppuccin.Mocha.BLUE,
        endColor = Color.Catppuccin.Mocha.PINK
    )
```

## Typography Enhancements

### Font Weight

Summon provides a `FontWeight` enum for common font weight presets:

```kotlin
// Using FontWeight enum
Modifier.fontWeight(FontWeight.Thin)        // 100
Modifier.fontWeight(FontWeight.ExtraLight)  // 200
Modifier.fontWeight(FontWeight.Light)       // 300
Modifier.fontWeight(FontWeight.Normal)      // 400
Modifier.fontWeight(FontWeight.Medium)      // 500
Modifier.fontWeight(FontWeight.SemiBold)    // 600
Modifier.fontWeight(FontWeight.Bold)        // 700
Modifier.fontWeight(FontWeight.ExtraBold)   // 800
Modifier.fontWeight(FontWeight.Black)       // 900

// Using numeric values directly
Modifier.fontWeight(400)  // Normal
Modifier.fontWeight(700)  // Bold
```

### Line Height

The `lineHeight` modifier accepts numeric values directly:

```kotlin
// Using numeric values directly
Modifier.lineHeight(1.5)   // 1.5 times the font size
Modifier.lineHeight(2.0)   // Double the font size

// Using string values
Modifier.lineHeight("1.5em")
Modifier.lineHeight("24px")
```
