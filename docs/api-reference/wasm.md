# WebAssembly (WASM) API Reference

This document provides the API reference for Summon's WebAssembly implementation.

## Table of Contents

- [Entry Points](#entry-points)
- [DOM API](#dom-api)
- [Performance Monitoring](#performance-monitoring)
- [Hydration](#hydration)
- [Build Configuration](#build-configuration)

## Entry Points

### `renderComposableRoot`

The main entry point for WASM applications. Renders a composable function to a DOM element.

**Location:** `codes.yousef.summon.WasmApi`

```kotlin
fun renderComposableRoot(
    rootElementId: String,
    composable: @Composable () -> Unit
)
```

**Parameters:**

- `rootElementId`: The DOM element ID where the app will be mounted
- `composable`: The root composable function to render

**Example:**

```kotlin
fun main() {
    renderComposableRoot("app") {
        App()
    }
}
```

### `hydrateComposableRoot`

Hydrates server-rendered HTML with client-side interactivity. Use this when HTML has been pre-rendered by the server (
SSR).

**Location:** `codes.yousef.summon.WasmApi`

```kotlin
fun hydrateComposableRoot(
    rootElementId: String,
    composable: @Composable () -> Unit
)
```

**Parameters:**

- `rootElementId`: The DOM element ID containing server-rendered content
- `composable`: The same composable function used for server rendering

**Example:**

```kotlin
fun main() {
    hydrateComposableRoot("app") {
        App()
    }
}
```

**Note:** If hydration fails, the function automatically falls back to client-side rendering.

---

## DOM API

### `WasmDOMAPI`

WASM-optimized DOM manipulation API using string-based element IDs.

**Location:** `codes.yousef.summon.runtime.WasmDOMAPI`

```kotlin
class WasmDOMAPI : DOMAPIContract {
    fun createElement(tagName: String): DOMElement
    fun setTextContent(element: DOMElement, text: String)
    fun setAttribute(element: DOMElement, name: String, value: String)
    fun addClass(element: DOMElement, className: String)
    fun removeClass(element: DOMElement, className: String)
    fun appendChild(parent: DOMElement, child: DOMElement)
    fun removeElement(element: DOMElement)
    fun addEventListener(element: DOMElement, eventType: String, handler: (WasmDOMEvent) -> Unit)
    fun removeEventListener(element: DOMElement, eventType: String, handler: (WasmDOMEvent) -> Unit)

    // WASM-specific optimizations
    fun batchDOMOperations(operations: List<() -> Unit>)
    fun measurePerformance(operation: String, block: () -> Unit): Long
    fun getMemoryUsage(): WasmMemoryInfo
    fun clearCache()
}
```

### `WasmDOMElement`

WASM-specific DOM element implementation.

```kotlin
class WasmDOMElement(
    val tagName: String,
    val id: String,
    val nativeElementId: String
) : DOMElement {
    val className: String
    val textContent: String?

    fun getAttribute(name: String): String?
    fun setAttribute(name: String, value: String)
    fun removeAttribute(name: String)
    fun appendChild(child: DOMElement)
    fun removeChild(child: DOMElement)
    fun querySelector(selector: String): DOMElement?
    fun querySelectorAll(selector: String): List<DOMElement>
}
```

### `WasmMemoryInfo`

Memory usage statistics for WASM DOM operations.

```kotlin
data class WasmMemoryInfo(
    val totalElements: Int,
    val totalEventHandlers: Int,
    val cacheSize: Int,
    val timestamp: Long
)
```

---

## Performance Monitoring

### `WasmPerformanceMonitor`

Comprehensive performance tracking for WASM applications.

**Location:** `codes.yousef.summon.runtime.WasmPerformanceMonitor`

```kotlin
class WasmPerformanceMonitor {
    // Measure operation performance
    fun <T> measure(operationName: String, block: () -> T): T

    // Manual timing
    fun startOperation(operationName: String)
    fun endOperation(operationName: String)

    // Frame tracking
    fun recordFrame()

    // Custom metrics
    fun recordMetric(name: String, value: Double, unit: String = "")
    fun recordMemoryUsage(allocatedBytes: Long, freedBytes: Long = 0)

    // Reports and stats
    fun generateReport(): WasmPerformanceReport
    fun getCurrentStats(): WasmPerformanceStats
    fun getSlowestOperations(count: Int = 10): List<OperationSummary>

    // Configuration
    fun setEnabled(enabled: Boolean)
    fun setMaxHistorySize(size: Int)
    fun reset()
}
```

**Example:**

```kotlin
val monitor = WasmPerformanceMonitor()

// Measure an operation
val result = monitor.measure("renderComponent") {
    renderExpensiveComponent()
}

// Generate performance report
val report = monitor.generateReport()
println("Total operations: ${report.totalOperations}")
println("Average frame rate: ${report.averageFrameRate}")
```

### `WasmPerformanceStats`

Real-time performance statistics.

```kotlin
data class WasmPerformanceStats(
    val currentFrameRate: Double,
    val currentFrameTime: Long,
    val activeOperations: List<String>,
    val memoryUsage: WasmMemoryUsage,
    val operationCount: Int,
    val isMonitoring: Boolean
)
```

### `WasmPerformanceReport`

Comprehensive performance report.

```kotlin
data class WasmPerformanceReport(
    val totalOperations: Int,
    val totalMeasurements: Int,
    val totalTime: Long,
    val averageFrameRate: Double,
    val slowestOperations: List<Pair<String, Long>>,
    val operationBreakdown: List<OperationSummary>,
    val frameMetrics: FrameSummary,
    val memoryMetrics: MemorySummary,
    val errorCount: Int,
    val reportTimestamp: Long
)
```

---

## Hydration

### `GlobalEventListener`

Document-level event handler for SSR hydration.

**Location:** `codes.yousef.summon.hydration.GlobalEventListener`

```kotlin
object GlobalEventListener {
    var enableLogging: Boolean

    fun init()
    fun reset()
    fun isElementHydrated(elementId: String): Boolean
    fun markElementHydrated(elementId: String)
    fun handleEvent(type: String, sid: String, event: Event, element: Element? = null)
}
```

**Features:**

- Handles document-level events for `data-action` based client-side interactions
- Buffers events for non-hydrated components and replays them after hydration
- Prevents duplicate initialization when both JS and WASM bundles load

**Example:**

```kotlin
// Initialize event handling (called automatically by hydrateComposableRoot)
GlobalEventListener.init()

// Enable debug logging
GlobalEventListener.enableLogging = true

// Check hydration status
if (GlobalEventListener.isElementHydrated("my-button")) {
    // Element is ready for interaction
}
```

### `EventBuffer`

Buffers events for elements that haven't been hydrated yet.

**Location:** `codes.yousef.summon.hydration.EventBuffer`

```kotlin
class EventBuffer {
    fun captureEvent(event: CapturedEvent)
    fun hasEventsFor(elementId: String): Boolean
    fun eventCountFor(elementId: String): Int
    fun replayEventsFor(elementId: String, handler: (CapturedEvent) -> Unit)
    fun clear()

    companion object {
        val instance: EventBuffer
    }
}
```

### `ClientDispatcher`

Dispatches client-side actions from hydrated elements.

**Location:** `codes.yousef.summon.hydration.ClientDispatcher`

```kotlin
object ClientDispatcher {
    fun dispatch(actionJson: String)
}
```

---

## Build Configuration

### Gradle Configuration

```kotlin
// build.gradle.kts
kotlin {
    wasmJs {
        moduleName = "my-app"
        browser {
            commonWebpackConfig {
                outputFileName = "my-app.js"
                devtool = "source-map"
            }
        }
        binaries.executable()
    }
}

dependencies {
    implementation("codes.yousef:summon-wasm-js:0.6.2.2")
}
```

### Development Commands

```bash
# Development build with hot reload
./gradlew wasmJsBrowserDevelopmentRun

# Production build
./gradlew wasmJsBrowserProductionWebpack

# Run WASM tests
./gradlew :summon-core:wasmJsNodeTest
```

### HTML Integration

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My WASM App</title>
</head>
<body>
    <div id="app">Loading...</div>
    <script src="my-app.js"></script>
</body>
</html>
```

---

## Platform Detection

The WASM target uses `PlatformRenderer` (same as JS target) rather than a separate `WasmRenderer`. Platform-specific
behavior is handled internally through the Kotlin/WASM compiler's JavaScript interop.

---

For complete examples and integration guides, see the [WASM Guide](../wasm-guide.md).
