@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon

import code.yousef.summon.components.display.Text
import kotlinx.html.TagConsumer
import kotlinx.html.span
import kotlinx.html.style

/**
 * JS implementation for the Text component rendering.
 * This is used by the JsPlatformRenderer.
 */
fun <T> Text.renderJs(consumer: TagConsumer<T>): TagConsumer<T> {
    consumer.span {
        // Apply the modifier styles and additional text-specific styles
        val additionalStyles = getAdditionalStyles()
        val combinedStyles = modifier.styles + additionalStyles
        style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
        
        // Apply accessibility attributes
        getAccessibilityAttributes().forEach { (key, value) ->
            attributes[key] = value
        }
        
        +text
    }
    return consumer
} 