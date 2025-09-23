package code.yousef.summon

import code.yousef.summon.annotation.Composable
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.setPlatformRenderer
import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmGetDocumentBodyId

/**
 * Global reference to the PlatformRenderer for WASM.
 */
private var globalRenderer: PlatformRenderer? = null

// The renderComposableRoot function is now in WasmApi.kt with proper CompositionLocal setup

/**
 * Hydrates an existing server-rendered DOM with client-side interactivity.
 * This is used for SSR/hydration scenarios in WASM.
 *
 * @param rootElementId The ID of the DOM element containing server-rendered content
 * @param content The root composable function
 */
fun hydrateComposableRoot(rootElementId: String = "root", content: @Composable () -> Unit) {
    try {
        wasmConsoleLog("Summon WASM: Starting hydration...")

        // Initialize the platform renderer if not already done
        val renderer = globalRenderer ?: PlatformRenderer().also {
            globalRenderer = it
            setPlatformRenderer(it)
        }

        // Initialize the renderer with the root element
        renderer.initialize(rootElementId)

        // Hydrate the existing DOM
        renderer.hydrateComposableRoot(rootElementId, content)

        wasmConsoleLog("Summon WASM: Hydration completed successfully")
    } catch (e: Exception) {
        wasmConsoleLog("Summon WASM: Hydration failed - ${e.message}")
        throw e
    }
}

/**
 * Entry point for WASM applications.
 * This function is automatically called when the WASM module loads.
 *
 * Applications should override this function or use renderComposableRoot directly.
 */
fun main() {
    wasmConsoleLog("Summon WASM: Module loaded")
    wasmConsoleLog("Summon WASM: Call renderComposableRoot() to mount your app")

    // Log that the framework is ready
    try {
        // Check if document body is available
        val bodyId = wasmGetDocumentBodyId()
        if (bodyId != null) {
            wasmConsoleLog("Summon WASM: DOM is ready (body id: $bodyId)")
        } else {
            wasmConsoleLog("Summon WASM: Waiting for DOM...")
        }
    } catch (e: Exception) {
        wasmConsoleLog("Summon WASM: DOM check failed - ${e.message}")
    }
}