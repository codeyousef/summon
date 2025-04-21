package code.yousef.summon.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import code.yousef.summon.annotation.Composable

class CompositionContextTest {

    /**
     * A mock implementation of the Composer interface for testing.
     */
    private class MockComposer : Composer {
        override var inserting: Boolean = true

        var nodeStarted = false
        var nodeEnded = false
        var disposeCalled = false

        override fun startNode() {
            nodeStarted = true
        }

        override fun endNode() {
            nodeEnded = true
        }

        override fun startGroup(key: Any?) {
            // Not needed for these tests
        }

        override fun endGroup() {
            // Not needed for these tests
        }

        override fun changed(value: Any?): Boolean {
            return false
        }

        override fun updateValue(value: Any?) {
            // Not needed for these tests
        }

        override fun nextSlot() {
            // Not needed for these tests
        }

        override fun getSlot(): Any? {
            return null
        }

        override fun setSlot(value: Any?) {
            // Not needed for these tests
        }

        override fun recordRead(state: Any) {
            // Not needed for these tests
        }

        override fun recordWrite(state: Any) {
            // Not needed for these tests
        }

        override fun reportChanged() {
            // Not needed for these tests
        }

        override fun registerDisposable(disposable: () -> Unit) {
            // Not needed for these tests
        }

        override fun dispose() {
            disposeCalled = true
        }

        override fun startCompose() {
            // Not needed for these tests
        }

        override fun endCompose() {
            // Not needed for these tests
        }

        override fun <T> compose(composable: @Composable () -> T): T {
            startCompose()
            val result = composable()
            endCompose()
            return result
        }
    }

    @Test
    fun testCreateComposition() {
        var composeCalled = false

        val context = CompositionContextImpl.createComposition {
            composeCalled = true
        }

        assertNotNull(context, "Context should not be null")
        assertEquals(true, composeCalled, "Compose function should be called")
    }

    @Test
    fun testCompose() {
        var composeCalled = false

        // Create a test composition context
        val context = CompositionContextImpl.createComposition {}

        // Save the current composer to restore it later
        val originalComposer = CompositionLocal.currentComposer

        try {
            // Test the compose method
            context.compose {
                composeCalled = true
                assertNotNull(CompositionLocal.currentComposer, 
                    "Current composer should be set during composition")
            }

            assertEquals(true, composeCalled, "Content should be executed")
            assertNull(CompositionLocal.currentComposer, 
                "Current composer should be reset after composition")
        } finally {
            // Restore the original composer
            CompositionLocal.setCurrentComposer(originalComposer)
        }
    }

    @Test
    fun testDispose() {
        // Create a test composition context
        val context = CompositionContextImpl.createComposition {}

        // Save the current composer to restore it later
        val originalComposer = CompositionLocal.currentComposer

        try {
            // Test the dispose method
            context.dispose()

            assertNull(CompositionLocal.currentComposer, 
                "Current composer should be reset after dispose")
        } finally {
            // Restore the original composer
            CompositionLocal.setCurrentComposer(originalComposer)
        }
    }
}
