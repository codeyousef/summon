package codes.yousef.summon.core

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/**
 * JavaScript implementation of RenderUtils
 */
actual object RenderUtils {

    private val renderer = PlatformRenderer()
    private val parentStack = ArrayDeque<HTMLElement>()
    internal var currentParent: HTMLElement = document.body ?: error("document.body is null")

    actual fun renderComposable(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        val htmlContainer = container as? HTMLElement
            ?: throw IllegalArgumentException("Container must be an HTMLElement for JS target")

        // Clear container and manage parent context
        htmlContainer.innerHTML = ""
        parentStack.addLast(currentParent)
        currentParent = htmlContainer

        try {
            composable()
        } finally {
            currentParent = parentStack.removeLastOrNull() ?: (document.body ?: error("document.body is null"))
        }

        return object : Renderer<Any> {
            override fun render(composable: @Composable () -> Unit): Any {
                renderComposable(htmlContainer, composable)
                return js("{}")
            }

            override fun dispose() {
                // No-op for now
            }
        }
    }

    actual fun renderToString(composable: @Composable () -> Unit): String {
        return renderer.renderComposableRoot(composable)
    }

    actual fun hydrate(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        // For now, hydrate falls back to renderComposable
        return renderComposable(container, composable)
    }

    actual fun renderToFile(composable: @Composable () -> Unit, file: Any) {
        console.warn("renderToFile is not supported in JavaScript target")
    }
}