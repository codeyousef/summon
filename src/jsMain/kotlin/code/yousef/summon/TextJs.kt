@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon

import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import kotlinx.html.span
import kotlinx.html.style

/**
 * Data class to hold Text-related properties for JS rendering
 */
data class TextJsExtension(
    val text: String,
    val modifier: Modifier,
    val additionalStyles: Map<String, String> = emptyMap(),
    val accessibilityAttributes: Map<String, String> = emptyMap()
)

/**
 * JS implementation for the Text component rendering.
 * This is used by the JsPlatformRenderer.
 */
fun <T> renderTextJs(consumer: TagConsumer<T>, textExt: TextJsExtension): TagConsumer<T> {
    consumer.span {
        // Apply the modifier styles and additional text-specific styles
        val combinedStyles = textExt.modifier.styles + textExt.additionalStyles
        style = combinedStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
        
        // Apply accessibility attributes
        textExt.accessibilityAttributes.forEach { (key, value) ->
            attributes[key] = value
        }
        
        +textExt.text
    }
    return consumer
} 
