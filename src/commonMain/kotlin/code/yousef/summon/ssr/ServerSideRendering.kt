package code.yousef.summon.ssr

import code.yousef.summon.core.Composable
import kotlinx.coroutines.flow.Flow

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
    fun render(composable: Composable, context: RenderContext = RenderContext()): String
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
    val initialState: Map<String, Any?> = emptyMap()
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
        composable: Composable,
        strategy: HydrationStrategy = HydrationStrategy.FULL
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
    fun renderStream(composable: Composable, context: RenderContext = RenderContext()): Flow<String>
} 