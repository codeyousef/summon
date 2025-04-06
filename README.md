# Summon

Summon is a Kotlin Multiplatform (JVM/JS) library that enables developers to build user interfaces using a Compose-like syntax. With Summon, you can write your UI in a declarative style familiar to Compose developers, and the library will handle rendering for both browser and JVM environments.

## Features

- **Cross-Platform**: Build once, run on both JavaScript and JVM platforms
- **Component-Based**: Create reusable UI components
- **Type-Safe**: Leverage Kotlin's type system for safer UI development
- **Styling System**: Flexible and powerful styling using a modifier API
- **State Management**: Simple yet powerful state management solutions
- **Routing**: Cross-platform routing system
- **Lifecycle Aware**: Built-in lifecycle management
- **Framework Interoperability**: Integrate with existing frameworks

## Inspiration

Summon is proudly inspired by [Kobweb](https://github.com/varabyte/kobweb), a modern framework for full stack web apps in Kotlin built upon Compose HTML. Kobweb's elegant API design and approach to creating web applications using Kotlin has been instrumental in shaping Summon's philosophy. We highly recommend checking out Kobweb if you're looking for a mature, feature-rich solution for Kotlin web development.

## Documentation

For detailed documentation, please check the [docs](docs/README.md) directory:

- [Getting Started Guide](docs/getting-started.md) - Introduction and basic setup
- [Components](docs/components.md) - Learn about Summon's built-in UI components
- [Routing](docs/routing.md) - Set up navigation in your application
- [State Management](docs/state-management.md) - Manage application state effectively
- [Styling](docs/styling.md) - Apply styles to your components
- [Integration Guides](docs/integration-guides.md) - Integrate with existing frameworks

### API Reference

Comprehensive API reference documentation is available in the [docs/api-reference](docs/api-reference) directory:

- [Core API](docs/api-reference/core.md) - Core interfaces and classes
- [Components API](docs/api-reference/components.md) - Built-in UI components
- [Modifier API](docs/api-reference/modifier.md) - Styling and layout modifiers
- [State API](docs/api-reference/state.md) - State management utilities
- [Routing API](docs/api-reference/routing.md) - Navigation and routing
- [Effects API](docs/api-reference/effects.md) - Side effects and lifecycle management
- [Events API](docs/api-reference/events.md) - Event handling and listeners

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
                implementation("code.yousef:summon:0.1.5")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation("code.yousef:summon-jvm:0.1.5")
            }
        }
        
        val jsMain by getting {
            dependencies {
                implementation("code.yousef:summon-js:0.1.5")
            }
        }
    }
}
```

## Quick Example

For a simple example of what you can build with Summon, check out the [Getting Started Guide](docs/getting-started.md).

## Contributing

Contributions are welcome! We are currently **focusing on testing contributions** as our top priority. We especially welcome:

- Unit tests for core components and APIs
- Integration tests for platform-specific functionality
- End-to-end tests for sample applications
- Test utilities and frameworks to make testing Summon applications easier

Of course, bug fixes, documentation improvements, and feature enhancements are also appreciated. Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.