# Integration Guides

Summon's standalone implementation is designed to work with various frameworks and platforms. These guides will help you integrate the standalone Summon components into your existing applications.

## Web Frameworks

### React Integration

You can use standalone Summon components within a React application:

```kotlin
// File: src/main/kotlin/SummonComponents.kt  
// Include the complete standalone Summon implementation (from quickstart.md)

// Create Summon components
@Composable
fun SummonGreeting(name: String): String {
    return Div(
        modifier = Modifier().padding("16px").gap("8px")
    ) {
        Text("Hello, $name from Summon!", modifier = Modifier().fontSize("18px").fontWeight("bold")) +
        Button(
            text = "Click me",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("console.log('Button clicked from Summon!')")
        )
    }
}

// Export function for React to use
@JsExport
@JsName("renderSummonGreeting")
fun renderSummonGreeting(name: String): String {
    return SummonGreeting(name)
}
```

```typescript
// React component that uses Summon (TypeScript/JavaScript)
import React, { useEffect, useRef } from 'react';

interface SummonProps {
  name: string;
}

const SummonComponent: React.FC<SummonProps> = ({ name }) => {
  const containerRef = useRef<HTMLDivElement>(null);
  
  useEffect(() => {
    if (containerRef.current) {
      // Use the exported Summon function
      const summonHtml = (window as any).renderSummonGreeting(name);
      containerRef.current.innerHTML = summonHtml;
    }
  }, [name]);
  
  return <div ref={containerRef} />;
};

export default SummonComponent;

// Usage in React:
// <SummonComponent name="World" />
```

### Vue Integration

Integrate standalone Summon with Vue:

```kotlin
// Summon counter component
@Composable
fun SummonCounter(count: Int): String {
    return Div(
        modifier = Modifier().padding("16px").gap("8px")
    ) {
        Text("Count: $count", modifier = Modifier().fontSize("18px")) +
        Button(
            text = "Increment",
            modifier = Modifier()
                .backgroundColor("#28a745")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("incrementCounter()")
        )
    }
}

// Export for Vue to use
@JsExport
@JsName("renderSummonCounter")
fun renderSummonCounter(count: Int): String {
    return SummonCounter(count)
}
```

```javascript
// Vue component that uses Summon (JavaScript)
const SummonCounterComponent = {
  name: 'SummonCounter',
  data() {
    return {
      count: 0
    };
  },
  template: '<div ref="summonContainer"></div>',
  mounted() {
    this.updateSummonContent();
    
    // Add global increment function
    window.incrementCounter = () => {
      this.count++;
      this.updateSummonContent();
    };
  },
  watch: {
    count() {
      this.updateSummonContent();
    }
  },
  methods: {
    updateSummonContent() {
      if (this.$refs.summonContainer) {
        const summonHtml = window.renderSummonCounter(this.count);
        this.$refs.summonContainer.innerHTML = summonHtml;
      }
    }
  },
  beforeDestroy() {
    // Clean up global function
    delete window.incrementCounter;
  }
};

// Usage in Vue:
// <SummonCounter />
```

### Angular Integration

Integrate standalone Summon with Angular:

```kotlin
// Simple todo list component
@Composable
fun SummonTodoList(todos: List<String>): String {
    return Div(
        modifier = Modifier().padding("16px").gap("8px")
    ) {
        Text("Todo List", modifier = Modifier().fontSize("20px").fontWeight("bold")) +
        
        todos.joinToString("") { todo ->
            Div(
                modifier = Modifier()
                    .padding("8px")
                    .backgroundColor("#f8f9fa")
                    .borderRadius("4px")
                    .style("margin-bottom", "4px")
            ) {
                Text("• $todo")
            }
        } +
        
        Button(
            text = "Add New Todo",
            modifier = Modifier()
                .backgroundColor("#007bff")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("addNewTodo()")
        )
    }
}

// Export for Angular
@JsExport
@JsName("renderSummonTodoList")
fun renderSummonTodoList(todos: Array<String>): String {
    return SummonTodoList(todos.toList())
}
```

