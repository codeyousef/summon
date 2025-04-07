package code.yousef.summon.runtime

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

/**
 * Interface for the Composer, which is responsible for managing the composition and recomposition of UI.
 * The Composer tracks which parts of the UI need to be redrawn when state changes.
 */
interface Composer {
    /**
     * Indicates whether nodes are currently being inserted into the composition tree.
     */
    val inserting: Boolean
    
    /**
     * Starts a new composition node.
     */
    fun startNode()
    
    /**
     * Starts a new composition group with the given key.
     * Groups allow for managing nodes with the same lifecycle.
     * 
     * @param key The key identifying this group.
     */
    fun startGroup(key: Any? = null)
    
    /**
     * Ends the current composition node.
     */
    fun endNode()
    
    /**
     * Ends the current composition group.
     */
    fun endGroup()
    
    /**
     * Checks if the value has changed since the last composition.
     * 
     * @param value The value to check for changes.
     * @return True if the value has changed, false otherwise.
     */
    fun changed(value: Any?): Boolean
    
    /**
     * Updates the value in the composition.
     * 
     * @param value The new value.
     */
    fun updateValue(value: Any?)
    
    /**
     * Moves to the next slot in the current group.
     */
    fun nextSlot()
    
    /**
     * Gets the current slot's value.
     * 
     * @return The value stored in the current slot
     */
    fun getSlot(): Any?
    
    /**
     * Sets the value of the current slot.
     * 
     * @param value The value to store
     */
    fun setSlot(value: Any?)
    
    /**
     * Records that a state object was read during composition.
     * 
     * @param state The state object that was read
     */
    fun recordRead(state: Any)
    
    /**
     * Records that a state object was written to during composition.
     * 
     * @param state The state object that was written
     */
    fun recordWrite(state: Any)
    
    /**
     * Reports that a tracked state object has changed.
     */
    fun reportChanged()
    
    /**
     * Registers a disposable resource that should be cleaned up 
     * when the composition is disposed.
     * 
     * @param disposable The resource to be disposed when the composition ends
     */
    fun registerDisposable(disposable: () -> Unit)
    
    /**
     * Disposes the composer and cleans up all resources.
     */
    fun dispose()
} 
