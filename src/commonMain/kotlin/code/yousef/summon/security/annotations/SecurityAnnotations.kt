package security.annotations

import code.yousef.summon.runtime.PlatformRendererProvider
import code.yousef.summon.runtime.PlatformRenderer
import security.Permission
import security.Role

/**
 * Annotation to mark a route or component as requiring authentication
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresAuthentication

/**
 * Annotation to mark a route or component as requiring specific roles
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRoles(vararg val roles: String)

/**
 * Annotation to mark a route or component as requiring specific permissions
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPermissions(vararg val permissions: String)

/**
 * Comprehensive annotation that combines authentication, roles, and permissions requirements.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresAccess(
    val requiresAuthentication: Boolean = true,
    val roles: Array<String> = [],
    val permissions: Array<String> = []
)

/**
 * Annotation to mark a route or component as public (no authentication required)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Public 
