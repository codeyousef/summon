package code.yousef.summon.components.display

import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.PlatformRendererProvider

/**
 * Displays text with styling options.
 * This is a composable function that delegates rendering to the platform-specific renderer.
 *
 * @param text The text content to display.
 * @param modifier Modifier for applying styling and layout properties.
 * @param color Text color.
 * @param fontSize Font size (e.g., "16px", "1.2em").
 * @param fontWeight Font weight (e.g., "normal", "bold", "700").
 * @param fontFamily Font family (e.g., "Arial, sans-serif").
 * @param textAlign Text alignment ("left", "center", "right", "justify").
 * @param lineHeight Line height (e.g., "1.5", "24px").
 * @param letterSpacing Letter spacing (e.g., "normal", "1px").
 * @param textDecoration Text decoration ("none", "underline", "line-through").
 * @param textTransform Text transformation ("none", "uppercase", "lowercase", "capitalize").
 * @param whiteSpace White space handling ("normal", "nowrap", "pre").
 * @param wordBreak Word breaking strategy ("normal", "break-all", "keep-all").
 * @param wordSpacing Spacing between words ("normal", "2px").
 * @param textShadow Text shadow effect ("1px 1px 2px black").
 * @param textOverflow Handling of overflowing text ("clip", "ellipsis").
 * @param maxLines Maximum number of lines to display before truncating (uses -webkit-line-clamp).
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier(),
    color: String? = null,
    fontSize: String? = null,
    fontWeight: String? = null,
    fontFamily: String? = null,
    textAlign: String? = null,
    lineHeight: String? = null,
    letterSpacing: String? = null,
    textDecoration: String? = null,
    textTransform: String? = null,
    whiteSpace: String? = null,
    wordBreak: String? = null,
    wordSpacing: String? = null,
    textShadow: String? = null,
    textOverflow: String? = null,
    maxLines: Int? = null
    // Accessibility parameters (role, ariaLabel, etc.) are removed for now.
    // They might be added back as Modifier extensions or handled differently.
) {
    // Build the final modifier by applying text-specific styles
    val finalModifier = modifier
        .let { m -> color?.let { m.color(it) } ?: m }
        .let { m -> fontSize?.let { m.fontSize(it) } ?: m }
        .let { m -> fontWeight?.let { m.fontWeight(it) } ?: m }
        .let { m -> fontFamily?.let { m.fontFamily(it) } ?: m }
        .let { m -> textAlign?.let { m.textAlign(it) } ?: m }
        .let { m -> lineHeight?.let { m.lineHeight(it) } ?: m }
        .let { m -> letterSpacing?.let { m.letterSpacing(it) } ?: m }
        .let { m -> textDecoration?.let { m.textDecoration(it) } ?: m }
        .let { m -> textTransform?.let { m.textTransform(it) } ?: m }
        .let { m -> whiteSpace?.let { m.whiteSpace(it) } ?: m }
        .let { m -> wordBreak?.let { m.wordBreak(it) } ?: m }
        .let { m -> wordSpacing?.let { m.wordSpacing(it) } ?: m }
        .let { m -> textShadow?.let { m.textShadow(it) } ?: m }
        .let { m -> textOverflow?.let { m.textOverflow(it) } ?: m }
        .let { m -> maxLines?.let { m.maxLines(it) } ?: m }

    // Get the current platform renderer
    val renderer = PlatformRendererProvider.getPlatformRenderer()

    // Call the renderer's method
    renderer.renderText(value = text, modifier = finalModifier)
} 
