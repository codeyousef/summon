package code.yousef.summon.runtime

/**
 * Core interface that manages the composition process.
 * Each platform will provide its own implementation of this interface.
 */
interface Composer {
    /**
     * Indicates whether the composer is currently inserting new nodes.
     */
    val inserting: Boolean
    
    /**
     * Start a new composition node.
     */
    fun startNode()
    
    /**
     * End the current composition node.
     */
    fun endNode()
    
    /**
     * Start a group that can be skipped as a unit during recomposition.
     */
    fun startGroup(key: Any?)
    
    /**
     * End the current group.
     */
    fun endGroup()
    
    /**
     * Report a change to the composition system.
     */
    fun reportChanged()
    
    /**
     * Get the next available slot index.
     */
    fun nextSlot(): Int
    
    /**
     * Get the value stored in the given slot.
     */
    fun getSlot(index: Int): Any?
    
    /**
     * Set the value for the given slot.
     */
    fun setSlot(index: Int, value: Any?)
    
    /**
     * Record that a state object was read.
     * Used for tracking dependencies for recomposition.
     *
     * @param state The state object that was read.
     */
    fun recordRead(state: Any)
    
    /**
     * Record that a state object was written to.
     * Used for triggering recomposition of dependent components.
     *
     * @param state The state object that was written to.
     * @param newValue The new value of the state.
     */
    fun recordWrite(state: Any, newValue: Any?)
} 