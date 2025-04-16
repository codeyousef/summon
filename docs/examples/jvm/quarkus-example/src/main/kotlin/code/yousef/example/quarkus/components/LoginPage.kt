package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.utils.boxShadow
import code.yousef.example.quarkus.utils.hover
import code.yousef.example.quarkus.utils.marginVH
import code.yousef.example.quarkus.utils.paddingVH
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.Checkbox
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable
import kotlinx.html.input
import kotlinx.html.script

/**
 * Login page component with email and password form.
 */
@Composable
fun LoginPage() {
    ThemeWrapper {
        Box(
            modifier = Modifier().fillMaxWidth().fillMaxHeight()
                .padding("16px")
                .backgroundColor("#f8f9fa")
        ) {
            Column(
                modifier = Modifier().fillMaxWidth()
                    .maxWidth("400px")
                    .marginVH(vertical = "0", horizontal = "auto")
                    .marginTop("48px")
                    .padding("24px")
                    .backgroundColor("#fff")
                    .borderRadius("8px")
                    .boxShadow("0 4px 6px rgba(0, 0, 0, 0.1)")
            ) {
                // Logo and Header
                Box(
                    modifier = Modifier().fillMaxWidth()
                        .textAlign("center")
                        .marginBottom("24px")
                ) {
                    Text(
                        text = "Login",
                        modifier = Modifier().fontSize("24px")
                            .fontWeight("bold")
                    )
                }

                // Login Form
                Box(
                    modifier = Modifier().fillMaxWidth()
                        .attribute("id", "login-form")
                        .attribute("hx-post", "/api/auth/login")
                        .attribute("hx-swap", "outerHTML")
                ) {
                    Column(
                        modifier = Modifier().fillMaxWidth()
                            .gap("16px")
                    ) {
                        // Email field
                        Column(modifier = Modifier().fillMaxWidth()) {
                            Text(
                                text = "Email",
                                modifier = Modifier().fontWeight("bold").marginBottom("4px")
                            )
                            TextField(
                                value = "",
                                onValueChange = { /* This will be handled by the form submission */ },
                                modifier = Modifier().fillMaxWidth()
                                    .padding("8px")
                                    .borderRadius("4px")
                                    .border("1px", "solid", "#ced4da")
                                    .attribute("name", "email")
                                    .attribute("type", "email")
                                    .attribute("required", "true")
                                    .attribute("placeholder", "Enter your email")
                            )
                        }

                        // Password field
                        Column(modifier = Modifier().fillMaxWidth()) {
                            Text(
                                text = "Password",
                                modifier = Modifier().fontWeight("bold").marginBottom("4px")
                            )
                            TextField(
                                value = "",
                                onValueChange = { /* This will be handled by the form submission */ },
                                modifier = Modifier().fillMaxWidth()
                                    .padding("8px")
                                    .borderRadius("4px")
                                    .border("1px", "solid", "#ced4da")
                                    .attribute("name", "password")
                                    .attribute("type", "password")
                                    .attribute("required", "true")
                                    .attribute("placeholder", "Enter your password")
                            )
                        }

                        // Remember me and forgot password row
                        Row(
                            modifier = Modifier().fillMaxWidth()
                                .justifyContent("space-between")
                                .alignItems("center")
                                .marginTop("8px")
                        ) {
                            Row(
                                modifier = Modifier().alignItems("center")
                                    .gap("4px")
                            ) {
                                Checkbox(
                                    checked = false,
                                    onCheckedChange = { /* No action needed here */ },
                                    modifier = Modifier()
                                        .attribute("name", "remember")
                                        .attribute("id", "remember")
                                )
                                Text(
                                    text = "Remember me",
                                    modifier = Modifier().fontSize("14px")
                                )
                            }

                            Text(
                                text = "Forgot Password?",
                                modifier = Modifier().fontSize("14px")
                                    .color("#0d6efd")
                                    .cursor("pointer")
                                    .attribute("hx-get", "/forgot-password")
                                    .attribute("hx-target", "body")
                                    .attribute("hx-swap", "innerHTML")
                            )
                        }

                        // Login button
                        Button(
                            onClick = {},
                            label = "Login",
                            modifier = Modifier().fillMaxWidth()
                                .marginTop("24px")
                                .paddingVH(vertical = "10px", horizontal = "0")
                                .border("none", "", "")
                                .borderRadius("4px")
                                .backgroundColor("#0d6efd")
                                .color("#fff")
                                .cursor("pointer")
                                .attribute("type", "submit")
                        )

                        // Register link
                        Box(
                            modifier = Modifier().fillMaxWidth()
                                .textAlign("center")
                                .marginTop("16px")
                        ) {
                            Text(
                                text = "Don't have an account? ",
                                modifier = Modifier().fontSize("14px")
                                    .color("#6c757d")
                            )
                            Text(
                                text = "Sign up",
                                modifier = Modifier().fontSize("14px")
                                    .color("#0d6efd")
                                    .cursor("pointer")
                                    .attribute("hx-get", "/register")
                                    .attribute("hx-target", "body")
                                    .attribute("hx-swap", "innerHTML")
                            )
                        }
                    }
                }

                // Error message container
                Box(
                    modifier = Modifier().fillMaxWidth()
                        .marginTop("16px")
                        .padding("8px")
                        .backgroundColor("#f8d7da")
                        .color("#842029")
                        .borderRadius("4px")
                        .display("none")
                        .attribute("id", "login-error")
                ) {
                    Text(text = "")
                }

                // Define the JavaScript as a constant string
                val loginFormScript = """
                    document.addEventListener('htmx:beforeSend', function(evt) {
                        if (evt.target.id === 'login-form') {
                            const errorElement = document.getElementById('login-error');
                            if (errorElement) errorElement.style.display = 'none';
                        }
                    });

                    document.addEventListener('htmx:responseError', function(evt) {
                        if (evt.target.id === 'login-form') {
                            const errorElement = document.getElementById('login-error');
                             if (errorElement) {
                                errorElement.style.display = 'block';
                                errorElement.textContent = 'Invalid email or password';
                             }
                        }
                    });
                    """.trimIndent()

                // Use Box with modifiers to render the script tag
                Box(modifier = Modifier()
                    .style("element", "script") // Tell renderer to create a <script> tag
                    .style("content", loginFormScript) // Inject the raw JS content
                ) {}
            }
        }

        // Simple footer
        Footer()
    }
}

