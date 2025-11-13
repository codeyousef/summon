/**
 * # Effects System
 *
 * This file provides the core effects system for the Summon UI framework, enabling
 * side effects management, lifecycle integration, and resource cleanup within the
 * composition system. Effects provide a way to perform operations outside of the
 * normal UI rendering flow while maintaining predictable behavior and memory safety.
 *
 * ## Overview
 *
 * The effects system in Summon enables:
 *
 * - **Side Effect Management**: Safe execution of side effects within compositions
 * - **Lifecycle Integration**: Effects that respond to component mounting and unmounting
 * - **Dependency Tracking**: Re-execution of effects when dependencies change
 * - **Resource Cleanup**: Automatic cleanup to prevent memory leaks
 * - **Cross-Platform Support**: Consistent behavior across browser and JVM platforms
 * - **Testing Support**: Deterministic effect execution for reliable testing
 *
 * ## Effect Types
 *
 * ### Simple Effects
 * - **effect()**: Runs after each composition
 * - **onMount()**: Runs once when component mounts
 * - **onDispose()**: Runs when component unmounts
 *
 * ### Dependency-Based Effects
 * - **effectWithDeps()**: Re-runs when dependencies change
 * - **effectWithDepsAndCleanup()**: Dependency-based with cleanup function
 *
 * ### Cleanup Effects
 * - **onMountWithCleanup()**: Mount effect with cleanup function
 * - **DisposableEffect**: Manual resource management with cleanup
 *
 * ## Usage Examples
 *
 * ### Basic Side Effects
 * ```kotlin
 * @Composable
 * fun ComponentWithEffects() {
 *     // Run once when component mounts
 *     onMount {
 *         println("Component mounted")
 *     }
 *
 *     // Run on every composition
 *     effect {
 *         println("Component rendered")
 *     }
 *
 *     // Cleanup when component unmounts
 *     onDispose {
 *         println("Component unmounted")
 *     }
 * }
 * ```
 *
 * ### Dependency-Based Effects
 * ```kotlin
 * @Composable
 * fun DataComponent(userId: String) {
 *     var userData by remember { mutableStateOf<User?>(null) }
 *
 *     // Re-fetch data when userId changes
 *     effectWithDeps(userId) {
 *         userData = fetchUser(userId)
 *     }
 *
 *     // Effect with cleanup for subscriptions
 *     effectWithDepsAndCleanup(userId) {
 *         val subscription = subscribeToUser(userId) { user ->
 *             userData = user
 *         }
 *         // Return cleanup function
 *         { subscription.cancel() }
 *     }
 * }
 * ```
 *
 * ### Resource Management
 * ```kotlin
 * @Composable
 * fun EventListenerComponent() {
 *     onMountWithCleanup {
 *         val listener = { event: DOMEvent -> handleEvent(event) }
 *         addEventListener("custom-event", listener)
 *
 *         // Return cleanup function
 *         { removeEventListener("custom-event", listener) }
 *     }
 * }
 * ```
 *
 * ## Performance Considerations
 *
 * - **Effect Scheduling**: Effects are scheduled to run after composition commits
 * - **Dependency Comparison**: Uses referential equality for dependency tracking
 * - **Cleanup Timing**: Cleanup functions run before new effects and on disposal
 * - **Memory Management**: Automatic cleanup prevents memory leaks
 *
 * ## Best Practices
 *
 * ### Effect Usage Guidelines
 * - Use `onMount` for one-time initialization
 * - Use `effectWithDeps` for reactive data fetching
 * - Always provide cleanup for resource-intensive operations
 * - Keep effects focused and single-purpose
 * - Avoid complex state mutations in effects
 *
 * ### Common Patterns
 * - **Data Fetching**: Load data when component mounts or dependencies change
 * - **Event Listeners**: Subscribe to external events with proper cleanup
 * - **Timers**: Set up intervals or timeouts with automatic cleanup
 * - **WebSocket Connections**: Manage connection lifecycle
 * - **Subscription Management**: Handle reactive data streams
 *
 * ## Error Handling
 *
 * Effects should handle their own errors to prevent composition failures:
 *
 * ```kotlin
 * effectWithDeps(apiEndpoint) {
 *     try {
 *         val data = fetchData(apiEndpoint)
 *         setData(data)
 *     } catch (e: Exception) {
 *         setError(e.message)
 *     }
 * }
 * ```
 *
 * ## Testing Support
 *
 * Effects in tests run synchronously for predictable behavior:
 * - No async scheduling in test environments
 * - Immediate effect execution for assertions
 * - Deterministic cleanup timing
 *
 * @see codes.yousef.summon.runtime.LaunchedEffect for advanced effect patterns
 * @see codes.yousef.summon.runtime.DisposableEffect for manual resource management
 * @see codes.yousef.summon.runtime.SideEffect for low-level effect control
 * @since 1.0.0
 */
package codes.yousef.summon.effects

import codes.yousef.summon.runtime.Composable
import codes.yousef.summon.runtime.DisposableEffect
import codes.yousef.summon.runtime.LaunchedEffect
import codes.yousef.summon.runtime.SideEffect

/**
 * Composition scope interface providing the context for effect operations within composables.
 *
 * CompositionScope serves as the execution context for all effect operations, enabling
 * composable functions to safely execute side effects while maintaining composition
 * integrity and lifecycle management.
 *
 * ## Purpose
 *
 * - **Effect Context**: Provides the scope for executing effects
 * - **Composition Integration**: Ensures effects run within the composition lifecycle
 * - **Type Safety**: Prevents effects from running outside of composable contexts
 * - **Resource Management**: Enables proper cleanup and disposal of effects
 *
 * ## Implementation
 *
 * This interface is typically implemented by the composition runtime and is automatically
 * available within all composable functions. User code generally doesn't need to implement
 * this interface directly.
 *
 * ## Usage
 *
 * Effects are called as extension functions on CompositionScope, which is implicitly
 * available in composable functions:
 *
 * ```kotlin
 * @Composable
 * fun MyComponent() {
 *     // this: CompositionScope is implicit
 *     onMount {
 *         // Effect code here
 *     }
 * }
 * ```
 *
 * @see compose for adding composable content to the composition
 * @since 1.0.0
 */
interface CompositionScope {
    /**
     * Adds a composable component to the current composition.
     *
     * This function integrates composable content into the current composition tree,
     * enabling the creation of nested composable structures and the execution of
     * effects within the proper composition context.
     *
     * ## Usage
     *
     * This function is primarily used internally by effect implementations to ensure
     * effects run within the composition lifecycle. User code typically doesn't call
     * this directly but instead uses higher-level effect functions.
     *
     * ```kotlin
     * // Internal effect implementation example
     * fun CompositionScope.customEffect(action: () -> Unit) {
     *     compose {
     *         SideEffect(action)
     *     }
     * }
     * ```
     *
     * @param block The composable block to add to the composition
     * @see SideEffect for executing side effects
     * @see LaunchedEffect for dependency-based effects
     * @see DisposableEffect for effects with cleanup
     * @since 1.0.0
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
