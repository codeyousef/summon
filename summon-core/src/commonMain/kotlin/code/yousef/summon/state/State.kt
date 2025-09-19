/**
 * # Summon State Package
 *
 * This package provides reactive state management primitives for Summon UI applications.
 *
 * ## Overview
 *
 * The state package implements a reactive state system that enables:
 *
 * - **Reactive Updates**: Automatic UI recomposition when state changes
 * - **Type Safety**: Compile-time type checking for state values
 * - **Delegation Support**: Kotlin property delegation for ergonomic state access
 * - **Integration**: Seamless integration with the composition system
 * - **Performance**: Efficient change detection and minimal recomposition
 *
 * ## Key Components
 *
 * ### State Interfaces
 * - [State] - Read-only state holder interface
 * - [SummonMutableState] - Mutable state holder interface
 * - [MutableState] - Extended mutable state with delegation and listeners
 *
 * ### State Creation
 * - [mutableStateOf] - Creates reactive mutable state
 * - Property delegation operators for ergonomic access
 *
 * ### State Integration
 * - Automatic dependency tracking with composition system
 * - Recomposition triggering when state changes
 * - Integration with [remember] for state persistence
 *
 * ## Usage Patterns
 *
 * ### Basic State
 * ```kotlin
 * @Composable
 * fun Counter() {
 *     var count by remember { mutableStateOf(0) }
 *
 *     Button(
 *         onClick = { count++ },
 *         label = "Count: $count"
 *     )
 * }
 * ```
 *
 * ### State Hoisting
 * ```kotlin
 * @Composable
 * fun CounterApp() {
 *     var count by remember { mutableStateOf(0) }
 *
 *     Column {
 *         Counter(count = count, onIncrement = { count++ })
 *         CounterDisplay(count = count)
 *     }
 * }
 * ```
 *
 * ### Complex State
 * ```kotlin
 * data class User(val name: String, val email: String)
 *
 * @Composable
 * fun UserProfile() {
 *     var user by remember { mutableStateOf(User("", "")) }
 *
 *     Column {
 *         TextField(
 *             value = user.name,
 *             onValueChange = { user = user.copy(name = it) }
 *         )
 *         TextField(
 *             value = user.email,
 *             onValueChange = { user = user.copy(email = it) }
 *         )
 *     }
 * }
 * ```
 *
 * @since 1.0.0
 */
package code.yousef.summon.state

import code.yousef.summon.runtime.RecomposerHolder
import kotlin.reflect.KProperty

/**
 * Interface for a read-only state holder.
 *
 * State represents a snapshot of data at a particular point in time that can be
 * observed by composable functions. When used within a composable, reading the
 * state value establishes a dependency that will trigger recomposition when
 * the state changes.
 *
 * ## Reactive Behavior
 *
 * State objects integrate with Summon's composition system:
 * - Reading state during composition establishes a dependency
 * - State changes trigger recomposition of dependent composables
 * - Only composables that actually read changed state are recomposed
 *
 * ## Implementation Notes
 *
 * State implementations should:
 * - Be thread-safe for multi-threaded environments
 * - Notify the composition system when values change
 * - Support efficient equality checking for change detection
 *
 * @param T The type of value held by this state
 * @see SummonMutableState
 * @see mutableStateOf
 * @since 1.0.0
 */
interface State<T> {
    /**
     * The current value stored in this state.
     *
     * Reading this property during composition establishes a dependency
     * that will trigger recomposition when the value changes.
     *
     * @return The current state value
     */
    val value: T
}

/**
 * Interface for a mutable state holder.
 *
 * SummonMutableState extends [State] to allow mutations that trigger reactive updates.
 * When the value is changed, the composition system is notified and dependent
 * composables are scheduled for recomposition.
 *
 * ## Mutation and Recomposition
 *
 * Setting the [value] property:
 * 1. Updates the stored value
 * 2. Notifies the composition system of the change
 * 3. Schedules recomposition of dependent composables
 * 4. Only recomposes composables that actually read this state
 *
 * ## Thread Safety
 *
 * State mutations should be performed on the appropriate thread:
 * - UI-related state should typically be updated from the main/UI thread
 * - Background state updates should use appropriate dispatchers
 * - State implementations handle thread-safe change notifications
 *
 * @param T The type of value held by this state
 * @see State
 * @see MutableState
 * @since 1.0.0
 */
interface SummonMutableState<T> : State<T> {
    /**
     * The current value stored in this state.
     *
     * Reading this property establishes a dependency for recomposition.
     * Writing to this property triggers change notifications and schedules
     * recomposition of dependent composables.
     *
     * ## Change Detection
     *
     * The state system uses structural equality to detect changes:
     * - Primitive values are compared by value
     * - Data classes use generated equals methods
     * - Objects are compared by reference unless overridden
     *
     * Only actual changes trigger recomposition - setting the same value
     * multiple times will not cause unnecessary updates.
     *
     * @return The current state value
     */
    override var value: T
}

/**
 * Extended mutable state interface with component functions and listeners.
 */
interface MutableState<T> : SummonMutableState<T> {
    operator fun component1(): T
    operator fun component2(): (T) -> Unit

    fun addListener(listener: (T) -> Unit)
    fun removeListener(listener: (T) -> Unit)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}

/**
 * Implementation of MutableState that notifies observers when its value changes.
 * @param initialValue The initial value of this state
 */
class MutableStateImpl<T>(initialValue: T) : MutableState<T> {
    private val listeners = mutableListOf<(T) -> Unit>()

    override var value: T = initialValue
        get() {
            // Only record read if we're in a composition context
            if (RecomposerHolder.current().isComposing()) {
                RecomposerHolder.current().recordRead(this)
            }
            return field
        }
        set(newValue) {
            if (field != newValue) {
                field = newValue
                notifyListeners()
                // Notify the recomposer that this state has changed
                RecomposerHolder.current().recordStateWrite(this)
            }
        }

    override fun component1(): T = value

    override fun component2(): (T) -> Unit = { value = it }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    /**
     * Adds a listener that will be called whenever the state value changes.
     * @param listener A function that receives the new value
     */
    override fun addListener(listener: (T) -> Unit) {
        listeners.add(listener)
    }

    /**
     * Removes a previously added listener.
     * @param listener The listener to remove
     */
    override fun removeListener(listener: (T) -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it(value) }
    }
}

/**
 * Creates a new MutableState instance with the given initial value.
 *
 * NOTE: This is the primary implementation of mutableStateOf that should be used for new code.
 * The runtime package also contains a mutableStateOf function, but it delegates to this one
 * and exists only for backward compatibility.
 *
 * @param initialValue The initial value of the state
 * @return A MutableState holding the initial value
 * @see code.yousef.summon.runtime.mutableStateOf
 */
fun <T> mutableStateOf(initialValue: T): MutableState<T> =
    MutableStateImpl(initialValue)

/**
 * Enables read access for property delegation with State objects.
 * This allows using the syntax: val property by state
 */
operator fun <T> State<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value

/**
 * Enables write access for property delegation with SummonMutableState objects.
 * This allows using the syntax: var property by mutableState
 */
operator fun <T> SummonMutableState<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}
