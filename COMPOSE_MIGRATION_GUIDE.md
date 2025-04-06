# Summon Migration Guide: Transitioning from `Composable` interface to `@Composable` annotation

This document outlines the comprehensive plan for migrating the Summon library from the current interface-based approach to a more Jetpack Compose-like annotation-based system. Since the library is not in use yet, we can implement a clean migration without concerns for backward compatibility.

## Current Architecture

Currently, Summon implements composable components using:

1. An interface-based approach where components implement the `Composable` interface
2. Each component must override the `compose` method
3. Component instances are created via class constructors
4. Rendering happens through a `PlatformRenderer` implementation

```kotlin
// Current implementation
interface Composable {
    fun <T> compose(receiver: T): T
}

class Button(
    val text: String,
    val onClick: () -> Unit,
    val modifier: Modifier = Modifier()
) : Composable {
    override fun <T> compose(receiver: T): T {
        // Rendering implementation
        return PlatformRendererProvider.getRenderer().renderButton(this, receiver as TagConsumer<T>)
    }
}

// Usage
Button(
    text = "Click me",
    onClick = { /* do something */ }
).compose(consumer)
```

## Target Architecture

We want to transition to a more Jetpack Compose-like approach:

1. Components become functions annotated with `@Composable`
2. Components are invoked directly rather than calling a `.compose()` method
3. A composition system manages the rendering
4. Improved DSL for nested components

```kotlin
// Target implementation
@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier()
) {
    // Implementation that will be transformed by the compiler
    currentComposer.startNode()
    
    if (currentComposer.inserting) {
        // Create and emit actual UI elements
    }
    
    currentComposer.endNode()
}

// Usage
Button(
    text = "Click me",
    onClick = { /* do something */ }
)
```

## Files to Change

Here's a comprehensive list of all files that need to be modified or created for this migration:

### 1. Core Runtime Files

#### New Files to Create:

1. **src/commonMain/kotlin/code/yousef/summon/runtime/Composable.kt**
   - Define the `@Composable` annotation
   - Remove the old `Composable` interface (or deprecate it)

```kotlin
package code.yousef.summon.runtime

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
annotation class Composable
```

2. **src/commonMain/kotlin/code/yousef/summon/runtime/Composer.kt**
   - Define the Composer interface and core composition logic

```kotlin
package code.yousef.summon.runtime

interface Composer {
    fun startNode()
    fun endNode()
    val inserting: Boolean
    // Other composition methods
}
```

3. **src/commonMain/kotlin/code/yousef/summon/runtime/CompositionLocal.kt**
   - Implement the thread-local composition system

```kotlin
package code.yousef.summon.runtime

object CompositionLocal {
    private val currentComposerThreadLocal = ThreadLocal<Composer?>()
    
    val currentComposer: Composer?
        get() = currentComposerThreadLocal.get()
    
    fun setCurrentComposer(composer: Composer?) {
        currentComposerThreadLocal.set(composer)
    }
}
```

4. **src/commonMain/kotlin/code/yousef/summon/runtime/CompositionContext.kt**
   - Manage composition context for rendering

```kotlin
package code.yousef.summon.runtime

class CompositionContext(val composer: Composer) {
    companion object {
        fun withContext(context: CompositionContext, block: () -> Unit) {
            val previousComposer = CompositionLocal.currentComposer
            CompositionLocal.setCurrentComposer(context.composer)
            try {
                block()
            } finally {
                CompositionLocal.setCurrentComposer(previousComposer)
            }
        }
    }
}
```

5. **src/commonMain/kotlin/code/yousef/summon/runtime/ComposableDsl.kt**
   - Define DSL markers for scope-specific composables

```kotlin
package code.yousef.summon.runtime

@DslMarker
annotation class ComposableDsl
```

#### Files to Modify:

1. **src/commonMain/kotlin/code/yousef/summon/core/Composable.kt**
   - Remove or deprecate the existing interface
   - Replace with imports for the new annotation-based system

2. **src/commonMain/kotlin/code/yousef/summon/core/PlatformRenderer.kt**
   - Transition from component-based to function-based rendering
   - Update method signatures to accept parameters instead of component instances

