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

The migration demonstrates several improvements:
1. **Functional approach** - Composable function instead of a data class
2. **Modern composition** - Uses `CompositionLocal.currentComposer` for state management
3. **Proper node management** - Uses `startNode()` and `endNode()` for composition lifecycle
4. **Extracted helper methods** - Moved internal methods to top-level private functions
5. **Enhanced modularity** - More easily testable and extendable without class inheritance
6. **Component consolidation** - Removed duplicate implementations to avoid confusion

## Recent Component Fixes

### 1. ResponsiveLayout Component

The `ResponsiveLayout` component has been updated to fix renderer method parameter mismatches:

**Before (Incorrect):**
```kotlin
override fun <T> compose(receiver: T): T {
    if (receiver is TagConsumer<*>) {
        @Suppress("UNCHECKED_CAST")
        return getRenderer().renderResponsiveLayout(this, receiver as TagConsumer<T>)
    }
    return receiver
}
```

**After (Fixed):**
```kotlin
override fun <T> compose(receiver: T): T {
    if (receiver is TagConsumer<*>) {
        getRenderer().renderResponsiveLayout(modifier)
    }
    return receiver
}
```

This fix addresses several issues:
1. Return type mismatch (Unit vs T)
2. Argument type mismatch (ResponsiveLayout vs Modifier)
3. Too many arguments for renderResponsiveLayout

### 2. Spacer Component

The `Spacer` component has been updated to follow the same pattern:

**Before (Incorrect):**
```kotlin
class Spacer(
    val size: String,
    val isVertical: Boolean = true
) : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return getRenderer().renderSpacer(this, receiver as TagConsumer<T>)
        }
        return receiver
    }
}
```

**After (Fixed):**
```kotlin
class Spacer(
    val size: String,
    val isVertical: Boolean = true,
    val modifier: Modifier = Modifier()
) : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            getRenderer().renderSpacer(modifier)
        }
        return receiver
    }
}
```

Key changes:
1. Added a `modifier` parameter to the class constructor
2. Updated the `compose` method to pass only the modifier to `renderSpacer`
3. Removed the return in the `if` block to fix return type mismatch
4. Removed the `@Suppress("UNCHECKED_CAST")` annotation that's no longer needed

### 3. TabLayout Component

The `TabLayout` component has been fixed to properly pass parameters to the renderer:

**Before (Incorrect):**
```kotlin
override fun <T> compose(receiver: T): T {
    if (receiver is TagConsumer<*>) {
        @Suppress("UNCHECKED_CAST")
        return getRenderer().renderTabLayout(this, receiver as TagConsumer<T>)
    }
    return receiver
}
```

**After (Fixed):**
```kotlin
override fun <T> compose(receiver: T): T {
    if (receiver is TagConsumer<*>) {
        getRenderer().renderTabLayout(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected ?: {},
            modifier = modifier
        )
    }
    return receiver
}
```

Key changes:
1. Updated the `compose` method to pass all required parameters to `renderTabLayout`
2. Added a null-safe callback for `onTabSelected` using the Elvis operator (`?:`)
3. Removed the return in the `if` block to fix return type mismatch
4. Removed the `@Suppress("UNCHECKED_CAST")` annotation

### 4. LocalDate Implementation

The `LocalDate` class has been replaced with extension functions for `kotlinx.datetime.LocalDate`:

**Before (Problematic):**
```kotlin
// Custom LocalDate implementation
data class LocalDate(val year: Int, val month: Int, val day: Int) {
    // Methods with direct property access
    fun format(pattern: String): String {
        var result = pattern
        result = result.replace("yyyy", year.toString().padStart(4, '0'))
        result = result.replace("MM", month.toString().padStart(2, '0'))
        // ...
    }
    
    fun isBefore(other: LocalDate): Boolean {
        if (year < other.year) return true
        if (year > other.year) return false
        // ... direct property comparisons
    }
    // ...
}
```

**After (Fixed):**
```kotlin
// Extension functions for kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDate

// Extension function
fun LocalDate.format(pattern: String): String {
    var result = pattern
    result = result.replace("yyyy", year.toString().padStart(4, '0'))
    result = result.replace("MM", monthNumber.toString().padStart(2, '0'))
    // ...using kotlinx property names
}

// Simplified comparison using built-in compareTo
fun LocalDate.isBefore(other: LocalDate): Boolean {
    return compareTo(other) < 0
}
```

