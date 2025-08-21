package code.yousef.example.springboot.pages

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.input.Checkbox
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.style.*
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.FontWeight
// import code.yousef.summon.modifier.GradientPresets.modernPrimary  // Temporarily disabled
// import code.yousef.summon.modifier.glassMorphism  // Temporarily disabled
import code.yousef.summon.extensions.*
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.runtime.remember
// import code.yousef.summon.i18n.rememberI18n  // Temporarily disabled
import code.yousef.example.springboot.*

@Composable
fun AuthPage(
    isLogin: Boolean = true,
    mode: String = "login" // "login" or "register"
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val rememberMe = remember { mutableStateOf(false) }
    val isLoginMode = mode == "login"
    // val i18n = rememberI18n()  // Temporarily disabled
    
    // Temporarily disabled theming features
    
    Column(
        modifier = Modifier()
            .width("100%")
            .minHeight(100.vh)
            .backgroundColor("#f0f2f5") // Simple background color instead of gradient
            .display("flex")
            .alignItems("center")
            .justifyContent("center")
            .padding(20.px)
    ) {
        Div(
            modifier = Modifier()
                .width(400.px)
                .maxWidth(400.px)
                .margin("0 auto")
                .attribute("class", "auth-container")
        ) {
            Column(
                modifier = Modifier()
                    .padding(32.px)
                    .backgroundColor("white")
                    .boxShadow("0 4px 6px rgba(0, 0, 0, 0.1)") // Simple shadow instead of glassmorphism
                    .borderRadius(16.px)
            ) {
            // App Title Section
            Column(
                modifier = Modifier()
                    .textAlign("center")
                    .marginBottom(24.px)
            ) {
                Text(
                    "Todo App",
                    modifier = Modifier()
                        .fontSize(28.px)
                        .fontWeight(FontWeight.Bold)
                        .color("#2d3748")
                        .marginBottom(8.px)
                )
                
                Text(
                    "Manage your tasks with style",
                    modifier = Modifier()
                        .fontSize(14.px)
                        .color("#718096")
                        .attribute("style", "line-height: 1.5;")
                )
            }
            
            // Welcome Message
            Text(
                if (isLoginMode) "Welcome back!" else "Create your account",
                modifier = Modifier()
                    .fontSize(18.px)
                    .fontWeight(FontWeight.SemiBold)
                    .color("#4a5568")
                    .textAlign("center")
                    .marginBottom(24.px)
            )
            
            // Login Form
            if (isLoginMode) {
                // HTML Form element for login
                Div(
                    modifier = Modifier()
                        .attribute("id", "loginFormContainer")
                ) {
                    // Create actual HTML form for login
                    Div(
                        modifier = Modifier()
                            .attribute("id", "loginForm")
                            .attribute("data-form", "login")
                    ) {
                        Column(modifier = Modifier().gap(16.px)) {
                            // Username field
                            Column(modifier = Modifier().gap(4.px)) {
                                Text(
                                    "Username",
                                    modifier = Modifier()
                                        .fontSize(14.px)
                                        .fontWeight(FontWeight.Medium)
                                        .color("#555")
                                )
                                TextField(
                                    value = username.value,
                                    onValueChange = { username.value = it },
                                    placeholder = "Enter your username",
                                    modifier = Modifier()
                                        .padding(12.px)
                                        .borderRadius(8.px)
                                        .border("1px", "solid", "#ddd")
                                        .backgroundColor("white")
                                        .attribute("name", "username")
                                        .attribute("required", "true")
                                )
                            }
                            
                            // Password field
                            Column(modifier = Modifier().gap(4.px)) {
                                Text(
                                    "Password",
                                    modifier = Modifier()
                                        .fontSize(14.px)
                                        .fontWeight(FontWeight.Medium)
                                        .color("#555")
                                )
                                TextField(
                                    value = password.value,
                                    onValueChange = { password.value = it },
                                    placeholder = "Enter your password",
                                    modifier = Modifier()
                                        .padding(12.px)
                                        .borderRadius(8.px)
                                        .border("1px", "solid", "#ddd")
                                        .backgroundColor("white")
                                        .attribute("type", "password")
                                        .attribute("name", "password")
                                        .attribute("required", "true")
                                )
                            }
                            
                            // Remember me checkbox
                            Row(
                                modifier = Modifier()
                                    .alignItems("center")
                                    .gap(8.px)
                            ) {
                                Checkbox(
                                    checked = rememberMe.value,
                                    onCheckedChange = { rememberMe.value = it },
                                    modifier = Modifier().attribute("name", "rememberMe")
                                )
                                Text(
                                    "Remember me",
                                    modifier = Modifier()
                                        .fontSize(14.px)
                                        .color("#666")
                                )
                            }
                            
                            // Submit Button
                            Button(
                                onClick = {
                                    // This will be handled by JavaScript
                                },
                                label = "Login",
                                modifier = Modifier()
                                    .width("100%")
                                    .padding("12px 24px")
                                    .borderRadius(8.px)
                                    .backgroundColor("#1976d2")
                                    .color("white")
                                    .fontWeight(FontWeight.Bold)
                                    .cursor("pointer")
                                    .hover { backgroundColor("#1565c0") }
                                    .attribute("type", "submit")
                            )
                        }
                    }
                }
                
                // Sample Credentials Section (like JS example)
                Column(
                    modifier = Modifier()
                        .marginTop(16.px)
                        .padding(16.px)
                        .backgroundColor("#f0fff4")
                        .borderRadius(8.px)
                        .border("1px", "solid", "#9ae6b4")
                ) {
                    Text(
                        "💡 New here? Just register with any credentials!",
                        modifier = Modifier()
                            .fontSize(12.px)
                            .fontWeight(FontWeight.SemiBold)
                            .color("#2f855a")
                            .marginBottom(8.px)
                    )
                    
                    Text(
                        "The app uses a clean database - create your account to get started.",
                        modifier = Modifier()
                            .fontSize(11.px)
                            .color("#38a169")
                            .attribute("style", "line-height: 1.4;")
                    )
                }
            } else {
                // Register Form
                Div(
                    modifier = Modifier()
                        .attribute("id", "registerFormContainer")
                ) {
                    Div(
                        modifier = Modifier()
                            .attribute("id", "registerForm")
                            .attribute("data-form", "register")
                    ) {
                        Column(modifier = Modifier().gap(16.px)) {
                            // Email field
                            Column(modifier = Modifier().gap(4.px)) {
                                Text(
                                    "Email",
                                    modifier = Modifier()
                                        .fontSize(14.px)
                                        .fontWeight(FontWeight.Medium)
                                        .color("#555")
                                )
                                TextField(
                                    value = email.value,
                                    onValueChange = { email.value = it },
                                    placeholder = "Enter your email",
                                    modifier = Modifier()
                                        .padding(12.px)
                                        .borderRadius(8.px)
                                        .border("1px", "solid", "#ddd")
                                        .backgroundColor("white")
                                        .attribute("name", "email")
                                        .attribute("type", "email")
                                        .attribute("required", "true")
                                )
                            }
                            
                            // Username field
                            Column(modifier = Modifier().gap(4.px)) {
                                Text(
                                    "Username",
                                    modifier = Modifier()
                                        .fontSize(14.px)
                                        .fontWeight(FontWeight.Medium)
                                        .color("#555")
                                )
                                TextField(
                                    value = username.value,
                                    onValueChange = { username.value = it },
                                    placeholder = "Enter your username",
                                    modifier = Modifier()
                                        .padding(12.px)
                                        .borderRadius(8.px)
                                        .border("1px", "solid", "#ddd")
                                        .backgroundColor("white")
                                        .attribute("name", "username")
                                        .attribute("required", "true")
                                )
                            }
                            
                            // Password field
                            Column(modifier = Modifier().gap(4.px)) {
                                Text(
                                    "Password",
                                    modifier = Modifier()
                                        .fontSize(14.px)
                                        .fontWeight(FontWeight.Medium)
                                        .color("#555")
                                )
                                TextField(
                                    value = password.value,
                                    onValueChange = { password.value = it },
                                    placeholder = "Enter your password",
                                    modifier = Modifier()
                                        .padding(12.px)
                                        .borderRadius(8.px)
                                        .border("1px", "solid", "#ddd")
                                        .backgroundColor("white")
                                        .attribute("type", "password")
                                        .attribute("name", "password")
                                        .attribute("required", "true")
                                )
                            }
                            
                            // Submit Button
                            Button(
                                onClick = {
                                    // This will be handled by JavaScript
                                },
                                label = "Register",
                                modifier = Modifier()
                                    .width("100%")
                                    .padding("12px 24px")
                                    .borderRadius(8.px)
                                    .backgroundColor("#1976d2")
                                    .color("white")
                                    .fontWeight(FontWeight.Bold)
                                    .cursor("pointer")
                                    .hover { backgroundColor("#1565c0") }
                                    .attribute("type", "submit")
                            )
                        }
                    }
                }
            }
            
            // Toggle between login/register
            Row(
                modifier = Modifier()
                    .marginTop(16.px)
                    .justifyContent("center")
                    .alignItems("center")
                    .gap(4.px)
            ) {
                Text(
                    if (isLoginMode) "Don't have an account?" else "Already have an account?",
                    modifier = Modifier()
                        .color("#666")
                        .fontSize(14.px)
                )
                
                Button(
                    onClick = {
                        // Toggle will be handled by server-side routing
                    },
                    label = if (isLoginMode) "Register" else "Login",
                    modifier = Modifier()
                        .color("#1976d2")
                        .fontSize(14.px)
                        .fontWeight(FontWeight.Bold)
                        .backgroundColor("transparent")
                        .border("0", "none", "transparent")
                        .padding(0.px)
                        .cursor("pointer")
                        .hover { textDecoration("underline") }
                        .attribute("id", "toggleAuth")
                )
            }
            
            // JWT info
            Text(
                "Using JWT Authentication",
                modifier = Modifier()
                    .fontSize(12.px)
                    .color("#1976d2")
                    .marginTop(24.px)
                    .textAlign("center")
            )
            
            // Registration info
            if (isLoginMode) {
                Div(
                    modifier = Modifier()
                        .marginTop(16.px)
                        .padding(12.px)
                        .backgroundColor("#e3f2fd")
                        .borderRadius(8.px)
                        .border("1px", "solid", "#bbdefb")
                ) {
                    Text(
                        "New User?",
                        modifier = Modifier()
                            .fontSize(12.px)
                            .fontWeight(FontWeight.Bold)
                            .color("#1565c0")
                            .marginBottom(4.px)
                    )
                    Text(
                        "Click 'Register' to create a new account",
                        modifier = Modifier()
                            .fontSize(11.px)
                            .color("#1976d2")
                    )
                }
            }
            
            // Powered by
            Text(
                "Powered by Spring Boot & Summon",
                modifier = Modifier()
                    .fontSize(12.px)
                    .color("#999")
                    .marginTop(16.px)
                    .textAlign("center")
            )
            }
        }
    }
}