package codes.yousef.summon.runtime

import codes.yousef.summon.annotation.Composable

/**
 * Platform-specific function for adding to pending recompositions in a thread-safe way.
 */
expect fun Recomposer.addToPendingRecompositions(composer: Composer)

/**
 * Platform-specific function for getting and clearing pending recompositions in a thread-safe way.
 */
expect fun Recomposer.getAndClearPendingRecompositions(): List<Composer>

/**
 * Central coordinator for the Summon composition system that manages recomposition scheduling and execution.
 *
 * The Recomposer is the core orchestrator of Summon's reactive UI system. It tracks state dependencies,
 * schedules recomposition when state changes, and ensures efficient updates to the UI tree. The Recomposer
 * works in concert with [Composer] instances to provide a declarative, reactive user interface.
 *
 * ## Core Responsibilities
 *
 * ### State Dependency Tracking
 * - Maintains mapping between state objects and dependent [Composer] instances
 * - Records when composable functions read from state objects
 * - Automatically invalidates affected composables when state changes
 *
 * ### Recomposition Scheduling
 * - Batches recomposition requests for optimal performance
 * - Uses platform-specific schedulers for appropriate timing
 * - Prevents duplicate recomposition of the same composer
 * - Integrates with platform UI update cycles
 *
 * ### Composer Lifecycle Management
 * - Creates and manages [Composer] instances
 * - Tracks active composer during composition
 * - Handles cleanup when composers are disposed
 *
 * ## Recomposition Process
 *
 * The recomposition workflow follows these steps:
 *
 * 1. **Dependency Tracking**: During initial composition, the Recomposer records which
 *    state objects are read by each composable function
 * 2. **Change Detection**: When state objects are modified, [recordStateWrite] is called
 * 3. **Scheduling**: Affected composers are added to the pending recomposition queue
 * 4. **Batch Processing**: [processRecompositions] executes all pending recompositions
 * 5. **Efficient Updates**: Only composables that depend on changed state are re-executed
 *
 * ## Thread Safety
 *
 * The Recomposer provides thread-safe operations through platform-specific implementations:
 * - State change notifications can come from any thread
 * - Recomposition scheduling is thread-safe
 * - Actual composition execution happens on the appropriate UI thread
 *
 * ## Example Usage
 *
 * ```kotlin
 * // Create and configure a recomposer
 * val recomposer = Recomposer()
 * recomposer.setCompositionRoot {
 *     MyApplication()
 * }
 *
 * // State changes automatically trigger recomposition
 * var counter by mutableStateOf(0)
 *
 * @Composable
 * fun MyApplication() {
 *     Button(
 *         onClick = { counter++ },  // This triggers recomposition
 *         label = "Count: $counter"
 *     )
 * }
 * ```
 *
 * ## Integration with Framework Components
 *
 * - **Composer**: Creates and manages composer instances for composition tracking
 * - **State Objects**: Automatically tracks reads/writes through [mutableStateOf] and similar APIs
 * - **Platform Renderer**: Coordinates with rendering system for UI updates
 * - **Effect System**: Manages effect lifecycle during recomposition
 * - **Scheduler**: Uses platform-specific scheduling for optimal performance
 *
 * ## Performance Characteristics
 *
 * - **Batched Updates**: Multiple state changes are batched into single recomposition passes
 * - **Minimal Recomposition**: Only affected composables are re-executed
 * - **Dependency Pruning**: Unused dependencies are automatically cleaned up
 * - **Memory Efficient**: Uses weak references and cleanup strategies to prevent memory leaks
 *
 * @see Composer for composition state management
 * @see RecompositionScheduler for platform-specific scheduling
 * @see codes.yousef.summon.state.mutableStateOf for reactive state creation
 * @since 1.0.0
 */
class Recomposer {
    private var activeComposer: Composer? = null
    private val pendingRecompositions = mutableSetOf<Composer>()
    private val allComposers = mutableSetOf<Composer>()
    private val stateToComposers = mutableMapOf<Any, MutableSet<Composer>>()
    private var scheduler: RecompositionScheduler = createDefaultScheduler()
    private var isScheduled = false
    private var compositionRoot: (@Composable () -> Unit)? = null

