package code.yousef.summon.examples.js

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.feedback.Alert
import code.yousef.summon.components.feedback.AlertVariant
import code.yousef.summon.components.input.*
import code.yousef.summon.components.layout.*
import code.yousef.summon.components.navigation.Link
import code.yousef.summon.extensions.px
import code.yousef.summon.modifier.AlignContent
import code.yousef.summon.modifier.AlignItems
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifiers.boxShadow
import code.yousef.summon.modifier.StylingModifiers.fontWeight
import code.yousef.summon.modifier.StylingModifiers.lineHeight
import code.yousef.summon.modifier.StylingModifiers.textAlign
import code.yousef.summon.modifier.StylingModifiers.transform
import code.yousef.summon.modifier.StylingModifiers.transition
import code.yousef.summon.modifier.alignContent
import code.yousef.summon.modifier.alignItems
import code.yousef.summon.modifier.backgroundClip
import code.yousef.summon.modifier.backgroundImage
import code.yousef.summon.modifier.borderBottomWidth
import code.yousef.summon.modifier.borderColor
import code.yousef.summon.modifier.fillMaxWidth
import code.yousef.summon.modifier.flexWrap
import code.yousef.summon.modifier.gap
import code.yousef.summon.modifier.horizontalAlignment
import code.yousef.summon.modifier.hover
import code.yousef.summon.modifier.padding
import code.yousef.summon.modifier.paddingBottom
import code.yousef.summon.modifier.verticalAlignment
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLInputElement

/**
 * Demonstrates core components like Text, Button, TextField, and Checkbox
 */
