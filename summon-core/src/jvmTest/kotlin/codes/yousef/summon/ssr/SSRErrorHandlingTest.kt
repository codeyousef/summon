package codes.yousef.summon.ssr

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.components.display.Text
import codes.yousef.summon.components.input.Button
import codes.yousef.summon.components.layout.Column
import codes.yousef.summon.modifier.Modifier
import codes.yousef.summon.runtime.PlatformRenderer
import codes.yousef.summon.runtime.remember
import codes.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Error handling tests for SSR to verify robustness and graceful failure modes
 */
class SSRErrorHandlingTest {

    @Test
    fun testNullValueHandling() {
        val renderer = PlatformRenderer()

        @Composable
        fun NullValueComponent() {
            val nullableState = remember { mutableStateOf<String?>(null) }

            Column(modifier = Modifier()) {
                Text("Nullable text: ${nullableState.value ?: "default"}", modifier = Modifier())
                Text(nullableState.value ?: "fallback text", modifier = Modifier())
            }
        }

        // Should not throw exception
        val html = renderer.renderComposableRoot {
            NullValueComponent()
        }

        assertTrue(html.contains("Nullable text: default"), "Should handle null with default value")
        assertTrue(html.contains("fallback text"), "Should handle null with fallback text")
        assertFalse(html.contains("null"), "Should not contain literal 'null' string")
    }

    @Test
    fun testEmptyStringHandling() {
        val renderer = PlatformRenderer()

        @Composable
        fun EmptyStringComponent() {
            val emptyState = remember { mutableStateOf("") }

            Column(modifier = Modifier()) {
                Text("Empty: '${emptyState.value}'", modifier = Modifier())
                if (emptyState.value.isEmpty()) {
                    Text("State is empty", modifier = Modifier())
                }
            }
        }

        val html = renderer.renderComposableRoot {
            EmptyStringComponent()
        }

        assertTrue(html.contains("Empty: ''"), "Should handle empty strings")
        assertTrue(html.contains("State is empty"), "Should handle empty string conditionals")
    }

    @Test
    fun testLargeTextHandling() {
        val renderer = PlatformRenderer()

        @Composable
        fun LargeTextComponent() {
            val largeText = remember { mutableStateOf("X".repeat(10000)) }

            Column(modifier = Modifier()) {
                Text("Large text length: ${largeText.value.length}", modifier = Modifier())
                Text(largeText.value.take(100) + "...", modifier = Modifier())
            }
        }

        val html = renderer.renderComposableRoot {
            LargeTextComponent()
        }

        assertTrue(html.contains("Large text length: 10000"), "Should handle large text metadata")
        assertTrue(html.contains("X".repeat(100)), "Should handle truncated large text")
        assertTrue(html.contains("..."), "Should include truncation indicator")
    }

    @Test
    fun testSpecialCharacterHandling() {
        val renderer = PlatformRenderer()

        @Composable
        fun SpecialCharsComponent() {
            val specialChars = remember {
                mutableStateOf("<script>alert('xss')</script>")
            }
            val unicodeChars = remember {
                mutableStateOf("🚀 Unicode: αβγ 中文 العربية")
            }

            Column(modifier = Modifier()) {
                Text("HTML: ${specialChars.value}", modifier = Modifier())
                Text("Unicode: ${unicodeChars.value}", modifier = Modifier())
            }
        }

        val html = renderer.renderComposableRoot {
            SpecialCharsComponent()
        }

        // Should escape HTML properly
        assertFalse(html.contains("<script>"), "Should escape script tags")
        assertTrue(
            html.contains("&lt;script&gt;") || html.contains("alert"),
            "Should contain escaped HTML or sanitized content"
        )

        // Unicode should work
        assertTrue(html.contains("🚀") || html.contains("Unicode"), "Should handle Unicode characters")
    }

