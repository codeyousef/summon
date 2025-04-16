package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable
import code.yousef.summon.routing.RouteDefinition
import code.yousef.summon.routing.RouteParams
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer

/**
 * Handles static rendering of Summon components to HTML with improved compatibility
 * with the new composition system.
 */
class StaticRenderer(
    private val platformRenderer: PlatformRenderer = getPlatformRenderer()
) : ServerSideRenderer {
    /**
     * Render a composable to HTML
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return The generated HTML as a string
     */
    override fun render(composable: @Composable () -> Unit, context: RenderContext): String {
        val componentHtml = platformRenderer.renderComposableRoot {
            composable()
        }

        // Wrap with HTML structure
        return wrapWithHtml(componentHtml, context)
    }

    // Get the metadata values safely with defaults
    private fun getMetadataValue(context: RenderContext, key: String, default: String = ""): String {
        return context.metadata[key] ?: context.seoMetadata.customMetaTags[key] ?: default
    }

    /**
     * Wraps the rendered component HTML in a complete HTML document
     */
    internal fun wrapWithHtml(componentHtml: String, context: RenderContext): String {
        val titleValue = getMetadataValue(context, "title", "Static Page")
        val descriptionValue = getMetadataValue(context, "description", "")
        val canonicalValue = getMetadataValue(context, "canonical", "")

        // Build simple HTML document with minimal SEO
        val headElements = platformRenderer.getHeadElements().joinToString("\n                ")

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$titleValue</title>
                ${if (descriptionValue.isNotEmpty()) "<meta name=\"description\" content=\"$descriptionValue\">" else ""}
                ${if (canonicalValue.isNotEmpty()) "<link rel=\"canonical\" href=\"$canonicalValue\">" else ""}
                $headElements
                <link rel="stylesheet" href="/css/styles.css">
            </head>
            <body>
                <div id="root">
                    $componentHtml
                </div>
                ${generateInitialStateScript(context)}
            </body>
            </html>
        """.trimIndent()
    }

    /**
     * Generate a script for the initial state
     */
    private fun generateInitialStateScript(context: RenderContext): String {
        if (context.initialState.isEmpty()) return ""

        // Simple JSON serialization
        val stateJson = context.initialState.entries.joinToString(",\n    ", "{\n    ", "\n}") { (key, value) ->
            "\"${key}\": ${serializeValue(value)}"
        }

        return """
            <script>
                window.__INITIAL_STATE__ = $stateJson;
            </script>
        """.trimIndent()
    }

    /**
     * Serialize a value to JSON
     */
    private fun serializeValue(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> "\"${value.replace("\"", "\\\"")}\""
            is Number, is Boolean -> value.toString()
            is Map<*, *> -> {
                value.entries.joinToString(",", "{", "}") { (k, v) ->
                    "\"${k}\": ${serializeValue(v)}"
                }
            }

            is List<*> -> {
                value.joinToString(",", "[", "]") { serializeValue(it) }
            }

            else -> "\"${value.toString().replace("\"", "\\\"")}\""
        }
    }
}

/**
 * Utility object for static server-side rendering
 */
object StaticRendering {
    private val renderer = StaticRenderer()

    /**
     * Render a composable to static HTML
     *
     * @param composable The composable to render
     * @param metadata Additional metadata for SEO
     * @return The generated HTML as a string
     */
    fun renderToString(
        composable: @Composable () -> Unit,
        metadata: Map<String, String> = emptyMap()
    ): String {
        val context = RenderContext(
            enableHydration = false,
            metadata = metadata
        )
        return renderer.render(composable, context)
    }

    /**
     * Create a custom static renderer
     *
     * @return A configured StaticRenderer
     */
    fun createRenderer(): StaticRenderer {
        return StaticRenderer()
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

        // Create a rendering context based on route information
        val context = RenderContext(
            enableHydration = false, // Static sites typically don't need hydration
            seoMetadata = SeoMetadata(
                title = route.title ?: "Page ${route.path}",
                description = route.description ?: "Static page for ${route.path}",
                canonical = route.canonicalUrl ?: ""
            )
        )

        // Create a composable that renders the route's content
        val routeComposable: @Composable () -> Unit = {
            route.content(RouteParams(params))
        }

        // Render the content using the modified renderComposableRoot
        val platformRenderer = getPlatformRenderer()
        val htmlContent = platformRenderer.renderComposableRoot {
            routeComposable()
        }

        // Generate the full HTML document using the local StaticRenderer instance
        // Ensure the platformRenderer instance used for wrapping is the same one used for rendering
        return StaticRenderer(platformRenderer).wrapWithHtml(htmlContent, context)
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
        dataProvider: suspend (routePath: String, params: Map<String, String>) -> Map<String, Any> = { _, _ -> emptyMap() }
    ) {
        println("StaticSiteGenerator: Generating static site to $baseOutputDir")

        // Create base output directory if it doesn't exist
        FileSystemAccess.createDirectory(baseOutputDir)

        // Process each route
        routes.forEach { route ->
            if (!route.path.contains("{")) {
                // Static route - straightforward rendering
                val htmlContent = renderRouteToString(route)
                val outputPath = "$baseOutputDir/${normalizePath(route.path)}"
                saveHtmlFile(outputPath, htmlContent)
                println("  - Generated static page for ${route.path}")
            } else {
                // Dynamic route - needs parameter values
                println("  - Processing dynamic route: ${route.path}")
                val dynamicParams = extractRouteParameters(route.path)

                // Generate parameter combinations (in a real app, this would use dataProvider)
                val paramCombinations = generateParameterCombinations(route.path, dynamicParams)

                // Render each parameter combination
                paramCombinations.forEach { params ->
                    val resolvedPath = resolvePathWithParams(route.path, params)
                    val htmlContent = renderRouteToString(route, params)
                    val outputPath = "$baseOutputDir/${normalizePath(resolvedPath)}"
                    saveHtmlFile(outputPath, htmlContent)
                    println("    - Generated dynamic page: $resolvedPath")
                }
            }
        }

        // Generate index file if needed
        val indexRoute = routes.find { it.path == "/" || it.path == "" }
        if (indexRoute != null) {
            val indexHtml = renderRouteToString(indexRoute)
            saveHtmlFile("$baseOutputDir/index.html", indexHtml)
            println("  - Generated index page")
        }

        // Copy static assets if needed
        copyStaticAssets(baseOutputDir)

        println("StaticSiteGenerator: Static site generation completed")
    }

    /**
     * Extracts parameter placeholders from a route path
     */
    private fun extractRouteParameters(path: String): List<String> {
        val regex = "\\{([^}]+)\\}".toRegex()
        return regex.findAll(path).map { it.groupValues[1] }.toList()
    }

    /**
     * Generates combinations of parameter values for dynamic routes
     * In a real app, this would fetch actual data from the dataProvider
     */
    private fun generateParameterCombinations(
        routePath: String,
        parameters: List<String>
    ): List<Map<String, String>> {
        // For demo purposes, generate some sample values for each parameter
        return when {
            routePath.contains("/products/{id}") -> {
                (1..5).map { mapOf("id" to "product-$it") }
            }

            routePath.contains("/users/{userId}") -> {
                (1..3).map { mapOf("userId" to "user-$it") }
            }

            routePath.contains("/blog/{slug}") -> {
                listOf(
                    mapOf("slug" to "first-post"),
                    mapOf("slug" to "getting-started"),
                    mapOf("slug" to "advanced-techniques")
                )
            }

            else -> {
                // Fallback for other dynamic routes
                parameters.map { param ->
                    mapOf(param to "sample-$param-value")
                }
            }
        }
    }

    /**
     * Resolves a route path with parameter values
     */
    private fun resolvePathWithParams(path: String, params: Map<String, String>): String {
        var result = path
        params.forEach { (key, value) ->
            result = result.replace("{$key}", value)
        }
        return result
    }

    /**
     * Normalizes a path for file system storage
     */
    private fun normalizePath(path: String): String {
        // Convert route path to filename
        var fileName = path.trim('/')

        // Handle root path
        if (fileName.isEmpty()) {
            fileName = "index"
        }

        // Add .html extension if not present
        if (!fileName.endsWith(".html")) {
            fileName = "$fileName.html"
        }

        return fileName
    }

    /**
     * Saves HTML content to a file
     */
    private fun saveHtmlFile(path: String, content: String) {
        FileSystemAccess.writeTextFile(path, content)
    }

    /**
     * Copies static assets to the output directory
     */
    private fun copyStaticAssets(baseOutputDir: String) {
        // Create assets directory
        val assetsDir = "$baseOutputDir/assets"
        FileSystemAccess.createDirectory(assetsDir)

        // In a real implementation, this would copy CSS, JS, images, etc.
        // For this example, we'll just create a basic CSS file
        val cssContent = """
            body {
                font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
                line-height: 1.5;
                color: #333;
                max-width: 1200px;
                margin: 0 auto;
                padding: 16px;
            }
            a {
                color: #0070f3;
                text-decoration: none;
            }
            a:hover {
                text-decoration: underline;
            }
        """.trimIndent()

        FileSystemAccess.writeTextFile("$assetsDir/styles.css", cssContent)
    }
}

/**
 * Platform-specific file system access abstraction
 */
expect object FileSystemAccess {
    /**
     * Creates a directory if it doesn't exist
     */
    fun createDirectory(path: String)

    /**
     * Writes text content to a file
     */
    fun writeTextFile(path: String, content: String)

    /**
     * Reads text content from a file
     */
    fun readTextFile(path: String): String
} 
