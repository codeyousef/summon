package code.yousef.summon.runtime

import kotlin.reflect.KProperty

/**
 * A readable state holder interface.
 * State is a value that can change over time, and it can be observed.
 *
 * @param T The type of the state value.
 */
interface State<T> {
    /**
     * Current value of the state.
     */
    val value: T
}

/**
 * A mutable state holder interface.
 * MutableState allows reading and writing a value.
 *
 * @param T The type of the state value.
 */
interface MutableState<T> : State<T> {
    /**
     * Current value of the state.
     * When the value is updated, any composable function that reads this value
     * will be recomposed.
     */
    override var value: T
}

/**
 * Implementation of MutableState that notifies observers when the value changes.
 *
 * @param T The type of the state value.
 * @param initialValue The initial value of the state.
 */
class MutableStateImpl<T>(initialValue: T) : MutableState<T> {
    private var _value: T = initialValue
    private val observers = mutableListOf<() -> Unit>()
    
    override var value: T
        get() {
            // Notify the composer about the state read
            CompositionLocal.currentComposer?.recordRead(this)
            return _value
        }
        set(newValue) {
            val oldValue = _value
            _value = newValue
            if (oldValue != newValue) {
                // Notify the composer about the state write
                CompositionLocal.currentComposer?.recordWrite(this, newValue)
                notifyObservers()
            }
        }
    
    /**
     * Add an observer that will be notified when the value changes.
     *
     * @param observer The observer function.
     */
    fun addObserver(observer: () -> Unit) {
        observers.add(observer)
    }
    
    /**
     * Remove an observer.
     *
     * @param observer The observer function to remove.
     */
    fun removeObserver(observer: () -> Unit) {
        observers.remove(observer)
    }
    
    /**
     * Notify all observers that the value has changed.
     */
    private fun notifyObservers() {
        observers.forEach { it() }
    }
}

/**
 * Create a new MutableState instance with the given initial value.
 *
 * @param initialValue The initial value of the state.
 * @return A new MutableState instance.
 */
fun <T> mutableStateOf(initialValue: T): MutableState<T> = MutableStateImpl(initialValue)

// Add getValue delegate operator for State
operator fun <T> State<T>.getValue(thisRef: Any?, property: KProperty<*>): T = this.value

// Add setValue delegate operator for MutableState
operator fun <T> MutableState<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
} 