package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.core.mapOfCompat
import code.yousef.summon.runtime.LaunchedEffect
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import kotlinx.coroutines.delay
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe

/**
 * Represents the result of a dynamic rendering process.
 *
 * @property html The rendered HTML content.
 * @property headElements A list of HTML strings to be injected into the <head> section.
 */
data class RenderResult(val html: String, val headElements: List<String>)


/**
 * Renders a composable function to an HTML string using a specific PlatformRenderer.
 *
 * @param T The type of the PlatformRenderer implementation.
 * @param renderer An instance of the PlatformRenderer.
 * @param rootComposable The composable function to render.
 * @return A RenderResult containing the HTML string and head elements.
 */
fun <T : PlatformRenderer> renderToString( // Use the canonical renderer type
    renderer: T,
    rootComposable: @Composable () -> Unit
): RenderResult {
    val renderedHtml = renderer.renderComposableRoot {
        rootComposable()
    }
    return RenderResult(
        html = renderedHtml,
        headElements = renderer.getHeadElements()
    )
}

/**
 * Renders a full HTML document including head elements.
 *
 * @param T The type of the PlatformRenderer implementation.
 * @param renderer An instance of the PlatformRenderer.
 * @param rootComposable The composable function to render within the body.
 * @return The complete HTML document as a string.
 */
fun <T : PlatformRenderer> renderDocumentToString( // Use the canonical renderer type
    renderer: T,
    rootComposable: @Composable () -> Unit
): String {
    val result = renderToString(renderer, rootComposable)

    return createHTML().html {
        head { // Use imported head
            // Inject head elements gathered during rendering
            result.headElements.forEach { unsafe { raw(it) } } // Use imported unsafe/raw
        }
        body { // Use imported body
            // Inject the rendered body content
            unsafe { raw(result.html) } // Use imported unsafe/raw
        }
    }
}

/**
 * Implementation of dynamic server-side rendering for Summon components
 * This renderer can handle dynamic data and produce HTML with hydration markers
 */
