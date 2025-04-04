package code.yousef.summon

import kotlinx.html.TagConsumer

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
data class Text(
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
) : Composable, TextComponent {
    /**
     * Renders this Text composable using the platform-specific renderer.
     * @param receiver TagConsumer to render to
     * @return The TagConsumer for method chaining
     */
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            return PlatformRendererProvider.getRenderer().renderText(this, receiver as TagConsumer<T>)
        }
        return receiver
    }

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