```typescript
// Angular component (TypeScript)
import { Component, ElementRef, ViewChild, OnInit } from '@angular/core';

@Component({
  selector: 'app-summon-todo',
  template: '<div #summonContainer></div>'
})
export class SummonTodoComponent implements OnInit {
  @ViewChild('summonContainer', { static: true }) container!: ElementRef;
  
  todos: string[] = ['Learn Summon', 'Integrate with Angular', 'Build amazing apps'];

  ngOnInit() {
    this.updateSummonContent();
    
    // Add global function for Summon to call
    (window as any).addNewTodo = () => {
      this.todos.push(`New todo ${this.todos.length + 1}`);
      this.updateSummonContent();
    };
  }
  
  private updateSummonContent() {
    if (this.container?.nativeElement) {
      const html = (window as any).renderSummonTodoList(this.todos);
      this.container.nativeElement.innerHTML = html;
    }
  }
}
```

## Backend Frameworks

### Spring Boot Integration

Integrate standalone Summon with Spring Boot for server-side rendering:

```kotlin
// Include the standalone Summon implementation

@Composable
fun HomePage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Welcome to Spring Boot + Summon!", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("This page is rendered server-side with Spring Boot and Summon.") +
        Button(
            text = "Click me",
            modifier = Modifier()
                .backgroundColor("#28a745")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("alert('Hello from Spring Boot + Summon!')")
        )
    }
}

// Spring Boot controller
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.http.MediaType

@Controller
class SummonController {
    
    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    @ResponseBody
    fun home(): String {
        // Render Summon component to HTML string
        val summonHtml = HomePage()
        
        // Return complete HTML page
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Summon with Spring Boot</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { 
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    margin: 0; 
                    padding: 0; 
                    background-color: #f8f9fa;
                }
            </style>
        </head>
        <body>
            $summonHtml
        </body>
        </html>
        """.trimIndent()
    }
    
    @GetMapping("/api/data")
    @ResponseBody
    fun apiData(): String {
        // You can also use Summon to generate JSON responses or partial HTML
        return """{"message": "Hello from Spring Boot API"}"""
    }
}
```

### Ktor Integration

Integrate standalone Summon with Ktor for server-side rendering:

```kotlin
// Include the standalone Summon implementation

@Composable
fun KtorHomePage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Welcome to Ktor + Summon!", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("This page is rendered server-side with Ktor and Summon.") +
        Text("Ktor provides a lightweight, asynchronous server framework.") +
        Button(
            text = "Ktor Power!",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("alert('Hello from Ktor + Summon!')")
        )
    }
}

// Ktor application setup
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                // Render Summon component to HTML
                val summonHtml = KtorHomePage()
                
                call.respondText(
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Summon with Ktor</title>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            body { 
                                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                                margin: 0; 
                                padding: 0; 
                                background-color: #f8f9fa;
                            }
                        </style>
                    </head>
                    <body>
                        $summonHtml
                    </body>
                    </html>
                    """.trimIndent(),
                    ContentType.Text.Html
                )
            }
            
            get("/api/health") {
                call.respondText("""{"status": "healthy", "framework": "Ktor + Summon"}""")
            }
        }
    }.start(wait = true)
}
```

### Quarkus Integration

Integrate standalone Summon with Quarkus for server-side rendering:

```kotlin
// Include the standalone Summon implementation

@Composable
fun QuarkusHomePage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Welcome to Quarkus + Summon!", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("This page is rendered server-side with Quarkus and Summon.") +
        Text("Quarkus provides supersonic, subatomic Java frameworks.") +
        Button(
            text = "Quarkus Speed!",
            modifier = Modifier()
                .backgroundColor("#4695EB")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("alert('Hello from Quarkus + Summon!')")
        )
    }
}

// Quarkus resource
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/")
class SummonResource {
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        // Render Summon component to HTML
        val summonHtml = QuarkusHomePage()
        
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Summon with Quarkus</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { 
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    margin: 0; 
                    padding: 0; 
                    background-color: #f8f9fa;
                }
            </style>
        </head>
        <body>
            $summonHtml
        </body>
        </html>
        """.trimIndent()
    }
    
    @GET
    @Path("/api/health")
    @Produces(MediaType.APPLICATION_JSON)
    fun health(): String {
        return """{"status": "healthy", "framework": "Quarkus + Summon"}"""
    }
}
```

