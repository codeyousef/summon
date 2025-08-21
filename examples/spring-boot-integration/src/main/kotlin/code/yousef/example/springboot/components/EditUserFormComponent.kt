package code.yousef.example.springboot.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.extensions.*
import code.yousef.summon.state.*
import code.yousef.summon.modifier.FontWeight
import code.yousef.summon.runtime.remember
import code.yousef.example.springboot.*

/**
 * Pure Summon edit user form component - no raw HTML/CSS/JS
 */
@Composable
fun EditUserFormComponent(user: User) {
    val name = remember { mutableStateOf(user.name) }
    val email = remember { mutableStateOf(user.email) }
    val role = remember { mutableStateOf(user.role) }
    
    // Card-like div
    Div(
        modifier = Modifier()
            .fillMaxWidth()
            .backgroundColor("white")
            .boxShadow("0 2px 4px rgba(0,0,0,0.1)")
            .borderRadius(8.px)
    ) {
        Column(
            modifier = Modifier()
                .padding(2.rem)
        ) {
            // Title
            Text(
                "Edit User",
                modifier = Modifier()
                    .fontSize(1.5.rem)
                    .fontWeight(FontWeight.Bold)
                    .marginBottom(1.5.rem)
                    .color("#333")
            )
            
            // Form div with attributes
            Div(
                modifier = Modifier()
                    .fillMaxWidth()
                    .attribute("action", "/users/${user.id}")
                    .attribute("method", "POST")
            ) {
                Column(
                    modifier = Modifier()
                        .gap(1.rem)
                ) {
                    // Name field
                    Column(modifier = Modifier().gap(0.25.rem)) {
                        Text(
                            "Name *",
                            modifier = Modifier()
                                .fontSize(0.875.rem)
                                .fontWeight(FontWeight.Medium)
                                .color("#555")
                        )
                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            placeholder = "Enter user name",
                            modifier = Modifier()
                                .fillMaxWidth()
                                .padding(0.75.rem)
                                .borderRadius(4.px)
                                .border("1px", "solid", "#ddd")
                                .attribute("name", "name")
                                .attribute("required", "true")
                        )
                    }
                    
                    // Email field
                    Column(modifier = Modifier().gap(0.25.rem)) {
                        Text(
                            "Email *",
                            modifier = Modifier()
                                .fontSize(0.875.rem)
                                .fontWeight(FontWeight.Medium)
                                .color("#555")
                        )
                        TextField(
                            value = email.value,
                            onValueChange = { email.value = it },
                            placeholder = "Enter email address",
                            modifier = Modifier()
                                .fillMaxWidth()
                                .padding(0.75.rem)
                                .borderRadius(4.px)
                                .border("1px", "solid", "#ddd")
                                .attribute("name", "email")
                                .attribute("type", "email")
                                .attribute("required", "true")
                        )
                    }
                    
                    // Role field
                    Column(modifier = Modifier().gap(0.25.rem)) {
                        Text(
                            "Role *",
                            modifier = Modifier()
                                .fontSize(0.875.rem)
                                .fontWeight(FontWeight.Medium)
                                .color("#555")
                        )
                        // Simple dropdown simulation
                        Div(
                            modifier = Modifier()
                                .fillMaxWidth()
                                .padding(0.75.rem)
                                .borderRadius(4.px)
                                .border("1px", "solid", "#ddd")
                                .backgroundColor("white")
                                .attribute("name", "role")
                        ) {
                            Text(role.value.ifEmpty { "Select a role" })
                        }
                    }
                    
                    // Buttons
                    Row(
                        modifier = Modifier()
                            .fillMaxWidth()
                            .marginTop(1.rem)
                            .gap(1.rem)
                    ) {
                        Button(
                            onClick = { /* Form submission handled by HTML form */ },
                            label = "Update User",
                            modifier = Modifier()
                                .padding("0.75rem 2rem")
                                .backgroundColor("#1976d2")
                                .color("white")
                                .borderRadius(4.px)
                                .cursor("pointer")
                                .hover { backgroundColor("#1565c0") }
                        )
                        
                        // Cancel button styled as link
                        Div(
                            modifier = Modifier()
                                .padding("0.75rem 2rem")
                                .backgroundColor("#e0e0e0")
                                .color("#666")
                                .borderRadius(4.px)
                                .cursor("pointer")
                                .onClick { /* Navigate to /users */ }
                                .attribute("onclick", "window.location.href='/users'")
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}