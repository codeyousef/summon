@file:Suppress("UNCHECKED_CAST")

package com.summon

import kotlinx.html.TagConsumer
import kotlinx.html.span
import kotlinx.html.style

/**
 * JS implementation for the Text component rendering.
 * This is used by the JsPlatformRenderer.
 */
fun <T> Text.renderJs(consumer: TagConsumer<T>): TagConsumer<T> {
    consumer.span {
        style = modifier.toStyleString()
        +text
    }
    return consumer
} 