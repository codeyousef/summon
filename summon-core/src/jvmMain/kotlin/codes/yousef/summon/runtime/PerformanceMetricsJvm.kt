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

    actual fun checkAndInitialize() {
        // No-op on JVM - SSR doesn't have browser Performance API
    }

    actual fun markStart(name: String, phase: HydrationPhase) {
        // No-op on JVM
    }

    actual fun markEnd(name: String): Double {
        // No-op on JVM
        return 0.0
    }

    actual fun <T> measure(name: String, phase: HydrationPhase, block: () -> T): T {
        // Just execute the block without measurement on JVM
        return block()
    }

    actual fun recordMetric(name: String, value: Double, unit: String) {
        // No-op on JVM
    }

    actual fun getMetrics(): List<MetricEntry> {
        // No metrics collected on JVM
        return emptyList()
    }

    actual fun getMetricsByPhase(phase: HydrationPhase): List<MetricEntry> {
        // No metrics collected on JVM
        return emptyList()
    }

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

    actual fun markHydrationComplete() {
        // No-op on JVM
    }

    actual fun reset() {
        // No-op on JVM
    }

    actual fun isEnabled(): Boolean {
        // Always disabled on JVM
        return false
    }
}
