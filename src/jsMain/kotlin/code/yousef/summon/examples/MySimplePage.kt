package code.yousef.summon.examples

import code.yousef.summon.components.display.Text
import code.yousef.summon.components.input.Button
import code.yousef.summon.components.layout.Column
import code.yousef.summon.core.Composable
import code.yousef.summon.modifier.Modifier
import kotlinx.html.TagConsumer
import kotlinx.html.div

class MySimplePage : Composable {
    override fun <T> compose(receiver: T): T {
        if (receiver is TagConsumer<*>) {
            @Suppress("UNCHECKED_CAST")
            val consumer = receiver as TagConsumer<Any?>

            return consumer.div {
                Column(
                    modifier = Modifier()
                        .padding("20px")
                        .maxWidth("800px")
                        .margin("0 auto")
                        .background("#f5f5f5")
                        .borderRadius("8px"),
                    content = listOf(
                        Text(
                            text = "My First Summon Page",
                            modifier = Modifier()
                                .fontSize("24px")
                                .fontWeight("bold")
                                .margin("20px 0")
                                .color("#333")
                        ),
                        Text(
                            text = "This is a simple webpage created using Summon composables.",
                            modifier = Modifier().margin("0 0 20px 0")
                        ),
                        Button(
                            label = "Click Me!",
                            onClick = { println("Button clicked!") },
                            modifier = Modifier()
                                .padding("10px 20px")
                                .background("#2196f3")
                                .color("white")
                                .borderRadius("4px")
                                .cursor("pointer")
                        )
                    )
                ).compose(this)
            } as T
        }
        return receiver
    }
} 