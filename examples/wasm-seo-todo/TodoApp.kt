package code.yousef.summon.examples.wasm.todo

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.core.ThemeProvider
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.feedback.Alert
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.input.Checkbox
import code.yousef.summon.components.input.FormField
import code.yousef.summon.components.input.TextField
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.components.layout.Spacer
import code.yousef.summon.components.style.GlobalStyle
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.remember
import code.yousef.summon.runtime.mutableStateOf
import code.yousef.summon.seo.*
import code.yousef.summon.state.getValue
import code.yousef.summon.state.setValue
import code.yousef.summon.theme.AppTheme
import code.yousef.summon.theme.MaterialTheme

/**
 * Data model for a Todo item.
 */
data class Todo(
    val id: Int,
    val text: String,
    val completed: Boolean = false
)

/**
 * Main Todo application with comprehensive SEO optimization.
 * Demonstrates the new Kotlin-idiomatic SEO API.
 */
@Composable
fun SEOTodoApp() {
    // SEO metadata for the application
    WebAppSEO(
        name = "Summon Todo App",
        description = "A modern, accessible todo list application built with Kotlin Multiplatform and WebAssembly. Manage your tasks efficiently with our lightweight, fast-loading app.",
        url = "https://example.com/todo",
        category = "Productivity",
        author = "Summon Framework Team",
        siteName = "Summon Examples",
        themeColor = "#1976D2"
    )

    // Add structured data for the web application
    val appSchema = WebApplicationSchema(
        name = "Summon Todo App",
        description = "Task management application with WebAssembly support",
        url = "https://example.com/todo",
        applicationCategory = "TaskManagement",
        operatingSystem = "Web Browser",
        offers = WebApplicationSchema.Offer(
            price = "0",
            priceCurrency = "USD",
            availability = "https://schema.org/InStock"
        ),
        aggregateRating = WebApplicationSchema.AggregateRating(
            ratingValue = 4.8,
            ratingCount = 1250
        ),
        author = OrganizationSchema(
            name = "Summon Framework",
            url = "https://github.com/codeyousef/summon"
        )
    )

    StructuredData(appSchema)

    // FAQ structured data for better search results
    val faqSchema = FAQPageSchema(
        questions = listOf(
            FAQPageSchema.QuestionAnswer(
                question = "What is the Summon Todo App?",
                answer = "The Summon Todo App is a demonstration of the Summon Kotlin Multiplatform framework's capabilities for building modern web applications with WebAssembly support."
            ),
            FAQPageSchema.QuestionAnswer(
                question = "Is the app free to use?",
                answer = "Yes, the Summon Todo App is completely free and open source."
            ),
            FAQPageSchema.QuestionAnswer(
                question = "Does it work offline?",
                answer = "Yes, once loaded, the app works entirely offline as it runs in WebAssembly."
            )
        )
    )

    StructuredData(faqSchema)

    // Breadcrumb navigation for better site structure
    val breadcrumbSchema = BreadcrumbListSchema(
        items = listOf(
            BreadcrumbListSchema.BreadcrumbItem(
                name = "Home",
                url = "https://example.com"
            ),
            BreadcrumbListSchema.BreadcrumbItem(
                name = "Examples",
                url = "https://example.com/examples"
            ),
            BreadcrumbListSchema.BreadcrumbItem(
                name = "Todo App",
                url = "https://example.com/todo"
            )
        )
    )

    StructuredData(breadcrumbSchema)

    // Global styles for the app
    GlobalStyle(
        """
        * {
            box-sizing: border-box;
        }
        
        body {
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        
        .todo-item {
            transition: all 0.3s ease;
        }
        
        .todo-item:hover {
            transform: translateX(4px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }
        
        .completed {
            opacity: 0.6;
            text-decoration: line-through;
        }
        
        @media (prefers-reduced-motion: reduce) {
            .todo-item {
                transition: none;
            }
        }
    """
    )

    // Main app content
    ThemeProvider(theme = MaterialTheme) {
        TodoAppContent()
    }
}

