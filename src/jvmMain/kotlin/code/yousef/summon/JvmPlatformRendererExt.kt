package code.yousef.summon

import code.yousef.summon.core.Composable
import kotlinx.html.stream.createHTML

/**
 * Extension functions for JvmPlatformRenderer.
 */

/**
 * Renders a Composable to an HTML string.
 *
 * @param component The component to render
 * @return HTML string representation of the component
 */
fun JvmPlatformRenderer.render(component: Composable): String {
    return createHTML().let { consumer ->
        component.compose(consumer)
        consumer.finalize()
    }
} 