@Composable
private fun Footer() {
    Box(
        modifier = Modifier().fillMaxWidth()
            .padding("24px")
            .backgroundColor("#343a40")
            .color("#fff")
    ) {
        Row(
            modifier = Modifier().maxWidth("1200px")
                .marginVH(vertical = "0", horizontal = "auto")
                .justifyContent("space-between")
                .alignItems("center")
                .flexWrap("wrap")
                .gap("16px")
        ) {
            Text(
                text = "© 2023 Summon + Quarkus Demo",
                modifier = Modifier().color("#adb5bd")
            )

            Row(
                modifier = Modifier().gap("24px")
            ) {
                Text(
                    text = "About",
                    modifier = Modifier().color("#adb5bd")
                        .hover(
                            color = "#fff"
                        )
                        .cursor("pointer")
                )

                Text(
                    text = "Features",
                    modifier = Modifier().color("#adb5bd")
                        .hover(
                            color = "#fff"
                        )
                        .cursor("pointer")
                )

                Text(
                    text = "Contact",
                    modifier = Modifier().color("#adb5bd")
                        .hover(
                            color = "#fff"
                        )
                        .cursor("pointer")
                        .attribute("hx-get", "/contact")
                        .attribute("hx-target", "body")
                        .attribute("hx-swap", "innerHTML")
                )
            }
        }
    }
} 