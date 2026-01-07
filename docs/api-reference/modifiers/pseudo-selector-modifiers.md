# Pseudo-Selector Modifiers API Reference

## Overview

Pseudo-selector modifiers enable styling for interactive states and structural positions that can't be applied as inline
styles. The framework automatically generates scoped CSS rules for these selectors.

**Package**: `codes.yousef.summon.modifier`  
**Since**: 1.0.0 (enhanced in 0.4.8.4)

---

## Interactive State Selectors

### hover

Applies styles when the mouse hovers over the element.

**Signature**:

```kotlin
fun Modifier.hover(hoverModifier: Modifier): Modifier
fun Modifier.hover(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
Box(modifier = Modifier()
    .backgroundColor("#007bff")
    .hover(Modifier()
        .backgroundColor("#0056b3")
        .transform("scale(1.05)")
    )
)
```

**Generated CSS**:

```css
.pseudo-hover-abc123:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}
```

---

### focus

Applies styles when the element has keyboard or mouse focus.

**Signature**:

```kotlin
fun Modifier.focus(focusModifier: Modifier): Modifier
fun Modifier.focus(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
TextField(
    modifier = Modifier()
        .border("1px solid #ccc")
        .focus(Modifier()
            .border("2px solid #007bff")
            .outline("none")
        )
)
```

**Use Cases**:

- Form field focus indicators
- Keyboard navigation highlights
- Accessibility improvements

---

### active

Applies styles when the element is being clicked or pressed.

**Signature**:

```kotlin
fun Modifier.active(activeModifier: Modifier): Modifier
fun Modifier.active(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
Button(
    modifier = Modifier()
        .backgroundColor("#007bff")
        .active(Modifier()
            .backgroundColor("#004085")
            .transform("scale(0.98)")
        )
)
```

**Use Cases**:

- Button press feedback
- Interactive elements
- Touch interaction states

---

### focusWithin

Applies styles when the element or any of its descendants have focus.

**Signature**:

```kotlin
fun Modifier.focusWithin(focusWithinModifier: Modifier): Modifier
fun Modifier.focusWithin(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
Form(modifier = Modifier()
    .border("1px solid #ddd")
    .focusWithin(Modifier()
        .border("2px solid #007bff")
        .boxShadow("0 0 0 3px rgba(0,123,255,0.25)")
    )
)
```

**Use Cases**:

- Form container highlights
- Search bars
- Composite input components

---

### visited

Applies styles to visited links (security restrictions apply).

**Signature**:

```kotlin
fun Modifier.visited(visitedModifier: Modifier): Modifier
fun Modifier.visited(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
Link(
    href = "/page",
    modifier = Modifier()
        .color("#007bff")
        .visited(Modifier()
            .color("#6c757d")
        )
)
```

**Note**: Browsers restrict which styles can be applied for security reasons.

---

### disabled

Applies styles to disabled form elements.

**Signature**:

```kotlin
fun Modifier.disabledStyles(disabledModifier: Modifier): Modifier
fun Modifier.disabledStyles(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
Button(
    modifier = Modifier()
        .backgroundColor("#007bff")
        .disabledStyles(Modifier()
            .backgroundColor("#6c757d")
            .cursor("not-allowed")
            .opacity(0.5)
        ),
    disabled = isDisabled
)
```

---

### checked

Applies styles to checked checkboxes and radio buttons.

**Signature**:

```kotlin
fun Modifier.checkedStyles(checkedModifier: Modifier): Modifier
fun Modifier.checkedStyles(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
Checkbox(
    modifier = Modifier()
        .borderColor("#ccc")
        .checkedStyles(Modifier()
            .backgroundColor("#007bff")
            .borderColor("#007bff")
        )
)
```

---

## Structural Selectors

### firstChild

Applies styles when the element is the first child of its parent.

**Signature**:

```kotlin
fun Modifier.firstChild(firstChildModifier: Modifier): Modifier
fun Modifier.firstChild(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
ListItem(modifier = Modifier()
    .borderTop("1px solid #ddd")
    .firstChild(Modifier()
        .borderTop("none")
    )
)
```