Key changes:
1. Removed custom `LocalDate` class to resolve conflicts with `kotlinx.datetime.LocalDate`
2. Converted instance methods to extension functions
3. Updated property access to match the kotlinx implementation (e.g., `month` → `monthNumber`, `day` → `dayOfMonth`)
4. Simplified comparison logic using the built-in `compareTo` method
5. Added a `parseLocalDate` helper function to match the previous API

This change aligns the codebase with the Kotlin Multiplatform ecosystem by:
1. Using standard libraries instead of custom implementations
2. Maintaining API compatibility through extension functions
3. Eliminating type conflicts between our code and external libraries

These fixes maintain backward compatibility while aligning with the ongoing migration to the new annotation-based composition system. They follow the pattern described in the "Renderer Method Parameter Mismatches" section of this guide.

### 5. RouteParams Class Fix

The `RouteParams` class had a recursive definition that was causing type mismatch errors:

**Before (Problematic):**
```kotlin
// In Router.kt
data class RouteParams(val params: RouteParams) {
    fun get(key: String): String? = params[key]
    fun asMap(): Map<String, String> = params
}
```

**After (Fixed):**
```kotlin
// In Router.kt
data class RouteParams(val params: Map<String, String>) {
    fun get(key: String): String? = params[key]
    fun asMap(): Map<String, String> = params
}
```

This fix:
1. Removes the recursive type definition that was causing confusion
2. Makes the class properly accept a `Map<String, String>` as its parameter
3. Maintains the same API for accessing parameters
4. Resolves type mismatch errors in the `Route.extractParams()` method

The fix aligns with the ongoing migration to improve type safety and clarity throughout the codebase.

### 6. CanonicalLinks Component Migration

The `CanonicalLinks` component has been migrated from the interface-based approach to the annotation-based approach:

**Before (Interface-based):**
```kotlin
class CanonicalLinks(
    private val url: String,
    private val alternateLanguages: Map<String, String> = emptyMap(),
    private val ampUrl: String? = null
) : Composable {
    @Suppress("UNCHECKED_CAST")
    override fun <T> compose(receiver: T): T {
        if (receiver is HEAD) {
            receiver.apply {
                // Canonical URL - the definitive/preferred version of the page
                link {
                    attributes["rel"] = "canonical"
                    attributes["href"] = url
                }
                // ... more code for alternate languages and AMP
            }
        }
        return receiver
    }
    
    companion object {
        fun simple(url: String): CanonicalLinks { ... }
        fun withLanguages(...): CanonicalLinks { ... }
        fun withAmp(...): CanonicalLinks { ... }
    }
}
```

**After (Annotation-based):**
```kotlin
@Composable
fun CanonicalLinks(
    url: String,
    alternateLanguages: Map<String, String> = emptyMap(),
    ampUrl: String? = null
) {
    val composer = CompositionLocal.currentComposer
    
    SideEffect {
        // Platform-specific head manipulation
        println("CanonicalLinks SideEffect: Setting canonical URL to $url")
        // ... more code for alternate languages and AMP
    }
}

@Composable
fun SimpleCanonicalLink(url: String) {
    CanonicalLinks(url)
}

@Composable
fun CanonicalLinksWithLanguages(...) { ... }

@Composable
fun CanonicalLinksWithAmp(...) { ... }
```

Key changes:
1. Converted the class to a composable function with the `@Composable` annotation
2. Replaced the `compose` method with a `SideEffect` for head manipulation
3. Converted companion object factory methods to separate composable functions
4. Used `CompositionLocal.currentComposer` for composition management
5. Simplified the API by making parameters public and removing the need for getters

This migration demonstrates how to convert head-manipulating components to use the new annotation-based approach, which is particularly useful for SEO-related components that need to modify the document head.

### 7. OpenGraphTags Component Migration

The `OpenGraphTags` component has been migrated from the interface-based approach to the annotation-based approach:

**Before (Interface-based):**
```kotlin
class OpenGraphTags(
    private val title: String,
    private val type: String = "website",
    private val url: String,
    private val image: String? = null,
    private val description: String? = null,
    private val siteName: String? = null,
    private val locale: String? = null,
    private val extraTags: Map<String, String> = emptyMap()
) : Composable {
    @Suppress("UNCHECKED_CAST")
    override fun <T> compose(receiver: T): T {
        if (receiver is HEAD) {
            receiver.apply {
                // Basic OG tags
                meta {
                    attributes["property"] = "og:title"
                    attributes["content"] = title
                }
                // ... more code for other OG tags
            }
        }
        return receiver
    }
    
    companion object {
        fun article(...): OpenGraphTags { ... }
        fun product(...): OpenGraphTags { ... }
    }
}
```

