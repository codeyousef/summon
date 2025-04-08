package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable
import kotlin.js.JsName

// External declaration for JavaScript console access
@JsName("console")
external object console {
    fun log(message: String)
}

/**
 * JS implementation of the Composer interface.
 * This provides JS-specific functionality for the composition system.
 */
class JsComposer : Composer {
    override val inserting: Boolean = true
    
    private val slots = mutableMapOf<Int, Any?>()
    private var slotIndex = 0
    private var currentNodeIndex = 0
    private val nodeStack = mutableListOf<Int>()
    private val groupStack = mutableListOf<Any?>()
    private val stateReads = mutableSetOf<Any>()
    private val stateWrites = mutableSetOf<Any>()
    private val disposables = mutableListOf<() -> Unit>()
    
    override fun startNode() {
        nodeStack.add(currentNodeIndex++)
    }
    
    override fun endNode() {
        nodeStack.removeAt(nodeStack.size - 1)
    }
    
    override fun startGroup(key: Any?) {
        groupStack.add(key)
        // Save the current slot index so we can restore it when the group ends
        slots[slotIndex] = slotIndex
        slotIndex++
    }
    
    override fun endGroup() {
        groupStack.removeAt(groupStack.size - 1)
        // Restore the slot index from when the group started
        slotIndex = (slots[slotIndex - 1] as? Int) ?: slotIndex
    }
    
    override fun changed(value: Any?): Boolean {
        val slotValue = getSlot()
        val hasChanged = slotValue != value
        if (hasChanged) {
            setSlot(value)
        }
        return hasChanged
    }
    
    override fun updateValue(value: Any?) {
        setSlot(value)
    }
    
    override fun nextSlot() {
        slotIndex++
    }
    
    override fun getSlot(): Any? {
        return slots[slotIndex]
    }
    
    override fun setSlot(value: Any?) {
        slots[slotIndex] = value
    }
    
    override fun recordRead(state: Any) {
        stateReads.add(state)
    }
    
    override fun recordWrite(state: Any) {
        stateWrites.add(state)
        reportChanged()
    }
    
    override fun reportChanged() {
        // In a JS environment, this would schedule a recomposition
        // For now, we'll simply mark that a change occurred
        // In a real implementation, this would trigger the recomposition process
        console.log("State changed, recomposition needed")
    }
    
    override fun registerDisposable(disposable: () -> Unit) {
        disposables.add(disposable)
    }
    
    /**
     * Disposes all registered disposables.
     * This would be called when the composition is destroyed.
     */
    override fun dispose() {
        disposables.forEach { it() }
        disposables.clear()
        slots.clear()
        stateReads.clear()
        stateWrites.clear()
    }
    
    /**
     * Factory method to create a JsComposer.
     */
    companion object {
        fun create(): JsComposer {
            return JsComposer()
        }
    }
} 
