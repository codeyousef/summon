package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.*
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
 * For SSR hydration, this should NOT be called. Instead, use SummonHydrationClient.
 *
 * @param renderer The platform renderer to use
 * @param composable The composable to render
 * @param container The DOM element to render into
 */
fun renderComposable(renderer: PlatformRenderer, composable: @Composable () -> Unit, container: HTMLElement) {
    globalRootContainer = container

    // Check if this is an SSR container with existing content
    val isSSRContainer = container.getAttribute("data-summon-hydration") == "root" ||
            container.getAttribute("data-ssr") == "true"

    if (isSSRContainer) {
        js("console.warn('renderComposable called on SSR container - this may break hydration. Use SummonHydrationClient instead.');")
        // Don't clear the container in SSR mode - it contains server-rendered content
    } else {
        // Clear container only for client-side rendering
        container.innerHTML = ""
    }

    try {
        setPlatformRenderer(renderer)
        val recomposer = RecomposerHolder.current()
        
        // Define the root composable that sets up the environment
        val root: @Composable () -> Unit = {
            LocalPlatformRenderer.provides(renderer)
            renderer.startRecomposition()
            renderer.renderInto(container) {
                composable()
            }
            renderer.endRecomposition()
        }

        recomposer.setCompositionRoot(root)
        
        // Use composeInitial to ensure the initial composition follows the same
        // structure (startGroup/endGroup) as subsequent recompositions.
        recomposer.composeInitial(root)
    } catch (e: Exception) {
        js("console.error('Error rendering composable to container: ', e);")
        js("console.error('Stack trace: ', e.stack);")
    }
}
