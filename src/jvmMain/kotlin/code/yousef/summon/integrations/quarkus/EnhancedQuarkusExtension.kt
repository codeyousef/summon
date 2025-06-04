package code.yousef.summon.integrations.quarkus

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.layout.Box
import code.yousef.summon.integrations.quarkus.htmx.HtmxAttributeHandler
import code.yousef.summon.integrations.quarkus.htmx.htmlAttribute
import code.yousef.summon.integrations.quarkus.qute.QuteTemplateRenderer
import code.yousef.summon.integrations.quarkus.renderer.HtmxAwareRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.PlatformRenderer
import io.quarkus.qute.Template
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import kotlinx.html.FlowContent
import kotlinx.html.stream.createHTML
import org.jboss.logging.Logger

/**
 * Enhanced Quarkus Extension for Summon - Provides improved integration with Quarkus for server-side rendering.
 * This class extends the functionality of the original QuarkusExtension with support for HTMX attributes
 * and Qute templates.
 */
object EnhancedQuarkusExtension {

    // Constants
    const val FEATURE = "summon-enhanced"
    private val logger = Logger.getLogger(EnhancedQuarkusExtension::class.java)

    /**
     * Enhanced Summon renderer that properly handles HTMX attributes and Qute templates.
     * This class wraps the JvmPlatformRenderer and adds support for HTMX attributes and raw HTML content.
     */
    @Singleton
    class EnhancedSummonRenderer {
        private val logger = Logger.getLogger(EnhancedSummonRenderer::class.java)
        private val htmxAwareRenderer = HtmxAwareRenderer()

        /**
         * Renders a composable component to a string.
         * 
         * @param content The composable content to render
         * @return The rendered HTML as a string
         */
        fun renderToString(content: @Composable () -> Unit): String {
            // Use the HtmxAwareRenderer to render the composable content
            return htmxAwareRenderer.renderToString(content)
        }

        /**
         * Renders a template with the given title and content.
         * 
         * @param title The page title
         * @param content The HTML content to include in the template
         * @return The rendered HTML as a string
         */
        fun renderTemplate(title: String, content: String): String {
            logger.info("Rendering template with title: $title")
            logger.info("Content length: ${content.length} characters")

            // Use the HtmxAwareRenderer to render the template
            val result = htmxAwareRenderer.renderTemplate(title, content)

            logger.info("Finished rendering template, result length: ${result.length} characters")
            return result
        }
    }

    /**
     * A CDI producer for the EnhancedSummonRenderer.
     * This class is used to provide the EnhancedSummonRenderer as a CDI bean.
     */
    @ApplicationScoped
    class EnhancedQuarkusExtensionProducer {

        /**
         * Produces an EnhancedSummonRenderer instance for injection.
         * 
         * @return A singleton instance of EnhancedSummonRenderer
         */
        @Produces
        @Singleton
        fun produceEnhancedSummonRenderer(): EnhancedSummonRenderer {
            return EnhancedSummonRenderer()
        }
    }
}

/**
 * A composable function that renders a Qute template using the EnhancedSummonRenderer.
 * This function is a convenience wrapper around the QuteTemplate function.
 * 
 * @param template The Qute template to render
 * @param data Map of data to pass to the template
 * @param modifier Additional modifiers to apply to the container
 */
@Composable
fun EnhancedQuteTemplate(
    template: Template,
    data: Map<String, Any>,
    modifier: Modifier = Modifier()
) {
    // Use the QuteTemplateRenderer to render the template
    val html = QuteTemplateRenderer.renderTemplate(template, data)

    // Get the current platform renderer
    val renderer = LocalPlatformRenderer.current

    // Render a Box with the raw HTML content
    // Use htmlAttribute instead of style to ensure proper processing by HtmxAwareRenderer
    renderer.renderBox(modifier.htmlAttribute("__raw_html", html)) { }
}
