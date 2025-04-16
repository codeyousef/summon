package code.yousef.example.quarkus.components

import code.yousef.example.quarkus.User
import code.yousef.example.quarkus.utils.boxShadow
import code.yousef.example.quarkus.utils.hover
import code.yousef.example.quarkus.utils.marginVH
import code.yousef.example.quarkus.utils.paddingVH
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.*
import code.yousef.summon.runtime.Composable


@Composable
fun HomePage(currentUser: User? = null) {
    ThemeWrapper {
        // Add navbar
        NavBar(currentUser)

        // Hero section
        Box(
            modifier = Modifier().fillMaxWidth()
                .backgroundColor("#007bff")
                .paddingVH(vertical = "80px", horizontal = "24px")
                .color("#fff")
        ) {
            Column(
                modifier = Modifier().fillMaxWidth()
                    .maxWidth("1200px")
                    .marginVH(vertical = "0", horizontal = "auto")
                    .textAlign("center")
            ) {
                Text(
                    text = "Welcome to Summon with Quarkus",
                    modifier = Modifier().fontSize("42px")
                        .fontWeight("bold")
                        .marginBottom("16px")
                )

                Text(
                    text = "A modern full-stack Kotlin application framework for building web applications",
                    modifier = Modifier().fontSize("20px")
                        .maxWidth("700px")
                        .marginVH(vertical = "0", horizontal = "auto")
                        .marginBottom("32px")
                        .opacity("0.9")
                )

                if (currentUser == null) {
                    Row(
                        modifier = Modifier().justifyContent("center")
                            .gap("16px")
                    ) {
                        Button(
                            onClick = {},
                            label = "Get Started",
                            modifier = Modifier().paddingVH(vertical = "12px", horizontal = "24px")
                                .backgroundColor("#fff")
                                .color("#007bff")
                                .fontWeight("medium")
                                .fontSize("16px")
                                .borderRadius("4px")
                                .border("none", "", "")
                                .attribute("hx-get", "/register")
                                .attribute("hx-target", "body")
                                .attribute("hx-swap", "innerHTML")
                        )

                        Button(
                            onClick = {},
                            label = "Log In",
                            modifier = Modifier().paddingVH(vertical = "12px", horizontal = "24px")
                                .backgroundColor("transparent")
                                .color("#fff")
                                .fontWeight("medium")
                                .fontSize("16px")
                                .border("1px", "solid", "#fff")
                                .borderRadius("4px")
                                .attribute("hx-get", "/login")
                                .attribute("hx-target", "body")
                                .attribute("hx-swap", "innerHTML")
                        )
                    }
                } else {
                    Button(
                        onClick = {},
                        label = "Go to Dashboard",
                        modifier = Modifier().paddingVH(vertical = "12px", horizontal = "24px")
                            .backgroundColor("#fff")
                            .color("#007bff")
                            .fontWeight("medium")
                            .fontSize("16px")
                            .borderRadius("4px")
                            .border("none", "", "")
                            .attribute("hx-get", "/dashboard")
                            .attribute("hx-target", "body")
                            .attribute("hx-swap", "innerHTML")
                    )
                }
            }
        }

        // Features section
        Box(
            modifier = Modifier().fillMaxWidth()
                .paddingVH(vertical = "80px", horizontal = "24px")
                .backgroundColor("#fff")
        ) {
            Column(
                modifier = Modifier().maxWidth("1200px")
                    .marginVH(vertical = "0", horizontal = "auto")
            ) {
                Text(
                    text = "Key Features",
                    modifier = Modifier().fontSize("32px")
                        .fontWeight("bold")
                        .marginBottom("48px")
                        .textAlign("center")
                )

                Row(
                    modifier = Modifier().fillMaxWidth()
                        .gap("32px")
                        .flexWrap("wrap")
                        .justifyContent("center")
                ) {
                    // Feature cards
                    FeatureCard(
                        icon = "🚀",
                        title = "Fast & Lightweight",
                        description = "Built with Kotlin and optimized for performance. Minimal overhead for maximum speed."
                    )

                    FeatureCard(
                        icon = "🔄",
                        title = "HTMX Integration",
                        description = "Seamless integration with HTMX for dynamic content without writing JavaScript."
                    )

                    FeatureCard(
                        icon = "📱",
                        title = "Responsive Design",
                        description = "Mobile-first design ensures your application looks great on all devices."
                    )

                    FeatureCard(
                        icon = "🛠️",
                        title = "Developer Friendly",
                        description = "Intuitive APIs and comprehensive documentation make development a breeze."
                    )

                    FeatureCard(
                        icon = "🔒",
                        title = "Secure by Default",
                        description = "Built with security best practices to protect your application and users."
                    )

                    FeatureCard(
                        icon = "⚡",
                        title = "Reactive",
                        description = "Built on reactive principles for responsive and resilient applications."
                    )
                }
            }
        }

        // CTA Section
        Box(
            modifier = Modifier().fillMaxWidth()
                .paddingVH(vertical = "80px", horizontal = "24px")
                .backgroundColor("#f8f9fa")
        ) {
            Column(
                modifier = Modifier().maxWidth("1000px")
                    .marginVH(vertical = "0", horizontal = "auto")
                    .textAlign("center")
            ) {
                Text(
                    text = "Ready to build your next project?",
                    modifier = Modifier().fontSize("32px")
                        .fontWeight("bold")
                        .marginBottom("24px")
                )

                Text(
                    text = "Experience the power and simplicity of Kotlin with Summon and Quarkus.",
                    modifier = Modifier().fontSize("18px")
                        .color("#6c757d")
                        .maxWidth("700px")
                        .marginVH(vertical = "0", horizontal = "auto")
                        .marginBottom("32px")
                )

                Button(
                    onClick = {},
                    label = "Get Started",
                    modifier = Modifier().paddingVH(vertical = "14px", horizontal = "32px")
                        .backgroundColor("#007bff")
                        .color("#fff")
                        .fontWeight("medium")
                        .fontSize("16px")
                        .borderRadius("4px")
                        .border("none", "", "")
                        .cursor("pointer")
                        .attribute("hx-get", "/register")
                        .attribute("hx-target", "body")
                        .attribute("hx-swap", "innerHTML")
                )
            }
        }

        // Footer
        Footer()
    }
}

