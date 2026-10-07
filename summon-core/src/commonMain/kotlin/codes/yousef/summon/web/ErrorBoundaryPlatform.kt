package codes.yousef.summon.web

/**
 * Platform-specific error boundary function declarations.
 * These functions are implemented differently on each platform.
 */

// Platform-specific function declarations
/** Enables static form fallbacks platform. */
expect fun enableStaticFormFallbacksPlatform()
/**
 * Clears WASM cache platform.
 *
 * @return The resulting value.
 */
expect fun clearWasmCachePlatform(): Boolean
/**
 * Executes the verify JS fallback platform operation.
 *
 * @return The resulting value.
 */
expect fun verifyJSFallbackPlatform(): Boolean
/**
 * Clears module cache platform.
 *
 * @return The resulting value.
 */
expect fun clearModuleCachePlatform(): Boolean
/**
 * Loads compatibility shims platform.
 *
 * @return The resulting value.
 */
expect fun loadCompatibilityShimsPlatform(): Boolean
/**
 * Executes the check network connectivity platform operation.
 *
 * @return The resulting value.
 */
expect fun checkNetworkConnectivityPlatform(): Boolean
/**
 * Executes the retry network operation platform operation.
 *
 * @return The resulting value.
 */
expect fun retryNetworkOperationPlatform(): Boolean
/**
 * Enables offline mode platform.
 *
 * @return The resulting value.
 */
expect fun enableOfflineModePlatform(): Boolean
/**
 * Clears all caches platform.
 *
 * @return The resulting value.
 */
expect fun clearAllCachesPlatform(): Boolean
/**
 * Resets to known state platform.
 *
 * @return The resulting value.
 */
expect fun resetToKnownStatePlatform(): Boolean
/**
 * Executes the verify basic functionality platform operation.
 *
 * @return The resulting value.
 */
expect fun verifyBasicFunctionalityPlatform(): Boolean
/**
 * Executes the delay platform operation.
 *
 * @param ms The ms value.
 */
expect fun delayPlatform(ms: Int)