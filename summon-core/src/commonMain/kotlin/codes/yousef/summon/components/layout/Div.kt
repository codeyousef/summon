package codes.yousef.summon.components.layout

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.LocalPlatformRenderer

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