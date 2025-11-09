package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.*
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
    globalRootContainer = container
    container.innerHTML = ""

    try {
        setPlatformRenderer(renderer)
        val recomposer = RecomposerHolder.current()
        val composer = recomposer.createComposer()

        fun runComposition() {
            CompositionLocal.provideComposer(composer) {
                LocalPlatformRenderer.provides(renderer)
                renderer.startRecomposition()
                renderer.renderInto(container) {
                    composable()
                }
                renderer.endRecomposition()
            }
        }

        recomposer.setCompositionRoot {
            runComposition()
        }

        runComposition()
    } catch (e: Exception) {
        js("console.error('Error rendering composable to container: ', e);")
        js("console.error('Stack trace: ', e.stack);")
    }
}
