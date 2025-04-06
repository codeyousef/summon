package code.yousef.summon.runtime

import kotlinx.html.TagConsumer
import java.util.UUID

/**
 * JVM-specific implementation of the Composer interface.
 * This version works with kotlinx.html's TagConsumer for rendering.
 */
class JvmComposer(private val consumer: TagConsumer<*>) : Composer {
    override val inserting: Boolean = true
    
    private var currentIndex = 0
    private val slotTable = mutableMapOf<Int, Any?>()
    private val stateObservers = mutableMapOf<Any, MutableList<() -> Unit>>()
    
    override fun startNode() {
        // Start a new composition node
        // In a real implementation, this would track the node in a tree structure
    }
    
    override fun endNode() {
        // End the current composition node
        // In a real implementation, this would update the node in the tree structure
    }
    
    override fun startGroup(key: Any?) {
        // Start a new group with the given key
        // Groups can be skipped as a unit during recomposition
    }
    
    override fun endGroup() {
        // End the current group
    }
    
    override fun reportChanged() {
        // Report that something has changed and invalidate related parts of the composition
    }
    
    /**
     * Get the next available slot index.
     */
    override fun nextSlot(): Int {
        return currentIndex++
    }
    
    /**
     * Get the value stored in the given slot.
     */
    override fun getSlot(index: Int): Any? {
        return slotTable[index]
    }
    
    /**
     * Set the value for the given slot.
     */
    override fun setSlot(index: Int, value: Any?) {
        slotTable[index] = value
    }
    
    /**
     * Record that a state object was read.
     */
    override fun recordRead(state: Any) {
        // In a real implementation, this would track dependencies for recomposition
    }
    
    /**
     * Record that a state object was written to.
     */
    override fun recordWrite(state: Any, newValue: Any?) {
        // Trigger recomposition for components that depend on this state
        stateObservers[state]?.forEach { it() }
    }
    
    /**
     * Get the underlying TagConsumer for rendering.
     */
    fun getConsumer(): TagConsumer<*> {
        return consumer
    }
    
    /**
     * Create a unique key for the current composition.
     */
    fun createKey(): Any {
        return UUID.randomUUID()
    }
} 