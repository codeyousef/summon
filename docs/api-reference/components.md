# Components API Reference

This document provides detailed information about the core components available in the standalone Summon implementation. These components generate HTML strings and can be used across all platforms.

## Table of Contents

- [Core Implementation](#core-implementation)
- [Layout Components](#layout-components)
  - [Column](#column)
  - [Row](#row)
  - [Div](#div)
- [Display Components](#display-components)
  - [Text](#text)
  - [Button](#button)
  - [TextField](#textfield)
- [Component Patterns](#component-patterns)
  - [Creating Custom Components](#creating-custom-components)
  - [Styling Patterns](#styling-patterns)
  - [State Management](#state-management)

---

## Core Implementation

All components in the standalone implementation follow these patterns and use this core infrastructure:

```kotlin
// Core annotation for marking composable functions
@Target(AnnotationTarget.FUNCTION)
annotation class Composable

// Core modifier system for styling and attributes
data class Modifier(
    val styles: Map<String, String> = emptyMap(),
    val attributes: Map<String, String> = emptyMap()
) {
    fun style(propertyName: String, value: String): Modifier =
        copy(styles = this.styles + (propertyName to value))
        
    fun attribute(name: String, value: String): Modifier =
        copy(attributes = this.attributes + (name to value))
        
    // Common style methods
    fun padding(value: String): Modifier = style("padding", value)
    fun margin(value: String): Modifier = style("margin", value)
    fun backgroundColor(color: String): Modifier = style("background-color", color)
    fun color(value: String): Modifier = style("color", value)
    fun fontSize(value: String): Modifier = style("font-size", value)
    fun fontWeight(value: String): Modifier = style("font-weight", value)
    fun borderRadius(value: String): Modifier = style("border-radius", value)
    fun width(value: String): Modifier = style("width", value)
    fun height(value: String): Modifier = style("height", value)
    fun cursor(value: String): Modifier = style("cursor", value)
    fun gap(value: String): Modifier = style("gap", value)
    
    // Common attribute methods
    fun onClick(handler: String): Modifier = attribute("onclick", handler)
    fun id(value: String): Modifier = attribute("id", value)
    fun className(value: String): Modifier = attribute("class", value)
    
    // Helper methods for HTML generation
    fun toStyleString(): String =
        if (styles.isEmpty()) "" else styles.entries.joinToString("; ") { "${it.key}: ${it.value}" }
        
    fun toAttributesString(): String =
        attributes.entries.joinToString(" ") { """${it.key}="${it.value}"""" }
}

// Factory function
fun Modifier(): Modifier = Modifier(emptyMap(), emptyMap())

// Basic state management
class State<T>(var value: T)
fun <T> mutableStateOf(initial: T) = State(initial)
fun <T> remember(calculation: () -> State<T>) = calculation()
```

---

## Layout Components

Layout components help you structure your UI by organizing other components in a specific layout pattern.

### Column

A layout component that arranges its children vertically using CSS flexbox.

#### Definition

```kotlin
@Composable
fun Column(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val columnModifier = modifier
        .display(Display.Flex)
        .flexDirection(FlexDirection.Column)
    
    val styleStr = if (columnModifier.styles.isNotEmpty()) 
        """ style="${columnModifier.toStyleString()}"""" else ""
    val attrsStr = if (columnModifier.attributes.isNotEmpty()) 
        " ${columnModifier.toAttributesString()}" else ""
    
    return """<div$attrsStr$styleStr>${content()}</div>"""
}
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | () -> String | Function that returns the content to be arranged vertically |

#### Example

```kotlin
@Composable
fun VerticalLayout(): String {
    return Column(
        modifier = Modifier()
            .padding("16px")
            .gap("8px")
            .backgroundColor("#f8f9fa")
    ) {
        Text("First item") +
        Text("Second item") +
        Text("Third item")
    }
}
```

### Row

A layout component that arranges its children horizontally using CSS flexbox.

#### Definition

```kotlin
@Composable
fun Row(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val rowModifier = modifier
        .display(Display.Flex)
        .flexDirection(FlexDirection.Row)
        .alignItems(AlignItems.Center)
    
    val styleStr = if (rowModifier.styles.isNotEmpty()) 
        """ style="${rowModifier.toStyleString()}"""" else ""
    val attrsStr = if (rowModifier.attributes.isNotEmpty()) 
        " ${rowModifier.toAttributesString()}" else ""
    
    return """<div$attrsStr$styleStr>${content()}</div>"""
}
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | () -> String | Function that returns the content to be arranged horizontally |

#### Example

```kotlin
@Composable
fun HorizontalLayout(): String {
    return Row(
        modifier = Modifier()
            .padding("16px")
            .gap("12px")
            .style("justify-content", "space-between")
    ) {
        Text("✓") +
        Text("Task completed") +
        Button(
            text = "Details",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("4px 8px")
                .borderRadius("4px")
                .onClick("showDetails()")
        )
    }
}
```

### Div

A basic container element for grouping content.

#### Definition

```kotlin
@Composable
fun Div(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) 
        """ style="${modifier.toStyleString()}"""" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) 
        " ${modifier.toAttributesString()}" else ""
    
    return """<div$attrsStr$styleStr>${content()}</div>"""
}
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| modifier | Modifier | Optional styling and layout modifiers |
| content | () -> String | Function that returns the content to be rendered |

#### Example

```kotlin
@Composable
fun ContainerExample(): String {
    return Div(
        modifier = Modifier()
            .backgroundColor("white")
            .padding("20px")
            .borderRadius("8px")
            .style("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
    ) {
        Text("This content is inside a styled container")
    }
}
```

---

## Display Components

Display components are used to show content to users such as text, buttons, and form inputs.

### Text

A component for displaying text content.

#### Definition

```kotlin
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier()
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) 
        """ style="${modifier.toStyleString()}"""" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) 
        " ${modifier.toAttributesString()}" else ""
    
    return """<span$attrsStr$styleStr>$text</span>"""
}
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| text | String | The text to display |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
@Composable
fun TextExamples(): String {
    return Column(modifier = Modifier().gap("16px")) {
        Text("Default text") +
        Text(
            text = "Large bold text", 
            modifier = Modifier()
                .fontSize("24px")
                .fontWeight("bold")
                .color("#333")
        ) +
        Text(
            text = "Colored text",
            modifier = Modifier()
                .color("#0077cc")
                .fontSize("16px")
        )
    }
}
```

### Button

A clickable button component.

#### Definition

```kotlin
@Composable
fun Button(
    text: String,
    modifier: Modifier = Modifier()
): String {
    val buttonModifier = modifier
        .border("0", BorderStyle.None, "transparent")
        .style("cursor", "pointer")
        .display(Display.InlineBlock)
        .style("text-align", "center")
    
    val styleStr = if (buttonModifier.styles.isNotEmpty()) 
        """ style="${buttonModifier.toStyleString()}"""" else ""
    val attrsStr = if (buttonModifier.attributes.isNotEmpty()) 
        " ${buttonModifier.toAttributesString()}" else ""
    
    return """<button$attrsStr$styleStr>$text</button>"""
}
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| text | String | The text to display on the button |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
@Composable
fun ButtonExamples(): String {
    return Row(modifier = Modifier().gap("12px")) {
        Button(
            text = "Primary Button",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
                .onClick("handlePrimaryAction()")
        ) + Button(
            text = "Secondary Button",
            modifier = Modifier()
                .backgroundColor("#6c757d")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
                .onClick("handleSecondaryAction()")
        ) + Button(
            text = "Danger Button",
            modifier = Modifier()
                .backgroundColor("#dc3545")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
                .onClick("handleDangerAction()")
        )
    }
}
```

### TextField

A single-line text input component.

#### Definition

```kotlin
@Composable
fun TextField(
    value: String = "",
    placeholder: String = "",
    modifier: Modifier = Modifier()
): String {
    val inputModifier = modifier
        .border(1.px, BorderStyle.Solid, "#ddd")
        .style("border-radius", "4px")
        .style("font-family", "inherit")
    
    val styleStr = if (inputModifier.styles.isNotEmpty()) 
        """ style="${inputModifier.toStyleString()}"""" else ""
    val attrsStr = if (inputModifier.attributes.isNotEmpty()) 
        " ${inputModifier.toAttributesString()}" else ""
    
    val placeholderAttr = if (placeholder.isNotEmpty()) """ placeholder="$placeholder"""" else ""
    val valueAttr = if (value.isNotEmpty()) """ value="$value"""" else ""
    
    return """<input type="text"$attrsStr$styleStr$placeholderAttr$valueAttr />"""
}
```

#### Parameters

| Name | Type | Description |
|------|------|-------------|
| value | String | Current value of the input field |
| placeholder | String | Placeholder text when field is empty |
| modifier | Modifier | Optional styling and layout modifiers |

#### Example

```kotlin
@Composable
fun FormExample(): String {
    return Column(modifier = Modifier().gap("12px").padding("20px")) {
        Text("Contact Form", modifier = Modifier().fontSize("20px").fontWeight("bold")) +
        
        TextField(
            placeholder = "Enter your name",
            modifier = Modifier()
                .width("100%")
                .padding("12px")
                .style("box-sizing", "border-box")
                .attribute("id", "name")
        ) +
        
        TextField(
            placeholder = "Enter your email",
            modifier = Modifier()
                .width("100%")
                .padding("12px")
                .style("box-sizing", "border-box")
                .attribute("type", "email")
                .attribute("id", "email")
        ) +
        
        Button(
            text = "Submit",
            modifier = Modifier()
                .backgroundColor("#28a745")
                .color("white")
                .padding("12px 24px")
                .borderRadius("4px")
                .onClick("submitForm()")
        )
    }
}
```

---

## Component Patterns

This section demonstrates practical patterns for building more complex components using the standalone Summon implementation.

### Creating Custom Components

Here's how to create reusable custom components:

```kotlin
// A reusable card component
@Composable
fun Card(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val cardModifier = modifier
        .backgroundColor("white")
        .borderRadius("8px")
        .style("border", "1px solid #e0e0e0")
        .style("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
    
    return Div(cardModifier) { content() }
}

// A product card using the Card component
@Composable
fun ProductCard(
    name: String,
    price: String,
    image: String
): String {
    return Card(
        modifier = Modifier()
            .width("300px")
            .padding("16px")
    ) {
        Column(modifier = Modifier().gap("12px")) {
            // Simple image implementation
            """<img src="$image" alt="$name" style="width: 100%; height: 200px; object-fit: cover; border-radius: 4px;" />""" +
            
            Text(name, modifier = Modifier().fontSize("18px").fontWeight("bold")) +
            Text(price, modifier = Modifier().color("#28a745").fontSize("16px").fontWeight("bold")) +
            
            Button(
                text = "Add to Cart",
                modifier = Modifier()
                    .backgroundColor("#0077cc")
                    .color("white")
                    .padding("10px 20px")
                    .borderRadius("4px")
                    .width("100%")
                    .onClick("addToCart('$name')")
            )
        }
    }
}
```

### Styling Patterns

Common styling patterns and utilities:

```kotlin
// Style utility functions
fun Modifier.primaryButton(): Modifier = this
    .backgroundColor("#0077cc")
    .color("white")
    .padding("10px 20px")
    .borderRadius("4px")
    .cursor("pointer")
    .style("border", "none")

fun Modifier.secondaryButton(): Modifier = this
    .backgroundColor("transparent")
    .color("#0077cc")
    .padding("10px 20px")
    .borderRadius("4px")
    .cursor("pointer")
    .style("border", "1px solid #0077cc")

fun Modifier.dangerButton(): Modifier = this
    .backgroundColor("#dc3545")
    .color("white")
    .padding("10px 20px")
    .borderRadius("4px")
    .cursor("pointer")
    .style("border", "none")

// Usage
@Composable
fun ActionButtons(): String {
    return Row(modifier = Modifier().gap("12px")) {
        Button(
            text = "Save",
            modifier = Modifier().primaryButton().onClick("save()")
        ) + Button(
            text = "Cancel", 
            modifier = Modifier().secondaryButton().onClick("cancel()")
        ) + Button(
            text = "Delete",
            modifier = Modifier().dangerButton().onClick("delete()")
        )
    }
}
```

### State Management

State management patterns in the standalone implementation:

```kotlin
// Simple counter component with state
@Composable
fun Counter(): String {
    // In a real application, state would be managed externally
    // This is just for demonstration
    return Column(modifier = Modifier().gap("8px").padding("20px")) {
        Text("Count: {currentCount}", modifier = Modifier().fontSize("18px")) +
        
        Row(modifier = Modifier().gap("8px")) {
            Button(
                text = "-",
                modifier = Modifier()
                    .backgroundColor("#dc3545")
                    .color("white")
                    .padding("8px 12px")
                    .borderRadius("4px")
                    .onClick("decrement()")
            ) + Button(
                text = "+",
                modifier = Modifier()
                    .backgroundColor("#28a745")
                    .color("white")
                    .padding("8px 12px")
                    .borderRadius("4px")
                    .onClick("increment()")
            )
        }
    }
}

// Todo list component
@Composable
fun TodoList(todos: List<String>): String {
    return Column(modifier = Modifier().gap("8px")) {
        Text("Todo List", modifier = Modifier().fontSize("20px").fontWeight("bold")) +
        
        todos.joinToString("") { todo ->
            Row(
                modifier = Modifier()
                    .backgroundColor("#f8f9fa")
                    .padding("12px")
                    .borderRadius("4px")
                    .alignItems(AlignItems.Center)
                    .style("justify-content", "space-between")
            ) {
                Text(todo) +
                Button(
                    text = "✓",
                    modifier = Modifier()
                        .backgroundColor("#28a745")
                        .color("white")
                        .padding("4px 8px")
                        .borderRadius("4px")
                        .onClick("completeTodo('$todo')")
                )
            }
        } +
        
        Row(modifier = Modifier().gap("8px").style("margin-top", "16px")) {
            TextField(
                placeholder = "Add new todo...",
                modifier = Modifier()
                    .style("flex", "1")
                    .padding("8px")
                    .attribute("id", "newTodo")
            ) + Button(
                text = "Add",
                modifier = Modifier()
                    .backgroundColor("#0077cc")
                    .color("white")
                    .padding("8px 16px")
                    .borderRadius("4px")
                    .onClick("addTodo()")
            )
        }
    }
}
```

### Advanced Component Composition

Building complex UIs through composition:

```kotlin
// Navigation bar component
@Composable
fun NavigationBar(currentPage: String): String {
    return Row(
        modifier = Modifier()
            .backgroundColor("white")
            .padding("16px")
            .style("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .style("justify-content", "space-between")
            .alignItems(AlignItems.Center)
    ) {
        Text("My App", modifier = Modifier().fontSize("20px").fontWeight("bold")) +
        
        Row(modifier = Modifier().gap("16px")) {
            listOf("Home", "About", "Contact").joinToString("") { page ->
                val isActive = page.lowercase() == currentPage.lowercase()
                Text(
                    text = page,
                    modifier = Modifier()
                        .color(if (isActive) "#0077cc" else "#333")
                        .fontWeight(if (isActive) "bold" else "normal")
                        .cursor(Cursor.Pointer)
                        .onClick("navigateTo('${page.lowercase()}')")
                )
            }
        }
    }
}

// Complete page layout
@Composable
fun PageLayout(
    title: String,
    currentPage: String,
    content: () -> String
): String {
    return Column(modifier = Modifier().style("min-height", "100vh")) {
        NavigationBar(currentPage) +
        
        Column(
            modifier = Modifier()
                .style("flex", "1")
                .padding("20px")
                .gap("20px")
        ) {
            Text(title, modifier = Modifier().fontSize("28px").fontWeight("bold")) +
            content()
        } +
        
        // Footer
        Div(
            modifier = Modifier()
                .backgroundColor("#f8f9fa")
                .padding("20px")
                .style("text-align", "center")
        ) {
            Text("© 2024 My App. All rights reserved.", modifier = Modifier().color("#666"))
        }
    }
}

// Usage
@Composable
fun HomePage(): String {
    return PageLayout(
        title = "Welcome Home",
        currentPage = "home"
    ) {
        Text("Welcome to our application!") +
        
        Row(modifier = Modifier().gap("20px").style("margin-top", "20px")) {
            ProductCard(
                name = "Sample Product",
                price = "$29.99",
                image = "/product1.jpg"
            ) + ProductCard(
                name = "Another Product", 
                price = "$39.99",
                image = "/product2.jpg"
            )
        }
    }
}
```

## Best Practices

### 1. Component Naming
- Use descriptive names that indicate the component's purpose
- Follow PascalCase for component functions
- Group related components in the same file

### 2. Modifier Usage
- Always provide a default `Modifier()` parameter
- Apply component-specific default styles before user modifiers
- Use modifier extension functions for common styling patterns

### 3. HTML Generation
- Escape user input to prevent XSS attacks
- Use semantic HTML elements when possible
- Include proper accessibility attributes

### 4. Performance
- Keep components pure and stateless when possible
- Avoid complex calculations inside component functions
- Use string concatenation efficiently

This standalone implementation provides:

✅ **Simple Components**: Easy to understand and extend  
✅ **Type Safety**: Full Kotlin compile-time checking  
✅ **Cross-Platform**: Works on both JavaScript and JVM  
✅ **No Dependencies**: Uses only standard libraries  
✅ **Debugging Friendly**: Generated HTML is readable and debuggable  
✅ **Framework Agnostic**: Can be used with any web framework or as standalone  

The component API is designed to be familiar to developers coming from other UI frameworks while maintaining the simplicity and transparency of the standalone approach.