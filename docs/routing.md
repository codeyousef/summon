# Routing

Summon provides flexible routing patterns that work across platforms, allowing you to create navigation in both browser and JVM applications using the standalone implementation.

## Overview

Routing in the standalone Summon implementation is achieved through:

1. **Manual Route Handling**: Define routes explicitly using conditional rendering
2. **Hash-Based Routing**: Use URL hash fragments for client-side navigation
3. **Server-Side Routing**: Handle different paths on the server side
4. **Simple Navigation**: Create navigation using basic HTML links and JavaScript

## Client-Side Routing (JavaScript)

### Hash-Based Routing

The simplest approach for client-side routing uses URL hash fragments:

```kotlin
// Include the standalone Summon implementation (as shown in quickstart.md)

// Router state
data class Router(
    var currentRoute: String = "home"
)

val router = Router()

// Page components
@Composable
fun HomePage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Home Page", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Navigation()
    }
}

@Composable
fun AboutPage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("About Page", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("Learn more about our application.") +
        Navigation()
    }
}

@Composable
fun UserPage(userId: String): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("User Profile", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("User ID: $userId") +
        Navigation()
    }
}

@Composable
fun NotFoundPage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("404 - Page Not Found", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("The page you're looking for doesn't exist.") +
        Navigation()
    }
}

// Navigation component
@Composable
fun Navigation(): String {
    return Row(
        modifier = Modifier().gap("16px")
    ) {
        Button(
            text = "Home",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("navigateTo('home')")
        ) + Button(
            text = "About",
            modifier = Modifier()
                .backgroundColor("#6c757d")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("navigateTo('about')")
        ) + Button(
            text = "User Profile",
            modifier = Modifier()
                .backgroundColor("#28a745")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("navigateTo('user/123')")
        )
    }
}

// Main router component
@Composable
fun App(): String {
    return when (router.currentRoute) {
        "home" -> HomePage()
        "about" -> AboutPage()
        else -> {
            when {
                router.currentRoute.startsWith("user/") -> {
                    val userId = router.currentRoute.substringAfter("user/")
                    UserPage(userId)
                }
                else -> NotFoundPage()
            }
        }
    }
}
```

### JavaScript Setup for Hash Routing

```kotlin
// In your main.kt file
fun main() {
    val root = kotlinx.browser.document.getElementById("root")
        ?: error("Root element not found")
    
    // Initial render
    updateUI()
    
    // Add JavaScript for routing
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <script>
        // Navigation function
        function navigateTo(route) {
            window.location.hash = route;
        }
        
        // Update UI when route changes
        function updateUI() {
            const hash = window.location.hash.substring(1) || 'home';
            // Update Kotlin router state (simplified)
            // In a real implementation, you'd call back into Kotlin
            document.getElementById('root').innerHTML = getPageForRoute(hash);
        }
        
        // Listen for hash changes
        window.addEventListener('hashchange', updateUI);
        
        // Simple route mapping (in a real app, this would call Kotlin functions)
        function getPageForRoute(route) {
            // This is a simplified example - in practice you'd integrate with your Kotlin router
            console.log('Navigating to:', route);
            return '<div>Route: ' + route + '</div>';
        }
        </script>
    """)
}

// Helper function to update UI
fun updateUI() {
    val root = kotlinx.browser.document.getElementById("root")
        ?: error("Root element not found")
    
    // Get current hash
    val hash = kotlinx.browser.window.location.hash.removePrefix("#").ifEmpty { "home" }
    router.currentRoute = hash
    
    // Re-render
    root.innerHTML = App()
}
```

## Server-Side Routing (JVM)

For JVM applications, you can handle routing server-side by examining the request path:

### Basic Server-Side Routing

```kotlin
// Route handler function
fun handleRequest(path: String): String {
    return when (path) {
        "/" -> renderPage { HomePage() }
        "/about" -> renderPage { AboutPage() }
        "/contact" -> renderPage { ContactPage() }
        else -> {
            when {
                path.startsWith("/user/") -> {
                    val userId = path.substringAfter("/user/")
                    renderPage { UserPage(userId) }
                }
                else -> renderPage { NotFoundPage() }
            }
        }
    }
}

// Helper function to render a page with HTML wrapper
fun renderPage(pageContent: () -> String): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>My Summon App</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { font-family: Arial, sans-serif; margin: 0; padding: 0; }
            </style>
        </head>
        <body>
            ${pageContent()}
        </body>
        </html>
    """.trimIndent()
}

// Page components (same as client-side but without navigation JavaScript)
@Composable
fun HomePage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Home Page", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("Welcome to our application!") +
        ServerNavigation()
    }
}

@Composable
fun ServerNavigation(): String {
    return Div(
        modifier = Modifier().gap("16px").style("margin-top", "20px")
    ) {
        """
        <a href="/" style="display: inline-block; background-color: #0077cc; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; margin-right: 8px;">Home</a>
        <a href="/about" style="display: inline-block; background-color: #6c757d; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px; margin-right: 8px;">About</a>
        <a href="/user/123" style="display: inline-block; background-color: #28a745; color: white; padding: 8px 16px; text-decoration: none; border-radius: 4px;">User Profile</a>
        """
    }
}
```

