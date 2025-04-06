# Security System

The Summon framework includes a comprehensive security system that provides authentication, authorization, and role-based access control (RBAC) capabilities.

## Features

- Authentication with support for multiple credential types (username/password, JWT, OAuth2)
- Role-based access control (RBAC)
- Permission-based authorization
- Security-aware components
- Route guards for protecting routes
- Declarative security with annotations
- Session management
- CORS and CSRF protection

## Getting Started

### Configuration

First, configure the security system:

```kotlin
val securityConfig = securityConfig {
    authenticationProvider = createJwtAuthenticationProvider(
        apiBaseUrl = "https://api.example.com",
        tokenExpiration = 3600L
    )
    loginUrl = "/login"
    defaultSuccessUrl = "/"
    logoutUrl = "/login"
    requireHttps = true
    sessionTimeout = 3600L
}

val securityService = SecurityService(securityConfig)
```

### Authentication

Use the `LoginComponent` to handle user authentication:

```kotlin
val loginComponent = createLoginComponent(securityService, router)

// Login
when (val result = loginComponent.login("username", "password")) {
    is LoginResult.Success -> {
        // Handle successful login
    }
    is LoginResult.Failure -> {
        // Handle login failure
    }
}

// Logout
loginComponent.logout()
```

### Route Protection

Protect routes using annotations:

```kotlin
@RequiresAuthentication
class ProfilePage : Component {
    // ...
}

@RequiresRoles(["admin"])
class AdminPage : Component {
    // ...
}

@RequiresPermissions(["read:users"])
class UserListPage : Component {
    // ...
}
```

Or use route guards directly:

```kotlin
val routes = listOf(
    Route("/profile", ProfilePage::class) {
        guards = listOf(AuthenticationGuard())
    },
    Route("/admin", AdminPage::class) {
        guards = listOf(AuthenticationGuard(), RoleGuard(Role("admin")))
    }
)
```

### Security-Aware Components

Use the `SecuredComponent` to conditionally render content based on security requirements:

```kotlin
val secured = SecuredComponent()

secured.authenticated {
    // Render content for authenticated users
}

secured.withRole(Role("admin")) {
    // Render content for admin users
}

secured.withPermission(Permission("read:users")) {
    // Render content for users with the read:users permission
}
```

## Security Best Practices

1. Always use HTTPS in production
2. Store sensitive data securely
3. Implement proper password hashing
4. Use CSRF protection
5. Set appropriate CORS policies
6. Implement rate limiting
7. Use secure session management
8. Implement proper error handling
9. Log security events
10. Keep dependencies up to date

## Architecture

The security system consists of the following components:

- `SecurityContext`: Manages the current security context
- `AuthenticationProvider`: Handles authentication
- `SecurityService`: Provides high-level security operations
- `SecuredComponent`: Security-aware UI components
- `SecurityGuards`: Route protection
- `SecurityConfig`: Configuration management

## JWT Authentication

The framework provides JWT authentication support for frontend applications. The JWT authentication provider:

1. Makes API calls to the backend for authentication
2. Stores the JWT token received from the backend
3. Includes the token in requests to protected resources
4. Handles token refresh when needed

The JWT token is stored in memory for security, and the refresh token is stored in an HTTP-only cookie.

### JWT Authentication Flow

1. User submits credentials through the login component
2. The JWT authentication provider makes an API call to the backend
3. The backend validates the credentials and returns a JWT token and refresh token
4. The authentication provider stores the tokens and creates an authentication object
5. The security context is updated with the authentication
6. The user is redirected to the protected route or return URL

### Token Refresh

When the JWT token expires, the authentication provider:

1. Uses the refresh token to request a new JWT token from the backend
2. Updates the authentication with the new token
3. Continues with the original request

## Extending the Security System

### Custom Authentication Provider

Implement the `AuthenticationProvider` interface to support custom authentication methods:

```kotlin
class CustomAuthenticationProvider : AuthenticationProvider {
    override suspend fun authenticate(credentials: Credentials): AuthenticationResult {
        // Implement custom authentication logic
    }

    override suspend fun refresh(authentication: Authentication): AuthenticationResult {
        // Implement token refresh logic
    }

    override suspend fun invalidate(authentication: Authentication) {
        // Implement logout logic
    }
}
```

### Custom Route Guards

Create custom route guards by implementing the `RouteGuard` interface:

```kotlin
class CustomGuard : RouteGuard {
    override fun canActivate(): Boolean {
        // Implement custom guard logic
        return true
    }
}
```

## Security Considerations

1. **Token Storage**: JWT tokens are stored in memory for security
2. **Session Management**: Sessions are managed securely with proper timeout handling
3. **Password Security**: Passwords are never stored in plain text
4. **CSRF Protection**: Built-in CSRF protection for forms
5. **CORS**: Configurable CORS policies
6. **Error Handling**: Secure error handling to prevent information leakage
7. **Logging**: Security event logging for auditing

## API Reference

For detailed API documentation, see the [Security API Reference](api-reference/security.md).

## Examples

### Login Page

```kotlin
@Public
class LoginPage : Component {
    private val loginComponent = createLoginComponent(securityService, router)
    private var username = ""
    private var password = ""
    private var error: String? = null

    override fun render() {
        Column {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = "Username"
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                type = TextFieldType.Password
            )
            Button(
                onClick = {
                    launch {
                        when (val result = loginComponent.login(username, password)) {
                            is LoginResult.Success -> {
                                router.navigate("/")
                            }
                            is LoginResult.Failure -> {
                                error = result.error.message
                            }
                        }
                    }
                }
            ) {
                Text("Login")
            }
            error?.let {
                Text(it, color = Color.Red)
            }
        }
    }
}
```

### Protected Route

```kotlin
@RequiresAuthentication
class ProfilePage : Component {
    private val principal = SecurityContext.getPrincipal()

    override fun render() {
        Column {
            Text("Welcome, ${principal?.id}")
            Button(
                onClick = {
                    launch {
                        loginComponent.logout()
                    }
                }
            ) {
                Text("Logout")
            }
        }
    }
}
```

### Role-Based Access

```kotlin
@RequiresRoles(["admin"])
class AdminDashboard : Component {
    private val secured = SecuredComponent()

    override fun render() {
        Column {
            Text("Admin Dashboard")
            secured.withRole(Role("admin")) {
                Button(
                    onClick = { /* ... */ }
                ) {
                    Text("Manage Users")
                }
            }
        }
    }
} 