package code.yousef.summon

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.MigratedPlatformRenderer
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
fun MigratedPlatformRenderer.render(content: @Composable () -> Unit): String {
    return createHTML().let { consumer ->
        this.renderComposable(content, consumer)
        consumer.finalize()
    }
} 
