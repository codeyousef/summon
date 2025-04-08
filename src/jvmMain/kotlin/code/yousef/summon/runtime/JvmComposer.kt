package code.yousef.summon.runtime

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import kotlinx.html.TagConsumer
import java.util.UUID

/**
 * JVM implementation of the Composer interface.
 * This provides JVM-specific functionality for the composition system.
 */
class JvmComposer : Composer {
    override val inserting: Boolean = true
    
    private val slots = mutableMapOf<Int, Any?>()
    private var slotIndex = 0
    private val stateReads = mutableSetOf<Any>()
    private val disposables = mutableListOf<() -> Unit>()
    
    override fun startNode() {
        // JVM implementation of start node
    }
    
    override fun endNode() {
        // JVM implementation of end node
    }
    
    override fun startGroup(key: Any?) {
        // JVM implementation of start group
    }
    
    override fun endGroup() {
        // JVM implementation of end group
    }
    
    override fun changed(value: Any?): Boolean {
        // JVM implementation would compare with previous value
        return true
    }
    
    override fun updateValue(value: Any?) {
        // JVM implementation would store the value
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
        // JVM implementation would trigger recomposition for affected compositions
    }
    
    override fun reportChanged() {
        // JVM implementation would trigger recomposition
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
    }
    
    /**
     * Factory method to create a JvmComposer.
     */
    companion object {
        fun create(): JvmComposer {
            return JvmComposer()
        }
    }

    /**
     * Implementation of renderComposable for the JVM platform
     */
    override fun <T> renderComposable(composable: Composable, consumer: T) {
        // Call compose on the composable with the provided consumer
        composable.compose(consumer)
    }
} 
