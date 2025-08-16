# Summon Examples

This directory contains example projects demonstrating different ways to use Summon in various environments.

## Available Examples

### 1. JS Basic (`js-basic/`)
A simple browser-based example showing basic Summon usage with JavaScript/Kotlin target.

**To run:**
```bash
cd js-basic
./gradlew jsBrowserDevelopmentRun
```

Open http://localhost:8080 to see the example.

### 2. Spring Boot Integration (`spring-boot-integration/`)
Demonstrates how to integrate Summon with Spring Boot for server-side rendering and web applications.

**To run:**
```bash
cd spring-boot-integration
./gradlew bootRun
```

Open http://localhost:8080 to see the example.

### 3. Quarkus Integration (`quarkus-integration/`)
Shows how to use Summon with Quarkus for high-performance web applications.

**To run:**
```bash
cd quarkus-integration
./gradlew quarkusDev
```

Open http://localhost:8080 to see the example.

### 4. Portfolio App (`portfolio-app/`)
A complete portfolio application built with Summon and Ktor, featuring:
- User authentication
- Database integration
- Beautiful UI components
- Responsive design

**To run:**
```bash
cd portfolio-app
./gradlew run
```

Open http://localhost:8080 to see the portfolio.

## Building Examples

To include examples in your build, uncomment the relevant lines in the root `settings.gradle.kts`:

```kotlin
// Examples - uncomment to include in build
include(":examples:spring-boot-integration")
include(":examples:quarkus-integration") 
include(":examples:portfolio-app")
include(":examples:js-basic")
```

Then run:
```bash
./gradlew build
```

## Requirements

- JDK 17 or higher
- Node.js (for JS examples)
- Docker (for some examples with databases)

## Notes

Examples are excluded from the default build to keep build times fast when working on the core library. Enable them only when you need to test or run the examples.