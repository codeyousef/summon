package code.yousef.summon.ssr.examples

import code.yousef.summon.Column
import code.yousef.summon.Composable
import code.yousef.summon.Modifier
import code.yousef.summon.Text

/**
 * Example of how to use Summon SSR with Spring Boot.
 * Note: This is a pseudo-implementation since this is in a common module,
 * but it demonstrates the usage pattern.
 */
object SpringControllerExample {
    /**
     * Example of a Spring controller for static SSR
     *
     * In a real JVM implementation, this would be:
     *
     * ```
     * @Controller
     * class SummonController {
     *     @GetMapping("/")
     *     fun home(userAgent: String): String {
     *         // Check if the request is from a search engine
     *         val isSearchEngine = SEOPrerender.isSearchEngineCrawler(userAgent)
     *
     *         // Create the page component
     *         val page = HomePage()
     *
     *         // Create SEO metadata
     *         val seoMetadata = SeoMetadata(
     *             title = "Summon - Home",
     *             description = "Summon is a Kotlin Multiplatform library for static site generation"
     *         )
     *
     *         // Create a rendering context
     *         val context = RenderContext(seoMetadata = seoMetadata)
     *
     *         // Render the page differently based on user agent
     *         return if (isSearchEngine) {
     *             // Optimized version for search engines
     *             SEOPrerender.prerender(page, context)
     *         } else {
     *             // Regular version for users
     *             StaticRendering.render(page, context)
     *         }
     *     }
     *
     *     @GetMapping("/dynamic")
     *     fun dynamicPage(): String {
     *         // Create the page component
     *         val page = DynamicPage()
     *
     *         // Create a context with hydration enabled
     *         val context = RenderContext(
     *             enableHydration = true,
     *             seoMetadata = SeoMetadata(title = "Summon - Dynamic Page")
     *         )
     *
     *         // Render with hydration support
     *         return DynamicRendering.render(page, context)
     *     }
     * }
     * ```
     */
    fun staticControllerPseudoCode() {
        // This is just a placeholder showing the concept
        println("Spring controller example - see comments for implementation")
    }

    /**
     * Example of a Spring controller for streaming SSR
     *
     * In a real JVM implementation, this would be:
     *
     * ```
     * @Controller
     * class StreamingController {
     *     @GetMapping("/stream", produces = ["text/html"])
     *     fun streamPage(response: HttpServletResponse): ResponseBodyEmitter {
     *         // Set appropriate headers
     *         response.setHeader("Transfer-Encoding", "chunked")
     *         response.setHeader("Content-Type", "text/html; charset=UTF-8")
     *
     *         // Create a ResponseBodyEmitter for streaming
     *         val emitter = ResponseBodyEmitter()
     *
     *         // Create the page component
     *         val page = LargePage()
     *
     *         // Create a context
     *         val context = RenderContext(
     *             seoMetadata = SeoMetadata(title = "Summon - Streaming Page")
     *         )
     *
     *         // Get a stream of HTML chunks
     *         val htmlStream = StreamingSSR.renderStream(page, context)
     *
     *         // Process the stream asynchronously
     *         GlobalScope.launch {
     *             try {
     *                 htmlStream.collect { chunk ->
     *                     emitter.send(chunk)
     *                 }
     *                 emitter.complete()
     *             } catch (e: Exception) {
     *                 emitter.completeWithError(e)
     *             }
     *         }
     *
     *         return emitter
     *     }
     * }
     * ```
     */
    fun streamingControllerPseudoCode() {
        // This is just a placeholder showing the concept
        println("Spring streaming controller example - see comments for implementation")
    }

    /**
     * Example of a Spring WebFlux controller for reactive streaming SSR
     *
     * In a real JVM implementation, this would be:
     *
     * ```
     * @RestController
     * class ReactiveController {
     *     @GetMapping("/reactive", produces = ["text/html"])
     *     fun reactivePage(): Flux<String> {
     *         // Create the page component
     *         val page = LargePage()
     *
     *         // Create a context
     *         val context = RenderContext(
     *             seoMetadata = SeoMetadata(title = "Summon - Reactive Page")
     *         )
     *
     *         // Get a stream of HTML chunks
     *         val htmlStream = StreamingSSR.renderStream(page, context)
     *
     *         // Convert the Flow to a Flux
     *         return Flux.from(htmlStream.asPublisher())
     *     }
     * }
     * ```
     */
    fun reactiveControllerPseudoCode() {
        // This is just a placeholder showing the concept
        println("Spring WebFlux controller example - see comments for implementation")
    }

    /**
     * Example of a homepage component
     */
    class HomePage : Composable {
        override fun <T> compose(receiver: T): T {
            // Basic implementation - in a real app, this would be more complex
            return Column(
                modifier = Modifier(),
                content = listOf(
                    Text("Summon - Home Page")
                )
            ).compose(receiver)
        }
    }

    /**
     * Example of a dynamic page component
     */
    class DynamicPage : Composable {
        override fun <T> compose(receiver: T): T {
            // Basic implementation - in a real app, this would be more complex
            return Column(
                modifier = Modifier(),
                content = listOf(
                    Text("Summon - Dynamic Page")
                )
            ).compose(receiver)
        }
    }

    /**
     * Example of a large page component
     */
    class LargePage : Composable {
        override fun <T> compose(receiver: T): T {
            // Basic implementation - in a real app, this would generate a lot of content
            val items = (1..100).map { index ->
                Text("Item $index")
            }

            return Column(
                modifier = Modifier(),
                content = listOf(Text("Summon - Large Page")) + items
            ).compose(receiver)
        }
    }
} 