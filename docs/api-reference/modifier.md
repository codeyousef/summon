# Modifier API Reference

This document provides detailed information about the Modifier system in the Summon library. Modifiers are used to apply
styling, layout, and behavior to components in a type-safe, composable way.

## Table of Contents

- [Core Modifier Class](#core-modifier-class)
- [CSS Units Extensions](#css-units-extensions)
- [Layout Modifiers](#layout-modifiers)
- [Styling Modifiers](#styling-modifiers)
- [CSS Enums](#css-enums)
- [Accessibility Modifiers](#accessibility-modifiers)
- [Interactive Modifiers](#interactive-modifiers)
- [Advanced Modifiers](#advanced-modifiers)
- [Theme Integration](#theme-integration)
- [Best Practices](#best-practices)

---

## Core Modifier Class

The `Modifier` is a data class that holds CSS styles and HTML attributes.

### Class Definition

```kotlin
@JsExport
data class Modifier(
    val styles: Map<String, String> = emptyMap(),
    val attributes: Map<String, String> = emptyMap()
) {
    // Generic style method
    fun style(propertyName: String, value: String): Modifier

    // Batch style application
    fun withStyles(properties: Map<String, String>): Modifier

    // Core styling methods
    fun background(color: String): Modifier
    fun backgroundColor(color: String): Modifier
    fun padding(value: String): Modifier
    fun padding(top: String, right: String, bottom: String, left: String): Modifier
    fun width(value: String): Modifier
    fun height(value: String): Modifier
    fun margin(value: String): Modifier
    fun color(value: String): Modifier
    fun fontSize(value: String): Modifier
    fun fontWeight(value: String): Modifier
    fun textAlign(value: String): Modifier
    fun border(width: String, style: String, color: String): Modifier
    fun borderRadius(value: String): Modifier
    fun display(value: String): Modifier

    // Layout utilities
    fun fillMaxWidth(): Modifier
    fun fillMaxHeight(): Modifier
}
```

### Factory Function

```kotlin
// Create a new modifier
fun Modifier(): Modifier = Modifier(emptyMap(), emptyMap())
```

### Basic Usage

```kotlin
val modifier = Modifier()
    .width("200px")
    .height("100px")
    .backgroundColor("#007bff")
    .color("#ffffff")
    .padding("16px")
    .borderRadius("4px")
```

---

## CSS Units Extensions

Summon provides convenient extension properties for creating CSS values with proper units.

### Number Extensions

```kotlin
// Extension properties in code.yousef.summon.extensions
val Number.px: String      // Pixels: 16.px -> "16px"
val Number.rem: String     // Root em: 1.5.rem -> "1.5rem"
val Number.em: String      // Em units: 1.2.em -> "1.2em"
val Number.percent: String // Percentage: 50.percent -> "50%"
val Number.vw: String      // Viewport width: 100.vw -> "100vw"
val Number.vh: String      // Viewport height: 100.vh -> "100vh"
val Number.vmin: String    // Viewport minimum: 50.vmin -> "50vmin"
val Number.vmax: String    // Viewport maximum: 50.vmax -> "50vmax"
val Number.sp: String      // Scale-independent pixels: 14.sp -> "14sp"
val Number.ch: String      // Character width: 2.ch -> "2ch"
val Number.ex: String      // X-height: 3.ex -> "3ex"
val Number.pt: String      // Points: 12.pt -> "12pt"
val Number.pc: String      // Picas: 6.pc -> "6pc"
```

### Usage Examples

```kotlin
import code.yousef.summon.extensions.*

Modifier()
    .width(100.percent)
    .height(50.vh)
    .padding(16.px)
    .fontSize(1.2.rem)
    .margin(8.px, 16.px, 8.px, 16.px)
```

---

## Layout Modifiers

Layout modifiers control size, spacing, and positioning.

### Size Modifiers

```kotlin
// Basic dimensions
fun Modifier.width(value: String): Modifier
fun Modifier.height(value: String): Modifier
fun Modifier.minWidth(value: String): Modifier
fun Modifier.minHeight(value: String): Modifier
fun Modifier.maxWidth(value: String): Modifier
fun Modifier.maxHeight(value: String): Modifier

// Fill utilities
fun Modifier.fillMaxWidth(): Modifier = width("100%")
fun Modifier.fillMaxHeight(): Modifier = height("100%")
fun Modifier.fillMaxSize(): Modifier = fillMaxWidth().fillMaxHeight()
```

### Padding Modifiers

```kotlin
// Uniform padding
fun Modifier.padding(value: String): Modifier

// Directional padding
fun Modifier.padding(top: String, right: String, bottom: String, left: String): Modifier
fun Modifier.padding(vertical: String, horizontal: String): Modifier
fun Modifier.paddingTop(value: String): Modifier
fun Modifier.paddingRight(value: String): Modifier
fun Modifier.paddingBottom(value: String): Modifier
fun Modifier.paddingLeft(value: String): Modifier

// Named parameters
fun Modifier.paddingOf(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier
```

### Margin Modifiers

```kotlin
// Uniform margin
fun Modifier.margin(value: String): Modifier

// Directional margin
fun Modifier.margin(top: String, right: String, bottom: String, left: String): Modifier
fun Modifier.margin(vertical: String, horizontal: String): Modifier
fun Modifier.marginTop(value: String): Modifier
fun Modifier.marginRight(value: String): Modifier
fun Modifier.marginBottom(value: String): Modifier
fun Modifier.marginLeft(value: String): Modifier

// Named parameters
fun Modifier.marginOf(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier

// Auto margins for centering
fun Modifier.marginAuto(): Modifier = margin("auto")
fun Modifier.centerHorizontally(): Modifier = style("margin-left", "auto").style("margin-right", "auto")
```

### Position Modifiers

```kotlin
// Position type
fun Modifier.position(value: Position): Modifier
fun Modifier.position(value: String): Modifier

// Position values
fun Modifier.top(value: String): Modifier
fun Modifier.right(value: String): Modifier
fun Modifier.bottom(value: String): Modifier
fun Modifier.left(value: String): Modifier

// Z-index
fun Modifier.zIndex(value: String): Modifier
fun Modifier.zIndex(value: Int): Modifier
```

### Usage Examples

```kotlin
// Size and spacing
Modifier()
    .width(300.px)
    .minHeight(200.px)
    .padding(16.px)
    .margin(vertical = 8.px, horizontal = 16.px)

// Positioning
Modifier()
    .position(Position.Absolute)
    .top(10.px)
    .right(10.px)
    .zIndex(100)

// Centering
Modifier()
    .width(200.px)
    .centerHorizontally()
```

---

## Styling Modifiers

Visual styling modifiers for appearance and typography.

### Background and Colors

```kotlin
// Background
fun Modifier.background(color: String): Modifier
fun Modifier.backgroundColor(color: String): Modifier
fun Modifier.backgroundImage(url: String): Modifier
fun Modifier.backgroundSize(value: String): Modifier
fun Modifier.backgroundRepeat(value: String): Modifier
fun Modifier.backgroundPosition(value: String): Modifier

// Text color
fun Modifier.color(value: String): Modifier
```

### Typography Modifiers

```kotlin
// Font properties
fun Modifier.fontSize(value: String): Modifier
fun Modifier.fontWeight(value: String): Modifier
fun Modifier.fontWeight(value: FontWeight): Modifier
fun Modifier.fontFamily(value: String): Modifier
fun Modifier.fontStyle(value: String): Modifier

// Text styling
fun Modifier.textAlign(value: String): Modifier
fun Modifier.textAlign(value: TextAlign): Modifier
fun Modifier.textDecoration(value: String): Modifier
fun Modifier.textTransform(value: String): Modifier
fun Modifier.lineHeight(value: String): Modifier
fun Modifier.letterSpacing(value: String): Modifier
```

### Border Modifiers

```kotlin
// Complete border
fun Modifier.border(width: String, style: String, color: String): Modifier
fun Modifier.border(value: String): Modifier

// Border sides
fun Modifier.borderTop(value: String): Modifier
fun Modifier.borderRight(value: String): Modifier
fun Modifier.borderBottom(value: String): Modifier
fun Modifier.borderLeft(value: String): Modifier

// Border properties
fun Modifier.borderWidth(value: String): Modifier
fun Modifier.borderStyle(value: String): Modifier
fun Modifier.borderColor(value: String): Modifier
fun Modifier.borderRadius(value: String): Modifier

// Rounded corners
fun Modifier.rounded(radius: String = "4px"): Modifier
fun Modifier.roundedTop(radius: String): Modifier
fun Modifier.roundedBottom(radius: String): Modifier
```

### Display and Layout

```kotlin
// Display type
fun Modifier.display(value: String): Modifier
fun Modifier.display(value: Display): Modifier

// Visibility
fun Modifier.visibility(value: String): Modifier
fun Modifier.opacity(value: String): Modifier
fun Modifier.opacity(value: Double): Modifier

// Overflow
fun Modifier.overflow(value: String): Modifier
fun Modifier.overflow(value: Overflow): Modifier
fun Modifier.overflowX(value: String): Modifier
fun Modifier.overflowY(value: String): Modifier
```

### Flexbox Modifiers

```kotlin
// Flex container
fun Modifier.flexDirection(value: String): Modifier
fun Modifier.flexDirection(value: FlexDirection): Modifier
fun Modifier.justifyContent(value: String): Modifier
fun Modifier.justifyContent(value: JustifyContent): Modifier
fun Modifier.alignItems(value: String): Modifier
fun Modifier.alignItems(value: AlignItems): Modifier
fun Modifier.alignContent(value: String): Modifier
fun Modifier.flexWrap(value: String): Modifier
fun Modifier.gap(value: String): Modifier

// Flex items
fun Modifier.flexGrow(value: String): Modifier
fun Modifier.flexGrow(value: Number): Modifier
fun Modifier.flexShrink(value: String): Modifier
fun Modifier.flexBasis(value: String): Modifier
fun Modifier.alignSelf(value: String): Modifier
fun Modifier.order(value: Int): Modifier
```

### Usage Examples

```kotlin
// Styling
Modifier()
    .backgroundColor("#f8f9fa")
    .color("#212529")
    .fontSize(16.px)
    .fontWeight(FontWeight.Medium)
    .border("1px", "solid", "#dee2e6")
    .borderRadius(8.px)
    .opacity(0.9)

// Flexbox layout
Modifier()
    .display(Display.Flex)
    .flexDirection(FlexDirection.Column)
    .justifyContent(JustifyContent.Center)
    .alignItems(AlignItems.Center)
    .gap(16.px)
```

---

## CSS Enums

Type-safe enums for common CSS values.

### Layout Enums

```kotlin
enum class Display(val value: String) {
    None("none"), Block("block"), Inline("inline"),
    InlineBlock("inline-block"), Flex("flex"), Grid("grid"),
    InlineFlex("inline-flex"), InlineGrid("inline-grid")
}

enum class Position(val value: String) {
    Static("static"), Relative("relative"), Absolute("absolute"),
    Fixed("fixed"), Sticky("sticky")
}

enum class Overflow(val value: String) {
    Visible("visible"), Hidden("hidden"),
    Scroll("scroll"), Auto("auto")
}
```

### Flexbox Enums

```kotlin
enum class FlexDirection(val value: String) {
    Row("row"), RowReverse("row-reverse"),
    Column("column"), ColumnReverse("column-reverse")
}

enum class JustifyContent(val value: String) {
    FlexStart("flex-start"), FlexEnd("flex-end"), Center("center"),
    SpaceBetween("space-between"), SpaceAround("space-around"),
    SpaceEvenly("space-evenly")
}

enum class AlignItems(val value: String) {
    FlexStart("flex-start"), FlexEnd("flex-end"), Center("center"),
    Baseline("baseline"), Stretch("stretch")
}
```

### Typography Enums

```kotlin
enum class FontWeight(val value: String) {
    Thin("100"), ExtraLight("200"), Light("300"), Normal("400"),
    Medium("500"), SemiBold("600"), Bold("700"),
    ExtraBold("800"), Black("900")
}

enum class TextAlign(val value: String) {
    Left("left"), Right("right"), Center("center"),
    Justify("justify"), Start("start"), End("end")
}
```

### Usage Examples

```kotlin
Modifier()
    .display(Display.Flex)
    .position(Position.Relative)
    .flexDirection(FlexDirection.Column)
    .justifyContent(JustifyContent.Center)
    .alignItems(AlignItems.Center)
    .fontWeight(FontWeight.Bold)
    .textAlign(TextAlign.Center)
    .overflow(Overflow.Hidden)
```

---

## Accessibility Modifiers

Modifiers for accessibility attributes and ARIA support.

### ARIA Attributes

```kotlin
// ARIA properties
fun Modifier.ariaLabel(value: String): Modifier
fun Modifier.ariaDescribedBy(value: String): Modifier
fun Modifier.ariaLabelledBy(value: String): Modifier
fun Modifier.ariaHidden(value: Boolean): Modifier
fun Modifier.ariaExpanded(value: Boolean): Modifier
fun Modifier.ariaPressed(value: Boolean): Modifier
fun Modifier.ariaChecked(value: Boolean): Modifier
fun Modifier.ariaSelected(value: Boolean): Modifier
fun Modifier.ariaDisabled(value: Boolean): Modifier

// Role attribute
fun Modifier.role(value: String): Modifier
```

### Focus Modifiers

```kotlin
// Focus styling
fun Modifier.focus(modifier: Modifier): Modifier
fun Modifier.focusVisible(modifier: Modifier): Modifier
fun Modifier.tabIndex(value: Int): Modifier
```

### Usage Examples

```kotlin
Modifier()
    .ariaLabel("Close dialog")
    .role("button")
    .tabIndex(0)
    .focus(
        Modifier()
            .borderColor("#007bff")
            .outline("2px solid #007bff")
    )
```

---

## Interactive Modifiers

Modifiers for user interactions and state changes.

### Hover and Active States

```kotlin
// Pseudo-class modifiers
fun Modifier.hover(modifier: Modifier): Modifier
fun Modifier.active(modifier: Modifier): Modifier
fun Modifier.focus(modifier: Modifier): Modifier
fun Modifier.visited(modifier: Modifier): Modifier
fun Modifier.disabled(modifier: Modifier): Modifier
```

### Cursor Modifiers

```kotlin
// Cursor styles
fun Modifier.cursor(value: String): Modifier
fun Modifier.cursor(value: Cursor): Modifier

enum class Cursor(val value: String) {
    Auto("auto"), Default("default"), Pointer("pointer"),
    Text("text"), Wait("wait"), Help("help"), NotAllowed("not-allowed")
}
```

### Pointer Events

```kotlin
// Pointer event handling
fun Modifier.pointerEvents(value: String): Modifier
fun Modifier.userSelect(value: String): Modifier
```

### Usage Examples

```kotlin
// Interactive button
Modifier()
    .cursor(Cursor.Pointer)
    .hover(
        Modifier()
            .backgroundColor("#0056b3")
            .transform("translateY(-2px)")
    )
    .active(
        Modifier().backgroundColor("#004085")
    )
    .focus(
        Modifier().outline("2px solid #80bdff")
    )
```

---

## Advanced Modifiers

Advanced styling capabilities for complex designs.

### Transform Modifiers

```kotlin
// Transform functions
fun Modifier.transform(value: String): Modifier
fun Modifier.rotate(degrees: String): Modifier
fun Modifier.scale(value: String): Modifier
fun Modifier.translate(x: String, y: String): Modifier
fun Modifier.skew(x: String, y: String): Modifier
```

### Animation and Transition

```kotlin
// Transitions
fun Modifier.transition(value: String): Modifier
fun Modifier.transitionProperty(value: String): Modifier
fun Modifier.transitionDuration(value: String): Modifier
fun Modifier.transitionDelay(value: String): Modifier
fun Modifier.transitionTimingFunction(value: String): Modifier

// Animations
fun Modifier.animation(value: String): Modifier
fun Modifier.animationName(value: String): Modifier
fun Modifier.animationDuration(value: String): Modifier
fun Modifier.animationDelay(value: String): Modifier
fun Modifier.animationIterationCount(value: String): Modifier
```

### Shadow and Effects

```kotlin
// Shadows
fun Modifier.boxShadow(value: String): Modifier
fun Modifier.textShadow(value: String): Modifier
fun Modifier.dropShadow(value: String): Modifier

// Filters
fun Modifier.filter(value: String): Modifier
fun Modifier.backdropFilter(value: String): Modifier
```

### Grid Layout

```kotlin
// Grid container
fun Modifier.gridTemplateColumns(value: String): Modifier
fun Modifier.gridTemplateRows(value: String): Modifier
fun Modifier.gridTemplateAreas(value: String): Modifier
fun Modifier.gridGap(value: String): Modifier
fun Modifier.gridAutoColumns(value: String): Modifier
fun Modifier.gridAutoRows(value: String): Modifier

// Grid items
fun Modifier.gridColumn(value: String): Modifier
fun Modifier.gridRow(value: String): Modifier
fun Modifier.gridArea(value: String): Modifier
fun Modifier.justifySelf(value: String): Modifier
fun Modifier.alignSelf(value: String): Modifier
```

### Usage Examples

```kotlin
// Card with advanced styling
Modifier()
    .backgroundColor("#ffffff")
    .borderRadius(12.px)
    .boxShadow("0 4px 6px rgba(0, 0, 0, 0.1)")
    .transition("all 0.3s ease")
    .hover(
        Modifier()
            .transform("translateY(-4px)")
            .boxShadow("0 8px 25px rgba(0, 0, 0, 0.15)")
    )

// Grid layout
Modifier()
    .display(Display.Grid)
    .gridTemplateColumns("repeat(auto-fit, minmax(200px, 1fr))")
    .gridGap(20.px)
    .padding(20.px)
```

---

## Theme Integration

Modifiers integrate seamlessly with the Summon theme system.

### Theme Modifier Extensions

```kotlin
// Theme-aware modifiers from theme system
fun Modifier.themeColor(colorName: String): Modifier
fun Modifier.themeBackgroundColor(colorName: String): Modifier
fun Modifier.themeTextStyle(styleName: String): Modifier
fun Modifier.themePadding(spacingName: String): Modifier
fun Modifier.themeMargin(spacingName: String): Modifier
fun Modifier.themeBorderRadius(radiusName: String): Modifier
fun Modifier.themeElevation(elevationName: String): Modifier
```

### Usage with Themes

```kotlin
// Using theme values
Modifier()
    .themeBackgroundColor("primary")
    .themeColor("onPrimary")
    .themeTextStyle("button")
    .themePadding("md")
    .themeBorderRadius("sm")
    .themeElevation("md")

// Mixed with direct styling
Modifier()
    .themeBackgroundColor("surface")
    .width(100.percent)
    .hover(
        Modifier().themeBackgroundColor("surfaceVariant")
    )
```

---

## Best Practices

### 1. Use Type-Safe Enums

```kotlin
// ✅ Preferred
Modifier()
    .display(Display.Flex)
    .justifyContent(JustifyContent.Center)
    .alignItems(AlignItems.Center)

// ❌ Avoid
Modifier()
    .display("flex")
    .justifyContent("center")
    .alignItems("center")
```

### 2. Use CSS Unit Extensions

```kotlin
// ✅ Preferred
Modifier()
    .width(200.px)
    .height(100.percent)
    .padding(16.px)
    .fontSize(1.2.rem)

// ❌ Avoid
Modifier()
    .width("200px")
    .height("100%")
    .padding("16px")
    .fontSize("1.2rem")
```

### 3. Chain Modifiers Logically

```kotlin
// ✅ Group related properties
Modifier()
    // Layout
    .width(300.px)
    .height(200.px)
    .padding(20.px)
    // Appearance
    .backgroundColor("#f8f9fa")
    .borderRadius(8.px)
    .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
    // Interaction
    .cursor(Cursor.Pointer)
    .hover(Modifier().backgroundColor("#e9ecef"))
```

### 4. Use Theme Integration

```kotlin
// ✅ Theme-aware
Modifier()
    .themeBackgroundColor("primary")
    .themeColor("onPrimary")
    .themePadding("md")
    .themeBorderRadius("md")

// ❌ Hardcoded values
Modifier()
    .backgroundColor("#007bff")
    .color("#ffffff")
    .padding("16px")
    .borderRadius("8px")
```

### 5. Organize Complex Modifiers

```kotlin
// ✅ Extract complex modifiers
fun cardModifier() = Modifier()
    .backgroundColor("#ffffff")
    .borderRadius(12.px)
    .boxShadow("0 2px 8px rgba(0, 0, 0, 0.1)")
    .transition("all 0.2s ease")
    .hover(
        Modifier()
            .transform("translateY(-2px)")
            .boxShadow("0 4px 12px rgba(0, 0, 0, 0.15)")
    )

// Usage
Card(modifier = cardModifier()) {
    // Card content
}
```

## Migration Notes

When updating from older versions:

- Replace string-based CSS values with type-safe enums
- Use CSS unit extensions instead of string literals
- Update to use new theme integration functions
- Test interactive states (hover, focus, active)

## See Also

- [Theme API](theme.md) - Theme integration with modifiers
- [Components API](components.md) - Using modifiers with components
- [Animation API](animation.md) - Animation-specific modifiers
- [Accessibility API](accessibility.md) - Accessibility best practices