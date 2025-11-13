package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable
import codes.yousef.summon.modifier.Modifier
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

/**
 * Tests for the PlatformRendererCompositionLocal
 */
class PlatformRendererCompositionLocalTest {

    @Test
    fun testLocalPlatformRenderer() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Provide a value to the CompositionLocal
        val newProvider = LocalPlatformRenderer.provides(mockRenderer)

        // Get the value from the CompositionLocal
        val retrievedRenderer = newProvider.current

        // Verify it's the same instance
        assertSame(mockRenderer, retrievedRenderer, "LocalPlatformRenderer should return the provided renderer")

        // Test that we can use the retrieved renderer
        retrievedRenderer.renderText("Test", Modifier())
    }

    @Test
    fun testGetCurrentRenderer() {
        // Create a mock renderer
        val mockRenderer = MockPlatformRenderer()

        // Test that calling getCurrentRenderer without providing a value throws an exception
        assertFailsWith<codes.yousef.summon.core.error.ComponentNotFoundException> {
            getCurrentRenderer()
        }

        // We need to use provideComposer to set up a composition context
        // and then provide a value to the CompositionLocal
        CompositionLocal.provideComposer(MockComposer()) {
            // Provide a value to the CompositionLocal
            val newProvider = LocalPlatformRenderer.provides(mockRenderer)

            // Get the renderer using getCurrentRenderer
            val retrievedRenderer = getCurrentRenderer()

            // Verify it's the same instance
            assertSame(mockRenderer, retrievedRenderer, "getCurrentRenderer should return the provided renderer")

            // Test that we can use the retrieved renderer
            retrievedRenderer.renderText("Hello", Modifier())
        }
    }

    // Mock implementation of Composer for testing
    private class MockComposer : Composer {
        override val inserting: Boolean = false

        override fun startNode() {}
        override fun startGroup(key: Any?) {}
        override fun endNode() {}
        override fun endGroup() {}
        override fun changed(value: Any?): Boolean = true
        override fun updateValue(value: Any?) {}
        override fun nextSlot() {}
        override fun getSlot(): Any? = null
        override fun setSlot(value: Any?) {}
        override fun recordRead(state: Any) {}
        override fun recordWrite(state: Any) {}
        override fun reportChanged() {}
        override fun registerDisposable(disposable: () -> Unit) {}
        override fun dispose() {}
        override fun startCompose() {}
        override fun endCompose() {}
        override fun <T> compose(composable: @Composable () -> T): T {
            throw UnsupportedOperationException("Not implemented in mock")
        }

        override fun recompose() {
            // Mock implementation
        }

        override fun rememberedValue(key: Any): Any? {
            return null
        }

        override fun updateRememberedValue(key: Any, value: Any?) {
            // Mock implementation
        }
    }
}
