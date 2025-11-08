# Styling

Summon provides a powerful and intuitive styling system that works across platforms, allowing you to create consistent UIs in both browser and JVM environments.

## Modifier API

The primary way to style components in Summon is through the `Modifier` API, which provides a fluent interface for applying styles using the standalone implementation:

```kotlin
// Using the standalone Summon implementation - no imports needed
@Composable
fun StyledButton(): String {
    return Button(
        text = "Click me",
        modifier = Modifier()
            .padding("16px")
            .backgroundColor("#0077cc")
            .color("#ffffff")
            .borderRadius("4px")
            .fontWeight("700")
            .cursor("pointer")
            .onClick("handleClick()")
            // Hover effect can be added via CSS
            .style("transition", "background-color 0.3s ease")
    )
}

// You can also add CSS for hover effects
// <style>
// button:hover { background-color: #005599 !important; }
// </style>
```

## CSS Units

In the standalone implementation, CSS units are provided as simple string values:

```kotlin
// CSS unit helper functions (you can add these to your standalone implementation)
fun Int.px() = "${this}px"
fun Double.px() = "${this}px"
fun Int.rem() = "${this}rem"
fun Double.rem() = "${this}rem"
fun Int.em() = "${this}em"
fun Double.em() = "${this}em"
fun Int.percent() = "${this}%"
fun Double.percent() = "${this}%"
fun Int.vw() = "${this}vw"
fun Int.vh() = "${this}vh"
fun Int.ch() = "${this}ch"
fun Int.ex() = "${this}ex"

// Or use string values directly
@Composable
fun StyledText(): String {
    return Text(
        text = "Styled text with various units",
        modifier = Modifier()
            .style("width", "200px")
            .fontSize("1.2rem")           // Root relative unit
            .style("margin-top", "10px")  // Pixels
            .style("margin-bottom", "1.5em")  // Relative to current font size
            .style("letter-spacing", "0.5ex") // Based on x-height
            .style("line-height", "2ch")      // Based on character width
    )
}

// Using helper functions
@Composable
fun StyledTextWithHelpers(): String {
    return Text(
        text = "Styled text with helper functions",
        modifier = Modifier()
            .style("width", 200.px())
            .fontSize(1.2.rem())
            .style("margin-top", 10.px())
            .style("margin-bottom", 1.5.em())
    )
}
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

With the standalone implementation, you can extend the Modifier class to add these layout functions:

```kotlin
// Extended Modifier for layout (add these to your standalone implementation)
fun Modifier.width(value: String): Modifier = style("width", value)
fun Modifier.height(value: String): Modifier = style("height", value)
fun Modifier.maxWidth(value: String): Modifier = style("max-width", value)
fun Modifier.minHeight(value: String): Modifier = style("min-height", value)

// Margin helpers are available from the framework:
// - margin(vertical: String, horizontal: String)
// - margin(top: String, right: String, bottom: String, left: String)
// Additional margin overloads available in ModifierExtensions for Number parameters

// Usage examples
@Composable
fun LayoutExample(): String {
    return Column(
        modifier = Modifier()
            .width("200px")
            .height("100px")
            .style("max-width", "500px")
            .style("min-height", "50px")
            .margin("8px")
            .padding("16px")
            
            // Flexbox layout
            .display("flex")
            .flexDirection("column")
            .style("justify-content", "space-between")
            .alignItems(AlignItems.Center)
            .gap("8px")
            
            // Grid layout
            .display(Display.Grid)
            .style("grid-template-columns", "1fr 1fr 1fr")
            .style("grid-template-rows", "auto")
            
            // Positioning
            .style("position", "relative")
            .style("top", "0px")
            .style("z-index", "10")
    ) {
        Text("Layout content example")
    }
}

// Auto margins for centering
@Composable 
fun CenteredElement(): String {
    return Div(
        modifier = Modifier()
            .width(cssMin("1200px", "92vw"))
            .height("100px")
            .centerHorizontally() // Horizontal centering
            .padding("${cssClamp("22px", "4vw", "48px")} 0")
            .backgroundColor("#f0f0f0")
    ) {
        Text("Centered content")
    }
}

