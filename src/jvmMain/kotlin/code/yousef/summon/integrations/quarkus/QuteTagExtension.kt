package code.yousef.summon.integrations.quarkus

import code.yousef.summon.Composable
import code.yousef.summon.JvmPlatformRenderer
import code.yousef.summon.render
import io.quarkus.qute.RawString
import io.quarkus.qute.TemplateExtension

/**
 * Extension that provides custom tags for Qute templates to work with Summon components.
 *
 * Usage:
 * ```html
 * {component.render}
 * ```
 *
 * Where `component` is a Summon Composable object passed into the template.
 */
@TemplateExtension
object QuteTagExtension {

    /**
     * Render a Summon component to HTML.
     *
     * @param component The Summon component to render
     * @return Raw HTML string that can be inserted into a template
     */
    @JvmStatic
    fun render(component: Composable): RawString {
        val renderer = JvmPlatformRenderer()
        val html = renderer.render(component)
        return RawString(html)
    }

    /**
     * Check if an object is a Summon component.
     *
     * @param obj The object to check
     * @return True if the object is a Summon component
     */
    @JvmStatic
    fun isSummonComponent(obj: Any?): Boolean {
        return obj is Composable
    }

    /**
     * Apply a modifier to a component for template use.
     *
     * @param component The component to modify
     * @param className The CSS class name to add
     * @return A new component with the modifier applied
     */
    @JvmStatic
    fun withClass(component: Composable, className: String): Composable {
        // This implementation would depend on the specific API of your Composable type
        // Here's a placeholder implementation
        return component
    }

    /**
     * Create a wrapper around a component that adds a container div.
     *
     * @param component The component to wrap
     * @param id Optional div ID
     * @return A raw HTML string containing the wrapped component
     */
    @JvmStatic
    fun withContainer(component: Composable, id: String? = null): RawString {
        val renderer = JvmPlatformRenderer()
        val html = renderer.render(component)

        val idAttr = if (id != null) " id=\"$id\"" else ""
        val wrapped = "<div class=\"summon-component\"$idAttr>$html</div>"

        return RawString(wrapped)
    }
} 