package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable

/**
 * Interface for the Composer, which manages composition state and tracks UI changes for efficient recomposition.
 *
 * The Composer is a fundamental part of Summon's reactive UI system. It maintains the composition state,
 * tracks dependencies between composable functions and state objects, and enables efficient partial
 * recomposition when state changes.
 *
 * ## Core Responsibilities
 *
 * ### Composition Tree Management
 * - Maintains a hierarchical structure of composable function calls
 * - Tracks node and group relationships through [startNode]/[endNode] and [startGroup]/[endGroup]
 * - Manages slot-based storage for remembered values and state
 *
 * ### Change Detection
 * - Detects when state objects are read via [recordRead]
 * - Tracks state mutations through [recordWrite]
 * - Determines what needs recomposition using [changed]
 *
 * ### Memory Management
 * - Provides slot-based storage for values that persist across recomposition
 * - Manages cleanup of resources through [registerDisposable]
 * - Integrates with the [remember] function for value persistence
 *
 * ## Composition Process
 *
 * The composer works in several phases:
 *
 * 1. **Initial Composition**: Creates the initial composition tree and slots
 * 2. **Dependency Tracking**: Records which state objects are read by each composable
 * 3. **Change Detection**: Monitors state mutations and identifies affected composables
 * 4. **Recomposition**: Re-executes only the composables that depend on changed state
 *
 * ## Slot Management
 *
 * The composer uses a slot-based system for efficient storage:
 * - Each composable function gets allocated slots during composition
 * - Slots store remembered values, state objects, and intermediate data
 * - [nextSlot], [getSlot], and [setSlot] manage slot access
 *
 * ## Integration with Framework
 *
 * The Composer integrates with several framework systems:
 * - **Recomposer**: Schedules and executes recomposition
 * - **State System**: Tracks state reads/writes for dependency management
 * - **Effects**: Manages effect lifecycle and cleanup
 * - **Remember Functions**: Provides persistent storage across recomposition
 *
 * ## Example Usage
 *
 * While typically used internally by the framework, the Composer enables patterns like:
 *
 * ```kotlin
 * @Composable
 * fun ExampleComponent() {
 *     // The composer automatically manages:
 *     var count by remember { mutableStateOf(0) }  // Slot storage
 *     val derived by remember(count) { count * 2 }  // Dependency tracking
 *
 *     LaunchedEffect(count) {  // Effect lifecycle
 *         // Side effect tied to composition
 *     }
 * }
 * ```
 *
 * ## Implementation Notes
 *
 * - Implementations must be thread-safe for multi-threaded environments
 * - Slot indices should be deterministic based on composition order
 * - Cleanup must be performed when composition is disposed
 * - Integration with platform-specific scheduling mechanisms
 *
 * @see code.yousef.summon.runtime.Recomposer
 * @see code.yousef.summon.runtime.remember
 * @see code.yousef.summon.state.mutableStateOf
 * @see code.yousef.summon.runtime.LaunchedEffect
 * @since 1.0.0
 */
interface Composer {
    /**
     * Indicates whether nodes are currently being inserted into the composition tree.
     *
     * This property is true during the initial composition when the composition tree
     * is being built for the first time. During recomposition, this is typically false
     * as the composer is updating existing nodes rather than inserting new ones.
     *
     * @return true if currently inserting nodes, false during recomposition
     * @since 1.0.0
     */
    val inserting: Boolean

    /**
     * Starts a new composition node in the composition tree.
     *
     * Composition nodes represent individual UI elements in the composition hierarchy.
     * Each node corresponds to a rendered component and maintains its own state and
     * lifecycle. Nodes must be properly paired with [endNode] calls.
     *
     * ## Usage Pattern
     *
     * ```kotlin
     * composer.startNode()
     * try {
     *     // Render component content
     * } finally {
     *     composer.endNode()
     * }
     * ```
     *
     * @see endNode
     * @since 1.0.0
     */
    fun startNode()

