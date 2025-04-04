package code.yousef.summon.examples

import code.yousef.summon.*

/**
 * An example showing how to use the Divider component in different configurations.
 */
object DividerExample {
    /**
     * Creates a sample UI with horizontal and vertical dividers.
     */
    fun create(): Column {
        return Column(
            modifier = Modifier().padding("20px").width("100%"),
            content = listOf(
                Text(
                    text = "Divider Examples",
                    modifier = Modifier().fontSize("24px").fontWeight("bold").margin("0 0 20px 0")
                ),

                // Horizontal divider example
                Text(
                    text = "Horizontal Divider (default)",
                    modifier = Modifier().fontSize("16px").margin("10px 0")
                ),
                Divider(),

                // Custom horizontal divider
                Text(
                    text = "Custom Horizontal Divider",
                    modifier = Modifier().fontSize("16px").margin("20px 0 10px 0")
                ),
                Divider(
                    thickness = "3px",
                    color = "#FF5722",
                    length = "80%",
                    modifier = Modifier().margin("10px 0").borderRadius("2px")
                ),

                // Row with vertical divider
                Text(
                    text = "Vertical Divider Example",
                    modifier = Modifier().fontSize("16px").margin("20px 0 10px 0")
                ),
                Row(
                    modifier = Modifier().height("100px").width("100%").alignItems("center"),
                    content = listOf(
                        Text(
                            text = "Left Content",
                            modifier = Modifier().padding("10px")
                        ),
                        Divider(
                            isVertical = true,
                            thickness = "2px",
                            color = "#2196F3",
                            length = "80px",
                            modifier = Modifier().margin("0 10px")
                        ),
                        Text(
                            text = "Right Content",
                            modifier = Modifier().padding("10px")
                        )
                    )
                ),

                // Divider with custom styling using modifier
                Text(
                    text = "Styled Divider with Shadow",
                    modifier = Modifier().fontSize("16px").margin("20px 0 10px 0")
                ),
                Divider(
                    thickness = "5px",
                    color = "#4CAF50",
                    modifier = Modifier()
                        .margin("15px 0")
                        .boxShadow("0 2px 4px rgba(0,0,0,0.2)")
                        .borderRadius("3px")
                )
            )
        )
    }
} 