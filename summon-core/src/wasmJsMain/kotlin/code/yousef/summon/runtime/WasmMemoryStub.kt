package code.yousef.summon.runtime

import code.yousef.summon.runtime.wasmConsoleLog
import code.yousef.summon.runtime.wasmConsoleWarn
import code.yousef.summon.runtime.wasmConsoleError

/**
 * Simple memory usage tracking for WASM.
 */
data class WasmMemoryUsage(
    val totalElements: Int,
    val totalEventHandlers: Int,
    val cacheSize: Int,
    val estimatedMemoryBytes: Long,
    val timestamp: Long
)