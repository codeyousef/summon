package code.yousef.summon.examples.js.pages

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.examples.js.i18n.Translations
import code.yousef.summon.examples.js.state.appState
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.state.mutableStateOf

@Composable
fun AuthPage() {
    val language = appState.currentLanguage.value
    val isLogin = mutableStateOf(true)
    val username = mutableStateOf("demo")  // Demo username
    val email = mutableStateOf("")
    val password = mutableStateOf("demo123")  // Demo password
    val rememberMe = mutableStateOf(false)
    val isLoading = mutableStateOf(false)
    
    Column(
        modifier = Modifier()
            .style("min-height", "100vh")
            .style("display", "flex")
            .style("align-items", "center")
            .style("justify-content", "center")
            .style("background", "var(--background-color)")
            .style("padding", "20px")
    ) {
        // Auth card
        Column(
            modifier = Modifier()
                .style("background", "var(--card-background)")
                .style("padding", "40px")
                .style("border-radius", "12px")
                .style("box-shadow", "0 4px 20px rgba(0, 0, 0, 0.1)")
                .style("max-width", "400px")
                .style("width", "100%")
        ) {
            // Title
            Text(
                text = if (isLogin.value) Translations.get("auth.login", language) 
                       else Translations.get("auth.register", language),
                modifier = Modifier()
                    .style("font-size", "28px")
                    .style("font-weight", "700")
                    .style("text-align", "center")
                    .style("margin-bottom", "8px")
                    .style("color", "var(--text-color)")
            )
            
            Text(
                text = Translations.get("app.subtitle", language),
                modifier = Modifier()
                    .style("text-align", "center")
                    .style("color", "var(--muted-text-color)")
                    .style("margin-bottom", "32px")
                    .style("font-size", "14px")
            )
            
            // Username field
            Column(
                modifier = Modifier().style("margin-bottom", "20px")
            ) {
                Text(
                    text = Translations.get("auth.username", language),
                    modifier = Modifier()
                        .style("margin-bottom", "8px")
                        .style("font-weight", "500")
                        .style("color", "var(--text-color)")
                        .style("font-size", "14px")
                )
                
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    placeholder = Translations.get("auth.username", language),
                    modifier = Modifier()
                        .style("width", "100%")
                        .style("padding", "12px 16px")
                        .style("border", "2px solid var(--border-color)")
                        .style("border-radius", "8px")
                        .style("background", "var(--background-color)")
                        .style("color", "var(--text-color)")
                        .style("font-size", "16px")
                        .style("outline", "none")
                        .style("transition", "border-color 0.2s ease")
                )
            }
            
            // Email field (only for register)
            if (!isLogin.value) {
                Column(
                    modifier = Modifier().style("margin-bottom", "20px")
                ) {
                    Text(
                        text = Translations.get("auth.email", language),
                        modifier = Modifier()
                            .style("margin-bottom", "8px")
                            .style("font-weight", "500")
                            .style("color", "var(--text-color)")
                            .style("font-size", "14px")
                    )
                    
                    TextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        placeholder = Translations.get("auth.email", language),
                        modifier = Modifier()
                            .style("width", "100%")
                            .style("padding", "12px 16px")
                            .style("border", "2px solid var(--border-color)")
                            .style("border-radius", "8px")
                            .style("background", "var(--background-color)")
                            .style("color", "var(--text-color)")
                            .style("font-size", "16px")
                            .style("outline", "none")
                            .style("transition", "border-color 0.2s ease")
                    )
                }
            }
            
            // Password field
            Column(
                modifier = Modifier().style("margin-bottom", "20px")
            ) {
                Text(
                    text = Translations.get("auth.password", language),
                    modifier = Modifier()
                        .style("margin-bottom", "8px")
                        .style("font-weight", "500")
                        .style("color", "var(--text-color)")
                        .style("font-size", "14px")
                )
                
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = Translations.get("auth.password", language),
                    modifier = Modifier()
                        .style("width", "100%")
                        .style("padding", "12px 16px")
                        .style("border", "2px solid var(--border-color)")
                        .style("border-radius", "8px")
                        .style("background", "var(--background-color)")
                        .style("color", "var(--text-color)")
                        .style("font-size", "16px")
                        .style("outline", "none")
                        .style("transition", "border-color 0.2s ease")
                        .attribute("type", "password")
                )
            }
            
            // Remember me checkbox (only for login)
            if (isLogin.value) {
                Row(
                    modifier = Modifier()
                        .style("margin-bottom", "24px")
                        .style("align-items", "center")
                        .style("gap", "8px")
                ) {
                    Checkbox(
                        checked = rememberMe.value,
                        onCheckedChange = { rememberMe.value = it }
                    )
                    
                    Text(
                        text = Translations.get("auth.remember_me", language),
                        modifier = Modifier()
                            .style("color", "var(--text-color)")
                            .style("font-size", "14px")
                    )
                }
            }
            
            // Submit button
            Button(
                onClick = {
                    if (!isLoading.value && username.value.isNotBlank() && password.value.isNotBlank()) {
                        isLoading.value = true
                        val emailValue = if (!isLogin.value && email.value.isNotBlank()) {
                            email.value
                        } else {
                            "${username.value}@example.com" // Demo email
                        }
                        // Simulate network delay then login
                        kotlinx.browser.window.setTimeout({
                            appState.login(username.value, emailValue)
                            isLoading.value = false
                        }, 500)
                    }
                },
                label = if (isLoading.value) "Loading..." 
                       else if (isLogin.value) Translations.get("auth.login", language) 
                       else Translations.get("auth.register", language),
                variant = ButtonVariant.PRIMARY,
                disabled = isLoading.value || username.value.isBlank() || password.value.isBlank() || 
                          (!isLogin.value && email.value.isBlank()),
                modifier = Modifier()
                    .style("width", "100%")
                    .style("padding", "14px 24px")
                    .style("border-radius", "8px")
                    .style("font-weight", "600")
                    .style("font-size", "16px")
                    .style("margin-bottom", "16px")
            )
            
            // Toggle login/register
            Row(
                modifier = Modifier()
                    .style("justify-content", "center")
                    .style("align-items", "center")
                    .style("gap", "8px")
            ) {
                Text(
                    text = if (isLogin.value) "Don't have an account?" else "Already have an account?",
                    modifier = Modifier()
                        .style("color", "var(--muted-text-color)")
                        .style("font-size", "14px")
                )
                
                Button(
                    onClick = { 
                        isLogin.value = !isLogin.value
                        // Clear form
                        username.value = ""
                        email.value = ""
                        password.value = ""
                        rememberMe.value = false
                    },
                    label = if (isLogin.value) Translations.get("auth.register", language) 
                           else Translations.get("auth.login", language),
                    variant = ButtonVariant.LINK,
                    modifier = Modifier()
                        .style("background", "transparent")
                        .style("border", "none")
                        .style("color", "var(--primary-color)")
                        .style("font-size", "14px")
                        .style("font-weight", "600")
                        .style("text-decoration", "underline")
                        .style("padding", "4px 8px")
                )
            }
        }
    }
}