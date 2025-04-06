package code.yousef.summon.components.layout

import code.yousef.summon.annotation.Composable
import code.yousef.summon.core.UIElement
import code.yousef.summon.modifier.Modifier

/**
 * A basic container component that renders a div element.
 *
 * @param modifier The modifier to apply to this composable.
 * @param content The composable content to display inside the div.
 */
@Composable
fun Div(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    UIElement(
        factory = { DivData(modifier = modifier) },
        update = { /* No specific update needed for DivData itself, rely on content recomposition */ },
        content = content
    )
}

/**
 * Data class holding parameters for the Div component.
 */
data class DivData(
    val modifier: Modifier
)

/**
 * A basic container component that renders a span element.
 *
 * @param modifier The modifier to apply to this composable.
 * @param content The composable content to display inside the span.
 */
@Composable
fun Span(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    UIElement(
        factory = { SpanData(modifier = modifier) },
        update = { /* No specific update needed for SpanData itself, rely on content recomposition */ },
        content = content
    )
}

/**
 * Data class holding parameters for the Span component.
 */
data class SpanData(
    val modifier: Modifier
) 