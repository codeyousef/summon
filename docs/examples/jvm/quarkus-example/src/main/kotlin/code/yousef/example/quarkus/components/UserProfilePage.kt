package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.AppRoot
import code.yousef.example.quarkus.User
import code.yousef.example.quarkus.currentConsumer
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.script

/**
 * User profile page component that displays and allows editing of user information.
 *
 * @param user The user data to display
 */
@Composable
fun UserProfilePage(user: User) {
    AppRoot {
        Box(
            modifier = Modifier()
                .style("padding", "2rem")
                .style("max-width", "800px")
                .style("margin", "0 auto")
        ) {
            Column(
                modifier = Modifier()
                    .width("100%")
                    .style("gap", "2rem")
            ) {
                // Page header
                Text(
                    text = "User Profile",
                    modifier = Modifier()
                        .style("font-size", "2rem")
                        .style("font-weight", "bold")
                        .style("margin-bottom", "1rem")
                )

                // Profile information card
                Box(
                    modifier = Modifier()
                        .attribute("class", "card")
                        .style("padding", "1.5rem")
                        .style("border-radius", "0.5rem")
                        .style("box-shadow", "0 2px 12px rgba(0, 0, 0, 0.1)")
                ) {
                    val consumer = currentConsumer()
                    consumer.div {
                        attributes["id"] = "profile-form"
                        attributes["hx-put"] = "/api/users/${user.id}"
                        attributes["hx-trigger"] = "submit"
                        attributes["hx-target"] = "#page-content"
                        attributes["hx-swap"] = "innerHTML"

                        // User information section
                        consumer.h3 {
                            attributes["style"] = "font-size: 1.25rem; font-weight: 600; margin-bottom: 1.5rem;"
                            +"Personal Information"
                        }

                        // User information fields
                        Column(
                            modifier = Modifier()
                                .width("100%")
                                .style("gap", "1.25rem")
                        ) {
                            // User ID (read-only)
                            Column(
                                modifier = Modifier()
                                    .style("gap", "0.25rem")
                            ) {
                                Text(
                                    text = "User ID",
                                    modifier = Modifier()
                                        .style("font-weight", "500")
                                        .style("font-size", "0.875rem")
                                        .style("color", "#6c757d")
                                )

                                CustomTextField(
                                    name = "id",
                                    value = user.id,
                                    disabled = true,
                                    modifier = Modifier()
                                        .attribute("class", "form-control")
                                        .width("100%")
                                        .style("padding", "0.75rem")
                                        .style("border", "1px solid #ced4da")
                                        .style("border-radius", "0.375rem")
                                        .style("background-color", "#f8f9fa")
                                )
                            }

                            // Name field
                            Column(
                                modifier = Modifier()
                                    .style("gap", "0.25rem")
                            ) {
                                Text(
                                    text = "Full Name",
                                    modifier = Modifier()
                                        .style("font-weight", "500")
                                        .style("font-size", "0.875rem")
                                )

                                CustomTextField(
                                    name = "name",
                                    value = user.name,
                                    required = true,
                                    modifier = Modifier()
                                        .attribute("class", "form-control")
                                        .width("100%")
                                        .style("padding", "0.75rem")
                                        .style("border", "1px solid #ced4da")
                                        .style("border-radius", "0.375rem")
                                )
                            }

                            // Email field
                            Column(
                                modifier = Modifier()
                                    .style("gap", "0.25rem")
                            ) {
                                Text(
                                    text = "Email Address",
                                    modifier = Modifier()
                                        .style("font-weight", "500")
                                        .style("font-size", "0.875rem")
                                )

                                CustomTextField(
                                    name = "email",
                                    value = user.email,
                                    required = true,
                                    modifier = Modifier()
                                        .attribute("class", "form-control")
                                        .width("100%")
                                        .style("padding", "0.75rem")
                                        .style("border", "1px solid #ced4da")
                                        .style("border-radius", "0.375rem")
                                )
                            }

                            // Role field (read-only)
                            Column(
                                modifier = Modifier()
                                    .style("gap", "0.25rem")
                            ) {
                                Text(
                                    text = "Role",
                                    modifier = Modifier()
                                        .style("font-weight", "500")
                                        .style("font-size", "0.875rem")
                                )

                                CustomTextField(
                                    name = "role",
                                    value = user.role,
                                    disabled = true,
                                    modifier = Modifier()
                                        .attribute("class", "form-control")
                                        .width("100%")
                                        .style("padding", "0.75rem")
                                        .style("border", "1px solid #ced4da")
                                        .style("border-radius", "0.375rem")
                                        .style("background-color", "#f8f9fa")
                                )
                            }

                            // Status field (read-only)
                            Column(
                                modifier = Modifier()
                                    .style("gap", "0.25rem")
                            ) {
                                Text(
                                    text = "Account Status",
                                    modifier = Modifier()
                                        .style("font-weight", "500")
                                        .style("font-size", "0.875rem")
                                )

                                Row(
                                    modifier = Modifier()
                                        .style("align-items", "center")
                                        .style("gap", "0.5rem")
                                ) {
                                    Box(
                                        modifier = Modifier()
                                            .style("width", "12px")
                                            .style("height", "12px")
                                            .style("border-radius", "50%")
                                            .style("background-color", if (user.active) "#10b981" else "#ef4444")
                                    ) {}

                                    Text(
                                        text = if (user.active) "Active" else "Inactive",
                                        modifier = Modifier()
                                            .style("font-size", "0.875rem")
                                            .style("color", if (user.active) "#10b981" else "#ef4444")
                                    )
                                }
                            }

                            // Action buttons
                            Row(
                                modifier = Modifier()
                                    .style("gap", "1rem")
                                    .style("margin-top", "1rem")
                            ) {
                                // Save button
                                Button(
                                    onClick = { /* HTMX handles this through the form */ },
                                    label = "Save Changes",
                                    modifier = Modifier()
                                        .attribute("class", "btn btn-primary")
                                        .style("padding", "0.75rem 1.5rem")
                                        .style("background-color", "#0d6efd")
                                        .style("color", "white")
                                        .style("border", "none")
                                        .style("border-radius", "0.375rem")
                                        .style("font-weight", "500")
                                        .style("cursor", "pointer")
                                        .attribute("type", "submit")
                                )

                                // Back button
                                Button(
                                    onClick = { /* HTMX handles navigation */ },
                                    label = "Back to Users",
                                    modifier = Modifier()
                                        .attribute("class", "btn btn-secondary")
                                        .style("padding", "0.75rem 1.5rem")
                                        .style("background-color", "#6c757d")
                                        .style("color", "white")
                                        .style("border", "none")
                                        .style("border-radius", "0.375rem")
                                        .style("font-weight", "500")
                                        .style("cursor", "pointer")
                                        .attribute("hx-get", "/users")
                                        .attribute("hx-target", "#page-content")
                                        .attribute("hx-swap", "innerHTML")
                                )

                                // Conditionally show activate/deactivate button
                                if (user.active) {
                                    Button(
                                        onClick = { /* HTMX handles action */ },
                                        label = "Deactivate Account",
                                        modifier = Modifier()
                                            .attribute("class", "btn btn-outline-danger")
                                            .style("padding", "0.75rem 1.5rem")
                                            .style("color", "#dc3545")
                                            .style("border", "1px solid #dc3545")
                                            .style("border-radius", "0.375rem")
                                            .style("font-weight", "500")
                                            .style("cursor", "pointer")
                                            .style("margin-left", "auto")
                                            .attribute("hx-delete", "/api/users/${user.id}/deactivate")
                                            .attribute("hx-target", "#page-content")
                                            .attribute("hx-swap", "innerHTML")
                                            .attribute(
                                                "hx-confirm",
                                                "Are you sure you want to deactivate this account?"
                                            )
                                    )
                                } else {
                                    Button(
                                        onClick = { /* HTMX handles action */ },
                                        label = "Activate Account",
                                        modifier = Modifier()
                                            .attribute("class", "btn btn-outline-success")
                                            .style("padding", "0.75rem 1.5rem")
                                            .style("color", "#198754")
                                            .style("border", "1px solid #198754")
                                            .style("border-radius", "0.375rem")
                                            .style("font-weight", "500")
                                            .style("cursor", "pointer")
                                            .style("margin-left", "auto")
                                            .attribute("hx-post", "/api/users/${user.id}/activate")
                                            .attribute("hx-target", "#page-content")
                                            .attribute("hx-swap", "innerHTML")
                                    )
                                }
                            }
                        }

                        // Error message placeholder
                        div {
                            attributes["id"] = "profile-error"
                            attributes["class"] = "alert alert-danger mt-3"
                            attributes["style"] =
                                "display: none; margin-top: 1rem; padding: 0.75rem; color: #842029; background-color: #f8d7da; border: 1px solid #f5c2c7; border-radius: 0.375rem;"
                        }
                    }
                }
            }
        }

        // Add validation script
        val consumer = currentConsumer()
        consumer.script {
            +"""
            document.addEventListener('htmx:afterSwap', function(event) {
                const form = document.getElementById('profile-form');
                if (!form) return;
                
                form.addEventListener('submit', function(e) {
                    const name = form.querySelector('input[name="name"]').value;
                    const email = form.querySelector('input[name="email"]').value;
                    const errorDiv = document.getElementById('profile-error');
                    
                    // Check required fields
                    if (!name || !email) {
                        e.preventDefault();
                        errorDiv.textContent = 'Please fill in all required fields';
                        errorDiv.style.display = 'block';
                        return false;
                    }
                    
                    // Email validation
                    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                    if (!emailRegex.test(email)) {
                        e.preventDefault();
                        errorDiv.textContent = 'Please enter a valid email address';
                        errorDiv.style.display = 'block';
                        return false;
                    }
                    
                    // Hide any previous error messages
                    errorDiv.style.display = 'none';
                });
            });
            """
        }
    }
}

/**
 * Custom TextField implementation that wraps the Summon TextField
 * for use with form elements
 */
@Composable
private fun CustomTextField(
    name: String,
    value: String,
    modifier: Modifier = Modifier(),
    required: Boolean = false,
    disabled: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = { /* Form submission handles this */ },
        modifier = modifier
            .attribute("name", name)
            .apply {
                if (required) attribute("required", "true")
                if (disabled) attribute("disabled", "true")
            }
    )
}

/**
 * Overloaded CustomTextField for Long values
 */
@Composable
private fun CustomTextField(
    name: String,
    value: Long,
    modifier: Modifier = Modifier(),
    required: Boolean = false,
    disabled: Boolean = false
) {
    CustomTextField(
        name = name,
        value = value.toString(),
        modifier = modifier,
        required = required,
        disabled = disabled
    )
} 