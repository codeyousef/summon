package code.yousef.example.quarkus

import code.yousef.summon.components.layout.Box
import code.yousef.summon.integrations.quarkus.qute.QuteTemplate
import code.yousef.summon.integrations.quarkus.qute.QuteTemplateRenderer
import code.yousef.summon.integrations.quarkus.htmx.htmlAttribute
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import io.quarkus.qute.Template
import io.quarkus.qute.Location
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Components that use Qute templates to render UI components.
 * This class uses the library's QuteTemplateRenderer to render Qute templates.
 */
@ApplicationScoped
class QuteComponents {

    @Location("hero-component-qute.html")
    @Inject
    lateinit var heroTemplate: Template

    @Location("counter-component-qute.html")
    @Inject
    lateinit var counterTemplate: Template

    /**
     * Renders the hero component using a Qute template.
     * 
     * @param username The username to display in the hero component
     * @return The rendered HTML
     */
    fun renderHeroComponent(username: String): String {
        return QuteTemplateRenderer.renderTemplate(heroTemplate, "username" to username)
    }

    /**
     * Renders the counter component using a Qute template.
     * 
     * @param currentValue The current value of the counter
     * @return The rendered HTML
     */
    fun renderCounterComponent(currentValue: Int): String {
        return QuteTemplateRenderer.renderTemplate(counterTemplate, "currentValue" to currentValue)
    }
}

/**
 * A composable function that renders the hero component using Qute.
 * This function is used in Summon components to render the hero component.
 * 
 * @param username The username to display in the hero component
 * @param quteComponents The QuteComponents instance to use for rendering
 */
@Composable
fun QuteHeroComponent(username: String, quteComponents: QuteComponents) {
    println("QuteHeroComponent rendering with username: $username")

    // Use the library's QuteTemplate component to render the hero component
    QuteTemplate(
        template = quteComponents.heroTemplate,
        modifier = Modifier()
            .htmlAttribute("id", "hero-component-container")
            .htmlAttribute("data-component", "hero"),
        "username" to username
    )

    println("QuteHeroComponent rendering completed")
}

/**
 * A composable function that renders the counter component using Qute.
 * This function is used in Summon components to render the counter component.
 * 
 * @param initialValue The initial value of the counter
 * @param quteComponents The QuteComponents instance to use for rendering
 */
@Composable
fun QuteCounterComponent(initialValue: Int, quteComponents: QuteComponents) {
    println("QuteCounterComponent rendering with initialValue: $initialValue")

    // Use the library's QuteTemplate component to render the counter component
    QuteTemplate(
        template = quteComponents.counterTemplate,
        modifier = Modifier()
            .htmlAttribute("id", "counter-component-container")
            .htmlAttribute("data-component", "counter"),
        "currentValue" to initialValue
    )

    println("QuteCounterComponent rendering completed")
}