3. **src/jvmMain/kotlin/code/yousef/summon/JvmPlatformRenderer.kt**
   - Update implementation for function-based rendering

4. **src/jsMain/kotlin/code/yousef/summon/JsPlatformRenderer.kt**
   - Update implementation for function-based rendering

5. **src/commonMain/kotlin/code/yousef/summon/core/PlatformRendererProvider.kt**
   - Update provider to support the new composition system

### 2. Platform-Specific Renderers

#### New Files to Create:

1. **src/jvmMain/kotlin/code/yousef/summon/runtime/JvmComposer.kt**
   - Implement JVM-specific composer

```kotlin
package code.yousef.summon.runtime

import kotlinx.html.TagConsumer

class JvmComposer(private val consumer: TagConsumer<*>) : Composer {
    // Implementation for JVM platform
}
```

2. **src/jsMain/kotlin/code/yousef/summon/runtime/JsComposer.kt**
   - Implement JS-specific composer

```kotlin
package code.yousef.summon.runtime

import org.w3c.dom.Element

class JsComposer(private val rootElement: Element) : Composer {
    // Implementation for JS platform
}
```

### 3. Component Files

Convert all component classes to functions with @Composable annotation. Here's a list of all component files that need to be changed:

#### Layout Components:

1. **src/commonMain/kotlin/code/yousef/summon/components/layout/Box.kt**
   - Convert class to @Composable function
   - Update implementation to use composition runtime

2. **src/commonMain/kotlin/code/yousef/summon/components/layout/Row.kt**
   - Convert class to @Composable function
   - Implement RowScope for DSL

3. **src/commonMain/kotlin/code/yousef/summon/components/layout/Column.kt**
   - Convert class to @Composable function
   - Implement ColumnScope for DSL

4. **src/commonMain/kotlin/code/yousef/summon/components/layout/Grid.kt**
   - Convert class to @Composable function

5. **src/commonMain/kotlin/code/yousef/summon/components/layout/Spacer.kt**
   - Convert class to @Composable function

6. **src/commonMain/kotlin/code/yousef/summon/components/layout/Divider.kt**
   - Convert class to @Composable function

7. **src/commonMain/kotlin/code/yousef/summon/components/layout/Card.kt**
   - Convert class to @Composable function

8. **src/commonMain/kotlin/code/yousef/summon/components/layout/LazyColumn.kt**
   - Convert class to @Composable function
   - Implement LazyColumnScope

9. **src/commonMain/kotlin/code/yousef/summon/components/layout/LazyRow.kt**
   - Convert class to @Composable function
   - Implement LazyRowScope

10. **src/commonMain/kotlin/code/yousef/summon/components/layout/AspectRatio.kt**
    - Convert class to @Composable function

11. **src/commonMain/kotlin/code/yousef/summon/components/layout/ResponsiveLayout.kt**
    - Convert class to @Composable function

12. **src/commonMain/kotlin/code/yousef/summon/components/layout/ExpansionPanel.kt**
    - Convert class to @Composable function

#### Input Components:

1. **src/commonMain/kotlin/code/yousef/summon/components/input/Button.kt**
   - Convert class to @Composable function

2. **src/commonMain/kotlin/code/yousef/summon/components/input/TextField.kt**
   - Convert class to @Composable function

3. **src/commonMain/kotlin/code/yousef/summon/components/input/Checkbox.kt**
   - Convert class to @Composable function

4. **src/commonMain/kotlin/code/yousef/summon/components/input/RadioButton.kt**
   - Convert class to @Composable function

5. **src/commonMain/kotlin/code/yousef/summon/components/input/Slider.kt**
   - Convert class to @Composable function

6. **src/commonMain/kotlin/code/yousef/summon/components/input/Switch.kt**
   - Convert class to @Composable function

7. **src/commonMain/kotlin/code/yousef/summon/components/input/DatePicker.kt**
   - Convert class to @Composable function

8. **src/commonMain/kotlin/code/yousef/summon/components/input/TimePicker.kt**
   - Convert class to @Composable function

#### Display Components:

1. **src/commonMain/kotlin/code/yousef/summon/components/display/Text.kt**
   - Convert class to @Composable function

