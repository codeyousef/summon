package code.yousef.summon.runtime

/**
 * WASM implementation of the RecompositionScheduler.
 * Uses external functions to call JavaScript's requestAnimationFrame for optimal rendering performance.
 */
class WasmRecompositionScheduler : RecompositionScheduler {
    private var animationFrameId: Int? = null

    override fun scheduleRecomposition(work: () -> Unit) {
        wasmConsoleLog("WasmRecompositionScheduler.scheduleRecomposition called")

        // Cancel any previously scheduled work
        animationFrameId?.let {
            wasmCancelAnimationFrame(it)
            AnimationFrameCallbackRegistry.clearCallback(it)
            wasmConsoleLog("Cancelled previous animation frame: $it")
        }

        // Schedule new work
        animationFrameId = wasmRequestAnimationFrame()

        wasmConsoleLog("Scheduled recomposition with animation frame ID: $animationFrameId")

        // Register the callback in our registry instead of passing the function directly
        animationFrameId?.let { frameId ->
            AnimationFrameCallbackRegistry.registerCallback(frameId) {
                wasmConsoleLog("Animation frame callback executed for ID: $frameId")
                try {
                    work.invoke()
                    wasmConsoleLog("Recomposition work completed")
                } catch (e: Exception) {
                    wasmConsoleError("Recomposition work failed: ${e.message}")
                    wasmConsoleError("Stack trace: ${e.stackTraceToString()}")
                } finally {
                    animationFrameId = null
                }
            }
        }
    }
}

actual fun createDefaultScheduler(): RecompositionScheduler {
    wasmConsoleLog("Creating WASM recomposition scheduler with requestAnimationFrame")
    return WasmRecompositionScheduler()
}