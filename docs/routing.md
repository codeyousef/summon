# Routing

Summon provides a powerful routing system that works across platforms, allowing you to create navigation in both browser and JVM applications. The library supports two complementary approaches to routing:

1. **Programmatic Routing**: Define routes explicitly using the Router builder API (covered in this document)
2. **File-Based Routing**: Use a Next.js-style file structure where file paths map to URL routes (see [File-Based Routing](file-based-routing.md))

## Next.js-Style File-Based Routing (Recommended)

Summon now features a complete Next.js-style file-based routing system that automatically discovers page files from your codebase and maps them to routes based on their file paths.

```kotlin
// Example: Creating a file automatically creates a route
// src/commonMain/kotlin/code/yousef/summon/routing/pages/About.kt -> /about

// Create a router with all auto-discovered pages
val router = PageLoader.createRouter()

// Use the router in your application
SummonApp {
    router.create()
}
```

This approach follows conventions to simplify route creation and is especially useful for larger applications with many routes. For detailed documentation on this approach, see [File-Based Routing](file-based-routing.md).

## Programmatic Routing

### Basic Setup

Setting up routes programmatically using the Router builder API:

### Setting Up Routes

First, create your page components:

```kotlin
// HomePage.kt
import code.yousef.summon.annotation.Composable

@Composable
fun HomePage() {
    Column {
        Text("Home Page")
        Link(text = "Go to About", href = "/about")
    }
}

// AboutPage.kt
import code.yousef.summon.annotation.Composable

@Composable
fun AboutPage() {
    Column {
        Text("About Page")
        Link(text = "Go to Home", href = "/")
    }
}
```

Then, register your routes:

```kotlin
import code.yousef.summon.routing.*

val router = Router {
    route("/", { HomePage() })
    route("/about", { AboutPage() })
    // Route with parameters
    route("/user/{id}", { UserPage() })
    // Nested routes
    route("/admin") {
        route("/dashboard", { AdminDashboard() })
        route("/users", { AdminUsers() })
    }
    // Catch-all route for 404 pages
    notFound({ NotFoundPage() })
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
- Back/forward navigation (v0.2.7+)
- Link click interception
- Browser history popstate events (v0.2.7+)

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
// Single parameter
route("/user/{id}", { UserPage() })

// Multiple parameters
route("/blog/{year}/{month}/{slug}", { BlogPost() })

// Optional parameters
route("/products/{category?}", { ProductList() })

// Wildcard parameters (catch-all)
route("/docs/{...path}", { Documentation() })

// Dynamic parameters (v0.2.7+)
route("/user/:id", { UserPage() })
route("/posts/:category/:slug", { BlogPost() })
```

And access them in your page component:

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.routing.RouteParams

@Composable
fun UserPage() {
    // Get the current route parameters
    val params = RouteParams.current()
    
    // Access the 'id' parameter
    val userId = params["id"]
    
    Column {
        Text("User ID: $userId")
    }
}

@Composable
fun BlogPost() {
    val params = RouteParams.current()
    val year = params["year"]
    val month = params["month"] 
    val slug = params["slug"]
    
    Column {
        Text("Blog Post: $year/$month/$slug")
    }
}

@Composable
fun Documentation() {
    val params = RouteParams.current()
    // For wildcard routes, the parameter contains the remaining path
    val path = params["path"] // e.g., "getting-started/installation"
    
    Column {
        Text("Documentation: /$path")
    }
}
```

### Dynamic Route Patterns

Summon supports various dynamic route patterns:

| Pattern | Example | Matches | Description |
|---------|---------|---------|-------------|
| `{param}` | `/user/{id}` | `/user/123` | Single parameter |
| `{param?}` | `/shop/{category?}` | `/shop` or `/shop/electronics` | Optional parameter |
| `{...param}` | `/files/{...path}` | `/files/docs/readme.md` | Catch-all parameter |
| Multiple | `/posts/{year}/{month}` | `/posts/2024/01` | Multiple parameters |

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
import code.yousef.summon.annotation.Composable
import code.yousef.summon.routing.Route

@Composable
fun SearchPage() {
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

// Apply the guard to a route
route("/profile", authGuard, { ProfilePage() })

// Apply multiple guards
val adminGuard = RouteGuard { next ->
    if (isAdmin()) {
        next()
    } else {
        Router.navigate("/unauthorized")
    }
}

route("/admin/dashboard", listOf(authGuard, adminGuard), { AdminDashboard() })
```

## Route Groups

You can organize routes into logical groups:

```kotlin
Router {
    // Public routes
    route("/", { HomePage() })
    route("/about", { AboutPage() })
    
    // User routes with authentication
    group(authGuard) {
        route("/profile", { ProfilePage() })
        route("/settings", { SettingsPage() })
    }
    
    // Admin routes with both guards
    group(listOf(authGuard, adminGuard)) {
        route("/admin/dashboard", { AdminDashboard() })
        route("/admin/users", { AdminUsers() })
    }
}
```

## Lazy Loading

For better performance, you can lazily load components:

```kotlin
Router {
    route("/", { HomePage() })
    
    // Lazy loaded route
    route("/large-page") {
        LazyComponent { 
            // This will only be loaded when the route is accessed
            LargePage()
        }
    }
}
```

## Route Transitions

You can add transitions between routes:

```kotlin
Router {
    // Apply transitions to all routes
    transitions {
        enter = fadeIn(duration = 300.ms)
        exit = fadeOut(duration = 300.ms)
    }
    
    route("/", { HomePage() })
    route("/about", { AboutPage() })
    
    // Route with custom transition
    route("/special") {
        transition {
            enter = slideIn(direction = SlideDirection.Left, duration = 500.ms)
            exit = slideOut(direction = SlideDirection.Right, duration = 500.ms)
        }
        
        SpecialPage()
    }
}
```

Check out the [API Reference](api-reference/routing.md) for complete details on the routing system.

## File-Based Routing Alternative

Summon also supports a file-based routing approach where the file structure in a `pages` directory automatically maps to URL routes. This is similar to frameworks like Next.js.

For example:
- `/pages/Index.kt` → `/` (home route)
- `/pages/About.kt` → `/about`
- `/pages/users/Profile.kt` → `/users/profile`

This approach follows conventions to simplify route creation and is especially useful for larger applications with many routes.

For detailed documentation on this approach, see [File-Based Routing](file-based-routing.md). 