2. **src/commonMain/kotlin/code/yousef/summon/components/display/Image.kt**
   - Convert class to @Composable function

3. **src/commonMain/kotlin/code/yousef/summon/components/display/Icon.kt**
   - Convert class to @Composable function

#### Feedback Components:

1. **src/commonMain/kotlin/code/yousef/summon/components/feedback/Alert.kt**
   - Convert class to @Composable function

2. **src/commonMain/kotlin/code/yousef/summon/components/feedback/Badge.kt**
   - Convert class to @Composable function

3. **src/commonMain/kotlin/code/yousef/summon/components/feedback/Progress.kt**
   - Convert class to @Composable function

4. **src/commonMain/kotlin/code/yousef/summon/components/feedback/Tooltip.kt**
   - Convert class to @Composable function

#### Navigation Components:

1. **src/commonMain/kotlin/code/yousef/summon/components/navigation/Link.kt**
   - Convert class to @Composable function

2. **src/commonMain/kotlin/code/yousef/summon/components/navigation/TabLayout.kt**
   - Convert class to @Composable function

3. **src/commonMain/kotlin/code/yousef/summon/routing/Router.kt**
   - Convert class to @Composable function

#### Animation Components:

1. **src/commonMain/kotlin/code/yousef/summon/animation/AnimatedVisibility.kt**
   - Convert class to @Composable function

2. **src/commonMain/kotlin/code/yousef/summon/animation/AnimatedContent.kt**
   - Convert class to @Composable function

### 4. State and Effects System

#### New Files to Create:

1. **src/commonMain/kotlin/code/yousef/summon/runtime/State.kt**
   - Implement reactive state system

```kotlin
package code.yousef.summon.runtime

interface State<T> {
    val value: T
}

class MutableState<T>(initialValue: T) : State<T> {
    private var _value: T = initialValue
    
    override var value: T
        get() = _value
        set(newValue) {
            val oldValue = _value
            _value = newValue
            if (oldValue != newValue) {
                notifyObservers()
            }
        }
    
    // Observer pattern implementation
    private fun notifyObservers() {
        // Implementation
    }
}
```

2. **src/commonMain/kotlin/code/yousef/summon/runtime/Remember.kt**
   - Implement remember functionality

```kotlin
package code.yousef.summon.runtime

@Composable
fun <T> remember(calculation: () -> T): T {
    // Implementation
    return calculation()
}

@Composable
fun <T> rememberState(initial: T): MutableState<T> {
    return remember { MutableState(initial) }
}
```

3. **src/commonMain/kotlin/code/yousef/summon/runtime/Effects.kt**
   - Implement composable effects

```kotlin
package code.yousef.summon.runtime

import kotlinx.coroutines.CoroutineScope

@Composable
fun LaunchedEffect(
    key1: Any?,
    block: suspend CoroutineScope.() -> Unit
) {
    // Implementation
}

interface DisposableEffectResult {
    fun onDispose()
}

interface DisposableEffectScope {
    fun onDispose(callback: () -> Unit): DisposableEffectResult
}

@Composable
fun DisposableEffect(
    key1: Any?,
    effect: DisposableEffectScope.() -> DisposableEffectResult
) {
    // Implementation
}
```

### 5. Import Changes

When migrating from the interface-based approach to the annotation-based approach, you'll need to update imports across the codebase. Here's a guide to the import changes needed:

#### For Component Files:

Old imports (to be removed):
```kotlin
import code.yousef.summon.core.Composable // Interface
import code.yousef.summon.core.PlatformRendererProvider
import kotlinx.html.TagConsumer
```

New imports (to be added):
```kotlin
import code.yousef.summon.runtime.Composable // Annotation
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.remember
import code.yousef.summon.runtime.rememberState
```

#### For Scope-based Components (like Column):

Additional imports:
```kotlin
import code.yousef.summon.runtime.ComposableDsl
```

#### For Components Using Effects:

Additional imports:
```kotlin
import code.yousef.summon.runtime.LaunchedEffect
import code.yousef.summon.runtime.DisposableEffect
import code.yousef.summon.runtime.DisposableEffectScope
import kotlinx.coroutines.CoroutineScope
```

#### For Components Using State:

