package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable
import code.yousef.summon.routing.RouteDefinition
import code.yousef.summon.runtime.MigratedPlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer
import kotlinx.html.stream.createHTML

/**
 * Implementation of static HTML rendering for Summon components
 */
class StaticRenderer(
    private val platformRenderer: MigratedPlatformRenderer = getPlatformRenderer()
) : ServerSideRenderer {
    /**
     * Render a composable to static HTML
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return The generated HTML as a string
     */
    override fun render(composable: @Composable () -> Unit, context: RenderContext): String {
        val html = renderToString(composable)
        return wrapWithHtml(html, context)
    }

    /**
     * Renders a composable to a string
     */
    private fun renderToString(composable: @Composable () -> Unit): String {
        // Using createHTML from kotlinx.html to render the component
        return createHTML().let { consumer ->
            platformRenderer.renderComposable(composable, consumer)
            consumer.finalize()
        }
    }

    /**
     * Generates a complete HTML document with the rendered component
     *
     * @param componentHtml The rendered component HTML
     * @param context The rendering context with additional metadata
     * @return A complete HTML document as a string
     */
    private fun wrapWithHtml(componentHtml: String, context: RenderContext): String {
        val seo = context.seoMetadata

        val metaTags = buildMetaTags(seo, context.metadata)
        val openGraphTags = buildOpenGraphTags(seo.openGraph)
        val twitterCardTags = buildTwitterCardTags(seo.twitterCard)
        val structuredDataScript = if (seo.structuredData.isNotEmpty()) {
            """<script type="application/ld+json">${seo.structuredData}</script>"""
        } else ""

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                $metaTags
                $openGraphTags
                $twitterCardTags
                $structuredDataScript
                ${generateCanonicalLink(seo.canonical)}
                <title>${seo.title}</title>
                ${generateStylesheets(context)}
            </head>
            <body>
                <div id="root">${componentHtml}</div>
                ${generateInitialStateScript(context)}
            </body>
            </html>
        """.trimIndent()
    }

    /**
     * Builds meta tags from SEO metadata
     */
    private fun buildMetaTags(seo: SeoMetadata, additionalMetadata: Map<String, String>): String {
        val tags = mutableListOf<String>()

        if (seo.description.isNotEmpty()) {
            tags.add("""<meta name="description" content="${escapeHtml(seo.description)}">""")
        }

        if (seo.keywords.isNotEmpty()) {
            tags.add("""<meta name="keywords" content="${escapeHtml(seo.keywords.joinToString(", "))}">""")
        }

        tags.add("""<meta name="robots" content="${escapeHtml(seo.robots)}">""")

        // Add custom meta tags
        seo.customMetaTags.forEach { (name, content) ->
            tags.add("""<meta name="${escapeHtml(name)}" content="${escapeHtml(content)}">""")
        }

        // Add additional metadata
        additionalMetadata.forEach { (name, content) ->
            tags.add("""<meta name="${escapeHtml(name)}" content="${escapeHtml(content)}">""")
        }

        return tags.joinToString("\n")
    }

    /**
     * Builds OpenGraph tags from OpenGraph metadata
     */
    private fun buildOpenGraphTags(og: OpenGraphMetadata): String {
        val tags = mutableListOf<String>()

        if (og.title.isNotEmpty()) {
            tags.add("""<meta property="og:title" content="${escapeHtml(og.title)}">""")
        }

        if (og.description.isNotEmpty()) {
            tags.add("""<meta property="og:description" content="${escapeHtml(og.description)}">""")
        }

        if (og.type.isNotEmpty()) {
            tags.add("""<meta property="og:type" content="${escapeHtml(og.type)}">""")
        }

        if (og.url.isNotEmpty()) {
            tags.add("""<meta property="og:url" content="${escapeHtml(og.url)}">""")
        }

        if (og.image.isNotEmpty()) {
            tags.add("""<meta property="og:image" content="${escapeHtml(og.image)}">""")
        }

        if (og.siteName.isNotEmpty()) {
            tags.add("""<meta property="og:site_name" content="${escapeHtml(og.siteName)}">""")
        }

        return tags.joinToString("\n")
    }

    /**
     * Builds Twitter Card tags from Twitter Card metadata
     */
    private fun buildTwitterCardTags(twitter: TwitterCardMetadata): String {
        val tags = mutableListOf<String>()

        if (twitter.card.isNotEmpty()) {
            tags.add("""<meta name="twitter:card" content="${escapeHtml(twitter.card)}">""")
        }

        if (twitter.site.isNotEmpty()) {
            tags.add("""<meta name="twitter:site" content="${escapeHtml(twitter.site)}">""")
        }

        if (twitter.creator.isNotEmpty()) {
            tags.add("""<meta name="twitter:creator" content="${escapeHtml(twitter.creator)}">""")
        }

        if (twitter.title.isNotEmpty()) {
            tags.add("""<meta name="twitter:title" content="${escapeHtml(twitter.title)}">""")
        }

        if (twitter.description.isNotEmpty()) {
            tags.add("""<meta name="twitter:description" content="${escapeHtml(twitter.description)}">""")
        }

        if (twitter.image.isNotEmpty()) {
            tags.add("""<meta name="twitter:image" content="${escapeHtml(twitter.image)}">""")
        }

        return tags.joinToString("\n")
    }

    /**
     * Generates a canonical link if a canonical URL is provided
     */
    private fun generateCanonicalLink(canonical: String): String {
        return if (canonical.isNotEmpty()) {
            """<link rel="canonical" href="${escapeHtml(canonical)}">"""
        } else ""
    }

    /**
     * Generates stylesheet links
     */
    private fun generateStylesheets(context: RenderContext): String {
        // In a real implementation, this would retrieve stylesheets from the render context
        // For this example, we'll return an empty string
        return ""
    }

    /**
     * Generates a script tag with initial state for hydration
     */
    private fun generateInitialStateScript(context: RenderContext): String {
        if (context.initialState.isEmpty()) return ""

        // In a real implementation, this would serialize the state to JSON
        // For this example, we'll return a placeholder
        return """<script>window.__SUMMON_INITIAL_STATE__ = {};</script>"""
    }

    /**
     * Escapes HTML special characters in a string
     */
    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#039;")
    }
}

/**
 * Utility class for generating static HTML from Summon components
 */
object StaticRendering {
    private val renderer = StaticRenderer()

    /**
     * Render a composable to static HTML
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return The generated HTML as a string
     */
    fun render(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): String {
        return renderer.render(composable, context)
    }

    /**
     * Generate a static site from multiple composables
     *
     * @param pages Map of page paths to composables
     * @param contextProvider Function that provides a render context for each page
     * @return Map of page paths to generated HTML
     */
    fun generateStaticSite(
        pages: Map<String, @Composable () -> Unit>,
        contextProvider: (String) -> RenderContext = { RenderContext() }
    ): Map<String, String> {
        return pages.mapValues { (path, composable) ->
            render(composable, contextProvider(path))
        }
    }
}

/**
 * Utilities for static site generation (SSG) - rendering pages at build time.
 */
object StaticSiteGenerator {

    /**
     * Renders a specific route statically to an HTML string.
     *
     * @param route The RouteDefinition to render.
     * @param params Parameters for the route (if dynamic).
     * @return The rendered HTML string.
     */
    fun renderRouteToString(route: RouteDefinition, params: Map<String, String> = emptyMap()): String {
        println("StaticSiteGenerator: Rendering route path '${route.path}' with params $params")
        // TODO: Implement static rendering logic.
        // Needs a Composer and Renderer setup, similar to SSR/pre-rendering.

        // Placeholder rendering:
        // route.content(params)
        return "<html><body><!-- Static content for ${route.path} --></body></html>"
    }

    /**
     * Generates static HTML files for a list of routes.
     *
     * @param routes A list of RouteDefinitions to render.
     * @param baseOutputDir The directory to output the generated HTML files.
     * @param dataProvider Optional function to provide data for dynamic routes.
     */
    fun generateStaticSite(
        routes: List<RouteDefinition>,
        baseOutputDir: String,
        // TODO: Need a better way to handle data fetching for dynamic routes during SSG.
        dataProvider: suspend (routePath: String, params: Map<String, String>) -> Map<String, Any> = { _, _ -> emptyMap() }
    ) {
        println("StaticSiteGenerator: Generating static site to $baseOutputDir")
        // TODO: This likely needs to run within a coroutine scope if dataProvider is suspend.
        routes.forEach { route ->
            // TODO: Handle dynamic routes - need paths to generate.
            if (!route.path.contains("{")) { // Static route
                val htmlContent = renderRouteToString(route)
                val outputPath = "$baseOutputDir${route.path}.html".replace("//", "/")
                // TODO: Write htmlContent to outputPath (platform specific FS access).
                println("  - Writing static route ${route.path} to $outputPath")
            } else {
                println("  - Skipping dynamic route (needs path generation): ${route.path}")
                // Example: Generate paths based on data
                // val pathsToGenerate = getPathsForDynamicRoute(route, dataProvider)
                // pathsToGenerate.forEach { params -> ... renderRouteToString(route, params) ... }
            }
        }
    }
} 
