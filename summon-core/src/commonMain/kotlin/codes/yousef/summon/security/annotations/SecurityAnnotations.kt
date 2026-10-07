package codes.yousef.summon.security.annotations

/**
 * Annotation to mark a route or component as requiring authentication
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresAuthentication

/**
 * Annotation to mark a route or component as requiring specific roles.
 *
 * @property roles Roles accepted by the protected declaration.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRoles(vararg val roles: String)

/**
 * Annotation to mark a route or component as requiring specific permissions.
 *
 * @property permissions Permissions required by the protected declaration.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPermissions(vararg val permissions: String)

/**
 * Combines authentication, role, and permission requirements.
 *
 * @property requiresAuthentication Whether an authenticated principal is required.
 * @property roles Roles accepted by the protected declaration.
 * @property permissions Permissions required by the protected declaration.
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
