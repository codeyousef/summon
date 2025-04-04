@file:Suppress("UNCHECKED_CAST")

package code.yousef.summon.examples

import code.yousef.summon.core.Composable
import code.yousef.summon.Spacer
import code.yousef.summon.components.display.*
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import kotlinx.html.div

/**
 * Example component that demonstrates the enhanced Text component capabilities.
 */
class EnhancedTextExample : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<T>

            consumer.div {
                Column(
                    modifier = Modifier()
                        .padding("20px")
                        .maxWidth("800px")
                        .margin("0 auto"),
                    content = listOf(
                        h1("Enhanced Text Component"),
                        paragraph("The Text component has been enhanced with additional styling options and accessibility features."),

                        h2("Basic Text Styles"),
                        Row(
                            modifier = Modifier().margin("20px 0"),
                            content = listOf(
                                Column(
                                    modifier = Modifier()
                                        .padding("10px")
                                        .width("45%"),
                                    content = listOf(
                                        Text(
                                            text = "Default Text",
                                            modifier = Modifier().margin("10px 0")
                                        ),
                                        h1("Heading 1"),
                                        h2("Heading 2"),
                                        h3("Heading 3"),
                                        paragraph("This is a paragraph with some longer text content.")
                                    )
                                ),
                                Column(
                                    modifier = Modifier()
                                        .padding("10px")
                                        .width("45%"),
                                    content = listOf(
                                        caption("This is a caption"),
                                        label("This is a label"),
                                        monospace("console.log('Hello');"),
                                        errorText("This is an error message"),
                                        successText("This is a success message")
                                    )
                                )
                            )
                        ),

                        h2("Text Overflow & Multi-line Support"),
                        Row(
                            modifier = Modifier().margin("20px 0"),
                            content = listOf(
                                Column(
                                    modifier = Modifier()
                                        .padding("10px")
                                        .width("100%")
                                        .background("#f5f5f5")
                                        .borderRadius("8px"),
                                    content = listOf(
                                        Text(
                                            text = "Normal text with no overflow handling",
                                            modifier = Modifier().margin("10px 0")
                                        ),
                                        Text(
                                            text = "This text has a maximum line count of one and will truncate if it's too long. Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                            maxLines = 1,
                                            overflow = "ellipsis",
                                            modifier = Modifier().margin("10px 0").maxWidth("500px")
                                        ),
                                        Text(
                                            text = "This text has a maximum line count of two and will truncate after that. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam euismod, nisl eget aliquam ultricies, nisl nisl consectetur nisl, eget aliquam nisl nisl eget.",
                                            maxLines = 2,
                                            overflow = "ellipsis",
                                            modifier = Modifier().margin("10px 0").maxWidth("500px")
                                        )
                                    )
                                )
                            )
                        ),

                        h2("Accessibility Features"),
                        Row(
                            modifier = Modifier().margin("20px 0"),
                            content = listOf(
                                Column(
                                    modifier = Modifier()
                                        .padding("10px")
                                        .width("100%")
                                        .border("1px solid #e0e0e0", "solid", "#e0e0e0")
                                        .borderRadius("8px"),
                                    content = listOf(
                                        Text(
                                            text = "Accessible Heading",
                                            role = "heading",
                                            ariaLabel = "Section heading level 2",
                                            modifier = Modifier().fontSize("1.2rem").fontWeight("bold").margin("10px 0")
                                        ),
                                        Text(
                                            text = "This is alert message for screen readers",
                                            role = "alert",
                                            modifier = Modifier().margin("10px 0").padding("10px").background("#ffebee")
                                                .color("#c62828")
                                        ),
                                        Text(
                                            text = "This is a status message",
                                            role = "status",
                                            modifier = Modifier().margin("10px 0").padding("10px").background("#e8f5e9")
                                                .color("#2e7d32")
                                        )
                                    )
                                )
                            )
                        ),

                        h2("Custom Text Styles"),
                        Row(
                            modifier = Modifier().margin("20px 0"),
                            content = listOf(
                                Column(
                                    modifier = Modifier()
                                        .padding("10px")
                                        .width("45%"),
                                    content = listOf(
                                        badgeText("New", "#2196f3", "white"),
                                        Spacer(size = "10px", isVertical = true),
                                        badgeText("Sale", "#f44336", "white"),
                                        Spacer(size = "10px", isVertical = true),
                                        badgeText("Premium", "#ff9800", "white")
                                    )
                                ),
                                Column(
                                    modifier = Modifier()
                                        .padding("10px")
                                        .width("45%"),
                                    content = listOf(
                                        keyboardText("Ctrl"),
                                        Text(text = " + "),
                                        keyboardText("C"),
                                        Spacer(size = "10px", isVertical = true),
                                        quoteText("This is a blockquote style text for testimonials.")
                                    )
                                )
                            )
                        )
                    )
                ).compose(this)
            }

            return consumer as T
        }
        return receiver
    }
} 