# Server-Side Rendering (SSR) Guide

Summon provides a comprehensive Server-Side Rendering (SSR) implementation that enables you to render your Kotlin Multiplatform UI components on the server for improved SEO, faster initial page loads, and better user experience.

## Table of Contents

- [Overview](#overview)
- [Basic Usage](#basic-usage)
- [Advanced Features](#advanced-features)
- [SEO Optimization](#seo-optimization)
- [Hydration](#hydration)
- [Performance](#performance)
- [Real-World Examples](#real-world-examples)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Overview

### What is SSR?

Server-Side Rendering (SSR) is a technique where web pages are rendered on the server before being sent to the client. Instead of sending a mostly empty HTML page that gets populated by JavaScript, SSR sends fully rendered HTML content.

### Benefits of SSR

- **SEO Optimization**: Search engines can crawl your content immediately
- **Faster Initial Load**: Users see content before JavaScript loads
- **Better Performance**: Especially beneficial for slow devices and networks
- **Improved Accessibility**: Content is available even if JavaScript fails
- **Social Media Sharing**: Rich previews with proper meta tags

### Summon's SSR Implementation

Summon's SSR system includes:
- **Complete Composition Context**: Full support for `@Composable` functions with proper state management
- **State Management**: `remember`, `mutableStateOf`, and reactive state work during server rendering
- **HTML Generation**: Production-quality HTML output using kotlinx.html
- **SEO Support**: Built-in meta tag, OpenGraph, and Twitter Card support
- **Hydration**: Client-side reactivation of server-rendered content
- **Performance**: Optimized for production use with comprehensive testing

## Basic Usage

### Simple Component Rendering

```kotlin
import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf

// Create a renderer instance
val renderer = PlatformRenderer()

@Composable
fun HelloWorld() {
    val message = remember { mutableStateOf("Hello from SSR!") }
    
    Column(modifier = Modifier()) {
        Text("Welcome to Summon SSR", modifier = Modifier())
        Text(message.value, modifier = Modifier())
    }
}

// Render to HTML
val html = renderer.renderComposableRoot {
    HelloWorld()
}

println(html)
// Output: Complete HTML document with proper structure
```

### With State Management

```kotlin
@Composable
fun Counter() {
    val count = remember { mutableStateOf(10) }
    val description = remember { mutableStateOf("Server-rendered counter") }
    
    Column(modifier = Modifier()) {
        Text(description.value, modifier = Modifier())
        Text("Count: ${count.value}", modifier = Modifier())
        
        // This button will be interactive after hydration
        Button(
            onClick = { count.value += 1 },
            label = "Increment",
            modifier = Modifier()
        )
    }
}
```

## Advanced Features

### High-Level Rendering Utility

The `ServerSideRenderUtils` class provides convenient methods for common SSR scenarios:

```kotlin
import code.yousef.summon.ssr.ServerSideRenderUtils

val html = ServerSideRenderUtils.renderPageToString(
    rootComposable = { MyApp() },
    initialData = mapOf(
        "userId" to "123",
        "theme" to "dark",
        "language" to "en"
    ),
    includeHydrationScript = true
)
```

### Custom Render Context

For advanced scenarios, use `RenderContext` to configure the rendering environment:

```kotlin
import code.yousef.summon.ssr.*

val context = RenderContext(
    enableHydration = true,
    hydrationIdPrefix = "app-",
    metadata = mapOf("version" to "1.0.0"),
    debug = true,
    initialState = mapOf("user" to userData)
)

// Use with custom renderer methods
val renderer = PlatformRenderer()
// Custom rendering logic here
```

## SEO Optimization

### Basic Meta Tags

```kotlin
val seoMetadata = SeoMetadata(
    title = "My Awesome App",
    description = "A powerful web application built with Summon",
    keywords = listOf("kotlin", "web", "multiplatform", "ssr"),
    robots = "index, follow",
    canonical = "https://myapp.com/page"
)

val context = RenderContext(seoMetadata = seoMetadata)
```

### OpenGraph for Social Sharing

```kotlin
val openGraph = OpenGraphMetadata(
    title = "My Awesome App",
    description = "Check out this amazing Kotlin web app!",
    type = "website",
    url = "https://myapp.com",
    image = "https://myapp.com/images/og-image.jpg",
    siteName = "My Company"
)

val seoMetadata = SeoMetadata(
    title = "My App",
    description = "Great app",
    openGraph = openGraph
)
```

### Twitter Cards

```kotlin
val twitterCard = TwitterCardMetadata(
    card = "summary_large_image",
    site = "@mycompany",
    creator = "@developer",
    title = "My Awesome App",
    description = "Built with Summon framework",
    image = "https://myapp.com/images/twitter-card.jpg"
)

val seoMetadata = SeoMetadata(
    twitterCard = twitterCard,
    // ... other metadata
)
```

### Custom Meta Tags

```kotlin
val customMeta = mapOf(
    "author" to "My Company",
    "application-name" to "My App",
    "theme-color" to "#007bff",
    "msapplication-TileColor" to "#da532c"
)

val seoMetadata = SeoMetadata(
    customMetaTags = customMeta,
    // ... other metadata
)
```

## Hydration

Hydration makes server-rendered content interactive on the client side.

### Basic Hydration

```kotlin
// Server-side: render with hydration support
val html = renderer.renderComposableRootWithHydration {
    MyInteractiveApp()
}

// The HTML will include hydration scripts and data
// Client-side JavaScript will make components interactive
```

### Hydration with Initial State

```kotlin
val initialState = mapOf(
    "user" to mapOf(
        "id" to "123",
        "name" to "John Doe",
        "preferences" to mapOf("theme" to "dark")
    ),
    "cart" to mapOf(
        "items" to listOf("item1", "item2"),
        "total" to 99.99
    )
)

val html = ServerSideRenderUtils.renderPageToString(
    rootComposable = { ECommerceApp() },
    initialData = initialState,
    includeHydrationScript = true
)
```

### Hydration Strategies

```kotlin
// Different hydration approaches
enum class HydrationStrategy {
    NONE,        // Static HTML only
    FULL,        // Hydrate entire page
    PARTIAL,     // Hydrate only interactive elements
    PROGRESSIVE  // Hydrate based on visibility
}
```

## Performance

### Optimized for Scale

Summon's SSR implementation is optimized for production use:

- **Handles 100+ components** efficiently in a single render
- **Deep nesting support** (15+ levels tested)
- **Memory management** with automatic cleanup
- **Concurrent rendering** support for multi-user servers

### Performance Tips

1. **Use remember for expensive computations**:
```kotlin
@Composable
fun ExpensiveComponent() {
    val expensiveData = remember { computeExpensiveData() }
    // Use expensiveData...
}
```

2. **Batch database calls**:
```kotlin
@Composable
fun DataDrivenPage(ids: List<String>) {
    // Load all data at once instead of individual calls
    val allData = remember { loadDataBatch(ids) }
    
    allData.forEach { item ->
        ItemComponent(item)
    }
}
```

3. **Use selective rendering for large lists**:
```kotlin
@Composable
fun LargeList(items: List<Item>) {
    val visibleItems = remember { items.take(50) } // Only render first 50
    
    Column(modifier = Modifier()) {
        visibleItems.forEach { item ->
            ItemComponent(item)
        }
        
        if (items.size > 50) {
            Text("... and ${items.size - 50} more items")
        }
    }
}
```

## Real-World Examples

### E-commerce Product Page

```kotlin
@Composable
fun ProductPage(productId: String) {
    val product = remember { mutableStateOf(loadProduct(productId)) }
    val reviews = remember { mutableStateOf(loadReviews(productId)) }
    val cartQuantity = remember { mutableStateOf(0) }
    val selectedVariant = remember { mutableStateOf(product.value.variants.first()) }
    
    Column(modifier = Modifier()) {
        // Product header
        Row(modifier = Modifier()) {
            // Product image would go here
            Column(modifier = Modifier()) {
                Text(product.value.name, modifier = Modifier())
                Text("$${selectedVariant.value.price}", modifier = Modifier())
                Text(if (selectedVariant.value.inStock) "In Stock" else "Out of Stock", 
                     modifier = Modifier())
            }
        }
        
        // Product variants
        Text("Options:", modifier = Modifier())
        product.value.variants.forEach { variant ->
            Button(
                onClick = { selectedVariant.value = variant },
                label = "${variant.name} - $${variant.price}",
                modifier = Modifier()
            )
        }
        
        // Add to cart
        Row(modifier = Modifier()) {
            Button(
                onClick = { if (cartQuantity.value > 0) cartQuantity.value-- },
                label = "-",
                modifier = Modifier()
            )
            Text("${cartQuantity.value}", modifier = Modifier())
            Button(
                onClick = { cartQuantity.value++ },
                label = "+",
                modifier = Modifier()
            )
            Button(
                onClick = { addToCart(selectedVariant.value, cartQuantity.value) },
                label = "Add to Cart",
                modifier = Modifier()
            )
        }
        
        // Product description
        Text("Description:", modifier = Modifier())
        Text(product.value.description, modifier = Modifier())
        
        // Reviews
        Text("Reviews (${reviews.value.size}):", modifier = Modifier())
        reviews.value.take(5).forEach { review ->
            Column(modifier = Modifier()) {
                Row(modifier = Modifier()) {
                    Text(review.author, modifier = Modifier())
                    Text("${"★".repeat(review.rating)}", modifier = Modifier())
                }
                Text(review.comment, modifier = Modifier())
            }
        }
    }
}
```

### Blog with Comments

```kotlin
@Composable
fun BlogPost(slug: String) {
    val post = remember { mutableStateOf(loadBlogPost(slug)) }
    val comments = remember { mutableStateOf(loadComments(post.value.id)) }
    val newComment = remember { mutableStateOf("") }
    val user = remember { mutableStateOf(getCurrentUser()) }
    
    Column(modifier = Modifier()) {
        // Blog post header
        Text(post.value.title, modifier = Modifier())
        Text("By ${post.value.author} on ${post.value.publishDate}", modifier = Modifier())
        
        // Post content
        Text(post.value.content, modifier = Modifier())
        
        // Tags
        Row(modifier = Modifier()) {
            Text("Tags: ", modifier = Modifier())
            post.value.tags.forEach { tag ->
                Button(
                    onClick = { navigateToTag(tag) },
                    label = tag,
                    modifier = Modifier()
                )
            }
        }
        
        // Comments section
        Text("Comments (${comments.value.size})", modifier = Modifier())
        
        // Comment form (if user is logged in)
        user.value?.let { currentUser ->
            Column(modifier = Modifier()) {
                Text("Add a comment:", modifier = Modifier())
                // In a real app, this would be a TextArea
                Text("Comment: ${newComment.value}", modifier = Modifier())
                
                Row(modifier = Modifier()) {
                    Button(
                        onClick = {
                            if (newComment.value.isNotBlank()) {
                                submitComment(post.value.id, newComment.value, currentUser.id)
                                newComment.value = ""
                                comments.value = loadComments(post.value.id) // Refresh
                            }
                        },
                        label = "Submit Comment",
                        modifier = Modifier()
                    )
                    Button(
                        onClick = { newComment.value = "" },
                        label = "Clear",
                        modifier = Modifier()
                    )
                }
            }
        }
        
        // Display comments
        comments.value.forEach { comment ->
            Column(modifier = Modifier()) {
                Row(modifier = Modifier()) {
                    Text(comment.author, modifier = Modifier())
                    Text(comment.timestamp, modifier = Modifier())
                }
                Text(comment.content, modifier = Modifier())
            }
        }
    }
}
```

### Dashboard Application

```kotlin
@Composable
fun Dashboard() {
    val metrics = remember { mutableStateOf(loadDashboardMetrics()) }
    val selectedPeriod = remember { mutableStateOf("Monthly") }
    val refreshing = remember { mutableStateOf(false) }
    
    Column(modifier = Modifier()) {
        // Dashboard header
        Row(modifier = Modifier()) {
            Text("Business Dashboard", modifier = Modifier())
            
            // Period selector
            listOf("Daily", "Weekly", "Monthly", "Yearly").forEach { period ->
                Button(
                    onClick = { 
                        selectedPeriod.value = period
                        refreshing.value = true
                        // In real app, this would trigger data reload
                        metrics.value = loadDashboardMetrics(period.lowercase())
                        refreshing.value = false
                    },
                    label = period,
                    modifier = Modifier()
                )
            }
            
            Button(
                onClick = {
                    refreshing.value = true
                    metrics.value = loadDashboardMetrics(selectedPeriod.value.lowercase())
                    refreshing.value = false
                },
                label = if (refreshing.value) "Refreshing..." else "Refresh",
                modifier = Modifier()
            )
        }
        
        // Key metrics
        Row(modifier = Modifier()) {
            metrics.value.keyMetrics.forEach { metric ->
                Column(modifier = Modifier()) {
                    Text(metric.name, modifier = Modifier())
                    Text("${metric.value}", modifier = Modifier())
                    
                    val change = metric.percentChange
                    val changeColor = if (change >= 0) "green" else "red"
                    val changeSymbol = if (change >= 0) "↗" else "↘"
                    Text("$changeSymbol ${change}%", modifier = Modifier())
                }
            }
        }
        
        // Charts (simplified representation for SSR)
        Text("Revenue Trends", modifier = Modifier())
        metrics.value.chartData.forEach { dataPoint ->
            Row(modifier = Modifier()) {
                Text(dataPoint.label, modifier = Modifier())
                Text("$${dataPoint.value}", modifier = Modifier())
                // ASCII bar chart
                val barLength = (dataPoint.value / 1000).toInt()
                Text("█".repeat(barLength), modifier = Modifier())
            }
        }
        
        // Recent activity
        Text("Recent Activity", modifier = Modifier())
        metrics.value.recentActivity.take(10).forEach { activity ->
            Row(modifier = Modifier()) {
                Text(activity.timestamp, modifier = Modifier())
                Text(activity.description, modifier = Modifier())
            }
        }
    }
}
```

## Best Practices

### 1. Structure Your Components for SSR

```kotlin
// Good: Separate data loading from presentation
@Composable
fun ProductPageContainer(productId: String) {
    val product = remember { mutableStateOf(loadProduct(productId)) }
    ProductPagePresentation(product.value)
}

@Composable
fun ProductPagePresentation(product: Product) {
    // Pure presentation logic
    Column(modifier = Modifier()) {
        Text(product.name, modifier = Modifier())
        // ... rest of UI
    }
}
```

### 2. Handle Loading States

```kotlin
@Composable
fun DataDrivenComponent(dataId: String) {
    val data = remember { mutableStateOf<DataType?>(null) }
    val loading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }
    
    // Simulate data loading
    remember {
        try {
            data.value = loadData(dataId)
            loading.value = false
        } catch (e: Exception) {
            error.value = e.message
            loading.value = false
        }
    }
    
    when {
        loading.value -> Text("Loading...", modifier = Modifier())
        error.value != null -> Text("Error: ${error.value}", modifier = Modifier())
        data.value != null -> DataPresentation(data.value!!)
        else -> Text("No data available", modifier = Modifier())
    }
}
```

### 3. Optimize Database Queries

```kotlin
// Good: Batch load related data
@Composable
fun UserProfilePage(userId: String) {
    val profileData = remember { 
        mutableStateOf(
            ProfileData(
                user = loadUser(userId),
                posts = loadUserPosts(userId),
                followers = loadUserFollowers(userId),
                following = loadUserFollowing(userId)
            )
        )
    }
    
    UserProfilePresentation(profileData.value)
}

// Avoid: Multiple separate queries
@Composable 
fun UserProfilePageBad(userId: String) {
    val user = remember { mutableStateOf(loadUser(userId)) }
    val posts = remember { mutableStateOf(loadUserPosts(userId)) } // Separate query
    val followers = remember { mutableStateOf(loadUserFollowers(userId)) } // Separate query
    // ... This results in multiple database round trips
}
```

### 4. Use Proper Error Boundaries

```kotlin
@Composable
fun SafeComponent(content: @Composable () -> Unit) {
    val error = remember { mutableStateOf<String?>(null) }
    
    if (error.value != null) {
        Column(modifier = Modifier()) {
            Text("Something went wrong: ${error.value}", modifier = Modifier())
            Button(
                onClick = { error.value = null },
                label = "Try Again",
                modifier = Modifier()
            )
        }
    } else {
        try {
            content()
        } catch (e: Exception) {
            error.value = e.message
        }
    }
}
```

## Troubleshooting

### Common Issues

#### 1. "Rendering function called outside of renderComposableRoot scope"

**Problem**: Trying to render components without proper context.

**Solution**: Ensure you're using `PlatformRenderer` correctly:

```kotlin
val renderer = PlatformRenderer()
val html = renderer.renderComposableRoot {
    MyComponent() // This works
}

// Don't do this:
MyComponent() // This will fail - no rendering context
```

#### 2. State not working during SSR

**Problem**: State isn't initialized properly during server rendering.

**Solution**: Use `remember` correctly:

```kotlin
@Composable
fun MyComponent() {
    // Good: State initialized during composition
    val state = remember { mutableStateOf("initial value") }
    
    // Bad: State created outside composition
    // val state = mutableStateOf("initial value")
}
```

#### 3. HTML structure issues

**Problem**: Generated HTML doesn't have proper structure.

**Solution**: Use `renderComposableRoot` for complete documents:

```kotlin
// Good: Complete HTML document
val html = renderer.renderComposableRoot {
    MyApp()
}

// Limited: Just content without HTML structure
val content = renderer.renderComposableContent {
    MyApp()
}
```

#### 4. Memory leaks during SSR

**Problem**: Server memory usage grows over time.

**Solution**: Don't hold references to renderers unnecessarily:

```kotlin
// Good: Create renderer when needed
fun handleRequest(): String {
    val renderer = PlatformRenderer()
    return renderer.renderComposableRoot { MyApp() }
    // Renderer can be garbage collected
}

// Avoid: Global renderer instance
val globalRenderer = PlatformRenderer() // May hold onto resources
```

#### 5. onClick handlers not working after hydration

**Problem**: Buttons and interactive elements don't respond to clicks in the browser, despite being rendered correctly.

**Symptoms**:
- Callback IDs in HTML don't match hydration data
- Console shows "Callback not found" warnings
- No network requests to `/summon/callback` when clicking

**Root Cause**: In coroutine-based frameworks (Ktor, Spring WebFlux), the request may be processed by different threads during coroutine suspension/resumption. This causes callbacks to be registered on one thread but collected from another, resulting in mismatched callback IDs.

**Solution**: Ensure you're using version **0.4.9.4 or later** and the proper hydration method:

```kotlin
// Ktor - Use respondSummonHydrated (already includes the fix)
get("/") {
    call.respondSummonHydrated {
        HomePage()
    }
}

// Manual rendering - Ensure proper context
suspend fun renderWithHydration(content: @Composable () -> Unit): String {
    val renderer = PlatformRenderer()
    setPlatformRenderer(renderer)
    
    val callbackContext = CallbackContextElement()
    
    return try {
        withContext(callbackContext) {
            renderer.renderComposableRootWithHydration(content)
        }
    } finally {
        clearPlatformRenderer()
    }
}
```

**Why this works**: The `CallbackContextElement` maintains a stable callback context ID throughout the request lifecycle, even when the coroutine switches threads. This ensures callbacks registered during rendering are collected correctly for hydration.

**Fixed in**: Version 0.4.8.7 (2025-01-16)

### Performance Issues

#### Slow rendering

1. **Profile your data loading**:
   ```kotlin
   val startTime = System.currentTimeMillis()
   val data = loadData()
   println("Data loading took: ${System.currentTimeMillis() - startTime}ms")
   ```

2. **Use selective rendering for large lists**:
   ```kotlin
   val items = remember { largeItemList.take(50) } // Limit initial render
   ```

3. **Cache expensive computations**:
   ```kotlin
   val expensiveResult = remember(key) { expensiveComputation(key) }
   ```

#### Memory usage

1. **Monitor memory usage**:
   ```kotlin
   System.gc()
   val memoryUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
   println("Memory used: ${memoryUsed / 1024 / 1024}MB")
   ```

2. **Clear large objects after use**:
   ```kotlin
   @Composable
   fun LargeDataComponent() {
       val largeData = remember { loadLargeDataSet() }
       
       // Use data...
       
       // Clear when done (in a real app, this would be in cleanup)
       // largeData.clear()
   }
   ```

### Debug Mode

Enable debug mode for additional information:

```kotlin
val context = RenderContext(
    debug = true,
    // ... other options
)
```

This will include helpful comments in the generated HTML for debugging purposes.

## Framework Integration

### Ktor

```kotlin
fun Application.configureSummon() {
    routing {
        get("/") {
            val renderer = PlatformRenderer()
            val html = renderer.renderComposableRoot {
                HomePage()
            }
            call.respondText(html, ContentType.Text.Html)
        }
        
        get("/products/{id}") {
            val productId = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val renderer = PlatformRenderer()
            val html = renderer.renderComposableRoot {
                ProductPage(productId)
            }
            call.respondText(html, ContentType.Text.Html)
        }
    }
}
```

### Spring Boot

```kotlin
@RestController
class SummonController {
    
    @GetMapping("/", produces = ["text/html"])
    fun homePage(): String {
        val renderer = PlatformRenderer()
        return renderer.renderComposableRoot {
            HomePage()
        }
    }
    
    @GetMapping("/products/{id}", produces = ["text/html"])
    fun productPage(@PathVariable id: String): String {
        val renderer = PlatformRenderer()
        return renderer.renderComposableRoot {
            ProductPage(id)
        }
    }
}
```

### Quarkus

```kotlin
@Path("/")
class SummonResource {
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun homePage(): String {
        val renderer = PlatformRenderer()
        return renderer.renderComposableRoot {
            HomePage()
        }
    }
    
    @GET
    @Path("/products/{id}")
    @Produces(MediaType.TEXT_HTML)
    fun productPage(@PathParam("id") id: String): String {
        val renderer = PlatformRenderer()
        return renderer.renderComposableRoot {
            ProductPage(id)
        }
    }
}
```

## Conclusion

Summon's SSR implementation provides a robust, production-ready solution for server-side rendering of Kotlin Multiplatform UI components. With comprehensive state management, SEO optimization, hydration support, and excellent performance characteristics, it enables you to build fast, accessible, and SEO-friendly web applications using familiar Kotlin syntax and patterns.

For more advanced topics and specific use cases, refer to the [API Reference](api-reference/ssr.md) and [Integration Guides](integration-guides.md).