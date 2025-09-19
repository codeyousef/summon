package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.getPlatformRenderer
import kotlinx.coroutines.flow.Flow

/**
 * Platform-independent HTML builder for server-side rendering.
 *
 * HtmlBuilder provides a simple interface for constructing HTML strings
 * during server-side rendering operations. It abstracts HTML generation
 * to enable platform-specific optimizations while maintaining a consistent API.
 *
 * ## Usage
 *
 * ```kotlin
 * val builder = createHTML()
 * builder.append("<div>")
 * builder.append("Hello, World!")
 * builder.append("</div>")
 * val html = builder.finalize()
 * ```
 *
 * ## Implementation Notes
 *
 * Implementations should be optimized for:
 * - **Memory Efficiency**: Minimal memory allocations during building
 * - **Performance**: Fast string concatenation and building
 * - **Safety**: Proper escaping and encoding of content
 *
 * @see SimpleHtmlBuilder for default implementation
 * @see createHTML for builder factory
 * @since 1.0.0
 */
interface HtmlBuilder {
    /**
     * Appends content to the HTML being built.
     *
     * Content is added in the order of append calls. The implementation
     * should handle HTML encoding and escaping as appropriate.
     *
     * @param content HTML content to append (should be properly escaped)
     */
    fun append(content: String)

    /**
     * Finalizes the HTML building process and returns the complete HTML string.
     *
     * After calling finalize(), the builder should not be used for further
     * append operations. The returned HTML should be well-formed and ready
     * for client delivery.
     *
     * @return Complete HTML string ready for rendering
     */
    fun finalize(): String
}

/**
 * A basic implementation of HtmlBuilder that uses a StringBuilder
 */
class SimpleHtmlBuilder : HtmlBuilder {
    private val builder = StringBuilder()

    override fun append(content: String) {
        builder.append(content)
    }

    override fun finalize(): String {
        return builder.toString()
    }
}

/**
 * Create a new HTML builder
 */
fun createHTML(): HtmlBuilder {
    return SimpleHtmlBuilder()
}

/**
 * Core interface for server-side rendering of Summon composables to HTML.
 *
 * ServerSideRenderer provides the fundamental contract for converting Summon
 * composable functions into HTML strings that can be served to clients. It
 * supports various rendering strategies including static generation, dynamic
 * server-side rendering, and hybrid approaches.
 *
 * ## Rendering Strategies
 *
 * Different implementations support various rendering approaches:
 * - **Static Rendering**: Pre-generate HTML at build time
 * - **Dynamic Rendering**: Generate HTML per request with server state
 * - **Streaming Rendering**: Stream HTML chunks for faster perceived performance
 * - **Hydration-Ready**: Include client-side activation markers
 *
 * ## Context-Aware Rendering
 *
 * Rendering can be customized through RenderContext:
 * - **SEO Metadata**: Title, description, Open Graph, Twitter Cards
 * - **Hydration**: Client-side activation configuration
 * - **State Management**: Initial state for client hydration
 * - **Head Elements**: Custom head content injection
 *
 * ## Usage Examples
 *
 * ### Basic Page Rendering
 * ```kotlin
 * val renderer = createServerSideRenderer()
 * val html = renderer.render(
 *     composable = { HomePage() },
 *     context = RenderContext(
 *         seoMetadata = SeoMetadata(
 *             title = "Home - My App",
 *             description = "Welcome to my application"
 *         )
 *     )
 * )
 * ```
 *
 * ### Dynamic Content with State
 * ```kotlin
 * val renderer = createServerSideRenderer()
 * val userState = mapOf("userId" to "123", "userName" to "John")
 *
 * val html = renderer.render(
 *     composable = { UserProfile() },
 *     context = RenderContext(
 *         enableHydration = true,
 *         initialState = userState,
 *         seoMetadata = SeoMetadata(
 *             title = "Profile - ${userState["userName"]}",
 *             description = "User profile page"
 *         )
 *     )
 * )
 * ```
 *
 * ### Framework Integration
 * ```kotlin
 * // Ktor integration
 * fun Routing.pages() {
 *     get("/") {
 *         val html = renderer.render({ HomePage() })
 *         call.respondText(html, ContentType.Text.Html)
 *     }
 * }
 *
 * // Spring Boot integration
 * @GetMapping("/")
 * fun home(): ResponseEntity<String> {
 *     val html = renderer.render({ HomePage() })
 *     return ResponseEntity.ok()
 *         .contentType(MediaType.TEXT_HTML)
 *         .body(html)
 * }
 * ```
 *
 * ## Performance Considerations
 *
 * - **Caching**: Implement caching strategies for static content
 * - **Streaming**: Use streaming rendering for large pages
 * - **Preloading**: Preload critical resources in rendered HTML
 * - **Compression**: Apply gzip/brotli compression to output
 *
 * @see RenderContext for rendering configuration
 * @see StreamingServerSideRenderer for streaming implementation
 * @see HydrationSupport for client activation
 * @since 1.0.0
 */
