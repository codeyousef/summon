# Modifier Extension Consolidation Plan

## Current State
StylingModifiers.kt has excessive overloads:
- boxShadow: 9 overloads (mixing Number/String parameters)
- transition: 4+ overloads
- linearGradient: 5 overloads
- radialGradient: 14 overloads

## Consolidation Strategy

### 1. boxShadow - Reduce to 3 overloads:
```kotlin
// Basic string version (keep)
fun Modifier.boxShadow(value: String): Modifier

// Most flexible version with all parameters as strings (keep)
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    spreadRadius: String? = null,
    color: String,
    inset: Boolean = false
): Modifier

// Color object version for type safety (keep)
fun Modifier.boxShadow(
    horizontalOffset: String,
    verticalOffset: String,
    blurRadius: String,
    spreadRadius: String? = null,
    color: Color,
    inset: Boolean = false
): Modifier
```

### 2. transition - Reduce to 2 overloads:
```kotlin
// Basic string version (keep)
fun Modifier.transition(value: String): Modifier

// Full control with enums (keep)
fun Modifier.transition(
    property: TransitionProperty = TransitionProperty.All,
    duration: String = "300ms",
    timingFunction: TransitionTimingFunction = TransitionTimingFunction.Ease,
    delay: String = "0ms"
): Modifier
```

### 3. linearGradient - Reduce to 3 overloads:
```kotlin
// Basic string version
fun Modifier.linearGradient(value: String): Modifier

// Direction + color list (most flexible)
fun Modifier.linearGradient(
    direction: String,
    colors: List<String>
): Modifier

// Type-safe version with Color objects
fun Modifier.linearGradient(
    direction: String,
    colors: List<Pair<Color, String>>
): Modifier
```

### 4. radialGradient - Reduce to 3 overloads:
```kotlin
// Basic string version
fun Modifier.radialGradient(value: String): Modifier

// Shape + color list
fun Modifier.radialGradient(
    shape: String = "circle",
    position: String = "center",
    colors: List<String>
): Modifier

// Type-safe version with enums and Color objects
fun Modifier.radialGradient(
    shape: RadialGradientShape = RadialGradientShape.Circle,
    position: RadialGradientPosition = RadialGradientPosition.Center,
    colors: List<Pair<Color, String>>
): Modifier
```

## Benefits
1. Reduces API surface area from ~32 functions to ~11
2. Maintains backward compatibility with string versions
3. Provides type-safe options with Color objects and enums
4. Reduces import ambiguity issues
5. Easier to maintain and document