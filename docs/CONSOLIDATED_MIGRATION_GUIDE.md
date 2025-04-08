# Summon Consolidated Migration Guide

This document tracks the progress and provides guidance for two major refactoring efforts in the Summon library:
1.  **Renderer Access Migration**: Updating components to use `getPlatformRenderer()` instead of `PlatformRendererProvider.getRenderer()`.
2.  **Composition System Migration**: Transitioning from the `Composable` interface to a Jetpack Compose-like `@Composable` annotation system.
3.  **Package Structure Migration**: Ensuring consistent package naming across the codebase.
4.  **Platform Independence Migration**: Removing direct dependencies on specific frameworks like Quarkus.

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
2.  **Platform Composers**: `JvmComposer` 🔄, `JsComposer` ✅
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

## Part 4: Platform Independence Migration

### Rationale

Making Summon a framework-agnostic UI library that can be integrated with various backend technologies offers several benefits:

1. **Greater flexibility**: Users can integrate Summon with their preferred server technology (Quarkus, Spring, Ktor, etc.).
2. **Lower barriers to adoption**: No forced adoption of specific backend technologies.
3. **Cleaner architecture**: Clear separation between UI components and server integration.
4. **Simpler dependency management**: Fewer direct dependencies for the core library.

### Key Changes

1. **Removed direct framework dependencies**: Core Summon library no longer directly depends on Quarkus.
2. **Added optional integration configurations**: Created separate configurations for framework integration.
3. **Decoupled server-side rendering**: SSR capabilities work with any compatible server framework.
4. **Simplified build system**: Cleaner build dependencies focused on essential UI functionality.
5. **Integration packages**: Added dedicated packages under `code.yousef.summon.integration.*` for framework-specific adapters.

### Migration Steps

If you were using Summon with direct Quarkus integration, you'll need to:

1. **Add explicit Quarkus dependencies** to your project:
   ```kotlin
   // In your application's build.gradle.kts
   dependencies {
       implementation("io.quarkus:quarkus-core:3.21.1")
       implementation("io.quarkus:quarkus-kotlin:3.21.1")
       implementation("io.quarkus:quarkus-qute:3.21.1")
       
       // For Quarkus 3.7.x, you would use quarkus-vertx-web
       // implementation("io.quarkus:quarkus-vertx-web:3.7.1")
       
       // For Quarkus 3.21.x, you need these dependencies instead
       implementation("io.quarkus:quarkus-resteasy-reactive:3.21.1")
       implementation("io.quarkus:quarkus-vertx-http:3.21.1")
   }
   ```

2. **Use integration adapters** for server-side rendering with Quarkus:
   ```kotlin
   // Import the integration adapter
   import code.yousef.summon.integration.quarkus.QuarkusRenderer
   
   // Use the adapter in your Route handler
   @Route(path = "/my-page")
   fun handleRoute(context: RoutingContext) {
       val renderer = QuarkusRenderer(context.response())
       renderer.render {
           MyComponent()
       }
   }
   ```

### Common Dependency Issues

When migrating to newer versions of Quarkus (3.21.x and above), be aware of the following changes:

1. **Vertx Web Package Changes**:
   - The `quarkus-vertx-web` artifact from Quarkus 3.7.x has been restructured in newer versions.
   - Use `quarkus-resteasy-reactive` and `quarkus-vertx-http` for equivalent functionality.

2. **Import Path Adjustments**:
   - You may need to update import paths for Vertx classes.
   - For example, `io.vertx.ext.web.RoutingContext` may need to be imported directly.

3. **Integration Configuration**:
   - Add dependencies to both the `jvmMain` source set and the `quarkusIntegration` configuration:
   ```kotlin
   // In jvmMain dependencies block
   implementation("io.quarkus:quarkus-resteasy-reactive:3.21.1")
   
   // In quarkusIntegration configuration
   "quarkusIntegration"("io.quarkus:quarkus-resteasy-reactive:3.21.1")
   ```

4. **HTML Generation Imports**:
   - If you're using kotlinx-html for generating HTML content, ensure you have:
   ```kotlin
   implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
   ```

### Framework Integrations

#### Quarkus Integration

For Quarkus applications, Summon provides both direct server-side rendering and integration with Quarkus Qute templates:

##### Qute Template Integration

1. **Add Qute dependency** to your project:
   ```kotlin
   implementation("io.quarkus:quarkus-qute:3.7.1")
   ```

