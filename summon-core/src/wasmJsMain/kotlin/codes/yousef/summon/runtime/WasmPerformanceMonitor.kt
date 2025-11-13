package codes.yousef.summon.runtime

import codes.yousef.summon.core.getCurrentTimeMillis

/**
 * WebAssembly-specific performance monitoring system.
 *
 * This implementation provides comprehensive performance tracking optimized for
 * WebAssembly execution context with memory-efficient data structures and
 * minimal overhead measurement.
 *
 * @since 0.3.3.0
 */
class WasmPerformanceMonitor {

    private val operationMetrics = mutableMapOf<String, OperationMetrics>()
    private val timingHistory = mutableListOf<TimingEntry>()
    private val frameMetrics = mutableListOf<FrameMetrics>()

    private var isEnabled = true
    private var maxHistorySize = 1000
    private var frameCount = 0
    private var lastFrameTime = 0L
    private var frameRateSum = 0.0
    private var frameRateCount = 0

    // Current operation tracking
    private var currentOperation: String? = null
    private var operationStartTime = 0L
    private val operationStack = mutableListOf<String>()

    /**
     * Measures the performance of an operation.
     */
    fun <T> measure(operationName: String, block: () -> T): T {
        if (!isEnabled) return block()

        val startTime = getCurrentTimeMillis()
        operationStack.add(operationName)
        currentOperation = operationName

        return try {
            val result = block()
            result
        } catch (e: Exception) {
            recordError(operationName, e)
            throw e
        } finally {
            val endTime = getCurrentTimeMillis()
            val duration = endTime - startTime

            recordOperation(operationName, duration, startTime)
            operationStack.removeLastOrNull()
            currentOperation = operationStack.lastOrNull()
        }
    }

    /**
     * Records a frame performance measurement.
     */
    fun recordFrame() {
        if (!isEnabled) return

        val currentTime = getCurrentTimeMillis()
        if (lastFrameTime > 0) {
            val frameDuration = currentTime - lastFrameTime
            val frameRate = if (frameDuration > 0) 1000.0 / frameDuration else 0.0

            frameRateSum += frameRate
            frameRateCount++

            val frameMetric = FrameMetrics(
                frameNumber = frameCount,
                duration = frameDuration,
                frameRate = frameRate,
                timestamp = currentTime
            )

            frameMetrics.add(frameMetric)

            // Keep only recent frame metrics
            if (frameMetrics.size > maxHistorySize) {
                frameMetrics.removeFirst()
            }
        }

        lastFrameTime = currentTime
        frameCount++
    }

    /**
     * Records the start of an operation for manual timing.
     */
    fun startOperation(operationName: String) {
        if (!isEnabled) return

        operationStartTime = getCurrentTimeMillis()
        currentOperation = operationName
        operationStack.add(operationName)
    }

    /**
     * Records the end of an operation for manual timing.
     */
    fun endOperation(operationName: String) {
        if (!isEnabled || currentOperation != operationName) return

        val endTime = getCurrentTimeMillis()
        val duration = endTime - operationStartTime

        recordOperation(operationName, duration, operationStartTime)
        operationStack.removeLastOrNull()
        currentOperation = operationStack.lastOrNull()
    }

    /**
     * Records a custom metric value.
     */
    fun recordMetric(name: String, value: Double, unit: String = "") {
        if (!isEnabled) return

        val metrics = operationMetrics.getOrPut(name) { OperationMetrics(name) }
        metrics.customMetrics[unit] = value
    }

    /**
     * Records memory usage.
     */
    fun recordMemoryUsage(allocatedBytes: Long, freedBytes: Long = 0) {
        if (!isEnabled) return

        recordMetric("memory.allocated", allocatedBytes.toDouble(), "bytes")
        if (freedBytes > 0) {
            recordMetric("memory.freed", freedBytes.toDouble(), "bytes")
        }
    }

    /**
     * Generates a comprehensive performance report.
     */
    fun generateReport(): WasmPerformanceReport {
        val sortedOperations = operationMetrics.values
            .filter { it.callCount > 0 }
            .sortedByDescending { it.totalTime }

        val slowestOperations = sortedOperations
            .take(10)
            .map { it.name to it.averageTime }

        val averageFrameRate = if (frameRateCount > 0) frameRateSum / frameRateCount else 0.0

        val memoryUsage = calculateMemoryUsage()

        return WasmPerformanceReport(
            totalOperations = operationMetrics.size,
            totalMeasurements = operationMetrics.values.sumOf { it.callCount },
            totalTime = operationMetrics.values.sumOf { it.totalTime },
            averageFrameRate = averageFrameRate,
            slowestOperations = slowestOperations,
            operationBreakdown = sortedOperations.take(20).map { it.toSummary() },
            frameMetrics = getFrameSummary(),
            memoryMetrics = getMemorySummary(),
            errorCount = operationMetrics.values.sumOf { it.errorCount },
            reportTimestamp = getCurrentTimeMillis()
        )
    }

