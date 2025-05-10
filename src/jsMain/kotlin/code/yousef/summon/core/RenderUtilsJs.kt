package code.yousef.summon.core

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import kotlin.collections.ArrayDeque // More idiomatic Kotlin for a stack

/**
 * JavaScript implementation of RenderUtils
 */
actual object RenderUtils {

    private val renderer = PlatformRenderer()

    // --- Internal State Management ---
    // currentParent will now be managed by Kotlin.
    // It needs to be dynamic because it changes during rendering.
    // We can use a thread-local or a similar mechanism if we were in a multi-threaded JVM env,
    // but for JS (single-threaded event loop), a simple var in the object can work,
    // though care must be taken if there are async operations during rendering.
    // For simplicity here, let's make it internal to RenderUtils.
    // The PlatformRenderer will likely be the one needing access to the current parent.

    // Option 1: Pass currentParent explicitly (cleaner)
    // Option 2: Make PlatformRenderer aware of a stack (more encapsulated)

    // Let's assume PlatformRenderer can handle the parent context.
    // If not, we might need a small internal stack here.

    // For the parent stack previously in JS:
    private val parentStack = ArrayDeque<HTMLElement>()
    // currentParent needs to be accessible by the composable functions when they create elements.
    // kotlinx.html builders usually take a consumer or append to a specific parent.
    // We need to see how `composable()` actually appends to the DOM.
    // For now, let's assume PlatformRenderer handles this.
    // If PlatformRenderer needs it, it should be passed or be part of its state.

    // Let's reconsider. The `composable()` lambda is opaque here. It's likely that
    // other parts of Summon (like element builders Box, Text, etc.) implicitly
    // use the "current parent". So, RenderUtils *does* need to manage this context.

    internal var currentParent: HTMLElement = document.body ?: error("document.body is null")

    init {
        // The large js block in the original init is mostly trying to set up
        // globals and work around potential conflicts. We want to remove that.
        // If `window.renderComposable_udbimr_k$` was a specific function Kotlin expected
        // to be globally available for *itself* to call, that's a deeper issue with
        // how Kotlin/JS interop or export was being handled.
        // Ideally, Kotlin functions are called directly from Kotlin or exported cleanly.
        println("Summon RenderUtils initialized (Kotlin version)")
    }

    actual fun renderComposable(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        val htmlContainer = container as? HTMLElement
            ?: throw IllegalArgumentException("Container must be an HTMLElement for JS target")

        // Clear container
        htmlContainer.innerHTML = "" // This is okay for a top-level render.

        // Manage parent context using Kotlin properties
        parentStack.addLast(currentParent)
        currentParent = htmlContainer

        try {
            // Call the composable. Assumes composable functions somehow access RenderUtils.currentParent
            // or that element creation functions within the composable lambda do.
            composable()
        } finally {
            // Restore parent
            currentParent = parentStack.removeLastOrNull() ?: (document.body ?: error("document.body is null after stack pop"))
        }

        // Return a renderer
        return object : Renderer<Any> {
            override fun render(composable: @Composable () -> Unit): Any {
                // Re-rendering within the same container.
                // This might need more sophisticated diffing in the future,
                // but for now, it matches the original behavior.
                renderComposable(htmlContainer, composable)
                return js("{}") // Original behavior, returns an empty JS object
            }

            override fun dispose() {
                // Clear container content on dispose? Or leave it to the user?
                // For now, no-op like original.
                // htmlContainer.innerHTML = "" // Optional: clear on dispose
            }
        }
    }

    actual fun renderToString(composable: @Composable () -> Unit): String {
        // This looks fine, assuming PlatformRenderer is designed for string rendering.
        // It would internally build up a string representation.
        return renderer.renderComposableRoot(composable)
    }

    actual fun hydrate(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        // For now, hydrate is the same as renderComposable
        // True hydration is more complex and involves reconciling virtual DOM with existing DOM.
        println("Summon: Hydration called, currently falling back to full render.")
        return renderComposable(container, composable)
    }

    actual fun renderToFile(composable: @Composable () -> Unit, file: Any) {
        console.warn("Summon: renderToFile is not supported in JavaScript target.")
    }
}