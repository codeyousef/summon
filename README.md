# Summon

> ⚠️ **Alpha Status**: Summon is currently in alpha development and actively seeking testers and feedback. While core functionality is stable, APIs may change between releases. We welcome early adopters and contributors! Please report issues and share your experience.

**Summon** is a powerful, type-safe frontend framework for Kotlin Multiplatform that brings the elegance of Jetpack Compose to both browser and JVM environments. Build beautiful, responsive applications with a declarative syntax that feels natural to Kotlin developers.

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
- **SSR Support**: Server-side rendering capabilities for improved performance
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
- **Snackbar** & **SnackbarHost** - Temporary notifications
- **Progress** & **ProgressBar** - Progress indicators
- **Tooltip** - Hover tooltips

### Navigation Components
- **Link** - Navigation links
- **TabLayout** - Tab-based navigation

### Utility Components
- **Div** - Basic container element
- **AccessibleElement** - Accessibility wrapper

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
# Project Setup

## Database Setup

This project uses PostgreSQL as the database. You can run it using Docker Compose:

```shell
docker-compose up -d
```

This will start a PostgreSQL database on port 5432.

## Running in Development Mode

After starting the database, you can run the application in dev mode:

```shell
./mvnw compile quarkus:dev
```

## Troubleshooting

### ClosedChannelException with Docker

If you encounter `java.io.IOException: java.nio.channels.ClosedChannelException` errors when Quarkus tries to start a PostgreSQL container using TestContainers, it could be related to Docker connectivity issues.

Possible solutions:

1. Make sure Docker is running correctly
2. If using Docker Desktop, try restarting it
3. If the issue persists, use an external database instead of DevServices by configuring the datasource in application.properties

For test environments, DevServices is still enabled to provide an isolated test database.
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

Add Summon to your project dependencies from GitHub Packages:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

// Dependencies same as above
```

**Note**: GitHub Packages requires authentication. Add your credentials to `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

Generate a GitHub token with `read:packages` permission at https://github.com/settings/tokens

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
   gpr.user=your_github_username
   gpr.key=your_github_token
   ```

2. Store your actual credentials in `local.properties` (which is ignored by Git):
   ```properties
   gpr.user=YOUR_ACTUAL_USERNAME
   gpr.key=YOUR_ACTUAL_TOKEN
   ```

3. In your build script, prioritize values from `local.properties` over `gradle.properties`:
   ```kotlin
   val localProperties = java.util.Properties().apply {
       val localFile = rootProject.file("local.properties")
       if (localFile.exists()) {
           load(localFile.inputStream())
       }
   }

   val githubUser = localProperties.getProperty("gpr.user") 
       ?: project.findProperty("gpr.user") as String? 
       ?: System.getenv("GITHUB_ACTOR")

   val githubToken = localProperties.getProperty("gpr.key") 
       ?: project.findProperty("gpr.key") as String? 
       ?: System.getenv("GITHUB_TOKEN")
   ```

This approach ensures that sensitive credentials are never committed to version control while maintaining a clear example of what credentials are needed in the version-controlled `gradle.properties` file.
