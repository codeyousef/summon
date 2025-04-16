package code.yousef.summon.components.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.theme.TextStyle

/**
 * Represents the result of a text layout operation.
 * Contains information about text dimensions, line breaks, etc.
 */
class TextLayoutResult(
    val width: Float,
    val height: Float,
    val lineCount: Int = 1,
    val hasVisualOverflow: Boolean = false
)

/**
 * A basic Text component that renders text.
 * This is a simpler version of the Text component with fewer options.
 */
@Composable
fun BasicText(
    text: String,
    modifier: Modifier = Modifier(),
    style: TextStyle = TextStyle(),
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    // Get the platform renderer using CompositionLocal
    val renderer = LocalPlatformRenderer.current
    
    // TODO: Convert TextStyle to Modifier styles if needed.
    // For now, just use the passed modifier.
    val finalModifier = modifier
    
    // Render the text using the platform renderer with correct arguments
    renderer.renderText(text = text, modifier = finalModifier)
    
    // Create a layout result (simplified for now)
    val layoutResult = TextLayoutResult(
        width = text.length.toFloat() * 8, // Rough estimate
        height = 20f // Default height
    )
    
    // Notify the callback
    onTextLayout(layoutResult)
}

/**
 * Marker annotation for composable functions.
 * This will eventually be replaced by the actual Compose annotation.
 */
// Removed redundant annotation class as we're now using the one from runtime package 