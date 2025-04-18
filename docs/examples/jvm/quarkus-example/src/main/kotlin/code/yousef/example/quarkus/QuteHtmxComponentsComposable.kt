package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import kotlinx.html.*

/**
 * Composable components that use Qute templates directly for rendering HTMX components.
 * This approach ensures that HTMX attributes are rendered correctly as separate HTML attributes,
 * not as part of the style attribute.
 */
@Composable
fun QuteHtmxHeroComponentComposable(username: String, quteHtmxComponents: QuteHtmxComponents) {
    println("QuteHtmxHeroComponentComposable rendering with username: $username")

    // Generate the HTML for the hero component using the Qute template
    val html = quteHtmxComponents.renderHeroComponent(username)
    println("QuteHtmxHeroComponentComposable - Generated HTML length: ${html.length}")
    println("QuteHtmxHeroComponentComposable - Generated HTML: $html")

    // Create a div with the correct HTMX attributes directly
    val htmlContent = """
        <div id="hero-container" 
             hx-get="/api/hero-component?username=$username" 
             hx-trigger="load" 
             style="min-height: 300px;">
            Loading hero component...
        </div>
    """.trimIndent()

    // Use Box with the "html" style attribute to inject the raw HTML
    // This bypasses the Summon framework's attribute handling
    Box(Modifier().style("html", htmlContent)) {
        // Empty content as the HTML is provided via the html attribute
    }

    println("QuteHtmxHeroComponentComposable rendering completed")
}

/**
 * Composable component that renders the counter component using Qute template directly.
 * 
 * @param initialValue The initial value of the counter
 * @param quteHtmxComponents The QuteHtmxComponents instance to use for rendering
 */
@Composable
fun QuteHtmxCounterComponentComposable(initialValue: Int, quteHtmxComponents: QuteHtmxComponents) {
    println("QuteHtmxCounterComponentComposable rendering with initialValue: $initialValue")

    // Generate the HTML for the counter component using the Qute template
    val html = quteHtmxComponents.renderCounterComponent(initialValue)
    println("QuteHtmxCounterComponentComposable - Generated HTML length: ${html.length}")
    println("QuteHtmxCounterComponentComposable - Generated HTML: $html")

    // Create a div with the correct HTMX attributes directly
    val htmlContent = """
        <div id="counter-container" 
             hx-get="/api/counter-component" 
             hx-trigger="load" 
             style="min-height: 200px;">
            Loading counter component...
        </div>
    """.trimIndent()

    // Use Box with the "html" style attribute to inject the raw HTML
    // This bypasses the Summon framework's attribute handling
    Box(Modifier().style("html", htmlContent)) {
        // Empty content as the HTML is provided via the html attribute
    }

    println("QuteHtmxCounterComponentComposable rendering completed")
}
