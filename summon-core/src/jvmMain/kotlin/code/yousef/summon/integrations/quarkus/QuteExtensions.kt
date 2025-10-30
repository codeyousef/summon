package code.yousef.summon.integrations.quarkus


import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CallbackRegistry
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.clearPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import io.quarkus.qute.RawString
import io.quarkus.qute.TemplateExtension
import kotlinx.html.div
import kotlinx.html.stream.appendHTML

/**
 * Registry for Summon components that can be accessed by name in Qute templates.
 */
object QuteComponentRegistry {
    private val components = java.util.concurrent.ConcurrentHashMap<String, @Composable () -> Unit>()

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
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        return try {
            buildString {
                appendHTML().div {
                    component()
                }
            }
        } finally {
            CallbackRegistry.clear()
            clearPlatformRenderer()
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
