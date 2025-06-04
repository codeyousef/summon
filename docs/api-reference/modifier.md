# Modifier API Reference

This document provides detailed information about the Modifier API in the Summon library.

## Table of Contents

- [Modifier](#modifier)
- [CSS Units Extensions](#css-units-extensions)
- [Layout Modifiers](#layout-modifiers)
- [Appearance Modifiers](#appearance-modifiers)
- [Typography Modifiers](#typography-modifiers)
- [Border Modifiers](#border-modifiers)
- [Transform Modifiers](#transform-modifiers)
- [Gradient Modifiers](#gradient-modifiers)
- [Interactive Modifiers](#interactive-modifiers)
- [Animation Modifiers](#animation-modifiers)
- [Accessibility Modifiers](#accessibility-modifiers)
- [Row/Column Specific Modifiers](#rowcolumn-specific-modifiers)
- [Advanced Modifiers](#advanced-modifiers)
- [Utility Functions](#utility-functions)
- [CSS Enums](#css-enums)

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

Modifier()
    .width(100.percent)  // Sets width to "100%"
    .fontSize(1.25.rem)  // Sets font size to "1.25rem"
    .padding(16.px)      // Sets padding to "16px"
    .marginTop(20.px)    // Sets top margin to "20px"
    .letterSpacing(0.5.ex) // Sets letter spacing based on x-height
    .lineHeight(2.ch)    // Sets line height based on character width
```

---

## Layout Modifiers

Layout modifiers control the size, position, and arrangement of components.

### Size Modifiers

```kotlin
// Basic size modifiers
fun Modifier.width(value: String): Modifier
fun Modifier.width(value: Number): Modifier  // Auto-converts to px
fun Modifier.height(value: String): Modifier
fun Modifier.height(value: Number): Modifier  // Auto-converts to px
fun Modifier.size(width: String, height: String): Modifier
fun Modifier.size(size: String): Modifier  // Square size

// Size constraints
fun Modifier.maxWidth(value: String): Modifier
fun Modifier.maxHeight(value: String): Modifier
fun Modifier.minWidth(value: String): Modifier
fun Modifier.minHeight(value: String): Modifier

// Fill modifiers
fun Modifier.fillMaxWidth(): Modifier
fun Modifier.fillMaxHeight(): Modifier
fun Modifier.fillMaxSize(): Modifier
fun Modifier.wrapContent(): Modifier
```

### Margin and Padding

```kotlin
// Basic margin functions
fun Modifier.margin(all: String): Modifier
fun Modifier.margin(all: Number): Modifier  // Auto-converts to px
fun Modifier.margin(vertical: String, horizontal: String): Modifier
fun Modifier.margin(vertical: Number, horizontal: Number): Modifier
fun Modifier.margin(top: String, right: String, bottom: String, left: String): Modifier
fun Modifier.marginTop(value: String): Modifier
fun Modifier.marginTop(value: Number): Modifier
fun Modifier.marginRight(value: String): Modifier
fun Modifier.marginBottom(value: String): Modifier
fun Modifier.marginLeft(value: String): Modifier

// Auto margin modifiers for centering
fun Modifier.marginAuto(): Modifier
fun Modifier.marginHorizontalAuto(vertical: String = "0px"): Modifier
fun Modifier.marginVerticalAuto(horizontal: String = "0px"): Modifier
fun Modifier.marginHorizontalAutoZero(): Modifier
fun Modifier.marginVerticalAutoZero(): Modifier

// Padding functions (similar overloads)
fun Modifier.padding(all: String): Modifier
fun Modifier.padding(all: Number): Modifier
fun Modifier.padding(vertical: String, horizontal: String): Modifier
fun Modifier.padding(top: String, right: String, bottom: String, left: String): Modifier
fun Modifier.paddingTop(value: String): Modifier
fun Modifier.paddingRight(value: String): Modifier
fun Modifier.paddingBottom(value: String): Modifier
fun Modifier.paddingLeft(value: String): Modifier
```

### Position Modifiers

```kotlin
fun Modifier.position(value: Position): Modifier
fun Modifier.top(value: String): Modifier
fun Modifier.right(value: String): Modifier
fun Modifier.bottom(value: String): Modifier
fun Modifier.left(value: String): Modifier
fun Modifier.zIndex(value: Int): Modifier
```

### Flexbox Layout

```kotlin
fun Modifier.display(value: Display): Modifier
fun Modifier.flexDirection(value: FlexDirection): Modifier
fun Modifier.justifyContent(value: JustifyContent): Modifier
fun Modifier.alignItems(value: AlignItems): Modifier
fun Modifier.alignSelf(value: AlignSelf): Modifier
fun Modifier.alignContent(value: AlignContent): Modifier
fun Modifier.flexWrap(value: FlexWrap): Modifier
fun Modifier.flex(value: Number): Modifier
fun Modifier.flexGrow(value: Number): Modifier
fun Modifier.flexShrink(value: Number): Modifier
fun Modifier.flexBasis(value: String): Modifier
fun Modifier.gap(value: String): Modifier
fun Modifier.gap(value: Number): Modifier  // Auto-converts to px
fun Modifier.rowGap(value: String): Modifier
fun Modifier.columnGap(value: String): Modifier
```

### Grid Layout

```kotlin
fun Modifier.gridTemplateColumns(value: String): Modifier
fun Modifier.gridTemplateRows(value: String): Modifier
fun Modifier.gridColumn(value: String): Modifier
fun Modifier.gridRow(value: String): Modifier
fun Modifier.gridArea(value: String): Modifier
fun Modifier.gridGap(value: String): Modifier
fun Modifier.gridAutoFlow(value: String): Modifier
fun Modifier.gridAutoColumns(value: String): Modifier
fun Modifier.gridAutoRows(value: String): Modifier
```

### Overflow

```kotlin
fun Modifier.overflow(value: Overflow): Modifier
fun Modifier.overflowX(value: String): Modifier
fun Modifier.overflowY(value: String): Modifier
fun Modifier.scrollBehavior(value: String): Modifier
fun Modifier.scrollbarWidth(value: String): Modifier
```

---

## Appearance Modifiers

### Background

```kotlin
fun Modifier.backgroundColor(value: String): Modifier
fun Modifier.backgroundColor(value: Color): Modifier
fun Modifier.backgroundImage(value: String): Modifier
fun Modifier.backgroundSize(value: String): Modifier
fun Modifier.backgroundPosition(value: String): Modifier
fun Modifier.backgroundRepeat(value: String): Modifier
fun Modifier.backgroundClip(value: BackgroundClip): Modifier
fun Modifier.background(value: String): Modifier  // Shorthand
```

### Colors

```kotlin
fun Modifier.color(value: String): Modifier
fun Modifier.color(value: Color): Modifier
fun Modifier.opacity(value: Double): Modifier
fun Modifier.opacity(value: Float): Modifier
```

### Box Shadow

```kotlin
// Multiple overloads for flexibility
fun Modifier.boxShadow(value: String): Modifier
fun Modifier.boxShadow(
    offsetX: String,
    offsetY: String,
    blurRadius: String,
    color: String
): Modifier
fun Modifier.boxShadow(
    offsetX: String,
    offsetY: String,
    blurRadius: String,
    spreadRadius: String,
    color: String
): Modifier
fun Modifier.boxShadow(
    offsetX: String,
    offsetY: String,
    blurRadius: String,
    spreadRadius: String,
    color: Color
): Modifier
fun Modifier.boxShadow(
    inset: Boolean,
    offsetX: String,
    offsetY: String,
    blurRadius: String,
    spreadRadius: String,
    color: String
): Modifier
```

### Filters & Effects

```kotlin
fun Modifier.filter(value: String): Modifier
fun Modifier.blur(value: String): Modifier
fun Modifier.brightness(value: Double): Modifier
fun Modifier.contrast(value: Double): Modifier
fun Modifier.grayscale(value: Double): Modifier
fun Modifier.saturate(value: Double): Modifier
fun Modifier.sepia(value: Double): Modifier
fun Modifier.hueRotate(value: String): Modifier
fun Modifier.invert(value: Double): Modifier
```

---

## Typography Modifiers

```kotlin
fun Modifier.fontSize(value: String): Modifier
fun Modifier.fontSize(value: Number): Modifier  // Auto-converts to px
fun Modifier.fontWeight(value: FontWeight): Modifier
fun Modifier.fontWeight(value: Int): Modifier
fun Modifier.fontFamily(value: String): Modifier
fun Modifier.fontStyle(value: String): Modifier
fun Modifier.textAlign(value: TextAlign): Modifier
fun Modifier.textDecoration(value: String): Modifier
fun Modifier.textTransform(value: TextTransform): Modifier
fun Modifier.lineHeight(value: String): Modifier
fun Modifier.lineHeight(value: Number): Modifier
fun Modifier.letterSpacing(value: String): Modifier
fun Modifier.letterSpacing(value: Number): Modifier
fun Modifier.wordSpacing(value: String): Modifier
fun Modifier.textIndent(value: String): Modifier
fun Modifier.whiteSpace(value: String): Modifier
fun Modifier.textOverflow(value: String): Modifier
fun Modifier.wordBreak(value: String): Modifier
fun Modifier.textShadow(value: String): Modifier
fun Modifier.verticalAlign(value: String): Modifier
```

---

## Border Modifiers

### Basic Border

```kotlin
// Shorthand border
fun Modifier.border(width: String, style: String, color: String): Modifier
fun Modifier.border(width: Number, style: String, color: String): Modifier
fun Modifier.border(width: String, style: BorderStyle, color: String): Modifier
fun Modifier.border(width: Number, style: BorderStyle, color: Color): Modifier

// Border radius
fun Modifier.borderRadius(value: String): Modifier
fun Modifier.borderRadius(value: Number): Modifier  // Auto-converts to px
fun Modifier.borderRadius(
    topLeft: String,
    topRight: String,
    bottomRight: String,
    bottomLeft: String
): Modifier
```

### Individual Border Sides

```kotlin
// Border width for individual sides
fun Modifier.borderTopWidth(value: Number): Modifier
fun Modifier.borderRightWidth(value: Number): Modifier
fun Modifier.borderBottomWidth(value: Number): Modifier
fun Modifier.borderLeftWidth(value: Number): Modifier
fun Modifier.borderWidth(value: Number, side: BorderSide): Modifier

// Border style
fun Modifier.borderStyle(value: BorderStyle): Modifier
fun Modifier.borderTopStyle(value: BorderStyle): Modifier
fun Modifier.borderRightStyle(value: BorderStyle): Modifier
fun Modifier.borderBottomStyle(value: BorderStyle): Modifier
fun Modifier.borderLeftStyle(value: BorderStyle): Modifier

// Border color
fun Modifier.borderColor(value: String): Modifier
fun Modifier.borderColor(value: Color): Modifier
fun Modifier.borderTopColor(value: String): Modifier
fun Modifier.borderRightColor(value: String): Modifier
fun Modifier.borderBottomColor(value: String): Modifier
fun Modifier.borderLeftColor(value: String): Modifier

// Individual side shortcuts
fun Modifier.borderTop(width: String, style: String, color: String): Modifier
fun Modifier.borderRight(width: String, style: String, color: String): Modifier
fun Modifier.borderBottom(width: String, style: String, color: String): Modifier
fun Modifier.borderLeft(width: String, style: String, color: String): Modifier
```

---

## Transform Modifiers

```kotlin
fun Modifier.transform(value: String): Modifier
fun Modifier.translateX(value: String): Modifier
fun Modifier.translateY(value: String): Modifier
fun Modifier.translate(x: String, y: String): Modifier
fun Modifier.scale(value: Double): Modifier
fun Modifier.scale(x: Double, y: Double): Modifier
fun Modifier.scaleX(value: Double): Modifier
fun Modifier.scaleY(value: Double): Modifier
fun Modifier.rotate(value: String): Modifier
fun Modifier.rotateX(value: String): Modifier
fun Modifier.rotateY(value: String): Modifier
fun Modifier.rotateZ(value: String): Modifier
fun Modifier.skewX(value: String): Modifier
fun Modifier.skewY(value: String): Modifier
fun Modifier.skew(x: String, y: String): Modifier
fun Modifier.transformOrigin(value: String): Modifier
fun Modifier.perspective(value: String): Modifier
```

---

## Gradient Modifiers

### Linear Gradients

```kotlin
// Basic linear gradient
fun Modifier.linearGradient(
    direction: String,
    vararg colors: String
): Modifier

// Linear gradient with positions
fun Modifier.linearGradient(
    startColor: String,
    endColor: String,
    startPosition: String = "0%",
    endPosition: String = "100%",
    direction: String = "to bottom"
): Modifier

// Linear gradient with Color objects
fun Modifier.linearGradient(
    direction: String,
    vararg colors: Color
): Modifier

// Linear gradient with Color objects and positions
fun Modifier.linearGradient(
    startColor: Color,
    endColor: Color,
    startPosition: Number = 0,
    endPosition: Number = 100,
    direction: String = "to bottom"
): Modifier
```

### Radial Gradients

```kotlin
// Basic radial gradient
fun Modifier.radialGradient(
    shape: RadialGradientShape = RadialGradientShape.Ellipse,
    position: RadialGradientPosition = RadialGradientPosition.Center,
    vararg colors: String
): Modifier

// Radial gradient with inner/outer colors
fun Modifier.radialGradient(
    innerColor: String,
    outerColor: String,
    shape: RadialGradientShape = RadialGradientShape.Ellipse,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier

// Radial gradient with Color objects
fun Modifier.radialGradient(
    shape: RadialGradientShape = RadialGradientShape.Ellipse,
    position: RadialGradientPosition = RadialGradientPosition.Center,
    vararg colors: Color
): Modifier

// Radial gradient with positions
fun Modifier.radialGradient(
    innerColor: Color,
    outerColor: Color,
    innerPosition: Number = 0,
    outerPosition: Number = 100,
    shape: RadialGradientShape = RadialGradientShape.Ellipse,
    position: RadialGradientPosition = RadialGradientPosition.Center
): Modifier
```

---

## Interactive Modifiers

### Pointer Events

```kotlin
fun Modifier.onClick(handler: String): Modifier
fun Modifier.onMouseEnter(handler: String): Modifier
fun Modifier.onMouseLeave(handler: String): Modifier
fun Modifier.onMouseMove(handler: String): Modifier
fun Modifier.onMouseDown(handler: String): Modifier
fun Modifier.onMouseUp(handler: String): Modifier
fun Modifier.onTouchStart(handler: String): Modifier
fun Modifier.onTouchEnd(handler: String): Modifier
fun Modifier.onTouchMove(handler: String): Modifier
fun Modifier.onDragStart(handler: String): Modifier
fun Modifier.onDragEnd(handler: String): Modifier
fun Modifier.onDragOver(handler: String): Modifier
fun Modifier.onDrop(handler: String): Modifier
fun Modifier.draggable(value: Boolean = true): Modifier
fun Modifier.disablePointerEvents(): Modifier
fun Modifier.enablePointerEvents(): Modifier
```

### Cursor

```kotlin
fun Modifier.cursor(value: Cursor): Modifier
fun Modifier.cursor(value: String): Modifier
```

---

## Animation Modifiers

### Transitions

```kotlin
// Basic transition
fun Modifier.transition(
    property: String,
    duration: String,
    timingFunction: String = "ease",
    delay: String = "0s"
): Modifier

// Transition with multiple properties
fun Modifier.transition(
    properties: List<String>,
    duration: String,
    timingFunction: String = "ease",
    delay: String = "0s"
): Modifier

// Individual transition properties
fun Modifier.transitionProperty(value: TransitionProperty): Modifier
fun Modifier.transitionDuration(value: String): Modifier
fun Modifier.transitionDuration(value: Number): Modifier  // In milliseconds
fun Modifier.transitionTimingFunction(value: TransitionTimingFunction): Modifier
fun Modifier.transitionDelay(value: String): Modifier
fun Modifier.transitionDelay(value: Number): Modifier  // In milliseconds
```

### Animations

```kotlin
fun Modifier.animation(value: String): Modifier
fun Modifier.animationName(value: String): Modifier
fun Modifier.animationDuration(value: String): Modifier
fun Modifier.animationTimingFunction(value: String): Modifier
fun Modifier.animationDelay(value: String): Modifier
fun Modifier.animationIterationCount(value: String): Modifier
fun Modifier.animationDirection(value: String): Modifier
fun Modifier.animationFillMode(value: String): Modifier
fun Modifier.animationPlayState(value: String): Modifier
```

---

## Accessibility Modifiers

### ARIA Attributes

```kotlin
fun Modifier.ariaLabel(value: String): Modifier
fun Modifier.ariaDescribedBy(value: String): Modifier
fun Modifier.ariaLabelledBy(value: String): Modifier
fun Modifier.ariaRole(value: String): Modifier
fun Modifier.ariaHidden(value: Boolean): Modifier
fun Modifier.ariaLive(value: String): Modifier
fun Modifier.ariaExpanded(value: Boolean): Modifier
fun Modifier.ariaPressed(value: Boolean): Modifier
fun Modifier.ariaChecked(value: Boolean): Modifier
fun Modifier.ariaSelected(value: Boolean): Modifier
fun Modifier.ariaDisabled(value: Boolean): Modifier
fun Modifier.ariaValueNow(value: Int): Modifier
fun Modifier.ariaValueMin(value: Int): Modifier
fun Modifier.ariaValueMax(value: Int): Modifier
fun Modifier.ariaValueText(value: String): Modifier
fun Modifier.ariaControls(value: String): Modifier
fun Modifier.ariaHasPopup(value: Boolean): Modifier
fun Modifier.ariaBusy(value: Boolean): Modifier
```

### Accessibility Helpers

```kotlin
fun Modifier.tabIndex(value: Int): Modifier
fun Modifier.focusable(): Modifier
fun Modifier.tabbable(): Modifier
fun Modifier.disabled(): Modifier
fun Modifier.autoFocus(): Modifier
fun Modifier.removeAttribute(name: String): Modifier
```

---

## Row/Column Specific Modifiers

### Row Alignment

```kotlin
// For Row components - vertical alignment
fun Modifier.verticalAlignment(alignment: Alignment.Vertical): Modifier

// Alignment.Vertical values:
// - Top
// - CenterVertically
// - Bottom
```

### Column Alignment

```kotlin
// For Column components - horizontal alignment
fun Modifier.horizontalAlignment(alignment: Alignment.Horizontal): Modifier

// Alignment.Horizontal values:
// - Start
// - CenterHorizontally  
// - End
```

### Example

```kotlin
Row(
    modifier = Modifier
        .fillMaxWidth()
        .verticalAlignment(Alignment.CenterVertically)
) {
    // Children will be vertically centered
}

Column(
    modifier = Modifier
        .fillMaxHeight()
        .horizontalAlignment(Alignment.CenterHorizontally)
) {
    // Children will be horizontally centered
}
```

---

## Advanced Modifiers

### Miscellaneous

```kotlin
fun Modifier.visibility(value: String): Modifier
fun Modifier.userSelect(value: String): Modifier
fun Modifier.outline(value: String): Modifier
fun Modifier.outlineColor(value: String): Modifier
fun Modifier.outlineStyle(value: String): Modifier
fun Modifier.outlineWidth(value: String): Modifier
fun Modifier.outlineOffset(value: String): Modifier
fun Modifier.resize(value: String): Modifier
fun Modifier.objectFit(value: String): Modifier  // For images/videos
fun Modifier.objectPosition(value: String): Modifier
fun Modifier.aspectRatio(value: String): Modifier
fun Modifier.willChange(value: String): Modifier
fun Modifier.isolation(value: String): Modifier
fun Modifier.mixBlendMode(value: String): Modifier
```

---

## Utility Functions

### Conditional Modifiers

```kotlin
// Apply modifier conditionally
fun Modifier.applyIf(
    condition: Boolean,
    block: Modifier.() -> Modifier
): Modifier

// Example
Modifier
    .padding(16.px)
    .applyIf(isHighlighted) {
        backgroundColor("#ffff00")
        border(2.px, BorderStyle.Solid, "#ff0000")
    }
```

### Modifier Operations

```kotlin
// Clone a modifier
fun Modifier.clone(): Modifier

// Combine modifiers
fun Modifier.combine(other: Modifier): Modifier
operator fun Modifier.plus(other: Modifier): Modifier

// Generic event handler
fun Modifier.event(eventName: String, handler: String): Modifier
```

---

## CSS Enums

Summon provides type-safe enums for CSS values:

### Display

```kotlin
enum class Display {
    None, Block, Inline, InlineBlock, Flex, Grid, 
    InlineFlex, InlineGrid, Table, InlineTable, 
    TableRow, TableCell, TableColumn, None
}
```

### Position

```kotlin
enum class Position {
    Static, Relative, Absolute, Fixed, Sticky
}
```

### FlexDirection

```kotlin
enum class FlexDirection {
    Row, Column, RowReverse, ColumnReverse
}
```

### JustifyContent

```kotlin
enum class JustifyContent {
    FlexStart, FlexEnd, Center, SpaceBetween,
    SpaceAround, SpaceEvenly, Start, End
}
```

### AlignItems

```kotlin
enum class AlignItems {
    FlexStart, FlexEnd, Center, Baseline, Stretch
}
```

### TextAlign

```kotlin
enum class TextAlign {
    Left, Right, Center, Justify, Start, End
}
```

### FontWeight

```kotlin
enum class FontWeight {
    Thin, ExtraLight, Light, Normal, Medium,
    SemiBold, Bold, ExtraBold, Black
}
```

### BorderStyle

```kotlin
enum class BorderStyle {
    None, Solid, Dashed, Dotted, Double, Groove,
    Ridge, Inset, Outset, Hidden
}
```

### Cursor

```kotlin
enum class Cursor {
    Auto, Default, Pointer, Wait, Text, Move,
    NotAllowed, Crosshair, ZoomIn, ZoomOut,
    Grab, Grabbing, Help, Progress, ContextMenu
}
```

### Overflow

```kotlin
enum class Overflow {
    Visible, Hidden, Scroll, Auto, Clip
}
```

### TextTransform

```kotlin
enum class TextTransform {
    None, Uppercase, Lowercase, Capitalize, FullWidth
}
```

### TransitionProperty

```kotlin
enum class TransitionProperty {
    All, None, Background, Border, Color, Height,
    Margin, Opacity, Padding, Transform, Width
}
```

### TransitionTimingFunction

```kotlin
enum class TransitionTimingFunction {
    Linear, Ease, EaseIn, EaseOut, EaseInOut,
    StepStart, StepEnd
}
```

### FlexWrap

```kotlin
enum class FlexWrap {
    NoWrap, Wrap, WrapReverse
}
```

### RadialGradientShape

```kotlin
enum class RadialGradientShape {
    Circle, Ellipse
}
```

### RadialGradientPosition

```kotlin
enum class RadialGradientPosition {
    Center, Top, Bottom, Left, Right,
    TopLeft, TopRight, BottomLeft, BottomRight
}
```

### BackgroundClip

```kotlin
enum class BackgroundClip {
    BorderBox, PaddingBox, ContentBox, Text
}
```

### BorderSide

```kotlin
enum class BorderSide {
    Top, Right, Bottom, Left
}
```

---

## Complete Example

Here's a comprehensive example showing various modifiers in action:

```kotlin
import code.yousef.summon.modifier.*
import code.yousef.summon.extensions.*

Card(
    modifier = Modifier()
        // Layout
        .width(300.px)
        .maxWidth(100.percent)
        .marginHorizontalAutoZero()  // Center horizontally
        .padding(24.px)
        
        // Appearance
        .backgroundColor("#ffffff")
        .borderRadius(12.px)
        .boxShadow("0px", "4px", "12px", "0px", "rgba(0,0,0,0.1)")
        
        // Gradient background
        .linearGradient(
            direction = "to bottom right",
            colors = arrayOf("#667eea", "#764ba2")
        )
        
        // Typography (for text content)
        .fontSize(16.px)
        .fontWeight(FontWeight.Normal)
        .lineHeight(1.6)
        .color("#333333")
        
        // Transitions
        .transition(
            properties = listOf("transform", "box-shadow"),
            duration = "300ms",
            timingFunction = "ease-out"
        )
        
        // Interactive
        .cursor(Cursor.Pointer)
        .applyIf(isHovered) {
            transform("translateY(-4px)")
            boxShadow("0px", "8px", "24px", "0px", "rgba(0,0,0,0.15)")
        }
        
        // Accessibility
        .ariaLabel("Interactive card")
        .tabIndex(0)
) {
    // Card content
}
```