package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.PlatformRenderer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.getPlatformRenderer

/**
 * A basic container component that renders a div element.
 *
 * @param modifier The modifier to apply to this composable.
 * @param content The composable content to display inside the div.
 */
@Composable
fun Div(
    modifier: Modifier = Modifier.create(),
    content: @Composable () -> Unit
) {
    val renderer = getPlatformRenderer()
    renderer.renderDiv(modifier)
    content()
}

/**
 * A basic container component that renders a span element.
 *
 * @param modifier The modifier to apply to this composable.
 * @param content The composable content to display inside the span.
 */
@Composable
fun Span(
    modifier: Modifier = Modifier.create(),
    content: @Composable () -> Unit
) {
    val renderer = getPlatformRenderer()
    renderer.renderSpan(modifier)
    content()
}

/**
 * Data class holding parameters for the Div component.
 */
data class DivData(
    val modifier: Modifier
)

/**
 * Data class holding parameters for the Span component.
 */
data class SpanData(
    val modifier: Modifier
) 