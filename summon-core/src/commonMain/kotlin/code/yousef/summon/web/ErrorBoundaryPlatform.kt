package code.yousef.summon.web

/**
 * Platform-specific error boundary function declarations.
 * These functions are implemented differently on each platform.
 */

// Platform-specific function declarations
expect fun enableStaticFormFallbacksPlatform()
expect fun clearWasmCachePlatform(): Boolean
expect fun verifyJSFallbackPlatform(): Boolean
expect fun clearModuleCachePlatform(): Boolean
expect fun loadCompatibilityShimsPlatform(): Boolean
expect fun checkNetworkConnectivityPlatform(): Boolean
expect fun retryNetworkOperationPlatform(): Boolean
expect fun enableOfflineModePlatform(): Boolean
expect fun clearAllCachesPlatform(): Boolean
expect fun resetToKnownStatePlatform(): Boolean
expect fun verifyBasicFunctionalityPlatform(): Boolean
expect fun delayPlatform(ms: Int)