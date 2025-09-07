package code.yousef.summon.security.components

import code.yousef.summon.security.Permission
import code.yousef.summon.security.Role
import code.yousef.summon.security.SecurityContext
import code.yousef.summon.security.annotations.RequiresAccess

/**
 * A component that conditionally renders content based on security requirements
 */
class SecuredComponent {
    /**
     * Renders content only if the user is authenticated
     */
    fun authenticated(block: () -> Unit) {
        if (SecurityContext.isAuthenticated()) {
            block()
        }
    }

    /**
     * Renders content only if the user is not authenticated
     */
    fun unauthenticated(block: () -> Unit) {
        if (!SecurityContext.isAuthenticated()) {
            block()
        }
    }

    /**
     * Renders content only if the user has the specified role
     */
    fun withRole(role: Role, block: () -> Unit) {
        if (SecurityContext.hasRole(role)) {
            block()
        }
    }

    /**
     * Renders content only if the user has the specified permission
     */
    fun withPermission(permission: Permission, block: () -> Unit) {
        if (SecurityContext.hasPermission(permission)) {
            block()
        }
    }

    /**
     * Renders content based on the security requirements specified by annotations
     */
    fun withSecurityRequirements(
        requiresAuthentication: Boolean = false,
        requiredRoles: Set<Role> = emptySet(),
        requiredPermissions: Set<Permission> = emptySet(),
        block: () -> Unit
    ) {
        val isAuthenticated = SecurityContext.isAuthenticated()
        val hasRequiredRoles = requiredRoles.isEmpty() || requiredRoles.any { SecurityContext.hasRole(it) }
        val hasRequiredPermissions =
            requiredPermissions.isEmpty() || requiredPermissions.any { SecurityContext.hasPermission(it) }

        if ((!requiresAuthentication || isAuthenticated) && hasRequiredRoles && hasRequiredPermissions) {
            block()
        }
    }

    /**
     * Renders content based on the security requirements specified by annotations
     */
    fun withSecurityRequirements(
        annotation: RequiresAccess,
        block: () -> Unit
    ) {
        val requiredRoles = annotation.roles.map { Role(it) }.toSet()
        val requiredPermissions = annotation.permissions.map { Permission(it) }.toSet()

        withSecurityRequirements(
            requiresAuthentication = true,
            requiredRoles = requiredRoles,
            requiredPermissions = requiredPermissions,
            block = block
        )
    }
} 
