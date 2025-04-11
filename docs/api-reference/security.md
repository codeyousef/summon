# Security API Reference

This document provides a detailed reference for the security APIs in Summon.

## Authentication

### AuthenticationProvider

Interface for authentication providers that handle different types of authentication.

```kotlin
interface AuthenticationProvider {
    suspend fun authenticate(credentials: Credentials): AuthenticationResult
    suspend fun refresh(authentication: Authentication): AuthenticationResult
    suspend fun invalidate(authentication: Authentication)
}
```

### JwtAuthenticationProvider

JWT-based authentication provider for frontend applications. This provider handles JWT tokens received from the backend but does not perform token signing or verification, which should be done on the backend.

```kotlin
class JwtAuthenticationProvider(
    private val apiBaseUrl: String,
    private val tokenExpiration: Long = 3600L // 1 hour in seconds
) : AuthenticationProvider
```

### Credentials

Base interface for all credential types.

```kotlin
interface Credentials
```

### UsernamePasswordCredentials

Username and password credentials.

```kotlin
data class UsernamePasswordCredentials(
    val username: String,
    val password: String
) : Credentials
```

### JwtCredentials

JWT token credentials.

```kotlin
data class JwtCredentials(
    val token: String
) : Credentials
```

### OAuth2Credentials

OAuth2 credentials.

```kotlin
data class OAuth2Credentials(
    val accessToken: String,
    val refreshToken: String? = null,
    val tokenType: String = "Bearer",
    val expiresIn: Long? = null
) : Credentials
```

### AuthenticationResult

Result of an authentication attempt.

```kotlin
sealed class AuthenticationResult {
    data class Success(val authentication: Authentication) : AuthenticationResult()
    data class Failure(val error: Throwable) : AuthenticationResult()
}
```

## Authorization

### Principal

Represents an authenticated user in the system.

```kotlin
interface Principal {
    val id: String
    val roles: Set<Role>
    val permissions: Set<Permission>
    val attributes: Map<String, Any>
}
```

### Role

Represents a role in the system.

```kotlin
@JvmInline
value class Role(val name: String)
```

### Permission

Represents a permission in the system.

```kotlin
@JvmInline
value class Permission(val name: String)
```

### SecurityContext

Manages the current security context, including authentication state.

```kotlin
object SecurityContext {
    fun getAuthentication(): Authentication?
    fun setAuthentication(authentication: Authentication?)
    fun clearAuthentication()
    fun isAuthenticated(): Boolean
    fun getPrincipal(): Principal?
    fun hasRole(role: Role): Boolean
    fun hasPermission(permission: Permission): Boolean
    fun <T> withAuthentication(authentication: Authentication?, block: () -> T): T
}
```

## Security Annotations

### RequiresAuthentication

Annotation to mark a route or component as requiring authentication.

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresAuthentication
```

### RequiresRoles

Annotation to mark a route or component as requiring specific roles.

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRoles(val roles: Array<String>)
```

### RequiresPermissions

Annotation to mark a route or component as requiring specific permissions.

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPermissions(val permissions: Array<String>)
```

### RequiresAccess

Annotation to mark a route or component as requiring specific roles or permissions.

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresAccess(
    val roles: Array<String> = [],
    val permissions: Array<String> = []
)
```

### Public

Annotation to mark a route or component as public (no authentication required).

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Public
```

## Security Components

### SecuredComponent

A component that conditionally renders content based on security requirements.

```kotlin
object SecuredComponent {
    @Composable
    fun authenticated(content: @Composable () -> Unit)
    
    @Composable
    fun unauthenticated(content: @Composable () -> Unit)
    
    @Composable
    fun withRole(role: Role, content: @Composable () -> Unit)
    
    @Composable
    fun withPermission(permission: Permission, content: @Composable () -> Unit)
    
    @Composable
    fun withSecurityRequirements(
        requiresAuthentication: Boolean = false,
        requiredRoles: Set<Role> = emptySet(),
        requiredPermissions: Set<Permission> = emptySet(),
        content: @Composable () -> Unit
    )
    
