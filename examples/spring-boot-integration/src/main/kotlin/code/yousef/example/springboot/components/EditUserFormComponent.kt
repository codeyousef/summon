package code.yousef.example.springboot.components

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.extensions.*
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.modifier.FontWeight
import code.yousef.summon.runtime.remember
import code.yousef.summon.runtime.SelectOption
import code.yousef.example.springboot.*

/**
 * Pure Summon edit user form component using proper Form components
 */
@Composable
fun EditUserFormComponent(user: User) {
    // State for form fields - initialized with user data
    val nameState = remember { mutableStateOf(user.name) }
    val emailState = remember { mutableStateOf(user.email) }
    val roleState = remember { mutableStateOf<String?>(user.role) }
    
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
            
            // Form using proper Summon Form component
            Form(
                onSubmit = { formData ->
                    // This will be handled by the server when form is submitted
                    println("Form submitted: $formData")
                },
                modifier = Modifier()
                    .attribute("action", "/users/${user.id}")
                    .attribute("method", "POST")
            ) {
                Column(
                    modifier = Modifier().gap(1.5.rem)
                ) {
                    // Name field using FormField
                    FormField(
                        label = {
                            Text(
                                "Name *",
                                modifier = Modifier()
                                    .fontSize(0.875.rem)
                                    .fontWeight(FontWeight.Medium)
                                    .color("#555")
                            )
                        },
                        isRequired = true,
                        fieldContent = {
                            TextField(
                                value = nameState.value,
                                onValueChange = { nameState.value = it },
                                placeholder = "Enter user name",
                                modifier = Modifier()
                                    .fillMaxWidth()
                                    .padding(0.75.rem)
                                    .borderRadius(4.px)
                                    .border("1px", "solid", "#ddd")
                                    .backgroundColor("white")
                                    .attribute("name", "name")
                                    .attribute("value", user.name) // Ensure value is set for SSR
                                    .attribute("required", "true")
                            )
                        }
                    )
                    
                    // Email field using FormField
                    FormField(
                        label = {
                            Text(
                                "Email *",
                                modifier = Modifier()
                                    .fontSize(0.875.rem)
                                    .fontWeight(FontWeight.Medium)
                                    .color("#555")
                            )
                        },
                        isRequired = true,
                        fieldContent = {
                            TextField(
                                value = emailState.value,
                                onValueChange = { emailState.value = it },
                                placeholder = "Enter email address",
                                type = TextFieldType.Email,
                                modifier = Modifier()
                                    .fillMaxWidth()
                                    .padding(0.75.rem)
                                    .borderRadius(4.px)
                                    .border("1px", "solid", "#ddd")
                                    .backgroundColor("white")
                                    .attribute("name", "email")
                                    .attribute("value", user.email) // Ensure value is set for SSR
                                    .attribute("required", "true")
                            )
                        }
                    )
                    
                    // Role field using FormField and Select
                    FormField(
                        label = {
                            Text(
                                "Role *",
                                modifier = Modifier()
                                    .fontSize(0.875.rem)
                                    .fontWeight(FontWeight.Medium)
                                    .color("#555")
                            )
                        },
                        isRequired = true,
                        fieldContent = {
                            Select(
                                selectedValue = roleState,
                                options = listOf(
                                    SelectOption("admin", "Admin"),
                                    SelectOption("user", "User"),
                                    SelectOption("editor", "Editor"),
                                    SelectOption("moderator", "Moderator")
                                ),
                                onSelectedChange = { newRole ->
                                    if (newRole != null) {
                                        roleState.value = newRole
                                    }
                                },
                                placeholder = "Select a role",
                                modifier = Modifier()
                                    .fillMaxWidth()
                                    .padding(0.75.rem)
                                    .borderRadius(4.px)
                                    .border("1px", "solid", "#ddd")
                                    .backgroundColor("white")
                                    .attribute("name", "role")
                                    .attribute("required", "true")
                            )
                        }
                    )
                    
                    // Buttons
                    Row(
                        modifier = Modifier()
                            .fillMaxWidth()
                            .marginTop(1.rem)
                            .gap(1.rem)
                    ) {
                        Button(
                            onClick = {
                                // Form submission will be handled by the Form component
                            },
                            label = "Update User",
                            modifier = Modifier()
                                .padding("0.75rem 2rem")
                                .backgroundColor("#1976d2")
                                .color("white")
                                .borderRadius(4.px)
                                .cursor("pointer")
                                .attribute("type", "submit")
                                .hover { backgroundColor("#1565c0") }
                        )
                        
                        Button(
                            onClick = {
                                // Navigate back to users list
                            },
                            label = "Cancel",
                            modifier = Modifier()
                                .padding("0.75rem 2rem")
                                .backgroundColor("#e0e0e0")
                                .color("#666")
                                .borderRadius(4.px)
                                .cursor("pointer")
                                .attribute("type", "button")
                                .attribute("onclick", "window.location.href='/users'")
                        )
                    }
                }
            }
        }
    }
}
