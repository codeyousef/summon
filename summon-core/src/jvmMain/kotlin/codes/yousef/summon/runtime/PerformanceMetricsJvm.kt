package codes.yousef.summon.runtime

/**
 * JVM implementation of PerformanceMetrics.
 *
 * For server-side rendering, performance metrics are not collected as there
 * is no browser Performance API available. This is a no-op implementation
 * that allows shared code to compile for JVM without errors.
 *
 * SSR performance should be measured using JMH benchmarks in the diagnostics module.
 */
actual object PerformanceMetrics {

    /** Executes the check and initialize operation. */
    actual fun checkAndInitialize() {
        // No-op on JVM - SSR doesn't have browser Performance API
    }

    /**
     * Executes the mark start operation.
     *
     * @param name Human-readable name.
     * @param phase The phase value.
     */
    actual fun markStart(name: String, phase: HydrationPhase) {
        // No-op on JVM
    }

    /**
     * Executes the mark end operation.
     *
     * @param name Human-readable name.
     * @return The resulting value.
     */
    actual fun markEnd(name: String): Double {
        // No-op on JVM
        return 0.0
    }

    /**
     * Measures this value.
     *
     * @param name Human-readable name.
     * @param phase The phase value.
     * @param block Operation to execute.
     * @return The resulting value.
     */
    actual fun <T> measure(name: String, phase: HydrationPhase, block: () -> T): T {
        // Just execute the block without measurement on JVM
        return block()
    }

    /**
     * Executes the record metric operation.
     *
     * @param name Human-readable name.
     * @param value Value to process.
     * @param unit The unit value.
     */
    actual fun recordMetric(name: String, value: Double, unit: String) {
        // No-op on JVM
    }

    /**
     * Returns metrics.
     *
     * @return The resulting value.
     */
    actual fun getMetrics(): List<MetricEntry> {
        // No metrics collected on JVM
        return emptyList()
    }

    /**
     * Returns metrics by phase.
     *
     * @param phase The phase value.
     * @return The resulting value.
     */
    actual fun getMetricsByPhase(phase: HydrationPhase): List<MetricEntry> {
        // No metrics collected on JVM
        return emptyList()
    }

    /**
     * Returns report.
     *
     * @return The resulting value.
     */
    actual fun getReport(): HydrationPerformanceReport {
        // Return empty report on JVM
        return HydrationPerformanceReport(
            totalHydrationTime = 0.0,
            phases = emptyMap(),
            metrics = emptyList(),
            summaries = emptyList(),
            timestamp = System.currentTimeMillis().toDouble()
        )
    }

    /** Executes the mark hydration complete operation. */
    actual fun markHydrationComplete() {
        // No-op on JVM
    }

    /** Resets the operation. */
    actual fun reset() {
        // No-op on JVM
    }

    /**
     * Returns whether enabled.
     *
     * @return The resulting value.
     */
    actual fun isEnabled(): Boolean {
        // Always disabled on JVM
        return false
    }
}
