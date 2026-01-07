# Media Query Modifiers API Reference

## Overview

Type-safe media query support for responsive design in the Summon framework. Provides declarative helpers for applying
styles based on viewport dimensions, device characteristics, and user preferences.

**Package**: `codes.yousef.summon.modifier`  
**Since**: 0.4.8.4

---

## MediaQuery Types

All media query types extend the sealed class `MediaQuery`:

```kotlin
sealed class MediaQuery {
    abstract override fun toString(): String
}
```

### Viewport Queries

#### MinWidth

Applies styles when viewport width is at least the specified value.

```kotlin
data class MinWidth(val pixels: Int) : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.MinWidth(768) // (min-width: 768px)
```

#### MaxWidth

Applies styles when viewport width is at most the specified value.

```kotlin
data class MaxWidth(val pixels: Int) : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.MaxWidth(1024) // (max-width: 1024px)
```

#### MinHeight

Applies styles when viewport height is at least the specified value.

```kotlin
data class MinHeight(val pixels: Int) : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.MinHeight(600) // (min-height: 600px)
```

#### MaxHeight

Applies styles when viewport height is at most the specified value.

```kotlin
data class MaxHeight(val pixels: Int) : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.MaxHeight(900) // (max-height: 900px)
```

---

### Orientation Queries

#### Portrait

Applies styles when device is in portrait mode (height >= width).

```kotlin
object Portrait : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.Portrait // (orientation: portrait)
```

#### Landscape

Applies styles when device is in landscape mode (width > height).

```kotlin
object Landscape : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.Landscape // (orientation: landscape)
```

---

### User Preference Queries

#### PrefersDarkScheme

Applies styles when user prefers dark color scheme.

```kotlin
object PrefersDarkScheme : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.PrefersDarkScheme // (prefers-color-scheme: dark)
```

#### PrefersLightScheme

Applies styles when user prefers light color scheme.

```kotlin
object PrefersLightScheme : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.PrefersLightScheme // (prefers-color-scheme: light)
```

#### PrefersReducedMotion

Applies styles when user prefers reduced motion.

```kotlin
object PrefersReducedMotion : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.PrefersReducedMotion // (prefers-reduced-motion: reduce)
```

---

### Device Capability Queries

#### CanHover

Applies styles on devices that support hover (typically desktops).

```kotlin
object CanHover : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.CanHover // (hover: hover)
```

#### NoHover

Applies styles on devices without hover support (typically touch devices).

```kotlin
object NoHover : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.NoHover // (hover: none)
```

#### FinePointer

Applies styles on devices with precise pointer input (mouse).

```kotlin
object FinePointer : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.FinePointer // (pointer: fine)
```

#### CoarsePointer

Applies styles on devices with imprecise pointer input (touch screens).

```kotlin
object CoarsePointer : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.CoarsePointer // (pointer: coarse)
```

---

### Logical Operators

#### And

Combines multiple queries with AND logic (all must be true).

```kotlin
data class And(val queries: List<MediaQuery>) : MediaQuery() {
    constructor(vararg queries: MediaQuery) : this(queries.toList())
}
```

**Example**:

```kotlin
MediaQuery.And(
    MediaQuery.MinWidth(768),
    MediaQuery.Portrait
)
// (min-width: 768px) and (orientation: portrait)
```

#### Or

Combines multiple queries with OR logic (at least one must be true).

```kotlin
data class Or(val queries: List<MediaQuery>) : MediaQuery() {
    constructor(vararg queries: MediaQuery) : this(queries.toList())
}
```

**Example**:

```kotlin
MediaQuery.Or(
    MediaQuery.MinWidth(1024),
    MediaQuery.Landscape
)
// (min-width: 1024px), (orientation: landscape)
```

#### Custom

Custom media query for advanced use cases.

```kotlin
data class Custom(val query: String) : MediaQuery()
```

**Example**:

```kotlin
MediaQuery.Custom("(aspect-ratio: 16/9)")
```

---

## Modifier Functions

### mediaQuery

Applies styles conditionally based on a media query.

**Signature**:

```kotlin
fun Modifier.mediaQuery(
    query: MediaQuery,
    builder: Modifier.() -> Modifier
): Modifier
```

**Parameters**:

- `query: MediaQuery` - The media query condition
- `builder: Modifier.() -> Modifier` - DSL builder for styles to apply when query matches

**Returns**: A new `Modifier` with media query styles

**Example**:

```kotlin
Box(modifier = Modifier()
    .padding("8px")
    .mediaQuery(MediaQuery.MinWidth(768)) {
        padding("16px")
    }
    .mediaQuery(MediaQuery.MinWidth(1024)) {
        padding("24px")
    }
)
```

---

## Breakpoints Object

Predefined breakpoint constants for common screen sizes.

```kotlin
object Breakpoints {
    /** Small mobile devices (320px) */
    val XS = 320
    
    /** Mobile devices (640px) */
    val SM = 640
    
    /** Tablets (768px) */
    val MD = 768
    
    /** Small desktops (1024px) */
    val LG = 1024
    
    /** Large desktops (1280px) */
    val XL = 1280
    
    /** Extra large screens (1536px) */
    val XXL = 1536
}
```

**Usage**:

```kotlin
Box(modifier = Modifier()
    .fontSize("14px")
    .mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) {
        fontSize("16px")
    }
    .mediaQuery(MediaQuery.MinWidth(Breakpoints.LG)) {
        fontSize("18px")
    }
)
```

---

## Usage Examples

### Responsive Padding