interface ServerSideRenderer {
    /**
     * Renders a Summon composable function to a complete HTML string.
     *
     * This method performs the core server-side rendering operation, converting
     * a composable function tree into HTML that can be served to clients. The
     * rendering process includes layout calculation, content generation, and
     * optional metadata injection.
     *
     * ## Rendering Process
     *
     * 1. **Composition**: Execute the composable function tree
     * 2. **Layout**: Calculate positioning and sizing of elements
     * 3. **Content Generation**: Convert elements to HTML markup
     * 4. **Metadata Injection**: Add SEO, hydration, and head elements
     * 5. **Document Assembly**: Combine into complete HTML document
     *
     * ## Context Configuration
     *
     * The RenderContext parameter controls various aspects of rendering:
     *
     * ```kotlin
     * val context = RenderContext(
     *     enableHydration = true,               // Enable client-side activation
     *     seoMetadata = SeoMetadata(
     *         title = "Page Title",
     *         description = "Page description",
     *         openGraph = OpenGraphMetadata(...)
     *     ),
     *     initialState = mapOf("key" to "value"), // State for hydration
     *     debug = true                          // Include debug information
     * )
     * ```
     *
     * ## Output Format
     *
     * The returned HTML includes:
     * - Complete HTML document with DOCTYPE
     * - Head section with metadata and stylesheets
     * - Body section with rendered content
     * - Optional hydration scripts and data
     * - SEO optimization tags
     *
     * ## Error Handling
     *
     * Rendering errors are handled gracefully:
     * - Component errors result in fallback content
     * - Missing resources are logged but don't break rendering
     * - Invalid context parameters use sensible defaults
     *
     * ## Performance
     *
     * For optimal performance:
     * - Cache rendered output when possible
     * - Use streaming rendering for large pages
     * - Minimize expensive operations in composables
     * - Consider partial hydration for interactive elements only
     *
     * @param composable Root composable function to render
     * @param context Rendering configuration and metadata
     * @return Complete HTML document ready for client delivery
     * @throws RenderingException if critical rendering errors occur
     * @see RenderContext for configuration options
     * @see StreamingServerSideRenderer for streaming alternative
     * @since 1.0.0
     */
    fun render(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): String
}

/**
 * Context object for rendering with additional metadata
 */
class RenderContext(
    /**
     * Whether to include hydration markers in the rendered HTML
     */
    val enableHydration: Boolean = false,

    /**
     * ID prefix for hydration markers
     */
    val hydrationIdPrefix: String = "summon-",

    /**
     * Additional metadata to include in the rendered HTML
     */
    val metadata: Map<String, String> = emptyMap(),

    /**
     * Whether to include debugging information in the rendered HTML
     */
    val debug: Boolean = false,

    /**
     * SEO-related metadata
     */
    val seoMetadata: SeoMetadata = SeoMetadata(),

    /**
     * Optional state bundle to initialize client-side state
     */
    val initialState: Map<String, Any?> = emptyMap(),

    /**
     * List of head elements collected during rendering
     */
    val headElements: MutableList<@Composable () -> Unit> = mutableListOf()
)

