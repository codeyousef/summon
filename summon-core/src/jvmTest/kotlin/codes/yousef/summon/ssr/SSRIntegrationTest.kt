package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.components.layout.Row
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Integration tests for SSR functionality to verify the complete system works end-to-end.
 */
class SSRIntegrationTest {

    @Test
    fun testCompleteSSRWorkflow() {
        val renderer = PlatformRenderer()

        @Composable
        fun CompleteApp() {
            val counter = remember { mutableStateOf(0) }

            Column(modifier = Modifier()) {
                Text("Summon SSR Integration Test", modifier = Modifier())
                Text("Current count: ${counter.value}", modifier = Modifier())

                Row(modifier = Modifier()) {
                    Button(
                        onClick = { counter.value++ },
                        label = "Increment",
                        modifier = Modifier()
                    )

                    Button(
                        onClick = { counter.value-- },
                        label = "Decrement",
                        modifier = Modifier()
                    )

                    Button(
                        onClick = { counter.value = 0 },
                        label = "Reset",
                        modifier = Modifier()
                    )
                }

                Text("Footer: SSR is working!", modifier = Modifier())
            }
        }

        // Test standard SSR
        val html = renderer.renderComposableRoot {
            CompleteApp()
        }

        println("=== SSR Integration Test Output ===")
        println(html)

        // Verify HTML structure and content
        assertTrue(html.contains("Summon SSR Integration Test"), "Should contain app title")
        assertTrue(html.contains("Current count: 0"), "Should show initial count")
        assertTrue(html.contains("Increment"), "Should contain increment button")
        assertTrue(html.contains("Decrement"), "Should contain decrement button")
        assertTrue(html.contains("Reset"), "Should contain reset button")
        assertTrue(html.contains("Footer: SSR is working!"), "Should contain footer")

        // Verify HTML structure
        assertTrue(html.contains("<html"), "Should contain HTML structure")
        assertTrue(html.contains("<body"), "Should contain body")
        assertTrue(html.contains("<button"), "Should contain button elements")
        assertTrue(html.contains("<span"), "Should contain text elements")

        // Verify no runtime errors occurred
        assertFalse(html.contains("Exception") || html.contains("Error"), "Should not contain error messages")
    }

    @Test
    fun testSSRWithHydration() {
        val renderer = PlatformRenderer()

        @Composable
        fun HydrationApp() {
            val state = remember { mutableStateOf("server-rendered") }

            Column(modifier = Modifier()) {
                Text("Hydration Test: ${state.value}", modifier = Modifier())
                Button(
                    onClick = { state.value = "client-updated" },
                    label = "Update State",
                    modifier = Modifier()
                )
            }
        }

        val html = renderer.renderComposableRootWithHydration {
            HydrationApp()
        }

        println("=== SSR Hydration Test Output ===")
        println(html)

        // Verify hydration-specific content
        assertTrue(html.contains("Hydration Test: server-rendered"), "Should show server state")
        assertTrue(html.contains("Update State"), "Should contain interactive button")
        assertTrue(
            html.contains("summon-hydration") || html.contains("hydration"),
            "Should contain hydration data or scripts"
        )

        // Verify complete document structure for hydration
        assertTrue(html.contains("<!DOCTYPE html>"), "Should contain doctype for hydration")
        assertTrue(html.contains("<script"), "Should contain script tags for hydration")
    }

    @Test
    fun testMultipleSSROperationsIsolated() {
        val renderer = PlatformRenderer()

        @Composable
        fun App1() {
            val state = remember { mutableStateOf("app1") }
            Text("Application 1: ${state.value}", modifier = Modifier())
        }

        @Composable
        fun App2() {
            val state = remember { mutableStateOf("app2") }
            Text("Application 2: ${state.value}", modifier = Modifier())
        }

        // Render multiple apps to verify isolation
        val html1 = renderer.renderComposableRoot { App1() }
        val html2 = renderer.renderComposableRoot { App2() }

        // Verify each app renders correctly and independently
        assertTrue(html1.contains("Application 1: app1"), "First app should render correctly")
        assertTrue(html2.contains("Application 2: app2"), "Second app should render correctly")

        // Verify isolation - each app should not contain the other's content
        assertFalse(html1.contains("Application 2"), "First app should not contain second app content")
        assertFalse(html2.contains("Application 1"), "Second app should not contain first app content")
    }

    @Test
    fun testSSRPerformance() {
        val renderer = PlatformRenderer()

        @Composable
        fun PerformanceTestApp() {
            Column(modifier = Modifier()) {
                // Create multiple components to test performance
                repeat(10) { index ->
                    Row(modifier = Modifier()) {
                        Text("Item $index", modifier = Modifier())
                        Button(
                            onClick = { println("Clicked $index") },
                            label = "Action $index",
                            modifier = Modifier()
                        )
                    }
                }
            }
        }

        // Test multiple renders to verify no memory leaks or performance degradation
        val startTime = System.currentTimeMillis()

        repeat(5) { iteration ->
            val html = renderer.renderComposableRoot {
                PerformanceTestApp()
            }

            // Verify content is correct for each iteration
            assertTrue(html.contains("Item 0"), "Should contain first item on iteration $iteration")
            assertTrue(html.contains("Item 9"), "Should contain last item on iteration $iteration")
            assertTrue(html.contains("Action 0"), "Should contain first button on iteration $iteration")
            assertTrue(html.contains("Action 9"), "Should contain last button on iteration $iteration")
        }

        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime

        println("SSR Performance: 5 renders with 10 components each took ${totalTime}ms")

        // Verify reasonable performance (should complete within 5 seconds for this simple test)
        assertTrue(totalTime < 5000, "SSR should complete within reasonable time")
    }

    @Test
    fun testSSRErrorHandling() {
        val renderer = PlatformRenderer()

        @Composable
        fun ProblematicApp() {
            // This should work fine with proper composition context
            val state = remember { mutableStateOf("working") }
            Text("State: ${state.value}", modifier = Modifier())
        }

        // This should not throw exceptions anymore
        val html = renderer.renderComposableRoot {
            ProblematicApp()
        }

        assertTrue(html.contains("State: working"), "Should handle composition correctly")
        assertFalse(
            html.contains("Exception") || html.contains("Error"),
            "Should not contain error messages"
        )
    }
}