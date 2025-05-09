package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.User
import code.yousef.example.quarkus.utils.boxShadow
import code.yousef.example.quarkus.utils.hover
import code.yousef.example.quarkus.utils.paddingVH
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable


/**
 * Users management page component.
 */
@Composable
fun UsersPage(users: List<User>) {
    ThemeWrapper {
        Box(
            modifier = Modifier()
                .fillMaxWidth()
                .fillMaxHeight()
                .padding("16px")
        ) {
            Column(
                modifier = Modifier()
                    .fillMaxWidth()
                    .maxWidth("1000px")
                    .margin("0 auto")
                    .padding("24px")
                    .backgroundColor("#fff")
                    .borderRadius("8px")
                    .boxShadow("0 4px 6px rgba(0, 0, 0, 0.1)")
            ) {
                // Header with title and add button
                Row(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .justifyContent("space-between")
                        .alignItems("center")
                        .style("margin-bottom", "24px")
                ) {
                    Text(
                        text = "User Management",
                        modifier = Modifier()
                            .fontSize("24px")
                            .fontWeight("bold")
                    )

                    Button(
                        onClick = { /* HTMX handles this */ },
                        label = "Add New User",
                        modifier = Modifier()
                            .paddingVH(vertical = "8px", horizontal = "16px")
                            .border(style = BorderStyle.None, width = "0px")
                            .borderRadius("4px")
                            .backgroundColor("#28a745")
                            .color("#fff")
                            .cursor("pointer")
                            .attribute("hx-get", "/register")
                            .attribute("hx-target", "body")
                            .attribute("hx-swap", "innerHTML")
                    )
                }

                // Users table
                Box(
                    modifier = Modifier()
                        .fillMaxWidth()
                        .style("overflow", "auto")
                        .border(1, style = BorderStyle.Solid, color = "#dee2e6")
                        .borderRadius("4px")
                ) {
                    // Table header
                    Row(
                        modifier = Modifier()
                            .fillMaxWidth()
                            .backgroundColor("#f8f9fa")
                            .padding("12px")
                            .border(1, style = BorderStyle.Solid, color = "#dee2e6")
                            .fontWeight("bold")
                    ) {
                        Box(modifier = Modifier().width("5%").textAlign("center")) {
                            Text(text = "ID")
                        }
                        Box(modifier = Modifier().width("25%")) {
                            Text(text = "Name")
                        }
                        Box(modifier = Modifier().width("30%")) {
                            Text(text = "Email")
                        }
                        Box(modifier = Modifier().width("15%").textAlign("center")) {
                            Text(text = "Role")
                        }
                        Box(modifier = Modifier().width("10%").textAlign("center")) {
                            Text(text = "Status")
                        }
                        Box(modifier = Modifier().width("15%").textAlign("center")) {
                            Text(text = "Actions")
                        }
                    }

                    // Table rows for each user
                    if (users.isEmpty()) {
                        Row(
                            modifier = Modifier()
                                .fillMaxWidth()
                                .padding("16px")
                                .textAlign("center")
                        ) {
                            Box(modifier = Modifier().fillMaxWidth()) {
                                Text(
                                    text = "No users found. Click 'Add New User' to create one.",
                                    modifier = Modifier().color("#6c757d")
                                )
                            }
                        }
                    } else {
                        Column(modifier = Modifier().fillMaxWidth()) {
                            users.forEach { user ->
                                Row(
                                    modifier = Modifier()
                                        .fillMaxWidth()
                                        .padding("12px")
                                        .border(1, style = BorderStyle.Solid, color = "#dee2e6")
                                        .backgroundColor("#ffffff")
                                        .hover(
                                            backgroundColor = "#f8f9fa"
                                        )
                                ) {
                                    // ID Column
                                    Box(modifier = Modifier().width("5%").textAlign("center")) {
                                        Text(text = "${user.id}")
                                    }

                                    // Name Column
                                    Box(modifier = Modifier().width("25%")) {
                                        Text(text = user.name)
                                    }

                                    // Email Column
                                    Box(modifier = Modifier().width("30%")) {
                                        Text(text = user.email)
                                    }

                                    // Role Column
                                    Box(modifier = Modifier().width("15%").textAlign("center")) {
                                        Box(
                                            modifier = Modifier()
                                                .display("inline-block")
                                                .paddingVH(vertical = "4px", horizontal = "8px")
                                                .borderRadius("4px")
                                                .backgroundColor(
                                                    when (user.role) {
                                                        "ADMIN" -> "#dc3545"
                                                        else -> "#6c757d"
                                                    }
                                                )
                                                .color("#fff")
                                                .fontSize("12px")
                                        ) {
                                            Text(text = user.role)
                                        }
                                    }

                                    // Status Column
                                    Box(modifier = Modifier().width("10%").textAlign("center")) {
                                        Box(
                                            modifier = Modifier()
                                                .display("inline-block")
                                                .paddingVH(vertical = "4px", horizontal = "8px")
                                                .borderRadius("4px")
                                                .backgroundColor(
                                                    if (user.active) "#28a745" else "#dc3545"
                                                )
                                                .color("#fff")
                                                .fontSize("12px")
                                        ) {
                                            Text(
                                                text = if (user.active) "Active" else "Inactive"
                                            )
                                        }
                                    }

                                    // Actions Column
                                    Box(modifier = Modifier().width("15%").textAlign("center")) {
                                        Row(
                                            modifier = Modifier()
                                                .justifyContent("center")
                                                .gap("8px")
                                        ) {
                                            // Edit Button
                                            Button(
                                                onClick = { /* HTMX handles this */ },
                                                label = "Edit",
                                                modifier = Modifier()
                                                    .paddingVH(vertical = "4px", horizontal = "8px")
                                                    .border(style = BorderStyle.None, width = "0px")
                                                    .borderRadius("4px")
                                                    .backgroundColor("#007bff")
                                                    .color("#fff")
                                                    .cursor("pointer")
                                                    .attribute("hx-get", "/edit/${user.id}")
                                                    .attribute("hx-target", "body")
                                                    .attribute("hx-swap", "innerHTML")
                                            )

                                            // Toggle Status Button
                                            Button(
                                                onClick = { /* HTMX handles this */ },
                                                label = if (user.active) "Deactivate" else "Activate",
                                                modifier = Modifier()
                                                    .paddingVH(vertical = "4px", horizontal = "8px")
                                                    .border(style = BorderStyle.None, width = "0px")
                                                    .borderRadius("4px")
                                                    .backgroundColor(
                                                        if (user.active) "#dc3545" else "#28a745"
                                                    )
                                                    .color("#fff")
                                                    .cursor("pointer")
                                                    .attribute(
                                                        "hx-put",
                                                        if (user.active) "/api/users/${user.id}/deactivate" else "/api/users/${user.id}/activate"
                                                    )
                                                    .attribute("hx-target", "body")
                                                    .attribute("hx-swap", "innerHTML")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Helper function to get a color based on user role
 */
private fun getRoleColor(role: String): String {
    return when (role.lowercase()) {
        "admin" -> "#FF5722"  // Deep Orange
        "moderator" -> "#9C27B0"  // Purple
        "editor" -> "#3F51B5"  // Indigo
        "user" -> "#009688"  // Teal
        else -> "#607D8B"  // Blue Grey
    }
} 