/**
 * The main content of the Todo application.
 */
@Composable
private fun TodoAppContent() {
    var todos by remember { mutableStateOf(listOf<Todo>()) }
    var newTodoText by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(TodoFilter.ALL) }
    var nextId by remember { mutableStateOf(1) }

    // Calculate filtered todos
    val filteredTodos = when (filter) {
        TodoFilter.ALL -> todos
        TodoFilter.ACTIVE -> todos.filter { !it.completed }
        TodoFilter.COMPLETED -> todos.filter { it.completed }
    }

    // Calculate stats
    val totalTodos = todos.size
    val completedTodos = todos.count { it.completed }
    val activeTodos = totalTodos - completedTodos

    Column(
        modifier = Modifier()
            .width(Width.FULL)
            .minHeight("100vh")
            .padding(Spacing.LG)
    ) {
        // App container with max width for better readability
        Box(
            modifier = Modifier()
                .width(Width.FULL)
                .maxWidth(MaxWidth.LG)
                .margin(Margin.AUTO)
        ) {
            Card(
                modifier = Modifier()
                    .width(Width.FULL)
                    .backgroundColor("white")
                    .borderRadius("12px")
                    .boxShadow("0 10px 40px rgba(0,0,0,0.1)")
                    .padding(Spacing.XL)
            ) {
                // Header
                Column(modifier = Modifier().marginBottom(Spacing.LG)) {
                    Text(
                        text = "✨ Todo List",
                        modifier = Modifier()
                            .fontSize("2.5rem")
                            .fontWeight("700")
                            .color("#2c3e50")
                            .marginBottom(Spacing.XS)
                    )

                    Text(
                        text = "Manage your tasks efficiently",
                        modifier = Modifier()
                            .fontSize("1.1rem")
                            .color("#7f8c8d")
                    )
                }

                // Stats section
                if (todos.isNotEmpty()) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .marginBottom(Spacing.MD)
                            .gap(Spacing.MD)
                    ) {
                        StatCard(
                            label = "Total",
                            value = totalTodos.toString(),
                            color = "#3498db"
                        )
                        StatCard(
                            label = "Active",
                            value = activeTodos.toString(),
                            color = "#f39c12"
                        )
                        StatCard(
                            label = "Completed",
                            value = completedTodos.toString(),
                            color = "#27ae60"
                        )
                    }
                }

                // Add todo form
                FormField(
                    modifier = Modifier().marginBottom(Spacing.LG)
                ) {
                    Row(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .gap(Spacing.SM)
                    ) {
                        TextField(
                            value = newTodoText,
                            onValueChange = { newTodoText = it },
                            modifier = Modifier()
                                .flex("1")
                                .padding("12px 16px")
                                .fontSize("1rem")
                                .borderRadius("8px")
                                .border("2px solid #e1e4e8")
                                .focus { border("2px solid #3498db") }
                                .attr("placeholder", "What needs to be done?")
                                .attr("aria-label", "New todo input")
                        )

                        Button(
                            onClick = {
                                if (newTodoText.isNotBlank()) {
                                    todos = todos + Todo(
                                        id = nextId++,
                                        text = newTodoText.trim(),
                                        completed = false
                                    )
                                    newTodoText = ""
                                }
                            },
                            modifier = Modifier()
                                .padding("12px 24px")
                                .backgroundColor("#3498db")
                                .color("white")
                                .borderRadius("8px")
                                .fontWeight("600")
                                .hover { backgroundColor("#2980b9") }
                                .attr("aria-label", "Add todo")
                        ) {
                            Text("Add")
                        }
                    }
                }

                // Filter tabs
                if (todos.isNotEmpty()) {
                    FilterTabs(
                        currentFilter = filter,
                        onFilterChange = { filter = it },
                        modifier = Modifier().marginBottom(Spacing.MD)
                    )
                }

                // Todo list
                if (filteredTodos.isEmpty()) {
                    EmptyState(
                        message = when (filter) {
                            TodoFilter.ALL -> "No todos yet. Add one above!"
                            TodoFilter.ACTIVE -> "No active todos."
                            TodoFilter.COMPLETED -> "No completed todos."
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .gap(Spacing.SM)
                    ) {
                        filteredTodos.forEach { todo ->
                            TodoItem(
                                todo = todo,
                                onToggle = {
                                    todos = todos.map { t ->
                                        if (t.id == todo.id) {
                                            t.copy(completed = !t.completed)
                                        } else t
                                    }
                                },
                                onDelete = {
                                    todos = todos.filter { it.id != todo.id }
                                }
                            )
                        }
                    }
                }

                // Clear completed button
                if (completedTodos > 0) {
                    Box(
                        modifier = Modifier()
                            .width(Width.FULL)
                            .marginTop(Spacing.LG)
                            .textAlign("center")
                    ) {
                        Button(
                            onClick = {
                                todos = todos.filter { !it.completed }
                            },
                            modifier = Modifier()
                                .padding("10px 20px")
                                .backgroundColor("#e74c3c")
                                .color("white")
                                .borderRadius("6px")
                                .hover { backgroundColor("#c0392b") }
                        ) {
                            Text("Clear completed ($completedTodos)")
                        }
                    }
                }
            }
        }

        // Footer with SEO-friendly links
        Footer()
    }
}

