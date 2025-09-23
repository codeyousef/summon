package code.yousef.summon.core

/**
 * WASM implementation of FlowContentCompat that provides the necessary interface
 * for the composition system without depending on kotlinx.html.
 *
 * This implementation ensures that the composition system has a proper object
 * to work with during recomposition, preventing type cast errors.
 */
class FlowContentWasm : FlowContentCompat() {
    // WASM-specific implementation that doesn't depend on kotlinx.html
    // This provides a concrete implementation that the composition system can work with

    // Since WASM uses direct DOM manipulation through external functions,
    // this class serves as a proper receiver for content lambdas
    // without requiring specific HTML content building functionality
}

/**
 * Helper function to create a proper WASM FlowContentCompat instance.
 * This replaces the anonymous object creation with a concrete implementation.
 */
fun createWasmFlowContentCompat(): FlowContentCompat {
    return FlowContentWasm()
}