# Summon Consolidated Migration Guide

This document tracks the progress and provides guidance for two major refactoring efforts in the Summon library:
1.  **Renderer Access Migration**: Updating components to use `getPlatformRenderer()` instead of `PlatformRendererProvider.getRenderer()`.
2.  **Composition System Migration**: Transitioning from the `Composable` interface to a Jetpack Compose-like `@Composable` annotation system.

## Part 1: Renderer Access Migration (`getPlatformRenderer()`)

### Rationale

The transition from `PlatformRendererProvider.getRenderer()` to `getPlatformRenderer()` offers several benefits:

1.  **Improved maintainability**: Centralizes renderer access logic.
2.  **Better Compose pattern alignment**: Matches patterns in other Compose-based frameworks.
3.  **Simpler API**: Reduces verbosity.
4.  **Preparation for CompositionLocal**: Serves as a stepping stone toward using proper `CompositionLocal` for renderer access.

### How to Update a Component (Renderer Access)

1.  **Replace the import**:
    ```kotlin
    // Old
    import code.yousef.summon.core.PlatformRendererProvider
    // New
    import code.yousef.summon.core.getPlatformRenderer
    ```
2.  **Update renderer access**:
    ```kotlin
    // Old
    val renderer = PlatformRendererProvider.getRenderer()
    // New
    val renderer = getPlatformRenderer()
    ```
3.  **Update TODO comments**:
    ```kotlin
    // Old: // TODO: Replace PlatformRendererProvider with CompositionLocal access
    // New: // TODO: Replace getPlatformRenderer with CompositionLocal access
    ```

### Example (Renderer Access Migration - Text Component)

**Before:**
```kotlin
@Composable
fun Text(text: String, modifier: Modifier = Modifier()) {
    // ... modifier logic ...
    val renderer = PlatformRendererProvider.getRenderer() // Old access
    renderer.renderText(value = text, modifier = finalModifier)
}
```

**After:**
```kotlin
@Composable
fun Text(text: String, modifier: Modifier = Modifier()) {
    // ... modifier logic ...
    val renderer = getPlatformRenderer() // New access
    renderer.renderText(value = text, modifier = finalModifier)
}
```

### Status (Renderer Access Migration)

*(See Consolidated Status section below)*

---

## Part 2: Composition System Migration (`@Composable` Annotation)

### Rationale

Migrating from the `Composable` interface to `@Composable` functions aims to:

1.  Align Summon's API more closely with Jetpack Compose.
2.  Improve developer experience with a more idiomatic functional approach.
3.  Enable a more powerful composition runtime (state management, effects, etc.).
4.  Remove the need for manual `.compose(consumer)` calls.

### Current vs. Target Architecture

**Current (Interface-based):**
- Components implement `Composable` interface.
- Override `compose(receiver)` method.
- Manual invocation: `MyComponent(...).compose(consumer)`

**Target (Annotation-based):**
- Components are functions annotated with `@Composable`.
- Invoked directly: `MyComponent(...)`.
- Composition managed by a runtime `Composer`.

### Core Changes for Annotation Migration

1.  **New Runtime Files**:
    - `@Composable` annotation (`runtime/Composable.kt`) ✅
    - `Composer` interface (`runtime/Composer.kt`) ✅
    - `CompositionLocal` (`runtime/CompositionLocal.kt`) ✅
    - `CompositionContext` (`runtime/CompositionContext.kt`) ✅
    - `State`, `MutableState`, `remember` (`runtime/State.kt`, `runtime/Remember.kt`) ✅
    - `Recomposer` for state change tracking (`runtime/Recomposer.kt`) ✅
    - `Effects` for side effects (`runtime/Effects.kt`) ✅
2.  **Platform Composers**: `JvmComposer`, `JsComposer` 🔄
3.  **Component Transformation**: Convert all component *classes* implementing `Composable` to *functions* annotated with `@Composable`.
4.  **Renderer Updates**: `PlatformRenderer` methods updated to accept parameters directly instead of component instances.
5.  **Import Updates**: Replace `core.Composable` with `runtime.Composable`, add imports for new runtime utilities.

### How to Update a Component (Annotation Migration)

