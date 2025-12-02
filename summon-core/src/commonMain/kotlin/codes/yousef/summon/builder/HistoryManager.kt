package codes.yousef.summon.builder

import codes.yousef.summon.core.registry.JsonBlock
import codes.yousef.summon.state.SummonMutableState
import codes.yousef.summon.state.mutableStateOf
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * Generic history stack for undo/redo functionality.
 *
 * Maintains a list of states and a pointer to the current state,
 * enabling navigation through edit history.
 *
 * ## Features
 *
 * - **Undo/Redo**: Navigate through edit history
 * - **State Snapshots**: Stores each state for navigation
 * - **Capacity Limit**: Configurable maximum history size
 *
 * ## Usage
 *
 * ```kotlin
 * val history = HistoryManager<MyState>()
 * history.push(initialState)
 * history.push(newState)
 * history.undo() // Back to initialState
 * history.redo() // Forward to newState
 * ```
 *
 * @param T The type of state being tracked
 * @since 1.0.0
 */
class HistoryManager<T>(
    /**
     * Maximum number of history entries to keep.
     */
    var maxHistorySize: Int = 50
) {
    private val lock = ReentrantLock()
    
    /**
     * The history stack of states.
     */
    private val history = mutableListOf<T>()
    
    /**
     * Current position in the history stack.
     */
    private var pointer: Int = -1
    
    /**
     * Pushes a new state onto the history stack.
     *
     * If we're not at the end of the history (after undos),
     * this will discard any future states.
     *
     * @param state The new state after an edit
     */
    fun push(state: T) {
        lock.withLock {
            // Discard any states after current pointer
            if (pointer < history.size - 1) {
                val toRemove = history.size - pointer - 1
                repeat(toRemove) {
                    history.removeAt(history.size - 1)
                }
            }
            
            // Add new state
            history.add(state)
            pointer = history.size - 1
            
            // Enforce max size
            while (history.size > maxHistorySize) {
                history.removeAt(0)
                pointer--
            }
        }
    }
    
    /**
     * Returns the current state.
     *
     * @throws NoSuchElementException if history is empty
     */
    fun current(): T {
        return lock.withLock {
            if (pointer >= 0 && pointer < history.size) {
                history[pointer]
            } else {
                throw NoSuchElementException("History is empty")
            }
        }
    }
    
    /**
     * Checks if undo is available.
     */
    fun canUndo(): Boolean = lock.withLock { pointer > 0 }
    
    /**
     * Checks if redo is available.
     */
    fun canRedo(): Boolean = lock.withLock { pointer < history.size - 1 }
    
    /**
     * Undoes the last edit, moving back in history.
     *
     * @return true if undo was performed, false if at beginning
     */
    fun undo(): Boolean {
        return lock.withLock {
            if (pointer > 0) {
                pointer--
                true
            } else {
                false
            }
        }
    }
    
    /**
     * Redoes the last undone edit, moving forward in history.
     *
     * @return true if redo was performed, false if at end
     */
    fun redo(): Boolean {
        return lock.withLock {
            if (pointer < history.size - 1) {
                pointer++
                true
            } else {
                false
            }
        }
    }
    
    /**
     * Returns the number of states in history.
     */
    fun size(): Int = lock.withLock { history.size }
    
    /**
     * Clears all history.
     */
    fun clear() {
        lock.withLock {
            history.clear()
            pointer = -1
        }
    }
}

/**
 * Singleton history manager for JsonBlock trees in visual builder mode.
 *
 * Maintains a list of JSON tree states and a pointer to the current state,
 * enabling navigation through edit history.
 *
 * ## Features
 *
 * - **Undo/Redo**: Navigate through edit history
 * - **State Snapshots**: Clones tree state for each edit
 * - **Capacity Limit**: Configurable maximum history size
 * - **Reactive State**: Current state is observable
 *
 * ## Usage
 *
 * ```kotlin
 * // Initialize with a starting state
 * JsonTreeHistoryManager.initialize(initialTree)
 *
 * // After making an edit
 * JsonTreeHistoryManager.push(newTreeState)
 *
 * // Undo
 * JsonTreeHistoryManager.undo()
 *
 * // Redo
 * JsonTreeHistoryManager.redo()
 *
 * // Access current state
 * val currentTree by JsonTreeHistoryManager.currentState
 * ```
 *
 * @since 1.0.0
 */