/**
 * SEO-related metadata for rendering
 */
class SeoMetadata(
    val title: String = "",
    val description: String = "",
    val keywords: List<String> = emptyList(),
    val canonical: String = "",
    val openGraph: OpenGraphMetadata = OpenGraphMetadata(),
    val twitterCard: TwitterCardMetadata = TwitterCardMetadata(),
    val structuredData: String = "", // JSON-LD as string
    val robots: String = "index, follow",
    val customMetaTags: Map<String, String> = emptyMap()
)

/**
 * OpenGraph metadata for social sharing
 */
class OpenGraphMetadata(
    val title: String = "",
    val description: String = "",
    val type: String = "website",
    val url: String = "",
    val image: String = "",
    val siteName: String = ""
)

/**
 * Twitter card metadata for Twitter sharing
 */
class TwitterCardMetadata(
    val card: String = "summary",
    val site: String = "",
    val creator: String = "",
    val title: String = "",
    val description: String = "",
    val image: String = ""
)

/**
 * Hydration strategy for client-side reactivation of server-rendered HTML
 */
enum class HydrationStrategy {
    /**
     * No hydration, static HTML only
     */
    NONE,

    /**
     * Full hydration of the entire page
     */
    FULL,

    /**
     * Selective hydration of interactive elements only
     */
    PARTIAL,

    /**
     * Progressive hydration based on visibility
     */
    PROGRESSIVE
}

/**
 * Base interface for hydration support
 */
interface HydrationSupport {
    /**
     * Generate hydration data for client-side use
     *
     * @param composable The composable to generate hydration data for
     * @param strategy The hydration strategy to use
     * @return Client-side hydration data as a string (typically JSON)
     */
    fun generateHydrationData(
        composable: @Composable () -> Unit,
        strategy: HydrationStrategy
    ): String

    /**
     * Add hydration markers to rendered HTML
     *
     * @param html The rendered HTML
     * @param hydrationData The hydration data as a string
     * @return HTML with hydration markers
     */
    fun addHydrationMarkers(html: String, hydrationData: String): String
}

/**
 * Interface for streaming SSR implementations
 */
interface StreamingServerSideRenderer {
    /**
     * Render a composable to an HTML stream
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return Flow of HTML chunks
     */
    fun renderStream(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): Flow<String>
}

/**
 * Core utilities for server-side rendering (SSR).
 * Replaces old ServerSideRenderer object/class.
 */
object ServerSideRenderUtils {

