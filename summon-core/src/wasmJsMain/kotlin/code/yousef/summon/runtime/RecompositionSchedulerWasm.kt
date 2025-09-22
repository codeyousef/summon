package code.yousef.summon.runtime

/**
 * WASM implementation of the RecompositionScheduler.
 * Uses external functions to call JavaScript's requestAnimationFrame for optimal rendering performance.
 */
class WasmRecompositionScheduler : RecompositionScheduler {
    private var scheduledCallback: (() -> Unit)? = null
    private var animationFrameId: Int? = null

    override fun scheduleRecomposition(work: () -> Unit) {
        wasmConsoleLog("WasmRecompositionScheduler.scheduleRecomposition called")

        // Cancel any previously scheduled work
        animationFrameId?.let {
            wasmCancelAnimationFrame(it)
            wasmConsoleLog("Cancelled previous animation frame: $it")
        }

        // Schedule new work
        scheduledCallback = work
        animationFrameId = wasmRequestAnimationFrame()

        wasmConsoleLog("Scheduled recomposition with animation frame ID: $animationFrameId")

        // Register callback for when the animation frame executes
        registerWasmAnimationFrameCallback(animationFrameId!!) {
            wasmConsoleLog("Animation frame callback executed for ID: $animationFrameId")
            try {
                scheduledCallback?.invoke()
                wasmConsoleLog("Recomposition work completed")
            } catch (e: Exception) {
                wasmConsoleError("Recomposition work failed: ${e.message}")
            } finally {
                scheduledCallback = null
                animationFrameId = null
            }
        }
    }
}

actual fun createDefaultScheduler(): RecompositionScheduler {
    wasmConsoleLog("Creating WASM recomposition scheduler with requestAnimationFrame")
    return WasmRecompositionScheduler()
}

// External function for registering animation frame callbacks
external fun registerWasmAnimationFrameCallback(frameId: Int, callback: () -> Unit)