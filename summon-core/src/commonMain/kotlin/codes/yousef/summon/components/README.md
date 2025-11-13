# Summon Component API Reference

This document provides a reference for the standardized component APIs in the Summon framework during the transition
from interface-based composition to annotation-based composition.

## Core API Changes

The framework is transitioning from an interface-based composition model to an annotation-based composition model using
the `@Composable` annotation. This change impacts the parameter names, types, and usage patterns for all components.

### Key Parameter Standardization

| Component Type     | Old Parameter              | New Parameter                     | Notes                             |
|--------------------|----------------------------|-----------------------------------|-----------------------------------|
| Text               | `text: String`             | `text: String`                    | Consistent                        |
| Link               | Content as param           | Content as lambda                 | Now uses `{ Text(...) }` pattern  |
| TextField          | `state`                    | `value`/`onValueChange`           | Controlled component pattern      |
| Checkbox           | `state`/`onSelectedChange` | `checked`/`onCheckedChange`       | Controlled component pattern      |
| Select             | `state`/`onSelectedChange` | `value`/`onValueChange`           | Controlled component pattern      |
| Layout (Box, etc.) | `content: List<...>`       | `content: @Composable () -> Unit` | Content as lambda instead of list |

## Using the New API

### Display Components

```kotlin
// Text component
Text(
    text = "Hello World",
    modifier = Modifier().fontSize("16px")
)

// Image component
Image(
    src = "/images/photo.jpg",
    alt = "Description",
    modifier = Modifier().width("200px")
)
```

### Input Components

```kotlin
// TextField component
TextField(
    value = "Current text",
    onValueChange = { newValue -> /* Handle text change */ },
    label = "Username",
    placeholder = "Enter your username",
    modifier = Modifier().width("100%")
)

// Checkbox component
Checkbox(
    checked = true,
    onCheckedChange = { isChecked -> /* Handle state change */ },
    modifier = Modifier()
)
```

### Layout Components

```kotlin
// Box component
Box(
    modifier = Modifier().width("300px")
) {
    Text(text = "Content inside box")
}

// Row component
Row(
    modifier = Modifier().width("100%")
) {
    Text(text = "Left item")
    Text(text = "Right item")
}
```

## Known Issues

1. **Checkbox Overload Ambiguity**: There may be linter errors showing overload ambiguity for the Checkbox component.
   This is usually caused by temporary files during the transition. To work around this, use an explicitly typed lambda:

```kotlin
val checkHandler: (Boolean) -> Unit = { isChecked -> /* Handle state change */ }
Checkbox(
    checked = true,
    onCheckedChange = checkHandler,
    modifier = Modifier(),
    enabled = true
)
```

2. **Progress Component Parameters**: The Progress component is transitioning from `value`/`type` to `progress`/`type`
   parameters. Use the new signature:

```kotlin
Progress(
    progress = 0.75f, // 75%
    modifier = Modifier(),
    type = ProgressType.LINEAR
)
```

## Best Practices

1. Consistently use the new parameter names across all components
2. Prefer lambda-based content specification instead of lists
3. Use the controlled component pattern (value/onValueChange) for all input components
4. Make all UI functions `@Composable`
5. Reference `ComponentAPIExamples.kt` for correct usage examples

For more specific component documentation, refer to the KDoc comments in each component's source file. 