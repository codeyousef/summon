package com.summon

import kotlinx.html.TagConsumer
import kotlinx.html.span

/**
 * JVM implementation for the Text component rendering.
 * This is used by the JvmPlatformRenderer.
 */
fun <T> Text.renderJvm(consumer: TagConsumer<T>): TagConsumer<T> {
    consumer.span {
        // Apply the modifier styles
        val hoverClass = modifier.applyStyles(this)
        
        // Add hover styles to the stylesheet if present
        hoverClass?.addToStyleSheet()
        
        // Add the text content
        +text
    }
    return consumer
} 