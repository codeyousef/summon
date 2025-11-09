# WebAssembly (WASM) API Reference

This document provides comprehensive API reference for Summon's WebAssembly implementation, covering all WASM-specific
functions, classes, and utilities.

## Table of Contents

- [Core WASM APIs](#core-wasm-apis)
- [Platform Detection](#platform-detection)
- [Performance APIs](#performance-apis)
- [Browser Integration](#browser-integration)
- [Memory Management](#memory-management)
- [Error Handling](#error-handling)
- [Build Configuration](#build-configuration)

## Core WASM APIs

### `wasmMain`

The main entry point for WASM applications.

```kotlin
fun wasmMain(
    enableHydration: Boolean = true,
    fallbackToJs: Boolean = true,
    rootElementId: String = "root",
    content: @Composable () -> Unit
)
```

**Parameters:**

- `enableHydration`: Enable server-side rendering hydration
- `fallbackToJs`: Automatically fallback to JavaScript if WASM fails
- `rootElementId`: DOM element ID to mount the application
- `content`: Root composable function

**Example:**

```kotlin
fun main() {
    wasmMain(
        enableHydration = true,
        fallbackToJs = true,
        rootElementId = "app"
    ) {
        App()
    }
}
```

### `WasmRenderer`

Direct WASM rendering interface for advanced use cases.

```kotlin
class WasmRenderer {
    fun renderToElement(
        element: Element,
        content: @Composable () -> Unit
    )

    fun renderToString(
        content: @Composable () -> Unit
    ): String

    fun hydrate(
        element: Element,
        content: @Composable () -> Unit
    )
}
```

**Methods:**

- `renderToElement`: Render directly to a DOM element
- `renderToString`: Server-side rendering to HTML string
- `hydrate`: Hydrate server-rendered content with WASM interactivity

**Example:**

```kotlin
val renderer = WasmRenderer()

// Client-side rendering
renderer.renderToElement(document.getElementById("root")!!) {
    MyApp()
}

// Server-side rendering
val html = renderer.renderToString { MyApp() }

// Hydration
renderer.hydrate(document.getElementById("root")!!) {
    MyApp()
}
```

## Platform Detection

### `WasmPlatform`

Platform detection utilities for WASM environments.

```kotlin
object WasmPlatform {
    val isWasmSupported: Boolean
    val wasmVersion: WasmVersion
    val browserInfo: BrowserInfo
    val performanceInfo: PerformanceInfo

    fun detectCapabilities(): WasmCapabilities
    fun isFeatureSupported(feature: WasmFeature): Boolean
}
```

**Properties:**

- `isWasmSupported`: True if current browser supports WASM
- `wasmVersion`: Detected WASM version and capabilities
- `browserInfo`: Browser name, version, and engine information
- `performanceInfo`: Performance metrics and capabilities

**Example:**

```kotlin
@Composable
fun ConditionalComponent() {
    if (WasmPlatform.isWasmSupported) {
        HighPerformanceWasmComponent()
    } else {
        FallbackJsComponent()
    }
}
```

### `WasmCapabilities`

Detailed WASM capability detection.

```kotlin
data class WasmCapabilities(
    val hasThreadSupport: Boolean,
    val hasSIMDSupport: Boolean,
    val hasGCSupport: Boolean,
    val hasExceptionHandling: Boolean,
    val maxMemorySize: Long,
    val supportedFeatures: Set<WasmFeature>
)
```

### `BrowserInfo`

Browser detection and compatibility information.

```kotlin
data class BrowserInfo(
    val name: String,
    val version: String,
    val engine: String,
    val isMobile: Boolean,
    val supportLevel: WasmSupportLevel
)

enum class WasmSupportLevel {
    FULL,        // Complete WASM support
    PARTIAL,     // Limited WASM support
    NONE         // No WASM support
}
```

## Performance APIs

### `WasmPerformance`

Performance monitoring and optimization utilities.

```kotlin
object WasmPerformance {
    fun measureExecutionTime(block: () -> Unit): Double
    fun getMemoryUsage(): MemoryInfo
    fun enableProfiling(enabled: Boolean)
    fun getPerformanceMarks(): List<PerformanceMark>

    // Optimization hints
    fun optimizeForLatency()
    fun optimizeForThroughput()
    fun optimizeForMemory()
}
```

**Methods:**

- `measureExecutionTime`: Measure execution time in milliseconds
- `getMemoryUsage`: Current memory usage statistics
- `enableProfiling`: Enable/disable performance profiling
- `getPerformanceMarks`: Retrieve performance markers

**Example:**

```kotlin
@Composable
fun PerformanceMonitoredComponent() {
    val executionTime = remember {
        WasmPerformance.measureExecutionTime {
            // Expensive computation
            processLargeDataset()
        }
    }

    Text("Computation took: ${executionTime}ms")
}
```

### `MemoryInfo`

Memory usage statistics for WASM applications.

```kotlin
data class MemoryInfo(
    val usedHeapSize: Long,
    val totalHeapSize: Long,
    val heapSizeLimit: Long,
    val wasmMemoryPages: Int,
    val maxWasmMemoryPages: Int
)
```

### `PerformanceMark`

Performance timing markers.

```kotlin
data class PerformanceMark(
    val name: String,
    val timestamp: Double,
    val duration: Double?,
    val category: PerformanceCategory
)

enum class PerformanceCategory {
    INITIALIZATION,
    RENDERING,
    COMPUTATION,
    NETWORK,
    USER_INTERACTION
}
```

## Browser Integration

### `WasmBridge`

Bridge for communicating between WASM and JavaScript.

```kotlin
object WasmBridge {
    fun callJsFunction(
        functionName: String,
        vararg args: Any
    ): Any?

    fun registerJsCallback(
        name: String,
        callback: (Array<Any>) -> Any?
    )

    fun importJsModule(moduleName: String): JsModule
}
```

**Methods:**

- `callJsFunction`: Call JavaScript functions from WASM
- `registerJsCallback`: Register WASM callbacks for JavaScript
- `importJsModule`: Import and use JavaScript modules

**Example:**

```kotlin
// Call JavaScript function
val result = WasmBridge.callJsFunction("console.log", "Hello from WASM!")

// Register callback
WasmBridge.registerJsCallback("onDataReceived") { args ->
    val data = args[0] as String
    processData(data)
}

// Import JS module
val chartModule = WasmBridge.importJsModule("chart.js")
```

### `DomApi`

WASM-optimized DOM manipulation APIs.

```kotlin
object DomApi {
    fun getElementById(id: String): Element?
    fun createElement(tagName: String): Element
    fun querySelector(selector: String): Element?
    fun querySelectorAll(selector: String): List<Element>

    // Optimized batch operations
    fun batchUpdates(block: () -> Unit)
    fun createDocumentFragment(): DocumentFragment
}
```

## Memory Management

### `WasmMemory`

Memory management utilities for WASM applications.

```kotlin
object WasmMemory {
    fun allocate(size: Int): MemoryBuffer
    fun deallocate(buffer: MemoryBuffer)
    fun resize(size: Int): Boolean
    fun getUsage(): MemoryUsage

    // Garbage collection hints
    fun suggestGC()
    fun enableAutoGC(enabled: Boolean)
}
```

### `MemoryBuffer`

Low-level memory buffer for WASM.

```kotlin
class MemoryBuffer(val size: Int) {
    fun readBytes(offset: Int, length: Int): ByteArray
    fun writeBytes(offset: Int, data: ByteArray)
    fun readInt(offset: Int): Int
    fun writeInt(offset: Int, value: Int)
    fun clear()
    fun dispose()
}
```

### `MemoryUsage`

Memory usage tracking and statistics.

```kotlin
data class MemoryUsage(
    val allocated: Long,
    val used: Long,
    val free: Long,
    val fragmentation: Double,
    val gcCount: Int,
    val gcTime: Double
)
```

## Error Handling

### `WasmError`

WASM-specific error types and handling.

```kotlin
sealed class WasmError : Exception() {
    class InitializationError(message: String) : WasmError()
    class MemoryError(message: String) : WasmError()
    class RuntimeError(message: String) : WasmError()
    class BridgeError(message: String) : WasmError()
}
```

### `ErrorBoundary`

Error boundary component for WASM applications.

```kotlin
@Composable
fun WasmErrorBoundary(
    fallback: @Composable (WasmError) -> Unit = { DefaultErrorUI(it) },
    onError: (WasmError) -> Unit = { },
    content: @Composable () -> Unit
)
```

**Example:**

```kotlin
@Composable
fun App() {
    WasmErrorBoundary(
        fallback = { error ->
            Column {
                Text("WASM Error: ${error.message}")
                Button(
                    onClick = { window.location.reload() },
                    label = "Reload"
                )
            }
        },
        onError = { error ->
            // Log error to analytics
            Analytics.logError("wasm_error", error)
        }
    ) {
        MainApplication()
    }
}
```

## Build Configuration

### `WasmConfig`

Build-time configuration for WASM optimization.

```kotlin
data class WasmConfig(
    val optimizationLevel: OptimizationLevel,
    val enableSourceMaps: Boolean,
    val enableProfiling: Boolean,
    val memoryInitialSize: Int,
    val memoryMaximumSize: Int,
    val features: Set<WasmFeature>
)

enum class OptimizationLevel {
    DEBUG,      // -O0: No optimization, fast compilation
    BALANCED,   // -O2: Balanced optimization
    AGGRESSIVE  // -O3: Maximum optimization
}
```

### `WasmFeature`

Available WASM features for configuration.

```kotlin
enum class WasmFeature {
    THREADS,
    SIMD,
    GARBAGE_COLLECTION,
    EXCEPTION_HANDLING,
    REFERENCE_TYPES,
    BULK_MEMORY,
    SIGN_EXTENSION
}
```

### Build Configuration Example

```kotlin
// build.gradle.kts
kotlin {
    wasmJs {
        moduleName = "my-app"
        browser {
            commonWebpackConfig {
                // Development configuration
                devtool = "source-map"
                mode = "development"

                // Production configuration for optimized builds
                optimization {
                    minimize = true
                    splitChunks {
                        chunks = "all"
                        cacheGroups {
                            default {
                                minChunks = 2
                                priority = -20
                                reuseExistingChunk = true
                            }
                            vendor {
                                test = "[\\/]node_modules[\\/]"
                                priority = -10
                                chunks = "all"
                            }
                        }
                    }
                }
            }
        }
        binaries.executable()
    }
}
```

## Advanced APIs

### `WasmWorker`

Web Workers integration for WASM.

```kotlin
class WasmWorker(scriptUrl: String) {
    fun postMessage(data: Any)
    fun onMessage(callback: (Any) -> Unit)
    fun terminate()

    companion object {
        fun isSupported(): Boolean
        fun createWorker(wasmModule: WasmModule): WasmWorker
    }
}
```

### `WasmStreaming`

Streaming and progressive loading for WASM.

```kotlin
object WasmStreaming {
    suspend fun loadModuleStreaming(url: String): WasmModule
    suspend fun compileStreaming(source: ByteArray): WasmModule
    fun instantiateStreaming(
        module: WasmModule,
        imports: Map<String, Any> = emptyMap()
    ): WasmInstance
}
```

### `WasmInterop`

Advanced interoperability with JavaScript and Web APIs.

```kotlin
object WasmInterop {
    fun exportFunction(name: String, function: Function<*>)
    fun importFunction(name: String): Function<*>
    fun createTypedArray(type: TypedArrayType, size: Int): TypedArray
    fun shareMemory(buffer: MemoryBuffer): SharedArrayBuffer
}
```

## Constants and Enumerations

### `WasmConstants`

Common constants used in WASM operations.

```kotlin
object WasmConstants {
    const val PAGE_SIZE = 65536 // 64KB
    const val MAX_MEMORY_PAGES = 65536
    const val STACK_SIZE_DEFAULT = 1048576 // 1MB
    const val HEAP_SIZE_DEFAULT = 16777216 // 16MB
}
```

### `WasmVersion`

WASM version and capability enumeration.

```kotlin
enum class WasmVersion(val version: String, val capabilities: Set<WasmFeature>) {
    V1_0("1.0", setOf(/* basic features */)),
    V2_0("2.0", setOf(/* extended features */)),
    UNSUPPORTED("0.0", emptySet())
}
```

## Migration and Compatibility

### `WasmMigration`

Utilities for migrating from JavaScript to WASM.

```kotlin
object WasmMigration {
    fun migrateJsState(jsState: Map<String, Any>): Map<String, Any>
    fun createCompatibilityLayer(): CompatibilityLayer
    fun validateMigration(): MigrationResult
}
```

### `CompatibilityLayer`

Compatibility layer for gradual WASM adoption.

```kotlin
class CompatibilityLayer {
    fun registerJsCompatFunction(name: String, impl: Function<*>)
    fun enableLegacySupport(enabled: Boolean)
    fun getCompatibilityReport(): CompatibilityReport
}
```

This API reference covers all major WASM-specific functionality in Summon 0.4.7.0. For complete examples and integration
guides, see the [WASM Guide](../wasm-guide.md).
