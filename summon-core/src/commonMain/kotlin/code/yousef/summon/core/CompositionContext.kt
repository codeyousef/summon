/**
 * # Composition Context
 *
 * Core composition context management for the Summon framework.
 * This module provides the fundamental infrastructure for managing
 * composition state, renderer integration, and cross-platform rendering.
 *
 * ## Core Components
 *
 * ### CompositionContext
 * - **Hierarchical Structure**: Parent-child context relationships
 * - **Depth Tracking**: Composition nesting level management
 * - **Renderer Integration**: Platform-specific renderer association
 * - **Thread Safety**: Thread-local context storage
 *
 * ### Renderer Interface
 * - **Platform Abstraction**: Unified rendering interface
 * - **Resource Management**: Automatic cleanup and disposal
 * - **Target Flexibility**: Support for DOM, string, and file outputs
 *
 * ### Platform Utilities
 * - **Cross-Platform Rendering**: Unified API across JVM and JavaScript
 * - **Hydration Support**: Client-side hydration of server-rendered content
 * - **Error Handling**: Comprehensive error reporting and recovery
 *
 * ## Usage Patterns
 *
 * ### Basic Composition
 * ```kotlin
 * val context = CompositionContext.createRoot(renderer)
 * CompositionContext.withContext(context) {
 *     // Compose UI within this context
 *     MyComponent()
 * }
 * ```
 *
 * ### Nested Composition
 * ```kotlin
 * val rootContext = CompositionContext.createRoot(renderer)
 * CompositionContext.withContext(rootContext) {
 *     val childContext = rootContext.createChildContext()
 *     CompositionContext.withContext(childContext) {
 *         // Nested composition
 *         NestedComponent()
 *     }
 * }
 * ```
 *
 * ### Platform-Specific Rendering
 * ```kotlin
 * // JavaScript platform - DOM rendering
 * val domRenderer = RenderUtils.renderComposable(containerElement) {
 *     App()
 * }
 *
 * // JVM platform - String rendering
 * val htmlString = RenderUtils.renderToString {
 *     App()
 * }
 *
 * // Hydration on client
 * val hydratedRenderer = RenderUtils.hydrate(containerElement) {
 *     App()
 * }
 * ```
 *
 * ## Architecture Features
 *
 * ### Thread-Local Context
 * - **Isolation**: Each thread maintains its own context
 * - **Safety**: Prevents context mixing between concurrent compositions
 * - **Performance**: Fast context access without synchronization
 *
 * ### Hierarchical Composition
 * - **Nesting Support**: Unlimited composition depth
 * - **Context Inheritance**: Child contexts inherit from parents
 * - **Scope Management**: Clear composition boundaries
 *
 * ### Renderer Abstraction
 * - **Platform Independence**: Same code works across platforms
 * - **Output Flexibility**: Multiple output formats (DOM, HTML, files)
 * - **Resource Management**: Automatic cleanup and memory management
 *
 * ## Performance Considerations
 *
 * - **Minimal Overhead**: Lightweight context management
 * - **Thread-Local Storage**: Fast context access
 * - **Lazy Initialization**: Contexts created only when needed
 * - **Memory Efficiency**: Automatic cleanup of unused contexts
 *
 * ## Error Handling
 *
 * ```kotlin
 * try {
 *     val result = RenderUtils.renderToString {
 *         PotentiallyFailingComponent()
 *     }
 * } catch (e: SummonRenderException) {
 *     // Handle rendering errors gracefully
 *     handleRenderError(e)
 * }
 * ```
 *
 * ## Cross-Platform Implementation
 *
 * - **JavaScript**: DOM-based rendering with hydration support
 * - **JVM**: String and file-based rendering for SSR
 * - **Consistent API**: Same composition patterns across platforms
 *
 * @see Composer for the composition engine
 * @see PlatformRenderer for platform-specific rendering
 * @see Recomposer for recomposition management
 * @since 1.0.0
 */
package code.yousef.summon.core

import code.yousef.summon.core.CompositionContext.Companion.withContext
import code.yousef.summon.runtime.Composable

/**
 * Platform-specific holder for thread-local data.
 *
 * This abstraction enables thread-local storage across different platforms,
 * ensuring composition context isolation in multi-threaded environments.
 *
 * ## Platform Implementations
 *
 * - **JVM**: Uses Java's ThreadLocal
 * - **JavaScript**: Uses global variable (single-threaded environment)
 *
 * @param T The type of data to store in thread-local storage
 * @since 1.0.0
 */
