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
    renderComponent(component)
}
```

---

## CompositionScope

The `CompositionScope` interface defines an environment where composable components can be added.

### Interface Definition

```kotlin
package code.yousef.summon.core

interface CompositionScope {
    fun compose(composable: Composable)
}
```

### Description

`CompositionScope` provides a way to add child components to a parent component during composition. It is typically used in content blocks of container components.

### Example

```kotlin
class Column(
    private val modifier: Modifier = Modifier,
    private val content: CompositionScope.() -> Unit
) : Composable {
    override fun render() {
        Div(modifier = modifier.display(Display.Flex).flexDirection(FlexDirection.Column)) {
            // Create a composition scope and execute the content block
            object : CompositionScope {
                override fun compose(composable: Composable) {
                    composable.render()
                }
            }.apply(content)
        }
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
    fun render(composable: Composable): T
    fun dispose()
}
```

### Description

`Renderer` is responsible for taking a `Composable` and rendering it to a specific target, such as the DOM for JavaScript or another rendering target for JVM. It also provides a way to clean up resources when rendering is no longer needed.

### Built-in Renderers

- **DOMRenderer** - Renders components to the browser DOM (JavaScript)
- **StringRenderer** - Renders components to an HTML string (JVM)

### Example

```kotlin
// JavaScript
val container = document.getElementById("app")
val renderer = DOMRenderer(container)
val component = MyComponent()
renderer.render(component)

// Clean up when done
renderer.dispose()

// JVM
val renderer = StringRenderer()
val component = MyComponent()
val html = renderer.render(component)
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
    fun renderComposable(container: Element, composable: Composable): Renderer<Element>
    fun hydrate(container: Element, composable: Composable): Renderer<Element>
    
    // JVM platform
    fun renderToString(composable: Composable): String
    fun renderToFile(composable: Composable, file: File)
}
```

### Description

`RenderUtils` provides convenient functions for rendering components to different targets without having to create a renderer instance manually.

### Example

```kotlin
// JavaScript
val container = document.getElementById("app")
val renderer = renderComposable(container, MyComponent())

// JVM
val html = renderToString(MyComponent())
println(html)

// Hydration (for server-side rendering with client-side reactivation)
val container = document.getElementById("app")
hydrate(container, MyComponent())
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
    renderComposable(container, component)
} catch (e: SummonRenderException) {
    console.error("Render error:", e.message)
    // Display fallback UI
}
``` 