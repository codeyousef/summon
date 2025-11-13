/**
 * # Summon Annotation Package
 *
 * This package contains core annotations for the Summon UI framework.
 *
 * ## Overview
 *
 * The annotation package provides the fundamental annotations that enable
 * the composition system in Summon. The primary annotation is [Composable],
 * which marks functions as participating in the declarative UI composition.
 *
 * ## Key Concepts
 *
 * - **Composable Functions**: Functions marked with [@Composable] that can
 *   participate in the composition tree and reactive recomposition
 * - **Composition**: The process of building and maintaining a tree of UI
 *   elements that can be efficiently updated when state changes
 * - **Recomposition**: The process of re-executing composable functions
 *   when their dependencies change
 *
 * @since 1.0.0
 */
package codes.yousef.summon.annotation

/**
 * Marks a function as composable, enabling it to participate in the Summon composition system.
 *
 * Composable functions are the building blocks of Summon UI applications. They describe
 * what the UI should look like for a given state, and the framework automatically handles
 * updating the UI when state changes through a process called recomposition.
 *
 * ## Key Characteristics
 *
 * - **Declarative**: Composable functions describe what the UI should look like
 *   rather than how to build it imperatively
 * - **Reactive**: Automatically recompose when observed state changes
 * - **Hierarchical**: Can only be called from other composable functions,
 *   forming a composition tree
 * - **Idempotent**: Should produce the same result when called multiple times
 *   with the same inputs
 *
 * ## Usage Rules
 *
 * 1. Composable functions can only be called from within other composable functions
 * 2. They should be side-effect free or use proper effect APIs for side effects
 * 3. They should not perform long-running operations synchronously
 * 4. State should be managed through the state management APIs like [remember] and [mutableStateOf]
 *
 * ## Example
 *
 * ```kotlin
 * @Composable
 * fun Greeting(name: String) {
 *     Text("Hello, $name!")
 * }
 *
 * @Composable
 * fun App() {
 *     var name by remember { mutableStateOf("World") }
 *
 *     Column {
 *         Greeting(name = name)
 *         Button(
 *             onClick = { name = "Summon" },
 *             label = "Change Name"
 *         )
 *     }
 * }
 * ```
 *
 * ## Architecture Integration
 *
 * Composable functions integrate with several framework systems:
 *
 * - **Composer**: Tracks composition state and manages recomposition
 * - **Recomposer**: Schedules and executes recomposition when state changes
 * - **PlatformRenderer**: Renders composable output to platform-specific formats
 * - **State Management**: Integrates with reactive state holders
 * - **Effects**: Manages side effects through LaunchedEffect, DisposableEffect, etc.
 *
 * @see codes.yousef.summon.runtime.Composer
 * @see codes.yousef.summon.runtime.Recomposer
 * @see codes.yousef.summon.runtime.PlatformRenderer
 * @see codes.yousef.summon.runtime.remember
 * @see codes.yousef.summon.state.mutableStateOf
 * @see codes.yousef.summon.runtime.LaunchedEffect
 * @see codes.yousef.summon.runtime.DisposableEffect
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY_GETTER
)
annotation class Composable 