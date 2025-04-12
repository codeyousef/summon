package code.yousef.example.quarkus

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.runtime.Composable
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.runtime.remember
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Root component that contains the main application layout.
 */
@Composable
fun AppRoot(content: @Composable () -> Unit) {
    // Use Box which renders a div, apply container class
    Box(modifier = Modifier().style("class", "container")) {
        Column {
            NavigationComponent()
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
    // Use Box, apply nav classes
    Box(modifier = Modifier().style("class", "nav")) {
        // Use Row for flex layout, apply ul classes
        Row(modifier = Modifier().style("class", "nav-list")) {
            NavItem("/", "Home")
            NavItem("/users", "Users")
            NavItem("/dashboard", "Dashboard")
            NavItem("/contact", "Contact")
            NavItem("/theme", "Theme")
            NavItem("/chat", "Chat")
            NavItem("/chat-summon", "Chat Summon")
        }
    }
}

/**
 * Individual navigation item.
 */
@Composable
fun NavItem(href: String, text: String) {
    // Use Box, apply li classes
    Box(modifier = Modifier().style("class", "nav-item")) {
        // Use Box, apply a classes and href
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
            .style("class", "footer") // Use class for potential footer styling
            .style(
                "style",
                "margin-top: 3rem; padding-top: 1.5rem; border-top: 1px solid #ddd; text-align: center;"
            )
    ) {
        // Use Column for vertical layout
        Column(modifier = Modifier().style("style", "align-items: center;")) { // Style for alignment
            Text(text = "© ${LocalDateTime.now().year} Summon Quarkus Example. All rights reserved.")
            // Use Row for horizontal layout
            Row(modifier = Modifier().style("style", "align-items: center;")) { // Style for alignment
                Text(text = "Built with ")
                // Use Box with href for link
                Box(
                    modifier = Modifier().style("href", "https://quarkus.io/").style("style", "display: inline;")
                ) { // Inline display
                    Text(text = "Quarkus")
                }
                Text(text = " and ")
                // Use Box with href for link
                Box(
                    modifier = Modifier().style("href", "https://github.com/yebaital/summon")
                        .style("style", "display: inline;")
                ) { // Inline display
                    Text(text = "Summon")
                }
            }
        }
    }
}

/**
 * Hero section component with a welcome message and call-to-action button.
 */
@Composable
fun HeroComponent(username: String) {
    Box(
        modifier = Modifier()
            .style(
                "style",
                "text-align: center; padding: 4rem 2rem; background: linear-gradient(135deg, #4695EB 0%, #2A5298 100%); color: white; border-radius: 10px; margin-bottom: 2rem;"
            )
    ) {
        Column(modifier = Modifier().style("style", "align-items: center;")) { // Style for alignment
            // Use Text and style to mimic H1
            Text(
                text = "Welcome to Summon with Quarkus",
                modifier = Modifier().style("style", "font-size: 2.5rem; margin-bottom: 1rem; font-weight: bold;")
            )
            // Use Text and style to mimic P
            Text(
                text = "Hello, $username! This example showcases how to integrate Summon with Quarkus to build amazing web applications.",
                modifier = Modifier().style(
                    "style",
                    "font-size: 1.25rem; margin-bottom: 2rem; max-width: 800px; margin-left: auto; margin-right: auto;"
                )
            )
            Button(
                label = "Get Started",
                onClick = { /* onClick JS execution needs Summon specific solution */ },
                modifier = Modifier()
                    .style("class", "btn")
                    .style(
                        "style",
                        "background: white; color: var(--primary-color); font-weight: 600; padding: 12px 24px; font-size: 1.1rem;"
                    )
                    .style("id", "get-started-btn")
            )
        }
    }
}

/**
 * Feature cards component that displays key features of the application.
 */
@Composable
fun FeatureCardsComponent() {
    Column {
        // Use Text and style to mimic H2
        Text(
            text = "Key Features",
            modifier = Modifier().style(
                "style",
                "text-align: center; margin: 2rem 0; font-size: 1.8rem; font-weight: bold;"
            )
        )
        Row(modifier = Modifier().style("class", "row")) {
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
 */
@Composable
fun FeatureCard(title: String, description: String, icon: String) {
    Box(modifier = Modifier().style("class", "col")) {
        Card(modifier = Modifier().style("style", "text-align: center;")) {
            Column {
                Text(text = icon, modifier = Modifier().style("style", "font-size: 3rem; margin-bottom: 1rem;"))
                // Use Text and style to mimic H3
                Text(
                    text = title,
                    modifier = Modifier().style(
                        "style",
                        "font-size: 1.5rem; font-weight: bold; margin-bottom: 0.5rem;"
                    )
                )
                // Use Text and style to mimic P
                Text(text = description)
            }
        }
    }
}

/**
 * Interactive counter component demonstrating state management.
 */
@Composable
fun CounterComponent() {
    Card(modifier = Modifier().style("class", "card")) {
        Column {
            // Use Text and style to mimic H3
            Text(
                text = "Interactive Counter",
                modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 0.5rem;")
            )
            Text(
                text = "This component uses HTMX for updates triggered by buttons below."
            )

            Row(
                modifier = Modifier()
                    .style("style", "display: flex; align-items: center; justify-content: center; margin: 1rem 0;")
            ) {
                Button(
                    label = "−",
                    onClick = {}, // Action handled by HTMX
                    modifier = Modifier()
                        .style("class", "btn")
                        .style("id", "decrement-btn")
                        .style("hx-post", "/api/counter/decrement")
                        .style("hx-target", "#counter-value")
                        .style("hx-swap", "innerHTML")
                        .style("style", "margin-right: 1rem;")
                )
                // Use Text, style to mimic span
                Text(
                    text = "0", // Initial value
                    modifier = Modifier()
                        .style("id", "counter-value")
                        .style("style", "font-size: 1.5rem; font-weight: 600; margin: 0 1rem;")
                )

                Button(
                    label = "+",
                    onClick = {}, // Action handled by HTMX
                    modifier = Modifier()
                        .style("class", "btn")
                        .style("id", "increment-btn")
                        .style("hx-post", "/api/counter/increment")
                        .style("hx-target", "#counter-value")
                        .style("hx-swap", "innerHTML")
                        .style("style", "margin-left: 1rem;")
                )
            }
            // Use Text, style to mimic div
            Text(
                text = "Click the buttons to change the counter via HTMX POST requests.",
                modifier = Modifier()
                    .style("id", "counter-message")
                    .style("style", "text-align: center; margin-top: 1rem; font-style: italic; color: #666;")
            )
        }
    }
}

/**
 * Dashboard component showing various metrics and statistics.
 */
@Composable
fun DashboardComponent() {
    Column {
        Text(
            text = "Dashboard",
            modifier = Modifier().style("style", "font-size: 2rem; font-weight: bold; margin-bottom: 1.5rem;")
        )

        // Statistics overview
        Row(modifier = Modifier().style("class", "row").style("style", "margin-bottom: 2rem;")) {
            StatCard(title = "Users", value = "254", iconText = "👥", color = "#3498db")
            StatCard(title = "Products", value = "1,254", iconText = "📦", color = "#2ecc71")
            StatCard(title = "Revenue", value = "$10,350", iconText = "💰", color = "#f39c12")
            StatCard(title = "Orders", value = "846", iconText = "🛒", color = "#9b59b6")
        }

        // Recent activity section
        Card(modifier = Modifier().style("style", "margin-bottom: 2rem;")) {
            Column {
                Text(
                    text = "Recent Activity",
                    modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem;")
                )

                // Activity list
                ActivityItem(
                    action = "User Login",
                    details = "John Doe logged in",
                    time = "10 minutes ago"
                )
                ActivityItem(
                    action = "New Order",
                    details = "Order #1234 placed for $129.99",
                    time = "1 hour ago"
                )
                ActivityItem(
                    action = "User Signup",
                    details = "Jane Smith created an account",
                    time = "3 hours ago"
                )
                ActivityItem(
                    action = "Product Update",
                    details = "Product 'Wireless Headphones' updated",
                    time = "Yesterday"
                )
            }
        }
    }
}

/**
 * Statistics card for the dashboard.
 */
@Composable
private fun StatCard(title: String, value: String, iconText: String, color: String) {
    Box(modifier = Modifier().style("class", "col")) {
        Card(modifier = Modifier().style("style", "border-top: 4px solid $color;")) {
            Row(
                modifier = Modifier().style(
                    "style",
                    "align-items: center; padding: 0.5rem;"
                )
            ) {
                // Icon
                Box(
                    modifier = Modifier().style(
                        "style",
                        "font-size: 2rem; margin-right: 1rem; color: $color;"
                    )
                ) {
                    Text(iconText)
                }

                // Content
                Column {
                    Text(
                        text = title,
                        modifier = Modifier().style("style", "color: #7f8c8d; font-size: 0.9rem;")
                    )
                    Text(
                        text = value,
                        modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold;")
                    )
                }
            }
        }
    }
}

/**
 * Activity item showing a single activity entry.
 */
@Composable
private fun ActivityItem(action: String, details: String, time: String) {
    Box(
        modifier = Modifier().style(
            "style",
            "padding: 0.75rem; border-bottom: 1px solid #eee; display: flex; justify-content: space-between;"
        )
    ) {
        Column(modifier = Modifier().style("style", "flex: 1;")) {
            Text(
                text = action,
                modifier = Modifier().style("style", "font-weight: bold;")
            )
            Text(
                text = details,
                modifier = Modifier().style("style", "color: #7f8c8d; font-size: 0.9rem;")
            )
        }
        Text(
            text = time,
            modifier = Modifier().style("style", "color: #95a5a6; font-size: 0.8rem;")
        )
    }
}

/**
 * Contact form component for sending messages.
 */
@Composable
fun ContactFormComponent() {
    Card {
        Column {
            Text(
                text = "Contact Us",
                modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem;")
            )
            Text(
                text = "Fill out the form below to get in touch with our team.",
                modifier = Modifier().style("style", "margin-bottom: 1.5rem;")
            )

            // Use Box to wrap the form
            Box(
                modifier = Modifier()
                    .style("style", "width: 100%;")
                    .style("hx-post", "/api/contact")
                    .style("hx-swap", "outerHTML")
            ) {
                Column {
                    // Name field
                    Column(modifier = Modifier().style("style", "margin-bottom: 1rem;")) {
                        Text(
                            text = "Name",
                            modifier = Modifier().style(
                                "style",
                                "font-weight: 600; display: block; margin-bottom: 0.5rem;"
                            )
                        )
                        TextField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Your Name",
                            modifier = Modifier()
                                .style("name", "name")
                                .style("required", "true")
                        )
                    }

                    // Email field
                    Column(modifier = Modifier().style("style", "margin-bottom: 1rem;")) {
                        Text(
                            text = "Email",
                            modifier = Modifier().style(
                                "style",
                                "font-weight: 600; display: block; margin-bottom: 0.5rem;"
                            )
                        )
                        TextField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Your Email",
                            modifier = Modifier()
                                .style("name", "email")
                                .style("type", "email")
                                .style("required", "true")
                        )
                    }

                    // Subject field
                    Column(modifier = Modifier().style("style", "margin-bottom: 1rem;")) {
                        Text(
                            text = "Subject",
                            modifier = Modifier().style(
                                "style",
                                "font-weight: 600; display: block; margin-bottom: 0.5rem;"
                            )
                        )
                        TextField(
                            value = "",
                            onValueChange = {},
                            placeholder = "Message Subject",
                            modifier = Modifier()
                                .style("name", "subject")
                                .style("required", "true")
                        )
                    }

                    // Message field
                    Column(modifier = Modifier().style("style", "margin-bottom: 1.5rem;")) {
                        Text(
                            text = "Message",
                            modifier = Modifier().style(
                                "style",
                                "font-weight: 600; display: block; margin-bottom: 0.5rem;"
                            )
                        )
                        TextArea(
                            value = "",
                            onValueChange = {},
                            placeholder = { Text("Your Message") },
                            modifier = Modifier()
                                .style("name", "message")
                                .style("required", "true")
                                .style("style", "min-height: 120px;")
                        )
                    }

                    // Submit button
                    Button(
                        label = "Send Message",
                        onClick = {},
                        modifier = Modifier().style("class", "btn")
                    )
                }
            }
        }
    }
}

/**
 * Helper component for displaying an H2 heading.
 */
@Composable
fun H2(content: @Composable () -> Unit) {
    Box(modifier = Modifier().style("style", "font-size: 2rem; font-weight: bold; margin-bottom: 1.5rem;")) {
        content()
    }
}

/**
 * Displays the current time, updating every second.
 */
@Composable
fun CurrentTimeComponent() {
    Box(
        modifier = Modifier()
            .style("class", "card")
            .style("style", "margin-bottom: 2rem; text-align: center;")
    ) {
        Column {
            Text(
                text = "Current Server Time",
                modifier = Modifier().style("style", "font-size: 1.5rem; font-weight: bold; margin-bottom: 0.5rem;")
            )

            Text(
                text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                modifier = Modifier()
                    .style("id", "current-time")
                    .style("style", "font-size: 1.25rem; color: var(--primary-color);")
                    .style("hx-get", "/api/time")
                    .style("hx-trigger", "every 1s")
                    .style("hx-swap", "innerHTML")
            )
        }
    }
}

/**
 * Theme selection component.
 *
 * Note: The actual implementation is in ThemeSelectionComponent.kt
 * to avoid function name conflicts.
 */
// ThemeSelectionComponent moved to its own file: ThemeSelectionComponent.kt

/**
 * Form with various input types for testing.
 */
@Composable
fun VariousInputsFormComponent() {
    // Use direct state access
    val textValue = remember { mutableStateOf("") }
    val sliderValue = remember { mutableStateOf(50f..50f) }
    val checkboxChecked = remember { mutableStateOf(false) }

    Card {
        // Use renamed HtmxForm composable
        HtmxForm(modifier = Modifier().style("hx-post", "/api/form-test")) {
            Column(modifier = Modifier().style("style", "gap: 1rem;")) { // Style for gap
                Text(
                    text = "Various Input Types Test",
                    modifier = Modifier().style(
                        "style",
                        "font-size: 1.5rem; font-weight: bold; margin-bottom: 0.5rem;"
                    )
                )

                FormGroup("Text Field") {
                    TextField(
                        value = textValue.value, // Access state value
                        onValueChange = { textValue.value = it }, // Update state value
                        placeholder = "Enter some text",
                        modifier = Modifier().style("name", "textField")
                    )
                }

                FormGroup("Range Slider (0-100)") {
                    Row(
                        modifier = Modifier().style(
                            "style",
                            "align-items: center; gap: 1rem;"
                        )
                    ) { // Style for alignment and gap
                        // Assuming RangeSlider exists and takes these parameters
                        RangeSlider(
                            value = sliderValue.value, // Now using a ClosedFloatingPointRange<Float>
                            onValueChange = { sliderValue.value = it }, // Update the range
                            valueRange = 0f..100f, // Set the range from 0 to 100
                            // Apply style attributes directly
                            modifier = Modifier().style("style", "flex: 1;").style("name", "slider")
                        )
                        Text(
                            text = "${sliderValue.value.start.toInt()}-${sliderValue.value.endInclusive.toInt()}"
                        ) // Format the range display
                    }
                }

                FormGroup("Checkbox") {
                    // Assuming Checkbox exists and takes these parameters
                    Checkbox(
                        checked = checkboxChecked.value, // Access state value
                        onCheckedChange = { checkboxChecked.value = it }, // Update state value
                        label = "Agree to terms?",
                        modifier = Modifier().style("name", "checkbox")
                    )
                }

                Button(
                    label = "Submit Inputs",
                    onClick = {},
                    modifier = Modifier().style("type", "submit").style("class", "btn")
                )
            }
        }
    }
}

// Renamed custom 'form' composable to avoid conflicts
@Composable
fun HtmxForm(
    modifier: Modifier = Modifier(),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier, // Apply all attributes (id, hx-*) here
        content = content
    )
}

/**
 * A form group that wraps form elements with a label.
 */
@Composable
fun FormGroup(label: String, content: @Composable () -> Unit) {
    Box(modifier = Modifier().style("class", "form-group").style("style", "margin-bottom: 1rem;")) {
        Box(modifier = Modifier().style("style", "font-weight: bold; margin-bottom: 0.5rem;")) {
            Text(text = label)
        }
        content()
    }
} 