class DynamicRenderer(
    private val platformRenderer: PlatformRenderer = code.yousef.summon.runtime.getPlatformRenderer(),
    private val hydrationSupport: HydrationSupport = StandardHydrationSupport()
) : ServerSideRenderer {
    /**
     * Render a composable to HTML with dynamic data
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return The generated HTML as a string
     */
    override fun render(composable: @Composable () -> Unit, context: RenderContext): String {
        // Render the composable to HTML
        val html = renderToString(platformRenderer, composable).html

        // If hydration is enabled, add hydration markers
        val finalHtml = if (context.enableHydration) {
            val hydrationData = hydrationSupport.generateHydrationData(composable, HydrationStrategy.FULL)
            hydrationSupport.addHydrationMarkers(html, hydrationData)
        } else {
            html
        }

        // Wrap the HTML in a complete document
        return wrapWithHtml(finalHtml, context)
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

        // Generate a script for the initial state
        val initialStateScript = generateInitialStateScript(context)

        // Generate a script for the hydration bundle
        val hydrationScript = if (context.enableHydration) {
            """<script src="/summon-hydration.js"></script>"""
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
                <div id="root" data-summon-hydration="root">${componentHtml}</div>
                $initialStateScript
                $hydrationScript
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
        // Check for stylesheets in the metadata
        val stylesheets = mutableListOf<String>()

        // Add default stylesheet
        stylesheets.add("""<link rel="stylesheet" href="/summon.css">""")

        // Check for additional stylesheets in metadata
        context.metadata.entries.forEach { (key, value) ->
            if (key.startsWith("stylesheet.") || key == "stylesheet") {
                stylesheets.add("""<link rel="stylesheet" href="$value">""")
            }
        }

        // Check for theme in metadata
        val theme = context.metadata["theme"]
        if (theme != null) {
            stylesheets.add("""<link rel="stylesheet" href="/themes/$theme.css">""")
        }

        return stylesheets.joinToString("\n                ")
    }

    /**
     * Generates a script tag with initial state for hydration
     */
    private fun generateInitialStateScript(context: RenderContext): String {
        if (context.initialState.isEmpty()) return ""

        // Serialize the initial state to JSON
        val serializedState = serializeInitialState(context.initialState)

        // Create a script tag with the serialized state
        return """
            <script>
                window.__SUMMON_INITIAL_STATE__ = $serializedState;
                document.dispatchEvent(new CustomEvent('summon:state-loaded'));
            </script>
        """.trimIndent()
    }

    /**
     * Serialize the initial state to JSON
     */
    private fun serializeInitialState(state: Map<String, Any?>): String {
        return SerializationUtils.serializeInitialState(state)
    }

    /**
     * Serialize a value to JSON
     */
    private fun serializeValue(value: Any?): String {
        return SerializationUtils.serializeValue(value)
    }

    /**
     * Escape special characters in JSON strings
     */
    private fun escapeJsonString(str: String): String {
        return SerializationUtils.escapeJsonString(str)
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
 * Utility object for dynamic server-side rendering of Summon components
 */
object DynamicRendering {
    private val renderer = DynamicRenderer()

    /**
     * Render a composable to HTML with dynamic data
     *
     * @param composable The composable to render
     * @param context Optional rendering context with additional metadata
     * @return The generated HTML as a string
     */
    fun render(composable: @Composable () -> Unit, context: RenderContext = RenderContext()): String {
        return renderer.render(composable, context)
    }

    /**
     * Render a composable to HTML with hydration support
     *
     * @param composable The composable to render
     * @param initialState Initial state for hydration
     * @param seoMetadata SEO metadata for the page
     * @return The generated HTML as a string
     */
    fun renderWithHydration(
        composable: @Composable () -> Unit,
        initialState: Map<String, Any?> = emptyMap(),
        seoMetadata: SeoMetadata = SeoMetadata()
    ): String {
        val context = RenderContext(
            enableHydration = true,
            initialState = initialState,
            seoMetadata = seoMetadata
        )
        return render(composable, context)
    }

    /**
     * Create a context-specific renderer with custom configuration
     *
     * @param hydrationSupport Custom hydration support implementation
     * @return A configured DynamicRenderer instance
     */
    fun createRenderer(hydrationSupport: HydrationSupport = StandardHydrationSupport()): DynamicRenderer {
        return DynamicRenderer(hydrationSupport = hydrationSupport)
    }

    /**
     * Renders a composable function to a string (e.g., HTML) dynamically on the server.
     *
     * @param content The composable function to render.
     * @return The rendered output string.
     */
    fun renderToString(content: @Composable () -> Unit): String {
        // Get the platform renderer
        val platformRenderer = code.yousef.summon.runtime.getPlatformRenderer()

        // Use the renderToString helper function to render the composable
        val renderResult = renderToString(platformRenderer, content)

        // Return the HTML content
        return renderResult.html
    }

    /**
     * Fetches server-side data needed for dynamic rendering.
     *
     * @param routeId The route ID to fetch data for
     * @return A map of data for the route
     */
    suspend fun fetchDataForRoute(routeId: String): Map<String, Any> {
        // Get current timestamp in a platform-independent way
        val timestamp = kotlin.time.TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds

        // Determine the data type based on the route ID prefix
        return when {
            // Product data
            routeId.startsWith("product-") -> {
                val productId = routeId.substringAfter("product-")
                mapOfCompat(
                    "id" to productId,
                    "name" to "Product $productId",
                    "price" to (10 + productId.hashCode() % 90),
                    "description" to "This is a dynamic product description for $productId",
                    "rating" to (3.5 + (productId.hashCode() % 15) / 10.0),
                    "inStock" to (productId.hashCode() % 2 == 0),
                    "categories" to listOf("Electronics", "Gadgets", "New Arrivals"),
                    "metadata" to mapOfCompat(
                        "sku" to "SKU-$productId",
                        "weight" to "${1 + productId.hashCode() % 5} kg",
                        "dimensions" to "${10 + productId.hashCode() % 20} x ${5 + productId.hashCode() % 15} x ${2 + productId.hashCode() % 8} cm"
                    ),
                    "timestamp" to timestamp
                )
            }

            // User data
            routeId.startsWith("user-") -> {
                val userId = routeId.substringAfter("user-")
                mapOfCompat(
                    "id" to userId,
                    "name" to "User $userId",
                    "email" to "user$userId@example.com",
                    "memberSince" to "2023-${1 + (userId.hashCode() % 12)}-${1 + (userId.hashCode() % 28)}",
                    "isActive" to (userId.hashCode() % 3 != 0),
                    "preferences" to mapOfCompat(
                        "theme" to if (userId.hashCode() % 2 == 0) "light" else "dark",
                        "notifications" to (userId.hashCode() % 3 != 1),
                        "language" to listOf("en", "fr", "de", "es", "ja")[(userId.hashCode() % 5)]
                    ),
                    "lastLogin" to "2023-${1 + (userId.hashCode() % 12)}-${1 + (userId.hashCode() % 28)} ${(userId.hashCode() % 24)}:${(userId.hashCode() % 60)}",
                    "timestamp" to timestamp
                )
            }

            // Blog post data
            routeId.startsWith("blog-") -> {
                val blogId = routeId.substringAfter("blog-")
                mapOfCompat(
                    "id" to blogId,
                    "title" to "Blog Post $blogId",
                    "author" to "Author ${1 + (blogId.hashCode() % 10)}",
                    "date" to "2023-${1 + (blogId.hashCode() % 12)}-${1 + (blogId.hashCode() % 28)}",
                    "content" to "This is the content of blog post $blogId. It contains dynamic content generated for server-side rendering.",
                    "tags" to listOf("Technology", "Web Development", "SSR", "Kotlin"),
                    "comments" to (1..3).map { commentId ->
                        mapOfCompat(
                            "id" to "$blogId-comment-$commentId",
                            "author" to "Commenter $commentId",
                            "text" to "This is comment $commentId on blog post $blogId",
                            "date" to "2023-${1 + ((blogId.hashCode() + commentId) % 12)}-${1 + ((blogId.hashCode() + commentId) % 28)}"
                        )
                    },
                    "timestamp" to timestamp
                )
            }

            // Dashboard data
            routeId.startsWith("dashboard-") -> {
                val dashboardId = routeId.substringAfter("dashboard-")
                mapOfCompat(
                    "id" to dashboardId,
                    "title" to "Dashboard $dashboardId",
                    "metrics" to mapOfCompat(
                        "visitors" to (1000 + dashboardId.hashCode() % 9000),
                        "pageViews" to (5000 + dashboardId.hashCode() % 45000),
                        "conversionRate" to (1.5 + (dashboardId.hashCode() % 40) / 10.0),
                        "revenue" to (10000 + dashboardId.hashCode() % 90000)
                    ),
                    "charts" to listOf(
                        mapOfCompat(
                            "type" to "line",
                            "title" to "Traffic Trend",
                            "data" to (1..7).map { day ->
                                mapOfCompat("day" to day, "value" to (100 + (dashboardId.hashCode() + day) % 900))
                            }
                        ),
                        mapOfCompat(
                            "type" to "bar",
                            "title" to "Revenue by Channel",
                            "data" to listOf("Organic", "Direct", "Social", "Email").mapIndexed { index, channel ->
                                mapOfCompat(
                                    "channel" to channel,
                                    "value" to (2000 + (dashboardId.hashCode() + index) % 8000)
                                )
                            }
                        )
                    ),
                    "timestamp" to timestamp
                )
            }

            // Default data for any other route
            else -> {
                mapOfCompat(
                    "id" to routeId,
                    "title" to "Content for $routeId",
                    "description" to "This is dynamic content for route ID: $routeId",
                    "lastUpdated" to "2023-${1 + (routeId.hashCode() % 12)}-${1 + (routeId.hashCode() % 28)}",
                    "metadata" to mapOfCompat(
                        "type" to "generic",
                        "version" to "1.0.${routeId.hashCode() % 100}"
                    ),
                    "timestamp" to timestamp
                )
            }
        }
    }
}

/**
 * Example composable demonstrating dynamic data fetching.
 */
@Composable
fun DynamicDataComponent(routeId: String) {
    println("DynamicDataComponent composed for route '$routeId'")

    // Use remember to store the fetched data with explicit type parameters
    val dataState = remember { mutableStateOf<Map<String, Any>?>(null) }
    val data: Map<String, Any>? = dataState.value
    val setData: (Map<String, Any>?) -> Unit = { dataState.value = it }

    val loadingState = remember { mutableStateOf(true) }
    val isLoading: Boolean = loadingState.value
    val setIsLoading: (Boolean) -> Unit = { loadingState.value = it }

    val errorState = remember { mutableStateOf<String?>(null) }
    val error: String? = errorState.value
    val setError: (String?) -> Unit = { errorState.value = it }

    // Use LaunchedEffect for data fetching when the component is first composed
    LaunchedEffect(routeId) {
        setIsLoading(true)
        try {
            // Simulate data fetching (in a real app, this would be an API call)
            delay(300) // Simulate network delay

            // Create demo data based on routeId
            val fetchedData = when {
                routeId.startsWith("product-") -> {
                    val productId = routeId.substringAfter("product-")
                    mapOfCompat(
                        "id" to productId,
                        "name" to "Product $productId",
                        "price" to (10 + productId.hashCode() % 90),
                        "description" to "This is a dynamic product description for $productId",
                        "rating" to (3.5 + (productId.hashCode() % 15) / 10.0)
                    )
                }

                routeId.startsWith("user-") -> {
                    val userId = routeId.substringAfter("user-")
                    mapOfCompat(
                        "id" to userId,
                        "name" to "User $userId",
                        "email" to "user$userId@example.com",
                        "memberSince" to "2023-${1 + (userId.hashCode() % 12)}-${1 + (userId.hashCode() % 28)}"
                    )
                }

                else -> {
                    mapOfCompat(
                        "id" to routeId,
                        "title" to "Content for $routeId",
                        "description" to "This is dynamic content for route ID: $routeId"
                    )
                }
            }

            setData(fetchedData)
            setError(null)
        } catch (e: Exception) {
            setError("Failed to load data: ${e.message}")
            setData(null)
        } finally {
            setIsLoading(false)
        }
    }

    // Simple placeholder content for server rendering
    println("Rendering state - isLoading: $isLoading, error: $error, data: ${data?.size ?: 0} items")

    // Note: For SSR, we don't need the full interactive UI components
    // This simpler representation avoids JS platform compatibility issues
    Text("Data for route: $routeId")

    if (data != null) {
        Text("Found ${data.size} data items")

        // Display basic data properties
        val title = data["title"] ?: data["name"] ?: "Data"
        Text("Title: $title")

        if (data.containsKey("description")) {
            Text("Description: ${data["description"]}")
        }

        // Display remaining keys
        Text("Properties:")
        data.entries.filter { it.key !in listOf("title", "name", "description") }
            .forEach { (key, value) ->
                Text("$key: $value")
            }
    } else if (error != null) {
        Text("Error: $error")
    } else if (isLoading) {
        Text("Loading data...")
    } else {
        Text("No data available")
    }

    // The original UI code with platform-specific components is commented out
    /* 
    // Display the appropriate UI based on loading/error/data state
    when {
        isLoading -> {
            // Show loading state
            Column(modifier = Modifier.padding("16px")) {
                Text("Loading data for $routeId...")
                LinearProgress(modifier = Modifier.width("200px").margin("16px 0"))
            }
        }
        error != null -> {
            // Show error state
            Column(modifier = Modifier.padding("16px")) {
                Text("Error", modifier = Modifier.fontWeight("bold").fontSize("18px").color("#d32f2f"))
                Spacer(modifier = Modifier.height("8px"))
                Text(error, modifier = Modifier.color("#d32f2f"))
                Spacer(modifier = Modifier.height("16px"))
                Button(
                    onClick = { 
                        // Retry logic
                        setIsLoading(true)
                        setError(null)
                        // Trigger refetch
                    },
                    modifier = Modifier.background("#f44336").color("white")
                ) {
                    Text("Retry")
                }
            }
        }
        data != null -> {
            // Show data content
            Column(modifier = Modifier.padding("16px")) {
                // Title or Name
                val title = data["title"] ?: data["name"] ?: "Data for $routeId"
                Text(
                    text = title.toString(),
                    modifier = Modifier.fontSize("24px").fontWeight("bold").margin("0 0 16px 0")
                )

                // Description (if available)
                if (data.containsKey("description")) {
                    Text(
                        text = data["description"].toString(),
                        modifier = Modifier.margin("0 0 16px 0")
                    )
                }

                // Display all data fields
                Card(modifier = Modifier.padding("16px").background("#f5f5f5")) {
                    Column(modifier = Modifier.padding("16px")) {
                        Text("Data Fields:", modifier = Modifier.fontWeight("bold").margin("0 0 8px 0"))
                        data.entries.forEach { entry ->
                            val key = entry.key
                            val value = entry.value
                            if (key != "title" && key != "name" && key != "description") {
                                Row(modifier = Modifier.margin("4px 0")) {
                                    Text(
                                        text = "$key: ",
                                        modifier = Modifier.fontWeight("bold").width("100px")
                                    )
                                    Text(value.toString())
                                }
                            }
                        }
                    }
                }

                // Action button (example)
                Spacer(modifier = Modifier.height("16px"))
                Button(
                    onClick = { /* Action handler */ },
                    modifier = Modifier.background("#2196f3").color("white")
                ) {
                    Text("View Details")
                }
            }
        }
        else -> {
            // Empty state
            Column(modifier = Modifier.padding("16px")) {
                Text("No data available for $routeId")
            }
        }
    }
    */
} 
