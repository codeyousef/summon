package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.User
import code.yousef.example.quarkus.currentConsumer
import code.yousef.example.quarkus.utils.marginVH
import code.yousef.example.quarkus.utils.padding
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
import kotlinx.html.*

/**
 * User edit page component for editing existing users.
 */
@Composable
fun UserEditPage(user: User) {
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
                    text = "Edit User",
                    modifier = Modifier()
                        .fontSize("24px")
                        .fontWeight("bold")
                        .marginBottom("24px")
                )

                // Form with hx-put attribute
                Box(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .attribute("hx-put", "/api/users/${user.id}")
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
                            value = user.name,
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
                            value = user.email,
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
                                        selected = user.role == "USER"
                                        +"User"
                                    }
                                    option {
                                        value = "ADMIN"
                                        selected = user.role == "ADMIN"
                                        +"Admin"
                                    }
                                }
                            }
                        }
                    }

                    // Active checkbox
                    Box(modifier = Modifier().marginBottom("16px").display("flex").alignItems("center")) {
                        Checkbox(
                            checked = user.active,
                            onCheckedChange = { /* Handled by form submission */ },
                            modifier = Modifier()
                                .marginRight("8px")
                                .attribute("id", "active")
                                .attribute("name", "active")
                        )
                        Text(
                            text = "Active",
                            modifier = Modifier()
                                .fontSize("14px")
                        )
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
                            label = "Save Changes",
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
                                if (evt.detail.requestConfig.path === '/api/users/${user.id}' && evt.detail.xhr.status === 200) {
                                    window.location.href = '/';
                                } else if (evt.detail.requestConfig.path === '/api/users/${user.id}' && evt.detail.xhr.status !== 200) {
                                    alert('Error updating user: ' + evt.detail.xhr.responseText);
                                }
                            });
                        """
                    }
                }
            }
        }
    }
} 