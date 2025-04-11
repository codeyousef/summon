# Integration Guides

Summon is designed to work with various frameworks and platforms. These guides will help you integrate Summon into your existing applications.

## Web Frameworks

### React Integration

You can use Summon within a React application:

```kotlin
// Set up the Summon component
@Composable
fun SummonGreeting(name: String) {
    Div {
        Text("Hello, $name from Summon!")
        Button(
            text = "Click me",
            onClick = { console.log("Button clicked!") }
        )
    }
}

// Create a React wrapper
external interface SummonProps : Props {
    var name: String
}

val SummonComponent = FC<SummonProps> { props ->
    val containerRef = useRef<HTMLDivElement>()
    
    useEffect({
        // Render Summon component when the component mounts
        val container = containerRef.current ?: return@useEffect
        val renderer = renderComposable(container) {
            SummonGreeting(props.name)
        }
        
        // Clean up when the component unmounts
        return@useEffect {
            renderer.dispose()
        }
    }, arrayOf(props.name))
    
    // Create a container for Summon to render into
    return@FC createElement("div", createObj {
        ref = containerRef
    })
}

// Use in React
// <SummonComponent name="World" />
```

### Vue Integration

Integrate Summon with Vue:

```kotlin
// Set up the Summon component
@Composable
fun SummonCounter() {
    var count by remember { mutableStateOf(0) }
    
    Div {
        Text("Count: $count")
        Button(
            text = "Increment",
            onClick = { count++ }
        )
    }
}

// Create a Vue component that uses Summon
@JsExport
@JsName("createSummonVueComponent")
fun createSummonVueComponent() = js("""
{
    name: 'SummonCounter',
    props: {},
    template: '<div ref="summonContainer"></div>',
    data() {
        return {
            summonRenderer: null
        };
    },
    mounted() {
        const container = this.$refs.summonContainer;
        this.summonRenderer = renderComposable(container, () => {
            SummonCounter();
        });
    },
    beforeDestroy() {
        if (this.summonRenderer) {
            this.summonRenderer.dispose();
        }
    }
}
""")
```

### Angular Integration

Integrate Summon with Angular:

```kotlin
// Set up the Summon component
@Composable
fun SummonTodoList() {
    var todos by remember { mutableStateOf(listOf("Learn Summon", "Integrate with Angular")) }
    var newTodo by remember { mutableStateOf("") }
    
    Div {
        // Input for new todos
        TextField(
            value = newTodo,
            onValueChange = { newTodo = it },
            placeholder = "Add todo"
        )
        
        Button(
            text = "Add",
            onClick = { 
                if (newTodo.isNotEmpty()) {
                    todos = todos + newTodo
                    newTodo = ""
                }
            }
        )
        
        // Todo list
        for (todo in todos) {
            Div {
                Text(todo)
            }
        }
    }
}

// Angular integration code in TypeScript:
/*
import { Component, ElementRef, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { renderComposable } from 'summon-js';
import { SummonTodoList } from './summon-components';

@Component({
  selector: 'app-summon',
  template: '<div #summonContainer></div>'
})
export class SummonComponent implements OnInit, OnDestroy {
  @ViewChild('summonContainer', { static: true }) container: ElementRef;
  private renderer: any;

  ngOnInit() {
    this.renderer = renderComposable(this.container.nativeElement, () => {
      SummonTodoList();
    });
  }

  ngOnDestroy() {
    if (this.renderer) {
      this.renderer.dispose();
    }
  }
}
*/
```

## Backend Frameworks

### Spring Boot Integration

Integrate Summon with Spring Boot for server-side rendering:

```kotlin
import code.yousef.summon.renderToString
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SummonController {
    @GetMapping("/")
    fun home(): String {
        // Render to HTML string
        val html = renderToString {
            HomePage()
        }
        
        // Return the HTML
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Summon with Spring Boot</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body>
            $html
            <script src="/js/app.js"></script>
        </body>
        </html>
        """.trimIndent()
    }
}
```

### Ktor Integration

Integrate Summon with Ktor for server-side rendering:

