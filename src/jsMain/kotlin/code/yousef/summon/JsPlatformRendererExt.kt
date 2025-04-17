package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.JsPlatformRenderer
import org.w3c.dom.HTMLElement

/**
 * Extension functions for JsPlatformRenderer.
 */

/**
 * Renders a Composable to a DOM element.
 *
 * @param content The composable content to render
 * @param container The DOM element to render into
 */
fun JsPlatformRenderer.renderComposable(content: @Composable () -> Unit, container: HTMLElement) {
    // Clear the container first to avoid appending to existing content
    container.innerHTML = ""

    // Set up the container as the current parent for rendering
    js("var previousParent = currentParent;")
    js("currentParent = container;")

    try {
        // Render the composable content into the container
        renderComposable(content)
    } catch (e: Exception) {
        // Log any errors that occur during rendering
        js("console.error('Error rendering composable to container: ', e);")
    } finally {
        // Restore the previous parent
        js("currentParent = previousParent;")
    }
} 
