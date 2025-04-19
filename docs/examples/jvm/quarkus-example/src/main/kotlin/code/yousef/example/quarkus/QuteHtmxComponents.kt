package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.integrations.quarkus.htmx.HtmxContainer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import io.quarkus.qute.Template
import io.quarkus.qute.Location
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

/**
 * Components that use Qute templates to render HTMX components.
 * This approach ensures that HTMX attributes are rendered correctly as separate HTML attributes,
 * not as part of the style attribute.
 */
@ApplicationScoped
class QuteHtmxComponents {

    @Location("hero-component.html")
    @Inject
    lateinit var heroTemplate: Template

    @Location("counter-component.html")
    @Inject
    lateinit var counterTemplate: Template

    /**
     * Renders the hero component using a Qute template.
     * 
     * @param username The username to display in the hero component
     * @return The rendered HTML
     */
    fun renderHeroComponent(username: String): String {
        return heroTemplate.data("username", username).render()
    }

    /**
     * Renders the counter component using a Qute template.
     * 
     * @param currentValue The current value of the counter
     * @return The rendered HTML
     */
    fun renderCounterComponent(currentValue: Int): String {
        return counterTemplate.data("currentValue", currentValue).render()
    }
}

/**
 * A composable function that renders the hero component using HTMX.
 * This function is used in Summon components to render the hero component.
 * 
 * @param username The username to display in the hero component
 */
@Composable
fun QuteHtmxHeroComponent(username: String) {
    println("QuteHtmxHeroComponent rendering with username: $username")

    HtmxContainer(
        id = "hero-container",
        endpoint = "/api/hero-component?username=$username",
        minHeight = "300px",
        loadingText = "Loading hero component..."
    )

    println("QuteHtmxHeroComponent rendering completed")
}

/**
 * A composable function that renders the counter component using HTMX.
 * This function is used in Summon components to render the counter component.
 * 
 * @param initialValue The initial value of the counter
 */
@Composable
fun QuteHtmxCounterComponent(initialValue: Int = 0) {
    println("QuteHtmxCounterComponent rendering with initialValue: $initialValue")

    HtmxContainer(
        id = "counter-container",
        endpoint = "/api/counter-component",
        minHeight = "200px",
        loadingText = "Loading counter component..."
    )

    println("QuteHtmxCounterComponent rendering completed")
}