**After (Annotation-based):**
```kotlin
@Composable
fun OpenGraphTags(
    title: String,
    type: String = "website",
    url: String,
    image: String? = null,
    description: String? = null,
    siteName: String? = null,
    locale: String? = null,
    extraTags: Map<String, String> = emptyMap()
) {
    val composer = CompositionLocal.currentComposer
    
    SideEffect {
        // Platform-specific head manipulation
        println("OpenGraphTags SideEffect: Setting og:title='$title'")
        // ... more code for other OG tags
    }
}

@Composable
fun OpenGraphArticle(...) { ... }

@Composable
fun OpenGraphProduct(...) { ... }
```

Key changes:
1. Converted the class to a composable function with the `@Composable` annotation
2. Replaced the `compose` method with a `SideEffect` for head manipulation
3. Converted companion object factory methods to separate composable functions with more descriptive names
4. Used `CompositionLocal.currentComposer` for composition management
5. Simplified the API by making parameters public and removing the need for getters

This migration follows the same pattern as the `CanonicalLinks` component, demonstrating how to convert head-manipulating components to use the new annotation-based approach.

### SitemapGeneration Component Migration

The `SitemapGeneration` component has been migrated from an interface-based approach to the new annotation-based approach. This component provides utilities for creating XML sitemaps that help search engines discover and understand the structure of your site.

**Before (Interface-based):**
```kotlin
class SitemapXml(
    private val urls: List<SitemapUrl>,
    private val baseUrl: String
) : Composable {

    @Suppress("UNCHECKED_CAST")
    override fun <T> compose(receiver: T): T {
        if (receiver is FlowContent) {
            val xml = buildSitemapXml()
            receiver.pre {
                +xml
            }
        } else if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>
            val xml = buildSitemapXml()
            consumer.onTagContent(xml)
            return receiver
        }

        return receiver
    }

    fun getXmlString(): String {
        return buildSitemapXml()
    }

    private fun buildSitemapXml(): String {
        // XML building logic
    }
}
```

**After (Annotation-based):**
```kotlin
@Composable
fun SitemapXml(
    urls: List<SitemapUrl>,
    baseUrl: String
) {
    val renderer = getPlatformRenderer()
    val xml = buildSitemapXml(urls, baseUrl)
    
    renderer.renderPre {
        +xml
    }
}

fun getXmlString(urls: List<SitemapUrl>, baseUrl: String): String {
    return buildSitemapXml(urls, baseUrl)
}

private fun buildSitemapXml(urls: List<SitemapUrl>, baseUrl: String): String {
    // XML building logic
}
```

Key changes in the migration:
1. Converted the `SitemapXml` class to a composable function with the `@Composable` annotation
2. Removed the `compose` method and replaced it with direct rendering using `getPlatformRenderer()`
3. Made parameters public and passed them directly to the functions that need them
4. Simplified the API by removing the need for getters and private fields
5. Updated the companion object's `fromRoutes` method to return a list of `SitemapUrl` objects instead of a `SitemapXml` instance

This migration follows the same pattern as the `CanonicalLinks` and `OpenGraphTags` components, demonstrating how to convert components that manipulate HTML content to use the new annotation-based approach.

### StructuredData Component Migration

The `StructuredData` component has been migrated from an interface-based approach to an annotation-based approach. This component is used to embed JSON-LD structured data in the document head, helping search engines understand the content of the page.

**Before (Interface-based)**:
```kotlin
class JsonLdStructuredData(private val jsonLdString: String) : Composable {
    override fun compose() {
        // Head manipulation logic
    }
}

object StructuredData {
    fun WebPage(name: String, description: String, url: String) = buildJsonObject {
        put("@context", "https://schema.org")
        put("@type", "WebPage")
        put("name", name)
        put("description", description)
        put("url", url)
    }
    // ... other factory methods
}
```

**After (Annotation-based):**
```kotlin
@Composable
fun JsonLdStructuredData(jsonLdString: String) {
    val composer = CompositionLocal.currentComposer
    SideEffect {
        // Head manipulation logic
    }
}

@Composable
fun WebPageStructuredData(
    name: String,
    description: String,
    url: String
) {
    val data = JsonObject(mapOf(
        "@context" to JsonPrimitive("https://schema.org"),
        "@type" to JsonPrimitive("WebPage"),
        "name" to JsonPrimitive(name),
        "description" to JsonPrimitive(description),
        "url" to JsonPrimitive(url)
    ))
    StructuredData(data)
}
// ... other composable functions
```