expect class ThreadLocalHolder<T>() {
    /**
     * Gets the current thread-local value.
     *
     * @return The current value, or null if not set
     */
    fun get(): T?

    /**
     * Sets the thread-local value.
     *
     * @param value The value to set, or null to clear
     */
    fun set(value: T?)
}

/**
 * Manages the current composition state during rendering.
 *
 * The CompositionContext provides the execution environment for composable
 * functions, tracking composition hierarchy, managing renderers, and
 * maintaining composition state across recomposition cycles.
 *
 * ## Key Features
 *
 * - **Hierarchical Structure**: Supports nested composition contexts
 * - **Renderer Integration**: Associates compositions with specific renderers
 * - **Thread Safety**: Uses thread-local storage for context isolation
 * - **Resource Management**: Automatic cleanup and lifecycle management
 *
 * ## Usage
 *
 * ```kotlin
 * val rootContext = CompositionContext.createRoot(myRenderer)
 * CompositionContext.withContext(rootContext) {
 *     // All composition happens within this context
 *     MyApp()
 * }
 * ```
 *
 * @property parent The parent context, null for root contexts
 * @property depth The nesting depth in the composition hierarchy
 * @property renderer The renderer associated with this context
 * @see Renderer for rendering interface
 * @see withContext for context execution
 * @since 1.0.0
 */
class CompositionContext private constructor(
    /**
     * The parent context, if this is a child context.
     */
    val parent: CompositionContext?,

    /**
     * The depth of this context in the composition hierarchy.
     */
    val depth: Int,

    /**
     * The renderer associated with this context.
     */
    val renderer: Renderer<*>
) {
    companion object {
        private val threadLocal = ThreadLocalHolder<CompositionContext>()

        /**
         * Returns the current composition context, or null if no composition is in progress.
         */
        val current: CompositionContext?
            get() = threadLocal.get()

        /**
         * Executes a block with a specific composition context.
         *
         * @param context The context to use
         * @param block The block to execute
         * @return The result of the block
         */
        fun <T> withContext(context: CompositionContext, block: () -> T): T {
            val previous = threadLocal.get()
            threadLocal.set(context)
            try {
                return block()
            } finally {
                threadLocal.set(previous)
            }
        }

        /**
         * Creates a root composition context.
         *
         * @param renderer The renderer to use
         * @return A new root composition context
         */
        internal fun createRoot(renderer: Renderer<*>): CompositionContext {
            return CompositionContext(null, 0, renderer)
        }
    }

    /**
     * Creates a child context with this context as the parent.
     *
     * @return A new child composition context
     */
    fun createChildContext(): CompositionContext {
        return CompositionContext(this, depth + 1, renderer)
    }

    /**
     * The composables currently being composed in this context.
     */
    private val composables = mutableListOf<Any>()

    /**
     * Adds a composable to this context.
     *
     * @param composable The composable to add
     */
    internal fun addComposable(composable: Any) {
        composables.add(composable)
    }

    /**
     * Gets all composables in this context.
     *
     * @return A list of all composables
     */
    internal fun getComposables(): List<Any> {
        return composables.toList()
    }
}

/**
 * Interface for renderers that can render composables to a specific target.
 */
interface Renderer<T> {
    /**
     * Renders a composable to the target type.
     *
     * @param composable The composable to render
     * @return The rendered result
     */
    fun render(composable: @Composable () -> Unit): T

    /**
     * Disposes of any resources used by this renderer.
     */
    fun dispose()
}

/**
 * Utility functions for rendering components.
 */
expect object RenderUtils {
    /**
     * Renders a composable to a DOM element (JavaScript platform).
     *
     * @param container The container element
     * @param composable The composable to render
     * @return A renderer instance
     */
    fun renderComposable(container: Any, composable: @Composable () -> Unit): Renderer<Any>

    /**
     * Hydrates a server-rendered DOM tree with a composable (JavaScript platform).
     *
     * @param container The container element
     * @param composable The composable to hydrate with
     * @return A renderer instance
     */
    fun hydrate(container: Any, composable: @Composable () -> Unit): Renderer<Any>

    /**
     * Renders a composable to a string (JVM platform).
     *
     * @param composable The composable to render
     * @return The rendered HTML string
     */
    fun renderToString(composable: @Composable () -> Unit): String

    /**
     * Renders a composable to a file (JVM platform).
     *
     * @param composable The composable to render
     * @param file The file to write to
     */
    fun renderToFile(composable: @Composable () -> Unit, file: Any)
}

/**
 * Exception thrown when there is an error during rendering.
 */
class SummonRenderException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) 
