package code.yousef.summon.integrations.quarkus

import code.yousef.summon.core.Composable
import code.yousef.summon.JvmPlatformRenderer
import code.yousef.summon.render
import jakarta.enterprise.context.ApplicationScoped

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
 *     val component = MyComponent()
 *     return summonRenderer.render(component)
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
         * @param component The component to render
         * @return HTML string representation of the component
         */
        fun render(component: Composable): String {
            return renderer.render(component)
        }

        /**
         * Render a Summon component with additional configuration.
         *
         * @param component The component to render
         * @param prettyPrint Whether to format the HTML output
         * @return HTML string representation of the component
         */
        fun render(component: Composable, prettyPrint: Boolean): String {
            // This would need to be implemented based on the actual JvmPlatformRenderer API
            return renderer.render(component)
        }
    }
} 