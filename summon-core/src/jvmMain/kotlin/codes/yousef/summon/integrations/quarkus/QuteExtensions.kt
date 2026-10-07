package codes.yousef.summon.integration.quarkus


import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import io.quarkus.qute.RawString
import io.quarkus.qute.TemplateExtension

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
    internal fun contains(name: String): Boolean = components.containsKey(name)


    /**
     * Render a component by name
     *
     * @param name The name of the component to render
     * @return HTML string or empty string if component not found
     */
    fun renderComponent(name: String): String {
        val component = components[name] ?: return ""

        return PlatformRenderer().renderComposableRoot(component)
    }
}

/**
 * Qute template extensions for rendering Summon components
 */
@TemplateExtension
class QuteExtensions {
    /** Provides qute extensions factory and constant members. */
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
         * <HTML>
         *   <body>
         *     {summon:component('header')}
         *   </body>
         * </HTML>
         * ```
         */
    }
}