1.  **Convert Class to Function**: Change `class MyComponent(...) : Composable` to `@Composable fun MyComponent(...)`.
2.  **Remove `compose` Method**: Delete the `override fun compose(...)` method.
3.  **Update Imports**: Replace `code.yousef.summon.core.Composable` with `code.yousef.summon.runtime.Composable`. Add imports for `CompositionLocal`, `remember`, state/effect functions as needed.
4.  **Use Composition Runtime**: Access the composer via `CompositionLocal.currentComposer`. Use `startNode()`, `endNode()`, `inserting`.
5.  **Call Renderer Directly**: Get the renderer using `getPlatformRenderer()` and call its methods with the function parameters.
6.  **Handle Content Lambdas**: Adapt components like `Button`, `Card`, `Box` to accept `@Composable () -> Unit` content lambdas.

### Example (Annotation Migration - Button Component)

**Before (Class-based):**
```kotlin
class Button(
    val text: String, // Often replaced by content lambda
    val onClick: () -> Unit,
    /* ... */
) : Composable {
    override fun <T> compose(receiver: T): T {
        // Calls renderer with 'this' instance
        return PlatformRendererProvider.getRenderer().renderButton(this, receiver)
    }
}
// Usage: Button("Click", { ... }).compose(consumer)
```

**After (Annotation-based):**
```kotlin
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier(),
    // Use content lambda for children
    content: @Composable () -> Unit
) {
    val composer = CompositionLocal.currentComposer
    composer?.startNode() // Start composition node
    if (composer?.inserting == true) {
        // Render implementation using getPlatformRenderer()
        val renderer = getPlatformRenderer()
        // Pass parameters directly
        renderer.renderButton(onClick = onClick, enabled = true, modifier = modifier)
    }
    // Compose the content lambda within the button's scope
    content()
    composer?.endNode() // End composition node
}
// Usage: Button(onClick = { ... }) { Text("Click") }
```

### Status (Annotation Migration)

*(See Consolidated Status section below)*

---

## Consolidated Migration Status

**Legend:**
- ✅: Completed
- 🔄: In Progress
- ⬜: Not Started
- `[R]`: Renderer Access Migration (`getPlatformRenderer()`) Status
- `[A]`: Annotation Migration (`@Composable`) Status

### Core Runtime & Infrastructure
- `[A]` ✅ `@Composable` annotation in `runtime/Composable.kt`
- `[A]` ✅ `Composer` interface in `runtime/Composer.kt`
- `[A]` ✅ `CompositionLocal` in `runtime/CompositionLocal.kt`
- `[A]` ✅ `Recomposer` for state change tracking in `runtime/Recomposer.kt`
- `[A]` ✅ `CompositionContext` in `runtime/CompositionContext.kt`
- `[A]` ✅ `State`, `MutableState`, `remember` in `runtime/State.kt`, `runtime/Remember.kt`
- `[A]` ✅ `LaunchedEffect`, `DisposableEffect` in `runtime/Effects.kt`
- `[A]` 🔄 Platform Composers (`JvmComposer`, `JsComposer`)
- `[A]` ✅ Sample application using the new compose runtime (`SimpleHelloWorld.kt`)
- `[R]` 🔄 `getPlatformRenderer()` function 

### Display Components
| Component | `[R]` Status | `[A]` Status | Notes                                                              |
| :-------- | :----------: | :----------: | :----------------------------------------------------------------- |
| Text      |      ✅      |      ✅      |                                                                    |
| Image     |      ✅      |      ⬜      |                                                                    |
| Icon      |      ✅      |      ⬜      |                                                                    |

### Layout Components
| Component        | `[R]` Status | `[A]` Status | Notes                                                                  |
| :--------------- | :----------: | :----------: | :--------------------------------------------------------------------- |
| Box              |      ✅      |      ✅      | Already was @Composable function, added CompositionLocal usage         |
| Row              |      ✅      |      ✅      | Refactored for CompositionLocal & RowScope                             |
| Column           |      ✅      |      ✅      | Refactored for CompositionLocal & ColumnScope                          |
| Card             |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Divider          |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| LazyColumn       |      ✅      |      ✅      | Refactored for CompositionLocal, needs state/visibility logic          |
| LazyRow          |      ✅      |      ✅      | Refactored for CompositionLocal, needs state/visibility logic          |
| Grid             |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| AspectRatio      |      ✅      |      ✅      | Refactored for CompositionLocal, needs modifier implementation         |
| Spacer           |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| ResponsiveLayout |      ✅      |      ✅      | Refactored for CompositionLocal, responsive logic via modifier         |
| ExpansionPanel   |      ✅      |      ✅      | Refactored (simplified), persistent linter errors, needs state/content |

