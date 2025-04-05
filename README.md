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

## Documentation

For detailed documentation, please check the [docs](docs/README.md) directory:

- [Getting Started Guide](docs/getting-started.md) - Introduction and basic setup
- [Components](docs/components.md) - Learn about Summon's built-in UI components
- [Routing](docs/routing.md) - Set up navigation in your application
- [State Management](docs/state-management.md) - Manage application state effectively
- [Styling](docs/styling.md) - Apply styles to your components
- [Integration Guides](docs/integration-guides.md) - Integrate with existing frameworks
- [Publishing to Maven Central](docs/publishing.md) - Publish your Summon-based library

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
                implementation("code.yousef:summon:0.1.0-SNAPSHOT")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation("code.yousef:summon-jvm:0.1.0-SNAPSHOT")
            }
        }
        
        val jsMain by getting {
            dependencies {
                implementation("code.yousef:summon-js:0.1.0-SNAPSHOT")
            }
        }
    }
}
```

## Quick Example

For a simple example of what you can build with Summon, check out the [Getting Started Guide](docs/getting-started.md).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.