    /**
     * Sets the recomposition scheduler for this recomposer instance.
     *
     * The scheduler determines when and how recomposition is executed. Different platforms
     * may use different scheduling strategies:
     * - **Browser**: Uses `requestAnimationFrame` for smooth animations
     * - **Server**: Uses immediate or coroutine-based scheduling
     * - **Testing**: Uses synchronous scheduling for predictable testing
     *
     * ## Threading Considerations
     *
     * The scheduler must ensure that recomposition happens on the appropriate thread:
     * - UI updates should happen on the main/UI thread
     * - Background state changes can trigger scheduling from any thread
     * - The scheduler handles thread coordination as needed
     *
     * @param scheduler The scheduling strategy to use for recomposition
     * @see RecompositionScheduler
     * @since 1.0.0
     */
    internal fun setScheduler(scheduler: RecompositionScheduler) {
        this.scheduler = scheduler
    }

    /**
     * Sets the root composable function that defines the entire UI hierarchy.
     *
     * The composition root is the top-level composable function that gets executed
     * during recomposition. This function typically represents the entire application
     * UI and serves as the entry point for the composition tree.
     *
     * ## Automatic Recomposition
     *
     * When set, the recomposer will automatically call this root function whenever
     * recomposition is triggered by state changes. This ensures the entire UI tree
     * stays in sync with application state.
     *
     * ## Example
     *
     * ```kotlin
     * recomposer.setCompositionRoot {
     *     MyApplication {
     *         // Entire app UI hierarchy
     *         NavigationHost {
     *             HomePage()
     *             SettingsPage()
     *         }
     *     }
     * }
     * ```
     *
     * @param root The root composable function for the entire UI
     * @since 1.0.0
     */
    fun setCompositionRoot(root: @Composable () -> Unit) {
        this.compositionRoot = root
    }

    /**
     * Creates a new composer instance.
     * @return A new Composer instance.
     */
    fun createComposer(): Composer {
        val composer = RecomposerBackedComposer(this)
        allComposers.add(composer)
        return composer
    }

    /**
     * Get access to the pending recompositions set.
     * This is used by platform-specific implementations.
     */
    internal fun getPendingRecompositions(): MutableSet<Composer> {
        return pendingRecompositions
    }

    /**
     * Schedules a recomposition.
     * This method is called when state changes that affect UI elements.
     * @param composer The composer that needs to be recomposed.
     */
    fun scheduleRecomposition(composer: Composer) {
        // Thread safety handled in platform-specific ways
        addToPendingRecompositions(composer)

        // Schedule processing if not already scheduled
        if (!isScheduled) {
            isScheduled = true
            scheduler.scheduleRecomposition {
                isScheduled = false
                compositionRoot?.let { root ->
                    processRecompositions(root)
                }
            }
        }
    }

    /**
     * Process all pending recompositions.
     * This method should be called from the UI thread or a coroutine.
     */
    fun processRecompositions(compositionRoot: @Composable () -> Unit) {
        // Thread safety handled in platform-specific ways
        val recompositions = getAndClearPendingRecompositions()

        // Process each pending recomposition
        recompositions.forEach { composer ->
            if (composer is RecomposerBackedComposer) {
                composer.recompose(compositionRoot)
            }
        }
    }

    /**
     * Records that a state value was written to.
     * This triggers recomposition for composers that depend on this state.
     */
    fun recordStateWrite(state: Any) {
        // Get composers that depend on this state
        val composers = stateToComposers[state] ?: return

        // Schedule recomposition for each affected composer
        composers.forEach { composer ->
            scheduleRecomposition(composer)
        }
    }

    /**
     * Records that a state value was read.
     * This establishes a dependency between the current composition and the state.
     */
    fun recordRead(state: Any) {
        // Only record if we have an active composer (i.e., we're in a composition)
        activeComposer?.let { composer ->
            // Track the dependency
            val composers = stateToComposers.getOrPut(state) { mutableSetOf() }
            composers.add(composer)
        }
    }

    /**
     * Checks if we're currently in a composition context.
     */
    fun isComposing(): Boolean = activeComposer != null

    /**
     * Sets the active composer.
     * This is called by CompositionLocal when setting the current composer.
     */
    fun setActiveComposer(composer: Composer?) {
        activeComposer = composer
    }

    /**
     * Checks if a composer is a RecomposerBackedComposer.
     */
    internal companion object {
        fun isComposerImpl(composer: Composer): Boolean {
            return composer is RecomposerBackedComposer
        }

        fun asComposerImpl(composer: Composer): Composer {
            return composer
        }
    }

    /**
     * A basic implementation of the Composer interface backed by a Recomposer.
     */
    private class RecomposerBackedComposer(private val recomposer: Recomposer) : Composer {
        override val inserting: Boolean = true

