package codes.yousef.summon.runtime

/**
 * Simple memory usage tracking for WASM.

 * @property totalElements The total elements value.
 * @property totalEventHandlers The total event handlers value.
 * @property cacheSize The cache size value.
 * @property estimatedMemoryBytes The estimated memory bytes value.
 * @property timestamp The timestamp value.
 */
data class WasmMemoryUsage(
    val totalElements: Int,
    val totalEventHandlers: Int,
    val cacheSize: Int,
    val estimatedMemoryBytes: Long,
    val timestamp: Long
)