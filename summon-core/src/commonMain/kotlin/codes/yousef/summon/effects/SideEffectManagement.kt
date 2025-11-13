package codes.yousef.summon.effects

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.LaunchedEffect
import codes.yousef.summon.state.SummonMutableState

/**
 * A dummy job interface for API compatibility.
 */
interface JobLike {
    fun cancel()
}

/**
 * A simple job implementation.
 */
class SimpleJob : JobLike {
    override fun cancel() {
        // No-op implementation
    }
}

/**
 * Launch a coroutine scoped to the composition.
 *
 * @param block The coroutine code to execute
 * @return The JobLike representing the coroutine
 */
@Composable
fun CompositionScope.launchEffect(
    block: suspend () -> Unit
): JobLike {
    compose {
        LaunchedEffect(Unit) {
            block()
        }
    }

    return SimpleJob()
}

/**
 * Launch a coroutine with dependencies.
 *
 * @param dependencies Objects that will trigger the coroutine when they change
 * @param block The coroutine code to execute
 * @return The JobLike representing the coroutine
 */
@Composable
fun CompositionScope.launchEffectWithDeps(
    vararg dependencies: Any?,
    block: suspend () -> Unit
): JobLike {
    compose {
        LaunchedEffect(dependencies) {
            block()
        }
    }

    return SimpleJob()
}

/**
 * Execute an async effect with cleanup.
 *
 * @param effect The async effect that returns a cleanup function
 */
@Composable
fun CompositionScope.asyncEffect(
    effect: () -> (() -> Unit)
) {
    compose {
        DisposableEffect(Unit) {
            val cleanup = effect()
            cleanup
        }
    }
}

/**
 * Execute an async effect when dependencies change.
 *
 * @param dependencies Objects that will trigger the effect when they change
 * @param effect The async effect that returns a cleanup function
 */
@Composable
fun CompositionScope.asyncEffectWithDeps(
    vararg dependencies: Any?,
    effect: () -> (() -> Unit)
) {
    compose {
        DisposableEffect(dependencies) {
            val cleanup = effect()
            cleanup
        }
    }
}

/**
 * Safe state updates with cancellation handling.
 *
 * @param state The state to update
 * @param block The async operation that produces the new state value
 * @return The JobLike representing the async operation
 */
@Composable
fun <T> CompositionScope.updateStateAsync(
    state: SummonMutableState<T>,
    block: suspend () -> T
): JobLike {
    compose {
        LaunchedEffect(Unit) {
            try {
                val result = block()
                state.value = result
            } catch (e: Exception) {
                // Handle exceptions
            }
        }
    }

    return SimpleJob()
}
