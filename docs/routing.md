# Routing

Summon provides a powerful routing system that works across platforms, allowing you to create navigation in both browser and JVM applications.

## Basic Setup

### Setting Up Routes

First, create your page components:

```kotlin
// HomePage.kt
class HomePage : Composable {
    override fun render() {
        Column {
            Text("Home Page")
            Link(text = "Go to About", href = "/about")
        }
    }
}

// AboutPage.kt
class AboutPage : Composable {
    override fun render() {
        Column {
            Text("About Page")
            Link(text = "Go to Home", href = "/")
        }
    }
}
```

Then, register your routes:

```kotlin
import code.yousef.summon.routing.*

val router = Router {
    route("/", HomePage())
    route("/about", AboutPage())
    // Route with parameters
    route("/user/{id}", UserPage())
    // Nested routes
    route("/admin") {
        route("/dashboard", AdminDashboard())
        route("/users", AdminUsers())
    }
    // Catch-all route for 404 pages
    notFound(NotFoundPage())
}
```

## JS Platform Setup

For browser applications, set up the router in your main function:

```kotlin
import code.yousef.summon.routing.*
import kotlinx.browser.document

fun main() {
    val container = document.getElementById("app") ?: error("Container not found")
    
    // Initialize the router with a browser adapter
    val browserRouter = router.withBrowserAdapter()
    
    // Render the current route in the container
    browserRouter.renderIn(container)
}
```

The browser adapter automatically handles:
- URL changes
- History API integration
- Back/forward navigation
- Link click interception

## JVM Platform Setup

For JVM applications, you can use the router for server-side rendering:

```kotlin
import code.yousef.summon.routing.*

fun handleRequest(path: String): String {
    // Create a server adapter that responds to a specific path
    val serverRouter = router.withServerAdapter(path)
    
    // Render the page to HTML
    return serverRouter.renderToString()
}
```

## Route Parameters

You can define routes with parameters:

```kotlin
route("/user/{id}", UserPage())
```

And access them in your page component:

```kotlin
class UserPage : Composable {
    override fun render() {
        // Get the current route parameters
        val params = RouteParams.current()
        
        // Access the 'id' parameter
        val userId = params["id"]
        
        Column {
            Text("User ID: $userId")
        }
    }
}
```

## Navigation

### Declarative Navigation

Use the `Link` component for declarative navigation:

```kotlin
Link(
    text = "Go to About",
    href = "/about"
)
```

### Programmatic Navigation

Use the `Router` API for programmatic navigation:

```kotlin
import code.yousef.summon.routing.Router

// Navigate to a new route
Button(
    text = "Go to About",
    onClick = { 
        Router.navigate("/about")
    }
)

// Navigate with parameters
Button(
    text = "View User",
    onClick = { 
        Router.navigate("/user/123")
    }
)

// Navigate with query parameters
Button(
    text = "Search",
    onClick = { 
        Router.navigate("/search?query=example&page=1")
    }
)
```

## Query Parameters

Access query parameters in your components:

```kotlin
class SearchPage : Composable {
    override fun render() {
        // Get the current route
        val route = Route.current()
        
        // Access query parameters
        val query = route.queryParams["query"] ?: ""
        val page = route.queryParams["page"]?.toIntOrNull() ?: 1
        
        Column {
            Text("Searching for: $query")
            Text("Page: $page")
        }
    }
}
```

## Guards and Middleware

You can protect routes with guards:

```kotlin
// Define an authentication guard
val authGuard = RouteGuard { next ->
    if (isAuthenticated()) {
        next() // Continue to the route
    } else {
        Router.navigate("/login") // Redirect to login
    }
}

// Apply the guard to routes
route("/dashboard", DashboardPage(), guards = listOf(authGuard))

// Apply to a group of routes
route("/admin", guards = listOf(authGuard)) {
    route("/users", AdminUsersPage())
    route("/settings", AdminSettingsPage())
}
```

## Nested Layouts

You can create nested layouts with shared UI elements:

```kotlin
// Define a layout
class AdminLayout(private val content: Composable) : Composable {
    override fun render() {
        Column {
            // Shared header
            Row {
                Text("Admin Panel")
                Link(text = "Dashboard", href = "/admin/dashboard")
                Link(text = "Users", href = "/admin/users")
                Link(text = "Settings", href = "/admin/settings")
            }
            
            // Render the nested route content
            content.render()
            
            // Shared footer
            Text("© 2023 Admin Panel")
        }
    }
}

// Use the layout in your router
route("/admin") {
    route("/dashboard") { AdminLayout(DashboardPage()).render() }
    route("/users") { AdminLayout(UsersPage()).render() }
    route("/settings") { AdminLayout(SettingsPage()).render() }
}
```

## Lazy Loading Routes

For larger applications, you can lazy-load routes:

```kotlin
// Define a lazy-loaded route
route("/large-page", lazy { 
    // This code only executes when the route is visited
    LargePage() 
})
```

## Advanced Features

### Route-Based State Management

Preserve state between navigation events:

```kotlin
// Create route-specific state
val searchState = RouteState<SearchState>("/search")

class SearchPage : Composable {
    override fun render() {
        // Get or initialize state for this route
        val state = searchState.getOrCreate { SearchState() }
        
        // Use the state
        Text("Search results: ${state.results.size}")
    }
}
```

### Location Observers

React to route changes:

```kotlin
// Add a location observer
Router.addLocationObserver { location ->
    println("Route changed to: ${location.path}")
    
    // Analytics tracking example
    trackPageView(location.path)
}
```

### Custom Router Configuration

Configure advanced router behavior:

```kotlin
val router = Router {
    // Change the base path for all routes
    basePath = "/app"
    
    // Configure hash mode (#) for SPA without server config
    useHashMode = true
    
    // Custom scroll behavior
    scrollBehavior = ScrollBehavior.SMOOTH
    
    // Define routes
    route("/", HomePage())
    route("/about", AboutPage())
}
``` 