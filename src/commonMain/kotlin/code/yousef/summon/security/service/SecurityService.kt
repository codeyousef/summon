package security.service

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import security.*
import security.annotations.RequiresAccess
import security.annotations.RequiresAuthentication
import security.annotations.RequiresPermissions
import security.annotations.RequiresRoles
import security.config.SecurityConfig

/**
 * Service that handles authentication and authorization operations
 */
class SecurityService(private val config: SecurityConfig) {
    /**
     * Authenticates a user with the provided credentials
     */
    suspend fun authenticate(credentials: Credentials): AuthenticationResult {
        return config.authenticationProvider.authenticate(credentials)
    }

    /**
     * Refreshes the current authentication
     */
    suspend fun refreshAuthentication(): AuthenticationResult {
        val currentAuth = SecurityContext.getAuthentication()
        return if (currentAuth != null) {
            config.authenticationProvider.refresh(currentAuth)
        } else {
            AuthenticationResult.Failure(IllegalStateException("No authentication to refresh"))
        }
    }

    /**
     * Logs out the current user
     */
    suspend fun logout() {
        val currentAuth = SecurityContext.getAuthentication()
        if (currentAuth != null) {
            config.authenticationProvider.invalidate(currentAuth)
        }
        SecurityContext.clearAuthentication()
    }

    /**
     * Checks if the current user has the specified role
     */
    fun hasRole(role: Role): Boolean = SecurityContext.hasRole(role)

    /**
     * Checks if the current user has the specified permission
     */
    fun hasPermission(permission: Permission): Boolean = SecurityContext.hasPermission(permission)

    /**
     * Checks if the current user meets the security requirements specified by annotations
     */
    fun checkSecurityRequirements(
        requiresAuthentication: Boolean = false,
        requiredRoles: Set<Role> = emptySet(),
        requiredPermissions: Set<Permission> = emptySet()
    ): Boolean {
        val isAuthenticated = SecurityContext.isAuthenticated()
        val hasRequiredRoles = requiredRoles.isEmpty() || requiredRoles.any { SecurityContext.hasRole(it) }
        val hasRequiredPermissions =
            requiredPermissions.isEmpty() || requiredPermissions.any { SecurityContext.hasPermission(it) }

        return (!requiresAuthentication || isAuthenticated) && hasRequiredRoles && hasRequiredPermissions
    }

    /**
     * Checks if the current user meets the security requirements specified by annotations
     */
    fun checkSecurityRequirements(annotation: RequiresAccess): Boolean {
        val requiredRoles = annotation.roles.map { Role(it) }.toSet()
        val requiredPermissions = annotation.permissions.map { Permission(it) }.toSet()

        return checkSecurityRequirements(
            requiresAuthentication = true,
            requiredRoles = requiredRoles,
            requiredPermissions = requiredPermissions
        )
    }

    /**
     * Checks if the current user meets the security requirements specified by annotations
     */
    fun checkSecurityRequirements(annotation: RequiresRoles): Boolean {
        val requiredRoles = annotation.roles.map { Role(it) }.toSet()
        return checkSecurityRequirements(requiredRoles = requiredRoles)
    }

    /**
     * Checks if the current user meets the security requirements specified by annotations
     */
    fun checkSecurityRequirements(annotation: RequiresPermissions): Boolean {
        val requiredPermissions = annotation.permissions.map { Permission(it) }.toSet()
        return checkSecurityRequirements(requiredPermissions = requiredPermissions)
    }

    /**
     * Checks if the current user meets the security requirements specified by annotations
     */
    fun checkSecurityRequirements(annotation: RequiresAuthentication): Boolean {
        return checkSecurityRequirements(requiresAuthentication = true)
    }

    /**
     * Gets the current principal
     */
    fun getCurrentPrincipal(): Principal? = SecurityContext.getPrincipal()

    /**
     * Gets the current authentication
     */
    fun getCurrentAuthentication(): Authentication? = SecurityContext.getAuthentication()

    /**
     * Executes the given block with the specified authentication
     */
    fun <T> withAuthentication(authentication: Authentication?, block: () -> T): T {
        return SecurityContext.withAuthentication(authentication, block)
    }
} 
