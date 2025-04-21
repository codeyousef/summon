package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertFailsWith

class CompositionLocalTest {

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
    }

    @Test
    fun testCompositionLocalWithDefaultValue() {
        // Create a composition local with a default value
        val defaultValue = "default"
        val compositionLocal = CompositionLocal.compositionLocalOf(defaultValue)

        // Check that the current value is the default value
        assertEquals(defaultValue, compositionLocal.current, "Current value should be the default value")

        // Provide a new value
        val newValue = "new value"
        val newProvider = compositionLocal.provides(newValue)

        // Check that the provider has the new value
        assertEquals(newValue, newProvider.current, "Provider should have the new value")

        // Check that the original provider reference also has the new value
        // since provides() modifies the provider in-place
        assertEquals(newValue, compositionLocal.current, "Original provider reference should have the new value")
    }

    @Test
    fun testStaticCompositionLocal() {
        // Create a static composition local
        val compositionLocal = CompositionLocal.staticCompositionLocalOf<String>()

        // Accessing current value without providing should throw
        assertFailsWith<IllegalStateException> {
            compositionLocal.current
        }

        // Provide a value
        val value = "provided value"
        val newProvider = compositionLocal.provides(value)

        // Check that the new provider has the provided value
        assertEquals(value, newProvider.current, "Provider should have the provided value")
    }

    @Test
    fun testLocalFlowContent() {
        // LocalFlowContent should be initialized with null
        assertNull(LocalFlowContent.current, "LocalFlowContent should initially be null")

        // Provide a new value (null in this case since we can't easily mock FlowContent)
        val newProvider = LocalFlowContent.provides(null)

        // Check that the new provider has the new value
        assertNull(newProvider.current, "New provider should have the provided value")
    }

    @Test
    fun testProvideComposer() {
        // Create mock composers
        val mockComposer1 = MockComposer()
        val mockComposer2 = MockComposer()

        // Use provideComposer to temporarily set a composer and execute a block
        var composerInsideBlock: Composer? = null
        val result = CompositionLocal.provideComposer(mockComposer1) {
            composerInsideBlock = CompositionLocal.currentComposer
            "test result"
        }

        // Check that the composer was set inside the block
        assertEquals(mockComposer1, composerInsideBlock, "Composer should be set inside provideComposer block")

        // Check that the result is correctly returned
        assertEquals("test result", result, "provideComposer should return the result of the block")

        // Check that the composer is reset after the block
        assertNull(CompositionLocal.currentComposer, "Composer should be reset after provideComposer block")
    }
}