    /**
     * Gets real-time performance statistics.
     */
    fun getCurrentStats(): WasmPerformanceStats {
        val recentFrames = frameMetrics.takeLast(60) // Last 60 frames
        val avgFrameRate = if (recentFrames.isNotEmpty()) {
            recentFrames.map { it.frameRate }.average()
        } else 0.0

        val currentFrameTime = recentFrames.lastOrNull()?.duration ?: 0L

        return WasmPerformanceStats(
            currentFrameRate = avgFrameRate,
            currentFrameTime = currentFrameTime,
            activeOperations = operationStack.toList(),
            memoryUsage = calculateMemoryUsage(),
            operationCount = operationMetrics.size,
            isMonitoring = isEnabled
        )
    }

    /**
     * Enables or disables performance monitoring.
     */
    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    /**
     * Sets the maximum number of historical entries to keep.
     */
    fun setMaxHistorySize(size: Int) {
        maxHistorySize = size

        // Trim existing data if necessary
        if (timingHistory.size > maxHistorySize) {
            timingHistory.subList(0, timingHistory.size - maxHistorySize).clear()
        }

        if (frameMetrics.size > maxHistorySize) {
            frameMetrics.subList(0, frameMetrics.size - maxHistorySize).clear()
        }
    }

    /**
     * Resets all performance data.
     */
    fun reset() {
        operationMetrics.clear()
        timingHistory.clear()
        frameMetrics.clear()
        frameCount = 0
        lastFrameTime = 0L
        frameRateSum = 0.0
        frameRateCount = 0
        operationStack.clear()
        currentOperation = null
    }

    /**
     * Gets performance data for a specific operation.
     */
    fun getOperationMetrics(operationName: String): OperationMetrics? {
        return operationMetrics[operationName]
    }

    /**
     * Gets the top N slowest operations.
     */
    fun getSlowestOperations(count: Int = 10): List<OperationSummary> {
        return operationMetrics.values
            .filter { it.callCount > 0 }
            .sortedByDescending { it.averageTime }
            .take(count)
            .map { it.toSummary() }
    }

    /**
     * Gets recent timing history.
     */
    fun getRecentTimings(count: Int = 100): List<TimingEntry> {
        return timingHistory.takeLast(count)
    }

    /**
     * Exports performance data for external analysis.
     */
    fun exportData(): String {
        val report = generateReport()
        // Simple JSON-like export - in real implementation would use proper serialization
        return buildString {
            appendLine("{")
            appendLine("  \"report\": ${report}")
            appendLine("  \"timingHistory\": [")
            timingHistory.takeLast(100).forEachIndexed { index, entry ->
                appendLine("    ${entry}")
                if (index < timingHistory.size - 1) appendLine(",")
            }
            appendLine("  ]")
            appendLine("}")
        }
    }

    // --- Private Methods ---

    private fun recordOperation(operationName: String, duration: Long, startTime: Long) {
        val metrics = operationMetrics.getOrPut(operationName) { OperationMetrics(operationName) }
        metrics.recordCall(duration)

        val timingEntry = TimingEntry(
            operation = operationName,
            duration = duration,
            timestamp = startTime,
            success = true
        )

        timingHistory.add(timingEntry)

        // Keep only recent timing history
        if (timingHistory.size > maxHistorySize) {
            timingHistory.removeFirst()
        }
    }

    private fun recordError(operationName: String, error: Exception) {
        val metrics = operationMetrics.getOrPut(operationName) { OperationMetrics(operationName) }
        metrics.recordError()

        wasmConsoleWarn("Performance monitoring detected error in $operationName: ${error.message}")
    }