    /**
     * Starts a new composition group with the specified key.
     *
     * Groups are logical containers that allow efficient management of related
     * composition nodes. They provide a way to:
     * - Group related UI elements for efficient recomposition
     * - Enable key-based optimization for list rendering
     * - Manage shared lifecycle and state
     *
     * ## Key-Based Optimization
     *
     * When a key is provided, the composer can:
     * - Efficiently detect when groups are moved or reordered
     * - Preserve state when items change position
     * - Skip recomposition of unchanged groups
     *
     * ## Example
     *
     * ```kotlin
     * items.forEach { item ->
     *     composer.startGroup(key = item.id)
     *     // Render item
     *     composer.endGroup()
     * }
     * ```
     *
     * @param key Optional key for identifying this group across recompositions
     * @see endGroup
     * @since 1.0.0
     */
    fun startGroup(key: Any? = null)

    /**
     * Ends the current composition node.
     *
     * This must be called to properly close a node started with [startNode].
     * Failing to properly pair start/end calls can result in corrupted composition state.
     *
     * @see startNode
     * @since 1.0.0
     */
    fun endNode()

    /**
     * Ends the current composition group.
     *
     * This must be called to properly close a group started with [startGroup].
     * Groups must be properly nested and balanced.
     *
     * @see startGroup
     * @since 1.0.0
     */
    fun endGroup()

    /**
     * Checks if a value has changed since the last composition.
     *
     * This is a key optimization mechanism that enables efficient recomposition.
     * By comparing values across composition passes, the composer can skip
     * recomposition of unchanged subtrees.
     *
     * ## Change Detection
     *
     * The comparison uses:
     * - Structural equality for data classes and primitives
     * - Reference equality for objects
     * - Custom equality for state objects
     *
     * ## Example
     *
     * ```kotlin
     * @Composable
     * fun Component(text: String) {
     *     if (composer.changed(text)) {
     *         // Only re-render when text actually changes
     *         renderExpensiveText(text)
     *     }
     * }
     * ```
     *
     * @param value The value to check for changes
     * @return true if the value has changed, false if it's the same
     * @since 1.0.0
     */
    fun changed(value: Any?): Boolean

    /**
     * Updates a value in the current composition slot.
     *
     * This method stores a value in the current slot position, making it
     * available for future composition passes and change detection.
     *
     * @param value The new value to store
     * @see getSlot
     * @see setSlot
     * @since 1.0.0
     */
    fun updateValue(value: Any?)

    /**
     * Advances to the next slot in the current composition group.
     *
     * The composer maintains a slot index that tracks the current position
     * within a group. This method advances to the next slot, enabling
     * sequential access to stored values.
     *
     * ## Slot Management
     *
     * Slots are allocated sequentially within groups and must be accessed
     * in the same order across composition passes for consistency.
     *
     * @see getSlot
     * @see setSlot
     * @since 1.0.0
     */
    fun nextSlot()

    /**
     * Retrieves the value stored in the current composition slot.
     *
     * Returns the value previously stored at the current slot position,
     * or null if no value has been stored or this is the first composition.
     *
     * ## Type Safety
     *
     * Callers are responsible for casting the returned value to the expected type.
     * The slot system is type-erased for performance but type safety is maintained
     * through consistent usage patterns.
     *
     * @return The value stored in the current slot, or null if empty
     * @see setSlot
     * @see nextSlot
     * @since 1.0.0
     */
    fun getSlot(): Any?

    /**
     * Stores a value in the current composition slot.
     *
     * The value will be available in subsequent composition passes at the same
     * slot position, enabling state persistence across recomposition.
     *
     * ## Persistence
     *
     * Values stored in slots persist across recomposition until:
     * - The containing group is removed from composition
     * - The composition is disposed
     * - The slot is explicitly overwritten
     *
     * @param value The value to store in the current slot
     * @see getSlot
     * @see nextSlot
     * @since 1.0.0
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
     * Recomposes the UI, re-running composable functions that have changed.
     */
    fun recompose()

    /**
     * Remembers a value for the given key.
     */
    fun rememberedValue(key: Any): Any?

    /**
     * Updates a remembered value for the given key.
     */
    fun updateRememberedValue(key: Any, value: Any?)

    /**
     * Disposes the composer and cleans up all resources.
     */
    fun dispose()

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
