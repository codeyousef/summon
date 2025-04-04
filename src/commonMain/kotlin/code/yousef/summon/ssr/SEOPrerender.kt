package code.yousef.summon.ssr

import code.yousef.summon.Composable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Implementation of SEO pre-rendering for search engine crawlers
 * This renders optimized versions of pages specifically for search engines
 */
class SEOPrerenderer(
    private val renderer: StaticRenderer = StaticRenderer(),
    private val userAgentPatterns: List<String> = defaultSearchEngineUserAgents
) {
    /**
     * Determine if the given user agent is a search engine crawler
     *
     * @param userAgent The user agent string to check
     * @return True if the user agent is a search engine crawler
     */
    fun isSearchEngineCrawler(userAgent: String): Boolean {
        return userAgentPatterns.any { pattern ->
            userAgent.lowercase().contains(pattern.lowercase())
        }
    }

    /**
     * Pre-render a composable for a search engine crawler
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return The pre-rendered HTML optimized for search engines
     */
    fun prerender(composable: Composable, context: RenderContext = RenderContext()): String {
        // Create a SEO-optimized context
        val seoContext = RenderContext(
            enableHydration = false,  // No need for hydration for crawlers
            hydrationIdPrefix = context.hydrationIdPrefix,
            metadata = context.metadata,
            debug = false,            // No debug info for crawlers
            seoMetadata = enrichSeoMetadata(context.seoMetadata),
            initialState = context.initialState
        )

        // Render the composable
        return renderer.render(composable, seoContext)
    }

    /**
     * Enhance SEO metadata with defaults if not provided
     */
    private fun enrichSeoMetadata(metadata: SeoMetadata): SeoMetadata {
        // Only add defaults if values are missing
        return SeoMetadata(
            title = metadata.title,
            description = metadata.description,
            keywords = metadata.keywords,
            canonical = metadata.canonical,
            openGraph = metadata.openGraph,
            twitterCard = metadata.twitterCard,
            structuredData = metadata.structuredData,
            robots = if (metadata.robots.isEmpty()) "index, follow" else metadata.robots,
            customMetaTags = metadata.customMetaTags
        )
    }

    companion object {
        /**
         * Default list of user agent patterns for common search engine crawlers
         */
        val defaultSearchEngineUserAgents = listOf(
            "googlebot",      // Google
            "bingbot",        // Bing
            "slurp",          // Yahoo
            "duckduckbot",    // DuckDuckGo
            "baiduspider",    // Baidu
            "yandex",         // Yandex
            "sogou",          // Sogou
            "exabot",         // Exalead
            "facebot",        // Facebook
            "ia_archiver",    // Alexa
            "twitterbot",     // Twitter
            "linkedinbot",    // LinkedIn
            "pinterestbot"    // Pinterest
        )
    }
}

/**
 * Utility object for SEO pre-rendering
 */
object SEOPrerender {
    private val prerenderer = SEOPrerenderer()

    /**
     * Determine if the given user agent is a search engine crawler
     *
     * @param userAgent The user agent string to check
     * @return True if the user agent is a search engine crawler
     */
    fun isSearchEngineCrawler(userAgent: String): Boolean {
        return prerenderer.isSearchEngineCrawler(userAgent)
    }

    /**
     * Pre-render a composable for a search engine crawler
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return The pre-rendered HTML optimized for search engines
     */
    fun prerender(composable: Composable, context: RenderContext = RenderContext()): String {
        return prerenderer.prerender(composable, context)
    }

    /**
     * Create a sitemap.xml file for the given pages
     *
     * @param pages Map of URL paths to metadata about the page
     * @param baseUrl The base URL of the site
     * @return The sitemap XML as a string
     */
    fun generateSitemap(pages: Map<String, SitemapEntry>, baseUrl: String): String {
        val sitemapEntries = pages.map { (path, entry) ->
            val fullUrl = if (baseUrl.endsWith("/") && path.startsWith("/")) {
                baseUrl + path.substring(1)
            } else if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
                "$baseUrl/$path"
            } else {
                baseUrl + path
            }

            """
            <url>
                <loc>${escapeXml(fullUrl)}</loc>
                <lastmod>${entry.lastModified}</lastmod>
                <changefreq>${entry.changeFrequency.value}</changefreq>
                <priority>${entry.priority}</priority>
            </url>
            """.trimIndent()
        }

        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                ${sitemapEntries.joinToString("\n")}
            </urlset>
        """.trimIndent()
    }

    /**
     * Pre-render multiple pages for search engine crawlers
     *
     * @param pages Map of URL paths to composables
     * @param contextProvider Function to provide a render context for each page
     * @return Map of URL paths to pre-rendered HTML
     */
    suspend fun prerenderSite(
        pages: Map<String, Composable>,
        contextProvider: (String) -> RenderContext = { RenderContext() }
    ): Map<String, String> = coroutineScope {
        // Pre-render each page in parallel
        val prerenders = pages.map { (path, composable) ->
            path to async { prerender(composable, contextProvider(path)) }
        }

        // Collect the results
        prerenders.associate { (path, deferred) ->
            path to deferred.await()
        }
    }

    /**
     * Escape XML special characters
     */
    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}

/**
 * Entry in a sitemap.xml file
 */
data class SitemapEntry(
    /**
     * Last modified date in ISO 8601 format (YYYY-MM-DD)
     */
    val lastModified: String,

    /**
     * How frequently the page is likely to change
     */
    val changeFrequency: ChangeFrequency = ChangeFrequency.MONTHLY,

    /**
     * Priority of this URL relative to other URLs on the site (0.0 to 1.0)
     */
    val priority: Double = 0.5
)

/**
 * How frequently a page is likely to change
 */
enum class ChangeFrequency(val value: String) {
    ALWAYS("always"),
    HOURLY("hourly"),
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly"),
    NEVER("never")
} 