object JsonTreeHistoryManager {
    private val lock = ReentrantLock()
    
    /**
     * Maximum number of history entries to keep.
     */
    var maxHistorySize: Int = 50
    
    /**
     * The history stack of tree states.
     */
    private val history = mutableListOf<List<JsonBlock>>()
    
    /**
     * Current position in the history stack.
     */
    private var pointer: Int = -1
    
    /**
     * Reactive state holding the current tree.
     */
    val currentState: SummonMutableState<List<JsonBlock>> = mutableStateOf(emptyList())
    
    /**
     * Reactive state indicating if undo is available.
     */
    val canUndo: SummonMutableState<Boolean> = mutableStateOf(false)
    
    /**
     * Reactive state indicating if redo is available.
     */
    val canRedo: SummonMutableState<Boolean> = mutableStateOf(false)
    
    /**
     * Initializes the history with a starting state.
     *
     * Clears any existing history and sets the initial state.
     *
     * @param initialState The starting tree state
     */
    fun initialize(initialState: List<JsonBlock>) {
        lock.withLock {
            history.clear()
            history.add(deepClone(initialState))
            pointer = 0
            updateStates()
        }
    }
    
    /**
     * Pushes a new state onto the history stack.
     *
     * If we're not at the end of the history (after undos),
     * this will discard any future states.
     *
     * @param newState The new tree state after an edit
     */
    fun push(newState: List<JsonBlock>) {
        lock.withLock {
            // Discard any states after current pointer
            if (pointer < history.size - 1) {
                val toRemove = history.size - pointer - 1
                repeat(toRemove) {
                    history.removeAt(history.size - 1)
                }
            }
            
            // Add new state
            history.add(deepClone(newState))
            pointer = history.size - 1
            
            // Enforce max size
            while (history.size > maxHistorySize) {
                history.removeAt(0)
                pointer--
            }
            
            updateStates()
        }
    }
    
    /**
     * Undoes the last edit, moving back in history.
     *
     * @return true if undo was performed, false if at beginning
     */
    fun undo(): Boolean {
        return lock.withLock {
            if (pointer > 0) {
                pointer--
                updateStates()
                true
            } else {
                false
            }
        }
    }
    
    /**
     * Redoes the last undone edit, moving forward in history.
     *
     * @return true if redo was performed, false if at end
     */
    fun redo(): Boolean {
        return lock.withLock {
            if (pointer < history.size - 1) {
                pointer++
                updateStates()
                true
            } else {
                false
            }
        }
    }
    
    /**
     * Returns the current tree state without modifying history.
     */
    fun getCurrentState(): List<JsonBlock> {
        return lock.withLock {
            if (pointer >= 0 && pointer < history.size) {
                deepClone(history[pointer])
            } else {
                emptyList()
            }
        }
    }
    
    /**
     * Clears all history.
     */
    fun clear() {
        lock.withLock {
            history.clear()
            pointer = -1
            updateStates()
        }
    }
    
    /**
     * Returns the number of states in history.
     */
    fun historySize(): Int {
        return lock.withLock {
            history.size
        }
    }
    
    /**
     * Returns the current history position.
     */
    fun currentPosition(): Int {
        return lock.withLock {
            pointer
        }
    }
    
    private fun updateStates() {
        canUndo.value = pointer > 0
        canRedo.value = pointer < history.size - 1
        currentState.value = if (pointer >= 0 && pointer < history.size) {
            deepClone(history[pointer])
        } else {
            emptyList()
        }
    }
    
    /**
     * Deep clones a list of JsonBlocks to prevent reference sharing.
     */
    private fun deepClone(blocks: List<JsonBlock>): List<JsonBlock> {
        return blocks.map { block ->
            JsonBlock(
                type = block.type,
                props = block.props.toMap(), // Shallow copy of map
                children = deepClone(block.children)
            )
        }
    }
}
