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

## Ktor Integration

The Ktor integration provides support for rendering Summon components in a Ktor application.

### Setup

Add the following dependencies to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.ktor:ktor-server-core:2.x.x")
    implementation("io.ktor:ktor-server-netty:2.x.x")
    // Or another Ktor server engine of your choice
}
```

### Basic Usage

```kotlin
import code.yousef.summon.integration.ktor.KtorRenderer
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val renderer = KtorRenderer()
    
    routing {
        get("/") {
            renderer.renderHtml(call) {
                // Your Summon components here
            }
        }
        
        get("/stream") {
            renderer.renderStreaming(call) {
                // Your Summon components here
            }
        }
    }
}
```

## Spring Boot Integration

The Spring Boot integration provides support for rendering Summon components in a Spring Boot application.

### Setup

Add the following dependencies to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.x.x")
}
```

### Basic Usage

#### Using with Spring MVC

```kotlin
import code.yousef.summon.integration.springboot.SpringBootRenderer
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import jakarta.servlet.http.HttpServletResponse

@Controller
class MyController {
    
    @GetMapping("/summon")
    fun renderSummon(response: HttpServletResponse) {
        SpringBootRenderer.render(response) {
            // Your Summon components here
        }
    }
}
```

#### Using with WebFlux

Add this dependency:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.x.x")
}
```

Then use it in your WebFlux controllers:

```kotlin
import code.yousef.summon.integration.springboot.WebFluxSupport
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class MyWebFluxController {
    
    @GetMapping("/webflux")
    fun webflux(): Mono<String> {
        return WebFluxSupport.renderToMono {
            // Your Summon components here
        }
    }
    
    @GetMapping("/webflux-streaming")
    fun webfluxStreaming() = WebFluxSupport.renderToFlux {
        // Your Summon components here
    }
}
```

#### Thymeleaf Integration

Add this dependency:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.x.x")
}
```

Then in your Thymeleaf template:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Summon + Thymeleaf</title>
</head>
<body>
    <div th:text="${#summon.render(@myComponent)}"></div>
</body>
</html>
```

And in your Spring Boot controller:

```kotlin
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MyThymeleafController {
    
    @GetMapping("/thymeleaf")
    fun thymeleafPage(model: Model): String {
        model.addAttribute("myComponent") {
            // Your Summon components here
        }
        return "template-name"
    }
}
```

## Examples

Check the example test files in each integration package for more complete examples:
- `KtorIntegrationTest.kt`
- `SpringBootIntegrationTest.kt` 
- `WebFluxIntegrationTest.kt`

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