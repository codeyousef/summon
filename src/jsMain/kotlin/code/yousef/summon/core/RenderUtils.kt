package code.yousef.summon.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.JsPlatformRenderer
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * JavaScript-specific implementation of RenderUtils methods.
 * This file provides the actual implementations for the platform-specific methods
 * declared in the common code.
 */

// Initialize early
private val initializeRenderUtils = run {
    js("""
    // Make sure parent tracking is set up
    if (typeof window.currentParent === 'undefined') {
        window.currentParent = document.body;
        window._parentStack = [];
    }
    """)
    true
}

/**
 * Renders a composable to a DOM element.
 * 
 * @param container The container element
 * @param composable The composable to render
 * @return A renderer instance
 */
fun RenderUtils.renderComposable(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
    return if (container is HTMLElement) {
        // Clear the container
        container.innerHTML = ""
        
        // Track parent container
        val previousParent = js("window.currentParent")
        js("window.currentParent = container")
        
        try {
            // Call the composable
            composable()
        } finally {
            // Restore parent
            js("window.currentParent = previousParent")
        }
        
        // Return an appropriate renderer
        object : Renderer<Any> {
            override fun render(composable: @Composable () -> Unit): Any {
                renderComposable(container, composable)
                return js("{}")
            }
            
            override fun dispose() {
                // Nothing to do
            }
        }
    } else {
        throw IllegalArgumentException("Container must be an HTMLElement")
    }
}

/**
 * Hydrates a server-rendered DOM tree with a composable.
 * 
 * @param container The container element
 * @param composable The composable to hydrate with
 * @return A renderer instance
 */
fun RenderUtils.hydrate(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
    // For now, hydrate is the same as renderComposable
    return renderComposable(container, composable)
}

/**
 * Renders a composable to a string.
 * 
 * @param composable The composable to render
 * @return The rendered HTML string
 */
fun RenderUtils.renderToString(composable: @Composable () -> Unit): String {
    // Create a JsPlatformRenderer
    val renderer = JsPlatformRenderer()

    // Use renderComposableRoot to render the composable to a string
    return renderer.renderComposableRoot(composable)
}

/**
 * Renders a composable to a file.
 * 
 * @param composable The composable to render
 * @param file The file to write to
 */
fun RenderUtils.renderToFile(composable: @Composable () -> Unit, file: Any) {
    // In JavaScript, we can't directly write to a file
    // So we'll just render to a string and log it
    val html = renderToString(composable)
    console.log("Rendered to string: $html")
    console.log("Would write to file: $file")
}