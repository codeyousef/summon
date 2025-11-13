package codes.yousef.summon.runtime

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