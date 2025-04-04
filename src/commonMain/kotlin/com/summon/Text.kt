package com.summon

import kotlinx.html.TagConsumer

/**
 * A composable that displays text.
 * @param text The text content to display
 * @param modifier The modifier to apply to this composable
 * @param overflow Determines how text overflow should be handled ('ellipsis', 'clip', etc.)
 * @param lineHeight Line height for the text (e.g., '1.5', '24px')
 * @param textAlign Text alignment ('left', 'center', 'right', 'justify')
 * @param fontFamily Font family for the text
 * @param textDecoration Text decoration ('underline', 'line-through', 'none', etc.)
 * @param textTransform Text transformation ('uppercase', 'lowercase', 'capitalize', 'none')
 * @param letterSpacing Letter spacing (e.g., '0.5px', 'normal')
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
    val letterSpacing: String? = null
) : Composable {
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

        return styles
    }
} 