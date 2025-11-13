package codes.yousef.summon.runtime

import codes.yousef.summon.core.FlowContent
import codes.yousef.summon.runtime.CompositionLocal.compositionLocalOf
import codes.yousef.summon.runtime.CompositionLocal.currentComposer
import codes.yousef.summon.runtime.CompositionLocal.setCurrentComposer
import codes.yousef.summon.runtime.CompositionLocal.staticCompositionLocalOf

/**
 * Provides access to composition-local values and manages the current composition context.
 *
 * CompositionLocal is the central registry for implicit context propagation through the
 * composition tree. It provides a mechanism for sharing values down the composition hierarchy
 * without explicitly passing them as parameters to each composable function.
 *
 * ## Core Functionality
 *
 * ### Composer Management
 * - Tracks the currently active [Composer] during composition
 * - Provides safe access to composition state and lifecycle management
 * - Coordinates with the [Recomposer] for change tracking
 *
 * ### Context Propagation
 * - Enables creation of typed composition locals with [compositionLocalOf]
 * - Supports required composition locals with [staticCompositionLocalOf]
 * - Maintains a hierarchy of provided values through the composition tree
 *
 * ## Usage Patterns
 *
 * ### Creating Composition Locals
 * ```kotlin
 * // With default value
 * val LocalTheme = CompositionLocal.compositionLocalOf(defaultTheme)
 *
 * // Required value (throws if not provided)
 * val LocalDatabase = CompositionLocal.staticCompositionLocalOf<Database>()
 * ```
 *
 * ### Providing Values
 * ```kotlin
 * @Composable
 * fun App() {
 *     CompositionLocalProvider(LocalTheme provides customTheme) {
 *         // All child composables can access customTheme
 *         HomePage()
 *     }
 * }
 * ```
 *
 * ### Consuming Values
 * ```kotlin
 * @Composable
 * fun ThemedButton() {
 *     val theme = LocalTheme.current
 *     Button(
 *         modifier = Modifier().backgroundColor(theme.primaryColor)
 *     ) {
 *         Text("Themed Button")
 *     }
 * }
 * ```
 *
 * ## Common Use Cases
 *
 * - **Theming**: Propagate theme objects through the component tree
 * - **Localization**: Share locale and resource bundles
 * - **Configuration**: Distribute app-wide configuration settings
 * - **Dependency Injection**: Provide services and repositories
 * - **Platform Context**: Share platform-specific rendering context
 *
 * ## Thread Safety
 *
 * CompositionLocal operations are thread-safe within the composition context.
 * The current composer is managed per-thread during composition execution.
 *
 * ## Integration with Framework
 *
 * - **Recomposer**: Coordinates with recomposition scheduling
 * - **Platform Renderer**: Provides rendering context (e.g., FlowContent)
 * - **Effects**: Supports effect lifecycle through composer access
 * - **State**: Enables state management within composition context
 *
 * @see CompositionLocalProvider
 * @see Composer
 * @see Recomposer
 * @since 1.0.0
 */
object CompositionLocal {
    /**
     * The currently active composer, or null if no composition is in progress.
     *
     * This field is managed internally and tracks the [Composer] instance
     * currently executing composition. It's used to provide composition
     * context to composable functions and state management systems.
     *
     * @since 1.0.0
     */
    private var _currentComposer: Composer? = null

    /**
     * Gets the current composer instance that is actively executing composition.
     *
     * This property provides access to the [Composer] that is currently managing
     * the composition process. It returns null when no composition is in progress,
     * such as outside composable functions or between composition passes.
     *
     * ## Usage in Framework
     *
     * The current composer is used by:
     * - [remember] functions for slot-based value storage
     * - State objects for dependency tracking
     * - Effect functions for lifecycle management
     * - Composable functions for managing their execution context
     *
     * ## Thread Safety
     *
     * This property is thread-local within the composition context. Each
     * composition thread maintains its own current composer.
     *
     * @return The active [Composer] instance, or null if no composition is active
     * @see setCurrentComposer
     * @since 1.0.0
     */
    val currentComposer: Composer?
        get() = _currentComposer

