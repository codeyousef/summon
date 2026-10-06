package codes.yousef.summon.routing

import codes.yousef.summon.runtime.Composable


/**
 * Defines a route with a pattern and associated component.
 *
 * @param pattern The URL pattern (e.g., "/users/:id" or "/about")
 * @param component The component factory that creates a component for this route
 */
data class Route(
    val pattern: String,
    val component: (RouteParams) -> @Composable () -> Unit
) {
    /**
     * Extracts parameter names from the route pattern.
     * Parameters may use ":paramName" or legacy "{paramName}" syntax.
     *
     * @return List of parameter names in this route
     */
    fun getParameterNames(): List<String> =
        PARAM_REGEX.findAll(pattern)
            .map { match -> match.groupValues.drop(1).first { it.isNotEmpty() } }
            .toList()

    /**
     * Checks if a given path matches this route's pattern.
     *
     * @param path The path to check against this route's pattern
     * @return True if the path matches this route's pattern
     */
    fun matches(path: String): Boolean {
        val internalPath = InternalRoutePath.parse(path) ?: return false
        return matchInternalRoute(pattern, internalPath) != null
    }

    /**
     * Extracts parameters from a path that matches this route's pattern.
     *
     * @param path The path to extract parameters from
     * @return RouteParams containing extracted parameters or null if no match
     */
    fun extractParams(path: String): RouteParams? {
        val internalPath = InternalRoutePath.parse(path) ?: return null
        val params = matchInternalRoute(pattern, internalPath) ?: return null
        return RouteParams(params)
    }

    companion object {
        private val PARAM_REGEX = Regex("(?::([\\w-]+)|\\{([\\w-]+)\\})")
    }
} 
