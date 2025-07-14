package code.yousef.example.springboot

/**
 * Pure Summon implementation using Modifier-based styling.
 * This demonstrates declarative UI components without manual HTML/CSS/JS.
 * All styling is done through the Modifier system, no hardcoded Bootstrap classes.
 */

// Essential Summon framework
@Target(AnnotationTarget.FUNCTION)
annotation class Composable

/**
 * Summon Modifier system for type-safe styling
 */
data class Modifier(
    val styles: Map<String, String> = emptyMap(),
    val attributes: Map<String, String> = emptyMap()
) {
    fun style(propertyName: String, value: String): Modifier =
        copy(styles = this.styles + (propertyName to value))
        
    fun background(color: String): Modifier =
        style("background", color)
        
    fun backgroundColor(color: String): Modifier =
        style("background-color", color)
        
    fun padding(value: String): Modifier =
        style("padding", value)
        
    fun margin(value: String): Modifier =
        style("margin", value)
        
    fun width(value: String): Modifier =
        style("width", value)
        
    fun height(value: String): Modifier =
        style("height", value)
        
    fun fillMaxWidth(): Modifier =
        style("width", "100%")
        
    fun fillMaxHeight(): Modifier =
        style("height", "100%")
        
    fun border(width: String, style: String, color: String): Modifier =
        this.style("border", "$width $style $color")
        
    fun borderRadius(value: String): Modifier =
        style("border-radius", value)
        
    fun color(value: String): Modifier =
        style("color", value)
        
    fun fontSize(value: String): Modifier =
        style("font-size", value)
        
    fun fontWeight(value: String): Modifier =
        style("font-weight", value)
        
    fun textAlign(value: String): Modifier =
        style("text-align", value)
        
    fun display(value: String): Modifier =
        style("display", value)
        
    fun flexDirection(value: String): Modifier =
        style("flex-direction", value)
        
    fun justifyContent(value: String): Modifier =
        style("justify-content", value)
        
    fun alignItems(value: String): Modifier =
        style("align-items", value)
        
    fun gap(value: String): Modifier =
        style("gap", value)
        
    fun shadow(offsetX: String = "0px", offsetY: String = "2px", blurRadius: String = "4px", color: String = "rgba(0,0,0,0.1)"): Modifier =
        style("box-shadow", "$offsetX $offsetY $blurRadius $color")
        
    fun cursor(value: String): Modifier =
        style("cursor", value)
        
    fun maxWidth(value: String): Modifier =
        style("max-width", value)
        
    fun attribute(name: String, value: String): Modifier =
        copy(attributes = this.attributes + (name to value))
        
    fun onClick(handler: String): Modifier =
        attribute("onclick", handler)
        
    fun id(value: String): Modifier =
        attribute("id", value)
        
    fun className(value: String): Modifier =
        attribute("class", value)
        
    fun toStyleString(): String =
        if (styles.isEmpty()) "" else styles.entries.joinToString(
            separator = "; ",
            postfix = ";"
        ) { "${it.key}: ${it.value}" }
        
    fun toAttributesString(): String =
        attributes.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" }
}

// State management
class State<T>(var value: T)
fun <T> mutableStateOf(initial: T) = State(initial)
fun <T> remember(calculation: () -> State<T>) = calculation()

// Utility to create empty modifier
fun Modifier(): Modifier = Modifier(emptyMap(), emptyMap())

// Pure Summon UI Components
@Composable
fun Div(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<div$attrsStr$styleStr>${content()}</div>"
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier(),
    tag: String = "p"
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<$tag$attrsStr$styleStr>$text</$tag>"
}

@Composable
fun Button(
    text: String,
    modifier: Modifier = Modifier()
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<button$attrsStr$styleStr>$text</button>"
}

@Composable
fun Row(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val rowModifier = modifier
        .display("flex")
        .flexDirection("row")
    return Div(rowModifier) { content() }
}

@Composable
fun Column(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val columnModifier = modifier
        .display("flex")
        .flexDirection("column")
    return Div(columnModifier) { content() }
}

@Composable
fun Card(
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val cardModifier = modifier
        .backgroundColor("white")
        .borderRadius("8px")
        .shadow()
        .padding("1.5rem")
    return Div(cardModifier) { content() }
}

@Composable
fun Input(
    type: String = "text",
    modifier: Modifier = Modifier()
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<input type=\"$type\"$attrsStr$styleStr>"
}