    @Composable
    fun withSecurityRequirements(
        annotation: RequiresAccess,
        content: @Composable () -> Unit
    )
}
```

### LoginComponent

A component that handles user authentication.

```kotlin
class LoginComponent(
    private val securityService: SecurityService,
    private val router: Router
) {
    suspend fun login(username: String, password: String): LoginResult
    suspend fun logout()
    
    sealed class LoginResult {
        object Success : LoginResult()
        data class Failure(val error: Throwable) : LoginResult()
    }
}
```

## Security Guards

### AuthenticationGuard

A route guard that checks if the user is authenticated.

```kotlin
class AuthenticationGuard : RouteGuard {
    override fun canActivate(): Boolean = SecurityContext.isAuthenticated()
}
```

### RoleGuard

A route guard that checks if the user has the specified role.

```kotlin
class RoleGuard(private val role: Role) : RouteGuard {
    override fun canActivate(): Boolean = SecurityContext.hasRole(role)
}
```

### PermissionGuard

A route guard that checks if the user has the specified permission.

```kotlin
class PermissionGuard(private val permission: Permission) : RouteGuard {
    override fun canActivate(): Boolean = SecurityContext.hasPermission(permission)
}
```

### AnnotationBasedGuard

A route guard that checks if the user meets the security requirements specified by annotations.

```kotlin
class AnnotationBasedGuard(
    private val requiresAuthentication: Boolean = false,
    private val requiredRoles: Set<Role> = emptySet(),
    private val requiredPermissions: Set<Permission> = emptySet()
) : RouteGuard {
    override fun canActivate(): Boolean
}
```

### SecurityGuardFactory

A factory that creates route guards based on annotations.

```kotlin
object SecurityGuardFactory {
    fun createGuard(annotation: RequiresAccess): RouteGuard
    fun createGuard(annotation: RequiresRoles): RouteGuard
    fun createGuard(annotation: RequiresPermissions): RouteGuard
    fun createGuard(annotation: RequiresAuthentication): RouteGuard
}
```

## Security Configuration

### SecurityConfig

Configuration class for security settings.

```kotlin
data class SecurityConfig(
    val authenticationProvider: AuthenticationProvider,
    val loginUrl: String = "/login",
    val defaultSuccessUrl: String = "/",
    val logoutUrl: String = "/login",
    val requireHttps: Boolean = true,
    val sessionTimeout: Long = 3600L,
    val corsConfig: CorsConfig = CorsConfig(),
    val csrfConfig: CsrfConfig = CsrfConfig()
)
```

### CorsConfig

Configuration for CORS settings.

```kotlin
data class CorsConfig(
    val allowedOrigins: Set<String> = setOf("*"),
    val allowedMethods: Set<String> = setOf("GET", "POST", "PUT", "DELETE", "OPTIONS"),
    val allowedHeaders: Set<String> = setOf("*"),
    val allowCredentials: Boolean = true,
    val maxAge: Long = 3600L
)
```

### CsrfConfig

Configuration for CSRF settings.

```kotlin
data class CsrfConfig(
    val enabled: Boolean = true,
    val tokenHeaderName: String = "X-CSRF-TOKEN",
    val tokenCookieName: String = "XSRF-TOKEN"
)
```

### SecurityConfigBuilder

Builder for SecurityConfig.

```kotlin
class SecurityConfigBuilder {
    fun authenticationProvider(provider: AuthenticationProvider): SecurityConfigBuilder
    fun loginUrl(url: String): SecurityConfigBuilder
    fun defaultSuccessUrl(url: String): SecurityConfigBuilder
    fun logoutUrl(url: String): SecurityConfigBuilder
    fun requireHttps(required: Boolean): SecurityConfigBuilder
    fun sessionTimeout(timeout: Long): SecurityConfigBuilder
    fun corsConfig(config: CorsConfig): SecurityConfigBuilder
    fun csrfConfig(config: CsrfConfig): SecurityConfigBuilder
    fun build(): SecurityConfig
}
```

### Extension Functions

```kotlin
fun securityConfig(init: SecurityConfigBuilder.() -> Unit): SecurityConfig
fun createJwtAuthenticationProvider(
    apiBaseUrl: String,
    tokenExpiration: Long = 3600L
): JwtAuthenticationProvider
fun createLoginComponent(
    securityService: SecurityService,
    router: Router
): LoginComponent
``` 