package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.currentConsumer
import code.yousef.example.quarkus.utils.marginVH
import code.yousef.example.quarkus.utils.padding
import code.yousef.example.quarkus.utils.paddingVH
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.attribute
import code.yousef.summon.modifier.display
import code.yousef.summon.modifier.justifyContent
import code.yousef.summon.runtime.Composable
import kotlinx.html.InputType
import kotlinx.html.div
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.option
import kotlinx.html.script
import kotlinx.html.select
import kotlinx.html.style

/**
 * Registration page component for creating new users.
 */
@Composable
fun RegistrationPage() {
    ThemeWrapper {
        Box(
            modifier = Modifier()
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(all = "16px")
        ) {
            Column(
                modifier = Modifier()
                    .fillMaxWidth()
                    .maxWidth("600px")
                    .marginVH(vertical = "0px", horizontal = "auto")
                    .padding(all = "24px")
                    .backgroundColor("#fff")
                    .borderRadius("8px")
                    .style("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)")
            ) {
                Text(
                    text = "Register New User",
                    modifier = Modifier()
                        .fontSize("24px")
                        .fontWeight("bold")
                        .marginBottom("24px")
                )

                // Form with hx-post attribute
                Box(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .attribute("hx-post", "/api/users")
                        .attribute("hx-swap", "none")
                ) {
                    // Name input
                    Box(modifier = Modifier().marginBottom("16px")) {
                        Text(
                            text = "Name",
                            modifier = Modifier()
                                .display("block")
                                .marginBottom("8px")
                                .fontSize("14px")
                        )
                        TextField(
                            value = "",
                            onValueChange = { /* Handled by form submission */ },
                            modifier = Modifier()
                                .fillMaxWidth()
                                .paddingVH(vertical = "8px", horizontal = "12px")
                                .border("1px", "solid", "#ccc")
                                .borderRadius("4px")
                                .attribute("id", "name")
                                .attribute("name", "name")
                                .attribute("required", "true")
                        )
                    }

                    // Email input
                    Box(modifier = Modifier().marginBottom("16px")) {
                        Text(
                            text = "Email",
                            modifier = Modifier()
                                .display("block")
                                .marginBottom("8px")
                                .fontSize("14px")
                        )
                        TextField(
                            value = "",
                            onValueChange = { /* Handled by form submission */ },
                            modifier = Modifier()
                                .fillMaxWidth()
                                .paddingVH(vertical = "8px", horizontal = "12px")
                                .border("1px", "solid", "#ccc")
                                .borderRadius("4px")
                                .attribute("id", "email")
                                .attribute("name", "email")
                                .attribute("type", "email")
                                .attribute("required", "true")
                        )
                    }

                    // Role dropdown
                    Box(modifier = Modifier().marginBottom("16px")) {
                        Text(
                            text = "Role",
                            modifier = Modifier()
                                .display("block")
                                .marginBottom("8px")
                                .fontSize("14px")
                        )
                        Box(
                            modifier = Modifier()
                                .fillMaxWidth()
                                .backgroundColor("#fff")
                                .border("1px", "solid", "#ccc")
                                .borderRadius("4px")
                                .paddingVH(vertical = "8px", horizontal = "12px")
                        ) {
                            currentConsumer().div {
                                style = "width: 100%"
                                select {
                                    id = "role"
                                    name = "role"
                                    style = "width: 100%; border: none; outline: none; background: transparent;"

                                    option {
                                        value = "USER"
                                        +"User"
                                    }
                                    option {
                                        value = "ADMIN"
                                        +"Admin"
                                    }
                                }
                            }
                        }
                    }

                    // Active checkbox (hidden - new users are active by default)
                    currentConsumer().div {
                        input {
                            type = InputType.checkBox
                            id = "active"
                            name = "active"
                            value = "true"
                            checked = true
                            attributes["hidden"] = "hidden"
                        }
                    }

                    // Buttons
                    Row(
                        modifier = Modifier()
                            .fillMaxWidth()
                            .marginTop("24px")
                            .justifyContent("space-between")
                    ) {
                        Button(
                            onClick = { /* No action needed */ },
                            label = "Cancel",
                            modifier = Modifier()
                                .paddingVH(vertical = "8px", horizontal = "16px")
                                .border("none", "", "")
                                .borderRadius("4px")
                                .backgroundColor("#6c757d")
                                .color("#fff")
                                .cursor("pointer")
                                .attribute("hx-get", "/")
                        )

                        Button(
                            onClick = { /* No action needed */ },
                            label = "Register",
                            modifier = Modifier()
                                .paddingVH(vertical = "8px", horizontal = "16px")
                                .border("none", "", "")
                                .borderRadius("4px")
                                .backgroundColor("#007bff")
                                .color("#fff")
                                .cursor("pointer")
                                .attribute("type", "submit")
                        )
                    }

                    // Add script for form handling
                    currentConsumer().script {
                        +"""
                            document.body.addEventListener('htmx:afterRequest', function(evt) {
                                if (evt.detail.requestConfig.path === '/api/users' && evt.detail.xhr.status === 201) {
                                    window.location.href = '/';
                                } else if (evt.detail.requestConfig.path === '/api/users' && evt.detail.xhr.status !== 201) {
                                    alert('Error creating user: ' + evt.detail.xhr.responseText);
                                }
                            });
                        """
                    }
                }
            }
        }
    }
} 