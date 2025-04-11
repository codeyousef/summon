# Using the Standard Composable Annotation

This guide demonstrates how to use the Composable annotation in your code following the consolidation described in [annotation-consolidation.md](annotation-migration.md).

## Importing and Using StandardComposable

For new code, use the `StandardComposable` type alias which provides a stable abstraction:

```kotlin
import code.yousef.summon.annotation.StandardComposable as Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Div
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.extensions.padding

/**
 * A simple component using the StandardComposable annotation.
 */
@Composable
fun MyComponent(text: String) {
    Div(
        modifier = Modifier().padding(16)
    ) {
        Text("Hello, $text")
    }
}
```

## Benefits of Using StandardComposable

1. **Consistent API**: StandardComposable provides a consistent way to define composable functions
2. **Clear semantics**: StandardComposable clearly communicates the intent of the annotation
3. **Clean imports**: The import alias pattern (`StandardComposable as Composable`) keeps your code clean and readable

## In Existing Code

When working with existing code, you have two options:

### Option 1: Keep using the existing imports (for minimal changes)

Both import patterns now point to the same underlying annotation, so existing code will continue to work:

```kotlin
import code.yousef.summon.annotation.Composable
// or
import code.yousef.summon.runtime.Composable

@Composable
fun ExistingComponent() {
    // Implementation
}
```

### Option 2: Migrate to StandardComposable (recommended for consistency)

```kotlin
import code.yousef.summon.annotation.StandardComposable as Composable

@Composable
fun ExistingComponent() {
    // Implementation
}
```

## Example: Building a Complex UI with StandardComposable

Here's a more complete example showing how to build a UI with standardized composable functions:

```kotlin
import code.yousef.summon.annotation.StandardComposable as Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Div
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.input.Button
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.extensions.padding
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.state.remember

@Composable
fun Counter() {
    val count = remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier().padding(16)
    ) {
        Text("Count: ${count.value}")
        
        Row(
            modifier = Modifier().padding(8)
        ) {
            Button(
                onClick = { count.value-- },
                text = "Decrease"
            )
            
            Button(
                onClick = { count.value++ },
                text = "Increase"
            )
        }
    }
}
```

## Best Practices

1. **For new code**: Always use the StandardComposable alias
2. **For modified code**: Consider migrating to StandardComposable if making substantial changes
3. **For existing code**: No immediate changes needed, as both import patterns work
4. **Be consistent**: Within a single file, use the same annotation approach throughout

By following these guidelines, you'll help create a more consistent and maintainable codebase. 