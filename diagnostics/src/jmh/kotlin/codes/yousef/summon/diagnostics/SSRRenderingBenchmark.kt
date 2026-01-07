package codes.yousef.summon.diagnostics

import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.modifier.background
import codes.yousef.summon.modifier.padding
import codes.yousef.summon.runtime.CallbackRegistry
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.clearPlatformRenderer
import codes.yousef.summon.runtime.setPlatformRenderer
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * JMH benchmarks for SSR rendering performance.
 *
 * Run with: ./gradlew :diagnostics:jmh
 *
 * These benchmarks measure:
 * - Basic HTML rendering throughput
 * - Hydration-ready HTML rendering
 * - Component tree rendering overhead
 * - Large document generation
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
open class SSRRenderingBenchmark {

    private lateinit var renderer: PlatformRenderer

    @Setup(Level.Iteration)
    fun setup() {
        renderer = PlatformRenderer()
        setPlatformRenderer(renderer)
    }

    @TearDown(Level.Iteration)
    fun tearDown() {
        CallbackRegistry.clear()
        clearPlatformRenderer()
    }

    /**
     * Baseline: Render empty composable.
     */
    @Benchmark
    fun renderEmpty(): String {
        return renderer.renderComposableRoot { }
    }

    /**
     * Render single Text component.
     */
    @Benchmark
    fun renderSingleText(): String {
        return renderer.renderComposableRoot {
            Text("Hello, World!")
        }
    }

    /**
     * Render simple component tree.
     */
    @Benchmark
    fun renderSimpleTree(): String {
        return renderer.renderComposableRoot {
            Column(modifier = Modifier()) {
                Text("Header")
                Row(modifier = Modifier()) {
                    Text("Item 1")
                    Text("Item 2")
                    Text("Item 3")
                }
                Text("Footer")
            }
        }
    }

    /**
     * Render component with modifiers.
     */
    @Benchmark
    fun renderWithModifiers(): String {
        return renderer.renderComposableRoot {
            Column(
                modifier = Modifier()
                    .padding("16px")
                    .background("blue")
            ) {
                Text("Styled content")
            }
        }
    }

    /**
     * Render list of items.
     */
    @Benchmark
    fun renderList20Items(): String {
        return renderer.renderComposableRoot {
            Column(modifier = Modifier()) {
                repeat(20) { i ->
                    Text("Item $i")
                }
            }
        }
    }

    /**
     * Render larger list.
     */
    @Benchmark
    fun renderList100Items(): String {
        return renderer.renderComposableRoot {
            Column(modifier = Modifier()) {
                repeat(100) { i ->
                    Text("Item $i")
                }
            }
        }
    }

    /**
     * Render with hydration markers.
     */
    @Benchmark
    fun renderWithHydration(): String {
        return renderer.renderComposableRootWithHydration {
            Column(modifier = Modifier()) {
                Text("Hydrated content")
                Button(onClick = {}, label = "Click me")
            }
        }
    }

    /**
     * Render complex tree with hydration.
     */
    @Benchmark
    fun renderComplexWithHydration(): String {
        return renderer.renderComposableRootWithHydration {
            Column(modifier = Modifier()) {
                repeat(10) { i ->
                    Row(modifier = Modifier()) {
                        Text("Row $i")
                        Button(onClick = {}, label = "Button $i")
                    }
                }
            }
        }
    }

    /**
     * Render nested layout structure.
     */
    @Benchmark
    fun renderNestedLayouts(): String {
        return renderer.renderComposableRoot {
            Column(modifier = Modifier()) {
                repeat(5) { outer ->
                    Row(modifier = Modifier()) {
                        repeat(5) { inner ->
                            Column(modifier = Modifier()) {
                                Text("Cell $outer-$inner")
                            }
                        }
                    }
                }
            }
        }
    }
}
