package codes.yousef.summon.builder

import codes.yousef.summon.state.SummonMutableState
import codes.yousef.summon.state.mutableStateOf
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * Selection state manager for visual builder mode.
 *
 * Tracks which component is currently selected in the editor, provides
 * selection overlay dimensions, and manages selection lifecycle.
 *
 * ## Features
 *
 * - **Single Selection**: Only one component can be selected at a time
 * - **Selection Overlay**: Tracks dimensions for visual overlay rendering
 * - **Selection Events**: Callbacks for selection changes
 *
 * ## Usage
 *
 * ```kotlin
 * // Select a component
 * SelectionManager.select("component-123")
 *
 * // Check selection
 * if (SelectionManager.isSelected("component-123")) {
 *     // Render selection UI
 * }
 *
 * // Clear selection
 * SelectionManager.clearSelection()
 *
 * // Observe selection changes
 * val selectedId by SelectionManager.selection
 * ```
 *
 * @since 1.0.0
 */
object SelectionManager {
    private val lock = ReentrantLock()
    
    /**
     * Reactive state holding the currently selected component ID.
     * Null when nothing is selected.
     */
    val selection: SummonMutableState<String?> = mutableStateOf(null)
    
    /**
     * Reactive state holding the bounding rect of the selected component.
     */
    val selectionBounds: SummonMutableState<SelectionBounds?> = mutableStateOf(null)
    
    private val selectionListeners = mutableListOf<(String?) -> Unit>()
    
    /**
     * Selects a component by its ID.
     *
     * @param componentId The unique ID of the component to select
     */
    fun select(componentId: String) {
        lock.withLock {
            selection.value = componentId
            notifyListeners(componentId)
        }
    }
    
    /**
     * Clears the current selection.
     */
    fun clearSelection() {
        lock.withLock {
            selection.value = null
            selectionBounds.value = null
            notifyListeners(null)
        }
    }
    
    /**
     * Checks if a specific component is selected.
     *
     * @param componentId The component ID to check
     * @return true if the component is selected
     */
    fun isSelected(componentId: String): Boolean {
        return selection.value == componentId
    }
    
    /**
     * Updates the selection bounds for overlay positioning.
     *
     * @param bounds The bounding rectangle of the selected component
     */
    fun updateBounds(bounds: SelectionBounds) {
        lock.withLock {
            selectionBounds.value = bounds
        }
    }
    
    /**
     * Adds a listener for selection changes.
     *
     * @param listener Callback invoked when selection changes
     */
    fun addSelectionListener(listener: (String?) -> Unit) {
        lock.withLock {
            selectionListeners.add(listener)
        }
    }
    
    /**
     * Removes a selection listener.
     *
     * @param listener The listener to remove
     */
    fun removeSelectionListener(listener: (String?) -> Unit) {
        lock.withLock {
            selectionListeners.remove(listener)
        }
    }
    
    private fun notifyListeners(componentId: String?) {
        selectionListeners.forEach { it(componentId) }
    }
}

/**
 * Represents the bounding rectangle of a selected component.
 */
data class SelectionBounds(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double,
    val top: Double,
    val right: Double,
    val bottom: Double,
    val left: Double
)
