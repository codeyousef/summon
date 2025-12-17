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
    // Clear the container first to avoid appending to existing content
    container.innerHTML = ""

    // Set up the container as the current parent for rendering
    // Use window.currentParent to access the global variable set by Initialize.kt
    js("var previousParent = window.currentParent;")
    js("window.currentParent = container;")

    try {
        // Render the composable content into the container
        renderComposable(content)
    } catch (e: Exception) {
        // Log any errors that occur during rendering
        js("console.error('Error rendering composable to container: ', e);")
    } finally {
        // Restore the previous parent
        js("window.currentParent = previousParent;")
    }
} 
