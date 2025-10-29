# Summon Ktor Integration

This module provides comprehensive integration between the Summon UI library and Ktor web framework. It enables you to use Summon components within Ktor applications, offering both standard and streaming rendering options.

## Features

1. **KtorRenderer**: Basic renderer for Ktor responses
2. **Route Extensions**: Convenient route handling with Summon components
3. **Streaming Support**: Efficient streaming of large HTML content
4. **Response Extensions**: ApplicationCall extensions for easy response generation

## Getting Started

### 1. Add Summon Ktor Dependencies

In your Ktor application's `build.gradle.kts` file:

```kotlin
dependencies {
    // Ktor dependencies
    implementation("io.ktor:ktor-server-core:2.3.1")
    implementation("io.ktor:ktor-server-netty:2.3.1")
    implementation("io.ktor:ktor-html-builder:2.3.1")
    
    // Summon core
    implementation("code.yousef:summon:0.1.0")
    
    // Summon Ktor integration
    implementation("code.yousef:summon-ktor:0.1.0")
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

### 3. Use Components in Ktor Routes

Several approaches are available depending on your needs:

#### Method 1: Using the `summon` Route Extension

```kotlin
routing {
    summon("/hello", title = "My Greeting Page") {
        GreetingComponent("Ktor User")
    }
}
```

#### Method 2: Using the `respondSummon` Call Extension

```kotlin
routing {
    get("/greeting") {
        call.respondSummon("My Greeting Page") {
            GreetingComponent("Greeting User")
        }
    }
}
```

### Hydrated SSR (one-liner)

```kotlin
routing {
  get("/") {
    call.respondSummonHydrated {
      HomePage() // @Composable
    }
  }
}
```

### File-based router mounted on Ktor

```kotlin
routing {
  summonRouter(basePath = "/")
}
```

This uses Summon’s server router to resolve the current request path and renders with hydration by default.
You can disable hydration or provide your own 404 handler if you need to integrate with existing error pages:

```kotlin
routing {
  summonRouter(basePath = "/app", enableHydration = false) {
    respond(HttpStatusCode.NotFound, "Custom 404")
  }
}
```

#### Method 3: Using the Streaming Support

```kotlin
routing {
    get("/large-page") {
        call.respondStreamingSummon("Large Page") {
            // Generate large content that will be streamed to the client
            Column {
                repeat(1000) { index ->
                    Text("Line $index: Dynamic content")
                }
            }
        }
    }
}
```

#### Method 4: Hybrid Approach with Traditional Ktor HTML DSL

```kotlin
routing {
    get("/hybrid") {
        val renderer = KtorRenderer()
        
        // Render Summon components to strings
        val header = renderer.renderToString {
            Header("My Application")
        }
        
        val content = renderer.renderToString {
            GreetingComponent("Mixed User")
        }
        
        // Use in a regular Ktor HTML response
        call.respondHtml {
            head {
                meta(charset = "UTF-8")
                title("Hybrid Page")
            }
            body {
                unsafe {
                    +header
                }
                div {
                    h1 { +"Welcome to the hybrid page" }
                    unsafe {
                        +content
                    }
                }
            }
        }
    }
}
```

## Example Application

For a complete example application, see the `KtorDemo.kt` file in this package.

## Advanced Features

### Custom HTML Templates

You can customize the HTML structure by creating your own templates:

```kotlin
fun customHtmlTemplate(title: String, content: String): String {
    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>$title</title>
        <link rel="stylesheet" href="/styles.css">
    </head>
    <body>
        <header>
            <nav>
                <!-- Your navigation -->
            </nav>
        </header>
        <main>
            $content
        </main>
        <footer>
            <!-- Your footer -->
        </footer>
        <script src="/app.js"></script>
    </body>
    </html>
    """
}

// Then use it with KtorRenderer
val html = customHtmlTemplate(
    "My Page",
    KtorRenderer().renderToString { GreetingComponent("Custom User") }
)
call.respondText(html, ContentType.Text.Html)
```

### WebSocket Integration

For interactive applications, you can integrate with Ktor's WebSocket support:

```kotlin
routing {
    webSocket("/interactive") {
        // Handle interactive applications with WebSockets
        // This would typically involve a client-side JavaScript component
        // that communicates with your server via WebSockets
    }
}
```

### Static Resources

For applications requiring client-side JavaScript for interactivity:

1. Place your static resources in the `resources/static` directory
2. Configure Ktor to serve static files:

```kotlin
routing {
    static("/static") {
        resources("static")
    }
}
```

3. Reference these resources in your HTML templates 