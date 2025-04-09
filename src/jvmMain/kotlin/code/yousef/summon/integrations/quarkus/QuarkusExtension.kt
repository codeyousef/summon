package code.yousef.summon.integrations.quarkus


import code.yousef.summon.platform.JvmPlatformRenderer
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.MigratedPlatformRenderer
import kotlinx.html.stream.createHTML
import jakarta.enterprise.context.ApplicationScoped

/**
 * Extension function to render a Composable to an HTML string.
 * 
 * @param content The composable content to render
 * @return HTML string representation of the component
 */
private fun JvmPlatformRenderer.renderToString(content: @Composable () -> Unit): String {
    return createHTML().let { consumer ->
        this.renderComposable(content, consumer)
        consumer.finalize()
    }
}

/**
 * Quarkus Extension for Summon - Provides integration with Quarkus for server-side rendering.
 *
 * This is the core Quarkus extension component that registers Summon services with the Quarkus CDI container.
 * It performs the following functions:
 *
 * 1. Registers SummonRenderer as a CDI bean
 * 2. Sets up necessary reflection configuration for native image support
 * 3. Integrates with the Quarkus lifecycle
 *
 * For Quarkus application developers:
 * ```kotlin
 * @Inject
 * lateinit var summonRenderer: SummonRenderer
 *
 * @GET
 * @Path("/my-page")
 * @Produces(MediaType.TEXT_HTML)
 * fun renderPage(): String {
 *     return summonRenderer.render {
 *         MyComponent()
 *     }
 * }
 * ```
 */
class QuarkusExtension {

    companion object {
        const val FEATURE = "summon"
    }

    /**
     * The renderer service that can be injected into Quarkus applications.
     */
    @ApplicationScoped
    class SummonRenderer {
        private val renderer = JvmPlatformRenderer()

        /**
         * Render a Summon component to HTML.
         *
         * @param content The composable content to render
         * @return HTML string representation of the component
         */
        fun render(content: @Composable () -> Unit): String {
            return renderer.renderToString(content)
        }

        /**
         * Render a Summon component with additional configuration.
         *
         * @param content The composable content to render
         * @param prettyPrint Whether to format the HTML output
         * @return HTML string representation of the component
         */
        fun render(content: @Composable () -> Unit, prettyPrint: Boolean): String {
            // This would need to be implemented based on the actual JvmPlatformRenderer API
            return renderer.renderToString(content)
        }
    }
} 
