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
- [Pseudo-Selector Modifiers](#pseudo-selector-modifiers)
- [CSS Variables](#css-variables)
- [Media Queries](#media-queries)
- [Breakpoint Shortcuts](#breakpoint-shortcuts) ⭐ NEW (v0.7.0)
- [Scoped Style Selectors](#scoped-style-selectors) ⭐ NEW (v0.7.0)
- [Scroll Modifiers](#scroll-modifiers)
- [Media Element Modifiers](#media-element-modifiers)
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
// Extension properties in codes.yousef.summon.extensions
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
import codes.yousef.summon.extensions.*

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

// Convenience helper
fun Modifier.centerHorizontally(vertical: String = "0"): Modifier
```

### CSS Function Helpers

```kotlin
fun cssMin(vararg values: CssValue): CssValue
fun cssMax(vararg values: CssValue): CssValue
fun cssClamp(min: CssValue, preferred: CssValue, max: CssValue): CssValue
```

Use the existing numeric extensions (e.g., `22.px`, `4.vw`, `48.px`) from `codes.yousef.summon.extensions.*` to produce
`CssValue` instances that can be passed to these helpers and then into modifiers like `fontSize`, `padding`, or `width`.

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

// Inset shorthand
fun Modifier.inset(value: String): Modifier
fun Modifier.inset(vertical: String, horizontal: String): Modifier
fun Modifier.inset(top: String, right: String, bottom: String, left: String): Modifier
fun Modifier.positionInset(
    top: String? = null,
    right: String? = null,
    bottom: String? = null,
    left: String? = null
): Modifier

// Aspect ratio
fun Modifier.aspectRatio(ratio: Number): Modifier
fun Modifier.aspectRatio(width: Number, height: Number): Modifier
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
fun Modifier.backgroundLayers(vararg layers: GradientLayer): Modifier
fun Modifier.backgroundLayers(builder: GradientLayerScope.() -> Unit): Modifier
fun Modifier.backgroundBlendModes(vararg modes: BlendMode): Modifier
fun Modifier.backgroundBlendModes(value: String): Modifier
fun Modifier.backgroundClip(value: BackgroundClip): Modifier
fun Modifier.backgroundClipText(includeWebkitPrefix: Boolean = true): Modifier

// Text color
fun Modifier.color(value: String): Modifier
```

`GradientLayerScope` exposes `radialGradient`, `linearGradient`, `conicGradient`, and `image/url` helpers so complex
multi-layer
backgrounds can be expressed with type-safe builders instead of composing raw CSS strings.

### Typography Modifiers

```kotlin
// Font properties
fun Modifier.fontSize(value: String): Modifier
fun Modifier.fontSize(value: Number): Modifier
fun Modifier.fontWeight(value: String): Modifier
fun Modifier.fontWeight(value: FontWeight): Modifier
fun Modifier.fontFamily(value: String): Modifier
fun Modifier.fontStyle(value: String): Modifier
fun Modifier.fontStyle(value: FontStyle): Modifier

// Text styling
fun Modifier.textAlign(value: String): Modifier
fun Modifier.textAlign(value: TextAlign): Modifier
fun Modifier.textDecoration(value: String): Modifier
fun Modifier.textDecoration(value: TextDecoration): Modifier
fun Modifier.textDecoration(vararg values: TextDecoration): Modifier
fun Modifier.textTransform(value: String): Modifier
fun Modifier.textTransform(value: TextTransform): Modifier
fun Modifier.lineHeight(value: String): Modifier
fun Modifier.lineHeight(value: Number): Modifier
fun Modifier.letterSpacing(value: String): Modifier
fun Modifier.letterSpacing(value: Number): Modifier
fun Modifier.whiteSpace(value: String): Modifier
fun Modifier.whiteSpace(value: WhiteSpace): Modifier
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
fun Modifier.flex(grow: Number, shrink: Number, basis: String): Modifier

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

### Media Enums

```kotlin
enum class ObjectFit(val value: String) {
    Fill("fill"), Contain("contain"), Cover("cover"),
    None("none"), ScaleDown("scale-down")
}

enum class Cursor(val value: String) {
    Auto("auto"), Default("default"), Pointer("pointer"),
    Text("text"), Wait("wait"), Help("help"), NotAllowed("not-allowed"),
    Move("move"), Crosshair("crosshair"), Grab("grab"), Grabbing("grabbing"),
    // ... and more
}
```

### Type-Safe Modifier Functions

All CSS enum values have corresponding type-safe modifier functions:

```kotlin
// Layout
fun Modifier.display(value: Display): Modifier
fun Modifier.position(value: Position): Modifier
fun Modifier.overflow(value: Overflow): Modifier
fun Modifier.flexDirection(value: FlexDirection): Modifier
fun Modifier.flexWrap(value: FlexWrap): Modifier
fun Modifier.justifyContent(value: JustifyContent): Modifier
fun Modifier.alignItems(value: AlignItems): Modifier
fun Modifier.alignSelf(value: AlignSelf): Modifier
fun Modifier.alignContent(value: AlignContent): Modifier

// Typography
fun Modifier.fontWeight(value: FontWeight): Modifier
fun Modifier.fontWeight(value: Int): Modifier  // numeric: 100-900

// Colors (type-safe)
fun Modifier.color(value: Color): Modifier
fun Modifier.backgroundColor(value: Color): Modifier
fun Modifier.background(value: Color): Modifier

// Media
fun Modifier.objectFit(value: ObjectFit): Modifier
fun Modifier.cursor(value: Cursor): Modifier
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

### Pseudo-element Modifiers

```kotlin
fun Modifier.before(
    ensurePositionRelative: Boolean = true,
    content: String? = null,
    builder: Modifier.() -> Modifier
): Modifier

fun Modifier.after(
    ensurePositionRelative: Boolean = true,
    content: String? = null,
    builder: Modifier.() -> Modifier
): Modifier
```

Both helpers collect modifier styles for their respective pseudo-element and automatically inject a `data-summon-id` +
scoped `<style>` rule during rendering, which keeps background glows and grain overlays encapsulated without manual DOM
nodes.

### Shadow and Effects

```kotlin
// Shadows
fun Modifier.boxShadow(value: String): Modifier
fun Modifier.textShadow(value: String): Modifier
fun Modifier.dropShadow(value: String): Modifier

// Filters
fun Modifier.filter(value: String): Modifier
fun Modifier.backdropFilter(value: String): Modifier
fun Modifier.filter(function: FilterFunction, value: String): Modifier
fun Modifier.filter(function: FilterFunction, value: Number, unit: String): Modifier
fun Modifier.filter(builder: FilterBuilder.() -> Unit): Modifier
fun Modifier.backdropFilter(builder: FilterBuilder.() -> Unit): Modifier
fun Modifier.mixBlendMode(value: String): Modifier
fun Modifier.mixBlendMode(value: BlendMode): Modifier
```

The builder overloads accept blur/brightness/contrast/hue-rotate/drop-shadow steps, mirroring the CSS filter functions
available to the raw string helpers.

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

## Pseudo-Selector Modifiers

Apply styles based on element state and structural position using pseudo-selectors.

### Interactive State Selectors

```kotlin
// Hover state
Box(
    modifier = Modifier()
        .backgroundColor("#f0f0f0")
        .hover(Modifier().backgroundColor("#e0e0e0"))
)

// Focus state
TextField(
    modifier = Modifier()
        .outlineColor("gray")
        .focus(Modifier().outlineColor("blue"))
)

// Active state (being clicked)
Button(
    modifier = Modifier()
        .backgroundColor("#007bff")
        .active(Modifier().backgroundColor("#0056b3"))
)

// Focus-within (element or descendants have focus)
Box(
    modifier = Modifier()
        .borderColor("gray")
        .focusWithin(Modifier().borderColor("blue"))
)
```

### Structural Selectors

```kotlin
// First child
Box(
    modifier = Modifier()
        .firstChild(Modifier().marginTop("0px"))
)

// Last child
Box(
    modifier = Modifier()
        .lastChild(Modifier().marginBottom("0px"))
)

// Nth child
Box(
    modifier = Modifier()
        .nthChild("2n", Modifier().backgroundColor("#f5f5f5")) // Even rows
        .nthChild("odd", Modifier().backgroundColor("white"))   // Odd rows
)

// Only child
Box(
    modifier = Modifier()
        .onlyChild(Modifier().margin("0 auto"))
)
```

### State Selectors

```kotlin
// Visited links
Link(
    modifier = Modifier()
        .color("blue")
        .visited(Modifier().color("purple"))
)

// Disabled state
Button(
    modifier = Modifier()
        .disabledStyles(Modifier()
            .backgroundColor("gray")
            .cursor("not-allowed")
        )
)

// Checked state (checkboxes/radios)
Checkbox(
    modifier = Modifier()
        .checkedStyles(Modifier().backgroundColor("blue"))
)
```

### Using Map Syntax

All pseudo-selectors support map-based syntax:

```kotlin
Modifier().hover(mapOf(
    "background-color" to "#e0e0e0",
    "transform" to "scale(1.05)"
))
```

---

## CSS Variables

Define and use CSS custom properties (CSS variables) for theming and dynamic styling.

### Defining Variables

```kotlin
// Define on a container
Box(
    modifier = Modifier()
        .cssVar("primary-color", "#007bff")
        .cssVar("spacing", "16px")
        .cssVar("border-radius", "8px")
) {
    // Children can use these variables
}

// Define multiple variables
Box(
    modifier = Modifier()
        .cssVars(mapOf(
            "primary-color" to "#007bff",
            "secondary-color" to "#6c757d",
            "success-color" to "#28a745"
        ))
)
```

### Using Variables

```kotlin
// Use variables in styles
Box(
    modifier = Modifier()
        .backgroundColor(cssVar("primary-color"))
        .padding(cssVar("spacing"))
        .borderRadius(cssVar("border-radius"))
)

// With fallback values
Box(
    modifier = Modifier()
        .color(cssVar("text-color", "#000000"))
)
```

### Theming Pattern

```kotlin
@Composable
fun ThemedApp() {
    val isDark = remember { mutableStateOf(false) }
    
    val theme = if (isDark.value) {
        mapOf(
            "bg-primary" to "#1a1a1a",
            "text-primary" to "#ffffff",
            "surface" to "#2a2a2a"
        )
    } else {
        mapOf(
            "bg-primary" to "#ffffff",
            "text-primary" to "#000000",
            "surface" to "#f5f5f5"
        )
    }
    
    Box(modifier = Modifier().cssVars(theme)) {
        // All children use theme variables
        Content()
    }
}
```

---

## Media Queries

Apply responsive styles based on viewport dimensions and device characteristics.

### Viewport Breakpoints

```kotlin
// Mobile-first approach
Box(
    modifier = Modifier()
        .padding("8px")
        .mediaQuery(MediaQuery.MinWidth(768)) {
            padding("16px")
        }
        .mediaQuery(MediaQuery.MinWidth(1024)) {
            padding("24px")
        }
)

// Max-width queries
Box(
    modifier = Modifier()
        .display("block")
        .mediaQuery(MediaQuery.MaxWidth(767)) {
            display("none") // Hide on mobile
        }
)
```

### Device Orientation

```kotlin
Box(
    modifier = Modifier()
        .mediaQuery(MediaQuery.Portrait) {
            flexDirection("column")
        }
        .mediaQuery(MediaQuery.Landscape) {
            flexDirection("row")
        }
)
```

### User Preferences

```kotlin
// Dark mode support
Box(
    modifier = Modifier()
        .backgroundColor("#ffffff")
        .color("#000000")
        .mediaQuery(MediaQuery.PrefersDarkScheme) {
            backgroundColor("#1a1a1a")
            color("#ffffff")
        }
)

// Reduced motion
Box(
    modifier = Modifier()
        .transition("all", 300)
        .mediaQuery(MediaQuery.PrefersReducedMotion) {
            transition("none")
        }
)
```

### Device Capabilities

```kotlin
// Hover-capable devices
Box(
    modifier = Modifier()
        .mediaQuery(MediaQuery.CanHover) {
            // Add hover effects only on devices that support it
        }
        .mediaQuery(MediaQuery.NoHover) {
            // Touch-optimized styles
            padding("12px") // Larger touch targets
        }
)

// Pointer precision
Box(
    modifier = Modifier()
        .mediaQuery(MediaQuery.FinePointer) {
            // Mouse/trackpad styles
        }
        .mediaQuery(MediaQuery.CoarsePointer) {
            // Touch styles
            minHeight("44px") // Larger touch targets
        }
)
```

### Common Breakpoints

Use the predefined `Breakpoints` constants:

```kotlin
import codes.yousef.summon.modifier.Breakpoints

Box(
    modifier = Modifier()
        .mediaQuery(MediaQuery.MinWidth(Breakpoints.SM)) { /* Tablet */ }
        .mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) { /* Desktop */ }
        .mediaQuery(MediaQuery.MinWidth(Breakpoints.LG)) { /* Large desktop */ }
)
```

### Complex Queries

```kotlin
// Combine multiple conditions with AND
modifier.mediaQuery(
    MediaQuery.And(
        MediaQuery.MinWidth(768),
        MediaQuery.MaxWidth(1024),
        MediaQuery.Landscape
    )
) {
    // Styles for tablets in landscape
}

// Multiple conditions with OR
modifier.mediaQuery(
    MediaQuery.Or(
        MediaQuery.MaxWidth(767),
        MediaQuery.Portrait
    )
) {
    // Mobile or any device in portrait
}
```

---

## Breakpoint Shortcuts

Convenient shorthand modifiers for responsive breakpoints. Added in v0.7.0.

**Package**: `codes.yousef.summon.modifier`

### Mobile-First Breakpoints (min-width)

Apply styles starting at a minimum viewport width and up.

```kotlin
fun Modifier.xs(builder: Modifier.() -> Modifier): Modifier   // 320px+
fun Modifier.sm(builder: Modifier.() -> Modifier): Modifier   // 640px+
fun Modifier.md(builder: Modifier.() -> Modifier): Modifier   // 768px+
fun Modifier.lg(builder: Modifier.() -> Modifier): Modifier   // 1024px+
fun Modifier.xl(builder: Modifier.() -> Modifier): Modifier   // 1280px+
fun Modifier.xxl(builder: Modifier.() -> Modifier): Modifier  // 1536px+
```

**Example:**

```kotlin
// Mobile-first approach: base styles for mobile, override for larger screens
Box(
    modifier = Modifier()
        .padding(8.px)           // Mobile default
        .fontSize(14.px)
        .sm { padding(12.px) }   // 640px+
        .md {                    // 768px+
            padding(16.px)
            fontSize(16.px)
        }
        .lg { padding(24.px) }   // 1024px+
        .xl { padding(32.px) }   // 1280px+
)
```

### Desktop-First Breakpoints (max-width)

Apply styles below a maximum viewport width.

```kotlin
fun Modifier.smDown(builder: Modifier.() -> Modifier): Modifier   // < 640px
fun Modifier.mdDown(builder: Modifier.() -> Modifier): Modifier   // < 768px
fun Modifier.lgDown(builder: Modifier.() -> Modifier): Modifier   // < 1024px
fun Modifier.xlDown(builder: Modifier.() -> Modifier): Modifier   // < 1280px
fun Modifier.xxlDown(builder: Modifier.() -> Modifier): Modifier  // < 1536px
```

**Example:**

```kotlin
// Desktop-first approach: base styles for desktop, override for smaller screens
Box(
    modifier = Modifier()
        .display(Display.Block)
        .width(960.px)
        .lgDown { width(100.percent) }  // Full width below 1024px
        .mdDown { display(Display.None) }  // Hide on tablets and below
)
```

### Range Breakpoints (exact ranges)

Apply styles only within a specific viewport range.

```kotlin
fun Modifier.smOnly(builder: Modifier.() -> Modifier): Modifier   // 640px - 767px
fun Modifier.mdOnly(builder: Modifier.() -> Modifier): Modifier   // 768px - 1023px
fun Modifier.lgOnly(builder: Modifier.() -> Modifier): Modifier   // 1024px - 1279px
fun Modifier.xlOnly(builder: Modifier.() -> Modifier): Modifier   // 1280px - 1535px

// Custom range
fun Modifier.breakpointBetween(min: Int, max: Int, builder: Modifier.() -> Modifier): Modifier
```

**Example:**

```kotlin
// Target specific device classes
Box(
    modifier = Modifier()
        .backgroundColor(Color.Blue)           // Default
        .smOnly { backgroundColor(Color.Red) }  // Only small screens
        .mdOnly { backgroundColor(Color.Green) }  // Only tablets
        .lgOnly { backgroundColor(Color.Purple) }  // Only desktops
        .breakpointBetween(400, 600) {          // Custom range
            fontSize(13.px)
        }
)
```

### Breakpoint Constants

```kotlin
object Breakpoints {
    val XS = 320    // Small mobile devices
    val SM = 640    // Mobile devices
    val MD = 768    // Tablets
    val LG = 1024   // Small desktops
    val XL = 1280   // Large desktops
    val XXL = 1536  // Extra large screens
}
```

### Comparison with mediaQuery

The breakpoint shortcuts are syntactic sugar for common `mediaQuery` patterns:

```kotlin
// These are equivalent:
Modifier().md { padding(16.px) }
Modifier().mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) { padding(16.px) }

// These are equivalent:
Modifier().mdDown { display(Display.None) }
Modifier().mediaQuery(MediaQuery.MaxWidth(Breakpoints.MD - 1)) { display(Display.None) }

// These are equivalent:
Modifier().mdOnly { color("red") }
Modifier().breakpointBetween(Breakpoints.MD, Breakpoints.LG - 1) { color("red") }
```

---

## Scoped Style Selectors

CSS combinator selectors for styling nested content. Added in v0.7.0.

**Package**: `codes.yousef.summon.modifier`

### Descendant Selector

Styles any matching element anywhere inside the component (CSS space combinator).

```kotlin
fun Modifier.descendant(selector: String, builder: Modifier.() -> Modifier): Modifier
```

**Example:**

```kotlin
// Style all paragraphs anywhere inside this box
Box(
    modifier = Modifier()
        .descendant("p") { color("gray").lineHeight("1.6") }
        .descendant(".highlight") { backgroundColor("yellow") }
        .descendant("a") {
            color("blue")
            textDecoration("underline")
        }
) {
    P { Text("This is gray") }
    Div {
        P { Text("This is also gray - nested") }
        Span(modifier = Modifier().className("highlight")) {
            Text("Highlighted!")
        }
    }
}
```

### Child Selector

Styles only direct children (CSS `>` combinator).

```kotlin
fun Modifier.child(selector: String, builder: Modifier.() -> Modifier): Modifier
```

**Example:**

```kotlin
// Style only direct paragraph children
Box(
    modifier = Modifier()
        .child("p") { marginBottom(16.px) }
) {
    P { Text("Has margin - direct child") }
    Div {
        P { Text("No margin - not direct child") }
    }
}
```

### Adjacent Sibling Selector

Styles the immediately following sibling (CSS `+` combinator).

```kotlin
fun Modifier.adjacentSibling(selector: String, builder: Modifier.() -> Modifier): Modifier
```

**Example:**

```kotlin
// Remove top margin from paragraph immediately after heading
H2(
    modifier = Modifier()
        .adjacentSibling("p") { marginTop("0") }
) {
    Text("Section Title")
}
P { Text("This has no top margin") }
P { Text("This has normal margin") }
```

### General Sibling Selector

Styles all following siblings (CSS `~` combinator).

```kotlin
fun Modifier.generalSibling(selector: String, builder: Modifier.() -> Modifier): Modifier
```

**Example:**

```kotlin
// Style all paragraphs that follow this heading
H2(
    modifier = Modifier()
        .generalSibling("p") { fontSize(14.px).textIndent("1em") }
) {
    Text("Section Title")
}
P { Text("14px with indent") }
P { Text("Also 14px with indent") }
Div { Text("Not affected - different element") }
P { Text("Still 14px with indent") }
```

### Convenience Functions

```kotlin
// Common element selectors
fun Modifier.paragraphStyle(builder: Modifier.() -> Modifier): Modifier  // descendant("p")
fun Modifier.linkStyle(builder: Modifier.() -> Modifier): Modifier       // descendant("a")
fun Modifier.listItemStyle(builder: Modifier.() -> Modifier): Modifier   // descendant("li")
fun Modifier.headingStyle(builder: Modifier.() -> Modifier): Modifier    // descendant("h1-h6")

// Class selectors
fun Modifier.classStyle(className: String, builder: Modifier.() -> Modifier): Modifier

// Position selectors
fun Modifier.firstChildStyle(builder: Modifier.() -> Modifier): Modifier
fun Modifier.lastChildStyle(builder: Modifier.() -> Modifier): Modifier
```

**Example:**

```kotlin
// Style all content within an article
Article(
    modifier = Modifier()
        .paragraphStyle {
            marginBottom(16.px)
            lineHeight("1.6")
        }
        .linkStyle {
            color("blue")
            hover(Modifier().textDecoration("underline"))
        }
        .headingStyle {
            fontWeight(FontWeight.Bold)
            marginTop(24.px)
        }
        .firstChildStyle { marginTop("0") }
        .lastChildStyle { marginBottom("0") }
) {
    H2 { Text("Article Title") }
    P { Text("First paragraph...") }
    P {
        Text("With a ")
        A(href = "/link") { Text("link") }
        Text(" inside.")
    }
}
```

### Implementation Note

Scoped styles are stored as data attributes and processed by platform renderers:

- **JS/WASM**: StyleInjector generates CSS rules with unique class names
- **JVM/SSR**: Generates inline `<style>` elements with scoped rules

---

## Scroll Modifiers

Control scroll behavior and handle scroll events.

### Scroll Events

```kotlin
Box(
    modifier = Modifier()
        .height("400px")
        .overflowY("scroll")
        .onScroll("handleScroll(event)")
)
```

### Scroll Behavior

```kotlin
// Smooth scrolling
Box(
    modifier = Modifier()
        .scrollBehavior(ScrollBehavior.SMOOTH)
        .overflowY("auto")
)
```

### Scroll Snapping

```kotlin
// Container with snap points
Box(
    modifier = Modifier()
        .scrollSnapType(ScrollSnapType.Y_MANDATORY)
        .overflowY("scroll")
) {
    // Child elements
    repeat(10) {
        Box(
            modifier = Modifier()
                .scrollSnapAlign(ScrollSnapAlign.START)
                .height("100vh")
        ) {
            Text("Section $it")
        }
    }
}
```

### Overflow Control

```kotlin
// Vertical scrolling only
Modifier().overflowY("auto").overflowX("hidden")

// Horizontal scrolling
Modifier().overflowX("scroll").overflowY("hidden")

// Both directions
Modifier().overflow("auto")
```

### Scroll Margin and Padding

```kotlin
// Scroll margin (space between snap point and viewport)
Modifier().scrollMargin("20px")

// Scroll padding (offset for snap alignment)
Modifier().scrollPadding("20px")
```

---

## Media Element Modifiers

Special modifiers for Video and Audio elements. These modifiers enable advanced media control behaviors.

### Pause on Scroll

Automatically pause media when it scrolls out of view:

```kotlin
Video(
    src = "video.mp4",
    modifier = Modifier()
        .pauseOnScroll()
)

// With custom threshold (percentage of element visible)
Audio(
    src = "audio.mp3", 
    modifier = Modifier()
        .pauseOnScroll(threshold = 0.5) // Pause when less than 50% visible
)
```

### Lazy Loading

Defer media loading until the element approaches the viewport:

```kotlin
// Lazy load with Intersection Observer
Video(
    src = "large-video.mp4",
    modifier = Modifier()
        .lazyLoad() // Default margin: 200px
)

// Custom margin for earlier loading
Video(
    src = "video.mp4",
    modifier = Modifier()
        .lazyLoad(margin = "400px") // Start loading 400px before visible
)

// Native lazy loading (uses browser's loading="lazy")
Image(
    src = "image.jpg",
    modifier = Modifier()
        .nativeLazyLoad()
)
```

### Aspect Ratio

Maintain consistent aspect ratios for media elements:

```kotlin
// 16:9 widescreen
Video(
    src = "video.mp4",
    modifier = Modifier()
        .aspectRatio(16, 9)
)

// 4:3 standard
Video(
    src = "old-video.mp4",
    modifier = Modifier()
        .aspectRatio(4, 3)
)

// 1:1 square (for Instagram-style content)
Video(
    src = "square.mp4",
    modifier = Modifier()
        .aspectRatio(1, 1)
)
```

### Responsive Media

Create fluid, responsive media containers:

```kotlin
// Full-width responsive video
Video(
    src = "video.mp4",
    modifier = Modifier()
        .responsiveMedia()
)

// Responsive with max dimensions
Video(
    src = "video.mp4",
    modifier = Modifier()
        .responsiveMedia(maxWidth = "800px", maxHeight = "600px")
)
```

### Combined Media Modifiers

```kotlin
// Optimized video component
Video(
    src = "hero-video.mp4",
    modifier = Modifier()
        .aspectRatio(16, 9)
        .responsiveMedia(maxWidth = "1200px")
        .lazyLoad(margin = "300px")
        .pauseOnScroll(threshold = 0.25)
        .borderRadius(8.px)
        .boxShadow("0 4px 12px rgba(0,0,0,0.15)")
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
- [Media Modifiers](modifiers/media.md) - Detailed media modifier reference
- [Video Component](components/display/Video.md) - Video component documentation
- [Audio Component](components/display/Audio.md) - Audio component documentation
- [Animation API](animation.md) - Animation-specific modifiers
- [Accessibility API](accessibility.md) - Accessibility best practices
