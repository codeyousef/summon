package code.yousef.summon.examples

import code.yousef.summon.*

/**
 * Example showcasing the Link component and its utility functions.
 */
class LinkExample {
    /**
     * Creates a component that demonstrates different link variants.
     */
    fun createLinkDemo(): Column {
        return Column(
            modifier = Modifier()
                .padding("20px")
                .maxWidth("800px")
                .margin("0 auto")
                .background("#ffffff"),
            content = listOf(
                // Heading
                h1(
                    "Link Component Demo",
                    Modifier().color("#2196F3").margin("0 0 20px 0")
                ),

                h2("Regular Links"),

                // Regular link
                Link(
                    text = "Regular Link",
                    href = "/some-page",
                    modifier = Modifier()
                        .margin("0 0 10px 0")
                ),

                // Styled link
                Link(
                    text = "Styled Link",
                    href = "/another-page",
                    modifier = Modifier()
                        .color("#E91E63")
                        .fontSize("18px")
                        .textDecoration("none")
                        .hover(mapOf("text-decoration" to "underline"))
                        .margin("0 0 20px 0")
                ),

                h2("External Links"),

                // External link using utility function
                externalLink(
                    text = "External Link (opens in new tab)",
                    href = "https://example.com",
                    modifier = Modifier()
                        .margin("0 0 10px 0")
                ),

                // NoFollow external link
                externalLink(
                    text = "External Link with NoFollow",
                    href = "https://example.com/promo",
                    modifier = Modifier()
                        .margin("0 0 20px 0"),
                    noFollow = true
                ),

                h2("Button-styled Links"),

                Row(
                    modifier = Modifier()
                        .margin("0 0 20px 0"),
                    content = listOf(
                        // Button-styled link using utility function
                        buttonLink(
                            text = "Primary Action",
                            href = "/action"
                        ),

                        Spacer("10px", false),

                        // Custom button-styled link
                        Link(
                            text = "Secondary Action",
                            href = "/secondary-action",
                            modifier = Modifier()
                                .padding("10px 20px")
                                .background("#f1f1f1")
                                .color("#333")
                                .border("1px", "solid", "#ccc")
                                .borderRadius("4px")
                                .display("inline-block")
                                .textDecoration("none")
                                .hover(mapOf("background-color" to "#ddd"))
                        )
                    )
                ),

                h2("Links with Click Handlers"),

                // Link with client-side click handler
                Link(
                    text = "Click Handler Link",
                    href = "#",
                    onClick = { println("Link clicked!") },
                    modifier = Modifier()
                        .color("#4CAF50")
                        .cursor("pointer")
                        .margin("0 0 10px 0")
                )
            )
        )
    }
} 