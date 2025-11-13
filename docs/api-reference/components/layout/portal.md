# Portal / Teleport

A Portal (also known as Teleport) renders its children into a different part of the DOM tree, outside of the current component hierarchy.

## Overview

The Portal component allows you to render content anywhere in the DOM tree while maintaining the composable context from the source location. This is particularly useful for modals, tooltips, and overlays that need to escape their parent container's styling or z-index context.

## Basic Usage

```kotlin
import code.yousef.summon.components.layout.Portal

Portal(target = "body") {
    Modal(
        visible = isOpen,
        onClose = { isOpen = false }
    ) {
        Text("Modal content")
    }
}
```

## API Reference

### Portal

```kotlin
@Composable
fun Portal(
    target: String = "body",
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

#### Parameters

- **target**: CSS selector for the target container (e.g., "body", "#modal-root"). Default: "body"
- **modifier**: Modifier applied to the portal wrapper
- **content**: The content to render in the portal

### Teleport (Alias)

```kotlin
@Composable
fun Teleport(
    to: String = "body",
    modifier: Modifier = Modifier(),
    content: @Composable FlowContent.() -> Unit
)
```

Alternative name matching Vue's terminology. Identical functionality to Portal.

## Use Cases

### Modals

Render modals at the document body level to avoid z-index issues:

```kotlin
Portal(target = "body") {
    Box(
        modifier = Modifier()
            .style("position", "fixed")
            .style("top", "50%")
            .style("left", "50%")
            .style("transform", "translate(-50%, -50%)")
            .style("z-index", "9999")
            .style("background-color", "white")
            .style("padding", "20px")
            .style("border-radius", "8px")
    ) {
        Text("Modal Content")
        Button(
            onClick = { /* close modal */ },
            label = "Close"
        )
    }
}
```

### Tooltips

Escape overflow:hidden containers:

```kotlin
Portal(target = "body") {
    Box(
        modifier = Modifier()
            .style("position", "absolute")
            .style("top", "${tooltipY}px")
            .style("left", "${tooltipX}px")
            .style("background-color", "black")
            .style("color", "white")
            .style("padding", "8px")
            .style("border-radius", "4px")
    ) {
        Text(tooltipText)
    }
}
```

### Global Notifications

Render notifications at a consistent location:

```kotlin
Portal(target = "#notification-root") {
    Box(
        modifier = Modifier()
            .style("position", "fixed")
            .style("top", "20px")
            .style("right", "20px")
            .style("z-index", "9999")
    ) {
        Notification(message = "Action completed!")
    }
}
```

### Dropdown Menus

Ensure proper stacking context for complex layouts:

```kotlin
Portal(target = "body") {
    Box(
        modifier = Modifier()
            .style("position", "absolute")
            .style("top", "${menuTop}px")
            .style("left", "${menuLeft}px")
            .style("z-index", "1000")
    ) {
        // Dropdown items
    }
}
```

## Examples

### Conditional Portal Rendering

```kotlin
@Composable
fun MyComponent() {
    val isModalOpen = remember { mutableStateOf(false) }
    
    Button(
        onClick = { isModalOpen.value = true },
        label = "Open Modal"
    )
    
    if (isModalOpen.value) {
        Portal(target = "body") {
            ModalDialog(
                onClose = { isModalOpen.value = false }
            )
        }
    }
}
```

### Custom Portal Container

```kotlin
// HTML setup: <div id="modal-root"></div>

Portal(target = "#modal-root") {
    // Content will be rendered inside #modal-root
    MyModalContent()
}
```

### Fullscreen Overlay

```kotlin
Portal(target = "body") {
    Box(
        modifier = Modifier()
            .style("position", "fixed")
            .style("top", "0")
            .style("left", "0")
            .style("right", "0")
            .style("bottom", "0")
            .style("background-color", "rgba(0, 0, 0, 0.5)")
            .style("z-index", "9998")
            .onClick("closeOverlay()")
    ) {
        // Overlay content
    }
}
```

### Using Teleport (Alias)

```kotlin
// Teleport is an alias for Portal
Teleport(to = "body") {
    MyContent()
}
```

## Common Patterns

### Modal with Backdrop

```kotlin
@Composable
fun Modal(visible: Boolean, onClose: () -> Unit, content: @Composable () -> Unit) {
    if (visible) {
        Portal(target = "body") {
            // Backdrop
            Box(
                modifier = Modifier()
                    .style("position", "fixed")
                    .style("inset", "0")
                    .style("background-color", "rgba(0, 0, 0, 0.5)")
                    .style("z-index", "9998")
                    .onClick("event.stopPropagation(); closeModal()")
            )
            
            // Modal content
            Box(
                modifier = Modifier()
                    .style("position", "fixed")
                    .style("top", "50%")
                    .style("left", "50%")
                    .style("transform", "translate(-50%, -50%)")
                    .style("z-index", "9999")
            ) {
                content()
            }
        }
    }
}
```

### Toast Notification System

```kotlin
@Composable
fun ToastContainer() {
    val toasts = remember { mutableStateListOf<Toast>() }
    
    Portal(target = "body") {
        Column(
            modifier = Modifier()
                .style("position", "fixed")
                .style("top", "20px")
                .style("right", "20px")
                .style("z-index", "9999")
                .style("gap", "10px")
        ) {
            toasts.forEach { toast ->
                ToastMessage(toast)
            }
        }
    }
}
```

## Implementation Notes

The Portal component marks content with `data-portal="true"` and `data-portal-target="<target>"` attributes. Platform-specific renderers should implement the actual DOM teleportation logic to move the content to the specified target location.

## Browser Compatibility

Portal/Teleport works in all modern browsers that support:
- DOM manipulation
- CSS selectors (querySelector)
- Fixed positioning

## Performance Considerations

- Portals maintain the same composable context as their source location
- Event handlers and state management work normally within portals
- Minimal performance overhead compared to rendering in place
- Content is still part of the same composition tree

## See Also

- [Modal](../feedback/modal.md) - Modal dialog component
- [Tooltip](../feedback/tooltip.md) - Tooltip component
- [Dropdown](../navigation/dropdown.md) - Dropdown menu component
- [Box](./box.md) - Container component often used with Portal
