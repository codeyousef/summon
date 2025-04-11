package code.yousef.summon.core

import code.yousef.summon.runtime.Composable

/**
 * Platform-specific holder for thread-local data.
 */
expect class ThreadLocalHolder<T>() {
    fun get(): T?
    fun set(value: T?)
}

/**
 * The CompositionContext class manages the current composition state during rendering.
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
object RenderUtils {
    /**
     * Renders a composable to a DOM element (JavaScript platform).
     * 
     * @param container The container element
     * @param composable The composable to render
     * @return A renderer instance
     */
    fun renderComposable(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        // Platform-specific implementation to be provided
        throw NotImplementedError("Platform-specific implementation required")
    }
    
    /**
     * Hydrates a server-rendered DOM tree with a composable (JavaScript platform).
     * 
     * @param container The container element
     * @param composable The composable to hydrate with
     * @return A renderer instance
     */
    fun hydrate(container: Any, composable: @Composable () -> Unit): Renderer<Any> {
        // Platform-specific implementation to be provided
        throw NotImplementedError("Platform-specific implementation required")
    }
    
    /**
     * Renders a composable to a string (JVM platform).
     * 
     * @param composable The composable to render
     * @return The rendered HTML string
     */
    fun renderToString(composable: @Composable () -> Unit): String {
        // Platform-specific implementation to be provided
        throw NotImplementedError("Platform-specific implementation required")
    }
    
    /**
     * Renders a composable to a file (JVM platform).
     * 
     * @param composable The composable to render
     * @param file The file to write to
     */
    fun renderToFile(composable: @Composable () -> Unit, file: Any) {
        // Platform-specific implementation to be provided
        throw NotImplementedError("Platform-specific implementation required")
    }
}

/**
 * Exception thrown when there is an error during rendering.
 */
class SummonRenderException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) 