### Ktor Integration

```kotlin
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(handleRequest("/"), ContentType.Text.Html)
        }
        
        get("/about") {
            call.respondText(handleRequest("/about"), ContentType.Text.Html)
        }
        
        get("/user/{id}") {
            val userId = call.parameters["id"] ?: "unknown"
            call.respondText(handleRequest("/user/$userId"), ContentType.Text.Html)
        }
        
        // Catch-all route
        get("/{...}") {
            val path = call.request.uri
            call.respondText(handleRequest(path), ContentType.Text.Html)
        }
    }
}
```

### Spring Boot Integration

```kotlin
import org.springframework.web.bind.annotation.*
import org.springframework.http.MediaType

@RestController
class PageController {
    
    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun home(): String = handleRequest("/")
    
    @GetMapping("/about", produces = [MediaType.TEXT_HTML_VALUE])
    fun about(): String = handleRequest("/about")
    
    @GetMapping("/user/{id}", produces = [MediaType.TEXT_HTML_VALUE])
    fun userProfile(@PathVariable id: String): String = handleRequest("/user/$id")
    
    @GetMapping("/{path:.*}", produces = [MediaType.TEXT_HTML_VALUE])
    fun catchAll(@PathVariable path: String): String = handleRequest("/$path")
}
```

## Route Parameters

In the standalone implementation, you can extract parameters from URLs using simple string operations:

### Client-Side Parameter Extraction

```kotlin
// Parameter extraction functions
fun extractRouteParams(route: String, pattern: String): Map<String, String> {
    val params = mutableMapOf<String, String>()
    val routeParts = route.split("/")
    val patternParts = pattern.split("/")
    
    for (i in patternParts.indices) {
        if (i < routeParts.size && patternParts[i].startsWith("{") && patternParts[i].endsWith("}")) {
            val paramName = patternParts[i].removeSurrounding("{", "}")
            params[paramName] = routeParts[i]
        }
    }
    
    return params
}

// Enhanced router with parameter support
data class RouterState(
    var currentRoute: String = "home",
    var params: Map<String, String> = emptyMap()
)

val router = RouterState()

// Route handling with parameters
@Composable
fun App(): String {
    return when {
        router.currentRoute == "home" -> HomePage()
        router.currentRoute == "about" -> AboutPage()
        router.currentRoute.startsWith("user/") -> {
            val userId = router.currentRoute.substringAfter("user/")
            UserPage(userId)
        }
        router.currentRoute.startsWith("blog/") -> {
            val pathParts = router.currentRoute.substringAfter("blog/").split("/")
            when (pathParts.size) {
                3 -> {
                    val (year, month, slug) = pathParts
                    BlogPost(year, month, slug)
                }
                else -> NotFoundPage()
            }
        }
        else -> NotFoundPage()
    }
}

// Page components with parameters
@Composable
fun UserPage(userId: String): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("User Profile", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("User ID: $userId", modifier = Modifier().fontSize("18px")) +
        Text("Viewing profile for user $userId") +
        Navigation()
    }
}

@Composable
fun BlogPost(year: String, month: String, slug: String): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Blog Post", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("Date: $year/$month", modifier = Modifier().fontSize("16px")) +
        Text("Slug: $slug", modifier = Modifier().fontSize("16px")) +
        Text("Blog post content for $slug would be displayed here.") +
        Navigation()
    }
}
```

### Server-Side Parameter Extraction