    /**
     * Renders a composable function to a string, suitable for SSR.
     *
     * @param rootComposable The root composable function of the page/application.
     * @param initialData Optional initial data to be used during rendering (e.g., from server state).
     * @param includeHydrationScript Whether to include a script tag with hydration data.
     * @return The fully rendered HTML string.
     */
    fun renderPageToString(
        rootComposable: @Composable () -> Unit,
        initialData: Map<String, Any?> = emptyMap(),
        includeHydrationScript: Boolean = true
    ): String {
        println("ServerSideRenderUtils.renderPageToString called.")

        val context = RenderContext(initialState = initialData, enableHydration = includeHydrationScript)

        // Get the platform renderer (create one if needed)
        val platformRenderer = try {
            getPlatformRenderer()
        } catch (e: Exception) {
            // If no renderer is set, create a new one
            code.yousef.summon.runtime.PlatformRenderer()
        }

        // Use the global renderToString helper which handles renderComposableRoot correctly
        val renderResult = renderToString(platformRenderer, rootComposable)
        val bodyContent = renderResult.html
        val headElements = renderResult.headElements // Head elements are now collected by renderToString

        // Collect head elements from the renderer
        val headContent = headElements.joinToString("\n") // Join collected head strings

        // 4. Optionally generate hydration data
        val hydrationScript = if (includeHydrationScript) {
            generateHydrationScript(initialData)
        } else {
            ""
        }

        // 5. Construct the final HTML document with SEO metadata from context
        // Extract basic SEO metadata
        val titleValue = context.seoMetadata.title.ifEmpty { "SSR Page" }
        val descriptionValue = context.seoMetadata.description
        val canonicalValue = context.seoMetadata.canonical

        // Get additional SEO metadata
        val keywordsValue = context.seoMetadata.keywords.joinToString(", ")
        val robotsValue = context.seoMetadata.robots

        // Get OpenGraph metadata
        val ogTitle = context.seoMetadata.openGraph.title.ifEmpty { titleValue }
        val ogDescription = context.seoMetadata.openGraph.description.ifEmpty { descriptionValue }
        val ogType = context.seoMetadata.openGraph.type
        val ogUrl = context.seoMetadata.openGraph.url.ifEmpty { canonicalValue }
        val ogImage = context.seoMetadata.openGraph.image

        // Get Twitter Card metadata
        val twitterCard = context.seoMetadata.twitterCard.card
        val twitterSite = context.seoMetadata.twitterCard.site
        val twitterCreator = context.seoMetadata.twitterCard.creator

        // Collect custom meta tags
        val customMetaTags = context.seoMetadata.customMetaTags.entries
            .joinToString("\n    ") { (key, value) ->
                if (key.startsWith("og:") || key.startsWith("twitter:")) {
                    "<meta property=\"$key\" content=\"$value\">"
                } else {
                    "<meta name=\"$key\" content=\"$value\">"
                }
            }

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$titleValue</title>

            <!-- Basic SEO -->
            ${if (descriptionValue.isNotEmpty()) "<meta name=\"description\" content=\"$descriptionValue\">" else ""}
            ${if (keywordsValue.isNotEmpty()) "<meta name=\"keywords\" content=\"$keywordsValue\">" else ""}
            ${if (robotsValue.isNotEmpty()) "<meta name=\"robots\" content=\"$robotsValue\">" else ""}
            ${if (canonicalValue.isNotEmpty()) "<link rel=\"canonical\" href=\"$canonicalValue\">" else ""}

            <!-- Open Graph / Facebook -->
            <meta property="og:type" content="$ogType">
            <meta property="og:title" content="$ogTitle">
            ${if (ogDescription.isNotEmpty()) "<meta property=\"og:description\" content=\"$ogDescription\">" else ""}
            ${if (ogImage.isNotEmpty()) "<meta property=\"og:image\" content=\"$ogImage\">" else ""}
            ${if (ogUrl.isNotEmpty()) "<meta property=\"og:url\" content=\"$ogUrl\">" else ""}

            <!-- Twitter -->
            <meta name="twitter:card" content="$twitterCard">
            ${if (twitterSite.isNotEmpty()) "<meta name=\"twitter:site\" content=\"$twitterSite\">" else ""}
            ${if (twitterCreator.isNotEmpty()) "<meta name=\"twitter:creator\" content=\"$twitterCreator\">" else ""}

            <!-- Custom meta tags -->
            $customMetaTags

            <!-- Head elements -->
            $headContent
        </head>
        <body>
            <div id="root">$bodyContent</div>
            $hydrationScript
        </body>
        </html>
        """.trimIndent()
    }

    private fun generateHydrationScript(initialData: Map<String, Any?>): String {
        if (initialData.isEmpty()) return ""
        val stateJson = SerializationUtils.serializeInitialState(initialData)
        return """
        <script>
            window.__SUMMON_INITIAL_STATE__ = $stateJson;
        </script>
        """.trimIndent()
    }

    // Remove the old internal renderToString as it's superseded by the global one
    /*
    private fun renderToString(composable: @Composable () -> Unit): String {
        val platformRenderer = getPlatformRenderer()
        val htmlBuilder = createHTML()
        // Incorrect call: platformRenderer.renderComposable(composable, htmlBuilder)
        // Correct approach uses renderComposableRoot or the global renderToString helper.
        return htmlBuilder.finalize()
    }
    */

}
