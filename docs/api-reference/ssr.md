# Server-Side Rendering (SSR) API Reference

This document provides detailed API reference for Summon's Server-Side Rendering capabilities. SSR enables rendering Summon components on the server to improve SEO, initial page load times, and user experience.

## Core SSR Classes

### `PlatformRenderer`

The core abstraction for rendering Summon components across platforms.

```kotlin
abstract class PlatformRenderer {
    abstract fun renderComposableRoot(content: @Composable () -> Unit): String
    abstract fun renderComposableRootWithHydration(content: @Composable () -> Unit): String
}
```

#### Methods

- `renderComposableRoot(content: @Composable () -> Unit): String`
  - Renders a composable to HTML string
  - **Parameters**: `content` - The root composable to render
  - **Returns**: HTML string representation of the component tree
  - **Platform**: JVM only

- `renderComposableRootWithHydration(content: @Composable () -> Unit): String`
  - Renders a composable with client-side hydration support
  - **Parameters**: `content` - The root composable to render
  - **Returns**: HTML string with hydration scripts included
  - **Platform**: JVM only

- `renderComposableRootWithHydration(state: Any?, content: @Composable () -> Unit): String`
  - Renders a composable with client-side hydration support and initial state injection.
  - **Parameters**: 
    - `state`: The initial state object (should implement `UiState` or be serializable).
    - `content`: The root composable to render.
  - **Returns**: HTML string with hydration scripts and state payload included.
  - **Platform**: JVM only

### `JvmPlatformRenderer`

JVM-specific implementation of `PlatformRenderer` for server-side rendering.

```kotlin
class JvmPlatformRenderer : PlatformRenderer() {
    override fun renderComposableRoot(content: @Composable () -> Unit): String
    override fun renderComposableRootWithHydration(content: @Composable () -> Unit): String
}
```

#### Usage Example

```kotlin
val renderer = JvmPlatformRenderer()
val html = renderer.renderComposableRoot {
    MyApp()
}
```

## SSR Utility Classes

### `ServerSideRenderUtils`

High-level utilities for server-side rendering with additional features.

```kotlin
object ServerSideRenderUtils {
    fun renderPageToString(
        rootComposable: @Composable () -> Unit,
        initialData: Map<String, Any> = emptyMap(),
        includeHydrationScript: Boolean = false
    ): String
    
    fun renderWithSEO(
        rootComposable: @Composable () -> Unit,
        seoMetadata: SeoMetadata
    ): String
}
```

#### Methods

- `renderPageToString(rootComposable, initialData, includeHydrationScript): String`
  - **Parameters**:
    - `rootComposable` - The root composable to render
    - `initialData` - Initial data to pass to the client for hydration
    - `includeHydrationScript` - Whether to include client-side hydration scripts
  - **Returns**: Complete HTML page string with proper document structure
  - **Use Case**: High-level page rendering with optional hydration

- `renderWithSEO(rootComposable, seoMetadata): String`
  - **Parameters**:
    - `rootComposable` - The root composable to render
    - `seoMetadata` - SEO metadata for the page
  - **Returns**: HTML string with SEO meta tags included
  - **Use Case**: SEO-optimized server-side rendering

### `RenderContext`

Configuration context for SSR operations.

```kotlin
data class RenderContext(
    val enableHydration: Boolean = false,
    val seoMetadata: SeoMetadata? = null,
    val customMetaTags: Map<String, String> = emptyMap(),
    val includeViewport: Boolean = true,
    val charset: String = "UTF-8"
)
```

#### Properties

- `enableHydration: Boolean` - Enable client-side hydration support
- `seoMetadata: SeoMetadata?` - SEO metadata configuration
- `customMetaTags: Map<String, String>` - Additional custom meta tags
- `includeViewport: Boolean` - Include viewport meta tag for responsive design
- `charset: String` - Character encoding for the document

## SEO Support Classes

### `SeoMetadata`

Comprehensive SEO metadata configuration.

```kotlin
data class SeoMetadata(
    val title: String,
    val description: String? = null,
    val keywords: List<String> = emptyList(),
    val author: String? = null,
    val canonical: String? = null,
    val openGraph: OpenGraphMetadata? = null,
    val twitterCard: TwitterCardMetadata? = null,
    val robots: String? = null,
    val viewport: String? = null
)
```

