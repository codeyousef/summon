# Modifier API Reference

This document provides detailed information about the Modifier API in the Summon library.

## Table of Contents

- [Modifier](#modifier)
- [Layout Modifiers](#layout-modifiers)
- [Appearance Modifiers](#appearance-modifiers)
- [Interactive Modifiers](#interactive-modifiers)
- [Animation Modifiers](#animation-modifiers)
- [Accessibility Modifiers](#accessibility-modifiers)
- [Utility Modifiers](#utility-modifiers)
- [Extension Functions](#extension-functions)

---

## Modifier

The `Modifier` class is the foundation of the styling system in Summon.

### Class Definition

```kotlin
package code.yousef.summon.modifier

class Modifier {
    // Factory method
    companion object {
        operator fun invoke(): Modifier = Modifier()
    }

    // Methods to apply CSS properties
    fun property(name: String, value: String): Modifier
    fun rawCss(css: String): Modifier

    // Create a combined modifier
    operator fun plus(other: Modifier): Modifier
}
```

### Description

`Modifier` is the main class for applying styles and layout to components. It follows a builder pattern where each method returns a new `Modifier` instance with the added property.

### Example

```kotlin
val modifier = Modifier()
    .width(200.px)
    .height(100.px)
    .backgroundColor("#0077cc")
    .color("#ffffff")
    .padding(16.px)
    .borderRadius(4.px)
```

---

## CSS Units Extensions

Summon provides convenient extension properties for numeric values to create CSS dimension values.

### Number Extensions

```kotlin
// In code.yousef.summon.extensions package

// General CSS units
val Number.px: String  // Pixels (e.g., 16.px -> "16px")
val Number.rem: String // Root em units (e.g., 1.5.rem -> "1.5rem")
val Number.em: String  // Em units (e.g., 1.2.em -> "1.2em")
val Number.percent: String // Percentage values (e.g., 50.percent -> "50%")
val Number.vw: String  // Viewport width (e.g., 100.vw -> "100vw")
val Number.vh: String  // Viewport height (e.g., 100.vh -> "100vh")
val Number.vmin: String // Viewport minimum (e.g., 50.vmin -> "50vmin")
val Number.vmax: String // Viewport maximum (e.g., 50.vmax -> "50vmax")

// Font-specific CSS units
val Number.sp: String  // Scale-independent pixels (e.g., 14.sp -> "14sp")
val Number.ch: String  // Character units - width of "0" (e.g., 2.ch -> "2ch")
val Number.ex: String  // X-height units - height of "x" (e.g., 3.ex -> "3ex")
val Number.pt: String  // Points - traditional print unit (e.g., 12.pt -> "12pt")
val Number.pc: String  // Picas - traditional print unit (e.g., 6.pc -> "6pc")
```

### Example

```kotlin
import code.yousef.summon.extensions.*

Modifier
    .width(100.percent)  // Sets width to "100%"
    .fontSize(1.25.rem)  // Sets font size to "1.25rem"
    .padding(16.px)      // Sets padding to "16px"
    .marginTop(20.px)    // Sets top margin to "20px"
    .letterSpacing(0.5.ex) // Sets letter spacing based on x-height
    .lineHeight(2.ch)    // Sets line height based on character width
```

These extensions provide a more type-safe and concise way to specify dimensions in your styles, and they can be used with all modifiers that accept CSS dimension values.

---

## Layout Modifiers

Layout modifiers control the size, position, and arrangement of components.

### Size Modifiers

```kotlin
fun Modifier.width(value: CSSSize): Modifier
fun Modifier.height(value: CSSSize): Modifier
fun Modifier.size(width: CSSSize, height: CSSSize): Modifier
fun Modifier.maxWidth(value: CSSSize): Modifier
fun Modifier.maxHeight(value: CSSSize): Modifier
fun Modifier.minWidth(value: CSSSize): Modifier
fun Modifier.minHeight(value: CSSSize): Modifier
// Note: These are member functions of the Modifier class, not extension functions
fun Modifier.fillMaxWidth(): Modifier
fun Modifier.fillMaxHeight(): Modifier
fun Modifier.fillMaxSize(): Modifier
```

#### Example

```kotlin
Modifier
    .width(200.px)
    .height(100.px)
    .maxWidth(500.px)
    .minHeight(50.px)
```

### Margin and Padding

```kotlin
// Basic margin functions
fun Modifier.margin(all: CSSSize): Modifier
fun Modifier.margin(vertical: CSSSize, horizontal: CSSSize): Modifier
fun Modifier.margin(top: CSSSize, right: CSSSize, bottom: CSSSize, left: CSSSize): Modifier
fun Modifier.marginTop(value: CSSSize): Modifier
fun Modifier.marginRight(value: CSSSize): Modifier
fun Modifier.marginBottom(value: CSSSize): Modifier
fun Modifier.marginLeft(value: CSSSize): Modifier

// Auto margin modifiers for centering (import from code.yousef.summon.modifier.AutoMarginModifiers)
fun Modifier.marginAuto(): Modifier
fun Modifier.marginHorizontalAuto(vertical: CSSSize = "0px"): Modifier
fun Modifier.marginVerticalAuto(horizontal: CSSSize = "0px"): Modifier
fun Modifier.marginHorizontalAutoZero(): Modifier  // Convenience function with no parameters
fun Modifier.marginVerticalAutoZero(): Modifier    // Convenience function with no parameters

// Padding functions
fun Modifier.padding(all: CSSSize): Modifier
fun Modifier.padding(vertical: CSSSize, horizontal: CSSSize): Modifier
fun Modifier.padding(top: CSSSize, right: CSSSize, bottom: CSSSize, left: CSSSize): Modifier
fun Modifier.paddingTop(value: CSSSize): Modifier
fun Modifier.paddingRight(value: CSSSize): Modifier
fun Modifier.paddingBottom(value: CSSSize): Modifier
fun Modifier.paddingLeft(value: CSSSize): Modifier
```

#### Example

```kotlin
// Import the auto margin modifiers
import code.yousef.summon.modifier.AutoMarginModifiers.marginHorizontalAuto
import code.yousef.summon.modifier.AutoMarginModifiers.marginVerticalAuto
import code.yousef.summon.modifier.AutoMarginModifiers.marginAuto
import code.yousef.summon.modifier.AutoMarginModifiers.marginHorizontalAutoZero
import code.yousef.summon.modifier.AutoMarginModifiers.marginVerticalAutoZero

// Basic margin and padding
Modifier
    .margin(8.px)
    .padding(16.px, 24.px)
    .marginTop(32.px)

// Centering with auto margins
// Center element horizontally (most common usage, no parameters needed)
.width(200.px)
.marginHorizontalAutoZero()  // Sets "margin: 0px auto" - clearest way to use

// Using default parameter approach
.width(200.px)
.marginHorizontalAuto()  // Sets "margin: 0px auto" - no vertical margins

// Same with explicit vertical margin
.width(200.px)
.marginHorizontalAuto(20.px)  // Sets "margin: 20px auto"

// Using vertical auto margins
.height(100.px)
.marginVerticalAutoZero()    // Sets "margin: auto 0px" - clearest way to use

// Using vertical auto margins with default parameter
.height(100.px)
.marginVerticalAuto()    // Sets "margin: auto 0px"

// Setting all margins to auto
.size(200.px, 100.px)
.marginAuto()            // Sets "margin: auto"
```

### Flexbox Layout

```kotlin
fun Modifier.display(value: Display): Modifier
fun Modifier.flexDirection(value: FlexDirection): Modifier
fun Modifier.justifyContent(value: JustifyContent): Modifier
fun Modifier.justifyItems(value: JustifyItems): Modifier
fun Modifier.justifySelf(value: JustifySelf): Modifier
fun Modifier.alignItems(value: AlignItems): Modifier
fun Modifier.alignItems(value: String): Modifier
fun Modifier.alignSelf(value: AlignSelf): Modifier
fun Modifier.alignSelf(value: String): Modifier
fun Modifier.alignContent(value: AlignContent): Modifier
fun Modifier.alignContent(value: String): Modifier
fun Modifier.verticalAlignment(alignment: Alignment.Vertical): Modifier
fun Modifier.horizontalAlignment(alignment: Alignment.Horizontal): Modifier
fun Modifier.flexWrap(value: FlexWrap): Modifier
fun Modifier.flex(value: Number): Modifier
fun Modifier.flexGrow(value: Number): Modifier
fun Modifier.flexShrink(value: Number): Modifier
fun Modifier.flexBasis(value: String): Modifier
fun Modifier.gap(value: CSSSize): Modifier
fun Modifier.rowGap(value: CSSSize): Modifier
fun Modifier.columnGap(value: CSSSize): Modifier
```

#### Enums

```kotlin
enum class Display(val value: String) {
    None("none"),
    Block("block"),
    Inline("inline"),
    InlineBlock("inline-block"),
    Flex("flex"),
    Grid("grid"),
    InlineFlex("inline-flex"),
    InlineGrid("inline-grid");

    override fun toString(): String = value
}
enum class FlexDirection { Row, Column, RowReverse, ColumnReverse }
enum class JustifyContent(val value: String) {
    FlexStart("flex-start"),
    FlexEnd("flex-end"),
    Center("center"),
    SpaceBetween("space-between"),
    SpaceAround("space-around"),
    SpaceEvenly("space-evenly");

    override fun toString(): String = value
}
enum class JustifyItems(val value: String) {
    Start("start"),
    End("end"),
    Center("center"),
    Stretch("stretch"),
    Baseline("baseline");

    override fun toString(): String = value
}
enum class JustifySelf(val value: String) {
    Auto("auto"),
    Start("start"),
    End("end"),
    Center("center"),
    Stretch("stretch"),
    Baseline("baseline");

    override fun toString(): String = value
}
enum class AlignItems(val value: String) {
    FlexStart("flex-start"),
    FlexEnd("flex-end"),
    Center("center"),
    Baseline("baseline"),
    Stretch("stretch");

    override fun toString(): String = value
}
enum class AlignSelf(val value: String) {
    Auto("auto"),
    FlexStart("flex-start"),
    FlexEnd("flex-end"),
    Center("center"),
    Baseline("baseline"),
    Stretch("stretch");

    override fun toString(): String = value
}
enum class AlignContent(val value: String) {
    FlexStart("flex-start"),
    FlexEnd("flex-end"),
    Center("center"),
    SpaceBetween("space-between"),
    SpaceAround("space-around"),
    Stretch("stretch");

    override fun toString(): String = value
}
enum class FlexWrap(val value: String) {
    NoWrap("nowrap"),
    Wrap("wrap"),
    WrapReverse("wrap-reverse");

    override fun toString(): String = value
}

// Alignment enums for Row and Column components
object Alignment {
    enum class Vertical { Top, CenterVertically, Bottom }
    enum class Horizontal { Start, CenterHorizontally, End }
}
```

#### Example

```kotlin
// Basic flexbox layout
Modifier
    .display(Display.Flex)
    .flexDirection(FlexDirection.Column)
    .justifyContent(JustifyContent.Center)
    .justifyItems(JustifyItems.Center)
    .justifySelf(JustifySelf.Center)
    .alignItems(AlignItems.Center)
    .gap(16.px)

// Using string values
Modifier
    .alignItems("center")
    .alignSelf("flex-start")
    .alignContent("space-between")

// Using enum values
Modifier
    .alignItems(AlignItems.Center)
    .alignSelf(AlignSelf.FlexStart)
    .alignContent(AlignContent.SpaceBetween)

// Row with vertical alignment
Modifier
    .verticalAlignment(Alignment.Vertical.CenterVertically)

// Column with horizontal alignment
Modifier
    .horizontalAlignment(Alignment.Horizontal.CenterHorizontally)
```

### Grid Layout

```kotlin
fun Modifier.gridTemplateColumns(value: String): Modifier
fun Modifier.gridTemplateRows(value: String): Modifier
fun Modifier.gridColumn(value: String): Modifier
fun Modifier.gridRow(value: String): Modifier
fun Modifier.gridArea(value: String): Modifier
fun Modifier.gridAutoColumns(value: String): Modifier
fun Modifier.gridAutoRows(value: String): Modifier
fun Modifier.gridAutoFlow(value: GridAutoFlow): Modifier
```

#### Enums

```kotlin
enum class GridAutoFlow { Row, Column, RowDense, ColumnDense }
```

#### Example

```kotlin
Modifier
    .display(Display.Grid)
    .gridTemplateColumns("repeat(3, 1fr)")
    .gridGap(16.px)
    .padding(16.px)
```

### Positioning

```kotlin
fun Modifier.position(value: Position): Modifier
fun Modifier.top(value: CSSSize): Modifier
fun Modifier.right(value: CSSSize): Modifier
fun Modifier.bottom(value: CSSSize): Modifier
fun Modifier.left(value: CSSSize): Modifier
fun Modifier.zIndex(value: Int): Modifier
```

#### Enums

```kotlin
enum class Position(val value: String) {
    Static("static"),
    Relative("relative"),
    Absolute("absolute"),
    Fixed("fixed"),
    Sticky("sticky");

    override fun toString(): String = value
}
```

#### Example

```kotlin
Modifier
    .position(Position.Absolute)
    .top(0.px)
    .right(0.px)
    .zIndex(10)
```

---

## Appearance Modifiers

Appearance modifiers control the visual styling of components.

### Colors

```kotlin
fun Modifier.backgroundColor(value: String): Modifier
fun Modifier.color(value: String): Modifier
fun Modifier.opacity(value: Number): Modifier
```

#### Example

```kotlin
Modifier
    .backgroundColor("#f0f0f0")
    .color("#333333")
    .opacity(0.8)
```

### Borders

```kotlin
// Comprehensive border modifier with all properties
fun Modifier.border(width: Number? = null, style: String? = null, color: String? = null, radius: Number? = null): Modifier
fun Modifier.border(width: Number? = null, style: BorderStyle? = null, color: String? = null, radius: Number? = null): Modifier
fun Modifier.border(width: String? = null, style: String? = null, color: String? = null, radius: String? = null): Modifier
fun Modifier.border(width: String? = null, style: BorderStyle? = null, color: String? = null, radius: String? = null): Modifier

// Individual border properties
fun Modifier.borderWidth(value: Number): Modifier
fun Modifier.borderWidth(value: Number, side: BorderSide = BorderSide.All): Modifier
fun Modifier.borderTopWidth(value: Number): Modifier
fun Modifier.borderRightWidth(value: Number): Modifier
fun Modifier.borderBottomWidth(value: Number): Modifier
fun Modifier.borderLeftWidth(value: Number): Modifier
fun Modifier.borderStyle(value: String): Modifier
fun Modifier.borderStyle(value: BorderStyle): Modifier
fun Modifier.borderColor(value: String): Modifier
fun Modifier.borderRadius(value: CSSSize): Modifier
fun Modifier.borderRadius(topLeft: CSSSize, topRight: CSSSize, bottomRight: CSSSize, bottomLeft: CSSSize): Modifier
```

#### Enums

```kotlin
enum class BorderStyle(val value: String) { 
    None("none"),
    Hidden("hidden"),
    Dotted("dotted"),
    Dashed("dashed"),
    Solid("solid"),
    Double("double"),
    Groove("groove"),
    Ridge("ridge"),
    Inset("inset"),
    Outset("outset");

    override fun toString(): String = value
}

enum class BorderSide(val value: String) {
    Top("top"),
    Right("right"),
    Bottom("bottom"),
    Left("left"),
    All("all");

    override fun toString(): String = value
}
```

#### Example

```kotlin
Modifier
    .border(1.px, "#cccccc")
    .borderRadius(4.px)
    .borderBottom(2.px, BorderStyle.Solid, "#0077cc")
```

### Shadows

```kotlin
fun Modifier.boxShadow(value: String): Modifier
fun Modifier.textShadow(value: String): Modifier
```

#### Example

```kotlin
Modifier
    .boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
    .textShadow("1px 1px 2px rgba(0, 0, 0, 0.2)")
```

### Typography

```kotlin
// Type-safe modifiers (recommended)
fun Modifier.fontFamily(value: String, component: TextComponent? = null): Modifier
fun Modifier.fontSize(value: CSSSize, component: TextComponent? = null): Modifier
fun Modifier.fontWeight(value: Int, component: TextComponent? = null): Modifier
fun Modifier.fontWeight(value: FontWeight, component: TextComponent? = null): Modifier
fun Modifier.fontStyle(value: FontStyle, component: TextComponent? = null): Modifier
fun Modifier.textAlign(value: TextAlign, component: TextComponent? = null): Modifier
fun Modifier.textDecoration(value: TextDecoration, component: TextComponent? = null): Modifier
fun Modifier.textTransform(value: TextTransform, component: TextComponent? = null): Modifier
fun Modifier.letterSpacing(value: CSSSize, component: TextComponent? = null): Modifier
fun Modifier.lineHeight(value: Number, component: TextComponent? = null): Modifier
fun Modifier.whiteSpace(value: WhiteSpace, component: TextComponent? = null): Modifier

// Legacy modifiers (deprecated)
@Deprecated fun Modifier.fontFamily(value: String): Modifier
@Deprecated fun Modifier.fontSize(value: CSSSize): Modifier
@Deprecated fun Modifier.fontWeight(value: Int): Modifier
@Deprecated fun Modifier.fontWeight(value: FontWeight): Modifier
@Deprecated fun Modifier.fontStyle(value: FontStyle): Modifier
@Deprecated fun Modifier.textAlign(value: TextAlign): Modifier
@Deprecated fun Modifier.textDecoration(value: TextDecoration): Modifier
@Deprecated fun Modifier.textTransform(value: TextTransform): Modifier
@Deprecated fun Modifier.letterSpacing(value: CSSSize): Modifier
@Deprecated fun Modifier.lineHeight(value: Number): Modifier
@Deprecated fun Modifier.whiteSpace(value: WhiteSpace): Modifier
```

#### Enums

```kotlin
enum class FontWeight(val value: String) {
    Thin("100"),
    ExtraLight("200"),
    Light("300"),
    Normal("400"),
    Medium("500"),
    SemiBold("600"),
    Bold("700"),
    ExtraBold("800"),
    Black("900")
}
enum class FontStyle { Normal, Italic, Oblique }
enum class TextAlign { Left, Right, Center, Justify, Start, End }
enum class TextDecoration { None, Underline, Overline, LineThrough }
enum class TextTransform { None, Capitalize, Uppercase, Lowercase }
enum class WhiteSpace { Normal, NoWrap, Pre, PreWrap, PreLine }
```

#### Example

```kotlin
Modifier
    .fontFamily("Arial, sans-serif")
    .fontSize(16.px)
    .fontWeight(700)
    .textAlign(TextAlign.Center)
    .lineHeight(1.5)
```

### Backgrounds

```kotlin
fun Modifier.backgroundImage(value: String): Modifier
fun Modifier.backgroundSize(value: BackgroundSize): Modifier
fun Modifier.backgroundSize(value: String): Modifier
fun Modifier.backgroundPosition(value: String): Modifier
fun Modifier.backgroundRepeat(value: BackgroundRepeat): Modifier
fun Modifier.backgroundClip(value: String): Modifier
fun Modifier.backgroundClip(value: BackgroundClip): Modifier
fun Modifier.radialGradient(shape: String = "circle", colors: List<String>, position: String = "center"): Modifier
fun Modifier.radialGradient(innerColor: String, outerColor: String, innerPosition: String = "0%", outerPosition: String = "100%", shape: String = "circle", position: String = "center"): Modifier
fun Modifier.radialGradient(shape: String = "circle", colorStops: List<Pair<Color, String>>, position: String = "center"): Modifier
fun Modifier.radialGradient(innerColor: Color, outerColor: Color, innerPosition: String = "0%", outerPosition: String = "100%", shape: String = "circle", position: String = "center"): Modifier
fun Modifier.linearGradient(direction: String = "to right", colors: List<String>): Modifier
fun Modifier.linearGradient(startColor: String, endColor: String, startPosition: String = "0%", endPosition: String = "100%", direction: String = "to right"): Modifier
fun Modifier.linearGradient(direction: String = "to right", colorStops: List<Pair<Color, String>>): Modifier
fun Modifier.linearGradient(startColor: Color, endColor: Color, startPosition: String = "0%", endPosition: String = "100%", direction: String = "to right"): Modifier
fun Modifier.linearGradient(gradientDirection: String, startColor: Color, endColor: Color, startPosition: String = "0%", endPosition: String = "100%"): Modifier
```

#### Enums

```kotlin
enum class BackgroundSize { Cover, Contain, Auto }
enum class BackgroundRepeat { Repeat, RepeatX, RepeatY, NoRepeat, Space, Round }
enum class BackgroundClip { BorderBox, PaddingBox, ContentBox, Text }
```

#### Example

```kotlin
// Basic background image
Modifier
    .backgroundImage("url('image.jpg')")
    .backgroundSize(BackgroundSize.Cover)
    .backgroundPosition("center")
    .backgroundRepeat(BackgroundRepeat.NoRepeat)

// Background clip
Modifier
    .backgroundClip(BackgroundClip.Text)
    .color("transparent")
    .backgroundImage("url('pattern.jpg')")

// Radial gradient with list of colors
Modifier
    .radialGradient(
        shape = "circle",
        colors = listOf("rgba(0, 247, 255, 0.15) 0%", "rgba(0, 247, 255, 0) 70%"),
        position = "center"
    )

// Simplified radial gradient with two colors
Modifier
    .radialGradient(
        innerColor = "rgba(0, 247, 255, 0.15)",
        outerColor = "rgba(0, 247, 255, 0)",
        innerPosition = "0%",
        outerPosition = "70%"
    )

// Radial gradient with Color objects
Modifier
    .radialGradient(
        innerColor = Color.rgba(0, 247, 255, 38),
        outerColor = Color.rgba(0, 247, 255, 0),
        innerPosition = "0%",
        outerPosition = "70%"
    )

// Radial gradient with Color objects and custom positions
Modifier
    .radialGradient(
        shape = "circle",
        colorStops = listOf(
            Color.rgba(0, 247, 255, 38) to "0%",
            Color.hex("#ff2a6d") to "100%"
        ),
        position = "center"
    )

// Radial gradient with enum values for shape and position
Modifier
    .radialGradient(
        shape = RadialGradientShape.Circle,
        colors = listOf("rgba(0, 247, 255, 0.15) 0%", "rgba(0, 247, 255, 0) 70%"),
        position = RadialGradientPosition.Center
    )

// Radial gradient with numeric values for positions
Modifier
    .radialGradient(
        innerColor = "rgba(0, 247, 255, 0.15)",
        outerColor = "rgba(0, 247, 255, 0)",
        innerPosition = 0,
        outerPosition = 70,
        shape = "circle",
        position = "center"
    )

// Radial gradient with Color objects, enum values, and numeric positions
Modifier
    .radialGradient(
        innerColor = Color.rgba(0, 247, 255, 0.15f),
        outerColor = Color.rgba(0, 247, 255, 0f),
        innerPosition = 0,
        outerPosition = 70,
        shape = RadialGradientShape.Ellipse,
        position = RadialGradientPosition.TopLeft
    )

// Linear gradient with list of colors
Modifier
    .linearGradient(
        direction = "to right",
        colors = listOf("rgba(255, 0, 0, 0.8) 0%", "rgba(0, 0, 255, 0.8) 100%")
    )

// Linear gradient with angle in degrees
Modifier
    .linearGradient(
        direction = "90deg",
        colors = listOf("#00f7ff 0%", "#ff2a6d 100%")
    )

// Simplified linear gradient with two colors
Modifier
    .linearGradient(
        startColor = "rgba(255, 0, 0, 0.8)",
        endColor = "rgba(0, 0, 255, 0.8)",
        direction = "to bottom"
    )

// Linear gradient with Color objects
Modifier
    .linearGradient(
        startColor = Color.rgb(255, 0, 0),
        endColor = Color.rgb(0, 0, 255),
        direction = "45deg"
    )

// Linear gradient with Color objects and custom positions
Modifier
    .linearGradient(
        direction = "to right",
        colorStops = listOf(
            Color.rgba(0, 247, 255, 128) to "0%",
            Color.hex("#ff2a6d") to "100%"
        )
    )
```

### Visibility

```kotlin
fun Modifier.visibility(value: Visibility): Modifier
fun Modifier.overflow(value: Overflow): Modifier
fun Modifier.overflowX(value: Overflow): Modifier
fun Modifier.overflowY(value: Overflow): Modifier
```

#### Enums

```kotlin
enum class Visibility { Visible, Hidden, Collapse }
enum class Overflow(val value: String) {
    Visible("visible"),
    Hidden("hidden"),
    Scroll("scroll"),
    Auto("auto");

    override fun toString(): String = value
}
```

#### Example

```kotlin
Modifier
    .visibility(Visibility.Visible)
    .overflow(Overflow.Auto)
```

---

## Interactive Modifiers

Interactive modifiers apply styles based on user interaction states.

### State Modifiers

```kotlin
fun Modifier.hover(block: Modifier.() -> Modifier): Modifier
fun Modifier.focus(block: Modifier.() -> Modifier): Modifier
fun Modifier.active(block: Modifier.() -> Modifier): Modifier
fun Modifier.disabled(block: Modifier.() -> Modifier): Modifier
fun Modifier.visited(block: Modifier.() -> Modifier): Modifier
```

#### Example

```kotlin
Modifier
    .backgroundColor("#0077cc")
    .color("#ffffff")
    .hover {
        backgroundColor("#005599")
    }
    .focus {
        outline("2px solid #0077cc")
    }
```

### Conditional Modifier

```kotlin
fun Modifier.applyIf(condition: Boolean, block: Modifier.() -> Modifier): Modifier
```

#### Example

```kotlin
val isSelected = true

Modifier
    .applyIf(isSelected) {
        backgroundColor("#e0f0ff")
        fontWeight(700)
    }
```

---

## Animation Modifiers

Animation modifiers control transitions and animations.

### Transition

```kotlin
// Basic transition
fun Modifier.transition(value: String): Modifier

// Parameterized transitions
fun Modifier.transition(
    property: TransitionProperty = TransitionProperty.All,
    duration: Number = 300,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: Number = 0
): Modifier

fun Modifier.transition(
    property: String,
    duration: Number = 300,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: Number = 0
): Modifier

fun Modifier.transition(
    property: TransitionProperty = TransitionProperty.All,
    duration: String,
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: String = "0ms"
): Modifier

// Individual transition properties
fun Modifier.transitionProperty(value: String): Modifier
fun Modifier.transitionProperty(value: TransitionProperty): Modifier
fun Modifier.transitionDuration(value: String): Modifier
fun Modifier.transitionDuration(value: Number): Modifier
fun Modifier.transitionTimingFunction(value: String): Modifier
fun Modifier.transitionTimingFunction(value: TransitionTimingFunction): Modifier
fun Modifier.transitionDelay(value: String): Modifier
fun Modifier.transitionDelay(value: Number): Modifier
```

#### Enums

```kotlin
enum class TransitionProperty(val value: String) {
    All("all"),
    None("none"),
    Transform("transform"),
    Opacity("opacity"),
    Background("background"),
    BackgroundColor("background-color"),
    Color("color"),
    Height("height"),
    Width("width"),
    Margin("margin"),
    Padding("padding"),
    Border("border"),
    BorderColor("border-color"),
    BorderRadius("border-radius"),
    BoxShadow("box-shadow"),
    TextShadow("text-shadow"),
    FontSize("font-size"),
    FontWeight("font-weight"),
    LineHeight("line-height"),
    LetterSpacing("letter-spacing"),
    Visibility("visibility"),
    ZIndex("z-index");

    override fun toString(): String = value
}

enum class TransitionTimingFunction(val value: String) {
    Ease("ease"),
    Linear("linear"),
    EaseIn("ease-in"),
    EaseOut("ease-out"),
    EaseInOut("ease-in-out"),
    StepStart("step-start"),
    StepEnd("step-end"),
    CubicBezier("cubic-bezier(0.4, 0, 0.2, 1)"); // Material Design standard easing

    override fun toString(): String = value
}
```

#### Time Unit Extensions

```kotlin
val Number.s: String  // Seconds (e.g., 0.3.s -> "0.3s")
val Number.ms: String // Milliseconds (e.g., 300.ms -> "300ms")
```

#### Examples

```kotlin
// Basic string transition
Modifier
    .transition("all 0.3s ease")
    .hover {
        backgroundColor("#005599")
    }

// Using enums and time unit extensions
Modifier
    .transition(
        property = TransitionProperty.BackgroundColor,
        duration = 0.3.s,
        timingFunction = TransitionTimingFunction.EaseInOut,
        delay = 0.s
    )
    .hover {
        backgroundColor("#005599")
    }

// Using individual properties with enums
Modifier
    .transitionProperty(TransitionProperty.All)
    .transitionDuration(300.ms)
    .transitionTimingFunction(TransitionTimingFunction.Ease)
    .transitionDelay(0.ms)
    .hover {
        backgroundColor("#005599")
    }

// Multiple transitions
Modifier
    .transition("background-color 0.3s ease, color 0.2s ease-in-out")
    .hover {
        backgroundColor("#005599")
        color("#ffffff")
    }
```

### Transform

```kotlin
fun Modifier.transform(value: String): Modifier
fun Modifier.transformOrigin(value: String): Modifier
fun Modifier.rotate(value: CSSAngle): Modifier
fun Modifier.scale(value: Number): Modifier
fun Modifier.scaleX(value: Number): Modifier
fun Modifier.scaleY(value: Number): Modifier
fun Modifier.translateX(value: CSSSize): Modifier
fun Modifier.translateY(value: CSSSize): Modifier
```

#### Example

```kotlin
Modifier
    .transform("rotate(45deg)")
    .hover {
        transform("rotate(45deg) scale(1.1)")
    }
```

### Animation

```kotlin
fun Modifier.animation(name: String, duration: CSSTime, timingFunction: TimingFunction = TimingFunction.Ease, delay: CSSTime = 0.s, iterationCount: String = "1", direction: AnimationDirection = AnimationDirection.Normal, fillMode: AnimationFillMode = AnimationFillMode.None): Modifier
fun Modifier.keyframes(name: String, block: KeyframesBuilder.() -> Unit): Modifier
```

#### Enums

```kotlin
enum class AnimationDirection { Normal, Reverse, Alternate, AlternateReverse }
enum class AnimationFillMode { None, Forwards, Backwards, Both }
```

#### Example

```kotlin
Modifier
    .keyframes("fadeIn") {
        from {
            opacity(0)
        }
        to {
            opacity(1)
        }
    }
    .animation(
        name = "fadeIn",
        duration = 0.3.s,
        timingFunction = TimingFunction.EaseInOut
    )
```

---

## Accessibility Modifiers

Accessibility modifiers add ARIA attributes and other accessibility features to elements.

### ARIA Attributes

```kotlin
// Core ARIA attribute modifier
fun Modifier.attribute(name: String, value: String): Modifier
fun Modifier.removeAttribute(name: String): Modifier

// Role
fun Modifier.role(value: String): Modifier

// Labels and Descriptions
fun Modifier.ariaLabel(value: String): Modifier
fun Modifier.ariaLabelledBy(value: String): Modifier
fun Modifier.ariaDescribedBy(value: String): Modifier

// State
fun Modifier.ariaHidden(value: Boolean): Modifier
fun Modifier.ariaExpanded(value: Boolean): Modifier
fun Modifier.ariaPressed(value: Boolean): Modifier
fun Modifier.ariaChecked(value: Boolean): Modifier
fun Modifier.ariaChecked(value: String): Modifier
fun Modifier.ariaSelected(value: Boolean): Modifier
fun Modifier.ariaDisabled(value: Boolean): Modifier
fun Modifier.ariaInvalid(value: Boolean): Modifier
fun Modifier.ariaInvalid(value: String): Modifier
fun Modifier.ariaRequired(value: Boolean): Modifier
fun Modifier.ariaCurrent(value: String): Modifier
fun Modifier.ariaLive(value: String): Modifier

// Relationships
fun Modifier.ariaControls(id: String): Modifier

// Other
fun Modifier.ariaHasPopup(value: Boolean = true): Modifier
fun Modifier.ariaBusy(value: Boolean = true): Modifier
```

### Focus Management

```kotlin
fun Modifier.tabIndex(value: Int): Modifier
fun Modifier.focusable(): Modifier       // Makes an element focusable but not in the tab order
fun Modifier.tabbable(): Modifier        // Makes an element focusable and in the tab order
fun Modifier.disabled(): Modifier        // Marks an element as disabled and not focusable
fun Modifier.autoFocus(): Modifier       // Marks an element for autofocus when rendered
```

### Example

```kotlin
// Basic button with accessibility attributes
Button(
    onClick = { /* handle click */ },
    modifier = Modifier
        .role("button")
        .ariaLabel("Close dialog")
        .ariaControls("main-dialog")
        .tabbable()
)

// Disabled element
TextField(
    value = "",
    onValueChange = {},
    modifier = Modifier
        .disabled()
        .ariaDisabled(true)
)

// Element with several accessibility attributes
Box(
    modifier = Modifier
        .role("alertdialog")
        .ariaLabelledBy("dialog-title")
        .ariaDescribedBy("dialog-desc")
        .ariaModal(true)
        .attribute("data-test-id", "warning-dialog")
)
```

---

## Utility Modifiers

Utility modifiers provide additional functionality.

### Media Queries

```kotlin
fun Modifier.media(query: String, block: Modifier.() -> Modifier): Modifier
fun Modifier.small(block: Modifier.() -> Modifier): Modifier
fun Modifier.medium(block: Modifier.() -> Modifier): Modifier
fun Modifier.large(block: Modifier.() -> Modifier): Modifier
```

#### Example

```kotlin
Modifier
    .padding(16.px)
    .media("(max-width: 768px)") {
        padding(8.px)
    }
    .large {
        fontSize(18.px)
    }
    .small {
        fontSize(14.px)
    }
```

### Class and ID

```kotlin
fun Modifier.className(value: String): Modifier
fun Modifier.id(value: String): Modifier
```

#### Example

```kotlin
Modifier
    .className("button primary")
    .id("submit-button")
```

### Custom CSS

```kotlin
fun Modifier.cssRules(rules: String): Modifier
```

#### Example

```kotlin
Modifier
    .cssRules("""
        & > .item {
            margin-bottom: 8px;
        }
        &:last-child {
            margin-bottom: 0;
        }
    """)
```

---

## Extension Functions

These are utility extension functions for creating CSS size units.

### Size Units

```kotlin
val Number.px: CSSSize
    get() = "${this}px"

val Number.rem: CSSSize
    get() = "${this}rem"

val Number.em: CSSSize
    get() = "${this}em"

val Number.percent: CSSSize
    get() = "${this}%"

val Number.vw: CSSSize
    get() = "${this}vw"

val Number.vh: CSSSize
    get() = "${this}vh"
```

### Time Units

```kotlin
val Number.ms: CSSTime
    get() = "${this}ms"

val Number.s: CSSTime
    get() = "${this}s"
```

### Angle Units

```kotlin
val Number.deg: CSSAngle
    get() = "${this}deg"

val Number.rad: CSSAngle
    get() = "${this}rad"

val Number.turn: CSSAngle
    get() = "${this}turn"
```

#### Example

```kotlin
Modifier
    .width(100.percent)
    .height(100.vh)
    .padding(1.5.rem)
    .fontSize(1.2.em)
    .transition("all ${0.3.s} ease")
    .rotate(45.deg)
``` 
