package codes.yousef.summon.integration.quarkus.qute

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.layout.Box
import codes.yousef.summon.integration.quarkus.htmx.htmlAttribute
import codes.yousef.summon.modifier.*
import io.quarkus.qute.Template

/**
 * Renderer for Qute templates in Summon components.
 * This class provides utilities for rendering Qute templates and integrating them with Summon components.
 */
class QuteTemplateRenderer {
    companion object {
        /**
         * Renders a Qute template with the given data and returns the HTML as a string.
         *
         * @param template The Qute template to render
         * @param data Map of data to pass to the template
         * @return The rendered HTML as a string
         */
        fun renderTemplate(template: Template, data: Map<String, Any>): String {
            // Create a template instance with the data
            val templateInstance = template.instance()

            // Add all data to the template instance
            data.forEach { (key, value) ->
                templateInstance.data(key, value)
            }

            // Render the template and return the HTML
            return templateInstance.render()
        }

        /**
         * Renders a Qute template with the given data and returns the HTML as a string.
         * This overload allows for a more concise syntax with varargs.
         *
         * @param template The Qute template to render
         * @param pairs Pairs of data to pass to the template
         * @return The rendered HTML as a string
         */
        fun renderTemplate(template: Template, vararg pairs: Pair<String, Any>): String {
            return renderTemplate(template, pairs.toMap())
        }
    }
}

/**
 * A composable function that renders a Qute template.
 * This function uses the QuteTemplateRenderer to render the template and integrates it with Summon components.
 *
 * @param template The Qute template to render
 * @param data Map of data to pass to the template
 * @param modifier Additional modifiers to apply to the container
 */
@Composable
fun QuteTemplate(
    template: Template,
    data: Map<String, Any>,
    modifier: Modifier = Modifier()
) {
    // Render the template
    val html = QuteTemplateRenderer.renderTemplate(template, data)

    // Use the htmlAttribute extension function to add the raw HTML content
    // This will be processed by the enhanced JvmPlatformRenderer
    Box(modifier.htmlAttribute("__raw_html", html)) {
        // Empty content as the HTML is provided via the __raw_html attribute
    }
}

/**
 * A composable function that renders a Qute template.
 * This overload allows for a more concise syntax with varargs.
 *
 * @param template The Qute template to render
 * @param modifier Additional modifiers to apply to the container
 * @param pairs Pairs of data to pass to the template
 */
@Composable
fun QuteTemplate(
    template: Template,
    modifier: Modifier = Modifier(),
    vararg pairs: Pair<String, Any>
) {
    QuteTemplate(template, pairs.toMap(), modifier)
}

/**
 * A composable function that renders a Qute template with a specific ID.
 * This function is useful when you need to reference the rendered template by ID.
 *
 * @param id The ID to assign to the container element
 * @param template The Qute template to render
 * @param data Map of data to pass to the template
 * @param modifier Additional modifiers to apply to the container
 */
@Composable
fun QuteTemplateWithId(
    id: String,
    template: Template,
    data: Map<String, Any>,
    modifier: Modifier = Modifier()
) {
    // Render the template
    val html = QuteTemplateRenderer.renderTemplate(template, data)

    // Use the htmlAttribute extension function to add the raw HTML content and ID
    Box(modifier.htmlAttribute("id", id).htmlAttribute("__raw_html", html)) {
        // Empty content as the HTML is provided via the __raw_html attribute
    }
}

/**
 * A composable function that renders a Qute template with a specific ID.
 * This overload allows for a more concise syntax with varargs.
 *
 * @param id The ID to assign to the container element
 * @param template The Qute template to render
 * @param modifier Additional modifiers to apply to the container
 * @param pairs Pairs of data to pass to the template
 */
@Composable
fun QuteTemplateWithId(
    id: String,
    template: Template,
    modifier: Modifier = Modifier(),
    vararg pairs: Pair<String, Any>
) {
    QuteTemplateWithId(id, template, pairs.toMap(), modifier)
}