Key changes made:
1. Converted the `JsonLdStructuredData` class to a composable function with the `@Composable` annotation
2. Removed the `compose` method, replaced with `SideEffect` for head manipulation
3. Transformed companion object factory methods into separate composable functions with more descriptive names
4. Simplified JSON construction by using direct `JsonObject` creation instead of builder pattern
5. Made parameters public and removed the need for getters
6. Added proper null handling with `JsonNull` for optional fields

This migration follows the same pattern as the `CanonicalLinks` and `OpenGraphTags` components, demonstrating how to convert head-manipulating components to use the new annotation-based approach.

### TwitterCards Component Migration

The `TwitterCards` component has been migrated from an interface-based approach to an annotation-based approach. This component is used to add Twitter-specific metadata to the document head, helping display rich previews when users share content on Twitter.

**Before (Interface-based)**:
```kotlin
class TwitterCards(
    private val card: CardType = CardType.SUMMARY,
    private val title: String? = null,
    private val description: String? = null,
    private val image: String? = null,
    private val imageAlt: String? = null,
    private val site: String? = null,
    private val creator: String? = null,
    private val extraTags: Map<String, String> = emptyMap()
) : Composable {
    enum class CardType(val value: String) {
        SUMMARY("summary"),
        SUMMARY_LARGE_IMAGE("summary_large_image"),
        APP("app"),
        PLAYER("player")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> compose(receiver: T): T {
        if (receiver is HEAD) {
            receiver.apply {
                // Card type
                meta {
                    attributes["name"] = "twitter:card"
                    attributes["content"] = card.value
                }
                // ... more code for other Twitter tags
            }
        }
        return receiver
    }
    
    companion object {
        fun article(...): TwitterCards { ... }
        fun product(...): TwitterCards { ... }
    }
}
```

**After (Annotation-based)**:
```kotlin
enum class TwitterCardType(val value: String) {
    SUMMARY("summary"),
    SUMMARY_LARGE_IMAGE("summary_large_image"),
    APP("app"),
    PLAYER("player")
}

@Composable
fun TwitterCards(
    card: TwitterCardType = TwitterCardType.SUMMARY,
    title: String? = null,
    description: String? = null,
    image: String? = null,
    imageAlt: String? = null,
    site: String? = null,
    creator: String? = null,
    extraTags: Map<String, String> = emptyMap()
) {
    val composer = CompositionLocal.currentComposer
    
    SideEffect {
        // Platform-specific head manipulation
        println("TwitterCards SideEffect: Setting card='${card.value}'")
        // ... more code for other Twitter tags
    }
}

@Composable
fun TwitterArticleCard(...) { ... }

@Composable
fun TwitterProductCard(...) { ... }
```

Key changes made:
1. Converted the `TwitterCards` class to a composable function with the `@Composable` annotation
2. Moved the `CardType` enum outside the class and renamed it to `TwitterCardType` for clarity
3. Removed the `compose` method, replaced with `SideEffect` for head manipulation
4. Transformed companion object factory methods into separate composable functions with more descriptive names
5. Used `CompositionLocal.currentComposer` for composition management
6. Simplified the API by making parameters public and removing the need for getters
7. Updated the head manipulation code to use `getPlatformRenderer()` instead of the kotlinx.html DSL

This migration follows the same pattern as the other SEO components, demonstrating how to convert head-manipulating components to use the new annotation-based approach.

### StaticRendering Component Migration

The `StaticRendering` component has been migrated to support the new annotation-based approach. This component is responsible for generating static HTML from Summon components, which is useful for static site generation (SSG) and pre-rendering.

**Before (Interface-based)**:
```kotlin
class StaticRenderer(
    private val platformRenderer: PlatformRenderer = PlatformRendererProvider.getPlatformRenderer()
) : ServerSideRenderer {
    override fun render(composable: Composable, context: RenderContext): String {
        val html = renderToString(composable)
        return wrapWithHtml(html, context)
    }

    private fun renderToString(composable: Composable): String {
        return createHTML().let { consumer ->
            platformRenderer.renderComposable(composable, consumer)
            consumer.finalize()
        }
    }
    // ... other methods
}

object StaticRendering {
    private val renderer = StaticRenderer()

    fun render(composable: Composable, context: RenderContext = RenderContext()): String {
        return renderer.render(composable, context)
    }

    fun generateStaticSite(
        pages: Map<String, Composable>,
        contextProvider: (String) -> RenderContext = { RenderContext() }
    ): Map<String, String> {
        return pages.mapValues { (path, composable) ->
            render(composable, contextProvider(path))
        }
    }
}
```

