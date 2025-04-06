package code.yousef.summon.runtime

import org.w3c.dom.Element

/**
 * JS-specific implementation of the Composer interface.
 * This version works with the DOM for rendering.
 */
class JsComposer(private val rootElement: Element) : Composer {
    override val inserting: Boolean = true
    
    private var currentIndex = 0
    private val slotTable = mutableMapOf<Int, Any?>()
    private val stateObservers = mutableMapOf<Any, MutableList<() -> Unit>>()
    private val nodeStack = mutableListOf<Element>()
    
    init {
        // Initialize with the root element
        nodeStack.add(rootElement)
    }
    
    override fun startNode() {
        // In a real implementation, this would create a new DOM node
        // and add it to the current parent node
    }
    
    override fun endNode() {
        // In a real implementation, this would finalize the current node
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
     * Get the current parent element for adding new nodes.
     */
    fun getCurrentParent(): Element {
        return nodeStack.last()
    }
    
    /**
     * Push a new element onto the node stack.
     */
    fun pushElement(element: Element) {
        nodeStack.add(element)
    }
    
    /**
     * Pop the current element from the node stack.
     */
    fun popElement() {
        if (nodeStack.size > 1) {
            nodeStack.removeAt(nodeStack.size - 1)
        }
    }
    
    /**
     * Create a DOM element and add it to the current parent.
     */
    fun createElement(tag: String): Element {
        val element = rootElement.ownerDocument!!.createElement(tag)
        getCurrentParent().appendChild(element)
        return element
    }
} 