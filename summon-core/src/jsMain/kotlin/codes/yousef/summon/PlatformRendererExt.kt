package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import org.w3c.dom.HTMLElement

/**
 * Extension functions for PlatformRenderer.
 */

/**
 * Renders a Composable to a DOM element.
 *
 * @param content The composable content to render
 * @param container The DOM element to render into
 */
fun PlatformRenderer.renderComposable(content: @Composable () -> Unit, container: HTMLElement) {
    codes.yousef.summon.renderComposable(this, content, container)
}
