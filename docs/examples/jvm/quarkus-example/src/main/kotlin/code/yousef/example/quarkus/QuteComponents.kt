package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import io.quarkus.qute.Template
import io.quarkus.qute.Location
import io.quarkus.qute.RawString
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import kotlinx.html.stream.createHTML

/**
 * Components that use Qute templates to render UI components.
 * This approach follows Strategy 1 from the Qute integration guide:
 * Embedding kotlinx.html Output in Qute Templates.
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
 * A composable function that renders the hero component using Qute.
 * This function is used in Summon components to render the hero component.
 * 
 * @param username The username to display in the hero component
 * @param quteComponents The QuteComponents instance to use for rendering
 */
@Composable
fun QuteHeroComponent(username: String, quteComponents: QuteComponents) {
    println("QuteHeroComponent rendering with username: $username")

    // Generate the HTML for the hero component using the Qute template
    val html = quteComponents.renderHeroComponent(username)
    println("QuteHeroComponent - Generated HTML length: ${html.length}")

    // Create a div element to hold the hero component
    // We'll use direct HTML injection in WebResource.kt instead of trying to use Box with html style
    // Use a more direct approach to ensure the ID is properly set
    Box(
        Modifier()
            .style("id", "hero-component-container")
            .style("data-component", "hero")  // Add a data attribute for easier debugging
    ) {
        Text("Hero component will be rendered here")
    }

    // Log that we've created the hero container
    println("Created hero container with ID: hero-component-container")

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

    // Generate the HTML for the counter component using the Qute template
    val html = quteComponents.renderCounterComponent(initialValue)
    println("QuteCounterComponent - Generated HTML length: ${html.length}")

    // Create a div element to hold the counter component
    // We'll use direct HTML injection in WebResource.kt instead of trying to use Box with html style
    // Use a more direct approach to ensure the ID is properly set
    Box(
        Modifier()
            .style("id", "counter-component-container")
            .style("data-component", "counter")  // Add a data attribute for easier debugging
    ) {
        Text("Counter component will be rendered here")
    }

    // Log that we've created the counter container
    println("Created counter container with ID: counter-component-container")

    println("QuteCounterComponent rendering completed")
}
