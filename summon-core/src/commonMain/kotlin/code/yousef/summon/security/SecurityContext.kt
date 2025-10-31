package code.yousef.summon.security

/**
 * Platform-specific holder for the current Authentication.
 */
internal expect object SecurityContextHolder {
    fun get(): Authentication?
    fun set(value: Authentication?)
    fun remove()
}

/**
 * Manages the current security context, including authentication state.
 * This is a singleton that maintains the current authentication state using platform-specific mechanisms.
 */
object SecurityContext {
    /**
     * Gets the current authentication
     */
    fun getAuthentication(): Authentication? = SecurityContextHolder.get()

    /**
     * Sets the current authentication
     */
    private fun setAuthentication(authentication: Authentication?) {
        SecurityContextHolder.set(authentication)
    }

    /**
     * Clears the current authentication
     */
    fun clearAuthentication() {
        SecurityContextHolder.remove()
    }

    /**
     * Checks if the current context is authenticated
     */
    fun isAuthenticated(): Boolean = getAuthentication()?.isAuthenticated == true

    /**
     * Gets the current principal
     */
    fun getPrincipal(): Principal? = getAuthentication()?.principal

    /**
     * Checks if the current principal has the specified role
     */
    fun hasRole(role: Role): Boolean = getPrincipal()?.roles?.contains(role) == true

    /**
     * Checks if the current principal has the specified permission
     */
    fun hasPermission(permission: Permission): Boolean = getPrincipal()?.permissions?.contains(permission) == true

    /**
     * Executes the given block with the specified authentication
     */
    fun <T> withAuthentication(authentication: Authentication?, block: () -> T): T {
        val previous = getAuthentication()
        try {
            setAuthentication(authentication)
            return block()
        } finally {
            setAuthentication(previous)
        }
    }
} 
