package code.yousef.summon

import code.yousef.summon.components.display.Text
import kotlinx.html.TagConsumer
import kotlinx.html.span

/**
 * JVM implementation for the Text component rendering.
 * This is used by the JvmPlatformRenderer.
 */
fun <T> Text.renderJvm(consumer: TagConsumer<T>): TagConsumer<T> {
    consumer.span {
        // Apply the modifier styles - explicitly calling JVM implementation
        val hoverClass = modifier.applyStyles(this)

        // Apply additional text-specific styles
        val additionalStyles = getAdditionalStyles()
        if (additionalStyles.isNotEmpty()) {
            val existingStyle = this.attributes["style"] ?: ""
            val additionalStyleString = additionalStyles.entries.joinToString(";") { (key, value) -> "$key:$value" }
            this.attributes["style"] =
                if (existingStyle.isEmpty()) additionalStyleString else "$existingStyle;$additionalStyleString"
        }

        // Apply accessibility attributes
        getAccessibilityAttributes().forEach { (key, value) ->
            attributes[key] = value
        }

        // Add hover styles to the stylesheet if present
        hoverClass?.addToStyleSheet()

        // Add the text content
        +text
    }
    return consumer
} 