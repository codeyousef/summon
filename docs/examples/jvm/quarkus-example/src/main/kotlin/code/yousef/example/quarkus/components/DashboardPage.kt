package code.yousef.example.quarkus.components

// Import utils individually to avoid ambiguity
// Import specific modifier functions to avoid ambiguity
import code.yousef.example.quarkus.utils.*
import code.yousef.example.quarkus.utils.boxShadow
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.alignItems
import code.yousef.summon.modifier.attribute
import code.yousef.summon.modifier.borderBottom
import code.yousef.summon.modifier.display
import code.yousef.summon.modifier.flex
import code.yousef.summon.modifier.flexWrap
import code.yousef.summon.modifier.gap
import code.yousef.summon.modifier.justifyContent
import code.yousef.summon.modifier.minHeight
import code.yousef.summon.modifier.minWidth
import code.yousef.summon.modifier.overflow
import code.yousef.summon.routing.seo.Heading
import code.yousef.summon.runtime.Composable

/**
 * Dashboard page component for displaying key metrics and user stats
 */
@Composable
fun DashboardPage() {
    Box(
        modifier = Modifier()
            .width("100%")
            .minHeight("100vh")
            .backgroundColor("#f8f9fa")
    ) {
        NavBar()

        Column(
            modifier = Modifier()
                .width("100%")
                .maxWidth("1200px")
                .marginVH("30px", "auto")
                .paddingVH("0", "20px")
        ) {
            // Dashboard header
            Row(
                modifier = Modifier()
                    .width("100%")
                    .marginBottom("30px")
                    .justifyContent("space-between")
                    .alignItems("center")
            ) {
                Column {
                    Heading(
                        level = 1,
                        modifier = Modifier()
                            .fontSize("28px")
                            .fontWeight("600")
                            .marginBottom("8px")
                    ) {
                        Text(text = "Dashboard")
                    }
                    Text(
                        text = "Welcome back, Admin",
                        modifier = Modifier()
                            .fontSize("16px")
                            .color("#6c757d")
                    )
                }

                Button(
                    onClick = { /* Action for new report */ },
                    label = "New Report",
                    modifier = Modifier()
                        .paddingVH("8px", "16px")
                )
            }

            // Stats overview
            Row(
                modifier = Modifier()
                    .width("100%")
                    .display("flex")
                    .flexWrap("wrap")
                    .gap("20px")
                    .marginBottom("30px")
            ) {
                StatCard(
                    title = "Total Users",
                    value = "2,543",
                    change = "+12.4%",
                    positive = true,
                    icon = "users"
                )
                StatCard(
                    title = "Total Revenue",
                    value = "$45,217",
                    change = "+8.7%",
                    positive = true,
                    icon = "dollar"
                )
                StatCard(
                    title = "Pending Orders",
                    value = "17",
                    change = "-2.3%",
                    positive = false,
                    icon = "cart"
                )
                StatCard(
                    title = "Conversion Rate",
                    value = "3.8%",
                    change = "+0.6%",
                    positive = true,
                    icon = "chart"
                )
            }

            // Recent activity section
            Box(
                modifier = Modifier()
                    .width("100%")
                    .marginBottom("30px")
                    .backgroundColor("white")
                    .borderRadius("8px")
                    .boxShadow("0 1px 3px rgba(0,0,0,0.1)")
                    .overflow("hidden")
            ) {
                Column(
                    modifier = Modifier()
                        .width("100%")
                ) {
                    // Section header
                    Box(
                        modifier = Modifier()
                            .width("100%")
                            .paddingVH("16px", "20px")
                            .borderBottom("1px solid #e9ecef")
                    ) {
                        Heading(
                            level = 2,
                            modifier = Modifier()
                                .fontSize("18px")
                                .fontWeight("600")
                                .margin("0")
                        ) {
                            Text(text = "Recent Activity")
                        }
                    }

                    // Activity items
                    Column(
                        modifier = Modifier()
                            .width("100%")
                            .padding("10px")
                    ) {
                        ActivityItem(
                            title = "New user registered",
                            time = "5 minutes ago",
                            description = "John Doe created a new account"
                        )
                        ActivityItem(
                            title = "Order completed",
                            time = "1 hour ago",
                            description = "Order #1234 has been processed and shipped"
                        )
                        ActivityItem(
                            title = "Payment received",
                            time = "3 hours ago",
                            description = "Payment of $1,250 received from customer #5678"
                        )
                        ActivityItem(
                            title = "New feature added",
                            time = "Yesterday",
                            description = "The team deployed the new reporting dashboard",
                            last = true
                        )
                    }
                }
            }

            // Charts section
            Row(
                modifier = Modifier()
                    .width("100%")
                    .display("flex")
                    .flexWrap("wrap")
                    .gap("20px")
                    .marginBottom("30px")
            ) {
                // Sales chart
                Box(
                    modifier = Modifier()
                        .flex("1")
                        .minWidth("300px")
                        .backgroundColor("white")
                        .borderRadius("8px")
                        .boxShadow("0 1px 3px rgba(0,0,0,0.1)")
                        .overflow("hidden")
                ) {
                    Column(
                        modifier = Modifier()
                            .width("100%")
                    ) {
                        Box(
                            modifier = Modifier()
                                .width("100%")
                                .paddingVH("16px", "20px")
                                .borderBottom("1px solid #e9ecef")
                        ) {
                            Heading(
                                level = 3,
                                modifier = Modifier()
                                    .fontSize("18px")
                                    .fontWeight("600")
                                    .margin("0")
                            ) {
                                Text(text = "Sales Overview")
                            }
                        }

                        Box(
                            modifier = Modifier()
                                .width("100%")
                                .height("300px")
                                .padding("20px")
                        ) {
                            Box(
                                modifier = Modifier()
                                    .width("100%")
                                    .height("100%")
                                    .attribute("id", "sales-chart")
                            ) {
                                // Empty content but adding the lambda block
                            }
                        }
                    }
                }

                // Traffic chart
                Box(
                    modifier = Modifier()
                        .flex("1")
                        .minWidth("300px")
                        .backgroundColor("white")
                        .borderRadius("8px")
                        .boxShadow("0 1px 3px rgba(0,0,0,0.1)")
                        .overflow("hidden")
                ) {
                    Column(
                        modifier = Modifier()
                            .width("100%")
                    ) {
                        Box(
                            modifier = Modifier()
                                .width("100%")
                                .paddingVH("16px", "20px")
                                .borderBottom("1px solid #e9ecef")
                        ) {
                            Heading(
                                level = 3,
                                modifier = Modifier()
                                    .fontSize("18px")
                                    .fontWeight("600")
                                    .margin("0")
                            ) {
                                Text(text = "Traffic Sources")
                            }
                        }

                        Box(
                            modifier = Modifier()
                                .width("100%")
                                .height("300px")
                                .padding("20px")
                        ) {
                            Box(
                                modifier = Modifier()
                                    .width("100%")
                                    .height("100%")
                                    .attribute("id", "traffic-chart")
                            ) {
                                // Empty content but adding the lambda block
                            }
                        }
                    }
                }
            }
        }

        SimpleFooter()
    }
}