```kotlin
import code.yousef.summon.renderToString
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.html.*

fun Application.module() {
    routing {
        get("/") {
            // Render to HTML string
            val html = renderToString {
                HomePage()
            }
            
            // Return the HTML
            call.respondText(
                """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Summon with Ktor</title>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body>
                    $html
                    <script src="/js/app.js"></script>
                </body>
                </html>
                """.trimIndent(),
                ContentType.Text.Html
            )
        }
    }
}
```

### Quarkus Integration

Integrate Summon with Quarkus for server-side rendering:

```kotlin
import code.yousef.summon.renderToString
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/")
@ApplicationScoped
class SummonResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        // Render to HTML string
        val html = renderToString {
            HomePage()
        }
        
        // Return the HTML
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Summon with Quarkus</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body>
            $html
            <script src="/js/app.js"></script>
        </body>
        </html>
        """.trimIndent()
    }
}
```

## Desktop Integration

### JavaFX Integration

Integrate Summon with JavaFX for desktop applications:

```kotlin
import code.yousef.summon.renderToString
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.web.WebView
import javafx.stage.Stage

class SummonJavaFXApp : Application() {
    override fun start(stage: Stage) {
        // Render to HTML string
        val html = renderToString {
            DesktopApp()
        }
        
        // Create a WebView to display the HTML
        val webView = WebView()
        val webEngine = webView.engine
        
        // Load the HTML
        webEngine.loadContent("""
        <!DOCTYPE html>
        <html>
        <head>
            <title>Summon with JavaFX</title>
            <meta charset="UTF-8">
            <style>
                body { font-family: 'Arial', sans-serif; margin: 0; padding: 0; }
            </style>
        </head>
        <body>
            $html
            <script>
                // Bridge between Kotlin and JavaScript
                window.javaFXBridge = {
                    callKotlin: function(methodName, args) {
                        // This will be implemented in the WebEngine
                    }
                };
            </script>
        </body>
        </html>
        """.trimIndent())
        
        // Set up two-way communication
        webEngine.javaScriptEngineEnabled = true
        
        // Create the scene
        val scene = Scene(webView, 800.0, 600.0)
        stage.title = "Summon JavaFX App"
        stage.scene = scene
        stage.show()
    }
}

fun main() {
    Application.launch(SummonJavaFXApp::class.java)
}
```

### Compose for Desktop Integration

Integrate Summon with Compose for Desktop:

```kotlin
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import code.yousef.summon.renderToString

@Composable
fun SummonInCompose() {
    // Render to HTML string
    val html = remember { 
        renderToString {
            DesktopApp()
        }
    }
    
    // Use Compose's WebView (requires a dependency)
    WebView(
        modifier = Modifier.fillMaxSize(),
        content = html,
        onEvent = { event ->
            // Handle events from Summon
            println("Event from Summon: $event")
        }
    )
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Summon in Compose") {
        SummonInCompose()
    }
}
```

## Mobile Integration

### Android Integration

Integrate Summon with Android for mobile applications:

```kotlin
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import code.yousef.summon.renderToString

class SummonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Render to HTML string
        val html = renderToString {
            MobileApp()
        }
        
        // Create a WebView to display the HTML
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        
        // Add JavaScript interface
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
        
        // Load the HTML
        webView.loadDataWithBaseURL(
            "file:///android_asset/",
            """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Summon on Android</title>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    body { font-family: 'Roboto', sans-serif; margin: 0; padding: 0; }
                </style>
            </head>
            <body>
                $html
                <script>
                    // Bridge between Kotlin and JavaScript
                    window.androidBridge = {
                        callKotlin: function(methodName, args) {
                            Android.callFromJs(methodName, JSON.stringify(args));
                        }
                    };
                </script>
            </body>
            </html>
            """.trimIndent(),
            "text/html",
            "UTF-8",
            null
        )
        
        // Set the WebView as the content view
        setContentView(webView)
    }
    
    // JavaScript interface class
    class WebAppInterface(private val activity: SummonActivity) {
        @JavascriptInterface
        fun callFromJs(methodName: String, args: String) {
            // Handle calls from JavaScript
            println("Call from JS: $methodName, args: $args")
            
            // Example: Show a native Android toast
            if (methodName == "showToast") {
                activity.runOnUiThread {
                    Toast.makeText(activity, args, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
```

## Isomorphic Apps (Server + Client)