Additional imports:
```kotlin
import code.yousef.summon.runtime.State
import code.yousef.summon.runtime.MutableState
```

#### Import Changes by Component Type:

1. **Basic Components (Button, Text, etc.)**:
   ```kotlin
   // Old
   import code.yousef.summon.core.Composable
   import code.yousef.summon.core.PlatformRendererProvider
   
   // New
   import code.yousef.summon.runtime.Composable
   import code.yousef.summon.runtime.CompositionLocal
   import code.yousef.summon.getPlatformRenderer
   ```

2. **Container Components (Row, Column, etc.)**:
   ```kotlin
   // Old
   import code.yousef.summon.core.Composable
   import code.yousef.summon.core.PlatformRendererProvider
   
   // New
   import code.yousef.summon.runtime.Composable
   import code.yousef.summon.runtime.CompositionLocal
   import code.yousef.summon.runtime.ComposableDsl
   import code.yousef.summon.getPlatformRenderer
   ```

3. **Stateful Components**:
   ```kotlin
   // Old
   import code.yousef.summon.core.Composable
   import code.yousef.summon.core.PlatformRendererProvider
   
   // New
   import code.yousef.summon.runtime.Composable
   import code.yousef.summon.runtime.CompositionLocal
   import code.yousef.summon.runtime.State
   import code.yousef.summon.runtime.MutableState
   import code.yousef.summon.runtime.remember
   import code.yousef.summon.runtime.rememberState
   import code.yousef.summon.getPlatformRenderer
   ```

### 6. Example File Transformations

Here are examples of how to transform specific component files:

#### Button Component:

**Current (src/commonMain/kotlin/code/yousef/summon/components/input/Button.kt):**

```kotlin
package code.yousef.summon.components.input

import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer

class Button(
    val text: String,
    val onClick: () -> Unit,
    val modifier: Modifier = Modifier()
) : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderButton(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
}
```

**New Version:**

```kotlin
package code.yousef.summon.components.input

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.getPlatformRenderer
import code.yousef.summon.modifier.Modifier

@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier()
) {
    val composer = CompositionLocal.currentComposer
    
    composer?.startNode()
    if (composer?.inserting == true) {
        // Render implementation
        val renderer = getPlatformRenderer()
        renderer.renderButton(text, onClick, modifier)
    }
    composer?.endNode()
}
```

#### Spacer Component:

**Current (src/commonMain/kotlin/code/yousef/summon/components/layout/Spacer.kt):**

```kotlin
package code.yousef.summon.components.layout

import code.yousef.summon.core.Composable
import code.yousef.summon.core.PlatformRendererProvider
import kotlinx.html.TagConsumer

class Spacer(
    val size: String,
    val isVertical: Boolean = true
) : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderSpacer(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
}
```

**New Version:**

```kotlin
package code.yousef.summon.components.layout

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.getPlatformRenderer

@Composable
fun Spacer(
    size: String,
    isVertical: Boolean = true
) {
    val composer = CompositionLocal.currentComposer
    
    composer?.startNode()
    if (composer?.inserting == true) {
        // Render implementation
        val renderer = getPlatformRenderer()
        renderer.renderSpacer(size, isVertical)
    }
    composer?.endNode()
}
```

## Implementation Timeline

1. **Phase 1 (Foundation)**: Create the core annotation, composition system, and state management
2. **Phase 2 (Adaptation)**: Implement the first few basic components and ensure they work on all platforms
3. **Phase 3 (Migration)**: Systematically migrate all components, starting with the most fundamental ones
4. **Phase 4 (Optimization)**: Improve performance, fix bugs, and add more advanced features
5. **Phase 5 (Documentation)**: Update all documentation and examples to use the new system

## Potential Challenges and Solutions

1. **Platform differences**: Use expect/actual declarations to handle platform-specific implementation details
2. **Compiler limitations**: Use runtime code if compiler plugins are not an option
3. **Testing**: Create test utilities that work with the new annotation-based system

## Conclusion

This migration will significantly improve the developer experience by making Summon more similar to Jetpack Compose, while maintaining the cross-platform capabilities. Since the library is not in use yet, we can implement a clean migration to the new API model without concerns for backward compatibility. 