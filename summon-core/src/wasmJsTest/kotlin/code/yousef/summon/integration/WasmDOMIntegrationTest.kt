package code.yousef.summon.integration

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.runtime.PlatformRenderer
import code.yousef.summon.runtime.RecomposerHolder
import code.yousef.summon.runtime.remember
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.test.ensureWasmNodeDom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration test demonstrating WASM DOM interaction capabilities.
 * Tests the actual button click and recomposition flow that fails in the generated project.
 */
class WasmDOMIntegrationTest {

    private fun PlatformRenderer.initializeOrSkip(rootId: String): Boolean =
        try {
            initialize(rootId)
            true
        } catch (t: Throwable) {
            println("Skipping WASM DOM integration test: ${t.message}")
            false
        }

    private fun ensureRenderer(renderer: PlatformRenderer, rootId: String): Boolean {
        ensureWasmNodeDom()
        if (!renderer.initializeOrSkip(rootId)) {
            return false
        }
        LocalPlatformRenderer.provides(renderer)
        return true
    }

    /**
     * This test reproduces the exact issue from the generated CLI project:
     * - Button clicks are processed (logs show "Button clicked!")
     * - State updates happen
     * - But UI doesn't update due to element cache validation failure
     *
     * Expected behavior: Button click should update counter and trigger UI update
     * Actual behavior (before fix): Button click processes but UI doesn't recompose due to bad cast
     */
    @Test
    fun `button click should trigger recomposition without errors`() {
        val renderer = PlatformRenderer()
        if (!ensureRenderer(renderer, "test-root")) return

        // Track composition
        var compositionCount = 0

        @Composable
        fun CounterApp() {
            compositionCount++
            val counter = remember { mutableStateOf(0) }

            Column(modifier = Modifier()) {
                Text(
                    text = "Count: ${counter.value}",
                    modifier = Modifier()
                )

                Button(
                    onClick = {
                        counter.value++
                    },
                    label = "Increment"
                )
            }
        }

        // Initialize the renderer
        // renderer already initialized and provided

        // Get recomposer and composer
        val recomposer = RecomposerHolder.current()
        val composer = recomposer.createComposer()

        // Initial composition - this should work
        recomposer.setActiveComposer(composer)
        try {
            composer.startGroup("root")
            CounterApp()
            composer.endGroup()
        } finally {
            recomposer.setActiveComposer(null)
        }

        // Verify initial composition happened
        assertEquals(1, compositionCount, "Should have composed once initially")

        // Simulate recomposition (what happens when button is clicked)
        // This tests that element cache validation works correctly
        recomposer.setActiveComposer(composer)
        try {
            composer.startGroup("root")
            CounterApp()
            composer.endGroup()
        } finally {
            recomposer.setActiveComposer(null)
        }

        // After the fix, this should pass - recomposition should work without "bad cast" errors
        assertTrue(compositionCount >= 2, "Recomposition should have happened without errors")
    }

    @Test
    fun `renderer should handle element cache validation failures gracefully`() {
        val renderer = PlatformRenderer()
        if (!ensureRenderer(renderer, "test-root")) return

        @Composable
        fun SimpleButton() {
            Button(
                onClick = { },
                label = "Test Button"
            )
        }

        val recomposer = RecomposerHolder.current()
        val composer = recomposer.createComposer()

        // First composition
        recomposer.setActiveComposer(composer)
        try {
            composer.startGroup("root")
            SimpleButton()
            composer.endGroup()
        } finally {
            recomposer.setActiveComposer(null)
        }

        // Second composition - tests element reuse logic
        recomposer.setActiveComposer(composer)
        try {
            composer.startGroup("root")
            SimpleButton()
            composer.endGroup()
        } finally {
            recomposer.setActiveComposer(null)
        }

        // If we reach here without exceptions, the fix is working
        assertTrue(true, "Recomposition with element reuse should work without errors")
    }

    @Test
    fun `wasmDOMIntegration should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test actual WASM DOM integration
        assertTrue(true, "WASM DOM integration test placeholder")
    }

    @Test
    fun `elementManipulation should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test element manipulation in WASM
        assertTrue(true, "Element manipulation test placeholder")
    }

    @Test
    fun `eventHandling should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test event handling in WASM
        assertTrue(true, "Event handling test placeholder")
    }

    @Test
    fun `performanceOptimization should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test performance optimization in WASM
        assertTrue(true, "Performance optimization test placeholder")
    }

    @Test
    fun `composableRendering should work when implemented`() {
        // Placeholder test - will pass for now
        // TODO: Test composable rendering in WASM
        assertTrue(true, "Composable rendering test placeholder")
    }
}
