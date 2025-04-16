package code.yousef.example.quarkus

import code.yousef.summon.components.layout.Box
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import kotlinx.html.*

/**
 * A component that displays a list of users in a table format.
 * Provides options to view details, edit, or delete each user.
 *
 * @param users The list of users to display in the table.
 */
@Composable
fun UserTableComponent(users: List<User>) {
    Box(
        modifier = Modifier()
            .style(
                "style",
                "border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); background-color: white; padding: 16px; margin: 16px 0; width: 100%; overflow-x: auto;"
            )
    ) {
        // Use FlowContent for direct HTML rendering
        val consumer = currentConsumer()

        consumer.table {
            attributes["style"] = "width: 100%; border-collapse: collapse;"

            thead {
                tr {
                    attributes["style"] = "background-color: #f5f5f5;"

                    th {
                        attributes["style"] =
                            "padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; font-size: 14px; font-weight: bold;"
                        +"ID"
                    }
                    th {
                        attributes["style"] =
                            "padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; font-size: 14px; font-weight: bold;"
                        +"Name"
                    }
                    th {
                        attributes["style"] =
                            "padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; font-size: 14px; font-weight: bold;"
                        +"Email"
                    }
                    th {
                        attributes["style"] =
                            "padding: 12px 15px; text-align: left; border-bottom: 1px solid #ddd; font-size: 14px; font-weight: bold;"
                        +"Role"
                    }
                    th {
                        attributes["style"] =
                            "padding: 12px 15px; text-align: center; border-bottom: 1px solid #ddd; font-size: 14px; font-weight: bold;"
                        +"Status"
                    }
                    th {
                        attributes["style"] =
                            "padding: 12px 15px; text-align: center; border-bottom: 1px solid #ddd; font-size: 14px; font-weight: bold;"
                        +"Actions"
                    }
                }
            }

            tbody {
                for (user in users) {
                    tr {
                        attributes["style"] = "border-bottom: 1px solid #eee;"
                        attributes["onmouseover"] = "this.style.backgroundColor='#f9f9f9'"
                        attributes["onmouseout"] = "this.style.backgroundColor=''"

                        td {
                            attributes["style"] = "padding: 12px 15px;"
                            +user.id.toString()
                        }
                        td {
                            attributes["style"] = "padding: 12px 15px;"
                            +user.name
                        }
                        td {
                            attributes["style"] = "padding: 12px 15px;"
                            +user.email
                        }
                        td {
                            attributes["style"] = "padding: 12px 15px;"
                            span {
                                attributes["style"] =
                                    "display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 500; background-color: ${
                                        getRoleColor(user.role)
                                    }; color: white;"
                                +user.role
                            }
                        }
                        td {
                            attributes["style"] = "padding: 12px 15px; text-align: center;"
                            span {
                                attributes["style"] =
                                    "display: inline-block; padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 500; background-color: ${if (user.active) "#4CAF50" else "#F44336"}; color: white;"
                                +(if (user.active) "Active" else "Inactive")
                            }
                        }
                        td {
                            attributes["style"] = "padding: 12px 15px; text-align: center;"
                            div {
                                attributes["style"] = "display: flex; gap: 8px; justify-content: center;"

                                // View button
                                button {
                                    attributes["type"] = "button"
                                    attributes["style"] =
                                        "background-color: #4CAF50; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 12px;"
                                    attributes["onmouseover"] = "this.style.backgroundColor='#45a049'"
                                    attributes["onmouseout"] = "this.style.backgroundColor='#4CAF50'"
                                    attributes["onclick"] = "console.log('View details for user: ${user.id}')"
                                    +"View"
                                }

                                // Edit button
                                button {
                                    attributes["type"] = "button"
                                    attributes["style"] =
                                        "background-color: #2196F3; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 12px;"
                                    attributes["onmouseover"] = "this.style.backgroundColor='#0b7dda'"
                                    attributes["onmouseout"] = "this.style.backgroundColor='#2196F3'"
                                    attributes["onclick"] = "console.log('Edit user: ${user.id}')"
                                    +"Edit"
                                }

                                // Delete button
                                button {
                                    attributes["type"] = "button"
                                    attributes["style"] =
                                        "background-color: #f44336; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 12px;"
                                    attributes["onmouseover"] = "this.style.backgroundColor='#d32f2f'"
                                    attributes["onmouseout"] = "this.style.backgroundColor='#f44336'"
                                    attributes["onclick"] = "console.log('Delete user: ${user.id}')"
                                    +"Delete"
                                }

                                if (!user.active) {
                                    // Activate button
                                    button {
                                        attributes["type"] = "button"
                                        attributes["style"] =
                                            "background-color: #FF9800; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 12px;"
                                        attributes["onmouseover"] = "this.style.backgroundColor='#F57C00'"
                                        attributes["onmouseout"] = "this.style.backgroundColor='#FF9800'"
                                        attributes["onclick"] = "console.log('Activate user: ${user.id}')"
                                        +"Activate"
                                    }
                                } else {
                                    // Deactivate button
                                    button {
                                        attributes["type"] = "button"
                                        attributes["style"] =
                                            "background-color: #9E9E9E; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 12px;"
                                        attributes["onmouseover"] = "this.style.backgroundColor='#757575'"
                                        attributes["onmouseout"] = "this.style.backgroundColor='#9E9E9E'"
                                        attributes["onclick"] = "console.log('Deactivate user: ${user.id}')"
                                        +"Deactivate"
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
 * Returns a color based on the user's role for visual identification.
 *
 * @param role The user's role.
 * @return A hexadecimal color string.
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

/**
 * Gets the current consumer for HTML rendering
 */
fun currentConsumer(): FlowContent {
    val composer = currentComposer()
    return composer.getProperty("consumer") as FlowContent
}

/**
 * Helper component for rendering icons
 */
@Composable
fun Icon(name: String, modifier: Modifier = Modifier()) {
    when (name) {
        "edit" -> Box(
            modifier.style(
                "html", """
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
            </svg>
        """
            )
        ) {}

        "trash" -> Box(
            modifier.style(
                "html", """
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
            </svg>
        """
            )
        ) {}

        else -> Box(
            modifier.style(
                "html", """
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
        """
            )
        ) {}
    }
}

/**
 * Extension function to capitalize the first letter of a string
 */
private fun String.capitalize(): String {
    return if (this.isNotEmpty()) this.substring(0, 1).uppercase() + this.substring(1) else this
} 