    /**
     * Sets the current composer for the composition context.
     *
     * This method establishes the active [Composer] for the current composition
     * thread. It coordinates with the [Recomposer] to ensure proper change
     * tracking and recomposition scheduling.
     *
     * ## Framework Integration
     *
     * When a composer is set:
     * - The [Recomposer] is notified to track the active composer
     * - Composition state becomes available to composable functions
     * - State reads/writes are properly tracked for recomposition
     *
     * ## Thread Context
     *
     * The composer is set per-thread during composition execution:
     * - Each composition thread has its own current composer
     * - Switching threads requires re-establishing the composer context
     * - The framework handles this automatically during normal composition
     *
     * @param composer The [Composer] to set as current, or null to clear
     * @see currentComposer
     * @see Recomposer.setActiveComposer
     * @since 1.0.0
     */
    internal fun setCurrentComposer(composer: Composer?) {
        _currentComposer = composer

        // Also update the recomposer
        if (composer != null && Recomposer.isComposerImpl(composer)) {
            RecomposerHolder.recomposer.setActiveComposer(Recomposer.asComposerImpl(composer))
        } else {
            RecomposerHolder.recomposer.setActiveComposer(null)
        }
    }

    /**
     * Temporarily sets the current composer and executes a block within that composition context.
     *
     * This method provides a scoped composition context where the specified [Composer]
     * is active during the execution of the block. After the block completes, the
     * previous composer is restored, ensuring proper context isolation.
     *
     * ## Use Cases
     *
     * ### Framework Integration
     * Used internally by the framework for:
     * - Establishing composition context for root composables
     * - Switching composers during recomposition
     * - Testing scenarios with specific composer instances
     *
     * ### Custom Composition Roots
     * ```kotlin
     * CompositionLocal.provideComposer(customComposer) {
     *     // Execute composables with the custom composer
     *     MyRootComponent()
     * }
     * ```
     *
     * ## Exception Safety
     *
     * The method uses try-finally to ensure the previous composer is always
     * restored, even if an exception occurs during block execution. This
     * prevents composition context corruption.
     *
     * ## Nesting Support
     *
     * Multiple calls can be nested safely:
     * ```kotlin
     * CompositionLocal.provideComposer(composer1) {
     *     // Context: composer1
     *     CompositionLocal.provideComposer(composer2) {
     *         // Context: composer2
     *     }
     *     // Context: composer1 (restored)
     * }
     * ```
     *
     * @param R The return type of the block
     * @param composer The [Composer] to set as current during block execution
     * @param block The function to execute within the composition context
     * @return The result returned by the block
     * @see setCurrentComposer
     * @see currentComposer
     * @since 1.0.0
     */
    fun <R> provideComposer(composer: Composer, block: () -> R): R {
        val previous = _currentComposer
        setCurrentComposer(composer)
        try {
            return block()
        } finally {
            setCurrentComposer(previous)
        }
    }

    /**
     * Creates a CompositionLocal with a default value.
     *
     * This function creates a [CompositionLocalProvider] that provides a fallback
     * value when no explicit provider is present in the composition hierarchy.
     * This makes the composition local safe to access anywhere in the tree.
     *
     * ## Usage Pattern
     *
     * ```kotlin
     * // Create with default value
     * val LocalTheme = CompositionLocal.compositionLocalOf(defaultTheme)
     *
     * @Composable
     * fun Component() {
     *     // Safe to access - returns defaultTheme if not provided
     *     val theme = LocalTheme.current
     *     Text("Color: ${theme.primaryColor}")
     * }
     * ```
     *
     * ## When to Use
     *
     * - For optional configuration that has sensible defaults
     * - When you want to provide fallback behavior
     * - For values that might not always be provided by parent composables
     * - When creating reusable components that work in any context
     *
     * ## Default Value Considerations
     *
     * The default value is used when:
     * - No provider is present in the composition hierarchy
     * - The composable is used outside of a provider scope
     * - Testing scenarios where providers aren't set up
     *
     * @param T The type of value provided by this composition local
     * @param defaultValue The fallback value used when no provider exists
     * @return A [CompositionLocalProvider] that provides the default value when unprovided
     * @see staticCompositionLocalOf for required composition locals
     * @since 1.0.0
     */
    fun <T> compositionLocalOf(defaultValue: T): CompositionLocalProvider<T> =
        CompositionLocalProviderImpl(defaultValue)

