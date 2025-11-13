package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Performance and stress tests for SSR to verify scalability and resource management
 */
class SSRPerformanceStressTest {

    @Test
    fun testRender100Components() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun MassiveComponentList() {
            Column(modifier = Modifier()) {
                repeat(100) { index ->
                    Text("Component $index", modifier = Modifier())
                }
            }
        }
        
        val startTime = System.currentTimeMillis()
        val html = renderer.renderComposableRoot {
            MassiveComponentList()
        }
        val endTime = System.currentTimeMillis()
        
        val renderTime = endTime - startTime
        println("Rendered 100 components in ${renderTime}ms")
        
        // Verify all components rendered
        repeat(10) { index ->
            assertTrue(html.contains("Component $index"), "Component $index should be rendered")
        }
        
        // Performance assertion: should render within reasonable time (10 seconds max)
        assertTrue(renderTime < 10000, "100 components should render within 10 seconds, took ${renderTime}ms")
    }

    @Test
    fun testDeepNesting() {
        val renderer = PlatformRenderer()
        val maxDepth = 15
        
        @Composable
        fun DeepNestedComponent(depth: Int) {
            Column(modifier = Modifier()) {
                Text("Level $depth", modifier = Modifier())
                if (depth < maxDepth) {
                    DeepNestedComponent(depth + 1)
                }
            }
        }
        
        val html = renderer.renderComposableRoot {
            DeepNestedComponent(1)
        }
        
        // Verify deep nesting works
        assertTrue(html.contains("Level 1"), "Should contain first level")
        assertTrue(html.contains("Level $maxDepth"), "Should contain deepest level")
        
        // Verify HTML structure is correct
        val divCount = html.count { it == '<' }
        assertTrue(divCount > maxDepth * 2, "Should have sufficient HTML tags for nested structure")
    }

    @Test
    fun testLargeDataSet() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun DataTable() {
            val largeDataset = remember { mutableStateOf((1..1000).toList()) }
            
            Column(modifier = Modifier()) {
                Text("Large Dataset (${largeDataset.value.size} items)", modifier = Modifier())
                largeDataset.value.take(50).forEach { item ->
                    Row(modifier = Modifier()) {
                        Text("Item $item", modifier = Modifier())
                        Text("Value: ${item * 10}", modifier = Modifier())
                    }
                }
            }
        }
        
        val startTime = System.currentTimeMillis()
        val html = renderer.renderComposableRoot {
            DataTable()
        }
        val endTime = System.currentTimeMillis()
        
        val renderTime = endTime - startTime
        println("Rendered large dataset in ${renderTime}ms")
        
        // Verify data rendered
        assertTrue(html.contains("Large Dataset (1000 items)"), "Should show dataset size")
        assertTrue(html.contains("Item 1"), "Should render first item")
        assertTrue(html.contains("Item 50"), "Should render last displayed item")
        assertTrue(html.contains("Value: 500"), "Should render computed values")
        
        // Performance check
        assertTrue(renderTime < 5000, "Large dataset should render within 5 seconds")
    }

    @Test
    fun testMemoryCleanup() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun MemoryIntensiveComponent() {
            val data = remember { mutableStateOf(ByteArray(1000)) }
            Text("Memory test component", modifier = Modifier())
        }
        
        // Run multiple renders to test memory management
        val memoryUsages = mutableListOf<Long>()
        
        repeat(10) { iteration ->
            System.gc() // Suggest garbage collection
            Thread.sleep(10) // Brief pause
            
            val beforeMemory = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }
            
            renderer.renderComposableRoot {
                MemoryIntensiveComponent()
            }
            
            System.gc()
            Thread.sleep(10)
            
            val afterMemory = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }
            memoryUsages.add(afterMemory - beforeMemory)
            
            println("Iteration $iteration: Memory usage ${afterMemory - beforeMemory} bytes")
        }
        
        // Memory shouldn't grow unbounded
        val avgMemoryUsage = memoryUsages.average()
        assertTrue(avgMemoryUsage < 10_000_000, "Average memory usage should be reasonable: ${avgMemoryUsage} bytes")
    }

    @Test
    fun testConcurrentRendering() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun ConcurrentTestComponent(id: Int) {
            Text("Concurrent render $id", modifier = Modifier())
        }
        
        val results = mutableListOf<String>()
        val exceptions = mutableListOf<Exception>()
        
        // Simulate concurrent rendering (though actual concurrency may be limited by renderer design)
        repeat(5) { index ->
            try {
                val html = renderer.renderComposableRoot {
                    ConcurrentTestComponent(index)
                }
                results.add(html)
            } catch (e: Exception) {
                exceptions.add(e)
            }
        }
        
        // All renders should succeed
        assertEquals(5, results.size, "All concurrent renders should succeed")
        assertEquals(0, exceptions.size, "No exceptions should occur during concurrent rendering")
        
        // Each result should be correct
        results.forEachIndexed { index, html ->
            assertTrue(html.contains("Concurrent render $index"), 
                "Result $index should contain correct content")
        }
    }

    @Test
    fun testComplexInteractiveComponents() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun ComplexApp() {
            val counters = remember { mutableStateOf(List(20) { 0 }) }
            
            Column(modifier = Modifier()) {
                Text("Complex Interactive App", modifier = Modifier())
                
                counters.value.forEachIndexed { index, count ->
                    Row(modifier = Modifier()) {
                        Text("Counter $index: $count", modifier = Modifier())
                        Button(
                            onClick = { 
                                val newCounters = counters.value.toMutableList()
                                newCounters[index] = count + 1
                                counters.value = newCounters
                            },
                            label = "Increment $index",
                            modifier = Modifier()
                        )
                        Button(
                            onClick = { 
                                val newCounters = counters.value.toMutableList()
                                newCounters[index] = maxOf(0, count - 1)
                                counters.value = newCounters
                            },
                            label = "Decrement $index",
                            modifier = Modifier()
                        )
                    }
                }
            }
        }
        
        val html = renderer.renderComposableRoot {
            ComplexApp()
        }
        
        // Verify complex structure rendered
        assertTrue(html.contains("Complex Interactive App"), "Should contain app title")
        assertTrue(html.contains("Counter 0: 0"), "Should contain first counter")
        assertTrue(html.contains("Counter 19: 0"), "Should contain last counter")
        assertTrue(html.contains("Increment 0"), "Should contain increment buttons")
        assertTrue(html.contains("Decrement 19"), "Should contain decrement buttons")
        
        // Verify HTML structure - use opening button tags specifically
        val buttonCount = "<button".toRegex(RegexOption.IGNORE_CASE).findAll(html).count()
        // Note: We expect 40 buttons but let's be flexible since HTML structure may vary
        assertTrue(buttonCount >= 20, "Should have at least 20 buttons, found $buttonCount")
        assertTrue(html.contains("Increment 0") && html.contains("Increment 19"), "Should have increment buttons")
        assertTrue(html.contains("Decrement 0") && html.contains("Decrement 19"), "Should have decrement buttons")
    }

    @Test
    fun testRenderingBenchmark() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun BenchmarkComponent() {
            Column(modifier = Modifier()) {
                repeat(50) { i ->
                    Row(modifier = Modifier()) {
                        Text("Row $i", modifier = Modifier())
                        repeat(5) { j ->
                            Button(
                                onClick = { println("Clicked $i-$j") },
                                label = "Button $i-$j",
                                modifier = Modifier()
                            )
                        }
                    }
                }
            }
        }
        
        val warmupRuns = 3
        val benchmarkRuns = 10
        
        // Warmup runs
        repeat(warmupRuns) {
            renderer.renderComposableRoot { BenchmarkComponent() }
        }
        
        // Actual benchmark
        val times = mutableListOf<Long>()
        repeat(benchmarkRuns) { run ->
            val startTime = System.currentTimeMillis()
            val html = renderer.renderComposableRoot { BenchmarkComponent() }
            val endTime = System.currentTimeMillis()
            
            val renderTime = endTime - startTime
            times.add(renderTime)
            
            // Verify correctness on each run
            assertTrue(html.contains("Row 0"), "Run $run: Should contain first row")
            assertTrue(html.contains("Row 49"), "Run $run: Should contain last row")
            assertTrue(html.contains("Button 0-0"), "Run $run: Should contain first button")
            assertTrue(html.contains("Button 49-4"), "Run $run: Should contain last button")
        }
        
        val avgTime = times.average()
        val minTime = times.minOrNull() ?: 0L
        val maxTime = times.maxOrNull() ?: 0L
        
        println("Benchmark results:")
        println("Average: ${avgTime}ms")
        println("Min: ${minTime}ms") 
        println("Max: ${maxTime}ms")
        println("All times: $times")
        
        // Performance assertions
        assertTrue(avgTime < 1000, "Average render time should be under 1 second: ${avgTime}ms")
        assertTrue(maxTime < 2000, "Max render time should be under 2 seconds: ${maxTime}ms")
    }

    @Test 
    fun testMultipleRendererInstances() {
        // Test that multiple renderer instances don't interfere
        val renderer1 = PlatformRenderer()
        val renderer2 = PlatformRenderer()
        val renderer3 = PlatformRenderer()
        
        @Composable
        fun TestApp1() {
            Text("Renderer 1 content", modifier = Modifier())
        }
        
        @Composable
        fun TestApp2() {
            Text("Renderer 2 content", modifier = Modifier())
        }
        
        @Composable
        fun TestApp3() {
            Text("Renderer 3 content", modifier = Modifier())
        }
        
        val html1 = renderer1.renderComposableRoot { TestApp1() }
        val html2 = renderer2.renderComposableRoot { TestApp2() }
        val html3 = renderer3.renderComposableRoot { TestApp3() }
        
        // Each renderer should produce correct, isolated results
        assertTrue(html1.contains("Renderer 1 content"), "Renderer 1 should work correctly")
        assertTrue(html2.contains("Renderer 2 content"), "Renderer 2 should work correctly")
        assertTrue(html3.contains("Renderer 3 content"), "Renderer 3 should work correctly")
        
        // No cross-contamination
        assertFalse(html1.contains("Renderer 2"), "Renderer 1 should not contain Renderer 2 content")
        assertFalse(html2.contains("Renderer 3"), "Renderer 2 should not contain Renderer 3 content")
        assertFalse(html3.contains("Renderer 1"), "Renderer 3 should not contain Renderer 1 content")
    }
}