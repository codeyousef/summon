package code.yousef.summon.integrations.quarkus


import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.setPlatformRenderer
import io.quarkus.qute.TemplateExtension
import io.quarkus.qute.TemplateInstance
import io.quarkus.qute.RawString
import kotlinx.html.div
import kotlinx.html.stream.appendHTML

/**
 * Registry for Summon components that can be accessed by name in Qute templates.
 */
object QuteComponentRegistry {
    private val components = mutableMapOf<String, @Composable () -> Unit>()
    private val renderer = JvmPlatformRenderer()

    /**
     * Register a component with a specific name
     *
     * @param name The name to use in templates
     * @param component The composable function
     */
    fun register(name: String, component: @Composable () -> Unit) {
        components[name] = component
    }

    /**
     * Render a component by name
     *
     * @param name The name of the component to render
     * @return HTML string or empty string if component not found
     */
    fun renderComponent(name: String): String {
        val component = components[name] ?: return ""
        
        // Set up the renderer
        setPlatformRenderer(renderer)
        
        // Create HTML output
        return buildString {
            appendHTML().div {
                component()
            }
        }
    }
}

/**
 * Qute template extensions for rendering Summon components
 */
@TemplateExtension
class QuteExtensions {
    companion object {
        /**
         * Render a Summon component by name
         *
         * @param name The name of the component to render
         * @return Raw HTML string that can be included in templates
         */
        @JvmStatic
        @TemplateExtension(namespace = "summon")
        fun component(name: String): RawString {
            return RawString(QuteComponentRegistry.renderComponent(name))
        }

        /**
         * Example usage:
         *
         * First, register your components in your application:
         * ```
         * QuteComponentRegistry.register("header") {
         *     Text("This is the header component")
         * }
         * ```
         *
         * Then use them in Qute templates:
         * ```
         * <html>
         *   <body>
         *     {summon:component('header')}
         *   </body>
         * </html>
         * ```
         */
    }
} 