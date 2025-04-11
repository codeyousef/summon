package code.yousef.summon.effects

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.LaunchedEffect
import code.yousef.summon.runtime.DisposableEffect
import code.yousef.summon.state.SummonMutableState
import code.yousef.summon.state.mutableStateOf

/**
 * Launch a coroutine scoped to the composition
 * 
 * @param block The coroutine code to execute
 * @return A dummy Job object for compatibility
 */
@Composable
fun CompositionScope.launchEffect(
    block: suspend () -> Unit
): DummyJob {
    LaunchedEffect(Unit) {
        block()
    }
    
    return DummyJob()
}

/**
 * A dummy Job implementation for compatibility
 */
class DummyJob {
    fun cancel() {
        // No implementation in this simplified version
    }
}

/**
 * Launch a coroutine with dependencies
 * 
 * @param dependencies Objects that will trigger the coroutine when they change
 * @param block The coroutine code to execute
 * @return A dummy Job object for compatibility
 */
@Composable
fun CompositionScope.launchEffectWithDeps(
    vararg dependencies: Any?,
    block: suspend () -> Unit
): DummyJob {
    LaunchedEffect(dependencies) {
        block()
    }
    
    return DummyJob()
}

/**
 * A simplified Promise-like interface for JavaScript compatibility
 */
interface Promise<T> {
    fun then(onFulfilled: (T) -> Unit): Promise<T>
    fun catch(onRejected: (Throwable) -> Unit): Promise<T>
}

/**
 * A simple implementation of the Promise interface
 */
class SimplePromise<T>(private val value: T) : Promise<T> {
    private val thenCallbacks = mutableListOf<(T) -> Unit>()
    private val catchCallbacks = mutableListOf<(Throwable) -> Unit>()
    
    override fun then(onFulfilled: (T) -> Unit): Promise<T> {
        thenCallbacks.add(onFulfilled)
        onFulfilled(value)
        return this
    }
    
    override fun catch(onRejected: (Throwable) -> Unit): Promise<T> {
        catchCallbacks.add(onRejected)
        return this
    }
}

/**
 * Execute an async effect with cleanup
 * 
 * @param effect The effect to execute, returning a Promise with a cleanup function
 */
@Composable
fun CompositionScope.asyncEffect(
    effect: () -> Promise<() -> Unit>
) {
    DisposableEffect(Unit) {
        var cleanupFn: (() -> Unit)? = null
        
        try {
            val promise = effect()
            promise.then { cleanup ->
                cleanupFn = cleanup
            }
        } catch (e: Exception) {
            // Log error
        }
        
        // Return the cleanup function for DisposableEffect
        { cleanupFn?.invoke() }
    }
}

/**
 * Execute an async effect when dependencies change
 * 
 * @param dependencies Objects that will trigger the effect when they change
 * @param effect The effect to execute, returning a Promise with a cleanup function
 */
@Composable
fun CompositionScope.asyncEffectWithDeps(
    vararg dependencies: Any?,
    effect: () -> Promise<() -> Unit>
) {
    DisposableEffect(dependencies) {
        var cleanupFn: (() -> Unit)? = null
        
        try {
            val promise = effect()
            promise.then { cleanup ->
                cleanupFn = cleanup
            }
        } catch (e: Exception) {
            // Log error
        }
        
        // Return the cleanup function for DisposableEffect
        { cleanupFn?.invoke() }
    }
}

/**
 * Safe state updates with cancellation handling
 * 
 * @param state The state to update
 * @param block The async operation that produces the new state value
 * @return A dummy Job object for compatibility
 */
@Composable
fun <T> CompositionScope.updateStateAsync(
    state: SummonMutableState<T>,
    block: suspend () -> T
): DummyJob {
    LaunchedEffect(Unit) {
        try {
            val result = block()
            state.value = result
        } catch (e: Exception) {
            // Handle exceptions
        }
    }
    
    return DummyJob()
} 