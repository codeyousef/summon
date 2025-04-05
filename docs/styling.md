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

// These are all equivalent to writing the CSS strings manually
val pixels = 16.px        // "16px"
val rootEm = 1.5.rem      // "1.5rem"
val emUnits = 1.2.em      // "1.2em"
val percentage = 50.percent  // "50%"
val viewportWidth = 100.vw   // "100vw"
val viewportHeight = 100.vh  // "100vh"
val viewportMin = 50.vmin    // "50vmin"
val viewportMax = 50.vmax    // "50vmax"
```

These extensions can be used with all modifiers that accept CSS dimension values:

```kotlin
Text(
    text = "Sized text",
    modifier = Modifier
        .width(200.px)
        .fontSize(1.2.rem)
        .marginTop(10.px)
        .marginBottom(20.px)
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
    
    // Flexbox layout
    .display(Display.Flex)
    .flexDirection(FlexDirection.Column)
    .justifyContent(JustifyContent.SpaceBetween)
    .alignItems(AlignItems.Center)
    .flexWrap(FlexWrap.Wrap)
    .gap(8.px)
    .rowGap(8.px)
    .columnGap(16.px)
    .flex(1)
    .flexGrow(1)
    .flexShrink(0)
    .flexBasis("auto")
    
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
    
    // Borders
    .border(1.px, "#cccccc")
    .border(1.px, BorderStyle.Dashed, "#cccccc")
    .borderTop(1.px, "#cccccc")
    .borderRight(1.px, "#cccccc")
    .borderBottom(1.px, "#cccccc")
    .borderLeft(1.px, "#cccccc")
    .borderRadius(4.px)
    .borderRadius(4.px, 0.px, 4.px, 0.px) // top-left, top-right, bottom-right, bottom-left
    
    // Shadows
    .boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
    .textShadow("1px 1px 2px rgba(0, 0, 0, 0.2)")
    
    // Typography
    .fontFamily("Arial, sans-serif")
    .fontSize(16.px)
    .fontWeight(400)
    .fontStyle(FontStyle.Italic)
    .textAlign(TextAlign.Center)
    .textDecoration(TextDecoration.Underline)
    .textTransform(TextTransform.Uppercase)
    .letterSpacing(0.5.px)
    .lineHeight(1.5)
    .whiteSpace(WhiteSpace.NoWrap)
    
    // Backgrounds
    .backgroundImage("url('image.jpg')")
    .backgroundSize(BackgroundSize.Cover)
    .backgroundPosition("center")
    .backgroundRepeat(BackgroundRepeat.NoRepeat)
    
    // Filters
    .filter("blur(5px)")
    .filter("brightness(150%)")
    
    // Transitions
    .transition("background-color 0.3s ease")
    .transition("all 0.3s ease")
    
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

class MyApp : Composable {
    override fun render() {
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
}
```

## Themes

Create and apply themes for consistent styling:

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
class ThemedButton(
    private val text: String,
    private val onClick: () -> Unit,
    private val variant: ButtonVariant = ButtonVariant.PRIMARY
) : Composable {
    enum class ButtonVariant { PRIMARY, SECONDARY, SUCCESS, ERROR }
    
    override fun render() {
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
}

// Use in your app
class MyApp : Composable {
    override fun render() {
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
}
```

## CSS-in-JS

For complex styling needs, you can use the CSS-in-JS approach:

```kotlin
import code.yousef.summon.theme.*

class StyledComponent : Composable {
    override fun render() {
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