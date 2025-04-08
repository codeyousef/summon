# Summon Platform Independence

This document describes the changes made to transform Summon from a Quarkus-dependent library to a framework-agnostic UI library.

## Overview

Summon has been restructured to be framework-independent, allowing it to be integrated with various server-side frameworks like Quarkus, Spring Boot, Ktor, and more. This change improves flexibility, reduces adoption barriers, and creates a cleaner architecture.

## Key Changes

### 1. Dependency Management

- **Removed direct Quarkus dependencies** from core build
- **Added optional integration configuration** for framework-specific dependencies
- **Created `quarkusIntegration` configuration** in build.gradle.kts for optional Quarkus dependencies

Before:
```kotlin
val jvmMain by getting {
    dependencies {
        implementation("io.quarkus:quarkus-core:3.7.1")
        implementation("io.quarkus:quarkus-vertx-web:3.7.1")
        implementation("io.quarkus:quarkus-kotlin:3.7.1")
        // ...other dependencies
    }
}
```

After:
```kotlin
val jvmMain by getting {
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.9.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")
        // No direct Quarkus dependencies
    }
}

// Optional Quarkus integration
configurations {
    create("quarkusIntegration")
}

dependencies {
    "quarkusIntegration"("io.quarkus:quarkus-core:3.7.1")
    "quarkusIntegration"("io.quarkus:quarkus-vertx-web:3.7.1")
    "quarkusIntegration"("io.quarkus:quarkus-kotlin:3.7.1")
    "quarkusIntegration"("io.quarkus:quarkus-qute:3.7.1")
}
```

### 2. Integration Structure

- **Created `integration` package** for framework-specific adapters
- **Added `QuarkusRenderer`** for rendering Summon components in Quarkus applications
- **Added `SummonQuteExtensions`** for integrating with Quarkus Qute templates
- **Planned integrations** for Spring Boot and Ktor

### 3. Documentation Updates

- **Updated migration guide** with details on platform independence
- **Added integration examples** for various frameworks
- **Created this document** for summarizing the changes

## Integration Example

### Quarkus Integration

```kotlin
// In your Quarkus route handler
@Route(path = "/hello")
fun helloWorld(context: RoutingContext) {
    context.summonRenderer("Hello World").render {
        Text("Hello from Summon!")
    }
}
```

### Qute Template Integration

```kotlin
// Register a component for use in templates
SummonQuteExtensions.registerComponent("Greeting") { props ->
    {
        val name = props["name"] as? String ?: "World"
        Text("Hello, $name!")
    }
}

// Use in Qute template
// {renderSummon('Greeting', {'name': 'Quarkus User'})}
```

## Next Steps

1. Complete Quarkus integration implementation
2. Add Spring Boot integration adapter
3. Add Ktor integration adapter
4. Provide comprehensive documentation for all integrations
5. Create examples for each integration type 