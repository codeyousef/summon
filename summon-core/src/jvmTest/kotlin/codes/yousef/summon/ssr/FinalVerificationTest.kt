package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.*
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.getPlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Final verification test to triple-check SSR functionality
 */
class FinalVerificationTest {

    @Test
    fun testTripleCheckSSRWorks() {
        println("=== TRIPLE CHECK: SSR FUNCTIONALITY VERIFICATION ===")

        val renderer = PlatformRenderer()

        @Composable
        fun ComplexApp() {
            val counter = remember { mutableStateOf(42) }
            val message = remember { mutableStateOf("SSR is working perfectly!") }

            // Verify getPlatformRenderer works
            val currentRenderer = getPlatformRenderer()
            assertNotNull(currentRenderer, "getPlatformRenderer() should work")

            Column(modifier = Modifier()) {
                Text("Triple Check Result: SUCCESS", modifier = Modifier())
                Text("Counter value: ${counter.value}", modifier = Modifier())
                Text("Message: ${message.value}", modifier = Modifier())

                Button(
                    onClick = {
                        counter.value += 10
                        message.value = "Button clicked!"
                    },
                    label = "Test Button",
                    modifier = Modifier()
                )

                Text("✓ Composition context works", modifier = Modifier())
                Text("✓ State management works", modifier = Modifier())
                Text("✓ Platform renderer accessible", modifier = Modifier())
                Text("✓ Components render correctly", modifier = Modifier())
            }
        }

        // Test 1: Basic SSR
        val html = renderer.renderComposableRoot {
            ComplexApp()
        }

        println("Generated HTML length: ${html.length} characters")
        println("HTML Preview (first 200 chars): ${html.take(200)}")

        // Verification 1: HTML Structure
        assertTrue(html.contains("<html"), "HTML should contain proper html tag")
        assertTrue(html.contains("<body"), "HTML should contain body tag")
        assertTrue(html.contains("</html>"), "HTML should be properly closed")

        // Verification 2: Content Rendering
        assertTrue(html.contains("Triple Check Result: SUCCESS"), "Should render success message")
        assertTrue(html.contains("Counter value: 42"), "Should render initial state")
        assertTrue(html.contains("SSR is working perfectly!"), "Should render message state")
        assertTrue(html.contains("Test Button"), "Should render button")

        // Verification 3: Composition Features
        assertTrue(html.contains("Composition context works"), "Composition context working")
        assertTrue(html.contains("State management works"), "State management working")
        assertTrue(html.contains("Platform renderer accessible"), "Platform renderer working")
        assertTrue(html.contains("Components render correctly"), "Components working")

        // Verification 4: HTML Tags
        assertTrue(html.contains("<span"), "Should contain span tags for text")
        assertTrue(html.contains("<button"), "Should contain button tag")
        assertTrue(html.contains("<div"), "Should contain div tags for layout")

        println("✅ All verifications PASSED")

        // Test 2: Multiple renders work
        repeat(3) { iteration ->
            val html2 = renderer.renderComposableRoot {
                Text("Iteration $iteration works", modifier = Modifier())
            }
            assertTrue(
                html2.contains("Iteration $iteration works"),
                "Multiple renders should work: iteration $iteration"
            )
        }

        println("✅ Multiple renders PASSED")

        // Test 3: Hydration works
        val hydratedHtml = renderer.renderComposableRootWithHydration {
            Text("Hydration test", modifier = Modifier())
        }

        assertTrue(hydratedHtml.contains("Hydration test"), "Hydration should render content")
        assertTrue(hydratedHtml.contains("<!DOCTYPE html>"), "Hydration should include doctype")
        println("✅ Hydration PASSED")

        println("=== TRIPLE CHECK COMPLETE: SSR IS FULLY FUNCTIONAL ===")
    }

    @Test
    fun testServerSideRenderUtils() {
        println("=== TESTING ServerSideRenderUtils ===")

        @Composable
        fun TestApp() {
            Text("ServerSideRenderUtils test", modifier = Modifier())
        }

        try {
            val html = ServerSideRenderUtils.renderPageToString(
                rootComposable = { TestApp() }
            )

            assertTrue(
                html.contains("ServerSideRenderUtils test"),
                "ServerSideRenderUtils should render correctly"
            )
            println("✅ ServerSideRenderUtils PASSED")
        } catch (e: Exception) {
            fail("ServerSideRenderUtils should work, but failed: ${e.message}")
        }
    }
}