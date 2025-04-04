package code.yousef.summon.routing

import code.yousef.summon.core.Composable

/**
 * Defines a route with a pattern and associated component.
 * 
 * @param pattern The URL pattern (e.g., "/users/:id" or "/about")
 * @param component The component factory that creates a component for this route
 */
data class Route(
    val pattern: String,
    val component: (RouteParams) -> Composable
) {
    /**
     * Extracts parameter names from the route pattern.
     * Parameters are defined as ":paramName" in the pattern.
     * 
     * @return List of parameter names in this route
     */
    fun getParameterNames(): List<String> {
        return PARAM_REGEX.findAll(pattern)
            .map { it.groupValues[1] }
            .toList()
    }
    
    /**
     * Checks if a given path matches this route's pattern.
     * 
     * @param path The path to check against this route's pattern
     * @return True if the path matches this route's pattern
     */
    fun matches(path: String): Boolean {
        // Convert route pattern to regex
        val regexPattern = pattern
            .replace("/", "\\/")
            .replace(PARAM_REGEX) { "([^\\/]+)" }
        
        val regex = Regex("^$regexPattern$")
        return regex.matches(path)
    }
    
    /**
     * Extracts parameters from a path that matches this route's pattern.
     * 
     * @param path The path to extract parameters from
     * @return RouteParams containing extracted parameters or null if no match
     */
    fun extractParams(path: String): RouteParams? {
        if (!matches(path)) {
            return null
        }
        
        val paramNames = getParameterNames()
        if (paramNames.isEmpty()) {
            return RouteParams(emptyMap())
        }
        
        // Convert route pattern to regex with capturing groups
        val regexPattern = pattern
            .replace("/", "\\/")
            .replace(PARAM_REGEX) { "([^\\/]+)" }
        
        val regex = Regex("^$regexPattern$")
        val matchResult = regex.find(path) ?: return null
        
        // Extract parameter values from match groups
        val paramValues = matchResult.groupValues.drop(1) // First group is the entire match
        
        // Create map of parameter names to values
        val params = paramNames.zip(paramValues).toMap()
        return RouteParams(params)
    }
    
    companion object {
        private val PARAM_REGEX = Regex(":([\\w-]+)")
    }
} 