### Input Components
| Component   | `[R]` Status | `[A]` Status | Notes                                                                  |
| :---------- | :----------: | :----------: | :--------------------------------------------------------------------- |
| Button      |      ✅      |      ✅      |                                                                        |
| TextField   |      ✅      |      ✅      | Refactored for CompositionLocal, check modifier/renderer calls         |
| Checkbox    |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| RadioButton |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Switch      |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Select      |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Slider      |      ✅      |      ✅      | Refactored for CompositionLocal, needs API reconciliation              |
| DatePicker  |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| TimePicker  |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| FileUpload  |      ✅      |      ✅      | Refactored (conceptually), needs structure review for CompositionLocal |

### Feedback Components
| Component        | `[R]` Status | `[A]` Status | Notes                                                                  |
| :--------------- | :----------: | :----------: | :--------------------------------------------------------------------- |
| Alert            |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Badge            |      ✅      |      ✅      | Refactored (conceptually) for CompositionLocal                         |
| ProgressBar      |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| CircularProgress |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Tooltip          |      ✅      |      ✅      | Refactored for CompositionLocal, significant structure change          |

### Navigation Components
| Component        | `[R]` Status | `[A]` Status | Notes                                    |
| :--------------- | :----------: | :----------: | :--------------------------------------- |
| Link             |      ✅      |      ✅      | Refactored for CompositionLocal          |
| TabLayout        |      ✅      |      ✅      | Refactored for CompositionLocal          |

### Animation Components
| Component          | `[R]` Status | `[A]` Status | Notes                           |
| :----------------- | :----------: | :----------: | :------------------------------ |
| AnimatedVisibility |      ✅      |      ✅      | Refactored for CompositionLocal |
| AnimatedContent    |      ✅      |      ✅      | Refactored for CompositionLocal |


---

## Core Runtime Progress

We've successfully implemented the core components for the Compose-style runtime:

### Architecture Overview

The new runtime system is built on several key components:

1. **@Composable Annotation**: Marks functions that can participate in the composition system.
2. **Composer Interface**: Defines the core operations of a composition, including node hierarchy, state tracking, and change detection.
3. **Recomposer**: Manages state changes and triggers recomposition when state changes.
4. **CompositionLocal**: Provides context-based values within a composition, similar to React's Context.
5. **CompositionContext**: Manages the lifecycle of a composition, including creation, updating, and disposal.
6. **State Management**: State and MutableState interfaces with remember functions for tracking state across recompositions.
7. **Effects System**: LaunchedEffect, DisposableEffect, and SideEffect for managing side effects and lifecycle events.

### Effects System

The Effects system provides three main types of effects:

1. **LaunchedEffect**: Runs a suspend function in a coroutine scope that is automatically canceled when the composition leaves the scene or when the key changes. Useful for asynchronous operations.
   ```kotlin
   @Composable
   fun LaunchedEffect(key: Any? = null, block: suspend () -> Unit)
   ```

2. **DisposableEffect**: Performs an action when entering the composition and provides a cleanup function that runs when leaving or when the key changes. Perfect for resource management.
   ```kotlin
   @Composable
   fun DisposableEffect(key: Any? = null, effect: () -> (() -> Unit))
   ```

3. **SideEffect**: Executes code on every successful composition. Useful for synchronizing with non-compose state.
   ```kotlin
   @Composable
   fun SideEffect(effect: () -> Unit)
   ```

These effects are managed through the Composer's slot system, which tracks effect state across recompositions and ensures proper cleanup.

### SimpleHelloWorld Example

We've created a simple demo application using the new composition system:

```kotlin
// In SimpleHelloWorld.kt
@Composable
fun HelloWorld() {
    // Create some state
    val count = remember { mutableStateOf(0) }
    
    // Display a greeting
    Text("Hello, Summon World!")
    
    // Display the counter
    Text("You've clicked ${count.value} times")
    
    // Add a button to increment the counter
    Button(onClick = { count.value++ }) {
        Text("Click me")
    }
}
```

This demonstrates:
- State creation and usage with `remember` and `mutableStateOf`
- Content lambda for nested components
- Reactive UI that updates when state changes

## Next Steps

1. **Platform Composers**: Create JVM and JS implementations of the Composer.
2. **CompositionLocal for Renderer Access**: Replace direct calls to getPlatformRenderer with a proper CompositionLocal.
3. **Component Migration**: Continue converting component classes to use the annotation-based approach.
4. **Integration Testing**: Test the state management and recomposition system in real applications.
5. **Documentation**: Create guides for component authors on using the new system.

---

## Common Issues and Solutions