**Use Cases**:

- Remove top margin/border from first item
- Special styling for first element
- Layout adjustments

---

### lastChild

Applies styles when the element is the last child of its parent.

**Signature**:

```kotlin
fun Modifier.lastChild(lastChildModifier: Modifier): Modifier
fun Modifier.lastChild(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
ListItem(modifier = Modifier()
    .borderBottom("1px solid #ddd")
    .lastChild(Modifier()
        .borderBottom("none")
    )
)
```

**Use Cases**:

- Remove bottom margin/border from last item
- Special styling for last element
- Layout adjustments

---

### nthChild

Applies styles based on element position with a pattern.

**Signature**:

```kotlin
fun Modifier.nthChild(
    pattern: String,
    nthChildModifier: Modifier
): Modifier
```

**Parameters**:

- `pattern: String` - CSS nth-child pattern (e.g., "odd", "even", "2n+1", "3")

**Example**:

```kotlin
// Zebra striping
TableRow(modifier = Modifier()
    .backgroundColor("#ffffff")
    .nthChild("even", Modifier()
        .backgroundColor("#f8f9fa")
    )
)

// Every third item
ListItem(modifier = Modifier()
    .nthChild("3n", Modifier()
        .fontWeight("bold")
    )
)
```

**Common Patterns**:

- `"odd"` - Odd-numbered children (1, 3, 5, ...)
- `"even"` - Even-numbered children (2, 4, 6, ...)
- `"2n+1"` - Same as "odd"
- `"3n"` - Every third child (3, 6, 9, ...)
- `"n+3"` - Starting from third child
- `"5"` - Only the fifth child

---

### onlyChild

Applies styles when the element is the only child of its parent.

**Signature**:

```kotlin
fun Modifier.onlyChild(onlyChildModifier: Modifier): Modifier
fun Modifier.onlyChild(styles: Map<String, String>): Modifier
```

**Example**:

```kotlin
ListItem(modifier = Modifier()
    .padding("8px")
    .onlyChild(Modifier()
        .padding("16px")
        .textAlign("center")
    )
)
```

**Use Cases**:

- Single item special handling
- Center alignment when alone
- Different layout for single child

---

## Usage Patterns

### Button States

```kotlin
Button(
    onClick = { /* action */ },
    label = "Submit",
    modifier = Modifier()
        .backgroundColor("#007bff")
        .color("white")
        .padding("10px 20px")
        .borderRadius("4px")
        .border("none")
        .cursor("pointer")
        .transition("all", 150)
        .hover(Modifier()
            .backgroundColor("#0056b3")
            .transform("translateY(-2px)")
            .boxShadow("0 4px 8px rgba(0,0,0,0.2)")
        )
        .active(Modifier()
            .backgroundColor("#004085")
            .transform("translateY(0)")
            .boxShadow("0 2px 4px rgba(0,0,0,0.2)")
        )
        .focus(Modifier()
            .outline("none")
            .boxShadow("0 0 0 3px rgba(0,123,255,0.5)")
        )
)
```

### Form Field States

```kotlin
TextField(
    value = value,
    onValueChange = { value = it },
    modifier = Modifier()
        .width("100%")
        .padding("8px 12px")
        .border("1px solid #ced4da")
        .borderRadius("4px")
        .transition("border-color", 150)
        .focus(Modifier()
            .borderColor("#007bff")
            .outline("none")
            .boxShadow("0 0 0 0.2rem rgba(0,123,255,0.25)")
        )
        .disabledStyles(Modifier()
            .backgroundColor("#e9ecef")
            .cursor("not-allowed")
        )
)
```

### List Styling

