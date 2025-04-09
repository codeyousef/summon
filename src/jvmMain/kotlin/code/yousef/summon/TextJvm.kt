package code.yousef.summon

import code.yousef.summon.modifier.Modifier
import kotlinx.html.*

/**
 * Data class to hold Text-related properties for JVM rendering
 */
data class TextJvmExtension(
    val text: String,
    val modifier: Modifier,
    val additionalStyles: Map<String, String> = emptyMap(),
    val accessibilityAttributes: Map<String, String> = emptyMap()
)

/**
 * Extension function to convert Modifier to CSS style string
 */
fun Modifier.toStyleString(): String {
    return this.styles.entries.joinToString(";") { (key, value) -> "$key:$value" }
}

/**
 * JVM implementation for the Text component rendering.
 * This is used by the JvmPlatformRenderer.
 */
fun <T> renderTextJvm(consumer: TagConsumer<T>, textExt: TextJvmExtension): TagConsumer<T> {
    consumer.span {
        // Apply the modifier styles
        style = textExt.modifier.toStyleString()

        // Apply additional text-specific styles
        if (textExt.additionalStyles.isNotEmpty()) {
            val existingStyle = this.attributes["style"] ?: ""
            val additionalStyleString = textExt.additionalStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
            this.attributes["style"] =
                if (existingStyle.isEmpty()) additionalStyleString else "$existingStyle;$additionalStyleString"
        }

        // Apply accessibility attributes
        textExt.accessibilityAttributes.forEach { (key, value) ->
            this.attributes[key] = value
        }

        // Add the text content
        +textExt.text
    }
    return consumer
} 
