package com.summon.routing

/**
 * Container for route parameters parsed from a URL path.
 * 
 * @param params Map of parameter names to their values
 */
class RouteParams(private val params: Map<String, String>) {
    /**
     * Gets a parameter value by name.
     * 
     * @param name The parameter name
     * @return The parameter value or null if not found
     */
    operator fun get(name: String): String? = params[name]
    
    /**
     * Gets a parameter value by name, with a default value if not found.
     * 
     * @param name The parameter name
     * @param defaultValue The default value to return if the parameter is not found
     * @return The parameter value or the default value
     */
    fun getOrDefault(name: String, defaultValue: String): String {
        return params[name] ?: defaultValue
    }
    
    /**
     * Gets all parameter names.
     * 
     * @return Set of all parameter names
     */
    fun getNames(): Set<String> = params.keys
    
    /**
     * Gets all parameters as a map.
     * 
     * @return Map of parameter names to values
     */
    fun asMap(): Map<String, String> = params.toMap()
    
    /**
     * Checks if a parameter exists.
     * 
     * @param name The parameter name
     * @return True if the parameter exists
     */
    fun contains(name: String): Boolean = params.containsKey(name)
} 