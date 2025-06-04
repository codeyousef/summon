package code.yousef.summon.integrations.quarkus

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import io.quarkus.qute.RawString
import io.quarkus.qute.TemplateExtension
import java.io.StringWriter

/**
 * Extension that provides custom tags for Qute templates to work with Summon components.
 *
 * Usage:
 * ```html
 * {component.render}
 * ```
 *
 * Where `component` is a Summon component object passed into the template.
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
    fun render(component: Any): RawString {
        val renderer = PlatformRenderer()
        // Create a simple HTML representation
        val html = "<div class=\"summon-component\">Component: ${component::class.simpleName}</div>"
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
        // Check if the class is annotated with @Composable
        return obj != null && obj::class.java.annotations.any { 
            it.annotationClass == Composable::class
        }
    }

    /**
     * Apply a modifier to a component for template use.
     *
     * @param component The component to modify
     * @param className The CSS class name to add
     * @return A new component with the modifier applied
     */
    @JvmStatic
    fun withClass(component: Any, className: String): Any {
        // This implementation would depend on the specific API of your component
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
    fun withContainer(component: Any, id: String? = null): RawString {
        val html = render(component).toString()
        
        val idAttr = if (id != null) " id=\"$id\"" else ""
        val wrapped = "<div class=\"summon-container\"$idAttr>$html</div>"

        return RawString(wrapped)
    }
} 