@Composable
private fun FeatureCard(icon: String, title: String, description: String) {
    Box(
        modifier = Modifier().flex("1")
            .minWidth("300px")
            .maxWidth("350px")
            .padding("24px")
            .backgroundColor("#fff")
            .borderRadius("8px")
            .boxShadow("0 4px 6px rgba(0, 0, 0, 0.05)")
            .marginBottom("24px")
    ) {
        Column(
            modifier = Modifier().gap("16px")
        ) {
            Text(
                text = icon,
                modifier = Modifier().fontSize("48px")
                    .marginBottom("8px")
            )

            Text(
                text = title,
                modifier = Modifier().fontSize("20px")
                    .fontWeight("bold")
                    .color("#212529")
                    .marginBottom("8px")
            )

            Text(
                text = description,
                modifier = Modifier().fontSize("16px")
                    .color("#6c757d")
                    .lineHeight("1.5")
            )
        }
    }
}

@Composable
private fun Footer() {
    Box(
        modifier = Modifier().fillMaxWidth()
            .padding("24px")
            .backgroundColor("#343a40")
            .color("#fff")
    ) {
        Row(
            modifier = Modifier().maxWidth("1200px")
                .marginVH(vertical = "0", horizontal = "auto")
                .justifyContent("space-between")
                .alignItems("center")
                .flexWrap("wrap")
                .gap("16px")
        ) {
            Text(
                text = "© 2023 Summon + Quarkus Demo",
                modifier = Modifier().color("#adb5bd")
            )

            Row(
                modifier = Modifier().gap("24px")
            ) {
                Text(
                    text = "About",
                    modifier = Modifier().color("#adb5bd")
                        .hover(
                            color = "#fff"
                        )
                        .cursor("pointer")
                )

                Text(
                    text = "Features",
                    modifier = Modifier().color("#adb5bd")
                        .hover(
                            color = "#fff"
                        )
                        .cursor("pointer")
                )

                Text(
                    text = "Contact",
                    modifier = Modifier().color("#adb5bd")
                        .hover(
                            color = "#fff"
                        )
                        .cursor("pointer")
                        .attribute("hx-get", "/contact")
                        .attribute("hx-target", "body")
                        .attribute("hx-swap", "innerHTML")
                )
            }
        }
    }
} 