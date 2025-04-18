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

// The renderComposable extension function has been removed because it was causing issues.
// It was calling renderComposable without first setting up the context via renderComposableRoot,
// which was causing the error "Rendering function called outside of renderComposableRoot scope".
// Use the render extension function instead, which uses renderComposableRoot correctly.
