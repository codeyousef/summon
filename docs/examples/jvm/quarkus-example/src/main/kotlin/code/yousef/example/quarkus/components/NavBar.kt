package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.User
import code.yousef.example.quarkus.utils.boxShadow
import code.yousef.example.quarkus.utils.hover
import code.yousef.example.quarkus.utils.marginVH
import code.yousef.example.quarkus.utils.paddingVH
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable
import kotlinx.html.script
import kotlinx.html.unsafe

@Composable
fun NavBar(currentUser: User? = null) {
    Box(
        modifier = Modifier().fillMaxWidth()
            .backgroundColor("#343a40")
            .paddingVH(vertical = "12px", horizontal = "24px")
            .boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
    ) {
        Row(
            modifier = Modifier().fillMaxWidth()
                .maxWidth("1200px")
                .marginVH(vertical = "0", horizontal = "auto")
                .justifyContent("space-between")
                .alignItems("center")
        ) {
            // Logo/Brand
            Box(
                modifier = Modifier().cursor("pointer")
                    .attribute("hx-get", "/")
                    .attribute("hx-target", "body")
                    .attribute("hx-swap", "innerHTML")
            ) {
                Text(
                    text = "Quarkus + Summon",
                    modifier = Modifier().fontSize("20px")
                        .fontWeight("bold")
                        .color("#fff")
                )
            }

            // Navigation Links
            Row(
                modifier = Modifier().gap("24px")
                    .alignItems("center")
            ) {
                // Home link
                NavLink(text = "Home", path = "/", isActive = true)

                // Users link (only for admins)
                if (currentUser?.role == "ADMIN") {
                    NavLink(text = "Users", path = "/users")
                }

                // Contact link
                NavLink(text = "Contact", path = "/contact")

                // Auth links
                if (currentUser == null) {
                    // Login link
                    Box(
                        modifier = Modifier().cursor("pointer")
                            .paddingVH(vertical = "8px", horizontal = "16px")
                            .backgroundColor("#007bff")
                            .borderRadius("4px")
                            .attribute("hx-get", "/login")
                            .attribute("hx-target", "body")
                            .attribute("hx-swap", "innerHTML")
                    ) {
                        Text(
                            text = "Log In",
                            modifier = Modifier().color("#fff")
                                .fontWeight("medium")
                        )
                    }
                } else {
                    // User dropdown
                    Box(
                        modifier = Modifier().position("relative")
                            .attribute("id", "user-dropdown")
                    ) {
                        // Dropdown toggle
                        Row(
                            modifier = Modifier().gap("8px")
                                .alignItems("center")
                                .cursor("pointer")
                                .paddingVH(vertical = "4px", horizontal = "8px")
                                .attribute("onclick", "toggleDropdown()")
                        ) {
                            // User avatar
                            Box(
                                modifier = Modifier().width("32px")
                                    .height("32px")
                                    .borderRadius("50%")
                                    .backgroundColor("#6c757d")
                                    .display("flex")
                                    .justifyContent("center")
                                    .alignItems("center")
                            ) {
                                Text(
                                    text = currentUser.name.substring(0, 1).uppercase(),
                                    modifier = Modifier().color("#fff")
                                        .fontWeight("bold")
                                )
                            }

                            // Username
                            Text(
                                text = currentUser.name,
                                modifier = Modifier().color("#fff")
                            )
                        }

                        // Dropdown menu
                        Box(
                            modifier = Modifier().position("absolute")
                                .top("40px")
                                .right("0")
                                .width("200px")
                                .backgroundColor("#fff")
                                .borderRadius("4px")
                                .boxShadow("0 2px 10px rgba(0,0,0,0.1)")
                                .display("none")
                                .zIndex(100)
                                .attribute("id", "dropdown-menu")
                        ) {
                            // Profile link
                            DropdownItem(
                                text = "Profile",
                                path = "/profile/${currentUser.id}",
                                icon = "👤"
                            )

                            // Settings link
                            DropdownItem(
                                text = "Settings",
                                path = "/settings",
                                icon = "⚙️"
                            )

                            // Divider
                            Box(
                                modifier = Modifier().height("1px")
                                    .backgroundColor("#e9ecef")
                                    .marginVH(vertical = "8px", horizontal = "0")
                            ) {}

                            // Logout link
                            DropdownItem(
                                text = "Logout",
                                path = "/logout",
                                icon = "🚪",
                                isDanger = true
                            )
                        }

                        // Define the dropdown JavaScript as a constant string
                        val dropdownScript = """
                            function toggleDropdown() {
                                const menu = document.getElementById('dropdown-menu');
                                if (menu) {
                                    menu.style.display = menu.style.display === 'none' || menu.style.display === '' ? 'block' : 'none';
                                }
                            }
                            
                            document.addEventListener('click', function(event) {
                                const dropdown = document.getElementById('user-dropdown');
                                const menu = document.getElementById('dropdown-menu');
                                // Check if dropdown and menu exist, and if the click was outside the dropdown trigger area
                                if (dropdown && menu && !dropdown.contains(event.target)) {
                                    menu.style.display = 'none';
                                }
                            });
                            """.trimIndent()

                        // Use Box with modifiers to render the script tag
                        Box(modifier = Modifier()
                            .style("element", "script") // Tell renderer to create a <script> tag
                            .style("content", dropdownScript) // Inject the raw JS content
                        ) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun NavLink(text: String, path: String, isActive: Boolean = false) {
    Box(
        modifier = Modifier().cursor("pointer")
            .attribute("hx-get", path)
            .attribute("hx-target", "body")
            .attribute("hx-swap", "innerHTML")
    ) {
        Text(
            text = text,
            modifier = Modifier().color(if (isActive) "#fff" else "#ced4da")
                .fontWeight(if (isActive) "medium" else "normal")
                .hover(
                    color = "#fff"
                )
        )
    }
}

@Composable
private fun DropdownItem(text: String, path: String, icon: String, isDanger: Boolean = false) {
    Box(
        modifier = Modifier().padding("12px")
            .cursor("pointer")
            .hover(
                backgroundColor = if (isDanger) "#ffebee" else "#f8f9fa"
            )
            .attribute("hx-get", path)
            .attribute("hx-target", "body")
            .attribute("hx-swap", "innerHTML")
    ) {
        Row(
            modifier = Modifier().gap("8px")
                .alignItems("center")
        ) {
            Text(
                text = icon,
                modifier = Modifier().fontSize("16px")
            )

            Text(
                text = text,
                modifier = Modifier().fontSize("14px")
                    .color(if (isDanger) "#dc3545" else "#212529")
            )
        }
    }
} 