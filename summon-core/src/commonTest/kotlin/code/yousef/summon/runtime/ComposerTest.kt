package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the Composer interface using a mock implementation.
 */
class ComposerTest {
    
    /**
     * A mock implementation of the Composer interface for testing.
     */
    private class MockComposer : Composer {
        override var inserting: Boolean = true
        
        private val slots = mutableListOf<Any?>()
        private var currentSlotIndex = 0
        
        private val trackedReads = mutableSetOf<Any>()
        private val trackedWrites = mutableSetOf<Any>()
        
        private val disposables = mutableListOf<() -> Unit>()
        
        private var nodeCount = 0
        private var groupCount = 0
        
        private var lastValue: Any? = null
        private var changeReported = false
        
        override fun startNode() {
            nodeCount++
        }
        
        override fun startGroup(key: Any?) {
            groupCount++
        }
        
        override fun endNode() {
            nodeCount--
        }
        
        override fun endGroup() {
            groupCount--
        }
        
        override fun changed(value: Any?): Boolean {
            val hasChanged = value != lastValue
            lastValue = value
            return hasChanged
        }
        
        override fun updateValue(value: Any?) {
            lastValue = value
        }
        
        override fun nextSlot() {
            currentSlotIndex++
            if (currentSlotIndex >= slots.size) {
                slots.add(null)
            }
        }
        
        override fun getSlot(): Any? {
            if (currentSlotIndex >= slots.size) {
                slots.add(null)
            }
            return slots[currentSlotIndex]
        }
        
        override fun setSlot(value: Any?) {
            if (currentSlotIndex >= slots.size) {
                slots.add(value)
            } else {
                slots[currentSlotIndex] = value
            }
        }
        
        override fun recordRead(state: Any) {
            trackedReads.add(state)
        }
        
        override fun recordWrite(state: Any) {
            trackedWrites.add(state)
        }
        
        override fun reportChanged() {
            changeReported = true
        }
        
        override fun registerDisposable(disposable: () -> Unit) {
            disposables.add(disposable)
        }
        
        override fun dispose() {
            disposables.forEach { it() }
            disposables.clear()
            slots.clear()
            currentSlotIndex = 0
            trackedReads.clear()
            trackedWrites.clear()
            nodeCount = 0
            groupCount = 0
            lastValue = null
            changeReported = false
        }
        
        override fun startCompose() {
            // No-op for mock
        }
        
        override fun endCompose() {
            // No-op for mock
        }
        
        override fun <T> compose(composable: @Composable () -> T): T {
            startCompose()
            val result = composable()
            endCompose()
            return result
        }
        
        override fun recompose() {
            // Mock implementation
        }
        
        override fun rememberedValue(key: Any): Any? {
            return slots.getOrNull(key.hashCode())
        }
        
        override fun updateRememberedValue(key: Any, value: Any?) {
            val index = key.hashCode()
            while (slots.size <= index) {
                slots.add(null)
            }
            slots[index] = value
        }
        
        // Helper methods for testing
        fun getNodeCount(): Int = nodeCount
        fun getGroupCount(): Int = groupCount
        fun getTrackedReads(): Set<Any> = trackedReads
        fun getTrackedWrites(): Set<Any> = trackedWrites
        fun wasChangeReported(): Boolean = changeReported
        fun getDisposables(): List<() -> Unit> = disposables
    }
    
    @Test
    fun testNodeManagement() {
        val composer = MockComposer()
        
        // Start with no nodes
        assertEquals(0, composer.getNodeCount(), "Initial node count should be 0")
        
        // Start a node
        composer.startNode()
        assertEquals(1, composer.getNodeCount(), "Node count should be 1 after starting a node")
        
        // Start another node
        composer.startNode()
        assertEquals(2, composer.getNodeCount(), "Node count should be 2 after starting another node")
        
        // End a node
        composer.endNode()
        assertEquals(1, composer.getNodeCount(), "Node count should be 1 after ending a node")
        
        // End the last node
        composer.endNode()
        assertEquals(0, composer.getNodeCount(), "Node count should be 0 after ending all nodes")
    }
    
