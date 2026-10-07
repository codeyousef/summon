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
 * Compatibility parameters for a division.
 *
 * @property modifier typed styles and attributes
 */
data class DivData(
    val modifier: Modifier
)

/**
 * Compatibility parameters for an inline span.
 *
 * @property modifier typed styles and attributes
 */
data class SpanData(
    val modifier: Modifier
)