@Composable
fun CoreComponentsShowcase() {
    Box(
        modifier = Modifier()
            .padding(24.px)
            .maxWidth(800.px)
            .boxShadow("0 8px 16px rgba(0, 0, 0, 0.08)")
            .borderRadius(16.px)
            .backgroundColor("#ffffff")
            .border(1.px, "solid", "rgba(230, 230, 230, 0.7)")
    ) {
        Column(modifier = Modifier().padding(20.px).gap(32.px)) {
            // Section title with gradient underline
            Box(
                modifier = Modifier()
                    .border(3.px, "solid", "#4568dc")
                    .paddingBottom(8.px)
                    .backgroundImage("linear-gradient(90deg, #4568dc 0%, #b06ab3 100%)")
                    .backgroundClip("text")
            ) {
                Text(
                    text = "Core Components",
                    modifier = Modifier()
                        .fontSize(28.px)
                        .fontWeight(800)
                )
            }

            // Text component examples
            Box(
                modifier = Modifier()
                    .padding(16.px)
                    .backgroundColor("#f8f9fa")
                    .borderRadius(12.px)
                    .border(1.px, "solid", "#e9ecef")
                    .boxShadow("0 2px 6px rgba(0, 0, 0, 0.05)")
                    .transition("transform 0.2s ease, box-shadow 0.2s ease")
                    .hover(
                        Modifier()
                            .transform("translateY(-2px)")
                            .boxShadow("0 4px 8px rgba(0, 0, 0, 0.08)")
                    )
            ) {
                Column(modifier = Modifier().padding(16.px).gap(16.px)) {
                    Text(
                        text = "Text Component Examples",
                        modifier = Modifier()
                            .fontSize(20.px)
                            .fontWeight(700)
                            .color("#2d3748")
                            .paddingBottom(8.px)
                            .border(1.px, "solid", "#e9ecef")
                    )
                    Text(
                        text = "Regular text with default styling",
                        modifier = Modifier().fontSize(16.px).lineHeight(1.6)
                    )
                    Text(
                        text = "Blue text with large size",
                        modifier = Modifier().color("#4568dc").fontSize(20.px).fontWeight(600)
                    )
                    Text(
                        text = "Bold red text",
                        modifier = Modifier().color("#b06ab3").fontWeight(700).fontSize(18.px)
                    )
                }
            }

            // Button examples
            Box(
                modifier = Modifier()
                    .padding(16.px)
                    .backgroundColor("#f8f9fa")
                    .borderRadius(12.px)
                    .border(1.px, "solid", "#e9ecef")
                    .boxShadow("0 2px 6px rgba(0, 0, 0, 0.05)")
                    .transition("transform 0.2s ease, box-shadow 0.2s ease")
                    .hover(
                        Modifier()
                            .transform("translateY(-2px)")
                            .boxShadow("0 4px 8px rgba(0, 0, 0, 0.08)")
                    )
            ) {
                Column(modifier = Modifier().padding(16.px).gap(16.px)) {
                    Text(
                        text = "Button Component Examples",
                        modifier = Modifier()
                            .fontSize(20.px)
                            .fontWeight(700)
                            .color("#2d3748")
                            .paddingBottom(8.px)
                            .border(1.px, "solid", "#e9ecef")
                    )

                    Row(modifier = Modifier().gap(16.px).flexWrap("wrap")) {
                        Button(
                            onClick = { window.alert("Primary button clicked!") },
                            label = "Primary Button",
                            modifier = Modifier()
                                .padding(12.px, 20.px)
                                .backgroundColor("#4568dc")
                                .color("#ffffff")
                                .borderRadius(8.px)
                                .fontWeight(600)
                                .boxShadow("0 4px 6px rgba(69, 104, 220, 0.25)")
                                .transition("all 0.2s ease")
                                .hover(
                                    Modifier()
                                        .backgroundColor("#3a58c8")
                                        .boxShadow("0 6px 8px rgba(69, 104, 220, 0.3)")
                                        .transform("translateY(-2px)")
                                ),
                            variant = ButtonVariant.PRIMARY
                        )

                        Button(
                            onClick = { window.alert("Secondary button clicked!") },
                            label = "Secondary Button",
                            modifier = Modifier()
                                .padding(12.px, 20.px)
                                .backgroundColor("#ffffff")
                                .color("#4568dc")
                                .borderRadius(8.px)
                                .border(1.px, "solid", "#4568dc")
                                .fontWeight(600)
                                .transition("all 0.2s ease")
                                .hover(
                                    Modifier()
                                        .backgroundColor("#f0f4ff")
                                        .boxShadow("0 2px 4px rgba(69, 104, 220, 0.15)")
                                ),
                            variant = ButtonVariant.SECONDARY
                        )

                        Button(
                            onClick = { window.alert("Danger button clicked!") },
                            label = "Danger Button",
                            modifier = Modifier()
                                .padding(12.px, 20.px)
                                .backgroundColor("#b06ab3")
                                .color("#ffffff")
                                .borderRadius(8.px)
                                .fontWeight(600)
                                .boxShadow("0 4px 6px rgba(176, 106, 179, 0.25)")
                                .transition("all 0.2s ease")
                                .hover(
                                    Modifier()
                                        .backgroundColor("#9c5ca0")
                                        .boxShadow("0 6px 8px rgba(176, 106, 179, 0.3)")
                                ),
                            variant = ButtonVariant.DANGER
                        )
                    }
                }
            }

            // TextField examples
            Box(
                modifier = Modifier()
                    .padding(16.px)
                    .backgroundColor("#f8f9fa")
                    .borderRadius(12.px)
                    .border(1.px, "solid", "#e9ecef")
                    .boxShadow("0 2px 6px rgba(0, 0, 0, 0.05)")
                    .transition("transform 0.2s ease, box-shadow 0.2s ease")
                    .hover(
                        Modifier()
                            .transform("translateY(-2px)")
                            .boxShadow("0 4px 8px rgba(0, 0, 0, 0.08)")
                    )
            ) {
                Column(modifier = Modifier().padding(16.px).gap(16.px)) {
                    Text(
                        text = "TextField Component Examples",
                        modifier = Modifier()
                            .fontSize(20.px)
                            .fontWeight(700)
                            .color("#2d3748")
                            .paddingBottom(8.px)
                            .border(1.px, "solid", "#e9ecef")
                    )

                    TextField(
                        value = "",
                        onValueChange = { /* In a real app, update state here */ },
                        placeholder = "Enter your name",
                        modifier = Modifier()
                            .width(300.px)
                            .padding(12.px)
                            .borderRadius(8.px)
                            .border(1.px, "solid", "#cbd5e0")
                            .boxShadow("0 1px 3px rgba(0, 0, 0, 0.05)")
                            .transition("border-color 0.2s ease, box-shadow 0.2s ease")
                            .hover(Modifier().borderColor("#4568dc").boxShadow("0 2px 4px rgba(69, 104, 220, 0.15)"))
                    )

                    TextField(
                        value = "",
                        onValueChange = { /* In a real app, update state here */ },
                        placeholder = "Enter password",
                        type = TextFieldType.Password,
                        modifier = Modifier()
                            .width(300.px)
                            .padding(12.px)
                            .borderRadius(8.px)
                            .border(1.px, "solid", "#cbd5e0")
                            .boxShadow("0 1px 3px rgba(0, 0, 0, 0.05)")
                            .transition("border-color 0.2s ease, box-shadow 0.2s ease")
                            .hover(
                                Modifier().borderColor("#4568dc").boxShadow("0 2px 4px rgba(69, 104, 220, 0.15)")
                            )
                    )
                }
            }

            // Checkbox examples
            Box(
                modifier = Modifier()
                    .padding(16.px)
                    .backgroundColor("#f8f9fa")
                    .borderRadius(12.px)
                    .border(1.px, "solid", "#e9ecef")
                    .boxShadow("0 2px 6px rgba(0, 0, 0, 0.05)")
                    .transition("transform 0.2s ease, box-shadow 0.2s ease")
                    .hover(
                        Modifier()
                            .transform("translateY(-2px)")
                            .boxShadow("0 4px 8px rgba(0, 0, 0, 0.08)")
                    )
            ) {
                Column(modifier = Modifier().padding(16.px).gap(16.px)) {
                    Text(
                        text = "Checkbox Component Examples",
                        modifier = Modifier()
                            .fontSize(20.px)
                            .fontWeight(700)
                            .color("#2d3748")
                            .paddingBottom(8.px)
                            .borderBottomWidth(1)
                    )

                    Row(modifier = Modifier().gap(12.px).padding(8.px).alignItems("center")) {
                        // Note: In a real app, you'd track the checked state
                        val checkboxId = "exampleCheckbox"
                        Checkbox(
                            checked = false,
                            onCheckedChange = {
                                val element = document.getElementById(checkboxId) as? HTMLInputElement
                                element?.let { window.alert("Checkbox state: ${it.checked}") }
                            },
                            modifier = Modifier()
                                .padding(4.px)
                                .transform("scale(1.15)")
                                .cursor("pointer")
                        )
                        Text(
                            text = "Accept terms and conditions",
                            modifier = Modifier()
                                .color("#4a5568")
                                .fontSize(16.px)
                                .cursor("pointer")
                        )
                    }
                }
            }

            // Link examples
            Box(
                modifier = Modifier()
                    .padding(8.px)
                    .backgroundColor("#f5f5f5")
                    .borderRadius(4.px)
            ) {
                Column(modifier = Modifier().padding(8.px).gap(8.px)) {
                    Text(
                        text = "Link Component Examples",
                        modifier = Modifier().fontSize(18.px).fontWeight(600)
                    )

                    Link(
                        href = "https://kotlinlang.org",
                        target = "_blank",
                        modifier = Modifier().color("#0077cc")
                    ) {
                        Text("Visit Kotlin website")
                    }
                }
            }
        }
    }
}

