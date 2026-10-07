package codes.yousef.summon.state

import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * An explicitly owned flow subscription. [dispose] stops collection without canceling the
 * caller's scope. Use that scope's UI dispatcher when the state is consumed by a renderer.
 */
interface FlowBinding<T> {
    /** Current state; StateFlow bindings expose the source's initial value immediately. */
    val state: State<T>

    /** Detaches this binding permanently. Repeated calls have no effect. */
    fun dispose()
}

/** A two-way binding; writes to [state] update the source until disposal. */
interface MutableFlowBinding<T> : FlowBinding<T> {
    /** The property declaration value. */
    override val state: SummonMutableState<T>
}

/**
 * Collects a read-only [flow] in a child of [scope].
 *
 * The current [StateFlow.value] is exposed synchronously. Dispose the returned binding when its
 * view leaves composition. Disposal does not cancel [scope], and a canceled parent scope is never
 * replaced.
 *
 * @param flow source retained only until disposal or parent cancellation
 * @param scope caller-owned scope whose dispatcher must be safe for renderer state writes
 * @return an idempotently disposable, read-only binding
 * @sample codes.yousef.summon.state.ownedFlowBindingSample
 */
fun <T> bindStateFlow(flow: StateFlow<T>, scope: CoroutineScope): FlowBinding<T> =
    OwnedFlowBinding(flow, flow.value, scope).readOnlyView()

/**
 * Connects [flow] and UI state with structural-equality feedback suppression.
 *
 * Writes to the returned state update [flow] while the binding is active. Parent cancellation and
 * explicit disposal both detach collection and reverse writes without canceling the caller's
 * scope.
 *
 * @param flow mutable source and destination
 * @param scope caller-owned scope whose dispatcher must be safe for renderer state writes
 * @return an idempotently disposable two-way binding
 * @sample codes.yousef.summon.state.ownedFlowBindingSample
 */
fun <T> bindMutableStateFlow(
    flow: MutableStateFlow<T>,
    scope: CoroutineScope
): MutableFlowBinding<T> = OwnedFlowBinding(flow, flow.value, scope, flow)

/**
 * Collects [flow], exposing exactly [initialValue] until the first event arrives.
 *
 * @param flow shared event source retained only until disposal or parent cancellation
 * @param initialValue deterministic state before the first event
 * @param scope caller-owned scope whose dispatcher must be safe for renderer state writes
 * @return an idempotently disposable, read-only binding
 * @sample codes.yousef.summon.state.ownedFlowBindingSample
 */
fun <T> bindSharedFlow(
    flow: SharedFlow<T>,
    initialValue: T,
    scope: CoroutineScope
): FlowBinding<T> = OwnedFlowBinding(flow, initialValue, scope).readOnlyView()

private class OwnedFlowBinding<T>(
    flow: Flow<T>,
    initialValue: T,
    scope: CoroutineScope,
    private val writableFlow: MutableStateFlow<T>? = null
) : MutableFlowBinding<T> {
    private val lock = ReentrantLock()
    private var active = true
    private val sourceState = mutableStateOf(initialValue)
    private val bindingJob = Job(scope.coroutineContext[Job])

    // The wrapper serializes reads/writes/disposal. Exposing the underlying mutable state
    // would allow a racing listener to write back after disposal has returned.
    override val state: SummonMutableState<T> = object : SummonMutableState<T> {
        override var value: T
            get() = lock.withLock { sourceState.value }
            set(value) = lock.withLock {
                if (active && bindingJob.isActive) {
                    sourceState.value = value
                    if (writableFlow?.value != value) writableFlow?.value = value
                }
            }
    }

    init {
        bindingJob.invokeOnCompletion { deactivate() }
        CoroutineScope(scope.coroutineContext + bindingJob).launch {
            try {
                flow.collect { value ->
                    lock.withLock {
                        if (active && bindingJob.isActive) sourceState.value = value
                    }
                }
            } finally {
                deactivate()
                bindingJob.complete()
            }
        }
    }

    private fun deactivate() = lock.withLock { active = false }

    override fun dispose() {
        deactivate()
        bindingJob.cancel()
    }
}

private fun <T> MutableFlowBinding<T>.readOnlyView(): FlowBinding<T> {
    val owner = this
    return object : FlowBinding<T> {
        override val state: State<T> = object : State<T> {
            override val value: T get() = owner.state.value
        }
        override fun dispose() = owner.dispose()
    }
}