### 1. Parameter Mismatches (Renderer Access)
- **Issue**: Component function signature differs from the renderer method expects (e.g., `ProgressBar` needing `ProgressType`).
- **Solution**: Check `PlatformRenderer` interface, import required types, update the component call.

### 2. Missing Imports / Unresolved References
- **Issue**: Errors for unresolved modifiers, types, or runtime functions after changes.
- **Solution**: Check imports:
  ```kotlin
  import code.yousef.summon.runtime.Composable
  import code.yousef.summon.runtime.remember
  import code.yousef.summon.runtime.mutableStateOf
  import code.yousef.summon.runtime.CompositionLocal
  import code.yousef.summon.core.getPlatformRenderer
  ```

### 3. Content Lambda Conversion
- **Issue**: Converting container components that need to render children.
- **Solution**: Replace child component parameters with a content lambda parameter:
  ```kotlin
  // Old
  class Card(val content: Composable) : Composable { ... }
  
  // New
  @Composable
  fun Card(content: @Composable () -> Unit) { ... }
  ```

### 4. State Management Conversion
- **Issue**: Converting component state properties to remembered state.
- **Solution**: Use the remember function with mutableStateOf:
  ```kotlin
  // Old (in a class)
  var isExpanded = false
  
  // New (in a function)
  val isExpanded = remember { mutableStateOf(false) }
  // Access with isExpanded.value or use property delegate:
  var expanded by remember { mutableStateOf(false) }
  ```

## Part 3: Package Structure Migration

### Rationale

To ensure proper organization and maintain consistency across the codebase, we're restructuring package names to use fully qualified paths. This change addresses several issues:

1. **Compiler errors**: Many unresolved references due to improper package declarations
2. **Import conflicts**: Ambiguous imports leading to compilation failures
3. **Code organization**: Better alignment with Kotlin's package naming conventions
4. **KMP support**: Improved compatibility with Kotlin Multiplatform

### Key Changes

1. All packages should use fully qualified names following the pattern: `code.yousef.summon.<module>`
2. Import statements must reference fully qualified names
3. Files that previously used short package names (e.g., `package modifier`) must be updated to use full package paths (e.g., `package code.yousef.summon.modifier`)

### Example Package Structure Updates

**Before (problematic):**
```kotlin
package modifier

import PlatformRendererProvider
```

**After (fixed):**
```kotlin
package code.yousef.summon.modifier

import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.PlatformRendererProvider
```

### Progress on Package Structure Migration

- ✅ Created utility function `getPlatformRenderer()` and `getCompositionLocal()` to centralize imports
- ✅ Fixed core modifier files (Modifier.kt, CoreModifiers.kt, etc.)
- ✅ Corrected package structure in PlatformRenderer.kt
- ✅ Updated ModifierExtensions.kt with proper imports
- 🔄 Working on fixing components to use the correct imports
- 🔄 Addressing Composable annotation imports across components

This migration is currently in progress with focus on resolving the 'Modifier' related issues first, followed by 'CompositionLocal' references.

---

## Common Issues and Solutions

### 1. Parameter Mismatches (Renderer Access)
- **Issue**: Component function signature differs from the renderer method expects (e.g., `ProgressBar` needing `ProgressType`).
- **Solution**: Check `PlatformRenderer` interface, import required types, update the component call.

### 2. Missing Imports / Unresolved References
- **Issue**: Errors for unresolved modifiers, types, or runtime functions after changes.
- **Solution**: Check imports:
  ```kotlin
  import code.yousef.summon.runtime.Composable
  import code.yousef.summon.runtime.remember
  import code.yousef.summon.runtime.mutableStateOf
  import code.yousef.summon.runtime.CompositionLocal
  import code.yousef.summon.core.getPlatformRenderer
  ```

### 3. Content Lambda Conversion
- **Issue**: Converting container components that need to render children.
- **Solution**: Replace child component parameters with a content lambda parameter:
  ```kotlin
  // Old
  class Card(val content: Composable) : Composable { ... }
  
  // New
  @Composable
  fun Card(content: @Composable () -> Unit) { ... }
  ```

### 4. State Management Conversion
- **Issue**: Converting component state properties to remembered state.
- **Solution**: Use the remember function with mutableStateOf:
  ```kotlin
  // Old (in a class)
  var isExpanded = false
  
  // New (in a function)
  val isExpanded = remember { mutableStateOf(false) }
  // Access with isExpanded.value or use property delegate:
  var expanded by remember { mutableStateOf(false) }
  ```