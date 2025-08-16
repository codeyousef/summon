package code.yousef.example.quarkus.ui.pages

import code.yousef.example.quarkus.ui.i18n.AppTranslations
import code.yousef.example.quarkus.ui.state.AppState
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.state.mutableStateOf

/**
 * Login page component
 */
@Composable
fun LoginPage() {
    val username = mutableStateOf("")
    val password = mutableStateOf("")
    val email = mutableStateOf("")
    val isRegistering = mutableStateOf(false)
    val currentLanguage = AppState.currentLanguage.value.code
    val theme = AppState.currentTheme.value
    
    Column(
        modifier = Modifier()
            .style("min-height", "100vh")
            .style("display", "flex")
            .style("align-items", "center")
            .style("justify-content", "center")
            .style("background-color", theme.colors.background)
            .style("padding", theme.spacing.lg)
    ) {
        Card(
            modifier = Modifier()
                .style("width", "100%")
                .style("max-width", "400px")
                .style("background-color", theme.colors.cardBackground)
                .style("border", "1px solid ${theme.colors.border}")
                .style("border-radius", "12px")
                .style("padding", theme.spacing.xl)
                .style("box-shadow", "0 4px 12px rgba(0,0,0,0.1)")
        ) {
            Column(
                modifier = Modifier()
                    .style("gap", theme.spacing.lg)
                    .style("display", "flex")
                    .style("flex-direction", "column")
            ) {
                // Header
                Column(
                    modifier = Modifier()
                        .style("text-align", "center")
                        .style("margin-bottom", theme.spacing.lg)
                ) {
                    Text(
                        text = AppTranslations.getString("welcome.title", currentLanguage),
                        modifier = Modifier()
                            .style("font-size", "28px")
                            .style("font-weight", "bold")
                            .style("color", theme.colors.textPrimary)
                            .style("margin-bottom", theme.spacing.sm)
                    )
                    
                    Text(
                        text = AppTranslations.getString("welcome.subtitle", currentLanguage),
                        modifier = Modifier()
                            .style("font-size", "16px")
                            .style("color", theme.colors.textSecondary)
                    )
                }
                
                // Form fields
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = AppTranslations.getString("auth.username", currentLanguage),
                    modifier = Modifier()
                        .style("width", "100%")
                        .style("margin-bottom", theme.spacing.md)
                )
                
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = AppTranslations.getString("auth.password", currentLanguage),
                    modifier = Modifier()
                        .style("width", "100%")
                        .style("margin-bottom", theme.spacing.md)
                )
                
                if (isRegistering.value) {
                    TextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = AppTranslations.getString("auth.email", currentLanguage),
                        modifier = Modifier()
                            .style("width", "100%")
                            .style("margin-bottom", theme.spacing.md)
                    )
                }
                
                // Action buttons
                Button(
                    onClick = {
                        if (isRegistering.value) {
                            handleRegister(username.value, password.value, email.value)
                        } else {
                            handleLogin(username.value, password.value)
                        }
                    },
                    label = if (isRegistering.value) {
                        AppTranslations.getString("auth.register", currentLanguage)
                    } else {
                        AppTranslations.getString("auth.login", currentLanguage)
                    },
                    modifier = Modifier()
                        .style("width", "100%")
                        .style("background-color", theme.colors.primary)
                        .style("color", theme.colors.onPrimary)
                        .style("padding", "${theme.spacing.md} ${theme.spacing.lg}")
                        .style("border", "none")
                        .style("border-radius", "8px")
                        .style("font-size", "16px")
                        .style("font-weight", "600")
                        .style("cursor", "pointer")
                        .style("margin-bottom", theme.spacing.md)
                )
                
                // Toggle registration mode
                Row(
                    modifier = Modifier()
                        .style("justify-content", "center")
                        .style("align-items", "center")
                        .style("gap", theme.spacing.sm)
                ) {
                    Text(
                        text = if (isRegistering.value) {
                            "Already have an account?"
                        } else {
                            "Don't have an account?"
                        },
                        modifier = Modifier()
                            .style("color", theme.colors.textSecondary)
                            .style("font-size", "14px")
                    )
                    
                    Button(
                        onClick = { isRegistering.value = !isRegistering.value },
                        label = if (isRegistering.value) {
                            AppTranslations.getString("auth.login", currentLanguage)
                        } else {
                            AppTranslations.getString("auth.register", currentLanguage)
                        },
                        modifier = Modifier()
                            .style("background", "transparent")
                            .style("color", theme.colors.primary)
                            .style("border", "none")
                            .style("text-decoration", "underline")
                            .style("cursor", "pointer")
                            .style("font-size", "14px")
                            .style("padding", "0")
                    )
                }
                
                // Demo credentials note
                Card(
                    modifier = Modifier()
                        .style("background-color", theme.colors.surface)
                        .style("border", "1px solid ${theme.colors.border}")
                        .style("border-radius", "8px")
                        .style("padding", theme.spacing.md)
                        .style("margin-top", theme.spacing.lg)
                ) {
                    Column(
                        modifier = Modifier()
                            .style("gap", theme.spacing.xs)
                    ) {
                        Text(
                            text = "Demo Credentials:",
                            modifier = Modifier()
                                .style("font-weight", "600")
                                .style("color", theme.colors.textPrimary)
                                .style("font-size", "14px")
                        )
                        
                        Text(
                            text = "Username: demo",
                            modifier = Modifier()
                                .style("color", theme.colors.textSecondary)
                                .style("font-size", "13px")
                        )
                        
                        Text(
                            text = "Password: demo123",
                            modifier = Modifier()
                                .style("color", theme.colors.textSecondary)
                                .style("font-size", "13px")
                        )
                    }
                }
            }
        }
    }
}

/**
 * Handles user login
 */
private fun handleLogin(username: String, password: String) {
    if (username.isBlank() || password.isBlank()) {
        AppState.showError("Username and password are required")
        return
    }
    
    AppState.setLoading(true)
    
    // Simulate API call - in real app, this would be an HTTP request
    if (username == "demo" && password == "demo123") {
        // Simulate successful login
        val mockUser = code.yousef.example.quarkus.model.PublicUser(
            id = "demo-user-id",
            username = "demo",
            email = "demo@example.com",
            themePreference = code.yousef.example.quarkus.model.ThemePreference.LIGHT,
            languagePreference = "en",
            createdAt = java.time.LocalDateTime.now(),
            lastLoginAt = java.time.LocalDateTime.now()
        )
        
        AppState.setUserSession(mockUser, "demo-session-token")
        AppState.showSuccess("Login successful!")
    } else {
        AppState.showError("Invalid username or password")
    }
    
    AppState.setLoading(false)
}

/**
 * Handles user registration
 */
private fun handleRegister(username: String, password: String, email: String) {
    if (username.isBlank() || password.isBlank()) {
        AppState.showError("Username and password are required")
        return
    }
    
    if (password.length < 6) {
        AppState.showError("Password must be at least 6 characters")
        return
    }
    
    AppState.setLoading(true)
    
    // Simulate API call - in real app, this would be an HTTP request
    AppState.showSuccess("Registration successful! Please login.")
    AppState.setLoading(false)
}