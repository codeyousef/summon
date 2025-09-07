package code.yousef.summon.routing

/**
 * Provides a context for accessing the current router instance.
 * This is a simple implementation of the context pattern.
 */
expect object RouterContext {
    /**
     * The current router instance.
     */
    var current: Router?
        internal set

    /**
     * Clears the current router instance.
     */
    fun clear()

    /**
     * Executes a block with the specified router as the current router.
     *
     * @param router The router to use for the block
     * @param block The block to execute
     * @return The result of the block
     */
    fun <T> withRouter(router: Router, block: () -> T): T
}