/**
 * Todo filter options.
 */
enum class TodoFilter {
    ALL, ACTIVE, COMPLETED
}

/**
 * Filter tabs component.
 */
@Composable
private fun FilterTabs(
    currentFilter: TodoFilter,
    onFilterChange: (TodoFilter) -> Unit,
    modifier: Modifier = Modifier()
) {
    Row(
        modifier = modifier
            .width(Width.FULL)
            .padding("8px")
            .backgroundColor("#f8f9fa")
            .borderRadius("8px")
            .gap("4px")
    ) {
        TodoFilter.values().forEach { filter ->
            Button(
                onClick = { onFilterChange(filter) },
                modifier = Modifier()
                    .flex("1")
                    .padding("8px 16px")
                    .backgroundColor(
                        if (filter == currentFilter) "#3498db" else "transparent"
                    )
                    .color(
                        if (filter == currentFilter) "white" else "#6c757d"
                    )
                    .borderRadius("6px")
                    .fontWeight(if (filter == currentFilter) "600" else "400")
                    .hover {
                        if (filter != currentFilter) {
                            backgroundColor("#e9ecef")
                        }
                    }
            ) {
                Text(filter.name.lowercase().capitalize())
            }
        }
    }
}

/**
 * Individual todo item component.
 */
@Composable
private fun TodoItem(
    todo: Todo,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier()
            .width(Width.FULL)
            .padding("12px 16px")
            .backgroundColor(if (todo.completed) "#f8f9fa" else "white")
            .border("1px solid #e1e4e8")
            .borderRadius("8px")
            .className("todo-item")
    ) {
        Row(
            modifier = Modifier()
                .width(Width.FULL)
                .alignItems(AlignItems.CENTER)
                .gap(Spacing.SM)
        ) {
            Checkbox(
                checked = todo.completed,
                onCheckedChange = { onToggle() },
                modifier = Modifier()
                    .size("20px")
                    .attr("aria-label", "Toggle todo completion")
            )

            Text(
                text = todo.text,
                modifier = Modifier()
                    .flex("1")
                    .fontSize("1rem")
                    .color(if (todo.completed) "#6c757d" else "#2c3e50")
                    .className(if (todo.completed) "completed" else "")
            )

            Button(
                onClick = onDelete,
                modifier = Modifier()
                    .padding("6px 12px")
                    .backgroundColor("#dc3545")
                    .color("white")
                    .borderRadius("4px")
                    .fontSize("0.875rem")
                    .hover { backgroundColor("#c82333") }
                    .attr("aria-label", "Delete todo")
            ) {
                Text("✕")
            }
        }
    }
}

