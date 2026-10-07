package codes.yousef.summon.runtime

import kotlinx.browser.window

/**
 * WASM implementation of PerformanceMetrics using browser Performance API.
 *
 * Exposes metrics via `window.__SUMMON_PERF__` when enabled.
 */
actual object PerformanceMetrics {
    private val metrics = mutableListOf<MetricEntry>()
    private val activeMarks = mutableMapOf<String, Pair<Double, HydrationPhase>>()
    private var hydrationStartTime: Double = 0.0
    private var hydrationComplete: Boolean = false

    /** Executes the check and initialize operation. */
    actual fun checkAndInitialize() {
        val enabled = perf_checkEnabled()
        PerformanceConfig.enabled = enabled
        PerformanceConfig.exposeToWindow = enabled

        if (enabled) {
            hydrationStartTime = perf_now()
            markStart("script-load", HydrationPhase.SCRIPT_LOAD)
            exposeToWindow()
            perf_log("[Summon Perf] Performance metrics enabled (WASM)")
        }
    }

    /**
     * Executes the mark start operation.
     *
     * @param name Human-readable name.
     * @param phase The phase value.
     */
    actual fun markStart(name: String, phase: HydrationPhase) {
        if (!PerformanceConfig.enabled) return

        val startTime = perf_now()
        activeMarks[name] = Pair(startTime, phase)

        // Also create browser Performance API mark
        perf_mark("summon:$name-start")
    }

    /**
     * Executes the mark end operation.
     *
     * @param name Human-readable name.
     * @return The resulting value.
     */
    actual fun markEnd(name: String): Double {
        if (!PerformanceConfig.enabled) return 0.0

        val markData = activeMarks.remove(name) ?: return 0.0
        val (startTime, phase) = markData
        val endTime = perf_now()
        val duration = endTime - startTime

        // Create browser Performance API measure
        perf_mark("summon:$name-end")
        perf_measure("summon:$name", "summon:$name-start", "summon:$name-end")

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

    /**
     * Measures this value.
     *
     * @param name Human-readable name.
     * @param phase The phase value.
     * @param block Operation to execute.
     * @return The resulting value.
     */
    actual fun <T> measure(name: String, phase: HydrationPhase, block: () -> T): T {
        if (!PerformanceConfig.enabled) return block()

        markStart(name, phase)
        return try {
            block()
        } finally {
            markEnd(name)
        }
    }

    /**
     * Executes the record metric operation.
     *
     * @param name Human-readable name.
     * @param value Value to process.
     * @param unit The unit value.
     */
    actual fun recordMetric(name: String, value: Double, unit: String) {
        if (!PerformanceConfig.enabled) return

        val entry = MetricEntry(
            name = name,
            startTime = perf_now(),
            duration = value,
            phase = HydrationPhase.INITIALIZATION,
            metadata = if (unit.isNotEmpty()) mapOf("unit" to unit) else emptyMap()
        )
        addMetric(entry)
    }

    /**
     * Returns metrics.
     *
     * @return The resulting value.
     */
    actual fun getMetrics(): List<MetricEntry> = metrics.toList()

    /**
     * Returns metrics by phase.
     *
     * @param phase The phase value.
     * @return The resulting value.
     */
    actual fun getMetricsByPhase(phase: HydrationPhase): List<MetricEntry> =
        metrics.filter { it.phase == phase }

    /**
     * Returns report.
     *
     * @return The resulting value.
     */
    actual fun getReport(): HydrationPerformanceReport {
        val currentTime = perf_now()
        val totalTime = if (hydrationComplete) {
            metrics.lastOrNull { it.name == "hydration-complete" }?.let {
                it.startTime - hydrationStartTime
            } ?: (currentTime - hydrationStartTime)
        } else {
            currentTime - hydrationStartTime
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
            timestamp = currentTime
        )
    }

    /** Executes the mark hydration complete operation. */
    actual fun markHydrationComplete() {
        if (!PerformanceConfig.enabled) return

        hydrationComplete = true
        markEnd("script-load")

        val currentTime = perf_now()
        val entry = MetricEntry(
            name = "hydration-complete",
            startTime = currentTime,
            duration = currentTime - hydrationStartTime,
            phase = HydrationPhase.COMPLETE
        )
        addMetric(entry)

        // Update window object with final report
        updateWindowReport()

        // Dispatch completion event
        wasmDispatchHydrationComplete(getReport().totalHydrationTime)

        // Log summary to console
        val report = getReport()
        perf_log("[Summon Perf] Hydration complete in ${formatDouble(report.totalHydrationTime)}ms")
        perf_log("[Summon Perf] Phase breakdown:")
        report.phases.forEach { (phase, time) ->
            perf_log("  - ${phase.displayName}: ${formatDouble(time)}ms")
        }
    }

    /** Resets the operation. */
    actual fun reset() {
        metrics.clear()
        activeMarks.clear()
        hydrationStartTime = 0.0
        hydrationComplete = false
    }

    /**
     * Returns whether enabled.
     *
     * @return The resulting value.
     */
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
        wasmExposePerf()
    }

    private fun updateWindowMetrics() {
        metrics.forEach { entry ->
            wasmAddMetricToWindow(entry.name, entry.startTime, entry.duration, entry.phase.name)
        }
    }

    private fun updateWindowReport() {
        val report = getReport()
        wasmSetHydrationComplete(report.totalHydrationTime, report.timestamp)
        report.phases.forEach { (phase, time) ->
            wasmSetPhaseTime(phase.name, time)
        }
    }

    private fun formatDouble(value: Double): String {
        return ((value * 100).toInt() / 100.0).toString()
    }
}

// External JS interop functions - prefixed with perf_ to avoid conflicts
private fun perf_checkEnabled(): Boolean =
    js("window.__SUMMON_PERF_ENABLED__ === true")

private fun perf_now(): Double =
    js("performance.now()")

private fun perf_mark(name: String) {
    js("try { performance.mark(name); } catch(e) {}")
}

private fun perf_measure(name: String, startMark: String, endMark: String) {
    js("try { performance.measure(name, startMark, endMark); } catch(e) {}")
}

private fun perf_log(message: String) {
    js("console.log(message)")
}

private fun wasmExposePerf() {
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

private fun wasmAddMetricToWindow(name: String, startTime: Double, duration: Double, phase: String) {
    js("""
        if (window.__SUMMON_PERF__ && window.__SUMMON_PERF__.metrics) {
            window.__SUMMON_PERF__.metrics.push({
                name: name,
                startTime: startTime,
                duration: duration,
                phase: phase
            });
        }
    """)
}

private fun wasmSetHydrationComplete(totalTime: Double, timestamp: Double) {
    js("""
        if (window.__SUMMON_PERF__) {
            window.__SUMMON_PERF__._hydrationComplete = true;
            window.__SUMMON_PERF__._report = {
                totalHydrationTime: totalTime,
                phases: {},
                timestamp: timestamp
            };
        }
    """)
}

private fun wasmSetPhaseTime(phaseName: String, time: Double) {
    js("""
        if (window.__SUMMON_PERF__ && window.__SUMMON_PERF__._report) {
            window.__SUMMON_PERF__._report.phases[phaseName] = time;
        }
    """)
}

private fun wasmDispatchHydrationComplete(totalTime: Double) {
    js("""
        try {
            window.dispatchEvent(new CustomEvent('summon:hydration-complete', {
                detail: { totalTime: totalTime }
            }));
        } catch(e) {}
    """)
}
