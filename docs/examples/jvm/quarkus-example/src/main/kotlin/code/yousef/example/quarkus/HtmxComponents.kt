package code.yousef.example.quarkus

import code.yousef.summon.integrations.quarkus.htmx.HtmxContainer
import code.yousef.summon.runtime.Composable

/**
 * Components that use HTMX to load content from endpoints.
 * These components use the library's HtmxComponents to ensure that HTMX attributes
 * are rendered correctly as separate HTML attributes.
 */

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