```kotlin
List(modifier = Modifier()
    .listStyle("none")
    .padding("0")
) {
    items.forEach { item ->
        ListItem(modifier = Modifier()
            .padding("12px 16px")
            .borderBottom("1px solid #dee2e6")
            .transition("background-color", 150)
            .firstChild(Modifier()
                .borderTop("1px solid #dee2e6")
            )
            .lastChild(Modifier()
                .borderBottom("none")
            )
            .nthChild("odd", Modifier()
                .backgroundColor("#f8f9fa")
            )
            .hover(Modifier()
                .backgroundColor("#e9ecef")
                .cursor("pointer")
            )
        ) {
            Text(item.name)
        }
    }
}
```

### Card Hover Effect

```kotlin
Card(modifier = Modifier()
    .border("1px solid #dee2e6")
    .borderRadius("8px")
    .padding("20px")
    .transition("all", 200)
    .hover(Modifier()
        .boxShadow("0 4px 12px rgba(0,0,0,0.15)")
        .transform("translateY(-4px)")
        .borderColor("#007bff")
    )
)
```

---

## Implementation Details

### How It Works

1. Modifier stores pseudo-selector styles as data attributes
2. PlatformRenderer detects these attributes during rendering
3. StyleInjector generates unique class names
4. CSS rules are created and injected into `<head>`
5. Elements automatically receive the generated classes

### Generated CSS Structure

```kotlin
// This code:
Box(modifier = Modifier()
    .backgroundColor("#007bff")
    .hover(Modifier().backgroundColor("#0056b3"))
)

// Generates:
<div class="pseudo-hover-abc123" style="background-color: #007bff;"></div>

<style>
.pseudo-hover-abc123:hover {
  background-color: #0056b3;
}
</style>
```

---

## Best Practices

### 1. Use Transitions for Smooth State Changes

```kotlin
Modifier()
    .transition("all", 200)
    .hover(Modifier().backgroundColor("#0056b3"))
```

### 2. Combine Multiple States

```kotlin
Modifier()
    .hover(Modifier().backgroundColor("#0056b3"))
    .focus(Modifier().borderColor("#007bff"))
    .active(Modifier().backgroundColor("#004085"))
```

### 3. Consider Accessibility

```kotlin
// Always include focus styles for keyboard navigation
Button(modifier = Modifier()
    .focus(Modifier()
        .outline("none")
        .boxShadow("0 0 0 3px rgba(0,123,255,0.5)")
    )
)
```

### 4. Use Semantic Selectors

```kotlin
// Good: Use firstChild/lastChild for layout
.firstChild(Modifier().marginTop("0"))

// Avoid: Use nth-child for layout when not necessary
.nthChild("1", Modifier().marginTop("0"))
```

---

## Performance Considerations

### Efficient Styling

- Styles are cached and reused across similar elements
- Unique class names prevent conflicts
- Automatic cleanup prevents memory leaks

### Optimization Tips

1. Reuse modifier combinations
2. Avoid excessive unique pseudo-selector styles
3. Group related states together
4. Use standard patterns for consistent styling

---

## Browser Support

| Pseudo-Selector | Support                               |
|-----------------|---------------------------------------|
| `:hover`        | All browsers                          |
| `:focus`        | All browsers                          |
| `:active`       | All browsers                          |
| `:focus-within` | Chrome 60+, Safari 10.1+, Firefox 52+ |
| `:visited`      | All browsers (limited styles)         |
| `:disabled`     | All browsers                          |
| `:checked`      | All browsers                          |
| `:first-child`  | All browsers                          |
| `:last-child`   | All browsers                          |
| `:nth-child()`  | All browsers                          |
| `:only-child`   | All browsers                          |

---

## Related APIs

- [StyleInjector](../runtime/style-injector.md) - CSS injection system
- [TransitionModifiers](./transition-modifiers.md) - Smooth state transitions
- [InteractionModifiers](./interaction-modifiers.md) - Event handlers

---

## See Also

- [Pseudo-Selector Guide](../../guides/pseudo-selectors.md)
- [Interactive Components](../../guides/interactive-components.md)
- [Quick Reference](../../../QUICK_REFERENCE.md#pseudo-selectors)

