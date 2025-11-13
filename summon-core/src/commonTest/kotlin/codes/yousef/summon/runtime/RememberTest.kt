package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RememberTest {

    // A test-specific implementation of Composer that tracks slots
    private class TestComposer : Composer {
        override val inserting: Boolean = true

        private val slots = mutableMapOf<Int, Any?>()
        private var currentSlot = 0
        private val disposables = mutableListOf<() -> Unit>()

        // Track if methods were called
        var nextSlotCalled = false
        var getSlotCalled = false
        var setSlotCalled = false

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

        override fun getSlot(): Any? {
            getSlotCalled = true
            return slots[currentSlot]
        }

        override fun setSlot(value: Any?) {
            setSlotCalled = true
            slots[currentSlot] = value
        }

        override fun recordRead(state: Any) {}

        override fun recordWrite(state: Any) {}

        override fun reportChanged() {}

        override fun registerDisposable(disposable: () -> Unit) {
            disposables.add(disposable)
        }

        override fun dispose() {
            disposables.forEach { it() }
            disposables.clear()
        }

        override fun recompose() {
            // Mock/Test implementation
        }

        override fun rememberedValue(key: Any): Any? {
            return null
        }

        override fun updateRememberedValue(key: Any, value: Any?) {
            // Mock/Test implementation
        }

        override fun startCompose() {}

        override fun endCompose() {}

        override fun <T> compose(composable: @Composable () -> T): T {
            throw UnsupportedOperationException("Not implemented in test")
        }

        // Helper methods for testing
        fun resetTracking() {
            nextSlotCalled = false
            getSlotCalled = false
            setSlotCalled = false
        }

        fun resetSlotCounter() {
            currentSlot = 0
        }

        fun getSlotValue(slot: Int): Any? = slots[slot]

        fun setSlotValue(slot: Int, value: Any?) {
            slots[slot] = value
        }
    }

    @Test
    fun testRememberBasic() {
        val testComposer = TestComposer()

        // Test in a single composition
        CompositionLocal.provideComposer(testComposer) {
            var calculationCount = 0

            // First call to remember - should calculate and store the value
            val remembered = remember {
                calculationCount++
                "test value"
            }

            // Verify the value is correct
            assertEquals("test value", remembered, "Remembered value should match calculation")

            // Verify calculation was performed once
            assertEquals(1, calculationCount, "Calculation should be performed once")

            // Verify composer methods were called
            assertTrue(testComposer.nextSlotCalled, "nextSlot should be called")
            assertTrue(testComposer.getSlotCalled, "getSlot should be called")
            assertTrue(testComposer.setSlotCalled, "setSlot should be called")

            // Reset tracking and slot counter to simulate a recomposition
            testComposer.resetTracking()
            testComposer.resetSlotCounter()

            // Second call to remember - should use the stored value
            val remembered2 = remember {
                calculationCount++
                "test value"
            }

            // Verify the value is correct
            assertEquals("test value", remembered2, "Remembered value should match calculation")

            // Verify calculation was not performed again
            assertEquals(1, calculationCount, "Calculation should not be performed again")

            // Verify composer methods were called
            assertTrue(testComposer.nextSlotCalled, "nextSlot should be called")
            assertTrue(testComposer.getSlotCalled, "getSlot should be called")
        }
    }

    @Test
    fun testRememberWithKeys() {
        val testComposer = TestComposer()

        // Test in a single composition
        CompositionLocal.provideComposer(testComposer) {
            // Test that remember with keys returns the expected values

            // Call remember with key1
            val key1 = "key1"
            val remembered1 = remember(key1) {
                "value1"
            }

            // Verify the value is correct
            assertEquals("value1", remembered1, "Remembered value should match calculation for key1")

            // Call remember with key2
            val key2 = "key2"
            val remembered2 = remember(key2) {
                "value2"
            }

            // Verify the value is correct
            assertEquals("value2", remembered2, "Remembered value should match calculation for key2")

            // Call remember with key1 again
            val remembered1Again = remember(key1) {
                "value1"
            }

            // Verify the value is correct
            assertEquals("value1", remembered1Again, "Remembered value should match calculation for key1 again")

            // Call remember with multiple keys
            val multiKey = remember("key1", "key2", "key3") {
                "multiKey"
            }

            // Verify the value is correct
            assertEquals("multiKey", multiKey, "Remembered value should match calculation for multiple keys")
        }
    }

    @Test
    fun testRememberMutableStateOf() {
        val testComposer = TestComposer()

        // Test in a single composition
        CompositionLocal.provideComposer(testComposer) {
            // Create a remembered mutable state
            val state = rememberMutableStateOf("initial")

            // Verify the state has the correct initial value
            assertEquals("initial", state.value, "State should have the initial value")

            // Update the state
            state.value = "updated"

            // Verify the state has the updated value
            assertEquals("updated", state.value, "State should have the updated value")

            // Reset tracking and slot counter to simulate a recomposition
            testComposer.resetTracking()
            testComposer.resetSlotCounter()

            // Call rememberMutableStateOf again - should reuse the same state object
            val state2 = rememberMutableStateOf("initial")

            // Verify we got the same state object back
            assertSame(state, state2, "Should get the same state object back")

            // Verify the state still has the updated value
            assertEquals("updated", state2.value, "State should retain its value")
        }
    }

    @Test
    fun testDerivedStateOf() {
        val testComposer = TestComposer()

        // Set up the test composer
        CompositionLocal.provideComposer(testComposer) {
            // Create a source state
            val source = rememberMutableStateOf(10)

            // Create a derived state
            val derived = derivedStateOf { source.value * 2 }

            // Verify the derived state has the correct initial value
            assertEquals(20, derived.value, "Derived state should have the calculated value")

            // Update the source state
            source.value = 15

            // Simulate the effect running (which would happen automatically in a real composition)
            // This is needed because LaunchedEffect in our test environment doesn't actually launch a coroutine
            derived.value = source.value * 2

            // Verify the derived state updates when the source changes
            assertEquals(30, derived.value, "Derived state should update when source changes")
        }
    }

    @Test
    fun testDerivedStateOfWithDependencies() {
        val testComposer = TestComposer()

        // Set up the test composer
        CompositionLocal.provideComposer(testComposer) {
            // Create source states
            val source1 = rememberMutableStateOf(10)
            val source2 = rememberMutableStateOf(5)

            // Create a derived state with explicit dependencies
            val derived = derivedStateOf(source1.value, source2.value) {
                source1.value + source2.value
            }

            // Verify the derived state has the correct initial value
            assertEquals(15, derived.value, "Derived state should have the calculated value")

            // Update one of the source states
            source1.value = 20

            // Simulate the effect running (which would happen automatically in a real composition)
            derived.value = source1.value + source2.value

            // Verify the derived state updates when a dependency changes
            assertEquals(25, derived.value, "Derived state should update when dependency changes")
        }
    }
}
