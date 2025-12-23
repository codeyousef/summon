package codes.yousef.summon.runtime

/**
 * Performance metrics configuration.
 *
 * Metrics are disabled by default and require explicit opt-in via
 * `window.__SUMMON_PERF_ENABLED__ = true` before page load.
 */
object PerformanceConfig {
    /**
     * Whether performance metrics collection is enabled.
     * Set automatically based on `window.__SUMMON_PERF_ENABLED__`.
     */
    var enabled: Boolean = false

    /**
     * Whether to expose metrics to window object for external tools.
     */
    var exposeToWindow: Boolean = false

    /**
     * Maximum number of metric entries to keep in history.
     */
    var maxHistorySize: Int = 1000
}

/**
 * Phases of the hydration lifecycle for metric categorization.
 */
enum class HydrationPhase(val displayName: String) {
    SCRIPT_LOAD("Script Load"),
    DOM_READY("DOM Ready"),
    INITIALIZATION("Initialization"),
    EVENT_SYSTEM("Event System"),
    COMPONENT_HYDRATION("Component Hydration"),
    EVENT_REPLAY("Event Replay"),
    COMPLETE("Complete")
}

/**
 * A single metric entry capturing timing information.
 */
data class MetricEntry(
    val name: String,
    val startTime: Double,
    val duration: Double,
    val phase: HydrationPhase,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Summary of metrics for a specific operation type.
 */
data class MetricSummary(
    val name: String,
    val count: Int,
    val totalTime: Double,
    val averageTime: Double,
    val minTime: Double,
    val maxTime: Double
)

/**
 * Complete hydration performance report.
 */
data class HydrationPerformanceReport(
    val totalHydrationTime: Double,
    val phases: Map<HydrationPhase, Double>,
    val metrics: List<MetricEntry>,
    val summaries: List<MetricSummary>,
    val timestamp: Double
)

/**
 * Cross-platform performance metrics API for hydration lifecycle measurement.
 *
 * This API is designed for explicit opt-in only - zero overhead when disabled.
 * Enable by setting `window.__SUMMON_PERF_ENABLED__ = true` before page load.
 *
 * ## Usage
 *
 * ```kotlin
 * // Check and initialize at startup
 * PerformanceMetrics.checkAndInitialize()
 *
 * // Measure an operation
 * PerformanceMetrics.measure("hydration-start", HydrationPhase.INITIALIZATION) {
 *     // ... work to measure
 * }
 *
 * // Or use manual marks
 * PerformanceMetrics.markStart("component-hydrate", HydrationPhase.COMPONENT_HYDRATION)
 * // ... work
 * PerformanceMetrics.markEnd("component-hydrate")
 *
 * // Get report
 * val report = PerformanceMetrics.getReport()
 * ```
 */
expect object PerformanceMetrics {
    /**
     * Check for opt-in flag and initialize if enabled.
     * Call this early in the hydration process.
     */
    fun checkAndInitialize()

    /**
     * Mark the start of a timed operation.
     * No-op if metrics are disabled.
     */
    fun markStart(name: String, phase: HydrationPhase = HydrationPhase.INITIALIZATION)

    /**
     * Mark the end of a timed operation and return the duration.
     * No-op if metrics are disabled (returns 0.0).
     */
    fun markEnd(name: String): Double

    /**
     * Measure the execution time of a block.
     * If metrics are disabled, simply executes the block with no overhead.
     */
    fun <T> measure(name: String, phase: HydrationPhase = HydrationPhase.INITIALIZATION, block: () -> T): T

    /**
     * Record a custom metric value.
     */
    fun recordMetric(name: String, value: Double, unit: String = "ms")

    /**
     * Get all recorded metrics.
     */
    fun getMetrics(): List<MetricEntry>

    /**
     * Get metrics filtered by phase.
     */
    fun getMetricsByPhase(phase: HydrationPhase): List<MetricEntry>

    /**
     * Generate a comprehensive performance report.
     */
    fun getReport(): HydrationPerformanceReport

    /**
     * Signal that hydration is complete.
     * This finalizes the report and dispatches completion event.
     */
    fun markHydrationComplete()

    /**
     * Reset all metrics.
     */
    fun reset()

    /**
     * Check if metrics collection is enabled.
     */
    fun isEnabled(): Boolean
}

/**
 * Helper for conditional instrumentation.
 */
fun <T> withPerfMetrics(
    name: String,
    phase: HydrationPhase = HydrationPhase.INITIALIZATION,
    block: () -> T
): T {
    return if (PerformanceConfig.enabled) {
        PerformanceMetrics.measure(name, phase, block)
    } else {
        block()
    }
}

/**
 * Helper for marking operation start.
 */
fun perfMarkStart(name: String, phase: HydrationPhase = HydrationPhase.INITIALIZATION) {
    if (PerformanceConfig.enabled) {
        PerformanceMetrics.markStart(name, phase)
    }
}

/**
 * Helper for marking operation end.
 */
fun perfMarkEnd(name: String): Double {
    return if (PerformanceConfig.enabled) {
        PerformanceMetrics.markEnd(name)
    } else {
        0.0
    }
}