    /**
     * Creates a static CompositionLocal that requires an explicit provider.
     *
     * This function creates a [CompositionLocalProvider] that must be explicitly
     * provided in the composition hierarchy. Accessing the value without a provider
     * will throw an exception, making this suitable for required dependencies.
     *
     * ## Usage Pattern
     *
     * ```kotlin
     * // Create without default - requires provider
     * val LocalDatabase = CompositionLocal.staticCompositionLocalOf<Database>()
     *
     * @Composable
     * fun App() {
     *     CompositionLocalProvider(LocalDatabase provides myDatabase) {
     *         UserList() // Can safely access LocalDatabase.current
     *     }
     * }
     * ```
     *
     * ## When to Use
     *
     * - For critical dependencies that must be provided
     * - When there's no sensible default value
     * - For platform-specific services that require explicit setup
     * - When you want to enforce dependency injection patterns
     *
     * ## Error Behavior
     *
     * Accessing [CompositionLocalProvider.current] without a provider throws:
     * ```
     * IllegalStateException: CompositionLocal not provided
     * ```
     *
     * ## Design Benefits
     *
     * - **Compile-time Safety**: Forces consideration of dependency provision
     * - **Clear Dependencies**: Makes required dependencies explicit
     * - **Fail-Fast**: Errors occur early rather than with unexpected defaults
     * - **Testing**: Forces proper test setup with realistic dependencies
     *
     * @param T The type of value provided by this composition local
     * @return A [CompositionLocalProvider] that requires explicit provision
     * @throws IllegalStateException when accessed without a provider
     * @see compositionLocalOf for composition locals with defaults
     * @since 1.0.0
     */
    fun <T> staticCompositionLocalOf(): CompositionLocalProvider<T> =
        StaticCompositionLocalProviderImpl()
}

/**
 * Interface for providing values through the composition hierarchy.
 *
 * CompositionLocalProvider enables implicit value propagation down the composition
 * tree without requiring explicit parameter passing. It supports both static
 * (required) and dynamic (default) composition locals.
 *
 * ## Value Access
 *
 * The [current] property provides access to the contextual value:
 * - For static providers: throws if no value is provided
 * - For dynamic providers: returns default value or provided value
 *
 * ## Provider Chain
 *
 * Composition locals form a provider chain through the composition hierarchy:
 * - Child composables inherit values from parent providers
 * - Closer providers override values from distant parents
 * - Each provider creates a new scope for its children
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Create composition local
 * val LocalTheme = CompositionLocal.compositionLocalOf(defaultTheme)
 *
 * @Composable
 * fun App() {
 *     CompositionLocalProvider(LocalTheme provides darkTheme) {
 *         // All children see darkTheme
 *         Page()
 *     }
 * }
 *
 * @Composable
 * fun Page() {
 *     val theme = LocalTheme.current // Gets darkTheme
 *     ThemedContent(theme)
 * }
 * ```
 *
 * @param T The type of value provided by this composition local
 * @see CompositionLocal.compositionLocalOf
 * @see CompositionLocal.staticCompositionLocalOf
 * @since 1.0.0
 */
interface CompositionLocalProvider<T> {
    /**
     * The current value provided by this composition local in the current composition context.
     *
     * This property returns the contextually appropriate value based on the
     * composition hierarchy and provider chain. The behavior depends on the
     * type of composition local:
     *
     * ## Static Composition Locals
     * - Returns the provided value if a provider exists in the hierarchy
     * - Throws [IllegalStateException] if no provider is found
     * - Ensures required dependencies are explicitly provided
     *
     * ## Dynamic Composition Locals
     * - Returns the provided value if a provider exists in the hierarchy
     * - Returns the default value if no provider is found
     * - Provides safe fallback behavior
     *
     * ## Provider Resolution
     *
     * Value resolution follows the composition hierarchy:
     * 1. Check for immediate parent provider
     * 2. Walk up the composition tree looking for providers
     * 3. Use the closest provider's value
     * 4. Fall back to default (dynamic) or throw (static)
     *
     * ## Usage in Composables
     *
     * ```kotlin
     * @Composable
     * fun Component() {
     *     val theme = LocalTheme.current
     *     val database = LocalDatabase.current // May throw if not provided
     *
     *     // Use the provided values
     *     Text(
     *         text = "Hello",
     *         color = theme.textColor
     *     )
     * }
     * ```
     *
     * @return The current contextual value
     * @throws IllegalStateException for static composition locals without providers
     * @since 1.0.0
     */
    val current: T

