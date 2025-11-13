package codes.yousef.summon.runtime

/**
 * Minimal WASM Renderer stub to enable compilation.
 * This provides basic structure without complex dependencies.
 */
class WasmRenderer {

    fun render(content: String): String {
        return "<div>$content</div>"
    }

    fun renderToString(block: () -> Unit): String {
        // Simplified rendering
        return "<html><body>Rendered content</body></html>"
    }

    fun getMemoryUsage(): WasmMemoryUsage {
        return WasmMemoryUsage(
            totalElements = 0,
            totalEventHandlers = 0,
            cacheSize = 0,
            estimatedMemoryBytes = 0L,
            timestamp = wasmPerformanceNow().toLong()
        )
    }
}