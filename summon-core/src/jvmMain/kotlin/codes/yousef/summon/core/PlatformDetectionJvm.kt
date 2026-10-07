package codes.yousef.summon.core

/**
 * JVM implementation of platform detection.
 */

actual fun detectPlatformTarget(): PlatformTarget = PlatformTarget.JVM

/**
 * Executes the detect browser info operation.
 *
 * @return The resulting value.
 */
actual fun detectBrowserInfo(): BrowserInfo? = null // No browser in JVM context

/**
 * Returns whether WASM supported.
 *
 * @return The resulting value.
 */
actual fun isWasmSupported(): Boolean = false // Not applicable on JVM

/**
 * Returns whether WASM simd supported.
 *
 * @return The resulting value.
 */
actual fun isWasmSIMDSupported(): Boolean = false // Not applicable on JVM

/**
 * Returns whether WASM threads supported.
 *
 * @return The resulting value.
 */
actual fun isWasmThreadsSupported(): Boolean = false // Not applicable on JVM

/**
 * Returns whether module supported.
 *
 * @return The resulting value.
 */
actual fun isModuleSupported(): Boolean = false // Not applicable on JVM

/**
 * Returns whether dynamic import supported.
 *
 * @return The resulting value.
 */
actual fun isDynamicImportSupported(): Boolean = false // Not applicable on JVM

/**
 * Returns whether web workers supported.
 *
 * @return The resulting value.
 */
actual fun isWebWorkersSupported(): Boolean = false // Not applicable on JVM

/**
 * Returns whether this value has DOM capabilities.
 *
 * @return The resulting value.
 */
actual fun hasDOMCapabilities(): Boolean = false // No DOM on JVM

/**
 * Returns whether this value has ssr capabilities.
 *
 * @return The resulting value.
 */
actual fun hasSSRCapabilities(): Boolean = true // JVM supports SSR

/**
 * Returns user agent.
 *
 * @return The resulting value.
 */
actual fun getUserAgent(): String? = null // No user agent on JVM

/**
 * Returns current URL.
 *
 * @return The resulting value.
 */
actual fun getCurrentURL(): String? = null // No URL on JVM (unless using server context)

/**
 * Returns whether mobile device.
 *
 * @return The resulting value.
 */
actual fun isMobileDevice(): Boolean = false // JVM is not mobile

/**
 * Returns whether touch supported.
 *
 * @return The resulting value.
 */
actual fun isTouchSupported(): Boolean = false // JVM doesn't support touch

/**
 * Returns screen width.
 *
 * @return The resulting value.
 */
actual fun getScreenWidth(): Int = -1 // No screen on JVM

/**
 * Returns screen height.
 *
 * @return The resulting value.
 */
actual fun getScreenHeight(): Int = -1 // No screen on JVM

/**
 * Returns device pixel ratio.
 *
 * @return The resulting value.
 */
actual fun getDevicePixelRatio(): Double = 1.0 // Default ratio for JVM