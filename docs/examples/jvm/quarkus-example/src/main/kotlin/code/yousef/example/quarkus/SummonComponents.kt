package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.Checkbox
import code.yousef.summon.components.input.RangeSlider
import code.yousef.summon.components.input.TextArea
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.AttributeModifiers.attribute
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Root component for the application structure.
 * Note: Navigation is provided by the template, so we don't include NavigationComponent here.
 */
@Composable
fun AppRoot(content: @Composable () -> Unit) {
    Box(modifier = Modifier().style("class", "container")) {
        Column {
            // NavigationComponent removed to avoid duplication with the template's navbar
            content()
            FooterComponent()
        }
    }
}

/**
 * Navigation component that provides app-wide navigation.
 */
@Composable
fun NavigationComponent() {
    Box(modifier = Modifier().style("class", "nav")) {
        Row(modifier = Modifier().style("class", "nav-list")) {
            NavItem(href = "/", text = "Home")
            NavItem(href = "/users", text = "Users")
            NavItem(href = "/dashboard", text = "Dashboard")
            NavItem(href = "/contact", text = "Contact")
            NavItem(href = "/theme", text = "Theme")
            NavItem(href = "/chat", text = "Chat")
        }
    }
}

/**
 * Individual navigation item.
 */
@Composable
fun NavItem(href: String, text: String) {
    Box(modifier = Modifier().style("class", "nav-item")) {
        Box(modifier = Modifier().style("class", "nav-link").style("href", href)) {
            Text(text)
        }
    }
}

/**
 * Footer component with copyright and company information.
 */
@Composable
fun FooterComponent() {
    Box(
        modifier = Modifier()
            .style("class", "footer")
            .style(
                "style",
                "margin-top: 3rem; padding-top: 1.5rem; border-top: 1px solid #ddd; text-align: center;"
            )
    ) {
        Column(
            modifier = Modifier().style("style", "align-items: center;")
        ) {
            Text(text = "© ${LocalDateTime.now().year} Summon Quarkus Example. All rights reserved.")
            Row(
                modifier = Modifier().style("style", "align-items: center; display: flex; justify-content: center; gap: 5px;")
            ) {
                Text(text = "Built with ")
                Box(
                    modifier = Modifier().style("href", "https://quarkus.io/").style("style", "display: inline;")
                ) { Text(text = "Quarkus") }
                Text(text = " and ")
                Box(
                    modifier = Modifier().style("href", "https://github.com/yebaital/summon").style("style", "display: inline;")
                ) { Text(text = "Summon") }
            }
        }
    }
}

/**
 * Hero section component with a welcome message and call-to-action button.
 * 
 * This version uses a direct HTML approach to ensure the HTMX attributes are correctly rendered.
 */
@Composable
fun HeroComponent(username: String) {
    println("HeroComponent rendering with username: $username")

    // Create a container with HTMX attributes to load the hero component
    // Use style() method directly for all attributes to ensure they're rendered correctly in HTML
    Box(
        modifier = Modifier()
            .style("id", "hero-container")
            .style("hx-get", "/api/hero-component?username=$username")
            .style("hx-trigger", "load")
            .style("min-height", "300px")
    ) {
        // Initial loading state or fallback content
        Text("Loading hero component...")
    }

    println("HeroComponent rendering completed")
}

/**
 * Feature cards component that displays key features of the application.
 */
@Composable
fun FeatureCardsComponent() {
    Column {
        Text(
            text = "Key Features",
            modifier = Modifier().style(
                "style",
                "text-align: center; margin: 2rem 0; font-size: 1.8rem; font-weight: bold;"
            )
        )
        Row(modifier = Modifier().style("class", "row").style("style", "display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem;")) {
            FeatureCard(
                title = "Reactive UI",
                description = "Build reactive user interfaces with state management.",
                icon = "🔄"
            )
            FeatureCard(
                title = "Component-Based",
                description = "Create reusable UI components with a Compose-like syntax.",
                icon = "🧩"
            )
            FeatureCard(
                title = "Server Integration",
                description = "Seamless integration with Quarkus server features.",
                icon = "🖥️"
            )
        }
    }
}

/**
 * Individual feature card component.
 * Placeholder - Needs definition based on Summon's capabilities or custom implementation.
 */
