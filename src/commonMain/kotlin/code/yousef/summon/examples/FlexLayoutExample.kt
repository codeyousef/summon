package code.yousef.summon.examples

import code.yousef.summon.*

/**
 * Example showcasing the Flex Layout capabilities using the new flexbox modifiers.
 */
object FlexLayoutExample {
    /**
     * Creates an example with various flex layout configurations.
     */
    fun create(): Column {
        return Column(
            modifier = Modifier()
                .padding("20px")
                .maxWidth("800px")
                .margin("0 auto")
                .background("#f5f5f5"),
            content = listOf(
                Text(
                    "Flex Layout Examples",
                    modifier = Modifier()
                        .fontSize("24px")
                        .fontWeight("bold")
                        .marginBottom("20px")
                ),
                
                // Basic row with flex
                Text(
                    "Basic Row (flex-direction: row)",
                    modifier = Modifier().fontSize("16px").marginBottom("10px")
                ),
                createColoredBoxesRow("row"),
                
                Spacer("20px"),
                
                // Row Reverse
                Text(
                    "Row Reverse (flex-direction: row-reverse)",
                    modifier = Modifier().fontSize("16px").marginBottom("10px")
                ),
                createColoredBoxesRow("row-reverse"),
                
                Spacer("20px"),
                
                // Column
                Text(
                    "Column (flex-direction: column)",
                    modifier = Modifier().fontSize("16px").marginBottom("10px")
                ),
                createColoredBoxesRow("column"),
                
                Spacer("20px"),
                
                // Space Between
                Text(
                    "Space Between (justify-content: space-between)",
                    modifier = Modifier().fontSize("16px").marginBottom("10px")
                ),
                createFlexWithJustify("space-between"),
                
                Spacer("20px"),
                
                // Space Around
                Text(
                    "Space Around (justify-content: space-around)",
                    modifier = Modifier().fontSize("16px").marginBottom("10px")
                ),
                createFlexWithJustify("space-around"),
                
                Spacer("20px"),
                
                // Center Alignment
                Text(
                    "Center Alignment (justify-content: center, align-items: center)",
                    modifier = Modifier().fontSize("16px").marginBottom("10px")
                ),
                createCenteredFlexContainer()
            )
        )
    }
    
    /**
     * Creates a row of colored boxes with the specified flex direction.
     */
    private fun createColoredBoxesRow(direction: String): Composable {
        return Column(
            modifier = Modifier()
                .border("1px", "solid", "#ddd")
                .padding("10px")
                .background("#fff")
                .flex(direction = direction),
            content = listOf(
                createBox("#FF5722", "1"),
                createBox("#2196F3", "2"),
                createBox("#4CAF50", "3"),
                createBox("#9C27B0", "4")
            )
        )
    }
    
    /**
     * Creates a flex container with specified justify-content value.
     */
    private fun createFlexWithJustify(justifyValue: String): Composable {
        return Column(
            modifier = Modifier()
                .border("1px", "solid", "#ddd")
                .padding("10px")
                .background("#fff")
                .flex(justifyContent = justifyValue),
            content = listOf(
                createBox("#FF5722", "1"),
                createBox("#2196F3", "2"),
                createBox("#4CAF50", "3")
            )
        )
    }
    
    /**
     * Creates a flex container with items centered both horizontally and vertically.
     */
    private fun createCenteredFlexContainer(): Composable {
        return Column(
            modifier = Modifier()
                .border("1px", "solid", "#ddd")
                .padding("10px")
                .background("#fff")
                .height("200px")
                .flex(
                    justifyContent = "center",
                    alignItems = "center"
                ),
            content = listOf(
                createBox("#FF9800", "Centered Box")
            )
        )
    }
    
    /**
     * Creates a colored box with text inside.
     */
    private fun createBox(backgroundColor: String, text: String): Composable {
        return Column(
            modifier = Modifier()
                .background(backgroundColor)
                .padding("15px")
                .margin("5px")
                .borderRadius("4px")
                .flex(
                    justifyContent = "center",
                    alignItems = "center"
                )
                .size("60px"),
            content = listOf(
                Text(
                    text = text,
                    modifier = Modifier()
                        .color("#FFFFFF")
                        .fontWeight("bold")
                )
            )
        )
    }
} 