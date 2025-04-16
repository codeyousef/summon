package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.CommonComposer
import code.yousef.summon.runtime.ComposeManagerContext
import code.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

/**
 * Simple HTML builder for platform-independent HTML generation
 */
interface HtmlBuilder {
    /**
     * Add content to the HTML
     */
    fun append(content: String)
    
    /**
     * Finalize and return the HTML string
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
 * Base interface for all server-side rendering strategies in Summon
 */
interface ServerSideRenderer {
    /**
     * Render a composable to HTML
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return The generated HTML as a string
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
        
        // Get the platform renderer (assumes one is available/set)
        val platformRenderer = getPlatformRenderer()

        // Use the global renderToString helper which handles renderComposableRoot correctly
        val renderResult = renderToString(platformRenderer, rootComposable)
        val bodyContent = renderResult.html
        val headElements = renderResult.headElements // Head elements are now collected by renderToString

        // TODO: Collect head elements properly via context or renderer capabilities
        // val headContent = context.headElements.joinToString("\n") { /* Render head composable? */ "" }
        val headContent = headElements.joinToString("\n") // Join collected head strings
        
        // 4. Optionally generate hydration data
        val hydrationScript = if (includeHydrationScript) {
            generateHydrationScript(initialData)
        } else {
            ""
        }
        
        // 5. Construct the final HTML document
        // TODO: Integrate SEO metadata from context
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>SSR Page</title>
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
        // Basic JSON-like representation (needs proper serialization)
        val stateJson = initialData.entries.joinToString(",\n") { 
            "\"${it.key}\": ${serializeValue(it.value)}"
        }
        return """
        <script>
            window.__SUMMON_INITIAL_STATE__ = {\n$stateJson\n};
        </script>
        """.trimIndent()
    }

    // Basic serialization placeholder
    private fun serializeValue(value: Any?): String {
        return when (value) {
            is String -> "\"${value.replace("\"", "\\\"")}\""
            is Number, is Boolean -> value.toString()
            null -> "null"
            else -> "\"[unsupported object]$\"" // Placeholder
        }
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

// Removed old SSRServer or related classes that implemented Composable 
