# StyleInjector API Reference

## Overview

The `StyleInjector` provides dynamic CSS injection for pseudo-selectors and media queries that can't be applied as
inline styles. It generates scoped CSS rules and manages their lifecycle automatically.

**Platforms**: JS, WASM  
**Package**: `code.yousef.summon.runtime`  
**Since**: 0.4.8.4

---

## Architecture

### JS Platform (`StyleInjector`)

```kotlin
object StyleInjector {
    fun injectPseudoSelectorStyles(
        element: Element,
        pseudoSelector: String,
        styles: Map<String, String>
    ): String
    
    fun injectMediaQueryStyles(
        element: Element,
        mediaQuery: String,
        styles: Map<String, String>
    ): String
    
    fun cleanupElementStyles(element: Element)
}
```

### WASM Platform (`StyleInjectorWasm`)

```kotlin
object StyleInjectorWasm {
    fun injectPseudoSelectorStyles(
        elementId: String,
        pseudoSelector: String,
        styles: Map<String, String>
    ): String
    
    fun injectMediaQueryStyles(
        elementId: String,
        mediaQuery: String,
        styles: Map<String, String>
    ): String
    
    fun cleanupElementStyles(elementId: String)
}
```

---

## Functions

### injectPseudoSelectorStyles

Injects CSS rules for pseudo-selectors like `:hover`, `:focus`, `:active`, etc.

**JS Signature**:

```kotlin
fun injectPseudoSelectorStyles(
    element: Element,
    pseudoSelector: String,
    styles: Map<String, String>
): String
```

**Parameters**:

- `element` - The DOM element to apply styles to
- `pseudoSelector` - The pseudo-selector (e.g., `:hover`, `:focus`)
- `styles` - Map of CSS property names to values

**Returns**: The generated unique class name

**Example**:

```kotlin
val element = document.getElementById("myButton")
StyleInjector.injectPseudoSelectorStyles(
    element = element,
    pseudoSelector = ":hover",
    styles = mapOf(
        "background-color" to "#0056b3",
        "transform" to "scale(1.05)"
    )
)
// Generates CSS like:
// .pseudo-hover-abc123:hover {
//   background-color: #0056b3;
//   transform: scale(1.05);
// }
```

---

### injectMediaQueryStyles

Injects CSS rules with media queries for responsive design.

**JS Signature**:

```kotlin
fun injectMediaQueryStyles(
    element: Element,
    mediaQuery: String,
    styles: Map<String, String>
): String
```

**Parameters**:

- `element` - The DOM element to apply styles to
- `mediaQuery` - The media query condition (e.g., `(min-width: 768px)`)
- `styles` - Map of CSS property names to values

**Returns**: The generated unique class name

**Example**:

```kotlin
val element = document.getElementById("myBox")
StyleInjector.injectMediaQueryStyles(
    element = element,
    mediaQuery = "(min-width: 768px)",
    styles = mapOf(
        "padding" to "24px",
        "font-size" to "18px"
    )
)
// Generates CSS like:
// @media (min-width: 768px) {
//   .media-abc123 {
//     padding: 24px;
//     font-size: 18px;
//   }
// }
```

---

### cleanupElementStyles

Removes all injected styles associated with an element.

**JS Signature**:

```kotlin
fun cleanupElementStyles(element: Element)
```

**Parameters**:

- `element` - The DOM element to clean up

**Example**:

```kotlin
val element = document.getElementById("myElement")
StyleInjector.cleanupElementStyles(element)
// Removes all <style> elements created for this element
```

---

## Integration with Modifiers

The `StyleInjector` is automatically used by the PlatformRenderer when you use pseudo-selector or media query modifiers:

```kotlin
// This automatically uses StyleInjector
Box(modifier = Modifier()
    .backgroundColor("#007bff")
    .hover(Modifier().backgroundColor("#0056b3"))
    .mediaQuery(MediaQuery.MinWidth(768)) {
        padding("24px")
    }
)
```

---

## Implementation Details

### How It Works

1. **Class Generation**: Generates unique class names like `pseudo-hover-abc123`
2. **Style Element Creation**: Creates `<style>` elements in `<head>`
3. **Element Tracking**: Tracks which styles belong to which elements
4. **Automatic Cleanup**: Removes styles when elements are unmounted

### Generated CSS Example

```css
/* For pseudo-selector */
.pseudo-hover-abc123:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}

/* For media query */
@media (min-width: 768px) {
  .media-xyz789 {
    padding: 24px;
    font-size: 18px;
  }
}
```

---

## Supported Pseudo-Selectors

The following pseudo-selectors are automatically handled:

- `:hover` - Mouse hover state
- `:focus` - Keyboard/mouse focus
- `:active` - Element being pressed/clicked
- `:focus-within` - Contains focused descendant
- `:first-child` - First child of parent
- `:last-child` - Last child of parent
- `:nth-child()` - Nth child with pattern
- `:only-child` - Only child of parent
- `:visited` - Visited links
- `:disabled` - Disabled form elements
- `:checked` - Checked checkboxes/radios

---

## Performance Considerations

### Caching

- Styles are cached and reused when possible
- Duplicate styles are not created

### Memory Management

- Automatic cleanup prevents memory leaks
- Styles are removed when elements are unmounted

### Best Practices

1. Use modifier functions instead of calling StyleInjector directly
2. Let the framework manage lifecycle
3. Avoid creating excessive unique styles
4. Group similar styles together

---

## Browser Compatibility

- **Modern Browsers**: Full support (Chrome, Firefox, Safari, Edge)
- **Legacy Browsers**: May require polyfills for older pseudo-selectors
- **SSR**: N/A (client-side only)

---

## Related APIs

- [TransitionModifiers](../modifiers/transition-modifiers.md) - Pseudo-selector modifiers
- [MediaQueryModifiers](../modifiers/media-query-modifiers.md) - Media query modifiers
- [PlatformRenderer](./platform-renderer.md) - Integration point

---

## See Also

- [Pseudo-Selector Guide](../../guides/pseudo-selectors.md)
- [Responsive Design Guide](../../guides/responsive-design.md)
- [Quick Reference](../../../QUICK_REFERENCE.md#pseudo-selectors)

