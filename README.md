<p align="center">
  <img src="https://raw.githubusercontent.com/codeyousef/summon/main/assets/logo.png" alt="Summon logo" width="200" />
  <br>
  <a href="https://x.com/DeepIssueMassaj">x.com/@DeepIssueMassaj</a>
</p>

# Summon

> 📦 **IMPORTANT: Group ID Migration** (November 2025)
>
> Summon is migrating from `io.github.codeyousef` to `codes.yousef`!
> - Version **0.5.0.0** will be the LAST release under `io.github.codeyousef`
> - Please switch your dependencies to `codes.yousef` as soon as possible
>
> **Action Required:**
> Update your dependencies from:
> ```kotlin
> implementation("io.github.codeyousef:summon:0.6.2.0")  // Old - deprecated
> ```
> To:
> ```kotlin
> implementation("codes.yousef:summon:0.6.2.0")  // New - use this!
> ```
>
> See [Migration Guide](#group-id-migration) below for details.

> ⚠️ **Alpha Status**: Summon is currently in alpha development and actively seeking testers and feedback. While core functionality is stable, APIs may change between releases. We welcome early adopters and contributors! Please report issues and share your experience.

**Summon** is a powerful, type-safe frontend framework for Kotlin Multiplatform that brings the elegance of Jetpack Compose to both browser and JVM environments. Build beautiful, responsive applications with a declarative syntax that feels natural to Kotlin developers.

## Project Structure

This monorepo contains:

- **`summon-core/`** - The main Summon library
- **`summon-cli/`** - Command-line tool for project generation 
- **`diagnostics/`** - Stress tests, leak detectors, and JMH benchmarks
- **`docs/`** - Documentation and guides

> 📝 **Examples**: Example projects showing various integrations have been moved to a separate repository for cleaner core library maintenance.

> 🎨 **Type-safe styling** with an intuitive modifier API inspired by Compose.
> 
> 🧩 **Component-based architecture** for maximum reusability and maintainability.
> 
> 🔄 **Reactive state management** that automatically updates your UI when data changes.

Summon combines the best ideas from modern frontend frameworks like React, Vue, and Next.js with the declarative UI patterns of Jetpack Compose and SwiftUI, while leveraging Kotlin's powerful type system to catch errors at compile time rather than runtime. Whether you're building a simple website or a complex web application, Summon provides the tools you need to create polished, professional user interfaces with less code and fewer bugs.

## Features

- **Cross-Platform**: Build once, run on JavaScript, WebAssembly (WASM), and JVM platforms
- **Component-Based**: Create reusable UI components with a declarative syntax
- **Type-Safe**: Leverage Kotlin's type system for safer UI development with compile-time checks
- **Enhanced Styling System**: 
  - Flexible and powerful styling using a comprehensive modifier API
  - Type-safe CSS properties with enum support for values like BorderStyle, Alignment, etc.
  - Numeric extensions for CSS units (px, rem, em, etc.) for cleaner code
  - Comprehensive gradient support with both linear and radial options
  - Extensive color system with Material Design and Catppuccin palettes
- **Flexible Layout System**:
  - Powerful flexbox layout with alignment controls
  - Row and Column components with intuitive alignment modifiers
  - Grid layout support with comprehensive controls
- **State Management**: Simple yet powerful reactive state management solutions
- **Next.js-Style Routing**: File-based routing with automatic page discovery and code generation
- **Lifecycle Aware**: Built-in lifecycle management with side effects and cleanup
- **Framework Interoperability**: Integrate with existing frameworks like Quarkus, Ktor, and Spring Boot
- **Security**: Comprehensive JWT authentication and role-based access control (RBAC)
- **Accessibility**: Built-in accessibility features with ARIA support for inclusive applications
- **Animation and Transitions**: 
  - Smooth animations with keyframes support
  - CSS transitions with type-safe timing functions and properties
  - Transform operations for scaling, rotation, and translation
- **Server-Side Rendering (SSR)**:
  - Complete SSR implementation with composition context management
  - State management during server rendering (`remember`, `mutableStateOf`)
  - SEO optimization with meta tags, OpenGraph, and Twitter Cards
  - Client-side hydration for interactive components
  - Production-ready performance handling 100+ components efficiently
- **Enhanced Theme System**: 
  - Flexible theming with dark mode support
  - Type-safe theme properties for typography, spacing, colors, and more
  - Easy theme switching at runtime
- **Internationalization**: Full i18n support with RTL layouts for languages like Arabic and Hebrew
- **WebAssembly (WASM) Support**:
    - Native-like performance with near-zero startup time
    - Server-side rendering with seamless client-side hydration
    - Progressive enhancement with automatic JS fallback
    - Cross-browser compatibility and progressive loading
    - Production-ready WASM with optimized bundle sizes


## Component Categories

Summon provides a comprehensive set of UI components organized into logical categories:

### Input Components
- **TextField** - Single-line text input
- **TextArea** - Multi-line text input
- **Button** - Clickable button with various styles
- **Checkbox** - Checkbox input with label
- **RadioButton** - Radio button for single selection
- **Switch** - Toggle switch component
- **Select** - Dropdown selection component
- **Slider** - Single value slider
- **RangeSlider** - Dual-handle range slider
- **DatePicker** - Date selection component
- **TimePicker** - Time selection component
- **FileUpload** - File upload with drag & drop support
- **Form** & **FormField** - Form management components

### Layout Components
- **Row** - Horizontal layout with alignment controls
- **Column** - Vertical layout with alignment controls
- **Box** - Container with positioning capabilities
- **Grid** - CSS Grid layout component
- **Spacer** - Flexible spacing component
- **Divider** - Visual separator
- **Card** - Elevated container with shadow
- **AspectRatio** - Maintains aspect ratio for content
- **ExpansionPanel** - Collapsible/expandable panel
- **LazyColumn** & **LazyRow** - Virtualized lists for performance
- **ResponsiveLayout** - Responsive layout utilities

### Display Components
- **Text** - Text display with styling
- **Image** - Image display component
- **Icon** - Icon component with various sources
- **Badge** - Small status indicator

### Feedback Components
- **Alert** - Dismissible alert messages
- **Modal** - Dialog system with variants (DEFAULT, ALERT, CONFIRMATION, FULLSCREEN) and sizes
- **Loading** & **LoadingOverlay** - Loading indicators with multiple animation types (SPINNER, DOTS, LINEAR, CIRCULAR)
- **Toast** & **ToastManager** - Notification system with positioning and action support
- **Snackbar** & **SnackbarHost** - Temporary notifications
- **Progress** & **ProgressBar** - Progress indicators
- **Tooltip** - Hover tooltips

### Navigation Components
- **Link** - Navigation links
- **TabLayout** - Tab-based navigation

### Utility Components
- **Div** - Basic container element
- **AccessibleElement** - Accessibility wrapper

### Network and Communication
- **WebSocket** - Cross-platform WebSocket with auto-reconnection and lifecycle management
- **HttpClient** - Comprehensive HTTP client with JSON and form data support
- **Storage** - Local, session, and memory storage abstraction with TypedStorage wrapper

## Inspiration

Summon draws inspiration from several excellent projects:

- **[Kobweb](https://github.com/varabyte/kobweb)** - Modern full-stack web framework built on Compose HTML. Kobweb's elegant API design shaped Summon's philosophy.
- **[Spring Petclinic WASM](https://github.com/sdeleuze/spring-petclinic)** (Sébastien Deleuze) - Pioneered SSR + WASM hydration patterns, proving WASM maintains SEO compatibility.
- **[Kilua](https://github.com/rjaros/kilua)** (Robert Jaros) - First production-ready Kotlin/WASM framework with comprehensive SSR capabilities.
- **[kotlinx-browser](https://github.com/Kotlin/kotlinx-browser)** & **[kotlinx.html](https://github.com/Kotlin/kotlinx.html)** - Official Kotlin libraries providing WASM browser APIs and type-safe HTML generation.

Summon stands on the shoulders of these giants and the broader Kotlin/WASM community.

## Documentation

For detailed documentation, please check the [docs](docs/README.md) directory:

- [Components](docs/components.md) - Learn about Summon's built-in UI components
- [Routing](docs/routing.md) - Set up navigation in your application with Next.js-style file-based routing
- [File-Based Routing](docs/file-based-routing.md) - Next.js-style file-based routing system
- [State Management](docs/state-management.md) - Manage application state effectively
- [Styling](docs/styling.md) - Apply styles to your components
- [Integration Guides](docs/integration-guides.md) - Integrate with existing frameworks
- [Security](docs/security.md) - Authentication and authorization features
- [Accessibility and SEO](docs/accessibility-and-seo.md) - Build accessible and SEO-friendly applications
- [Internationalization](docs/i18n.md) - Add multi-language support with RTL layouts

### API Reference

Comprehensive API reference documentation is available in the [docs/api-reference](docs/api-reference) directory:

- [Core API](docs/api-reference/core.md) - Core interfaces and classes
- [Components API](docs/api-reference/components.md) - Built-in UI components
- [Modifier API](docs/api-reference/modifier.md) - Styling and layout modifiers
- [State API](docs/api-reference/state.md) - State management utilities
- [Routing API](docs/api-reference/routing.md) - Navigation and routing
- [Effects API](docs/api-reference/effects.md) - Side effects and lifecycle management
- [Events API](docs/api-reference/events.md) - Event handling and listeners
- [Animation API](docs/api-reference/animation.md) - Animations, transitions, and keyframes
- [Theme API](docs/api-reference/theme.md) - Theming and styling system
- [Color API](docs/api-reference/color.md) - Color system with Material Design and Catppuccin palettes
- [Focus Management API](docs/api-reference/focus.md) - Focus management and keyboard navigation
- [Validation API](docs/api-reference/validation.md) - Form validation and input validation
- [Security API](docs/api-reference/security.md) - Security features and access control
- [Authentication API](docs/api-reference/auth.md) - Authentication providers and JWT integration
- [Accessibility API](docs/api-reference/accessibility.md) - Accessibility features and ARIA support
- [SEO API](docs/api-reference/seo.md) - SEO features and meta tags
- [Internationalization API](docs/api-reference/i18n-api.md) - Multi-language and RTL layout support

## Installation

### Summon CLI Tool

The Summon CLI helps you quickly scaffold new projects and generate components.

#### Option 1: Download JAR (Simplest)

Download the latest JAR from [GitHub Releases](https://github.com/codeyousef/summon/releases):

```bash
# Download summon-cli-0.6.2.0.jar

# Run commands directly
java -jar summon-cli-0.6.2.0.jar init my-app
java -jar summon-cli-0.6.2.0.jar --help
```

#### Option 2: Build from Source

```bash
git clone https://github.com/codeyousef/summon.git
cd summon
./gradlew :summon-cli:shadowJar
java -jar summon-cli/build/libs/summon-cli-0.6.2.0.jar init my-app
```

#### Quick Start

```bash
# Let Summon CLI prompt for stack + backend
java -jar summon-cli-0.6.2.0.jar init portal

# Or skip the prompts entirely
java -jar summon-cli-0.6.2.0.jar init landing --mode=standalone --here
java -jar summon-cli-0.6.2.0.jar init portal --mode=fullstack --backend=ktor
```

# After generation (examples)
cd portal
./gradlew build
./gradlew run          # Ktor full-stack projects

cd ../portal-spring
./gradlew build
./gradlew bootRun      # Spring Boot full-stack projects

cd ../portal-quarkus
./gradlew build
./gradlew unitTest     # Lightweight backend checks
./gradlew quarkusDev   # Hot reload backend + Summon UI

cd ../landing
./gradlew jsBrowserDevelopmentRun  # Standalone browser project
```

### Summon Library

Add Summon to your project dependencies from Maven Central:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    // ⚠️ NEW GROUP ID - Use codes.yousef (not io.github.codeyousef)

    // For JVM projects (Ktor, Spring Boot, Quarkus)
    implementation("codes.yousef:summon-jvm:0.6.2.0")

    // For JavaScript/Browser projects
    implementation("codes.yousef:summon-js:0.6.2.0")

    // For WebAssembly projects
    implementation("codes.yousef:summon-wasm-js:0.6.2.0")

    // For Kotlin Multiplatform projects (includes all targets)
    implementation("codes.yousef:summon:0.6.2.0")
}
```

> 📦 **Migration Note**: If you're upgrading from an older version, change `io.github.codeyousef` to `codes.yousef` in
> your dependencies. Both group IDs will be published until version 0.5.0.0 for compatibility.

**Note**: No authentication required - Summon is available directly from Maven Central!

## Group ID Migration

### Why We're Migrating

We're transitioning from `io.github.codeyousef` to `codes.yousef` to:

- Use our official domain (`codes.yousef`)
- Simplify the group ID structure
- Align with Maven Central best practices

### Migration Timeline

| Version     | io.github.codeyousef | codes.yousef | Notes                         |
|-------------|----------------------|--------------|-------------------------------|
| 0.4.8.7     | ✅ Published          | ✅ Published  | Both available                |
| 0.4.9.0     | ✅ Published          | ✅ Published  | Both available                |
| 0.5.8.3     | ✅ Published          | ✅ Published  | Both available                |
| **0.5.0.0** | ✅ **FINAL**          | ✅ Published  | **Last version on old group** |
| 0.6.2.0+      | ❌ Not published      | ✅ Published  | **New group only**            |

### How to Migrate

#### Step 1: Update Your Dependencies

In your `build.gradle.kts`, change:

```kotlin
// ❌ OLD - Don't use
dependencies {
    implementation("io.github.codeyousef:summon:0.4.7.0")
}
```

To:

```kotlin
// ✅ NEW - Use this
dependencies {
    implementation("codes.yousef:summon:0.6.2.0")
}
```

#### Step 2: Update Imports (If Using Direct Imports)

If you have any hardcoded imports (unlikely), update:

```kotlin
// ❌ OLD
import io.github.codeyousef.summon.*

// ✅ NEW
import codes.yousef.summon.*
```

**Note**: For most users, you don't need to change imports - they're handled automatically by your dependencies.

#### Step 3: Verify

Run `./gradlew build` to ensure everything compiles correctly.

### Backward Compatibility

- **Versions 0.4.8.7 through 0.5.0.0**: Published to BOTH group IDs
- **Version 0.6.2.0 onwards**: Published ONLY to `codes.yousef`
- **No code changes required**: Just update the dependency declaration

### Need Help?

If you encounter issues during migration:

1. Check that you've updated ALL Summon dependencies
2. Clean your build: `./gradlew clean`
3. Refresh dependencies: `./gradlew --refresh-dependencies`
4. File an issue on GitHub if problems persist

## Version Management

Summon uses a centralized version management approach to ensure consistency across the main project and example projects. The version information is defined in a single place and referenced from all other places.

For more information, see [VERSIONING.md](VERSIONING.md).

## WebAssembly (WASM) Support

Summon 0.4.0.0 introduces comprehensive WebAssembly support, bringing near-native performance to web applications while
maintaining full compatibility with server-side rendering and JavaScript fallbacks.

### Getting Started with WASM

Create a new project with the Summon CLI, then enable the WASM target using the steps that follow:

```bash
# Download the CLI JAR from releases first, then run:
java -jar summon-cli-0.6.2.0.jar init my-wasm-app --mode=standalone
```

### Basic WASM Application

```kotlin
// src/wasmJsMain/kotlin/Main.kt
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.wasmMain
import codes.yousef.summon.state.mutableStateOf
import codes.yousef.summon.runtime.remember

@Composable
fun App() {
    val counter = remember { mutableStateOf(0) }

    Column(modifier = Modifier()) {
        Text("WASM Counter: ${counter.value}", modifier = Modifier())
        Button(
            onClick = { counter.value++ },
            label = "Increment",
            modifier = Modifier()
        )
    }
}

fun main() {
    wasmMain {
        App()
    }
}
```

### WASM Features

#### 🚀 **Performance Benefits**

- **Near-Native Speed**: Execute at 95%+ of native performance
- **Minimal Startup Time**: Sub-100ms initialization
- **Memory Efficiency**: Precise garbage collection and memory management
- **Bundle Optimization**: Tree-shaking and dead code elimination

#### 🌐 **Browser Compatibility**

- **Progressive Enhancement**: Automatic fallback to JavaScript
- **Browser Detection**: Smart feature detection and capability assessment
- **Polyfill Support**: Seamless compatibility across browser versions
- **Mobile Optimization**: Optimized for mobile browsers and PWA

#### 🔄 **SSR Integration**

- **Seamless Hydration**: Server-rendered content enhanced with WASM interactivity
- **SEO Compatibility**: Full search engine crawling with enhanced performance
- **State Synchronization**: Server state automatically synced to WASM client
- **Progressive Loading**: Initial content renders immediately, WASM loads in background

### WASM Build Configuration

```kotlin
// build.gradle.kts
kotlin {
    wasmJs {
        moduleName = "my-wasm-app"
        browser {
            commonWebpackConfig {
                outputFileName = "my-wasm-app.js"
            }
        }
        binaries.executable()
    }
}

dependencies {
  implementation("codes.yousef:summon-wasm-js:0.6.2.0")
}
```

### Production Deployment

```html
<!-- index.html -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My WASM App</title>
</head>
<body>
<div id="root">
    <!-- Server-rendered content for SEO -->
    <div>Loading...</div>
</div>

<!-- Progressive WASM loading with JS fallback -->
<script src="wasm-loader.js"></script>
</body>
</html>
```

### Performance Comparison

| Platform       | Bundle Size | Load Time | Runtime Performance |
|----------------|-------------|-----------|---------------------|
| **WASM**       | ~450KB      | ~80ms     | ~95% native         |
| **JavaScript** | ~380KB      | ~120ms    | ~60% native         |
| **JVM**        | N/A         | N/A       | ~100% native        |

### WASM Development Tools

The Summon CLI provides comprehensive WASM development tools:

```bash
# Build WASM for development
./gradlew wasmJsBrowserDevelopmentRun

# Build optimized WASM for production
./gradlew wasmJsBrowserProductionWebpack

# Run WASM development server with hot reload
./gradlew wasmJsBrowserDevelopmentRun --continuous

# Analyze WASM bundle size and performance
./gradlew wasmJsBrowserDistribution
```

## Server-Side Rendering (SSR)

Summon provides production-ready Server-Side Rendering capabilities for improved SEO, faster initial page loads, and better user experience. The SSR implementation includes full composition context management, state handling, and client-side hydration.

### Basic SSR Usage

```kotlin
import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf

// Create a renderer instance
val renderer = PlatformRenderer()

@Composable
fun MyApp() {
    val counter = remember { mutableStateOf(0) }
    
    Column(modifier = Modifier()) {
        Text("Server-Rendered Counter: ${counter.value}", modifier = Modifier())
        Text("This content is generated on the server!", modifier = Modifier())
    }
}

// Render to HTML string
val html = renderer.renderComposableRoot {
    MyApp()
}

// The generated HTML includes proper document structure:
// <!DOCTYPE html>
// <html>
//   <head>...</head>
//   <body>
//     <div>Server-Rendered Counter: 0</div>
//     <div>This content is generated on the server!</div>
//   </body>
// </html>
```

### Advanced SSR with SEO and Hydration

```kotlin
import code.yousef.summon.ssr.*

// High-level utility for complete page rendering
val html = ServerSideRenderUtils.renderPageToString(
    rootComposable = { MyApp() },
    initialData = mapOf("userId" to "123", "theme" to "dark"),
    includeHydrationScript = true
)

// With custom SEO metadata
val context = RenderContext(
    enableHydration = true,
    seoMetadata = SeoMetadata(
        title = "My Awesome App",
        description = "A server-rendered Kotlin app built with Summon",
        keywords = listOf("kotlin", "ssr", "web", "multiplatform"),
        openGraph = OpenGraphMetadata(
            title = "My Awesome App",
            description = "Server-side rendered with Summon",
            type = "website",
            url = "https://myapp.com",
            image = "https://myapp.com/og-image.jpg"
        )
    )
)

val seoOptimizedHtml = renderer.renderComposableRootWithHydration {
    MyApp()
}
```

### SSR Benefits

- **SEO Optimization**: Search engines can crawl your content immediately
- **Faster Initial Load**: Users see content before JavaScript loads
- **Better Performance**: Especially on slow devices and networks
- **Improved Accessibility**: Content is available even if JavaScript fails
- **Social Media Sharing**: Rich previews with OpenGraph metadata

### Framework Integration

SSR works seamlessly with popular JVM frameworks:

- **Ktor**: Integrate with routing and HTML responses
- **Spring Boot**: Use with WebFlux and Thymeleaf templates  
- **Quarkus**: Combine with Qute templates and reactive endpoints

See our [integration guides](docs/integration-guides.md) for detailed framework-specific examples.

For maintainers: Publishing instructions are available at docs/private/publishing.md.
- [WebAssembly Specification](https://webassembly.github.io/spec/)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)

### 🎉 **Contributing to the Ecosystem**

Summon aims to give back to the Kotlin/WASM community by:

- Sharing implementation techniques and patterns
- Contributing to upstream projects where appropriate
- Documenting lessons learned and best practices
- Supporting other developers building WASM applications

Together, we're building the future of web development with Kotlin! 🚀

