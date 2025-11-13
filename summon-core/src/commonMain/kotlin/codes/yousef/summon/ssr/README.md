# Server-Side Rendering in Summon

This module provides comprehensive server-side rendering (SSR) capabilities for Summon, allowing you to generate HTML on
the server and optionally hydrate it on the client.

## Features

### StaticRendering

The `StaticRendering` class provides functions for generating static HTML from Summon components:

```kotlin
// Create a component
val homePage = HomePage()

// Generate static HTML
val html = StaticRendering.render(homePage)

// Generate static HTML with SEO metadata
val seoMetadata = SeoMetadata(
    title = "Home Page",
    description = "Welcome to my website",
    keywords = listOf("kotlin", "multiplatform", "static site"),
    canonical = "https://example.com/"
)
val context = RenderContext(seoMetadata = seoMetadata)
val html = StaticRendering.render(homePage, context)

// Generate a complete static site
val pages = mapOf(
    "/" to HomePage(),
    "/about" to AboutPage(),
    "/contact" to ContactPage()
)
val siteHtml = StaticRendering.generateStaticSite(pages)
```

### DynamicRendering

The `DynamicRendering` class provides functions for generating HTML with hydration support:

```kotlin
// Create a component with interactive elements
val interactivePage = InteractivePage()

// Generate HTML with hydration
val html = DynamicRendering.renderWithHydration(interactivePage)

// Generate HTML with hydration and initial state
val initialState = mapOf(
    "counter" to 0,
    "darkMode" to false
)
val html = DynamicRendering.renderWithHydration(
    composable = interactivePage,
    initialState = initialState
)

// Custom rendering with specific hydration support
val partialHydrationSupport = PartialHydrationSupport()
val renderer = DynamicRendering.createRenderer(hydrationSupport = partialHydrationSupport)
val html = renderer.render(interactivePage, RenderContext(enableHydration = true))
```

### StreamingSSR

The `StreamingSSR` class provides functions for streaming HTML chunks for large pages:

```kotlin
// Create a large page component
val largePage = LargePage()

// Get a stream of HTML chunks
val htmlStream = StreamingSSR.renderStream(largePage)

// Process the stream
htmlStream.collect { chunk ->
    // Send chunk to client
    output.write(chunk)
    output.flush()
}

// Create a custom streaming renderer with specific configuration
val renderer = StreamingSSR.createRenderer(
    chunkSize = 4096,
    hydrationSupport = StandardHydrationSupport()
)
val htmlStream = renderer.renderStream(largePage)
```

### HydrationSupport

The hydration support classes provide implementations for different hydration strategies:

```kotlin
// Standard hydration (full page)
val standardHydration = StandardHydrationSupport()

// Partial hydration (only interactive elements)
val partialHydration = PartialHydrationSupport()

// Progressive hydration (based on visibility)
val progressiveHydration = ProgressiveHydrationSupport()
```

### SEOPrerender

The `SEOPrerender` class provides functions for optimizing pages for search engines:

```kotlin
// Check if a user agent is a search engine crawler
val isSearchEngine = SEOPrerender.isSearchEngineCrawler(userAgent)

// Pre-render a page for search engines
val html = SEOPrerender.prerender(page)

// Generate a sitemap
val sitemapEntries = mapOf(
    "/" to SitemapEntry(
        lastModified = "2023-06-15",
        changeFrequency = ChangeFrequency.WEEKLY,
        priority = 1.0
    ),
    "/about" to SitemapEntry(
        lastModified = "2023-05-20",
        changeFrequency = ChangeFrequency.MONTHLY,
        priority = 0.8
    )
)
val sitemap = SEOPrerender.generateSitemap(sitemapEntries, "https://example.com")
```

## Framework Integration

### Spring Boot Integration

```kotlin
@Controller
class SummonController {
    @GetMapping("/")
    fun home(userAgent: String): String {
        // Check if the request is from a search engine
        val isSearchEngine = SEOPrerender.isSearchEngineCrawler(userAgent)
        
        // Create the page component
        val page = HomePage()
        
        // Create SEO metadata
        val seoMetadata = SeoMetadata(
            title = "Summon - Home",
            description = "Summon is a Kotlin Multiplatform library for static site generation"
        )
        
        // Create a rendering context
        val context = RenderContext(seoMetadata = seoMetadata)
        
        // Render the page differently based on user agent
        return if (isSearchEngine) {
            // Optimized version for search engines
            SEOPrerender.prerender(page, context)
        } else {
            // Regular version for users
            StaticRendering.render(page, context)
        }
    }
}
```

### Spring WebFlux Integration (Reactive)

```kotlin
@RestController
class ReactiveController {
    @GetMapping("/reactive", produces = ["text/html"])
    fun reactivePage(): Flux<String> {
        // Create the page component
        val page = LargePage()
        
        // Create a context
        val context = RenderContext(
            seoMetadata = SeoMetadata(title = "Summon - Reactive Page")
        )
        
        // Get a stream of HTML chunks
        val htmlStream = StreamingSSR.renderStream(page, context)
        
        // Convert the Flow to a Flux
        return Flux.from(htmlStream.asPublisher())
    }
}
```

### Ktor Integration

```kotlin
fun Application.configureRouting() {
    routing {
        get("/") {
            // Create the page component
            val page = HomePage()
            
            // Create a rendering context
            val context = RenderContext(
                seoMetadata = SeoMetadata(title = "Summon - Home")
            )
            
            // Render the page
            val html = StaticRendering.render(page, context)
            
            // Send the response
            call.respondText(html, ContentType.Text.Html)
        }
        
        get("/stream") {
            // Create the page component
            val page = LargePage()
            
            // Create a rendering context
            val context = RenderContext(
                seoMetadata = SeoMetadata(title = "Summon - Streaming")
            )
            
            // Get a stream of HTML chunks
            val htmlStream = StreamingSSR.renderStream(page, context)
            
            // Set up chunked response
            call.response.header(HttpHeaders.TransferEncoding, "chunked")
            call.respondTextWriter(ContentType.Text.Html) {
                htmlStream.collect { chunk ->
                    write(chunk)
                    flush()
                }
            }
        }
    }
}
```

## Configuration

The `RenderContext` class provides various configuration options for rendering:

```kotlin
val context = RenderContext(
    // Enable hydration for client-side reactivation
    enableHydration = true,
    
    // Prefix for hydration IDs
    hydrationIdPrefix = "summon-",
    
    // Additional metadata for the page
    metadata = mapOf(
        "app-version" to "1.0.0",
        "viewport" to "width=device-width, initial-scale=1.0"
    ),
    
    // Enable debug mode for development
    debug = true,
    
    // SEO metadata
    seoMetadata = SeoMetadata(
        title = "Page Title",
        description = "Page description"
    ),
    
    // Initial state for hydration
    initialState = mapOf(
        "counter" to 0,
        "darkMode" to false
    )
)
``` 