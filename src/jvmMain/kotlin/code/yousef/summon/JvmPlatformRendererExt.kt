package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.JvmPlatformRenderer
import kotlinx.html.TagConsumer
import kotlinx.html.html
import kotlinx.html.stream.createHTML

/**
 * Extension functions for JvmPlatformRenderer.
 */

/**
 * Renders a Composable to an HTML string.
 *
 * @param content The composable content to render
 * @return HTML string representation of the component
 */
fun JvmPlatformRenderer.render(content: @Composable () -> Unit): String {
    return renderComposableRoot(content)
}

/**
 * Renders a Composable to a tag consumer.
 * 
 * @param content The composable content to render
 * @param consumer The tag consumer to render to
 * @return The tag consumer for method chaining
 */
fun <T> JvmPlatformRenderer.renderComposable(content: @Composable () -> Unit, consumer: TagConsumer<T>): TagConsumer<T> {
    consumer.html {
        renderComposable(content)
    }
    return consumer
} 