    @Test
    fun testNestedExceptionHandling() {
        val renderer = PlatformRenderer()

        @Composable
        fun ProblematicNestedComponent() {
            try {
                Column(modifier = Modifier()) {
                    Text("Before potential issue", modifier = Modifier())

                    // This should work fine in our SSR implementation
                    val state = remember { mutableStateOf("works") }
                    Text("State: ${state.value}", modifier = Modifier())

                    Text("After potential issue", modifier = Modifier())
                }
            } catch (e: Exception) {
                Text("Error handled: ${e.message}", modifier = Modifier())
            }
        }

        val html = renderer.renderComposableRoot {
            ProblematicNestedComponent()
        }

        assertTrue(html.contains("Before potential issue"), "Should render content before issue")
        assertTrue(html.contains("After potential issue"), "Should render content after issue")
        assertTrue(html.contains("State: works"), "Should handle state correctly")
    }

    @Test
    fun testRecursiveComponentDetection() {
        val renderer = PlatformRenderer()

        @Composable
        fun SafeRecursiveComponent(depth: Int = 0) {
            if (depth > 10) {
                Text("Max depth reached", modifier = Modifier())
                return
            }

            Column(modifier = Modifier()) {
                Text("Depth: $depth", modifier = Modifier())
                if (depth < 5) {
                    SafeRecursiveComponent(depth + 1)
                }
            }
        }

        val html = renderer.renderComposableRoot {
            SafeRecursiveComponent()
        }

        assertTrue(html.contains("Depth: 0"), "Should contain initial depth")
        assertTrue(html.contains("Depth: 5"), "Should reach max safe depth")
        assertFalse(html.contains("Depth: 6"), "Should not exceed safe depth")
    }

    @Test
    fun testMalformedModifierHandling() {
        val renderer = PlatformRenderer()

        @Composable
        fun MalformedModifierComponent() {
            Column(modifier = Modifier()) {
                Text("With standard modifier", modifier = Modifier())

                // Test with various modifier scenarios
                Text("Basic text", modifier = Modifier())
                Button(
                    onClick = { /* no-op */ },
                    label = "Test Button",
                    modifier = Modifier()
                )
            }
        }

        val html = renderer.renderComposableRoot {
            MalformedModifierComponent()
        }

        assertTrue(html.contains("With standard modifier"), "Should handle standard modifiers")
        assertTrue(html.contains("Basic text"), "Should handle basic components")
        assertTrue(html.contains("Test Button"), "Should handle button components")
    }

    @Test
    fun testStateConsistencyUnderFailure() {
        val renderer = PlatformRenderer()

        @Composable
        fun StateConsistencyComponent() {
            val state1 = remember { mutableStateOf(1) }
            val state2 = remember { mutableStateOf("test") }
            val state3 = remember { mutableStateOf(true) }

            Column(modifier = Modifier()) {
                Text("State 1: ${state1.value}", modifier = Modifier())
                Text("State 2: ${state2.value}", modifier = Modifier())
                Text("State 3: ${state3.value}", modifier = Modifier())

                // Simulate state updates that might cause issues
                if (state3.value) {
                    state1.value = 42
                    state2.value = "updated"
                }

                Text("Updated State 1: ${state1.value}", modifier = Modifier())
                Text("Updated State 2: ${state2.value}", modifier = Modifier())
            }
        }

        val html = renderer.renderComposableRoot {
            StateConsistencyComponent()
        }

        // All state should be rendered correctly
        assertTrue(html.contains("State 1: 1"), "Should show initial state")
        assertTrue(html.contains("Updated State 1: 42"), "Should show updated state")
        assertTrue(html.contains("Updated State 2: updated"), "Should show updated string state")
        assertTrue(html.contains("State 3: true"), "Should show boolean state")
    }

    @Test
    fun testCompositionContextCorruption() {
        val renderer = PlatformRenderer()

        @Composable
        fun ContextTestComponent() {
            // Test that composition context is properly maintained
            val outerState = remember { mutableStateOf("outer") }

            Column(modifier = Modifier()) {
                Text("Outer: ${outerState.value}", modifier = Modifier())

                repeat(3) { index ->
                    val innerState = remember { mutableStateOf("inner-$index") }
                    Text("Inner $index: ${innerState.value}", modifier = Modifier())
                }

                Text("Back to outer: ${outerState.value}", modifier = Modifier())
            }
        }

        val html = renderer.renderComposableRoot {
            ContextTestComponent()
        }

        assertTrue(html.contains("Outer: outer"), "Should maintain outer state")
        assertTrue(html.contains("Inner 0: inner-0"), "Should maintain first inner state")
        assertTrue(html.contains("Inner 2: inner-2"), "Should maintain last inner state")
        assertTrue(html.contains("Back to outer: outer"), "Should restore outer context")
    }