`cssMin(...)`, `cssMax(...)`, and `cssClamp(min, preferred, max)` are utility helpers that build the corresponding CSS functions for you, so you can express responsive sizes (e.g., `width(cssMin("1200px", "92vw"))` or `padding("${cssClamp("22px", "4vw", "48px")} 0")`) without hand-writing the function strings.
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
    .backgroundClipText()

    // Radial gradients
    .radialGradient(
        shape = "circle",
        colors = listOf("rgba(0, 247, 255, 0.15) 0%", "rgba(0, 247, 255, 0) 70%"),
        position = "center"
    )
    .backgroundLayers {
        linearGradient {
            direction("180deg")
            colorStop("#ffffff", "0%")
            colorStop("#dfdfea", "65%")
            colorStop("#c5c5d4", "100%")
        }
    }
    .backgroundClipText() // sets background-clip + prefixed version
    .color("transparent")
    .textShadow("0 1px 0 #ffffff33")
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

    // Layered backgrounds (aurora + grain stack)
    .backgroundLayers {
        radialGradient {
            shape(RadialGradientShape.Ellipse)
            size("900px", "600px")
            position("25%", "20%")
            colorStop("rgba(0, 247, 255, 0.35)", "0%")
            colorStop("transparent", "80%")
        }
        linearGradient {
            direction("to top")
            colorStop("rgba(8, 0, 50, 0.65)", "0%")
            colorStop("transparent", "90%")
        }
        conicGradient {
            from(210)
            colorStop("#ffffff88", null)
            colorStop("#ffffff00", "40%")
            colorStop("#ffffff00", "70%")
            colorStop("#ffffff33", null)
        }
        url("/static/grain.png")
    }

The `backgroundLayers` DSL accepts any number of `radialGradient`, `linearGradient`, or `url/image` entries. Each builder exposes helpers for shape, size, positions, and color stops so you can mirror complex CSS strings from the portfolio mock without hand-written serialization.

    // Filters
    .filter("blur(5px)")
    .filter("brightness(150%)")

    // Filter/backdrop DSL
    .filter {
        blur(20)
        saturate(1.15)
        hueRotate(40)
    }
    .backdropFilter {
        brightness(1.05)
        contrast(1.1)
    }
    .mixBlendMode(BlendMode.Multiply)
    .backgroundBlendModes(BlendMode.Screen, BlendMode.Screen, BlendMode.Screen, BlendMode.Normal, BlendMode.Normal)

`filter { ... }` and `backdropFilter { ... }` use the same builder under the hood, so you can mix blur, brightness, contrast, hue rotation, or drop-shadow steps with readable Kotlin while keeping the generated CSS in sync with the mock’s blur/saturate stack. Pair this with `backgroundBlendModes(...)` when layering aurora gradients over texture images so the blend order stays consistent with the mock, and use `Canvas`/`ScriptTag` from `code.yousef.summon.components.foundation` when you need raw shader hooks inside the same hero section.

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
    .aspectRatio(16, 9)
    .inset("0")
```

### Pseudo-element Hooks

Use `before`/`after` to attach glow layers or film-grain canvases without extra DOM nodes. The pseudo-element inherits
the host element’s `data-summon-id`, so the runtime injects the proper scoped CSS automatically.

```kotlin
Modifier()
    .before {
        style("content", "\"\"")
            .style("position", "absolute")
            .style("inset", "-20vmax")
            .style("background", "radial-gradient(circle, rgba(255,255,255,0.15), transparent)")
            .style("filter", "blur(40px)")
            .style("opacity", "0.6")
    }
    .after {
        style("content", "\"\"")
            .style("position", "absolute")
            .style("inset", "0")
            .style("background", "url('/static/grain.png')")
            .style("mix-blend-mode", "soft-light")
            .style("opacity", "0.4")
    }
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
// Theme configuration using standalone implementation

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
// Theme configuration using standalone implementation

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
// Theme configuration using standalone implementation

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
// Theme configuration using standalone implementation

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
// Modifier system using standalone implementation

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
// Modifier system using standalone implementation

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
// Auto margin utilities using standalone implementation
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
