package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier // Correctly importing the @JsExported Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import kotlinx.html.FlowContent // Assuming this is for HTML DSL context, which is fine

/**
 * A basic container component that renders a div element.
 *
 * @param modifier The modifier to apply to this composable. Defaults to an empty Modifier.
 * @param content The composable content to display inside the div.
 */
@Composable
fun Div(
    modifier: Modifier = Modifier(), // Updated: Using the Modifier() constructor for an empty modifier
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderDiv(modifier, content)
}

/**
 * A basic container component that renders a span element.
 *
 * @param modifier The modifier to apply to this composable. Defaults to an empty Modifier.
 * @param content The composable content to display inside the span.
 */
@Composable
fun Span(
    modifier: Modifier = Modifier(), // Updated: Using the Modifier() constructor for an empty modifier
    content: @Composable FlowContent.() -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    renderer.renderSpan(modifier, content)
}

/**
 * Data class holding parameters for the Div component.
 * This remains unchanged as it's a type declaration, not an instantiation with .create().
 */
data class DivData(
    val modifier: Modifier
)

/**
 * Data class holding parameters for the Span component.
 * This remains unchanged as it's a type declaration, not an instantiation with .create().
 */
data class SpanData(
    val modifier: Modifier
)