#### Properties

- `title: String` - Page title
- `description: String?` - Meta description for search engines
- `keywords: List<String>` - SEO keywords
- `author: String?` - Content author
- `canonical: String?` - Canonical URL for the page
- `openGraph: OpenGraphMetadata?` - Open Graph metadata for social sharing
- `twitterCard: TwitterCardMetadata?` - Twitter Card metadata
- `robots: String?` - Robots meta tag content
- `viewport: String?` - Viewport configuration

### `OpenGraphMetadata`

Open Graph protocol metadata for rich social media previews.

```kotlin
data class OpenGraphMetadata(
    val title: String,
    val description: String? = null,
    val type: String = "website",
    val url: String? = null,
    val image: String? = null,
    val imageAlt: String? = null,
    val siteName: String? = null,
    val locale: String? = null
)
```

#### Properties

- `title: String` - Open Graph title
- `description: String?` - Open Graph description
- `type: String` - Content type (website, article, etc.)
- `url: String?` - Canonical URL
- `image: String?` - Preview image URL
- `imageAlt: String?` - Alt text for preview image
- `siteName: String?` - Site name
- `locale: String?` - Content locale

### `TwitterCardMetadata`

Twitter Card metadata for Twitter sharing previews.

```kotlin
data class TwitterCardMetadata(
    val card: String = "summary",
    val site: String? = null,
    val creator: String? = null,
    val title: String? = null,
    val description: String? = null,
    val image: String? = null
)
```

#### Properties

- `card: String` - Twitter card type (summary, summary_large_image, etc.)
- `site: String?` - Twitter handle for the website
- `creator: String?` - Twitter handle for content creator
- `title: String?` - Twitter card title
- `description: String?` - Twitter card description
- `image: String?` - Twitter card image URL

## State Management in SSR

### `remember` Function

State management during server-side rendering.

```kotlin
@Composable
fun <T> remember(calculation: () -> T): T
```

#### Usage in SSR

```kotlin
@Composable
fun ServerRenderedComponent() {
    val state = remember { mutableStateOf("initial") }
    Text("Value: ${state.value}")
}
```

**Note**: During SSR, `remember` creates state that exists only for the duration of the render. For persistent state across client hydration, use appropriate state management patterns.

### `mutableStateOf` Function

Creates mutable state that works during SSR.

```kotlin
fun <T> mutableStateOf(value: T): MutableState<T>
```

#### SSR Behavior

- State is created and managed during server rendering
- Initial values are serialized for client hydration when enabled
- State changes during SSR affect the rendered HTML output

## Composition Context

### `CompositionLocal`

Provides composition-local values during SSR.

```kotlin
object CompositionLocal {
    fun <T> staticCompositionLocalOf(defaultFactory: () -> T): CompositionLocal<T>
    fun <T> compositionLocalOf(defaultFactory: () -> T): CompositionLocal<T>
}
```

#### SSR-Specific Locals

- `LocalPlatformRenderer` - Provides access to the current platform renderer
- `LocalRenderContext` - Provides access to the current render context

### `LocalPlatformRenderer`

Composition local for accessing the platform renderer.

```kotlin
val LocalPlatformRenderer: CompositionLocal<PlatformRenderer?>
```

#### Usage

```kotlin
@Composable
fun ComponentNeedingRenderer() {
    val renderer = LocalPlatformRenderer.current
    // Use renderer for nested rendering operations
}
```

## Error Handling

### SSR Error Types

Common errors during server-side rendering:

- `ComponentNotFoundException` - When a required component or renderer is not found
- `CompositionException` - When composition setup fails
- `RenderingException` - When HTML generation fails

### Error Handling Patterns

```kotlin
try {
    val html = renderer.renderComposableRoot { MyApp() }
} catch (e: ComponentNotFoundException) {
    // Handle missing component
} catch (e: CompositionException) {
    // Handle composition errors
} catch (e: RenderingException) {
    // Handle rendering errors
}
```

## Performance Considerations

### Memory Management

- SSR creates temporary composition contexts that are cleaned up after rendering
- Large component trees are handled efficiently with lazy evaluation
- Memory usage scales linearly with component complexity

### Rendering Performance

