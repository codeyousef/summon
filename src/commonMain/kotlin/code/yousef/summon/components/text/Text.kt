package code.yousef.summon.components.text

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.core.PlatformRenderer
import code.yousef.summon.runtime.getPlatformRenderer

/**
 * A composable that displays text.
 *
 * @param text The text to be displayed
 * @param modifier The modifier to be applied to the text
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier.create()
) {
    val renderer = getPlatformRenderer()
    renderer.renderText(text, modifier)
}

/**
 * A composable that displays text using a content lambda.
 * This overload provides a more composable-friendly API that accepts a content lambda.
 *
 * @param modifier The modifier to be applied to the text
 * @param content The function that provides the text content
 */
@Composable
fun Text(
    modifier: Modifier = Modifier.create(),
    content: @Composable () -> String
) {
    val text = content()
    Text(text = text, modifier = modifier)
} 
