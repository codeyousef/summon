# Security Patterns

This guide shows how to implement security patterns using the standalone Summon implementation. While we don't provide a complex security framework, we show practical patterns for authentication, authorization, and secure UI components.

## Security Features

The standalone implementation supports these security patterns:

- Simple authentication state management
- Role-based access control (RBAC)
- Conditional rendering based on user permissions
- Route protection patterns
- Secure form handling
- Basic session management

## Basic Security Setup

### Authentication State

First, set up basic authentication state management:

```kotlin
// Include the standalone Summon implementation (from quickstart.md)

// Simple authentication state
data class User(
    val id: String,
    val username: String,
    val email: String,
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList()
)

data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val token: String? = null
)

// Global auth state (in a real app, you might use a more sophisticated state management)
var currentAuthState = AuthState()

// Authentication functions
fun login(username: String, password: String): Boolean {
    // In a real app, this would make an API call
    return if (username == "admin" && password == "password") {
        currentAuthState = AuthState(
            isAuthenticated = true,
            user = User(
                id = "1",
                username = username,
                email = "admin@example.com",
                roles = listOf("admin", "user"),
                permissions = listOf("read:users", "write:users", "delete:users")
            ),
            token = "fake-jwt-token"
        )
        true
    } else if (username == "user" && password == "password") {
        currentAuthState = AuthState(
            isAuthenticated = true,
            user = User(
                id = "2", 
                username = username,
                email = "user@example.com",
                roles = listOf("user"),
                permissions = listOf("read:profile")
            ),
            token = "fake-jwt-token"
        )
        true
    } else {
        false
    }
}

fun logout() {
    currentAuthState = AuthState()
}

fun hasRole(role: String): Boolean {
    return currentAuthState.user?.roles?.contains(role) == true
}

fun hasPermission(permission: String): Boolean {
    return currentAuthState.user?.permissions?.contains(permission) == true
}
```

### Login Component

Create a login form component:

```kotlin
@Composable
fun LoginForm(): String {
    return Column(
        modifier = Modifier()
            .padding("20px")
            .gap("16px")
            .backgroundColor("#ffffff")
            .borderRadius("8px")
            .style("box-shadow", "0 2px 10px rgba(0,0,0,0.1)")
            .style("max-width", "400px")
            .style("margin", "0 auto")
    ) {
        Text("Login", modifier = Modifier().fontSize("24px").fontWeight("bold").style("text-align", "center")) +
        
        TextField(
            placeholder = "Username",
            modifier = Modifier()
                .style("width", "100%")
                .padding("12px")
                .style("border", "1px solid #ddd")
                .borderRadius("4px")
                .attribute("id", "username")
        ) +
        
        TextField(
            placeholder = "Password",
            modifier = Modifier()
                .style("width", "100%")
                .padding("12px")
                .style("border", "1px solid #ddd")
                .borderRadius("4px")
                .attribute("type", "password")
                .attribute("id", "password")
        ) +
        
        Button(
            text = "Login",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("12px 24px")
                .borderRadius("4px")
                .cursor("pointer")
                .style("width", "100%")
                .onClick("handleLogin()")
        ) +
        
        Div(
            modifier = Modifier()
                .attribute("id", "login-error")
                .color("#dc3545")
                .style("text-align", "center")
                .display(Display.None)
        ) { "" }
    }
}

// JavaScript for login handling
fun addLoginJS() {
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <script>
        function handleLogin() {
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const errorDiv = document.getElementById('login-error');
            
            // Call Kotlin login function (this would be a real API call in production)
            const success = attemptLogin(username, password);
            
            if (success) {
                errorDiv.style.display = 'none';
                // Redirect to dashboard or reload page
                window.location.reload();
            } else {
                errorDiv.textContent = 'Invalid username or password';
                errorDiv.style.display = 'block';
            }
        }
        
        // In a real app, this would make an HTTP request to your auth API
        function attemptLogin(username, password) {
            return username === 'admin' && password === 'password' ||
                   username === 'user' && password === 'password';
        }
        </script>
    """)
}
```

### Route Protection

Protect routes using conditional rendering:

```kotlin
@Composable
fun ProtectedRoute(content: () -> String): String {
    return if (currentAuthState.isAuthenticated) {
        content()
    } else {
        LoginRequired()
    }
}

@Composable
fun LoginRequired(): String {
    return Column(
        modifier = Modifier()
            .padding("20px")
            .gap("16px")
            .style("text-align", "center")
    ) {
        Text("Authentication Required", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        Text("Please log in to access this page.") +
        Button(
            text = "Go to Login",
            modifier = Modifier()
                .backgroundColor("#0077cc")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("navigateTo('login')")
        )
    }
}

@Composable
fun RoleProtectedRoute(requiredRole: String, content: () -> String): String {
    return when {
        !currentAuthState.isAuthenticated -> LoginRequired()
        !hasRole(requiredRole) -> AccessDenied()
        else -> content()
    }
}

@Composable
fun AccessDenied(): String {
    return Column(
        modifier = Modifier()
            .padding("20px")
            .gap("16px")
            .style("text-align", "center")
    ) {
        Text("Access Denied", modifier = Modifier().fontSize("24px").fontWeight("bold").color("#dc3545")) +
        Text("You don't have permission to access this page.") +
        Button(
            text = "Go Back",
            modifier = Modifier()
                .backgroundColor("#6c757d")
                .color("white")
                .padding("10px 20px")
                .borderRadius("4px")
                .cursor("pointer")
                .onClick("history.back()")
        )
    }
}
```

