package code.yousef.summon.components.display

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.getPlatformRenderer

/**
 * A composable that displays text with enhanced styling and accessibility options.
 * @param text The text content to display
 * @param modifier The modifier to apply to this composable
 * @param overflow Determines how text overflow should be handled ('ellipsis', 'clip', etc.)
 * @param lineHeight Line height for the text (e.g., '1.5', '24px')
 * @param textAlign Text alignment ('left', 'center', 'right', 'justify')
 * @param fontFamily Font family for the text
 * @param textDecoration Text decoration ('underline', 'line-through', 'none', etc.)
 * @param textTransform Text transformation ('uppercase', 'lowercase', 'capitalize', 'none')
 * @param letterSpacing Letter spacing (e.g., '0.5px', 'normal')
 * @param whiteSpace How white space inside the element is handled ('normal', 'nowrap', 'pre', 'pre-line', 'pre-wrap', etc.)
 * @param wordBreak How to break words ('normal', 'break-all', 'keep-all', 'break-word')
 * @param wordSpacing Spacing between words (e.g., '0.5px', 'normal')
 * @param textShadow Text shadow (e.g., '1px 1px 2px black')
 * @param maxLines Maximum number of lines to display before truncating
 * @param role ARIA role for accessibility
 * @param ariaLabel Accessible name for screen readers
 * @param ariaDescribedBy ID of element that describes this text for accessibility
 */
@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier(),
    overflow: String? = null,
    lineHeight: String? = null,
    textAlign: String? = null,
    fontFamily: String? = null,
    textDecoration: String? = null,
    textTransform: String? = null,
    letterSpacing: String? = null,
    whiteSpace: String? = null,
    wordBreak: String? = null,
    wordSpacing: String? = null,
    textShadow: String? = null,
    maxLines: Int? = null,
    role: String? = null,
    ariaLabel: String? = null,
    ariaDescribedBy: String? = null
) {
    val composer = CompositionLocal.currentComposer

    // Apply text-specific styles to the modifier
    val additionalStyles = getAdditionalStyles(
        overflow, lineHeight, textAlign, fontFamily, textDecoration,
        textTransform, letterSpacing, whiteSpace, wordBreak, wordSpacing,
        textShadow, maxLines
    )

    // Apply accessibility attributes
    val accessibilityAttrs = getAccessibilityAttributes(role, ariaLabel, ariaDescribedBy)

    // Combine all styles and attributes
    val finalModifier = if (additionalStyles.isNotEmpty()) {
        modifier.styles(additionalStyles)
    } else {
        modifier
    }

    // Start composition node
    composer?.startNode()
    if (composer?.inserting == true) {
        val renderer = getPlatformRenderer()
        // Use the correct renderText method with value parameter
        renderer.renderText(value = text, modifier = finalModifier)
    }
    composer?.endNode()
}

/**
 * Backward compatibility class for existing code that expects a Text class instance
 * rather than using the @Composable function directly.
 *
 * @deprecated Use the @Composable Text function instead for new code
 */
data class TextComponent(
    val text: String,
    val modifier: Modifier = Modifier(),
    val overflow: String? = null,
    val lineHeight: String? = null,
    val textAlign: String? = null,
    val fontFamily: String? = null,
    val textDecoration: String? = null,
    val textTransform: String? = null,
    val letterSpacing: String? = null,
    val whiteSpace: String? = null,
    val wordBreak: String? = null,
    val wordSpacing: String? = null,
    val textShadow: String? = null,
    val maxLines: Int? = null,
    val role: String? = null,
    val ariaLabel: String? = null,
    val ariaDescribedBy: String? = null
) {
    /**
     * Renders this Text using the @Composable function.
     * Used for backward compatibility with existing code.
     */
    @Composable
    fun render() {
        Text(
            text = text,
            modifier = modifier,
            overflow = overflow,
            lineHeight = lineHeight,
            textAlign = textAlign,
            fontFamily = fontFamily,
            textDecoration = textDecoration,
            textTransform = textTransform,
            letterSpacing = letterSpacing,
            whiteSpace = whiteSpace,
            wordBreak = wordBreak,
            wordSpacing = wordSpacing,
            textShadow = textShadow,
            maxLines = maxLines,
            role = role,
            ariaLabel = ariaLabel,
            ariaDescribedBy = ariaDescribedBy
        )
    }

    /**
     * For compatibility with old TagConsumer-based code
     */
    fun <T> compose(receiver: T): T {
        // No-op for compatibility, actual rendering will happen via render() in a Composable context
        return receiver
    }
}

/**
 * Gets a map of all additional style properties that should be applied
 * beyond what is in the modifier.
 */
private fun getAdditionalStyles(
    overflow: String?,
    lineHeight: String?,
    textAlign: String?,
    fontFamily: String?,
    textDecoration: String?,
    textTransform: String?,
    letterSpacing: String?,
    whiteSpace: String?,
    wordBreak: String?,
    wordSpacing: String?,
    textShadow: String?,
    maxLines: Int?
): Map<String, String> {
    val styles = mutableMapOf<String, String>()

    overflow?.let { styles["overflow"] = it }
    overflow?.let { styles["text-overflow"] = it }
    lineHeight?.let { styles["line-height"] = it }
    textAlign?.let { styles["text-align"] = it }
    fontFamily?.let { styles["font-family"] = it }
    textDecoration?.let { styles["text-decoration"] = it }
    textTransform?.let { styles["text-transform"] = it }
    letterSpacing?.let { styles["letter-spacing"] = it }
    whiteSpace?.let { styles["white-space"] = it }
    wordBreak?.let { styles["word-break"] = it }
    wordSpacing?.let { styles["word-spacing"] = it }
    textShadow?.let { styles["text-shadow"] = it }

    // Handle max lines with -webkit-line-clamp for truncating
    if (maxLines != null) {
        styles["display"] = "-webkit-box"
        styles["-webkit-line-clamp"] = maxLines.toString()
        styles["-webkit-box-orient"] = "vertical"
        styles["overflow"] = "hidden"
    }

    return styles
}

/**
 * Gets a map of all accessibility attributes that should be applied.
 */
private fun getAccessibilityAttributes(
    role: String?,
    ariaLabel: String?,
    ariaDescribedBy: String?
): Map<String, String> {
    val attributes = mutableMapOf<String, String>()

    role?.let { attributes["role"] = it }
    ariaLabel?.let { attributes["aria-label"] = it }
    ariaDescribedBy?.let { attributes["aria-describedby"] = it }

    return attributes
} 