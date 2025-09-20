package code.yousef.summon.core

/**
 * JVM implementation of platform detection.
 */

actual fun detectPlatformTarget(): PlatformTarget = PlatformTarget.JVM

actual fun detectBrowserInfo(): BrowserInfo? = null // No browser in JVM context

actual fun isWasmSupported(): Boolean = false // Not applicable on JVM

actual fun isWasmSIMDSupported(): Boolean = false // Not applicable on JVM

actual fun isWasmThreadsSupported(): Boolean = false // Not applicable on JVM

actual fun isModuleSupported(): Boolean = false // Not applicable on JVM

actual fun isDynamicImportSupported(): Boolean = false // Not applicable on JVM

actual fun isWebWorkersSupported(): Boolean = false // Not applicable on JVM

actual fun hasDOMCapabilities(): Boolean = false // No DOM on JVM

actual fun hasSSRCapabilities(): Boolean = true // JVM supports SSR

actual fun getUserAgent(): String? = null // No user agent on JVM

actual fun getCurrentURL(): String? = null // No URL on JVM (unless using server context)

actual fun isMobileDevice(): Boolean = false // JVM is not mobile

actual fun isTouchSupported(): Boolean = false // JVM doesn't support touch

actual fun getScreenWidth(): Int = -1 // No screen on JVM

actual fun getScreenHeight(): Int = -1 // No screen on JVM

actual fun getDevicePixelRatio(): Double = 1.0 // Default ratio for JVM