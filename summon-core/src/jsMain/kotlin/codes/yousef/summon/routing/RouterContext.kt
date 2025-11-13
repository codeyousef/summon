package codes.yousef.summon.routing

/**
 * JS implementation of RouterContext
 */
actual object RouterContext {
    /**
     * The current router instance.
     */
    actual var current: Router? = null
        internal set

    /**
     * Clears the current router instance.
     */
    actual fun clear() {
        current = null
    }

    /**
     * Executes a block with the specified router as the current router.
     *
     * @param router The router to use for the block
     * @param block The block to execute
     * @return The result of the block
     */
    actual fun <T> withRouter(router: Router, block: () -> T): T {
        val previous = current
        current = router
        try {
            return block()
        } finally {
            current = previous
        }
    }
}
