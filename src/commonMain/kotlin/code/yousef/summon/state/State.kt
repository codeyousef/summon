package code.yousef.summon.state

/**
 * Interface for a read-only state holder.
 * @param T The type of value held by this state
 */
interface State<T> {
    /**
     * The current value stored in this state.
     */
    val value: T
}

/**
 * Interface for a mutable state holder.
 * @param T The type of value held by this state
 */
interface SummonMutableState<T> : State<T> {
    /**
     * The current value stored in this state.
     * Updating this value may trigger recomposition of UI components that use it.
     */
    override var value: T
}

/**
 * Implementation of SummonMutableState that notifies observers when its value changes.
 * @param initialValue The initial value of this state
 */
class MutableStateImpl<T>(initialValue: T) : SummonMutableState<T> {
    private val listeners = mutableListOf<(T) -> Unit>()
    
    override var value: T = initialValue
        set(newValue) {
            if (field != newValue) {
                field = newValue
                notifyListeners()
            }
        }
    
    /**
     * Adds a listener that will be called whenever the state value changes.
     * @param listener A function that receives the new value
     */
    fun addListener(listener: (T) -> Unit) {
        listeners.add(listener)
    }
    
    /**
     * Removes a previously added listener.
     * @param listener The listener to remove
     */
    fun removeListener(listener: (T) -> Unit) {
        listeners.remove(listener)
    }
    
    private fun notifyListeners() {
        listeners.forEach { it(value) }
    }
}

/**
 * Creates a new SummonMutableState instance with the given initial value.
 * @param initialValue The initial value of the state
 * @return A SummonMutableState holding the initial value
 */
fun <T> mutableStateOf(initialValue: T): SummonMutableState<T> = 
    MutableStateImpl(initialValue) 
