# PortalManager API Reference

## Overview

The `PortalManager` enables DOM teleportation, allowing elements to be rendered in different parts of the DOM tree while
maintaining their component context. This is useful for modals, tooltips, and overlays that need to escape layout
constraints.

**Platform**: JS only  
**Package**: `code.yousef.summon.runtime`  
**Since**: 0.4.8.3

---

## API

```kotlin
object PortalManager {
    fun portal(element: Element, targetSelector: String)
    fun unportal(element: Element)
    fun isPortaled(element: Element): Boolean
    fun getPortalTarget(element: Element): String?
}
```

---

## Functions

### portal

Moves a DOM element to a different container in the document.

**Signature**:

```kotlin
fun portal(element: Element, targetSelector: String)
```

**Parameters**:

- `element: Element` - The DOM element to move
- `targetSelector: String` - CSS selector for the target container (e.g., `"body"`, `"#modal-root"`)

**Behavior**:

- Stores the original parent for later restoration
- Finds or creates the target container
- Moves the element to the target
- Tracks the portal relationship

**Example**:

```kotlin
val modalElement = document.getElementById("myModal")
PortalManager.portal(modalElement, "body")
// Element is now a child of <body>
```

---

### unportal

Returns a portaled element to its original parent.

**Signature**:

```kotlin
fun unportal(element: Element)
```

**Parameters**:

- `element: Element` - The portaled element to restore

**Behavior**:

- Removes element from portal container
- Returns element to original parent
- Cleans up tracking information

**Example**:

```kotlin
val modalElement = document.getElementById("myModal")
PortalManager.unportal(modalElement)
// Element is back in its original location
```

---

### isPortaled

Checks if an element is currently portaled.

**Signature**:

```kotlin
fun isPortaled(element: Element): Boolean
```

**Parameters**:

- `element: Element` - The element to check

**Returns**: `true` if the element is portaled, `false` otherwise

**Example**:

```kotlin
if (PortalManager.isPortaled(modalElement)) {
    println("Element is currently portaled")
}
```

---

### getPortalTarget

Gets the target selector for a portaled element.

**Signature**:

```kotlin
fun getPortalTarget(element: Element): String?
```

**Parameters**:

- `element: Element` - The portaled element

**Returns**: The target selector string, or `null` if not portaled

**Example**:

```kotlin
val target = PortalManager.getPortalTarget(modalElement)
// Returns "body" if element was portaled to body
```

---

## Usage with Portal Component

The `Portal` component automatically uses `PortalManager`:

```kotlin
@Composable
fun MyModal(isOpen: Boolean, onClose: () -> Unit) {
    if (isOpen) {
        Portal(target = "body") {
            Box(modifier = Modifier()
                .position(Position.Fixed)
                .top("50%")
                .left("50%")
                .transform("translate(-50%, -50%)")
                .zIndex(9999)
            ) {
                Text("Modal Content")
                Button(onClick = onClose, label = "Close")
            }
        }
    }
}
```

---

## Target Selectors

### Supported Selector Types

1. **Element ID**: `"#modal-root"`
    - Targets element with specific ID
    - Creates container if doesn't exist

2. **Class Name**: `".portal-container"`
    - Targets first element with class
    - Creates container if doesn't exist

3. **Document Body**: `"body"`
    - Most common target
    - Always available

4. **Custom Selector**: Any valid CSS selector
    - Uses `document.querySelector()`
    - Creates generic container if not found

### Container Creation

If a target doesn't exist, `PortalManager` automatically creates it:

```kotlin
// For #modal-root
<div id="modal-root"></div>

// For .portal-container
<div class="portal-container"></div>

// For custom selectors
<div data-portal-container="custom-selector"></div>
```

---

## Common Use Cases

### Modal Dialog

```kotlin
@Composable
fun FullScreenModal(content: @Composable () -> Unit) {
    Portal(target = "body") {
        // Overlay
        Box(modifier = Modifier()
            .position(Position.Fixed)
            .top("0")
            .left("0")
            .right("0")
            .bottom("0")
            .backgroundColor("rgba(0,0,0,0.5)")
            .zIndex(9999)
        ) {
            // Modal content
            Box(modifier = Modifier()
                .backgroundColor("white")
                .borderRadius("8px")
                .padding("32px")
            ) {
                content()
            }
        }
    }
}
```

