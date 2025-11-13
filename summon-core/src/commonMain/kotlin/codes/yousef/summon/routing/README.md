# Summon Next.js-Style File-Based Routing

Summon now includes a complete Next.js-style file-based routing system. This system automatically discovers page files
from your codebase and maps them to routes based on their file paths.

**Version:** 0.2.0.1

## How it Works

1. **Page Discovery**: At build time, the build system scans your source directory for page files.
2. **Code Generation**: Based on discovered pages, the system generates route registration code.
3. **Runtime Routing**: At runtime, the generated code registers all discovered pages with the router.

## Page Conventions

Pages are discovered based on the following conventions:

| File Path                  | URL Route        | Notes                   |
|----------------------------|------------------|-------------------------|
| `/pages/Index.kt`          | `/`              | Home/index route        |
| `/pages/Home.kt`           | `/`              | Alternative home route  |
| `/pages/About.kt`          | `/about`         | Standard route          |
| `/pages/users/Profile.kt`  | `/users/profile` | Nested route            |
| `/pages/blog/[id].kt`      | `/blog/:id`      | Dynamic parameter route |
| `/pages/blog/[...slug].kt` | `/blog/*`        | Catch-all route         |
| `/pages/404.kt`            | N/A              | 404 Not Found page      |

## Creating Pages

To create a page, create a Kotlin file in the pages directory:

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
    pagesDirectory = "src/commonMain/kotlin/code/yousef/summon/routing/pages"
    outputDirectory = "build/generated/source/summon"
}
```

## Usage

The router is automatically created with all discovered pages:

```kotlin
// Create a router with all auto-discovered pages
val router = PageLoader.createRouter()

// Use the router in your application
SummonApp {
    router.create()
}
```

## Benefits of This Approach

- **Convention Over Configuration**: Routes are automatically derived from file paths, reducing boilerplate
- **Type Safety**: All routes are fully type-checked by the Kotlin compiler
- **Build-Time Validation**: Route conflicts and issues are detected at build time
- **Performance**: Runtime reflection is not needed, improving performance
- **Code Navigation**: IDE navigation between routes and page implementations works seamlessly

## Note on Programmatic Routing

While file-based routing is now the recommended approach, the programmatic routing API is still available and fully
supported for cases where more manual control is needed. The file-based router uses the same underlying router
implementation, so both approaches can be used together when necessary. 