@Composable
fun FeatureCard(title: String, description: String, icon: String) {
    Box(modifier = Modifier().style("class", "col")) {
        Card(modifier = Modifier().style("style", "text-align: center; padding: 1.5rem;")) {
            Column {
                Text(
                    text = icon,
                    modifier = Modifier().style("style", "font-size: 3rem; margin-bottom: 1rem;")
                )
                Text(
                    text = title,
                    modifier = Modifier().style(
                        "style",
                        "font-size: 1.5rem; font-weight: bold; margin-bottom: 0.5rem;"
                    )
                )
                Text(text = description)
            }
        }
    }
}

/**
 * Interactive counter component demonstrating state management.
 * 
 * This version uses a direct HTML approach to ensure the HTMX attributes are correctly rendered.
 */
@Composable
fun CounterComponent(initialValue: Int = 0) {
    println("CounterComponent rendering with initialValue: $initialValue")

    // Create a container with HTMX attributes to load the counter component
    // Use style() method directly for all attributes to ensure they're rendered correctly in HTML
    Box(
        modifier = Modifier()
            .style("id", "counter-container")
            .style("hx-get", "/api/counter-component")
            .style("hx-trigger", "load")
            .style("min-height", "200px")
    ) {
        // Initial loading state or fallback content
        Text("Loading counter component...")
    }

    println("CounterComponent rendering completed")
}

/**
 * Dashboard component showing various metrics and statistics.
 * Placeholder - Needs definition based on Summon's capabilities or custom implementation.
 */
