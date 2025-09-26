package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.*

/**
 * Renders a composable function to the DOM element with the specified ID.
 * This is the main entry point for WASM applications.
 */
fun renderComposableRoot(rootElementId: String, composable: @Composable () -> Unit) {
    wasmConsoleLog("renderComposableRoot called for element: $rootElementId")

    try {
        // Create renderer
        val renderer = PlatformRenderer()
        wasmConsoleLog("Created PlatformRenderer")

        // Initialize the renderer with the root element
        renderer.initialize(rootElementId)
        wasmConsoleLog("Initialized renderer with root: $rootElementId")

        // Clear the loading message
        val rootElement = DOMProvider.document.getElementById(rootElementId)
        if (rootElement != null) {
            wasmSetElementTextContent(DOMProvider.getNativeElementId(rootElement), "")
            wasmConsoleLog("Cleared loading message")
        }

        // Set the root container for rendering
        renderer.setRootContainer(rootElement ?: throw Exception("Root element not found"))

        // Get the global Recomposer instance
        val recomposer = RecomposerHolder.current()
        wasmConsoleLog("Got global Recomposer")

        // Create a composer from the recomposer
        val composer = recomposer.createComposer()
        wasmConsoleLog("Created Composer from Recomposer")

        // Set the composition root so it can be called during recomposition
        recomposer.setCompositionRoot {
            try {
                // Reset element counters to ensure stable IDs across recompositions
                @Suppress("USELESS_IS_CHECK")
                if (renderer is PlatformRenderer) {
                    renderer.resetElementCounters()
                }

                wasmConsoleLog("Starting recomposition")

                // Don't clear the root element - let the renderer handle updates
                val root = DOMProvider.document.getElementById(rootElementId)
                if (root == null) {
                    wasmConsoleError("Root element not found during recomposition")
                    return@setCompositionRoot
                }

                // Execute composition with LocalPlatformRenderer provided
                CompositionLocal.setCurrentComposer(composer)
                CompositionLocal.provideComposer(composer) {
                    val provider = LocalPlatformRenderer.provides(renderer)
                    if (composer is ComposerImpl) {
                        composer.provideLocal(provider, renderer)
                    }

                    composable()
                }

                wasmConsoleLog("Recomposition completed successfully")
            } catch (e: Exception) {
                wasmConsoleError("Error during recomposition: ${e.message}")
                wasmConsoleError("Stack trace: ${e.stackTraceToString()}")
            }
        }

        // Set up the composition context with the renderer provided
        CompositionLocal.setCurrentComposer(composer)
        wasmConsoleLog("Set current composer")

        // Execute initial composition with LocalPlatformRenderer provided
        CompositionLocal.provideComposer(composer) {
            wasmConsoleLog("Starting initial composition")

            // Provide the renderer manually to the composer
            val provider = LocalPlatformRenderer.provides(renderer)
            if (composer is ComposerImpl) {
                composer.provideLocal(provider, renderer)
            }

            // Execute the composable
            composable()

            wasmConsoleLog("Initial composition completed")
        }

    } catch (e: Exception) {
        wasmConsoleLog("ERROR in renderComposableRoot: ${e.message}")
        wasmConsoleLog("Stack trace: ${e.stackTraceToString()}")

        // Try a simpler fallback
        try {
            val renderer = PlatformRenderer()
            renderer.initialize(rootElementId)

            // Just update the text to show error
            val rootElement = DOMProvider.document.getElementById(rootElementId)
            if (rootElement != null) {
                val elementId = DOMProvider.getNativeElementId(rootElement)
                wasmSetElementTextContent(elementId, "WASM Error: ${e.message}")
            }
        } catch (fallbackError: Exception) {
            wasmConsoleLog("Fallback also failed: ${fallbackError.message}")
        }
    }
}

/**
 * Simple Composer implementation for WASM.
 */
private class ComposerImpl : Composer {
    private val locals = mutableMapOf<CompositionLocalProvider<*>, Any?>()
    private val slots = mutableListOf<Any?>()
    private var slotIndex = 0
    private val disposables = mutableListOf<() -> Unit>()
    private val rememberedValues = mutableMapOf<Any, Any?>()

    override val inserting: Boolean = true

    override fun startNode() {
        // No-op for simple implementation
    }

    override fun endNode() {
        // No-op for simple implementation
    }

    override fun startGroup(key: Any?) {
        // No-op for simple implementation
    }

    override fun endGroup() {
        // No-op for simple implementation
    }

    override fun changed(value: Any?): Boolean = false

    override fun updateValue(value: Any?) {
        if (slotIndex < slots.size) {
            slots[slotIndex] = value
        } else {
            slots.add(value)
        }
    }

    override fun nextSlot() {
        slotIndex++
    }

    override fun getSlot(): Any? {
        return if (slotIndex < slots.size) slots[slotIndex] else null
    }

    override fun setSlot(value: Any?) {
        if (slotIndex < slots.size) {
            slots[slotIndex] = value
        } else {
            slots.add(value)
        }
    }

    override fun recordRead(state: Any) {
        // No-op for simple implementation
    }

    override fun recordWrite(state: Any) {
        // No-op for simple implementation
    }

    override fun reportChanged() {
        // No-op for simple implementation
    }

    override fun registerDisposable(disposable: () -> Unit) {
        disposables.add(disposable)
    }

    override fun recompose() {
        // No-op for simple implementation
    }

    override fun rememberedValue(key: Any): Any? {
        return rememberedValues[key]
    }

    override fun updateRememberedValue(key: Any, value: Any?) {
        rememberedValues[key] = value
    }

    override fun dispose() {
        disposables.forEach { it() }
        disposables.clear()
        slots.clear()
        rememberedValues.clear()
    }

    override fun startCompose() {
        slotIndex = 0
    }

    override fun endCompose() {
        // No-op for simple implementation
    }

    override fun <T> compose(composable: @Composable () -> T): T {
        startCompose()
        return try {
            composable()
        } finally {
            endCompose()
        }
    }

    fun <T> provideLocal(local: CompositionLocalProvider<T>, value: T) {
        locals[local] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> consume(local: CompositionLocalProvider<T>): T {
        return locals[local] as? T ?: throw IllegalStateException("CompositionLocal not provided: $local")
    }
}