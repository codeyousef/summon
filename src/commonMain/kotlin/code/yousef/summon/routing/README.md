# Summon Router System

A flexible, lightweight routing system for the Summon UI library that works across both JS and JVM platforms.

## Key Components

### `Route`
Defines a route pattern and its associated component. Patterns can include dynamic parameters using the `:paramName` syntax.

```kotlin
// Static route
val homeRoute = Route("/") { _ -> HomeComponent() }

// Route with parameters
val userRoute = Route("/user/:id") { params -> UserProfileComponent(params["id"]) }
```

### `RouteParams`
Container for route parameters parsed from a URL path.

```kotlin
// Access parameters
val userId = params["id"]

// Access with default value
val page = params.getOrDefault("page", "1")
```

### `Router`
Main router component that manages routes and handles navigation.

```kotlin
// Create a router with routes
val router = Router(
    routes = listOf(homeRoute, aboutRoute, userRoute),
    notFoundComponent = { _ -> NotFoundComponent() }
)

// Navigate to a route
router.navigate("/user/123")
```

### `NavLink`
Navigation link component with active state awareness.

```kotlin
// Create a navigation link
NavLink(
    to = "/user/123",
    text = "User Profile",
    className = "nav-link",
    activeClassName = "active"
)
```

### `Redirect`
Component for declarative navigation redirects.

```kotlin
// Redirect to another route
Redirect.to("/login")
```

## Platform-Specific Features

### JS Platform

```kotlin
// Set up router for browser
router.setupForBrowser()

// Create a browser router
val router = createBrowserRouter {
    route("/") { _ -> HomeComponent() }
    route("/about") { _ -> AboutComponent() }
}
```

### JVM Platform

```kotlin
// Set up router for server
router.setupForServer("session-123")

// Create a server router
val router = createServerRouter("session-123") {
    route("/") { _ -> HomeComponent() }
    route("/about") { _ -> AboutComponent() }
}
```

## Example Usage

```kotlin
@Composable
fun RouterExample() {
    // Create a router using the DSL
    val router = createRouter {
        route("/") { _ -> HomeComponent() }
        route("/about") { _ -> AboutComponent() }
        route("/user/:id") { params -> UserProfileComponent(params["id"]) }
        setNotFound { _ -> NotFoundComponent() }
    }
    
    // Use the router component with an initial path
    RouterComponent(
        router = router,
        initialPath = "/"
    )
}

// Usage in a parent component
@Composable
fun App() {
    Column {
        // Navigation menu
        Row {
            NavLink(to = "/", content = { Text("Home") })
            NavLink(to = "/about", content = { Text("About") })
        }
        
        // Router content
        RouterExample()
    }
}
```

## Integration with HTML

```html
<!DOCTYPE html>
<html>
<head>
    <title>Summon Router Example</title>
    <style>
        .nav-link { margin: 0 10px; }
        .nav-link.active { font-weight: bold; }
    </style>
</head>
<body>
    <div id="root"></div>
    <script src="app.js"></script>
</body>
</html>
``` 