package code.yousef.summon.runtime

import code.yousef.summon.annotation.Composable

/**
 * Platform-specific function for adding to pending recompositions in a thread-safe way.
 */
expect fun Recomposer.addToPendingRecompositions(composer: Composer)

/**
 * Platform-specific function for getting and clearing pending recompositions in a thread-safe way.
 */
expect fun Recomposer.getAndClearPendingRecompositions(): List<Composer>

/**
 * Manages the recomposition of UI elements.
 * The Recomposer is responsible for tracking which parts of the UI need to be updated
 * when state changes.
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
     * Sets the scheduler for this recomposer.
     * Mainly used for testing.
     */
    internal fun setScheduler(scheduler: RecompositionScheduler) {
        this.scheduler = scheduler
    }

    /**
     * Sets the composition root for automatic recomposition.
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
    internal fun setActiveComposer(composer: Composer?) {
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
            // Save the current slot index so we can restore it when the group ends
            slots[slotIndex] = slotIndex
            slotIndex++
        }

        override fun endGroup() {
            if (groupStack.isNotEmpty()) {
                groupStack.removeAt(groupStack.size - 1)
                // Restore the slot index from when the group started
                slotIndex = (slots[slotIndex - 1] as? Int) ?: slotIndex
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
            return slots[slotIndex]
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
            // Store the previous active composer
            val previousComposer = recomposer.activeComposer
            // Set this as the active composer
            recomposer.setActiveComposer(this)

            startCompose()
            try {
                return composable()
            } finally {
                endCompose()
                // Restore the previous active composer
                recomposer.setActiveComposer(previousComposer)
            }
        }
    }
}

/**
 * Global holder for the Recomposer instance.
 * This is already defined in State.kt, so we'll remove the duplicate here.
 */
// Removed duplicate object declaration to resolve conflict with State.kt 