Create an isomorphic application that renders on the server and hydrates on the client:

```kotlin
// src/commonMain/kotlin/SummonApp.kt
@Composable
fun SummonApp() {
    var count by remember { mutableStateOf(0) }
    
    Div(
        modifier = Modifier.padding(16.px)
    ) {
        Text("Counter: $count")
        
        Button(
            text = "Increment",
            onClick = { count++ }
        )
    }
}

// src/jvmMain/kotlin/Server.kt
import code.yousef.summon.renderToString
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                // Server-side rendering
                val html = renderToString {
                    SummonApp()
                }
                
                // Send hydration-ready HTML
                call.respondText(
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <title>Isomorphic Summon App</title>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    </head>
                    <body>
                        <div id="app">$html</div>
                        <script src="/js/app.js"></script>
                    </body>
                    </html>
                    """.trimIndent(),
                    ContentType.Text.Html
                )
            }
            
            get("/js/app.js") {
                // Serve the compiled JS
                call.respondBytes(
                    this::class.java.classLoader.getResource("app.js")?.readBytes() ?: ByteArray(0),
                    ContentType.Text.JavaScript
                )
            }
        }
    }.start(wait = true)
}

// src/jsMain/kotlin/Client.kt
import code.yousef.summon.hydrate
import kotlinx.browser.document

fun main() {
    // Get the container with server-rendered content
    val container = document.getElementById("app") ?: error("Container not found")
    
    // Hydrate the app (reuse server-rendered DOM)
    hydrate(container) {
        SummonApp()
    }
}
```

## Using Summon with REST APIs

Integrate Summon with a REST API:

```kotlin
import code.yousef.summon.core.*
import code.yousef.summon.components.*
import code.yousef.summon.state.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

// Data class for a typical API response
@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String
)

@Composable
fun UserList() {
    // State for users and loading state
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Fetch data when component is first rendered
    onMount {
        try {
            isLoading = true
            error = null
            
            // Use a coroutine to fetch data
            lifecycleCoroutineScope().launch {
                try {
                    // Fetch users from API
                    val response = fetchUsers()
                    users = response
                    error = null
                } catch (e: Exception) {
                    error = "Failed to load users: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }
    
    // UI rendering
    Div(
        modifier = Modifier.padding(16.px)
    ) {
        Text(
            text = "User List",
            modifier = Modifier
                .fontSize(24.px)
                .fontWeight(700)
                .marginBottom(16.px)
        )
        
        when {
            isLoading -> {
                // Loading state
                Text("Loading users...")
            }
            
            error != null -> {
                // Error state
                Div(
                    modifier = Modifier
                        .padding(16.px)
                        .backgroundColor("#ffebee")
                        .color("#c62828")
                        .borderRadius(4.px)
                ) {
                    Text(error ?: "Unknown error")
                    
                    Button(
                        text = "Retry",
                        onClick = {
                            // Trigger a refetch
                            isLoading = true
                            lifecycleCoroutineScope().launch {
                                try {
                                    users = fetchUsers()
                                    error = null
                                } catch (e: Exception) {
                                    error = "Failed to load users: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.marginTop(8.px)
                    )
                }
            }
            
            users.isEmpty() -> {
                // Empty state
                Text("No users found.")
            }
            
            else -> {
                // Data display
                for (user in users) {
                    Div(
                        modifier = Modifier
                            .padding(8.px)
                            .marginBottom(8.px)
                            .border(1.px, "#e0e0e0")
                            .borderRadius(4.px)
                    ) {
                        Text(
                            text = user.name,
                            modifier = Modifier
                                .fontWeight(700)
                                .marginBottom(4.px)
                        )
                        Text(
                            text = user.email,
                            modifier = Modifier
                                .fontSize(14.px)
                                .color("#666666")
                        )
                    }
                }
            }
        }
    }
    
    // Function to fetch users from an API
    suspend fun fetchUsers(): List<User> = coroutineScope {
        // This would typically be a real API call
        // Simulating a network request with delay
        delay(1000)
        
        // Sample data
        listOf(
            User(1, "John Doe", "john@example.com"),
            User(2, "Jane Smith", "jane@example.com"),
            User(3, "Bob Johnson", "bob@example.com")
        )
    }
} 