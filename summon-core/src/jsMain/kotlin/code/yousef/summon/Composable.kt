package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.RecomposerHolder
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import org.w3c.dom.HTMLElement

/**
 * Global reference to the root container for recomposition management
 */
private var globalRootContainer: HTMLElement? = null

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
fun renderComposable(renderer: PlatformRenderer, composable: @Composable () -> Unit, container: HTMLElement) {
    // Store the container globally for recomposition
    globalRootContainer = container
    
    // Clear the container first to avoid appending to existing content
    container.innerHTML = ""

    try {
        // Set the renderer globally so composables can access it
        setPlatformRenderer(renderer)
        
        // Provide the renderer to the LocalPlatformRenderer CompositionLocal
        LocalPlatformRenderer.provides(renderer)
        
        // Get the recomposer and create a composer for this composition
        val recomposer = RecomposerHolder.current()
        val composer = recomposer.createComposer()
        
        // Set up the recomposer with a wrapped composable that performs smart recomposition
        recomposer.setCompositionRoot {
            // Mark the start of a recomposition cycle for the renderer
            renderer.startRecomposition()
            
            // Render the composable into the container with composer context
            renderer.renderInto(container) {
                // Set the composer as active during composition
                recomposer.setActiveComposer(composer)
                try {
                    composable()
                } finally {
                    // Clear the active composer after composition
                    recomposer.setActiveComposer(null)
                }
            }
            
            // Clean up any elements that weren't reused in this composition
            renderer.endRecomposition()
        }
        
        // Perform the initial render with composer context
        // Mark this as a recomposition so elements get cached
        renderer.startRecomposition()
        renderer.renderInto(container) {
            // Set the composer as active during initial composition
            recomposer.setActiveComposer(composer)
            try {
                composable()
            } finally {
                // Clear the active composer after composition
                recomposer.setActiveComposer(null)
            }
        }
        // Don't call endRecomposition here - we want to keep all elements
        
    } catch (e: Exception) {
        // Log any errors that occur during rendering
        js("console.error('Error rendering composable to container: ', e);")
        js("console.error('Stack trace: ', e.stack);")
    }
}