```kotlin
Box(modifier = Modifier()
    .padding("8px")  // Mobile
    .mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) {
        padding("16px")  // Tablet
    }
    .mediaQuery(MediaQuery.MinWidth(Breakpoints.LG)) {
        padding("24px")  // Desktop
    }
)
```

### Dark Mode Support

```kotlin
Box(modifier = Modifier()
    .backgroundColor("#ffffff")
    .color("#000000")
    .mediaQuery(MediaQuery.PrefersDarkScheme) {
        backgroundColor("#1a1a1a")
        color("#ffffff")
    }
)
```

### Reduced Motion Support

```kotlin
Box(modifier = Modifier()
    .transition("all", duration = 300)
    .mediaQuery(MediaQuery.PrefersReducedMotion) {
        transition("none")
    }
)
```

### Device-Specific Styles

```kotlin
Button(
    modifier = Modifier()
        .padding("8px 16px")
        .mediaQuery(MediaQuery.NoHover) {
            padding("12px 20px")  // Larger touch target
        }
        .mediaQuery(MediaQuery.CanHover) {
            cursor("pointer")
        }
)
```

### Complex Queries

```kotlin
Box(modifier = Modifier()
    .width("100%")
    .mediaQuery(
        MediaQuery.And(
            MediaQuery.MinWidth(768),
            MediaQuery.Landscape
        )
    ) {
        width("50%")
    }
)
```

### Mobile-First Approach

```kotlin
// Start with mobile styles, enhance for larger screens
Box(modifier = Modifier()
    .fontSize("14px")
    .lineHeight("1.5")
    .padding("16px")
    // Tablet
    .mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) {
        fontSize("16px")
        padding("24px")
    }
    // Desktop
    .mediaQuery(MediaQuery.MinWidth(Breakpoints.LG)) {
        fontSize("18px")
        lineHeight("1.6")
        padding("32px")
    }
)
```

### Desktop-First Approach

```kotlin
// Start with desktop styles, override for smaller screens
Box(modifier = Modifier()
    .fontSize("18px")
    .padding("32px")
    // Tablet
    .mediaQuery(MediaQuery.MaxWidth(Breakpoints.LG)) {
        fontSize("16px")
        padding("24px")
    }
    // Mobile
    .mediaQuery(MediaQuery.MaxWidth(Breakpoints.MD)) {
        fontSize("14px")
        padding("16px")
    }
)
```

---

## Implementation Details

### How It Works

1. Media query styles are stored as data attributes
2. Platform renderer detects these attributes
3. StyleInjector generates `@media` rules with unique class names
4. CSS is injected into document `<head>`
5. Elements automatically get the generated class

### Generated CSS Example

```kotlin
// This code:
Box(modifier = Modifier()
    .padding("8px")
    .mediaQuery(MediaQuery.MinWidth(768)) {
        padding("16px")
    }
)

// Generates CSS like:
@media (min-width: 768px) {
  .media-abc123 {
    padding: 16px;
  }
}
```

---

## Best Practices

### 1. Choose an Approach

```kotlin
// Mobile-first (recommended)
Modifier()
    .fontSize("14px")
    .mediaQuery(MediaQuery.MinWidth(768)) { fontSize("16px") }

// Desktop-first
Modifier()
    .fontSize("18px")
    .mediaQuery(MediaQuery.MaxWidth(768)) { fontSize("14px") }
```

### 2. Use Predefined Breakpoints

```kotlin
// Good
.mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) { }

// Avoid magic numbers
.mediaQuery(MediaQuery.MinWidth(768)) { }
```

### 3. Group Related Queries

```kotlin
// Good
.mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) {
    padding("16px")
    fontSize("16px")
    lineHeight("1.6")
}

// Avoid
.mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) { padding("16px") }
.mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) { fontSize("16px") }
.mediaQuery(MediaQuery.MinWidth(Breakpoints.MD)) { lineHeight("1.6") }
```

### 4. Consider User Preferences

```kotlin
// Always provide fallbacks for dark mode
.backgroundColor("#ffffff")
.mediaQuery(MediaQuery.PrefersDarkScheme) {
    backgroundColor("#1a1a1a")
}

// Respect reduced motion
.transition("all", 300)
.mediaQuery(MediaQuery.PrefersReducedMotion) {
    transition("none")
}
```

---

## Performance Considerations

### Efficient Media Queries

- Use standard breakpoints to maximize style reuse
- Avoid excessive unique media queries
- Group related styles together

### CSS Generation

- Styles are cached and reused
- Duplicate media queries share the same `@media` rule
- Automatic cleanup prevents memory leaks

---

## Browser Support

| Query Type        | Support                               |
|-------------------|---------------------------------------|
| Width/Height      | All modern browsers                   |
| Orientation       | All modern browsers                   |
| Dark/Light Scheme | Chrome 76+, Safari 12.1+, Firefox 67+ |
| Reduced Motion    | Chrome 74+, Safari 10.1+, Firefox 63+ |
| Hover             | Chrome 38+, Safari 9+, Firefox 64+    |
| Pointer           | Chrome 41+, Safari 9+, Firefox 64+    |

---

## Related APIs

- [StyleInjector](../runtime/style-injector.md) - CSS injection system
- [Responsive Layout](../components/layout/responsive-layout.md) - Responsive components
- [Breakpoints Guide](../../guides/responsive-design.md) - Responsive design patterns

---

## See Also

- [Responsive Design Guide](../../guides/responsive-design.md)
- [Dark Mode Guide](../../guides/dark-mode.md)
- [Quick Reference](../../../QUICK_REFERENCE.md#media-queries)

