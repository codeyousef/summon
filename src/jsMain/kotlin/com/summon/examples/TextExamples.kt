package com.summon.examples

import com.summon.*

/**
 * Example that demonstrates various text styles using the enhanced Text component.
 */
fun textExamples(): Composable = Column(
    modifier = Modifier()
        .padding("20px")
        .maxWidth("800px")
        .margin("0 auto"),
    content = listOf(
        h1("Text Component Examples"),
        h2("Basic Text Styles"),
        Row(
            modifier = Modifier().margin("20px 0"),
            content = listOf(
                Column(
                    modifier = Modifier()
                        .padding("10px")
                        .width("50%"),
                    content = listOf(
                        Text(
                            text = "Default Text",
                            modifier = Modifier().margin("10px 0")
                        ),
                        h1("Heading 1"),
                        h2("Heading 2"),
                        h3("Heading 3"),
                        paragraph("This is a paragraph with some longer text content to demonstrate line wrapping and other text features in the Text component.")
                    )
                ),
                Column(
                    modifier = Modifier()
                        .padding("10px")
                        .width("50%"),
                    content = listOf(
                        caption("This is a caption"),
                        label("This is a label"),
                        monospace("console.log('Hello, World!');"),
                        errorText("This is an error message"),
                        successText("This is a success message")
                    )
                )
            )
        ),

        h2("Advanced Text Styles"),
        Row(
            modifier = Modifier().margin("20px 0"),
            content = listOf(
                Column(
                    modifier = Modifier()
                        .padding("10px")
                        .width("50%"),
                    content = listOf(
                        Text(
                            text = "Custom Text with Multiple Styles",
                            modifier = Modifier()
                                .fontSize("18px")
                                .color("#3f51b5"),
                            fontFamily = "'Segoe UI', Roboto, sans-serif",
                            lineHeight = "1.4",
                            textAlign = "center",
                            letterSpacing = "0.5px"
                        ),
                        Text(
                            text = "This text has a maximum line count and will truncate if it's too long. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam euismod, nisl eget aliquam ultricies.",
                            maxLines = 2,
                            overflow = "ellipsis"
                        ),
                        Text(
                            text = "This text has custom white space handling",
                            whiteSpace = "nowrap"
                        ),
                        badgeText("New Feature", "#4caf50", "white"),
                        badgeText("Premium", "#ff9800", "white")
                    )
                ),
                Column(
                    modifier = Modifier()
                        .padding("10px")
                        .width("50%"),
                    content = listOf(
                        quoteText("This is a blockquote style text that can be used for quotes or testimonials."),
                        keyboardText("Ctrl+C"),
                        keyboardText("Alt+F4"),
                        Text(
                            text = "Accessible Text with ARIA",
                            role = "heading",
                            ariaLabel = "Heading level 2",
                            ariaDescribedBy = "text-desc"
                        ),
                        Text(
                            text = "This text describes the above heading",
                            modifier = Modifier().fontSize("12px").color("#666")
                        )
                    )
                )
            )
        )
    )
) 