# Modifier System Documentation

## Problem

The current implementation of modifiers is spread across multiple files with duplicate function definitions, leading to
ambiguity errors during compilation:

- `ModifierExt.kt`
- `LayoutModifiers.kt`
- `StylingModifiers.kt`
- `AccessibilityModifiers.kt`
- `PointerEventModifiers.kt`

This causes issues like:

```
fun Modifier.maxWidth(value: String): Modifier (conflicting overloads)
fun Modifier.color(value: String): Modifier (conflicting overloads)
// etc.
```

## Solution

We've created a unified, organized approach in `ModifierUtils.kt` that consolidates all modifiers into four main
categories:

1. `LayoutModifiers` - Dimensions, positioning, and flex/grid layout
2. `StylingModifiers` - Appearance, typography, and visual effects
3. `EventModifiers` - User interactions and events
4. `AttributeModifiers` - HTML attributes and accessibility

Each category is implemented as a Kotlin object with extension functions, allowing for explicit imports that avoid
ambiguity.

## Migration Guide

### Option 1: Import Specific Functions (Recommended)

When you need a particular modifier, import it specifically from the appropriate category:

```kotlin
// Before
import code.yousef.summon.modifier.maxWidth
import code.yousef.summon.modifier.color

// After
import code.yousef.summon.modifier.LayoutModifiers.maxWidth  
import code.yousef.summon.modifier.StylingModifiers.color
```

### Option 2: Import Entire Categories

If you use multiple functions from the same category, import the entire object:

```kotlin
import code.yousef.summon.modifier.LayoutModifiers
import code.yousef.summon.modifier.LayoutModifiers.width
import code.yousef.summon.modifier.LayoutModifiers.height
// Or with the alias:
import code.yousef.summon.modifier.Layout
import code.yousef.summon.modifier.Layout.width
import code.yousef.summon.modifier.Layout.height
```

### Option 3: Legacy Method (Temporary)

We've kept the original files for backward compatibility, but this approach is no longer recommended:

```kotlin
// Not recommended, may cause ambiguity errors
import code.yousef.summon.modifier.width
```

## Best Practices

1. Always use specific imports from the categorized objects
2. When using multiple modifiers from the same category, consider importing the entire object
3. Update existing code to use the new approach when making modifications

## Related Files

- `ModifierUtils.kt` - The new organized implementation
- `ModifierExt.kt` - Legacy implementation of general modifiers
- `LayoutModifiers.kt` - Legacy implementation of layout modifiers
- `StylingModifiers.kt` - Legacy implementation of styling modifiers
- `AccessibilityModifiers.kt` - Accessibility-specific modifiers
- `PointerEventModifiers.kt` - Pointer event modifiers

## Example Usage

```kotlin
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.Layout.width
import code.yousef.summon.modifier.Layout.height
import code.yousef.summon.modifier.Styling.color
import code.yousef.summon.modifier.Styling.backgroundColor
import code.yousef.summon.modifier.Events.onClick
import code.yousef.summon.modifier.Attributes.attribute

val myModifier = Modifier()
    .width("200px")
    .height("100px")
    .color("#333")
    .backgroundColor("#fff")
    .onClick { println("Clicked!") }
    .attribute("data-testid", "my-element")
``` 