@Composable
fun TextArea(
    rows: Int = 4,
    modifier: Modifier = Modifier()
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<textarea rows=\"$rows\"$attrsStr$styleStr></textarea>"
}

@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier()
): String {
    val styleStr = if (modifier.styles.isNotEmpty()) " style=\"${modifier.toStyleString()}\"" else ""
    val attrsStr = if (modifier.attributes.isNotEmpty()) " ${modifier.toAttributesString()}" else ""
    return "<label$attrsStr$styleStr>$text</label>"
}

@Composable
fun Form(
    action: String,
    method: String = "POST",
    modifier: Modifier = Modifier(),
    content: () -> String
): String {
    val formModifier = modifier.attribute("action", action).attribute("method", method)
    val styleStr = if (formModifier.styles.isNotEmpty()) " style=\"${formModifier.toStyleString()}\"" else ""
    val attrsStr = if (formModifier.attributes.isNotEmpty()) " ${formModifier.toAttributesString()}" else ""
    return "<form$attrsStr$styleStr>${content()}</form>"
}

// Application Components using pure Summon
@Composable
fun HeroComponent(username: String): String {
    return Div(
        Modifier()
            .className("hero-section")
            .background("linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
            .color("white")
            .padding("4rem 2rem")
            .textAlign("center")
            .margin("0 0 2rem 0")
    ) {
        Column {
            Text(
                text = "Welcome to Spring Boot + Summon, $username!",
                modifier = Modifier()
                    .fontSize("2.5rem")
                    .fontWeight("bold")
                    .margin("0 0 1rem 0"),
                tag = "h1"
            ) + Text(
                text = "Build reactive web applications with declarative UI components",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .margin("0 0 2rem 0")
            ) + Button(
                text = "Get Started",
                modifier = Modifier()
                    .backgroundColor("white")
                    .color("#667eea")
                    .padding("0.75rem 2rem")
                    .border("none", "solid", "transparent")
                    .borderRadius("6px")
                    .fontSize("1.1rem")
                    .fontWeight("600")
                    .cursor("pointer")
                    .onClick("scrollToFeatures()")
            )
        }
    }
}

@Composable
fun FeatureCardsComponent(): String {
    return Div(
        Modifier().className("feature-cards")
    ) {
        Text(
            text = "Key Features",
            modifier = Modifier()
                .fontSize("2rem")
                .fontWeight("bold")
                .textAlign("center")
                .margin("0 0 2rem 0"),
            tag = "h2"
        ) + Row(
            Modifier()
                .justifyContent("space-between")
                .gap("1rem")
        ) {
            FeatureCard(
                icon = "🔄",
                title = "Reactive UI",
                description = "Build reactive user interfaces with server-side rendering."
            ) + FeatureCard(
                icon = "🌱",
                title = "Spring Integration",
                description = "Seamless integration with Spring Boot and Thymeleaf templates."
            ) + FeatureCard(
                icon = "🛡️",
                title = "Type-Safe",
                description = "Kotlin's type safety with declarative UI components."
            )
        }
    }
}

@Composable
fun FeatureCard(icon: String, title: String, description: String): String {
    return Div(
        Modifier()
            .width("calc(33.333% - 0.66rem)")
    ) {
        Card(
            Modifier()
                .fillMaxHeight()
                .textAlign("center")
        ) {
            Text(
                text = icon,
                modifier = Modifier()
                    .fontSize("4rem")
                    .margin("0 0 1rem 0")
            ) + Text(
                text = title,
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("bold")
                    .margin("0 0 1rem 0"),
                tag = "h5"
            ) + Text(
                text = description,
                modifier = Modifier()
                    .color("#666")
            )
        }
    }
}

@Composable
fun CounterComponent(initialValue: Int = 0): String {
    return Div(
        Modifier()
            .className("counter-component")
            .maxWidth("400px")
            .margin("2rem auto")
    ) {
        Card(
            Modifier().textAlign("center")
        ) {
            Text(
                text = "Interactive Counter",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("bold")
                    .margin("0 0 1rem 0"),
                tag = "h5"
            ) + Text(
                text = initialValue.toString(),
                modifier = Modifier()
                    .id("counter-value")
                    .fontSize("3rem")
                    .fontWeight("bold")
                    .color("#0066cc")
                    .margin("0 0 2rem 0")
            ) + Row(
                Modifier()
                    .justifyContent("center")
                    .gap("1rem")
            ) {
                Button(
                    text = "Decrement",
                    modifier = Modifier()
                        .backgroundColor("#6c757d")
                        .color("white")
                        .border("none", "solid", "transparent")
                        .borderRadius("4px")
                        .padding("0.5rem 1rem")
                        .cursor("pointer")
                        .onClick("updateCounter('decrement')")
                ) + Button(
                    text = "Increment",
                    modifier = Modifier()
                        .backgroundColor("#0066cc")
                        .color("white")
                        .border("none", "solid", "transparent")
                        .borderRadius("4px")
                        .padding("0.5rem 1rem")
                        .cursor("pointer")
                        .onClick("updateCounter('increment')")
                ) + Button(
                    text = "Reset",
                    modifier = Modifier()
                        .backgroundColor("transparent")
                        .color("#6c757d")
                        .border("1px", "solid", "#6c757d")
                        .borderRadius("4px")
                        .padding("0.5rem 1rem")
                        .cursor("pointer")
                        .onClick("updateCounter('reset')")
                )
            }
        }
    }
}

@Composable
fun CounterComponentWithState(counter: State<Int>): String {
    return CounterComponent(counter.value)
}

@Composable
fun DashboardComponent(): String {
    return Div(
        Modifier().className("dashboard")
    ) {
        Text(
            text = "📊 Dashboard",
            modifier = Modifier()
                .fontSize("2rem")
                .fontWeight("bold")
                .margin("0 0 2rem 0"),
            tag = "h1"
        ) + Row(
            Modifier()
                .justifyContent("space-between")
                .gap("1rem")
                .margin("0 0 2rem 0")
        ) {
            StatCard("👥", "Users", "1,234", "#3498db") +
            StatCard("💰", "Revenue", "$8,345", "#2ecc71") +
            StatCard("📦", "Products", "567", "#f39c12") +
            StatCard("🛒", "Orders", "890", "#e74c3c")
        } + Card {
            Text(
                text = "Recent Activity",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("bold")
                    .margin("0 0 1rem 0"),
                tag = "h5"
            ) + ActivityItem("✅", "John Doe", "completed order #12345", "2 hours ago") +
            ActivityItem("🔄", "Jane Smith", "updated product inventory", "4 hours ago") +
            ActivityItem("➕", "Mike Johnson", "added new product", "Yesterday")
        }
    }
}

@Composable
fun StatCard(icon: String, label: String, value: String, color: String): String {
    return Div(
        Modifier()
            .width("calc(25% - 0.75rem)")
    ) {
        Card(
            Modifier()
                .textAlign("center")
                .border("none", "solid", "transparent")
                .style("border-left", "4px solid $color")
        ) {
            Text(
                text = icon,
                modifier = Modifier()
                    .fontSize("2rem")
                    .color(color)
                    .margin("0 0 0.5rem 0")
            ) + Text(
                text = label,
                modifier = Modifier()
                    .fontSize("1rem")
                    .margin("0 0 0.5rem 0"),
                tag = "h5"
            ) + Text(
                text = value,
                modifier = Modifier()
                    .fontSize("1.5rem")
                    .fontWeight("bold")
                    .color(color)
            )
        }
    }
}

@Composable
fun ActivityItem(icon: String, name: String, action: String, time: String): String {
    return Row(
        Modifier()
            .alignItems("flex-start")
            .margin("0 0 1rem 0")
    ) {
        Text(
            text = icon,
            modifier = Modifier()
                .backgroundColor("#f8f9fa")
                .padding("0.25rem 0.5rem")
                .borderRadius("50%")
                .margin("0 1rem 0 0")
        ) + Column {
            Text(
                text = "$name $action",
                modifier = Modifier()
                    .margin("0 0 0.25rem 0")
            ) + Text(
                text = time,
                modifier = Modifier()
                    .fontSize("0.875rem")
                    .color("#6c757d")
            )
        }
    }
}

@Composable
fun UserTableComponent(users: List<User>): String {
    return if (users.isEmpty()) {
        Div(
            Modifier().className("user-table")
        ) {
            Card(
                Modifier()
                    .textAlign("center")
                    .padding("2rem")
            ) {
                Text(
                    text = "No users found",
                    modifier = Modifier().color("#6c757d")
                )
            }
        }
    } else {
        Div(
            Modifier().className("user-table")
        ) {
            Card {
                UserTable(users)
            }
        }
    }
}

@Composable
fun UserTable(users: List<User>): String {
    return Div(
        Modifier()
            .style("display", "table")
            .fillMaxWidth()
    ) {
        // Table header
        Div(
            Modifier()
                .style("display", "table-header-group")
                .backgroundColor("#f8f9fa")
                .fontWeight("bold")
        ) {
            UserTableRow(
                listOf("ID", "Name", "Email", "Role", "Status", "Actions"),
                isHeader = true
            )
        } + 
        // Table body
        Div(
            Modifier().style("display", "table-row-group")
        ) {
            users.joinToString("") { user ->
                UserTableRow(
                    listOf(
                        user.id.toString(),
                        user.name,
                        user.email,
                        user.role,
                        if (user.active) "Active" else "Inactive",
                        buildUserActions(user)
                    ),
                    isHeader = false
                )
            }
        }
    }
}

@Composable
fun UserTableRow(cells: List<String>, isHeader: Boolean): String {
    return Div(
        Modifier()
            .style("display", "table-row")
            .style("border-bottom", "1px solid #dee2e6")
    ) {
        cells.joinToString("") { cell ->
            Div(
                Modifier()
                    .style("display", "table-cell")
                    .padding("0.75rem")
                    .style("vertical-align", "middle")
            ) {
                cell
            }
        }
    }
}

fun buildUserActions(user: User): String {
    return Row(
        Modifier().gap("0.5rem")
    ) {
        Button(
            text = "Edit",
            modifier = Modifier()
                .backgroundColor("transparent")
                .color("#0066cc")
                .border("1px", "solid", "#0066cc")
                .borderRadius("4px")
                .padding("0.25rem 0.5rem")
                .fontSize("0.875rem")
                .cursor("pointer")
                .onClick("editUser(${user.id})")
        ) + Button(
            text = "Delete",
            modifier = Modifier()
                .backgroundColor("transparent")
                .color("#dc3545")
                .border("1px", "solid", "#dc3545")
                .borderRadius("4px")
                .padding("0.25rem 0.5rem")
                .fontSize("0.875rem")
                .cursor("pointer")
                .onClick("deleteUser(${user.id}, '${user.name}')")
        ) + Button(
            text = if (user.active) "Deactivate" else "Activate",
            modifier = Modifier()
                .backgroundColor("transparent")
                .color(if (user.active) "#ffc107" else "#28a745")
                .border("1px", "solid", if (user.active) "#ffc107" else "#28a745")
                .borderRadius("4px")
                .padding("0.25rem 0.5rem")
                .fontSize("0.875rem")
                .cursor("pointer")
                .onClick("toggleUserStatus(${user.id}, ${user.active})")
        )
    }
}

@Composable
fun ContactFormComponent(): String {
    return Div(
        Modifier()
            .className("contact-form")
            .maxWidth("600px")
            .margin("2rem auto")
    ) {
        Card {
            Text(
                text = "📧 Contact Us",
                modifier = Modifier()
                    .fontSize("2rem")
                    .fontWeight("bold")
                    .textAlign("center")
                    .margin("0 0 2rem 0"),
                tag = "h1"
            ) + Form(
                action = "/contact",
                method = "POST"
            ) {
                FormField("Name", "name", "text") +
                FormField("Email", "email", "email") +
                FormField("Subject", "subject", "text") +
                FormFieldTextArea("Message", "message") +
                Div(
                    Modifier().margin("1rem 0 0 0")
                ) {
                    Button(
                        text = "Send Message",
                        modifier = Modifier()
                            .attribute("type", "submit")
                            .backgroundColor("#0066cc")
                            .color("white")
                            .border("none", "solid", "transparent")
                            .borderRadius("4px")
                            .padding("0.75rem 2rem")
                            .fontSize("1.1rem")
                            .fontWeight("600")
                            .cursor("pointer")
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun FormField(labelText: String, name: String, type: String): String {
    return Div(
        Modifier().margin("0 0 1rem 0")
    ) {
        Label(
            text = labelText,
            modifier = Modifier()
                .attribute("for", name)
                .display("block")
                .margin("0 0 0.5rem 0")
                .fontWeight("500")
        ) + Input(
            type = type,
            modifier = Modifier()
                .attribute("id", name)
                .attribute("name", name)
                .attribute("required", "true")
                .fillMaxWidth()
                .padding("0.5rem")
                .border("1px", "solid", "#dee2e6")
                .borderRadius("4px")
                .fontSize("1rem")
        )
    }
}

@Composable
fun FormFieldTextArea(labelText: String, name: String): String {
    return Div(
        Modifier().margin("0 0 1rem 0")
    ) {
        Label(
            text = labelText,
            modifier = Modifier()
                .attribute("for", name)
                .display("block")
                .margin("0 0 0.5rem 0")
                .fontWeight("500")
        ) + TextArea(
            rows = 6,
            modifier = Modifier()
                .attribute("id", name)
                .attribute("name", name)
                .attribute("required", "true")
                .fillMaxWidth()
                .padding("0.5rem")
                .border("1px", "solid", "#dee2e6")
                .borderRadius("4px")
                .fontSize("1rem")
                .style("resize", "vertical")
        )
    }
}

@Composable
fun QuickLinksComponent(): String {
    return Div(
        Modifier()
            .margin("2rem 0")
    ) {
        Row(
            Modifier()
                .gap("1rem")
        ) {
            QuickLinkCard(
                icon = "👥",
                title = "User Management",
                description = "Manage users with full CRUD operations and interactive tables.",
                link = "/users",
                buttonText = "View Users",
                buttonColor = "#0066cc"
            ) + QuickLinkCard(
                icon = "📊",
                title = "Dashboard",
                description = "View statistics, metrics, and activity feeds.",
                link = "/dashboard",
                buttonText = "View Dashboard",
                buttonColor = "#17a2b8"
            )
        }
    }
}

@Composable
fun QuickLinkCard(
    icon: String,
    title: String,
    description: String,
    link: String,
    buttonText: String,
    buttonColor: String
): String {
    return Div(
        Modifier()
            .style("flex", "1")
    ) {
        Card {
            Text(
                text = "$icon $title",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("bold")
                    .margin("0 0 1rem 0"),
                tag = "h3"
            ) + Text(
                text = description,
                modifier = Modifier()
                    .color("#666")
                    .margin("0 0 1rem 0")
            ) + Div(
                Modifier()
                    .textAlign("left")
            ) {
                """<a href="$link" style="display: inline-block; background: $buttonColor; color: white; padding: 0.5rem 1rem; text-decoration: none; border-radius: 4px;">$buttonText</a>"""
            }
        }
    }
}

@Composable
fun NavigationComponent(): String {
    return Div(
        Modifier()
            .backgroundColor("#343a40")
            .color("white")
            .padding("1rem 0")
    ) {
        Div(
            Modifier()
                .maxWidth("1200px")
                .margin("0 auto")
                .padding("0 1rem")
                .display("flex")
                .justifyContent("space-between")
                .alignItems("center")
        ) {
            """<a href="/" style="color: white; text-decoration: none; font-weight: bold;">🚀 Summon + Spring Boot</a>""" +
            Div(
                Modifier()
                    .display("flex")
                    .gap("1rem")
            ) {
                """<a href="/" style="color: white; text-decoration: none;">Home</a>""" +
                """<a href="/users" style="color: white; text-decoration: none;">Users</a>""" +
                """<a href="/dashboard" style="color: white; text-decoration: none;">Dashboard</a>""" +
                """<a href="/contact" style="color: white; text-decoration: none;">Contact</a>"""
            }
        }
    }
}

@Composable
fun FooterComponent(): String {
    return Div(
        Modifier()
            .backgroundColor("#343a40")
            .color("white")
            .padding("2rem 0")
            .textAlign("center")
    ) {
        Div(
            Modifier()
                .maxWidth("1200px")
                .margin("0 auto")
                .padding("0 1rem")
        ) {
            Text(
                text = "© 2025 Summon Spring Boot Example. Pure Summon implementation.",
                modifier = Modifier().margin("0")
            )
        }
    }
}

@Composable
fun CurrentTimeComponent(): String {
    return Div(
        Modifier()
            .margin("2rem 0")
            .textAlign("center")
    ) {
        Card {
            Text(
                text = "🕒 Current Server Time",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("bold")
                    .margin("0 0 1rem 0"),
                tag = "h5"
            ) + Text(
                text = java.time.LocalDateTime.now().toString(),
                modifier = Modifier()
                    .fontSize("1.1rem")
                    .color("#0066cc")
            )
        }
    }
}

@Composable
fun AddUserFormComponent(): String {
    return Div(
        Modifier()
            .margin("0 0 2rem 0")
    ) {
        Card {
            Text(
                text = "Add New User",
                modifier = Modifier()
                    .fontSize("1.25rem")
                    .fontWeight("bold")
                    .margin("0 0 1.5rem 0"),
                tag = "h5"
            ) + Form(
                action = "/users",
                method = "POST"
            ) {
                Row(
                    Modifier().gap("1rem")
                ) {
                    Div(
                        Modifier().style("flex", "1")
                    ) {
                        FormField("Name", "name", "text")
                    } + Div(
                        Modifier().style("flex", "1")
                    ) {
                        FormField("Email", "email", "email")
                    } + Div(
                        Modifier().style("flex", "1")
                    ) {
                        FormFieldSelect("Role", "role", listOf(
                            "" to "Choose role...",
                            "admin" to "Admin",
                            "editor" to "Editor", 
                            "moderator" to "Moderator",
                            "user" to "User"
                        ))
                    }
                } + Div(
                    Modifier().margin("1rem 0 0 0")
                ) {
                    Button(
                        text = "Add User",
                        modifier = Modifier()
                            .attribute("type", "submit")
                            .backgroundColor("#0066cc")
                            .color("white")
                            .border("none", "solid", "transparent")
                            .borderRadius("4px")
                            .padding("0.5rem 1rem")
                            .cursor("pointer")
                            .margin("0 0.5rem 0 0")
                    ) + Button(
                        text = "Refresh Table", 
                        modifier = Modifier()
                            .attribute("type", "button")
                            .backgroundColor("#6c757d")
                            .color("white")
                            .border("none", "solid", "transparent")
                            .borderRadius("4px")
                            .padding("0.5rem 1rem")
                            .cursor("pointer")
                            .onClick("location.reload()")
                    )
                }
            }
        }
    }
}

@Composable
fun FormFieldSelect(labelText: String, name: String, options: List<Pair<String, String>>): String {
    return Div(
        Modifier().margin("0 0 1rem 0")
    ) {
        Label(
            text = labelText,
            modifier = Modifier()
                .attribute("for", name)
                .display("block")
                .margin("0 0 0.5rem 0")
                .fontWeight("500")
        ) + Div {
            """<select id="$name" name="$name" required style="width: 100%; padding: 0.5rem; border: 1px solid #dee2e6; border-radius: 4px; font-size: 1rem;">""" +
            options.joinToString("") { (value, text) ->
                """<option value="$value">$text</option>"""
            } + """</select>"""
        }
    }
}

@Composable
fun UserStatisticsComponent(users: List<User>): String {
    val totalUsers = users.size
    val activeUsers = users.count { it.active }
    val adminUsers = users.count { it.role == "admin" }
    val regularUsers = users.count { it.role == "user" }
    
    return Div(
        Modifier()
            .margin("2rem 0")
    ) {
        Text(
            text = "User Statistics",
            modifier = Modifier()
                .fontSize("1.5rem")
                .fontWeight("bold")
                .margin("0 0 1rem 0"),
            tag = "h3"
        ) + Row(
            Modifier()
                .gap("1rem")
        ) {
            UserStatCard("Total Users", totalUsers.toString(), "#3498db") +
            UserStatCard("Active Users", activeUsers.toString(), "#2ecc71") +
            UserStatCard("Admins", adminUsers.toString(), "#f39c12") +
            UserStatCard("Regular Users", regularUsers.toString(), "#17a2b8")
        }
    }
}

@Composable
fun UserStatCard(title: String, value: String, color: String): String {
    return Div(
        Modifier()
            .style("flex", "1")
    ) {
        Card(
            Modifier()
                .textAlign("center")
                .style("border-left", "4px solid $color")
        ) {
            Text(
                text = title,
                modifier = Modifier()
                    .fontSize("1rem")
                    .margin("0 0 0.5rem 0"),
                tag = "h5"
            ) + Text(
                text = value,
                modifier = Modifier()
                    .fontSize("2rem")
                    .fontWeight("bold")
                    .color(color)
            )
        }
    }
}

@Composable
fun UserPageHeaderComponent(): String {
    return Div(
        Modifier().margin("0 0 2rem 0")
    ) {
        Text(
            text = "👥 User Management",
            modifier = Modifier()
                .fontSize("2rem")
                .fontWeight("bold"),
            tag = "h1"
        )
    }
}