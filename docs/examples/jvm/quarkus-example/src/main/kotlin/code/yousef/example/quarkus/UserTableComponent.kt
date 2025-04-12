package code.yousef.example.quarkus

import code.yousef.summon.runtime.Composable
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row

/**
 * A component that displays a table of users.
 *
 * @param users The list of users to display
 */
@Composable
fun UserTableComponent(users: List<User>) {
    Box(modifier = Modifier().style("class", "user-management")) {
        // Header and Add User Button
        Box(modifier = Modifier().style("style", "display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem;")) {
            Box(modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold;")) { Text("User Management") }
            Button(
                label = "Add New User",
                onClick = {},
                modifier = Modifier()
                    .style("class", "btn")
                    .style("hx-get", "/api/users/form")
                    .style("hx-target", "#user-form-container")
                    .style("hx-swap", "innerHTML")
            )
        }
        
        // User form container - will be populated by HTMX
        Box(modifier = Modifier().style("id", "user-form-container")) {}
        
        // Users table
        Box(modifier = Modifier().style("class", "table").style("id", "users-table").style("style", "width: 100%; border-collapse: collapse;")) {
            // Table headers
            Box(modifier = Modifier().style("style", "display: table-header-group;")) {
                Box(modifier = Modifier().style("style", "display: table-row;")) {
                    TableHeader { Text("ID") }
                    TableHeader { Text("Name") }
                    TableHeader { Text("Email") }
                    TableHeader { Text("Role") }
                    TableHeader { Text("Actions") }
                }
            }
            
            // Table body
            Box(modifier = Modifier().style("style", "display: table-row-group;")) {
                if (users.isEmpty()) {
                    Box(modifier = Modifier().style("style", "display: table-row;")) {
                        Box(modifier = Modifier().style("colspan", "5").style("style", "text-align: center; padding: 12px; display: table-cell;")) {
                            Text("No users found")
                        }
                    }
                } else {
                    users.forEach { user ->
                        Box(modifier = Modifier().style("style", "display: table-row;")) {
                            Box(modifier = Modifier().style("style", "padding: 12px; display: table-cell;")) { Text("${user.id}") }
                            Box(modifier = Modifier().style("style", "padding: 12px; display: table-cell;")) { Text(user.name) }
                            Box(modifier = Modifier().style("style", "padding: 12px; display: table-cell;")) { Text(user.email) }
                            Box(modifier = Modifier().style("style", "padding: 12px; display: table-cell;")) { Text(user.role) }
                            Box(modifier = Modifier().style("style", "padding: 12px; display: table-cell;")) {
                                Button(
                                    label = "Edit",
                                    onClick = {},
                                    modifier = Modifier()
                                        .style("class", "btn")
                                        .style("style", "padding: 4px 8px; margin-right: 8px; background-color: var(--info-color);")
                                        .style("hx-get", "/api/users/${user.id}/edit")
                                        .style("hx-target", "#user-form-container")
                                        .style("hx-swap", "innerHTML")
                                )
                                
                                Button(
                                    label = "Delete",
                                    onClick = {},
                                    modifier = Modifier()
                                        .style("class", "btn")
                                        .style("style", "padding: 4px 8px; background-color: var(--error-color);")
                                        .style("hx-delete", "/api/users/${user.id}")
                                        .style("hx-target", "#users-table")
                                        .style("hx-swap", "outerHTML")
                                        .style("hx-confirm", "Are you sure you want to delete this user?")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeader(content: @Composable () -> Unit) {
    Box(modifier = Modifier().style("style", "font-weight: bold; padding: 12px; text-align: left; background-color: #f5f5f5; border: 1px solid #ddd; display: table-cell;"), content = content)
} 