@Composable
fun DashboardComponent() {
    Column {
        Text(
            text = "Dashboard",
            modifier = Modifier().style("style", "font-size: 2rem; font-weight: bold; margin-bottom: 1.5rem;")
        )
        Row(
            modifier = Modifier().style("class", "row").style("style", "display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 2rem;")
        ) {
            StatCard(title = "Users", value = "1,234", iconText = "👥", color = "#4695EB")
            StatCard(title = "Revenue", value = "$8,345", iconText = "💰", color = "#4CAF50")
            StatCard(title = "Products", value = "567", iconText = "📦", color = "#FF9800")
            StatCard(title = "Orders", value = "890", iconText = "��", color = "#F44336")
        }

        Card(modifier = Modifier().style("style", "margin-top: 2rem;")) {
            Column(modifier = Modifier().style("style", "padding: 1.5rem;")) {
                Text(
                    text = "Recent Activity",
                    modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem; color: var(--primary-color);")
                )
                Box(modifier = Modifier().style("style", "border-left: 2px solid var(--border-color); margin-left: 0.5rem; padding-left: 1.5rem;")) {
                    Column {
                        ActivityItem(
                            user = "John Doe",
                            action = "Completed order #12345",
                            time = "2 hours ago",
                            icon = "✅"
                        )
                        ActivityItem(
                            user = "Jane Smith",
                            action = "Updated product inventory",
                            time = "4 hours ago",
                            icon = "🔄"
                        )
                        ActivityItem(
                            user = "Mike Johnson",
                            action = "Added new product",
                            time = "Yesterday",
                            icon = "➕"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Statistics card for the dashboard.
 * Placeholder - Needs definition based on Summon's capabilities or custom implementation.
 */
@Composable
fun StatCard(title: String, value: String, iconText: String, color: String) {
    Card(modifier = Modifier().style("style", "border-left: 4px solid $color; display: flex; align-items: center; padding: 1.5rem;")) {
        Row(modifier = Modifier().style("style", "align-items: center; width: 100%;")) {
            Box(
                modifier = Modifier().style("style", "font-size: 2rem; margin-right: 1rem; color: $color;")
            ) { Text(text = iconText) }
            Column {
                Text(
                    text = title,
                    modifier = Modifier().style("style", "color: var(--text-color); font-size: 1rem; margin: 0;")
                )
                Text(
                    text = value,
                    modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold; color: $color; margin: 0.25rem 0 0 0;")
                )
            }
        }
    }
}

/**
 * Activity item showing a single activity entry.
 * Placeholder - Needs definition based on Summon's capabilities or custom implementation.
 */
@Composable
fun ActivityItem(user: String, action: String, time: String, icon: String) {
    Box(modifier = Modifier().style("style", "display: flex; margin-bottom: 1.5rem; position: relative;")) {
        Box(
            modifier = Modifier().style(
                "style",
                "width: 2rem; height: 2rem; background-color: var(--panel-color); border-radius: 50%; display: flex; justify-content: center; align-items: center; margin-right: 1rem; position: absolute; left: -2.6rem; top: 0; border: 2px solid var(--border-color);"
            )
        ) { Text(text = icon) }

        Column(modifier = Modifier().style("style", "margin-left: 1rem;")) {
            Text(
                text = user,
                modifier = Modifier().style("style", "margin: 0; font-weight: bold; color: var(--primary-color);")
            )
            Text(
                text = action,
                modifier = Modifier().style("style", "margin: 0.25rem 0; color: var(--text-color);")
            )
            Text(
                text = time,
                modifier = Modifier().style("style", "margin: 0; font-size: 0.8rem; color: var(--text-color); opacity: 0.7;")
            )
        }
    }
}

/**
 * Contact form component for sending messages.
 */
@Composable
fun ContactFormComponent() {
    Card(modifier = Modifier().style("style", "max-width: 600px; margin: 2rem auto; padding: 2rem;")) {
        Column {
            Text(
                text = "Contact Us",
                modifier = Modifier().style("style", "font-size: 1.8rem; font-weight: bold; margin-bottom: 1rem; text-align: center;")
            )
            HtmxForm(
                action = "/api/contact",
                target = "closest .card",
                swap = "outerHTML"
            ) {
                Column(modifier = Modifier().style("style", "gap: 1rem;")) {
                    FormGroup("Name") {
                        TextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier().style("name", "name").style("required", "true").style("placeholder", "Your Name")
                        )
                    }
                    FormGroup("Email") {
                        TextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier().style("name", "email").style("type", "email").style("required", "true").style("placeholder", "Your Email")
                        )
                    }
                    FormGroup("Subject") {
                        TextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier().style("name", "subject").style("required", "true").style("placeholder", "Message Subject")
                        )
                    }
                    FormGroup("Message") {
                        TextArea(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier()
                                .style("name", "message")
                                .style("required", "true")
                                .style("placeholder", "Your Message")
                                .style("style", "min-height: 120px;")
                        )
                    }
                    Button(
                        label = "Send Message",
                        onClick = {},
                        modifier = Modifier().style("class", "btn").style("style", "width: 100%; margin-top: 1rem;")
                    )
                }
            }
        }
    }
}

/**
 * Form group helper component.
 * Placeholder - Needs definition based on Summon's capabilities or custom implementation.
 */
@Composable
fun FormGroup(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier().style("style", "margin-bottom: 1rem;")) {
        Text(
            text = label,
            modifier = Modifier().style("style", "font-weight: 600; display: block; margin-bottom: 0.5rem;")
        )
        content()
    }
}

/**
 * HTMX Form wrapper component.
 * Placeholder - Needs definition based on Summon's capabilities or custom implementation.
 */
@Composable
fun HtmxForm(
    action: String,
    target: String,
    swap: String = "innerHTML",
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .style("hx-post", action)
            .style("hx-target", target)
            .style("hx-swap", swap)
            .style("element", "form")
            .style("action", action)
            .style("method", "post")
    ) {
        content()
    }
}

/**
 * Displays the current time, updating via HTMX.
 */
@Composable
fun CurrentTimeComponent() {
    Card(modifier = Modifier().style("style", "text-align: center; padding: 2rem; background-color: var(--panel-color); margin-bottom: 2rem;")) {
        Column(modifier = Modifier().style("style", "align-items: center;")) {
            Text(
                text = "Current Server Time (HTMX)",
                modifier = Modifier().style("style", "margin-bottom: 1rem; color: var(--primary-color); font-size: 1.2rem;")
            )
            Text(
                text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                modifier = Modifier()
                    .style("id", "current-time-htmx")
                    .style("style", "font-size: 2.5rem; font-weight: bold; margin: 0.5rem 0;")
                    .style("hx-get", "/api/time")
                    .style("hx-trigger", "every 1s")
                    .style("hx-swap", "innerHTML")
            )
            Text(
                text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                modifier = Modifier().style("style", "font-size: 1rem; color: var(--text-color);")
            )
        }
    }
}

@Composable
fun StatefulExampleComponent() {
    val textValue = remember { mutableStateOf("") }

    Column {
        Text("Enter text:")
        TextField(
            value = textValue.value,
            onValueChange = { newValue -> textValue.value = newValue },
            modifier = Modifier().style("name", "statefulText")
        )
        Text("You entered: ${textValue.value}")
    }
}

@Composable
fun VariousInputsFormComponent() {
    val textValue = remember { mutableStateOf("") }
    val sliderValue = remember { mutableStateOf(0f..100f) }
    val checkboxChecked = remember { mutableStateOf(false) }

    Card(modifier = Modifier().style("style","padding: 1.5rem;")) {
        HtmxForm(action = "/api/form-test", target = "#form-test-output") {
            Column(modifier = Modifier().style("style", "gap: 1rem;")) {
                Text(
                    text = "Various Input Types Test",
                    modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem;")
                )

                FormGroup("Text Field") {
                    TextField(
                        value = textValue.value,
                        onValueChange = { textValue.value = it },
                        modifier = Modifier().style("name", "textField").style("placeholder", "Enter text")
                    )
                }

                FormGroup("Range Slider (0-100)") {
                    RangeSlider(
                        value = sliderValue.value,
                        onValueChange = { sliderValue.value = it },
                        valueRange = 0f..100f,
                        modifier = Modifier().style("name", "slider")
                    )
                    Text(text = "Value: ${sliderValue.value.start.toInt()} - ${sliderValue.value.endInclusive.toInt()}")
                }

                FormGroup("Checkbox") {
                    Checkbox(
                        checked = checkboxChecked.value,
                        onCheckedChange = { checkboxChecked.value = it },
                        label = "Agree to terms?",
                        modifier = Modifier().style("name", "checkbox")
                    )
                }

                Button(
                    label = "Submit Inputs",
                    onClick = {},
                    modifier = Modifier().style("class", "btn").style("type", "submit")
                )

                Box(modifier = Modifier().style("id", "form-test-output").style("style", "margin-top: 1rem; padding: 1rem; border: 1px dashed #ccc;")) {
                    Text("Form output will appear here.")
                }
            }
        }
    }
}

/**
 * A Summon-based UserForm component to replace the kotlinx.html-based implementation.
 * 
 * @param user The user to edit, or null if creating a new user
 * @param action The form action URL
 * @param method The HTTP method to use (post, put, etc.)
 */
@Composable
fun UserFormComponent(
    user: User? = null,
    action: String,
    method: String = "post"
) {
    val isUpdate = user != null
    val formId = if (isUpdate) "edit-user-form-${user?.id}" else "add-user-form"

    Box(
        modifier = Modifier()
            .style("id", formId)
            .style("class", "user-form card")
            .style("style", "padding: 1.5rem; margin-bottom: 1.5rem;")
            .style("hx-${method.lowercase()}", action)
            .style("hx-target", "#users-table-wrapper")
            .style("hx-swap", "outerHTML")
            .style("element", "form")
            .style("action", action)
            .style("method", "post")
    ) {
        Column {
            Text(
                text = if (isUpdate) "Edit User" else "Add New User",
                modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem;")
            )

            // Add hidden method field for PUT/DELETE requests
            if (method.equals("put", ignoreCase = true) || method.equals("delete", ignoreCase = true)) {
                Box(
                    modifier = Modifier()
                        .style("type", "hidden")
                        .style("name", "_method")
                        .style("value", method.uppercase())
                        .style("element", "input")
                ) {}
            }

            // Name field
            FormGroup("Name") {
                TextField(
                    value = user?.name ?: "",
                    onValueChange = {},  // No-op since this is server-rendered
                    modifier = Modifier()
                        .style("name", "name")
                        .style("required", "true")
                )
            }

            // Email field
            FormGroup("Email") {
                TextField(
                    value = user?.email ?: "",
                    onValueChange = {},  // No-op since this is server-rendered
                    modifier = Modifier()
                        .style("name", "email")
                        .style("type", "email")
                        .style("required", "true")
                )
            }

            // Role field
            FormGroup("Role") {
                TextField(
                    value = user?.role ?: "User",
                    onValueChange = {},  // No-op since this is server-rendered
                    modifier = Modifier()
                        .style("name", "role")
                        .style("required", "true")
                )
            }

            // Form buttons
            Row(modifier = Modifier().style("style", "gap: 0.5rem; margin-top: 1rem;")) {
                Button(
                    label = if (isUpdate) "Update User" else "Add User",
                    onClick = {},  // No-op since this is server-rendered
                    modifier = Modifier()
                        .style("class", "btn btn-primary")
                        .style("type", "submit")
                )

                Button(
                    label = "Cancel",
                    onClick = {},  // No-op since this is server-rendered
                    modifier = Modifier()
                        .style("class", "btn btn-secondary")
                        .style("hx-get", "/api/users/cancel-form")
                        .style("hx-target", "#user-form-container")
                        .style("hx-swap", "innerHTML")
                )
            }
        }
    }
}
