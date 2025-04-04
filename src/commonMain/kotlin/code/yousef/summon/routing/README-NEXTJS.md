# Next.js Style Router for Summon

This implements a Next.js style file-based routing system for the Summon UI library.

## Features

- **File-based routing**: Routes are automatically created based on file structure
- **Dynamic routes**: Support for dynamic segments with `[param]` syntax
- **Catch-all routes**: Support for catch-all routes with `[...param]` syntax
- **Nested routes**: Directory structure reflects URL structure
- **Index routes**: Special handling for index/home pages
- **404 pages**: Custom 404 Not Found pages

## File Naming Conventions

Routes are automatically created based on the file structure in the `/pages` directory:

| File Path                  | URL Route          | Notes                         |
|----------------------------|--------------------|-------------------------------|
| `/pages/Index.kt`          | `/`                | Home/index route              |
| `/pages/About.kt`          | `/about`           | Standard route                |
| `/pages/users/Profile.kt`  | `/users/profile`   | Nested route                  |
| `/pages/blog/[id].kt`      | `/blog/:id`        | Dynamic parameter route       |
| `/pages/blog/[...slug].kt` | `/blog/*`          | Catch-all route               |
| `/pages/404.kt`            | N/A                | 404 Not Found page            |

## Page Components

Each page component should implement the `Composable` interface and provide a factory function that accepts `RouteParams`:

```kotlin
class ProfilePage : Composable {
    override fun <T> compose(receiver: T): T {
        // Implement your page UI here
    }
    
    companion object {
        fun create(params: RouteParams): Composable {
            return ProfilePage()
        }
    }
}
```

For dynamic routes, you can access route parameters from the `params` object:

```kotlin
class BlogPost(private val postId: String?) : Composable {
    override fun <T> compose(receiver: T): T {
        // Use postId in your UI
    }
    
    companion object {
        fun create(params: RouteParams): Composable {
            val postId = params["id"]
            return BlogPost(postId)
        }
    }
}
```

## Usage Example

### Manually Creating a Router

```kotlin
// Create a router using the builder DSL
val router = createPageRouter {
    page("/", HomePageComponent::create)
    page("/about", AboutPageComponent::create)
    page("/users/profile", ProfilePageComponent::create)
    page("/blog/:id", BlogPostComponent::create)
    notFound(NotFoundPageComponent::create)
}

// Use the router in your app
router.compose(document.getElementById("root"))

// Navigate programmatically
router.navigate("/about")
```

### Automatic Page Discovery

In a real implementation, pages would be discovered automatically from the file system.
This would be implemented using reflection or code generation:

```kotlin
// The PageLoader would scan the file system and register all pages
val router = PageLoader.createRouter()

// Use the router in your app
router.compose(document.getElementById("root"))
```

## Implementation Details

This router implementation:

1. Scans the `/pages` directory structure
2. Converts file paths to route paths
3. Creates a mapping of routes to page components
4. Handles navigation between pages

## Integration with Browser History

When using in a browser environment, the router integrates with the browser's history API for seamless navigation:

```kotlin
// In JS code
window.onpopstate = { event ->
    val path = window.location.pathname
    router.navigate(path, false)
}
```

## Adding Navigation Links

Use the `NavLink` component to create navigation links:

```kotlin
NavLink(
    to = "/about",
    text = "About Us",
    className = "nav-link",
    activeClassName = "active"
).compose(this)
```

The `activeClassName` is automatically applied when the current route matches the link's destination. 