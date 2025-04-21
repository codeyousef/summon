package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EffectsTest {

    // A test-specific implementation of Composer that tracks effects
    private class TestComposer : Composer {
        override val inserting: Boolean = true

        private val slots = mutableMapOf<Int, Any?>()
        private var currentSlot = 0
        private val disposables = mutableListOf<() -> Unit>()

        // Track if methods were called
        var nextSlotCalled = false
        var setSlotCalled = false
        var registerDisposableCalled = false
        var disposeCalled = false

        override fun startNode() {}

        override fun startGroup(key: Any?) {}

        override fun endNode() {}

        override fun endGroup() {}

        override fun changed(value: Any?): Boolean = true

        override fun updateValue(value: Any?) {}

        override fun nextSlot() {
            nextSlotCalled = true
            currentSlot++
        }

        override fun getSlot(): Any? = slots[currentSlot]

        override fun setSlot(value: Any?) {
            setSlotCalled = true
            slots[currentSlot] = value
        }

        override fun recordRead(state: Any) {}

        override fun recordWrite(state: Any) {}

        override fun reportChanged() {}

        override fun registerDisposable(disposable: () -> Unit) {
            registerDisposableCalled = true
            disposables.add(disposable)
        }

        override fun dispose() {
            disposeCalled = true
            disposables.forEach { it() }
            disposables.clear()
        }

        override fun startCompose() {}

        override fun endCompose() {}

        override fun <T> compose(composable: @Composable () -> T): T {
            throw UnsupportedOperationException("Not implemented in test")
        }

        // Helper methods for testing
        fun getDisposablesCount(): Int = disposables.size

        fun executeDisposables() {
            disposables.forEach { it() }
        }
    }

    @Test
    fun testLaunchedEffect() {
        val testComposer = TestComposer()

        // Set up the test composer
        CompositionLocal.provideComposer(testComposer) {
            // Call LaunchedEffect
            LaunchedEffect {
                // This would normally be a suspend function
            }

            // Verify that the composer methods were called
            assertTrue(testComposer.nextSlotCalled, "nextSlot should be called")
            assertTrue(testComposer.setSlotCalled, "setSlot should be called")
            assertTrue(testComposer.registerDisposableCalled, "registerDisposable should be called")

            // Verify that a disposable was registered
            assertEquals(1, testComposer.getDisposablesCount(), "One disposable should be registered")
        }
    }

    @Test
    fun testDisposableEffect() {
        val testComposer = TestComposer()

        // Set up the test composer
        CompositionLocal.provideComposer(testComposer) {
            // Track if cleanup was called
            var cleanupCalled = false

            // Call DisposableEffect
            DisposableEffect {
                // Return cleanup function
                { cleanupCalled = true }
            }

            // Verify that the composer methods were called
            assertTrue(testComposer.nextSlotCalled, "nextSlot should be called")
            assertTrue(testComposer.setSlotCalled, "setSlot should be called")
            assertTrue(testComposer.registerDisposableCalled, "registerDisposable should be called")

            // Verify that a disposable was registered
            assertEquals(1, testComposer.getDisposablesCount(), "One disposable should be registered")

            // Execute the disposables to trigger cleanup
            testComposer.executeDisposables()

            // Verify cleanup was called
            assertTrue(cleanupCalled, "Cleanup function should be called on dispose")
        }
    }

    @Test
    fun testSideEffect() {
        val testComposer = TestComposer()

        // Set up the test composer
        CompositionLocal.provideComposer(testComposer) {
            // Track if effect was called
            var effectCalled = false

            // Call SideEffect
            SideEffect {
                effectCalled = true
            }

            // Verify effect was called immediately
            assertTrue(effectCalled, "SideEffect should be called immediately")
        }
    }

    @Test
    fun testLaunchedEffectWithKey() {
        val testComposer = TestComposer()

        // Set up the test composer
        CompositionLocal.provideComposer(testComposer) {
            // Call LaunchedEffect with a key
            LaunchedEffect(key = "testKey") {
                // This would normally be a suspend function
            }

            // Verify that the composer methods were called
            assertTrue(testComposer.nextSlotCalled, "nextSlot should be called")
            assertTrue(testComposer.setSlotCalled, "setSlot should be called")
            assertTrue(testComposer.registerDisposableCalled, "registerDisposable should be called")

            // Verify that a disposable was registered
            assertEquals(1, testComposer.getDisposablesCount(), "One disposable should be registered")
        }
    }
}
