# Core API Reference

This document provides detailed information about the core interfaces and classes in the Summon library.

## Table of Contents

- [Composable](#composable)
- [CompositionContext](#compositioncontext)
- [CompositionScope](#compositionscope)
- [Renderer](#renderer)
- [RenderUtils](#renderutils)

---

## Composable

The `@Composable` annotation marks functions that can participate in the composition system of Summon.

### Annotation Definition

```kotlin
package code.yousef.summon.annotation

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION, 
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER
)
annotation class Composable
```

### Description

`@Composable` marks functions that are part of the UI composition system. Functions annotated with `@Composable` can only be called from within other `@Composable` functions and are used to define the UI structure.

### Example

```kotlin
@Composable
fun MyComponent(text: String) {
    Div(
        modifier = Modifier.padding(16.px)
    ) {
        Text(text)
    }
}

// Usage
@Composable
fun App() {
    MyComponent("Hello, World!")
}
```

### Related Interfaces

- [CompositionScope](#compositionscope) - The scope within which composable functions operate

---

## CompositionContext

The `CompositionContext` class manages the current composition state during rendering.

### Class Definition

```kotlin
package code.yousef.summon.core

class CompositionContext {
    companion object {
        val current: CompositionContext?
            get() = // Returns the current composition context
        
        fun <T> withContext(context: CompositionContext, block: () -> T): T
    }
    
    // Properties
    val parent: CompositionContext?
    val depth: Int
    val renderer: Renderer
    
    // Methods
    fun createChildContext(): CompositionContext
}
```

### Description

`CompositionContext` represents the current state of composition and provides access to the renderer and parent context. It is used internally by the library to manage the composition tree.

### Example

```kotlin
val context = CompositionContext.current ?: error("No composition in progress")
val result = CompositionContext.withContext(context) {
    // Perform operations within this context
    renderComponent {
        MyComponent("Hello")
    }
}
```

---

## CompositionScope

The `CompositionScope` interface defines an environment where composable components can be added.

### Interface Definition

```kotlin
package code.yousef.summon.core

interface CompositionScope {
    fun compose(composable: @Composable () -> Unit)
}
```

### Description

`CompositionScope` provides a way to add child components to a parent component during composition. It is typically used in content blocks of container components.

### Example

```kotlin
@Composable
fun Column(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Div(modifier = modifier.display(Display.Flex).flexDirection(FlexDirection.Column)) {
        content()
    }
}

// Usage
Column {
    Text("Item 1")
    Text("Item 2")
}
```

---

## Renderer

The `Renderer` interface defines how components are rendered to a specific target.

### Interface Definition

```kotlin
package code.yousef.summon.core

interface Renderer<T> {
    fun render(composable: @Composable () -> Unit): T
    fun dispose()
}
```

### Description

`Renderer` is responsible for taking a `@Composable` function and rendering it to a specific target, such as the DOM for JavaScript or another rendering target for JVM. It also provides a way to clean up resources when rendering is no longer needed.

### Built-in Renderers

- **DOMRenderer** - Renders components to the browser DOM (JavaScript)
- **StringRenderer** - Renders components to an HTML string (JVM)

### Example

```kotlin
// JavaScript
val container = document.getElementById("app")
val renderer = DOMRenderer(container)
renderer.render {
    MyComponent("Hello, World!")
}

// Clean up when done
renderer.dispose()

// JVM
val renderer = StringRenderer()
val html = renderer.render {
    MyComponent("Hello, World!")
}
println(html)
```

---

## RenderUtils

The `RenderUtils` object provides utility functions for rendering components.

### Object Definition

```kotlin
package code.yousef.summon.core

object RenderUtils {
    // JavaScript platform
    fun renderComposable(container: Element, content: @Composable () -> Unit): Renderer<Element>
    fun hydrate(container: Element, content: @Composable () -> Unit): Renderer<Element>
    
    // JVM platform
    fun renderToString(content: @Composable () -> Unit): String
    fun renderToFile(file: File, content: @Composable () -> Unit)
}
```

### Description

`RenderUtils` provides convenient functions for rendering components to different targets without having to create a renderer instance manually.

### Example

```kotlin
// JavaScript
val container = document.getElementById("app")
val renderer = renderComposable(container) {
    MyComponent("Hello, World!")
}

// JVM
val html = renderToString {
    MyComponent("Hello, World!")
}
println(html)

// Hydration (for server-side rendering with client-side reactivation)
val container = document.getElementById("app")
hydrate(container) {
    MyComponent("Hello, World!")
}
```

---

## Error Handling

### SummonRenderException

```kotlin
package code.yousef.summon.core

class SummonRenderException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
```

This exception is thrown when there is an error during rendering. It includes the component stack trace to help identify where the error occurred.

### Example

```kotlin
try {
    renderComposable(container) {
        MyComponent("Hello, World!")
    }
} catch (e: SummonRenderException) {
    console.error("Render error:", e.message)
    // Display fallback UI
}
``` 