# Summon Spring Boot Integration

This module provides comprehensive integration between the Summon UI library and Spring Boot framework. It enables you to use Summon components within Spring Boot applications, including support for Thymeleaf templates, MVC controllers, and reactive WebFlux applications.

## Features

1. **SpringBootRenderer**: Simple renderer for Spring MVC responses
2. **ThymeleafExtensions**: Integration with Thymeleaf templates
3. **WebFluxSupport**: Integration with reactive Spring WebFlux
4. **AutoConfiguration**: Automatic configuration for Spring Boot applications

## Getting Started

### 1. Add Summon Spring Boot Dependencies

In your Spring Boot application's `build.gradle.kts` file:

```kotlin
dependencies {
    // Spring Boot dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Optional: Thymeleaf for template integration
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    
    // Summon core
    implementation("code.yousef:summon:0.1.0")
    
    // Summon Spring Boot integration
    implementation("code.yousef:summon-spring-boot:0.1.0")
}
```

### 2. Create Summon Components

Create your Summon components using the `@Composable` annotation:

```kotlin
@Composable
fun GreetingComponent(name: String) {
    Column {
        Text("Hello, $name!")
        Button(onClick = { /* handle click */ }) {
            Text("Click me")
        }
    }
}
```

### 3. Use Components in Spring Controllers

```kotlin
@Controller
class HomeController {
    
    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun home(): ResponseEntity<String> {
        val renderer = SpringBootRenderer()
        return renderer.render(title = "My App") {
            GreetingComponent("Spring Boot User")
        }
    }
}
```

### 4. Use with Thymeleaf Templates

First, register components:

```kotlin
@Configuration
class SummonConfiguration {
    init {
        SummonExpressionObject.registerGlobalComponent("Greeting") { props ->
            {
                val name = props["name"] as? String ?: "World"
                GreetingComponent(name)
            }
        }
    }
}
```

Then, use in your Thymeleaf templates:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${pageTitle}">Summon with Thymeleaf</title>
</head>
<body>
    <h1>Welcome to Summon + Spring Boot</h1>
    
    <!-- Render a Summon component -->
    <div th:utext="${#summon.render('Greeting', {'name': username})}"></div>
</body>
</html>
```

### 5. WebFlux Integration (Reactive)

For reactive applications, use the WebFlux integration:

```kotlin
@RestController
class ReactiveController {
    @GetMapping("/reactive", produces = [MediaType.TEXT_HTML_VALUE])
    fun reactivePage(): Flux<String> {
        return WebFluxSupport.renderToFlux {
            GreetingComponent("Reactive User")
        }
    }
}
```

## Example Application

For a complete example application, see the `SpringBootDemo.kt` file in this package.

## Advanced Configuration

### Custom Templates

You can customize the HTML template used for rendering:

```kotlin
val customRenderer = SpringBootRenderer(
    templateProvider = { title, content ->
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$title</title>
            <link rel="stylesheet" href="/styles.css">
        </head>
        <body>
            <div class="container">
                $content
            </div>
            <script src="/app.js"></script>
        </body>
        </html>
        """
    }
)
```

### Static Resource Integration

For applications requiring client-side JavaScript for interactivity, you can:

1. Include static assets in your Spring Boot project's `src/main/resources/static` directory
2. Reference them in your templates or custom HTML wrapper
3. Use the Summon hydration utilities to support client-side interactivity 