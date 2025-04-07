package code.yousef.summon.runtime

/**
 * Immutable state object that can be read but not modified.
 * 
 * This interface defines a state holder that provides value access
 * through the value property and component operators.
 */
interface State<T> {
    /**
     * The current value of this state.
     */
    val value: T
    
    /**
     * Component operator for property destructuring
     */
    operator fun component1(): T = value
}

/**
 * Mutable extension of State that can be both read and modified.
 *
 * This interface extends State to allow value modification through
 * the value property and component operators.
 */
interface MutableState<T> : State<T> {
    /**
     * The current value of this mutable state.
     */
    override var value: T
    
    /**
     * Component operator for property destructuring
     */
    operator fun component2(): (T) -> Unit
}

/**
 * Simple implementation of MutableState
 */
internal class MutableStateImpl<T>(override var value: T) : MutableState<T> {
    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }
}

/**
 * Creates a new mutable state with the specified initial value.
 */
fun <T> mutableStateOf(initialValue: T): MutableState<T> = MutableStateImpl(initialValue)

/**
 * Extension property to make accessing a State's value more ergonomic.
 * This allows writing `state()` instead of `state.value`.
 */
operator fun <T> State<T>.invoke(): T = this.value

/**
 * Extension function to update a MutableState's value.
 * This allows writing `state(newValue)` instead of `state.value = newValue`.
 */
operator fun <T> MutableState<T>.invoke(newValue: T) {
    this.value = newValue
}