2. **Create a template extension method**:
   ```kotlin
   // QuteExtensions.kt
   @TemplateExtension
   class SummonQuteExtension {
       fun renderSummon(template: Template, componentName: String, props: Map<String, Any>): String {
           return SummonRenderer.renderComponent(componentName, props)
       }
   }
   ```

3. **Use in Qute templates**:
   ```html
   <!-- In your Qute template -->
   <div>
     {renderSummon('MyComponent', {'title': 'Hello', 'count': 5})}
   </div>
   ```

#### Spring Boot Integration (Planned)

For Spring Boot applications, Summon will provide integrations for both direct server-side rendering and Thymeleaf templates:

```kotlin
// Example Spring integration (coming soon)
@Controller
class MyController {
    @GetMapping("/my-page")
    fun renderPage(model: Model): String {
        // Register components for use in templates
        SummonSpringExtensions.registerComponent("Greeting") { props ->
            {
                val name = props["name"] as? String ?: "World"
                Text("Hello, $name!")
            }
        }
        
        model.addAttribute("name", "Spring User")
        return "my-template"
    }
}
```

```html
<!-- In Thymeleaf template -->
<div th:utext="${summon.render('Greeting', {'name': name})}"></div>
```

#### Ktor Integration (Planned)

For Ktor applications, Summon will provide direct rendering through response extensions:

```kotlin
// Example Ktor integration (coming soon)
routing {
    get("/") {
        call.respondSummon {
            Column {
                Text("Hello from Summon + Ktor!")
                Button(onClick = { /* client-side action */ }) {
                    Text("Click Me")
                }
            }
        }
    }
}
```

### Status

✅ Removed direct Quarkus dependencies from core build
✅ Created integration package structure
🔄 Implementing Quarkus integration adapter
🔄 Developing Qute template integration
⬜ Spring Boot integration
⬜ Ktor integration
⬜ Documentation for all framework integrations

---

## Consolidated Migration Status

**Legend:**
- ✅: Completed
- 🔄: In Progress
- ⬜: Not Started
- `[R]`: Renderer Access Migration (`getPlatformRenderer()`) Status
- `[A]`: Annotation Migration (`@Composable`) Status
- `[P]`: Package Structure Migration Status
- `[I]`: Platform Independence Migration Status

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
- `[P]` 🔄 Updated package structure consistency
- `[I]` ✅ Removed direct Quarkus dependencies

### Framework Integration
| Integration    | Status | Notes                                                  |
| :------------- | :----: | :----------------------------------------------------- |
| Quarkus        |   🔄   | Basic integration & Qute template support in progress  |
| Spring Boot    |   ⬜   | Planned for future release                             |
| Ktor           |   ⬜   | Planned for future release                             |

### Display Components
| Component | `[R]` Status | `[A]` Status | Notes                                                              |
| :-------- | :----------: | :----------: | :----------------------------------------------------------------- |
| Text      |      ✅      |      ✅      | Fully migrated to @Composable with enhanced styling and accessibility support |
| Image     |      ✅      |      ✅      | Migrated to @Composable function with additional features like onLoad/onError callbacks |
| Icon      |      ✅      |      ✅      | Already migrated with helper components (MaterialIcon, FontAwesomeIcon, SvgIcon) |

### Layout Components
| Component        | `[R]` Status | `[A]` Status | Notes                                                                  |
| :--------------- | :----------: | :----------: | :--------------------------------------------------------------------- |
| Box              |      ✅      |      ✅      | Already was @Composable function, added CompositionLocal usage         |
| Row              |      ✅      |      ✅      | Refactored for CompositionLocal & RowScope                             |
| Column           |      ✅      |      ✅      | Refactored for CompositionLocal & ColumnScope                          |
| Card             |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Divider          |      ✅      |      ✅      | Fixed compose method to use renderDivider(modifier) instead of renderDivider(this, receiver) |
| LazyColumn       |      ✅      |      ✅      | Refactored for CompositionLocal, needs state/visibility logic          |
| LazyRow          |      ✅      |      ✅      | Fixed compose method to use renderLazyRow(modifier) instead of renderLazyRow(this, receiver) |
| Grid             |      ✅      |      ✅      | Fixed compose method to use renderGrid(modifier) instead of renderGrid(this, receiver) |
| AspectRatio      |      ✅      |      ✅      | Refactored for CompositionLocal, needs modifier implementation         |
| Spacer           |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| ResponsiveLayout |      ✅      |      ✅      | Refactored for CompositionLocal, responsive logic via modifier         |
| ExpansionPanel   |      ✅      |      ✅      | Fixed compose method to use renderExpansionPanel(modifier) instead of renderExpansionPanel(this, receiver) |

