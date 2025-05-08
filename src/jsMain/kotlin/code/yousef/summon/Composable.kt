package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.JsPlatformRenderer
import org.w3c.dom.HTMLElement

/**
 * Renders a composable to a DOM element.
 *
 * This is a top-level function that's used by RenderUtils.renderComposable
 * to render a composable to a DOM element.
 *
 * @param renderer The platform renderer to use
 * @param composable The composable to render
 * @param container The DOM element to render into
 */
fun renderComposable(renderer: JsPlatformRenderer, composable: @Composable () -> Unit, container: HTMLElement) {
    // Clear the container first to avoid appending to existing content
    container.innerHTML = ""

    // Set up the container as the current parent for rendering
    // Initialize currentParent if it doesn't exist
    js("""
        // Initialize currentParent if it doesn't exist
        if (typeof currentParent === 'undefined') {
            window.currentParent = document.body;
            console.log('Initialized currentParent to document.body in top-level renderComposable');
        }
        var previousParent = currentParent;
        currentParent = container;
    """)

    try {
        // Directly call the composable function instead of using renderer.renderComposable
        // This avoids the NotImplementedError that might be thrown by the platform-specific implementation
        js("console.log('Using direct composable call in top-level renderComposable to avoid NotImplementedError');")
        composable()
    } catch (e: Exception) {
        // Log any errors that occur during rendering
        js("console.error('Error rendering composable to container: ', e);")
    } finally {
        // Restore the previous parent
        js("currentParent = previousParent;")
    }
}
