package code.yousef.summon.ssr.examples

import code.yousef.summon.*
import code.yousef.summon.ssr.*

/**
 * Example class demonstrating how to use the server-side rendering features
 *
 * Note: This is a simplified example for demonstration purposes.
 * In a real implementation, you would use more sophisticated styling and components.
 */
object SSRExample {
    /**
     * Example of static rendering
     */
    fun staticRenderingExample() {
        // Create a simple page component
        val homePage = createSimpleHomePage()

        // Create SEO metadata
        val seoMetadata = SeoMetadata(
            title = "Summon - Home Page",
            description = "Summon is a Kotlin Multiplatform library for static site generation",
            keywords = listOf("kotlin", "multiplatform", "static site", "compose"),
            canonical = "https://example.com/",
            openGraph = OpenGraphMetadata(
                title = "Summon - Kotlin Multiplatform Static Site Generator",
                description = "Create stunning static websites with Kotlin and Compose-like syntax",
                type = "website",
                url = "https://example.com/",
                image = "https://example.com/images/og-image.jpg",
                siteName = "Summon"
            ),
            twitterCard = TwitterCardMetadata(
                card = "summary_large_image",
                site = "@SummonKMP",
                creator = "@KotlinDev",
                title = "Summon - Static Site Generator",
                description = "Create stunning static websites with Kotlin and Compose-like syntax",
                image = "https://example.com/images/twitter-image.jpg"
            )
        )

        // Create a rendering context
        val context = RenderContext(
            seoMetadata = seoMetadata
        )

        // Render the page to static HTML
        val html = StaticRendering.render(homePage, context)

        // Output or save the HTML
        println("Generated HTML: ${html.length} characters")
    }

    /**
     * Example of dynamic rendering with hydration
     */
    fun dynamicRenderingExample() {
        // Create a page with interactive elements
        val homePage = createSimpleInteractivePage()

        // Initial state for hydration
        val initialState = mapOf(
            "counter" to 0,
            "darkMode" to false,
            "userName" to "Guest"
        )

        // Create SEO metadata
        val seoMetadata = SeoMetadata(
            title = "Summon - Interactive Home Page",
            description = "Summon is a Kotlin Multiplatform library for dynamic web applications"
        )

        // Render with hydration
        val html = DynamicRendering.renderWithHydration(
            composable = homePage,
            initialState = initialState,
            seoMetadata = seoMetadata
        )

        // Output or save the HTML
        println("Generated HTML with hydration: ${html.length} characters")
    }

    /**
     * Example of partial hydration
     */
    fun partialHydrationExample() {
        // Create a page with interactive elements
        val homePage = createSimplePartiallyInteractivePage()

        // Create a partial hydration support instance
        val partialHydrationSupport = PartialHydrationSupport()

        // Create a custom renderer
        val renderer = DynamicRendering.createRenderer(hydrationSupport = partialHydrationSupport)

        // Create a context with hydration enabled
        val context = RenderContext(
            enableHydration = true,
            seoMetadata = SeoMetadata(
                title = "Summon - Partially Hydrated Page"
            )
        )

        // Render with partial hydration
        val html = renderer.render(homePage, context)

        // Output or save the HTML
        println("Generated HTML with partial hydration: ${html.length} characters")
    }

    /**
     * Example of streaming SSR
     */
    suspend fun streamingSSRExample() {
        // Create a large page
        val largePage = createSimpleLargePage()

        // Create a context
        val context = RenderContext(
            seoMetadata = SeoMetadata(
                title = "Summon - Large Streaming Page"
            )
        )

        // Create a streaming renderer with a small chunk size for demo purposes
        val renderer = StreamingSSR.createRenderer(chunkSize = 1024)

        // Get a stream of HTML chunks
        val htmlStream = renderer.renderStream(largePage, context)

        // Process the stream (in a real app, you would send these chunks to the client)
        var chunkCount = 0
        htmlStream.collect { chunk ->
            chunkCount++
            println("Received chunk $chunkCount: ${chunk.length} characters")
        }
    }