**After (Annotation-based)**:
```kotlin
class StaticRenderer(
    private val platformRenderer: MigratedPlatformRenderer = getPlatformRenderer()
) : ServerSideRenderer {
    override fun render(composable: @Composable () -> Unit, context: RenderContext): String {
        val html = renderToString(composable)
        return wrapWithHtml(html, context)
    }

    private fun renderToString(composable: @Composable () -> Unit): String {
        return createHTML().let { consumer ->
            platformRenderer.renderComposable(composable, consumer)
            consumer.finalize()
        }
    }
    // ... other methods
}

object StaticRendering {
    private val renderer = StaticRenderer()

    fun render(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): String {
        return renderer.render(composable, context)
    }

    fun generateStaticSite(
        pages: Map<String, @Composable () -> Unit>,
        contextProvider: (String) -> RenderContext = { RenderContext() }
    ): Map<String, String> {
        return pages.mapValues { (path, composable) ->
            render(composable, contextProvider(path))
        }
    }
}
```

Key changes made:
1. Updated the `StaticRenderer` class to use `MigratedPlatformRenderer` instead of `PlatformRenderer`
2. Changed the renderer access from `PlatformRendererProvider.getPlatformRenderer()` to `getPlatformRenderer()`
3. Updated method signatures to use `@Composable () -> Unit` instead of the `Composable` interface
4. Modified the `generateStaticSite` method to accept a map of composable functions instead of composable objects
5. Ensured proper implementation of the `ServerSideRenderer` interface with the correct method signature

This migration aligns the `StaticRendering` component with the new annotation-based approach, making it compatible with the rest of the migrated components. It also demonstrates how to update server-side rendering utilities to work with the new composition system.

### SEOPrerender Component Migration

The `SEOPrerender` component has been migrated to support the new annotation-based approach. This component is responsible for pre-rendering pages for search engine crawlers, ensuring that search engines can properly index dynamic content.

**Before (Interface-based)**:
```kotlin
class SEOPrerenderer(
    private val renderer: StaticRenderer = StaticRenderer(),
    private val userAgentPatterns: List<String> = defaultSearchEngineUserAgents
) {
    fun prerender(composable: Composable, context: RenderContext = RenderContext()): String {
        // Create a SEO-optimized context
        val seoContext = RenderContext(
            enableHydration = false,  // No need for hydration for crawlers
            hydrationIdPrefix = context.hydrationIdPrefix,
            metadata = context.metadata,
            debug = false,            // No debug info for crawlers
            seoMetadata = enrichSeoMetadata(context.seoMetadata),
            initialState = context.initialState
        )

        // Render the composable
        return renderer.render(composable, seoContext)
    }
    // ... other methods
}

object SEOPrerender {
    private val prerenderer = SEOPrerenderer()

    fun prerender(composable: Composable, context: RenderContext = RenderContext()): String {
        return prerenderer.prerender(composable, context)
    }

    suspend fun prerenderSite(
        pages: Map<String, Composable>,
        contextProvider: (String) -> RenderContext = { RenderContext() }
    ): Map<String, String> = coroutineScope {
        // Pre-render each page in parallel
        val prerenders = pages.map { (path, composable) ->
            path to async { prerender(composable, contextProvider(path)) }
        }

        // Collect the results
        prerenders.associate { (path, deferred) ->
            path to deferred.await()
        }
    }
    // ... other methods
}
```

**After (Annotation-based)**:
```kotlin
class SEOPrerenderer(
    private val renderer: StaticRenderer = StaticRenderer(),
    private val userAgentPatterns: List<String> = defaultSearchEngineUserAgents
) {
    fun prerender(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): String {
        // Create a SEO-optimized context
        val seoContext = RenderContext(
            enableHydration = false,  // No need for hydration for crawlers
            hydrationIdPrefix = context.hydrationIdPrefix,
            metadata = context.metadata,
            debug = false,            // No debug info for crawlers
            seoMetadata = enrichSeoMetadata(context.seoMetadata),
            initialState = context.initialState
        )

        // Render the composable
        return renderer.render(composable, seoContext)
    }
    // ... other methods
}

object SEOPrerender {
    private val prerenderer = SEOPrerenderer()

    fun prerender(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): String {
        return prerenderer.prerender(composable, context)
    }

    suspend fun prerenderSite(
        pages: Map<String, @Composable () -> Unit>,
        contextProvider: (String) -> RenderContext = { RenderContext() }
    ): Map<String, String> = coroutineScope {
        // Pre-render each page in parallel
        val prerenders = pages.map { (path, composable) ->
            path to async { prerender(composable, contextProvider(path)) }
        }

        // Collect the results
        prerenders.associate { (path, deferred) ->
            path to deferred.await()
        }
    }
    // ... other methods
}
```

