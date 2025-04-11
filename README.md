# Summon

Summon is a Kotlin Multiplatform (JVM/JS) library that enables developers to build user interfaces using a Compose-like syntax. With Summon, you can write your UI in a declarative style familiar to Compose developers, and the library will handle rendering for both browser and JVM environments.

## Features

- **Cross-Platform**: Build once, run on both JavaScript and JVM platforms
- **Component-Based**: Create reusable UI components
- **Type-Safe**: Leverage Kotlin's type system for safer UI development
- **Styling System**: Flexible and powerful styling using a modifier API
- **State Management**: Simple yet powerful state management solutions
- **Next.js-Style Routing**: File-based routing with automatic page discovery and code generation
- **Lifecycle Aware**: Built-in lifecycle management
- **Framework Interoperability**: Integrate with existing frameworks
- **Security**: Comprehensive JWT authentication and role-based access control (RBAC)
- **Accessibility**: Built-in accessibility features for inclusive applications
- **Animation**: Smooth animations and transitions
- **SSR Support**: Server-side rendering capabilities
- **Theme System**: Flexible theming with dark mode support

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
- [Focus Management API](docs/api-reference/focus.md) - Focus management and keyboard navigation
- [Validation API](docs/api-reference/validation.md) - Form validation and input validation
- [Security API](docs/api-reference/security.md) - Security features and access control
- [Authentication API](docs/api-reference/auth.md) - Authentication providers and JWT integration
- [Accessibility API](docs/api-reference/accessibility.md) - Accessibility features and ARIA support
- [SEO API](docs/api-reference/seo.md) - SEO features and meta tags

## Local Development Setup

Since Summon is not yet published to Maven Central, you'll need to build it locally:

### 1. Clone the Repository

```bash
git clone https://github.com/yebaital/summon.git
cd summon
```

### 2. Build and Install to Local Maven Repository

```bash
./gradlew publishToMavenLocal
```

### 3. Add the Local Repository to Your Project

In your project's `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        // other repositories
    }
}
```

### 4. Include the Dependency

In your project's `build.gradle.kts` file:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("code.yousef:summon:0.2.0.1")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation("code.yousef:summon-jvm:0.2.0.1")
            }
        }
        
        val jsMain by getting {
            dependencies {
                implementation("code.yousef:summon-js:0.2.0.1")
            }
        }
    }
}
```