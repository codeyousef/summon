package codes.yousef.summon.runtime

import kotlinx.browser.window

/**
 * WASM implementation of the RecompositionScheduler.
 * Uses external functions to call JavaScript's requestAnimationFrame for optimal rendering performance.
 */
class WasmRecompositionScheduler : RecompositionScheduler {
    private var animationFrameId: Int? = null

    override fun cancelPendingRecomposition() {
        animationFrameId?.let { kotlinx.browser.window.cancelAnimationFrame(it) }
        animationFrameId = null
    }

    override fun scheduleRecomposition(work: () -> Unit) {
        animationFrameId?.let { window.cancelAnimationFrame(it) }
        // Pass the Kotlin callback directly through the supported DOM binding. The
        // former no-argument helper scheduled an empty callback and never dispatched
        // the separate registry, so no pending composition could execute.
        animationFrameId = window.requestAnimationFrame {
            // Clear before invoking work so a newly scheduled frame remains owned.
            animationFrameId = null
            work()
        }
    }
}

actual fun createDefaultScheduler(): RecompositionScheduler {
    safeWasmConsoleLog("Creating WASM recomposition scheduler with requestAnimationFrame")
    return WasmRecompositionScheduler()
}
