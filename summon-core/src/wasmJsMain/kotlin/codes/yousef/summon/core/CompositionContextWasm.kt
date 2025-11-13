package codes.yousef.summon.core

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.safeWasmConsoleLog
import codes.yousef.summon.runtime.safeWasmConsoleWarn

actual class ThreadLocalHolder<T> actual constructor() {
    private var value: T? = null

    actual fun get(): T? {
        return value
    }

    actual fun set(value: T?) {
        this.value = value
    }
}

actual object RenderUtils {
    actual fun renderComposable(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        try {
            safeWasmConsoleLog("RenderUtils.renderComposable - WASM implementation")

            // Create a WASM-specific renderer that integrates with PlatformRenderer
            return WasmRenderer(container, composable)

        } catch (e: Exception) {
            safeWasmConsoleWarn("Failed to create WASM renderer: ${e.message}")
            return createFallbackRenderer()
        }
    }

    actual fun hydrate(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        try {
            safeWasmConsoleLog("RenderUtils.hydrate - WASM implementation")

            // For WASM, hydration is similar to rendering in test environment
            return WasmRenderer(container, composable, isHydration = true)

        } catch (e: Exception) {
            safeWasmConsoleWarn("Failed to create WASM hydration renderer: ${e.message}")
            return createFallbackRenderer()
        }
    }

    actual fun renderToString(composable: @Composable () -> Unit): String {
        try {
            safeWasmConsoleLog("RenderUtils.renderToString - WASM implementation")

            // Use PlatformRenderer to convert composable to string
            val platformRenderer = PlatformRenderer()
            return platformRenderer.renderComposableRoot(composable)

        } catch (e: Exception) {
            safeWasmConsoleWarn("Failed to render WASM composable to string: ${e.message}")
            return "<div class=\"summon-error\">WASM render error</div>"
        }
    }

    actual fun renderToFile(composable: @Composable () -> Unit, file: Any) {
        try {
            safeWasmConsoleLog("RenderUtils.renderToFile - WASM implementation")

            // In WASM test environment, we simulate file writing
            val content = renderToString(composable)
            safeWasmConsoleLog("Simulated file write for WASM: ${content.length} characters")

        } catch (e: Exception) {
            safeWasmConsoleWarn("Failed to render WASM composable to file: ${e.message}")
        }
    }

    private fun createFallbackRenderer(): Renderer<Any> {
        return object : Renderer<Any> {
            override fun render(composable: @Composable () -> Unit): Any {
                safeWasmConsoleWarn("Using fallback WASM renderer")
                return "wasm-fallback-result"
            }

            override fun dispose() {
                safeWasmConsoleLog("Disposing fallback WASM renderer")
            }
        }
    }
}

/**
 * WASM-specific renderer implementation that integrates with PlatformRenderer
 */
private class WasmRenderer(
    private val container: Any,
    private val initialComposable: @Composable () -> Unit,
    private val isHydration: Boolean = false
) : Renderer<Any> {

    private val platformRenderer = PlatformRenderer()
    private var isDisposed = false

    init {
        safeWasmConsoleLog("WasmRenderer created (hydration: $isHydration)")
    }

    override fun render(composable: @Composable () -> Unit): Any {
        return try {
            if (isDisposed) {
                safeWasmConsoleWarn("Attempting to render with disposed WasmRenderer")
                return "disposed-renderer"
            }

            safeWasmConsoleLog("WasmRenderer.render executing")

            if (isHydration) {
                // Use hydration method - convert container to string ID
                val rootElementId = container.toString()
                platformRenderer.hydrateComposableRoot(rootElementId, composable)
                rootElementId  // Return the element ID
            } else {
                // Use regular rendering method
                val result = platformRenderer.renderComposableRoot(composable)
                safeWasmConsoleLog("WasmRenderer.render completed: ${result.length} characters")
                result
            }

        } catch (e: Exception) {
            safeWasmConsoleWarn("WasmRenderer.render failed: ${e.message}")
            "wasm-render-error"
        }
    }

    override fun dispose() {
        try {
            if (!isDisposed) {
                safeWasmConsoleLog("WasmRenderer.dispose executing")
                isDisposed = true
                // Platform renderer cleanup would go here
            }
        } catch (e: Exception) {
            safeWasmConsoleWarn("WasmRenderer.dispose failed: ${e.message}")
        }
    }
}