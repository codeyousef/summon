# Modifier API Reference

This document provides detailed information about the Modifier API in the Summon library.

## Table of Contents

- [Modifier](#modifier)
- [Layout Modifiers](#layout-modifiers)
- [Appearance Modifiers](#appearance-modifiers)
- [Interactive Modifiers](#interactive-modifiers)
- [Animation Modifiers](#animation-modifiers)
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
fun Modifier.alignItems(value: AlignItems): Modifier
fun Modifier.alignSelf(value: AlignSelf): Modifier
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
enum class Display { None, Block, Inline, InlineBlock, Flex, Grid, InlineFlex, InlineGrid }
enum class FlexDirection { Row, Column, RowReverse, ColumnReverse }
enum class JustifyContent { FlexStart, FlexEnd, Center, SpaceBetween, SpaceAround, SpaceEvenly }
enum class AlignItems { FlexStart, FlexEnd, Center, Baseline, Stretch }
enum class AlignSelf { Auto, FlexStart, FlexEnd, Center, Baseline, Stretch }
enum class FlexWrap { NoWrap, Wrap, WrapReverse }
```

#### Example

```kotlin
Modifier
    .display(Display.Flex)
    .flexDirection(FlexDirection.Column)
    .justifyContent(JustifyContent.Center)
    .alignItems(AlignItems.Center)
    .gap(16.px)
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
enum class Position { Static, Relative, Absolute, Fixed, Sticky }
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
fun Modifier.border(width: CSSSize, color: String): Modifier
fun Modifier.border(width: CSSSize, style: BorderStyle, color: String): Modifier
fun Modifier.borderTop(width: CSSSize, color: String): Modifier
fun Modifier.borderRight(width: CSSSize, color: String): Modifier
fun Modifier.borderBottom(width: CSSSize, color: String): Modifier
fun Modifier.borderLeft(width: CSSSize, color: String): Modifier
fun Modifier.borderRadius(value: CSSSize): Modifier
fun Modifier.borderRadius(topLeft: CSSSize, topRight: CSSSize, bottomRight: CSSSize, bottomLeft: CSSSize): Modifier
```

#### Enums

```kotlin
enum class BorderStyle { 
    None, Hidden, Solid, Dashed, Dotted, Double, Groove, Ridge, Inset, Outset
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
fun Modifier.fontFamily(value: String): Modifier
fun Modifier.fontSize(value: CSSSize): Modifier
fun Modifier.fontWeight(value: Int): Modifier
fun Modifier.fontWeight(value: FontWeight): Modifier
fun Modifier.fontStyle(value: FontStyle): Modifier
fun Modifier.textAlign(value: TextAlign): Modifier
fun Modifier.textDecoration(value: TextDecoration): Modifier
fun Modifier.textTransform(value: TextTransform): Modifier
fun Modifier.letterSpacing(value: CSSSize): Modifier
fun Modifier.lineHeight(value: Number): Modifier
fun Modifier.whiteSpace(value: WhiteSpace): Modifier
```

#### Enums

```kotlin
enum class FontWeight { Normal, Bold, Bolder, Lighter, W100, W200, W300, W400, W500, W600, W700, W800, W900 }
enum class FontStyle { Normal, Italic, Oblique }
enum class TextAlign { Left, Right, Center, Justify }
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
```

#### Enums

```kotlin
enum class BackgroundSize { Cover, Contain, Auto }
enum class BackgroundRepeat { Repeat, RepeatX, RepeatY, NoRepeat, Space, Round }
```

#### Example

```kotlin
Modifier
    .backgroundImage("url('image.jpg')")
    .backgroundSize(BackgroundSize.Cover)
    .backgroundPosition("center")
    .backgroundRepeat(BackgroundRepeat.NoRepeat)
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
enum class Overflow { Visible, Hidden, Scroll, Auto }
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
fun Modifier.transition(value: String): Modifier
fun Modifier.transitionProperty(value: String): Modifier
fun Modifier.transitionDuration(value: CSSTime): Modifier
fun Modifier.transitionTimingFunction(value: TimingFunction): Modifier
fun Modifier.transitionDelay(value: CSSTime): Modifier
```

#### Enums

```kotlin
enum class TimingFunction { 
    Linear, Ease, EaseIn, EaseOut, EaseInOut, StepStart, StepEnd
}
```

#### Example

```kotlin
Modifier
    .transition("all 0.3s ease")
    .hover {
        backgroundColor("#005599")
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