- Simple components: < 1ms per component
- Complex components: 5-10ms per component
- Large pages (100+ components): ~100-500ms total render time

### Best Practices

1. **Minimize Server State**: Keep server-side state minimal for faster rendering
2. **Lazy Loading**: Use lazy evaluation for expensive computations
3. **Component Reuse**: Design components for efficient reuse across renders
4. **Error Boundaries**: Implement proper error handling to prevent render failures

## Integration Examples

### Ktor Integration

```kotlin
routing {
    get("/") {
        val renderer = JvmPlatformRenderer()
        val html = renderer.renderComposableRoot {
            HomePage()
        }
        call.respondText(html, ContentType.Text.Html)
    }
}
```

### Spring Boot Integration

```kotlin
@Controller
class PageController {
    @GetMapping("/")
    fun home(model: Model): String {
        val renderer = JvmPlatformRenderer()
        val html = renderer.renderComposableRoot {
            HomePage()
        }
        model.addAttribute("content", html)
        return "layout"
    }
}
```

### Quarkus Integration

```kotlin
@Path("/")
class PageResource {
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun home(): String {
        val renderer = JvmPlatformRenderer()
        return renderer.renderComposableRoot {
            HomePage()
        }
    }
}
```

## Testing SSR

### Test Utilities

```kotlin
class MockPlatformRenderer : PlatformRenderer() {
    override fun renderComposableRoot(content: @Composable () -> Unit): String {
        // Mock implementation for testing
    }
}
```

### Testing Patterns

```kotlin
@Test
fun testSSRComponent() {
    val renderer = JvmPlatformRenderer()
    val html = renderer.renderComposableRoot {
        TestComponent()
    }
    assertTrue(html.contains("expected content"))
}
```

## Migration and Compatibility

### From Client-Only to SSR

1. Ensure components are pure and side-effect free during initial render
2. Move side effects to `LaunchedEffect` or `DisposableEffect`
3. Test components with SSR renderer to verify compatibility

### Version Compatibility

- SSR support available from version 0.3.2.0+
- Backward compatible with existing client-only code
- Progressive enhancement approach - add SSR without breaking existing functionality

## Troubleshooting

### Common Issues

1. **"No Composer found"**: Ensure proper composition context setup
2. **"Missing Platform Renderer"**: Verify renderer initialization
3. **"State not preserved"**: Check hydration configuration
4. **"HTML malformed"**: Validate component HTML structure

### Debugging Tools

- Enable debug logging for composition context
- Use HTML validators to check generated output
- Test with different component combinations
- Monitor memory usage during rendering

---

## SiteBundler

Utility for bundling rendered HTML and CSS into portable static site packages.

### Object Definition

```kotlin
expect object SiteBundler {
    fun bundleSite(html: String, css: String): ByteArray
    fun bundleSite(files: Map<String, ByteArray>): ByteArray
}
```

### Methods

#### `bundleSite(html: String, css: String): ByteArray`
Bundles HTML and CSS into a zip archive containing:
- `index.html` with the provided HTML content
- `style.css` with the provided CSS content

**Parameters:**
- `html` - The HTML content for index.html
- `css` - The CSS content for style.css

**Returns:** ByteArray containing the zip archive

#### `bundleSite(files: Map<String, ByteArray>): ByteArray`
Bundles multiple files into a zip archive.

**Parameters:**
- `files` - Map of file paths to file contents

**Returns:** ByteArray containing the zip archive

### Usage Example

```kotlin
// Bundle a simple site
val html = renderer.renderComposableRoot { MyApp() }
val css = generateStyles()
val bundle = SiteBundler.bundleSite(html, css)

// Write to file
File("site.zip").writeBytes(bundle)

// Bundle with additional assets
val bundle = SiteBundler.bundleSite(mapOf(
    "index.html" to html.encodeToByteArray(),
    "styles/main.css" to css.encodeToByteArray(),
    "scripts/app.js" to js.encodeToByteArray(),
    "images/logo.png" to logoBytes
))
```

---

## See Also

- [SSR Guide](../ssr-guide.md) - Comprehensive SSR implementation guide
- [Integration Guides](../integration-guides.md) - Framework-specific integration patterns
- [Components API](components.md) - Component reference for SSR-compatible components
- [State API](state.md) - State management patterns for SSR