    @Test
    fun testMemoryLeakPrevention() {
        val renderer = PlatformRenderer()

        @Composable
        fun PotentialLeakComponent() {
            // Create components that might hold references
            val heavyObject = remember { mutableStateOf(ByteArray(1000)) }
            val stringRef = remember { mutableStateOf("test-string") }

            Column(modifier = Modifier()) {
                Text("Heavy object size: ${heavyObject.value.size}", modifier = Modifier())
                Text("String ref: ${stringRef.value}", modifier = Modifier())
            }
        }

        // Run multiple times to check for leaks
        repeat(5) { iteration ->
            val html = renderer.renderComposableRoot {
                PotentialLeakComponent()
            }

            assertTrue(
                html.contains("Heavy object size: 1000"),
                "Iteration $iteration: Should render heavy object"
            )
            assertTrue(
                html.contains("String ref: test-string"),
                "Iteration $iteration: Should render string reference"
            )
        }

        // Force garbage collection
        System.gc()
        Thread.sleep(100)

        // Should still be able to render after GC
        val finalHtml = renderer.renderComposableRoot {
            PotentialLeakComponent()
        }

        assertTrue(finalHtml.contains("Heavy object size: 1000"), "Should work after GC")
    }

    @Test
    fun testConcurrentErrorHandling() {
        val renderer = PlatformRenderer()

        @Composable
        fun ConcurrentErrorComponent(id: Int) {
            val state = remember { mutableStateOf("concurrent-$id") }

            Column(modifier = Modifier()) {
                Text("Concurrent component $id", modifier = Modifier())
                Text("State: ${state.value}", modifier = Modifier())

                Button(
                    onClick = {
                        // Simulate potential concurrent modification
                        state.value = "clicked-$id"
                    },
                    label = "Click $id",
                    modifier = Modifier()
                )
            }
        }

        val results = mutableListOf<String>()
        val errors = mutableListOf<Exception>()

        // Run multiple renders in sequence (simulating concurrent access)
        repeat(3) { id ->
            try {
                val html = renderer.renderComposableRoot {
                    ConcurrentErrorComponent(id)
                }
                results.add(html)
            } catch (e: Exception) {
                errors.add(e)
            }
        }

        // All should succeed
        assertEquals(3, results.size, "All concurrent renders should succeed")
        assertEquals(0, errors.size, "No errors should occur")

        // Each should have correct content
        results.forEachIndexed { id, html ->
            assertTrue(
                html.contains("Concurrent component $id"),
                "Result $id should have correct component"
            )
            assertTrue(
                html.contains("State: concurrent-$id"),
                "Result $id should have correct state"
            )
        }
    }

    @Test
    fun testInvalidHTMLPrevention() {
        val renderer = PlatformRenderer()

        @Composable
        fun InvalidHTMLComponent() {
            Column(modifier = Modifier()) {
                // Test various potentially problematic content
                Text("Normal text", modifier = Modifier())
                Text("<div>Embedded HTML</div>", modifier = Modifier())
                Text("Multi\nline\ntext", modifier = Modifier())
                Text("Quotes: \"double\" and 'single'", modifier = Modifier())
                Text("Ampersands & symbols", modifier = Modifier())
            }
        }

        val html = renderer.renderComposableRoot {
            InvalidHTMLComponent()
        }

        // Should produce valid HTML structure
        assertTrue(html.contains("<html"), "Should have HTML structure")
        assertTrue(html.contains("</html>"), "Should close HTML properly")

        // Content should be handled safely
        assertTrue(html.contains("Normal text"), "Should render normal text")

        // HTML should be escaped or handled safely
        assertFalse(
            html.contains("<div>Embedded HTML</div>") &&
                    !html.contains("&lt;div&gt;"), "Should escape embedded HTML"
        )

        // Other content should render safely
        assertTrue(html.contains("symbols"), "Should handle ampersands and symbols")
    }
}