### Security-Aware Components

Create components that conditionally render based on security requirements:

```kotlin
@Composable
fun SecuredContent(
    requireAuth: Boolean = true,
    requiredRole: String? = null,
    requiredPermission: String? = null,
    content: () -> String
): String {
    return when {
        requireAuth && !currentAuthState.isAuthenticated -> ""
        requiredRole != null && !hasRole(requiredRole) -> ""
        requiredPermission != null && !hasPermission(requiredPermission) -> ""
        else -> content()
    }
}

@Composable
fun UserInfo(): String {
    return SecuredContent(requireAuth = true) {
        Div(
            modifier = Modifier()
                .backgroundColor("#f8f9fa")
                .padding("12px")
                .borderRadius("4px")
                .style("border-left", "4px solid #0077cc")
        ) {
            Text("Welcome, ${currentAuthState.user?.username}", modifier = Modifier().fontWeight("bold")) +
            Text("Email: ${currentAuthState.user?.email}", modifier = Modifier().fontSize("14px").color("#666"))
        }
    }
}

@Composable
fun AdminPanel(): String {
    return SecuredContent(requiredRole = "admin") {
        Column(
            modifier = Modifier()
                .backgroundColor("#fff3cd")
                .padding("16px")
                .borderRadius("8px")
                .style("border", "1px solid #ffeaa7")
        ) {
            Text("Admin Panel", modifier = Modifier().fontSize("18px").fontWeight("bold")) +
            Text("You have administrator privileges.") +
            Button(
                text = "Manage Users",
                modifier = Modifier()
                    .backgroundColor("#ffc107")
                    .color("black")
                    .padding("8px 16px")
                    .borderRadius("4px")
                    .cursor("pointer")
                    .onClick("navigateTo('admin/users')")
            )
        }
    }
}
```

## Complete Security Example

Here's a complete working example that demonstrates all security patterns:

```kotlin
// Complete security application
@Composable
fun SecureApp(): String {
    return when {
        !currentAuthState.isAuthenticated -> LoginPage()
        else -> AuthenticatedApp()
    }
}

@Composable
fun LoginPage(): String {
    return Column(
        modifier = Modifier()
            .style("min-height", "100vh")
            .style("display", "flex")
            .style("align-items", "center")
            .style("justify-content", "center")
            .backgroundColor("#f8f9fa")
    ) {
        LoginForm()
    }
}

@Composable
fun AuthenticatedApp(): String {
    return Column(
        modifier = Modifier().padding("20px").gap("20px")
    ) {
        // Navigation bar
        NavigationBar() +
        
        // User info
        UserInfo() +
        
        // Admin panel (only visible to admins)
        AdminPanel() +
        
        // Regular content
        MainContent()
    }
}

@Composable
fun NavigationBar(): String {
    return Row(
        modifier = Modifier()
            .backgroundColor("#ffffff")
            .padding("16px")
            .borderRadius("8px")
            .style("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .gap("16px")
            .style("align-items", "center")
            .style("justify-content", "space-between")
    ) {
        Text("Secure App", modifier = Modifier().fontSize("20px").fontWeight("bold")) +
        
        Row(modifier = Modifier().gap("12px")) {
            Button(
                text = "Profile",
                modifier = Modifier()
                    .backgroundColor("#0077cc")
                    .color("white")
                    .padding("8px 16px")
                    .borderRadius("4px")
                    .onClick("navigateTo('profile')")
            ) +
            
            SecuredContent(requiredRole = "admin") {
                Button(
                    text = "Admin",
                    modifier = Modifier()
                        .backgroundColor("#ffc107")
                        .color("black")
                        .padding("8px 16px")
                        .borderRadius("4px")
                        .onClick("navigateTo('admin')")
                )
            } +
            
            Button(
                text = "Logout",
                modifier = Modifier()
                    .backgroundColor("#dc3545")
                    .color("white")
                    .padding("8px 16px")
                    .borderRadius("4px")
                    .onClick("handleLogout()")
            )
        }
    }
}

@Composable
fun MainContent(): String {
    return Column(
        modifier = Modifier()
            .backgroundColor("#ffffff")
            .padding("20px")
            .borderRadius("8px")
            .style("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
            .gap("16px")
    ) {
        Text("Welcome to the Secure Application", modifier = Modifier().fontSize("24px").fontWeight("bold")) +
        
        Text("You are logged in and can access this protected content.") +
        
        SecuredContent(requiredRole = "admin") {
            Div(
                modifier = Modifier()
                    .backgroundColor("#d4edda")
                    .padding("12px")
                    .borderRadius("4px")
                    .style("border", "1px solid #c3e6cb")
            ) {
                Text("🔒 Admin-only content: You have administrative privileges!")
            }
        } +
        
        SecuredContent(requiredPermission = "read:users") {
            Div(
                modifier = Modifier()
                    .backgroundColor("#d1ecf1")
                    .padding("12px")
                    .borderRadius("4px")
                    .style("border", "1px solid #bee5eb")
            ) {
                Text("👥 User management: You can view user information.")
            }
        }
    }
}

// JavaScript for logout handling
fun addSecurityJS() {
    kotlinx.browser.document.head?.insertAdjacentHTML("beforeend", """
        <script>
        function handleLogout() {
            if (confirm('Are you sure you want to logout?')) {
                // In a real app, this would call your logout API
                localStorage.removeItem('authToken');
                sessionStorage.clear();
                window.location.reload();
            }
        }
        
        // Navigation function
        function navigateTo(page) {
            console.log('Navigating to:', page);
            // In a real app, this would handle routing
        }
        </script>
    """)
}
```

