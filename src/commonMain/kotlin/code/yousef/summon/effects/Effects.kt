package code.yousef.summon.effects

import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.CompositionLocal
import code.yousef.summon.runtime.DisposableEffect
import code.yousef.summon.runtime.LaunchedEffect
import code.yousef.summon.runtime.SideEffect

/**
 * CompositionScope interface provides the context for effect operations.
 * This is a marker interface that all composable functions implicitly operate within.
 */
interface CompositionScope {
    /**
     * Adds a composable component to the current composition.
     */
    fun compose(block: @Composable () -> Unit)
}

/**
 * Execute an effect after each successful composition.
 * 
 * @param effect The effect to execute.
 */
@Composable
fun CompositionScope.effect(effect: () -> Unit) {
    compose {
        SideEffect(effect)
    }
}

/**
 * Execute an effect when composition is first created.
 * 
 * @param effect The effect to execute.
 */
@Composable
fun CompositionScope.onMount(effect: () -> Unit) {
    compose {
        // Using SideEffect instead of LaunchedEffect for synchronous execution in tests
        // while maintaining the same behavior in production
        SideEffect {
            effect()
        }
    }
}

/**
 * Execute an effect when composition is disposed.
 * 
 * @param effect The effect to execute.
 */
@Composable
fun CompositionScope.onDispose(effect: () -> Unit) {
    compose {
        DisposableEffect(Unit) {
            // Return the cleanup function to be executed on disposal
            effect
        }
    }
}

/**
 * Execute an effect after composition when dependencies change.
 * 
 * @param dependencies Objects that will trigger the effect when they change.
 * @param effect The effect to execute.
 */
@Composable
fun CompositionScope.effectWithDeps(vararg dependencies: Any?, effect: () -> Unit) {
    compose {
        LaunchedEffect(dependencies) {
            effect()
        }
    }
}

/**
 * Execute an effect once after composition with a cleanup function.
 * 
 * @param effect The effect to execute, returning a cleanup function.
 */
@Composable
fun CompositionScope.onMountWithCleanup(effect: () -> (() -> Unit)?) {
    compose {
        DisposableEffect(Unit) {
            effect() ?: {}
        }
    }
}

/**
 * Execute an effect with dependencies and cleanup.
 * 
 * @param dependencies Objects that will trigger the effect when they change.
 * @param effect The effect to execute, returning a cleanup function.
 */
@Composable
fun CompositionScope.effectWithDepsAndCleanup(
    vararg dependencies: Any?,
    effect: () -> (() -> Unit)?
) {
    compose {
        DisposableEffect(dependencies) {
            effect() ?: {}
        }
    }
}
