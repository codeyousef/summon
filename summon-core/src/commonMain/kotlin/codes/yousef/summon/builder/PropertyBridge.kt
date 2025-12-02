package codes.yousef.summon.builder

import codes.yousef.summon.core.registry.JsonBlock
import codes.yousef.summon.state.SummonMutableState
import codes.yousef.summon.state.mutableStateOf

/**
 * Property binding bridge for updating component properties in visual builder mode.
 *
 * Enables dynamic property updates to components in the JSON tree, with
 * automatic re-rendering of affected branches.
 *
 * ## Features
 *
 * - **Type-Safe Updates**: Update properties with proper type handling
 * - **Tree Navigation**: Find and update components by ID
 * - **Change Tracking**: Integrates with HistoryManager for undo/redo
 *
 * ## Usage
 *
 * ```kotlin
 * // Update a text property
 * PropertyBridge.updateProperty("text-123", "text", "New content")
 *
 * // Update a style property
 * PropertyBridge.updateProperty("button-456", "backgroundColor", "#ff0000")
 *
 * // Batch updates
 * PropertyBridge.updateProperties("card-789", mapOf(
 *     "title" to "Updated Title",
 *     "subtitle" to "New subtitle"
 * ))
 * ```
 *
 * @since 1.0.0
 */
object PropertyBridge {
    /**
     * Reactive state holding the current tree.
     * Linked to JsonTreeHistoryManager for persistence.
     */
    val currentTree: SummonMutableState<List<JsonBlock>> = mutableStateOf(emptyList())
    
    /**
     * Simple property storage for components (for testing and simple use cases).
     */
    private val propertyStore = mutableMapOf<String, MutableMap<String, Any?>>()
    
    /**
     * Callback invoked when properties are updated.
     */
    var onPropertyChange: ((componentId: String, propName: String, newValue: Any?) -> Unit)? = null
    
    /**
     * Binds a property value for a component.
     *
     * @param componentId The component ID
     * @param propName The property name
     * @param value The property value
     */
    fun bindProperty(componentId: String, propName: String, value: Any?) {
        val props = propertyStore.getOrPut(componentId) { mutableMapOf() }
        props[propName] = value
    }
    
    /**
     * Updates a single property on a component.
     *
     * @param componentId The unique ID of the component to update
     * @param propName The property name to update
     * @param value The new value for the property
     * @return true if the property was updated
     */
    fun updateProperty(componentId: String, propName: String, value: Any?): Boolean {
        val props = propertyStore[componentId]
        if (props != null) {
            props[propName] = value
            onPropertyChange?.invoke(componentId, propName, value)
            return true
        }
        // Also try updating in tree
        val tree = currentTree.value.toMutableList()
        val result = updatePropertyInTree(tree, componentId, propName, value)
        
        if (result.success) {
            currentTree.value = tree
            JsonTreeHistoryManager.push(tree)
            onPropertyChange?.invoke(componentId, propName, value)
        }
        
        return result.success
    }
    
    /**
     * Updates multiple properties on a component at once.
     *
     * @param componentId The unique ID of the component
     * @param properties Map of property names to new values
     * @return true if the component was found and updated
     */
    fun updateProperties(componentId: String, properties: Map<String, Any?>): Boolean {
        var anyUpdated = false
        properties.forEach { (propName, value) ->
            if (updateProperty(componentId, propName, value)) {
                anyUpdated = true
            }
        }
        return anyUpdated
    }
    
    /**
     * Gets a property value from a component.
     *
     * @param componentId The component ID
     * @param propName The property name
     * @return The property value, or null if not found
     */
    fun getProperty(componentId: String, propName: String): Any? {
        // First check simple store
        propertyStore[componentId]?.get(propName)?.let { return it }
        // Then check tree
        return findComponentById(currentTree.value, componentId)?.props?.get(propName)
    }
    
    /**
     * Gets all properties for a component.
     *
     * @param componentId The component ID
     * @return Map of all properties (empty if component not found)
     */
    fun getAllProperties(componentId: String): Map<String, Any?> {
        return propertyStore[componentId]?.toMap() 
            ?: findComponentById(currentTree.value, componentId)?.props 
            ?: emptyMap()
    }
    
    /**
     * Removes a property from a component.
     *
     * @param componentId The component ID
     * @param propName The property name to remove
     */
    fun removeProperty(componentId: String, propName: String) {
        propertyStore[componentId]?.remove(propName)
    }
    
    /**
     * Removes all properties for a component.
     *
     * @param componentId The component ID
     */
    fun removeAllProperties(componentId: String) {
        propertyStore.remove(componentId)
    }
    
    /**
     * Clears all stored properties.
     */
    fun clear() {
        propertyStore.clear()
        currentTree.value = emptyList()
    }
    
