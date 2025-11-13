package codes.yousef.summon.web

/**
 * Error boundary utilities shared between JS and WASM implementations.
 *
 * Provides consistent error handling, fallback strategies, and recovery
 * mechanisms that work across all web platforms supported by Summon.
 */

/**
 * Global error boundary for Summon applications.
 */
object SummonErrorBoundary {

    internal var errorHandler: ErrorBoundary? = null
    internal var performanceMonitor: PerformanceMonitor? = null
    internal var fallbackStrategy: RenderingStrategy = RenderingStrategy.STATIC_FALLBACK

    /**
     * Initializes the error boundary with platform-specific implementations.
     */
    fun initialize(
        errorHandler: ErrorBoundary,
        performanceMonitor: PerformanceMonitor,
        defaultFallback: RenderingStrategy = RenderingStrategy.JS_COMPATIBLE
    ) {
        this.errorHandler = errorHandler
        this.performanceMonitor = performanceMonitor
        this.fallbackStrategy = defaultFallback
    }

    /**
     * Handles errors with appropriate fallback strategies.
     */
    fun handleError(error: Throwable, context: String): ErrorAction {
        return errorHandler?.handleError(error, context) ?: ErrorAction.Fallback(fallbackStrategy)
    }

    /**
     * Reports errors for monitoring and debugging.
     */
    fun reportError(error: Throwable, context: String, metadata: Map<String, Any> = emptyMap()) {
        errorHandler?.reportError(error, context, metadata)
    }

    /**
     * Executes code with error boundary protection.
     */
    fun <T> withErrorBoundary(
        context: String,
        operation: () -> T
    ): ErrorBoundaryResult<T> {
        return try {
            val result = operation()
            ErrorBoundaryResult.Success(result)
        } catch (error: Throwable) {
            val action = handleError(error, context)
            reportError(error, context)
            ErrorBoundaryResult.Error(error, action)
        }
    }

    /**
     * Executes code with automatic retry on recoverable errors.
     */
    fun <T> withRetry(
        context: String,
        maxRetries: Int = 3,
        operation: () -> T
    ): ErrorBoundaryResult<T> {
        var lastError: Throwable? = null
        var attempts = 0

        while (attempts <= maxRetries) {
            try {
                val result = operation()
                if (attempts > 0) {
                    // Report successful recovery
                    performanceMonitor?.reportMetrics(
                        mapOf(
                            "recovery_successful" to true,
                            "attempts" to attempts,
                            "context" to context
                        )
                    )
                }
                return ErrorBoundaryResult.Success(result)
            } catch (error: Throwable) {
                lastError = error
                attempts++

                val canRecover = errorHandler?.canRecover(error) ?: false
                if (!canRecover || attempts > maxRetries) {
                    break
                }

                // Wait before retry (exponential backoff)
                delay(minOf(1000 * (1 shl (attempts - 1)), 10000))
            }
        }

        // All retries exhausted
        val action = handleError(lastError!!, context)
        reportError(lastError, context, mapOf("retries" to attempts))
        return ErrorBoundaryResult.Error(lastError, action)
    }

    /**
     * Platform-specific delay implementation.
     */
    internal fun delay(ms: Int) {
        // Platform-specific implementation will be provided by expect/actual
        delayPlatform(ms)
    }
}

/**
 * Result type for error boundary operations.
 */
sealed class ErrorBoundaryResult<T> {
    data class Success<T>(val value: T) : ErrorBoundaryResult<T>()
    data class Error<T>(val error: Throwable, val action: ErrorAction) : ErrorBoundaryResult<T>()

    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Error -> null
    }

    fun getOrElse(default: T): T = when (this) {
        is Success -> value
        is Error -> default
    }

    fun onSuccess(action: (T) -> Unit): ErrorBoundaryResult<T> {
        if (this is Success) action(value)
        return this
    }

    fun onError(action: (Throwable, ErrorAction) -> Unit): ErrorBoundaryResult<T> {
        if (this is Error) action(error, this.action)
        return this
    }
}

/**
 * Fallback mechanism for when primary rendering fails.
 */
object FallbackManager {

    /**
     * Applies fallback strategy based on error type and browser capabilities.
     */
    fun applyFallback(
        error: Throwable,
        currentStrategy: RenderingStrategy,
        browserSupport: BrowserSupport
    ): RenderingStrategy {
        return when (error) {
            is RuntimeException -> if (error.message?.contains("OutOfMemory", ignoreCase = true) == true) {
                RenderingStrategy.STATIC_FALLBACK
            } else {
                when (currentStrategy) {
                    RenderingStrategy.WASM_OPTIMIZED -> {
                        if (browserSupport.hasModules) {
                            RenderingStrategy.JS_MODERN
                        } else {
                            RenderingStrategy.JS_COMPATIBLE
                        }
                    }

                    RenderingStrategy.JS_MODERN -> RenderingStrategy.JS_COMPATIBLE
                    RenderingStrategy.JS_COMPATIBLE -> RenderingStrategy.STATIC_FALLBACK
                    RenderingStrategy.STATIC_FALLBACK -> RenderingStrategy.STATIC_FALLBACK
                }
            }

            else -> when (currentStrategy) {
                RenderingStrategy.WASM_OPTIMIZED -> {
                    if (browserSupport.hasModules) {
                        RenderingStrategy.JS_MODERN
                    } else {
                        RenderingStrategy.JS_COMPATIBLE
                    }
                }

                RenderingStrategy.JS_MODERN -> RenderingStrategy.JS_COMPATIBLE
                RenderingStrategy.JS_COMPATIBLE -> RenderingStrategy.STATIC_FALLBACK
                RenderingStrategy.STATIC_FALLBACK -> RenderingStrategy.STATIC_FALLBACK
            }
        }
    }

