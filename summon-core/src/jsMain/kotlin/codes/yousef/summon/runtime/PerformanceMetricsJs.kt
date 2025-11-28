package codes.yousef.summon.runtime

import kotlinx.browser.window

/**
 * JS implementation of PerformanceMetrics using browser Performance API.
 *
 * Exposes metrics via `window.__SUMMON_PERF__` when enabled.
 */
actual object PerformanceMetrics {
    private val metrics = mutableListOf<MetricEntry>()
    private val activeMarks = mutableMapOf<String, Pair<Double, HydrationPhase>>()
    private var hydrationStartTime: Double = 0.0
    private var hydrationComplete: Boolean = false

    actual fun checkAndInitialize() {
        val enabled = js("window.__SUMMON_PERF_ENABLED__ === true") as Boolean
        PerformanceConfig.enabled = enabled
        PerformanceConfig.exposeToWindow = enabled

        if (enabled) {
            hydrationStartTime = performanceNow()
            markStart("script-load", HydrationPhase.SCRIPT_LOAD)
            exposeToWindow()
            consoleLog("[Summon Perf] Performance metrics enabled")
        }
    }

    actual fun markStart(name: String, phase: HydrationPhase) {
        if (!PerformanceConfig.enabled) return

        val startTime = performanceNow()
        activeMarks[name] = Pair(startTime, phase)

        // Also create browser Performance API mark
        try {
            js("performance.mark('summon:' + name + '-start')")
        } catch (e: Exception) {
            // Performance API may not be available
        }
    }

    actual fun markEnd(name: String): Double {
        if (!PerformanceConfig.enabled) return 0.0

        val markData = activeMarks.remove(name) ?: return 0.0
        val (startTime, phase) = markData
        val endTime = performanceNow()
        val duration = endTime - startTime

        // Create browser Performance API measure
        try {
            js("performance.mark('summon:' + name + '-end')")
            js("performance.measure('summon:' + name, 'summon:' + name + '-start', 'summon:' + name + '-end')")
        } catch (e: Exception) {
            // Performance API may not be available
        }

        // Record metric
        val entry = MetricEntry(
            name = name,
            startTime = startTime,
            duration = duration,
            phase = phase
        )
        addMetric(entry)

        return duration
    }

    actual fun <T> measure(name: String, phase: HydrationPhase, block: () -> T): T {
        if (!PerformanceConfig.enabled) return block()

        markStart(name, phase)
        return try {
            block()
        } finally {
            markEnd(name)
        }
    }

    actual fun recordMetric(name: String, value: Double, unit: String) {
        if (!PerformanceConfig.enabled) return

        val entry = MetricEntry(
            name = name,
            startTime = performanceNow(),
            duration = value,
            phase = HydrationPhase.INITIALIZATION,
            metadata = if (unit.isNotEmpty()) mapOf("unit" to unit) else emptyMap()
        )
        addMetric(entry)
    }

    actual fun getMetrics(): List<MetricEntry> = metrics.toList()

    actual fun getMetricsByPhase(phase: HydrationPhase): List<MetricEntry> =
        metrics.filter { it.phase == phase }

    actual fun getReport(): HydrationPerformanceReport {
        val totalTime = if (hydrationComplete) {
            metrics.lastOrNull { it.name == "hydration-complete" }?.let {
                it.startTime - hydrationStartTime
            } ?: (performanceNow() - hydrationStartTime)
        } else {
            performanceNow() - hydrationStartTime
        }

        // Calculate time per phase
        val phaseTimings = metrics
            .groupBy { it.phase }
            .mapValues { (_, entries) -> entries.sumOf { it.duration } }

        // Generate summaries
        val summaries = metrics
            .groupBy { it.name }
            .map { (name, entries) ->
                val durations = entries.map { it.duration }
                MetricSummary(
                    name = name,
                    count = entries.size,
                    totalTime = durations.sum(),
                    averageTime = durations.average(),
                    minTime = durations.minOrNull() ?: 0.0,
                    maxTime = durations.maxOrNull() ?: 0.0
                )
            }
            .sortedByDescending { it.totalTime }

        return HydrationPerformanceReport(
            totalHydrationTime = totalTime,
            phases = phaseTimings,
            metrics = metrics.toList(),
            summaries = summaries,
            timestamp = performanceNow()
        )
    }

    actual fun markHydrationComplete() {
        if (!PerformanceConfig.enabled) return

        hydrationComplete = true
        markEnd("script-load")

        val entry = MetricEntry(
            name = "hydration-complete",
            startTime = performanceNow(),
            duration = performanceNow() - hydrationStartTime,
            phase = HydrationPhase.COMPLETE
        )
        addMetric(entry)

        // Update window object with final report
        updateWindowReport()

        // Dispatch completion event
        try {
            val report = getReport()
            js("""
                window.dispatchEvent(new CustomEvent('summon:hydration-complete', {
                    detail: { totalTime: report.totalHydrationTime }
                }))
            """)
        } catch (e: Exception) {
            // Custom events may not be available
        }

        // Log summary to console
        val report = getReport()
        consoleLog("[Summon Perf] Hydration complete in ${report.totalHydrationTime.toFixed(2)}ms")
        consoleLog("[Summon Perf] Phase breakdown:")
        report.phases.forEach { (phase, time) ->
            consoleLog("  - ${phase.displayName}: ${time.toFixed(2)}ms")
        }
    }

    actual fun reset() {
        metrics.clear()
        activeMarks.clear()
        hydrationStartTime = 0.0
        hydrationComplete = false
    }

    actual fun isEnabled(): Boolean = PerformanceConfig.enabled

    private fun addMetric(entry: MetricEntry) {
        metrics.add(entry)

        // Trim if exceeds max size
        while (metrics.size > PerformanceConfig.maxHistorySize) {
            metrics.removeAt(0)
        }

        // Update window object
        if (PerformanceConfig.exposeToWindow) {
            updateWindowMetrics()
        }
    }

    private fun exposeToWindow() {
        js("""
            window.__SUMMON_PERF__ = {
                metrics: [],
                _hydrationComplete: false,
                _report: null,

                getReport: function() {
                    return window.__SUMMON_PERF__._report;
                },

                isEnabled: function() {
                    return true;
                },

                waitForHydration: function(timeoutMs) {
                    timeoutMs = timeoutMs || 10000;
                    return new Promise(function(resolve, reject) {
                        if (window.__SUMMON_PERF__._hydrationComplete) {
                            resolve(window.__SUMMON_PERF__._report);
                            return;
                        }

                        var startTime = performance.now();
                        var checkInterval = setInterval(function() {
                            if (window.__SUMMON_PERF__._hydrationComplete) {
                                clearInterval(checkInterval);
                                resolve(window.__SUMMON_PERF__._report);
                            } else if (performance.now() - startTime > timeoutMs) {
                                clearInterval(checkInterval);
                                reject(new Error('Hydration timeout after ' + timeoutMs + 'ms'));
                            }
                        }, 50);
                    });
                },

                getMetricsByPhase: function(phase) {
                    return (window.__SUMMON_PERF__.metrics || []).filter(function(m) {
                        return m.phase === phase;
                    });
                },

                getAllEntries: function() {
                    return performance.getEntriesByType('measure').filter(function(e) {
                        return e.name.startsWith('summon:');
                    });
                }
            };
        """)
    }

    private fun updateWindowMetrics() {
        try {
            val metricsArray = metrics.map { entry ->
                js("""({
                    name: entry.name,
                    startTime: entry.startTime,
                    duration: entry.duration,
                    phase: entry.phase.name
                })""")
            }.toTypedArray()

            js("window.__SUMMON_PERF__.metrics = metricsArray")
        } catch (e: Exception) {
            // Ignore errors
        }
    }

    private fun updateWindowReport() {
        try {
            val report = getReport()
            js("""
                window.__SUMMON_PERF__._hydrationComplete = true;
                window.__SUMMON_PERF__._report = {
                    totalHydrationTime: report.totalHydrationTime,
                    phases: {},
                    timestamp: report.timestamp
                };
            """)

            report.phases.forEach { (phase, time) ->
                js("window.__SUMMON_PERF__._report.phases[phase.name] = time")
            }
        } catch (e: Exception) {
            // Ignore errors
        }
    }

    private fun performanceNow(): Double {
        return js("performance.now()") as Double
    }

    private fun consoleLog(message: String) {
        js("console.log(message)")
    }

    private fun Double.toFixed(digits: Int): String {
        return js("this.toFixed(digits)") as String
    }
}
