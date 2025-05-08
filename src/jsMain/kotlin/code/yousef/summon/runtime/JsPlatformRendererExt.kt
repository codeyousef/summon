package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import kotlinx.browser.document
import org.w3c.dom.HTMLElement


/**
 * Extension function for JsPlatformRenderer that implements the renderComposable method with a container.
 * This is a convenience method that's used by RenderUtils.
 */
fun JsPlatformRenderer.renderComposable(content: @Composable () -> Unit, container: HTMLElement) {
    // Log that we're using this implementation
    js("console.log('Using JsPlatformRenderer.renderComposable extension implementation');")

    // Clear the container first
    container.innerHTML = ""

    // Store the previous parent
    val previousParent = js("currentParent")

    // Set the container as the current parent
    js("currentParent = container")

    try {
        // Call the composable function directly
        content()
    } catch (e: Exception) {
        // Log any errors
        js("console.error('Error executing composable in renderComposable:', e);")
    } finally {
        // Restore the previous parent
        js("currentParent = previousParent")
    }
}