    /**
     * Adds a new child to a container component.
     *
     * @param parentId The ID of the parent container
     * @param child The new child JsonBlock to add
     * @param index Optional position to insert at (-1 for end)
     * @return true if the child was added
     */
    fun addChild(parentId: String, child: JsonBlock, index: Int = -1): Boolean {
        val tree = currentTree.value.toMutableList()
        val success = addChildToTree(tree, parentId, child, index)
        
        if (success) {
            currentTree.value = tree
            JsonTreeHistoryManager.push(tree)
        }
        
        return success
    }
    
    /**
     * Removes a component from the tree.
     *
     * @param componentId The ID of the component to remove
     * @return true if the component was found and removed
     */
    fun removeComponent(componentId: String): Boolean {
        val tree = currentTree.value.toMutableList()
        val success = removeFromTree(tree, componentId)
        
        if (success) {
            currentTree.value = tree
            JsonTreeHistoryManager.push(tree)
        }
        
        return success
    }
    
    /**
     * Moves a component to a new parent or position.
     *
     * @param componentId The ID of the component to move
     * @param newParentId The ID of the new parent container
     * @param index Position in the new parent (-1 for end)
     * @return true if the move was successful
     */
    fun moveComponent(componentId: String, newParentId: String, index: Int = -1): Boolean {
        val component = findComponentById(currentTree.value, componentId) ?: return false
        
        if (!removeComponent(componentId)) return false
        
        return addChild(newParentId, component, index)
    }
    
    /**
     * Initializes the tree from JsonTreeHistoryManager.
     */
    fun syncFromHistory() {
        currentTree.value = JsonTreeHistoryManager.getCurrentState()
    }
    
    // --- Private Helpers ---
    
    private data class UpdateResult(
        val success: Boolean,
        val oldValue: Any? = null
    )
    
    private fun updatePropertyInTree(
        blocks: MutableList<JsonBlock>,
        componentId: String,
        propName: String,
        value: Any?
    ): UpdateResult {
        for (i in blocks.indices) {
            val block = blocks[i]
            val nodeId = block.props["__nodeId"] as? String
            
            if (nodeId == componentId) {
                val oldValue = block.props[propName]
                val newProps = block.props.toMutableMap()
                if (value != null) {
                    newProps[propName] = value
                } else {
                    newProps.remove(propName)
                }
                blocks[i] = block.copy(props = newProps)
                return UpdateResult(success = true, oldValue = oldValue)
            }
            
            // Recursively check children
            if (block.children.isNotEmpty()) {
                val mutableChildren = block.children.toMutableList()
                val result = updatePropertyInTree(mutableChildren, componentId, propName, value)
                if (result.success) {
                    blocks[i] = block.copy(children = mutableChildren)
                    return result
                }
            }
        }
        
        return UpdateResult(success = false)
    }
    
    private fun findComponentById(blocks: List<JsonBlock>, componentId: String): JsonBlock? {
        for (block in blocks) {
            val nodeId = block.props["__nodeId"] as? String
            if (nodeId == componentId) {
                return block
            }
            
            if (block.children.isNotEmpty()) {
                val found = findComponentById(block.children, componentId)
                if (found != null) return found
            }
        }
        return null
    }
    
    private fun addChildToTree(
        blocks: MutableList<JsonBlock>,
        parentId: String,
        child: JsonBlock,
        index: Int
    ): Boolean {
        for (i in blocks.indices) {
            val block = blocks[i]
            val nodeId = block.props["__nodeId"] as? String
            
            if (nodeId == parentId) {
                val mutableChildren = block.children.toMutableList()
                if (index < 0 || index >= mutableChildren.size) {
                    mutableChildren.add(child)
                } else {
                    mutableChildren.add(index, child)
                }
                blocks[i] = block.copy(children = mutableChildren)
                return true
            }
            
            if (block.children.isNotEmpty()) {
                val mutableChildren = block.children.toMutableList()
                if (addChildToTree(mutableChildren, parentId, child, index)) {
                    blocks[i] = block.copy(children = mutableChildren)
                    return true
                }
            }
        }
        return false
    }
    
    private fun removeFromTree(blocks: MutableList<JsonBlock>, componentId: String): Boolean {
        val iterator = blocks.iterator()
        while (iterator.hasNext()) {
            val block = iterator.next()
            val nodeId = block.props["__nodeId"] as? String
            
            if (nodeId == componentId) {
                iterator.remove()
                return true
            }
        }
        
        for (i in blocks.indices) {
            val block = blocks[i]
            if (block.children.isNotEmpty()) {
                val mutableChildren = block.children.toMutableList()
                if (removeFromTree(mutableChildren, componentId)) {
                    blocks[i] = block.copy(children = mutableChildren)
                    return true
                }
            }
        }
        
        return false
    }
}
