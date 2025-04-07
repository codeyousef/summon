package routing

import code.yousef.summon.routing.Router

/**
 * Provides a context for accessing the current router instance.
 * This is a simple implementation of the context pattern.
 */
object RouterContext {
    /**
     * The current router instance.
     */
    var current: Router? = null
        internal set

    /**
     * Sets the current router instance.
     *
     * @param router The router to set as current
     */
    fun setCurrent(router: Router) {
        current = router
    }

    /**
     * Clears the current router instance.
     */
    fun clear() {
        current = null
    }

    /**
     * Executes a block with the specified router as the current router.
     *
     * @param router The router to use for the block
     * @param block The block to execute
     * @return The result of the block
     */
    fun <T> withRouter(router: Router, block: () -> T): T {
        val previous = current
        current = router
        try {
            return block()
        } finally {
            current = previous
        }
    }
} 