```kotlin
// Server-side parameter handling
fun handleRequestWithParams(path: String): String {
    return when {
        path == "/" -> renderPage { HomePage() }
        path == "/about" -> renderPage { AboutPage() }
        path.startsWith("/user/") -> {
            val userId = path.substringAfter("/user/")
            renderPage { UserPage(userId) }
        }
        path.startsWith("/blog/") -> {
            val blogPath = path.substringAfter("/blog/")
            val parts = blogPath.split("/")
            when (parts.size) {
                3 -> {
                    val (year, month, slug) = parts
                    renderPage { BlogPost(year, month, slug) }
                }
                else -> renderPage { NotFoundPage() }
            }
        }
        path.startsWith("/product/") -> {
            val productPath = path.substringAfter("/product/")
            val parts = productPath.split("?")
            val productId = parts[0]
            val queryParams = if (parts.size > 1) parseQueryString(parts[1]) else emptyMap()
            renderPage { ProductPage(productId, queryParams) }
        }
        else -> renderPage { NotFoundPage() }
    }
}

// Query parameter parsing
fun parseQueryString(query: String): Map<String, String> {
    return query.split("&")
        .mapNotNull { param ->
            val parts = param.split("=", limit = 2)
            if (parts.size == 2) parts[0] to parts[1] else null
        }
        .toMap()
}

@Composable
fun ProductPage(productId: String, queryParams: Map<String, String>): String {
    val color = queryParams["color"] ?: "default"
    val size = queryParams["size"] ?: "medium"
    
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Product Details", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("Product ID: $productId") +
        Text("Color: $color") +
        Text("Size: $size") +
        ServerNavigation()
    }
}
```

## Simple Navigation Patterns

### Link Component for Navigation

Create a simple link component for navigation:

```kotlin
@Composable
fun Link(text: String, href: String, modifier: Modifier = Modifier()): String {
    val linkModifier = modifier
        .color("#0077cc")
        .textDecoration(TextDecoration.Underline)
        .cursor(Cursor.Pointer)
    
    return """<a href="$href" style="${linkModifier.toStyleString()}">$text</a>"""
}

// Usage in components
@Composable
fun HomePage(): String {
    return Column(
        modifier = Modifier().padding(20.px).gap(16.px)
    ) {
        Text("Welcome to the Home Page", modifier = Modifier().fontSize(24.px).fontWeight(FontWeight.Bold)) +
        Link("Go to About Page", "/about") +
        Link("View User Profile", "/user/123") +
        Link("Read Blog", "/blog/2024/01/my-post")
    }
}
```

### JavaScript Navigation Functions

```kotlin
// Add navigation JavaScript
fun addNavigationJS() {
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <script>
        // Client-side navigation for hash routing
        function navigateTo(route) {
            window.location.hash = route;
            updateUI(); // Call your UI update function
        }
        
        // Programmatic navigation with parameters
        function navigateToUser(userId) {
            navigateTo('user/' + userId);
        }
        
        // Navigation with query-like parameters (using hash)
        function navigateToSearch(query, page) {
            navigateTo('search/' + encodeURIComponent(query) + '/' + page);
        }
        
        // Go back function
        function goBack() {
            window.history.back();
        }
        
        // Go forward function
        function goForward() {
            window.history.forward();
        }
        </script>
    """)
}
```

## Query Parameter Simulation

Since we're using a standalone implementation, we can simulate query parameters:

### Client-Side Query Handling

```kotlin
// Simulate query parameters using hash fragments
data class RouteInfo(
    val path: String,
    val params: Map<String, String> = emptyMap()
)

fun parseRoute(hash: String): RouteInfo {
    val parts = hash.split("?")
    val path = parts[0]
    val queryParams = if (parts.size > 1) {
        parts[1].split("&").mapNotNull { param ->
            val keyValue = param.split("=", limit = 2)
            if (keyValue.size == 2) keyValue[0] to keyValue[1] else null
        }.toMap()
    } else emptyMap()
    
    return RouteInfo(path, queryParams)
}

// Enhanced router with query support
var currentRouteInfo = RouteInfo("home")

@Composable
fun SearchPage(): String {
    val query = currentRouteInfo.params["query"] ?: ""
    val page = currentRouteInfo.params["page"]?.toIntOrNull() ?: 1
    
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Search Results", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("Searching for: $query") +
        Text("Page: $page") +
        Text("Results would be displayed here...") +
        Navigation()
    }
}

// Updated navigation with query support
@Composable
fun SearchNavigation(): String {
    return Row(modifier = Modifier().gap("8px")) {
        Button(
            text = "Search 'kotlin'",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .onClick("navigateTo('search?query=kotlin&page=1')")
        ) + Button(
            text = "Search 'programming'",
            modifier = Modifier()
                .backgroundColor("#28a745")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .onClick("navigateTo('search?query=programming&page=2')")
        )
    }
}
```

## Route Protection and Guards

Implement simple route protection using conditional rendering:

```kotlin
// Authentication state (you would manage this with your auth system)
data class AuthState(
    val isAuthenticated: Boolean = false,
    val userRole: String = "guest"
)

var authState = AuthState()

// Protected route helper
@Composable
fun ProtectedRoute(
    requiredRole: String = "user",
    loginRoute: String = "login",
    content: () -> String
): String {
    return when {
        !authState.isAuthenticated -> LoginPrompt(loginRoute)
        authState.userRole != requiredRole && requiredRole != "user" -> UnauthorizedPage()
        else -> content()
    }
}

@Composable
fun LoginPrompt(loginRoute: String): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Please log in to access this page", modifier = Modifier().fontSize("20px")) +
        Button(
            text = "Go to Login",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
                .onClick("navigateTo('$loginRoute')")
        )
    }
}

@Composable
fun UnauthorizedPage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("Unauthorized Access", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("You don't have permission to view this page.") +
        Navigation()
    }
}

// Usage in router
@Composable
fun App(): String {
    return when (router.currentRoute) {
        "home" -> HomePage()
        "about" -> AboutPage()
        "login" -> LoginPage()
        "profile" -> ProtectedRoute { ProfilePage() }
        "admin" -> ProtectedRoute("admin") { AdminDashboard() }
        else -> NotFoundPage()
    }
}

@Composable
fun ProfilePage(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("16px")
    ) {
        Text("User Profile", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("Welcome, ${authState.userRole}!") +
        Button(
            text = "Logout",
            modifier = Modifier()
                .backgroundColor("#dc3545")
                .color("white")
                .padding("8px 16px")
                .borderRadius("4px")
                .onClick("logout()")
        ) +
        Navigation()
    }
}
```

## Simple Page Transitions

Add basic CSS transitions for route changes:

```kotlin
// Add transition CSS
fun addTransitionCSS() {
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <style>
        .page-container {
            opacity: 1;
            transition: opacity 0.3s ease-in-out;
        }
        
        .page-container.fade-out {
            opacity: 0;
        }
        
        .slide-in {
            animation: slideIn 0.3s ease-in-out;
        }
        
        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        </style>
    """)
}

// Enhanced navigation with transitions
fun navigateWithTransition(route: String) {
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <script>
        function navigateWithTransition(route) {
            const container = document.getElementById('root');
            container.classList.add('fade-out');
            
            setTimeout(() => {
                navigateTo(route);
                container.classList.remove('fade-out');
                container.classList.add('slide-in');
                
                setTimeout(() => {
                    container.classList.remove('slide-in');
                }, 300);
            }, 300);
        }
        </script>
    """)
}
```

## Complete Working Example

Here's a complete working example combining all the routing concepts:

```kotlin
// Complete routing example
data class AppRouter(
    var currentRoute: String = "home",
    var params: Map<String, String> = emptyMap()
)

val appRouter = AppRouter()

@Composable
fun CompleteApp(): String {
    return Div(
        modifier = Modifier().id("page-container").style("min-height", "100vh")
    ) {
        when (appRouter.currentRoute) {
            "home" -> HomePage()
            "about" -> AboutPage()
            "login" -> LoginPage()
            "profile" -> ProtectedRoute { ProfilePage() }
            "admin" -> ProtectedRoute("admin") { AdminDashboard() }
            else -> {
                when {
                    appRouter.currentRoute.startsWith("user/") -> {
                        val userId = appRouter.currentRoute.substringAfter("user/")
                        ProtectedRoute { UserDetailPage(userId) }
                    }
                    appRouter.currentRoute.startsWith("search") -> {
                        val query = appRouter.params["query"] ?: ""
                        val page = appRouter.params["page"]?.toIntOrNull() ?: 1
                        SearchResultsPage(query, page)
                    }
                    else -> NotFoundPage()
                }
            }
        }
    }
}

// Main function with full routing setup
fun main() {
    val root = kotlinx.browser.document.getElementById("root")
        ?: error("Root element not found")
    
    // Add CSS and JavaScript
    addTransitionCSS()
    addNavigationJS()
    
    // Initial render
    updateUI()
    
    // Setup hash change listener
    kotlinx.browser.window.addEventListener("hashchange", { updateUI() })
}

fun updateUI() {
    val root = kotlinx.browser.document.getElementById("root")
        ?: error("Root element not found")
    
    val hash = kotlinx.browser.window.location.hash.removePrefix("#").ifEmpty { "home" }
    val routeInfo = parseRoute(hash)
    
    appRouter.currentRoute = routeInfo.path
    appRouter.params = routeInfo.params
    
    root.innerHTML = CompleteApp()
}
```

This standalone routing implementation provides:

✅ **Client-side routing** with hash-based navigation  
✅ **Server-side routing** with path-based routing  
✅ **Route parameters** and query parameter simulation  
✅ **Route protection** with authentication guards  
✅ **Simple transitions** with CSS animations  
✅ **No external dependencies** - works immediately  

For more advanced routing features, consider integrating with dedicated routing libraries or frameworks specific to your platform. 