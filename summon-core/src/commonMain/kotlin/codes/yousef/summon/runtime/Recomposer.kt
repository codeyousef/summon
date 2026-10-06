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
    private val stateSubscriptions = mutableMapOf<Any, () -> Unit>()
    private var scheduler: RecompositionScheduler = createDefaultScheduler()
    private var isScheduled = false
    private var compositionRoot: (@Composable () -> Unit)? = null
    private var disposed = false

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
        check(!disposed) { "Cannot set a root on a disposed recomposer" }
        this.compositionRoot = root
    }

    /**
     * Creates a new composer instance.
     * @return A new Composer instance.
     */
    fun createComposer(): Composer {
        check(!disposed) { "Cannot create a composer on a disposed recomposer" }
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
        if (disposed || composer !in allComposers) return
        // Thread safety handled in platform-specific ways
        addToPendingRecompositions(composer)

        // Schedule processing if not already scheduled
        if (!isScheduled) {
            isScheduled = true
            scheduler.scheduleRecomposition work@{
                isScheduled = false
                if (disposed) return@work
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
     * Permanently releases this recomposer's roots, dependencies and queued work.
     * All cleanup runs even if one composer fails; repeated disposal has no effect.
     */
    fun dispose() {
        if (disposed) return
        disposed = true
        activeComposer = null
        compositionRoot = null
        isScheduled = false
        var failure: Throwable? = null
        try { scheduler.cancelPendingRecomposition() } catch (error: Throwable) { failure = error }
        getAndClearPendingRecompositions()
        allComposers.toList().forEach { composer ->
            try { composer.dispose() } catch (error: Throwable) {
                if (failure == null) failure = error else failure.addSuppressed(error)
            }
        }
        failure?.let { throw it }
    }

    /**
     * Records that a state value was written to.
     * This triggers recomposition for composers that depend on this state.
     */
    fun recordStateWrite(state: Any) {
        // Get composers that depend on this state
        val composers = stateToComposers[state] ?: return

        // Schedule recomposition for each affected composer
        composers.toList().forEach { composer ->
            scheduleRecomposition(composer)
        }
    }

    /**
     * Records that a state value was read.
     * This establishes a dependency between the current composition and the state.
     */
    fun recordRead(state: Any) {
        // Only record if we have an active composer (i.e., we're in a composition)
        activeComposer?.recordRead(state)
    }

    @Suppress("UNCHECKED_CAST")
    private fun trackDependency(composer: Composer, state: Any) {
        val dependents = stateToComposers.getOrPut(state) { mutableSetOf() }
        dependents.add(composer)
        // Each root observes its own dependencies instead of broadcasting through
        // whichever global recomposer happened to mount most recently.
        if (state !in stateSubscriptions && state is codes.yousef.summon.state.MutableState<*>) {
            val source = state as codes.yousef.summon.state.MutableState<Any?>
            val listener: (Any?) -> Unit = { recordStateWrite(state) }
            source.addListener(listener)
            stateSubscriptions[state] = { source.removeListener(listener) }
        }
    }

    /**
     * Checks if we're currently in a composition context.
     */
    fun isComposing(): Boolean = activeComposer != null

    internal fun ownsComposer(composer: Composer): Boolean = composer in allComposers

    /**
     * Sets the active composer.
     * This is called by CompositionLocal when setting the current composer.
     */
    fun setActiveComposer(composer: Composer?) {
        activeComposer = if (disposed) null else composer
    }

    /**
     * Checks if a composer is a RecomposerBackedComposer.
     */
    internal companion object {
        fun enqueueSideEffect(composer: Composer, effect: () -> Unit): Boolean {
            if (composer !is RecomposerBackedComposer) return false
            check(!composer.disposed) { "Cannot add an effect to a disposed composition" }
            composer.postCommitEffects.add(effect)
            return true
        }

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

        private val renderer = PlatformRendererStore.get()
        private val slots = CompositionSlots()
        private var compositionDepth = 0
        private val stateReads = mutableSetOf<Any>()
        private val nodeStack = mutableListOf<Int>()
        private var currentNodeIndex = 0
        private val disposables = mutableListOf<() -> Unit>()
        var disposed = false
            private set
        val postCommitEffects = mutableListOf<() -> Unit>()

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
            if (disposed) return
            // Clear old dependencies before collecting this pass.
            clearDependencies()

            // Clear state reads before recomposition
            stateReads.clear()

            // Reset indices for the new composition
            slots.beginPass()
            currentNodeIndex = 0

            // Perform the actual recomposition
            compose(compositionRoot)
        }

        /**
         * Performs the actual composition by invoking the composable root.
         */
        private fun compose(compositionRoot: @Composable () -> Unit) {
            compositionDepth++
            try {
                withPlatformRenderer(renderer) {
                    RecomposerHolder.withRecomposer(recomposer) {
                        CompositionLocal.provideComposer(this) {
                            try {
                                PlatformRendererStore.get()?.startRecomposition()
                                try {
                                    startGroup("recomposition")
                                    try { compositionRoot() } finally { endGroup() }
                                    slots.endPass()
                                } finally {
                                    PlatformRendererStore.get()?.endRecomposition()
                                }
                                runPostCommitEffects()
                            } catch (error: Throwable) {
                                try { dispose() } catch (cleanupError: Throwable) { error.addSuppressed(cleanupError) }
                                println("Summon composition failed")
                                throw error
                            }
                        }
                    }
                }
            } finally { compositionDepth-- }
        }

        private fun runPostCommitEffects() {
            val effects = postCommitEffects.toList()
            postCommitEffects.clear()
            // Effect reads are not new render dependencies, and effects cannot
            // allocate remembered slots after the composition has committed.
            val previous = CompositionLocal.currentComposer
            CompositionLocal.setCurrentComposer(null)
            try { effects.forEach { it() } } finally { CompositionLocal.setCurrentComposer(previous) }
        }

        override fun startNode() {
            nodeStack.add(currentNodeIndex++)
        }

        override fun endNode() {
            if (nodeStack.isNotEmpty()) {
                nodeStack.removeAt(nodeStack.size - 1)
            }
        }

        override fun startGroup(key: Any?) = slots.startGroup(key)
        override fun endGroup() = slots.endGroup()

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

        override fun nextSlot() = slots.nextSlot()
        override fun getSlot(): Any? = slots.getSlot()
        override fun setSlot(value: Any?) = slots.setSlot(value)

        override fun recordRead(state: Any) {
            // Track that this state was read in this composition
            if (disposed) return
            stateReads.add(state)
            recomposer.trackDependency(this, state)
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
            if (disposed) disposable() else disposables.add(disposable)
        }

        override fun recompose() {
            reportChanged()
        }

        override fun rememberedValue(key: Any): Any? = slots.rememberedValue(key)
        override fun updateRememberedValue(key: Any, value: Any?) = slots.updateRememberedValue(key, value)

        private fun clearDependencies() {
            stateReads.forEach { state ->
                recomposer.stateToComposers[state]?.let { dependents ->
                    dependents.remove(this)
                    if (dependents.isEmpty()) {
                        recomposer.stateToComposers.remove(state)
                        recomposer.stateSubscriptions.remove(state)?.invoke()
                    }
                }
            }
            stateReads.clear()
        }

        override fun dispose() {
            if (disposed) return
            disposed = true
            // Detach first: cleanup can write state or call dispose recursively.
            recomposer.allComposers.remove(this)
            recomposer.pendingRecompositions.remove(this)
            clearDependencies()
            postCommitEffects.clear()
            nodeStack.clear()
            val cleanup = disposables.toList()
            disposables.clear()
            var failure: Throwable? = null
            try {
                slots.dispose()
            } catch (error: Throwable) {
                failure = error
            }
            cleanup.forEach { dispose ->
                try {
                    dispose()
                } catch (error: Throwable) {
                    if (failure == null) failure = error else failure.addSuppressed(error)
                }
            }
            failure?.let { throw it }
        }

        override fun startCompose() {
            startNode()
        }

        override fun endCompose() {
            endNode()
        }

        override fun <T> compose(composable: @Composable () -> T): T {
            check(!disposed) { "Cannot compose a disposed composition" }
            val outermost = compositionDepth == 0
            if (outermost) slots.beginPass()
            compositionDepth++
            try {
                return withPlatformRenderer(renderer) {
                    RecomposerHolder.withRecomposer(recomposer) {
                        CompositionLocal.provideComposer(this) {
                            try {
                                startCompose()
                                val result = try { composable() } finally { endCompose() }
                                if (outermost) {
                                    slots.endPass()
                                    runPostCommitEffects()
                                }
                                result
                            } catch (error: Throwable) {
                                try { dispose() } catch (cleanupError: Throwable) { error.addSuppressed(cleanupError) }
                                throw error
                            }
                        }
                    }
                }
            } finally { compositionDepth-- }
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