### Tooltip

```kotlin
@Composable
fun Tooltip(text: String, anchorId: String) {
    Portal(target = "body") {
        Box(modifier = Modifier()
            .position(Position.Absolute)
            .zIndex(10000)
            .backgroundColor("#333")
            .color("white")
            .padding("8px 12px")
            .borderRadius("4px")
            .fontSize("14px")
        ) {
            Text(text)
        }
    }
}
```

### Notification System

```kotlin
@Composable
fun NotificationContainer() {
    Portal(target = "body") {
        Box(modifier = Modifier()
            .position(Position.Fixed)
            .top("20px")
            .right("20px")
            .zIndex(10000)
            .display(Display.Flex)
            .flexDirection(FlexDirection.Column)
            .gap("10px")
        ) {
            // Notifications render here
        }
    }
}
```

---

## Implementation Details

### Internal Data Structures

```kotlin
private data class PortalInfo(
    val originalParent: Element,
    val targetSelector: String,
    val targetContainer: HTMLElement
)

private val portaledElements = mutableMapOf<Element, PortalInfo>()
private val portalContainers = mutableMapOf<String, HTMLElement>()
```

### Lifecycle

1. **Portal Creation**:
    - Store original parent
    - Find/create target container
    - Move element via `appendChild()`
    - Track in `portaledElements`

2. **Portal Removal**:
    - Retrieve original parent
    - Remove from portal container
    - Return to original location
    - Clean up tracking

---

## Performance Considerations

### Best Practices

1. **Minimize Portals**: Only portal when necessary (z-index issues, overflow constraints)
2. **Reuse Containers**: Multiple portals can share the same container
3. **Clean Up**: Always unportal when components unmount
4. **Batch Operations**: Portal multiple elements at once if possible

### Memory Management

- Automatic cleanup on element removal
- No memory leaks with proper unmounting
- Containers are cached and reused

---

## Limitations

### Platform Support

- **JS**: ✅ Full support
- **JVM**: ❌ Not applicable (SSR doesn't need DOM manipulation)
- **WASM**: ❌ Not yet implemented

### Known Constraints

1. Only works in browser environment
2. Requires element to be in DOM before portaling
3. CSS containment may affect portaled elements
4. Event bubbling is preserved (events bubble through original parent chain)

---

## Integration Points

### Automatic Integration

The `PortalManager` is automatically used by:

- `Portal` component via `data-portal-target` attribute
- `PlatformRenderer` during element creation
- Component cleanup during unmounting

### Manual Usage

You can also use `PortalManager` directly:

```kotlin
// Manual portal
val element = document.createElement("div")
document.body?.appendChild(element)
PortalManager.portal(element, "#custom-container")

// Later...
PortalManager.unportal(element)
```

---

## Error Handling

### Safe Operations

All operations are safe and handle edge cases:

```kotlin
// Portaling already portaled element: no-op or re-portal
PortalManager.portal(element, "body")
PortalManager.portal(element, "#other-target") // Moves to new target

// Unportaling non-portaled element: no-op
PortalManager.unportal(element) // Safe, does nothing

// Getting target of non-portaled element: returns null
val target = PortalManager.getPortalTarget(element) // null
```

---

## Debugging

### Check Portal Status

```kotlin
// Log portal information
if (PortalManager.isPortaled(element)) {
    val target = PortalManager.getPortalTarget(element)
    console.log("Element is portaled to: $target")
} else {
    console.log("Element is not portaled")
}
```

### Inspect DOM

Portaled elements have no special attributes but can be identified by:

1. Their location in the DOM (under target container)
2. The `PortalManager` tracking maps

---

## Related APIs

- [Portal Component](../components/layout/portal.md) - High-level portal API
- [Modal Component](../components/feedback/modal.md) - Modal dialogs using portals
- [Tooltip Component](../components/display/tooltip.md) - Tooltips using portals

---

## See Also

- [Portal Usage Guide](../../guides/portal-usage.md)
- [Modal Patterns](../../guides/modal-patterns.md)
- [Quick Reference](../../../QUICK_REFERENCE.md#portalteleport)

