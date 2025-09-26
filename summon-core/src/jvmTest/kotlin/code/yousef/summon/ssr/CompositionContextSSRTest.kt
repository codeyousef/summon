package code.yousef.summon.ssr

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.*
import code.yousef.summon.state.mutableStateOf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests specifically for composition context during SSR.
 * These tests verify that the composition framework (Composer, CompositionLocal, etc.)
 * works correctly during server-side rendering.
 */
class CompositionContextSSRTest {

    @Test
    fun testComposerContextDuringSSR() {
        // This should fail initially because no composer context is established during SSR
        val renderer = PlatformRenderer()
        
        @Composable
        fun ComposerAccessComponent() {
            // This should work - accessing the current composer during composition
            val composer = CompositionLocal.currentComposer
            assertNotNull(composer, "Composer should be available during SSR composition")

            @Suppress("SENSELESS_COMPARISON")
            Text("Composer available: ${composer != null}", modifier = Modifier())
        }
        
        val html = renderer.renderComposableRoot {
            ComposerAccessComponent()
        }
        
        assertTrue(html.contains("Composer available: true"), "Should have access to composer during SSR")
    }

    @Test
    fun testCompositionLocalProviders() {
        val renderer = PlatformRenderer()
        
        // Create a test CompositionLocal
        val TestLocal = CompositionLocal.compositionLocalOf("default_value")
        
        @Composable
        fun ComponentWithLocal() {
            val value = TestLocal.current
            Text("Local value: $value", modifier = Modifier())
        }
        
        @Composable
        fun AppWithProvider() {
            TestLocal.provides("provided_value")
            ComponentWithLocal()
        }
        
        val html = renderer.renderComposableRoot {
            AppWithProvider()
        }
        
        // This should work with proper composition context
        assertTrue(html.contains("Local value: provided_value"), 
            "CompositionLocal should work during SSR")
    }

    @Test
    fun testPlatformRendererCompositionLocal() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun ComponentThatUsesRenderer() {
            // Access renderer through CompositionLocal
            val currentRenderer = LocalPlatformRenderer.current
            assertNotNull(currentRenderer, "PlatformRenderer should be available via CompositionLocal")
            
            Text("Using renderer: ${currentRenderer::class.simpleName}", modifier = Modifier())
        }
        
        val html = renderer.renderComposableRoot {
            ComponentThatUsesRenderer()
        }
        
        assertTrue(html.contains("Using renderer:"), "Should be able to access renderer via CompositionLocal")
    }

    @Test
    fun testRememberDuringSSR() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun ComponentWithRemember() {
            val rememberedValue = remember { "server_remembered_value" }
            Text("Remembered: $rememberedValue", modifier = Modifier())
        }
        
        val html = renderer.renderComposableRoot {
            ComponentWithRemember()
        }
        
        assertTrue(html.contains("Remembered: server_remembered_value"), 
            "remember() should work during SSR")
    }

    @Test
    fun testMutableStateDuringSSR() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun ComponentWithState() {
            val state = remember { mutableStateOf("initial_value") }
            
            // Modify state during composition (this happens during SSR)
            state.value = "ssr_modified_value"
            
            Text("State: ${state.value}", modifier = Modifier())
        }
        
        val html = renderer.renderComposableRoot {
            ComponentWithState()
        }
        
        assertTrue(html.contains("State: ssr_modified_value"), 
            "mutableStateOf should work during SSR")
    }

    @Test
    fun testNestedCompositionContext() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun NestedComponent() {
            // Verify we still have composer context in nested components
            val composer = CompositionLocal.currentComposer
            assertNotNull(composer, "Composer should be available in nested components")
            
            Text("Nested component with composer", modifier = Modifier())
        }
        
        @Composable
        fun ParentComponent() {
            val composer = CompositionLocal.currentComposer
            assertNotNull(composer, "Composer should be available in parent component")
            
            Text("Parent component", modifier = Modifier())
            NestedComponent()
        }
        
        val html = renderer.renderComposableRoot {
            ParentComponent()
        }
        
        assertTrue(html.contains("Parent component"), "Should render parent component")
        assertTrue(html.contains("Nested component"), "Should render nested component")
    }

    @Test
    fun testCompositionContextCleanup() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun TestComponent() {
            Text("Test", modifier = Modifier())
        }
        
        // Render multiple times to test cleanup
        repeat(3) { iteration ->
            val html = renderer.renderComposableRoot {
                TestComponent()
            }
            
            assertTrue(html.contains("Test"), "Should render correctly on iteration $iteration")
            
            // Verify composition context is properly cleaned up between renders
            // (no lingering state from previous renders)
        }
    }

    @Test
    fun testComposerSlotManagement() {
        val renderer = PlatformRenderer()
        
        @Composable
        fun ComponentWithSlots() {
            // These operations require proper slot management in the composer
            val value1 = remember { "slot1" }
            val value2 = remember { "slot2" }
            val state = remember { mutableStateOf("state_slot") }
            
            Text("Values: $value1, $value2, ${state.value}", modifier = Modifier())
        }
        
        val html = renderer.renderComposableRoot {
            ComponentWithSlots()
        }
        
        assertTrue(html.contains("Values: slot1, slot2, state_slot"), 
            "Composer slot management should work during SSR")
    }

    @Test
    fun testCompositionRecomposerIntegration() {
        // Test that the composition integrates properly with the Recomposer
        val renderer = PlatformRenderer()
        
        @Composable
        fun ComponentWithRecomposer() {
            // Access the recomposer to verify integration
            val recomposer = RecomposerHolder.recomposer
            assertNotNull(recomposer, "Should have access to recomposer")
            
            Text("Recomposer available", modifier = Modifier())
        }
        
        val html = renderer.renderComposableRoot {
            ComponentWithRecomposer()
        }
        
        assertTrue(html.contains("Recomposer available"), "Should integrate with recomposer")
    }

    @Test
    fun testMultipleCompositionsIsolated() {
        // Test that multiple SSR operations don't interfere with each other
        val renderer = PlatformRenderer()
        
        @Composable
        fun TestComponent(id: String) {
            val state = remember { mutableStateOf(id) }
            Text("ID: ${state.value}", modifier = Modifier())
        }
        
        // Render multiple compositions
        val html1 = renderer.renderComposableRoot {
            TestComponent("first")
        }
        
        val html2 = renderer.renderComposableRoot {
            TestComponent("second")
        }
        
        // Verify isolation
        assertTrue(html1.contains("ID: first"), "First composition should contain 'first'")
        assertTrue(html2.contains("ID: second"), "Second composition should contain 'second'")
        assertFalse(html1.contains("second"), "First composition should not contain 'second'")
        assertFalse(html2.contains("first"), "Second composition should not contain 'first'")
    }
}