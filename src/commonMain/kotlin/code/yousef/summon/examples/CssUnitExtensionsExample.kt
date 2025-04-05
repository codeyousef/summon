package code.yousef.summon.examples

import code.yousef.summon.core.Composable
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.extensions.*

/**
 * Example showing how to use CSS unit extensions for Numbers with Summon's Modifier API.
 */
class CssUnitExtensionsExample {
    /**
     * Creates a component demonstrating various CSS unit extensions.
     */
    fun createDemo(): Composable {
        return Column(
            content = listOf(
                // Header section
                Text(
                    text = "CSS Unit Extensions Demo",
                    modifier = Modifier()
                        .fontSize(2.rem)
                        .fontWeight("700")
                        .marginBottom(20.px)
                        .color("#2563eb")
                ),
                
                // Pixel Units
                createSectionWithTitle("Pixel Units (px)", 
                    "Pixels are fixed-size units that are relative to the resolution of the screen."),
                
                createUnitBoxes(
                    listOf(
                        Triple("width: 100.px", 100.px, Modifier().width(100)),
                        Triple("height: 50.px", 50.px, Modifier().height(50)),
                        Triple("padding: 15.px", 15.px, Modifier().padding(15)),
                        Triple("margin: 10.px", 10.px, Modifier().margin(10))
                    )
                ),
                
                // REM Units
                createSectionWithTitle("Relative EM Units (rem)",
                    "REMs are relative to the root element's font size, making them ideal for responsive typography."),
                
                createUnitBoxes(
                    listOf(
                        Triple("fontSize: 1.rem", 1.rem, Modifier().fontSize(1.rem)),
                        Triple("fontSize: 1.5.rem", 1.5.rem, Modifier().fontSize(1.5.rem)),
                        Triple("fontSize: 2.rem", 2.rem, Modifier().fontSize(2.rem)),
                        Triple("margin: 1.5.rem", 1.5.rem, Modifier().margin("1.5rem"))
                    )
                ),
                
                // EM Units
                createSectionWithTitle("EM Units (em)",
                    "EMs are relative to the parent element's font size, useful for component-local scaling."),
                
                createUnitBoxes(
                    listOf(
                        Triple("fontSize: 1.em", 1.em, Modifier().fontSize(1.em)),
                        Triple("fontSize: 1.2.em", 1.2.em, Modifier().fontSize(1.2.em)),
                        Triple("padding: 0.5.em", 0.5.em, Modifier().padding("0.5em")),
                        Triple("margin: 0.8.em", 0.8.em, Modifier().margin("0.8em"))
                    )
                ),
                
                // Percentage Units
                createSectionWithTitle("Percentage Units (%)",
                    "Percentages are relative to the parent element's size, useful for responsive layouts."),
                
                createUnitBoxes(
                    listOf(
                        Triple("width: 50.percent", 50.percent, Modifier().width("50%")),
                        Triple("width: 75.percent", 75.percent, Modifier().width("75%")),
                        Triple("width: 100.percent", 100.percent, Modifier().width("100%")),
                        Triple("height: 25.percent", 25.percent, Modifier().height("25%"))
                    )
                ),
                
                // Viewport Units
                createSectionWithTitle("Viewport Units (vw, vh, vmin, vmax)",
                    "Viewport units are relative to the browser viewport dimensions."),
                
                createUnitBoxes(
                    listOf(
                        Triple("width: 25.vw", 25.vw, Modifier().width("25vw")),
                        Triple("height: 10.vh", 10.vh, Modifier().height("10vh")),
                        Triple("min-width: 20.vmin", 20.vmin, Modifier().minWidth("20vmin")),
                        Triple("max-width: 30.vmax", 30.vmax, Modifier().maxWidth("30vmax"))
                    )
                ),
                
                // Individual Property Examples
                createSectionWithTitle("Individual Properties",
                    "Apply margins and padding to specific sides instead of all sides at once."),
                
                Box(
                    content = listOf(
                        Box(
                            content = listOf(
                                Text(
                                    text = "Individual Margin Properties",
                                    modifier = Modifier().fontSize(1.2.rem)
                                )
                            ),
                            modifier = Modifier()
                                .padding("20px")
                                .background("#f0f4ff")
                                .borderRadius("8px")
                                .marginTop(20)
                                .marginRight(30)
                                .marginBottom(40)
                                .marginLeft(50)
                                .border("1px", "dashed", "#2563eb")
                        )
                    ),
                    modifier = Modifier()
                        .padding("20px")
                        .background("#f8fafc")
                        .marginBottom(30)
                ),
                
                Box(
                    content = listOf(
                        Box(
                            content = listOf(
                                Text(
                                    text = "Individual Padding Properties",
                                    modifier = Modifier().fontSize(1.2.rem)
                                )
                            ),
                            modifier = Modifier()
                                .paddingTop(20)
                                .paddingRight(30)
                                .paddingBottom(40)
                                .paddingLeft(50)
                                .background("#fff0f0")
                                .borderRadius("8px")
                                .border("1px", "dashed", "#ef4444")
                        )
                    ),
                    modifier = Modifier()
                        .padding("20px")
                        .background("#f8fafc")
                        .marginBottom(30)
                ),
                
                // Button Example
                Button(
                    label = "Interactive Button Example",
                    modifier = Modifier()
                        .padding("16px")
                        .fontSize(1.1.rem)
                        .backgroundColor("#2563eb")
                        .color("#ffffff")
                        .borderRadius("8px")
                        .border("0px", "none", "transparent")
                        .hover(Modifier().backgroundColor("#1d4ed8"))
                        .margin("30px 0px"),
                    onClick = { /* Handle click */ }
                )
            ),
            modifier = Modifier()
                .padding("30px")
                .maxWidth("800px")
                .margin("0 auto")
                .backgroundColor("#ffffff")
                .borderRadius("12px")
                .boxShadow("0 4px 6px rgba(0, 0, 0, 0.1)")
        )
    }
    
    /**
     * Creates a section with a title and description
     */
    private fun createSectionWithTitle(title: String, description: String): Composable {
        return Box(
            content = listOf(
                Text(
                    text = title,
                    modifier = Modifier()
                        .fontSize(1.5.rem)
                        .fontWeight("600")
                        .marginBottom("8px")
                        .color("#1e40af")
                ),
                Text(
                    text = description,
                    modifier = Modifier()
                        .fontSize(1.rem)
                        .color("#4b5563")
                        .marginBottom("16px")
                )
            ),
            modifier = Modifier()
                .marginTop("20px")
                .marginBottom("10px")
        )
    }
    
    /**
     * Creates example boxes showing different CSS units
     */
    private fun createUnitBoxes(examples: List<Triple<String, String, Modifier>>): Composable {
        return Box(
            content = examples.map { (label, value, modifier) ->
                Box(
                    content = listOf(
                        Text(
                            text = label,
                            modifier = Modifier().fontSize(0.9.rem).marginBottom("8px")
                        ),
                        Text(
                            text = "Value: $value",
                            modifier = Modifier().fontSize(0.8.rem).color("#6b7280")
                        )
                    ),
                    modifier = modifier
                        .padding("15px")
                        .marginRight("10px")
                        .marginBottom("10px")
                        .backgroundColor("#f3f4f6")
                        .borderRadius("6px")
                        .border("1px", "solid", "#e5e7eb")
                )
            },
            modifier = Modifier()
                .display("flex")
                .flexWrap("wrap")
                .marginBottom("30px")
        )
    }
} 