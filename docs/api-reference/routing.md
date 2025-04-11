# Routing API Reference

This document provides detailed information about the routing APIs in the Summon library.

## Table of Contents

- [Router](#router)
- [Route](#route)
- [RouteParams](#routeparams)
- [Link](#link)
- [RouteGuard](#routeguard)
- [File-Based Routing](#file-based-routing)
- [PlatformAdapters](#platformadapters)
- [Advanced Configuration](#advanced-configuration)

---

## Router

The `Router` class is the main entry point for defining and managing routes in Summon.

### Class Definition

```kotlin
package code.yousef.summon.routing

class Router private constructor(
    private val routes: List<Route>,
    private val notFoundRoute: Route? = null,
    private val config: RouterConfig = RouterConfig()
) {
    companion object {
        val current: Router?
        
        // Current location
        val location: Location
            get() = // Current location
            
        // Navigation methods
        fun navigate(path: String, options: NavigationOptions = NavigationOptions())
        fun replace(path: String, options: NavigationOptions = NavigationOptions())
        fun back()
        fun forward()
        
        // Location observers
        fun addLocationObserver(observer: (Location) -> Unit): Subscription
    }
    
    // Factory method
    class Builder {
        fun route(path: String, component: @Composable () -> Unit): Builder
        fun route(path: String, guard: RouteGuard, component: @Composable () -> Unit): Builder
        fun route(path: String, guards: List<RouteGuard>, component: @Composable () -> Unit): Builder
        fun route(path: String, builder: Builder.() -> Unit): Builder
        fun notFound(component: @Composable () -> Unit): Builder
        fun build(): Router
    }
    
    // Create a router instance
    fun withBrowserAdapter(): BrowserRouter
    fun withServerAdapter(path: String): ServerRouter
    fun withCustomAdapter(adapter: RouterAdapter): CustomRouter
}

// Helper function to create a router
fun router(builder: Router.Builder.() -> Unit): Router

// Navigation options
data class NavigationOptions(
    val replaceState: Boolean = false,
    val scrollToTop: Boolean = true,
    val state: Map<String, Any> = emptyMap()
)

// Location information
data class Location(
    val path: String,
    val queryParams: Map<String, String>,
    val hash: String,
    val state: Map<String, Any>?
)
```

### Description

The `Router` class manages navigation and rendering of the current route. It provides a fluent API for defining routes and their associated components.

### Example

```kotlin
// Define routes
val router = router {
    route("/") { HomePage() }
    route("/about") { AboutPage() }
    
    // Route with parameters
    route("/user/{id}") { UserPage() }
    
    // Nested routes
    route("/admin") {
        route("/dashboard") { AdminDashboardPage() }
        route("/users") { AdminUsersPage() }
    }
    
    // Route with guards
    route("/profile", authGuard) { ProfilePage() }
    
    // Catch-all for 404
    notFound { NotFoundPage() }
}

// Initialize the router
val browserRouter = router.withBrowserAdapter()

// Render the router
browserRouter.renderIn(document.getElementById("app")!!)

// Navigation
Button(
    text = "Go to About",
    onClick = {
        Router.navigate("/about")
    }
)
```

---

## Route

The `Route` class represents a single route in the application.

### Class Definition

```kotlin
package code.yousef.summon.routing

class Route(
    val path: String,
    val component: @Composable () -> Unit,
    val guards: List<RouteGuard> = emptyList(),
    val children: List<Route> = emptyList()
) {
    companion object {
        val current: Route
            get() = // Current active route
    }
    
    // Check if the route matches a path
    fun matches(path: String): Boolean
    
    // Extract parameters from a path
    fun extractParams(path: String): RouteParams?
}
```

### Description

A `Route` represents a path pattern and its associated component. It can also contain child routes for nested routing.

### Example

```kotlin
// Direct instantiation (not typically used)
val homeRoute = Route("/", { HomePage() })
val userRoute = Route("/user/{id}", { UserPage() })

// Checking for match
val matches = userRoute.matches("/user/123") // true
val params = userRoute.extractParams("/user/123") // {id=123}

// Access current route
val currentRoute = Route.current
if (currentRoute.path.startsWith("/admin")) {
    // Do something for admin routes
}
```

---

## RouteParams

The `RouteParams` class holds the parameters extracted from the URL path.

### Class Definition

```kotlin
package code.yousef.summon.routing

class RouteParams(val params: Map<String, String>) {
    companion object {
        val current: RouteParams
            get() = // Current route parameters
    }
    
    // Access parameters
    operator fun get(name: String): String?
    
    // Type conversion utilities
    fun getInt(name: String): Int?
    fun getLong(name: String): Long?
    fun getBoolean(name: String): Boolean?
    fun getFloat(name: String): Float?
    fun getDouble(name: String): Double?
}
```

### Description

`RouteParams` provides access to path parameters from dynamic route segments.

### Example

```kotlin
// In a component
@Composable
fun UserPage() {
    // Get route parameters
    val params = RouteParams.current
    val userId = params["id"] ?: "unknown"
    
    // Use parameters
    Column {
        Text("User ID: $userId")
        
        // Convert to type
        val userIdInt = params.getInt("id")
        if (userIdInt != null) {
            Text("User ID as Int: $userIdInt")
        }
    }
}
```

---

## Link

The `Link` component creates navigation links in the application.

### Component Definition

```kotlin
package code.yousef.summon.routing

@Composable
fun Link(
    text: String,
    href: String,
    target: String = "_self",
    modifier: Modifier = Modifier,
    onClick: ((Event) -> Unit)? = null
)
```

### Description

`Link` renders an anchor element that integrates with the router. Clicking the link will navigate to the specified path using the router's navigation mechanism instead of causing a full page reload.

### Example

```kotlin
Link(
    text = "Go to About",
    href = "/about",
    modifier = Modifier
        .color("#0077cc")
        .textDecoration(TextDecoration.None)
        .hover {
            textDecoration(TextDecoration.Underline)
        }
)

// With custom click handler
Link(
    text = "Profile",
    href = "/profile",
    onClick = { event ->
        // Check if user is logged in
        if (!isLoggedIn) {
            event.preventDefault()
            showLoginPrompt()
        }
    }
)
```

---

## File-Based Routing

The file-based routing system provides automatic route discovery based on file paths.

### PageLoader

The `PageLoader` class handles automatic discovery and registration of page components.

```kotlin
package code.yousef.summon.routing

object PageLoader {
    /**
     * Automatically register all pages from the pages directory structure.
     * The registration is handled by generated code that maps file paths to route paths.
     */
    fun registerPages(registry: PageRegistry)
    
    /**
     * Create a router with all registered pages.
     */
    fun createRouter(): Router
    
    /**
     * Maps a file path to its corresponding route path using Next.js conventions.
     */
    fun filePathToRoutePath(filePath: String): String
}
```

### PageRegistry

The `PageRegistry` interface provides registration of pages discovered at build time.

```kotlin
package code.yousef.summon.routing

typealias PageFactory = @Composable (params: RouteParams) -> Unit

interface PageRegistry {
    /**
     * Register a page for a specific path.
     */
    fun registerPage(pagePath: String, pageFactory: PageFactory)

    /**
     * Register a special 404 Not Found page.
     */
    fun registerNotFoundPage(pageFactory: PageFactory)

    /**
     * Get all registered page routes and their factories.
     */
    fun getPages(): Map<String, PageFactory>

    /**
     * Get the registered not found page factory if available.
     */
    fun getNotFoundPage(): PageFactory?
    
    /**
     * Normalize a file path to a route path.
     */
    fun normalizePath(path: String): String
}
```

### Page Discovery Plugin

In a production environment, the page discovery is handled by a Gradle plugin configured in your build script:

```kotlin
plugins {
    kotlin("multiplatform")
    id("code.yousef.summon.page-discovery")
}

// Configure the page discovery plugin
summonPages {
    // Source directory for page files
    pagesDirectory = "src/commonMain/kotlin/code/yousef/summon/routing/pages"
    
    // Output directory for generated code
    outputDirectory = "build/generated/source/summon"
    
    // Options for page file pattern matching
    options {
        // File extensions to consider as page files
        pageExtensions = listOf(".kt", ".page.kt")
        
        // Directories to exclude
        excludeDirectories = listOf("components", "layouts", "lib")
    }
}
```

### FileBasedRouter

The `FileBasedRouter` class implements the `Router` interface for file-based routing:

```kotlin
expect class FileBasedRouter() : Router {
    /**
     * Load pages from the file system.
     */
    fun loadPages()
}

/**
 * Creates a file-based router.
 */
expect fun createFileBasedRouter(): Router

/**
 * Creates a file-based router for the server with a specific path.
 */
expect fun createFileBasedServerRouter(path: String): Router
```

### Example

```kotlin
// Create a router with auto-discovered pages
val router = PageLoader.createRouter()

// Use the router in your application
SummonApp {
    router.create()
}
```

---

## PlatformAdapters

Summon provides platform-specific adapters for the router.

### Browser Adapter

```kotlin
package code.yousef.summon.routing

class BrowserRouter(private val router: Router) {
    // Render to a DOM element
    fun renderIn(element: Element)
    
    // Configure hash mode
    fun useHashMode(enable: Boolean = true): BrowserRouter
    
    // Configure base path
    fun setBasePath(path: String): BrowserRouter
}
```

### Server Adapter

```kotlin
package code.yousef.summon.routing

class ServerRouter(private val router: Router, private val path: String) {
    // Render to a string
    fun renderToString(): String
    
    // Render with hydration support
    fun renderToStringWithHydration(): String
    
    // Configure base path
    fun setBasePath(path: String): ServerRouter
}
```

### Description

Platform adapters connect the router to platform-specific APIs for rendering and navigation.

### Example

```kotlin
// Browser (JS)
val router = router {
    route("/") { HomePage() }
    route("/about") { AboutPage() }
}

val browserRouter = router.withBrowserAdapter()
    .useHashMode() // Use hash URLs (#/path) instead of normal paths
    .setBasePath("/app") // Set base path for all routes

browserRouter.renderIn(document.getElementById("app")!!)

// Server (JVM)
val router = router {
    route("/") { HomePage() }
    route("/about") { AboutPage() }
}

val path = request.getPathInfo() // Get path from request
val serverRouter = router.withServerAdapter(path)
val html = serverRouter.renderToString()

// Send HTML as response
response.writer.write(html)
```

---

## Advanced Configuration

Advanced configuration options for the router.

### RouterConfig

```kotlin
package code.yousef.summon.routing

class RouterConfig {
    // Base path for all routes
    var basePath: String = ""
    
    // Use hash mode for URLs
    var useHashMode: Boolean = false
    
    // Scroll behavior after navigation
    var scrollBehavior: ScrollBehavior = ScrollBehavior.AUTO
    
    // Case sensitivity for route matching
    var caseSensitive: Boolean = false
}

enum class ScrollBehavior {
    AUTO, // Automatic scrolling to top on navigation
    MANUAL, // Manual scrolling control
    PRESERVE, // Preserve scroll position
    SMOOTH // Smooth scrolling to top
}
```

### Route State Management

```kotlin
package code.yousef.summon.routing

class RouteState<T>(
    private val routePath: String,
    private val initialValue: T
) {
    // Get the state for the current route instance, or create new if not exists
    fun getOrCreate(factory: () -> T): T
    
    // Get the state for the current route instance, or null if not exists
    fun get(): T?
    
    // Update the state for the current route instance
    fun update(value: T)
    
    // Clear the state
    fun clear()
}
```

### Description

These APIs provide advanced configuration options and state management tied to routes.

### Example

```kotlin
// Configure the router
val router = router {
    // Custom configuration
    config.basePath = "/app"
    config.useHashMode = true
    config.scrollBehavior = ScrollBehavior.SMOOTH
    config.caseSensitive = true
    
    // Define routes
    route("/") { HomePage() }
    route("/about") { AboutPage() }
}

// Route-specific state
class SearchState(
    var query: String = "",
    var results: List<String> = emptyList()
)

val searchState = RouteState<SearchState>("/search")

@Composable
fun SearchPage() {
    // Get or create state for this route
    val state = searchState.getOrCreate { SearchState() }
    
    Column {
        TextField(
            value = state.query,
            onValueChange = { 
                state.query = it
                // Perform search and update results
                state.results = performSearch(it)
            },
            placeholder = "Search..."
        )
        
        // Show results
        for (result in state.results) {
            Text(result)
        }
    }
}
``` 