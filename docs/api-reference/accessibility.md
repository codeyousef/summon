# Accessibility API Reference

This document provides detailed API documentation for Summon's accessibility features.

## Accessibility Modifiers

### ARIA Attributes

```kotlin
fun Modifier.role(value: String): Modifier
fun Modifier.ariaLabel(value: String): Modifier
fun Modifier.ariaLabelledBy(value: String): Modifier
fun Modifier.ariaDescribedBy(value: String): Modifier
fun Modifier.ariaHidden(value: Boolean): Modifier
fun Modifier.ariaExpanded(value: Boolean): Modifier
fun Modifier.ariaPressed(value: Boolean): Modifier
fun Modifier.ariaChecked(value: Boolean): Modifier
fun Modifier.ariaChecked(value: String): Modifier
fun Modifier.ariaSelected(value: Boolean): Modifier
fun Modifier.ariaDisabled(value: Boolean): Modifier
fun Modifier.ariaInvalid(value: Boolean): Modifier
fun Modifier.ariaInvalid(value: String): Modifier
fun Modifier.ariaRequired(value: Boolean): Modifier
fun Modifier.ariaCurrent(value: String): Modifier
fun Modifier.ariaLive(value: String): Modifier
fun Modifier.tabIndex(value: Int): Modifier
fun Modifier.ariaControls(id: String): Modifier
fun Modifier.ariaHasPopup(value: Boolean = true): Modifier
fun Modifier.ariaBusy(value: Boolean = true): Modifier
```

These modifiers add ARIA attributes to elements for better accessibility. They are available on any `Modifier` instance.

### Focus Management

```kotlin
fun Modifier.focusable(): Modifier       // Makes an element focusable but not in the tab order
fun Modifier.tabbable(): Modifier        // Makes an element focusable and in the tab order
fun Modifier.disabled(): Modifier        // Marks an element as disabled and not focusable
fun Modifier.autoFocus(): Modifier       // Marks an element for autofocus when rendered
```

The Focus Management modifiers provide utilities for managing keyboard focus and accessibility relationships between elements.

## Accessibility Utilities

### AccessibilityUtils

```kotlin
object AccessibilityUtils {
    enum class NodeRole {
        BUTTON,
        CHECKBOX,
        COMBOBOX,
        DIALOG,
        GRID,
        HEADING,
        LINK,
        LISTBOX,
        MENU,
        MENUITEM,
        NAVIGATION,
        PROGRESSBAR,
        RADIOGROUP,
        REGION,
        SEARCH,
        SLIDER,
        SWITCH,
        TAB,
        TABLIST,
        TABPANEL,
        TEXTBOX,
        TOOLTIP
    }

    fun createRoleModifier(role: NodeRole): Modifier
    fun createRoleModifier(customRole: String): Modifier
    fun createLabelModifier(label: String): Modifier
    fun createRelationshipModifier(relation: String, targetId: String): Modifier
    fun inspectAccessibility(modifier: Modifier): Map<String, String>
}

// Extension function on Modifier
fun Modifier.inspectAccessibility(): Map<String, String>
```

The `AccessibilityUtils` object provides utilities for working with the accessibility tree and inspecting accessibility attributes.

### AccessibleElement

```kotlin
@Composable
fun AccessibleElement(
    content: @Composable () -> Unit,
    role: AccessibilityUtils.NodeRole? = null,
    customRole: String? = null,
    label: String? = null,
    relations: Map<String, String> = emptyMap(),
    modifier: Modifier = Modifier()
)
```

The `AccessibleElement` function is a wrapper component that adds accessibility attributes to its content.

## Semantic HTML Components

```kotlin
@Composable
fun Header(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)

@Composable
fun Main(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)

@Composable
fun Nav(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)

@Composable
fun Article(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)

@Composable
fun Section(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)

@Composable
fun Aside(
    id: String? = null, 
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)

@Composable
fun Footer(
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)

@Composable
fun Heading(
    level: Int,
    id: String? = null,
    className: String? = null,
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
)
```

These semantic HTML components help create semantically meaningful HTML elements that improve screen reader and assistive technology understanding of content structure.

### Dialog, input, and status behavior

- `Modal(..., dismissOnEscape = true, dismissOnBackdropClick = true, ariaLabel = ...)`
  supplies dialog semantics, inert background handling, a topmost-dialog focus
  trap, nested-dialog support, and invoker focus restoration.
- Controlled `TextField` and `TextArea` nodes preserve selection and defer model
  updates while an IME composition is active.
- `DatePicker` uses a native date input on every renderer, exposes `label` as
  its accessible name, and keeps its ISO `LocalDate` value independent of
  timezone conversion while inheriting locale presentation from `lang`.
- `Focusable` and `FocusableContainer` invoke `onFocusChange` from native focus
  and blur events, once per actual transition.
- `Button` has a 44 by 44 CSS-pixel minimum target and disables transition and
  transform effects when reduced motion is requested.
- `ToastManager(maxVisibleToasts = 5)` bounds visible announcements. Toast
  success/info/warning messages use polite status semantics; errors use an
  assertive alert.
- `FormValidationState` accepts only the latest asynchronous validation result
  for a field; a cleared or replaced value invalidates older work.

These APIs expose the semantics needed by accessibility tools. Release
qualification still requires manual keyboard, 200% zoom, high-contrast,
screen-reader, OS text-scaling, RTL/IME, and physical Safari checks.

## Examples

### Adding ARIA Attributes

```kotlin
Text(
    text = "Accessible Text",
    modifier = Modifier()
        .role("heading")
        .ariaLabel("Section heading")
        .ariaDescribedBy("text-description")
)
```

### Managing Focus

```kotlin
Button(
    onClick = { /* handle click */ },
    modifier = Modifier
        .tabbable()
        .ariaLabel("Submit form")
)
```

### Using Semantic HTML

```kotlin
Header {
    Heading(level = 1) { 
        Text("My Website") 
    }
}

Main {
    Section {
        Heading(level = 2) { 
            Text("About Us") 
        }
        Text("Content about the company")
    }
    
    Article {
        Heading(level = 2) { 
            Text("Latest News") 
        }
        Text("News content")
    }
}
```

### Creating Accessible Elements

```kotlin
AccessibleElement(
    role = AccessibilityUtils.NodeRole.ALERT,
    label = "Important alert message",
    modifier = Modifier
        .fontSize("1.2rem")
        .color("#c62828")
) {
    Text("Important Message")
}
```

### Inspecting Accessibility Attributes

```kotlin
val modifier = Modifier()
    .role("button")
    .ariaLabel("Close dialog")
    .ariaControls("dialog-1")

// Get all accessibility attributes
val accessibilityAttrs = modifier.inspectAccessibility()
// Result: {"role": "button", "aria-label": "Close dialog", "aria-controls": "dialog-1"}
``` 