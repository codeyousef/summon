package code.yousef.example.springboot

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.BasicText
import code.yousef.summon.components.display.Label
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Div
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.navigation.Link
import code.yousef.summon.state.mutableStateOf
import code.yousef.summon.runtime.LocalPlatformRenderer
import code.yousef.summon.modifier.*
import code.yousef.summon.modifier.LayoutModifierExtras.flex
import code.yousef.summon.modifier.LayoutModifiers.gap
import code.yousef.summon.modifier.TextAlign
import code.yousef.summon.modifier.Display
import code.yousef.summon.modifier.JustifyContent
import code.yousef.summon.modifier.AlignItems
import code.yousef.summon.modifier.FontWeight
import code.yousef.summon.modifier.BorderStyle
import code.yousef.summon.modifier.Cursor
import code.yousef.summon.modifier.textAlign
import code.yousef.summon.core.style.Color
import code.yousef.summon.extensions.*
import code.yousef.summon.state.mutableStateOf
import java.time.LocalDateTime

/**
 * A working link component that properly renders content inside anchor tags for server-side rendering.
 */
@Composable
fun WorkingLink(
    href: String,
    modifier: Modifier = Modifier(),
    target: String? = null,
    content: @Composable () -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    
    // Create final modifier with target attribute if specified
    val finalModifier = if (target != null) {
        modifier.attribute("target", target).attribute("rel", "noopener noreferrer")
    } else {
        modifier
    }
    
    // Use the working renderLink method that takes content
    renderer.renderLink(finalModifier, href, content)
}

/**
 * Hero component for the landing page using Summon framework.
 */
