package code.yousef.example.quarkus.components

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.Checkbox
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable
import kotlinx.html.script

/**
 * Registration page component for new user sign-up.
 */
@Composable
fun RegisterPage() {
    ThemeWrapper {
        Box(
            modifier = Modifier()
                .fillMaxWidth()
                .fillMaxHeight()
                .padding("16px")
                .background("#f8f9fa")
                .display("flex")
                .justifyContent("center")
                .alignItems("center")
        ) {
            Column(
                modifier = Modifier()
                    .maxWidth("450px")
                    .width("100%")
                    .padding("32px")
                    .background("#fff")
                    .borderRadius("8px")
                    .boxShadow("0 4px 6px rgba(0, 0, 0, 0.1)")
            ) {
                // Registration form header
                Text(
                    text = "Create Account",
                    modifier = Modifier()
                        .fontSize("24px")
                        .fontWeight("bold")
                        .color("#212529")
                        .marginBottom("24px")
                        .textAlign("center")
                )

                // Registration form
                Box(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .attribute("id", "registration-form")
                        .attribute("hx-post", "/api/register")
                        .attribute("hx-target", "body")
                        .attribute("hx-swap", "innerHTML")
                ) {
                    Column(
                        modifier = Modifier()
                            .gap("16px")
                            .fillMaxWidth()
                    ) {
                        // Full Name field
                        Column(modifier = Modifier().gap("8px")) {
                            Text(
                                text = "Full Name",
                                modifier = Modifier()
                                    .fontSize("14px")
                                    .fontWeight("medium")
                                    .color("#495057")
                            )

                            TextField(
                                value = "",
                                onValueChange = { /* Handled by form submission */ },
                                modifier = Modifier()
                                    .fillMaxWidth()
                                    .padding(top = "10px", bottom = "10px", left = "12px", right = "12px")
                                    .border("1px", "solid", "#ced4da")
                                    .borderRadius("4px")
                                    .fontSize("16px")
                                    .attribute("name", "name")
                                    .attribute("placeholder", "Enter your full name")
                                    .attribute("required", "true")
                            )
                        }

                        // Email field
                        Column(modifier = Modifier().gap("8px")) {
                            Text(
                                text = "Email Address",
                                modifier = Modifier()
                                    .fontSize("14px")
                                    .fontWeight("medium")
                                    .color("#495057")
                            )

                            TextField(
                                value = "",
                                onValueChange = { /* Handled by form submission */ },
                                modifier = Modifier()
                                    .fillMaxWidth()
                                    .padding(top = "10px", bottom = "10px", left = "12px", right = "12px")
                                    .border("1px", "solid", "#ced4da")
                                    .borderRadius("4px")
                                    .fontSize("16px")
                                    .attribute("type", "email")
                                    .attribute("name", "email")
                                    .attribute("placeholder", "Enter your email address")
                                    .attribute("required", "true")
                            )
                        }

                        // Password field
                        Column(modifier = Modifier().gap("8px")) {
                            Text(
                                text = "Password",
                                modifier = Modifier()
                                    .fontSize("14px")
                                    .fontWeight("medium")
                                    .color("#495057")
                            )

                            TextField(
                                value = "",
                                onValueChange = { /* Handled by form submission */ },
                                modifier = Modifier()
                                    .fillMaxWidth()
                                    .padding(top = "10px", bottom = "10px", left = "12px", right = "12px")
                                    .border("1px", "solid", "#ced4da")
                                    .borderRadius("4px")
                                    .fontSize("16px")
                                    .attribute("type", "password")
                                    .attribute("name", "password")
                                    .attribute("placeholder", "Create a password")
                                    .attribute("required", "true")
                            )
                        }

                        // Confirm Password field
                        Column(modifier = Modifier().gap("8px")) {
                            Text(
                                text = "Confirm Password",
                                modifier = Modifier()
                                    .fontSize("14px")
                                    .fontWeight("medium")
                                    .color("#495057")
                            )

                            TextField(
                                value = "",
                                onValueChange = { /* Handled by form submission */ },
                                modifier = Modifier()
                                    .fillMaxWidth()
                                    .padding(top = "10px", bottom = "10px", left = "12px", right = "12px")
                                    .border("1px", "solid", "#ced4da")
                                    .borderRadius("4px")
                                    .fontSize("16px")
                                    .attribute("type", "password")
                                    .attribute("name", "confirmPassword")
                                    .attribute("placeholder", "Confirm your password")
                                    .attribute("required", "true")
                            )
                        }

                        // Terms and conditions checkbox
                        Row(
                            modifier = Modifier()
                                .alignItems("center")
                                .gap("8px")
                                .marginTop("8px")
                        ) {
                            Checkbox(
                                checked = false,
                                onCheckedChange = { /* Handled by form submission */ },
                                modifier = Modifier()
                                    .attribute("name", "agreeTerms")
                                    .attribute("required", "true")
                            )

                            Text(
                                text = "I agree to the Terms of Service and Privacy Policy",
                                modifier = Modifier()
                                    .fontSize("14px")
                                    .color("#6c757d")
                            )
                        }

                        // Error message container
                        Box(
                            modifier = Modifier()
                                .fillMaxWidth()
                                .padding("12px")
                                .background("#f8d7da")
                                .borderRadius("4px")
                                .marginTop("16px")
                                .display("none")
                                .attribute("id", "registration-error")
                        ) {
                            Text(
                                text = "",
                                modifier = Modifier()
                                    .color("#842029")
                                    .fontSize("14px")
                            )
                        }

                        // Register button
                        Button(
                            onClick = { /* Handled by form submission */ },
                            label = "Create Account",
                            modifier = Modifier()
                                .fillMaxWidth()
                                .padding(top = "12px", bottom = "12px", left = "0", right = "0")
                                .background("#0d6efd")
                                .color("#fff")
                                .border("none", "", "")
                                .borderRadius("4px")
                                .fontSize("16px")
                                .fontWeight("medium")
                                .cursor("pointer")
                                .marginTop("16px")
                                .attribute("type", "submit")
                        )

                        // Login link
                        Row(
                            modifier = Modifier()
                                .justifyContent("center")
                                .marginTop("16px")
                        ) {
                            Text(
                                text = "Already have an account? ",
                                modifier = Modifier()
                                    .fontSize("14px")
                                    .color("#6c757d")
                            )

                            Text(
                                text = "Log in",
                                modifier = Modifier()
                                    .fontSize("14px")
                                    .color("#0d6efd")
                                    .cursor("pointer")
                                    .attribute("hx-get", "/login")
                                    .attribute("hx-target", "body")
                                    .attribute("hx-swap", "innerHTML")
                            )
                        }
                    }
                }
            }
        }

        // Define the validation script as a constant string
        val registrationFormScript = """
            document.addEventListener('htmx:afterSwap', function(event) {
                const form = document.getElementById('registration-form');
                if (!form) return;
                
                form.addEventListener('submit', function(e) {
                    const password = form.querySelector('input[name="password"]').value;
                    const confirmPassword = form.querySelector('input[name="confirmPassword"]').value;
                    const email = form.querySelector('input[name="email"]').value;
                    const errorDiv = document.getElementById('registration-error');
                    
                    // Always clear previous error first
                    if (errorDiv) {
                        errorDiv.textContent = '';
                        errorDiv.style.display = 'none';
                    }

                    // Validate email format
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailRegex.test(email)) {
                        e.preventDefault();
                        if(errorDiv) {
                           errorDiv.textContent = 'Please enter a valid email address';
                           errorDiv.style.display = 'block';
                        }
                        return false;
                    }

                    // Validate password strength
                    if (password.length < 8) {
                        e.preventDefault();
                        if(errorDiv) {
                           errorDiv.textContent = 'Password must be at least 8 characters long';
                           errorDiv.style.display = 'block';
                        }
                        return false;
                    }

                    // Validate passwords match
                    if (password !== confirmPassword) {
                        e.preventDefault();
                        if(errorDiv) {
                            errorDiv.textContent = 'Passwords do not match';
                            errorDiv.style.display = 'block';
                        }
                        return false;
                    }
                    
                });
            });
            """.trimIndent()

        // Use Box with modifiers to render the script tag
        Box(modifier = Modifier()
            .style("element", "script") // Tell renderer to create a <script> tag
            .style("content", registrationFormScript) // Inject the raw JS content
        ) {}
    }
} 