## Simple Integration Examples

### Static HTML Generation

Use Summon to generate static HTML content:

```kotlin
// Include the standalone Summon implementation

@Composable
fun StaticPage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Static HTML Page", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("This page was generated using standalone Summon components.") +
        Text("Perfect for static site generators, email templates, or reports.")
    }
}

// Generate HTML file
fun main() {
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Static Summon Page</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { 
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    margin: 0; 
                    padding: 0; 
                    background-color: #f8f9fa;
                }
            </style>
        </head>
        <body>
            ${StaticPage()}
        </body>
        </html>
    """.trimIndent()
    
    // Write to file or use in your application
    println(html)
}
```

### Simple API Response Generation

Use Summon to generate API responses:

```kotlin
// Data classes for API responses
data class User(val id: Int, val name: String, val email: String)

@Composable
fun UserCard(user: User): String {
    return Div(
        modifier = Modifier()
            .backgroundColor("#ffffff")
            .padding("16px")
            .borderRadius("8px")
            .style("border", "1px solid #e0e0e0")
            .style("margin-bottom", "8px")
    ) {
        Text(user.name, modifier = Modifier().fontSize("18px").fontWeight("bold")) +
        Text(user.email, modifier = Modifier().color("#666").fontSize("14px"))
    }
}

@Composable
fun UserList(users: List<User>): String {
    return Column(modifier = Modifier().gap("8px")) {
        users.joinToString("") { user -> UserCard(user) }
    }
}

// Use in API endpoint
fun getUsersHtml(): String {
    val users = listOf(
        User(1, "John Doe", "john@example.com"),
        User(2, "Jane Smith", "jane@example.com"),
        User(3, "Bob Johnson", "bob@example.com")
    )
    
    return UserList(users)
}
```

## Best Practices for Integration

### 1. Keep Components Pure

```kotlin
// Good: Pure function that always returns the same output for the same input
@Composable
fun ProductCard(product: Product): String {
    return Div(
        modifier = Modifier()
            .backgroundColor("white")
            .padding("16px")
            .borderRadius("8px")
    ) {
        Text(product.name, modifier = Modifier().fontSize("18px").fontWeight("bold")) +
        Text("$${product.price}", modifier = Modifier().color("#28a745"))
    }
}
```

### 2. Use Framework-Specific Patterns

```kotlin
// For Spring Boot - Use @Controller pattern
@RestController
class HtmlController {
    @GetMapping("/products", produces = [MediaType.TEXT_HTML_VALUE])
    fun products(): String = ProductList(getProducts())
}

// For Ktor - Use routing DSL
fun Application.configureRouting() {
    routing {
        get("/products") {
            call.respondText(ProductList(getProducts()), ContentType.Text.Html)
        }
    }
}

// For Quarkus - Use JAX-RS annotations
@Path("/products")
class ProductResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun products(): String = ProductList(getProducts())
}
```

### 3. Handle State Management Appropriately

```kotlin
// For client-side: Use simple state tracking
var appState = AppState()

@Composable
fun StatefulComponent(): String {
    return Div {
        Text("Current user: ${appState.currentUser}") +
        Button(
            text = "Login",
            modifier = Modifier().onClick("handleLogin()")
        )
    }
}

// For server-side: Pass data as parameters
@Composable
fun ServerComponent(user: User?, isLoggedIn: Boolean): String {
    return when {
        isLoggedIn && user != null -> Text("Welcome, ${user.name}!")
        else -> Text("Please log in")
    }
}
```

This standalone approach gives you:

✅ **Framework Flexibility**: Works with any JVM or JS framework  
✅ **No Dependencies**: No external libraries required  
✅ **Simple Integration**: Easy to understand and integrate  
✅ **Type Safety**: Full Kotlin type checking  
✅ **Performance**: Lightweight with minimal overhead  
✅ **Debugging**: Simple, debuggable code  

The standalone Summon implementation is particularly well-suited for server-side rendering, static site generation, and simple web applications where you want the benefits of a component system without complex dependencies. 