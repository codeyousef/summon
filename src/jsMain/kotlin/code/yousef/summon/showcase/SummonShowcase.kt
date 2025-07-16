package code.yousef.summon.showcase

import code.yousef.summon.annotation.Composable
import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Box
import code.yousef.summon.components.layout.Card
import code.yousef.summon.components.layout.Column
import code.yousef.summon.components.layout.Row
import code.yousef.summon.core.style.Color
import code.yousef.summon.modifier.FontWeight
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.modifier.StylingModifiers.backgroundColor
import code.yousef.summon.modifier.StylingModifiers.color
import code.yousef.summon.modifier.textAlign
import code.yousef.summon.modifier.TextAlign

/**
 * Showcase component that demonstrates various Summon UI components.
 * This is displayed at the root path ("/") to show off the library features.
 */
@Composable
fun SummonShowcase() {
    Column(modifier = Modifier().padding("20px").style("gap", "20px")) {
        // Title
        Text("Summon JS Example Showcase", modifier = Modifier().fontSize("24px").fontWeight(FontWeight.Bold.value))

        // Description
        Text(
            "This showcase demonstrates various Summon components running in the browser.",
            modifier = Modifier().color(Color.fromHex("#666")).style("margin-bottom", "20px")
        )

        // Text examples
        Card(modifier = Modifier().padding("16px")) {
            Column(modifier = Modifier().style("gap", "10px")) {
                Text("Text Components", modifier = Modifier().fontSize("18px").fontWeight(FontWeight.Bold.value))
                Text("This is a regular text component.")
                Text("This is styled text.", modifier = Modifier().color(Color.BLUE).style("font-style", "italic"))
                Text("This is large text.", modifier = Modifier().fontSize("20px"))
            }
        }

        // Button examples
        Card(modifier = Modifier().padding("16px")) {
            Column(modifier = Modifier().style("gap", "10px")) {
                Text("Button Components", modifier = Modifier().fontSize("18px").fontWeight(FontWeight.Bold.value))
                Row(modifier = Modifier().style("gap", "10px")) {
                    Button(
                        onClick = { js("console.log('Primary button clicked!')") },
                        label = "Primary Button"
                    )
                    Button(
                        onClick = { js("console.log('Secondary button clicked!')") },
                        label = "Secondary Button",
                        modifier = Modifier().backgroundColor(Color.fromHex("#6c757d"))
                    )
                }
            }
        }

        // Layout examples
        Card(modifier = Modifier().padding("16px")) {
            Column(modifier = Modifier().style("gap", "10px")) {
                Text("Layout Components", modifier = Modifier().fontSize("18px").fontWeight(FontWeight.Bold.value))

                Text("Row Layout:", modifier = Modifier().fontWeight(FontWeight.Bold.value))
                Row(
                    modifier = Modifier().style("gap", "10px").padding("10px").backgroundColor(Color.fromHex("#f8f9fa"))
                ) {
                    Box(modifier = Modifier().width("50px").height("50px").backgroundColor(Color.BLUE)) {
                        Text(
                            "1",
                            modifier = Modifier().color(Color.WHITE).textAlign(TextAlign.Center.value)
                                .style("line-height", "50px")
                        )
                    }
                    Box(modifier = Modifier().width("50px").height("50px").backgroundColor(Color.GREEN)) {
                        Text(
                            "2",
                            modifier = Modifier().color(Color.WHITE).textAlign(TextAlign.Center.value)
                                .style("line-height", "50px")
                        )
                    }
                    Box(modifier = Modifier().width("50px").height("50px").backgroundColor(Color.RED)) {
                        Text(
                            "3",
                            modifier = Modifier().color(Color.WHITE).textAlign(TextAlign.Center.value)
                                .style("line-height", "50px")
                        )
                    }
                }

                Text("Column Layout:", modifier = Modifier().fontWeight(FontWeight.Bold.value))
                Column(
                    modifier = Modifier().style("gap", "5px").padding("10px").backgroundColor(Color.fromHex("#f8f9fa"))
                ) {
                    Box(modifier = Modifier().width("100px").height("30px").backgroundColor(Color.fromHex("#ffc107"))) {
                        Text(
                            "A",
                            modifier = Modifier().color(Color.BLACK).textAlign(TextAlign.Center.value)
                                .style("line-height", "30px")
                        )
                    }
                    Box(modifier = Modifier().width("100px").height("30px").backgroundColor(Color.fromHex("#17a2b8"))) {
                        Text(
                            "B",
                            modifier = Modifier().color(Color.WHITE).textAlign(TextAlign.Center.value)
                                .style("line-height", "30px")
                        )
                    }
                    Box(modifier = Modifier().width("100px").height("30px").backgroundColor(Color.fromHex("#6f42c1"))) {
                        Text(
                            "C",
                            modifier = Modifier().color(Color.WHITE).textAlign(TextAlign.Center.value)
                                .style("line-height", "30px")
                        )
                    }
                }
            }
        }

        // Interactive example
        Card(modifier = Modifier().padding("16px")) {
            Column(modifier = Modifier().style("gap", "10px")) {
                Text("Interactive Example", modifier = Modifier().fontSize("18px").fontWeight(FontWeight.Bold.value))
                Text("Click the button below to see console output:")
                Button(
                    onClick = {
                        js("console.log('✅ Summon JS integration is working!')")
                        js("alert('Summon JS showcase is working perfectly!')")
                    },
                    label = "Test Interaction",
                    modifier = Modifier().backgroundColor(Color.GREEN).color(Color.WHITE)
                )
            }
        }

        // Footer
        Text(
            "🎉 Summon for Kotlin/JS is running successfully!",
            modifier = Modifier().textAlign(TextAlign.Center.value).fontWeight(FontWeight.Bold.value).color(Color.GREEN)
        )
    }
}
