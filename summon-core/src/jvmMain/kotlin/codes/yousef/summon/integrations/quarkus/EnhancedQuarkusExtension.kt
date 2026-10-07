package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.foundation.TrustedHtml
import codes.yousef.summon.integration.quarkus.qute.QuteTemplateRenderer
import codes.yousef.summon.integration.quarkus.renderer.HtmxAwareRenderer
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer
import io.quarkus.qute.Template
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Produces
import jakarta.inject.Singleton
import org.jboss.logging.Logger

/**
 * Enhanced Quarkus Extension for Summon - Provides improved integration with Quarkus for server-side rendering.
 * This class extends the functionality of the original QuarkusExtension with support for HTMX attributes
 * and Qute templates.
 */
object EnhancedQuarkusExtension {

    // Constants
    /** The property declaration value. */
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
        fun renderTemplate(title: String, content: TrustedHtml): String {
            logger.info("Rendering trusted author template")
            return htmxAwareRenderer.renderTemplate(title, content)
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
    val html = TrustedHtml.fromAuthorCode(QuteTemplateRenderer.renderTemplate(template, data))
    val renderer = LocalPlatformRenderer.current
    renderer.renderBox(modifier) {
        renderer.renderRawHtml(html)
    }
}