    /**
     * Enables static form fallbacks when JavaScript fails.
     */
    fun enableStaticFormFallbacks() {
        // This will be implemented platform-specifically
        enableStaticFormFallbacksPlatform()
    }

    /**
     * Configures progressive enhancement based on available features.
     */
    fun configureProgressiveEnhancement(support: BrowserSupport): EnhancementConfiguration {
        val plan = ProgressiveEnhancement.enhance(support)
        val polyfills = ProgressiveEnhancement.getRequiredPolyfills(support)
        val optimizations = CompatibilityUtils.getBrowserOptimizations(support.browserName, support.browserVersion)
        val workarounds = CompatibilityUtils.applyWorkarounds(support)

        return EnhancementConfiguration(
            plan = plan,
            polyfills = polyfills,
            optimizations = optimizations,
            workarounds = workarounds,
            fallbackStrategy = when (plan) {
                EnhancementPlan.WASM_FULL -> RenderingStrategy.WASM_OPTIMIZED
                EnhancementPlan.WASM_BASIC -> RenderingStrategy.WASM_OPTIMIZED
                EnhancementPlan.JS_MODERN -> RenderingStrategy.JS_MODERN
                EnhancementPlan.JS_COMPATIBLE -> RenderingStrategy.JS_COMPATIBLE
                EnhancementPlan.STATIC_ONLY -> RenderingStrategy.STATIC_FALLBACK
            }
        )
    }
}

/**
 * Configuration for progressive enhancement.
 */
data class EnhancementConfiguration(
    val plan: EnhancementPlan,
    val polyfills: List<String>,
    val optimizations: List<String>,
    val workarounds: List<String>,
    val fallbackStrategy: RenderingStrategy
)

/**
 * Recovery utilities for different error scenarios.
 */
object RecoveryManager {

    /**
     * Attempts to recover from WASM loading failures.
     */
    fun recoverFromWasmFailure(): Boolean {
        return try {
            // Clear any cached WASM modules
            clearWasmCachePlatform()
            // Verify JS fallback is available
            verifyJSFallbackPlatform()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Attempts to recover from JavaScript module loading failures.
     */
    fun recoverFromModuleFailure(): Boolean {
        return try {
            // Clear module cache
            clearModuleCachePlatform()
            // Load compatibility shims
            loadCompatibilityShimsPlatform()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Attempts to recover from network-related failures.
     */
    fun recoverFromNetworkFailure(): Boolean {
        return try {
            // Check network connectivity
            val isOnline = checkNetworkConnectivityPlatform()
            if (isOnline) {
                // Retry with exponential backoff
                retryNetworkOperationPlatform()
            } else {
                // Enable offline mode
                enableOfflineModePlatform()
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * General recovery attempt for unknown errors.
     */
    fun attemptGeneralRecovery(): Boolean {
        return try {
            // Clear all caches
            clearAllCachesPlatform()
            // Reset to known good state
            resetToKnownStatePlatform()
            // Verify basic functionality
            verifyBasicFunctionalityPlatform()
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Performance degradation detection and handling.
 */
object PerformanceDegradationHandler {

    private const val CRITICAL_MEMORY_THRESHOLD = 0.9 // 90% memory usage
    private const val LOW_FPS_THRESHOLD = 30.0 // Below 30 FPS
    private const val HIGH_LATENCY_THRESHOLD = 100.0 // Above 100ms

    /**
     * Monitors performance and applies degradation strategies.
     */
    fun monitorAndDegrade(metrics: RenderingMetrics, memoryInfo: MemoryInfo): DegradationAction {
        // Check memory pressure
        val memoryUsageRatio = if (memoryInfo.totalJSHeapSize > 0) {
            memoryInfo.usedJSHeapSize.toDouble() / memoryInfo.totalJSHeapSize
        } else {
            0.0
        }

        if (memoryUsageRatio > CRITICAL_MEMORY_THRESHOLD) {
            return DegradationAction.ReduceMemoryUsage
        }

        // Check frame rate
        if (metrics.frameRate < LOW_FPS_THRESHOLD) {
            return DegradationAction.ReduceVisualComplexity
        }

        // Check render time
        if (metrics.renderTime > HIGH_LATENCY_THRESHOLD) {
            return DegradationAction.OptimizeRendering
        }

        return DegradationAction.None
    }

    /**
     * Applies degradation strategies based on performance metrics.
     */
    fun applyDegradation(action: DegradationAction, currentStrategy: RenderingStrategy): RenderingStrategy {
        return when (action) {
            DegradationAction.ReduceMemoryUsage -> {
                // Fallback to less memory-intensive strategy
                when (currentStrategy) {
                    RenderingStrategy.WASM_OPTIMIZED -> RenderingStrategy.JS_MODERN
                    RenderingStrategy.JS_MODERN -> RenderingStrategy.JS_COMPATIBLE
                    else -> currentStrategy
                }
            }

            DegradationAction.ReduceVisualComplexity -> {
                // Disable complex visual features
                RenderingStrategy.JS_COMPATIBLE
            }

            DegradationAction.OptimizeRendering -> {
                // Use optimized rendering path
                currentStrategy // Keep current but with optimizations
            }

            DegradationAction.None -> currentStrategy
        }
    }
}

/**
 * Actions to take when performance degrades.
 */
enum class DegradationAction {
    None,
    ReduceMemoryUsage,
    ReduceVisualComplexity,
    OptimizeRendering
}

