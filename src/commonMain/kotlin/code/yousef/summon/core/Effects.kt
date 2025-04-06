package code.yousef.summon

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.DisposableEffect

// Counter for generating unique IDs for effects
private var nextEffectId = 0
private fun generateEffectId(): Int = nextEffectId++

/**
 * LaunchedEffect executes side effects in a coroutine scope when it enters composition
 * and cancels the scope when it leaves composition.
 *
 * In the context of Summon, this can be used to:
 * - Fetch data for static site generation
 * - Dynamically compute derived values
 * - Set up event listeners during hydration
 * - Initiate animations on page load
 *
 * @param key A key that determines when the effect should be restarted
 * @param block The suspend function to execute as a side effect
 */
class LaunchedEffect(
    private val key: Any,
    private val block: suspend CoroutineScope.() -> Unit,
    private val executionMode: EffectExecutionMode = EffectExecutionMode.CLIENT_ONLY
) {
    private var latestKey: Any = key
    private var job: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Client-side script ID used for HTML generation
    private val scriptId = "launched-effect-${generateEffectId()}"

    /**
     * Cancels the running coroutine when this component is removed from composition.
     * This should be called by the Summon runtime or lifecycle manager.
     */
    fun dispose() {
        job?.cancel()
        job = null
    }
}

/**
 * Creates a LaunchedEffect with the given key and block.
 *
 * Example usage for data fetching during SSR:
 * ```
 * // Fetch data during server-side rendering
 * launchedEffect("fetchData", EffectExecutionMode.SSR_ONLY) {
 *     val data = fetchFromApi("https://api.example.com/data")
 *     dataState.value = data
 * }
 * ```
 *
 * @param key1 A key that determines when the effect should be restarted
 * @param executionMode Determines if the effect runs during SSR, client-side, or both
 * @param block The suspend function to execute as a side effect
 * @return A LaunchedEffect instance
 */
fun launchedEffect(
    key1: Any,
    executionMode: EffectExecutionMode = EffectExecutionMode.CLIENT_ONLY,
    block: suspend CoroutineScope.() -> Unit
): LaunchedEffect = LaunchedEffect(key1, block, executionMode)

/**
 * Creates a LaunchedEffect with the given keys and block.
 *
 * @param key1 First key that determines when the effect should be restarted
 * @param key2 Second key that determines when the effect should be restarted
 * @param executionMode Determines if the effect runs during SSR, client-side, or both
 * @param block The suspend function to execute as a side effect
 * @return A LaunchedEffect instance
 */
fun launchedEffect(
    key1: Any,
    key2: Any,
    executionMode: EffectExecutionMode = EffectExecutionMode.CLIENT_ONLY,
    block: suspend CoroutineScope.() -> Unit
): LaunchedEffect = LaunchedEffect(listOf(key1, key2), block, executionMode)

/**
 * Creates a LaunchedEffect with the given keys and block.
 *
 * @param vararg keys Keys that determine when the effect should be restarted
 * @param executionMode Determines if the effect runs during SSR, client-side, or both
 * @param block The suspend function to execute as a side effect
 * @return A LaunchedEffect instance
 */
fun launchedEffect(
    vararg keys: Any,
    executionMode: EffectExecutionMode = EffectExecutionMode.CLIENT_ONLY,
    block: suspend CoroutineScope.() -> Unit
): LaunchedEffect = LaunchedEffect(keys.toList(), block, executionMode)

/**
 * DisposableEffect executes side effects when it enters composition
 * and cleanup effects when it leaves composition.
 *
 * In the context of Summon, this can be used to:
 * - Set up and clean up DOM event listeners when hydrating static sites
 * - Register and unregister analytics tracking
 * - Manage third-party libraries that need cleanup
 *
 * @param key A key that determines when the effect should be restarted
 * @param effect A function that returns an onDispose cleanup function
 * @param executionMode Determines if the effect runs during SSR, client-side, or both
 */
class DisposableEffect(
    private val key: Any,
    private val effect: () -> DisposableEffectResult,
    private val executionMode: EffectExecutionMode = EffectExecutionMode.CLIENT_ONLY
) {
    private var latestKey: Any = key
    private var currentEffect: DisposableEffectResult? = null

    // Client-side script ID used for HTML generation
    private val scriptId = "disposable-effect-${generateEffectId()}"

    /**
     * Disposes the effect when this component is removed from composition.
     * This should be called by the Summon runtime or lifecycle manager.
     */
    fun dispose() {
        currentEffect?.dispose()
        currentEffect = null
    }
}

/**
 * Result of a DisposableEffect, containing the cleanup logic.
 */
class DisposableEffectResult(private val onDispose: () -> Unit) {
    fun dispose() {
        onDispose()
    }
}

/**
 * Creates a DisposableEffect with the given key and effect.
 *
 * Example usage for managing DOM events:
 * ```
 * // Set up event listener for client-side interactivity
 * disposableEffect("scrollListener") {
 *    // Set up a scroll event listener
 *    val listener: (Event) -> Unit = { event ->
 *        scrollPosition.value = window.scrollY
 *    }
 *    window.addEventListener("scroll", listener)
 *
 *    // Return cleanup function
 *    onDispose {
 *        window.removeEventListener("scroll", listener)
 *    }
 * }
 * ```
 *
 * @param key A key that determines when the effect should be restarted
 * @param executionMode Determines if the effect runs during SSR, client-side, or both
 * @param effect A function that returns an onDispose cleanup function
 * @return A DisposableEffect instance
 */
