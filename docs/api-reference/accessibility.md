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
```

These modifiers add ARIA attributes to elements for better accessibility. They are available on any `Modifier` instance.

### Focus Management

```kotlin
object FocusManagement {
    enum class FocusBehavior {
        FOCUSABLE,
        TABBABLE,
        DISABLED,
        AUTO_FOCUS
    }

    fun createFocusModifier(behavior: FocusBehavior): Modifier
    fun createFocusModifier(customRole: String): Modifier
    fun createLabelModifier(label: String): Modifier
    fun createRelationshipModifier(relation: String, targetId: String): Modifier
}
```

The `FocusManagement` object provides utilities for managing keyboard focus and accessibility relationships between elements.

## Semantic HTML Components

### AccessibilityTree

```kotlin
object AccessibilityTree {
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
```

The `AccessibilityTree` object provides utilities for working with the accessibility tree and inspecting accessibility attributes.

### AccessibleElement

```kotlin
class AccessibleElement(
    private val content: List<Composable>,
    private val role: AccessibilityTree.NodeRole? = null,
    private val customRole: String? = null,
    private val label: String? = null,
    private val relations: Map<String, String> = emptyMap()
) : Composable
```

The `AccessibleElement` class is a wrapper component that adds accessibility attributes to its content.

## Semantic HTML Components

```kotlin
object SemanticHTML {
    class Header(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable

    class Main(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable

    class Nav(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable

    class Article(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable

    class Section(
        private val id: String? = null,
        private val className: String? = null,
        private val content: FlowContent.() -> Unit
    ) : Composable
}
```

The `SemanticHTML` object provides components for creating semantically meaningful HTML elements that help screen readers and assistive technologies understand the structure of your content.

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
    text = "Submit",
    modifier = FocusManagement.createFocusModifier(FocusManagement.FocusBehavior.TABBABLE)
        .ariaLabel("Submit form")
)
```

### Using Semantic HTML

```kotlin
SemanticHTML.Header {
    h1 { +"My Website" }
}

SemanticHTML.Main {
    SemanticHTML.Section {
        h2 { +"About Us" }
        p { +"Content about the company" }
    }
    
    SemanticHTML.Article {
        h2 { +"Latest News" }
        p { +"News content" }
    }
}
```

### Creating Accessible Elements

```kotlin
AccessibleElement(
    content = listOf(
        Text(
            text = "Important Message",
            modifier = Modifier()
                .fontSize(1.2.rem)
                .color("#c62828")
        )
    ),
    role = AccessibilityTree.NodeRole.ALERT,
    label = "Important alert message"
)
``` 