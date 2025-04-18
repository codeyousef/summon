package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import kotlinx.html.*
import kotlinx.html.stream.createHTML

/**
 * A component that renders an HTMX container directly as HTML.
 * This bypasses the JvmPlatformRenderer's attribute handling and ensures that
 * HTMX attributes are rendered correctly as separate HTML attributes.
 *
 * @param id The ID of the container element
 * @param endpoint The endpoint to load content from
 * @param trigger The HTMX trigger event (default: "load")
 * @param target The HTMX target element (optional)
 * @param swap The HTMX swap method (optional)
 * @param minHeight The minimum height of the container (default: "200px")
 * @param loadingText The text to display while loading (default: "Loading...")
 */
@Composable
fun DirectHtmxContainer(
    id: String,
    endpoint: String,
    trigger: String = "load",
    target: String? = null,
    swap: String? = null,
    minHeight: String = "200px",
    loadingText: String = "Loading..."
) {
    println("DirectHtmxContainer rendering with id: $id, endpoint: $endpoint")

    // Create a custom HTML string with the correct attributes
    val htmlAttributes = StringBuilder()
    htmlAttributes.append("id=\"$id\" ")
    htmlAttributes.append("hx-get=\"$endpoint\" ")
    htmlAttributes.append("hx-trigger=\"$trigger\" ")
    if (target != null) {
        htmlAttributes.append("hx-target=\"$target\" ")
    }
    if (swap != null) {
        htmlAttributes.append("hx-swap=\"$swap\" ")
    }
    htmlAttributes.append("style=\"min-height: $minHeight\"")

    // Use Box with HTML style to inject the container with correct attributes
    Box {
        Box(Modifier().style("html", """
            <div ${htmlAttributes}>
                $loadingText
            </div>
        """.trimIndent())) {
            Text("") // Empty content to satisfy the content parameter
        }
    }

    println("DirectHtmxContainer rendering completed")
}

/**
 * A hero component that loads content from the heroComponentHtml endpoint.
 * This component uses DirectHtmxContainer to ensure HTMX attributes are rendered correctly.
 * 
 * @param username The username to display in the hero component
 */
@Composable
fun DirectHtmxHeroComponent(username: String) {
    println("DirectHtmxHeroComponent rendering with username: $username")

    DirectHtmxContainer(
        id = "hero-container",
        endpoint = "/api/hero-component?username=$username",
        minHeight = "300px",
        loadingText = "Loading hero component..."
    )

    println("DirectHtmxHeroComponent rendering completed")
}

/**
 * A counter component that loads content from the counterComponentHtml endpoint.
 * This component uses DirectHtmxContainer to ensure HTMX attributes are rendered correctly.
 * 
 * @param initialValue The initial value of the counter (not used in the endpoint)
 */
@Composable
fun DirectHtmxCounterComponent(initialValue: Int = 0) {
    println("DirectHtmxCounterComponent rendering with initialValue: $initialValue")

    DirectHtmxContainer(
        id = "counter-container",
        endpoint = "/api/counter-component",
        minHeight = "200px",
        loadingText = "Loading counter component..."
    )

    println("DirectHtmxCounterComponent rendering completed")
}
