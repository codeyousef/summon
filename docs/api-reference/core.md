# Core API Reference

This document provides detailed information about the core concepts and implementation patterns in Summon using the standalone approach.

## Table of Contents

- [Composable Functions](#composable-functions)
- [Modifier System](#modifier-system)
- [State Management](#state-management)
- [Component Structure](#component-structure)
- [Rendering Patterns](#rendering-patterns)

---

## Composable Functions

The `@Composable` annotation marks functions that generate HTML strings for UI components.

### Annotation Definition

```kotlin
// Simple composable annotation for standalone implementation
@Target(AnnotationTarget.FUNCTION)
annotation class Composable
```

### Description

`@Composable` functions in the standalone implementation are pure functions that return HTML strings. They can be combined to build complex UI structures while maintaining type safety and composability.

### Example

```kotlin
@Composable
fun MyComponent(text: String): String {
    return Div(
        modifier = Modifier().padding("16px")
    ) {
        Text(text)
    }
}

// Usage
@Composable
fun App(): String {
    return MyComponent("Hello, World!")
}
```

---

## Modifier System

The Modifier system provides a fluent API for applying styles and attributes to components.

### Modifier Definition

```kotlin
// Core modifier data class
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
    fun borderRadius(value: String): Modifier = style("border-radius", value)
    
    // Common attribute methods
    fun onClick(handler: String): Modifier = attribute("onclick", handler)
    fun id(value: String): Modifier = attribute("id", value)
    
    // Helper methods for HTML generation
    fun toStyleString(): String =
        if (styles.isEmpty()) "" else styles.entries.joinToString("; ") { "${it.key}: ${it.value}" }
        
    fun toAttributesString(): String =
        attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
}

// Factory function
fun Modifier(): Modifier = Modifier(emptyMap(), emptyMap())
```

### Description

The Modifier system allows you to apply CSS styles and HTML attributes to components in a type-safe, chainable way. Each modifier method returns a new Modifier instance, making them immutable and composable.

### Example

```kotlin
@Composable
fun StyledButton(): String {
    return Button(
        text = "Click me",
        modifier = Modifier()
            .backgroundColor("#0077cc")
            .color("#ffffff")
            .padding("8px 16px")
            .borderRadius("4px")
            .onClick("handleClick()")
    )
}
```

---

## State Management

State in the standalone implementation is managed through simple state classes and helper functions.

### State Classes

```kotlin
// Basic state container
class State<T>(var value: T)

// Factory function for creating state
fun <T> mutableStateOf(initial: T) = State(initial)

// Remember function for state preservation pattern
fun <T> remember(calculation: () -> State<T>) = calculation()
```

### Description

State management in the standalone implementation uses simple containers that hold values. The `remember` function follows the pattern of state preservation, though in the standalone implementation it simply executes the calculation function.

### Example

```kotlin
@Composable
fun Counter(): String {
    val count = remember { mutableStateOf(0) }
    
    return Column(
        modifier = Modifier().gap("8px")
    ) {
        Text("Count: ${count.value}") +
        Button(
            text = "Increment",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("8px 16px")
                .onClick("incrementCounter()")
        )
    }
}
```

---

## Component Structure

Components in the standalone implementation follow a consistent pattern for generating HTML.

### Basic Component Pattern

```kotlin
@Composable
fun ComponentName(
    // Component-specific parameters
    parameter: String,
    // Optional modifier (always last parameter)
    modifier: Modifier = Modifier()
): String {
    // Apply any default styles to the modifier
    val finalModifier = modifier.someDefaultStyle("value")
    
    // Generate style and attribute strings
    val styleStr = if (finalModifier.styles.isNotEmpty()) 
        " style=\"${finalModifier.toStyleString()}\"" else ""
    val attrsStr = if (finalModifier.attributes.isNotEmpty()) 
        " ${finalModifier.toAttributesString()}" else ""
    
    // Return HTML string
    return "<tag$attrsStr$styleStr>content</tag>"
}
```

### Container Component Pattern

```kotlin
@Composable
fun ContainerComponent(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val containerModifier = modifier.defaultContainerStyles()
    val styleStr = if (containerModifier.styles.isNotEmpty()) 
        " style=\"${containerModifier.toStyleString()}\"" else ""
    val attrsStr = if (containerModifier.attributes.isNotEmpty()) 
        " ${containerModifier.toAttributesString()}" else ""
    
    return "<div$attrsStr$styleStr>${content()}</div>"
}
```

---

## Rendering Patterns

The standalone implementation supports multiple rendering patterns for different environments.

### JavaScript Browser Rendering

```kotlin
// Basic DOM rendering pattern
fun main() {
    val root = kotlinx.browser.document.getElementById("root")
        ?: error("Root element not found")
    
    // Render the component and set innerHTML
    root.innerHTML = App()
    
    // Optional: Add interactivity via JavaScript
    addInteractivity()
}

@Composable
fun App(): String {
    return Column(
        modifier = Modifier().padding("20px")
    ) {
        Text("Hello, Browser!") +
        Button(
            text = "Click me",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .onClick("handleClick()")
        )
    }
}

// Add JavaScript for interactivity
fun addInteractivity() {
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <script>
        function handleClick() {
            alert('Button clicked!');
        }
        </script>
    """)
}
```

### JVM Server-Side Rendering

```kotlin
// Basic server-side rendering
fun main() {
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Summon App</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body>
            ${App()}
        </body>
        </html>
    """.trimIndent()
    
    println(html)
}

@Composable
fun App(): String {
    return Column(
        modifier = Modifier().padding("20px")
    ) {
        Text("Hello, Server!") +
        Text("Generated on JVM", modifier = Modifier().fontSize("14px"))
    }
}
```

### Ktor Integration

```kotlin
// Ktor web server integration
fun Application.configureRouting() {
    routing {
        get("/") {
            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Summon with Ktor</title>
                </head>
                <body>
                    ${HomePage()}
                </body>
                </html>
            """.trimIndent()
            
            call.respondText(html, ContentType.Text.Html)
        }
    }
}

@Composable
fun HomePage(): String {
    return Column(
        modifier = Modifier().padding("20px")
    ) {
        Text("Welcome to Summon with Ktor!", modifier = Modifier().fontSize("24px")) +
        Text("This page was generated server-side!")
    }
}
```

### Component Composition

```kotlin
// Building complex UIs through composition
@Composable
fun UserCard(user: User): String {
    return Card(
        modifier = Modifier()
            .style("width", "300px")
            .padding("16px")
            .style("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
    ) {
        Column(modifier = Modifier().gap("8px")) {
            Text(user.name, modifier = Modifier().fontSize("18px").fontWeight("bold")) +
            Text(user.email, modifier = Modifier().color("#666")) +
            Button(
                text = "View Profile",
                modifier = Modifier()
                    .backgroundColor("#0077cc")
                    .color("white")
                    .padding("8px 16px")
                    .borderRadius("4px")
            )
        }
    }
}

@Composable
fun Card(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val cardModifier = modifier
        .backgroundColor("white")
        .borderRadius("8px")
        .style("border", "1px solid #e0e0e0")
    
    return Div(cardModifier) { content() }
}

// Usage
@Composable
fun UserList(users: List<User>): String {
    return Column(modifier = Modifier().gap("16px")) {
        users.joinToString("") { user ->
            UserCard(user)
        }
    }
}
```

### Error Handling

```kotlin
// Error handling in the standalone implementation
@Composable
fun SafeComponent(content: () -> String): String {
    return try {
        content()
    } catch (e: Exception) {
        ErrorComponent("An error occurred: ${e.message}")
    }
}

@Composable
fun ErrorComponent(message: String): String {
    return Div(
        modifier = Modifier()
            .backgroundColor("#fee")
            .color("#c00")
            .padding("16px")
            .borderRadius("4px")
            .style("border", "1px solid #fcc")
    ) {
        Text("Error: $message")
    }
}

// Usage
@Composable
fun App(): String {
    return SafeComponent {
        // Your potentially error-prone content
        ComplexComponent()
    }
}
```

## Best Practices

### 1. Keep Components Pure

```kotlin
// Good: Pure function that always returns the same output for the same input
@Composable
fun GreetingCard(name: String, age: Int): String {
    return Card {
        Text("Hello $name, you are $age years old!")
    }
}

// Avoid: Side effects or mutable global state
```

### 2. Use Consistent Patterns

```kotlin
// Consistent parameter order: content parameters first, modifier last
@Composable
fun MyComponent(
    title: String,
    content: String,
    modifier: Modifier = Modifier()
): String {
    // Implementation
}
```

### 3. Compose Small, Reusable Components

```kotlin
// Small, focused components
@Composable
fun IconButton(icon: String, onClick: String): String {
    return Button(
        text = icon,
        modifier = Modifier()
            .style("width", "32px")
            .style("height", "32px")
            .onClick(onClick)
    )
}

@Composable
fun Toolbar(): String {
    return Row(modifier = Modifier().gap("8px")) {
        IconButton("⏪", "goBack()") +
        IconButton("▶️", "play()") +
        IconButton("⏸️", "pause()")
    }
}
``` 