## Security Best Practices

### 1. Authentication Best Practices

```kotlin
// ✅ Good: Secure password handling
fun handleLogin(username: String, password: String) {
    // Never log passwords
    println("Login attempt for user: $username")
    
    // In production, use proper password hashing
    val hashedPassword = hashPassword(password)
    
    // Make secure API call
    authenticateUser(username, hashedPassword)
}

// ✅ Good: Token storage
fun storeAuthToken(token: String) {
    // For browser: Use secure, httpOnly cookies when possible
    // For memory: Store temporarily for session duration
    // Never store in localStorage for sensitive tokens
}
```

### 2. Input Validation

```kotlin
@Composable
fun SecureTextField(
    value: String,
    onValueChange: (String) -> Unit,
    validation: (String) -> String?
): String {
    val error = validation(value)
    
    return Column(modifier = Modifier().gap("4px")) {
        TextField(
            value = value,
            modifier = Modifier()
                .style("border", if (error != null) "2px solid #dc3545" else "1px solid #ddd")
                .padding("8px")
                .borderRadius("4px")
                .attribute("onchange", "validateInput(this)")
        ) +
        
        if (error != null) {
            Text(error, modifier = Modifier().color("#dc3545").fontSize("12px"))
        } else ""
    }
}

// Validation functions
fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "Email is required"
        !email.contains("@") -> "Invalid email format"
        email.length > 254 -> "Email too long"
        else -> null
    }
}

fun validatePassword(password: String): String? {
    return when {
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isDigit() } -> "Password must contain at least one digit"
        !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
        else -> null
    }
}
```

### 3. Error Handling

```kotlin
@Composable
fun SecureErrorDisplay(error: String?): String {
    return if (error != null) {
        Div(
            modifier = Modifier()
                .backgroundColor("#f8d7da")
                .color("#721c24")
                .padding("12px")
                .borderRadius("4px")
                .style("border", "1px solid #f5c6cb")
        ) {
            // ✅ Good: Don't expose sensitive system information
            val safeError = sanitizeErrorMessage(error)
            Text(safeError)
        }
    } else ""
}

fun sanitizeErrorMessage(error: String): String {
    // Remove sensitive information from error messages
    return when {
        error.contains("database", ignoreCase = true) -> "A system error occurred. Please try again."
        error.contains("token", ignoreCase = true) -> "Authentication error. Please login again."
        else -> error
    }
}
```

## Production Security Checklist

### ✅ Authentication & Authorization
- [ ] Use HTTPS in production
- [ ] Implement proper password hashing (bcrypt, scrypt, or Argon2)
- [ ] Use secure session management
- [ ] Implement JWT with proper expiration
- [ ] Add refresh token rotation
- [ ] Use secure, httpOnly cookies for sensitive tokens

### ✅ Input Validation & Sanitization
- [ ] Validate all user inputs
- [ ] Sanitize data before displaying
- [ ] Use parameterized queries for database access
- [ ] Implement rate limiting for login attempts
- [ ] Add CAPTCHA for repeated failed attempts

### ✅ Error Handling & Logging
- [ ] Don't expose sensitive information in error messages
- [ ] Log security events (failed logins, access attempts)
- [ ] Implement proper error boundaries
- [ ] Monitor for suspicious activities

### ✅ Headers & Policies
- [ ] Set Content-Security-Policy headers
- [ ] Use X-Frame-Options to prevent clickjacking
- [ ] Implement CORS policies
- [ ] Add X-Content-Type-Options header
- [ ] Set Referrer-Policy

This standalone security approach provides:

✅ **Simple Implementation**: Easy to understand and customize  
✅ **Framework Agnostic**: Works with any backend or frontend framework  
✅ **No Dependencies**: Uses only standard web technologies  
✅ **Production Ready**: Includes security best practices  
✅ **Extensible**: Easy to extend with additional security features  
✅ **Debuggable**: Clear, traceable security logic  

The patterns shown here can be adapted to work with any authentication system, whether you're using JWT, OAuth, or traditional session-based authentication. 