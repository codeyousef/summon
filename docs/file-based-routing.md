# File-Based Routing

Summon now includes a complete Next.js-style file-based routing system. This system automatically discovers page files from your codebase and maps them to routes based on their file paths.

## How it Works

1. **Page Discovery**: At build time, the build system scans your source directory for page files.
2. **Code Generation**: Based on discovered pages, the system generates route registration code.
3. **Runtime Routing**: At runtime, the generated code registers all discovered pages with the router.

## File Naming Conventions

The routing system follows these conventions to convert file paths to routes:

| File Path                  | URL Route          | Notes                         |
|----------------------------|--------------------|-------------------------------|
| `/pages/Index.kt`          | `/`                | Home/index route              |
| `/pages/Home.kt`           | `/`                | Alternative home route        |
| `/pages/About.kt`          | `/about`           | Standard route                |
| `/pages/users/Profile.kt`  | `/users/profile`   | Nested route                  |
| `/pages/blog/[id].kt`      | `/blog/:id`        | Dynamic parameter route       |
| `/pages/blog/[...slug].kt` | `/blog/*`          | Catch-all route               |
| `/pages/404.kt`            | N/A                | 404 Not Found page            |

## Creating Page Components

Each page file should export a composable function that serves as the page component:

```kotlin
package code.yousef.summon.routing.pages

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.runtime.Composable

@Composable
fun ExamplePage() {
    Column(
        modifier = Modifier().padding("16px")
    ) {
        Text("Example Page")
    }
}
```

## Dynamic Routes

For dynamic routes with parameters, use the `[param]` syntax in your file name:

```kotlin
// pages/users/[id].kt
package code.yousef.summon.routing.pages.users

import code.yousef.summon.components.display.Text
import code.yousef.summon.runtime.Composable

@Composable
fun UserProfilePage(userId: String?) {
    Text("User Profile for user ID: $userId")
}
```

## Custom 404 Page

Create a `404.kt` file in the pages directory to define a custom 404 Not Found page:

```kotlin
// pages/404.kt
package code.yousef.summon.routing.pages

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.padding
import code.yousef.summon.modifier.textAlign
import code.yousef.summon.runtime.Composable
import code.yousef.summon.routing.Link

@Composable
fun NotFoundPage() {
    Column(
        modifier = Modifier()
            .padding("16px")
            .textAlign("center")
    ) {
        Text("404 - Page Not Found")
        Text("Sorry, the page you are looking for does not exist.")
        
        // Navigation link back to home
        Link(
            text = "Back to Home",
            href = "/"
        )
    }
}
```

## How Code Generation Works

The routing system uses a build-time code generator that:

1. Scans the pages directory for page files
2. Maps file paths to route paths following Next.js conventions
3. Generates code to register these pages with the router
4. Includes the generated code in the build

The generator is configured in the Gradle build:

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

kotlin {
    sourceSets {
        val commonMain by getting {
            // Include the generated source directory
            kotlin.srcDir("build/generated/source/summon")
        }
    }
}
```

## Setting Up File-Based Routing

### Browser Application

For browser applications, set up the router in your main function:

```kotlin
import code.yousef.summon.routing.*
import kotlinx.browser.document

fun main() {
    val container = document.getElementById("app") ?: error("Container not found")
    
    // Create a router with all auto-discovered pages
    val router = PageLoader.createRouter()
    
    // Use the router in your application
    SummonApp {
        router.create()
    }
}
```

### JVM Application

For JVM applications, you can use the router for server-side rendering:

```kotlin
import code.yousef.summon.routing.*

fun handleRequest(path: String): String {
    // Create a server router for file-based routing
    val router = createFileBasedServerRouter(path)
    
    // Render the page to HTML
    return SummonApp {
        router.create(path)
    }.renderToString()
}
```

## Navigation

Navigation works the same way as with programmatic routing:

### Declarative Navigation

Use the `Link` component from our routing package for declarative navigation:

```kotlin
import code.yousef.summon.routing.Link

Link(
    text = "Go to About",
    href = "/about"
)
```

### Programmatic Navigation

Use the router's navigate method for programmatic navigation:

```kotlin
import code.yousef.summon.components.input.Button
import code.yousef.summon.routing.Router

Button(
    text = "Go to About",
    onClick = { 
        Router.navigate("/about")
    }
)
```

## Benefits of This Approach

- **Convention Over Configuration**: Routes are automatically derived from file paths, reducing boilerplate
- **Type Safety**: All routes are fully type-checked by the Kotlin compiler
- **Build-Time Validation**: Route conflicts and issues are detected at build time
- **Performance**: Runtime reflection is not needed, improving performance
- **Code Navigation**: IDE navigation between routes and page implementations works seamlessly 