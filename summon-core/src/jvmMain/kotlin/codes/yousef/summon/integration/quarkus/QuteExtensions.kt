package codes.yousef.summon.integration.quarkus

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
import io.quarkus.qute.TemplateExtension
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

/**
 * Extensions for integrating Summon with Quarkus Qute templates.
 *
 * IMPORTANT: To use these extensions, you must add the following dependencies to your project:
 * - io.quarkus:quarkus-core
 * - io.quarkus:quarkus-qute
 * - io.quarkus:quarkus-kotlin
 */
object SummonQuteExtensions {
    /**
     * A registry of composable functions that can be rendered by name.
     * This allows Qute templates to reference components by string name.
     */
    private val componentRegistry = mutableMapOf<String, (Map<String, Any>) -> @Composable () -> Unit>()

    /**
     * Register a composable function with a name for use in Qute templates.
     *
     * @param name The name that will be used to reference this component in templates
     * @param factory A function that takes a map of properties and returns a composable
     */
    fun registerComponent(
        name: String,
        factory: (Map<String, Any>) -> @Composable () -> Unit
    ) {
        componentRegistry[name] = factory
    }

    /**
     * Render a Summon component by name with the provided properties.
     * This function can be called from Qute templates.
     *
     * @param componentName The name of the component to render
     * @param props The properties to pass to the component
     * @return HTML string output of the rendered component
     */
    @TemplateExtension
    fun renderSummon(componentName: String, props: Map<String, Any> = emptyMap()): String {
        val componentFactory = componentRegistry[componentName]
            ?: throw IllegalArgumentException("Component '$componentName' not registered")

        // Get the composable function for these props
        val composable = componentFactory(props)

        // Initialize the renderer
        val renderer = PlatformRenderer()
        setPlatformRenderer(renderer)

        // Render to string
        return buildString {
            appendHTML().html {
                body {
                    div {
                        composable()
                    }
                }
            }
        }
    }
}

/**
 * Example of registering a component:
 *
 * ```
 * // Register a simple Text component
 * SummonQuteExtensions.registerComponent("Greeting") { props ->
 *     {
 *         val name = props["name"] as? String ?: "World"
 *         Text("Hello, $name!")
 *     }
 * }
 *
 * // In Qute template:
 * // {renderSummon('Greeting', {'name': 'Quarkus User'})}
 * ```
 */ 