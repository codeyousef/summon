package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.security.PublicHydrationState
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
 * val HTML = builder.finalize()
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

    /**
     * Executes the append operation.
     *
     * @param content Composable content emitted by this API.
     */
    override fun append(content: String) {
        builder.append(content)
    }

    /**
     * Executes the finalize operation.
     *
     * @return The resulting value.
     */
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
 * val HTML = renderer.render(
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
 * ### Dynamic Content with Public State
 * ```kotlin
 * val userState = mapOfCompat("userId" to "123", "userName" to "John")
 *
 * val HTML = renderer.render(
 *     composable = { UserProfile() },
 *     context = RenderContext(
 *         enableHydration = true,
 *         publicState = PublicHydrationState("""{"theme":"dark"}"""),
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
 *         val HTML = renderer.render({ HomePage() })
 *         call.respondText(HTML, ContentType.Text.Html)
 *     }
 * }
 *
 * // Spring Boot integration
 * @GetMapping("/")
 * fun home(): ResponseEntity<String> {
 *     val HTML = renderer.render({ HomePage() })
 *     return ResponseEntity.ok()
 *         .contentType(MediaType.TEXT_HTML)
 *         .body(HTML)
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
     *     publicState = PublicHydrationState("""{"theme":"dark"}"""),
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
     * Rendering failures are propagated to the caller.
     * @see RenderContext for configuration options
     * @see StreamingServerSideRenderer for streaming alternative
     * @since 1.0.0
     */
    fun render(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): String
}

/**
 * Context object for rendering with additional metadata

 * @property enableHydration The enable hydration value.
 * @property hydrationIdPrefix The hydration id prefix value.
 * @property metadata The metadata value.
 * @property debug The debug value.
 * @property seoMetadata The seo metadata value.
 * @property publicState The public state value.
 * @property headElements The head elements value.
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
     * Explicitly public state permitted to cross the SSR boundary.
     */
    val publicState: PublicHydrationState? = null,

    /**
     * List of head elements collected during rendering
     */
    val headElements: MutableList<@Composable () -> Unit> = mutableListOf()
)

/**
 * SEO-related metadata for rendering

 * @property title The title value.
 * @property description The description value.
 * @property keywords The keywords value.
 * @property canonical The canonical value.
 * @property openGraph The open graph value.
 * @property twitterCard The twitter card value.
 * @property structuredData The structured data value.
 * @property robots The robots value.
 * @property customMetaTags The custom meta tags value.
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

 * @property title The title value.
 * @property description The description value.
 * @property type The type value.
 * @property url Target URL.
 * @property image The image value.
 * @property siteName The site name value.
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

 * @property card The card value.
 * @property site The site value.
 * @property creator The creator value.
 * @property title The title value.
 * @property description The description value.
 * @property image The image value.
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
     * @param publicState Explicitly public state permitted in the HTML response.
     * @param includeHydrationScript Whether to include inert hydration data.
     * @param seoMetadata Escaped metadata emitted into the document head.
     * @return The fully rendered HTML string.
     */
    fun renderPageToString(
        rootComposable: @Composable () -> Unit,
        publicState: PublicHydrationState? = null,
        includeHydrationScript: Boolean = true,
        seoMetadata: SeoMetadata = SeoMetadata(),
    ): String {
        val context = RenderContext(
            publicState = publicState,
            enableHydration = includeHydrationScript,
            seoMetadata = seoMetadata,
        )

        // Always create a fresh PlatformRenderer for SSR operations
        // This ensures isolation between SSR calls and prevents state pollution
        // from previous rendering operations
        val platformRenderer = codes.yousef.summon.runtime.PlatformRenderer()

        // Use the global renderToString helper which handles renderComposableRoot correctly
        val renderResult = renderToString(platformRenderer, rootComposable)
        val bodyContent = renderResult.html
        val headElements = renderResult.headElements // Head elements are now collected by renderToString

        // Collect head elements from the renderer
        val headContent = headElements.joinToString("\n") // Join collected head strings

        // 4. Optionally generate hydration data
        val hydrationScript = if (includeHydrationScript) {
            generateHydrationScript(publicState)
        } else {
            ""
        }

        // 5. Construct the final HTML document with SEO metadata from context
        // Extract basic SEO metadata
        val titleValue = escapeSsrHtml(context.seoMetadata.title.ifEmpty { "SSR Page" })
        val descriptionValue = escapeSsrHtml(context.seoMetadata.description)
        val canonicalValue = escapeSsrHtml(context.seoMetadata.canonical)

        // Get additional SEO metadata
        val keywordsValue = escapeSsrHtml(context.seoMetadata.keywords.joinToString(", "))
        val robotsValue = escapeSsrHtml(context.seoMetadata.robots)

        // Get OpenGraph metadata
        val ogTitle = escapeSsrHtml(context.seoMetadata.openGraph.title.ifEmpty { context.seoMetadata.title.ifEmpty { "SSR Page" } })
        val ogDescription = escapeSsrHtml(context.seoMetadata.openGraph.description.ifEmpty { context.seoMetadata.description })
        val ogType = escapeSsrHtml(context.seoMetadata.openGraph.type)
        val ogUrl = escapeSsrHtml(context.seoMetadata.openGraph.url.ifEmpty { context.seoMetadata.canonical })
        val ogImage = escapeSsrHtml(context.seoMetadata.openGraph.image)

        // Get Twitter Card metadata
        val twitterCard = escapeSsrHtml(context.seoMetadata.twitterCard.card)
        val twitterSite = escapeSsrHtml(context.seoMetadata.twitterCard.site)
        val twitterCreator = escapeSsrHtml(context.seoMetadata.twitterCard.creator)

        // Collect custom meta tags
        val customMetaTags = context.seoMetadata.customMetaTags.entries
            .joinToString("\n    ") { (key, value) ->
                val safeKey = escapeSsrHtml(key)
                val safeValue = escapeSsrHtml(value)
                if (key.startsWith("og:") || key.startsWith("twitter:")) {
                    "<meta property=\"$safeKey\" content=\"$safeValue\">"
                } else {
                    "<meta name=\"$safeKey\" content=\"$safeValue\">"
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

    private fun generateHydrationScript(publicState: PublicHydrationState?): String {
        publicState ?: return ""
        val stateJson = scriptSafeJson(publicState.json)
        return """<script id="summon-public-state" type="application/json">$stateJson</script>"""
    }

}

internal fun escapeSsrHtml(value: String): String {
    var firstEscaped = -1
    for (index in value.indices) {
        if (value[index] == '&' || value[index] == '<' || value[index] == '>' ||
            value[index] == '"' || value[index] == '\''
        ) {
            firstEscaped = index
            break
        }
    }
    if (firstEscaped < 0) return value
    return buildString(value.length + 16) {
        append(value, 0, firstEscaped)
        for (index in firstEscaped until value.length) {
            when (val character = value[index]) {
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                '"' -> append("&quot;")
                '\'' -> append("&#39;")
                else -> append(character)
            }
        }
    }
}

internal fun scriptSafeJson(json: String): String {
    require(json.length <= 65_536) { "SSR hydration JSON exceeds 64 KiB" }
    return buildString(json.length) {
        json.forEach { character ->
            when (character) {
                '<' -> append("\\u003c")
                '\u2028' -> append("\\u2028")
                '\u2029' -> append("\\u2029")
                else -> append(character)
            }
        }
    }
}
