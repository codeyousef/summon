package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable

/**
 * Components that use HTMX to load content from endpoints.
 * These components use a direct HTML approach to ensure that HTMX attributes
 * are rendered correctly as separate HTML attributes, not as part of the style attribute.
 */

/**
 * A generic container that loads content from an HTMX endpoint.
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
fun HtmxContainer(
    id: String,
    endpoint: String,
    trigger: String = "load",
    target: String? = null,
    swap: String? = null,
    minHeight: String = "200px",
    loadingText: String = "Loading...",
    modifier: Modifier = Modifier()
) {
    println("HtmxContainer rendering with id: $id, endpoint: $endpoint")

    // Log the HTMX attributes for debugging
    println("HTMX attributes:")
    println("  - id: $id")
    println("  - hx-get: $endpoint")
    println("  - hx-trigger: $trigger")
    println("  - min-height: $minHeight")
    if (target != null) println("  - hx-target: $target")
    if (swap != null) println("  - hx-swap: $swap")

    // Create a modifier with only the standard attributes (not HTMX ones)
    var containerModifier = modifier
        .style("id", id)
        .style("min-height", minHeight)

    // Log the modifier to help with debugging
    println("HtmxContainer modifier: $containerModifier")

    // Create the raw HTML with all attributes properly separated
    val htmlContent = """
        <div id="$id" 
             hx-get="$endpoint" 
             hx-trigger="$trigger" 
             style="min-height: $minHeight;"
             ${if (target != null) "hx-target=\"$target\"" else ""}
             ${if (swap != null) "hx-swap=\"$swap\"" else ""}>
            $loadingText
        </div>
    """.trimIndent()

    // Directly inject the raw HTML at the top level
    // This ensures HTMX attributes are rendered correctly as separate HTML attributes
    Box(Modifier().style("html", htmlContent)) {
        // Empty content as the HTML is provided via the html attribute
    }

    println("HtmxContainer rendering completed")
}

/**
 * A hero component that loads content from the heroComponentHtml endpoint.
 * 
 * @param username The username to display in the hero component
 */
@Composable
fun HtmxHeroComponent(username: String) {
    println("HtmxHeroComponent rendering with username: $username")

    HtmxContainer(
        id = "hero-container",
        endpoint = "/api/hero-component?username=$username",
        minHeight = "300px",
        loadingText = "Loading hero component..."
    )

    println("HtmxHeroComponent rendering completed")
}

/**
 * A counter component that loads content from the counterComponentHtml endpoint.
 * 
 * @param initialValue The initial value of the counter (not used in the endpoint)
 */
@Composable
fun HtmxCounterComponent(initialValue: Int = 0) {
    println("HtmxCounterComponent rendering with initialValue: $initialValue")

    HtmxContainer(
        id = "counter-container",
        endpoint = "/api/counter-component",
        minHeight = "200px",
        loadingText = "Loading counter component..."
    )

    println("HtmxCounterComponent rendering completed")
}
