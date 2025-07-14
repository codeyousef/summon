# File-Based Routing

This guide shows how to implement a Next.js-style file-based routing system using the standalone Summon implementation. While you won't get automatic build-time discovery, you can manually organize your pages by file structure and create a simple convention-based router.

## How it Works

1. **File Organization**: Organize your page components following a consistent file structure
2. **Manual Registration**: Register routes based on your file organization conventions  
3. **Runtime Routing**: Use a simple mapping system to match routes to page components

## File Organization Conventions

Organize your page files following this suggested structure:

| File Path                  | URL Route          | Notes                         |
|----------------------------|--------------------|-------------------------------|
| `/pages/IndexPage.kt`      | `/`                | Home/index route              |
| `/pages/HomePage.kt`       | `/`                | Alternative home route        |
| `/pages/AboutPage.kt`      | `/about`           | Standard route                |
| `/pages/users/ProfilePage.kt` | `/users/profile` | Nested route                  |
| `/pages/blog/PostPage.kt`  | `/blog/post/{id}`  | Dynamic parameter route       |
| `/pages/NotFoundPage.kt`   | `*`                | 404 Not Found page            |

## Creating Page Components

Each page file contains a composable function using the standalone implementation:

```kotlin
// File: src/commonMain/kotlin/pages/AboutPage.kt
// Include the standalone Summon implementation (from quickstart.md)

@Composable
fun AboutPage(): String {
    return Column(
        modifier = Modifier().padding(20.px).gap(16.px)
    ) {
        Text("About Page", modifier = Modifier().fontSize(24.px).fontWeight(FontWeight.Bold)) +
        Text("Learn more about our application.") +
        Text("This page demonstrates file-based routing patterns.") +
        NavigationLinks()
    }
}

@Composable
fun NavigationLinks(): String {
    return Row(modifier = Modifier().gap(16.px).marginTop(20.px)) {
        Button(
            text = "Home",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding(8.px, 16.px)
                .borderRadius(4.px)
                .onClick("navigateTo('home')")
        ) + Button(
            text = "Profile",
            modifier = Modifier()
                .backgroundColor("#28a745")
                .color("white")
                .padding(8.px, 16.px)
                .borderRadius(4.px)
                .onClick("navigateTo('users/profile')")
        )
    }
}
```

## Example Page Files

Here are complete examples of different page types:

### Index/Home Page

```kotlin
// File: src/commonMain/kotlin/pages/IndexPage.kt
@Composable
fun IndexPage(): String {
    return Column(
        modifier = Modifier().padding(20.px).gap(16.px)
    ) {
        Text("Welcome Home", modifier = Modifier().fontSize(28.px).fontWeight(FontWeight.Bold)) +
        Text("This is the home page of our file-based routing example.") +
        
        Row(modifier = Modifier().gap(12.px).marginTop(20.px)) {
            Button(
                text = "About Us",
                modifier = Modifier()
                    .backgroundColor("#0077cc")
                    .color("white")
                    .padding(10.px, 20.px)
                    .borderRadius(4.px)
                    .onClick("navigateTo('about')")
            ) + Button(
                text = "User Profiles",
                modifier = Modifier()
                    .backgroundColor("#28a745")
                    .color("white")
                    .padding(10.px, 20.px)
                    .borderRadius(4.px)
                    .onClick("navigateTo('users/profile')")
            ) + Button(
                text = "Blog",
                modifier = Modifier()
                    .backgroundColor("#6c757d")
                    .color("white")
                    .padding(10.px, 20.px)
                    .borderRadius(4.px)
                    .onClick("navigateTo('blog/post/my-first-post')")
            )
        }
    }
}
```

### Dynamic User Profile Page

```kotlin
// File: src/commonMain/kotlin/pages/users/ProfilePage.kt
@Composable
fun ProfilePage(userId: String? = null): String {
    return Column(
        modifier = Modifier().padding(20.px).gap(16.px)
    ) {
        Text("User Profile", modifier = Modifier().fontSize(24.px).fontWeight(FontWeight.Bold)) +
        Text("User ID: ${userId ?: "Not specified"}") +
        Text("This page demonstrates dynamic routing with parameters.") +
        
        if (userId != null) {
            UserDetails(userId)
        } else {
            Text("No user ID provided in the route.")
        } +
        
        NavigationLinks()
    }
}

@Composable
fun UserDetails(userId: String): String {
    return Column(
        modifier = Modifier()
            .backgroundColor("#f8f9fa")
            .padding(16.px)
            .borderRadius(8.px)
            .marginTop(16.px)
    ) {
        Text("User Details", modifier = Modifier().fontWeight(FontWeight.Bold)) +
        Text("Name: User $userId") +
        Text("Email: user$userId@example.com") +
        Text("Member since: 2024")
    }
}
```