### Input Components
| Component   | `[R]` Status | `[A]` Status | Notes                                                                  |
| :---------- | :----------: | :----------: | :--------------------------------------------------------------------- |
| Button      |      ✅      |      ✅      |                                                                        |
| TextField   |      ✅      |      ✅      | Fully migrated to @Composable, with StatefulTextField variant added     |
| Checkbox    |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| RadioButton |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Switch      |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Select      |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Slider      |      ✅      |      ✅      | Refactored for CompositionLocal, needs API reconciliation              |
| RangeSlider |      ✅      |      ✅      | Migrated to @Composable with FloatRange utility & StatefulRangeSlider  |
| DatePicker  |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| TimePicker  |      ✅      |      ✅      | Migrated to @Composable with helper functions & StatefulTimePicker added |
| FileUpload  |      ✅      |      ✅      | Refactored (conceptually), needs structure review for CompositionLocal |

### Feedback Components
| Component        | `[R]` Status | `[A]` Status | Notes                                                                  |
| :--------------- | :----------: | :----------: | :--------------------------------------------------------------------- |
| Alert            |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| Badge            |      ✅      |      ✅      | Refactored (conceptually) for CompositionLocal                         |
| ProgressBar      |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| CircularProgress |      ✅      |      ✅      | Refactored for CompositionLocal                                        |
| LinearProgress   |      ✅      |      ✅      | Newly created with CompositionLocal integration                         |
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

### Recently Migrated Components

#### LinearProgress Component

The LinearProgress component has been created following the new annotation-based approach:

```kotlin
@Composable
fun LinearProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier()
) {
    val composer = CompositionLocal.currentComposer
    val finalModifier = modifier
    
    composer?.startNode() // Start LinearProgress node
    if (composer?.inserting == true) {
        val renderer = getPlatformRenderer()
        renderer.renderProgressIndicator(progress ?: 0f, finalModifier)
    }
    composer?.endNode() // End LinearProgress node (self-closing)
}
```

This implementation:
1. Uses the annotation-based approach with `@Composable`
2. Directly uses `CompositionLocal.currentComposer` for composition management
3. Applies the platform renderer API properly with `renderProgressIndicator`
4. Handles null progress values with sensible defaults

#### Text Component

The Text component has been migrated from the interface-based approach to the annotation-based approach, and we've consolidated duplicate implementations:

**Before (Interface-based):**
```kotlin
data class Text(
    val text: String,
    modifier: Modifier = Modifier(),
    // Additional styling properties...
) : Composable, TextComponent {
    override fun <T> compose(receiver: T): T {
        // Implementation details
    }
}
```

**After (Annotation-based):**
```kotlin
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier(),
    // Additional styling properties...
) {
    // Implementation using CompositionLocal and platform renderer
}
```

We also:
1. Created a `TextComponent` class for backward compatibility with code expecting a class instance
2. Removed a duplicate simpler `Text` implementation from the `components/text` package
3. Standardized on the more feature-rich version in the `components/display` package

The migration demonstrates several improvements:
1. **Functional approach** - Composable function instead of a data class
2. **Modern composition** - Uses `CompositionLocal.currentComposer` for state management
3. **Proper node management** - Uses `startNode()` and `endNode()` for composition lifecycle
4. **Extracted helper methods** - Moved internal methods to top-level private functions
5. **Enhanced modularity** - More easily testable and extendable without class inheritance
6. **Component consolidation** - Removed duplicate implementations to avoid confusion

## Next Component Migration: BasicButton

For the next migration, we will focus on the BasicButton component, which currently uses the interface-based approach but would benefit from the new annotation-based composition system, especially for handling pressed/hover states and accessibility.

---

## Core Runtime Progress

We've successfully implemented the core components for the Compose-style runtime:

### Architecture Overview

The new runtime system is built on several key components:

