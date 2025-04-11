package security.guards

import security.Permission
import security.Role
import security.SecurityContext
import code.yousef.summon.routing.RouteGuard
import code.yousef.summon.routing.Route
import code.yousef.summon.routing.RouteParams
import code.yousef.summon.routing.GuardResult
import security.annotations.RequiresAccess
import security.annotations.RequiresAuthentication
import security.annotations.RequiresPermissions
import security.annotations.RequiresRoles

/**
 * A route guard that checks if the user is authenticated.
 */
class AuthenticationGuard : RouteGuard {
    override fun canActivate(route: Route, params: RouteParams): GuardResult {
        return if (SecurityContext.isAuthenticated()) {
            GuardResult.Allow
        } else {
            GuardResult.Redirect("/login")
        }
    }
}

/**
 * A route guard that checks if the user has a specific role.
 */
class RoleGuard(private val role: Role) : RouteGuard {
    override fun canActivate(route: Route, params: RouteParams): GuardResult {
        return if (SecurityContext.hasRole(role)) {
            GuardResult.Allow
        } else {
            GuardResult.Deny
        }
    }
}

/**
 * A route guard that checks if the user has a specific set of roles.
 */
class RolesGuard(private val roles: Set<Role>, private val requireAll: Boolean = false) : RouteGuard {
    override fun canActivate(route: Route, params: RouteParams): GuardResult {
        val hasRoles = if (requireAll) {
            roles.all { SecurityContext.hasRole(it) }
        } else {
            roles.any { SecurityContext.hasRole(it) }
        }
        
        return if (hasRoles) {
            GuardResult.Allow
        } else {
            GuardResult.Deny
        }
    }
}

/**
 * A route guard that checks if the user has a specific permission.
 */
class PermissionGuard(private val permission: Permission) : RouteGuard {
    override fun canActivate(route: Route, params: RouteParams): GuardResult {
        return if (SecurityContext.hasPermission(permission)) {
            GuardResult.Allow
        } else {
            GuardResult.Deny
        }
    }
}

/**
 * A route guard that checks if the user has a specific set of permissions.
 */
class PermissionsGuard(private val permissions: Set<Permission>, private val requireAll: Boolean = false) : RouteGuard {
    override fun canActivate(route: Route, params: RouteParams): GuardResult {
        val hasPermissions = if (requireAll) {
            permissions.all { SecurityContext.hasPermission(it) }
        } else {
            permissions.any { SecurityContext.hasPermission(it) }
        }
        
        return if (hasPermissions) {
            GuardResult.Allow
        } else {
            GuardResult.Deny
        }
    }
}

/**
 * A route guard that checks if the user meets the security requirements specified by annotations.
 */
class AnnotationBasedGuard(
    private val requiresAuthentication: Boolean = false,
    private val requiredRoles: Set<Role> = emptySet(),
    private val requiredPermissions: Set<Permission> = emptySet()
) : RouteGuard {
    override fun canActivate(route: Route, params: RouteParams): GuardResult {
        // First check authentication if required
        if (requiresAuthentication && !SecurityContext.isAuthenticated()) {
            return GuardResult.Redirect("/login")
        }

        // Check roles if specified
        if (requiredRoles.isNotEmpty()) {
            val hasAllRoles = requiredRoles.all { SecurityContext.hasRole(it) }
            if (!hasAllRoles) {
                return GuardResult.Deny
            }
        }

        // Check permissions if specified
        if (requiredPermissions.isNotEmpty()) {
            val hasAllPermissions = requiredPermissions.all { SecurityContext.hasPermission(it) }
            if (!hasAllPermissions) {
                return GuardResult.Deny
            }
        }

        return GuardResult.Allow
    }
}

/**
 * A route guard that combines multiple guards.
 */
class CompositeGuard(private val guards: List<RouteGuard>) : RouteGuard {
    override fun canActivate(route: Route, params: RouteParams): GuardResult {
        for (guard in guards) {
            val result = guard.canActivate(route, params)
            if (result !is GuardResult.Allow) {
                return result
            }
        }
        return GuardResult.Allow
    }
    
    companion object {
        /**
         * Create a guard that requires all child guards to pass.
         */
        fun all(vararg guards: RouteGuard): CompositeGuard = CompositeGuard(guards.toList())
        
        /**
         * Create a guard that requires any child guard to pass.
         */
        fun any(vararg guards: RouteGuard): RouteGuard = object : RouteGuard {
            override fun canActivate(route: Route, params: RouteParams): GuardResult {
                for (guard in guards) {
                    val result = guard.canActivate(route, params)
                    if (result is GuardResult.Allow) {
                        return GuardResult.Allow
                    }
                }
                return GuardResult.Deny
            }
        }
    }
}

/**
 * A factory that creates route guards based on annotations.
 */
object SecurityGuardFactory {
    /**
     * Creates a guard from a RequiresAccess annotation.
     * This is a comprehensive annotation that can specify authentication, roles, and permissions.
     */
    fun createGuard(annotation: RequiresAccess): RouteGuard {
        // Convert string role names to Role objects
        val roles = annotation.roles.map { roleName -> 
            // This is a simplified implementation that assumes a Role constructor with String param
            // In a real implementation, you might have an enum or repository lookup
            Role(roleName) 
        }.toSet()
        
        // Convert string permission names to Permission objects
        val permissions = annotation.permissions.map { permName -> 
            // This is a simplified implementation that assumes a Permission constructor with String param
            // In a real implementation, you might have an enum or repository lookup
            Permission(permName) 
        }.toSet()
        
        return AnnotationBasedGuard(
            requiresAuthentication = annotation.requiresAuthentication,
            requiredRoles = roles,
            requiredPermissions = permissions
        )
    }

    /**
     * Creates a guard from a RequiresRoles annotation.
     */
    fun createGuard(annotation: RequiresRoles): RouteGuard {
        val roles = annotation.roles.map { Role(it) }.toSet()
        return RolesGuard(roles)
    }

    /**
     * Creates a guard from a RequiresPermissions annotation.
     */
    fun createGuard(annotation: RequiresPermissions): RouteGuard {
        val permissions = annotation.permissions.map { Permission(it) }.toSet()
        return PermissionsGuard(permissions)
    }

    /**
     * Creates a guard from a RequiresAuthentication annotation.
     */
    fun createGuard(annotation: RequiresAuthentication): RouteGuard {
        return AuthenticationGuard()
    }
} 
