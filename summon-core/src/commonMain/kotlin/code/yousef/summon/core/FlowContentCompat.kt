package code.yousef.summon.core

/**
 * Cross-platform abstraction for HTML content containers.
 * This replaces the JVM-specific kotlinx.html.FlowContent to enable WASM support.
 */
abstract class FlowContentCompat {
    // This will be implemented by platform-specific renderers
    // JVM will delegate to kotlinx.html.FlowContent
    // JS/WASM will implement DOM operations directly
}

/**
 * Type alias for backwards compatibility and readability
 */
typealias FlowContent = FlowContentCompat