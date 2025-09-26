package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.mapOfCompat
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
     * with comprehensive SEO metadata support
     */
    internal fun wrapWithHtml(componentHtml: String, context: RenderContext): String {
        // Extract basic SEO metadata
        val titleValue = getMetadataValue(context, "title", "Static Page")
        val descriptionValue = getMetadataValue(context, "description", "")
        val canonicalValue = getMetadataValue(context, "canonical", "")

        // Get additional SEO metadata
        val authorValue = getMetadataValue(context, "author", "")
        val keywordsValue = getMetadataValue(context, "keywords", "")
        val robotsValue = getMetadataValue(context, "robots", "")
        val ogTitleValue = getMetadataValue(context, "og:title", titleValue)
        val ogDescriptionValue = getMetadataValue(context, "og:description", descriptionValue)
        val ogImageValue = getMetadataValue(context, "og:image", "")
        val ogUrlValue = getMetadataValue(context, "og:url", canonicalValue)
        val ogTypeValue = getMetadataValue(context, "og:type", "website")
        val twitterCardValue = getMetadataValue(context, "twitter:card", "summary")
        val twitterSiteValue = getMetadataValue(context, "twitter:site", "")
        val twitterCreatorValue = getMetadataValue(context, "twitter:creator", authorValue)

        // Get language and direction
        val langValue = getMetadataValue(context, "lang", "en")
        val dirValue = getMetadataValue(context, "dir", "ltr")

        // Get favicon
        val faviconValue = getMetadataValue(context, "favicon", "/favicon.ico")

        // Collect all custom meta tags from context
        val customMetaTags = context.seoMetadata.customMetaTags.entries
            .filter {
                it.key !in listOf(
                    "title", "description", "canonical", "author", "keywords",
                    "robots", "og:title", "og:description", "og:image", "og:url",
                    "og:type", "twitter:card", "twitter:site", "twitter:creator",
                    "lang", "dir", "favicon"
                )
            }
            .joinToString("\n                ") { (key, value) ->
                if (key.startsWith("og:") || key.startsWith("twitter:")) {
                    "<meta property=\"$key\" content=\"$value\">"
                } else {
                    "<meta name=\"$key\" content=\"$value\">"
                }
            }

        // Get platform-specific head elements
        val headElements = platformRenderer.getHeadElements().joinToString("\n                ")

        return """
            <!DOCTYPE html>
            <html lang="$langValue" dir="$dirValue">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>$titleValue</title>

                <!-- Basic SEO -->
                ${if (descriptionValue.isNotEmpty()) "<meta name=\"description\" content=\"$descriptionValue\">" else ""}
                ${if (authorValue.isNotEmpty()) "<meta name=\"author\" content=\"$authorValue\">" else ""}
                ${if (keywordsValue.isNotEmpty()) "<meta name=\"keywords\" content=\"$keywordsValue\">" else ""}
                ${if (robotsValue.isNotEmpty()) "<meta name=\"robots\" content=\"$robotsValue\">" else ""}
                ${if (canonicalValue.isNotEmpty()) "<link rel=\"canonical\" href=\"$canonicalValue\">" else ""}

                <!-- Open Graph / Facebook -->
                <meta property="og:type" content="$ogTypeValue">
                <meta property="og:title" content="$ogTitleValue">
                ${if (ogDescriptionValue.isNotEmpty()) "<meta property=\"og:description\" content=\"$ogDescriptionValue\">" else ""}
                ${if (ogImageValue.isNotEmpty()) "<meta property=\"og:image\" content=\"$ogImageValue\">" else ""}
                ${if (ogUrlValue.isNotEmpty()) "<meta property=\"og:url\" content=\"$ogUrlValue\">" else ""}

                <!-- Twitter -->
                <meta name="twitter:card" content="$twitterCardValue">
                ${if (twitterSiteValue.isNotEmpty()) "<meta name=\"twitter:site\" content=\"$twitterSiteValue\">" else ""}
                ${if (twitterCreatorValue.isNotEmpty()) "<meta name=\"twitter:creator\" content=\"$twitterCreatorValue\">" else ""}

                <!-- Favicon -->
                <link rel="icon" href="$faviconValue">

                <!-- Custom meta tags -->
                $customMetaTags

                <!-- Platform-specific head elements -->
                $headElements

                <!-- Styles -->
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

        val stateJson = SerializationUtils.serializeInitialState(context.initialState)

        return """
            <script>
                window.__INITIAL_STATE__ = $stateJson;
            </script>
        """.trimIndent()
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
                (1..5).map { mapOfCompat("id" to "product-$it") }
            }

            routePath.contains("/users/{userId}") -> {
                (1..3).map { mapOfCompat("userId" to "user-$it") }
            }

            routePath.contains("/blog/{slug}") -> {
                listOf(
                    mapOfCompat("slug" to "first-post"),
                    mapOfCompat("slug" to "getting-started"),
                    mapOfCompat("slug" to "advanced-techniques")
                )
            }

            else -> {
                // Fallback for other dynamic routes
                parameters.map { param ->
                    mapOfCompat(param to "sample-$param-value")
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
        // Create assets directory structure
        val assetsDir = "$baseOutputDir/assets"
        val cssDir = "$assetsDir/css"
        val jsDir = "$assetsDir/js"
        val imgDir = "$assetsDir/img"
        val fontsDir = "$assetsDir/fonts"

        // Create all required directories
        FileSystemAccess.createDirectory(assetsDir)
        FileSystemAccess.createDirectory(cssDir)
        FileSystemAccess.createDirectory(jsDir)
        FileSystemAccess.createDirectory(imgDir)
        FileSystemAccess.createDirectory(fontsDir)

        // Create main CSS file
        val mainCssContent = """
            /* Main Summon Framework Styles */
            :root {
                --primary-color: #0070f3;
                --secondary-color: #0070f3;
                --background-color: #ffffff;
                --text-color: #333333;
                --error-color: #e53935;
                --success-color: #43a047;
                --border-radius: 4px;
                --spacing-unit: 8px;
                --font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
            }

            body {
                font-family: var(--font-family);
                line-height: 1.5;
                color: var(--text-color);
                background-color: var(--background-color);
                margin: 0;
                padding: 0;
            }

            .container {
                max-width: 1200px;
                margin: 0 auto;
                padding: calc(var(--spacing-unit) * 2);
            }

            a {
                color: var(--primary-color);
                text-decoration: none;
            }

            a:hover {
                text-decoration: underline;
            }

            button, .button {
                background-color: var(--primary-color);
                color: white;
                border: none;
                border-radius: var(--border-radius);
                padding: var(--spacing-unit) calc(var(--spacing-unit) * 2);
                cursor: pointer;
                font-family: var(--font-family);
                font-size: 1rem;
                transition: background-color 0.2s;
            }

            button:hover, .button:hover {
                background-color: color-mix(in srgb, var(--primary-color) 80%, black);
            }

            /* Utility classes */
            .text-center { text-align: center; }
            .text-left { text-align: left; }
            .text-right { text-align: right; }
            .mt-1 { margin-top: var(--spacing-unit); }
            .mt-2 { margin-top: calc(var(--spacing-unit) * 2); }
            .mb-1 { margin-bottom: var(--spacing-unit); }
            .mb-2 { margin-bottom: calc(var(--spacing-unit) * 2); }
        """.trimIndent()
        FileSystemAccess.writeTextFile("$cssDir/main.css", mainCssContent)

        // Create reset CSS file
        val resetCssContent = """
            /* Reset CSS */
            *, *::before, *::after {
                box-sizing: border-box;
            }

            body, h1, h2, h3, h4, h5, h6, p, ul, ol, dl, figure, blockquote, fieldset, legend {
                margin: 0;
                padding: 0;
            }

            html {
                scroll-behavior: smooth;
            }

            img, picture, video, canvas, svg {
                display: block;
                max-width: 100%;
            }

            input, button, textarea, select {
                font: inherit;
            }
        """.trimIndent()
        FileSystemAccess.writeTextFile("$cssDir/reset.css", resetCssContent)

        // Create hydration JavaScript file
        val hydrationJsContent = """
            // Summon Hydration Script
            window.SummonHydration = (function() {
                // Find hydration data in the page
                function findHydrationData() {
                    const dataScript = document.getElementById('summon-hydration-data');
                    if (!dataScript) {
                        console.warn('No hydration data found');
                        return null;
                    }

                    try {
                        return JSON.parse(dataScript.textContent);
                    } catch (e) {
                        console.error('Error parsing hydration data:', e);
                        return null;
                    }
                }

                // Hydrate the page
                function hydrate() {
                    const data = findHydrationData();
                    if (!data) return;

                    console.log('Hydrating with strategy:', data.strategy);

                    // Find all components that need hydration
                    const components = document.querySelectorAll('[data-summon-component]');

                    // Hydrate each component
                    components.forEach(function(element) {
                        const componentId = element.getAttribute('data-summon-component');
                        hydrateComponent(element, componentId, data);
                    });

                    // Dispatch event when hydration is complete
                    document.dispatchEvent(new CustomEvent('summon:hydration-complete'));
                }

                // Hydrate a specific component
                function hydrateComponent(element, componentId, data) {
                    // Find component data
                    const componentData = data.components.find(function(c) { return c.id === componentId; });
                    if (!componentData) return;

                    // Attach event handlers
                    attachEventHandlers(element, componentData);

                    // Mark as hydrated
                    element.setAttribute('data-summon-hydrated', 'true');
                }

                // Attach event handlers to a component
                function attachEventHandlers(element, componentData) {
                    if (!componentData.events || !componentData.events.length) return;

                    componentData.events.forEach(function(eventName) {
                        // For demonstration purposes, we'll just log the events
                        element.addEventListener(eventName.replace('on', '').toLowerCaseCompat(), function(e) {
                            console.log('Event ' + eventName + ' triggered on component ' + componentData.id);
                        });
                    });
                }

                // Public API
                return {
                    hydrate: hydrate
                };
            })();
        """.trimIndent()
        FileSystemAccess.writeTextFile("$jsDir/summon-hydration.js", hydrationJsContent)

        // Create a placeholder image
        val placeholderSvg = """
            <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">
                <rect width="200" height="200" fill="#f0f0f0"/>
                <text x="50%" y="50%" font-family="sans-serif" font-size="24" text-anchor="middle" dominant-baseline="middle" fill="#666">
                    Summon
                </text>
            </svg>
        """.trimIndent()
        FileSystemAccess.writeTextFile("$imgDir/placeholder.svg", placeholderSvg)

        // Create a favicon
        val faviconSvg = """
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
                <rect width="32" height="32" fill="#0070f3"/>
                <text x="50%" y="50%" font-family="sans-serif" font-size="20" text-anchor="middle" dominant-baseline="middle" fill="white">
                    S
                </text>
            </svg>
        """.trimIndent()
        FileSystemAccess.writeTextFile("$baseOutputDir/favicon.svg", faviconSvg)

        // Copy the files to the output directory
        println("Static assets copied to $assetsDir")
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
