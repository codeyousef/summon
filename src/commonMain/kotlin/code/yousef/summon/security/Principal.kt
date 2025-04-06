package code.yousef.summon.security

import kotlin.jvm.JvmInline

/**
 * Represents an authenticated user in the system.
 * This is the core interface for user identity and permissions.
 */
interface Principal {
    /**
     * Unique identifier for the user
     */
    val id: String

    /**
     * Set of roles assigned to the user
     */
    val roles: Set<Role>

    /**
     * Set of permissions granted to the user
     */
    val permissions: Set<Permission>

    /**
     * Additional user attributes
     */
    val attributes: Map<String, Any>
}

/**
 * Represents a role in the system.
 * Roles can be hierarchical and can be assigned permissions.
 */
@JvmInline
value class Role(val name: String)

/**
 * Represents a permission in the system.
 * Permissions are granular access rights that can be assigned to roles.
 */
@JvmInline
value class Permission(val name: String) 