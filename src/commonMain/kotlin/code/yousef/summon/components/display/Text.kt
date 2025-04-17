package code.yousef.summon.components.display

import code.yousef.summon.annotation.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.modifier.ModifierExtras.attribute
import kotlinx.html.FlowContent
import code.yousef.summon.modifier.style

/**
 * A composable that displays text with enhanced styling and accessibility options.
 * 
 * This component is used for general text display and renders as a semantic paragraph or span element.
 * For form labels that are associated with input elements, use the [Label] component instead.
 * 
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
 * @param semantic Optional semantic role for the text ('paragraph', 'heading', 'caption', etc.). 
 *                 This affects the HTML element used for rendering.
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
    ariaDescribedBy: String? = null,
    semantic: String? = null
) {
    // Apply text-specific styles to the modifier
    val additionalStyles = getAdditionalStyles(
        overflow, lineHeight, textAlign, fontFamily, textDecoration,
        textTransform, letterSpacing, whiteSpace, wordBreak, wordSpacing,
        textShadow, maxLines
    )

    // Apply accessibility attributes
    val accessibilityAttrs = getAccessibilityAttributes(role, ariaLabel, ariaDescribedBy)

    // Combine all styles and attributes
    var finalModifier = modifier
    if (additionalStyles.isNotEmpty()) {
        for ((key, value) in additionalStyles) {
            finalModifier = finalModifier.style(key, value)
        }
    }

    // Add accessibility attributes
    for ((key, value) in accessibilityAttrs) {
        finalModifier = finalModifier.attribute(key, value)
    }

    // Add semantic role if provided
    if (semantic != null) {
        finalModifier = finalModifier.attribute("data-semantic", semantic)
    }

    // Call the PlatformRenderer
    val renderer = LocalPlatformRenderer.current
    renderer.renderText(text = text, modifier = finalModifier)
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
    val ariaDescribedBy: String? = null,
    val semantic: String? = null
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
            ariaDescribedBy = ariaDescribedBy,
            semantic = semantic
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

/**
 * A composable that displays text specifically designed for form labels.
 * This component renders as a semantic HTML label element and should be used
 * when the text is labeling a form control.
 *
 * For general text display that is not associated with form controls,
 * use the [Text] component instead.
 *
 * @param text The text string to display.
 * @param modifier The modifier to apply to this composable.
 * @param forElement Optional ID of the form element this label is associated with.
 *                   This creates a programmatic association between the label and the form control.
 */
@Composable
fun Label(text: String, modifier: Modifier = Modifier(), forElement: String? = null) {
    // Get the platform renderer
    val renderer = LocalPlatformRenderer.current

    // Use the semantic renderLabel method
    renderer.renderLabel(text = text, modifier = modifier, forElement = forElement)
}