Key changes made:
1. Updated the import from `code.yousef.summon.runtime.Composable` to `code.yousef.summon.annotation.Composable`
2. Changed method signatures to use `@Composable () -> Unit` instead of the `Composable` interface
3. Updated the `prerenderSite` method to accept a map of composable functions instead of composable objects
4. Maintained the same functionality while ensuring compatibility with the new annotation-based approach

This migration aligns the `SEOPrerender` component with the new annotation-based approach, making it compatible with the rest of the migrated components. It also demonstrates how to update server-side rendering utilities to work with the new composition system.

### StreamingSSR Component Migration

The `StreamingSSR` component has been migrated to support the new annotation-based approach. This component provides streaming server-side rendering capabilities, allowing large pages to be rendered as a stream of HTML chunks for better performance and user experience.

**Before (Interface-based)**:
```kotlin
class StreamingRenderer(
    private val hydrationSupport: HydrationSupport = StandardHydrationSupport(),
    private val platformRenderer: PlatformRenderer = getPlatformRenderer(),
    private val chunkSize: Int = 4096
) : StreamingServerSideRenderer {
    override fun renderStream(composable: Composable, context: RenderContext): Flow<String> = flow {
        // Render implementation
    }

    private fun renderToString(composable: Composable): String {
        return createHTML().let { consumer ->
            platformRenderer.renderComposable(composable, consumer)
            consumer.finalize()
        }
    }
    // ... other methods
}

object StreamingSSR {
    private val renderer = StreamingRenderer()

    fun renderStream(composable: Composable, context: RenderContext = RenderContext()): Flow<String> {
        return renderer.renderStream(composable, context)
    }

    fun createRenderer(
        hydrationSupport: HydrationSupport = StandardHydrationSupport(),
        chunkSize: Int = 4096
    ): StreamingRenderer {
        return StreamingRenderer(hydrationSupport, chunkSize)
    }
}
```

**After (Annotation-based)**:
```kotlin
class StreamingRenderer(
    private val hydrationSupport: HydrationSupport = StandardHydrationSupport(),
    private val platformRenderer: MigratedPlatformRenderer = getPlatformRenderer(),
    private val chunkSize: Int = 4096
) : StreamingServerSideRenderer {
    override fun renderStream(composable: @Composable () -> Unit, context: RenderContext): Flow<String> = flow {
        // Render implementation
    }

    private fun renderToString(composable: @Composable () -> Unit): String {
        return createHTML().let { consumer ->
            platformRenderer.renderComposable(composable, consumer)
            consumer.finalize()
        }
    }
    // ... other methods
}

object StreamingSSR {
    private val renderer = StreamingRenderer()

    fun renderStream(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): Flow<String> {
        return renderer.renderStream(composable, context)
    }

    fun createRenderer(
        hydrationSupport: HydrationSupport = StandardHydrationSupport(),
        platformRenderer: MigratedPlatformRenderer = getPlatformRenderer(),
        chunkSize: Int = 4096
    ): StreamingRenderer {
        return StreamingRenderer(hydrationSupport, platformRenderer, chunkSize)
    }
}
```

Key changes made:
1. Updated the import from `code.yousef.summon.runtime.Composable` to `code.yousef.summon.annotation.Composable`
2. Changed the `platformRenderer` type from `PlatformRenderer` to `MigratedPlatformRenderer`
3. Updated method signatures to use `@Composable () -> Unit` instead of the `Composable` interface
4. Added `platformRenderer` parameter to `createRenderer` method to ensure proper initialization
5. Fixed the parameter order in `StreamingRenderer` constructor calls
6. Properly implemented the abstract member from `StreamingServerSideRenderer`

This migration aligns the `StreamingSSR` component with the new annotation-based approach, making it compatible with the rest of the migrated components. It also demonstrates how to update server-side rendering utilities to work with the new composition system, particularly in the context of streaming content generation.

### Next Component Migration: BasicButton

For the next migration, we will focus on the BasicButton component, which currently uses the interface-based approach but would benefit from the new annotation-based composition system, especially for handling pressed/hover states and accessibility.
