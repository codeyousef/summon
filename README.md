# Summon

> ⚠️ **Alpha Status**: Summon is currently in alpha development and actively seeking testers and feedback. While core functionality is stable, APIs may change between releases. We welcome early adopters and contributors! Please report issues and share your experience.

**Summon** is a powerful, type-safe frontend framework for Kotlin Multiplatform that brings the elegance of Jetpack Compose to both browser and JVM environments. Build beautiful, responsive applications with a declarative syntax that feels natural to Kotlin developers.

## Project Structure

This monorepo contains:

- **`summon-core/`** - The main Summon library
- **`summon-cli/`** - Command-line tool for project generation 
- **`docs/`** - Documentation and guides

> 📝 **Examples**: Example projects showing various integrations have been moved to a separate repository for cleaner core library maintenance.

> 🎨 **Type-safe styling** with an intuitive modifier API inspired by Compose.
> 
> 🧩 **Component-based architecture** for maximum reusability and maintainability.
> 
> 🔄 **Reactive state management** that automatically updates your UI when data changes.

Summon combines the best ideas from modern frontend frameworks like React, Vue, and Next.js with the declarative UI patterns of Jetpack Compose and SwiftUI, while leveraging Kotlin's powerful type system to catch errors at compile time rather than runtime. Whether you're building a simple website or a complex web application, Summon provides the tools you need to create polished, professional user interfaces with less code and fewer bugs.

## Features

- **Cross-Platform**: Build once, run on both JavaScript and JVM platforms
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

## What's New

Recent enhancements to Summon include:

- **Enhanced Theme System** with typed theme classes for more type-safe access
- **Improved Modifier API** with type-safe CSS properties and enum support
- **Comprehensive Border API** with support for individual sides and properties
- **Enhanced Flexbox Layout** with alignment controls for Row and Column components
- **Extensive Color System** with Material Design and Catppuccin palettes
- **Gradient Support** for both linear and radial gradients with flexible options
- **Animation Enhancements** with keyframes and transition support
- **Improved Documentation** with comprehensive examples and API references

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

Summon is proudly inspired by [Kobweb](https://github.com/varabyte/kobweb), a modern framework for full stack web apps in Kotlin built upon Compose HTML. Kobweb's elegant API design and approach to creating web applications using Kotlin has been instrumental in shaping Summon's philosophy. We highly recommend checking out Kobweb if you're looking for a mature, feature-rich solution for Kotlin web development.

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

Add Summon to your project dependencies from Maven Central:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    // For JVM projects (Ktor, Spring Boot, Quarkus)
    implementation("io.github.codeyousef:summon-jvm:0.3.2.0")

    // For JavaScript/Browser projects
    implementation("io.github.codeyousef:summon-js:0.3.2.0")

    // For Kotlin Multiplatform projects
    implementation("io.github.codeyousef:summon:0.3.2.0")
}
```

**Note**: No authentication required - Summon is available directly from Maven Central!

## Version Management

Summon uses a centralized version management approach to ensure consistency across the main project and example projects. The version information is defined in a single place and referenced from all other places.

For more information, see [VERSIONING.md](VERSIONING.md).

## Security Best Practices

### Protecting Sensitive Credentials

To prevent accidentally committing sensitive credentials to the repository, Summon includes a Git pre-commit hook that checks for actual credentials in `gradle.properties`.

#### Installing the Git Hook

1. **Windows**:
   ```
   .git-hooks\install-hooks.bat
   ```

2. **Unix/Linux/macOS**:
   ```
   chmod +x .git-hooks/install-hooks.sh
   .git-hooks/install-hooks.sh
   ```

The pre-commit hook will prevent commits that contain actual credentials (not placeholders) in `gradle.properties`.

#### Recommended Approach for Credentials

For local development:

1. Keep placeholder values in `gradle.properties` (which is version controlled):
   ```properties
   # No authentication needed - using Maven Central
   ```

2. Store your actual credentials in `local.properties` (which is ignored by Git):
   ```properties
   # No authentication needed - using Maven Central
   ```

3. In your build script, prioritize values from `local.properties` over `gradle.properties`:
   ```kotlin
   val localProperties = java.util.Properties().apply {
       val localFile = rootProject.file("local.properties")
       if (localFile.exists()) {
           load(localFile.inputStream())
       }
   }

   // No authentication needed when using Maven Central
   // repositories { mavenCentral() }
   ```

This approach ensures that sensitive credentials are never committed to version control while maintaining a clear example of what credentials are needed in the version-controlled `gradle.properties` file.

## Server-Side Rendering (SSR)

Summon provides production-ready Server-Side Rendering capabilities for improved SEO, faster initial page loads, and better user experience. The SSR implementation includes full composition context management, state handling, and client-side hydration.

### Basic SSR Usage

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf

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

### Real-World SSR Examples

#### E-commerce Product Page
```kotlin
@Composable
fun ProductPage(productId: String) {
    val product = remember { mutableStateOf(loadProduct(productId)) }
    val cartItems = remember { mutableStateOf(0) }
    
    Column(modifier = Modifier()) {
        Text("${product.value.name}", modifier = Modifier())
        Text("$${product.value.price}", modifier = Modifier())
        
        Button(
            onClick = { cartItems.value += 1 },
            label = "Add to Cart (${cartItems.value})",
            modifier = Modifier()
        )
    }
}
```

#### Blog Post with Comments
```kotlin
@Composable
fun BlogPost(postId: String) {
    val post = remember { mutableStateOf(loadBlogPost(postId)) }
    val comments = remember { mutableStateOf(loadComments(postId)) }
    
    Column(modifier = Modifier()) {
        Text(post.value.title, modifier = Modifier())
        Text(post.value.content, modifier = Modifier())
        
        Text("Comments (${comments.value.size})", modifier = Modifier())
        comments.value.forEach { comment ->
            Text("${comment.author}: ${comment.text}", modifier = Modifier())
        }
    }
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