@Composable
fun HeroComponent(username: String) {
    Div(
        modifier = Modifier()
            .className("hero-section")
            .background("linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            .color(Color.WHITE.toCssString())
            .padding("${4.rem} ${2.rem}")
            .textAlign(TextAlign.Center, null)
            .margin("0 0 ${2.rem} 0")
    ) {
        Column {
            Text(
                text = "Welcome to Spring Boot + Summon, $username!",
                modifier = Modifier()
                    .fontSize(2.5.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .margin("0 0 ${1.rem} 0")
            )
            Text(
                text = "Build reactive web applications with declarative UI components",
                modifier = Modifier()
                    .fontSize(1.25.rem)
                    .margin("0 0 ${2.rem} 0")
            )
            // Use a proper link to scroll to features section
            WorkingLink(
                href = "#features",
                modifier = Modifier()
                    .backgroundColor(Color.WHITE.toCssString())
                    .color(Color.fromHex("#667eea").toCssString())
                    .padding("${0.75.rem} ${2.rem}")
                    .border("none", BorderStyle.Solid.value, Color.TRANSPARENT.toCssString())
                    .borderRadius(6.px)
                    .fontSize(1.1.rem)
                    .fontWeight(FontWeight.SemiBold.value)
                    .cursor(Cursor.Pointer.value)
                    .textDecoration("none", null)
                    .display(Display.InlineBlock)
            ) {
                Text("Get Started")
            }
        }
    }
}

/**
 * Feature cards component showcasing key features.
 */
@Composable
fun FeatureCardsComponent() {
    Div(modifier = Modifier().className("feature-cards").id("features")) {
        Text(
            text = "Key Features",
            modifier = Modifier()
                .fontSize(2.rem)
                .fontWeight(FontWeight.Bold.value)
                .textAlign(TextAlign.Center, null)
                .margin("0 0 ${2.rem} 0")
        )
        Row(
            modifier = Modifier()
                .justifyContent(JustifyContent.SpaceBetween)
                .gap(1.rem)
        ) {
            FeatureCard(
                icon = "🔄",
                title = "Reactive UI",
                description = "Build reactive user interfaces with server-side rendering."
            )
            FeatureCard(
                icon = "🌱",
                title = "Spring Integration",
                description = "Seamless integration with Spring Boot and Thymeleaf templates."
            )
            FeatureCard(
                icon = "🛡️",
                title = "Type-Safe",
                description = "Kotlin's type safety with declarative UI components."
            )
        }
    }
}

/**
 * Individual feature card component.
 */
@Composable
fun FeatureCard(icon: String, title: String, description: String) {
    Div(modifier = Modifier().width("calc(33.333% - 0.66rem)")) {
        Card(
            modifier = Modifier()
                .fillMaxHeight()
                .textAlign(TextAlign.Center, null)
        ) {
            Text(
                text = icon,
                modifier = Modifier()
                    .fontSize(4.rem)
                    .margin("0 0 ${1.rem} 0")
            )
            Text(
                text = title,
                modifier = Modifier()
                    .fontSize(1.25.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .margin("0 0 ${1.rem} 0")
            )
            Text(
                text = description,
                modifier = Modifier().color(Color.fromHex("#666").toCssString())
            )
        }
    }
}

/**
 * Card component for consistent styling.
 */
@Composable
fun Card(modifier: Modifier = Modifier(), content: @Composable () -> Unit) {
    Div(
        modifier = modifier
            .backgroundColor(Color.WHITE.toCssString())
            .borderRadius(8.px)
            .shadow()
            .padding(1.5.rem)
    ) {
        content()
    }
}

/**
 * Interactive counter component with proper state management.
 */
@Composable
fun CounterComponent(initialValue: Int = 0) {
    val counterState = mutableStateOf(initialValue)
    
    Div(
        modifier = Modifier()
            .className("counter-component")
            .maxWidth(400.px)
            .margin("${2.rem} auto")
    ) {
        Card(modifier = Modifier().textAlign(TextAlign.Center, null)) {
            Text(
                text = "Interactive Counter",
                modifier = Modifier()
                    .fontSize(1.25.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .margin("0 0 ${1.rem} 0")
            )
            Text(
                text = counterState.value.toString(),
                modifier = Modifier()
                    .id("counter-value")
                    .fontSize(3.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .color(Color.fromHex("#0066cc").toCssString())
                    .margin("0 0 ${2.rem} 0")
            )
            Row(
                modifier = Modifier()
                    .justifyContent(JustifyContent.Center)
                    .gap(1.rem)
            ) {
                Button(
                    onClick = { counterState.value = counterState.value - 1 },
                    label = "Decrement",
                    modifier = Modifier()
                        .backgroundColor(Color.fromHex("#6c757d").toCssString())
                        .color(Color.WHITE.toCssString())
                        .border("none", BorderStyle.Solid.value, Color.TRANSPARENT.toCssString())
                        .borderRadius(4.px)
                        .padding("0.5rem 1rem")
                        .cursor(Cursor.Pointer.value)
                )
                Button(
                    onClick = { counterState.value = counterState.value + 1 },
                    label = "Increment",
                    modifier = Modifier()
                        .backgroundColor(Color.fromHex("#0066cc").toCssString())
                        .color(Color.WHITE.toCssString())
                        .border("none", BorderStyle.Solid.value, Color.TRANSPARENT.toCssString())
                        .borderRadius(4.px)
                        .padding("0.5rem 1rem")
                        .cursor(Cursor.Pointer.value)
                )
                Button(
                    onClick = { counterState.value = 0 },
                    label = "Reset",
                    modifier = Modifier()
                        .backgroundColor(Color.TRANSPARENT.toCssString())
                        .color(Color.fromHex("#6c757d").toCssString())
                        .border("1px", BorderStyle.Solid.value, Color.fromHex("#6c757d").toCssString())
                        .borderRadius(4.px)
                        .padding("0.5rem 1rem")
                        .cursor(Cursor.Pointer.value)
                )
            }
        }
    }
}

/**
 * Dashboard component showing statistics and activity.
 */
@Composable
fun DashboardComponent() {
    Div(modifier = Modifier().className("dashboard")) {
        Text(
            text = "📊 Dashboard",
            modifier = Modifier()
                .fontSize(2.rem)
                .fontWeight(FontWeight.Bold.value)
                .margin("0 0 ${2.rem} 0")
        )
        Row(
            modifier = Modifier()
                .justifyContent(JustifyContent.SpaceBetween)
                .gap(1.rem)
                .margin("0 0 ${2.rem} 0")
        ) {
            StatCard("👥", "Users", "1,234", Color.fromHex("#3498db"))
            StatCard("💰", "Revenue", "$8,345", Color.fromHex("#2ecc71"))
            StatCard("📦", "Products", "567", Color.fromHex("#f39c12"))
            StatCard("🛒", "Orders", "890", Color.fromHex("#e74c3c"))
        }
        Card {
            Text(
                text = "Recent Activity",
                modifier = Modifier()
                    .fontSize(1.25.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .margin("0 0 ${1.rem} 0")
            )
            ActivityItem("✅", "John Doe", "completed order #12345", "2 hours ago")
            ActivityItem("🔄", "Jane Smith", "updated product inventory", "4 hours ago")
            ActivityItem("➕", "Mike Johnson", "added new product", "Yesterday")
        }
    }
}

/**
 * Statistics card component.
 */
@Composable
fun StatCard(icon: String, label: String, value: String, color: Color) {
    Div(modifier = Modifier().width("calc(25% - 0.75rem)")) {
        Card(
            modifier = Modifier()
                .textAlign(TextAlign.Center, null)
                .border("none", BorderStyle.Solid.value, Color.TRANSPARENT.toCssString())
                .style("border-left", "4px solid ${color.toCssString()}")
        ) {
            Text(
                text = icon,
                modifier = Modifier()
                    .fontSize(2.rem)
                    .color(color.toCssString())
                    .margin("0 0 0.5rem 0")
            )
            Text(
                text = label,
                modifier = Modifier()
                    .fontSize(1.rem)
                    .margin("0 0 0.5rem 0")
            )
            Text(
                text = value,
                modifier = Modifier()
                    .fontSize(1.5.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .color(color.toCssString())
            )
        }
    }
}

/**
 * Activity item component.
 */
@Composable
fun ActivityItem(icon: String, name: String, action: String, time: String) {
    Row(
        modifier = Modifier()
            .alignItems(AlignItems.FlexStart)
            .margin("0 0 1rem 0")
    ) {
        Text(
            text = icon,
            modifier = Modifier()
                .backgroundColor(Color.fromHex("#f8f9fa").toCssString())
                .padding("${0.25.rem} ${0.5.rem}")
                .borderRadius("50%")
                .margin("0 1rem 0 0")
        )
        Column {
            Text(
                text = "$name $action",
                modifier = Modifier().margin("0 0 0.25rem 0")
            )
            Text(
                text = time,
                modifier = Modifier()
                    .fontSize(0.875.rem)
                    .color(Color.fromHex("#6c757d").toCssString())
            )
        }
    }
}

/**
 * User table component for displaying user data.
 */
@Composable
fun UserTableComponent(users: List<User>) {
    if (users.isEmpty()) {
        Div(modifier = Modifier().className("user-table")) {
            Card(
                modifier = Modifier()
                    .textAlign(TextAlign.Center, null)
                    .padding(2.rem)
            ) {
                Text(
                    text = "No users found",
                    modifier = Modifier().color(Color.fromHex("#6c757d").toCssString())
                )
            }
        }
    } else {
        Div(modifier = Modifier().className("user-table")) {
            Card {
                UserTable(users)
            }
        }
    }
}

/**
 * User table implementation.
 */
@Composable
fun UserTable(users: List<User>) {
    Div(
        modifier = Modifier()
            .style("display", "table")
            .fillMaxWidth()
    ) {
        // Table header
        Div(
            modifier = Modifier()
                .style("display", "table-header-group")
                .backgroundColor(Color.fromHex("#f8f9fa").toCssString())
                .fontWeight(FontWeight.Bold.value)
        ) {
            UserTableRow(
                listOf("ID", "Name", "Email", "Role", "Status", "Actions"),
                isHeader = true
            )
        }
        // Table body
        Div(modifier = Modifier().style("display", "table-row-group")) {
            users.forEach { user ->
                UserTableRow(
                    listOf(
                        user.id.toString(),
                        user.name,
                        user.email,
                        user.role,
                        if (user.active) "Active" else "Inactive",
                        "" // Actions will be rendered separately
                    ),
                    isHeader = false,
                    user = user
                )
            }
        }
    }
}

/**
 * User table row component.
 */
@Composable
fun UserTableRow(cells: List<String>, isHeader: Boolean, user: User? = null) {
    Div(
        modifier = Modifier()
            .style("display", "table-row")
            .style("border-bottom", "1px solid #dee2e6")
    ) {
        cells.forEachIndexed { index, cell ->
            Div(
                modifier = Modifier()
                    .style("display", "table-cell")
                    .padding(0.75.rem)
                    .style("vertical-align", "middle")
            ) {
                if (index == cells.size - 1 && user != null && !isHeader) {
                    // Render actions for the last column
                    UserActions(user)
                } else {
                    Text(cell)
                }
            }
        }
    }
}

/**
 * Action form component for handling form submissions using native browser form handling.
 */
@Composable
fun ActionForm(
    action: String,
    method: String = "POST",
    confirmMessage: String? = null,
    content: @Composable () -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    
    // Create a form element with proper attributes
    val formModifier = Modifier()
        .attribute("action", action)
        .attribute("method", method)
        .display(Display.InlineBlock)
    
    // For destructive actions, we'll use a simpler approach without JavaScript
    // Users can confirm through the browser's form submission
    
    // Render form using platform renderer
    renderer.renderForm(
        onSubmit = null, // Let browser handle form submission
        modifier = formModifier
    ) {
        content()
    }
}

/**
 * User actions buttons with functional Summon components.
 */
@Composable
fun UserActions(user: User) {
    Row(modifier = Modifier().gap(0.5.rem)) {
        // Edit Button - Use WorkingLink to navigate to edit page
        WorkingLink(
            href = "/users/${user.id}/edit",
            modifier = Modifier()
                .display(Display.InlineBlock)
                .backgroundColor(Color.TRANSPARENT.toCssString())
                .color(Color.fromHex("#0066cc").toCssString())
                .border("1px", BorderStyle.Solid.value, Color.fromHex("#0066cc").toCssString())
                .borderRadius("4px")
                .padding("${0.25.rem} ${0.5.rem}")
                .fontSize(0.875.rem)
                .textDecoration("none", null)
                .cursor(Cursor.Pointer.value)
        ) {
            Text("Edit")
        }
        
        // Delete Button - Use form submission with confirmation attribute
        ActionForm(
            action = "/users/${user.id}/delete",
            method = "POST"
        ) {
            Button(
                onClick = { /* Form will handle submission */ },
                label = "Delete",
                modifier = Modifier()
                    .attribute("type", "submit")
                    .attribute("data-confirm", "Are you sure you want to delete user \"${user.name}\"?")
                    .backgroundColor(Color.TRANSPARENT.toCssString())
                    .color(Color.fromHex("#dc3545").toCssString())
                    .border("1px", BorderStyle.Solid.value, Color.fromHex("#dc3545").toCssString())
                    .borderRadius(4.px)
                    .padding("${0.25.rem} ${0.5.rem}")
                    .fontSize(0.875.rem)
                    .cursor(Cursor.Pointer.value)
            )
        }
        
        // Toggle Status Button - Use form submission
        ActionForm(
            action = "/users/${user.id}/toggle-status",
            method = "POST",
            confirmMessage = null // No confirmation needed for status toggle
        ) {
            Button(
                onClick = { /* Form will handle submission */ },
                label = if (user.active) "Deactivate" else "Activate",
                modifier = Modifier()
                    .attribute("type", "submit")
                    .backgroundColor(Color.TRANSPARENT.toCssString())
                    .color(if (user.active) Color.fromHex("#ffc107").toCssString() else Color.fromHex("#28a745").toCssString())
                    .border(1.px, BorderStyle.Solid, if (user.active) Color.fromHex("#ffc107").toCssString() else Color.fromHex("#28a745").toCssString())
                    .borderRadius(4.px)
                    .padding("${0.25.rem} ${0.5.rem}")
                    .fontSize(0.875.rem)
                    .cursor(Cursor.Pointer.value)
            )
        }
    }
}

/**
 * Contact form component.
 */
@Composable
fun ContactFormComponent() {
    Div(
        modifier = Modifier()
            .className("contact-form")
            .maxWidth(600.px)
            .margin("${2.rem} auto")
    ) {
        Card {
            Text(
                text = "📧 Contact Us",
                modifier = Modifier()
                    .fontSize(2.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .textAlign(TextAlign.Center, null)
                    .margin("0 0 ${2.rem} 0")
            )
            FormWrapper(
                action = "/contact",
                method = "POST"
            ) {
                FormField("Name", "name", "text")
                FormField("Email", "email", "email")
                FormField("Subject", "subject", "text")
                FormFieldTextArea("Message", "message")
                Div(modifier = Modifier().margin("1rem 0 0 0")) {
                    Button(
                        onClick = { 
                            // Contact form submission
                            println("Contact form submitted!")
                        },
                        label = "Send Message",
                        modifier = Modifier()
                            .attribute("type", "submit")
                            .backgroundColor(Color.fromHex("#0066cc").toCssString())
                            .color(Color.WHITE.toCssString())
                            .border("none", BorderStyle.Solid.value, Color.TRANSPARENT.toCssString())
                            .borderRadius(4.px)
                            .padding("${0.75.rem} ${2.rem}")
                            .fontSize(1.1.rem)
                            .fontWeight(FontWeight.SemiBold.value)
                            .cursor(Cursor.Pointer.value)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

/**
 * Form wrapper component for server-side forms using native Summon form handling.
 */
@Composable
fun FormWrapper(
    action: String,
    method: String = "POST",
    content: @Composable () -> Unit
) {
    val renderer = LocalPlatformRenderer.current
    
    // Use Summon's native form rendering
    renderer.renderForm(
        onSubmit = null, // Let browser handle form submission to server
        modifier = Modifier()
            .attribute("action", action)
            .attribute("method", method)
    ) {
        content()
    }
}

/**
 * Form field component using custom form input.
 */
@Composable
fun FormField(labelText: String, name: String, type: String) {
    Div(modifier = Modifier().margin("0 0 1rem 0")) {
        FormLabel(text = labelText, forInput = name)
        FormInput(
            name = name,
            type = type,
            modifier = Modifier()
                .attribute("required", "true")
                .fillMaxWidth()
                .padding(0.5.rem)
                .border("1px", BorderStyle.Solid.value, Color.fromHex("#dee2e6").toCssString())
                .borderRadius(4.px)
                .fontSize(1.rem)
        )
    }
}

/**
 * Form field text area component using custom form textarea.
 */
@Composable
fun FormFieldTextArea(labelText: String, name: String) {
    Div(modifier = Modifier().margin("0 0 1rem 0")) {
        FormLabel(text = labelText, forInput = name)
        FormTextArea(
            name = name,
            rows = 6,
            modifier = Modifier()
                .attribute("required", "true")
                .fillMaxWidth()
                .padding(0.5.rem)
                .border("1px", BorderStyle.Solid.value, Color.fromHex("#dee2e6").toCssString())
                .borderRadius(4.px)
                .fontSize(1.rem)
                .style("resize", "vertical")
        )
    }
}

/**
 * Label component wrapper.
 * Uses Summon's Label component which properly handles the 'for' attribute.
 */
@Composable
fun FormLabel(text: String, forInput: String) {
    Label(
        text = text,
        forElement = forInput,
        modifier = Modifier()
            .display(Display.Block)
            .margin("0 0 0.5rem 0")
            .fontWeight(FontWeight.Medium.value)
    )
}

/**
 * Quick links component.
 */
@Composable
fun QuickLinksComponent() {
    Div(modifier = Modifier().margin("2rem 0")) {
        Row(modifier = Modifier().gap(1.rem)) {
            QuickLinkCard(
                icon = "👥",
                title = "User Management",
                description = "Manage users with full CRUD operations and interactive tables.",
                link = "/users",
                buttonText = "View Users",
                buttonColor = Color.fromHex("#0066cc")
            )
            QuickLinkCard(
                icon = "📊",
                title = "Dashboard",
                description = "View statistics, metrics, and activity feeds.",
                link = "/dashboard",
                buttonText = "View Dashboard",
                buttonColor = Color.fromHex("#17a2b8")
            )
        }
    }
}

/**
 * Quick link card component.
 */
@Composable
fun QuickLinkCard(
    icon: String,
    title: String,
    description: String,
    link: String,
    buttonText: String,
    buttonColor: Color
) {
    Div(modifier = Modifier().flex("1")) {
        Card {
            Text(
                text = "$icon $title",
                modifier = Modifier()
                    .fontSize(1.25.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .margin("0 0 ${1.rem} 0")
            )
            Text(
                text = description,
                modifier = Modifier()
                    .color(Color.fromHex("#666").toCssString())
                    .margin("0 0 ${1.rem} 0")
            )
            Div(modifier = Modifier().textAlign(TextAlign.Left, null)) {
                WorkingLink(
                    href = link,
                    modifier = Modifier()
                        .display(Display.InlineBlock)
                        .backgroundColor(buttonColor.toCssString())
                        .color(Color.WHITE.toCssString())
                        .padding("0.5rem 1rem")
                        .textDecoration("none", null)
                        .borderRadius(4.px)
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}

/**
 * Navigation component.
 */
@Composable
fun NavigationComponent() {
    Div(
        modifier = Modifier()
            .backgroundColor(Color.fromHex("#343a40").toCssString())
            .color(Color.WHITE.toCssString())
            .padding("1rem 0")
    ) {
        Div(
            modifier = Modifier()
                .maxWidth(1200.px)
                .margin("0 auto")
                .padding("0 1rem")
                .display(Display.Flex)
                .justifyContent(JustifyContent.SpaceBetween)
                .alignItems(AlignItems.Center)
        ) {
            WorkingLink(
                href = "/",
                modifier = Modifier()
                    .color(Color.WHITE.toCssString())
                    .textDecoration("none", null)
                    .fontWeight(FontWeight.Bold.value)
            ) {
                Text("🚀 Summon + Spring Boot")
            }
            Div(
                modifier = Modifier()
                    .display(Display.Flex)
                    .gap(1.rem)
            ) {
                WorkingLink(
                    href = "/",
                    modifier = Modifier()
                        .color(Color.WHITE.toCssString())
                        .textDecoration("none", null)
                ) {
                    Text("Home")
                }
                WorkingLink(
                    href = "/users",
                    modifier = Modifier()
                        .color(Color.WHITE.toCssString())
                        .textDecoration("none", null)
                ) {
                    Text("Users")
                }
                WorkingLink(
                    href = "/dashboard",
                    modifier = Modifier()
                        .color(Color.WHITE.toCssString())
                        .textDecoration("none", null)
                ) {
                    Text("Dashboard")
                }
                WorkingLink(
                    href = "/contact",
                    modifier = Modifier()
                        .color(Color.WHITE.toCssString())
                        .textDecoration("none", null)
                ) {
                    Text("Contact")
                }
            }
        }
    }
}

/**
 * Footer component.
 */
@Composable
fun FooterComponent() {
    Div(
        modifier = Modifier()
            .backgroundColor(Color.fromHex("#343a40").toCssString())
            .color(Color.WHITE.toCssString())
            .padding("2rem 0")
    ) {
        Div(
            modifier = Modifier()
                .maxWidth(1200.px)
                .margin("0 auto")
                .padding("0 1rem")
        ) {
            // Footer Links Section
            Row(
                modifier = Modifier()
                    .justifyContent(JustifyContent.Center)
                    .gap(2.rem)
                    .margin("0 0 1.5rem 0")
            ) {
                WorkingLink(
                    href = "/",
                    modifier = Modifier()
                        .color(Color.fromHex("#b8b8b8").toCssString())
                        .textDecoration("none", null)
                        .fontSize(0.95.rem)
                        .transition("color 0.2s")
                        .hover(Modifier().color(Color.WHITE.toCssString()))
                ) {
                    Text("Home")
                }
                WorkingLink(
                    href = "/users",
                    modifier = Modifier()
                        .color(Color.fromHex("#b8b8b8").toCssString())
                        .textDecoration("none", null)
                        .fontSize(0.95.rem)
                        .transition("color 0.2s")
                        .hover(Modifier().color(Color.WHITE.toCssString()))
                ) {
                    Text("Users")
                }
                WorkingLink(
                    href = "/dashboard",
                    modifier = Modifier()
                        .color(Color.fromHex("#b8b8b8").toCssString())
                        .textDecoration("none", null)
                        .fontSize(0.95.rem)
                        .transition("color 0.2s")
                        .hover(Modifier().color(Color.WHITE.toCssString()))
                ) {
                    Text("Dashboard")
                }
                WorkingLink(
                    href = "/contact",
                    modifier = Modifier()
                        .color(Color.fromHex("#b8b8b8").toCssString())
                        .textDecoration("none", null)
                        .fontSize(0.95.rem)
                        .transition("color 0.2s")
                        .hover(Modifier().color(Color.WHITE.toCssString()))
                ) {
                    Text("Contact")
                }
            }
            
            // External Links Section
            Row(
                modifier = Modifier()
                    .justifyContent(JustifyContent.Center)
                    .gap(2.rem)
                    .margin("0 0 1.5rem 0")
            ) {
                WorkingLink(
                    href = "https://github.com/codeyousef/summon",
                    target = "_blank",
                    modifier = Modifier()
                        .color(Color.fromHex("#0084ff").toCssString())
                        .textDecoration("none", null)
                        .fontSize(0.9.rem)
                        .transition("color 0.2s")
                        .hover(Modifier().color(Color.fromHex("#40a6ff").toCssString()))
                ) {
                    Text("🔗 GitHub Repository")
                }
                WorkingLink(
                    href = "https://kotlinlang.org/",
                    target = "_blank",
                    modifier = Modifier()
                        .color(Color.fromHex("#0084ff").toCssString())
                        .textDecoration("none", null)
                        .fontSize(0.9.rem)
                        .transition("color 0.2s")
                        .hover(Modifier().color(Color.fromHex("#40a6ff").toCssString()))
                ) {
                    Text("🔗 Kotlin")
                }
                WorkingLink(
                    href = "https://spring.io/projects/spring-boot",
                    target = "_blank",
                    modifier = Modifier()
                        .color(Color.fromHex("#0084ff").toCssString())
                        .textDecoration("none", null)
                        .fontSize(0.9.rem)
                        .transition("color 0.2s")
                        .hover(Modifier().color(Color.fromHex("#40a6ff").toCssString()))
                ) {
                    Text("🔗 Spring Boot")
                }
            }
            
            // Copyright Section
            Div(modifier = Modifier().textAlign(TextAlign.Center, null)) {
                Text(
                    text = "© 2025 Summon Spring Boot Example. Powered by Summon Framework.",
                    modifier = Modifier()
                        .margin("0")
                        .fontSize(0.875.rem)
                        .color(Color.fromHex("#888").toCssString())
                )
            }
        }
    }
}

/**
 * Current time component.
 */
@Composable
fun CurrentTimeComponent() {
    Div(
        modifier = Modifier()
            .margin("2rem 0")
            .textAlign(TextAlign.Center, null)
    ) {
        Card {
            Text(
                text = "🕒 Current Server Time",
                modifier = Modifier()
                    .fontSize(1.25.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .margin("0 0 ${1.rem} 0")
            )
            Text(
                text = LocalDateTime.now().toString(),
                modifier = Modifier()
                    .fontSize(1.1.rem)
                    .color(Color.fromHex("#0066cc").toCssString())
            )
        }
    }
}

/**
 * Add user form component.
 */
@Composable
fun AddUserFormComponent() {
    Div(modifier = Modifier().margin("0 0 2rem 0")) {
        Card {
            Text(
                text = "Add New User",
                modifier = Modifier()
                    .fontSize(1.25.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .margin("0 0 1.5rem 0")
            )
            FormWrapper(action = "/users", method = "POST") {
                Row(modifier = Modifier().gap(1.rem)) {
                    Div(modifier = Modifier().flex("1")) {
                        FormField("Name", "name", "text")
                    }
                    Div(modifier = Modifier().flex("1")) {
                        FormField("Email", "email", "email")
                    }
                    Div(modifier = Modifier().flex("1")) {
                        FormFieldSelect(
                            "Role", "role", listOf(
                                "" to "Choose role...",
                                "admin" to "Admin",
                                "editor" to "Editor",
                                "moderator" to "Moderator",
                                "user" to "User"
                            )
                        )
                    }
                }
                Div(modifier = Modifier().margin("1rem 0 0 0")) {
                    // Create submit button using renderer directly to avoid Button component JS interference
                    val renderer = LocalPlatformRenderer.current
                    renderer.renderButton(
                        onClick = { },
                        modifier = Modifier()
                            .attribute("type", "submit")
                            .backgroundColor(Color.fromHex("#0066cc").toCssString())
                            .color(Color.WHITE.toCssString())
                            .border("none", BorderStyle.Solid.value, Color.TRANSPARENT.toCssString())
                            .borderRadius(4.px)
                            .padding("0.5rem 1rem")
                            .cursor(Cursor.Pointer.value)
                            .margin("0 0.5rem 0 0")
                            .fontSize(1.rem)
                    ) {
                        +("Add User")
                    }
                    Button(
                        onClick = { 
                            // Refresh table functionality
                            println("Refresh table clicked!")
                        },
                        label = "Refresh Table",
                        modifier = Modifier()
                            .attribute("type", "button")
                            .backgroundColor(Color.fromHex("#6c757d").toCssString())
                            .color(Color.WHITE.toCssString())
                            .border("none", BorderStyle.Solid.value, Color.TRANSPARENT.toCssString())
                            .borderRadius(4.px)
                            .padding("0.5rem 1rem")
                            .cursor(Cursor.Pointer.value)
                    )
                }
            }
        }
    }
}

/**
 * Form field select component using custom form select.
 */
@Composable
fun FormFieldSelect(labelText: String, name: String, options: List<Pair<String, String>>) {
    Div(modifier = Modifier().margin("0 0 1rem 0")) {
        FormLabel(text = labelText, forInput = name)
        FormSelect(
            name = name,
            options = options,
            modifier = Modifier()
                .attribute("required", "true")
                .fillMaxWidth()
                .padding(0.5.rem)
                .border("1px", BorderStyle.Solid.value, Color.fromHex("#dee2e6").toCssString())
                .borderRadius(4.px)
                .fontSize(1.rem)
        )
    }
}

/**
 * User statistics component.
 */
@Composable
fun UserStatisticsComponent(users: List<User>) {
    val totalUsers = users.size
    val activeUsers = users.count { it.active }
    val adminUsers = users.count { it.role == "admin" }
    val regularUsers = users.count { it.role == "user" }

    Div(modifier = Modifier().margin("2rem 0")) {
        Text(
            text = "User Statistics",
            modifier = Modifier()
                .fontSize(1.5.rem)
                .fontWeight(FontWeight.Bold.value)
                .margin("0 0 1rem 0")
        )
        Row(modifier = Modifier().gap(1.rem)) {
            UserStatCard("Total Users", totalUsers.toString(), Color.fromHex("#3498db"))
            UserStatCard("Active Users", activeUsers.toString(), Color.fromHex("#2ecc71"))
            UserStatCard("Admins", adminUsers.toString(), Color.fromHex("#f39c12"))
            UserStatCard("Regular Users", regularUsers.toString(), Color.fromHex("#17a2b8"))
        }
    }
}

/**
 * User statistics card.
 */
@Composable
fun UserStatCard(title: String, value: String, color: Color) {
    Div(modifier = Modifier().flex("1")) {
        Card(
            modifier = Modifier()
                .textAlign(TextAlign.Center, null)
                .style("border-left", "4px solid ${color.toCssString()}")
        ) {
            Text(
                text = title,
                modifier = Modifier()
                    .fontSize(1.rem)
                    .margin("0 0 0.5rem 0")
            )
            Text(
                text = value,
                modifier = Modifier()
                    .fontSize(2.rem)
                    .fontWeight(FontWeight.Bold.value)
                    .color(color.toCssString())
            )
        }
    }
}

/**
 * User page header component.
 */
@Composable
fun UserPageHeaderComponent() {
    Div(modifier = Modifier().margin("0 0 2rem 0")) {
        Text(
            text = "👥 User Management",
            modifier = Modifier()
                .fontSize(2.rem)
                .fontWeight(FontWeight.Bold.value)
        )
    }
}