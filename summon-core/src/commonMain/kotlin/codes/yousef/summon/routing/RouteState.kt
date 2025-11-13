package codes.yousef.summon.routing

import codes.yousef.summon.runtime.Composable

/**
 * Manages state that is tied to a specific route path.
 * This allows components to maintain state that persists across route navigations
 * but is isolated to a specific route.
 *
 * @param routePath The path of the route this state is associated with
 * @param initialValue The initial value for the state
 */
class RouteState<T>(
    private val routePath: String,
    private val initialValue: T
) {
    // Store states by route path
    private val states = mutableMapOf<String, T>()

    /**
     * Gets the state for the current route instance, or creates new if not exists.
     * @param factory Function to create a new state value if needed
     * @return The current state value
     */
    @Composable
    fun getOrCreate(factory: () -> T): T {
        val router = LocalRouter
        val currentPath = router?.currentPath ?: ""

        // If the current path matches our route path pattern
        return if (matchesRoutePath(currentPath)) {
            // Get or create state for this path
            states.getOrPut(currentPath) { factory() }
        } else {
            // Not on the correct route, return the initial value
            initialValue
        }
    }

    /**
     * Gets the state for the current route instance, or null if not exists.
     * @return The current state value or null
     */
    @Composable
    fun get(): T? {
        val router = LocalRouter
        val currentPath = router?.currentPath ?: ""

        // If the current path matches our route path pattern
        return if (matchesRoutePath(currentPath)) {
            // Get state for this path
            states[currentPath]
        } else {
            // Not on the correct route
            null
        }
    }

    /**
     * Updates the state for the current route instance.
     * @param value The new state value
     */
    @Composable
    fun update(value: T) {
        val router = LocalRouter
        val currentPath = router?.currentPath ?: ""

        // If the current path matches our route path pattern
        if (matchesRoutePath(currentPath)) {
            // Update state for this path
            states[currentPath] = value
        }
    }

    /**
     * Clears the state for all instances of this route.
     */
    fun clear() {
        states.clear()
    }

    /**
     * Checks if the current path matches the route path pattern.
     */
    private fun matchesRoutePath(currentPath: String): Boolean {
        // This is a simple implementation
        // A more advanced implementation would handle path parameters
        return currentPath.startsWith(routePath)
    }
} 