1. **@Composable Annotation**: Marks functions that can participate in the composition system.
2. **Composer Interface**: Defines the core operations of a composition, including node hierarchy, state tracking, and change detection.
3. **Recomposer**: Manages state changes and triggers recomposition when state changes.
4. **CompositionLocal**: Provides context-based values within a composition, similar to React's Context. Now includes a `provideComposer` method for easier testing and composition.
5. **CompositionContext**: Manages the lifecycle of a composition, including creation, updating, and disposal.
6. **State Management**: State and MutableState interfaces with remember functions for tracking state across recompositions.
7. **Effects System**: LaunchedEffect, DisposableEffect, and SideEffect for managing side effects and lifecycle events.
8. **Platform Composers**: JsComposer implementation is complete with proper state, node tracking, and lifecycle management. It provides the foundation for JavaScript platform rendering.

### JsComposer Implementation

The JavaScript implementation of the Composer interface now correctly:

- Manages composition nodes and groups
- Tracks slot values and state changes
- Supports proper change detection
- Provides lifecycle management with disposable resources
- Integrates with JavaScript console for debugging
- Works with the CompositionLocal system

This implementation creates a solid foundation for JavaScript-based rendering of Summon components.

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

1. **Platform Composers**: Complete the JVM implementation of the Composer following the established patterns from JsComposer.
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

### 5. Renderer Method Parameter Mismatches
- **Issue**: When using the interface-based Composable approach, many components pass `this` and `receiver` to renderer methods, but the renderer expects only the `modifier` parameter.
- **Solution**: Update the compose method to call the renderer with just the modifier parameter:
  ```kotlin
  // Old (incorrect)
  override fun <T> compose(receiver: T): T {
      if (receiver is TagConsumer<*>) {
          @Suppress("UNCHECKED_CAST")
          return getRenderer().renderComponent(this, receiver as TagConsumer<T>)
      }
      return receiver
  }
  
  // New (fixed)
  override fun <T> compose(receiver: T): T {
      if (receiver is TagConsumer<*>) {
          getRenderer().renderComponent(modifier)
      }
      return receiver
  }
  ```

## Component Migration Example: TextField

To demonstrate the migration process, we've converted the TextField component from the interface-based approach to the annotation-based approach:

**Before (Interface-based):**
```kotlin
class TextField(
    val state: MutableState<String>,
    val onValueChange: (String) -> Unit = {},
    val label: String? = null,
    val placeholder: String? = null,
    val modifier: Modifier = Modifier(),
    val type: TextFieldType = TextFieldType.Text,
    val validators: List<Validator> = emptyList()
) : Composable, InputComponent, FocusableComponent {
    // State for validation errors
    private val validationErrors = mutableStateOf<List<String>>(emptyList())

    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderTextField(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
    
    // Validation methods
    fun validate(): Boolean { ... }
    fun getValidationErrors(): List<String> = ...
    fun isValid(): Boolean = ...
}
```

**After (Annotation-based):**
```kotlin
@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier(),
    label: String? = null,
    placeholder: String? = null,
    type: TextFieldType = TextFieldType.Text,
    isError: Boolean = false,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    validators: List<Validator> = emptyList()
) {
    // Use remember for local state
    val validationErrors = remember { mutableStateOf(emptyList<String>()) }
    
    // Direct renderer access via getPlatformRenderer()
    val renderer = getPlatformRenderer()
    
    // Call the renderer directly with parameters
    renderer.renderTextField(
        value = value,
        onValueChange = { ... },
        modifier = modifier,
        placeholder = placeholder ?: "",
        isError = isError || validationErrors.value.isNotEmpty(),
        type = type.toString().lowercase()
    )
    
    // Conditional rendering
    if (label != null) { ... }
    if (validationErrors.value.isNotEmpty()) { ... }
}

// Added a stateful version for easier state management
@Composable
fun StatefulTextField(...) {
    val textState = remember { mutableStateOf(initialValue) }
    TextField(
        value = textState.value,
        onValueChange = { newValue ->
            textState.value = newValue
            onValueChange(newValue)
        },
        ...
    )
}
```

This migration demonstrates several key improvements:
1. **Functional approach** - Composable function instead of a class
2. **Explicit state management** - Using remember {} for local state
3. **Direct parameter passing** - No need to pass "this" instance
4. **Simplified renderer access** - Using getPlatformRenderer() function
5. **Component variants** - Added StatefulTextField for convenience
6. **Declarative conditions** - Using if statements for conditional UI
7. **Improved separation of concerns** - UI logic separated from validation