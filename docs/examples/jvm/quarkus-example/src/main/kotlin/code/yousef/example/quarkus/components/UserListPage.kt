package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.AppRoot
import code.yousef.example.quarkus.User
import code.yousef.example.quarkus.currentConsumer
import code.yousef.example.quarkus.utils.paddingVH
import code.yousef.example.quarkus.utils.backgroundColor
import code.yousef.example.quarkus.utils.padding
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.width
import code.yousef.summon.modifier.style
import code.yousef.summon.modifier.attribute
import code.yousef.summon.runtime.Composable
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.script
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

/**
 * User list page component that displays all users with actions.
 * 
 * @param users The list of users to display
 */
@Composable
fun UserListPage(users: List<User>) {
    AppRoot {
        Box(
            modifier = Modifier()
                .style("padding", "2rem")
                .style("max-width", "1200px")
                .style("margin", "0 auto")
        ) {
            Column(
                modifier = Modifier()
                    .width("100%")
                    .style("gap", "2rem")
            ) {
                // Page header and actions
                Row(
                    modifier = Modifier()
                        .width("100%")
                        .style("justify-content", "space-between")
                        .style("align-items", "center")
                        .style("margin-bottom", "1.5rem")
                ) {
                    val consumer = currentConsumer()
                    consumer.h1 {
                        attributes["style"] = "font-size: 2rem; font-weight: bold; margin: 0;"
                        +"User Management"
                    }
                    
                    // Add user button
                    Button(
                        onClick = { /* No action needed */ },
                        label = "Add New User",
                        modifier = Modifier()
                            .attribute("class", "btn btn-primary")
                            .paddingVH(vertical = "0.75rem", horizontal = "1.5rem")
                            .backgroundColor("#0d6efd")
                            .style("color", "white")
                            .style("border", "none")
                            .style("border-radius", "0.375rem")
                            .style("font-weight", "500")
                            .style("cursor", "pointer")
                            .attribute("hx-get", "/register")
                            .attribute("hx-target", "#page-content")
                            .attribute("hx-swap", "innerHTML")
                    )
                }
                
                // Search box
                Row(
                    modifier = Modifier()
                        .width("100%")
                        .style("margin-bottom", "1.5rem")
                ) {
                    val consumer = currentConsumer()
                    consumer.div {
                        attributes["class"] = "search-container"
                        attributes["style"] = "width: 100%; max-width: 500px; position: relative;"
                        
                        TextField(
                            value = "",
                            onValueChange = { /* Handled by search script */ },
                            modifier = Modifier()
                                .attribute("class", "form-control")
                                .width("100%")
                                .style("padding", "0.75rem")
                                .style("border", "1px solid #ced4da")
                                .style("border-radius", "0.375rem")
                                .attribute("id", "user-search")
                                .attribute("name", "search")
                                .attribute("placeholder", "Search users by name or email...")
                        )
                    }
                }
                
                // Users table
                Box(
                    modifier = Modifier()
                        .attribute("class", "table-responsive")
                        .width("100%")
                        .style("overflow-x", "auto")
                ) {
                    val consumer = currentConsumer()
                    consumer.table {
                        attributes["class"] = "table table-hover"
                        attributes["style"] = "width: 100%; border-collapse: collapse;"
                        attributes["id"] = "users-table"
                        
                        // Table header
                        thead {
                            tr {
                                th { 
                                    attributes["style"] = "padding: 0.75rem; border-bottom: 2px solid #dee2e6; text-align: left; font-weight: 600;"
                                    +"ID" 
                                }
                                th { 
                                    attributes["style"] = "padding: 0.75rem; border-bottom: 2px solid #dee2e6; text-align: left; font-weight: 600;"
                                    +"Name" 
                                }
                                th { 
                                    attributes["style"] = "padding: 0.75rem; border-bottom: 2px solid #dee2e6; text-align: left; font-weight: 600;"
                                    +"Email" 
                                }
                                th { 
                                    attributes["style"] = "padding: 0.75rem; border-bottom: 2px solid #dee2e6; text-align: left; font-weight: 600;"
                                    +"Role" 
                                }
                                th { 
                                    attributes["style"] = "padding: 0.75rem; border-bottom: 2px solid #dee2e6; text-align: left; font-weight: 600;"
                                    +"Status" 
                                }
                                th { 
                                    attributes["style"] = "padding: 0.75rem; border-bottom: 2px solid #dee2e6; text-align: center; font-weight: 600;"
                                    +"Actions" 
                                }
                            }
                        }
                        
                        // Table body
                        tbody {
                            users.forEach { user ->
                                tr {
                                    attributes["style"] = "transition: background-color 0.2s;"
                                    attributes["class"] = "user-row"
                                    attributes["data-name"] = user.name.lowercase()
                                    attributes["data-email"] = user.email.lowercase()
                                    
                                    td { 
                                        attributes["style"] = "padding: 0.75rem; border-bottom: 1px solid #dee2e6;"
                                        +user.id 
                                    }
                                    td { 
                                        attributes["style"] = "padding: 0.75rem; border-bottom: 1px solid #dee2e6; font-weight: 500;"
                                        +user.name 
                                    }
                                    td { 
                                        attributes["style"] = "padding: 0.75rem; border-bottom: 1px solid #dee2e6;"
                                        +user.email 
                                    }
                                    td { 
                                        attributes["style"] = "padding: 0.75rem; border-bottom: 1px solid #dee2e6;"
                                        +user.role 
                                    }
                                    td { 
                                        attributes["style"] = "padding: 0.75rem; border-bottom: 1px solid #dee2e6;"
                                        
                                        Box(
                                            modifier = Modifier()
                                                .style("display", "inline-flex")
                                                .style("align-items", "center")
                                                .style("gap", "0.5rem")
                                        ) {
                                            Box(
                                                modifier = Modifier()
                                                    .style("width", "10px")
                                                    .style("height", "10px")
                                                    .style("border-radius", "50%")
                                                    .backgroundColor(if (user.active) "#10b981" else "#ef4444")
                                            ) {}
                                            
                                            Text(
                                                text = if (user.active) "Active" else "Inactive",
                                                modifier = Modifier()
                                                    .style("font-size", "0.875rem")
                                            )
                                        }
                                    }
                                    td { 
                                        attributes["style"] = "padding: 0.75rem; border-bottom: 1px solid #dee2e6; text-align: center;"
                                        
                                        Row(
                                            modifier = Modifier()
                                                .style("justify-content", "center")
                                                .style("gap", "0.5rem")
                                        ) {
                                            // View/Edit button
                                            Button(
                                                onClick = { /* No action needed */ },
                                                label = "Edit",
                                                modifier = Modifier()
                                                    .attribute("class", "btn btn-sm btn-outline-primary")
                                                    .style("padding", "0.25rem 0.5rem")
                                                    .style("font-size", "0.875rem")
                                                    .attribute("hx-get", "/users/${user.id}")
                                                    .attribute("hx-target", "#page-content")
                                                    .attribute("hx-swap", "innerHTML")
                                            )
                                            
                                            // Delete button
                                            Button(
                                                onClick = { /* No action needed */ },
                                                label = "Delete",
                                                modifier = Modifier()
                                                    .attribute("class", "btn btn-sm btn-outline-danger")
                                                    .style("padding", "0.25rem 0.5rem")
                                                    .style("font-size", "0.875rem")
                                                    .attribute("hx-delete", "/api/users/${user.id}")
                                                    .attribute("hx-target", "#page-content")
                                                    .attribute("hx-swap", "innerHTML")
                                                    .attribute("hx-confirm", "Are you sure you want to delete this user?")
                                            )
                                        }
                                    }
                                }
                            }
                            
                            // Empty state if no users
                            if (users.isEmpty()) {
                                tr {
                                    td { 
                                        attributes["colspan"] = "6"
                                        attributes["style"] = "padding: 2rem; text-align: center; color: #6c757d;"
                                        +"No users found. Click 'Add New User' to create one."
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Add search functionality script
        val consumer = currentConsumer()
        consumer.script {
            +"""
            document.addEventListener('htmx:afterSwap', function(event) {
                const searchInput = document.getElementById('user-search');
                
                if (searchInput) {
                    searchInput.addEventListener('input', function() {
                        const searchText = searchInput.value.toLowerCase();
                        const userRows = document.querySelectorAll('.user-row');
                        
                        userRows.forEach(row => {
                            const name = row.getAttribute('data-name');
                            const email = row.getAttribute('data-email');
                            
                            if (name.includes(searchText) || email.includes(searchText)) {
                                row.style.display = '';
                            } else {
                                row.style.display = 'none';
                            }
                        });
                        
                        // Check if no results are visible
                        let visibleCount = 0;
                        userRows.forEach(row => {
                            if (row.style.display !== 'none') {
                                visibleCount++;
                            }
                        });
                        
                        const tbody = document.querySelector('#users-table tbody');
                        const noResultsRow = document.getElementById('no-results-row');
                        
                        if (visibleCount === 0 && !noResultsRow) {
                            const tr = document.createElement('tr');
                            tr.id = 'no-results-row';
                            
                            const td = document.createElement('td');
                            td.setAttribute('colspan', '6');
                            td.style.textAlign = 'center';
                            td.style.padding = '2rem';
                            td.style.color = '#6c757d';
                            td.textContent = 'No users matching "' + searchText + '" found.';
                            
                            tr.appendChild(td);
                            tbody.appendChild(tr);
                        } else if (visibleCount > 0 && noResultsRow) {
                            noResultsRow.remove();
                        }
                    });
                }
            });
            """
        }
    }
} 