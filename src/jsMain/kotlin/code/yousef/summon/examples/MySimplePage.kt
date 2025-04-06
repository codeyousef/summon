package code.yousef.summon.examples

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.modifier.Modifier
import code.yousef.summon.annotation.Composable

/**
 * An example composable function defining a simple page structure.
 */
@Composable
fun MySimplePage() {

    // The root element (like the outer div) should typically be handled
    // by the composition root or a higher-level layout composable.
    // Here we directly use Column as the main layout for the page content.

    Column(
        modifier = Modifier()
            .padding("20px")
            .maxWidth("800px")
            .margin("0 auto")
            .background("#f5f5f5")
            .borderRadius("8px")
    ) {
        Text(
            text = "My First Summon Page",
            modifier = Modifier()
                .fontSize("24px")
                .fontWeight("bold")
                .margin("20px 0")
                .color("#333")
        )
        Text(
            text = "This is a simple webpage created using Summon composables.",
            modifier = Modifier().margin("0 0 20px 0")
        )
        Button(
            onClick = { println("Button clicked!") },
            modifier = Modifier()
                .padding("10px 20px")
                .background("#2196f3")
                .color("white")
                .borderRadius("4px")
                .cursor("pointer")
        ) {
            Text(text = "Click Me!")
        }
    }
} 