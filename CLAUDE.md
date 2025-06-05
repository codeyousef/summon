# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Summon is a Kotlin Multiplatform (KMP) UI framework that brings Jetpack Compose-style declarative UI to browser and JVM environments. It provides type-safe styling, component-based architecture, and reactive state management.

## Key Commands

### Build and Test
```bash
# Build the entire project
./gradlew build

# Run all tests
./gradlew test

# Run tests for specific platform
./gradlew jvmTest
./gradlew jsTest
./gradlew commonTest

# Run a single test
./gradlew test --tests "code.yousef.summon.components.input.ButtonTest"

# Build and install to local Maven repository (required for local development)
./gradlew publishToMavenLocal

# Run JS development server
./gradlew jsBrowserDevelopmentRun

# Build production JS bundle
./gradlew jsBrowserProductionWebpack
```

### Publishing
```bash
# Local Publishing (for development)
./gradlew publishToMavenLocal

# Publish to GitHub Packages
./gradlew publishAllPublicationsToGitHubPackagesRepository

# Clean Gradle cache (for troubleshooting)
./clean-gradle-cache.sh  # Linux/macOS
clean-gradle-cache.bat   # Windows
```

#### Automated Publishing via GitHub Actions
Publishing is automated through GitHub Actions. When you create a new release on GitHub:
1. The workflow automatically builds and tests the project
2. Publishes to GitHub Packages

No additional secrets are required - GitHub Actions automatically has access to publish to GitHub Packages.

### Verification Tasks
```bash
# Verify framework integrations
./gradlew verifyQuarkusIntegration
./gradlew verifyKtorIntegration
./gradlew verifySpringBootIntegration
```

## Architecture

### Platform Structure
- **commonMain**: Shared code across all platforms (core abstractions, components, state)
- **jsMain**: JavaScript-specific implementations (DOM rendering, browser APIs)
- **jvmMain**: JVM-specific implementations (server-side rendering, backend integrations)
- **commonTest/jsTest/jvmTest**: Platform-specific tests

### Core Patterns

1. **Composable Functions**: UI components are marked with `@Composable` annotation
2. **Modifier System**: Type-safe styling inspired by Compose, chaining modifiers for styling
3. **State Management**: Reactive state with `mutableStateOf()` and `remember()`
4. **Effects**: Side effects managed through `LaunchedEffect`, `DisposableEffect`
5. **Platform Renderer**: Abstract renderer with platform-specific implementations

### Key Components

- **PlatformRenderer**: Core abstraction for rendering across platforms
- **Composer/Recomposer**: Manages composition and recomposition
- **Router**: Next.js-style file-based routing with automatic page discovery
- **Theme**: Comprehensive theming system with Material Design colors
- **Security**: JWT authentication and RBAC support

## Important Development Rules

1. **Never modify build.gradle.kts files** - Ask the user to make changes instead
2. **Always run gradle build after changes** and fix any compilation errors
3. **Refer to troubleshooting.md** when fixing errors and update it with solutions
4. **Test thoroughly** - Each component should have corresponding tests
5. **Maintain platform separation** - Keep platform-specific code in appropriate source sets

## Common Issues and Solutions

### Phantom Publishing Tasks
If you see errors about `publishJsPublicationToCentralRepository` or similar tasks that don't exist:
- This is a Gradle cache issue from previous configurations
- Run `./clean-gradle-cache.sh` to completely clear the cache
- The project only publishes to GitHub Packages

## Testing Approach

- Use `MockPlatformRenderer` for testing without actual rendering
- Test utilities available in `util/TestFixtures.kt` and `util/TestUtils.kt`
- Tests should mirror source structure for easy navigation
- Platform-specific rendering tests go in platform test directories

## Publishing Configuration

### GitHub Packages Publishing
- Automated through GitHub Actions on releases and main branch pushes
- Snapshot versions published automatically from main branch
- JS publications include both .jar and .klib files

### Known Issues
- JS test compilation fails with Kotlin 2.2.0-Beta1 due to kotlinx-serialization issues
- Workaround: Use `./gradlew build -x jsTest -x jsBrowserTest`

## Using Summon in Your Project

Summon is available from GitHub Packages:
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/codeyousef/summon")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    implementation("io.github.codeyousef:summon:0.2.6")
}
```

## Framework Integrations

Summon integrates with:
- **Quarkus**: Through Qute templates and reactive REST endpoints
- **Ktor**: HTML DSL and server-side rendering
- **Spring Boot**: WebFlux and Thymeleaf integration

Examples available in `docs/examples/` directory.

## File-Based Routing

Pages are automatically discovered in `src/commonMain/kotlin/.../routing/pages/`:
- `Index.kt` → `/`
- `About.kt` → `/about`
- `404.kt` → Not found page

Router generates page loader code automatically during build.