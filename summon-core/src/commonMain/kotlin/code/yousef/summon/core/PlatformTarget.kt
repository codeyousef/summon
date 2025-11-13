package codes.yousef.summon.core

/**
 * Sealed class representing different compilation targets in the Summon framework.
 *
 * This abstraction provides type-safe platform identification and enables
 * platform-specific behavior through exhaustive when expressions.
 *
 * @since 0.3.3.0
 */
sealed class PlatformTarget {

    /**
     * JVM platform target for server-side rendering and backend applications.
     *
     * Capabilities:
     * - Server-side rendering (SSR)
     * - Backend integrations (databases, APIs)
     * - File system access
     * - No DOM access
     */
    object JVM : PlatformTarget() {
        override fun toString(): String = "PlatformTarget.JVM"
    }

    /**
     * JavaScript platform target for browser environments.
     *
     * Capabilities:
     * - DOM manipulation
     * - Browser APIs
     * - Client-side interactivity
     * - No server-side rendering
     */
    object JavaScript : PlatformTarget() {
        override fun toString(): String = "PlatformTarget.JavaScript"
    }

    /**
     * WebAssembly platform target for high-performance browser applications.
     *
     * Capabilities:
     * - DOM manipulation
     * - High-performance computation
     * - Memory-efficient execution
     * - Client-side interactivity
     * - No server-side rendering
     */
    object WebAssembly : PlatformTarget() {
        override fun toString(): String = "PlatformTarget.WebAssembly"
    }

    /**
     * Determines if this platform target runs in a browser environment.
     *
     * @return true for JavaScript and WebAssembly targets, false for JVM
     */
    val isBrowserTarget: Boolean
        get() = when (this) {
            is JVM -> false
            is JavaScript -> true
            is WebAssembly -> true
        }

    /**
     * Determines if this platform target supports server-side rendering.
     *
     * @return true for JVM target, false for browser targets
     */
    val hasSSRCapabilities: Boolean
        get() = when (this) {
            is JVM -> true
            is JavaScript -> false
            is WebAssembly -> false
        }

    /**
     * Determines if this platform target has DOM manipulation capabilities.
     *
     * @return true for browser targets, false for JVM
     */
    val hasDOMCapabilities: Boolean
        get() = when (this) {
            is JVM -> false
            is JavaScript -> true
            is WebAssembly -> true
        }
}