    /**
     * Creates a new provider instance that provides the specified value.
     *
     * This method is used to create provider instances that supply values
     * to the composition hierarchy. The resulting provider can be used with
     * CompositionLocalProvider to establish a value scope.
     *
     * ## Provider Scoping
     *
     * Each call to [provides] creates a new provider scope:
     * - The value is available to all child composables
     * - The value overrides any parent providers of the same type
     * - Multiple providers can be combined in a single scope
     *
     * ## Example Usage
     *
     * ```kotlin
     * @Composable
     * fun App() {
     *     CompositionLocalProvider(
     *         LocalTheme provides customTheme,
     *         LocalLocale provides Locale.ENGLISH
     *     ) {
     *         // Child composables see both values
     *         Content()
     *     }
     * }
     * ```
     *
     * ## Chaining and Overriding
     *
     * Providers can be nested to override values:
     * ```kotlin
     * CompositionLocalProvider(LocalTheme provides lightTheme) {
     *     // Uses lightTheme
     *     Header()
     *
     *     CompositionLocalProvider(LocalTheme provides darkTheme) {
     *         // Uses darkTheme (overrides lightTheme)
     *         Content()
     *     }
     *
     *     // Uses lightTheme again
     *     Footer()
     * }
     * ```
     *
     * @param value The value to provide to child composables
     * @return A new [CompositionLocalProvider] instance providing the value
     * @since 1.0.0
     */
    fun provides(value: T): CompositionLocalProvider<T>
}

/**
 * Default implementation of CompositionLocalProvider.
 */
private class CompositionLocalProviderImpl<T>(private val defaultValue: T) : CompositionLocalProvider<T> {
    private var value: T = defaultValue

    override val current: T
        get() = value

    override fun provides(value: T): CompositionLocalProvider<T> {
        this.value = value
        return this
    }
}

/**
 * Implementation of CompositionLocalProvider for static values that must be provided.
 */
private class StaticCompositionLocalProviderImpl<T> : CompositionLocalProvider<T> {
    private var value: T? = null

    override val current: T
        get() = value ?: throw IllegalStateException("CompositionLocal not provided")

    override fun provides(value: T): CompositionLocalProvider<T> {
        this.value = value
        return this
    }
}

/**
 * CompositionLocal providing the current kotlinx.html FlowContent context during rendering.
 *
 * This composition local enables platform renderer implementations to access the
 * current HTML rendering context. It's primarily used by JVM-based renderers that
 * generate HTML using the kotlinx.html library.
 *
 * ## Purpose
 *
 * ### HTML Context Tracking
 * - Maintains the current HTML element context during composition
 * - Enables child components to render into the correct parent element
 * - Supports nested HTML structure generation
 *
 * ### Platform Integration
 * - Used by [JvmPlatformRenderer] for server-side HTML generation
 * - Enables seamless integration with kotlinx.html DSL
 * - Supports both static and dynamic HTML generation
 *
 * ## Usage Pattern
 *
 * ```kotlin
 * // Platform renderer sets context
 * fun renderDiv(modifier: Modifier, content: @Composable FlowContent.() -> Unit) {
 *     val currentContext = LocalFlowContent.current
 *     currentContext?.div {
 *         // Set new context for children
 *         CompositionLocalProvider(LocalFlowContent provides this) {
 *             content(this)
 *         }
 *     }
 * }
 * ```
 *
 * ## Framework Integration
 *
 * - **Server-Side Rendering**: Critical for HTML generation on JVM
 * - **Component Nesting**: Enables proper parent-child HTML relationships
 * - **Template Systems**: Integrates with template engines and HTML builders
 * - **Cross-Platform**: Null on platforms that don't use kotlinx.html
 *
 * ## Null Safety
 *
 * The default value is null to accommodate platforms that don't use HTML generation:
 * - Browser/JS platforms typically use direct DOM manipulation
 * - JVM platforms use this for HTML string generation
 * - Custom platforms can provide their own context types
 *
 * @see kotlinx.html.FlowContent
 * @see codes.yousef.summon.runtime.PlatformRenderer
 * @since 1.0.0
 */
val LocalFlowContent = CompositionLocal.compositionLocalOf<FlowContent?>(null) 