    private fun calculateMemoryUsage(): WasmMemoryUsage {
        val totalElements = operationMetrics.values.sumOf { it.callCount }
        val totalHandlers = operationMetrics.size

        // Rough estimation - in real implementation would use actual memory APIs
        val estimatedBytes = totalElements * 150L + totalHandlers * 50L

        return WasmMemoryUsage(
            totalElements = totalElements,
            totalEventHandlers = totalHandlers,
            cacheSize = operationMetrics.size,
            estimatedMemoryBytes = estimatedBytes,
            timestamp = wasmPerformanceNow().toLong()
        )
    }

    private fun getFrameSummary(): FrameSummary {
        val recentFrames = frameMetrics.takeLast(60)

        return if (recentFrames.isNotEmpty()) {
            val frameRates = recentFrames.map { it.frameRate }
            val frameTimes = recentFrames.map { it.duration }

            FrameSummary(
                averageFrameRate = frameRates.average(),
                minFrameRate = frameRates.minOrNull() ?: 0.0,
                maxFrameRate = frameRates.maxOrNull() ?: 0.0,
                averageFrameTime = frameTimes.average(),
                frameCount = frameMetrics.size,
                droppedFrames = frameRates.count { it < 55.0 } // Below 55 FPS considered dropped
            )
        } else {
            FrameSummary(0.0, 0.0, 0.0, 0.0, 0, 0)
        }
    }

    private fun getMemorySummary(): MemorySummary {
        val allocatedMetric = operationMetrics.values
            .flatMap { it.customMetrics.entries }
            .filter { it.key == "bytes" }
            .sumOf { it.value }

        return MemorySummary(
            totalAllocated = allocatedMetric,
            currentUsage = calculateMemoryUsage().estimatedMemoryBytes.toDouble(),
            peakUsage = allocatedMetric, // Simplified
            gcCollections = 0 // WASM doesn't expose GC stats directly
        )
    }
}

/**
 * Metrics for a specific operation.
 */
class OperationMetrics(val name: String) {
    private var _totalTime = 0L
    private var _callCount = 0
    private var _errorCount = 0
    private var _minTime = Long.MAX_VALUE
    private var _maxTime = 0L

    val customMetrics = mutableMapOf<String, Double>()

    val totalTime: Long get() = _totalTime
    val callCount: Int get() = _callCount
    val errorCount: Int get() = _errorCount
    val minTime: Long get() = if (_minTime == Long.MAX_VALUE) 0L else _minTime
    val maxTime: Long get() = _maxTime
    val averageTime: Long get() = if (_callCount > 0) _totalTime / _callCount else 0L

    fun recordCall(duration: Long) {
        _totalTime += duration
        _callCount++
        _minTime = minOf(_minTime, duration)
        _maxTime = maxOf(_maxTime, duration)
    }

    fun recordError() {
        _errorCount++
    }

    fun toSummary(): OperationSummary {
        return OperationSummary(
            name = name,
            callCount = callCount,
            totalTime = totalTime,
            averageTime = averageTime,
            minTime = minTime,
            maxTime = maxTime,
            errorCount = errorCount
        )
    }
}

/**
 * Performance data structures.
 */
data class TimingEntry(
    val operation: String,
    val duration: Long,
    val timestamp: Long,
    val success: Boolean
)

data class FrameMetrics(
    val frameNumber: Int,
    val duration: Long,
    val frameRate: Double,
    val timestamp: Long
)

data class WasmPerformanceStats(
    val currentFrameRate: Double,
    val currentFrameTime: Long,
    val activeOperations: List<String>,
    val memoryUsage: WasmMemoryUsage,
    val operationCount: Int,
    val isMonitoring: Boolean
)

data class OperationSummary(
    val name: String,
    val callCount: Int,
    val totalTime: Long,
    val averageTime: Long,
    val minTime: Long,
    val maxTime: Long,
    val errorCount: Int
)

data class FrameSummary(
    val averageFrameRate: Double,
    val minFrameRate: Double,
    val maxFrameRate: Double,
    val averageFrameTime: Double,
    val frameCount: Int,
    val droppedFrames: Int
)

data class MemorySummary(
    val totalAllocated: Double,
    val currentUsage: Double,
    val peakUsage: Double,
    val gcCollections: Int
)

data class WasmPerformanceReport(
    val totalOperations: Int,
    val totalMeasurements: Int,
    val totalTime: Long,
    val averageFrameRate: Double,
    val slowestOperations: List<Pair<String, Long>>,
    val operationBreakdown: List<OperationSummary>,
    val frameMetrics: FrameSummary,
    val memoryMetrics: MemorySummary,
    val errorCount: Int,
    val reportTimestamp: Long
)