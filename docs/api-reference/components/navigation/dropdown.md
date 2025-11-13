# Dropdown

A flexible dropdown/menu component with proper state management, keyboard navigation, and accessibility features built-in.

## Overview

The Dropdown component provides a composable way to create dropdown menus with built-in hover and click triggers, keyboard navigation, and ARIA attributes for accessibility.

## Basic Usage

```kotlin
import code.yousef.summon.components.navigation.Dropdown
import code.yousef.summon.components.navigation.DropdownItem

Dropdown(
    trigger = { Text("Menu") }
) {
    DropdownItem("Option 1", onClick = { })
    DropdownItem("Option 2", onClick = { })
    DropdownItem("Option 3", onClick = { })
}
```

## API Reference

### Dropdown

```kotlin
@Composable
fun Dropdown(
    trigger: @Composable FlowContent.() -> Unit,
    modifier: Modifier = Modifier(),
    triggerBehavior: DropdownTrigger = DropdownTrigger.HOVER,
    alignment: DropdownAlignment = DropdownAlignment.LEFT,
    closeOnItemClick: Boolean = true,
    content: @Composable FlowContent.() -> Unit
)
```

#### Parameters

- **trigger**: Composable content for the trigger element that opens the dropdown
- **modifier**: Modifier applied to the dropdown container
- **triggerBehavior**: How the dropdown should open (HOVER, CLICK, or BOTH)
- **alignment**: How to align the menu relative to the trigger (LEFT, RIGHT, or CENTER)
- **closeOnItemClick**: Whether to close the menu when an item is clicked (default: true)
- **content**: The dropdown menu content

### DropdownItem

```kotlin
@Composable
fun DropdownItem(
    label: String,
    onClick: (() -> Unit)? = null,
    href: String? = null,
    modifier: Modifier = Modifier(),
    enabled: Boolean = true
)
```

#### Parameters

- **label**: The text label for the item
- **onClick**: Optional click handler
- **href**: Optional link URL (will render as an `<a>` tag if provided)
- **modifier**: Modifier applied to the item
- **enabled**: Whether the item is enabled (default: true)

### DropdownDivider

```kotlin
@Composable
fun DropdownDivider(
    modifier: Modifier = Modifier()
)
```

A visual divider for separating groups of dropdown items.

## Examples

### Basic Dropdown with Custom Trigger

```kotlin
Dropdown(
    trigger = {
        Button(
            onClick = { },
            label = "Actions"
        )
    }
) {
    DropdownItem("Edit", onClick = { /* handle edit */ })
    DropdownItem("Delete", onClick = { /* handle delete */ })
}
```

### Dropdown with Links

```kotlin
Dropdown(
    trigger = { Text("Projects") }
) {
    DropdownItem(
        label = "Project A",
        href = "/projects/a"
    )
    DropdownItem(
        label = "Project B",
        href = "/projects/b"
    )
    DropdownDivider()
    DropdownItem(
        label = "New Project",
        href = "/projects/new"
    )
}
```

### Click-triggered Dropdown

```kotlin
Dropdown(
    trigger = { Text("Click me") },
    triggerBehavior = DropdownTrigger.CLICK
) {
    DropdownItem("Option 1", onClick = { })
    DropdownItem("Option 2", onClick = { })
}
```

### Right-aligned Dropdown

```kotlin
Dropdown(
    trigger = { Text("User Menu") },
    alignment = DropdownAlignment.RIGHT
) {
    DropdownItem("Profile", href = "/profile")
    DropdownItem("Settings", href = "/settings")
    DropdownDivider()
    DropdownItem("Logout", onClick = { /* logout */ })
}
```

### Disabled Items

```kotlin
Dropdown(
    trigger = { Text("Actions") }
) {
    DropdownItem("Available Action", onClick = { })
    DropdownItem("Disabled Action", enabled = false)
}
```

## Enums

### DropdownTrigger

Controls how the dropdown opens:

- `HOVER`: Open on hover
- `CLICK`: Open on click
- `BOTH`: Open on both hover and click

### DropdownAlignment

Controls menu alignment relative to trigger:

- `LEFT`: Align left edges (default)
- `RIGHT`: Align right edges
- `CENTER`: Center menu under trigger

## Accessibility

The Dropdown component automatically includes:

- `aria-haspopup="true"` on the trigger
- `aria-expanded` to indicate menu state
- `role="menu"` on the dropdown menu
- `role="menuitem"` on each dropdown item
- `aria-disabled` for disabled items
- Keyboard navigation support (planned)

## Styling

You can style the dropdown and its items using modifiers:

```kotlin
Dropdown(
    trigger = { Text("Styled Menu") },
    modifier = Modifier()
        .style("font-family", "Arial")
) {
    DropdownItem(
        label = "Styled Item",
        modifier = Modifier()
            .style("font-weight", "bold")
    )
}
```

The default styles include:
- White background with border
- Box shadow for depth
- Hover effects on items
- Border between items

## See Also

- [Link](../navigation/link.md) - Navigation link component
- [Button](../input/button.md) - Button component (commonly used as trigger)
- [Modal](../feedback/modal.md) - Modal dialog component