/**
 * A simple footer implementation
 */
@Composable
fun SimpleFooter() {
    Box(
        modifier = Modifier()
            .width("100%")
            .backgroundColor("#343a40")
            .paddingVH("20px", "0")
            .color("#fff")
    ) {
        Text(
            text = "© 2023 Quarkus + Summon Demo",
            modifier = Modifier()
                .textAlign("center")
                .padding("10px")
        )
    }
}

/**
 * Statistics card component displaying an icon, title, and value.
 */
@Composable
private fun StatCard(
    title: String,
    value: String,
    change: String,
    positive: Boolean,
    icon: String
) {
    Box(
        modifier = Modifier().flex("1")
            .minWidth("220px")
            .backgroundColor("#fff")
            .borderRadius("8px")
            .boxShadow("0 4px 6px rgba(0, 0, 0, 0.05)")
            .padding(all = "24px")
            .marginBottom("16px")
            .hover(
                boxShadow = "0 6px 12px rgba(0, 0, 0, 0.1)",
                transform = "translateY(-2px)"
            )
    ) {
        Row(
            modifier = Modifier().justifyContent("space-between")
                .alignItems("center")
        ) {
            Column(
                modifier = Modifier().gap("8px")
            ) {
                Text(
                    text = title,
                    modifier = Modifier().fontSize("14px")
                        .color("#6c757d")
                )

                Text(
                    text = value,
                    modifier = Modifier().fontSize("28px")
                        .fontWeight("bold")
                )

                Row(
                    modifier = Modifier().alignItems("center")
                        .gap("4px")
                ) {
                    Text(
                        text = if (positive) "↑" else "↓",
                        modifier = Modifier().fontSize("14px")
                            .color(if (positive) "#198754" else "#dc3545")
                    )

                    Text(
                        text = change,
                        modifier = Modifier().fontSize("14px")
                            .color(if (positive) "#198754" else "#dc3545")
                    )
                }
            }

            // Icon
            Text(
                text = icon,
                modifier = Modifier().fontSize("32px")
                    .backgroundColor("#0d6efd15") // 15 is alpha
                    .color("#0d6efd")
                    .padding(all = "12px")
                    .borderRadius("50%")
            )
        }
    }
}

@Composable
private fun ActivityItem(title: String, time: String, description: String, last: Boolean = false) {
    Row(
        modifier = Modifier().paddingVH(vertical = "12px", horizontal = "8px")
            .borderBottomCustom("1px", "solid", "#e9ecef")
            .alignItems("center")
            .gap("16px")
    ) {
        // Activity icon based on type
        Box(
            modifier = Modifier().width("40px")
                .height("40px")
                .borderRadius("50%")
                .backgroundColor(
                    when (title) {
                        "New user registered" -> "#0d6efd15"
                        "Order completed" -> "#19875415"
                        "Payment received" -> "#fd7e1415"
                        "New feature added" -> "#dc354515"
                        else -> "#6c757d15"
                    }
                )
                .display("flex")
                .justifyContent("center")
                .alignItems("center")
        ) {
            Text(
                text = when (title) {
                    "New user registered" -> "👤"
                    "Order completed" -> "✅"
                    "Payment received" -> "$"
                    "New feature added" -> "🚀"
                    else -> "⚙️"
                }
            )
        }

        // Activity details
        Column(
            modifier = Modifier().flex("1")
        ) {
            Text(
                text = description,
                modifier = Modifier().fontSize("14px")
                    .color("#212529")
            )

            Text(
                text = time,
                modifier = Modifier().fontSize("12px")
                    .color("#6c757d")
            )
        }
    }
} 