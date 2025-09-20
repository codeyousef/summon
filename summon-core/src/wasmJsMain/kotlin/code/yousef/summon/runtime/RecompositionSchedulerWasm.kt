package code.yousef.summon.runtime

import code.yousef.summon.runtime.wasmConsoleLog

actual fun createDefaultScheduler(): RecompositionScheduler {
    wasmConsoleLog("Creating default recomposition scheduler - WASM stub")
    return object : RecompositionScheduler {
        override fun scheduleRecomposition(work: () -> Unit) {
            wasmConsoleLog("RecompositionScheduler.scheduleRecomposition - WASM stub")
            work() // Execute immediately for WASM stub
        }
    }
}