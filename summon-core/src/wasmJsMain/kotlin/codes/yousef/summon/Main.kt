package codes.yousef.summon

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
import codes.yousef.summon.runtime.wasmConsoleLog
import codes.yousef.summon.runtime.wasmGetDocumentBodyId

/**
 * Global reference to the PlatformRenderer for WASM.
 */
private var globalRenderer: PlatformRenderer? = null

// The renderComposableRoot function is now in WasmApi.kt with proper CompositionLocal setup

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