/**
 * Demonstrates layout components like Row, Column, Grid, and Card
 */
@Composable
fun LayoutComponentsShowcase() {
    Box(
        modifier = Modifier()
            .padding(16.px)
            .maxWidth(600.px)
            .boxShadow("0 4px 8px rgba(0, 0, 0, 0.1)")
            .borderRadius(8.px)
    ) {
        Column(modifier = Modifier().padding(16.px).gap(24.px)) {
            Text(
                text = "Layout Components",
                modifier = Modifier()
                    .fontSize(24.px)
                    .fontWeight(700)
                    .color("#333333")
            )

            // Row example
            Box(
                modifier = Modifier()
                    .padding(8.px)
                    .backgroundColor("#f5f5f5")
                    .borderRadius(4.px)
            ) {
                Column(modifier = Modifier().padding(8.px).gap(8.px)) {
                    Text(
                        text = "Row Layout Example",
                        modifier = Modifier().fontSize(18.px).fontWeight(600)
                    )

                    Row(
                        modifier = Modifier()
                            .fillMaxWidth()
                            .padding(8.px)
                            .gap(8.px)
                            .horizontalAlignment(Alignment.Horizontal.CenterHorizontally)
                    ) {
                        Box(
                            modifier = Modifier()
                                .padding(8.px)
                                .backgroundColor("#e9f5ff")
                                .borderRadius(4.px)
                                .width(80.px)
                        ) {
                            Text("Item 1", modifier = Modifier().textAlign("center"))
                        }

                        Box(
                            modifier = Modifier()
                                .padding(8.px)
                                .backgroundColor("#e9f5ff")
                                .borderRadius(4.px)
                                .width(80.px)
                        ) {
                            Text("Item 2", modifier = Modifier().textAlign("center"))
                        }

                        Box(
                            modifier = Modifier()
                                .padding(8.px)
                                .backgroundColor("#e9f5ff")
                                .borderRadius(4.px)
                                .width(80.px)
                        ) {
                            Text("Item 3", modifier = Modifier().textAlign("center"))
                        }
                    }
                }
            }

            // Column example
            Box(
                modifier = Modifier()
                    .padding(8.px)
                    .backgroundColor("#f5f5f5")
                    .borderRadius(4.px)
            ) {
                Column(modifier = Modifier().padding(8.px).gap(8.px)) {
                    Text(
                        text = "Column Layout Example",
                        modifier = Modifier().fontSize(18.px).fontWeight(600)
                    )

                    Column(
                        modifier = Modifier()
                            .padding(8.px)
                            .gap(8.px)
                    ) {
                        Box(
                            modifier = Modifier()
                                .padding(8.px)
                                .backgroundColor("#fff0e9")
                                .borderRadius(4.px)
                        ) {
                            Text("Item 1", modifier = Modifier().textAlign("center"))
                        }

                        Box(
                            modifier = Modifier()
                                .padding(8.px)
                                .backgroundColor("#fff0e9")
                                .borderRadius(4.px)
                        ) {
                            Text("Item 2", modifier = Modifier().textAlign("center"))
                        }

                        Box(
                            modifier = Modifier()
                                .padding(8.px)
                                .backgroundColor("#fff0e9")
                                .borderRadius(4.px)
                        ) {
                            Text("Item 3", modifier = Modifier().textAlign("center"))
                        }
                    }
                }
            }

            // Grid example
            Box(
                modifier = Modifier()
                    .padding(8.px)
                    .backgroundColor("#f5f5f5")
                    .borderRadius(4.px)
            ) {
                Column(modifier = Modifier().padding(8.px).gap(8.px)) {
                    Text(
                        text = "Grid Layout Example",
                        modifier = Modifier().fontSize(18.px).fontWeight(600)
                    )

                    Grid(
                        columns = "1fr 1fr 1fr",
                        modifier = Modifier()
                            .padding(8.px)
                            .gap(8.px)
                    ) {
                        repeat(9) { index ->
                            Box(
                                modifier = Modifier()
                                    .padding(8.px)
                                    .backgroundColor("#f0f4ff")
                                    .borderRadius(4.px)
                            ) {
                                Text("Grid ${index + 1}", modifier = Modifier().textAlign("center"))
                            }
                        }
                    }
                }
            }

            // Card example
            Box(
                modifier = Modifier()
                    .padding(8.px)
                    .backgroundColor("#f5f5f5")
                    .borderRadius(4.px)
            ) {
                Column(modifier = Modifier().padding(8.px).gap(8.px)) {
                    Text(
                        text = "Card Component Example",
                        modifier = Modifier().fontSize(18.px).fontWeight(600)
                    )

                    Box(
                        modifier = Modifier()
                            .padding(8.px)
                            .boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
                            .borderRadius(4.px)
                    ) {
                        Column(modifier = Modifier().padding(16.px).gap(8.px)) {
                            Text(
                                text = "Card Title",
                                modifier = Modifier()
                                    .fontSize(18.px)
                                    .fontWeight(700)
                            )
                            Text("This is a card component demonstration from the Summon library.")
                            Button(
                                onClick = { window.alert("Card button clicked!") },
                                modifier = Modifier()
                                    .padding(8.px, 16.px)
                                    .backgroundColor("#0077cc")
                                    .color("#ffffff")
                                    .borderRadius(4.px),
                                label = "Learn More",
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Demonstrates feedback components like Alert and Badge
 */
@Composable
fun FeedbackComponentsShowcase() {
    Box(
        modifier = Modifier()
            .padding(16.px)
            .maxWidth(600.px)
            .boxShadow("0 4px 8px rgba(0, 0, 0, 0.1)")
            .borderRadius(8.px)
    ) {
        Column(modifier = Modifier().padding(16.px).gap(24.px)) {
            Text(
                text = "Feedback Components",
                modifier = Modifier()
                    .fontSize(24.px)
                    .fontWeight(700)
                    .color("#333333")
            )

            // Alert examples
            Box(
                modifier = Modifier()
                    .padding(8.px)
                    .backgroundColor("#f5f5f5")
                    .borderRadius(4.px)
            ) {
                Column(modifier = Modifier().padding(8.px).gap(16.px)) {
                    Text(
                        text = "Alert Component Examples",
                        modifier = Modifier().fontSize(18.px).fontWeight(600)
                    )

                    Alert(
                        message = "Your changes have been saved successfully.",
                        title = "Success",
                        variant = AlertVariant.SUCCESS,
                        onDismiss = { window.alert("Alert dismissed") }
                    )

                    Alert(
                        message = "Your session will expire in 5 minutes.",
                        title = "Warning",
                        variant = AlertVariant.WARNING,
                        onDismiss = { window.alert("Alert dismissed") }
                    )

                    Alert(
                        message = "There was a problem processing your request.",
                        title = "Error",
                        variant = AlertVariant.ERROR,
                        onDismiss = { window.alert("Alert dismissed") }
                    )

                    Alert(
                        message = "A new version is available. Please update your application.",
                        title = "Info",
                        variant = AlertVariant.INFO,
                        onDismiss = { window.alert("Alert dismissed") }
                    )
                }
            }

            // Badge examples
            Box(
                modifier = Modifier()
                    .padding(8.px)
                    .backgroundColor("#f5f5f5")
                    .borderRadius(4.px)
            ) {
                Column(modifier = Modifier().padding(8.px).gap(8.px)) {
                    Text(
                        text = "Badge Component Examples",
                        modifier = Modifier().fontSize(18.px).fontWeight(600)
                    )

                    Row(modifier = Modifier().gap(8.px)) {
                        Box(
                            modifier = Modifier()
                                .backgroundColor("#ff0000")
                                .color("#ffffff")
                                .padding(4.px, 8.px)
                                .borderRadius(12.px)
                        ) {
                            Text(
                                text = "New",
                                modifier = Modifier().fontSize(12.px)
                            )
                        }

                        Box(
                            modifier = Modifier()
                                .backgroundColor("#0077cc")
                                .color("#ffffff")
                                .padding(4.px, 8.px)
                                .borderRadius(12.px)
                        ) {
                            Text(
                                text = "Featured",
                                modifier = Modifier().fontSize(12.px)
                            )
                        }

                        Box(
                            modifier = Modifier()
                                .backgroundColor("#28a745")
                                .color("#ffffff")
                                .padding(4.px, 8.px)
                                .borderRadius(12.px)
                        ) {
                            Text(
                                text = "Sale",
                                modifier = Modifier().fontSize(12.px)
                            )
                        }

                        Box(
                            modifier = Modifier()
                                .backgroundColor("#ffc107")
                                .color("#000000")
                                .padding(4.px, 8.px)
                                .borderRadius(12.px)
                        ) {
                            Text(
                                text = "Limited",
                                modifier = Modifier().fontSize(12.px)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Custom card component example
 */
@Composable
fun CustomCard(
    title: String,
    content: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier()
            .padding(16.px)
            .boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
            .borderRadius(8.px)
            .maxWidth(600.px)
    ) {
        Column(
            modifier = Modifier()
                .padding(16.px)
                .gap(8.px)
        ) {
            Text(
                text = title,
                modifier = Modifier()
                    .fontSize(18.px)
                    .fontWeight(700)
            )
            Text(content)
            Button(
                onClick = onButtonClick,
                label = buttonText,
                modifier = Modifier()
                    .padding(8.px, 16.px)
                    .backgroundColor("#0077cc")
                    .color("#ffffff")
                    .borderRadius(4.px),
                variant = ButtonVariant.PRIMARY
            )
        }
    }
}

/**
 * Demonstrates custom component composition
 */
@Composable
fun CustomComponentsShowcase() {
    Box(
        modifier = Modifier()
            .padding(16.px)
            .maxWidth(600.px)
            .boxShadow("0 4px 8px rgba(0, 0, 0, 0.1)")
            .borderRadius(8.px)
    ) {
        Column(modifier = Modifier().padding(16.px).gap(24.px)) {
            Text(
                text = "Custom Components",
                modifier = Modifier()
                    .fontSize(24.px)
                    .fontWeight(700)
                    .color("#333333")
            )

            CustomCard(
                title = "Welcome to Summon",
                content = "This is a custom component example using component composition.",
                buttonText = "Learn More",
                onButtonClick = { window.alert("Custom card button clicked!") }
            )

            CustomCard(
                title = "Create Your Own Components",
                content = "You can create your own reusable components by composing built-in components.",
                buttonText = "Try Now",
                onButtonClick = { window.alert("Custom components are awesome!") }
            )
        }
    }
}
