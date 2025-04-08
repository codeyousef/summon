# Summon Framework Integrations

This directory contains integration packages that help you use Summon with various backend frameworks.

## Available Integrations

### Quarkus Integration

The `quarkus` package provides utilities for integrating Summon UI components with Quarkus applications.

#### Setup

1. Add Quarkus dependencies to your project:

```kotlin
// In build.gradle.kts
dependencies {
    implementation("io.quarkus:quarkus-core:3.7.1")
    implementation("io.quarkus:quarkus-vertx-web:3.7.1")
    implementation("io.quarkus:quarkus-kotlin:3.7.1")
}
```

#### Basic Usage

```kotlin
import code.yousef.summon.components.Text
import code.yousef.summon.integration.quarkus.QuarkusRenderer
import code.yousef.summon.integration.quarkus.QuarkusRenderer.Companion.summonRenderer
import io.quarkus.vertx.web.Route
import io.vertx.ext.web.RoutingContext

class ExampleRoutes {
    @Route(path = "/hello")
    fun helloWorld(context: RoutingContext) {
        context.summonRenderer("Hello World").render {
            Text("Hello from Summon!")
        }
    }
}
```

#### Qute Template Integration

1. Add Qute dependency:

```kotlin
implementation("io.quarkus:quarkus-qute:3.7.1")
```

2. Register components:

```kotlin
import code.yousef.summon.components.Text
import code.yousef.summon.integration.quarkus.SummonQuteExtensions

fun registerComponents() {
    SummonQuteExtensions.registerComponent("Greeting") { props ->
        {
            val name = props["name"] as? String ?: "World"
            Text("Hello, $name!")
        }
    }
}
```

3. Use in Qute templates:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Summon with Qute</title>
</head>
<body>
    <h1>Welcome</h1>
    <div>
        {renderSummon('Greeting', {'name': 'Quarkus User'})}
    </div>
</body>
</html>
```

## Future Integrations

- Spring Boot
- Ktor
- Micronaut

## Creating Custom Integrations

If you need to integrate Summon with a different framework, follow these general steps:

1. Create an integration class that initializes the `JvmPlatformRenderer`
2. Set up the renderer using `setPlatformRenderer()`
3. Create a method to render composable functions 
4. Handle the output appropriately for your framework

Example template:

```kotlin
class MyFrameworkRenderer(/* framework-specific params */) {
    private val renderer = JvmPlatformRenderer()
    
    fun render(content: @Composable () -> Unit) {
        setPlatformRenderer(renderer)
        
        val html = buildString {
            appendHTML().html {
                // HTML structure
                content()
            }
        }
        
        // Framework-specific output handling
    }
} 