/**
 * Stat card component for displaying metrics.
 */
@Composable
private fun StatCard(
    label: String,
    value: String,
    color: String
) {
    Card(
        modifier = Modifier()
            .flex("1")
            .padding("16px")
            .backgroundColor("#f8f9fa")
            .borderRadius("8px")
            .textAlign("center")
    ) {
        Text(
            text = value,
            modifier = Modifier()
                .fontSize("2rem")
                .fontWeight("700")
                .color(color)
                .marginBottom("4px")
        )
        Text(
            text = label,
            modifier = Modifier()
                .fontSize("0.875rem")
                .color("#6c757d")
                .textTransform("uppercase")
                .letterSpacing("0.5px")
        )
    }
}

/**
 * Empty state component.
 */
@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier()
            .width(Width.FULL)
            .padding("48px")
            .textAlign("center")
    ) {
        Column(
            modifier = Modifier()
                .width(Width.FULL)
                .alignItems(AlignItems.CENTER)
        ) {
            Text(
                text = "📋",
                modifier = Modifier()
                    .fontSize("3rem")
                    .marginBottom(Spacing.MD)
            )
            Text(
                text = message,
                modifier = Modifier()
                    .fontSize("1.1rem")
                    .color("#6c757d")
            )
        }
    }
}

/**
 * Footer component with SEO-friendly links.
 */
@Composable
private fun Footer() {
    Box(
        modifier = Modifier()
            .width(Width.FULL)
            .maxWidth(MaxWidth.LG)
            .margin(Margin.AUTO)
            .marginTop(Spacing.XL)
            .padding(Spacing.MD)
            .textAlign("center")
    ) {
        Column(
            modifier = Modifier()
                .width(Width.FULL)
                .gap(Spacing.SM)
        ) {
            Text(
                text = "Built with Summon Framework",
                modifier = Modifier()
                    .color("rgba(255, 255, 255, 0.9)")
                    .fontSize("0.875rem")
            )

            Row(
                modifier = Modifier()
                    .width(Width.FULL)
                    .justifyContent(JustifyContent.CENTER)
                    .gap(Spacing.MD)
            ) {
                FooterLink(
                    text = "GitHub",
                    href = "https://github.com/codeyousef/summon"
                )
                FooterLink(
                    text = "Documentation",
                    href = "https://summon.dev/docs"
                )
                FooterLink(
                    text = "Examples",
                    href = "https://summon.dev/examples"
                )
            }
        }
    }
}

/**
 * Footer link component.
 */
@Composable
private fun FooterLink(
    text: String,
    href: String
) {
    // Note: Link component would be used here in a real app
    Text(
        text = text,
        modifier = Modifier()
            .color("rgba(255, 255, 255, 0.8)")
            .fontSize("0.875rem")
            .textDecoration("underline")
            .hover {
                color("white")
            }
            .attr("href", href)
            .attr("target", "_blank")
            .attr("rel", "noopener noreferrer")
    )
}

// Design system constants
object Spacing {
    const val XS = "4px"
    const val SM = "8px"
    const val MD = "16px"
    const val LG = "24px"
    const val XL = "32px"
    const val XXL = "48px"
}

object MaxWidth {
    const val SM = "640px"
    const val MD = "768px"
    const val LG = "1024px"
    const val XL = "1280px"
}

object Width {
    const val FULL = "100%"
    const val HALF = "50%"
}

object Margin {
    const val AUTO = "auto"
}

object AlignItems {
    const val CENTER = "center"
    const val START = "flex-start"
    const val END = "flex-end"
}

object JustifyContent {
    const val CENTER = "center"
    const val SPACE_BETWEEN = "space-between"
    const val SPACE_AROUND = "space-around"
}