### Blog Post Page

```kotlin
// File: src/commonMain/kotlin/pages/blog/PostPage.kt
@Composable
fun PostPage(postId: String? = null): String {
    return Column(
        modifier = Modifier().padding(20.px).gap(16.px)
    ) {
        Text("Blog Post", modifier = Modifier().fontSize(24.px).fontWeight(FontWeight.Bold)) +
        Text("Post ID: ${postId ?: "Not specified"}") +
        
        if (postId != null) {
            BlogPostContent(postId)
        } else {
            Text("No post ID provided in the route.")
        } +
        
        NavigationLinks()
    }
}

@Composable
fun BlogPostContent(postId: String): String {
    return Column(
        modifier = Modifier()
            .backgroundColor("#ffffff")
            .border(1.px, BorderStyle.Solid, "#e0e0e0")
            .padding(20.px)
            .borderRadius(8.px)
            .marginTop(16.px)
    ) {
        Text("$postId", modifier = Modifier().fontSize(20.px).fontWeight(FontWeight.Bold)) +
        Text("Published on: January 15, 2024", modifier = Modifier().color("#666").fontSize(14.px)) +
        Text("This is the content of the blog post. In a real application, you would fetch this content from a database or API based on the post ID.") +
        Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
    }
}
```

### Custom 404 Page

```kotlin
// File: src/commonMain/kotlin/pages/NotFoundPage.kt
@Composable
fun NotFoundPage(): String {
    return Column(
        modifier = Modifier()
            .padding(20.px)
            .gap(16.px)
            .textAlign(TextAlign.Center)
    ) {
        Text("404", modifier = Modifier().fontSize(48.px).fontWeight(FontWeight.Bold).color("#dc3545")) +
        Text("Page Not Found", modifier = Modifier().fontSize(24.px).fontWeight(FontWeight.Bold)) +
        Text("Sorry, the page you are looking for does not exist.") +
        
        Button(
            text = "Back to Home",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding(12.px, 24.px)
                .borderRadius(4.px)
                .cursor(Cursor.Pointer)
                .onClick("navigateTo('home')")
        )
    }
}
```

## Manual Route Registration

Since we're using a standalone implementation, you manually register routes based on your file organization:

```kotlin
// File: src/commonMain/kotlin/router/FileBasedRouter.kt
// Include the standalone Summon implementation (from quickstart.md)

// Route registry that maps file structure to routes
object FileBasedRouter {
    
    // Map routes to page functions following file-based conventions
    val routes = mapOf(
        "home" to ::IndexPage,
        "about" to ::AboutPage,
        "users/profile" to { ProfilePage("default-user") },
        "blog/post" to { PostPage("sample-post") },
        "404" to ::NotFoundPage
    )
    
    // Handle dynamic routes with parameters
    fun getPageForRoute(route: String): String {
        return when {
            route == "home" || route == "" -> IndexPage()
            route == "about" -> AboutPage()
            route.startsWith("users/profile") -> {
                val userId = extractUserIdFromRoute(route)
                ProfilePage(userId)
            }
            route.startsWith("blog/post") -> {
                val postId = extractPostIdFromRoute(route)
                PostPage(postId)
            }
            routes.containsKey(route) -> routes[route]!!()
            else -> NotFoundPage()
        }
    }
    
    // Helper functions to extract parameters from routes
    private fun extractUserIdFromRoute(route: String): String? {
        // Extract user ID from routes like "users/profile/123"
        val parts = route.split("/")
        return if (parts.size > 2) parts[2] else null
    }
    
    private fun extractPostIdFromRoute(route: String): String? {
        // Extract post ID from routes like "blog/post/my-first-post"
        val parts = route.split("/")
        return if (parts.size > 2) parts[2] else null
    }
}

// Enhanced router with file-based mapping
data class FileBasedRouterState(
    var currentRoute: String = "home",
    var params: Map<String, String> = emptyMap()
)

val fileBasedRouter = FileBasedRouterState()

@Composable
fun FileBasedApp(): String {
    return Div(
        modifier = Modifier()
            .style("min-height", "100vh")
            .backgroundColor("#f8f9fa")
    ) {
        FileBasedRouter.getPageForRoute(fileBasedRouter.currentRoute)
    }
}
```

### Project Structure

Organize your project following this structure:

```
src/
├── commonMain/kotlin/
│   ├── SummonComponents.kt          // Standalone Summon implementation
│   ├── router/
│   │   └── FileBasedRouter.kt       // Router registration
│   └── pages/
│       ├── IndexPage.kt             // Home page (route: /)
│       ├── AboutPage.kt             // About page (route: /about)
│       ├── NotFoundPage.kt          // 404 page
│       ├── users/
│       │   └── ProfilePage.kt       // User profile (route: /users/profile/{id})
│       └── blog/
│           └── PostPage.kt          // Blog post (route: /blog/post/{id})
├── jsMain/kotlin/
│   └── main.kt                      // Browser entry point
└── jvmMain/kotlin/
    └── server.kt                    // Server entry point (optional)
```

## Setting Up File-Based Routing

### Browser Application

For browser applications, set up the file-based router in your main function:

```kotlin
// File: src/jsMain/kotlin/main.kt
fun main() {
    val root = kotlinx.browser.document.getElementById("root")
        ?: error("Root element not found")
    
    // Add navigation JavaScript
    addFileBasedNavigationJS()
    
    // Initial render
    updateFileBasedUI()
    
    // Setup hash change listener for client-side routing
    kotlinx.browser.window.addEventListener("hashchange", { updateFileBasedUI() })
}

fun addFileBasedNavigationJS() {
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <script>
        // File-based navigation function
        function navigateTo(route) {
            window.location.hash = route;
        }
        
        // Specific navigation functions for type safety
        function goToHome() { navigateTo('home'); }
        function goToAbout() { navigateTo('about'); }
        function goToUserProfile(userId) { navigateTo('users/profile/' + userId); }
        function goToBlogPost(postId) { navigateTo('blog/post/' + postId); }
        
        // Browser history functions
        function goBack() { window.history.back(); }
        function goForward() { window.history.forward(); }
        </script>
    """)
}

fun updateFileBasedUI() {
    val root = kotlinx.browser.document.getElementById("root")
        ?: error("Root element not found")
    
    // Get current hash and parse route
    val hash = kotlinx.browser.window.location.hash.removePrefix("#").ifEmpty { "home" }
    fileBasedRouter.currentRoute = hash
    
    // Re-render with file-based router
    root.innerHTML = FileBasedApp()
}
```

### JVM Application

For server-side applications, handle file-based routing on the server:

```kotlin
// File: src/jvmMain/kotlin/server.kt
fun handleFileBasedRequest(path: String): String {
    // Convert URL path to route format
    val route = convertPathToRoute(path)
    
    // Set router state
    fileBasedRouter.currentRoute = route
    
    // Render page with HTML wrapper
    return renderFileBasedPage { FileBasedApp() }
}

fun convertPathToRoute(path: String): String {
    return when {
        path == "/" -> "home"
        path == "/about" -> "about"
        path.startsWith("/users/profile") -> {
            val userId = path.substringAfterLast("/")
            if (userId != "profile") "users/profile/$userId" else "users/profile"
        }
        path.startsWith("/blog/post") -> {
            val postId = path.substringAfterLast("/")
            if (postId != "post") "blog/post/$postId" else "blog/post"
        }
        else -> "404"
    }
}

fun renderFileBasedPage(pageContent: () -> String): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>File-Based Routing App</title>
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
            ${pageContent()}
        </body>
        </html>
    """.trimIndent()
}

// Ktor integration example
fun Application.configureFileBasedRouting() {
    routing {
        get("/") { call.respondText(handleFileBasedRequest("/"), ContentType.Text.Html) }
        get("/about") { call.respondText(handleFileBasedRequest("/about"), ContentType.Text.Html) }
        get("/users/profile/{id?}") { 
            val id = call.parameters["id"]
            val path = if (id != null) "/users/profile/$id" else "/users/profile"
            call.respondText(handleFileBasedRequest(path), ContentType.Text.Html) 
        }
        get("/blog/post/{id?}") { 
            val id = call.parameters["id"]
            val path = if (id != null) "/blog/post/$id" else "/blog/post"
            call.respondText(handleFileBasedRequest(path), ContentType.Text.Html) 
        }
        get("/{...}") {
            call.respondText(handleFileBasedRequest(call.request.uri), ContentType.Text.Html)
        }
    }
}
```

## Complete Working Example

Here's how all the pieces fit together in a complete working application:

```kotlin
// File: src/jsMain/kotlin/FileBasedExample.kt
fun main() {
    val root = kotlinx.browser.document.getElementById("root")
        ?: error("Root element not found")
    
    // Setup file-based routing
    addFileBasedNavigationJS()
    updateFileBasedUI()
    kotlinx.browser.window.addEventListener("hashchange", { updateFileBasedUI() })
    
    // Add some sample data and navigation
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <script>
        // Sample navigation functions
        function showUserProfile() {
            navigateTo('users/profile/user123');
        }
        
        function showBlogPost() {
            navigateTo('blog/post/getting-started-with-summon');
        }
        
        function showRandomUser() {
            const userId = 'user' + Math.floor(Math.random() * 1000);
            navigateTo('users/profile/' + userId);
        }
        </script>
    """)
}

// Enhanced navigation component for file-based routing
@Composable
fun FileBasedNavigation(): String {
    return Column(
        modifier = Modifier()
            .backgroundColor("#ffffff")
            .padding(16.px)
            .borderRadius(8.px)
            .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
            .marginBottom(20.px)
    ) {
        Text("Navigation", modifier = Modifier().fontSize(18.px).fontWeight(FontWeight.Bold).marginBottom(12.px)) +
        
        Row(modifier = Modifier().gap(8.px).flexWrap(FlexWrap.Wrap)) {
            Button(
                text = "🏠 Home",
                modifier = Modifier()
                    .backgroundColor("#0077cc")
                    .color("white")
                    .padding(8.px, 12.px)
                    .borderRadius(4.px)
                    .onClick("goToHome()")
            ) + Button(
                text = "ℹ️ About",
                modifier = Modifier()
                    .backgroundColor("#6c757d")
                    .color("white")
                    .padding(8.px, 12.px)
                    .borderRadius(4.px)
                    .onClick("goToAbout()")
            ) + Button(
                text = "👤 Profile",
                modifier = Modifier()
                    .backgroundColor("#28a745")
                    .color("white")
                    .padding(8.px, 12.px)
                    .borderRadius(4.px)
                    .onClick("showUserProfile()")
            ) + Button(
                text = "📝 Blog",
                modifier = Modifier()
                    .backgroundColor("#ffc107")
                    .color("black")
                    .padding(8.px, 12.px)
                    .borderRadius(4.px)
                    .onClick("showBlogPost()")
            ) + Button(
                text = "🎲 Random User",
                modifier = Modifier()
                    .backgroundColor("#17a2b8")
                    .color("white")
                    .padding(8.px, 12.px)
                    .borderRadius(4.px)
                    .onClick("showRandomUser()")
            )
        }
    }
}

// Update all page components to include the new navigation
@Composable
fun EnhancedIndexPage(): String {
    return Column(modifier = Modifier().padding(20.px)) {
        FileBasedNavigation() +
        Text("🏠 Welcome Home", modifier = Modifier().fontSize(28.px).fontWeight(FontWeight.Bold)) +
        Text("This demonstrates file-based routing with the standalone Summon implementation.", 
             modifier = Modifier().marginTop(16.px)) +
        
        Column(
            modifier = Modifier()
                .backgroundColor("#f8f9fa")
                .padding(16.px)
                .borderRadius(8.px)
                .marginTop(20.px)
        ) {
            Text("Current Route Information:", modifier = Modifier().fontWeight(FontWeight.Bold)) +
            Text("Route: ${fileBasedRouter.currentRoute}") +
            Text("URL: ${kotlinx.browser.window.location.href}")
        }
    }
}
```

## Benefits of This Approach

Using the standalone file-based routing implementation provides:

✅ **Convention Over Configuration**: Organize pages by file structure with clear naming patterns  
✅ **Type Safety**: All routes and page functions are fully type-checked by Kotlin compiler  
✅ **No Build Dependencies**: Works immediately without complex build tools or plugins  
✅ **Clear Organization**: File structure directly reflects your application's route structure  
✅ **Easy Maintenance**: Adding new pages is as simple as creating new files and registering routes  
✅ **Cross-Platform**: Same routing logic works on both JavaScript and JVM platforms  
✅ **Debugging Friendly**: Simple, debuggable code without complex framework magic  
✅ **IDE Support**: Full IDE navigation and refactoring support for all page components  

## Scaling the Implementation

As your application grows, you can enhance this pattern:

1. **Add Route Guards**: Implement authentication and authorization checks
2. **Lazy Loading**: Load page components on-demand for better performance
3. **Route Transitions**: Add CSS animations between page transitions
4. **Route Metadata**: Add page titles, descriptions, and other metadata
5. **Nested Layouts**: Implement shared layouts for different page sections
6. **Build Tools Integration**: Create custom build scripts to auto-generate route mappings

This approach gives you the benefits of file-based routing while maintaining full control and understanding of your routing system. 