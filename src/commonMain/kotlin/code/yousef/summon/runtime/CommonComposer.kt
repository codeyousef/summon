package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable

/**
 * A common interface for all Composer implementations across platforms
 * @deprecated Use [Composer] interface instead
 */
@Deprecated("Use Composer interface instead", ReplaceWith("Composer"))
interface ComposeManager {
    /**
     * Start composing a composable
     */
    fun startCompose()
    
    /**
     * End composing a composable
     */
    fun endCompose()
    
    /**
     * Execute a composable within this composer's context
     */
    fun <T> compose(composable: @Composable () -> T): T
}

/**
 * A simple implementation of Composer that can be used on any platform
 */
class CommonComposer : Composer {
    override val inserting: Boolean = true
    
    private val slots = mutableMapOf<Int, Any?>()
    private var slotIndex = 0
    
    override fun startCompose() {
        startNode()
    }
    
    override fun endCompose() {
        endNode()
    }
    
    override fun <T> compose(composable: @Composable () -> T): T {
        startCompose()
        try {
            return composable()
        } finally {
            endCompose()
        }
    }
    
    override fun startNode() {
        // Simple implementation
    }
    
    override fun endNode() {
        // Simple implementation
    }
    
    override fun startGroup(key: Any?) {
        // Simple implementation
    }
    
    override fun endGroup() {
        // Simple implementation
    }
    
    override fun changed(value: Any?): Boolean {
        // Simple implementation
        return true
    }
    
    override fun updateValue(value: Any?) {
        // Simple implementation
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
        // Simple implementation
    }
    
    override fun recordWrite(state: Any) {
        // Simple implementation
    }
    
    override fun reportChanged() {
        // Simple implementation
    }
    
    override fun registerDisposable(disposable: () -> Unit) {
        // Simple implementation
    }
    
    override fun dispose() {
        // Simple implementation
        slots.clear()
    }
}

/**
 * Utility object for managing the composition context
 */
object ComposeManagerContext {
    // Current composer, using platform-independent storage
    private var currentComposer: Composer? = null
    
    /**
     * Get the current composer, defaulting to CommonComposer if none set
     */
    val current: Composer
        get() = currentComposer ?: CommonComposer()
    
    /**
     * Execute a block with the given composer
     */
    fun <T> withComposer(composer: Composer, block: () -> T): T {
        val previous = currentComposer
        currentComposer = composer
        
        try {
            return block()
        } finally {
            currentComposer = previous
        }
    }
} 