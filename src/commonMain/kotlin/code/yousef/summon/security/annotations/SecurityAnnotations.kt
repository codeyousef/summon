package code.yousef.summon.security.annotations

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
annotation class RequiresRoles(val roles: Array<String>)

/**
 * Annotation to mark a route or component as requiring specific permissions
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPermissions(val permissions: Array<String>)

/**
 * Annotation to mark a route or component as requiring specific roles or permissions
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresAccess(
    val roles: Array<String> = [],
    val permissions: Array<String> = []
)

/**
 * Annotation to mark a route or component as public (no authentication required)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Public 