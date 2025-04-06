package code.yousef.summon.components.display

import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import code.yousef.summon.annotation.Composable

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
    // Get the current tag consumer (assuming a mechanism exists to get it - placeholder for now)
    // This part needs integration with the core composition logic update later.
    // For now, let's assume a way to get the receiver/consumer.
    // We might need to adjust this function signature later to accept the consumer/receiver.

    // Create the data holder object
    val textData = TextData(
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

    // Delegate rendering to the platform renderer
    // We assume PlatformRendererProvider.getRenderer() and a way to get the current TagConsumer 'receiver'
    // This composition logic needs refinement. Example:
    // val receiver = currentComposer.consume // Hypothetical
    // if (receiver is TagConsumer<*>) {
    //     @Suppress("UNCHECKED_CAST")
    //     PlatformRendererProvider.getRenderer().renderText(textData, receiver as TagConsumer<Any?>)
    // }
    
    // Placeholder: Actual rendering call needs integration with composer context
    println("Composable Text function called with text: $text") // Placeholder
}

/**
 * Data class holding parameters for the Text composable.
 * This is used internally and passed to the renderer.
 */
data class TextData(
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
     * Gets a map of all additional style properties that should be applied
     * beyond what is in the modifier.
     */
    internal fun getAdditionalStyles(): Map<String, String> {
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
    internal fun getAccessibilityAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        role?.let { attributes["role"] = it }
        ariaLabel?.let { attributes["aria-label"] = it }
        ariaDescribedBy?.let { attributes["aria-describedby"] = it }

        return attributes
    }
} 