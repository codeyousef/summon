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
        // Create renderer and composer
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

        // Create a composer for this composition
        val composer = ComposerImpl()
        wasmConsoleLog("Created Composer")

        // Set up the composition context with the renderer provided
        CompositionLocal.setCurrentComposer(composer)
        wasmConsoleLog("Set current composer")

        // Execute composition with LocalPlatformRenderer provided
        CompositionLocal.provideComposer(composer) {
            wasmConsoleLog("Starting composition")

            // Provide the renderer manually to the composer
            val provider = LocalPlatformRenderer.provides(renderer)
            composer.provideLocal(provider, renderer)

            // Execute the composable
            composable()

            wasmConsoleLog("Composition completed")
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

    fun <T> consume(local: CompositionLocalProvider<T>): T {
        return locals[local] as? T ?: throw IllegalStateException("CompositionLocal not provided: $local")
    }
}