package code.yousef.summon.components.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.*
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

    // Convert TextStyle to Modifier styles
    var finalModifier = modifier

    // Apply text style properties to the modifier
    style.fontFamily?.let { finalModifier = finalModifier.fontFamily(it, null) }
    style.fontSize?.let { finalModifier = finalModifier.fontSize(it) }
    style.fontWeight?.let { finalModifier = finalModifier.fontWeight(it) }
    style.fontStyle?.let { finalModifier = finalModifier.style("font-style", it) }
    style.color?.let { finalModifier = finalModifier.color(it) }
    style.textDecoration?.let { finalModifier = finalModifier.textDecoration(it, null) }
    style.lineHeight?.let { finalModifier = finalModifier.lineHeight(it, null) }
    style.letterSpacing?.let { finalModifier = finalModifier.letterSpacing(it, null) }

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