    @Test
    fun testGroupManagement() {
        val composer = MockComposer()
        
        // Start with no groups
        assertEquals(0, composer.getGroupCount(), "Initial group count should be 0")
        
        // Start a group
        composer.startGroup()
        assertEquals(1, composer.getGroupCount(), "Group count should be 1 after starting a group")
        
        // Start a group with a key
        composer.startGroup("testKey")
        assertEquals(2, composer.getGroupCount(), "Group count should be 2 after starting another group")
        
        // End a group
        composer.endGroup()
        assertEquals(1, composer.getGroupCount(), "Group count should be 1 after ending a group")
        
        // End the last group
        composer.endGroup()
        assertEquals(0, composer.getGroupCount(), "Group count should be 0 after ending all groups")
    }
    
    @Test
    fun testSlotManagement() {
        val composer = MockComposer()
        
        // Initial slot should be null
        assertEquals(null, composer.getSlot(), "Initial slot should be null")
        
        // Set a value
        val testValue = "Test Value"
        composer.setSlot(testValue)
        assertEquals(testValue, composer.getSlot(), "Slot should contain the set value")
        
        // Move to next slot
        composer.nextSlot()
        assertEquals(null, composer.getSlot(), "New slot should be null")
        
        // Set another value
        val testValue2 = "Test Value 2"
        composer.setSlot(testValue2)
        assertEquals(testValue2, composer.getSlot(), "New slot should contain the new value")
    }
    
    @Test
    fun testValueTracking() {
        val composer = MockComposer()
        
        // Initial value change should be true
        assertTrue(composer.changed("initial"), "Initial value change should be true")
        
        // Same value should not be a change
        assertFalse(composer.changed("initial"), "Same value should not be a change")
        
        // Different value should be a change
        assertTrue(composer.changed("changed"), "Different value should be a change")
        
        // Update value directly
        composer.updateValue("updated")
        assertFalse(composer.changed("updated"), "After updateValue, same value should not be a change")
    }
    
    @Test
    fun testStateTracking() {
        val composer = MockComposer()
        val stateObj1 = "State 1"
        val stateObj2 = "State 2"
        
        // Record reads
        composer.recordRead(stateObj1)
        composer.recordRead(stateObj2)
        
        val reads = composer.getTrackedReads()
        assertTrue(stateObj1 in reads, "State object 1 should be in tracked reads")
        assertTrue(stateObj2 in reads, "State object 2 should be in tracked reads")
        
        // Record writes
        composer.recordWrite(stateObj1)
        
        val writes = composer.getTrackedWrites()
        assertTrue(stateObj1 in writes, "State object 1 should be in tracked writes")
        assertFalse(stateObj2 in writes, "State object 2 should not be in tracked writes")
        
        // Report changes
        assertFalse(composer.wasChangeReported(), "Change should not be reported initially")
        composer.reportChanged()
        assertTrue(composer.wasChangeReported(), "Change should be reported after reportChanged")
    }
    
    @Test
    fun testDisposables() {
        val composer = MockComposer()
        var disposableCalled = false
        
        // Register a disposable
        composer.registerDisposable { disposableCalled = true }
        
        // Disposable should not be called yet
        assertFalse(disposableCalled, "Disposable should not be called before dispose")
        
        // Dispose should call the disposable
        composer.dispose()
        assertTrue(disposableCalled, "Disposable should be called after dispose")
        
        // Disposables list should be empty after dispose
        assertEquals(0, composer.getDisposables().size, "Disposables list should be empty after dispose")
    }
    
    @Test
    fun testCompose() {
        val composer = MockComposer()
        
        // Test compose with a simple lambda
        val result = composer.compose {
            "Composed Value"
        }
        
        assertEquals("Composed Value", result, "compose should return the result of the composable")
        
        // Test compose with a more complex function
        @Composable
        fun complexComposable(): String {
            composer.startNode()
            composer.setSlot("Complex")
            composer.endNode()
            return "Complex Result"
        }
        
        val complexResult = composer.compose {
            complexComposable()
        }
        
        assertEquals("Complex Result", complexResult, "compose should work with complex composables")
    }
}