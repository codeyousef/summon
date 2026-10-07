package codes.yousef.summon.web

import codes.yousef.summon.runtime.*

/**
 * WebAssembly-specific implementations of error boundary utilities.
 * Uses external JavaScript functions to access browser APIs safely.
 */

actual fun enableStaticFormFallbacksPlatform() {
    wasmEnableStaticFormFallbacks()
}

/**
 * Clears WASM cache platform.
 *
 * @return The resulting value.
 */
actual fun clearWasmCachePlatform(): Boolean {
    return wasmClearWasmCache()
}

/**
 * Executes the verify JS fallback platform operation.
 *
 * @return The resulting value.
 */
actual fun verifyJSFallbackPlatform(): Boolean {
    return wasmVerifyJSFallback()
}

/**
 * Clears module cache platform.
 *
 * @return The resulting value.
 */
actual fun clearModuleCachePlatform(): Boolean {
    return wasmClearModuleCache()
}

/**
 * Loads compatibility shims platform.
 *
 * @return The resulting value.
 */
actual fun loadCompatibilityShimsPlatform(): Boolean {
    return wasmLoadCompatibilityShims()
}

/**
 * Executes the check network connectivity platform operation.
 *
 * @return The resulting value.
 */
actual fun checkNetworkConnectivityPlatform(): Boolean {
    return wasmCheckNetworkConnectivity()
}

/**
 * Executes the retry network operation platform operation.
 *
 * @return The resulting value.
 */
actual fun retryNetworkOperationPlatform(): Boolean {
    return wasmRetryNetworkOperation()
}

/**
 * Enables offline mode platform.
 *
 * @return The resulting value.
 */
actual fun enableOfflineModePlatform(): Boolean {
    return wasmEnableOfflineMode()
}

/**
 * Clears all caches platform.
 *
 * @return The resulting value.
 */
actual fun clearAllCachesPlatform(): Boolean {
    return wasmClearAllCaches()
}

/**
 * Resets to known state platform.
 *
 * @return The resulting value.
 */
actual fun resetToKnownStatePlatform(): Boolean {
    return wasmResetToKnownState()
}

/**
 * Executes the verify basic functionality platform operation.
 *
 * @return The resulting value.
 */
actual fun verifyBasicFunctionalityPlatform(): Boolean {
    return wasmVerifyBasicFunctionality()
}

/**
 * Enhanced error boundary for WebAssembly environment.
 */
class WasmEnhancedErrorBoundary : ErrorBoundary {

    private var consecutiveErrors = 0
    private var lastErrorTime = 0L

    /**
     * Executes the handle error operation.
     *
     * @param error The error value.
     * @param context The context value.
     * @return The resulting value.
     */
    override fun handleError(error: Throwable, context: String): ErrorAction {
        val currentTime = wasmGetCurrentTime()

        // Increment consecutive error count if errors are happening rapidly
        if (currentTime - lastErrorTime < 5000) { // Within 5 seconds
            consecutiveErrors++
        } else {
            consecutiveErrors = 1
        }
        lastErrorTime = currentTime

        // Log error via WASM bridge
        wasmLogError("Error in $context: ${error.message}")

        // Apply escalating error handling
        return when {
            consecutiveErrors >= 5 -> {
                wasmLogError("Too many consecutive errors, enabling emergency fallback")
                ErrorAction.Fallback(RenderingStrategy.STATIC_FALLBACK)
            }

            error is RuntimeException && error.message?.contains("OutOfMemory", ignoreCase = true) == true -> {
                wasmLogError("Out of memory error, reducing functionality")
                ErrorAction.Fallback(RenderingStrategy.JS_COMPATIBLE)
            }

            error is RuntimeException && error.message?.contains("security", ignoreCase = true) == true -> {
                wasmLogError("Security error, using safe fallback")
                ErrorAction.Graceful("Security restrictions prevent this action")
            }

            canRecover(error) -> {
                wasmLogWarning("Recoverable error, retrying: ${error.message}")
                ErrorAction.Retry
            }

            else -> {
                wasmLogError("Unrecoverable error, falling back: ${error.message}")
                ErrorAction.Fallback(RenderingStrategy.JS_COMPATIBLE)
            }
        }
    }

    /**
     * Executes the report error operation.
     *
     * @param error The error value.
     * @param context The context value.
     * @param metadata The metadata value.
     */
    override fun reportError(error: Throwable, context: String, metadata: Map<String, Any>) {
        // Build error report as string for WASM bridge
        val errorMessage = error.message ?: "Unknown error"
        val stackTrace = error.stackTraceToString()

        // Create metadata string
        val metadataString = buildString {
            append("context=$context;")
            append("timestamp=${wasmGetCurrentTime()};")
            append("userAgent=${wasmGetUserAgent()};")
            append("url=${wasmGetLocationHref()};")
            append("consecutiveErrors=$consecutiveErrors;")
            metadata.forEach { (key, value) ->
                append("$key=$value;")
            }
        }

        // Report via WASM bridge
        wasmReportError(errorMessage, stackTrace, metadataString)
    }

