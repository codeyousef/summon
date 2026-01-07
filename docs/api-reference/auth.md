# Authentication API Reference

This document provides detailed information about the authentication and authorization APIs in the Summon library.

## Table of Contents

- [Authentication](#authentication)
  - [Authentication](#authentication-class)
  - [AuthenticationProvider](#authenticationprovider)
  - [JwtAuthenticationProvider](#jwtauthenticationprovider)
  - [SecurityContext](#securitycontext)
- [Authorization](#authorization)
  - [Principal](#principal)
  - [Role and Permission](#role-and-permission)
- [Security Annotations](#security-annotations)
  - [RequiresAuthentication](#requiresauthentication)
  - [RequiresRoles](#requiresroles)
  - [RequiresPermissions](#requirespermissions)
- [Security Components](#security-components)
  - [SecuredComponent](#securedcomponent)
- [Configuration](#configuration)
  - [SecurityConfig](#securityconfig)

---

## Authentication

Authentication in Summon provides a flexible system for handling user identity verification across different platforms.

### Authentication Class

The primary class representing an authenticated session.

```kotlin
package codes.yousef.summon.security

interface Authentication {
    val principal: Principal
    val credentials: Credentials
    val isAuthenticated: Boolean
    val expiresAt: Long?
    
    fun isExpired(): Boolean
}

data class JwtAuthentication(
    override val principal: Principal,
    override val credentials: JwtCredentials,
    override val expiresAt: Long? = null
) : Authentication {
    override val isAuthenticated: Boolean = true
    
    override fun isExpired(): Boolean = expiresAt?.let { System.currentTimeMillis() > it } ?: false
}
```

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

JWT-based authentication provider that works with token-based authentication systems.

```kotlin
class JwtAuthenticationProvider(
    private val apiBaseUrl: String,
    private val tokenExpiration: Long = 3600L // 1 hour in seconds
) : AuthenticationProvider {
    override suspend fun authenticate(credentials: Credentials): AuthenticationResult
    override suspend fun refresh(authentication: Authentication): AuthenticationResult
    override suspend fun invalidate(authentication: Authentication)
}
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

### Role and Permission

Classes that represent user roles and permissions.

```kotlin
@JvmInline
value class Role(val name: String)

@JvmInline
value class Permission(val name: String)
```

## Security Annotations

Summon provides annotations for securing components and routes.

### RequiresAuthentication

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresAuthentication
```

### RequiresRoles

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresRoles(val roles: Array<String>)
```

### RequiresPermissions

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiresPermissions(val permissions: Array<String>)
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
    fun withRole(role: String, content: @Composable () -> Unit)
    
    @Composable
    fun withPermission(permission: String, content: @Composable () -> Unit)
}
```

## Configuration

### SecurityConfig

Configuration for the security system.

```kotlin
data class SecurityConfig(
    val authenticationProvider: AuthenticationProvider,
    val loginUrl: String = "/login",
    val defaultSuccessUrl: String = "/",
    val unauthorizedUrl: String = "/unauthorized",
    val tokenStorage: TokenStorage = LocalStorageTokenStorage(),
    val securityContextHolder: SecurityContextHolder = ThreadLocalSecurityContextHolder()
)

fun securityConfig(init: SecurityConfig.Builder.() -> Unit): SecurityConfig
```

## Example Usage

```kotlin
// Configure security
val securityConfig = securityConfig {
    authenticationProvider = JwtAuthenticationProvider(
        apiBaseUrl = "https://api.example.com"
    )
    loginUrl = "/login"
    defaultSuccessUrl = "/"
}

// Protected component
@RequiresAuthentication
@Composable
fun UserProfile() {
    val user = SecurityContext.getPrincipal()
    Text("Welcome, ${user?.attributes?.get("name") ?: "User"}")
}

// Role-based authorization
@RequiresRoles(["admin"])
@Composable
fun AdminPanel() {
    // Admin-only content
}

// Using SecuredComponent
@Composable
fun AuthAwareComponent() {
    SecuredComponent.authenticated {
        Text("You are logged in!")
    }
    
    SecuredComponent.unauthenticated {
        Text("Please log in to continue")
        Button(text = "Login", onClick = { /* handle login */ })
    }
    
    SecuredComponent.withRole("admin") {
        Text("Admin panel")
    }
}
``` 