    /**
     * Example of SEO pre-rendering
     */
    fun seoPrerendererExample() {
        // Create a page
        val homePage = createSimpleHomePage()

        // Create SEO metadata
        val seoMetadata = SeoMetadata(
            title = "Summon - SEO Optimized Page",
            description = "Optimized page for search engines"
        )

        // Create a context
        val context = RenderContext(seoMetadata = seoMetadata)

        // Pre-render for a search engine
        val html = SEOPrerender.prerender(homePage, context)

        // Output or save the HTML
        println("Generated SEO-optimized HTML: ${html.length} characters")

        // Check if a user agent is a search engine
        val googleBot = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"
        val isSearchEngine = SEOPrerender.isSearchEngineCrawler(googleBot)
        println("Is Google Bot a search engine? $isSearchEngine")

        // Generate a sitemap
        val sitemapEntries = mapOf(
            "/" to SitemapEntry(
                lastModified = "2023-06-15",
                changeFrequency = ChangeFrequency.WEEKLY,
                priority = 1.0
            ),
            "/about" to SitemapEntry(
                lastModified = "2023-05-20",
                changeFrequency = ChangeFrequency.MONTHLY,
                priority = 0.8
            ),
            "/contact" to SitemapEntry(
                lastModified = "2023-06-10",
                changeFrequency = ChangeFrequency.MONTHLY,
                priority = 0.7
            )
        )

        val sitemap = SEOPrerender.generateSitemap(sitemapEntries, "https://example.com")
        println("Generated sitemap: $sitemap")
    }

    // Helper function to create a simple home page
    private fun createSimpleHomePage(): Composable {
        return Column(
            modifier = Modifier().padding("20px"),
            content = listOf(
                Text("Welcome to Summon"),
                Text("A Kotlin Multiplatform library for static site generation"),
                Row(
                    content = listOf(
                        Button(
                            label = "Learn More",
                            modifier = Modifier().padding("10px"),
                            onClick = { /* Client-side code */ }
                        ),
                        Button(
                            label = "Get Started",
                            modifier = Modifier().padding("10px"),
                            onClick = { /* Client-side code */ }
                        )
                    )
                )
            )
        )
    }

    // Helper function to create a page with interactive elements
    private fun createSimpleInteractivePage(): Composable {
        return Column(
            modifier = Modifier().padding("20px"),
            content = listOf(
                Text("Interactive Summon Example"),
                Button(
                    label = "Click Me",
                    modifier = Modifier().padding("10px"),
                    onClick = { /* Client-side code */ }
                ),
                TextField(
                    state = mutableStateOf(""),
                    placeholder = "Enter your name",
                    modifier = Modifier().padding("10px")
                )
            )
        )
    }

    // Helper function to create a page with both static and interactive elements
    private fun createSimplePartiallyInteractivePage(): Composable {
        return Column(
            modifier = Modifier().padding("20px"),
            content = listOf(
                // Static header section
                Text("Partially Interactive Page"),
                Text("This section is static and doesn't need hydration"),

                // Interactive section
                Button(
                    label = "Click Me",
                    modifier = Modifier().padding("10px"),
                    onClick = { /* Client-side code */ }
                ),
                TextField(
                    state = mutableStateOf(""),
                    placeholder = "Enter your name",
                    modifier = Modifier().padding("10px")
                )
            )
        )
    }

    // Helper function to create a large page for streaming
    private fun createSimpleLargePage(): Composable {
        val items = (1..100).map { index ->
            Text("Item $index - This is a long description for demonstration purposes.")
        }

        return Column(
            modifier = Modifier().padding("20px"),
            content = listOf(
                Text("Large Streaming Page Example"),
                Text("This page contains 100 items and is rendered as a stream")
            ) + items
        )
    }
} 