package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.MigratedPlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer
import code.yousef.summon.runtime.CommonComposer
import code.yousef.summon.runtime.ComposeManagerContext
import code.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.html.stream.createHTML
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
        
        // 1. Set up server-side Composer
        val composer = CommonComposer()
        val htmlBuilder = createHTML()
        
        // 2. Execute rootComposable within the CompositionContext to collect content
        ComposeManagerContext.withComposer(composer) {
            rootComposable()
        }
        
        // 3. Get the main HTML content from the renderer
        val platformRenderer = code.yousef.summon.runtime.getPlatformRenderer()
        val bodyContent = createHTML().apply {
            // Platform renderer will add content to the HTML builder
            platformRenderer.renderComposable(rootComposable, this)
        }.finalize()
        
        // 4. Optionally generate hydration data
        val hydrationScript = if (includeHydrationScript) {
            """
            <script id="summon-hydration-data" type="application/json">
                ${HydrationUtils.generateHydrationScript(rootComposable)}
            </script>
            <script src="/summon-hydration.js"></script>
            """
        } else {
            ""
        }
        
        // 5. Construct the full HTML document manually (without kotlinx.html DSL)
        val headContent = buildHeadContent(context, initialData)
        val bodyOpenTag = "<body>"
        val rootDiv = "<div id=\"summon-root\">${bodyContent}</div>"
        val stateScript = if (initialData.isNotEmpty()) {
            "<script>window.__SUMMON_INITIAL_STATE__ = ${serializeStateToJson(initialData)};</script>"
        } else {
            ""
        }
        val bodyCloseTag = "</body>"
        
        return "<!DOCTYPE html>\n<html>\n$headContent\n$bodyOpenTag\n$rootDiv\n$stateScript\n$hydrationScript\n$bodyCloseTag\n</html>"
    }
    
    /**
     * Builds the head content for an HTML document
     */
    private fun buildHeadContent(context: RenderContext, initialData: Map<String, Any?>): String {
        val sb = StringBuilder()
        
        sb.append("<head>\n")
        sb.append("  <meta charset=\"UTF-8\">\n")
        sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
        
        // Add title
        val title = if (initialData.containsKey("title")) {
            initialData["title"] as String
        } else {
            "Summon SSR Page"
        }
        sb.append("  <title>$title</title>\n")
        
        // Add description if available
        if (initialData.containsKey("description")) {
            sb.append("  <meta name=\"description\" content=\"${initialData["description"]}\">\n")
        }
        
        // Add canonical if available
        if (initialData.containsKey("canonical")) {
            sb.append("  <link rel=\"canonical\" href=\"${initialData["canonical"]}\">\n")
        }
        
        // Add stylesheets if available
        if (initialData.containsKey("stylesheets")) {
            (initialData["stylesheets"] as? List<String>)?.forEach { stylesheet ->
                sb.append("  <link rel=\"stylesheet\" href=\"$stylesheet\">\n")
            }
        }
        
        sb.append("</head>")
        
        return sb.toString()
    }
    
    /**
     * Serializes the state to JSON for client-side hydration
     */
    private fun serializeStateToJson(state: Map<String, Any?>): String {
        // Simple implementation - in a real app, use a proper JSON serializer
        return buildString {
            append("{")
            state.entries.forEachIndexed { index, (key, value) ->
                if (index > 0) append(",")
                append("\"$key\":")
                when (value) {
                    is String -> append("\"${value.replace("\"", "\\\"").replace("\n", "\\n")}\"")
                    is Number, is Boolean -> append(value)
                    null -> append("null")
                    else -> append("\"${value.toString().replace("\"", "\\\"").replace("\n", "\\n")}\"")
                }
            }
            append("}")
        }
    }
    
    /**
     * Helper object for hydration script generation
     */
    private object HydrationUtils {
        /**
         * Generates a hydration script for client-side rehydration
         */
        fun generateHydrationScript(rootComposable: @Composable () -> Unit): String {
            // In a real implementation, this would generate proper hydration data
            // For this example, we'll return a placeholder
            return "{ \"hydrationVersion\": 1, \"components\": [] }"
        }
    }
}

// Removed old SSRServer or related classes that implemented Composable 