    /**
     * Returns whether this value can recover.
     *
     * @param error The error value.
     * @return The resulting value.
     */
    override fun canRecover(error: Throwable): Boolean {
        return when (error) {
            is RuntimeException -> when {
                error.message?.contains("OutOfMemory", ignoreCase = true) == true -> false
                error.message?.contains("security", ignoreCase = true) == true -> false
                else -> consecutiveErrors < 3
            }

            else -> consecutiveErrors < 3 // Allow recovery if not too many consecutive errors
        }
    }

    /**
     * Clears error counters.
     */
    fun clearErrorState() {
        consecutiveErrors = 0
        lastErrorTime = 0L
    }

    /**
     * Gets current error state for debugging.
     */
    fun getErrorState(): String {
        return "consecutiveErrors=$consecutiveErrors,lastErrorTime=$lastErrorTime"
    }
}

/**
 * WASM-specific delay implementation using external JavaScript.
 */
actual fun delayPlatform(ms: Int) {
    wasmDelay(ms)
}

/**
 * Async delay for WASM.
 */
fun delay(ms: Int) {
    wasmDelay(ms)
}

/**
 * Global error handler setup for WASM.
 */
fun setupGlobalErrorHandling() {
    wasmSetupGlobalErrorHandling()
}

/**
 * WASM-specific memory management utilities.
 */
object WasmMemoryManager {

    /**
     * Monitors WASM memory usage and triggers cleanup when needed.
     */
    fun monitorMemoryUsage(): MemoryStatus {
        val usedMemory = wasmGetUsedMemory()
        val totalMemory = wasmGetTotalMemory()
        val memoryLimit = wasmGetMemoryLimit()

        val usageRatio = if (totalMemory > 0) {
            usedMemory.toDouble() / totalMemory
        } else {
            0.0
        }

        return when {
            usageRatio > 0.9 -> MemoryStatus.CRITICAL
            usageRatio > 0.7 -> MemoryStatus.HIGH
            usageRatio > 0.5 -> MemoryStatus.MODERATE
            else -> MemoryStatus.NORMAL
        }
    }

    /**
     * Attempts to free memory by running garbage collection.
     */
    fun freeMemory(): Boolean {
        return wasmForceGarbageCollection()
    }

    /**
     * Reduces memory usage by clearing non-essential data.
     */
    fun reduceMemoryUsage(): Boolean {
        return wasmReduceMemoryUsage()
    }
}

/**
 * Memory usage status levels.
 */
enum class MemoryStatus {
    /** The normal memory status option. */
    NORMAL,
    /** The moderate memory status option. */
    MODERATE,
    /** The high memory status option. */
    HIGH,
    /** The critical memory status option. */
    CRITICAL
}

/**
 * WASM-specific performance utilities.
 */
object WasmPerformanceUtils {

    /**
     * Monitors WASM performance and detects degradation.
     */
    fun checkPerformance(): PerformanceStatus {
        val frameRate = wasmGetCurrentFrameRate()
        val memoryStatus = WasmMemoryManager.monitorMemoryUsage()
        val cpuUsage = wasmGetCPUUsage()

        return when {
            frameRate < 15.0 || memoryStatus == MemoryStatus.CRITICAL || cpuUsage > 0.9 -> {
                PerformanceStatus.POOR
            }

            frameRate < 30.0 || memoryStatus == MemoryStatus.HIGH || cpuUsage > 0.7 -> {
                PerformanceStatus.DEGRADED
            }

            frameRate < 50.0 || memoryStatus == MemoryStatus.MODERATE || cpuUsage > 0.5 -> {
                PerformanceStatus.MODERATE
            }

            else -> PerformanceStatus.GOOD
        }
    }

    /**
     * Applies performance optimizations based on current status.
     */
    fun optimizePerformance(status: PerformanceStatus): Boolean {
        return when (status) {
            PerformanceStatus.POOR -> {
                wasmEnableEmergencyOptimizations()
            }

            PerformanceStatus.DEGRADED -> {
                wasmEnablePerformanceOptimizations()
            }

            PerformanceStatus.MODERATE -> {
                wasmEnableMinorOptimizations()
            }

            PerformanceStatus.GOOD -> {
                true // No optimizations needed
            }
        }
    }
}

/**
 * Performance status levels.
 */
enum class PerformanceStatus {
    /** The good performance status option. */
    GOOD,
    /** The moderate performance status option. */
    MODERATE,
    /** The degraded performance status option. */
    DEGRADED,
    /** The poor performance status option. */
    POOR
}

/**
 * Executes the WASM enable performance optimizations operation.
 *
 * @return The resulting value.
 */
external fun wasmEnablePerformanceOptimizations(): Boolean
/**
 * Executes the WASM enable minor optimizations operation.
 *
 * @return The resulting value.
 */
external fun wasmEnableMinorOptimizations(): Boolean