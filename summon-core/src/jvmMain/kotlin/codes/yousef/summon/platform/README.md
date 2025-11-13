# JVM Platform Renderer for Summon

This package contains the JVM-specific implementations of the Summon platform renderer and related functionality.

## Overview

The JVM platform renderer is responsible for rendering Summon components to HTML for server-side rendering (SSR). It
uses the `kotlinx.html` library to generate HTML content.

## Key Components

### JvmPlatformRenderer

The `JvmPlatformRenderer` class is the main implementation of the `MigratedPlatformRenderer` interface for the JVM
platform. It includes methods for rendering various UI components to HTML.

Key features:

- Manages a list of HTML head elements for SEO purposes
- Implements rendering methods for all Summon components
- Handles composition context and state tracking
- Manages error handling during rendering

### RenderToString

The `renderToString` function provides a convenient way to render Summon components to HTML strings:

```kotlin
// Basic usage
val html = renderToString {
    // Your composable content here
}

// Advanced usage with options
val html = renderToString(
    content = { YourComponent(param1, param2) },
    options = RenderToStringOptions(
        title = "Page Title",
        description = "Meta description for SEO",
        includeDocType = true,
        includeHtmlWrapper = true,
        additionalHeadElements = listOf("<link rel=\"stylesheet\" href=\"styles.css\">")
    )
)
```

## Usage with Web Frameworks

### Quarkus Example

```kotlin
@GET
@Path("/users")
@Produces(MediaType.TEXT_HTML)
fun getUsers(): String {
    val users = userService.getAllUsers()
    return renderToString {
        UserListPage(users)
    }
}
```

### Ktor Example

```kotlin
get("/users") {
    call.respondText(
        renderToString {
            UserListPage(userService.getAllUsers())
        },
        ContentType.Text.Html
    )
}
```

## Customizing Rendering

The `RenderToStringOptions` class allows you to customize how components are rendered to HTML:

- `title`: Sets the page title
- `description`: Sets the meta description for SEO
- `includeDocType`: Controls whether to include the DOCTYPE declaration
- `includeHtmlWrapper`: Controls whether to wrap content in html/body tags
- `additionalHeadElements`: Allows adding custom elements to the HEAD section

## Testing

Use the `renderToString` function in tests to verify component output:

```kotlin
@Test
fun testButtonRendering() {
    val html = renderToString {
        Button(onClick = {}) {
            Text("Click me")
        }
    }
    
    assertTrue(html.contains("<button"))
    assertTrue(html.contains("Click me"))
} 