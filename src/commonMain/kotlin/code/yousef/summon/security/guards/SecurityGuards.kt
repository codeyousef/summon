package security.guards

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer

import routing.RouteGuard
import security.Permission
import security.Role
import security.SecurityContext
import security.annotations.RequiresAccess
import security.annotations.RequiresAuthentication
import security.annotations.RequiresPermissions
import security.annotations.RequiresRoles

/**
 * A route guard that checks if the user is authenticated
 */
class AuthenticationGuard : RouteGuard {
    override fun canActivate(): Boolean = SecurityContext.isAuthenticated()
}

/**
 * A route guard that checks if the user has the specified role
 */
class RoleGuard(private val role: Role) : RouteGuard {
    override fun canActivate(): Boolean = SecurityContext.hasRole(role)
}

/**
 * A route guard that checks if the user has the specified permission
 */
class PermissionGuard(private val permission: Permission) : RouteGuard {
    override fun canActivate(): Boolean = SecurityContext.hasPermission(permission)
}

/**
 * A route guard that checks if the user meets the security requirements specified by annotations
 */
class AnnotationBasedGuard(
    private val requiresAuthentication: Boolean = false,
    private val requiredRoles: Set<Role> = emptySet(),
    private val requiredPermissions: Set<Permission> = emptySet()
) : RouteGuard {
    override fun canActivate(): Boolean {
        val isAuthenticated = SecurityContext.isAuthenticated()
        val hasRequiredRoles = requiredRoles.isEmpty() || requiredRoles.any { SecurityContext.hasRole(it) }
        val hasRequiredPermissions =
            requiredPermissions.isEmpty() || requiredPermissions.any { SecurityContext.hasPermission(it) }

        return (!requiresAuthentication || isAuthenticated) && hasRequiredRoles && hasRequiredPermissions
    }
}

/**
 * A route guard factory that creates guards based on annotations
 */
object SecurityGuardFactory {
    /**
     * Creates a route guard based on the specified annotation
     */
    fun createGuard(annotation: RequiresAccess): RouteGuard {
        val requiredRoles = annotation.roles.map { Role(it) }.toSet()
        val requiredPermissions = annotation.permissions.map { Permission(it) }.toSet()

        return AnnotationBasedGuard(
            requiresAuthentication = true,
            requiredRoles = requiredRoles,
            requiredPermissions = requiredPermissions
        )
    }

    /**
     * Creates a route guard based on the specified annotation
     */
    fun createGuard(annotation: RequiresRoles): RouteGuard {
        val requiredRoles = annotation.roles.map { Role(it) }.toSet()
        return AnnotationBasedGuard(requiredRoles = requiredRoles)
    }

    /**
     * Creates a route guard based on the specified annotation
     */
    fun createGuard(annotation: RequiresPermissions): RouteGuard {
        val requiredPermissions = annotation.permissions.map { Permission(it) }.toSet()
        return AnnotationBasedGuard(requiredPermissions = requiredPermissions)
    }

    /**
     * Creates a route guard based on the specified annotation
     */
    fun createGuard(annotation: RequiresAuthentication): RouteGuard {
        return AuthenticationGuard()
    }
} 
