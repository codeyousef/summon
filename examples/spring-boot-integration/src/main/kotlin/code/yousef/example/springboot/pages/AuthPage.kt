package code.yousef.example.springboot.pages

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.input.Checkbox
import code.yousef.summon.components.layout.*
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.FontWeight
import code.yousef.summon.extensions.*
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.runtime.remember
import code.yousef.example.springboot.*

@Composable
fun AuthPage(
    isLogin: Boolean = true
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val rememberMe = remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier()
            .fillMaxWidth()
            .minHeight(100.vh)
            .backgroundColor("#f5f5f5")
            .display("flex")
            .alignItems("center")
            .justifyContent("center")
            .padding(20.px)
    ) {
        Column(
            modifier = Modifier()
                .maxWidth(400.px)
                .fillMaxWidth()
                .padding(40.px)
                .backgroundColor("white")
                .borderRadius(12.px)
                .boxShadow("0 4px 6px rgba(0,0,0,0.1)")
        ) {
            // App Title
            Text(
                "Todo App",
                modifier = Modifier()
                    .fontSize(28.px)
                    .fontWeight(FontWeight.Bold)
                    .color("#333")
                    .marginBottom(8.px)
                    .textAlign("center")
            )
            
            Text(
                "Spring Boot + Summon Example",
                modifier = Modifier()
                    .fontSize(16.px)
                    .color("#666")
                    .marginBottom(32.px)
                    .textAlign("center")
            )
            
            // Auth Form Title
            Text(
                if (isLogin) "Login" else "Register",
                modifier = Modifier()
                    .fontSize(24.px)
                    .fontWeight(FontWeight.Bold)
                    .color("#333")
                    .marginBottom(24.px)
                    .textAlign("center")
            )
            
            // Form Fields
            if (!isLogin) {
                Column(modifier = Modifier().gap(4.px).marginBottom(16.px)) {
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
                            .fillMaxWidth()
                            .padding(12.px)
                            .borderRadius(8.px)
                            .border("1px", "solid", "#ddd")
                            .backgroundColor("white")
                    )
                }
            }
            
            Column(modifier = Modifier().gap(4.px).marginBottom(16.px)) {
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
                        .fillMaxWidth()
                        .padding(12.px)
                        .borderRadius(8.px)
                        .border("1px", "solid", "#ddd")
                        .backgroundColor("white")
                )
            }
            
            Column(modifier = Modifier().gap(4.px).marginBottom(16.px)) {
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
                        .fillMaxWidth()
                        .padding(12.px)
                        .borderRadius(8.px)
                        .border("1px", "solid", "#ddd")
                        .backgroundColor("white")
                        .attribute("type", "password")
                )
            }
            
            // Remember me checkbox
            if (isLogin) {
                Row(
                    modifier = Modifier()
                        .marginBottom(24.px)
                        .alignItems("center")
                        .gap(8.px)
                ) {
                    Checkbox(
                        checked = rememberMe.value,
                        onCheckedChange = { rememberMe.value = it }
                    )
                    Text(
                        "Remember me",
                        modifier = Modifier()
                            .fontSize(14.px)
                            .color("#666")
                    )
                }
            } else {
                Spacer(modifier = Modifier().height(24.px))
            }
            
            // Submit Button
            Button(
                onClick = {
                    // Form submission will be handled by the server
                },
                label = if (isLogin) "Login" else "Register",
                modifier = Modifier()
                    .fillMaxWidth()
                    .padding("12px 24px")
                    .borderRadius(8.px)
                    .backgroundColor("#1976d2")
                    .color("white")
                    .fontWeight(FontWeight.Bold)
                    .marginBottom(16.px)
                    .cursor("pointer")
                    .hover { backgroundColor("#1565c0") }
            )
            
            // Toggle between login/register
            Row(
                modifier = Modifier()
                    .marginTop(16.px)
                    .justifyContent("center")
                    .alignItems("center")
                    .gap(4.px)
            ) {
                Text(
                    if (isLogin) "Don't have an account?" else "Already have an account?",
                    modifier = Modifier()
                        .color("#666")
                        .fontSize(14.px)
                )
                
                Button(
                    onClick = {
                        // Toggle will be handled by server-side routing
                    },
                    label = if (isLogin) "Register" else "Login",
                    modifier = Modifier()
                        .color("#1976d2")
                        .fontSize(14.px)
                        .fontWeight(FontWeight.Bold)
                        .backgroundColor("transparent")
                        .border("0", "none", "transparent")
                        .padding(0.px)
                        .cursor("pointer")
                        .hover { textDecoration("underline") }
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