package codes.yousef.summon.web

/**
 * JVM implementations of error boundary platform functions.
 * Most web-specific operations are no-ops on JVM since it's server-side.
 */

actual fun enableStaticFormFallbacksPlatform() {
    // No-op on JVM - forms are handled server-side
}

actual fun clearWasmCachePlatform(): Boolean {
    // No-op on JVM - no client-side caching
    return true
}

actual fun verifyJSFallbackPlatform(): Boolean {
    // Always true on JVM - server can always render content
    return true
}

actual fun clearModuleCachePlatform(): Boolean {
    // No-op on JVM - no client-side modules
    return true
}

actual fun loadCompatibilityShimsPlatform(): Boolean {
    // No-op on JVM - no client-side polyfills needed
    return true
}

actual fun checkNetworkConnectivityPlatform(): Boolean {
    // Always assume connectivity on server
    return true
}

actual fun retryNetworkOperationPlatform(): Boolean {
    // No-op on JVM - server operations handled differently
    return true
}

actual fun enableOfflineModePlatform(): Boolean {
    // No-op on JVM - servers don't have offline mode
    return true
}

actual fun clearAllCachesPlatform(): Boolean {
    // Could clear server-side caches if needed
    return true
}

actual fun resetToKnownStatePlatform(): Boolean {
    // Could reset server state if needed
    return true
}

actual fun verifyBasicFunctionalityPlatform(): Boolean {
    // Always true on JVM - server functionality is always available
    return true
}

actual fun delayPlatform(ms: Int) {
    // Use Thread.sleep on JVM
    try {
        Thread.sleep(ms.toLong())
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
    }
}