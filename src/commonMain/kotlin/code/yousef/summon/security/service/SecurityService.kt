package security.service

import code.yousef.summon.security.Permission
import code.yousef.summon.security.Principal
import code.yousef.summon.security.Role
import code.yousef.summon.security.SecurityContext
import security.*
import code.yousef.summon.security.annotations.RequiresAccess
import code.yousef.summon.security.annotations.RequiresAuthentication
import code.yousef.summon.security.annotations.RequiresPermissions
import code.yousef.summon.security.annotations.RequiresRoles
import security.config.SecurityConfig

/**
 * Service that provides security-related functionality.
 */
class SecurityService(private val config: SecurityConfig) {
    /**
     * Logs in with the provided credentials.
     * @param credentials The credentials to use for authentication
     * @return AuthenticationResult The result of the authentication attempt
     */
    suspend fun login(credentials: Credentials): AuthenticationResult {
        val result = config.authenticationProvider.authenticate(credentials)
        
        if (result is AuthenticationResult.Success) {
            // Set authentication in the security context
            SecurityContext.withAuthentication(result.authentication) {}
        }
        
        return result
    }
    
    /**
     * Refreshes the current authentication.
     * @return AuthenticationResult The result of the refresh attempt, or null if not authenticated
     */
    suspend fun refreshAuthentication(): AuthenticationResult? {
        val currentAuth = SecurityContext.getAuthentication() ?: return null
        
        val result = config.authenticationProvider.refresh(currentAuth)
        
        if (result is AuthenticationResult.Success) {
            // Update authentication in the security context
            SecurityContext.withAuthentication(result.authentication) {}
        }
        
        return result
    }
    
    /**
     * Logs out the current user.
     */
    suspend fun logout() {
        val currentAuth = SecurityContext.getAuthentication()
        if (currentAuth != null) {
            // Invalidate authentication
            config.authenticationProvider.invalidate(currentAuth)
            
            // Clear security context
            SecurityContext.clearAuthentication()
        }
    }
    
    /**
     * Gets the current authenticated principal.
     * @return Principal? The current principal, or null if not authenticated
     */
    fun getCurrentPrincipal(): Principal? {
        return SecurityContext.getPrincipal()
    }
    
    /**
     * Checks if the current user is authenticated.
     * @return Boolean Whether the current user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return SecurityContext.isAuthenticated()
    }
    
    /**
     * Checks if the current user has the specified role.
     * @param role The role to check
     * @return Boolean Whether the current user has the role
     */
    fun hasRole(role: Role): Boolean {
        return SecurityContext.hasRole(role)
    }
    
    /**
     * Checks if the current user has the specified permission.
     * @param permission The permission to check
     * @return Boolean Whether the current user has the permission
     */
    fun hasPermission(permission: Permission): Boolean {
        return SecurityContext.hasPermission(permission)
    }

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
