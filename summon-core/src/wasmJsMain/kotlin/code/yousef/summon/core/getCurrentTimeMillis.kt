package code.yousef.summon.core

import code.yousef.summon.runtime.wasmPerformanceNow

/**
 * WebAssembly implementation of getCurrentTimeMillis using performance.now().
 */
actual fun getCurrentTimeMillis(): Long {
    return wasmPerformanceNow().toLong()
}