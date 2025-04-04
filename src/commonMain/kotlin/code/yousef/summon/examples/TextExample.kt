package code.yousef.summon.examples

import code.yousef.summon.*

/**
 * Example showcasing the enhanced Text component and its utilities.
 */
class TextExample {
    /**
     * Creates a component that demonstrates different text styles.
     */
    fun createTextDemo(): Column {
        return Column(
            modifier = Modifier()
                .padding("20px")
                .maxWidth("800px")
                .margin("0 auto")
                .background("#ffffff"),
            content = listOf(
                // Heading examples
                h1("Text Component Demo", 
                    Modifier().color("#2196F3").margin("0 0 20px 0")
                ),
                
                h2("Different Text Styles"),
                
                paragraph(
                    "The enhanced Text component in Summon supports a variety of text formatting options " +
                    "including font families, text alignment, decoration, transformation, line height, and letter spacing."
                ),
                
                // Paragraph with custom styling
                Text(
                    text = "This paragraph has custom text alignment, line height, and letter spacing.",
                    modifier = Modifier()
                        .padding("10px")
                        .background("#f5f5f5")
                        .borderRadius("4px")
                        .margin("10px 0"),
                    textAlign = "center",
                    lineHeight = "1.8",
                    letterSpacing = "0.5px"
                ),
                
                h3("Text Utilities"),
                
                paragraph("Below are examples of the utility functions that make it easy to create common text styles:"),
                
                // Caption example
                caption("This is a caption - smaller, secondary text for additional information."),
                
                // Label example
                label("Form Field Label"),
                
                // Monospace example
                monospace("const example = 'This is monospaced text for code';"),
                
                // Error and success text examples
                errorText("This is an error message in red color."),
                successText("This is a success message in green color."),
                
                // Text transformation example
                Text(
                    text = "this text is transformed to uppercase",
                    modifier = Modifier().margin("10px 0"),
                    textTransform = "uppercase"
                ),
                
                // Text decoration example
                Text(
                    text = "This text has an underline decoration",
                    modifier = Modifier().margin("10px 0"),
                    textDecoration = "underline"
                ),
                
                // Overflow handling example
                Text(
                    text = "This is a very long text that demonstrates overflow handling with ellipsis. It should truncate when it gets too long for its container.",
                    modifier = Modifier()
                        .width("200px")
                        .padding("10px")
                        .border("1px", "solid", "#ddd")
                        .borderRadius("4px"),
                    overflow = "ellipsis",
                    textAlign = "left"
                ),
                
                // Font family example
                Text(
                    text = "This text uses a different font family.",
                    modifier = Modifier().margin("10px 0"),
                    fontFamily = "'Courier New', Courier, monospace" 
                )
            )
        )
    }
} 