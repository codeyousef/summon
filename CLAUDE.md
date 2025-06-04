# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Summon is a Kotlin Multiplatform (KMP) frontend framework that brings Jetpack Compose-style declarative UI to browser and JVM environments. It provides type-safe styling, component-based architecture, and reactive state management.

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

## Testing Approach

- Use `MockPlatformRenderer` for testing without actual rendering
- Test utilities available in `util/TestFixtures.kt` and `util/TestUtils.kt`
- Tests should mirror source structure for easy navigation
- Platform-specific rendering tests go in platform test directories

## Local Development Setup

Since Summon is not published to Maven Central:
1. Clone the repository
2. Run `./gradlew publishToMavenLocal`
3. Add `mavenLocal()` to your project's repositories
4. Use version `0.2.5.1` in dependencies

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