fun disposableEffect(
    key: Any,
    executionMode: EffectExecutionMode = EffectExecutionMode.CLIENT_ONLY,
    effect: () -> DisposableEffectResult
): DisposableEffect = DisposableEffect(key, effect, executionMode)

/**
 * Creates a DisposableEffect with the given keys and effect.
 *
 * @param vararg keys Keys that determine when the effect should be restarted
 * @param executionMode Determines if the effect runs during SSR, client-side, or both
 * @param effect A function that returns an onDispose cleanup function
 * @return A DisposableEffect instance
 */
fun disposableEffect(
    vararg keys: Any,
    executionMode: EffectExecutionMode = EffectExecutionMode.CLIENT_ONLY,
    effect: () -> DisposableEffectResult
): DisposableEffect = DisposableEffect(keys.toList(), effect, executionMode)

/**
 * Creates an onDispose cleanup function for use with DisposableEffect.
 *
 * @param onDispose The cleanup function to execute when the effect is disposed
 * @return A DisposableEffectResult containing the cleanup function
 */
fun onDispose(onDispose: () -> Unit): DisposableEffectResult =
    DisposableEffectResult(onDispose)

/**
 * SideEffect executes non-cancellable side effects on every composition.
 * Unlike LaunchedEffect, it runs synchronously during composition.
 *
 * In the context of Summon, this can be used to:
 * - Update metadata or title tags during static site generation
 * - Synchronize external state with the UI
 * - Log or track page views during rendering
 *
 * @param effect The function to execute as a side effect
 * @param executionMode Determines if the effect runs during SSR, client-side, or both
 */
class SideEffect(
    private val effect: () -> Unit,
    private val executionMode: EffectExecutionMode = EffectExecutionMode.HYBRID
) {
    // Client-side script ID used for HTML generation
    private val scriptId = "side-effect-${generateEffectId()}"

    /**
     * Creates a SideEffect that executes the given function on every composition.
     *
     * Example usage for document title:
     * ```
     * // Update document title during both SSR and client-side rendering
     * sideEffect(EffectExecutionMode.HYBRID) {
     *    document.title = "Page: ${pageTitle.value}"
     * }
     * ```
     *
     * @param executionMode Determines if the effect runs during SSR, client-side, or both
     * @param effect The function to execute as a side effect
     * @return A SideEffect instance
     */
    fun sideEffect(
        executionMode: EffectExecutionMode = EffectExecutionMode.HYBRID,
        effect: () -> Unit
    ): SideEffect = SideEffect(effect, executionMode)
}

/**
 * DerivedState represents a state that is derived from one or more other states.
 * The value is recomputed when any of the source states change.
 *
 * In the context of Summon, this can be used to:
 * - Compute derived properties for UI components
 * - Filter or transform data before displaying it
 * - Combine multiple state values into a single value
 *
 * @param calculation A function that computes the derived state value
 * @param sources The source states to observe for changes
 */
class DerivedState<T>(
    private val calculation: () -> T,
    private vararg val sources: State<*>
) : State<T> {
    private var _value: T = calculation()
    private val listeners = mutableListOf<(T) -> Unit>()

    init {
        // Register listeners for all source states
        sources.forEach { source ->
            if (source is MutableStateImpl) {
                source.addListener {
                    updateValue()
                }
            }
        }
    }

    override val value: T
        get() = _value

    private fun updateValue() {
        val newValue = calculation()
        if (_value != newValue) {
            _value = newValue
            notifyListeners()
        }
    }

    /**
     * Adds a listener that will be called whenever the derived state value changes.
     * @param listener A function that receives the new value
     */
    fun addListener(listener: (T) -> Unit) {
        listeners.add(listener)
    }

    /**
     * Removes a previously added listener.
     * @param listener The listener to remove
     */
    fun removeListener(listener: (T) -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it(_value) }
    }
}

/**
 * Creates a derived state that computes its value from the given states.
 *
 * Example usage for content transformation:
 * ```
 * val firstName = mutableStateOf("John")
 * val lastName = mutableStateOf("Doe")
 *
 * // Derive full name from first and last name
 * val fullName = derivedStateOf(firstName, lastName) {
 *     "${firstName.value} ${lastName.value}"
 * }
 *
 * // Use in content
 * Text("Welcome, ${fullName.value}!")
 * ```
 *
 * @param vararg sources The source states to observe for changes
 * @param calculation A function that computes the derived state value
 * @return A State representing the derived value
 */
fun <T> derivedStateOf(
    vararg sources: State<*>,
    calculation: () -> T
): State<T> = DerivedState(calculation, *sources)

/**
 * Determines when the effect should be executed.
 */
enum class EffectExecutionMode {
    /**
     * Execute only during server-side rendering (static site generation)
     */
    SSR_ONLY,

    /**
     * Execute only on the client side after hydration
     */
    CLIENT_ONLY,

    /**
     * Execute during both server-side rendering and client-side hydration
     */
    HYBRID
}

/**
 * Checks if the receiver is an HTML TagConsumer.
 * Platform-specific implementations will override this.
 */
expect fun <T> isHtmlReceiver(receiver: T): Boolean

/**
 * Adds a client-side script to the HTML output.
 * Platform-specific implementations will override this.
 */
expect fun <T> addClientSideScript(
    receiver: T,
    scriptId: String,
    effectType: String,
    withCleanup: Boolean = false
) 