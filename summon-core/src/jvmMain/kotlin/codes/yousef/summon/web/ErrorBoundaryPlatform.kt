package codes.yousef.summon.web

/**
 * JVM implementations of error boundary platform functions.
 * Most web-specific operations are no-ops on JVM since it's server-side.
 */

actual fun enableStaticFormFallbacksPlatform() {
    // No-op on JVM - forms are handled server-side
}

/**
 * Clears WASM cache platform.
 *
 * @return The resulting value.
 */
actual fun clearWasmCachePlatform(): Boolean {
    // No-op on JVM - no client-side caching
    return true
}

/**
 * Executes the verify JS fallback platform operation.
 *
 * @return The resulting value.
 */
actual fun verifyJSFallbackPlatform(): Boolean {
    // Always true on JVM - server can always render content
    return true
}

/**
 * Clears module cache platform.
 *
 * @return The resulting value.
 */
actual fun clearModuleCachePlatform(): Boolean {
    // No-op on JVM - no client-side modules
    return true
}

/**
 * Loads compatibility shims platform.
 *
 * @return The resulting value.
 */
actual fun loadCompatibilityShimsPlatform(): Boolean {
    // No-op on JVM - no client-side polyfills needed
    return true
}

/**
 * Executes the check network connectivity platform operation.
 *
 * @return The resulting value.
 */
actual fun checkNetworkConnectivityPlatform(): Boolean {
    // Always assume connectivity on server
    return true
}

/**
 * Executes the retry network operation platform operation.
 *
 * @return The resulting value.
 */
actual fun retryNetworkOperationPlatform(): Boolean {
    // No-op on JVM - server operations handled differently
    return true
}

/**
 * Enables offline mode platform.
 *
 * @return The resulting value.
 */
actual fun enableOfflineModePlatform(): Boolean {
    // No-op on JVM - servers don't have offline mode
    return true
}

/**
 * Clears all caches platform.
 *
 * @return The resulting value.
 */
actual fun clearAllCachesPlatform(): Boolean {
    // Could clear server-side caches if needed
    return true
}

/**
 * Resets to known state platform.
 *
 * @return The resulting value.
 */
actual fun resetToKnownStatePlatform(): Boolean {
    // Could reset server state if needed
    return true
}

/**
 * Executes the verify basic functionality platform operation.
 *
 * @return The resulting value.
 */
actual fun verifyBasicFunctionalityPlatform(): Boolean {
    // Always true on JVM - server functionality is always available
    return true
}

/**
 * Executes the delay platform operation.
 *
 * @param ms The ms value.
 */
actual fun delayPlatform(ms: Int) {
    // Use Thread.sleep on JVM
    try {
        Thread.sleep(ms.toLong())
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
    }
}