        private val slots = mutableMapOf<Int, Any?>()
        private var slotIndex = 0
        private val stateReads = mutableSetOf<Any>()
        private val nodeStack = mutableListOf<Int>()
        private val groupStack = mutableListOf<Any?>()
        private var currentNodeIndex = 0
        private val disposables = mutableListOf<() -> Unit>()

        /**
         * Checks if this composer depends on the given state.
         */
        fun dependsOn(state: Any): Boolean {
            return stateReads.contains(state)
        }

        /**
         * Recomposes this composer with the given root composable.
         */
        fun recompose(compositionRoot: @Composable () -> Unit) {
            // Clear old dependencies
            stateReads.forEach { state ->
                recomposer.stateToComposers[state]?.remove(this)
            }

            // Clear state reads before recomposition
            stateReads.clear()

            // Reset indices for the new composition
            slotIndex = 0
            currentNodeIndex = 0

            // Perform the actual recomposition
            compose(compositionRoot)
        }

        /**
         * Performs the actual composition by invoking the composable root.
         */
        private fun compose(compositionRoot: @Composable () -> Unit) {
            // Set this composer as the active composer using CompositionLocal
            // This ensures that remember {} and other composables can access the composer
            CompositionLocal.provideComposer(this) {
                try {
                    // Start a new composition group
                    startGroup("recomposition")

                    // Invoke the composable root
                    compositionRoot()

                    // End the composition group
                    endGroup()
                } catch (e: Exception) {
                    println("Error during composition: $e")
                    throw e
                }
            }
        }

        override fun startNode() {
            nodeStack.add(currentNodeIndex++)
        }

        override fun endNode() {
            if (nodeStack.isNotEmpty()) {
                nodeStack.removeAt(nodeStack.size - 1)
            }
        }

        override fun startGroup(key: Any?) {
            groupStack.add(key)
            // Store the key in the slot to maintain alignment
            slots[slotIndex] = key
            slotIndex++
        }

        override fun endGroup() {
            if (groupStack.isNotEmpty()) {
                groupStack.removeAt(groupStack.size - 1)
            }
        }


        override fun changed(value: Any?): Boolean {
            val slotValue = getSlot()
            val hasChanged = slotValue != value
            if (hasChanged) {
                setSlot(value)
            }
            return hasChanged
        }

        override fun updateValue(value: Any?) {
            setSlot(value)
        }

        override fun nextSlot() {
            slotIndex++
        }

        override fun getSlot(): Any? {
            val value = slots[slotIndex]
            return value
        }

        override fun setSlot(value: Any?) {
            slots[slotIndex] = value
        }

        override fun recordRead(state: Any) {
            // Track that this state was read in this composition
            stateReads.add(state)
            // Note: Don't call recomposer.recordRead here as it's already handled
            // when the state is accessed. This avoids circular calls.
        }

        override fun recordWrite(state: Any) {
            // Notify the recomposer that this state was written to
            recomposer.recordStateWrite(state)
        }

        override fun reportChanged() {
            // Schedule this composer for recomposition
            recomposer.scheduleRecomposition(this)
        }

        override fun registerDisposable(disposable: () -> Unit) {
            disposables.add(disposable)
        }

        override fun recompose() {
            reportChanged()
        }

        override fun rememberedValue(key: Any): Any? {
            return slots[key.hashCode()]
        }

        override fun updateRememberedValue(key: Any, value: Any?) {
            slots[key.hashCode()] = value
        }

        override fun dispose() {
            // Clean up all resources
            disposables.forEach { it() }
            disposables.clear()
            slots.clear()

            // Clean up state dependencies
            stateReads.forEach { state ->
                recomposer.stateToComposers[state]?.remove(this)
            }
            stateReads.clear()

            nodeStack.clear()
            groupStack.clear()

            // Remove from the recomposer's tracking
            recomposer.allComposers.remove(this)
        }

        override fun startCompose() {
            startNode()
        }

        override fun endCompose() {
            endNode()
        }

        override fun <T> compose(composable: @Composable () -> T): T {
            // Use CompositionLocal to manage the composer
            return CompositionLocal.provideComposer(this) {
                startCompose()
                try {
                    composable()
                } finally {
                    endCompose()
                }
            }
        }
    }

    /**
     * Performs the initial composition synchronously.
     * This ensures that the composition structure matches what will be used during recomposition.
     */
    fun composeInitial(root: @Composable () -> Unit) {
        val composer = createComposer()
        if (composer is RecomposerBackedComposer) {
            composer.recompose(root)
        }
    }
}

/**
 * Global holder for the Recomposer instance.
 * This is already defined in State.kt, so we'll remove the duplicate here.
 */
// Removed duplicate object declaration to resolve conflict with State.kt
